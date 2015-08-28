package models.daos


import javax.inject.Inject

import models._
import org.joda.time.DateTimeZone

import scala.concurrent.{ExecutionContext, Future}

class PrescriptionDAOImpl @Inject()(timeZone: DateTimeZone)(implicit ex: ExecutionContext) extends PrescriptionDAO with DAOSlick {

  import driver.api._

  /**
   * Adds a new prescription to the database.
   *
   * @param prescription The prescription to save.
   */
  override def addPrescription(prescription: Prescription) = db.run {
    slickPrescriptions +=
      Prescription(prescription.ptHospitalNumber, prescription.prescriberID, prescription.date, prescription.MRDrug, prescription.MRDose, prescription.breakthroughDrug, prescription.breakthroughDose)
  }

  //    val compiledGetLatestPrescriptionWithDoseTitrationsQuery = {
  //
  //      def query(hospitalNumber: String) = for{
  //        prescriptionOption <- slickPrescriptions.filter(_.ptHospitalNumber === hospitalNumber).sortBy(_.date.asc).result.headOption
  //        prescriber <- slickPrescribers.filter(_.userID === prescriptionOption.map(_.prescriberID)).result
  //        patient <- slickPatients.filter(_.hospitalNumber === hospitalNumber).result
  //      }yield(prescriptionOption, prescriber, patient)
  //
  //      Compiled(query _)
  //    }

//  /**
//   * Gets the latest patient, prescriber, and prescription information for a particular patient from the database.
//   *
//   * @param hospitalNumber the patients hospital number
//   */
//  def getLatestPrescriptionInfo(hospitalNumber: String): Future[PrescriptionPrescriberPatientData] = {
//    val prescriberID =
//
//    val query = for {
////      prescriptionOption <- slickPrescriptions.filter(_.ptHospitalNumber === hospitalNumber).sortBy(_.date.asc)
//      prescriber <- slickPrescribers.filter(_.userID === prescriptionOption.map(_.prescriberID))
//      patient <- slickPatients.filter(_.hospitalNumber === hospitalNumber)
//    } yield (prescriptionOption, prescriber, patient)
//
//    db.run(query).map { info =>
//      val prescriber = info._2.head
//      val ptData = info._3.head
//
//      PrescriptionPrescriberPatientData(info._1, prescriber.title + " " + prescriber.firstName + " " + prescriber.lastName, Patient(ptData.hospitalNumber, ptData.title, ptData.firstName, ptData.surname, ptData.dob)) }
//    }

//  /**
//   * Gets the latest prescription for a particular patient.
//   *
//   * @param hospitalNumber the patients hospital number
//   */
//  def getLatestPrescription(hospitalNumber: String) = {
//    val prescriptionAction = for {
//      prescriptionOption <- slickPrescriptions.filter(_.ptHospitalNumber === hospitalNumber).sortBy(_.date.asc)
//    } yield prescriptionOption
//
//    db.run(prescriptionAction).result.headOption
//  }

  /**
   * Gets the latest prescription for a particular patient.
   *
   * @param hospitalNumber the patients hospital number
   */
  override def getLatestPrescription(hospitalNumber: String): Future[Option[Prescription]] = {
    val query = for {
      prescriptionOption <- slickPrescriptions.filter(_.ptHospitalNumber === hospitalNumber).sortBy(_.date.asc)
    }yield prescriptionOption
    db.run(query.result.headOption).map { resultOption =>
      resultOption.map {
        case(p) => Prescription(p.ptHospitalNumber, p.prescriberID, p.date, p.MRDrug, p.MRDose, p.breakthroughDrug, p.breakthroughDose)
      }
    }
  }

//  override def findPatient(hospitalNumber: String): Future[Option[Patient]] = {
//    val query = for {
//      patient <- slickPatients.filter(_.hospitalNumber === hospitalNumber)
//    } yield patient
//    db.run(query.result.headOption).map { resultOption =>
//      resultOption.map {
//        case (pt) =>
//          Patient(
//            pt.hospitalNumber,
//            pt.title,
//            pt.firstName,
//            pt.surname,
//            pt.dob)
//      }
//    }
//  }
}
