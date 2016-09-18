package functional

import java.util.UUID

import com.mohiva.play.silhouette.api.LoginInfo
import com.mohiva.play.silhouette.api.repositories.AuthInfoRepository
import com.mohiva.play.silhouette.api.util.{Clock, Credentials}
import com.mohiva.play.silhouette.impl.authenticators.CookieAuthenticator
import com.mohiva.play.silhouette.impl.providers.CredentialsProvider
import com.mohiva.play.silhouette.test.FakeEnvironment
import models.daos.UserDAO
import models.services.UserServiceImpl
import models.{Administrator, Prescriber, User}
import org.specs2.mock.Mockito
import play.api.Configuration
import play.api.i18n.MessagesApi
import play.api.libs.concurrent.Execution.Implicits._
import play.api.libs.json.Json
import play.api.test.{FakeApplication, FakeRequest, PlaySpecification, WithApplication}
import play.filters.csrf.CSRF

import scala.concurrent.Future


class CredentialsAuthControllerSpec extends PlaySpecification with Mockito{
  implicit val app: FakeApplication = FakeApplication(additionalConfiguration = inMemoryDatabase("test"))
  implicit val app2: FakeApplication = FakeApplication(additionalConfiguration = inMemoryDatabase("test"))
  implicit val app3: FakeApplication = FakeApplication(additionalConfiguration = inMemoryDatabase("test"))

  val email = "bill@thehospital.com"
  val password = "123"
  val title= "Mr"
  val firstName = "Bill"
  val surname = "Smith"

  "CredentialsAuthController.authenticate" should {
    "identify correctly and redirect an Administrator" in new WithApplication(app) {
      val mockUuid = UUID.randomUUID()
      val mockLoginInfo = LoginInfo("email", email)
      val identity = Administrator(mockUuid, mockLoginInfo, title, firstName, surname, email)
      val messagesApi = play.api.Play.current.injector.instanceOf[MessagesApi]
      val config = play.api.Play.current.injector.instanceOf[Configuration]
      implicit val env = FakeEnvironment[User, CookieAuthenticator](Seq(identity.loginInfo -> identity))
      val formData = Json.obj(
        "email" -> email,
        "password" -> password
      )
      val mockAuthInfRepo = mock[AuthInfoRepository]
      val mockCredentialsProvider = play.api.Play.current.injector.instanceOf[CredentialsProvider]
      val spyCredentialsProvider = spy(mockCredentialsProvider)
      val credentials = Credentials(email, password)
      doReturn(Future(mockLoginInfo)).when(spyCredentialsProvider).authenticate(credentials)
      val mockClock = mock[Clock]
      val mockUserDAO = mock[UserDAO]
      val mockUserService = new UserServiceImpl(mockUserDAO)
      val spyUserService = spy(mockUserService)
      doReturn(Future(Some(identity))).when(spyUserService).retrieve(mockLoginInfo)
      val request = FakeRequest().withJsonBody(formData).withSession("csrfToken" -> CSRF.SignedTokenProvider.generateToken)
      val controller = new CredentialsAuthController(messagesApi, env, spyUserService, mockAuthInfRepo, mockCredentialsProvider, config, mockClock)
      val result = controller.authenticate()(request)
      status(result) must equalTo(303)
    }
    "identify correctly and redirect a Prescriber" in new WithApplication(app2) {
      val mockUuid = UUID.randomUUID()
      val mockLoginInfo = LoginInfo("email", email)
      val identity = Prescriber(mockUuid, mockLoginInfo, title, firstName, surname, email)
      val messagesApi = play.api.Play.current.injector.instanceOf[MessagesApi]
      val config = play.api.Play.current.injector.instanceOf[Configuration]
      implicit val env = FakeEnvironment[User, CookieAuthenticator](Seq(identity.loginInfo -> identity))
      val formData = Json.obj(
        "email" -> email,
        "password" -> password
      )
      val mockAuthInfRepo = mock[AuthInfoRepository]
      val mockCredentialsProvider = play.api.Play.current.injector.instanceOf[CredentialsProvider]
      val spyCredentialsProvider = spy(mockCredentialsProvider)
      val credentials = Credentials(email, password)
      doReturn(Future(mockLoginInfo)).when(spyCredentialsProvider).authenticate(credentials)
      val mockClock = mock[Clock]
      val mockUserDAO = mock[UserDAO]
      val mockUserService = new UserServiceImpl(mockUserDAO)
      val spyUserService = spy(mockUserService)
      doReturn(Future(Some(identity))).when(spyUserService).retrieve(mockLoginInfo)
      val request = FakeRequest().withJsonBody(formData).withSession("csrfToken" -> CSRF.SignedTokenProvider.generateToken)
      val controller = new CredentialsAuthController(messagesApi, env, spyUserService, mockAuthInfRepo, mockCredentialsProvider, config, mockClock)
      val result = controller.authenticate()(request)
      status(result) must equalTo(303)
    }
    "return http bad request if the form data is of an incorrect format" in new WithApplication(app3) {
      val mockUuid = UUID.randomUUID()
      val mockLoginInfo = LoginInfo("email", email)
      val identity = Administrator(mockUuid, mockLoginInfo, title, firstName, surname, email)
      val messagesApi = play.api.Play.current.injector.instanceOf[MessagesApi]
      val config = play.api.Play.current.injector.instanceOf[Configuration]
      implicit val env = FakeEnvironment[User, CookieAuthenticator](Seq(identity.loginInfo -> identity))
      val formData = (email, password)
      val mockAuthInfRepo = mock[AuthInfoRepository]
      val mockCredentialsProvider = mock[CredentialsProvider]
      val mockClock = mock[Clock]
      val mockUserDAO = mock[UserDAO]
      val mockUserService = new UserServiceImpl(mockUserDAO)
      val spyUserService = spy(mockUserService)
      doReturn(Future(Some(identity))).when(spyUserService).retrieve(mockLoginInfo)
      val request = FakeRequest().withFormUrlEncodedBody(formData).withSession("csrfToken" -> CSRF.SignedTokenProvider.generateToken)
      val controller = new CredentialsAuthController(messagesApi, env, spyUserService, mockAuthInfRepo, mockCredentialsProvider, config, mockClock)
      val result = controller.authenticate(request)
      status(result) must equalTo(400)
    }
  }
}
