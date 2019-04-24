package io.monadplus.scalalens

import monocle.Iso
import monocle.macros.GenIso

/*
  An Iso[S, A] defines an isomorphism between the type S and A

               get
     -------------------->
   S                       A
     <--------------------
          reverseGet
*/
object IsoExample {
  case class Person(name: String, age: Int)

  val personToTuple = Iso[Person, (String, Int)](p => (p.name, p.age)) {
    case (name, age) => Person(name, age)
  }
  
  personToTuple.get(Person("Zoe", 25))
  personToTuple.reverseGet(("Zoe", 25)) == personToTuple("Zoe", 25)

  def listToVector[A] = Iso[List[A], Vector[A]](_.toVector)(_.toList)
  def vectorToList[A] = listToVector[A].reverse
  listToVector.get(List(1, 2, 3))
  vectorToList.get(Vector(1, 2, 3))

  val stringToList = Iso[String, List[Char]](_.toList)(_.mkString(""))
  stringToList.modify(_.tail)("Hello")

  case class MyString(s: String)
  case class Foo()
  case object Bar

  GenIso[MyString, String].get(MyString("Hello"))
  GenIso.unit[Foo]
  GenIso.fields[Person].get(Person("John", 30))
}

object IsoLaws {
  def roundTripOneWay[S, A](i: Iso[S, A], s: S): Boolean =
    i.reverseGet(i.get(s)) == s

  def roundTripOtherWay[S, A](i: Iso[S, A], a: A): Boolean =
    i.get(i.reverseGet(a)) == a
}
