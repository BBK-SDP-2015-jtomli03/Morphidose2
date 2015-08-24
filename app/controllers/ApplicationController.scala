package controllers

import javax.inject.Inject

import com.mohiva.play.silhouette.api.{ Environment, LogoutEvent, Silhouette }
import com.mohiva.play.silhouette.impl.authenticators.CookieAuthenticator
import models.forms._
import models.User
import play.api.i18n.MessagesApi

import scala.concurrent.Future

/**
 * The basic application controller.
 *
 * @param messagesApi The Play messages API.
 * @param env The Silhouette environment.
 */
class ApplicationController @Inject() (
  val messagesApi: MessagesApi,
  val env: Environment[User, CookieAuthenticator])
  extends Silhouette[User, CookieAuthenticator] {

  /**
   * Handles the index action.
   *
   * @return The result to display.
   */
  def index = SecuredAction.async { implicit request =>
    Future.successful(Ok(views.html.home(request.identity)))
  }

  /**
   * Handles the login action. If a user is already logged in then they will be redirected to their relevant home page,
   * otherwise they will be directed to the login page.
   *
   * @return The result to display.
   */
  def login = UserAwareAction.async { implicit request =>
    request.identity match {
      case Some(user) => user.getClass.getTypeName match {
        case "models.Administrator" => Future.successful(Redirect(routes.AdministratorController.index()))
        case "models.Prescriber" => Future.successful(Redirect(routes.PrescriberController.index()))
      }
      case None => Future.successful(Ok(views.html.login(SignInForm.form, "active", "", "active in", "fade")))
    }
  }

  /**
   * Handles the Log out action.
   *
   * @return The result to display.
   */
  def signOut = SecuredAction.async { implicit request =>
    val result = Redirect(routes.ApplicationController.login())
    env.eventBus.publish(LogoutEvent(request.identity, request, request2Messages))
    env.authenticatorService.discard(request.authenticator, result)
  }
}
