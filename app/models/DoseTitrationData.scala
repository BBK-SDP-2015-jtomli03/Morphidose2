package models

import play.api.libs.json.Json
import play.api.mvc.QueryStringBindable


case class DoseTitrationData (
                               numOfBreakthroughDoses: String,
                               daysSinceCurrentPrescription: String,
                               average24hrBreakthroughDose: String,
                               totalDailyMRDose: String,
                               averageTotalDailyDose: String,
                               mrDoseTitration: String,
                               breakthroughDoseTitration: String
                               )

//The QueryStringBinder for the DoseTitrationData class
object DoseTitrationData{
  implicit val doseTitrationDataFormat = Json.format[DoseTitrationData]

  implicit def queryStringBinder(implicit doubleBinder: QueryStringBindable[String]) = new QueryStringBindable[DoseTitrationData]{
    override def bind(key: String, params: Map[String, Seq[String]]): Option[Either[String, DoseTitrationData]] = {
      for{
        numOfBreakthroughDoses <- doubleBinder.bind(key + ".breakthroughDoses", params)
        daysSinceCurrentPrescription <- doubleBinder.bind(key + ".days", params)
        average24hrBreakthroughDose <- doubleBinder.bind(key + ".average24hrBreakthrough", params)
        totalDailyMRDose <- doubleBinder.bind(key + ".totalDailyMR", params)
        averageTotalDailyDose <- doubleBinder.bind(key + ".averageTotalDaily", params)
        mrDoseTitration <- doubleBinder.bind(key + ".mrDose", params)
        breakthroughDoseTitration <- doubleBinder.bind(key + ".breakthroughDose", params)
      }yield{
        (numOfBreakthroughDoses, daysSinceCurrentPrescription, average24hrBreakthroughDose, totalDailyMRDose, averageTotalDailyDose, mrDoseTitration, breakthroughDoseTitration) match {
          case (Right(breakthroughDoses), Right(days), Right(average24hrBreakthrough), Right(totalDailyMR), Right(averageTotalDaily), Right(mrDose), Right(breakthroughDose)) =>
            Right(DoseTitrationData(breakthroughDoses, days, average24hrBreakthrough, totalDailyMR, averageTotalDaily, mrDose, breakthroughDose))
          case _ => Left("Unable to bind Dose Titration Data")
        }
      }
    }

    override def unbind(key: String, doseTitrationData: DoseTitrationData): String = {
      doubleBinder.unbind(key + ".breakthroughDoses", doseTitrationData.numOfBreakthroughDoses) + "&" + doubleBinder.unbind(key + ".days", doseTitrationData.daysSinceCurrentPrescription) + "&" + doubleBinder.unbind(key + ".average24hrBreakthrough", doseTitrationData.average24hrBreakthroughDose) + "&" + doubleBinder.unbind(key + ".totalDailyMR", doseTitrationData.totalDailyMRDose) + "&" + doubleBinder.unbind(key + ".averageTotalDaily", doseTitrationData.averageTotalDailyDose) + "&" + doubleBinder.unbind(key + ".mrDose", doseTitrationData.mrDoseTitration) + "&" + doubleBinder.unbind(key + ".breakthroughDose", doseTitrationData.breakthroughDoseTitration)
    }
  }
}