package models.utils

import org.specs2.mutable.Specification


class DropDownUtilsSpec extends Specification{

  "the helper method intListToStringList returns a function that" >> {
    "converts a List[Int] to a List[String]" >> {
      List.range(1, 4).foldLeft(List[String]()){DropdownUtils.intListToStringList} must_== List("1","2","3")
    }
    "returns an empty List[Int] to an empty List[String]" >> {
      List[Int]().foldLeft(List[String]()){DropdownUtils.intListToStringList} must_== List[String]()
    }
  }

  "the helper method doubleListToStringList returns a function that" >> {
    "converts a List[Double] to a List[String]" >> {
      List[Double](1.0, 2.0, 3.0).foldLeft(List[String]()){DropdownUtils.doubleListToStringList} must_== List("1.0","2.0","3.0")
    }
    "returns an empty List[Double] to an empty List[String]" >> {
      List[Double]().foldLeft(List[String]()){DropdownUtils.doubleListToStringList} must_== List[String]()
    }
  }

  "the helper method createDoseList returns" >> {
    "a List[String] of doses in the correct format if passed the rangeIncrement 0.5 and the unitAndFrequency 'mg BD' " >> {
      DropdownUtils.createDoseList(0.0, 2.0, 0.5, "mg BD") must_== List("Dose", "0.0mg BD","0.5mg BD","1.0mg BD", "1.5mg BD", "2.0mg BD")
    }
    "a List[String] of doses in the correct format if passed the rangeIncrement 10.0 and the unitAndFrequency 'ml PRN' " >> {
      DropdownUtils.createDoseList(10.0, 30.0, 10.0, "ml PRN") must_== List("Dose", "10.0ml PRN","20.0ml PRN","30.0ml PRN")
    }
  }

}
