package controllers

import javax.inject.Inject
import java.sql.Timestamp

import com.mohiva.play.silhouette.api._
import com.mohiva.play.silhouette.api.repositories.AuthInfoRepository
import com.mohiva.play.silhouette.api.util.PasswordHasher
import com.mohiva.play.silhouette.impl.authenticators.CookieAuthenticator
import models.{Prescription, Patient, User}
import models.forms.PrescriptionForm
import models.services.UserService
import models.utils.{DropdownUtils, AuthorizedWithUserType}
import org.joda.time.{DateTime, DateTimeZone}
import play.api.i18n.MessagesApi

import scala.concurrent.Future

/**
 * The prescription controller.
 *
 * @param messagesApi The Play messages API.
 * @param env The Silhouette environment.
 * @param userService The user UserService implementation.
 * @param authInfoRepository The auth info repository implementation.
 * @param passwordHasher The password hasher implementation.
 */
class PrescriptionController @Inject() (
                                   val messagesApi: MessagesApi,
                                   val env: Environment[User, CookieAuthenticator],
                                   userService: UserService,
                                   authInfoRepository: AuthInfoRepository,
                                   passwordHasher: PasswordHasher,
                                   val timeZone: DateTimeZone) extends Silhouette[User, CookieAuthenticator] {

  /**
   * The add patient action.
   *
   * This is asynchronous, since we're invoking the asynchronous methods on PatientRepository.
   */
  def newPrescription(patient: Patient) = SecuredAction(AuthorizedWithUserType("models.Prescriber")).async{ implicit request =>
    PrescriptionForm.form.bindFromRequest.fold(
      form => Future.successful(BadRequest(views.html.prescription(PrescriptionForm.form, request.identity, patient, DropdownUtils.getMRMorphine, DropdownUtils.getMRMorphineDoses, DropdownUtils.getBreakthroughMorphine, DropdownUtils.getBreakthroughMorphineDoses))),
      prescriptionData => {
        val prescription = Prescription(patient.hospitalNumber, request.identity.userID, new Timestamp(new DateTime().withZone(timeZone).getMillis), prescriptionData.MRDrug, prescriptionData.MRDose, prescriptionData.breakthroughDrug, prescriptionData.breakthroughDose)
        ptDAO.save(pt)
        Future.successful(Ok(views.html.prescription(PrescriptionForm.form, request.identity, pt, DropdownUtils.getMRMorphine, DropdownUtils.getMRMorphineDoses, DropdownUtils.getBreakthroughMorphine, DropdownUtils.getBreakthroughMorphineDoses)))

      }
    )
  }

}