package controllers


import java.util.UUID

import com.mohiva.play.silhouette.api.LoginInfo
import com.mohiva.play.silhouette.impl.authenticators.CookieAuthenticator
import com.mohiva.play.silhouette.test.{FakeEnvironment, _}
import models.{Prescriber, Administrator, User}
import org.specs2.mutable._
import play.api.i18n.MessagesApi
import play.api.test.{WithApplication, FakeApplication, FakeRequest}
import play.api.test.Helpers._
import org.specs2.mock._
import play.filters.csrf.CSRF

import scala.concurrent.ExecutionContext

class AdministratorControllerSpec (implicit ec: ExecutionContext) extends Specification with Mockito {
  implicit val app: FakeApplication = FakeApplication(additionalConfiguration = inMemoryDatabase("test"))

  "AdministratorController.index" should {
    "return OK status if an authorized administrator requests it" in new WithApplication(app){
      val mockUuid = UUID.randomUUID()
      val identity = Administrator(mockUuid, LoginInfo("email", "bill@thehospital.com"), "Mr", "Bill", "Smith", "bill@thehospital.com")
      val messagesApi = play.api.Play.current.injector.instanceOf[MessagesApi]
      implicit val env = FakeEnvironment[User, CookieAuthenticator](Seq(identity.loginInfo -> identity))
      val request = FakeRequest().withAuthenticator(identity.loginInfo).withSession("csrfToken" -> CSRF.SignedTokenProvider.generateToken)
      val controller = new AdministratorController(messagesApi, env)
      val result = controller.index(request)
      status(result) must equalTo(OK)
    }
    "redirect the user to the sign in page if an unauthorized requests it (with a 303 redirect)" in new WithApplication(app){
      val mockUuid = UUID.randomUUID()
      val identity = Prescriber(mockUuid, LoginInfo("email", "bill@thehospital.com"), "Mr", "Bill", "Smith", "bill@thehospital.com")
      val messagesApi = play.api.Play.current.injector.instanceOf[MessagesApi]
      implicit val env = FakeEnvironment[User, CookieAuthenticator](Seq(identity.loginInfo -> identity))
      val request = FakeRequest().withAuthenticator(identity.loginInfo).withSession("csrfToken" -> CSRF.SignedTokenProvider.generateToken)
      val controller = new AdministratorController(messagesApi, env)
      val result = controller.index(request)
      status(result) must equalTo(303)
    }
    "return a view that contains text/html when requested by an authorized user" in new WithApplication(app){
      val mockUuid = UUID.randomUUID()
      val identity = Administrator(mockUuid, LoginInfo("email", "bill@thehospital.com"), "Mr", "Bill", "Smith", "bill@thehospital.com")
      val messagesApi = play.api.Play.current.injector.instanceOf[MessagesApi]
      implicit val env = FakeEnvironment[User, CookieAuthenticator](Seq(identity.loginInfo -> identity))
      val request = FakeRequest().withAuthenticator(identity.loginInfo).withSession("csrfToken" -> CSRF.SignedTokenProvider.generateToken)
      val controller = new AdministratorController(messagesApi, env)
      val result = controller.index(request)
      contentType(result) must beSome("text/html")
    }
    "return views.html.adminhome when requested by an authorized user" in new WithApplication(app){
      val mockUuid = UUID.randomUUID()
      val identity = Administrator(mockUuid, LoginInfo("email", "bill@thehospital.com"), "Mr", "Bill", "Smith", "bill@thehospital.com")
      val messagesApi = play.api.Play.current.injector.instanceOf[MessagesApi]
      implicit val env = FakeEnvironment[User, CookieAuthenticator](Seq(identity.loginInfo -> identity))
      val request = FakeRequest().withAuthenticator(identity.loginInfo).withSession("csrfToken" -> CSRF.SignedTokenProvider.generateToken)
      val controller = new AdministratorController(messagesApi, env)
      val result = controller.index(request)
      contentAsString(result) must contain("Create a new user account")
    }
  }
}
