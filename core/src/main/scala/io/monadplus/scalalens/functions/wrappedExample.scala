package io.monadplus.scalalens.functions

import cats._
import cats.implicits._
import newts._
import newts.syntax.all._
import monocle.Monocle._

/*
  Newts provides newtypes for Cats like in Haskell.

  Wrapped brings an Iso from the newtype to the unwrapped type.
 */
object wrappedExample extends App {
  // https://github.com/julien-truffaut/newts

  val xs = List(1, 2, 3)
  val ys = List(4, 5, 6)

  println(s"List product: ${Apply[List].product(xs, ys)}")
  println(s"ZipList product: ${Apply[ZipList].product(xs.asZipList, ys.asZipList)}")

  val xys: ZipList[(Int, Int)] =
    Apply[ZipList].product(xs.asZipList, ys.asZipList)

  println(s"Wrapped; ${xys.applyIso(wrapped).get}")

  // zipListTC == _.asZipList
  val zipListTC = (_: List[Int]).applyIso(unwrapped[ZipList[Int]])
}
