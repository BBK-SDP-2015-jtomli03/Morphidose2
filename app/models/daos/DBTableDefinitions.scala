package models.daos

import java.sql.Timestamp

import com.mohiva.play.silhouette.api.LoginInfo
import models.Prescription
import slick.driver.JdbcProfile
import slick.lifted.ProvenShape.proveShapeOf

trait DBTableDefinitions {
  
  protected val driver: JdbcProfile
  import driver.api._

  case class DBUser (
    userID: String,
    title: String,
    firstName: String,
    lastName: String,
    email: String
  )

  class Prescribers(tag: Tag) extends Table[DBUser](tag, "prescribers") {
    def userID = column[String]("userid", O.PrimaryKey)
    def title = column[String]("title")
    def firstName = column[String]("firstname")
    def lastName = column[String]("lastname")
    def email = column[String]("email")
    def * = (userID, title, firstName, lastName, email) <> (DBUser.tupled, DBUser.unapply)
  }

  class Administrators(tag: Tag) extends Table[DBUser](tag, "administrators") {
    def userID = column[String]("userid", O.PrimaryKey)
    def title = column[String]("title")
    def firstName = column[String]("firstname")
    def lastName = column[String]("lastname")
    def email = column[String]("email")
    def * = (userID, title, firstName, lastName, email) <> (DBUser.tupled, DBUser.unapply)
  }

  case class PtData (
    hospitalNumber: String,
    title: String,
    firstName: String,
    surname: String,
    dob: String)

  class Patients(tag: Tag) extends Table[PtData](tag, "patients") {
    def hospitalNumber = column[String]("hospital_number", O.PrimaryKey)
    def title = column[String]("title")
    def firstName = column[String]("firstname")
    def surname = column[String]("surname")
    def dob = column[String]("dob")
    def * = (hospitalNumber, title, firstName, surname, dob) <> (PtData.tupled, PtData.unapply)
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

  class Prescriptions(tag: Tag) extends Table[Prescription](tag, "prescriptions") {
    def ptHospitalNumber = column[String]("pt_hospital_number", O.PrimaryKey)
    def prescriberID = column[String]("prescriberid")
    def date = column[Timestamp]("date")
    def MRDrug = column[String]("mr_drug")
    def MRDose = column[Double]("mr_dose")
    def breakthroughDrug = column[String]("breakthrough_drug")
    def breakthroughDose = column[Double]("breakthrough_dose")
    def * = (ptHospitalNumber, prescriberID, date, MRDrug, MRDose, breakthroughDrug, breakthroughDose) <> (Prescription.tupled, Prescription.unapply)
    def pkey = primaryKey("prescriptons_pk", (ptHospitalNumber, date))
    def patient = foreignKey("patient_fk", ptHospitalNumber, slickPatients)(_.hospitalNumber)
    def prescriber = foreignKey("prescriber_fk", prescriberID, slickPrescribers)(_.userID)
  }

  // table query definitions
  val slickPrescribers = TableQuery[Prescribers]
  val slickAdministrators = TableQuery[Administrators]
  val slickLoginInfos = TableQuery[LoginInfos]
  val slickPasswordInfos = TableQuery[PasswordInfos]
  val slickPatients = TableQuery[Patients]
  val slickPrescriptions = TableQuery[Prescriptions]
  
  // queries used in multiple places
  def loginInfoQuery(loginInfo: LoginInfo) = 
    slickLoginInfos.filter(dbLoginInfo => dbLoginInfo.providerID === loginInfo.providerID && dbLoginInfo.providerKey === loginInfo.providerKey)

}
