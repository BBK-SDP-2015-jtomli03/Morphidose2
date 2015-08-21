package models.daos

import java.util.UUID

import com.mohiva.play.silhouette.api.LoginInfo
import models.{Prescriber, Administrator, User}
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
    getUserTypeAndID(loginInfo) match {
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
      case None => val userQuery = for {
        dbLoginInfo <- loginInfoQuery(loginInfo)
        dbUser <- slickAdministrators.filter(_.userID === dbLoginInfo.userID)
      } yield dbUser
        db.run(userQuery.result.headOption).map { dbUserOption =>
          dbUserOption.map { user =>
            Administrator(UUID.fromString(user.userID), loginInfo, user.title, user.firstName, user.lastName, user.email)
          }
        }
    }

    //    val userQuery = for {
    //      dbLoginInfo <- loginInfoQuery(loginInfo)
    //      dbUser <- slickUsers.filter(_.userID === dbLoginInfo.userID)
    //    } yield dbUser
    //    db.run(userQuery.result.headOption).map { dbUserOption =>
    //      dbUserOption.map { user =>
    //        Administrator(UUID.fromString(user.userID), loginInfo, user.title, user.firstName, user.lastName, user.email)
    //      }
    //    }
  }


//  def getUserType(loginInfo: LoginInfo) = {
//    val userQuery = for {
//      dbLoginInfo <- loginInfoQuery(loginInfo)
//    } yield dbLoginInfo
//    db.run(userQuery.result.headOption).map { loginInfoOption =>
//      loginInfoOption.map {
//        info => (info.userID, info.userType)
//      }
//    }
//  }

  //  /**
  //   * Finds a user in the database and returns an instance of that user.
  //   *
  //   * @param userType The type of user.
  //   * @param findUserAction The action to perform on the database to find the user data.
  //   * @param loginInfo The login info of the user to find.
  //   * @return An instance of the found user.
  //   */
  //  def getUser(userType: String, findUserAction: Option[Query[Users, DBUser, Seq]], loginInfo: LoginInfo) = {
  //    findUserAction match{
  //      case Some(actn) if userType == "administrator" => db.run(actn.result.headOption).map { resultOption =>
  //        resultOption.map {
  //          case usr =>
  //            Administrator(
  //              UUID.fromString(usr.userID),
  //              LoginInfo(loginInfo.providerID, loginInfo.providerKey),
  //              usr.title,
  //              usr.firstName,
  //              usr.lastName,
  //              usr.email)
  //        }
  //      }
  //      case Some(actn) if userType == "prescriber" => db.run(actn.result.headOption).map { resultOption =>
  //        resultOption.map {
  //          case usr =>
  //            Administrator(
  //              UUID.fromString(usr.userID),
  //              LoginInfo(loginInfo.providerID, loginInfo.providerKey),
  //              usr.title,
  //              usr.firstName,
  //              usr.lastName,
  //              usr.email)
  //        }
  //      }
  //    }
  //  }

//  /**
//   * Returns a Database Query to find a user from a tuple of the userType and userID.
//   *
//   * @param userToReturn a tuple of the userType and userID for a user.
//   * @return The Database Query to find the user.
//   */
//  def getFindUserAction(userToReturn: Option[(String, String)]) = {
//    userToReturn map {
//      case (id, user) if user == "administrator" => slickUsers.filter(_.userID === id)
//      case (id, user) if user == "prescriber" => slickUsers.filter(_.userID === id)
//    }
//  }

//  /**
//   * Gets the userType of the user as a String from a tuple of the userType and userID.
//   *
//   * @param userToReturn a tuple of the userType and userID for a user.
//   * @return The userType as a String.
//   */
//  def getUserType(userToReturn: Option[(String, String)]) = {
//    val userType = userToReturn map (usr => usr._2)
//    userType match {
//      case Some(userTypeString) => userTypeString
//      case None => ""
//    }
//  }

//  /**
//   * Gets the userID and userType of the user from the users loginInfo.
//   *
//   * @param loginInfo The loginInfo of the user to find.
//   * @return The userID and userType of the user.
//   */
  //  def getUserTypeAndID(loginInfo: LoginInfo) = {
  //    val userQuery = for {
  //      dbLoginInfo <- loginInfoQuery(loginInfo)
  //    } yield dbLoginInfo
  //    val dbresult = db.run(userQuery.result.headOption)
  //    val userOption = Await.result(dbresult, 5 second) // await until Future result is returned otherwise problems with Slick chronology of instantiating vals
  //    if(userOption.isEmpty) userOption map {info => (info.userID, info.userType)}
  //    else None
  //  }

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
    //        val actions = (for {
    //          _ <- slickAdministrators.insertOrUpdate(dbUser)
    //          loginInfo <- loginInfoAction
    //        } yield ()).transactionally
    //        // run actions and return user afterwards
    //        db.run(actions).map(_ => user)}

    // combine database actions to be run sequentially and as a whole transaction
    userType match {
      case "administrator" => val actions = (for {
        _ <- slickAdministrators.insertOrUpdate(dbUser)
        loginInfo <- loginInfoAction
      } yield ()).transactionally
        // run actions and return user afterwards
        db.run(actions).map(_ => user)
      case "prescriber" => val actions = (for {
        _ <- slickPrescribers.insertOrUpdate(dbUser)
        loginInfo <- loginInfoAction
      } yield ()).transactionally
        // run actions and return user afterwards
        db.run(actions).map(_ => user)
    }
  }
}

