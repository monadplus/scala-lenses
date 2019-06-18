package io.monadplus.scalalens

import cats._
import cats.implicits._
import io.monadplus.scalalens.extras.{First, Sum}
import monocle.Fold
import mouse.all._

/*
 A Fold[S, A] is a generalization of something Foldable.
 It allows you to extract multiple results from a container.
 A Foldable container can be characterized by the behavior of foldMap:

   def foldMap[M: Monoid](f: A => M)(s: S): M

 */
object FoldExample extends App {
  val list = (1 to 10).toList
  val set = (1 to 10).toSet

  val setFold = new Fold[Set[Int], Int] {
    override def foldMap[M](f: Int => M)(s: Set[Int])(implicit M: Monoid[M]): M =
      s.foldLeft(M.empty) { case (m, a) => m |+| f(a) }
  }

  val listFold: Fold[List[Int], Int] =
    Fold.fromFoldable[List, Int]

  listFold.foldMap(Sum(_))(list)
  // res: Sum(55)

  val sum2 = setFold.foldMap(Sum(_))(set)
  // res: Sum(55)

  setFold.getAll(set)
  // res: List(5, 10, 1, 6, 9, 2, 7, 3, 8, 4)

  val maybeList =
    (1 to 10).toList.map(i => (i % 2 == 0).fold(First(i.some), First(none[Int])))

  Fold.fromFoldable[List, First[Int]].fold(maybeList)
  // res: Some(2)

  val evenFold = Fold.select[Int](_ % 2 == 0)
  listFold.composeFold(evenFold).foldMap(Sum.apply)(list)
  // res: 30
}

object FoldLaws {

  /**
  * Since a Fold cannot be used to write back there are no Lens laws that apply.
  */
}
