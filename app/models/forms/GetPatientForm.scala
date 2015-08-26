package models.forms

import play.api.data.Form
import play.api.data.Forms._

/**
 * The get patient form.
 */
object GetPatientForm {

  /**
   * The mapping for the patient form.
   */
  val form = Form {
    mapping(
      "hospitalNumber" -> nonEmptyText
    )(Data.apply)(Data.unapply)
  }

  /**
   * The form data.
   *
   * @param hospitalNumber The patients hospital number.
   */
  case class Data(hospitalNumber: String)

}
