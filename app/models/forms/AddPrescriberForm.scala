package models.forms

import play.api.data.Form
import play.api.data.Forms._

/**
 * The create prescriber form.
 *
 * Generally for forms, you should define separate objects to your models, since forms very often need to present data
 * in a different way to your models.  In this case, it doesn't make sense to have an id parameter in the form, since
 * that is generated once it's created.
 */
object AddPrescriberForm {

  /**
   * The mapping for the add prescriber form.
   */
  val prescriberForm = Form {
    mapping(
      "title" -> nonEmptyText,
      "firstName" -> nonEmptyText,
      "surname" -> nonEmptyText,
      "email" -> email,
      "password" -> nonEmptyText
    )(Data.apply)(Data.unapply)
  }

  case class Data(title: String, firstName: String, surname: String, email: String, password: String)
}
