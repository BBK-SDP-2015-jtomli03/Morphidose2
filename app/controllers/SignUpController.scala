package controllers

import java.util.UUID
import javax.inject.Inject

import com.mohiva.play.silhouette.api._
import com.mohiva.play.silhouette.api.repositories.AuthInfoRepository
import com.mohiva.play.silhouette.api.util.PasswordHasher
import com.mohiva.play.silhouette.impl.authenticators.CookieAuthenticator
import com.mohiva.play.silhouette.impl.providers._
import models.forms.SignUpForm
import models.services.UserService
import models.utils.{AuthorizedWithUserType, DropdownUtils}
import models.{Administrator, Prescriber, User}
import play.api.i18n.{Messages, MessagesApi}
import play.api.libs.concurrent.Execution.Implicits._

import scala.concurrent.Future


/**
 * The sign up controller.
 *
 * @param messagesApi The Play messages API.
 * @param env The Silhouette environment.
 * @param userService The user UserService implementation.
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
   * Only authenticated administrators can access this page, otherwise the user is redirected to admin home page.
   *
   * @param userType the type of user to register.
   * @return The result to display.
   */
  def addUser(userType: String) = SecuredAction(AuthorizedWithUserType("models.Administrator")).async { implicit request =>
    SignUpForm.form.bindFromRequest.fold(
      form => Future.successful(BadRequest(views.html.adminhome(form, request.identity, DropdownUtils.getTitles, "active", "", "active in", "fade"))),
      data => {
        val loginInfo = LoginInfo(CredentialsProvider.ID, data.email)
          userService.exists(loginInfo) match {
            case true =>
              Future.successful(Redirect(routes.AdministratorController.index()).flashing("error" -> Messages("user.exists")))
            case false =>
              val authInfo = passwordHasher.hash(data.password)
              val user = createUser(userType, data, loginInfo)
              for {
                user <- userService.save(user, userType)
                authInfo <- authInfoRepository.add(loginInfo, authInfo)
              } yield {
                env.eventBus.publish(SignUpEvent(user, request, request2Messages))
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
  def createUser(userType: String, data: SignUpForm.Data, loginInfo: LoginInfo): User = userType match{
    case "administrator" => Administrator(
      userID = UUID.randomUUID(),
      loginInfo = loginInfo,
      title = data.title,
      firstName = data.firstName,
      lastName = data.lastName,
      email = data.email
    )
    case "prescriber" => Prescriber(
      userID = UUID.randomUUID(),
      loginInfo = loginInfo,
      title = data.title,
      firstName = data.firstName,
      lastName = data.lastName,
      email = data.email
    )
  }

}
