package controllers

import java.sql.Timestamp
import java.util.UUID

import com.mohiva.play.silhouette.api.LoginInfo
import com.mohiva.play.silhouette.impl.authenticators.CookieAuthenticator
import com.mohiva.play.silhouette.impl.providers.CredentialsProvider
import com.mohiva.play.silhouette.test.FakeEnvironment
import models.{User, Prescriber, PrescriptionDataFormatterImpl}
import models.daos.{DoseDAO, PatientDAO, PrescriberDAO, PrescriptionDAO}
import org.joda.time.{DateTime, DateTimeZone}
import org.specs2.mock.Mockito
import org.specs2.mutable.Specification
import play.api.i18n.MessagesApi
import play.api.test.Helpers._
import play.api.test.{FakeApplication, WithApplication}

import scala.concurrent.ExecutionContext


class PrescriptionControllerSpec (implicit ec: ExecutionContext) extends Specification with Mockito{
  implicit val app: FakeApplication = FakeApplication(additionalConfiguration = inMemoryDatabase("test"))

  val email = "bill@thehospital.com"
  val hospitalNumber = "123"
  val title = "Mrs"
  val firstName = "Cruella"
  val surname = "DaVille"
  val dob = "20-12-1958"
  val password = "1000"
  val MRDrug = "morphine"
  val MRDose = "10.0mg"
  val breakthroughDrug = "oramorph"
  val breakthroughDose = "2.5mg"

  "PrescriptionController.getDose" should {
    "return the correct dose for 10.0mg" in new WithApplication(app) {
      val mockUuid = UUID.randomUUID()
      val mockPrescriptionDAO = mock[PrescriptionDAO]
      val mockPrescriberDAO = mock[PrescriberDAO]
      val mockPtDAO = mock[PatientDAO]
      val mockDoseDAO = mock[DoseDAO]
      val mockCredentialsProvider = mock[CredentialsProvider]
      val timeZone = DateTimeZone.forID("Europe/London")
      val dataFormatter = new PrescriptionDataFormatterImpl(mockPrescriberDAO, mockDoseDAO, timeZone)
      val mockLoginInfo = LoginInfo("email", email)
      val identity = Prescriber(mockUuid, mockLoginInfo, title, firstName, surname, email)
      val messagesApi = play.api.Play.current.injector.instanceOf[MessagesApi]
      implicit val env = FakeEnvironment[User, CookieAuthenticator](Seq(identity.loginInfo -> identity))
      val controller = new PrescriptionController(messagesApi, env, mockPrescriptionDAO, mockPrescriberDAO, mockPtDAO, dataFormatter, timeZone)
      val result = controller.getDose("10.0mg")
      result must equalTo(10.0)
    }
    "return the correct dose for 5mg" in new WithApplication(app) {
      val mockUuid = UUID.randomUUID()
      val mockPrescriptionDAO = mock[PrescriptionDAO]
      val mockPrescriberDAO = mock[PrescriberDAO]
      val mockPtDAO = mock[PatientDAO]
      val mockDoseDAO = mock[DoseDAO]
      val mockCredentialsProvider = mock[CredentialsProvider]
      val timeZone = DateTimeZone.forID("Europe/London")
      val dataFormatter = new PrescriptionDataFormatterImpl(mockPrescriberDAO, mockDoseDAO, timeZone)
      val mockLoginInfo = LoginInfo("email", email)
      val identity = Prescriber(mockUuid, mockLoginInfo, title, firstName, surname, email)
      val messagesApi = play.api.Play.current.injector.instanceOf[MessagesApi]
      implicit val env = FakeEnvironment[User, CookieAuthenticator](Seq(identity.loginInfo -> identity))
      val controller = new PrescriptionController(messagesApi, env, mockPrescriptionDAO, mockPrescriberDAO, mockPtDAO, dataFormatter, timeZone)
      val result = controller.getDose("5mg")
      result must equalTo(5.0)
    }
  }
  "PrescriptionController.getDateAsString" should {
    "return the correctly formatted date" in new WithApplication(app) {
      val mockUuid = UUID.randomUUID()
      val mockPrescriptionDAO = mock[PrescriptionDAO]
      val mockPrescriberDAO = mock[PrescriberDAO]
      val mockPtDAO = mock[PatientDAO]
      val mockDoseDAO = mock[DoseDAO]
      val mockCredentialsProvider = mock[CredentialsProvider]
      val timeZone = DateTimeZone.forID("Europe/London")
      val dataFormatter = new PrescriptionDataFormatterImpl(mockPrescriberDAO, mockDoseDAO, timeZone)
      val mockLoginInfo = LoginInfo("email", email)
      val identity = Prescriber(mockUuid, mockLoginInfo, title, firstName, surname, email)
      val messagesApi = play.api.Play.current.injector.instanceOf[MessagesApi]
      implicit val env = FakeEnvironment[User, CookieAuthenticator](Seq(identity.loginInfo -> identity))
      val controller = new PrescriptionController(messagesApi, env, mockPrescriptionDAO, mockPrescriberDAO, mockPtDAO, dataFormatter, timeZone)
      val result = controller.getDateAsString(new Timestamp(new DateTime(2016,3,13,1,58).getMillis))
      result must equalTo("13-03-2016")
    }
    "return the correctly formatted date for 29th feb in a leap year" in new WithApplication(app) {
      val mockUuid = UUID.randomUUID()
      val mockPrescriptionDAO = mock[PrescriptionDAO]
      val mockPrescriberDAO = mock[PrescriberDAO]
      val mockPtDAO = mock[PatientDAO]
      val mockDoseDAO = mock[DoseDAO]
      val mockCredentialsProvider = mock[CredentialsProvider]
      val timeZone = DateTimeZone.forID("Europe/London")
      val dataFormatter = new PrescriptionDataFormatterImpl(mockPrescriberDAO, mockDoseDAO, timeZone)
      val mockLoginInfo = LoginInfo("email", email)
      val identity = Prescriber(mockUuid, mockLoginInfo, title, firstName, surname, email)
      val messagesApi = play.api.Play.current.injector.instanceOf[MessagesApi]
      implicit val env = FakeEnvironment[User, CookieAuthenticator](Seq(identity.loginInfo -> identity))
      val controller = new PrescriptionController(messagesApi, env, mockPrescriptionDAO, mockPrescriberDAO, mockPtDAO, dataFormatter, timeZone)
      val result = controller.getDateAsString(new Timestamp(new DateTime(2016,2,29,1,58).getMillis))
      result must equalTo("29-02-2016")
    }
  }
}
