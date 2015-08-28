package models

/**
 * Created by Jo on 28/08/2015.
 */
object DoseCalculator {

  def average24hrBreakthroughDose(dosesTaken: Int, breakthroughDose: Double, days: Int) = {
    days match{
      case 0 => 0.00
      case _ => (dosesTaken * breakthroughDose)/days
    }
  }

  def totalDailyMRDose(dose: Double) = {
    dose * 2.00
  }

  def averageTotalDailyDose(totalDailyMRDose: Double, average24hrBreakthroughDose: Double ) = {
    totalDailyMRDose + average24hrBreakthroughDose
  }

  def MRDoseTitration(averageTotalDailyDose: Double) = {
    averageTotalDailyDose / 2.00
  }

  def breakthroughDoseTitration(averageTotalDailyDose: Double) = {
    averageTotalDailyDose / 6.00
  }
}
