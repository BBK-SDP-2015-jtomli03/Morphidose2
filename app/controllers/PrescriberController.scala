package controllers

import javax.inject.Inject

import com.mohiva.play.silhouette.api.{Environment, Silhouette}
import com.mohiva.play.silhouette.impl.authenticators.CookieAuthenticator
import com.mohiva.play.silhouette.impl.providers.CredentialsProvider
import models.daos.{PatientDAO, PrescriptionDAO}
import models.forms.{AddPatientForm, PrescriptionForm}
import models.utils.{AuthorizedWithUserType, DropdownUtils}
import models.{Patient, User}
import org.joda.time.DateTimeZone
import play.api.i18n.{I18nSupport, Messages, MessagesApi}
import play.api.libs.concurrent.Execution.Implicits._
import play.api.mvc.Controller

import scala.concurrent.Future


class PrescriberController @Inject()(val messagesApi: MessagesApi, val env: Environment[User, CookieAuthenticator], val ptDAO: PatientDAO, credentialsProvider: CredentialsProvider, prescriptionDAO: PrescriptionDAO, timeZone: DateTimeZone)
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

  /**
   * The add patient action.
   *
   * This is asynchronous, since we're invoking the asynchronous methods on PatientRepository.
   */
  def addPatient() = SecuredAction(AuthorizedWithUserType("models.Prescriber")).async { implicit request =>
    AddPatientForm.form.bindFromRequest.fold(
      form => Future.successful(BadRequest(views.html.addPatient(form, request.identity, DropdownUtils.getTitles, DropdownUtils.getDaysOfMonth, DropdownUtils.getMonths, DropdownUtils.getYears))),
      patient => {
        ptDAO.findPatient(patient.hospitalNumber).flatMap{
          case Some(patientExists) =>
            Future.successful(Redirect(routes.PrescriberController.index()).flashing("error" -> Messages("patient.exists")))
          case None =>
            val pt = Patient(patient.hospitalNumber, patient.title, patient.firstName, patient.surname, dobToString(patient.dobDayOfMonth, patient.dobMonth, patient.dobYear))
            ptDAO.save(pt)
            Future.successful(Ok(views.html.prescription(PrescriptionForm.form, request.identity, pt, DropdownUtils.getMRMorphine, DropdownUtils.getMRMorphineDoses, DropdownUtils.getBreakthroughMorphine, DropdownUtils.getBreakthroughMorphineDoses)))
        }
      }
    )
  }

  /**
   * @return the patients dob formatted as a single String
   */
  def dobToString(day: String, month: String, year: String): String = {
    day + "-" + month + "-" + year
  }


}
