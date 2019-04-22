package io.monadplus.scalalens

import monocle.Optional

/*
  Lens where the zoomed element may not exist.
 */
object OptionalExample extends App {

  val head = Optional[List[Int], Int] {
    case Nil     => None
    case x :: xs => Some(x)
  } { x =>
    {
      case Nil     => Nil
      case _ :: xs => x :: xs
    }
  }

  val xs = List(1, 2, 3)
  val ys = List.empty[Int]

  println(s"Setting head of nonEmptyList: ${head.set(5)(xs)}")
  println(s"Setting head of emptyList: ${head.set(5)(ys)}")

  head.getOption(xs) // Some(1)
  head.getOption(ys) // None
}

class OptionalLaws[S, A](optional: Optional[S, A]) {

  def getOptionSet(s: S): Boolean =
    optional.getOrModify(s).fold(identity, optional.set(_)(s)) == s

  def setGetOption(s: S, a: A): Boolean =
    optional.getOption(optional.set(a)(s)) == optional.getOption(s).map(_ => a)

}
