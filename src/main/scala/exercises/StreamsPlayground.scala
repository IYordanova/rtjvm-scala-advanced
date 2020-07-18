package exercises

import scala.annotation.tailrec

abstract class MyStream[+A] {
  def isEmpty: Boolean

  def head: A

  def tail: MyStream[A]

  def #::[B >: A](element: B): MyStream[B] // prepend operator

  def ++[B >: A](anotherStream: => MyStream[B]): MyStream[B]

  def foreach(f: A => Unit): Unit

  def map[B](f: A => B): MyStream[B]

  def flatMap[B](f: A => MyStream[B]): MyStream[B]

  def filter(predicate: A => Boolean): MyStream[A]

  def take(n: Int): MyStream[A]

  def takeAsList(n: Int): List[A]

  @tailrec
  final def toList[B >: A](acc: List[B] = Nil): List[B] = {
    if (isEmpty) acc.reverse
    else tail.toList(head :: acc)
  }
}

object MyStream {
  def from[A](start: A)(generator: A => A): MyStream[A] = new Cons(start, MyStream.from(generator(start))(generator))
}


object EmptyStream extends MyStream[Nothing] {
  override def isEmpty: Boolean = true

  override def head: Nothing = throw new NoSuchElementException

  override def tail: MyStream[Nothing] = throw new NoSuchElementException

  override def #::[B >: Nothing](element: B): MyStream[B] = new Cons[B](element, this)

  override def ++[B >: Nothing](anotherStream: => MyStream[B]): MyStream[B] = anotherStream

  override def foreach(f: Nothing => Unit): Unit = ()

  override def map[B](f: Nothing => B): MyStream[B] = this

  override def flatMap[B](f: Nothing => MyStream[B]): MyStream[B] = this

  override def filter(predicate: Nothing => Boolean): MyStream[Nothing] = this

  override def take(n: Int): MyStream[Nothing] = this

  override def takeAsList(n: Int): List[Nothing] = Nil
}


class Cons[+A](hd: A, tl: => MyStream[A]) extends MyStream[A] {
  override def isEmpty: Boolean = false

  override val head: A = hd
  override lazy val tail: MyStream[A] = tl

  override def #::[B >: A](element: B): MyStream[B] = new Cons[B](element, this)

  override def ++[B >: A](anotherStream: => MyStream[B]): MyStream[B] = new Cons[B](head, tail ++ anotherStream) // "tail" will be eval only when accessing the tail of the new stream

  override def foreach(f: A => Unit): Unit = {
    f(head)
    tail.foreach(f)
  }

  override def map[B](f: A => B): MyStream[B] = new Cons[B](f(head), tail.map(f))

  override def flatMap[B](f: A => MyStream[B]): MyStream[B] = f(head) ++ tail.flatMap(f)

  override def filter(predicate: A => Boolean): MyStream[A] = {
    if (predicate(head)) new Cons(head, tail.filter(predicate))
    else tail.filter(predicate)
  }

  override def take(n: Int): MyStream[A] = {
    if (n <= 0) EmptyStream
    else if (n == 1) new Cons(head, EmptyStream)
    else new Cons(head, tail.take(n - 1))
  }

  override def takeAsList(n: Int): List[A] = take(n).toList()
}


object StreamsPlayground extends App {
  val naturals = MyStream.from(1)(_ + 1)

  println(naturals.head)
  println(naturals.tail.head)
  println(naturals.tail.tail.head)

  val starFrom0 = 0 #:: naturals
  println(starFrom0.head)

  starFrom0.take(10000).foreach(println)

  println(starFrom0.map(_ * 2).take(100).toList())
  println(starFrom0.flatMap(x => new Cons(x, new Cons(x + 1, EmptyStream))).take(10).toList())
  println(starFrom0.filter(_ < 10).take(10).toList())

  def fibonacci(first: Int, second: Int): MyStream[Int] = {
    new Cons(first, fibonacci(second, first + second))
  }

  println(fibonacci(1, 1).take(100).toList())


  // the head is always a prime, filter the tail to numbers filter out the ones not divisible by the head
  def eratosthenos(numbers: MyStream[Int]): MyStream[Int] = {
    if (numbers.isEmpty) numbers
    else new Cons(numbers.head, eratosthenos(numbers.tail.filter(_ % numbers.head != 0)))
  }

  println(eratosthenos(MyStream.from(2)(_ + 1)).take(100).toList())
}