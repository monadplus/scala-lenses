package io.monadplus.scalalens

import org.scalatest._
import cats.effect._

object MainSpec extends FreeSpec {

  "Main" - {
    "should run a println" in {
      assert(Main.run(List.empty[String]).unsafeRunSync === ExitCode.Success)
    }
  }

}