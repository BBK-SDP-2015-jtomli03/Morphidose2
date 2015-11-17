package models


object DoseCalculator {

  /**
   * Calculates the average daily breakthrough dose rounded down to the nearest 0.5 unit.
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

  /**
   * Calculates the total daily MR dose currently taken.
   *
   * @param dose the current 12hrly dose
   * @return Double the total daily MR dose currently taken
   */
  def totalDailyMRDose(dose: Double) = {
    dose * 2.00
  }

  /**
   * Calculates the average total daily dose currently taken.
   *
   * @param totalDailyMRDose the current total daily MR dose
   * @param average24hrBreakthroughDose the average daily breakthrough dose currently taken
   * @return Double the total daily dose currently taken
   */
  def averageTotalDailyDose(totalDailyMRDose: Double, average24hrBreakthroughDose: Double ) = {
    totalDailyMRDose + average24hrBreakthroughDose
  }

  /**
   * Calculates the titrated MR dose rounded down to the nearest 5 unit.
   *
   * @param averageTotalDailyDose the average total daily dose taken by the patient
   * @return Double the MR dose to titrate to next, to the nearest 5 unit
   */
  def MRDoseTitration(averageTotalDailyDose: Double) = {
    Math.floor((averageTotalDailyDose / 2.00)/5) * 5
  }

  /**
   * Calculates the titrated breakthrough dose rounded down to the nearest 2.5 unit.
   *
   * @param averageTotalDailyDose the average total daily dose taken by the patient
   * @return Double the breakthrough dose to titrate to next, to the nearest 2.5 unit
   */
  def breakthroughDoseTitration(averageTotalDailyDose: Double, currentBreakthroughDose: Double, numOfBreakthroughDoses: Int) = {
    Math.floor((averageTotalDailyDose / 6.00)/2.5) * 2.5 match {
      case dose if dose < 2.50 => 2.50
      case dose if numOfBreakthroughDoses == 0 && dose > currentBreakthroughDose => currentBreakthroughDose
      case dose => dose
    }
  }

}
