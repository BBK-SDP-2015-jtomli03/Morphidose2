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
   * The mapping for the add prescriber form.
   */
  /**
   * The mapping for the patient form.
   */
  val form = Form {
    mapping(
      "title" -> nonEmptyText,
      "firstName" -> nonEmptyText,
      "surname" -> nonEmptyText,
      "dobDayOfMonth" -> nonEmptyText,
      "dobMonth" -> nonEmptyText,
      "dobYear" -> nonEmptyText,
      "hospitalNumber" -> nonEmptyText
    )(Data.apply)(Data.unapply)
  }

//  /**
//   * Validates whether the date of birth has been entered
//   * @return None if the dob hasn't been entered
//   * @return Some() if the dob has been entered correctly
//   */
//  def validate(first_name: String, surname: String, dobDayOfMonth: String, dobMonth: String, dobYear: String, hospital_number: String) = {
//    (dobDayOfMonth, dobMonth, dobYear) match {
//      case("Day", _, _) => None
//      case(_, "Month", _) => None
//      case(_, _, "Year") => None
//      case(_, _, _) => Some(AddPatientForm(first_name, surname, dobDayOfMonth, dobMonth, dobYear, hospital_number))
//    }
//  }

  case class Data(title: String, firstName: String, surname: String, dobDayOfMonth: String, dobMonth: String, dobYear: String, hospitalNumber: String)
}
