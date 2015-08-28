package modules

import com.google.inject.AbstractModule
import models.{PrescriptionDataFormatterImpl, PrescriptionDataFormatter}
import models.daos._
import net.codingwell.scalaguice.ScalaModule
import org.joda.time.DateTimeZone

/**
 * The guice module for the application dependencies.
 */
class ApplicationModule extends AbstractModule with ScalaModule {

  /**
   * Configures the module.
   */
  def configure() {
    bind[PatientDAO].to[PatientDAOImpl]
    bind[PrescriptionDAO].to[PrescriptionDAOImpl]
    bind[PrescriberDAO].to[PrescriberDAOImpl]
    bind[DoseDAO].to[DoseDAOImpl]
    bind[PrescriptionDataFormatter].to[PrescriptionDataFormatterImpl]
    bind[DateTimeZone].toInstance(DateTimeZone.forID("Europe/London"))
  }

}
