package models

import java.util.UUID

import com.mohiva.play.silhouette.api.{ Identity, LoginInfo }

sealed trait User extends Identity{
  val userID: UUID
  val loginInfo: LoginInfo
  val title: Option[String]
  val firstName: Option[String]
  val lastName: Option[String]
  val email: Option[String]
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
  title: Option[String],
  firstName: Option[String],
  lastName: Option[String],
  email: Option[String]) extends User with Identity

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
                          title: Option[String],
                          firstName: Option[String],
                          lastName: Option[String],
                          email: Option[String]) extends User with Identity

/**
 * The patient object.
 *
 * @param userID The unique ID of the user.
 * @param loginInfo The linked login info.
 * @param title Maybe the title of the authenticated user.
 * @param firstName Maybe the first name of the authenticated user.
 * @param lastName Maybe the last name of the authenticated user.
 * @param email Maybe the email of the authenticated provider.
 */
case class Patient(
                       userID: UUID,
                       loginInfo: LoginInfo,
                       title: Option[String],
                       firstName: Option[String],
                       lastName: Option[String],
                       email: Option[String],
                       dob: String,
                       hospitalNumber: String) extends User with Identity
