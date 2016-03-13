package controllers

import java.util.UUID

import com.mohiva.play.silhouette.api.LoginInfo
import com.mohiva.play.silhouette.api.repositories.AuthInfoRepository
import com.mohiva.play.silhouette.api.util.Clock
import com.mohiva.play.silhouette.impl.authenticators.CookieAuthenticator
import com.mohiva.play.silhouette.impl.providers.CredentialsProvider
import com.mohiva.play.silhouette.test.FakeEnvironment
import models.daos.UserDAO
import models.services.UserServiceImpl
import models.{Administrator, User}
import org.specs2.mock.Mockito
import play.api.Configuration
import play.api.i18n.MessagesApi
import play.api.test.{FakeApplication, FakeRequest, PlaySpecification, WithApplication}
import play.filters.csrf.CSRF

import scala.concurrent.Future
import play.api.libs.concurrent.Execution.Implicits._


class CredentialsAuthControllerSpec extends PlaySpecification with Mockito{
  implicit val app: FakeApplication = FakeApplication(additionalConfiguration = inMemoryDatabase("test"))

  "CredentialsAuthController.authenticate" should {
    "redirect an authorised user to the relevant page" in new WithApplication(app) {
      val mockUuid = UUID.randomUUID()
      val mockLoginInfo = LoginInfo("email", "bill@thehospital.com")
      val identity = Administrator(mockUuid, mockLoginInfo, "Mr", "Bill", "Smith", "bill@thehospital.com")
      val messagesApi = play.api.Play.current.injector.instanceOf[MessagesApi]
      val config = play.api.Play.current.injector.instanceOf[Configuration]
      implicit val env = FakeEnvironment[User, CookieAuthenticator](Seq(identity.loginInfo -> identity))


      val formData = ("bill@thehospital.com", "123")

//      Result r = callAction(routes.ref.UserController.add(), fakeRequest()
//        .withFormUrlEncodedBody(Form.form(User.class).bind(userData).data()));
      val mockAuthInfRepo = mock[AuthInfoRepository]
      val mockCredentialsProvider = mock[CredentialsProvider]
      val mockClock = mock[Clock]
      val mockUserDAO = mock[UserDAO]
      val mockUserService = new UserServiceImpl(mockUserDAO)
      val spyUserService = spy(mockUserService)
      spyUserService.retrieve(mockLoginInfo) returns Future(Some(identity))

      val request = FakeRequest().withFormUrlEncodedBody(formData).withSession("csrfToken" -> CSRF.SignedTokenProvider.generateToken)
      val controller = new CredentialsAuthController(messagesApi, env, spyUserService, mockAuthInfRepo, mockCredentialsProvider, config, mockClock)
      val result = controller.authenticate(request)
      status(result) must equalTo(303)
    }
    "return http bad request if the form data is of an incorrect format" in new WithApplication(app) {
      val mockUuid = UUID.randomUUID()
      val mockLoginInfo = LoginInfo("email", "bill@thehospital.com")
      val identity = Administrator(mockUuid, mockLoginInfo, "Mr", "Bill", "Smith", "bill@thehospital.com")
      val messagesApi = play.api.Play.current.injector.instanceOf[MessagesApi]
      val config = play.api.Play.current.injector.instanceOf[Configuration]
      implicit val env = FakeEnvironment[User, CookieAuthenticator](Seq(identity.loginInfo -> identity))
      val formData = ("bill@thehospital.com", "123")
      val mockAuthInfRepo = mock[AuthInfoRepository]
      val mockCredentialsProvider = mock[CredentialsProvider]
      val mockClock = mock[Clock]
      val mockUserDAO = mock[UserDAO]
      val mockUserService = new UserServiceImpl(mockUserDAO)
      val spyUserService = spy(mockUserService)
      spyUserService.retrieve(mockLoginInfo) returns Future(Some(identity))
      val request = FakeRequest().withFormUrlEncodedBody(formData).withSession("csrfToken" -> CSRF.SignedTokenProvider.generateToken)
      val controller = new CredentialsAuthController(messagesApi, env, spyUserService, mockAuthInfRepo, mockCredentialsProvider, config, mockClock)
      val result = controller.authenticate(request)
      status(result) must equalTo(400)
    }
  }
}
