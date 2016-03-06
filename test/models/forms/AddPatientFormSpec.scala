package controllers.models.forms

import models.forms.AddPatientForm
import models.forms.AddPatientForm.Data
import org.specs2.mutable.Specification


class AddPatientFormSpec extends Specification{
  val hospitalNumber = "1"
  val title = "Miss"
  val firstName = "Bridget"
  val surname = "Jones"
  val day = "15"
  val month = "JUN"
  val year = "1970"


  "the validate function" should {
    "return the correct Data if the date is valid" in {
      AddPatientForm.validate(hospitalNumber, title, firstName, surname, day, month, year) must_== Some(Data(hospitalNumber, title, firstName, surname, day, month, year))
    }
    "return None if the day isn't entered" in {
      AddPatientForm.validate(hospitalNumber, title, firstName, surname, "Day", month, year) must_== None
    }
    "return None if the month isn't entered" in {
      AddPatientForm.validate(hospitalNumber, title, firstName, surname, day, "Month", year) must_== None
    }
    "return None if the year isn't entered" in {
      AddPatientForm.validate(hospitalNumber, title, firstName, surname, day, month, "Year") must_== None
    }
  }
}
