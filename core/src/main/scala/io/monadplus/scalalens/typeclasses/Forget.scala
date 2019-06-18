package io.monadplus.scalalens.typeclasses

/**
 * Source: https://github.com/julien-truffaut/LensImpl/blob/master/core/src/main/scala/lensimpl/data/Forget.scala
*/ 

import cats._
import cats.arrow._

case class Forget[R, A, B](run: A => R){
    def retag[C]: Forget[R, A, C] = asInstanceOf[Forget[R, A, C]]
  }
  
  object Forget {
    implicit def strong[R]: Strong[Forget[R, ?, ?]] = new Strong[Forget[R, ?, ?]] {
      override def dimap[A, B, C, D](pab: Forget[R, A, B])(f: C => A)(g: B => D): Forget[R, C, D] =
        Forget[R, C, D](c => pab.run(f(c)))
  
      override def lmap[A, B, C](pab: Forget[R, A, B])(f: C => A): Forget[R, C, B] =
        Forget[R, C, B](c => pab.run(f(c)))
  
      override def rmap[A, B, C](pab: Forget[R, A, B])(f: B => C): Forget[R, A, C] =
        pab.retag[C]
  
      override def second[A, B, C](pab: Forget[R, A, B]): Forget[R, (C, A), (C, B)] =
        Forget[R, (C, A), (C, B)]{
          case (c, a) => pab.run(a)
        }
      override def first[A, B, C](pab: Forget[R, A, B]): Forget[R, (A, C), (B, C)] =
        Forget[R, (A, C), (B, C)]{
          case (a, c) => pab.run(a)
        }
    }
  }