package models

case class PrescriptionPrescriberPatientData (
                                             prescription: Option[Prescription],
                                             prescriberName: String,
                                             patient: Patient
                                               )
