package controllers

import java.sql.Timestamp
import java.util.UUID

import com.mohiva.play.silhouette.api.LoginInfo
import com.mohiva.play.silhouette.impl.authenticators.CookieAuthenticator
import com.mohiva.play.silhouette.impl.providers.CredentialsProvider
import com.mohiva.play.silhouette.test.FakeEnvironment
import models._
import models.daos._
import org.joda.time.{DateTime, DateTimeZone}
import org.specs2.mock.Mockito
import play.api.i18n.MessagesApi
import play.api.libs.concurrent.Execution.Implicits._
import play.api.libs.json.Json
import play.api.test._
import play.filters.csrf.CSRF


class PatientControllerSpec extends PlaySpecification with Mockito{
  implicit val app: FakeApplication = FakeApplication(additionalConfiguration = inMemoryDatabase("test"))

  "PatientController.register" should {
    "returns an OK response if a patient exists" in new WithServer {
      val messagesApi = play.api.Play.current.injector.instanceOf[MessagesApi]
      val mockUuid = UUID.randomUUID()
      val mockLoginInfo = LoginInfo("email", "bill@thehospital.com")
      val identity = Prescriber(mockUuid, mockLoginInfo, "Mr", "Bill", "Smith", "bill@thehospital.com")
      implicit val env = FakeEnvironment[User, CookieAuthenticator](Seq(identity.loginInfo -> identity))
      val mockPtDAO = mock[PatientDAO]
      val mockDoseDAO = mock[DoseDAO]
      val mockCredentialsProvider = mock[CredentialsProvider]
      val mockPrescriptionDAO = mock[PrescriptionDAO]
      val mockPrescriberDAO = mock[PrescriberDAO]
      val timeZone = DateTimeZone.forID("Europe/London")
      val dataFormatter = new PrescriptionDataFormatterImpl(mockPrescriberDAO, mockDoseDAO, timeZone)
      val spyDataFormatter = spy(dataFormatter)
      val mockPrescription = new Prescription("123", "prescriberID", new Timestamp(new DateTime().withZone(timeZone).getMillis), "MRDrug", 5.00, "breakthroughDrug", 10.00)
      doReturn("Dr Doolittle").when(spyDataFormatter).getPrescriberName(mockPrescription)
      val controller = new PatientController(messagesApi, env, mockPtDAO, mockDoseDAO, mockCredentialsProvider, mockPrescriptionDAO, timeZone, spyDataFormatter)
      val spyController = spy(controller)
      val mockPatient = new Patient("123", "title", "firstName", "surname","dob")
      doReturn(Some(mockPatient)).when(spyController).retrievePatient("123")
      doReturn(Some(mockPrescription)).when(spyController).retrievePrescription("123")
      val requestData = Json.obj(
        "hospitalNumber" -> "123"
      )
      val request = FakeRequest().withBody(requestData).withSession("csrfToken" -> CSRF.SignedTokenProvider.generateToken)
      val result = spyController.register()(request)
      status(result) must equalTo(200)
    }
    "returns prescriptionData as json if a patient exists and has a prescription" in new WithServer {
      val messagesApi = play.api.Play.current.injector.instanceOf[MessagesApi]
      val mockUuid = UUID.randomUUID()
      val mockLoginInfo = LoginInfo("email", "bill@thehospital.com")
      val identity = Prescriber(mockUuid, mockLoginInfo, "Mr", "Bill", "Smith", "bill@thehospital.com")
      implicit val env = FakeEnvironment[User, CookieAuthenticator](Seq(identity.loginInfo -> identity))
      val mockPtDAO = mock[PatientDAO]
      val mockDoseDAO = mock[DoseDAO]
      val mockCredentialsProvider = mock[CredentialsProvider]
      val mockPrescriptionDAO = mock[PrescriptionDAO]
      val mockPrescriberDAO = mock[PrescriberDAO]
      val timeZone = DateTimeZone.forID("Europe/London")
      val dataFormatter = new PrescriptionDataFormatterImpl(mockPrescriberDAO, mockDoseDAO, timeZone)
      val spyDataFormatter = spy(dataFormatter)
      val mockPrescription = new Prescription("123", "prescriberID", new Timestamp(new DateTime(2016,3,13,1,58).getMillis), "MRDrug", 5.00, "breakthroughDrug", 10.00)
      doReturn("Dr Doolittle").when(spyDataFormatter).getPrescriberName(mockPrescription)
      val controller = new PatientController(messagesApi, env, mockPtDAO, mockDoseDAO, mockCredentialsProvider, mockPrescriptionDAO, timeZone, spyDataFormatter)
      val spyController = spy(controller)
      val mockPatient = new Patient("123", "title", "firstName", "surname","dob")
      doReturn(Some(mockPatient)).when(spyController).retrievePatient("123")
      doReturn(Some(mockPrescription)).when(spyController).retrievePrescription("123")
      val requestData = Json.obj(
        "hospitalNumber" -> "123"
      )
      val request = FakeRequest().withBody(requestData).withSession("csrfToken" -> CSRF.SignedTokenProvider.generateToken)
      val result = spyController.register()(request)
      val prescriptionDataJSON = """{"prescriber":"Dr Doolittle","date":"Sun, 13 Mar 2016","mrdrug":"MRDrug","mrdose":"5.0mg","breakthroughDrug":"breakthroughDrug","breakthroughDose":"10.0mg"}"""
      contentAsString(result) must equalTo(prescriptionDataJSON)
    }
  }

}
