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
  def find(loginInfo: LoginInfo) = {
    val userTypeAndID = getUserTypeAndID(loginInfo)
    val userToReturn = Await.result(userTypeAndID, 5 second) // await until Future result is returned otherwise problems with Slick chronology of instantiating vals
    val userType = getUserType(userToReturn)
    val findUserAction = getFindUserAction(userToReturn)
    getUser(userType, findUserAction, loginInfo)
  }

  /**
   * Finds a user in the database and returns an instance of that user.
   *
   * @param userType The type of user.
   * @param findUserAction The action to perform on the database to find the user data.
   * @param loginInfo The login info of the user to find.
   * @return An instance of the found user.
   */
  def getUser(userType: String, findUserAction: Option[Query[Users, DBUser, Seq]], loginInfo: LoginInfo) = {
    findUserAction match{
      case Some(actn) if userType == "administrator" => db.run(actn.result.headOption).map { resultOption =>
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
      case Some(actn) if userType == "prescriber" => db.run(actn.result.headOption).map { resultOption =>
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
  }

  /**
   * Returns a Database Query to find a user from a tuple of the userType and userID.
   *
   * @param userToReturn a tuple of the userType and userID for a user.
   * @return The Database Query to find the user.
   */
  def getFindUserAction(userToReturn: Option[(String, String)]) = {
    userToReturn map {
      case (id, user) if user == "administrator" => slickUsers.filter(_.userID === id)
      case (id, user) if user == "prescriber" => slickUsers.filter(_.userID === id)
    }
  }

  /**
   * Gets the userType of the user as a String from a tuple of the userType and userID.
   *
   * @param userToReturn a tuple of the userType and userID for a user.
   * @return The userType as a String.
   */
  def getUserType(userToReturn: Option[(String, String)]) = {
    val userType = userToReturn map (usr => usr._2)
    userType match {
      case Some(userTypeString) => userTypeString
      case None => ""
    }
  }

  /**
   * Gets the userID and userType of the user from the users loginInfo.
   *
   * @param loginInfo The loginInfo of the user to find.
   * @return The userID and userType of the user.
   */
  def getUserTypeAndID(loginInfo: LoginInfo) = {
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
