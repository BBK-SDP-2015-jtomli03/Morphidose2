package models

import java.sql.Timestamp

case class Dose (
                  ptHospitalNumber: String,
                  date: Timestamp
                  )


//object Dose {
//  implicit val readsDose: Reads[Dose] = new Reads[Dose] {
//    def reads(json: JsValue): JsResult[Dose] = {
//      for {
//        date <- (json \ "date").validate[String].map(dateString => new Timestamp(dateString.toLong))
//        ptHospitalNumber <- (json \ "ptHospitalNumber").validate[String]
//      } yield Dose(date, ptHospitalNumber)
//    }
//  }
//}


