package controllers

import java.util.UUID
import javax.inject.Inject

import com.mohiva.play.silhouette.api._
import com.mohiva.play.silhouette.api.repositories.AuthInfoRepository
import com.mohiva.play.silhouette.api.util.PasswordHasher
import com.mohiva.play.silhouette.impl.authenticators.CookieAuthenticator
import com.mohiva.play.silhouette.impl.providers._
import models.forms.{AddPatientForm, SignUpForm}
import models.services.UserService
import models.utils.{DropdownUtils, AuthorizedWithUserType}
import models.{Administrator, Prescriber, User}
import play.api.i18n.{Messages, MessagesApi}
import play.api.libs.concurrent.Execution.Implicits._
import play.api.mvc.Action

import scala.concurrent.Future

/**
 * The sign up controller.
 *
 * @param messagesApi The Play messages API.
 * @param env The Silhouette environment.
 * @param userService The user service implementation.
 * @param authInfoRepository The auth info repository implementation.
 * @param passwordHasher The password hasher implementation.
 */
class SignUpController @Inject() (
  val messagesApi: MessagesApi,
  val env: Environment[User, CookieAuthenticator],
  userService: UserService,
  authInfoRepository: AuthInfoRepository,
  passwordHasher: PasswordHasher)
  extends Silhouette[User, CookieAuthenticator] {


  /**
   * Registers a new administrator or prescriber.
   * Only authenticated administrators can access this page, otherwise the user is redirected to the sign in page.
   *
   * @param userType the type of user to register.
   * @return The result to display.
   */
  def signUp(userType: String) = SecuredAction(AuthorizedWithUserType("models.Administrator")).async { implicit request =>
    SignUpForm.form.bindFromRequest.fold(
      form => Future.successful(BadRequest(views.html.adminhome(form, request.identity, DropdownUtils.getTitles, "active", "", "active in", "fade"))),
      data => {
        val loginInfo = LoginInfo(CredentialsProvider.ID, data.email)
        userService.retrieve(loginInfo).flatMap {
          case Some(user) =>
            Future.successful(Redirect(routes.ApplicationController.signUp()).flashing("error" -> Messages("user.exists")))
          case None =>
            val authInfo = passwordHasher.hash(data.password)
            val user = getUser(userType, data, loginInfo)
            for {
              user <- userService.save(user)
              authInfo <- authInfoRepository.add(loginInfo, authInfo)
            //shouldn't need below data -> it creates cookie info to continue as the user added
              //authenticator <- env.authenticatorService.create(loginInfo)
              //value <- env.authenticatorService.init(authenticator)
              //result <- env.authenticatorService.embed(value, Redirect(routes.ApplicationController.index()))
            } yield {
              env.eventBus.publish(SignUpEvent(user, request, request2Messages))
              //env.eventBus.publish(LoginEvent(user, request, request2Messages))
              //result
              Redirect(routes.AdministratorController.index()).flashing("success" -> Messages("user.added"))
            }
        }
      }
    )
  }

  /**
   * Creates a new user according to the specified userType.
   *
   * @param userType the type of user required
   * @param data the data from the SignUpForm
   * @param loginInfo the users loginInfo
   * @return an instance of a User.
   */
  def getUser(userType: String, data: SignUpForm.Data, loginInfo: LoginInfo): User = userType match{
    case "administrator" => Administrator(
      userID = UUID.randomUUID(),
      loginInfo = loginInfo,
      title = Some(data.title),
      firstName = Some(data.firstName),
      lastName = Some(data.lastName),
      email = Some(data.email)
    )
    case "prescriber" => Prescriber(
      userID = UUID.randomUUID(),
      loginInfo = loginInfo,
      title = Some(data.title),
      firstName = Some(data.firstName),
      lastName = Some(data.lastName),
      email = Some(data.email)
    )
  }

  /**
   * The add prescriber action.
   *
   * This is asynchronous, since we're invoking the asynchronous methods on PrescriberRepository.
   */
  def addPatient = SecuredAction(AuthorizedWithUserType("models.Prescriber")){ implicit request =>
    // Bind the form first, then fold the result, passing a function to handle errors, and a function to handle success.
    AddPatientForm.form.bindFromRequest.fold(
      // The error function. We return the index page with the error form, which will render the errors.
      // We also wrap the result in a successful future, since this action is synchronous, but we're required to return
      // a future because the person creation function returns a future.
      form => {
        Future.successful(Ok(views.html.addPatient(form, request.identity, DropdownUtils.getTitles, DropdownUtils.getDaysOfMonth, DropdownUtils.getMonths, DropdownUtils.getYears))
      },
      // There were no errors in the form, so create the person.
      patient => {
        repo.create(patient.firstName, patient.surname, dobToString(patient.dobDayOfMonth, patient.dobMonth, patient.dobYear), patient.hospitalNumber).map { _ =>
          // If successful, we simply redirect to the index page.
          Redirect(routes.PrescriberController.index()).flashing("success" -> Messages("user.added"))
        }
      }
    )
  }

  /**
   * @return the patients dob formatted as a single String
   */
  def dobToString(day: String, month: String, year: String): String = {
    day + "-" + month + "-" + year
  }

//  /**
//   * Registers a new administrator or prescriber.
//   * Only authenticated administrators can access this page, otherwise the user is redirected to the sign in page.
//   *
//   * @param userType the type of user to register.
//   * @return The result to display.
//   */
//  def addPatient(userType: String) = SecuredAction(AuthorizedWithUserType("models.Prescriber")) { implicit request =>
//    AddPatientForm.form.bindFromRequest.fold(
//      form => BadRequest(views.html.addPatient(form, request.identity, DropdownUtils.getTitles, DropdownUtils.getDaysOfMonth, DropdownUtils.getMonths, DropdownUtils.getYears)),
//      data => {  Redirect(routes.PrescriberController.index()).flashing("success" -> Messages("user.added"))
//
//        val loginInfo = LoginInfo(CredentialsProvider.ID, data.hospitalNumber)
//        userService.retrieve(loginInfo).flatMap {
//          case Some(user) =>
//            Future.successful(Redirect(routes.ApplicationController.signUp()).flashing("error" -> Messages("user.exists")))
//          case None =>
//            val authInfo = passwordHasher.hash(data.password)
//            val user = getUser(userType, data, loginInfo)
//            for {
//              user <- userService.save(user)
//              authInfo <- authInfoRepository.add(loginInfo, authInfo)
//            //shouldn't need below data -> it creates cookie info to continue as the user added
//            //authenticator <- env.authenticatorService.create(loginInfo)
//            //value <- env.authenticatorService.init(authenticator)
//            //result <- env.authenticatorService.embed(value, Redirect(routes.ApplicationController.index()))
//            } yield {
//              env.eventBus.publish(SignUpEvent(user, request, request2Messages))
//              //env.eventBus.publish(LoginEvent(user, request, request2Messages))
//              //result
//              Redirect(routes.AdministratorController.index()).flashing("success" -> Messages("user.added"))
//            }
//        }
//      }
//    )
//  }
}
