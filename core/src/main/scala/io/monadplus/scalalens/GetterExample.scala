package io.monadplus.scalalens

import monocle._
import monocle.Monocle._

/*
  Getter[S, A]

  "Glorified S => A" - Monocle
*/
object GetterExample extends App {
  case class Box[+A](value: A)
  val numBox = Box(7)
  val strBox = Box("Seven")
  val pairBox = Box(7 -> "Seven")

  def boxGetter[A]: Getter[Box[A], A] = Getter(_.value)
  boxGetter.get(numBox) // 7
  boxGetter.get(strBox) // "Seven"

  boxGetter[(Int, String)].composeLens(first).get(pairBox)
  // res: 7

  /*
  >>> ("hello","world")^.to snd
  "world"

  >>> 5^.to succ
  6

  >>> (0, -5)^._2.to abs
  5
 */
}

object GetterLaws {

  /**
  * Unlike a Lens a Getter is read-only.
  * Since a Getter cannot be used to write back there are no Lens laws that can be applied to it.
  * In fact, it is isomorphic to an arbitrary function from (s -> a).
  */
}
