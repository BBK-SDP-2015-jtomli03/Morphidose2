package controllers

import javax.inject.Inject

import com.mohiva.play.silhouette.api.{Environment, Silhouette}
import com.mohiva.play.silhouette.impl.authenticators.CookieAuthenticator
import models.User
import models.forms.{SignInForm, SignUpForm}
import models.utils.{AuthorizedWithUserType, DropdownUtils}
import play.api.i18n.{I18nSupport, MessagesApi}
import play.api.mvc.Controller

import scala.concurrent.Future


class AdministratorController @Inject()(val messagesApi: MessagesApi, val env: Environment[User, CookieAuthenticator])
                                    extends Silhouette[User, CookieAuthenticator] with Controller with I18nSupport {

  /**
   * The adminLogin action.
   * If a user is already logged in then they will be redirected to the adminhome page
   * otherwise they will be directed to the admin sign in page.
   */
  def adminLogin = UserAwareAction.async { implicit request =>
    request.identity match {
      case Some(user) => Future.successful(Redirect(routes.ApplicationController.index()))
      case None => Future.successful(Ok(views.html.administrator(SignInForm.form, "active", "", "active in", "fade")))
    }
  }

  /**
   * Handles the index action for the administrator home page.
   * Only authenticated administrators can access this page, otherwise the user is redirected to the sign in page.
   *
   * @return The result to display.
   */
  def index = SecuredAction(AuthorizedWithUserType("models.Administrator")).async { implicit request =>
    Future.successful(Ok(views.html.adminhome(SignUpForm.form, request.identity, DropdownUtils.getTitles, "active", "", "active in", "fade")))
  }

}
