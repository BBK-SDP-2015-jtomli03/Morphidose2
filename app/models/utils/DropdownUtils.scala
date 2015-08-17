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
  def getTitles : List[String] = List("","Dr","Professor","Mr","Mrs","Miss","Ms")

  /**
   * @return List[String] possible brands of MR Morphine with the first element set as an empty String
   */
  def getMRMorphine : List[String] = List("", "Morphgesic SR Tablets", "MST Continus Suspension", "MST Continus Tablets","Zomorph Capsules")

  /**
   * @return List[String] possible doses of MR Morphine with the first element set as an empty String
   */
  def getMRMorphineDoses : List[String] = List("","5mg BD","10mg BD", "15mg", "20mg BD", "30mg BD", "60mg BD", "100mg BD", "200mg BD")

  /**
   * @return List[String] possible brands of breakthrough Morphine with the first element set as an empty String
   */
  def getBreakthroughMorphine : List[String] = List("","Oramorph 10mg/5ml","Sevredol Tablets")

  /**
   * @return List[String] possible doses of breakthrough Morphine with the first element set as an empty String
   */
  def getBreakthroughMorphineDoses : List[String] = List("","5mg prn","10mg prn", "20mg prn")

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

}
