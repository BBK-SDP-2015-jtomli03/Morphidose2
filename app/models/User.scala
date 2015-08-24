package models

import java.util.UUID

import com.mohiva.play.silhouette.api.{ Identity, LoginInfo }

sealed trait User extends Identity{
  val userID: UUID
  val loginInfo: LoginInfo
  val title: String
  val firstName: String
  val lastName: String
  val email: String
}

/**
 * The administrator object.
 *
 * @param userID The unique ID of the user.
 * @param loginInfo The linked login info.
 * @param title Maybe the title of the authenticated user.
 * @param firstName Maybe the first name of the authenticated user.
 * @param lastName Maybe the last name of the authenticated user.
 * @param email Maybe the email of the authenticated provider.
 */
case class Administrator(
  userID: UUID,
  loginInfo: LoginInfo,
  title: String,
  firstName: String,
  lastName: String,
  email: String) extends User with Identity

/**
 * The prescriber object.
 *
 * @param userID The unique ID of the user.
 * @param loginInfo The linked login info.
 * @param title Maybe the title of the authenticated user.
 * @param firstName Maybe the first name of the authenticated user.
 * @param lastName Maybe the last name of the authenticated user.
 * @param email Maybe the email of the authenticated provider.
 */
case class Prescriber(
                          userID: UUID,
                          loginInfo: LoginInfo,
                          title: String,
                          firstName: String,
                          lastName: String,
                          email: String) extends User with Identity


