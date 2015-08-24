package controllers

import java.sql.Timestamp
import javax.inject.Inject

import com.mohiva.play.silhouette.api.{Environment, Silhouette}
import com.mohiva.play.silhouette.impl.authenticators.CookieAuthenticator
import models.daos.PrescriptionDAO
import models.forms.PrescriptionForm
import models.utils.{AuthorizedWithUserType, DropdownUtils}
import models.{Prescription, Patient, User}
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
   * The add a prescription action.
   *
   * This is asynchronous, since we're invoking the asynchronous methods on prescriptionDAO.
   */
//  def newPrescription(patient: Patient) = SecuredAction(AuthorizedWithUserType("models.Prescriber")).async { implicit request =>
//    PrescriptionForm.form.bindFromRequest.fold(
//      form => Future.successful(BadRequest(views.html.prescription(PrescriptionForm.form, request.identity, patient, DropdownUtils.getMRMorphine, DropdownUtils.getMRMorphineDoses, DropdownUtils.getBreakthroughMorphine, DropdownUtils.getBreakthroughMorphineDoses))),
//      prescriptionData => {
//        //val patient = Patient("A1234N", "Mrs", "Janet", "Carr", "5-JUN-1948")
//        val prescription = Prescription(patient.hospitalNumber, request.identity.userID.toString, new Timestamp(new DateTime().withZone(timeZone).getMillis), prescriptionData.MRDrug, prescriptionData.MRDose, prescriptionData.breakthroughDrug, prescriptionData.breakthroughDose)
//        prescriptionDAO.addPrescription(prescription)
//        //        Future.successful(Ok(views.html.prescription(PrescriptionForm.form, request.identity, patient, DropdownUtils.getMRMorphine, DropdownUtils.getMRMorphineDoses, DropdownUtils.getBreakthroughMorphine, DropdownUtils.getBreakthroughMorphineDoses)))
//        Future.successful(Ok(views.html.test(request.identity)))
//
//      }
//    )
//  }

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
      prescriptionData => {
        //val patient = Patient("A1234N", "Mrs", "Janet", "Carr", "5-JUN-1948")
        val prescription = Prescription(patient.hospitalNumber, request.identity.userID.toString, new Timestamp(new DateTime().withZone(timeZone).getMillis), prescriptionData.MRDrug, getDose(prescriptionData.MRDose), prescriptionData.breakthroughDrug, getDose(prescriptionData.breakthroughDose))
//        val prescription = Prescription("huohdos", "udaihu", new Timestamp(new DateTime().withZone(timeZone).getMillis), "Morphine", 5.00, "hufhuoa", 10.00)

        prescriptionDAO.addPrescription(prescription)
        //        Future.successful(Ok(views.html.prescription(PrescriptionForm.form, request.identity, patient, DropdownUtils.getMRMorphine, DropdownUtils.getMRMorphineDoses, DropdownUtils.getBreakthroughMorphine, DropdownUtils.getBreakthroughMorphineDoses)))
        Future.successful(Ok(views.html.test(request.identity)))

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

}
