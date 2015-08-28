package models.daos

import models.Prescription

import scala.concurrent.Future

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
   * Gets the latest prescription information for a particular patient.
   *
   * @param hospitalNumber the patients hospital number
   * @return A Future Option[Precsription] or None if a prescription doesn't exist.
   */
  def getLatestPrescription(hospitalNumber: String): Future[Option[Prescription]]
}
