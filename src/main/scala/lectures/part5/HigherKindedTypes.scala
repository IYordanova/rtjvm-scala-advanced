package lectures.part5

import scala.concurrent.Future
import scala.concurrent.ExecutionContext.Implicits.global


object HigherKindedTypes extends App {

  trait HigherKindedType[F[_]] // has unknown type

  trait MyList[T] {
    def flatMap[B](f: T => B): MyList[B]
  }

  trait MyOption[T] {
    def flatMap[B](f: T => B): MyOption[B]
  }

  trait MyFuture[T] {
    def flatMap[B](f: T => B): MyFuture[B]
  }

  def multiply[A, B](listA: List[A], listB: List[B]): List[(A, B)] =
    for {
      a <- listA
      b <- listB
    } yield (a, b)


  def multiply[A, B](listA: Option[A], listB: Option[B]): Option[(A, B)] =
    for {
      a <- listA
      b <- listB
    } yield (a, b)


  def multiply[A, B](listA: Future[A], listB: Future[B]): Future[(A, B)] =
    for {
      a <- listA
      b <- listB
    } yield (a, b)


  // ----------------------------------------

  trait Monad[F[_], A] { // F[_] - be list, option, future, A - what it returns
    def flatMap[B](f: A => F[B]): F[B]
    def map[B](f: A => B): F[B]
  }

  implicit class MonadList[A](list: List[A]) extends Monad[List, A] {
    override def flatMap[B](f: A => List[B]): List[B] = list.flatMap(f)
    override def map[B](f: A => B): List[B] = list.map(f)
  }

  def multiply[F[_], A, B](implicit ma: Monad[F, A], mb: Monad[F, B]): F[(A,B)] = {
    for{
      a <- ma
      b <- mb
    } yield (a, b)
  }

  val monadList = new MonadList(List(1,2,3))
  monadList.flatMap(x => List(x, x + 1)) // List[Int]
  monadList.map(_+2)  // List[Int]

  println(multiply(List(1,2,3), List(4,5)))

}
