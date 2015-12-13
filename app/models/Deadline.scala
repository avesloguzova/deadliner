package models

import org.joda.time.DateTime


/**
  * Created by av on 13/12/15.
  */
case class Deadline(id: Option[Long], title: String, description: Option[String], date: DateTime)

object Deadline {
  implicit def dateToString(dateTime: DateTime):String = dateTime.toString()
  implicit def stringToDate(textDateTime: String):DateTime = DateTime.parse(textDateTime)
}
