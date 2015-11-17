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
import scala.language.postfixOps // Combinator syntax

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

//  def addDoses() = Action(BodyParsers.parse.json) { implicit request =>
//    var mostRecentDate: Timestamp = new Timestamp(0) //ie start of epoch time
//    val json: JsValue = request.body

//    val nameResult = json.validate[List[Dose]]   array.as[List[String]]
//
//    val doses = nameResult match {
//      case JsSuccess(list : List[Dose], _) => list
//      case e: JsError => {
//        List()
//      }
//    }

//    //val doseReader = Json.reads[(String, String)]
//    //val doses = request.body.as[List[Dose]]
//    doses foreach { dose =>
//      //val date = new Timestamp(dose._1.toLong)
//      if(dose.date.after(mostRecentDate)) mostRecentDate = dose.date
//      addDose(Dose(dose.date, dose.ptHospitalNumber))
//    }
//    //val doseData = Dose(mostRecentDate, doses.head._2)
//    val jsonDose = Json.toJson(Dose(mostRecentDate, doses.head.ptHospitalNumber))
//    Ok(jsonDose)
//
//    implicit val doseReader = Json.reads[(Timestamp, String)]
//    val doseReader = Json.reads[(Timestamp, String)]
//    val doses = (request.body \ "doses").as[List[Dose]]
//    doses foreach { dose =>
//      if(dose.date.after(mostRecentDate)) mostRecentDate = dose.date
//    }
//    val doseData = Dose(mostRecentDate, doses.head.ptHospitalNumber)
//    val jsonDose = Json.toJson(doseData)
//    Ok(jsonDose)
//    Ok("ok")
//  }

//  /**
//   * Function taken directly from https://groups.google.com/forum/#!topic/play-framework/G1O4Pmle6Fw
//   * The Write converter to convert a Timestamp model to a JsValue.
//   * The Read converter to convert a JsValue to a Timestamp model.
//   *
//   */
//  implicit val formatTimestamp = new Format[Timestamp] {
//
//    def writes(ts: Timestamp): JsValue = Json.obj("date" -> ts.toString())
//
//    def reads(ts: JsValue): JsResult[Timestamp] = {
//      try {
//        JsSuccess(Timestamp.valueOf(ts.as[String]))
//      } catch {
//        case e: IllegalArgumentException => JsError("Unable to parse timestamp")
//      }
//    }
//  }

//  implicit val doseReads: Reads[Dose] = (
//    (JsPath \ "date").read[String].map(dateString => new Timestamp(dateString.toLong))
//      (JsPath \ "hospitalNumber").read[String]
//    )(Dose.apply _)
//
//  def timestampToLong(t: Timestamp): Long = new Long(t.getTime)
//
//  def longToTimestamp(dt: Long): Timestamp = new Timestamp(dt.getMillis)
//
//  implicit val timestampFormat = new Format[Timestamp] {
//
//    def writes(t: Timestamp): JsValue = toJson(timestampToLong(t))
//
//    def reads(json: JsValue): JsResult[Timestamp] = fromJson[String](json).map(longToTimestamp)
//
//  }

//  implicit val readsDose: Reads[Dose] = new Reads[Dose] {
//    def reads(json: JsValue): Reads[Dose] = {
//      for {
//        date <- (JsPath \ "date").read[String].map(dateString => new Timestamp(dateString.toLong))
//        ptHospitalNumber <- (JsPath \ "ptHospitalNumber").read[String]
//      } yield Dose(date, ptHospitalNumber)
//    }
//  }

//  import play.api.libs.json._
//  import play.api.libs.functional.syntax._
//
//  implicit val doseFormat = (
//    (__ \ "date").format[java.sql.Timestamp] and
//      (__ \ "ptHospitalNumber").format[String]
//    )(Dose.apply, unlift(Dose.unapply))


  //********** read WORKS ****************************//
implicit val doseDataReads = new Reads[Dose] {
  def reads(json: JsValue): JsResult[Dose] = {
    val date = (json \ "date").as[Long]
    val hospitalNumber = (json \ "hospitalNumber").as[String]
    JsSuccess(Dose(hospitalNumber, new Timestamp(date)))
  }
}
  //********** read WORKS ****************************//

//  import play.api.libs.functional.syntax._
//  import play.api.libs.json._
//
//  implicit val doseReads: Reads[Dose] = (
//    (JsPath \ "date").read[Long].map(long => new Timestamp(long)) and
//      (JsPath \ "ptHospitalNumber").read[String]
//    )(Dose.apply _)

  def addDoses() = Action(BodyParsers.parse.json) { implicit request =>
    var mostRecentDate: Timestamp = new Timestamp(0) //ie start of epoch time

    //addDose(Dose(new Timestamp(1446291391), "A084NV51"))

    //val doseReader = Json.reads[(String, String)]
//    val doses = request.body.as[List[Dose]]
//    doses foreach { dose =>
//        //val date = new Timestamp(dose._1.toLong)
//        if(dose.date.after(mostRecentDate)) mostRecentDate = dose.date
//        addDose(Dose(dose.date, dose.ptHospitalNumber))
//    }
//    //val doseData = Dose(mostRecentDate, doses.head._2)
//    val jsonDose = Json.toJson(Dose(mostRecentDate, doses.head.ptHospitalNumber))
//    Ok(jsonDose)

    //implicit val doseReader = Json.reads[(Timestamp, String)]
    //val doseReader = Json.reads[(Timestamp, String)]

  //val json = (request.body \ "listToSend").as[List[String]]

//    val obj = request.body.asInstanceOf[JsObject]
//    val doses = (obj \ "doses").as[List[Dose]]

    //val doses = request.body.as[List[Dose]]

    /***** PHOTO ONE ******/
    //val doses = (request.body).toString()

    //val doses = (request.body)(1).as[Dose]

    //val date = (request.body \ "date").as[Long]
    //val hospitalNumber = (request.body \ "hospitalNumber").as[String]
    //addDose(Dose(new Timestamp(new DateTime().withZone(timeZone).getMillis), "A059ES21"))

    //val names = (json \\ "name").map(_.as[String])
    // Seq("Watership Down", "Fiver", "Bigwig")

    //*************** GETS DOSES BUT STACK OVERFLOW ERROR!!!!! **************************//
  //val doses = (request.body \ "doses").as[List[Dose]]

//    val doses = dosesJsResult match{
//      case s: JsSuccess[List[List[Dose]]] => s.get.head
//      case e: JsError => null
//    }
    //val doses = dosesJsResult.head
    //addDose(Dose(new Timestamp(new DateTime().withZone(timeZone).getMillis), "A059ES21"))
    //doseDAO.save(Dose(new Timestamp(new DateTime().withZone(timeZone).getMillis), "A059ES21"))
    //val number = Await.result(doseDAO.countBreakthroughDoses("A085JT47", mostRecentDate), 5.seconds)



    val doses = request.body.as[List[Dose]]

    val doseData = doses match {
      case head :: tail => saveDoses(doses)
      case Nil => null
      }

//      doses foreach { dose =>
//          count = count + 1
//          if(dose.date.after(mostRecentDate)) mostRecentDate = dose.date
//          addDose(Dose(dose.ptHospitalNumber, dose.date))
//      }
//      val doseData = Dose(doses.head.ptHospitalNumber, mostRecentDate)
      val jsonDose = Json.toJson(doseData)

    //Ok(Json.toJson(Dose(new Timestamp(1446291391), "A084NV51")))
    Ok(Json.toJson(jsonDose)) //jsonDose.toString()

    //*************** GETS DOSES BUT STACK OVERFLOW ERROR!!!!! **************************//

  //*************** BELOW WORKS!!!!! **************************//
//    val json = request.body.as[List[String]]
//
//    val ptHospitalNumber = json.head
//    val timestamps = json.drop(1)
////    timestamps foreach { timestamp =>
////      val date = new Timestamp(timestamp.toLong)
////      //if(date.after(mostRecentDate)) mostRecentDate = date
////      addDose(Dose(date, ptHospitalNumber))
////    }
//    //val doseData = Dose(mostRecentDate, ptHospitalNumber)
//    addDose(Dose(new Timestamp(timestamps.head.toLong), ptHospitalNumber))
//    val jsonDose = Json.toJson(timestamps.head)
//    Ok(jsonDose)
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

//  def addDoses() = Action(BodyParsers.parse.json) { implicit request =>
//    var mostRecentDate: Timestamp = new Timestamp(0) //ie start of epoch time
//    //val doseReader = Json.reads[(String, String)]
//    val doses = request.body.as[List[(String, String)]]
//    val ptHospitalNumber = doses.head._2
//    doses foreach { dose =>
//      val date = new Timestamp(dose._1.toLong)
//      if(date.after(mostRecentDate)) mostRecentDate = date
//      addDose(Dose(date, ptHospitalNumber))
//    }
//    //val doseData = Dose(mostRecentDate, doses.head._2)
//    val jsonDose = Json.toJson(Dose(mostRecentDate, ptHospitalNumber))
//    Ok("ok")
//  }

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
