package controllers

import javax.inject.Inject

import com.mohiva.play.silhouette.api._
import com.mohiva.play.silhouette.api.exceptions.ProviderException
import com.mohiva.play.silhouette.api.repositories.AuthInfoRepository
import com.mohiva.play.silhouette.api.util.{Clock, Credentials}
import com.mohiva.play.silhouette.impl.authenticators.CookieAuthenticator
import com.mohiva.play.silhouette.impl.exceptions.IdentityNotFoundException
import com.mohiva.play.silhouette.impl.providers._
import models.User
import models.forms.SignInForm
import models.services.UserService
import play.api.Configuration
import play.api.i18n.{Messages, MessagesApi}
import play.api.libs.concurrent.Execution.Implicits._
import play.api.mvc.Action

import scala.concurrent.Future
import scala.language.postfixOps

/**
 * The credentials auth controller.
 *
 * @param messagesApi The Play messages API.
 * @param env The Silhouette environment.
 * @param userService The user UserService implementation.
 * @param authInfoRepository The auth info repository implementation.
 * @param credentialsProvider The credentials provider.
 * @param configuration The Play configuration.
 * @param clock The clock instance.
 */
class CredentialsAuthController @Inject() (
  val messagesApi: MessagesApi,
  val env: Environment[User, CookieAuthenticator],
  userService: UserService,
  authInfoRepository: AuthInfoRepository,
  credentialsProvider: CredentialsProvider,
  configuration: Configuration,
  clock: Clock)
  extends Silhouette[User, CookieAuthenticator] {

  /**
   * Authenticates a user against the credentials provider.
   *
   * @return The result to display.
   */
  def authenticate = Action.async { implicit request =>
    SignInForm.form.bindFromRequest.fold(
      form => Future.successful(BadRequest(views.html.login(form, "active", "", "active in", "fade"))),
      data => {
        val credentials = Credentials(data.email, data.password)
        credentialsProvider.authenticate(credentials).flatMap { loginInfo =>
          userService.retrieve(loginInfo).flatMap {
            case Some(user) =>
              val result = user.getClass.getTypeName match {
                case "models.Administrator" => Redirect(routes.AdministratorController.index())
                case "models.Prescriber" => Redirect(routes.PrescriberController.index())
              }
              //val c = configuration.underlying
              env.authenticatorService.create(loginInfo).map {
                case authenticator => authenticator
                  //create the cookie -> v = cookie, result = redirect as above
              }.flatMap { authenticator =>
                env.eventBus.publish(LoginEvent(user, request, request2Messages))
                env.authenticatorService.init(authenticator).flatMap { v =>
                  env.authenticatorService.embed(v, result)
                }
              }
            case None => Future.failed(new IdentityNotFoundException("Couldn't find user"))
          }
        }.recover {
          case e: ProviderException =>
            Redirect(routes.ApplicationController.login()).flashing("error" -> Messages("invalid.credentials"))
        }
      }
    )
  }
}
