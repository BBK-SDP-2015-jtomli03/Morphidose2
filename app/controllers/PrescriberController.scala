package controllers

import javax.inject.Inject

import com.mohiva.play.silhouette.api.{Environment, Silhouette}
import com.mohiva.play.silhouette.impl.authenticators.CookieAuthenticator
import com.mohiva.play.silhouette.impl.providers.CredentialsProvider
import models.daos.{PatientDAO, PrescriptionDAO}
import models.forms.{GetPatientForm, EditPatientForm, AddPatientForm, PrescriptionForm}
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
    Future.successful(Ok(views.html.prescriberHome(AddPatientForm.form, request.identity, DropdownUtils.getTitles, DropdownUtils.getDaysOfMonth, DropdownUtils.getMonths, DropdownUtils.getYears)))
  }

  /**
   * Handles the addPatientForm action.
   * Only authenticated prescribers can access this page, otherwise the user is redirected to the sign in page.
   *
   * @return The result to display.
   */
  def addPatientForm = SecuredAction(AuthorizedWithUserType("models.Prescriber")).async { implicit request =>
    Future.successful(Ok(views.html.addPatient(AddPatientForm.form, request.identity, DropdownUtils.getTitles, DropdownUtils.getDaysOfMonth, DropdownUtils.getMonths, DropdownUtils.getYears)))
  }

  /**
   * Handles the editPatientForm action.
   * Only authenticated prescribers can access this page, otherwise the user is redirected to the sign in page.
   *
   * @return The result to display.
   */
  def editPatientForm(patient: models.Patient) = SecuredAction(AuthorizedWithUserType("models.Prescriber")).async { implicit request =>
    Future.successful(Ok(views.html.editPtDetails(EditPatientForm.form, request.identity, patient: Patient, DropdownUtils.getTitles.updated(0, patient.title), DropdownUtils.getDaysOfMonth.updated(0, formatDateOfBirth(patient.dob, "day")), DropdownUtils.getMonths.updated(0, formatDateOfBirth(patient.dob, "month")), DropdownUtils.getYears.updated(0, formatDateOfBirth(patient.dob, "year")))))
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
            Future.successful(Redirect(routes.PrescriberController.addPatientForm()).flashing("error" -> Messages("patient.exists")))
          case None =>
            val pt = Patient(patient.hospitalNumber, patient.title, formatName(patient.firstName), formatName(patient.surname), dobToString(patient.dobDayOfMonth, patient.dobMonth, patient.dobYear))
            ptDAO.save(pt)
            Future.successful(Ok(views.html.prescription(PrescriptionForm.form, request.identity, pt, DropdownUtils.getMRMorphine, DropdownUtils.getMRMorphineDoses, DropdownUtils.getBreakthroughMorphine, DropdownUtils.getBreakthroughMorphineDoses)))
        }
      }
    )
  }

  /**
   * The edit patient action.
   *
   * @param patient the patient to edit
   */
  def editPatient(patient: Patient) = SecuredAction(AuthorizedWithUserType("models.Prescriber")).async { implicit request =>
    EditPatientForm.form.bindFromRequest.fold(
      form => Future.successful(BadRequest(views.html.editPtDetails(form, request.identity, patient: Patient, DropdownUtils.getTitles, DropdownUtils.getDaysOfMonth, DropdownUtils.getMonths, DropdownUtils.getYears))),
      patientData => {
            val pt = Patient(patient.hospitalNumber, patientData.title, formatName(patientData.firstName), formatName(patientData.surname), dobToString(patientData.dobDayOfMonth, patientData.dobMonth, patientData.dobYear))
            ptDAO.save(pt)
            //below error highlight compiles -> problem is with intellij
            Future.successful(Redirect(routes.PrescriberController.editPatientForm(pt)).flashing("success" -> Messages("patient.edit")))
      }
    )
  }

  /**
   * @return the patients day, month, or year of dob formatted as a single String for a dropdown
   */
  def formatDateOfBirth(dob: String, format: String): String = {
    format match{
      case "day" => dob.substring(0, dob.length - 9)
      case "month" => dob.length match {
        case 10 => dob.substring(2,5)
        case 11 => dob.substring(3,6)
      }
      case "year" => dob.length match {
        case 10 => dob.substring(6)
        case 11 => dob.substring(7)
      }
    }
  }

  /**
   * @return the patients dob formatted as a single String
   */
  def dobToString(day: String, month: String, year: String): String = {
    day + "-" + month + "-" + year
  }

  /**
   * @return the patients name formatted as a String with the first letter in upper case and the rest lower case
   */
  def formatName(name: String): String = {
    name.charAt(0).toUpper + name.substring(1).toLowerCase
  }


}
