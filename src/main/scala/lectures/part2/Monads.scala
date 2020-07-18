package lectures.part2

/*
  kind of type with fundamental ops:
  - unit(value) -> monad
  - flatMap(monad) -> monad
  Example of monads -> list, option, try, future, set, stream
  must satisfy the following
  - left identity - unit(x).flatMap(f) == f(x)
  - right identity - someMonad.flatMap(unit) == someMonad
  - associativity - m.flatMap(f).flatMap(g) == m.flatMap(x => f(x).flatMap(g))
 */

object Monads extends App {

  trait Attempt[+A] {
    def flatMap[B](f: A => Attempt[B]): Attempt[B]
  }

  object Attempt {
    def apply[A](a: => A): Attempt[A] = {
      try {
        Success(a)
      } catch {
        case e: Throwable =>  Fail(e)
      }
    }
  }

  case class Success[+A](value: A) extends Attempt[A] {
    override def flatMap[B](f: A => Attempt[B]): Attempt[B] = {
      try {
        f(value)
      } catch  {
        case e: Throwable => Fail(e)
      }
    }
  }

  case class Fail(e: Throwable) extends Attempt[Nothing] {
    override def flatMap[B](f: Nothing => Attempt[B]): Attempt[B] = {
      this
    }
  }

  // lazy Monad
  class Lazy[+A](value: => A) {
    // call by need
    private lazy val internalValue = value
    def flatMap[B](f: ( => A) => Lazy[B]) = f(internalValue)
  }

  object Lazy {
    def apply[A](value: => A): Lazy[A] = new Lazy(value)
  }

}
