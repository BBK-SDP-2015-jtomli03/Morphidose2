package models.daos

import models.Prescription

/**
 * Created by Jo on 22/08/2015.
 */
trait PrescriptionDAO {

  /**
   * Adds a prescription.
   *
   * @param prescription The prescription to save.
   */
  def addPrescription(prescription: Prescription)
}
