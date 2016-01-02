package controllers

import java.sql.Timestamp
import javax.inject.Inject

import com.mohiva.play.silhouette.api.{Environment, Silhouette}
import com.mohiva.play.silhouette.impl.authenticators.CookieAuthenticator
import com.mohiva.play.silhouette.impl.providers.CredentialsProvider
import models._
import models.daos.{DoseDAO, PatientDAO, PrescriptionDAO}
import org.joda.time.DateTimeZone
import play.api.i18n.{I18nSupport, MessagesApi}
import play.api.libs.json.Json._
import play.api.libs.json.Reads._
import play.api.libs.json._
import play.api.mvc.{Action, BodyParsers, Controller}

import scala.concurrent.duration._
import scala.concurrent.{Await, ExecutionContext}
import scala.language.postfixOps

/**
 * The patient controller
 */
class PatientController @Inject()(val messagesApi: MessagesApi, val env: Environment[User, CookieAuthenticator], val ptDAO: PatientDAO, doseDAO: DoseDAO, credentialsProvider: CredentialsProvider, prescriptionDAO: PrescriptionDAO, timeZone: DateTimeZone, prescriptionDataFormatter: PrescriptionDataFormatter)
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
            case None =>
              val emptyPrescriptionData = Json.toJson(getEmptyPrescriptionData)
              Ok(emptyPrescriptionData)
          }
        case None => BadRequest(Json.obj("status" -> "KO", "message" -> ("patient.notfound")))
    }
  }

  /**
   * The Write converter to convert a Dose model to a JsValue.
   *
   */
  implicit val doseDataWrites = new Writes[Dose]{
    def writes(dose: Dose) = Json.obj(
      "date" -> dose.date,
      "hospitalNumber" -> dose.ptHospitalNumber
    )
  }

  implicit val doseDataReads = new Reads[Dose] {
    def reads(json: JsValue): JsResult[Dose] = {
      val date = (json \ "date").as[Long]
      val hospitalNumber = (json \ "hospitalNumber").as[String]
      JsSuccess(Dose(hospitalNumber, new Timestamp(date)))
    }
  }

  def addDoses() = Action(BodyParsers.parse.json) { implicit request =>
    val doses = request.body.as[List[Dose]]
    val mostRecentDose = doses match {
      case head :: tail => saveDoses(doses)
      case Nil => null
    }
    val jsonDose = Json.toJson(mostRecentDose)
    Ok(Json.toJson(jsonDose))
  }

  /**
   * Saves a list of doses to the database doses table.
   *
   * @param doses the list of doses to save
   * @return Dose the most recent dose added to the doses table
   */
  def saveDoses(doses: List[Dose]): Dose = {
    var mostRecentDate: Timestamp = new Timestamp(0) //ie start of epoch time
    doses foreach { dose =>
      if(dose.date.after(mostRecentDate)) mostRecentDate = dose.date
      addDose(Dose(dose.ptHospitalNumber, dose.date))
    }
    Dose(doses.head.ptHospitalNumber, mostRecentDate)
  }

  /**
   * Adds a dose to the database doses table.
   *
   * @param dose the dose to save
   */
  def addDose(dose: Dose) = {
    doseDAO.save(dose)
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

  /**
   * Returns empty prescription data.
   *
   * @return PrescriptionData that is empty.
   */
  def getEmptyPrescriptionData = PrescriptionData("","","","","","")



}
