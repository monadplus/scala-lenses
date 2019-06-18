package io.monadplus.scalalens.functions

import monocle.Traversal
import monocle.function.Plated
import cats._
import cats.implicits._
import alleycats.std.map._
import mouse.all._

/*
  A Plated type is one where we know how to extract its immediate self-similar children.
 */
object platedExample extends App {
  sealed trait Json

  case object JsNull extends Json
  case class JsString(s: String) extends Json
  case class JsNumber(n: Int) extends Json
  case class JsArray(l: List[Json]) extends Json
  case class JsObject(m: Map[String, Json]) extends Json

  implicit val jsonPlated: Plated[Json] = new Plated[Json] {
    override def plate: Traversal[Json, Json] = new Traversal[Json, Json] {
      def modifyF[F[_]: Applicative](f: Json => F[Json])(a: Json): F[Json] =
        a match {
          case j @ (JsString(_) | JsNumber(_) | JsNull) => Applicative[F].pure(j)
          case JsArray(l)                               => l.traverse(f).map(JsArray)
          case JsObject(m)                              => Traverse[Map[String, ?]].traverse(m)(f).map(JsObject)
        }
    }
  }

  val original: Json = JsObject(
    Map(
      "first_name" -> JsString("John"),
      "last_name" -> JsString("doe"),
      "age" -> JsNumber(28),
      "siblings" -> JsArray(
        List(
          JsObject(
            Map(
              "first_name" -> JsString("Elia"),
              "age" -> JsNumber(23)
            )),
          JsObject(
            Map(
              "first_name" -> JsString(""),
              "age" -> JsNumber(25)
            ))
        ))
    ))

  val updated = Plated.rewrite[Json] {
    case JsString(s) =>
      s.headOption.forall(_.isLower).option(JsNull)
    case _ =>
      None
  }(original)

  println(s"Updated: $updated")
}
