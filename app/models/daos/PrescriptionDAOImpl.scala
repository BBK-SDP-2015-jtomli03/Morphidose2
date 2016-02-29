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

  /**
   * Gets the latest prescription for a particular patient.
   *
   * @param hospitalNumber the patients hospital number
   */
  override def getLatestPrescription(hospitalNumber: String): Future[Option[Prescription]] = {
    val query = for {
      prescriptionOption <- slickPrescriptions.filter(_.ptHospitalNumber === hospitalNumber).sortBy(_.date.desc)
    }yield prescriptionOption
    db.run(query.result.headOption).map { resultOption =>
      resultOption.map {
        case(p) => Prescription(p.ptHospitalNumber, p.prescriberID, p.date, p.MRDrug, p.MRDose, p.breakthroughDrug, p.breakthroughDose)
      }
    }
  }
}
