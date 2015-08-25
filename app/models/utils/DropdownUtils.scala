package models.utils

import java.util.Calendar
import javax.inject.Singleton

@Singleton
object DropdownUtils {

  /**
   * @return a List of the days of the month (1 to 31) with the first element set as "Day"
   */
  def getDaysOfMonth : List[String] = "Day" :: List.range(1, 32).foldLeft(List[String]()){intListToStringList}

  /**
   * @return List[String] the days of the months with the first element set as "Month"
   */
  def getMonths : List[String] = List("Month","JAN","FEB","MAR","APR","MAY","JUN","JUL","AUG","SEPT","OCT","NOV","DEC")

  /**
   * @return List[String] a list of years from 1900 to the present with the first element set as "Year"
   */
  def getYears: List[String] = "Year" :: List.range(1900, getCurrentYearAsInt + 1).reverse.foldLeft(List[String]()){intListToStringList}

  /**
   * @return List[String] possible titles for people with the first element set as an empty String
   */
  def getTitles : List[String] = List("Dr","Professor","Mr","Mrs","Miss","Ms")

  /**
   * @return List[String] possible brands of MR Morphine with the first element set as an empty String
   */
  def getMRMorphine : List[String] = List("Drug", "Morphgesic SR Tablets", "MST Continus Suspension", "MST Continus Tablets","Zomorph Capsules")

  /**
   * @return List[String] possible doses of MR Morphine with the first element set as an empty String
   */
  def getMRMorphineDoses : List[String] = createDoseList(5.0, 400.0, 5.0, "mg BD")
  /**
   * @return List[String] possible brands of breakthrough Morphine with the first element set as an empty String
   */
  def getBreakthroughMorphine : List[String] = List("Drug","Oramorph 10mg/5ml","Sevredol Tablets")

  /**
   * @return List[String] possible doses of breakthrough Morphine with the first element set as an empty String
   */
//  def getBreakthroughMorphineDoses: List[String] = "Dose" :: List.range(2.5, 51.0, 2.5).foldLeft(List[String]()){intListToDoseList}.map(dose => dose + "mg prn")
  def getBreakthroughMorphineDoses: List[String] = createDoseList(2.5, 51.0, 2.5, "mg prn")


  /***********************
    * HELPER METHODS BELOW
    ***********************/

  /**
   * @return the current year as an Int
   */
  def getCurrentYearAsInt: Int = {
    Calendar.getInstance().get(Calendar.YEAR)
  }

  /**
   * Converts a List[Int] to List[String]
   */
  def intListToStringList = (acc: List[String], num: Int) => acc :+ num.toString

  /**
   * Converts a List[Double] to List[String]
   */
  def doubleListToStringList = (acc: List[String], num: Double) => acc :+ num.toString

  /**
   * Creates a List[String] of a range of doses
   *
   * @param rangeStart the lowest dose in the range
   * @param rangeEnd the highest dose in the range
   * @param rangeIncrement the dose increment
   * @param unitAndFrequency the dosage unit and frequency to be shown in the dropdown list (eg mg BD)
   */
  def createDoseList(rangeStart: Double, rangeEnd: Double, rangeIncrement: Double, unitAndFrequency: String): List[String] = {
    "Dose" :: (rangeStart to rangeEnd by rangeIncrement).toList.foldLeft(List[String]()){doubleListToStringList}.map(dose => dose + unitAndFrequency)
  }

}
