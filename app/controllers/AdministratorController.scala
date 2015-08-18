package controllers

import javax.inject.Inject

import com.mohiva.play.silhouette.api.{Environment, Silhouette}
import com.mohiva.play.silhouette.impl.authenticators.CookieAuthenticator
import models.User
import models.forms.{AddPrescriberForm, SignInForm}
import models.utils.DropdownUtils
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
   * Handles the index action.
   *
   * @return The result to display.
   */
  def index = SecuredAction.async { implicit request =>
    Future.successful(Ok(views.html.adminhome(AddPrescriberForm.prescriberForm, request.identity, DropdownUtils.getTitles)))
  }

}
