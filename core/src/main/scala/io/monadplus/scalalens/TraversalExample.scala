package io.monadplus.scalalens

import cats._
import cats.implicits._
import monocle.Traversal
import mouse.all._

/*
  It allows you to traverse over a structure and change out its contents with monadic or Applicative side-effects.
  A Traversal s t a b is a generalization of traverse from Traversable.

    traverse :: (Traversable t, Applicative f) => (a -> f b) -> t a -> f (t b)
    type Traversal s t a b = forall f. Applicative f => (a -> f b) -> s -> f t

  A Traversal can be used as a Fold.
  Any Traversal can be used for Getting like a Fold, because given a Monoid m, we have an Applicative for (Const m).

  TL;DR everything you know how to do with a Traversable container, you can with a Traversal.
 */
object TraversalExample extends App {

  val xs = List(1, 2, 3, 4, 5)

  val eachL = Traversal.fromTraverse[List, Int]
  eachL.set(0)(xs) //  List(0, 0 ...)
  eachL.modify(_ + 1)(xs)

  // Traversal is also a Fold
  eachL.getAll(xs) // xs ...
  eachL.headOption(xs) // Some(1)
  eachL.find(_ > 3)(xs) // Some(4)
  eachL.all(_ % 2 == 0)(xs)

  case class Point(id: String, x: Int, y: Int)
  val points = Traversal.apply2[Point, Int](_.x, _.y)((x, y, p) => p.copy(x = x, y = y))

  println {
    points.set(5)(Point("bottom-left", 0, 0))
  }

  def filterKey[K, V](predicate: K => Boolean): Traversal[Map[K, V], V] =
    new Traversal[Map[K, V], V] {
      def modifyF[F[_]: Applicative](f: V => F[V])(s: Map[K, V]): F[Map[K, V]] =
        s.toList
          .traverse {
            case (k, v) =>
              predicate(k).fold(f(v), v.pure[F]).tupleLeft(k)
          }
          .map(kvs => Map(kvs: _*))
    }

  val m = Map(1 -> "one", 2 -> "two", 3 -> "three", 4 -> "Four")
  val result = filterKey[Int, String](_ % 2 == 0).modify(_.toUpperCase)(m)
  println(result) // Map(1 -> one, 2 -> TWO, 3 -> three, 4 -> FOUR)
}

object TraversalLaws {

  def modifyGetAll[S, A](t: Traversal[S, A], s: S, f: A => A): Boolean =
    t.getAll(t.modify(f)(s)) == t.getAll(s).map(f)

  def composeModify[S, A](t: Traversal[S, A], s: S, f: A => A, g: A => A): Boolean =
    t.modify(g)(t.modify(f)(s)) == t.modify(g.compose(f))(s)
}
