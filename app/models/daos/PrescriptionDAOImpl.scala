package models.daos

package models.daos

import models.Prescription
import play.api.libs.concurrent.Execution.Implicits.defaultContext
import scala.concurrent.Future
import scala.language.postfixOps

class PrescriptionDAOImpl extends PatientDAO with DAOSlick {
    import driver.api._

//    /**
//     * Finds a patient by their user ID.
//     *
//     * @param hospitalNumber The hospital number of the patient to find.
//     * @return The found patient or None if no user for the given ID could be found.
//     */
//    override def findPatient(hospitalNumber: String): Future[Option[Patient]] = {
//      val query = for {
//        patient <- slickPatients.filter(_.hospitalNumber === hospitalNumber)
//      } yield (patient)
//      db.run(query.result.headOption).map { resultOption =>
//        resultOption.map {
//          case (pt) =>
//            Patient(
//              pt.hospitalNumber,
//              pt.title,
//              pt.firstName,
//              pt.surname,
//              pt.dob)
//        }
//      }
//    }


    /**
     * Inserts a new prescription into the database.
     *
     * @param prescription The patient to save.
     */
    override def addPrescription(prescription: Prescription) = db.run {
      slickPrescriptions += prescription
    }
  }
