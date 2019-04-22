package io.monadplus.scalalens.extras

import cats.Monoid

case class Sum[+A](value: A) extends AnyVal
object Sum {
  implicit def monoid[A](implicit N: Numeric[A]): Monoid[Sum[A]] = new Monoid[Sum[A]] {
    override def empty: Sum[A] = Sum(N.zero)
    override def combine(x: Sum[A], y: Sum[A]): Sum[A] = Sum(N.plus(x.value, y.value))
  }
}
