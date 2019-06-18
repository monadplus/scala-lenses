package io.monadplus.scalalens.functions

import monocle._
import monocle.Monocle._
import monocle.function.FilterIndex._
import monocle.macros.GenLens

/*

  ** Index **
  Type class that defines an Optional from an `S` to an `A` at an index `I`.

  ** FilterIndex **
  Type class that defines a Traversal from an `S` to all its elements `A` whose index `I` in `S` satisfies the predicate.

 */

object indexIndexFilter extends App {
  case class Key(opens: String)
  case class KeyChainStore(keys: List[Key])
  val keyRing = KeyChainStore(List(Key("door 1"), Key("door 2"), Key("door 3")))
  val keyL = GenLens[Key](_.opens)

  // Index
  val keys = GenLens[KeyChainStore](_.keys)
  val thirdKey: Optional[KeyChainStore, Key] = keys.composeOptional(index(3))
  val fourthKey: Optional[KeyChainStore, Key] = keys.composeOptional(index(4))
  println(s"Updating third key: ${thirdKey.set(Key("door 3 (updated)"))(keyRing)}")
  println(s"Updating fourth key: ${fourthKey.set(Key("door 4 (updated)"))(keyRing)}")

  // Filter Index
  val evenKeys: Traversal[KeyChainStore, Key] =
    keys.composeTraversal(listFilterIndex.filterIndex(_ % 2 == 0))
  println(s"Updating even keys: ${evenKeys.modify(keyL.modify(_ + " (updated)"))(keyRing)}")
}
