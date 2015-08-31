package controllers

import javax.inject.Inject

import com.mohiva.play.silhouette.api.{Environment, Silhouette}
import com.mohiva.play.silhouette.impl.authenticators.CookieAuthenticator
import com.mohiva.play.silhouette.impl.providers.CredentialsProvider
import models._
import models.daos.{DoseDAO, PatientDAO, PrescriptionDAO}
import org.joda.time.DateTimeZone
import play.api.i18n.{I18nSupport, MessagesApi}
import play.api.libs.json._
import play.api.mvc.{Action, BodyParsers, Controller}

import scala.concurrent.duration._
import scala.concurrent.{Await, ExecutionContext}

/**
 * The patient controller
 */
class PatientController @Inject()(val messagesApi: MessagesApi, val env: Environment[User, CookieAuthenticator], val ptDAO: PatientDAO, val doseDAO: DoseDAO, credentialsProvider: CredentialsProvider, prescriptionDAO: PrescriptionDAO, timeZone: DateTimeZone, prescriptionDataFormatter: PrescriptionDataFormatter)
                                 (implicit ec: ExecutionContext) extends Silhouette[User, CookieAuthenticator] with Controller with I18nSupport {

  /**
   * The Write converter to convert a PrescriptionData model to a JsValue.
   *
   */
  implicit val prescriptionDataWrites = new Writes[PrescriptionData]{
    def writes(prescription: PrescriptionData) = Json.obj(
      "prescriber" -> prescription.prescriber,
      "date" -> prescription.date,
      "mrdrug" -> prescription.MRDrug,
      "mrdose" -> prescription.MRDose,
      "breakthroughDrug" -> prescription.breakthroughDrug,
      "breakthroughDose" -> prescription.breakthroughDose
    )
  }

    /**
     * Handles the register action to confirm a patient exists and return the current prescription data.
     *
     * @return The JSON response.
     */
  def register() = Action(BodyParsers.parse.json) { implicit request =>
    val hospNumber = (request.body \ "hospitalNumber").as[String]
    retrievePatient(hospNumber) match{
        case Some(patient) =>
          retrievePrescription(hospNumber) match {
            case Some(prescription) =>
              val prescriptionData = prescriptionDataFormatter.getInstanceOfPrescriptionData(prescription)
              val jsonData = Json.toJson(prescriptionData)
              Ok(jsonData)
            case None => BadRequest(Json.obj("status" -> "KO", "message" -> ("prescription.notfound")))
          }
        case None => BadRequest(Json.obj("status" -> "KO", "message" -> ("patient.notfound")))
      }
  }

  /**
   * Retrieves a patient by their hospital number and returns Some(Patient) or returns None if there isn't one.
   *
   * @param hospitalNumber the patients hospital number
   * @return Some(Patient) or None if there isn't one.
   */
  def retrievePatient(hospitalNumber: String) = {
    Await.result(ptDAO.findPatient(hospitalNumber), 5.seconds)
  }

  /**
   * Retrieves a prescription by a patients hospital number and returns Some(Prescription) or returns None if there isn't one.
   *
   * @param hospitalNumber the patients hospital number
   * @return Some(Prescription) or None if there isn't one.
   */
  def retrievePrescription(hospitalNumber: String) = {
    Await.result(prescriptionDAO.getLatestPrescription(hospitalNumber: String), 5.seconds)
  }


  def addDose(dose: Dose) = {
    doseDAO.save(dose)
  }
}
