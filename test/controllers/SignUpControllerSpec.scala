package controllers

import java.util.UUID

import com.mohiva.play.silhouette.api.LoginInfo
import com.mohiva.play.silhouette.api.repositories.AuthInfoRepository
import com.mohiva.play.silhouette.api.util.{PasswordHasher, PasswordInfo}
import com.mohiva.play.silhouette.impl.authenticators.CookieAuthenticator
import com.mohiva.play.silhouette.impl.providers.CredentialsProvider
import com.mohiva.play.silhouette.test.{FakeEnvironment, _}
import models.daos.UserDAO
import models.forms.SignUpForm.Data
import models.services.{UserService, UserServiceImpl}
import models.{Administrator, Prescriber, User}
import org.specs2.mock.Mockito
import org.specs2.mutable.Specification
import play.api.i18n.MessagesApi
import play.api.libs.json.Json
import play.api.test.Helpers._
import play.api.test.{FakeApplication, FakeRequest, WithApplication}
import play.filters.csrf.CSRF

import scala.concurrent.{ExecutionContext, Future}


class SignUpControllerSpec (implicit ec: ExecutionContext) extends Specification with Mockito{
  implicit val app: FakeApplication = FakeApplication(additionalConfiguration = inMemoryDatabase("test"))
  val userEmail = "mm@thehospital.com"
  val userTitle = "Mrs"
  val userFirstName = "Minnie"
  val userSurname = "Mouse"
  val newUserTitle = "Mr"
  val newUserFirstName = "Bill"
  val newUserSurname = "Hobb"
  val newUserEmail = "bill@thehospital.com"
  val newUserPassword = "password"

  "SignUpController.addUser" should {
    "redirect an unauthorised user" in new WithApplication(app) {
      val mockUuid = UUID.randomUUID()
      val mockLoginInfo = LoginInfo("email", userEmail)
      val identity = Prescriber(mockUuid, mockLoginInfo, userTitle, userFirstName, userSurname, userEmail)
      val messagesApi = play.api.Play.current.injector.instanceOf[MessagesApi]
      implicit val env = FakeEnvironment[User, CookieAuthenticator](Seq(identity.loginInfo -> identity))
      val formData = Json.obj(
        "title" -> newUserTitle,
        "firstName" -> newUserFirstName,
        "lastName" -> newUserSurname,
        "email" -> newUserEmail,
        "password" -> newUserPassword
      )
      val mockUserService = mock[UserService]
      val mockAuthInfRepo = mock[AuthInfoRepository]
      val mockPasswordHasher = mock[PasswordHasher]
      val request = FakeRequest().withAuthenticator(identity.loginInfo).withJsonBody(formData).withSession("csrfToken" -> CSRF.SignedTokenProvider.generateToken)
      val controller = new SignUpController(messagesApi, env, mockUserService, mockAuthInfRepo, mockPasswordHasher)
      val result = controller.addUser("prescriber")(request)
      status(result) must equalTo(303)
    }
    "returns http 400 bad request if the form data is incorrect" in new WithApplication(app) {
      val mockUuid = UUID.randomUUID()
      val mockLoginInfo = LoginInfo("email", userEmail)
      val identity = Administrator(mockUuid, mockLoginInfo, userTitle, userFirstName, userSurname, userEmail)
      val messagesApi = play.api.Play.current.injector.instanceOf[MessagesApi]
      implicit val env = FakeEnvironment[User, CookieAuthenticator](Seq(identity.loginInfo -> identity))
      val formData = (newUserEmail, newUserPassword)
      val mockUserService = mock[UserService]
      val mockAuthInfRepo = mock[AuthInfoRepository]
      val mockPasswordHasher = mock[PasswordHasher]
      val request = FakeRequest().withAuthenticator(identity.loginInfo).withFormUrlEncodedBody(formData).withSession("csrfToken" -> CSRF.SignedTokenProvider.generateToken)
      val controller = new SignUpController(messagesApi, env, mockUserService, mockAuthInfRepo, mockPasswordHasher)
      val result = controller.addUser("prescriber")(request)
      status(result) must equalTo(400)
    }
    "redirect an authorised user back to the page with a message stating the user already exists if they are already in the system" in new WithApplication(app) {
      val mockUuid = UUID.randomUUID()
      val mockLoginInfo = LoginInfo("email", userEmail)
      val identity = Administrator(mockUuid, mockLoginInfo, userTitle, userFirstName, userSurname, userEmail)
      val messagesApi = play.api.Play.current.injector.instanceOf[MessagesApi]
      implicit val env = FakeEnvironment[User, CookieAuthenticator](Seq(identity.loginInfo -> identity))
      val formData = Json.obj(
        "title" -> newUserTitle,
        "firstName" -> newUserFirstName,
        "lastName" -> newUserSurname,
        "email" -> newUserEmail,
        "password" -> newUserPassword
      )
      val mockLoginInfoForNewUser = LoginInfo(CredentialsProvider.ID, newUserEmail)
      val mockUserDAO = mock[UserDAO]
      val mockUserService = new UserServiceImpl(mockUserDAO)
      val spyUserService = spy(mockUserService)
      doReturn(true).when(spyUserService).exists(mockLoginInfoForNewUser)
      val mockAuthInfRepo = mock[AuthInfoRepository]
      val mockPasswordHasher = mock[PasswordHasher]
      val request = FakeRequest().withAuthenticator(identity.loginInfo).withJsonBody(formData).withSession("csrfToken" -> CSRF.SignedTokenProvider.generateToken)
      val controller = new SignUpController(messagesApi, env, spyUserService, mockAuthInfRepo, mockPasswordHasher)
      val result = controller.addUser("prescriber")(request)
      redirectLocation(result) must beSome.which(_ == "/adminhome")
      status(result) must equalTo(303)
    }
    "redirect an authorised user back to the page with a message stating success on adding a new user" in new WithApplication(app) {
      val mockUuid = UUID.randomUUID()
      val mockLoginInfo = LoginInfo("email", userEmail)
      val identity = Administrator(mockUuid, mockLoginInfo, userTitle, userFirstName, userSurname, userEmail)
      val messagesApi = play.api.Play.current.injector.instanceOf[MessagesApi]
      implicit val env = FakeEnvironment[User, CookieAuthenticator](Seq(identity.loginInfo -> identity))
      val formData = Json.obj(
        "title" -> newUserTitle,
        "firstName" -> newUserFirstName,
        "lastName" -> newUserSurname,
        "email" -> newUserEmail,
        "password" -> newUserPassword
      )
      val mockLoginInfoForNewUser = LoginInfo(CredentialsProvider.ID, newUserEmail)
      val mockUserDAO = mock[UserDAO]
      val mockPasswordHasher = play.api.Play.current.injector.instanceOf[PasswordHasher]
      val mockPasswordInfo = new PasswordInfo("sha", newUserPassword)
      val spyPasswordHasher = spy(mockPasswordHasher)
      doReturn(mockPasswordInfo).when(spyPasswordHasher).hash(newUserPassword)
      val mockAuthInfRepo = play.api.Play.current.injector.instanceOf[AuthInfoRepository]
      val spyAuthInfoRepo = spy(mockAuthInfRepo)
      doReturn(Future(mockPasswordInfo)).when(spyAuthInfoRepo).add(mockLoginInfoForNewUser, mockPasswordInfo)
      val mockUserService = new UserServiceImpl(mockUserDAO)
      val spyUserService = spy(mockUserService)
      val controller = new SignUpController(messagesApi, env, spyUserService, spyAuthInfoRepo, spyPasswordHasher)
      val spyController = spy(controller)
      val uuid = UUID.fromString("38400000-8cf0-11bd-b23e-10b96e4ef00d")
      val user = Prescriber(uuid, mockLoginInfoForNewUser,newUserTitle,newUserFirstName,newUserSurname,newUserEmail)
      val data = Data(newUserTitle,newUserFirstName,newUserSurname,newUserEmail,newUserPassword)
      doReturn(user).when(spyController).createUser("prescriber", data, mockLoginInfoForNewUser)
      doReturn(false).when(spyUserService).exists(mockLoginInfoForNewUser)
      doReturn(Future(user)).when(spyUserService).save(user, "prescriber")
      val request = FakeRequest().withAuthenticator(identity.loginInfo).withJsonBody(formData).withSession("csrfToken" -> CSRF.SignedTokenProvider.generateToken)
      val result = spyController.addUser("prescriber")(request)
      redirectLocation(result) must beSome.which(_ == "/adminhome")
      status(result) must equalTo(303)
    }
  }
}
