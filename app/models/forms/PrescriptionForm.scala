package models.forms

import play.api.data.Form
import play.api.data.Forms._

object PrescriptionForm {

  /**
   * The mapping for the patient form.
   */
  val form: Form[Data] = Form {
    mapping(
      "ptName" -> nonEmptyText,
      "datePrescribed" -> nonEmptyText,
      "MRdose" -> nonEmptyText,
      "breakthroughDose" -> nonEmptyText
    )(Data.apply)(Data.unapply)
  }

  /**
   * The create patient form.
   *
   * Generally for forms, you should define separate objects to your models, since forms very often need to present data
   * in a different way to your models.  In this case, it doesn't make sense to have an id parameter in the form, since
   * that is generated once it's created.
   */
  case class Data(ptName: String, datePrescribed: String, MRdose: String, breakthroughDose: String)
}

