package models.daos

import models.Prescription

/**
 * Created by Jo on 22/08/2015.
 */
class PrescriptionDAOImpl extends PrescriptionDAO with DAOSlick  {

  /**
   * Adds a prescription.
   *
   * @param prescription The prescription to save.
   */
  def addPrescription(prescription: Prescription) = ???
}
