package controllers

import
import org.specs2.mock._
import org.specs2.mutable._

class AdministratorControllerSpec extends Specification with Mockito{

  "AdministratorController.index" should {
    "return views.html.adminhome" in {
      val mockMessagesApi = mock[MessagesApi]
      val fakeEnv = mock[Environment[User, CookieAuthenticator]]
      val result = new AdministratorController(mockMessagesApi, fakeEnv).index()(FakeRequest())
      val bodyText = contentAsString(result)
      status(result) must equalTo("ok")
    }

  }
}
