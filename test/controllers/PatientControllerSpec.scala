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
import play.api.libs.json.{Json, Writes}
import play.api.test._
import play.filters.csrf.CSRF


class PatientControllerSpec extends PlaySpecification with Mockito{
  implicit val app: FakeApplication = FakeApplication(additionalConfiguration = inMemoryDatabase("test"))
  val hospitalNumber = "123"

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
      val mockPrescription = new Prescription(hospitalNumber, "prescriberID", new Timestamp(new DateTime().withZone(timeZone).getMillis), "MRDrug", 5.00, "breakthroughDrug", 10.00)
      doReturn("Dr Doolittle").when(spyDataFormatter).getPrescriberName(mockPrescription)
      val controller = new PatientController(messagesApi, env, mockPtDAO, mockDoseDAO, mockCredentialsProvider, mockPrescriptionDAO, timeZone, spyDataFormatter)
      val spyController = spy(controller)
      val mockPatient = new Patient(hospitalNumber, "title", "firstName", "surname","dob")
      doReturn(Some(mockPatient)).when(spyController).retrievePatient(hospitalNumber)
      doReturn(Some(mockPrescription)).when(spyController).retrievePrescription(hospitalNumber)
      val requestData = Json.obj(
        "hospitalNumber" -> hospitalNumber
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
      val mockPrescription = new Prescription(hospitalNumber, "prescriberID", new Timestamp(new DateTime(2016,3,13,1,58).getMillis), "MRDrug", 5.00, "breakthroughDrug", 10.00)
      doReturn("Dr Doolittle").when(spyDataFormatter).getPrescriberName(mockPrescription)
      val controller = new PatientController(messagesApi, env, mockPtDAO, mockDoseDAO, mockCredentialsProvider, mockPrescriptionDAO, timeZone, spyDataFormatter)
      val spyController = spy(controller)
      val mockPatient = new Patient(hospitalNumber, "title", "firstName", "surname","dob")
      doReturn(Some(mockPatient)).when(spyController).retrievePatient(hospitalNumber)
      doReturn(Some(mockPrescription)).when(spyController).retrievePrescription(hospitalNumber)
      val requestData = Json.obj(
        "hospitalNumber" -> hospitalNumber
      )
      val request = FakeRequest().withBody(requestData).withSession("csrfToken" -> CSRF.SignedTokenProvider.generateToken)
      val result = spyController.register()(request)
      val prescriptionDataJSON = """{"prescriber":"Dr Doolittle","date":"Sun, 13 Mar 2016","mrdrug":"MRDrug","mrdose":"5.0mg","breakthroughDrug":"breakthroughDrug","breakthroughDose":"10.0mg"}"""
      contentAsString(result) must equalTo(prescriptionDataJSON)
    }
    "returns http 400 response if no patient with the entered hospital number is found" in new WithServer {
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
      val mockPrescription = new Prescription(hospitalNumber, "prescriberID", new Timestamp(new DateTime(2016,3,13,1,58).getMillis), "MRDrug", 5.00, "breakthroughDrug", 10.00)
      val controller = new PatientController(messagesApi, env, mockPtDAO, mockDoseDAO, mockCredentialsProvider, mockPrescriptionDAO, timeZone, dataFormatter)
      val spyController = spy(controller)
      doReturn(None).when(spyController).retrievePatient("999")
      val requestData = Json.obj(
        "hospitalNumber" -> "999"
      )
      val request = FakeRequest().withBody(requestData).withSession("csrfToken" -> CSRF.SignedTokenProvider.generateToken)
      val result = spyController.register()(request)
      status(result) must equalTo(400)
    }
  }
  "PatientController.addDoses" should {
    "returns an OK response if a list of doses is provided in the request" in new WithServer {
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
      val controller = new PatientController(messagesApi, env, mockPtDAO, mockDoseDAO, mockCredentialsProvider, mockPrescriptionDAO, timeZone, dataFormatter)
      val spyController = spy(controller)
      val mockPatient = new Patient(hospitalNumber, "title", "firstName", "surname","dob")
      val dose1 = new Dose(hospitalNumber, new Timestamp(new DateTime(2016,3,13,1,58).getMillis))
      val dose2 = new Dose(hospitalNumber, new Timestamp(1458327131495L))
      val doses = List(dose1, dose2)
      doReturn(dose2).when(spyController).saveDoses(doses)
      implicit val doseDataWrites = new Writes[Dose]{
        def writes(dose: Dose) = Json.obj(
          "date" -> dose.date,
          "hospitalNumber" -> dose.ptHospitalNumber
        )
      }
      val request = FakeRequest().withBody(Json.toJson(doses)).withSession("csrfToken" -> CSRF.SignedTokenProvider.generateToken)
      val result = spyController.addDoses()(request)
      status(result) must equalTo(200)
    }
    "returns a json of the most recent dose added to the database" in new WithServer {
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
      val controller = new PatientController(messagesApi, env, mockPtDAO, mockDoseDAO, mockCredentialsProvider, mockPrescriptionDAO, timeZone, dataFormatter)
      val spyController = spy(controller)
      val mockPatient = new Patient(hospitalNumber, "title", "firstName", "surname","dob")
      val dose1 = new Dose(hospitalNumber, new Timestamp(new DateTime(2016,3,13,1,58).getMillis))
      val dose2 = new Dose(hospitalNumber, new Timestamp(1458327131495L))
      val doses = List(dose1, dose2)
      doReturn(dose2).when(spyController).saveDoses(doses)
      implicit val doseDataWrites = new Writes[Dose]{
        def writes(dose: Dose) = Json.obj(
          "date" -> dose.date,
          "hospitalNumber" -> dose.ptHospitalNumber
        )
      }
      val request = FakeRequest().withBody(Json.toJson(doses)).withSession("csrfToken" -> CSRF.SignedTokenProvider.generateToken)
      val result = spyController.addDoses()(request)
      val jsonDose = Json.obj("date" -> new Timestamp(1458327131495L), "hospitalNumber" -> "123")
      contentAsString(result) must equalTo(Json.stringify(jsonDose))
    }
  }
  "PatientController.saveDoses" should {
    "returns the most recent dose added to the database" in new WithServer {
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
      val controller = new PatientController(messagesApi, env, mockPtDAO, mockDoseDAO, mockCredentialsProvider, mockPrescriptionDAO, timeZone, dataFormatter)
      val mockPatient = new Patient(hospitalNumber, "title", "firstName", "surname","dob")
      val dose1 = new Dose(hospitalNumber, new Timestamp(new DateTime(2016,3,13,1,58).getMillis))
      val dose2 = new Dose(hospitalNumber, new Timestamp(1458327131495L))
      val dose3 = new Dose(hospitalNumber, new Timestamp(new DateTime(2015,3,13,1,58).getMillis))
      val doses = List(dose1, dose2, dose3)
      val result = controller.saveDoses(doses)
      result must equalTo(dose2)
    }
  }

}
