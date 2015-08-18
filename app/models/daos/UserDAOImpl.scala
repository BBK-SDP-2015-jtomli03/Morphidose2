package models.daos

import java.util.UUID

import com.mohiva.play.silhouette.api.LoginInfo
import models.{Administrator, User}
import play.api.libs.concurrent.Execution.Implicits.defaultContext

import scala.concurrent.Await
import scala.concurrent.duration._
import scala.language.postfixOps

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
//  def find(loginInfo: LoginInfo) = {
//      val userQuery = for {
//        dbLoginInfo <- loginInfoQuery(loginInfo)
//        dbUser <- slickUsers.filter(_.userID === dbLoginInfo.userID)
//      } yield dbUser
//      db.run(userQuery.result.headOption).map { dbUserOption =>
//        dbUserOption.map { user =>
//          Administrator(UUID.fromString(user.userID), loginInfo, user.firstName, user.lastName, user.fullName, user.email)
//        }
//      }
//  }

  def find(loginInfo: LoginInfo) = {
      val userTypeAndID = getUserType(loginInfo)
      val userToReturn = Await.result(userTypeAndID, 5 second)
      val userType = userToReturn map (usr => usr._2)
      val userTypeToString = userType match {
        case Some(x) => x
        case None => ""
      }
      val findUserAction = userToReturn map {
        case (id, user) => if (user == "administrator") slickUsers.filter(_.userID === id)
                           else slickUsers.filter(_.userID === id)
      }
      val actionToRun = findUserAction match{
        case Some(actn) if userTypeToString == "administrator" => db.run(actn.result.headOption).map { resultOption =>
          resultOption.map {
            case usr =>
              Administrator(
                UUID.fromString(usr.userID),
                LoginInfo(loginInfo.providerID, loginInfo.providerKey),
                usr.firstName,
                usr.lastName,
                usr.fullName,
                usr.email)
          }
        }
          
      }
      actionToRun
  }

  def getUserType(loginInfo: LoginInfo) = {
    val userQuery = for {
      dbLoginInfo <- loginInfoQuery(loginInfo)
    } yield dbLoginInfo
    db.run(userQuery.result.headOption).map { loginInfoOption =>
      loginInfoOption.map {
        info => (info.userID, info.userType)
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
      dbLoginInfo <- slickLoginInfos.filter(_.userID === dbUser.userID)
    } yield (dbUser, dbLoginInfo)
    db.run(query.result.headOption).map { resultOption =>
      resultOption.map {
        case (user, loginInfo) =>
          Administrator(
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
    val userType = "administrator";
    //get a Slick database Action to run in actions below
    // -> if the user already exists then do nothing, and if this is a new user then save details to the database
    val loginInfoAction = {
      val retrieveLoginInfo = slickLoginInfos.filter(
        info => info.providerID === user.loginInfo.providerID &&
        info.providerKey === user.loginInfo.providerKey).result.headOption
      val insertLoginInfo = slickLoginInfos += DBLoginInfo(dbUser.userID, user.loginInfo.providerID, user.loginInfo.providerKey, userType)
      for {
        loginInfoOption <- retrieveLoginInfo
        loginInfo <- loginInfoOption.map(DBIO.successful(_)).getOrElse(insertLoginInfo)
      } yield loginInfo
    }
    // combine database actions to be run sequentially and as a whole transaction
    val actions = (for {
      _ <- slickUsers.insertOrUpdate(dbUser)
      loginInfo <- loginInfoAction
    } yield ()).transactionally
    // run actions and return user afterwards
    db.run(actions).map(_ => user)
  }
}
