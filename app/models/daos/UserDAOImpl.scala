package models.daos

import java.util.UUID

import com.mohiva.play.silhouette.api.LoginInfo
import models.{Administrator, Prescriber, User}
import play.api.libs.concurrent.Execution.Implicits.defaultContext

import scala.concurrent._
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
    (getUserTypeAndID(loginInfo): @unchecked) match {
      case Some((id, userType)) if userType.equals("administrator") => val userQuery = for {
        dbUser <- slickAdministrators.filter(_.userID === id)
      } yield dbUser
        db.run(userQuery.result.headOption).map { dbUserOption =>
          dbUserOption.map { user =>
            Administrator(UUID.fromString(user.userID), loginInfo, user.title, user.firstName, user.lastName, user.email)
          }
        }
      case Some((id, userType)) if userType.equals("prescriber") => val userQuery = for {
        dbUser <- slickPrescribers.filter(_.userID === id)
      } yield dbUser
        db.run(userQuery.result.headOption).map { dbUserOption =>
          dbUserOption.map { user =>
            Prescriber(UUID.fromString(user.userID), loginInfo, user.title, user.firstName, user.lastName, user.email)
          }
        }
    }
  }

  /**
   * Checks if a user exists.
   *
   * @param loginInfo The loginInfo of the user to find.
   * @return boolean whether the user exists or not.
   */
  def userExists(loginInfo: LoginInfo): Boolean = {
    val userQuery = for {
      dbLoginInfo <- loginInfoQuery(loginInfo)
    } yield dbLoginInfo
    val dbresult = db.run(userQuery.result.headOption)
    val userOption = Await.result(dbresult, 5 second) // await until Future result is returned otherwise problems with Slick chronology of instantiating vals
    if(userOption.isEmpty) false
    else true
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
    val dbresult = db.run(userQuery.result.headOption)
    val userOption = Await.result(dbresult, 5 second) // await until Future result is returned otherwise problems with Slick chronology of instantiating vals
    userOption map { info => (info.userID, info.userType) }
  }

  /**
   * Saves a user.
   *
   * @param user The user to save.
   * @return The saved user.
   */
  def save(user: User, userType: String) = {
    val dbUser = DBUser(user.userID.toString, user.title, user.firstName, user.lastName, user.email)
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
    userType match {
      case "administrator" => val actions = (for {
        _ <- slickAdministrators.insertOrUpdate(dbUser)
        loginInfo <- loginInfoAction
      } yield ()).transactionally
        db.run(actions).map(_ => user)
      case "prescriber" => val actions = (for {
        _ <- slickPrescribers.insertOrUpdate(dbUser)
        loginInfo <- loginInfoAction
      } yield ()).transactionally
        db.run(actions).map(_ => user)
    }
  }
}

