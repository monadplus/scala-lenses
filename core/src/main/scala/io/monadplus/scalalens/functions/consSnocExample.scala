package io.monadplus.scalalens.functions

import cats.data._
import monocle.Lens
import monocle.Optional
import monocle.macros.GenLens
import monocle.function.all._

/*
  ** Cons **
  Type class that defines a Prism between an `S` and its head `A` and tail `S`

  abstract class Cons[S, A] extends Serializable {
    def cons: Prism[S, (A, S)]
    def headOption: Optional[S, A] = cons composeLens first
    def tailOption: Optional[S, S] = cons composeLens second
  }

  ** Cons1 **
  Same as Cons but head is guaranteed

  ** Snoc **
  Inverse of Cons

  ** Snoc1 **
  Inverse of Cons1

 */
object consSnocExample extends App {
  // Cons
  case class Street(name: String)
  case class City(name: String, streets: List[Street])
  val barcelona =
    City(name = "Barcelona", streets = List(Street("Compte Urgell"), Street("Zamora")))

  val streetsOfCity: Lens[City, List[Street]] = GenLens[City](_.streets)
  val firstStreet: Optional[City, Street] = streetsOfCity.composeOptional(headOption)
  val setStreet = firstStreet.set(Street("Maria Cristina"))
  println(s"City: ${setStreet(barcelona)}")

  // Cons1
  case class Mouse(name: String)
  case class Cat(name: String, catches: NonEmptyList[Mouse])
  val cat = Cat("Darwin", NonEmptyList.of(Mouse("rat 1"), Mouse("rat 2")))

  val catFirstMouseOptionalFirstLetter: Optional[Cat, Char] =
    GenLens[Cat](_.catches)
      .composeLens(head)
      .composeLens(GenLens[Mouse](_.name))
      .composeOptional(headOption)
  val cat2 = catFirstMouseOptionalFirstLetter.modify(_.toUpper)(cat)
  println(s"Cat: $cat2")

  // Snoc

  def addStreet(city: City): City =
    streetsOfCity.composePrism(snoc).set(city.streets, Street("Aribau"))(city)

  println(s"Setting barcelona last street: ${addStreet(barcelona)}")
  println(
    s"Updating barcelona last street: ${streetsOfCity.composePrism(snoc).set(Nil, Street("Aribau"))(barcelona)}")
}
