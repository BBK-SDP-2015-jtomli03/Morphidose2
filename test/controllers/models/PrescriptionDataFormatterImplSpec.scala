package controllers.models

import java.sql.Timestamp

import models.{Prescription, PrescriptionDataFormatterImpl}
import models.daos.{DoseDAO, PrescriberDAO}
import org.joda.time.{DateTime, DateTimeZone}
import org.mockito.Mockito._
import org.specs2.mutable._

import scala.concurrent.ExecutionContext.Implicits.global

class PrescriptionDataFormatterImplSpec extends Specification{
  val timezone = DateTimeZone.forID("Europe/London")
  val mockPrescriberDAO = mock(classOf[PrescriberDAO])
  val mockDoseDAO = mock(classOf[DoseDAO])
  val dataFormatter = new PrescriptionDataFormatterImpl(mockPrescriberDAO, mockDoseDAO, timezone)

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

  val dateFrom = new Timestamp(new DateTime().withYear(2015).withMonthOfYear(1).withDayOfMonth(1).getMillis)

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

  val userId = "1"
  val name = "Eric Smith"
  val emptyString = ""
  //doReturn(name).when(mockPrescriberDAO.findPrescriberName(userId))
  val mockFindPrescriberName =
  when(mockPrescriberDAO.findPrescriberName(String.class)).withArguments(userId).thenReturn(name)
  val prescription = Prescription("1","1",new Timestamp(new DateTime().withYear(2015).withMonthOfYear(1).withDayOfMonth(1).getMillis),"morphine", 10.0, "oramorph", 2.5)
  val prescriptionNoPrescriber = Prescription("1","0",new Timestamp(new DateTime().withYear(2015).withMonthOfYear(1).withDayOfMonth(1).getMillis),"morphine", 10.0, "oramorph", 2.5)
//
//  "the getPrescriberName function" should {
//    "return the correct name if the prescriber exists" in {
//      dataFormatter.getPrescriberName(prescription) must_== name
//    }
//    "return an empty String if the prescriber doesn't exist" in {
//      dataFormatter.getPrescriberName(prescriptionNoPrescriber) must_== emptyString
//    }
//  }
}
