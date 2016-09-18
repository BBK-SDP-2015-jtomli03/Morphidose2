package functional

import java.util.UUID

import com.mohiva.play.silhouette.api.LoginInfo
import com.mohiva.play.silhouette.impl.authenticators.CookieAuthenticator
import com.mohiva.play.silhouette.test._
import models.{Administrator, Prescriber, User}
import org.specs2.mock.Mockito
import play.api.i18n.MessagesApi
import play.api.libs.concurrent.Execution.Implicits._
import play.api.test.{FakeApplication, FakeRequest, PlaySpecification, WithApplication}
import play.filters.csrf.CSRF

class ApplicationControllerSpec extends PlaySpecification with Mockito {
  implicit val app: FakeApplication = FakeApplication(additionalConfiguration = inMemoryDatabase("test"))

  "ApplicationController.login" should {
    "redirect an authorised user to the relevant page" in new WithApplication(app) {
      val mockUuid = UUID.randomUUID()
      val identity = Administrator(mockUuid, LoginInfo("email", "bill@thehospital.com"), "Mr", "Bill", "Smith", "bill@thehospital.com")
      val messagesApi = play.api.Play.current.injector.instanceOf[MessagesApi]
      implicit val env = FakeEnvironment[User, CookieAuthenticator](Seq(identity.loginInfo -> identity))
      val request = FakeRequest().withAuthenticator(identity.loginInfo).withSession("csrfToken" -> CSRF.SignedTokenProvider.generateToken)
      val controller = new ApplicationController(messagesApi, env)
      val result = controller.login(request)
      status(result) must equalTo(303)
    }
    "redirect an administrator to the view adminhome" in new WithApplication(app) {
      val mockUuid = UUID.randomUUID()
      val identity = Administrator(mockUuid, LoginInfo("email", "bill@thehospital.com"), "Mr", "Bill", "Smith", "bill@thehospital.com")
      val messagesApi = play.api.Play.current.injector.instanceOf[MessagesApi]
      implicit val env = FakeEnvironment[User, CookieAuthenticator](Seq(identity.loginInfo -> identity))
      val request = FakeRequest().withAuthenticator(identity.loginInfo).withSession("csrfToken" -> CSRF.SignedTokenProvider.generateToken)
      val controller = new ApplicationController(messagesApi, env)
      val result = controller.login(request)
      redirectLocation(result) must beSome.which(_ == "/adminhome")
    }
    "redirect a prescriber to the view prescriberHome" in new WithApplication(app) {
      val mockUuid = UUID.randomUUID()
      val identity = Prescriber(mockUuid, LoginInfo("email", "bill@thehospital.com"), "Mr", "Bill", "Smith", "bill@thehospital.com")
      val messagesApi = play.api.Play.current.injector.instanceOf[MessagesApi]
      implicit val env = FakeEnvironment[User, CookieAuthenticator](Seq(identity.loginInfo -> identity))
      val request = FakeRequest().withAuthenticator(identity.loginInfo).withSession("csrfToken" -> CSRF.SignedTokenProvider.generateToken)
      val controller = new ApplicationController(messagesApi, env)
      val result = controller.login(request)
      redirectLocation(result) must beSome.which(_ == "/home")
    }
    "redirect an unregistered user to the view login" in new WithApplication(app) {
      val mockUuid = UUID.randomUUID()
      val identity = Prescriber(mockUuid, LoginInfo("email", "bill@thehospital.com"), "Mr", "Bill", "Smith", "bill@thehospital.com")
      val fakeIdentity = Prescriber(UUID.randomUUID(), LoginInfo("email", "ben@thehospital.com"), "Mr", "Ben", "Smith", "ben@thehospital.com")
      val messagesApi = play.api.Play.current.injector.instanceOf[MessagesApi]
      implicit val env = FakeEnvironment[User, CookieAuthenticator](Seq(identity.loginInfo -> identity))
      val request = FakeRequest().withAuthenticator(fakeIdentity.loginInfo).withSession("csrfToken" -> CSRF.SignedTokenProvider.generateToken)
      val controller = new ApplicationController(messagesApi, env)
      val result = controller.login(request)
      contentAsString(result) must contain("Please login")
    }
  }

  "ApplicationController.signout" should {
    "redirect a the user to the login page" in new WithApplication(app) {
      val mockUuid = UUID.randomUUID()
      val identity = Administrator(mockUuid, LoginInfo("email", "bill@thehospital.com"), "Mr", "Bill", "Smith", "bill@thehospital.com")
      val messagesApi = play.api.Play.current.injector.instanceOf[MessagesApi]
      implicit val env = FakeEnvironment[User, CookieAuthenticator](Seq(identity.loginInfo -> identity))
      val request = FakeRequest().withAuthenticator(identity.loginInfo).withSession("csrfToken" -> CSRF.SignedTokenProvider.generateToken)
      val controller = new ApplicationController(messagesApi, env)
      val result = controller.signOut(request)
      redirectLocation(result) must beSome.which(_ == "/")
    }
  }
}