package models


object DoseCalculator {

  /**
   * Calculates the average daily breakthrough dose to the nearest 0.5 unit.
   *
   * @param dosesTaken the number of doses taken over the period of time
   * @param days the number of days the doses were taken over
   * @return Double the average daily breakthrough dose to the nearest 0.5 unit
   */
  def average24hrBreakthroughDose(dosesTaken: Int, breakthroughDose: Double, days: Int) = {
    days match{
      case 0 => 0.00
      case _ => Math.floor(((dosesTaken * breakthroughDose)/days)*2)/2
    }
  }

  def totalDailyMRDose(dose: Double) = {
    dose * 2.00
  }

  def averageTotalDailyDose(totalDailyMRDose: Double, average24hrBreakthroughDose: Double ) = {
    totalDailyMRDose + average24hrBreakthroughDose
  }

  /**
   * Calculates the titrated MR dose to the nearest 5 unit.
   *
   * @param averageTotalDailyDose the average total daily dose taken by the patient
   * @return Double the MR dose to titrate to next, to the nearest 5 unit
   */
  def MRDoseTitration(averageTotalDailyDose: Double) = {
    Math.floor((averageTotalDailyDose / 2.00)/5) * 5
  }

  /**
   * Calculates the titrated breakthrough dose to the nearest 0.5 unit.
   *
   * @param averageTotalDailyDose the average total daily dose taken by the patient
   * @return Double the breakthrough dose to titrate to next, to the nearest 0.5 unit
   */
  def breakthroughDoseTitration(averageTotalDailyDose: Double) = {
    averageTotalDailyDose / 6.00 match {
      case dose if dose < 2.50 => 2.50
      case dose =>
        Math.floor(dose * 2)/2
    }
  }

  //      case _ => BigDecimal((dosesTaken * breakthroughDose)/days).setScale(2, BigDecimal.RoundingMode.HALF_DOWN).toDouble

}
