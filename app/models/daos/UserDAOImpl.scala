package models.daos

import java.util.UUID

import com.mohiva.play.silhouette.api.LoginInfo
import models.User
import play.api.libs.concurrent.Execution.Implicits.defaultContext

/**
 * Give access to the user object using Slick
 */
class UserDAOImpl extends UserDAO with DAOSlick {

  import driver.api._

  /**
   * Finds a user by its login info.
   *
   * @param loginInfo The login info of the user to find.
   * @return The found user or None if no user for the given login info could be found.
   */
  def find(loginInfo: LoginInfo) = {
    val userQuery = for {
      dbLoginInfo <- loginInfoQuery(loginInfo)
//      dbUserLoginInfo <- slickUserLoginInfos.filter(_.loginInfoId === dbLoginInfo.id)
      dbUser <- slickUsers.filter(_.userID === dbLoginInfo.userID) //(_.id === dbUsersLoginInfo.userID)
    } yield dbUser
    db.run(userQuery.result.headOption).map { dbUserOption =>
      dbUserOption.map { user =>
        User(UUID.fromString(user.userID), loginInfo, user.firstName, user.lastName, user.fullName, user.email)
      }
    }
  }

  /**
   * Finds a user by its user ID.
   *
   * @param userID The ID of the user to find.
   * @return The found user or None if no user for the given ID could be found.
   */
  def find(userID: UUID) = {
    val query = for {
      dbUser <- slickUsers.filter(_.userID === userID.toString)
//      dbUserLoginInfo <- slickUserLoginInfos.filter(_.userID === dbUser.id)
//      dbLoginInfo <- slickLoginInfos.filter(_.id === dbUserLoginInfo.loginInfoId)
      dbLoginInfo <- slickLoginInfos.filter(_.userID === dbUser.userID)
    } yield (dbUser, dbLoginInfo)
    db.run(query.result.headOption).map { resultOption =>
      resultOption.map {
        case (user, loginInfo) =>
          User(
            UUID.fromString(user.userID),
            LoginInfo(loginInfo.providerID, loginInfo.providerKey),
            user.firstName,
            user.lastName,
            user.fullName,
            user.email)
      }
    }
  }

  /**
   * Saves a user.
   *
   * @param user The user to save.
   * @return The saved user.
   */
  def save(user: User) = {
    val dbUser = DBUser(user.userID.toString, user.firstName, user.lastName, user.fullName, user.email)
    val dbLoginInfo = DBLoginInfo(dbUser.userID, user.loginInfo.providerID, user.loginInfo.providerKey)
    // We don't have the LoginInfo id so we try to get it first.
    // If there is no LoginInfo yet for this user we retrieve the id on insertion.    
    val loginInfoAction = {
      val retrieveLoginInfo = slickLoginInfos.filter(
        info => info.providerID === user.loginInfo.providerID &&
        info.providerKey === user.loginInfo.providerKey).result.headOption
//      val insertLoginInfo = slickLoginInfos.returning(slickLoginInfos.map(_.id)).
//        into((info, id) => info.copy(id = Some(id))) += dbLoginInfo

//      val insertLoginInfo = slickLoginInfos.map(info => (info.userID, info.providerID, info.providerKey)) += (dbUser.userID, user.loginInfo.providerID, user.loginInfo.providerKey)
      val insertLoginInfo = slickLoginInfos += DBLoginInfo(dbUser.userID, user.loginInfo.providerID, user.loginInfo.providerKey)

      //retrieveLoginInfo is either LoginInfo if a user exists, or None if not
      //so loginInfo is either "retrieveLoginInfo" if user exists, or val insertLoginInfo if not
      for {
        loginInfoOption <- retrieveLoginInfo
        loginInfo <- loginInfoOption.map(DBIO.successful(_)).getOrElse(insertLoginInfo)
      } yield loginInfo
    }


    // combine database actions to be run sequentially
    val actions = (for {
      _ <- slickUsers.insertOrUpdate(dbUser)
      loginInfo <- loginInfoAction
//      _ <- slickUserLoginInfos += DBUserLoginInfo(dbUser.userID, loginInfo.id.get)
    } yield ()).transactionally
    // run actions and return user afterwards
    db.run(actions).map(_ => user)
  }
}
