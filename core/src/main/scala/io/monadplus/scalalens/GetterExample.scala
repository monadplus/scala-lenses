package io.monadplus.scalalens

import monocle._
import monocle.Monocle._

/*
  (Monocle) glorified S => A

  A Getter s a is just any function (s -> a), which we've flipped into continuation passing style [(s -> a) = s -> ((a -> r) -> r)],
  (a -> r) -> s -> r and decorated with Const to obtain:

    type Getting r s a = (a -> Const r a) -> s -> Const r s

  If we restrict access to knowledge about the type r, we could get:

    type Getter s a = forall r. Getting r s a

  However, for Getter (but not for Getting) we actually permit any functor f which is an instance of both Functor and Contravariant:

    type Getter s a = forall f. (Contravariant f, Functor f) => (a -> f a) -> s -> f s


  TL;DR: everything you can do with a function, you can do with a Getter,
         but note that because of the continuation passing style (.) composes them in the opposite order.

  Since it is only a function, every Getter obviously only retrieves a single value for a given input.

 */

object GetterExample extends App {
  case class Box[+A](value: A)
  val numBox = Box(7)
  val strBox = Box("Seven")
  val pairBox = Box(7 -> "Seven")

  def boxGetter[A]: Getter[Box[A], A] = Getter(_.value)
  boxGetter.get(numBox) // 7
  boxGetter.get(strBox) // "Seven"

  val res = boxGetter[(Int, String)].composeLens(first).get(pairBox)
  println(s"Composition of Getter: $res")

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
