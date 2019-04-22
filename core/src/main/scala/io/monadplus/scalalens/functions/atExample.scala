package io.monadplus.scalalens.functions

import cats.implicits._
import monocle.macros.GenLens
import monocle.Monocle._
import monocle.refined._
import eu.timepit.refined.auto._

/*
  At provides a Lens that can be used to read, write or delete the value
    associated with a key in a Map-like container on an ad hoc basis.

    >>> Map.fromList [(1,"world")] ^.at 1
    Just "world"

    >>> at 1 ?~ "hello" $ Map.empty
    fromList [(1,"hello")]

 */
object atExample extends App {

  type DayOfWeek = String
  case class Cooker(name: String)
  case class Restaurant(name: String, cookers: Map[DayOfWeek, Cooker])

  val restaurant = Restaurant(
    name = "La bona pizza",
    Map(
      "Monday" -> Cooker("Mateo"),
      "Tuesday" -> Cooker("Mateo"),
      "Wednesday" -> Cooker("Maria"),
      "Thursday" -> Cooker("Mateo"),
      "Friday" -> Cooker("Maria")
    )
  )

  val cookers = GenLens[Restaurant](_.cookers)

  // Read & Write
  cookers.composeLens(at("Wednesday")).modify(_.fold(Cooker("Maria").some)(_ => None))(restaurant)

  // Delete
  cookers.composeLens(at("Wednesday")).set(None)(restaurant)

  // Bitwise operations
  val res = 32.applyLens(bits.intAt.at(0)).set(true) // implicits: at(0: IntBits)
  println(s"Setting lower bit of 32 to 1: $res")
}
