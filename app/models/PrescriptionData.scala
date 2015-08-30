package models

/**
 * The prescription data object.
 *
 * @param prescriber the prescribers ID.
 * @param date the date prescribed.
 * @param MRDrug the name of the MR drug prescribed.
 * @param MRDose the dose of the MR drug prescribed.
 * @param breakthroughDrug the name of the breakthrough drug prescribed.
 * @param breakthroughDose the dose of the breakthrough drug prescribed.
 */
case class PrescriptionData (
                              prescriber: String,
                              date: String,
                              MRDrug: String,
                              MRDose: String,
                              breakthroughDrug: String,
                              breakthroughDose: String)

