package controllers

import java.util.UUID

import com.mohiva.play.silhouette.api.LoginInfo
import com.mohiva.play.silhouette.impl.authenticators.CookieAuthenticator
import com.mohiva.play.silhouette.impl.providers.CredentialsProvider
import com.mohiva.play.silhouette.test.FakeEnvironment
import models.{User, Prescriber}
import models.daos.{PrescriptionDAO, PatientDAO}
import org.joda.time.DateTimeZone
import org.specs2.mock.Mockito
import org.specs2.mutable.Specification
import play.api.i18n.MessagesApi
import play.api.test.{WithApplication, FakeApplication}
import play.api.test.Helpers._

import scala.concurrent.ExecutionContext


class PrescriberControllerSpec (implicit ec: ExecutionContext) extends Specification with Mockito{
  implicit val app: FakeApplication = FakeApplication(additionalConfiguration = inMemoryDatabase("test"))
  val mockPtDAO = mock[PatientDAO]
  val mockCredentialsProvider = mock[CredentialsProvider]
  val mockPrescriptionDAO = mock[PrescriptionDAO]
  val timeZone = DateTimeZone.forID("Europe/London")
  val hospitalNumber = "123"
  val title = "Mrs"
  val firstName = "Cruella"
  val surname = "DaVille"
  val dob = "20-12-1958"
  val email = "bill@thehospital.com"
  val password = "1000"
  val dobDayOfMonth = "10"
  val dobMonth = "10"
  val dobYear = "1999"

  "PrescriberController.formatDateOfBirth" should {
    val dobShort = "1-JAN-2016"
    val dobLong = "20-JAN-2016"
    val day = "day"
    val month = "month"
    val year = "year"

    "return the correctly formatted day for the dob 1-JAN-2016" in new WithApplication(app){
      val mockUuid = UUID.randomUUID()
      val identity = Prescriber(mockUuid, LoginInfo("email", email), "Mr", "Bill", "Smith", email)
      val messagesApi = play.api.Play.current.injector.instanceOf[MessagesApi]
      implicit val env = FakeEnvironment[User, CookieAuthenticator](Seq(identity.loginInfo -> identity))
      val controller = new PrescriberController(messagesApi, env, mockPtDAO, mockCredentialsProvider, mockPrescriptionDAO, timeZone)
      val result = controller.formatDateOfBirth(dobShort,day)
      result must equalTo("1")
    }
    "return the correctly formatted day for the dob 20-JAN-2016" in new WithApplication(app){
      val mockUuid = UUID.randomUUID()
      val identity = Prescriber(mockUuid, LoginInfo("email", email), "Mr", "Bill", "Smith", email)
      val messagesApi = play.api.Play.current.injector.instanceOf[MessagesApi]
      implicit val env = FakeEnvironment[User, CookieAuthenticator](Seq(identity.loginInfo -> identity))
      val controller = new PrescriberController(messagesApi, env, mockPtDAO, mockCredentialsProvider, mockPrescriptionDAO, timeZone)
      val result = controller.formatDateOfBirth(dobLong,day)
      result must equalTo("20")
    }
    "return the correctly formatted month for the dob 1-JAN-2016" in new WithApplication(app){
      val mockUuid = UUID.randomUUID()
      val identity = Prescriber(mockUuid, LoginInfo("email", email), "Mr", "Bill", "Smith", email)
      val messagesApi = play.api.Play.current.injector.instanceOf[MessagesApi]
      implicit val env = FakeEnvironment[User, CookieAuthenticator](Seq(identity.loginInfo -> identity))
      val controller = new PrescriberController(messagesApi, env, mockPtDAO, mockCredentialsProvider, mockPrescriptionDAO, timeZone)
      val result = controller.formatDateOfBirth(dobShort,month)
      result must equalTo("JAN")
    }
    "return the correctly formatted month for the dob 20-JAN-2016" in new WithApplication(app){
      val mockUuid = UUID.randomUUID()
      val identity = Prescriber(mockUuid, LoginInfo("email", email), "Mr", "Bill", "Smith", email)
      val messagesApi = play.api.Play.current.injector.instanceOf[MessagesApi]
      implicit val env = FakeEnvironment[User, CookieAuthenticator](Seq(identity.loginInfo -> identity))
      val controller = new PrescriberController(messagesApi, env, mockPtDAO, mockCredentialsProvider, mockPrescriptionDAO, timeZone)
      val result = controller.formatDateOfBirth(dobLong,month)
      result must equalTo("JAN")
    }
    "return the correctly formatted year for the dob 1-JAN-2016" in new WithApplication(app){
      val mockUuid = UUID.randomUUID()
      val identity = Prescriber(mockUuid, LoginInfo("email", email), "Mr", "Bill", "Smith", email)
      val messagesApi = play.api.Play.current.injector.instanceOf[MessagesApi]
      implicit val env = FakeEnvironment[User, CookieAuthenticator](Seq(identity.loginInfo -> identity))
      val controller = new PrescriberController(messagesApi, env, mockPtDAO, mockCredentialsProvider, mockPrescriptionDAO, timeZone)
      val result = controller.formatDateOfBirth(dobShort,year)
      result must equalTo("2016")
    }
    "return the correctly formatted year for the dob 20-JAN-2016" in new WithApplication(app){
      val mockUuid = UUID.randomUUID()
      val identity = Prescriber(mockUuid, LoginInfo("email", email), "Mr", "Bill", "Smith", email)
      val messagesApi = play.api.Play.current.injector.instanceOf[MessagesApi]
      implicit val env = FakeEnvironment[User, CookieAuthenticator](Seq(identity.loginInfo -> identity))
      val controller = new PrescriberController(messagesApi, env, mockPtDAO, mockCredentialsProvider, mockPrescriptionDAO, timeZone)
      val result = controller.formatDateOfBirth(dobLong,year)
      result must equalTo("2016")
    }
  }
  "PrescriberController.dobToString" should {
    "return the correctly formatted date" in new WithApplication(app) {
      val mockUuid = UUID.randomUUID()
      val identity = Prescriber(mockUuid, LoginInfo("email", email), "Mr", "Bill", "Smith", email)
      val messagesApi = play.api.Play.current.injector.instanceOf[MessagesApi]
      implicit val env = FakeEnvironment[User, CookieAuthenticator](Seq(identity.loginInfo -> identity))
      val controller = new PrescriberController(messagesApi, env, mockPtDAO, mockCredentialsProvider, mockPrescriptionDAO, timeZone)
      val result = controller.dobToString("10", "JAN", "2016")
      result must equalTo("10-JAN-2016")
    }
  }
  "PrescriberController.formatName" should {
    val formattedName = "Cruella"
    "return the correctly formatted name if all lower case" in new WithApplication(app) {
      val mockUuid = UUID.randomUUID()
      val identity = Prescriber(mockUuid, LoginInfo("email", email), "Mr", "Bill", "Smith", email)
      val messagesApi = play.api.Play.current.injector.instanceOf[MessagesApi]
      implicit val env = FakeEnvironment[User, CookieAuthenticator](Seq(identity.loginInfo -> identity))
      val controller = new PrescriberController(messagesApi, env, mockPtDAO, mockCredentialsProvider, mockPrescriptionDAO, timeZone)
      val result = controller.formatName("cruella")
      result must equalTo(formattedName)
    }
    "return the correctly formatted name if all upper case" in new WithApplication(app) {
      val mockUuid = UUID.randomUUID()
      val identity = Prescriber(mockUuid, LoginInfo("email", email), "Mr", "Bill", "Smith", email)
      val messagesApi = play.api.Play.current.injector.instanceOf[MessagesApi]
      implicit val env = FakeEnvironment[User, CookieAuthenticator](Seq(identity.loginInfo -> identity))
      val controller = new PrescriberController(messagesApi, env, mockPtDAO, mockCredentialsProvider, mockPrescriptionDAO, timeZone)
      val result = controller.formatName("CRUELLA")
      result must equalTo(formattedName)
    }
    "return the correctly formatted name if first letter is upper case and the rest lower case" in new WithApplication(app) {
      val mockUuid = UUID.randomUUID()
      val identity = Prescriber(mockUuid, LoginInfo("email", email), "Mr", "Bill", "Smith", email)
      val messagesApi = play.api.Play.current.injector.instanceOf[MessagesApi]
      implicit val env = FakeEnvironment[User, CookieAuthenticator](Seq(identity.loginInfo -> identity))
      val controller = new PrescriberController(messagesApi, env, mockPtDAO, mockCredentialsProvider, mockPrescriptionDAO, timeZone)
      val result = controller.formatName(formattedName)
      result must equalTo(formattedName)
    }
    "return the correctly formatted name if first letter is lower case and the rest upper case" in new WithApplication(app) {
      val mockUuid = UUID.randomUUID()
      val identity = Prescriber(mockUuid, LoginInfo("email", email), "Mr", "Bill", "Smith", email)
      val messagesApi = play.api.Play.current.injector.instanceOf[MessagesApi]
      implicit val env = FakeEnvironment[User, CookieAuthenticator](Seq(identity.loginInfo -> identity))
      val controller = new PrescriberController(messagesApi, env, mockPtDAO, mockCredentialsProvider, mockPrescriptionDAO, timeZone)
      val result = controller.formatName("cRUELLA")
      result must equalTo(formattedName)
    }
    "return the correctly formatted name if the casing is mixed" in new WithApplication(app) {
      val mockUuid = UUID.randomUUID()
      val identity = Prescriber(mockUuid, LoginInfo("email", email), "Mr", "Bill", "Smith", email)
      val messagesApi = play.api.Play.current.injector.instanceOf[MessagesApi]
      implicit val env = FakeEnvironment[User, CookieAuthenticator](Seq(identity.loginInfo -> identity))
      val controller = new PrescriberController(messagesApi, env, mockPtDAO, mockCredentialsProvider, mockPrescriptionDAO, timeZone)
      val result = controller.formatName("cRuElLa")
      result must equalTo(formattedName)
    }
  }
}
