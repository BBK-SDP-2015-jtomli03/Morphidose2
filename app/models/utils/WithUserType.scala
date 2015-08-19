package models.utils


import com.mohiva.play.silhouette.api.Authorization
import com.mohiva.play.silhouette.impl.authenticators.CookieAuthenticator
import models.User
import play.api.i18n.Messages
import play.api.mvc.Request

import scala.concurrent.Future

case class AuthorizedWithUserType(userType: String) extends Authorization[User, CookieAuthenticator] {
  def isAuthorized[B](user: User, authenticator: CookieAuthenticator)(implicit request: Request[B], messages: Messages) = {
    Future.successful(user.getClass.getTypeName.equals(userType))
  }
}
