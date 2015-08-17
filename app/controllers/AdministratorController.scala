package controllers

import javax.inject.Inject

import com.mohiva.play.silhouette.api.{Environment, Silhouette}
import com.mohiva.play.silhouette.impl.authenticators.CookieAuthenticator
import models.User
import models.forms.SignInForm
import play.api.i18n.MessagesApi

import scala.concurrent.Future


class AdministratorController @Inject()(val messagesApi: MessagesApi,
                                        val env: Environment[User, CookieAuthenticator])
  extends Silhouette[User, CookieAuthenticator] {

  /**
   * The index action.
   */
  def index = SecuredAction.async { implicit request =>
    Future.successful(Ok(views.html.administrator(SignInForm.form, "active", "", "active in", "fade")))
  }

}
