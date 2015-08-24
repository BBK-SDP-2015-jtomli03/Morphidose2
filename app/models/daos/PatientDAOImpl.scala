package models.daos

import javax.inject.Inject

import models.Patient
import play.api.libs.concurrent.Execution.Implicits.defaultContext

import scala.concurrent.Future
import scala.language.postfixOps

class PatientDAOImpl @Inject() extends PatientDAO with DAOSlick {
  import driver.api._

  /**
   * Finds a patient by their user ID.
   *
   * @param hospitalNumber The hospital number of the patient to find.
   * @return The found patient or None if no user for the given ID could be found.
   */
  override def findPatient(hospitalNumber: String): Future[Option[Patient]] = {
        val query = for {
          patient <- slickPatients.filter(_.hospitalNumber === hospitalNumber)
        } yield patient
        db.run(query.result.headOption).map { resultOption =>
          resultOption.map {
            case (pt) =>
              Patient(
                pt.hospitalNumber,
                pt.title,
                pt.firstName,
                pt.surname,
                pt.dob)
          }
        }
  }

  /**
   * Inserts a patient into the database, or updates them if they already exist.
   *
   * @param patient The patient to save.
   */
  override def save(patient: Patient) = db.run {
      slickPatients.insertOrUpdate(PtData(patient.hospitalNumber, patient.title, patient.firstName, patient.surname, patient.dob))
  }
}

