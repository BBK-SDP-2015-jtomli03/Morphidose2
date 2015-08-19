package models.daos

import com.mohiva.play.silhouette.api.LoginInfo
import slick.driver.JdbcProfile
import slick.lifted.ProvenShape.proveShapeOf

trait DBTableDefinitions {
  
  protected val driver: JdbcProfile
  import driver.api._

  case class DBUser (
    userID: String,
    title: Option[String],
    firstName: Option[String],
    lastName: Option[String],
    email: Option[String]
  )

  class Users(tag: Tag) extends Table[DBUser](tag, "user") {
    def userID = column[String]("userid", O.PrimaryKey)
    def title = column[Option[String]]("title")
    def firstName = column[Option[String]]("firstname")
    def lastName = column[Option[String]]("lastname")
    def email = column[Option[String]]("email")
    def * = (userID, title, firstName, lastName, email) <> (DBUser.tupled, DBUser.unapply)
  }

  case class DBLoginInfo (
     userID: String,
     providerID: String,
     providerKey: String,
     userType: String
  )

  class LoginInfos(tag: Tag) extends Table[DBLoginInfo](tag, "logininfo") {
    def userID = column[String]("userid", O.PrimaryKey)
    def providerID = column[String]("providerid")
    def providerKey = column[String]("providerkey")
    def userType = column[String]("usertype")
    def * = (userID, providerID, providerKey, userType) <> (DBLoginInfo.tupled, DBLoginInfo.unapply)
  }

  case class DBPasswordInfo (
    hasher: String,
    password: String,
    userID: String
  )

  class PasswordInfos(tag: Tag) extends Table[DBPasswordInfo](tag, "passwordinfo") {
    def hasher = column[String]("hasher")
    def password = column[String]("password")
    def userID = column[String]("userid")
    def * = (hasher, password, userID) <> (DBPasswordInfo.tupled, DBPasswordInfo.unapply)
  }

  // table query definitions
  val slickUsers = TableQuery[Users]
  val slickLoginInfos = TableQuery[LoginInfos]
  val slickPasswordInfos = TableQuery[PasswordInfos]
  
  // queries used in multiple places
  def loginInfoQuery(loginInfo: LoginInfo) = 
    slickLoginInfos.filter(dbLoginInfo => dbLoginInfo.providerID === loginInfo.providerID && dbLoginInfo.providerKey === loginInfo.providerKey)

}
