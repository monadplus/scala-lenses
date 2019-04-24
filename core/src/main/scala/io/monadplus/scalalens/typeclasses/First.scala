package io.monadplus.scalalens.extras

import cats._

case class First[A](value: Option[A])
object First {
  implicit def monoid[A]: Monoid[First[A]] = new Monoid[First[A]] {
    override def empty: First[A] = First(None)
    override def combine(x: First[A], y: First[A]): First[A] =
      x.value match {
        case None => y
        case _    => x
      }
  }
}
