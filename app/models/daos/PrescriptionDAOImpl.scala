package models.daos


import javax.inject.Inject

import models.Prescription
import org.joda.time.DateTimeZone

class PrescriptionDAOImpl @Inject()(timeZone: DateTimeZone) extends PrescriptionDAO with DAOSlick  {
  import driver.api._

  /**
   * Adds a new prescription to the database.
   *
   * @param prescription The prescription to save.
   */
  def addPrescription(prescription: Prescription) = db.run{
    slickPrescriptions +=
      Prescription(prescription.ptHospitalNumber, prescription.prescriberID, prescription.date, prescription.MRDrug, prescription.MRDose, prescription.breakthroughDrug, prescription.breakthroughDose)
 }
}
