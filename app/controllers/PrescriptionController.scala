package controllers

import java.sql.Timestamp
import java.text.SimpleDateFormat
import javax.inject.Inject

import com.mohiva.play.silhouette.api.{Environment, Silhouette}
import com.mohiva.play.silhouette.impl.authenticators.CookieAuthenticator
import models.daos.PrescriptionDAO
import models.forms.PrescriptionForm
import models.utils.{AuthorizedWithUserType, DropdownUtils}
import models.{Patient, Prescription, PrescriptionData, User}
import org.joda.time.{DateTime, DateTimeZone}
import play.api.i18n.{I18nSupport, MessagesApi}
import play.api.mvc.Controller

import scala.concurrent.Future

/**
 * The prescription controller.
 *
 * @param messagesApi The Play messages API.
 * @param env The Silhouette environment.
 */
class PrescriptionController @Inject()(
                                        val messagesApi: MessagesApi,
                                        val env: Environment[User, CookieAuthenticator],
                                        prescriptionDAO: PrescriptionDAO,
                                        timeZone: DateTimeZone) extends Silhouette[User, CookieAuthenticator] with Controller with I18nSupport{


  /**
   * The show a new prescription action.
   *
   * @param patient the patient for which the prescription will be written
   */
  def prescription(patient: Patient) = SecuredAction(AuthorizedWithUserType("models.Prescriber")) { implicit request =>
              Ok(views.html.prescription(PrescriptionForm.form, request.identity, patient, DropdownUtils.getMRMorphine, DropdownUtils.getMRMorphineDoses, DropdownUtils.getBreakthroughMorphine, DropdownUtils.getBreakthroughMorphineDoses))
  }

  /**
   * The add a prescription action.
   *
   * This is asynchronous, since we're invoking the asynchronous methods on prescriptionDAO.
   */
  def addPrescription(patient: Patient) = SecuredAction(AuthorizedWithUserType("models.Prescriber")).async { implicit request =>
    PrescriptionForm.form.bindFromRequest.fold(
      form => Future.successful(BadRequest(views.html.prescription(PrescriptionForm.form, request.identity, patient, DropdownUtils.getMRMorphine, DropdownUtils.getMRMorphineDoses, DropdownUtils.getBreakthroughMorphine, DropdownUtils.getBreakthroughMorphineDoses))),
      data => {
        val prescription = Prescription(patient.hospitalNumber, request.identity.userID.toString, new Timestamp(new DateTime().withZone(timeZone).getMillis), data.MRDrug, getDose(data.MRDose), data.breakthroughDrug, getDose(data.breakthroughDose))
        prescriptionDAO.addPrescription(prescription)
        val prescriptionData = PrescriptionData(getPrescriberDetails(request.identity.title, request.identity.firstName, request.identity.lastName), getDateAsString(prescription.date), prescription.MRDrug, data.MRDose, prescription.breakthroughDrug, data.breakthroughDose)
        Future.successful(Ok(views.html.doseCalculations(PrescriptionForm.form, request.identity, patient, prescriptionData, DropdownUtils.getMRMorphine, DropdownUtils.getMRMorphineDoses, DropdownUtils.getBreakthroughMorphine, DropdownUtils.getBreakthroughMorphineDoses)))
      }
    )
  }

  /**
   * Converts the dose from a String to a Double.
   *
   * @param stringDose the dose as a String
   * @return the dose as a Double
   */
  def getDose(stringDose: String) = {
    stringDose.substring(0, stringDose.indexOf("mg")).toDouble
  }

  /**
   * Formats a prescribers details into a String.
   *
   * @param title the prescribers title
   * @param firstName the prescribers first name
   * @param surname the prescribers surname
   * @return the prescribers details as a String
   */
  def getPrescriberDetails(title: String, firstName: String, surname: String) = title + " " + firstName + " " + surname

  /**
   * Converts a timestamp to a formatted string.
   *
   * @param timestamp the timestamp
   * @return the formatted timestamp
   */
  def getDateAsString(timestamp: Timestamp) = {
    val dateFormat = new SimpleDateFormat("dd-MM-yyyy")
    dateFormat.format(timestamp)
  }
}
