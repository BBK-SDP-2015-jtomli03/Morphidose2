package models.forms

import play.api.data.Form
import play.api.data.Forms._

/**
 * The create patient form.
 *
 * Generally for forms, you should define separate objects to your models, since forms very often need to present data
 * in a different way to your models.  In this case, it doesn't make sense to have an id parameter in the form, since
 * that is generated once it's created.
 */
object AddPatientForm {

  /**
   * The mapping for the patient form.
   */
  val form = Form {
    mapping(
      "hospitalNumber" -> nonEmptyText,
      "title" -> nonEmptyText,
      "firstName" -> nonEmptyText,
      "surname" -> nonEmptyText,
      "dobDayOfMonth" -> nonEmptyText,
      "dobMonth" -> nonEmptyText,
      "dobYear" -> nonEmptyText
    )(Data.apply)(Data.unapply)verifying("This field is required", fields => fields match {
      case userData => validate(userData.hospitalNumber, userData.title, userData.firstName, userData.surname, userData.dobDayOfMonth, userData.dobMonth, userData.dobYear).isDefined
    })
  }

  /**
   * Validates whether the date of birth has been entered correctly.
   * @return None if the dob hasn't been entered
   * @return Some() if the dob has been entered correctly
   */
  def validate(hospitalNumber: String, title: String, firstName: String, surname: String, dobDayOfMonth: String, dobMonth: String, dobYear: String) = {
    (dobDayOfMonth, dobMonth, dobYear) match {
      case("Day", _, _) => None
      case(_, "Month", _) => None
      case(_, _, "Year") => None
      case(_, _, _) => Some(Data(hospitalNumber, title, firstName, surname, dobDayOfMonth, dobMonth, dobYear))
    }
  }

  /**
   * The form data.
   *
   * @param hospitalNumber The patients hospital number.
   * @param title The title of the patient.
   * @param firstName The first name of a patient.
   * @param surname The last name of a patient.
   * @param dobDayOfMonth The day of the month the patient was born (eg, 15).
   * @param dobMonth The month the patient was born.
   * @param dobYear The year the patient was born.
   */
  case class Data(hospitalNumber: String, title: String, firstName: String, surname: String, dobDayOfMonth: String, dobMonth: String, dobYear: String)
}
