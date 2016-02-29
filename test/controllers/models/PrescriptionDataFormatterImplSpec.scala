package controllers.models

import java.sql.Timestamp

import models.PrescriptionDataFormatterImpl
import models.daos.{DoseDAOImpl, PrescriberDAOImpl}
import org.joda.time.{DateTime, DateTimeZone}
import scala.concurrent.ExecutionContext.Implicits.global

class PrescriptionDataFormatterImplSpec extends org.specs2.mutable.Specification{
  val timezone = DateTimeZone.forID("Europe/London")
  val prescriberDAO = new PrescriberDAOImpl()
  val doseDAO = new DoseDAOImpl(timezone)
  val dataFormatter = new PrescriptionDataFormatterImpl(prescriberDAO, doseDAO, timezone)
  val timestamp = new Timestamp(new DateTime().withYear(2015).withMonthOfYear(1).withDayOfMonth(1).getMillis)
  val formattedDate = "1-JAN-2015"

  "the helper method intListToStringList returns a function that" >> {
    "converts a List[Int] to a List[String]" >> {
      dataFormatter.getDateAsString(timestamp) must_== formattedDate
    }
  }
}
