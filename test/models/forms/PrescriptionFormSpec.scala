package models.forms

import models.forms.PrescriptionForm.Data
import org.specs2.mutable.Specification

class PrescriptionFormSpec extends Specification {
  val MRDrug = "Zomorph"
  val MRDose = "10.0mg"
  val breakthroughDrug = "Oramorph"
  val breakthroughDose = "2.5mg"

  "the validate function" should {
    "return the correct Data if the data is valid" in {
      PrescriptionForm.validate(MRDrug, MRDose, breakthroughDrug, breakthroughDose) must_== Some(Data(MRDrug, MRDose, breakthroughDrug, breakthroughDose))
    }
    "return None if the MRDrug isn't entered" in {
      PrescriptionForm.validate("Drug", MRDose, breakthroughDrug, breakthroughDose) must_== None
    }
    "return None if the MRDose isn't entered" in {
      PrescriptionForm.validate(MRDrug, "Dose", breakthroughDrug, breakthroughDose) must_== None
    }
    "return None if the breakthroughDrug isn't entered" in {
      PrescriptionForm.validate(MRDrug, MRDose, "Drug", breakthroughDose) must_== None
    }
    "return None if the breakthroughDose isn't entered" in {
      PrescriptionForm.validate(MRDrug, MRDose, breakthroughDrug, "Dose") must_== None
    }
  }
}
