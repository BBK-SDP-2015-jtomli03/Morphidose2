package models.daos

import models.Patient
import scala.concurrent.Future

/**
 * Created by Jo on 21/08/2015.
 */
trait PatientDAO {

  /**
   * Finds a patient by their user ID.
   *
   * @param hospitalNumber The hospital number of the patient to find.
   * @return The found patient or None if no user for the given ID could be found.
   */
  def findPatient(hospitalNumber: String): Future[Option[Patient]]

  /**
   * Adds a patient.
   *
   * @param patient The patient to save.
   */
  def save(patient: Patient)

  /**
   * Lists all the patients in the database.
   */
  def list(): Future[Seq[Patient]]
}
