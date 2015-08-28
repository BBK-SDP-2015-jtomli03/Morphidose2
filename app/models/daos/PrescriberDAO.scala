package models.daos

import scala.concurrent.Future

trait PrescriberDAO {

  /**
   * Finds a prescribers name and title by their user ID.
   *
   * @param userID The user ID of the prescriber to find.
   * @return The prescribers name and title as a String or None if no user for the given ID could be found.
   */
  def findPrescriberName(userID: String): Future[Option[String]]

}
