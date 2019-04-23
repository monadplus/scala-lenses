package io.monadplus.scalalens

import monocle.Optional

/*
  POptional can be seen as a weaker Lens/Prism.

  A POptional can be seen as pair of functions:
    getOrModify :: s -> Either t a
    set :: (b, s) -> t
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

  head.set(5)(xs) // List(5, 2, 3)
  head.set(5)(ys) // Nil

  head.getOption(xs) // Some(1)
  head.getOption(ys) // None
}

class OptionalLaws[S, A](optional: Optional[S, A]) {

  def getOptionSet(s: S): Boolean =
    optional.getOrModify(s).fold(identity, optional.set(_)(s)) == s

  def setGetOption(s: S, a: A): Boolean =
    optional.getOption(optional.set(a)(s)) == optional.getOption(s).map(_ => a)

}
