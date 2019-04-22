package io.monadplus.scalalens

import cats._
import cats.implicits._
import io.monadplus.scalalens.extras.{First, Sum}
import monocle.Fold
import mouse.all._

/*
 A Fold s a is a generalization of something Foldable.

 It allows you to extract multiple results from a container.

 A Foldable container can be characterized by the behavior of foldMap :: (Foldable t, Monoid m) => (a -> m) -> t a -> m.

 Since we want to be able to work with monomorphic containers, we could generalize this signature
 to forall m. Monoid m => (a -> m) -> s -> m, and then decorate it with Const to obtain

 type Fold s a = forall m. Monoid m => Getting m s a

 type Getting r s a = (a -> Const r a) -> s -> Const r s

 Every Getter is a valid Fold that simply doesn't use the Monoid it is passed.

 Unlike a Traversal a Fold is read-only. Since a Fold cannot be used to write back there are no Lens laws that apply.

 Source: https://hackage.haskell.org/package/lens-4.17/docs/Control-Lens-Fold.html
 */

object FoldExample extends App {
  val list = (1 to 10).toList
  val maybeList = (1 to 10).toList.map(i => (i % 2 == 0).fold(First(i.some), First(none[Int])))
  val set = (1 to 10).toSet

  val setFold = new Fold[Set[Int], Int] {
    override def foldMap[M](f: Int => M)(s: Set[Int])(implicit M: Monoid[M]): M =
      s.foldLeft(M.empty) { case (m, a) => m |+| f(a) }
  }

  val listFold: Fold[List[Int], Int] =
    Fold.fromFoldable[List, Int]

  val sum = listFold.foldMap(Sum.apply)(list)
  println(s"(List) Sum of values: ${sum.value}")

  val sum2 = setFold.foldMap(Sum.apply)(set)
  println(s"(Set) Sum of values: ${sum2.value}")

  val sum3 = Fold.fromFoldable[List, First[Int]].fold(maybeList)
  println(s"(Maybe List) Get first: ${sum3.value}")

  println(s"Set to List: ${setFold.getAll(set)}")

  val evenFold = Fold.select[Int](_ % 2 == 0)
  val sum4 = listFold.composeFold(evenFold).foldMap(Sum.apply)(list)
  println(s"(Maybe List) Sum of even values: ${sum4.value}")
}

object FoldLaws {

  /**
  * Since a Fold cannot be used to write back there are no Lens laws that apply.
  */
}
