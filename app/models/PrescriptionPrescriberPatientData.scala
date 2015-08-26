package models

/**
 * Created by Jo on 26/08/2015.
 */
case class PrescriptionPrescriberPatientData (
                                             prescription: Option[Prescription],
                                             prescriberName: String,
                                             patient: Patient
                                               )
