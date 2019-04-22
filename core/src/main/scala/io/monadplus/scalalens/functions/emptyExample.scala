package io.monadplus.scalalens.functions

import monocle.{Optional, Prism}
import monocle.macros.GenLens
import monocle.function.Empty._

/*
  Typeclass that defines a Prism from an `S` and its empty value
 */
object emptyExample extends App {

  case class Dog(name: String) extends AnyVal
  case class Man(name: String, age: Int, dog: Option[Dog])
  val man = Man("John", 20, None)
  val manWithDog = Man("Howard", 30, Some(Dog("Bella")))

  val hasDog: Optional[Man, Unit] = GenLens[Man](_.dog).composePrism(empty)
  println(s"Man has dog: ${hasDog.isEmpty(manWithDog)}")
}
