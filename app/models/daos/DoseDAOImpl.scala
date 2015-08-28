package models.daos

import java.sql.Timestamp
import javax.inject.Inject

import models.Dose

import scala.concurrent.ExecutionContext


class DoseDAOImpl @Inject()(implicit ex: ExecutionContext) extends DoseDAO with DAOSlick {
  import driver.api._

  /**
   * Inserts a dose into the database.
   *
   * @param dose The dose to save.
   */
  override def save(dose: Dose) = db.run {
    slickDoses += Dose(dose.ptHospitalNumber, dose.date)
  }

  /**
   * Counts the number of breakthrough doses for a patient since their last prescription.
   *
   * @param ptID The patients hospital number.
   * @param date The timestamp of the patients most recent prescription
   */
  override def countBreakthroughDoses(ptID: String, date: Timestamp) = {
    val query = for {
      dosesOption <- slickDoses.filter(_.ptHospitalNumber === ptID).filter(_.date > date)
    }yield dosesOption
    db.run(query.result).map {resultOption => resultOption.length}
  }
}
