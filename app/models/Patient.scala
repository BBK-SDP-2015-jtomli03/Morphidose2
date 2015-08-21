package models

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
