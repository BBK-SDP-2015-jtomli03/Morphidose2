package models.daos

import models.Prescription

class PrescriptionDAOImpl extends PrescriptionDAO with DAOSlick  {
  import driver.api._

  /**
   * Adds a new prescription to the database.
   *
   * @param prescription The prescription to save.
   */
  def addPrescription(prescription: Prescription) = db.run{
    slickPrescriptions += prescription
  }
}
