package io.monadplus.scalalens.functions

import monocle._
import monocle.Monocle._ // syntax and instances
import monocle.macros.GenLens
import monocle.macros.GenIso

/*
  Typeclass that defines a Traversal from a monomorphic container `S` to all of its elements of type `A`.
 */
object eachExample extends App {
  case class Moon(name: String) extends AnyVal
  case class Planet(name: String, moons: Vector[Moon])

  val planet = Planet(
    name = "Saturn",
    moons = Vector(Moon("Tethys"), Moon("Dione"))
  )

  val satellites: Lens[Planet, Vector[Moon]] = GenLens[Planet](_.moons)
  val satelliteName: Iso[Moon, String] = GenIso[Moon, String]
  val planetNames = satellites.composeTraversal(each).composeIso(satelliteName)
  println(s"Planets: ${planetNames.getAll(planet)}")

  val rgb = ("red", "green", "blue")
  val RGB = rgb.applyTraversal(each).composeOptional(headOption).modify(_.toUpper)
  println(s"RGB: $RGB")
}
