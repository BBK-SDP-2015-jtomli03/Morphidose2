package models

import java.util.UUID

import com.mohiva.play.silhouette.api.{ Identity, LoginInfo }

sealed trait User extends Identity{
  val userID: UUID
  val loginInfo: LoginInfo
  val firstName: Option[String]
  val lastName: Option[String]
  val fullName: Option[String]
  val email: Option[String]
}

/**
 * The user object.
 *
 * @param userID The unique ID of the user.
 * @param loginInfo The linked login info.
 * @param firstName Maybe the first name of the authenticated user.
 * @param lastName Maybe the last name of the authenticated user.
 * @param fullName Maybe the full name of the authenticated user.
 * @param email Maybe the email of the authenticated provider.
 */
case class Administrator(
  userID: UUID,
  loginInfo: LoginInfo,
  firstName: Option[String],
  lastName: Option[String],
  fullName: Option[String],
  email: Option[String]) extends User with Identity
