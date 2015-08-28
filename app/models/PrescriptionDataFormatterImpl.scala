package models

import java.sql.Timestamp
import java.text.SimpleDateFormat
import javax.inject.Inject

import models.daos.{DoseDAO, PrescriberDAO}
import org.joda.time.{Days, DateTimeZone, DateTime}

import scala.concurrent.duration._
import scala.concurrent.{Await, ExecutionContext}

trait PrescriptionDataFormatter{

  /**
   * Retrieves the prescribers title and full name for a given prescription.
   *
   * @param prescription the prescription
   * @return the prescribers title and full name.
   */
  def getPrescriberName(prescription: Prescription): String

  /**
   * Creates an instance of PrescriptionData for a given prescription.
   *
   * @param prescription the prescription
   * @return the PrescriptionData.
   */
  def getInstanceOfPrescriptionData(prescription: Prescription): PrescriptionData

}

class PrescriptionDataFormatterImpl @Inject()(prescriberDAO: PrescriberDAO, doseDAO: DoseDAO, timeZone: DateTimeZone)(implicit ec: ExecutionContext) extends PrescriptionDataFormatter{

  /**
   * Retrieves the prescribers title and full name for a given prescription.
   *
   * @param prescription the prescription
   * @return the prescribers title and full name.
   */
    override def getPrescriberName(prescription: Prescription) = {
      Await.result(
        prescriberDAO.findPrescriberName(prescription.prescriberID).map {
                      case Some(name) => name
                      case None => ""
                      },
        5.seconds
      )
    }

  /**
   * Creates an instance of PrescriptionData for a given prescription.
   *
   * @param prescription the prescription
   * @return the PrescriptionData.
   */
    def getInstanceOfPrescriptionData(prescription: Prescription) = {
      val prescriberName = getPrescriberName(prescription: Prescription)
      PrescriptionData(prescriberName, getDateAsString(prescription.date), prescription.MRDrug, getDoseAsString(prescription.MRDose), prescription.breakthroughDrug, getDoseAsString(prescription.breakthroughDose))
    }

  /**
   * Creates the dose titration data for a given patient.
   *
   * @param prescription the patients current prescription.
   * @return the dose titration data.
   */
    def getDoseTitrationData(prescription: Prescription) = {
      val numOfBreakthroughDoses = Await.result(doseDAO.countBreakthroughDoses(prescription.ptHospitalNumber, prescription.date), 5.seconds)
      val daysSinceCurrentPrescription = numberOfDays(prescription.date)
      val average24hrBreakthroughDose = DoseCalculator.average24hrBreakthroughDose(numOfBreakthroughDoses, prescription.breakthroughDose, daysSinceCurrentPrescription)
      val totalDailyMRDose = DoseCalculator.totalDailyMRDose(prescription.MRDose)
      val averageTotalDailyDose = DoseCalculator.averageTotalDailyDose(totalDailyMRDose, average24hrBreakthroughDose)
      val MRDoseTitration = DoseCalculator.MRDoseTitration(averageTotalDailyDose)
      val breakthroughDoseTitration = DoseCalculator.breakthroughDoseTitration(averageTotalDailyDose, prescription.breakthroughDose, numOfBreakthroughDoses)
      DoseTitrationData(numOfBreakthroughDoses.toString, daysSinceCurrentPrescription.toString, getDoseAsString(average24hrBreakthroughDose), getDoseAsString(totalDailyMRDose), getDoseAsString(averageTotalDailyDose), getDoseAsString(MRDoseTitration), getDoseAsString(breakthroughDoseTitration))
  }

  /**
   * Converts a timestamp to a formatted string.
   *
   * @param timestamp the timestamp
   * @return the formatted timestamp
   */
  def getDateAsString(timestamp: Timestamp) = {
    val dateFormat = new SimpleDateFormat("dd-MM-yyyy")
    dateFormat.format(timestamp)
  }

  /**
   * Converts a dose for a particular drug to a formatted string.
   *
   * @param dose the dose
   * @return String the formatted dose
   */
  def getDoseAsString(dose: Double) = {
    dose + "mg"
  }

  /**
   * Calculates the number of full days between a timestamp and the current day.
   *
   * @param date the timestamp
   * @return Int the number of days
   */
  def numberOfDays(date: Timestamp) = {
    Days.daysBetween(new DateTime(date.getTime()).toLocalDate(), new DateTime().withZone(timeZone).toLocalDate()).getDays - 2 match {
      case days if days < 0 => 0
      case days => days
    }
  }


}
