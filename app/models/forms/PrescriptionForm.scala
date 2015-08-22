package models.forms

import play.api.data.Form
import play.api.data.Forms._
import play.api.data.format.Formats._

object PrescriptionForm {

  /**
   * The mapping for the prescription form.
   */
  val form: Form[Data] = Form {
    mapping(
      "MRDrug" -> nonEmptyText,
      "MRDose" -> of(doubleFormat),
      "breakthroughDrug" -> nonEmptyText,
      "breakthroughDose" -> of(doubleFormat)
    )(Data.apply)(Data.unapply)
  }

  /**
   * The form data.
   *
   * @param MRDrug the name of the MR drug prescribed.
   * @param MRDose the dose of the MR drug prescribed.
   * @param breakthroughDrug the name of the breakthrough drug prescribed.
   * @param breakthroughDose the dose of the breakthrough drug prescribed.
   */
  case class Data(MRDrug: String, MRDose: Double, breakthroughDrug: String, breakthroughDose: Double)
}

