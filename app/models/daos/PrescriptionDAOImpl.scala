package models.daos


import javax.inject.Inject

import models._
import org.joda.time.DateTimeZone

import scala.concurrent.{Future, ExecutionContext}

class PrescriptionDAOImpl @Inject()(timeZone: DateTimeZone)(implicit ex: ExecutionContext) extends PrescriptionDAO with DAOSlick {

  import driver.api._

  /**
   * Adds a new prescription to the database.
   *
   * @param prescription The prescription to save.
   */
  def addPrescription(prescription: Prescription) = db.run {
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

  /**
   * Gets the latest patient, prescriber, and prescription information for a particular patient from the database.
   *
   * @param hospitalNumber the patients hospital number
   */
  def getLatestPrescriptionInfo(hospitalNumber: String): Future[PrescriptionPrescriberPatientData] = {
    val query = for {
      prescriptionOption <- slickPrescriptions.filter(_.ptHospitalNumber === hospitalNumber).sortBy(_.date.asc).result.headOption
      prescriber <- slickPrescribers.filter(_.userID === prescriptionOption.map(_.prescriberID)).result
      patient <- slickPatients.filter(_.hospitalNumber === hospitalNumber).result
    } yield (prescriptionOption, prescriber, patient)

    db.run(query).map { info =>
      val prescriber = info._2.head
      val ptData = info._3.head

      PrescriptionPrescriberPatientData(info._1, prescriber.title + " " + prescriber.firstName + " " + prescriber.lastName, Patient(ptData.hospitalNumber, ptData.title, ptData.firstName, ptData.surname, ptData.dob)) }
    }



}
