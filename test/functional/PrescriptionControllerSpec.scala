package functional

import java.sql.Timestamp
import java.util.UUID

import com.mohiva.play.silhouette.api.LoginInfo
import com.mohiva.play.silhouette.impl.authenticators.CookieAuthenticator
import com.mohiva.play.silhouette.impl.providers.CredentialsProvider
import com.mohiva.play.silhouette.test.{FakeEnvironment, _}
import models._
import models.daos._
import org.joda.time.{DateTime, DateTimeZone}
import org.specs2.mock.Mockito
import org.specs2.mutable.Specification
import play.api.i18n.MessagesApi
import play.api.libs.json.Json
import play.api.test.Helpers._
import play.api.test.{FakeApplication, FakeRequest, WithApplication}
import play.filters.csrf.CSRF
import controllers.PrescriptionController

import scala.concurrent.{Future, ExecutionContext}


class PrescriptionControllerSpec (implicit ec: ExecutionContext) extends Specification with Mockito{
  implicit val app: FakeApplication = FakeApplication(additionalConfiguration = inMemoryDatabase("test"))
  implicit val app2: FakeApplication = FakeApplication(additionalConfiguration = inMemoryDatabase("test"))

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

  "PrescriptionController.index" should {
    "redirect the user to the sign in page if an unauthorized user requests it (with a 303 redirect)" in new WithApplication(app) {
      val mockUuid = UUID.randomUUID()
      val mockPrescriptionDAO = mock[PrescriptionDAO]
      val mockPrescriberDAO = mock[PrescriberDAO]
      val mockPtDAO = mock[PatientDAO]
      val mockDoseDAO = mock[DoseDAO]
      val mockCredentialsProvider = mock[CredentialsProvider]
      val timeZone = DateTimeZone.forID("Europe/London")
      val dataFormatter = new PrescriptionDataFormatterImpl(mockPrescriberDAO, mockDoseDAO, timeZone)
      val identity = Administrator(mockUuid, LoginInfo("email", email), "Mr", "Bill", "Smith", email)
      val messagesApi = play.api.Play.current.injector.instanceOf[MessagesApi]
      val mockPatient = new Patient(hospitalNumber, title, firstName, surname, dob)
      implicit val env = FakeEnvironment[User, CookieAuthenticator](Seq(identity.loginInfo -> identity))
      val request = FakeRequest().withAuthenticator(identity.loginInfo).withSession("csrfToken" -> CSRF.SignedTokenProvider.generateToken)
      val controller = new PrescriptionController(messagesApi, env, mockPrescriptionDAO, mockPrescriberDAO, mockPtDAO, dataFormatter, timeZone)
      val result = controller.prescription(mockPatient)(request)
      status(result) must equalTo(303)
    }
    "return OK status if an authorized prescriber requests it" in new WithApplication(app) {
      val mockUuid = UUID.randomUUID()
      val mockPrescriptionDAO = mock[PrescriptionDAO]
      val mockPrescriberDAO = mock[PrescriberDAO]
      val mockPtDAO = mock[PatientDAO]
      val mockDoseDAO = mock[DoseDAO]
      val mockCredentialsProvider = mock[CredentialsProvider]
      val timeZone = DateTimeZone.forID("Europe/London")
      val dataFormatter = new PrescriptionDataFormatterImpl(mockPrescriberDAO, mockDoseDAO, timeZone)
      val identity = Prescriber(mockUuid, LoginInfo("email", email), "Mr", "Bill", "Smith", email)
      val messagesApi = play.api.Play.current.injector.instanceOf[MessagesApi]
      val mockPatient = new Patient(hospitalNumber, title, firstName, surname, dob)
      implicit val env = FakeEnvironment[User, CookieAuthenticator](Seq(identity.loginInfo -> identity))
      val request = FakeRequest().withAuthenticator(identity.loginInfo).withSession("csrfToken" -> CSRF.SignedTokenProvider.generateToken)
      val controller = new PrescriptionController(messagesApi, env, mockPrescriptionDAO, mockPrescriberDAO, mockPtDAO, dataFormatter, timeZone)
      val result = controller.prescription(mockPatient)(request)
      status(result) must equalTo(OK)
    }
    "return the prescription page to an authorised user" in new WithApplication(app) {
      val mockUuid = UUID.randomUUID()
      val mockPrescriptionDAO = mock[PrescriptionDAO]
      val mockPrescriberDAO = mock[PrescriberDAO]
      val mockPtDAO = mock[PatientDAO]
      val mockDoseDAO = mock[DoseDAO]
      val mockCredentialsProvider = mock[CredentialsProvider]
      val timeZone = DateTimeZone.forID("Europe/London")
      val dataFormatter = new PrescriptionDataFormatterImpl(mockPrescriberDAO, mockDoseDAO, timeZone)
      val identity = Prescriber(mockUuid, LoginInfo("email", email), "Mr", "Bill", "Smith", email)
      val messagesApi = play.api.Play.current.injector.instanceOf[MessagesApi]
      val mockPatient = new Patient(hospitalNumber, title, firstName, surname, dob)
      implicit val env = FakeEnvironment[User, CookieAuthenticator](Seq(identity.loginInfo -> identity))
      val request = FakeRequest().withAuthenticator(identity.loginInfo).withSession("csrfToken" -> CSRF.SignedTokenProvider.generateToken)
      val controller = new PrescriptionController(messagesApi, env, mockPrescriptionDAO, mockPrescriberDAO, mockPtDAO, dataFormatter, timeZone)
      val result = controller.prescription(mockPatient)(request)
      contentAsString(result) must contain("PRESCRIBE INITIAL DOSES")
    }
    "return the prescription page with the correct patients prescription in the view (to an authorised user)" in new WithApplication(app) {
      val mockUuid = UUID.randomUUID()
      val mockPrescriptionDAO = mock[PrescriptionDAO]
      val mockPrescriberDAO = mock[PrescriberDAO]
      val mockPtDAO = mock[PatientDAO]
      val mockDoseDAO = mock[DoseDAO]
      val mockCredentialsProvider = mock[CredentialsProvider]
      val timeZone = DateTimeZone.forID("Europe/London")
      val dataFormatter = new PrescriptionDataFormatterImpl(mockPrescriberDAO, mockDoseDAO, timeZone)
      val identity = Prescriber(mockUuid, LoginInfo("email", email), "Mr", "Bill", "Smith", email)
      val messagesApi = play.api.Play.current.injector.instanceOf[MessagesApi]
      val mockPatient = new Patient(hospitalNumber, title, firstName, surname, dob)
      implicit val env = FakeEnvironment[User, CookieAuthenticator](Seq(identity.loginInfo -> identity))
      val request = FakeRequest().withAuthenticator(identity.loginInfo).withSession("csrfToken" -> CSRF.SignedTokenProvider.generateToken)
      val controller = new PrescriptionController(messagesApi, env, mockPrescriptionDAO, mockPrescriberDAO, mockPtDAO, dataFormatter, timeZone)
      val result = controller.prescription(mockPatient)(request)
      contentAsString(result) must contain(mockPatient.hospitalNumber)
    }
  }
  "PrescriptionController.selectPatient" should {
    "redirect the user to the sign in page if an unauthorized user requests it (with a 303 redirect)" in new WithApplication(app) {
      val mockUuid = UUID.randomUUID()
      val mockPrescriptionDAO = mock[PrescriptionDAO]
      val mockPrescriberDAO = mock[PrescriberDAO]
      val mockPtDAO = mock[PatientDAO]
      val mockDoseDAO = mock[DoseDAO]
      val mockCredentialsProvider = mock[CredentialsProvider]
      val timeZone = DateTimeZone.forID("Europe/London")
      val dataFormatter = new PrescriptionDataFormatterImpl(mockPrescriberDAO, mockDoseDAO, timeZone)
      val identity = Administrator(mockUuid, LoginInfo("email", email), "Mr", "Bill", "Smith", email)
      val messagesApi = play.api.Play.current.injector.instanceOf[MessagesApi]
      val mockPatient = new Patient(hospitalNumber, title, firstName, surname, dob)
      implicit val env = FakeEnvironment[User, CookieAuthenticator](Seq(identity.loginInfo -> identity))
      val request = FakeRequest().withAuthenticator(identity.loginInfo).withSession("csrfToken" -> CSRF.SignedTokenProvider.generateToken)
      val controller = new PrescriptionController(messagesApi, env, mockPrescriptionDAO, mockPrescriberDAO, mockPtDAO, dataFormatter, timeZone)
      val result = controller.selectPatient(request)
      status(result) must equalTo(303)
    }
    "return OK status if an authorized prescriber requests it" in new WithApplication(app) {
          val mockUuid = UUID.randomUUID()
          val mockPrescriptionDAO = mock[PrescriptionDAO]
          val mockPrescriberDAO = mock[PrescriberDAO]
          val mockPtDAO = mock[PatientDAO]
          val mockDoseDAO = mock[DoseDAO]
          val mockCredentialsProvider = mock[CredentialsProvider]
          val timeZone = DateTimeZone.forID("Europe/London")
          val dataFormatter = new PrescriptionDataFormatterImpl(mockPrescriberDAO, mockDoseDAO, timeZone)
          val identity = Prescriber(mockUuid, LoginInfo("email", email), "Mr", "Bill", "Smith", email)
          val messagesApi = play.api.Play.current.injector.instanceOf[MessagesApi]
          val mockPatient = new Patient(hospitalNumber, title, firstName, surname, dob)
          implicit val env = FakeEnvironment[User, CookieAuthenticator](Seq(identity.loginInfo -> identity))
          val request = FakeRequest().withAuthenticator(identity.loginInfo).withSession("csrfToken" -> CSRF.SignedTokenProvider.generateToken)
          val controller = new PrescriptionController(messagesApi, env, mockPrescriptionDAO, mockPrescriberDAO, mockPtDAO, dataFormatter, timeZone)
          val result = controller.selectPatient(request)
          status(result) must equalTo(200)
        }
    "return the selectPatient page to an authorised user" in new WithApplication(app) {
      val mockUuid = UUID.randomUUID()
      val mockPrescriptionDAO = mock[PrescriptionDAO]
      val mockPrescriberDAO = mock[PrescriberDAO]
      val mockPtDAO = mock[PatientDAO]
      val mockDoseDAO = mock[DoseDAO]
      val mockCredentialsProvider = mock[CredentialsProvider]
      val timeZone = DateTimeZone.forID("Europe/London")
      val dataFormatter = new PrescriptionDataFormatterImpl(mockPrescriberDAO, mockDoseDAO, timeZone)
      val identity = Prescriber(mockUuid, LoginInfo("email", email), "Mr", "Bill", "Smith", email)
      val messagesApi = play.api.Play.current.injector.instanceOf[MessagesApi]
      val mockPatient = new Patient(hospitalNumber, title, firstName, surname, dob)
      implicit val env = FakeEnvironment[User, CookieAuthenticator](Seq(identity.loginInfo -> identity))
      val request = FakeRequest().withAuthenticator(identity.loginInfo).withSession("csrfToken" -> CSRF.SignedTokenProvider.generateToken)
      val controller = new PrescriptionController(messagesApi, env, mockPrescriptionDAO, mockPrescriberDAO, mockPtDAO, dataFormatter, timeZone)
      val result = controller.selectPatient(request)
      contentAsString(result) must contain("Find a Patient")
    }
  }



  "PrescriptionController.addPrescription" should {
    "return http bad request (400) if the form data is of an incorrect format" in new WithApplication(app) {
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
      val formData = (email, password)
      val mockPatient = new Patient(hospitalNumber, title, firstName, surname, dob)
      val request = FakeRequest().withAuthenticator(identity.loginInfo).withFormUrlEncodedBody(formData).withSession("csrfToken" -> CSRF.SignedTokenProvider.generateToken)
      val controller = new PrescriptionController(messagesApi, env, mockPrescriptionDAO, mockPrescriberDAO, mockPtDAO, dataFormatter, timeZone)
      val result = controller.addPrescription(mockPatient)(request)
      status(result) must equalTo(400)
    }
    "return http redirect (303) if the user is unauthorised to access this page" in new WithApplication(app) {
      val mockUuid = UUID.randomUUID()
      val mockPrescriptionDAO = mock[PrescriptionDAO]
      val mockPrescriberDAO = mock[PrescriberDAO]
      val mockPtDAO = mock[PatientDAO]
      val mockDoseDAO = mock[DoseDAO]
      val mockCredentialsProvider = mock[CredentialsProvider]
      val timeZone = DateTimeZone.forID("Europe/London")
      val dataFormatter = new PrescriptionDataFormatterImpl(mockPrescriberDAO, mockDoseDAO, timeZone)
      val mockLoginInfo = LoginInfo("email", email)
      val identity = Administrator(mockUuid, mockLoginInfo, title, firstName, surname, email)
      val messagesApi = play.api.Play.current.injector.instanceOf[MessagesApi]
      implicit val env = FakeEnvironment[User, CookieAuthenticator](Seq(identity.loginInfo -> identity))
      val formData = (email, password)
      val mockPatient = new Patient(hospitalNumber, title, firstName, surname, dob)
      val request = FakeRequest().withAuthenticator(identity.loginInfo).withFormUrlEncodedBody(formData).withSession("csrfToken" -> CSRF.SignedTokenProvider.generateToken)
      val controller = new PrescriptionController(messagesApi, env, mockPrescriptionDAO, mockPrescriberDAO, mockPtDAO, dataFormatter, timeZone)
      val result = controller.addPrescription(mockPatient)(request)
      status(result) must equalTo(303)
    }
    "show the prescriber the current prescription if the data is of the correct format" in new WithApplication(app) {
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
      val formData = Json.obj(
        "MRDrug" -> MRDrug,
        "MRDose" -> MRDose,
        "breakthroughDrug" -> breakthroughDrug,
        "breakthroughDose" -> breakthroughDose
      )
      val mockPatient = new Patient(hospitalNumber, title, firstName, surname, dob)
      val request = FakeRequest().withAuthenticator(identity.loginInfo).withJsonBody(formData).withSession("csrfToken" -> CSRF.SignedTokenProvider.generateToken)
      val controller = new PrescriptionController(messagesApi, env, mockPrescriptionDAO, mockPrescriberDAO, mockPtDAO, dataFormatter, timeZone)
      val result = controller.addPrescription(mockPatient)(request)
      contentAsString(result) must contain("PRESCRIPTION SUCCESSFUL!")
    }
    "show the prescriber the current prescription with the correct patient details if the data is of the correct format" in new WithApplication(app) {
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
      val formData = Json.obj(
        "MRDrug" -> MRDrug,
        "MRDose" -> MRDose,
        "breakthroughDrug" -> breakthroughDrug,
        "breakthroughDose" -> breakthroughDose
      )
      val mockPatient = new Patient(hospitalNumber, title, firstName, surname, dob)
      val request = FakeRequest().withAuthenticator(identity.loginInfo).withJsonBody(formData).withSession("csrfToken" -> CSRF.SignedTokenProvider.generateToken)
      val controller = new PrescriptionController(messagesApi, env, mockPrescriptionDAO, mockPrescriberDAO, mockPtDAO, dataFormatter, timeZone)
      val result = controller.addPrescription(mockPatient)(request)
      contentAsString(result) must contain(mockPatient.hospitalNumber)
    }
    "show the prescriber the current prescription with the correct prescriber displayed if the data is of the correct format" in new WithApplication(app) {
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
      val formData = Json.obj(
        "MRDrug" -> MRDrug,
        "MRDose" -> MRDose,
        "breakthroughDrug" -> breakthroughDrug,
        "breakthroughDose" -> breakthroughDose
      )
      val mockPatient = new Patient(hospitalNumber, title, firstName, surname, dob)
      val request = FakeRequest().withAuthenticator(identity.loginInfo).withJsonBody(formData).withSession("csrfToken" -> CSRF.SignedTokenProvider.generateToken)
      val controller = new PrescriptionController(messagesApi, env, mockPrescriptionDAO, mockPrescriberDAO, mockPtDAO, dataFormatter, timeZone)
      val result = controller.addPrescription(mockPatient)(request)
      contentAsString(result) must contain(identity.firstName)
      contentAsString(result) must contain(identity.lastName)
    }
    "show the prescriber the current prescription with the correct breakthrough drug displayed if the data is of the correct format" in new WithApplication(app) {
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
      val formData = Json.obj(
        "MRDrug" -> MRDrug,
        "MRDose" -> MRDose,
        "breakthroughDrug" -> breakthroughDrug,
        "breakthroughDose" -> breakthroughDose
      )
      val mockPatient = new Patient(hospitalNumber, title, firstName, surname, dob)
      val request = FakeRequest().withAuthenticator(identity.loginInfo).withJsonBody(formData).withSession("csrfToken" -> CSRF.SignedTokenProvider.generateToken)
      val controller = new PrescriptionController(messagesApi, env, mockPrescriptionDAO, mockPrescriberDAO, mockPtDAO, dataFormatter, timeZone)
      val result = controller.addPrescription(mockPatient)(request)
      contentAsString(result) must contain(breakthroughDrug)
    }
    "show the prescriber the current prescription with the correct breakthrough dose displayed if the data is of the correct format" in new WithApplication(app) {
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
      val formData = Json.obj(
        "MRDrug" -> MRDrug,
        "MRDose" -> MRDose,
        "breakthroughDrug" -> breakthroughDrug,
        "breakthroughDose" -> breakthroughDose
      )
      val mockPatient = new Patient(hospitalNumber, title, firstName, surname, dob)
      val request = FakeRequest().withAuthenticator(identity.loginInfo).withJsonBody(formData).withSession("csrfToken" -> CSRF.SignedTokenProvider.generateToken)
      val controller = new PrescriptionController(messagesApi, env, mockPrescriptionDAO, mockPrescriberDAO, mockPtDAO, dataFormatter, timeZone)
      val result = controller.addPrescription(mockPatient)(request)
      contentAsString(result) must contain(breakthroughDose)
    }
    "show the prescriber the current prescription with the correct MR drug displayed if the data is of the correct format" in new WithApplication(app) {
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
      val formData = Json.obj(
        "MRDrug" -> MRDrug,
        "MRDose" -> MRDose,
        "breakthroughDrug" -> breakthroughDrug,
        "breakthroughDose" -> breakthroughDose
      )
      val mockPatient = new Patient(hospitalNumber, title, firstName, surname, dob)
      val request = FakeRequest().withAuthenticator(identity.loginInfo).withJsonBody(formData).withSession("csrfToken" -> CSRF.SignedTokenProvider.generateToken)
      val controller = new PrescriptionController(messagesApi, env, mockPrescriptionDAO, mockPrescriberDAO, mockPtDAO, dataFormatter, timeZone)
      val result = controller.addPrescription(mockPatient)(request)
      contentAsString(result) must contain(MRDrug)
    }
    "show the prescriber the current prescription with the correct MR dose displayed if the data is of the correct format" in new WithApplication(app) {
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
      val formData = Json.obj(
        "MRDrug" -> MRDrug,
        "MRDose" -> MRDose,
        "breakthroughDrug" -> breakthroughDrug,
        "breakthroughDose" -> breakthroughDose
      )
      val mockPatient = new Patient(hospitalNumber, title, firstName, surname, dob)
      val request = FakeRequest().withAuthenticator(identity.loginInfo).withJsonBody(formData).withSession("csrfToken" -> CSRF.SignedTokenProvider.generateToken)
      val controller = new PrescriptionController(messagesApi, env, mockPrescriptionDAO, mockPrescriberDAO, mockPtDAO, dataFormatter, timeZone)
      val result = controller.addPrescription(mockPatient)(request)
      contentAsString(result) must contain(MRDose)
    }
  }
  "PrescriptionController.getLatestPrescriptionWithDoseTitrations" should {
    "return http bad request (400) if the form data is of an incorrect format" in new WithApplication(app) {
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
      val formData = (email, password)
      val mockPatient = new Patient(hospitalNumber, title, firstName, surname, dob)
      val request = FakeRequest().withAuthenticator(identity.loginInfo).withFormUrlEncodedBody(formData).withSession("csrfToken" -> CSRF.SignedTokenProvider.generateToken)
      val controller = new PrescriptionController(messagesApi, env, mockPrescriptionDAO, mockPrescriberDAO, mockPtDAO, dataFormatter, timeZone)
      val result = controller.getLatestPrescriptionWithDoseTitrations(request)
      status(result) must equalTo(400)
    }
    "return http redirect (303) if the user is unauthorised to access this page" in new WithApplication(app) {
      val mockUuid = UUID.randomUUID()
      val mockPrescriptionDAO = mock[PrescriptionDAO]
      val mockPrescriberDAO = mock[PrescriberDAO]
      val mockPtDAO = mock[PatientDAO]
      val mockDoseDAO = mock[DoseDAO]
      val mockCredentialsProvider = mock[CredentialsProvider]
      val timeZone = DateTimeZone.forID("Europe/London")
      val dataFormatter = new PrescriptionDataFormatterImpl(mockPrescriberDAO, mockDoseDAO, timeZone)
      val mockLoginInfo = LoginInfo("email", email)
      val identity = Administrator(mockUuid, mockLoginInfo, title, firstName, surname, email)
      val messagesApi = play.api.Play.current.injector.instanceOf[MessagesApi]
      implicit val env = FakeEnvironment[User, CookieAuthenticator](Seq(identity.loginInfo -> identity))
      val formData = (email, password)
      val mockPatient = new Patient(hospitalNumber, title, firstName, surname, dob)
      val request = FakeRequest().withAuthenticator(identity.loginInfo).withFormUrlEncodedBody(formData).withSession("csrfToken" -> CSRF.SignedTokenProvider.generateToken)
      val controller = new PrescriptionController(messagesApi, env, mockPrescriptionDAO, mockPrescriberDAO, mockPtDAO, dataFormatter, timeZone)
      val result = controller.getLatestPrescriptionWithDoseTitrations(request)
      status(result) must equalTo(303)
    }
    "redirect the prescriber to the selectPatient page if the patient doesn't exist" in new WithApplication(app) {
      val mockUuid = UUID.randomUUID()
      val mockPrescriptionDAO = mock[PrescriptionDAO]
      val mockPrescriberDAO = mock[PrescriberDAO]
      val mockPatientDAO = new PatientDAOImpl()
      val spyPatientDAO = spy(mockPatientDAO)
      doReturn(Future(None)).when(spyPatientDAO).findPatient(hospitalNumber)
      val mockDoseDAO = mock[DoseDAO]
      val mockCredentialsProvider = mock[CredentialsProvider]
      val timeZone = DateTimeZone.forID("Europe/London")
      val dataFormatter = new PrescriptionDataFormatterImpl(mockPrescriberDAO, mockDoseDAO, timeZone)
      val mockLoginInfo = LoginInfo("email", email)
      val identity = Prescriber(mockUuid, mockLoginInfo, title, firstName, surname, email)
      val messagesApi = play.api.Play.current.injector.instanceOf[MessagesApi]
      implicit val env = FakeEnvironment[User, CookieAuthenticator](Seq(identity.loginInfo -> identity))
      val formData = Json.obj(
        "hospitalNumber" -> hospitalNumber
      )
      val mockPatient = new Patient(hospitalNumber, title, firstName, surname, dob)
      val request = FakeRequest().withAuthenticator(identity.loginInfo).withJsonBody(formData).withSession("csrfToken" -> CSRF.SignedTokenProvider.generateToken)
      val controller = new PrescriptionController(messagesApi, env, mockPrescriptionDAO, mockPrescriberDAO, spyPatientDAO, dataFormatter, timeZone)
      val result = controller.getLatestPrescriptionWithDoseTitrations(request)
      redirectLocation(result) must beSome.which(_ == "/patient/select")
    }
    "redirect the prescriber to the retrieveCurrentPrescription if the patient exists" in new WithApplication(app) {
      val mockUuid = UUID.randomUUID()
      val mockPrescriptionDAO = mock[PrescriptionDAO]
      val mockPrescriberDAO = mock[PrescriberDAO]
      val mockPatient = new Patient(hospitalNumber, title, firstName, surname, dob)
      val mockPatientDAO = new PatientDAOImpl()
      val spyPatientDAO = spy(mockPatientDAO)
      doReturn(Future(Some(mockPatient))).when(spyPatientDAO).findPatient(hospitalNumber)
      val mockDoseDAO = mock[DoseDAO]
      val mockCredentialsProvider = mock[CredentialsProvider]
      val timeZone = DateTimeZone.forID("Europe/London")
      val dataFormatter = new PrescriptionDataFormatterImpl(mockPrescriberDAO, mockDoseDAO, timeZone)
      val mockLoginInfo = LoginInfo("email", email)
      val identity = Prescriber(mockUuid, mockLoginInfo, title, firstName, surname, email)
      val messagesApi = play.api.Play.current.injector.instanceOf[MessagesApi]
      implicit val env = FakeEnvironment[User, CookieAuthenticator](Seq(identity.loginInfo -> identity))
      val formData = Json.obj(
        "hospitalNumber" -> hospitalNumber
      )
      val request = FakeRequest().withAuthenticator(identity.loginInfo).withJsonBody(formData).withSession("csrfToken" -> CSRF.SignedTokenProvider.generateToken)
      val controller = new PrescriptionController(messagesApi, env, mockPrescriptionDAO, mockPrescriberDAO, spyPatientDAO, dataFormatter, timeZone)
      val result = controller.getLatestPrescriptionWithDoseTitrations(request)
      redirectLocation(result) must beSome.which(_ == "/prescription/current?patient.hospitalNumber=123&patient.title=Mrs&patient.firstName=Cruella&patient.surname=DaVille&patient.dob=20-12-1958")
    }
  }
  "PrescriptionController.retrieveCurrentPrescription" should {
    "return http redirect (303) if the user is unauthorised to access this page" in new WithApplication(app) {
      val mockUuid = UUID.randomUUID()
      val mockPrescriptionDAO = mock[PrescriptionDAO]
      val mockPrescriberDAO = mock[PrescriberDAO]
      val mockPtDAO = mock[PatientDAO]
      val mockDoseDAO = mock[DoseDAO]
      val mockCredentialsProvider = mock[CredentialsProvider]
      val timeZone = DateTimeZone.forID("Europe/London")
      val dataFormatter = new PrescriptionDataFormatterImpl(mockPrescriberDAO, mockDoseDAO, timeZone)
      val mockLoginInfo = LoginInfo("email", email)
      val identity = Administrator(mockUuid, mockLoginInfo, title, firstName, surname, email)
      val messagesApi = play.api.Play.current.injector.instanceOf[MessagesApi]
      implicit val env = FakeEnvironment[User, CookieAuthenticator](Seq(identity.loginInfo -> identity))
      val formData = (email, password)
      val mockPatient = new Patient(hospitalNumber, title, firstName, surname, dob)
      val request = FakeRequest().withAuthenticator(identity.loginInfo).withFormUrlEncodedBody(formData).withSession("csrfToken" -> CSRF.SignedTokenProvider.generateToken)
      val controller = new PrescriptionController(messagesApi, env, mockPrescriptionDAO, mockPrescriberDAO, mockPtDAO, dataFormatter, timeZone)
      val result = controller.retrieveCurrentPrescription(mockPatient)(request)
      status(result) must equalTo(303)
    }
    "redirects the user to PrescriptionController.prescription if there is no prescription prescribed for the patient" in new WithApplication(app2) {
      val mockUuid = UUID.randomUUID()
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
      val formData = (email, password)
      val mockPatient = new Patient(hospitalNumber, title, firstName, surname, dob)
      val mockPrescriptionDAO = new PrescriptionDAOImpl(timeZone)
      val spyPrescriptionDAO = spy(mockPrescriptionDAO)
      doReturn(Future(None)).when(spyPrescriptionDAO).getLatestPrescription(hospitalNumber)
      val request = FakeRequest().withAuthenticator(identity.loginInfo).withFormUrlEncodedBody(formData).withSession("csrfToken" -> CSRF.SignedTokenProvider.generateToken)
      val controller = new PrescriptionController(messagesApi, env, spyPrescriptionDAO, mockPrescriberDAO, mockPtDAO, dataFormatter, timeZone)
      val result = controller.retrieveCurrentPrescription(mockPatient)(request)
      redirectLocation(result) must beSome.which(_ == "/script?patient.hospitalNumber=123&patient.title=Mrs&patient.firstName=Cruella&patient.surname=DaVille&patient.dob=20-12-1958")
    }
    "returns a http 200 response if the user is authorised and the patient has a current prescription" in new WithApplication(app2) {
      val mockUuid = UUID.randomUUID()
      val mockPrescription = new Prescription(hospitalNumber, "prescriberID", new Timestamp(new DateTime(2016,3,13,1,58).getMillis), "MRDrug", 5.00, "breakthroughDrug", 10.00)
      val mockPrescriberDAO = new PrescriberDAOImpl()
      val spyPrescriberDAO = spy(mockPrescriberDAO)
      doReturn(Future(Some("Mr Bill Smith"))).when(spyPrescriberDAO).findPrescriberName(mockPrescription.prescriberID)
      val mockPtDAO = mock[PatientDAO]
      val timeZone = DateTimeZone.forID("Europe/London")
      val mockDoseDAO = new DoseDAOImpl(timeZone)
      val spyDoseDAO = spy(mockDoseDAO)
      doReturn(Future(10)).when(spyDoseDAO).countBreakthroughDoses(mockPrescription.ptHospitalNumber, mockPrescription.date)
      val mockCredentialsProvider = mock[CredentialsProvider]
      val dataFormatter = new PrescriptionDataFormatterImpl(spyPrescriberDAO, spyDoseDAO, timeZone)
      val mockLoginInfo = LoginInfo("email", email)
      val identity = Prescriber(mockUuid, mockLoginInfo, title, firstName, surname, email)
      val messagesApi = play.api.Play.current.injector.instanceOf[MessagesApi]
      implicit val env = FakeEnvironment[User, CookieAuthenticator](Seq(identity.loginInfo -> identity))
      val formData = (email, password)
      val mockPatient = new Patient(hospitalNumber, title, firstName, surname, dob)
      val mockPrescriptionDAO = new PrescriptionDAOImpl(timeZone)
      val spyPrescriptionDAO = spy(mockPrescriptionDAO)
      doReturn(Future(Some(mockPrescription))).when(spyPrescriptionDAO).getLatestPrescription(hospitalNumber)
      val request = FakeRequest().withAuthenticator(identity.loginInfo).withFormUrlEncodedBody(formData).withSession("csrfToken" -> CSRF.SignedTokenProvider.generateToken)
      val controller = new PrescriptionController(messagesApi, env, spyPrescriptionDAO, mockPrescriberDAO, mockPtDAO, dataFormatter, timeZone)
      val result = controller.retrieveCurrentPrescription(mockPatient)(request)
      status(result) must equalTo(200)
    }
    "displays the doseCalculations page if the user is authorised and the patient has a current prescription" in new WithApplication(app2) {
      val mockUuid = UUID.randomUUID()
      val mockPrescription = new Prescription(hospitalNumber, "prescriberID", new Timestamp(new DateTime(2016,3,13,1,58).getMillis), "MRDrug", 5.00, "breakthroughDrug", 10.00)
      val mockPrescriberDAO = new PrescriberDAOImpl()
      val spyPrescriberDAO = spy(mockPrescriberDAO)
      doReturn(Future(Some("Mr Bill Smith"))).when(spyPrescriberDAO).findPrescriberName(mockPrescription.prescriberID)
      val mockPtDAO = mock[PatientDAO]
      val timeZone = DateTimeZone.forID("Europe/London")
      val mockDoseDAO = new DoseDAOImpl(timeZone)
      val spyDoseDAO = spy(mockDoseDAO)
      doReturn(Future(10)).when(spyDoseDAO).countBreakthroughDoses(mockPrescription.ptHospitalNumber, mockPrescription.date)
      val mockCredentialsProvider = mock[CredentialsProvider]
      val dataFormatter = new PrescriptionDataFormatterImpl(spyPrescriberDAO, spyDoseDAO, timeZone)
      val mockLoginInfo = LoginInfo("email", email)
      val identity = Prescriber(mockUuid, mockLoginInfo, title, firstName, surname, email)
      val messagesApi = play.api.Play.current.injector.instanceOf[MessagesApi]
      implicit val env = FakeEnvironment[User, CookieAuthenticator](Seq(identity.loginInfo -> identity))
      val formData = (email, password)
      val mockPatient = new Patient(hospitalNumber, title, firstName, surname, dob)
      val mockPrescriptionDAO = new PrescriptionDAOImpl(timeZone)
      val spyPrescriptionDAO = spy(mockPrescriptionDAO)
      doReturn(Future(Some(mockPrescription))).when(spyPrescriptionDAO).getLatestPrescription(hospitalNumber)
      val request = FakeRequest().withAuthenticator(identity.loginInfo).withFormUrlEncodedBody(formData).withSession("csrfToken" -> CSRF.SignedTokenProvider.generateToken)
      val controller = new PrescriptionController(messagesApi, env, spyPrescriptionDAO, mockPrescriberDAO, mockPtDAO, dataFormatter, timeZone)
      val result = controller.retrieveCurrentPrescription(mockPatient)(request)
      contentAsString(result) must contain("Suggested Dose Titrations")
    }
    "displays the doseCalculations page with the correct prescriber details if the user is authorised and the patient has a current prescription" in new WithApplication(app2) {
      val mockUuid = UUID.randomUUID()
      val mockPrescription = new Prescription(hospitalNumber, "prescriberID", new Timestamp(new DateTime(2016,3,13,1,58).getMillis), "MRDrug", 5.00, "breakthroughDrug", 10.00)
      val mockPrescriberDAO = new PrescriberDAOImpl()
      val spyPrescriberDAO = spy(mockPrescriberDAO)
      doReturn(Future(Some("Mr Bill Smith"))).when(spyPrescriberDAO).findPrescriberName(mockPrescription.prescriberID)
      val mockPtDAO = mock[PatientDAO]
      val timeZone = DateTimeZone.forID("Europe/London")
      val mockDoseDAO = new DoseDAOImpl(timeZone)
      val spyDoseDAO = spy(mockDoseDAO)
      doReturn(Future(10)).when(spyDoseDAO).countBreakthroughDoses(mockPrescription.ptHospitalNumber, mockPrescription.date)
      val mockCredentialsProvider = mock[CredentialsProvider]
      val dataFormatter = new PrescriptionDataFormatterImpl(spyPrescriberDAO, spyDoseDAO, timeZone)
      val mockLoginInfo = LoginInfo("email", email)
      val identity = Prescriber(mockUuid, mockLoginInfo, title, firstName, surname, email)
      val messagesApi = play.api.Play.current.injector.instanceOf[MessagesApi]
      implicit val env = FakeEnvironment[User, CookieAuthenticator](Seq(identity.loginInfo -> identity))
      val formData = (email, password)
      val mockPatient = new Patient(hospitalNumber, title, firstName, surname, dob)
      val mockPrescriptionDAO = new PrescriptionDAOImpl(timeZone)
      val spyPrescriptionDAO = spy(mockPrescriptionDAO)
      doReturn(Future(Some(mockPrescription))).when(spyPrescriptionDAO).getLatestPrescription(hospitalNumber)
      val request = FakeRequest().withAuthenticator(identity.loginInfo).withFormUrlEncodedBody(formData).withSession("csrfToken" -> CSRF.SignedTokenProvider.generateToken)
      val controller = new PrescriptionController(messagesApi, env, spyPrescriptionDAO, mockPrescriberDAO, mockPtDAO, dataFormatter, timeZone)
      val result = controller.retrieveCurrentPrescription(mockPatient)(request)
      contentAsString(result) must contain(identity.firstName +" "+ identity.lastName)
    }
    "displays the doseCalculations page with the correct patient if the user is authorised and the patient has a current prescription" in new WithApplication(app2) {
      val mockUuid = UUID.randomUUID()
      val mockPrescription = new Prescription(hospitalNumber, "prescriberID", new Timestamp(new DateTime(2016,3,13,1,58).getMillis), "MRDrug", 5.00, "breakthroughDrug", 10.00)
      val mockPrescriberDAO = new PrescriberDAOImpl()
      val spyPrescriberDAO = spy(mockPrescriberDAO)
      doReturn(Future(Some("Mr Bill Smith"))).when(spyPrescriberDAO).findPrescriberName(mockPrescription.prescriberID)
      val mockPtDAO = mock[PatientDAO]
      val timeZone = DateTimeZone.forID("Europe/London")
      val mockDoseDAO = new DoseDAOImpl(timeZone)
      val spyDoseDAO = spy(mockDoseDAO)
      doReturn(Future(10)).when(spyDoseDAO).countBreakthroughDoses(mockPrescription.ptHospitalNumber, mockPrescription.date)
      val mockCredentialsProvider = mock[CredentialsProvider]
      val dataFormatter = new PrescriptionDataFormatterImpl(spyPrescriberDAO, spyDoseDAO, timeZone)
      val mockLoginInfo = LoginInfo("email", email)
      val identity = Prescriber(mockUuid, mockLoginInfo, title, firstName, surname, email)
      val messagesApi = play.api.Play.current.injector.instanceOf[MessagesApi]
      implicit val env = FakeEnvironment[User, CookieAuthenticator](Seq(identity.loginInfo -> identity))
      val formData = (email, password)
      val mockPatient = new Patient(hospitalNumber, title, firstName, surname, dob)
      val mockPrescriptionDAO = new PrescriptionDAOImpl(timeZone)
      val spyPrescriptionDAO = spy(mockPrescriptionDAO)
      doReturn(Future(Some(mockPrescription))).when(spyPrescriptionDAO).getLatestPrescription(hospitalNumber)
      val request = FakeRequest().withAuthenticator(identity.loginInfo).withFormUrlEncodedBody(formData).withSession("csrfToken" -> CSRF.SignedTokenProvider.generateToken)
      val controller = new PrescriptionController(messagesApi, env, spyPrescriptionDAO, mockPrescriberDAO, mockPtDAO, dataFormatter, timeZone)
      val result = controller.retrieveCurrentPrescription(mockPatient)(request)
      contentAsString(result) must contain(mockPatient.hospitalNumber)
    }
    "displays the doseCalculations page with the correct current prescription information if the user is authorised and the patient has a current prescription" in new WithApplication(app2) {
      val mockUuid = UUID.randomUUID()
      val mockPrescription = new Prescription(hospitalNumber, "prescriberID", new Timestamp(new DateTime(2016,3,13,1,58).getMillis), "MRDrug", 5.00, "breakthroughDrug", 10.00)
      val mockPrescriberDAO = new PrescriberDAOImpl()
      val spyPrescriberDAO = spy(mockPrescriberDAO)
      doReturn(Future(Some("Mr Bill Smith"))).when(spyPrescriberDAO).findPrescriberName(mockPrescription.prescriberID)
      val mockPtDAO = mock[PatientDAO]
      val timeZone = DateTimeZone.forID("Europe/London")
      val mockDoseDAO = new DoseDAOImpl(timeZone)
      val spyDoseDAO = spy(mockDoseDAO)
      doReturn(Future(10)).when(spyDoseDAO).countBreakthroughDoses(mockPrescription.ptHospitalNumber, mockPrescription.date)
      val mockCredentialsProvider = mock[CredentialsProvider]
      val dataFormatter = new PrescriptionDataFormatterImpl(spyPrescriberDAO, spyDoseDAO, timeZone)
      val mockLoginInfo = LoginInfo("email", email)
      val identity = Prescriber(mockUuid, mockLoginInfo, title, firstName, surname, email)
      val messagesApi = play.api.Play.current.injector.instanceOf[MessagesApi]
      implicit val env = FakeEnvironment[User, CookieAuthenticator](Seq(identity.loginInfo -> identity))
      val formData = (email, password)
      val mockPatient = new Patient(hospitalNumber, title, firstName, surname, dob)
      val mockPrescriptionDAO = new PrescriptionDAOImpl(timeZone)
      val spyPrescriptionDAO = spy(mockPrescriptionDAO)
      doReturn(Future(Some(mockPrescription))).when(spyPrescriptionDAO).getLatestPrescription(hospitalNumber)
      val request = FakeRequest().withAuthenticator(identity.loginInfo).withFormUrlEncodedBody(formData).withSession("csrfToken" -> CSRF.SignedTokenProvider.generateToken)
      val controller = new PrescriptionController(messagesApi, env, spyPrescriptionDAO, mockPrescriberDAO, mockPtDAO, dataFormatter, timeZone)
      val result = controller.retrieveCurrentPrescription(mockPatient)(request)
      contentAsString(result) must contain(mockPrescription.breakthroughDrug)
      contentAsString(result) must contain(mockPrescription.breakthroughDose.toString)
    }
  }
  "PrescriptionController.repeatPrescription" should {
    "return http redirect (303) if the user is unauthorised to access this page" in new WithApplication(app) {
      val mockUuid = UUID.randomUUID()
      val mockPrescriptionDAO = mock[PrescriptionDAO]
      val mockPrescriberDAO = mock[PrescriberDAO]
      val mockPtDAO = mock[PatientDAO]
      val mockDoseDAO = mock[DoseDAO]
      val mockCredentialsProvider = mock[CredentialsProvider]
      val timeZone = DateTimeZone.forID("Europe/London")
      val dataFormatter = new PrescriptionDataFormatterImpl(mockPrescriberDAO, mockDoseDAO, timeZone)
      val mockLoginInfo = LoginInfo("email", email)
      val identity = Administrator(mockUuid, mockLoginInfo, title, firstName, surname, email)
      val messagesApi = play.api.Play.current.injector.instanceOf[MessagesApi]
      implicit val env = FakeEnvironment[User, CookieAuthenticator](Seq(identity.loginInfo -> identity))
      val formData = (email, password)
      val mockPatient = new Patient(hospitalNumber, title, firstName, surname, dob)
      val doseTitrationData = DoseTitrationData("10","2","5","20","25","2.5","5")
      val request = FakeRequest().withAuthenticator(identity.loginInfo).withFormUrlEncodedBody(formData).withSession("csrfToken" -> CSRF.SignedTokenProvider.generateToken)
      val controller = new PrescriptionController(messagesApi, env, mockPrescriptionDAO, mockPrescriberDAO, mockPtDAO, dataFormatter, timeZone)
      val result = controller.repeatPrescription(doseTitrationData, mockPatient, MRDrug, breakthroughDrug)(request)
      status(result) must equalTo(303)
    }
    "returns a http 200 response if the user is authorised" in new WithApplication(app2) {
      val mockUuid = UUID.randomUUID()
      val mockPrescription = new Prescription(hospitalNumber, "prescriberID", new Timestamp(new DateTime(2016,3,13,1,58).getMillis), "MRDrug", 5.00, "breakthroughDrug", 10.00)
      val mockPrescriberDAO = mock[PrescriberDAO]
      val mockPtDAO = mock[PatientDAO]
      val timeZone = DateTimeZone.forID("Europe/London")
      val mockDoseDAO = mock[DoseDAO]
      val mockCredentialsProvider = mock[CredentialsProvider]
      val dataFormatter = new PrescriptionDataFormatterImpl(mockPrescriberDAO, mockDoseDAO, timeZone)
      val mockLoginInfo = LoginInfo("email", email)
      val identity = Prescriber(mockUuid, mockLoginInfo, title, firstName, surname, email)
      val messagesApi = play.api.Play.current.injector.instanceOf[MessagesApi]
      implicit val env = FakeEnvironment[User, CookieAuthenticator](Seq(identity.loginInfo -> identity))
      val formData = (email, password)
      val mockPatient = new Patient(hospitalNumber, title, firstName, surname, dob)
      val mockPrescriptionDAO = mock[PrescriptionDAO]
      val doseTitrationData = DoseTitrationData("10","2","5","20mg","25mg","2.5mg","5mg")
      val request = FakeRequest().withAuthenticator(identity.loginInfo).withFormUrlEncodedBody(formData).withSession("csrfToken" -> CSRF.SignedTokenProvider.generateToken)
      val controller = new PrescriptionController(messagesApi, env, mockPrescriptionDAO, mockPrescriberDAO, mockPtDAO, dataFormatter, timeZone)
      val result = controller.repeatPrescription(doseTitrationData, mockPatient, MRDrug, breakthroughDrug)(request)
      status(result) must equalTo(200)
    }
    "directs an authorised user to the currentPrescription page" in new WithApplication(app2) {
      val mockUuid = UUID.randomUUID()
      val mockPrescription = new Prescription(hospitalNumber, "prescriberID", new Timestamp(new DateTime(2016,3,13,1,58).getMillis), "MRDrug", 5.00, "breakthroughDrug", 10.00)
      val mockPrescriberDAO = mock[PrescriberDAO]
      val mockPtDAO = mock[PatientDAO]
      val timeZone = DateTimeZone.forID("Europe/London")
      val mockDoseDAO = mock[DoseDAO]
      val mockCredentialsProvider = mock[CredentialsProvider]
      val dataFormatter = new PrescriptionDataFormatterImpl(mockPrescriberDAO, mockDoseDAO, timeZone)
      val mockLoginInfo = LoginInfo("email", email)
      val identity = Prescriber(mockUuid, mockLoginInfo, title, firstName, surname, email)
      val messagesApi = play.api.Play.current.injector.instanceOf[MessagesApi]
      implicit val env = FakeEnvironment[User, CookieAuthenticator](Seq(identity.loginInfo -> identity))
      val formData = (email, password)
      val mockPatient = new Patient(hospitalNumber, title, firstName, surname, dob)
      val mockPrescriptionDAO = mock[PrescriptionDAO]
      val doseTitrationData = DoseTitrationData("10","2","5","20mg","25mg","2.5mg","5mg")
      val request = FakeRequest().withAuthenticator(identity.loginInfo).withFormUrlEncodedBody(formData).withSession("csrfToken" -> CSRF.SignedTokenProvider.generateToken)
      val controller = new PrescriptionController(messagesApi, env, mockPrescriptionDAO, mockPrescriberDAO, mockPtDAO, dataFormatter, timeZone)
      val result = controller.repeatPrescription(doseTitrationData, mockPatient, MRDrug, breakthroughDrug)(request)
      contentAsString(result) must contain("PRESCRIPTION SUCCESSFUL!")
    }
  }
}
