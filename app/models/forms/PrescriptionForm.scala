package models.forms

import play.api.data.Form
import play.api.data.Forms._

object PrescriptionForm {

  /**
   * The mapping for the prescription form.
   */
  val form: Form[Data] = Form {
    mapping(
      "MRDrug" -> nonEmptyText,
      "MRDose" -> nonEmptyText,
      "breakthroughDrug" -> nonEmptyText,
      "breakthroughDose" -> nonEmptyText
    )(Data.apply)(Data.unapply)verifying("This field is required", fields => fields match {
      case userData => validate(userData.MRDrug, userData.MRDose, userData.breakthroughDrug, userData.breakthroughDose).isDefined
    })
  }

  /**
   * Validates whether the drugs and doses have been entered correctly.
   * @return None if any haven't been entered
   * @return Some() if the drugs and doses have been entered correctly
   */
  def validate(MRDrug: String, MRDose: String, breakthroughDrug: String, breakthroughDose: String) = {
    (MRDrug, MRDose, breakthroughDrug, breakthroughDose) match {
      case("Drug", _, _, _) => None
      case(_, "Dose", _, _) => None
      case(_, _, "Drug",_) => None
      case(_, _, _ , "Dose") => None
      case(_, _, _, _) => Some(Data(MRDrug, MRDose, breakthroughDrug, breakthroughDose))
    }
  }

  /**
   * The form data.
   *
   * @param MRDrug the name of the MR drug prescribed.
   * @param MRDose the dose of the MR drug prescribed.
   * @param breakthroughDrug the name of the breakthrough drug prescribed.
   * @param breakthroughDose the dose of the breakthrough drug prescribed.
   */
  case class Data(MRDrug: String, MRDose: String, breakthroughDrug: String, breakthroughDose: String)
}

