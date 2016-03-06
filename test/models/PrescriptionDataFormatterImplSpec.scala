package models

import java.sql.Timestamp

import models.daos.{DoseDAO, PrescriberDAO}
import org.joda.time.{DateTime, DateTimeZone}
import org.specs2.mock._
import org.specs2.mutable._

import scala.concurrent.{ExecutionContext, Future}

class PrescriptionDataFormatterImplSpec(implicit ec: ExecutionContext) extends Specification with Mockito{
  val timezone = DateTimeZone.forID("Europe/London")
  val mockPrescriberDAO = mock[PrescriberDAO]
  val mockDoseDAO = mock[DoseDAO]
  val dataFormatter = new PrescriptionDataFormatterImpl(mockPrescriberDAO, mockDoseDAO, timezone)
  val dateFrom = new Timestamp(new DateTime().withYear(2015).withMonthOfYear(1).withDayOfMonth(1).getMillis)
  val name = "Eric Smith"
  val userId = "1"
  val invalidUserId = "0"
  val emptyString = ""
  val mrDrug = "morphine"
  val breakthroughDrug = "oramorph"
  val mrDose = 10.0
  val breakthroughDose = 2.5
  val prescription = Prescription("1",userId,new Timestamp(new DateTime().withYear(2015).withMonthOfYear(1).withDayOfMonth(1).getMillis),mrDrug, mrDose, breakthroughDrug, breakthroughDose)
  val prescriptionNoPrescriber = Prescription(userId,invalidUserId,new Timestamp(new DateTime().withYear(2015).withMonthOfYear(1).withDayOfMonth(1).getMillis),mrDrug, mrDose, breakthroughDrug, breakthroughDose)
  val mockFutureName = Future.successful[Option[String]](Some(name))
  mockPrescriberDAO.findPrescriberName(userId) returns mockFutureName
  val mockFutureInvalidName = Future.successful[Option[String]](None)
  mockPrescriberDAO.findPrescriberName(invalidUserId) returns mockFutureInvalidName
  val mockNumberOfBreakthroughDoses = 10
  val mockFutureNumberOfBreakthroughDoses = Future.successful[Int](mockNumberOfBreakthroughDoses)
  mockDoseDAO.countBreakthroughDoses(userId, prescription.date) returns mockFutureNumberOfBreakthroughDoses
  val spyPrescriptionDataFormatter = spy(dataFormatter)
  spyPrescriptionDataFormatter.getTodaysDateTime returns new DateTime().withYear(2015).withMonthOfYear(1).withDayOfMonth(3)

  "the getDateAsString function" should {
    "returns a String date of the correct format if the date is the beginning of the month" in {
      val timestamp = new Timestamp(new DateTime().withYear(2015).withMonthOfYear(1).withDayOfMonth(1).getMillis)
      val formattedDate = "Thu, 1 Jan 2015"
      dataFormatter.getDateAsString(timestamp) must_== formattedDate
    }
    "returns a String date of the correct format if the date is the end of the month" in {
      val timestamp = new Timestamp(new DateTime().withYear(2015).withMonthOfYear(1).withDayOfMonth(31).getMillis)
      val formattedDate = "Sat, 31 Jan 2015"
      dataFormatter.getDateAsString(timestamp) must_== formattedDate
    }
    "returns a String date of the correct format if the date is the middle of the month" in {
      val timestamp = new Timestamp(new DateTime().withYear(2015).withMonthOfYear(3).withDayOfMonth(15).getMillis)
      val formattedDate = "Sun, 15 Mar 2015"
      dataFormatter.getDateAsString(timestamp) must_== formattedDate
    }
    "returns a String date of the correct format for the 29th Feb in a leap year" in {
      val timestamp = new Timestamp(new DateTime().withYear(2016).withMonthOfYear(2).withDayOfMonth(29).getMillis)
      val formattedDate = "Mon, 29 Feb 2016"
      dataFormatter.getDateAsString(timestamp) must_== formattedDate
    }
  }

  "the numberOfDays function" should {
    "returns zero if the number of full days between the given dates is zero" in {
      val dateTo = new DateTime(new Timestamp(new DateTime().withYear(2015).withMonthOfYear(1).withDayOfMonth(1).withHourOfDay(12).getMillis).getTime)
      dataFormatter.numberOfDays(dateFrom, dateTo) must_== 0
    }
    "returns 4 if 4 full days between the dates" in {
      val dateTo = new DateTime(new Timestamp(new DateTime().withYear(2015).withMonthOfYear(1).withDayOfMonth(5).getMillis).getTime)
      dataFormatter.numberOfDays(dateFrom, dateTo) must_== 4
    }
    "returns 7 if there is a week between dates" in {
      val dateTo = new DateTime(new Timestamp(new DateTime().withYear(2015).withMonthOfYear(1).withDayOfMonth(8).getMillis).getTime)
      dataFormatter.numberOfDays(dateFrom, dateTo) must_== 7
    }
    "returns 32 if the number of days between dates are 32 and so into the following month" in {
      val dateTo = new DateTime(new Timestamp(new DateTime().withYear(2015).withMonthOfYear(2).withDayOfMonth(2).getMillis).getTime)
      dataFormatter.numberOfDays(dateFrom, dateTo) must_== 32
    }
    "returns 365 if it has been a year between dates and it was a non-leap year" in {
      val dateTo = new DateTime(new Timestamp(new DateTime().withYear(2016).withMonthOfYear(1).withDayOfMonth(1).getMillis).getTime)
      dataFormatter.numberOfDays(dateFrom, dateTo) must_== 365
    }
    "returns 366 if it has been a year between dates and it was a leap year" in {
      val dateFrom = new Timestamp(new DateTime().withYear(2015).withMonthOfYear(3).withDayOfMonth(1).getMillis)
      val dateTo = new DateTime(new Timestamp(new DateTime().withYear(2016).withMonthOfYear(3).withDayOfMonth(1).getMillis).getTime)
      dataFormatter.numberOfDays(dateFrom, dateTo) must_== 366
    }
  }

  "the getPrescriberName function" should {
    "return the correct name if the prescriber exists" in {
      dataFormatter.getPrescriberName(prescription) must_== name
    }
    "return an empty String if the prescriber doesn't exist" in {
      dataFormatter.getPrescriberName(prescriptionNoPrescriber) must_== emptyString
    }
  }

  "the getInstanceOfPrescriptionData function" should {
    "return a PrescriptionData object with the correct prescriberName" in {
      dataFormatter.getInstanceOfPrescriptionData(prescription).prescriber must_== name
    }
    "return a PrescriptionData object with the correct date" in {
      val formattedDate = "Thu, 1 Jan 2015"
      dataFormatter.getInstanceOfPrescriptionData(prescription).date must_== formattedDate
    }
    "return a PrescriptionData object with the correct MRDrug" in {
      dataFormatter.getInstanceOfPrescriptionData(prescription).MRDrug must_== prescription.MRDrug
    }
    "return a PrescriptionData object with the correct MR dose" in {
      val MRDoseAsString = "10.0mg"
      dataFormatter.getInstanceOfPrescriptionData(prescription).MRDose must_== MRDoseAsString
    }
    "return a PrescriptionData object with the correct breakthroughDrug" in {
      dataFormatter.getInstanceOfPrescriptionData(prescription).breakthroughDrug must_== prescription.breakthroughDrug
    }
    "return a PrescriptionData object with the correct breakthrough dose" in {
      val breakthroughDoseAsString = "2.5mg"
      dataFormatter.getInstanceOfPrescriptionData(prescription).breakthroughDose must_== breakthroughDoseAsString
    }
  }

  "the getDoseTitrationData function" should {
    "return a PrescriptionData object with the correct prescriberName" in {
      spyPrescriptionDataFormatter.getDoseTitrationData(prescription).numOfBreakthroughDoses must_== mockNumberOfBreakthroughDoses.toString
    }
    "return a PrescriptionData object with the correct daysSinceCurrentPrescription" in {
      val mockDaysSinceCurrentPrescription = 2
      spyPrescriptionDataFormatter.getDoseTitrationData(prescription).daysSinceCurrentPrescription must_== mockDaysSinceCurrentPrescription.toString
    }
    "return a PrescriptionData object with the correct average24hrBreakthroughDose" in {
      val mockAverage24hrBreakthroughDose = "12.5mg"
      spyPrescriptionDataFormatter.getDoseTitrationData(prescription).average24hrBreakthroughDose must_== mockAverage24hrBreakthroughDose
    }
    "return a PrescriptionData object with the correct totalDailyMRDose" in {
      val mockTotalDailyMRDose = "20.0mg"
      spyPrescriptionDataFormatter.getDoseTitrationData(prescription).totalDailyMRDose must_== mockTotalDailyMRDose
    }
    "return a PrescriptionData object with the correct averageTotalDailyDose" in {
      val mockAverageTotalDailyDose = "32.5mg"
      spyPrescriptionDataFormatter.getDoseTitrationData(prescription).averageTotalDailyDose must_== mockAverageTotalDailyDose
    }
    "return a PrescriptionData object with the correct MRDoseTitration" in {
      val mockMRDoseTitration = "15.0mg"
      spyPrescriptionDataFormatter.getDoseTitrationData(prescription).mrDoseTitration must_== mockMRDoseTitration
    }
    "return a PrescriptionData object with the correct breakthroughDoseTitration" in {
      val mockBreakthroughDoseTitration = "5.0mg"
      spyPrescriptionDataFormatter.getDoseTitrationData(prescription).breakthroughDoseTitration must_== mockBreakthroughDoseTitration
    }
  }

}
