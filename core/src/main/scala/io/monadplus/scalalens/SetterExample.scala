package io.monadplus.scalalens

import cats.implicits._
import io.circe.Encoder
import monocle._
import doobie.util.query._
import doobie.util.query.Query._

/*
  Setter[S, A]: generalisation of Functor:
     map:    (A => B) => F[A] => F[B]
     modify: (A => A) => S    => S

  It allows you to map into a structure and change out the contents,
    but it isn't strong enough to allow you to enumerate those contents.

  Everything you can do with a Functor, you can do with a Setter.
 */
object SetterExample extends App {

  case class Address(streetNumber: Int, streetName: String)
  case class Person(name: String, age: Int, address: Address)

  val sherlock = Person("Sherlock Holmes", 20, Address(221, "Baker Street"))

  val personAge: Setter[Person, Int] = Setter[Person, Int] { f => p =>
    p.copy(age = f(p.age))
  }
  val personAddress: Setter[Person, Address] = Setter[Person, Address] { f => p =>
    p.copy(address = f(p.address))
  }
  val addressNumber: Setter[Address, Int] = Setter[Address, Int] { f => a =>
    a.copy(streetNumber = f(a.streetNumber))
  }
  personAge.modify(_ + 1)(sherlock)
  personAge.set(21)(sherlock)

  personAddress.composeSetter(addressNumber).modify(_ + 1)(sherlock)
  // res: Person("Sherlock Holmes", 20, Address(222, "Baker Street"))


  // derive from functor
  PSetter.fromFunctor[List, Int, String]
  // circe Encoder
  PSetter.fromContravariant[Encoder, Int, String]
  // doobie Query
  PSetter.fromProfunctor[Query, Int, String, String]
}

class SetterLaws[S, A](setter: Setter[S, A]) {

  def setIdempotent(s: S, a: A): Boolean =
    setter.set(a)(setter.set(a)(s)) == setter.set(a)(s)

  def modifyIdentity(s: S): Boolean =
    setter.modify(identity)(s) == s

  def composeModify(s: S, f: A => A, g: A => A): Boolean =
    setter.modify(g)(setter.modify(f)(s)) == setter.modify(g.compose(f))(s)

  def consistentSetModify(s: S, a: A): Boolean =
    setter.modify(_ => a)(s) == setter.set(a)(s)
}
