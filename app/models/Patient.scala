package models

import play.api.libs.json.Json
import play.api.mvc.QueryStringBindable

/**
 * The patient object.
 *
 * @param hospitalNumber the hospital number of the patient.
 * @param title the title of the patient.
 * @param firstName the first name of the patient.
 * @param surname the last name of the patient.
 */
case class Patient(
                    hospitalNumber: String,
                    title: String,
                    firstName: String,
                    surname: String,
                    dob: String)


//The QueryStringBinder for the Patient class
object Patient{
  implicit val patientFormat = Json.format[Patient]

  implicit def queryStringBinder(implicit stringBinder: QueryStringBindable[String]) = new QueryStringBindable[Patient]{
    override def bind(key: String, params: Map[String, Seq[String]]): Option[Either[String, Patient]] = {
      for{
        ptHospitalNumber <- stringBinder.bind(key + ".hospitalNumber", params)
        ptTitle <- stringBinder.bind(key + ".title", params)
        ptFirstName <- stringBinder.bind(key + ".firstName", params)
        ptSurname <- stringBinder.bind(key + ".surname", params)
        ptDob <- stringBinder.bind(key + ".dob", params)
      }yield{
        (ptHospitalNumber, ptTitle, ptFirstName, ptSurname, ptDob) match {
          case (Right(hospitalNumber), Right(title), Right(firstName), Right(surname), Right(dob)) =>
            Right(Patient(hospitalNumber, title, firstName, surname, dob))
          case _ => Left("Unable to bind Patient")
        }
      }
    }
    override def unbind(key: String, patient: Patient): String = {
      stringBinder.unbind(key + ".hospitalNumber", patient.hospitalNumber) + "&" + stringBinder.unbind(key + ".title", patient.title) + "&" + stringBinder.unbind(key + ".firstName", patient.firstName) + "&" + stringBinder.unbind(key + ".surname", patient.surname) + "&" + stringBinder.unbind(key + ".dob", patient.dob)
    }
  }
}
