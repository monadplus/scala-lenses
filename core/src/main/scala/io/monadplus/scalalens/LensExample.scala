package io.monadplus.scalalens

import cats.implicits._
import monocle.Lens
import monocle.macros.{GenLens, Lenses, PLenses}
import monocle.macros.syntax.lens._

import scala.concurrent.Future
import scala.concurrent.ExecutionContext.Implicits.global // derive Functor instance for Future

/*
  A Lens[S, A] can be seen as a pair of functions:
    get: S => A
    set: (A, S) => S

  Typically a Lens can be defined between a Product and one of its component.
*/
object LensExample extends App {
  case class Address(streetNumber: Int, streetName: String)

  val address = Address(1, "The shire")
  val address2 = Address(2, "Mordor")

  val streetNumber: Lens[Address, Int] =
    Lens[Address, Int](_.streetNumber)(n => a => a.copy(streetNumber = n))

  streetNumber.set(streetNumber.get(address))(address2)
  streetNumber.modify(_ + 1)

  def neighbors(n: Int): List[Int] =
    if (n > 0) List(n - 1, n + 1) else List(n + 1)

  streetNumber.modifyF(neighbors)(address)
  // res: List(Address(0,The shire), Address(2,The shire))

  def updateNumber(n: Int): Future[Int] = Future.successful(n + 1)
  streetNumber.modifyF(updateNumber)(address) // Future[Address]

  // Composition

  case class Person(name: String, age: Int, address: Address)

  val personAddress = GenLens[Person](_.address)

  val john = Person("John", 20, address)
  personAddress.composeLens(streetNumber).get(john) // 20 
  personAddress.composeLens(streetNumber).set(2)(john) // Person(John,20,Address(2,The shire))
}

object LensLaws {

  /*

  With great power comes great responsibility and a Lens is subject to the three common sense Lens laws:

    1) You get back what you put in:

    view l (set l v s)  ≡ v

    2) Putting back what you got doesn't change anything:

    set l (view l s) s  ≡ s

    3) Setting twice is the same as setting once:

    set l v' (set l v s) ≡ set l v' s

   */
  def setGet[S, A](l: Lens[S, A], s: S, a: A): Boolean =
    l.get(l.set(a)(s)) == a

  def getSet[S, A](l: Lens[S, A], s: S): Boolean =
    l.set(l.get(s))(s) == s
}

object LensGeneration extends App {
  @Lenses case class Address(streetNumber: Int, streetName: String)
  @Lenses case class Person(name: String, age: Int, address: Address)

  object Manual {
    val _name = Lens[Person, String](_.name)(n => p => p.copy(name = n))
    val _age = Lens[Person, Int](_.age)(a => p => p.copy(age = a))
    val _address = Lens[Person, Address](_.address)(a => p => p.copy(address = a))
    val _streetNumber = Lens[Address, Int](_.streetNumber)(n => a => a.copy(streetNumber = n))
  }

  object Semi {
    val name = GenLens[Person](_.name)
    val age = GenLens[Person](_.age)
    val address = GenLens[Person](_.address)
    val streetNumber = GenLens[Address](_.streetNumber)
  }

  val john = Person("John", 30, Address(126, "High Street"))

  // Get
  Manual._name.get(john)
  Semi.name.get(john)
  Person.name.get(john)
  john.lens(_.name).get // macro syntax

  // Set
  Manual._age.set(45)(john)
  Semi.age.set(45)(john)
  Person.age.set(45)(john)
  john.lens(_.age).set(45)
}

object PolyLensGeneration extends App {
  @PLenses case class Foo[A, B](q: Map[(A, B), Double], default: Double)

  object Manual {
    def q[A, B] = Lens((_: Foo[A, B]).q)(q => f => f.copy(q = q))
    def default[A, B] = Lens((_: Foo[A, B]).default)(d => f => f.copy(default = d))
  }

  object Semi { // Lens generated semi automatically using GenLens macro
    def q[A, B] = GenLens[Foo[A, B]](_.q)
    def default[A, B] = GenLens[Foo[A, B]](_.default)
  }

  val candyTrade = Foo(Map[(Int, Symbol), Double](
                         (0, Symbol("Buy")) -> -3.0,
                         (12, Symbol("Sell")) -> 7
                       ),
                       default = 0.0)

  Manual.default.get(candyTrade)
  Semi.default.get(candyTrade)
  Foo.default.get(candyTrade)

  Foo.q.modify((_: Map[(Int, Symbol), Double]).updated((0, Symbol("Buy")),-2.0))(candyTrade)
}
