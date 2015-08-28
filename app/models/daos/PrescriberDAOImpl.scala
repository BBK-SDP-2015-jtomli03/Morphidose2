package models.daos

import javax.inject.Inject

import scala.concurrent.{ExecutionContext, Future}

class PrescriberDAOImpl @Inject()(implicit ex: ExecutionContext) extends PrescriberDAO with DAOSlick {
  import driver.api._

  /**
   * Finds a prescribers name and title by their user ID.
   *
   * @param userID The user ID of the prescriber to find.
   * @return The prescribers name and title as a String or None if no user for the given ID could be found.
   */
  override def findPrescriberName(userID: String): Future[Option[String]] = {
    val query = for {
      prescriber <- slickPrescribers.filter(_.userID === userID)
    } yield prescriber
    db.run(query.result.headOption).map { resultOption =>
      resultOption.map {
        case (prescriber) => prescriber.title + " " + prescriber.firstName + " " + prescriber.lastName
      }
    }
  }
}
