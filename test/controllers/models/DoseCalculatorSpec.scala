package controllers.models

import models.DoseCalculator


class DoseCalculatorSpec extends org.specs2.mutable.Specification{
  val dosesTaken = 10
  val breakthroughDose10 = 10.0
  val breakthroughDose22_25 = 22.25
  val breakthroughDose22_5 = 22.5
  val breakthroughDose22_75 = 22.75
  val days = 10

  "The function average24hrBreakthroughDose calculates the average daily breakthrough dose rounded down to the nearest 0.5 unit" >> {
    "if no breakthrough doses have been taken" >> {
      DoseCalculator.average24hrBreakthroughDose(0, 5.0, 7) must_== 0.0
    }
    "if several breakthrough doses have been taken and the average daily breakthrough dose equals 10" >> {
      DoseCalculator.average24hrBreakthroughDose(dosesTaken, breakthroughDose10, days) must_== breakthroughDose10
    }
    "if several breakthrough doses have been taken and the average daily breakthrough dose equals 22.25" >> {
      DoseCalculator.average24hrBreakthroughDose(dosesTaken, breakthroughDose22_25, days) must_== 22.0
    }
    "if several breakthrough doses have been taken and the average daily breakthrough dose equals 22.5" >> {
      DoseCalculator.average24hrBreakthroughDose(dosesTaken, breakthroughDose22_5, days) must_== breakthroughDose22_5
    }
    "if several breakthrough doses have been taken and the average daily breakthrough dose equals 22.75" >> {
      DoseCalculator.average24hrBreakthroughDose(dosesTaken, breakthroughDose22_75, days) must_== breakthroughDose22_5
    }
  }

  "The function MRDoseTitration calculates the titrated MR dose rounded down to the nearest 5 unit" >> {
    "if the averageTotalDailyDose equals 10.0" >> {
      DoseCalculator.MRDoseTitration(10.0) must_== 5.0
    }
    "if the averageTotalDailyDose equals 15.0" >> {
      DoseCalculator.MRDoseTitration(15.0) must_== 5.0
    }
    "if the averageTotalDailyDose equals 22.5" >> {
      DoseCalculator.MRDoseTitration(22.5) must_== 10.0
    }
    "if the averageTotalDailyDose equals 28.0" >> {
      DoseCalculator.MRDoseTitration(32.0) must_== 15.0
    }
  }

  "The function breakthroughDoseTitration calculates the titrated breakthrough dose rounded down to the nearest 2.5 unit" >> {
    "if the calculated breakthrough dose is less than 2.5" >> {
      DoseCalculator.breakthroughDoseTitration(10.0, 2.5, 3) must_== 2.5
    }
    "if the calculated breakthrough dose is greater than the current breakthrough dose and no breakthrough doses have been taken" >> {
      DoseCalculator.breakthroughDoseTitration(100.0, 2.5, 0) must_== 2.5
    }
    "if the calculated breakthrough dose is greater than the current breakthrough dose and breakthrough doses have been taken and the calculated breakthrough dose is a multiple of 2.5" >> {
      DoseCalculator.breakthroughDoseTitration(60.0, 2.5, 30) must_== 10.0
    }
    "if the calculated breakthrough dose is greater than the current breakthrough dose and breakthrough doses have been taken and the calculated breakthrough dose is not a multiple of 2.5" >> {
      DoseCalculator.breakthroughDoseTitration(100.0, 2.5, 30) must_== 15.0
    }
  }
}
