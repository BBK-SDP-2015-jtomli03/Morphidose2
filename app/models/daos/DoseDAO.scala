package models.daos

import java.sql.Timestamp

import models.Dose

import scala.concurrent.Future

/**
 * Created by Jo on 28/08/2015.
 */
trait DoseDAO {

  /**
   * Inserts a dose into the database.
   *
   * @param dose The dose to save.
   */
  def save(dose: Dose)

  /**
   * Counts the number of breakthrough doses for a patient since their last prescription.
   *
   * @param ptID The patients hospital number.
   * @param date The timestamp of the patients most recent prescription
   */
  def countBreakthroughDoses(ptID: String, date: Timestamp): Future[Int]

}
