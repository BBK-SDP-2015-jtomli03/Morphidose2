package controllers

import javax.inject.Inject

import com.mohiva.play.silhouette.api.{Environment, Silhouette}
import com.mohiva.play.silhouette.impl.authenticators.CookieAuthenticator
import com.mohiva.play.silhouette.impl.providers.CredentialsProvider
import models.daos.{DoseDAO, PrescriptionDAO}
import models.{Dose, User}
import org.joda.time.DateTimeZone
import play.api.i18n.{I18nSupport, MessagesApi}
import play.api.mvc.Controller

import scala.concurrent.ExecutionContext

/**
 * The patient controller
 */
class PatientController @Inject()(val messagesApi: MessagesApi, val env: Environment[User, CookieAuthenticator], val doseDAO: DoseDAO, credentialsProvider: CredentialsProvider, prescriptionDAO: PrescriptionDAO, timeZone: DateTimeZone)
                                 (implicit ec: ExecutionContext) extends Silhouette[User, CookieAuthenticator] with Controller with I18nSupport {


//  /**
//   * Handles the index action for the select patient.
//   * Only authenticated prescribers can access this page, otherwise the user is redirected to the sign in page.
//   *
//   * @return The page to display.
//   */
//  def index = SecuredAction(AuthorizedWithUserType("models.Prescriber")).async { implicit request =>
//    Future.successful(Ok(views.html.selectPatient(request.identity)))
//  }
//
//  /**
//   * A REST endpoint that gets all the patients as JSON.
//   */
//  def getPatients = Action.async {
//    ptDAO.list().map { patients =>
//      Ok(Json.toJson(patients))
//    }
//  }

  def addDose(dose: Dose) = {
    doseDAO.save(dose)
  }
}
