package models

import java.sql.Timestamp

/**
 * The prescription object.
 *
 * @param ptHospitalNumber the hospital number of the patient.
 * @param prescriberID the prescribers ID.
 * @param date the date prescribed.
 * @param MRDrug the name of the MR drug prescribed.
 * @param MRDose the dose of the MR drug prescribed.
 * @param breakthroughDrug the name of the breakthrough drug prescribed.
 * @param breakthroughDose the dose of the breakthrough drug prescribed.
 */
case class Prescription(
                    ptHospitalNumber: String,
                    prescriberID: String,
                    date: Timestamp,
                    MRDrug: String,
                    MRDose: Double,
                    breakthroughDrug: String,
                    breakthroughDose: Double)

//The QueryStringBinder for the Prescription class
//object Prescription{
//  implicit val patientFormat = Json.format[Prescription]
//
//  implicit def queryStringBinder(implicit stringBinder: QueryStringBindable[String]) = new QueryStringBindable[Prescription]{
//    override def bind(key: String, params: Map[String, Seq[String]]): Option[Either[String, Prescription]] = {
//      for{
//        ptHospitalNumber <- stringBinder.bind(key + ".ptHospitalNumber", params)
//        prescriberID <- stringBinder.bind(key + ".prescriberID", params)
//        date <- stringBinder.bind(key + ".date", params)
//        MRDrug <- stringBinder.bind(key + ".MRDrug", params)
//        ptDob <- stringBinder.bind(key + ".dob", params)
//      }yield{
//        (ptHospitalNumber, prescriberID, date, MRDrug, ptDob) match {
//          case (Right(hospitalNumber), Right(prescriberID), Right(date), Right(MRDrug), Right(dob)) =>
//            Right(Patient(hospitalNumber, prescriberID, date, MRDrug, dob))
//          case _ => Left("Unable to bind Patient")
//        }
//      }
//    }
//
//    override def unbind(key: String, prescription: Prescription): String = {
//      stringBinder.unbind(key + ".ptHospitalNumber", prescription.ptHospitalNumber) + "&" + stringBinder.unbind(key + ".prescriberID", prescription.prescriberID) + "&" + stringBinder.unbind(key + ".date", prescription.date) + "&" + stringBinder.unbind(key + ".MRDrug", prescription.MRDrug) + "&" + stringBinder.unbind(key + ".dob", patient.dob)
//    }
//  }
//}