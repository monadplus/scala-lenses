package io.monadplus.scalalens

import cats._
import cats.implicits._
import alleycats.std.map._
import mouse.all._

import monocle.{Prism, Traversal}
import monocle.function.FilterIndex
import monocle.macros.{GenIso, GenLens, GenPrism}
import monocle.function.all._

import json._

/*
  A Prism[S, A] can be seen as a pair of functions:
    getOrModify: S => Either[S, A]
    reverseGet: A => S

  A Prism usually encodes the relation between a Coproduct type and one of its elements.
 */
object PrismExample extends App {

  val str = "Arnau"
  val jsonStr = JStr("Arnau")
  val jsonNum = JNum(7)

  val jStr = Prism[Json, String] {
    case JStr(v) => Some(v)
    case _       => None
  }(JStr)

  // for convenience
  Prism.partial[Json, String] { case JStr(v) => v }(JStr)
  // macros
  GenPrism[Json, JNum].composeIso(GenIso[JNum, Double])
  GenPrism[Json, JNum].composeLens(GenLens[JNum](_.v))

  jStr.getOrModify(jsonNum) // Either[Json,String]
  jStr.reverseGet(str) // Json
  jStr.getOption(JStr("Hello")) // Some
  jStr.getOption(JNum(1.0)) // None
  jStr("hello") // .reverseGet
  jStr.modify(_.reverse)(JStr("olleH"): Json) // no-op if not a JStr
  jStr.modifyOption(_.reverse)(JNum(1.0))
  jStr.set("Bar")(JStr("Hello"): Json)

  def validateString(str: String): Either[Throwable, String] =
    (str.length > 100).either(new Exception("invalid"), str)

  jStr.modifyF(validateString)(jsonStr) // Either[Throwable, Json]
  jStr.isEmpty(jsonNum) // true
  jStr.first[Json].modify { case (str, i) => (str + "!", i) }(jsonStr, jsonNum)
  // res: (JStr("Arnau!"), JNum(7))
  jStr.right[Throwable].set(Right("new str"))(Left(new Exception("nope")))

  import monocle.std.either._

  stdLeft[Int, Nothing].modify(_ + 1)
  pStdLeft[Int, Nothing, String].modify(_.toString())
}

object PrismLaws {
  def partialRoundTripOneWay[S, A](p: Prism[S, A], s: S): Boolean =
    p.getOption(s) match {
      case None    => true // nothing to prove
      case Some(a) => p.reverseGet(a) == s
    }

  def partialRoundTripAnotherWay[S, A](p: Prism[S, A], a: A): Boolean =
    p.getOption(p.reverseGet(a)) == Some(a)
}

object RealWorldPrismExample extends App {
  val jsString = Prism[Json, String] { case JStr(s)            => Some(s); case _ => None }(JStr.apply)
  val jsNumber = Prism[Json, Double] { case JNum(n)            => Some(n); case _ => None }(JNum.apply)
  val jsArray = Prism[Json, List[Json]] { case JArray(a)       => Some(a); case _ => None }(JArray.apply)
  val jsObject = Prism[Json, Map[String, Json]] { case JObj(m) => Some(m); case _ => None }(
    JObj.apply)

  val json: Json = JObj(
    Map(
      "first_name" -> JStr("John"),
      "last_name" -> JStr("Doe"),
      "age" -> JNum(28),
      "siblings" -> JArray(
        List(
          JObj(
            Map(
              "first_name" -> JStr("Elia"),
              "age" -> JNum(23)
            )),
          JObj(
            Map(
              "first_name" -> JStr("Robert"),
              "age" -> JNum(25)
            ))
        ))
    ))

  // Get & Set
  jsObject.composeOptional(index("age")).composePrism(jsNumber).getOption(json)

  jsObject
    .composeOptional(index("siblings"))
    .composePrism(jsArray)
    .composeOptional(index(1))
    .composePrism(jsObject)
    .composeOptional(index("first_name"))
    .composePrism(jsString)
    .set("Robert Jr.")(json)

  // Add & Delete
  jsObject.composeLens(at("nick_name")).set(Some(JStr("Jojo")))(json)

  jsObject.composeLens(at("age")).set(None)(json)

  // Instance for Map
  implicit def mapFilterIndex[K, V]: FilterIndex[Map[K, V], K, V] =
    new FilterIndex[Map[K, V], K, V] {
      def filterIndex(predicate: K => Boolean) = new Traversal[Map[K, V], V] {
        def modifyF[F[_]: Applicative](f: V => F[V])(s: Map[K, V]): F[Map[K, V]] =
          s.toList
            .traverse {
              case (k, v) =>
                (if (predicate(k)) f(v) else v.pure[F]).tupleLeft(k)
            }
            .map(kvs => Map(kvs: _*))
      }
    }

  // Traversal & Index/IndexFilter
  val res = jsObject
    .composeTraversal(filterIndex((_: String).contains("name")))
    .composePrism(jsString)
    .composeOptional(headOption)
    .modify(_.toLower)(json)
  println(res)

  jsObject
    .composeOptional(index("siblings"))
    .composePrism(jsArray)
    .composeTraversal(each)
    .composePrism(jsObject)
    .composeOptional(index("age"))
    .composePrism(jsNumber)
    .modify(_ + 1)(json)
}
