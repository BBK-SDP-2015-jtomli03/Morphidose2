package controllers

import java.util.UUID

import com.mohiva.play.silhouette.api.LoginInfo
import com.mohiva.play.silhouette.impl.authenticators.CookieAuthenticator
import com.mohiva.play.silhouette.impl.providers.CredentialsProvider
import com.mohiva.play.silhouette.test.{FakeEnvironment, _}
import models.daos.{PatientDAOImpl, PatientDAO, PrescriptionDAO}
import models.{Administrator, Patient, Prescriber, User}
import org.joda.time.DateTimeZone
import org.specs2.mock.Mockito
import org.specs2.mutable.Specification
import play.api.i18n.MessagesApi
import play.api.libs.json.Json
import play.api.test.Helpers._
import play.api.test.{FakeApplication, FakeRequest, WithApplication}
import play.filters.csrf.CSRF

import scala.concurrent.{ExecutionContext, Future}


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

  "PrescriberController.index" should {
    "return OK status if an authorized prescriber requests it" in new WithApplication(app){
      val mockUuid = UUID.randomUUID()
      val identity = Prescriber(mockUuid, LoginInfo("email", email), "Mr", "Bill", "Smith", email)
      val messagesApi = play.api.Play.current.injector.instanceOf[MessagesApi]
      implicit val env = FakeEnvironment[User, CookieAuthenticator](Seq(identity.loginInfo -> identity))
      val request = FakeRequest().withAuthenticator(identity.loginInfo).withSession("csrfToken" -> CSRF.SignedTokenProvider.generateToken)
      val controller = new PrescriberController(messagesApi, env, mockPtDAO, mockCredentialsProvider, mockPrescriptionDAO, timeZone)
      val result = controller.index(request)
      status(result) must equalTo(OK)
    }
    "redirect the user to the sign in page if an unauthorized user requests it (with a 303 redirect)" in new WithApplication(app){
      val mockUuid = UUID.randomUUID()
      val identity = Administrator(mockUuid, LoginInfo("email", email), "Mr", "Bill", "Smith", email)
      val messagesApi = play.api.Play.current.injector.instanceOf[MessagesApi]
      implicit val env = FakeEnvironment[User, CookieAuthenticator](Seq(identity.loginInfo -> identity))
      val request = FakeRequest().withAuthenticator(identity.loginInfo).withSession("csrfToken" -> CSRF.SignedTokenProvider.generateToken)
      val controller = new PrescriberController(messagesApi, env, mockPtDAO, mockCredentialsProvider, mockPrescriptionDAO, timeZone)
      val result = controller.index(request)
      status(result) must equalTo(303)
    }
    "return a view that contains text/html when requested by an authorized user" in new WithApplication(app){
      val mockUuid = UUID.randomUUID()
      val identity = Prescriber(mockUuid, LoginInfo("email", email), "Mr", "Bill", "Smith", email)
      val messagesApi = play.api.Play.current.injector.instanceOf[MessagesApi]
      implicit val env = FakeEnvironment[User, CookieAuthenticator](Seq(identity.loginInfo -> identity))
      val request = FakeRequest().withAuthenticator(identity.loginInfo).withSession("csrfToken" -> CSRF.SignedTokenProvider.generateToken)
      val controller = new PrescriberController(messagesApi, env, mockPtDAO, mockCredentialsProvider, mockPrescriptionDAO, timeZone)
      val result = controller.index(request)
      contentType(result) must beSome("text/html")
    }
    "return views.html.adminhome when requested by an authorized user" in new WithApplication(app){
      val mockUuid = UUID.randomUUID()
      val identity = Prescriber(mockUuid, LoginInfo("email", email), "Mr", "Bill", "Smith", email)
      val messagesApi = play.api.Play.current.injector.instanceOf[MessagesApi]
      implicit val env = FakeEnvironment[User, CookieAuthenticator](Seq(identity.loginInfo -> identity))
      val request = FakeRequest().withAuthenticator(identity.loginInfo).withSession("csrfToken" -> CSRF.SignedTokenProvider.generateToken)
      val controller = new PrescriberController(messagesApi, env, mockPtDAO, mockCredentialsProvider, mockPrescriptionDAO, timeZone)
      val result = controller.index(request)
      contentAsString(result) must contain("Please select an option from the list on the left.")
    }
  }
  "PrescriberController.addPatientForm" should {
    "return OK status if an authorized prescriber requests it" in new WithApplication(app){
      val mockUuid = UUID.randomUUID()
      val identity = Prescriber(mockUuid, LoginInfo("email", email), "Mr", "Bill", "Smith", email)
      val messagesApi = play.api.Play.current.injector.instanceOf[MessagesApi]
      implicit val env = FakeEnvironment[User, CookieAuthenticator](Seq(identity.loginInfo -> identity))
      val request = FakeRequest().withAuthenticator(identity.loginInfo).withSession("csrfToken" -> CSRF.SignedTokenProvider.generateToken)
      val controller = new PrescriberController(messagesApi, env, mockPtDAO, mockCredentialsProvider, mockPrescriptionDAO, timeZone)
      val result = controller.addPatientForm(request)
      status(result) must equalTo(OK)
    }
    "redirect the user to the sign in page if an unauthorized user requests it (with a 303 redirect)" in new WithApplication(app){
      val mockUuid = UUID.randomUUID()
      val identity = Administrator(mockUuid, LoginInfo("email", email), "Mr", "Bill", "Smith", email)
      val messagesApi = play.api.Play.current.injector.instanceOf[MessagesApi]
      implicit val env = FakeEnvironment[User, CookieAuthenticator](Seq(identity.loginInfo -> identity))
      val request = FakeRequest().withAuthenticator(identity.loginInfo).withSession("csrfToken" -> CSRF.SignedTokenProvider.generateToken)
      val controller = new PrescriberController(messagesApi, env, mockPtDAO, mockCredentialsProvider, mockPrescriptionDAO, timeZone)
      val result = controller.addPatientForm(request)
      status(result) must equalTo(303)
    }
    "return a view that contains text/html when requested by an authorized user" in new WithApplication(app){
      val mockUuid = UUID.randomUUID()
      val identity = Prescriber(mockUuid, LoginInfo("email", email), "Mr", "Bill", "Smith", email)
      val messagesApi = play.api.Play.current.injector.instanceOf[MessagesApi]
      implicit val env = FakeEnvironment[User, CookieAuthenticator](Seq(identity.loginInfo -> identity))
      val request = FakeRequest().withAuthenticator(identity.loginInfo).withSession("csrfToken" -> CSRF.SignedTokenProvider.generateToken)
      val controller = new PrescriberController(messagesApi, env, mockPtDAO, mockCredentialsProvider, mockPrescriptionDAO, timeZone)
      val result = controller.addPatientForm(request)
      contentType(result) must beSome("text/html")
    }
    "return views.html.adminhome when requested by an authorized user" in new WithApplication(app){
      val mockUuid = UUID.randomUUID()
      val identity = Prescriber(mockUuid, LoginInfo("email", email), "Mr", "Bill", "Smith", email)
      val messagesApi = play.api.Play.current.injector.instanceOf[MessagesApi]
      implicit val env = FakeEnvironment[User, CookieAuthenticator](Seq(identity.loginInfo -> identity))
      val request = FakeRequest().withAuthenticator(identity.loginInfo).withSession("csrfToken" -> CSRF.SignedTokenProvider.generateToken)
      val controller = new PrescriberController(messagesApi, env, mockPtDAO, mockCredentialsProvider, mockPrescriptionDAO, timeZone)
      val result = controller.addPatientForm(request)
      contentAsString(result) must contain("Add A New Patient")
    }
  }
  "PrescriberController.editPatientForm" should {
    "return OK status if an authorized prescriber requests it" in new WithApplication(app){
      val mockUuid = UUID.randomUUID()
      val identity = Prescriber(mockUuid, LoginInfo("email", email), "Mr", "Bill", "Smith", email)
      val messagesApi = play.api.Play.current.injector.instanceOf[MessagesApi]
      implicit val env = FakeEnvironment[User, CookieAuthenticator](Seq(identity.loginInfo -> identity))
      val mockPatient = new Patient(hospitalNumber, title, firstName, surname, dob)
      val request = FakeRequest().withAuthenticator(identity.loginInfo).withSession("csrfToken" -> CSRF.SignedTokenProvider.generateToken)
      val controller = new PrescriberController(messagesApi, env, mockPtDAO, mockCredentialsProvider, mockPrescriptionDAO, timeZone)
      val result = controller.editPatientForm(mockPatient)(request)
      status(result) must equalTo(OK)
    }
    "redirect the user to the sign in page if an unauthorized user requests it (with a 303 redirect)" in new WithApplication(app){
      val mockUuid = UUID.randomUUID()
      val identity = Administrator(mockUuid, LoginInfo("email", email), "Mr", "Bill", "Smith", email)
      val messagesApi = play.api.Play.current.injector.instanceOf[MessagesApi]
      implicit val env = FakeEnvironment[User, CookieAuthenticator](Seq(identity.loginInfo -> identity))
      val mockPatient = new Patient(hospitalNumber, title, firstName, surname, dob)
      val request = FakeRequest().withAuthenticator(identity.loginInfo).withSession("csrfToken" -> CSRF.SignedTokenProvider.generateToken)
      val controller = new PrescriberController(messagesApi, env, mockPtDAO, mockCredentialsProvider, mockPrescriptionDAO, timeZone)
      val result = controller.editPatientForm(mockPatient)(request)
      status(result) must equalTo(303)
    }
    "return a view that contains text/html when requested by an authorized user" in new WithApplication(app){
      val mockUuid = UUID.randomUUID()
      val identity = Prescriber(mockUuid, LoginInfo("email", email), "Mr", "Bill", "Smith", email)
      val messagesApi = play.api.Play.current.injector.instanceOf[MessagesApi]
      implicit val env = FakeEnvironment[User, CookieAuthenticator](Seq(identity.loginInfo -> identity))
      val mockPatient = new Patient(hospitalNumber, title, firstName, surname, dob)
      val request = FakeRequest().withAuthenticator(identity.loginInfo).withSession("csrfToken" -> CSRF.SignedTokenProvider.generateToken)
      val controller = new PrescriberController(messagesApi, env, mockPtDAO, mockCredentialsProvider, mockPrescriptionDAO, timeZone)
      val result = controller.editPatientForm(mockPatient)(request)
      contentType(result) must beSome("text/html")
    }
    "return views.html.adminhome when requested by an authorized user" in new WithApplication(app){
      val mockUuid = UUID.randomUUID()
      val identity = Prescriber(mockUuid, LoginInfo("email", email), "Mr", "Bill", "Smith", email)
      val messagesApi = play.api.Play.current.injector.instanceOf[MessagesApi]
      implicit val env = FakeEnvironment[User, CookieAuthenticator](Seq(identity.loginInfo -> identity))
      val mockPatient = new Patient(hospitalNumber, title, firstName, surname, dob)
      val request = FakeRequest().withAuthenticator(identity.loginInfo).withSession("csrfToken" -> CSRF.SignedTokenProvider.generateToken)
      val controller = new PrescriberController(messagesApi, env, mockPtDAO, mockCredentialsProvider, mockPrescriptionDAO, timeZone)
      val result = controller.editPatientForm(mockPatient)(request)
      contentAsString(result) must contain("Edit Patient Details")
    }
  }
  "PrescriberController.addPatient" should {
    "return http bad request (400) if the form data is of an incorrect format" in new WithApplication(app) {
      val mockUuid = UUID.randomUUID()
      val mockLoginInfo = LoginInfo("email", email)
      val identity = Prescriber(mockUuid, mockLoginInfo, title, firstName, surname, email)
      val messagesApi = play.api.Play.current.injector.instanceOf[MessagesApi]
      implicit val env = FakeEnvironment[User, CookieAuthenticator](Seq(identity.loginInfo -> identity))
      val formData = (email, password)
      val request = FakeRequest().withAuthenticator(identity.loginInfo).withFormUrlEncodedBody(formData).withSession("csrfToken" -> CSRF.SignedTokenProvider.generateToken)
      val controller = new PrescriberController(messagesApi, env, mockPtDAO, mockCredentialsProvider, mockPrescriptionDAO, timeZone)
      val result = controller.addPatient()(request)
      status(result) must equalTo(400)
    }
    "return http redirect (303) if the user is unauthorised to access this page" in new WithApplication(app) {
      val mockUuid = UUID.randomUUID()
      val mockLoginInfo = LoginInfo("email", email)
      val identity = Administrator(mockUuid, mockLoginInfo, title, firstName, surname, email)
      val messagesApi = play.api.Play.current.injector.instanceOf[MessagesApi]
      implicit val env = FakeEnvironment[User, CookieAuthenticator](Seq(identity.loginInfo -> identity))
      val formData = (email, password)
      val request = FakeRequest().withAuthenticator(identity.loginInfo).withFormUrlEncodedBody(formData).withSession("csrfToken" -> CSRF.SignedTokenProvider.generateToken)
      val controller = new PrescriberController(messagesApi, env, mockPtDAO, mockCredentialsProvider, mockPrescriptionDAO, timeZone)
      val result = controller.addPatient()(request)
      status(result) must equalTo(303)
    }
    "redirect the user if the patient already exists" in new WithApplication(app) {
      val mockUuid = UUID.randomUUID()
      val mockLoginInfo = LoginInfo("email", email)
      val identity = Prescriber(mockUuid, mockLoginInfo, title, firstName, surname, email)
      val messagesApi = play.api.Play.current.injector.instanceOf[MessagesApi]
      implicit val env = FakeEnvironment[User, CookieAuthenticator](Seq(identity.loginInfo -> identity))
      val formData = Json.obj(
        "hospitalNumber" -> hospitalNumber,
        "title" -> title,
        "firstName" -> firstName,
        "surname" -> surname,
        "dobDayOfMonth" -> dobDayOfMonth,
        "dobMonth" -> dobMonth,
        "dobYear" -> dobYear
      )
      val mockPatient = new Patient(hospitalNumber, title, firstName, surname, dob)
      val mockPatientDAO = new PatientDAOImpl()
      val spyPatientDAO = spy(mockPatientDAO)
      doReturn(Future(Some(mockPatient))).when(spyPatientDAO).findPatient(hospitalNumber)
      val request = FakeRequest().withAuthenticator(identity.loginInfo).withJsonBody(formData).withSession("csrfToken" -> CSRF.SignedTokenProvider.generateToken)
      val controller = new PrescriberController(messagesApi, env, spyPatientDAO, mockCredentialsProvider, mockPrescriptionDAO, timeZone)
      val result = controller.addPatient()(request)
      status(result) must equalTo(303)
    }
    "redirects the user back to the addPatientForm with an error message if the patient already exists" in new WithApplication(app) {
      val mockUuid = UUID.randomUUID()
      val mockLoginInfo = LoginInfo("email", email)
      val identity = Prescriber(mockUuid, mockLoginInfo, title, firstName, surname, email)
      val messagesApi = play.api.Play.current.injector.instanceOf[MessagesApi]
      implicit val env = FakeEnvironment[User, CookieAuthenticator](Seq(identity.loginInfo -> identity))
      val formData = Json.obj(
        "hospitalNumber" -> hospitalNumber,
        "title" -> title,
        "firstName" -> firstName,
        "surname" -> surname,
        "dobDayOfMonth" -> dobDayOfMonth,
        "dobMonth" -> dobMonth,
        "dobYear" -> dobYear
      )
      val mockPatient = new Patient(hospitalNumber, title, firstName, surname, dob)
      val mockPatientDAO = new PatientDAOImpl()
      val spyPatientDAO = spy(mockPatientDAO)
      doReturn(Future(Some(mockPatient))).when(spyPatientDAO).findPatient(hospitalNumber)
      val request = FakeRequest().withAuthenticator(identity.loginInfo).withJsonBody(formData).withSession("csrfToken" -> CSRF.SignedTokenProvider.generateToken)
      val controller = new PrescriberController(messagesApi, env, spyPatientDAO, mockCredentialsProvider, mockPrescriptionDAO, timeZone)
      val result = controller.addPatient()(request)
      redirectLocation(result) must beSome.which(_ == "/form/patient/new")
    }
    "return a hhtp 200 repsonse if the patient doesn't exist" in new WithApplication(app) {
      val mockUuid = UUID.randomUUID()
      val mockLoginInfo = LoginInfo("email", email)
      val identity = Prescriber(mockUuid, mockLoginInfo, title, firstName, surname, email)
      val messagesApi = play.api.Play.current.injector.instanceOf[MessagesApi]
      implicit val env = FakeEnvironment[User, CookieAuthenticator](Seq(identity.loginInfo -> identity))
      val formData = Json.obj(
        "hospitalNumber" -> hospitalNumber,
        "title" -> title,
        "firstName" -> firstName,
        "surname" -> surname,
        "dobDayOfMonth" -> dobDayOfMonth,
        "dobMonth" -> dobMonth,
        "dobYear" -> dobYear
      )
      val mockPatient = new Patient(hospitalNumber, title, firstName, surname, dob)
      val mockPatientDAO = new PatientDAOImpl()
      val spyPatientDAO = spy(mockPatientDAO)
      doReturn(Future(None)).when(spyPatientDAO).findPatient(hospitalNumber)
      val request = FakeRequest().withAuthenticator(identity.loginInfo).withJsonBody(formData).withSession("csrfToken" -> CSRF.SignedTokenProvider.generateToken)
      val controller = new PrescriberController(messagesApi, env, spyPatientDAO, mockCredentialsProvider, mockPrescriptionDAO, timeZone)
      val result = controller.addPatient()(request)
      status(result) must equalTo(200)
    }
    "forwards the user to the prescription page if the user doesn't exist" in new WithApplication(app) {
      val mockUuid = UUID.randomUUID()
      val mockLoginInfo = LoginInfo("email", email)
      val identity = Prescriber(mockUuid, mockLoginInfo, title, firstName, surname, email)
      val messagesApi = play.api.Play.current.injector.instanceOf[MessagesApi]
      implicit val env = FakeEnvironment[User, CookieAuthenticator](Seq(identity.loginInfo -> identity))
      val formData = Json.obj(
        "hospitalNumber" -> hospitalNumber,
        "title" -> title,
        "firstName" -> firstName,
        "surname" -> surname,
        "dobDayOfMonth" -> dobDayOfMonth,
        "dobMonth" -> dobMonth,
        "dobYear" -> dobYear
      )
      val mockPatient = new Patient(hospitalNumber, title, firstName, surname, dob)
      val mockPatientDAO = new PatientDAOImpl()
      val spyPatientDAO = spy(mockPatientDAO)
      doReturn(Future(None)).when(spyPatientDAO).findPatient(hospitalNumber)
      val request = FakeRequest().withAuthenticator(identity.loginInfo).withJsonBody(formData).withSession("csrfToken" -> CSRF.SignedTokenProvider.generateToken)
      val controller = new PrescriberController(messagesApi, env, spyPatientDAO, mockCredentialsProvider, mockPrescriptionDAO, timeZone)
      val result = controller.addPatient()(request)
      contentAsString(result) must contain("PRESCRIBE INITIAL DOSES")
    }
    "forwards the user to the prescription page with the new patients details if the user didn't previously exist" in new WithApplication(app) {
      val mockUuid = UUID.randomUUID()
      val mockLoginInfo = LoginInfo("email", email)
      val identity = Prescriber(mockUuid, mockLoginInfo, title, firstName, surname, email)
      val messagesApi = play.api.Play.current.injector.instanceOf[MessagesApi]
      implicit val env = FakeEnvironment[User, CookieAuthenticator](Seq(identity.loginInfo -> identity))
      val formData = Json.obj(
        "hospitalNumber" -> hospitalNumber,
        "title" -> title,
        "firstName" -> firstName,
        "surname" -> surname,
        "dobDayOfMonth" -> dobDayOfMonth,
        "dobMonth" -> dobMonth,
        "dobYear" -> dobYear
      )
      val mockPatient = new Patient(hospitalNumber, title, firstName, surname, dob)
      val mockPatientDAO = new PatientDAOImpl()
      val spyPatientDAO = spy(mockPatientDAO)
      doReturn(Future(None)).when(spyPatientDAO).findPatient(hospitalNumber)
      val request = FakeRequest().withAuthenticator(identity.loginInfo).withJsonBody(formData).withSession("csrfToken" -> CSRF.SignedTokenProvider.generateToken)
      val controller = new PrescriberController(messagesApi, env, spyPatientDAO, mockCredentialsProvider, mockPrescriptionDAO, timeZone)
      val result = controller.addPatient()(request)
      contentAsString(result) must contain(mockPatient.hospitalNumber)
    }
  }
  "PrescriberController.editPatient" should {
    "return http redirect (303) if the user is unauthorised to access this page" in new WithApplication(app) {
      val mockUuid = UUID.randomUUID()
      val mockLoginInfo = LoginInfo("email", email)
      val identity = Administrator(mockUuid, mockLoginInfo, title, firstName, surname, email)
      val messagesApi = play.api.Play.current.injector.instanceOf[MessagesApi]
      implicit val env = FakeEnvironment[User, CookieAuthenticator](Seq(identity.loginInfo -> identity))
      val formData = (email, password)
      val mockPatient = new Patient(hospitalNumber, title, firstName, surname, dob)
      val request = FakeRequest().withAuthenticator(identity.loginInfo).withFormUrlEncodedBody(formData).withSession("csrfToken" -> CSRF.SignedTokenProvider.generateToken)
      val controller = new PrescriberController(messagesApi, env, mockPtDAO, mockCredentialsProvider, mockPrescriptionDAO, timeZone)
      val result = controller.editPatient(mockPatient)(request)
      status(result) must equalTo(303)
    }
    "return http bad request (400) if the form data is of an incorrect format" in new WithApplication(app) {
      val mockUuid = UUID.randomUUID()
      val mockLoginInfo = LoginInfo("email", email)
      val identity = Prescriber(mockUuid, mockLoginInfo, title, firstName, surname, email)
      val messagesApi = play.api.Play.current.injector.instanceOf[MessagesApi]
      implicit val env = FakeEnvironment[User, CookieAuthenticator](Seq(identity.loginInfo -> identity))
      val formData = (email, password)
      val mockPatient = new Patient(hospitalNumber, title, firstName, surname, dob)
      val request = FakeRequest().withAuthenticator(identity.loginInfo).withFormUrlEncodedBody(formData).withSession("csrfToken" -> CSRF.SignedTokenProvider.generateToken)
      val controller = new PrescriberController(messagesApi, env, mockPtDAO, mockCredentialsProvider, mockPrescriptionDAO, timeZone)
      val result = controller.editPatient(mockPatient)(request)
      status(result) must equalTo(400)
    }
    "returns an http 303 response if the form data is in the correct format and has been successfully edited" in new WithApplication(app) {
      val mockUuid = UUID.randomUUID()
      val mockLoginInfo = LoginInfo("email", email)
      val identity = Prescriber(mockUuid, mockLoginInfo, title, firstName, surname, email)
      val messagesApi = play.api.Play.current.injector.instanceOf[MessagesApi]
      implicit val env = FakeEnvironment[User, CookieAuthenticator](Seq(identity.loginInfo -> identity))
      val formData = Json.obj(
        "hospitalNumber" -> hospitalNumber,
        "title" -> title,
        "firstName" -> firstName,
        "surname" -> surname,
        "dobDayOfMonth" -> dobDayOfMonth,
        "dobMonth" -> dobMonth,
        "dobYear" -> dobYear
      )
      val mockPatient = new Patient(hospitalNumber, title, firstName, surname, dob)
      val mockPatientDAO = mock[PatientDAO]
      val request = FakeRequest().withAuthenticator(identity.loginInfo).withJsonBody(formData).withSession("csrfToken" -> CSRF.SignedTokenProvider.generateToken)
      val controller = new PrescriberController(messagesApi, env, mockPatientDAO, mockCredentialsProvider, mockPrescriptionDAO, timeZone)
      val result = controller.editPatient(mockPatient)(request)
      status(result) must equalTo(303)
    }
    "redirects the user back to the editPtDetails if the form data is in the correct format and has been successfully edited" in new WithApplication(app) {
      val mockUuid = UUID.randomUUID()
      val mockLoginInfo = LoginInfo("email", email)
      val identity = Prescriber(mockUuid, mockLoginInfo, title, firstName, surname, email)
      val messagesApi = play.api.Play.current.injector.instanceOf[MessagesApi]
      implicit val env = FakeEnvironment[User, CookieAuthenticator](Seq(identity.loginInfo -> identity))
      val formData = Json.obj(
        "hospitalNumber" -> hospitalNumber,
        "title" -> title,
        "firstName" -> firstName,
        "surname" -> surname,
        "dobDayOfMonth" -> dobDayOfMonth,
        "dobMonth" -> dobMonth,
        "dobYear" -> dobYear
      )
      val mockPatient = new Patient(hospitalNumber, title, firstName, surname, dob)
      val mockPatientDAO = mock[PatientDAO]
      val request = FakeRequest().withAuthenticator(identity.loginInfo).withJsonBody(formData).withSession("csrfToken" -> CSRF.SignedTokenProvider.generateToken)
      val controller = new PrescriberController(messagesApi, env, mockPatientDAO, mockCredentialsProvider, mockPrescriptionDAO, timeZone)
      val result = controller.editPatient(mockPatient)(request)
      redirectLocation(result) must beSome.which(_ == "/form/patient/edit?patient.hospitalNumber=123&patient.title=Mrs&patient.firstName=Cruella&patient.surname=Daville&patient.dob=10-10-1999")
    }
  }
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
