package io.monadplus.scalalens.functions

import monocle.macros.GenLens
import monocle.Monocle._

object fieldExample extends App {
  case class Monitor(inches: (Int, Int))
  val monitor = Monitor(inches = (3, 4))

  val monitorWidth = GenLens[Monitor](_.inches).composeLens(first)
  val monitorHeight = GenLens[Monitor](_.inches).composeLens(second)

  val monitor2 = monitorWidth.set(10).andThen(monitorHeight.set(18))(monitor)
  println(s"Monitor modified: $monitor2")

  val tuple = (("Hello", "World"), 1, true, 7.5)
  println(tuple.applyLens(first).composeLens(second).set("Composition"))
}
