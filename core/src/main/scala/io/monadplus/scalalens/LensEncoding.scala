package io.monadplus.scalalens

/**
 * Source: https://github.com/julien-truffaut/LensImpl
 */ 

import cats._ 
import cats.implicits._ 
import cats.data._
import cats.arrow._
import typeclasses.Forget

// *** Case Class ***
case class LensCC[S, A](get: S => A, set: (A, S) => S){

    def modify(f: A => A)(s: S): S =
      set(f(get(s)), s)
  
    def modifyF[F[_]](f: A => F[A])(s: S)(implicit F: Functor[F]): F[S] =
      F.map(f(get(s)))(set(_, s))
  
    def compose[B](other: LensCC[A, B]): LensCC[S, B] =
      LensCC(s => other.get(get(s)), (b, s) => modify(other.set(b, _))(s))
  
}

// *** Monocle ***
abstract class LensMO[S, A] { self =>
    def get(s: S): A
  
    def set(a: A, s: S): S
  
    def modify(f: A => A)(s: S): S
  
    def modifyF[F[_]](f: A => F[A])(s: S)(implicit F: Functor[F]): F[S]
  
    final def compose[B](other: LensMO[A, B]): LensMO[S, B] = new LensMO[S, B] {
      override def get(s: S): B =
        other.get(self.get(s))
  
      override def set(a: B, s: S): S =
        self.modify(other.set(a, _))(s)
  
      override def modify(f: B => B)(s: S): S =
        self.modify(other.modify(f)(_))(s)
  
      override def modifyF[F[_]](f: B => F[B])(s: S)(implicit F: Functor[F]): F[S] =
        self.modifyF(other.modifyF(f)(_))(s)
    }
  
}

object LensMO {
    def apply[S, A](_get: S => A, _set: (A, S) => S): LensMO[S, A] =
      new LensMO[S, A] {
        override def get(s: S): A = _get(s)
        override def set(a: A, s: S): S = _set(a, s)
        override def modify(f: A => A)(s: S): S = _set(f(get(s)), s)
        override def modifyF[F[_]](f: (A) => F[A])(s: S)(implicit F: Functor[F]): F[S] =
          F.map(f(_get(s)))(_set(_, s))
      }
}

// *** Profunctor ***
abstract class LensPF[S, A] { self =>
    def _lens[P[_,_]](implicit P: Strong[P]): P[A, A] => P[S, S]
  
    final def modifyF[F[_]](f: A => F[A])(s: S)(implicit F: Functor[F]): F[S] =
      _lens[Kleisli[F, ?, ?]].apply(Kleisli(f)).run(s)
  
    final def modify(f: A => A)(s: S): S =
      _lens[Function1].apply(f).apply(s)
  
    final def set(a: A, s: S): S =
      modify(_ => a)(s)
  
    final def get(s: S): A =
      _lens[Forget[A, ?, ?]].apply(Forget(identity)).run(s)
  
    final def compose[B](other: LensPF[A, B]): LensPF[S, B] = new LensPF[S, B] {
      override def _lens[P[_, _]](implicit P: Strong[P]): P[B, B] => P[S, S] =
        self._lens[P] compose other._lens[P]
    }
}

object LensPF {
    def apply[S, A](_get: S => A, _set: (A, S) => S): LensPF[S, A] =
      new LensPF[S, A] {
        override def _lens[P[_, _]](implicit P: Strong[P]): P[A, A] => P[S, S] =
          paa =>
            P.dimap(P.first[A, A, S](paa))((s: S) => (_get(s), s))(_set.tupled)
      }
}

// *** Van Laarhoven Lens ***
abstract class LensVL[S, A] { self =>
    def modifyF[F[_]](f: A => F[A])(s: S)(implicit F: Functor[F]):  F[S]
  
    final def modify(f: A => A)(s: S): S =
      modifyF[Id](f)(s)
  
    final def set(a: A, s: S): S =
      modify(_ => a)(s)
  
    final def get(s: S): A =
      modifyF[({type λ[α] = Const[A, α]})#λ](a => Const(a))(s).getConst
  
    final def compose[B](other: LensVL[A, B]): LensVL[S, B] = new LensVL[S, B] {
      override def modifyF[F[_]](f: (B) => F[B])(s: S)(implicit F: Functor[F]): F[S] =
        self.modifyF(other.modifyF(f))(s)
    }
  
  }
  
  object LensVL {
    def apply[S, A](_get: S => A, _set: (A, S) => S): LensVL[S, A] =
      new LensVL[S, A] {
        override def modifyF[F[_]](f: A => F[A])(s: S)(implicit F: Functor[F]): F[S] =
          F.map(f(_get(s)))(_set(_, s))
      }
}
