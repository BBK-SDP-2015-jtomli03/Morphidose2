package models

import java.sql.Timestamp

/**
 * The prescription object.
 *
 * @param ptHospitalNumber the hospital number of the patient.
 * @param prescriberID the prescribers ID.
 * @param date the date prescribed.
 * @param MRDrug the name of the MR drug prescribed.
 * @param MRDose the dose of the MR drug prescribed.
 * @param breakthroughDrug the name of the breakthrough drug prescribed.
 * @param breakthroughDose the dose of the breakthrough drug prescribed.
 */
case class Prescription(
                    ptHospitalNumber: String,
                    prescriberID: String,
                    date: Timestamp,
                    MRDrug: String,
                    MRDose: Double,
                    breakthroughDrug: String,
                    breakthroughDose: Double)
