package io.monadplus.scalalens.json

sealed trait Json

case object JNull extends Json
case class JStr(v: String) extends Json
case class JNum(v: Double) extends Json
case class JObj(v: Map[String, Json]) extends Json
case class JArray(v: List[Json]) extends Json