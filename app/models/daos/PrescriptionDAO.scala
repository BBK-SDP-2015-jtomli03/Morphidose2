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

  /**
   * Gets the latest patient, prescriber, and prescription information for a particular patient from the database.
   *
   * @param hospitalNumber the patients hospital number
   */
  def getLatestPrescriptionInfo(hospitalNumber: String)
}
