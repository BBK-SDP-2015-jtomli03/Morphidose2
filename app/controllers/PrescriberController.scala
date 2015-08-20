package controllers

import javax.inject.Inject

import com.mohiva.play.silhouette.api.{Environment, Silhouette}
import com.mohiva.play.silhouette.impl.authenticators.CookieAuthenticator
import models.User
import models.forms.AddPatientForm
import models.utils.{AuthorizedWithUserType, DropdownUtils}
import play.api.i18n.{I18nSupport, MessagesApi}
import play.api.mvc.Controller

import scala.concurrent.Future


class PrescriberController @Inject()(val messagesApi: MessagesApi, val env: Environment[User, CookieAuthenticator])
  extends Silhouette[User, CookieAuthenticator] with Controller with I18nSupport {

  /**
   * Handles the index action for the prescriber home page.
   * Only authenticated prescribers can access this page, otherwise the user is redirected to the sign in page.
   *
   * @return The result to display.
   */
  def index = SecuredAction(AuthorizedWithUserType("models.Prescriber")).async { implicit request =>
    Future.successful(Ok(views.html.addPatient(AddPatientForm.form, request.identity, DropdownUtils.getTitles, DropdownUtils.getDaysOfMonth, DropdownUtils.getMonths, DropdownUtils.getYears)))
  }

}
