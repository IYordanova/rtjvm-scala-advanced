package lectures.part2

import scala.annotation.tailrec

trait MySet[A] extends (A => Boolean) {

  def apply(elem: A): Boolean = contains(elem)

  def contains(elem: A): Boolean

  def map[B](f: A => B): MySet[B]

  def flatMap[B](f: A => MySet[B]): MySet[B]

  def filter(predicate: A => Boolean): MySet[A]

  def foreach(f: A => Unit): Unit

  def +(elem: A): MySet[A]

  def -(elem: A): MySet[A]

  // Union
  def ++(anotherSet: MySet[A]): MySet[A]

  // Difference
  def --(anotherSet: MySet[A]): MySet[A]

  // Intersection
  def &(anotherSet: MySet[A]): MySet[A]

  def unary_! : MySet[A]
}


class EmptySet[A] extends MySet[A] {
  override def contains(elem: A): Boolean = false

  override def map[B](f: A => B): MySet[B] = new EmptySet[B]

  override def flatMap[B](f: A => MySet[B]): MySet[B] = new EmptySet[B]

  override def filter(predicate: A => Boolean): MySet[A] = this

  override def foreach(f: A => Unit): Unit = ()

  override def +(elem: A): MySet[A] = new NonEmptySet[A](elem, this)

  override def -(elem: A): MySet[A] = this

  override def --(anotherSet: MySet[A]): MySet[A] = this

  override def ++(anotherSet: MySet[A]): MySet[A] = this

  override def &(anotherSet: MySet[A]): MySet[A] = this

  override def unary_! : MySet[A] = new PropertyBasedSet[A](_ => true)
}


// all elements that satisfy a property
// { x in A | property(x) }
class PropertyBasedSet[A](property: A => Boolean) extends MySet[A] {

  override def contains(elem: A): Boolean = property(elem)

  // not known whether the result is finite or not, so not able to check if elem in it, so break contract
  override def map[B](f: A => B): MySet[B] = politelyFail

  override def flatMap[B](f: A => MySet[B]): MySet[B] = politelyFail

  override def foreach(f: A => Unit): Unit = politelyFail

  override def filter(predicate: A => Boolean): MySet[A] = new PropertyBasedSet[A](x => property(x) && predicate(x))

  // { x in A | property(x) } + elem = { x in A | property(x) || x == element }
  override def +(elem: A): MySet[A] = new PropertyBasedSet[A](x => property(x) || x == elem)

  // { x in A | property(x) } + anotherSet = { x in A | property(x) || anotherSet contains x }
  override def ++(anotherSet: MySet[A]): MySet[A] = new PropertyBasedSet[A](x => property(x) || anotherSet(x))

  override def -(elem: A): MySet[A] = filter(x => x != elem)

  override def --(anotherSet: MySet[A]): MySet[A] = filter(!anotherSet)

  override def &(anotherSet: MySet[A]): MySet[A] = filter(anotherSet)

  override def unary_! : MySet[A] = new PropertyBasedSet[A](x => !property(x))

  def politelyFail = throw new IllegalArgumentException("Really deep rabbit hole")
}

class NonEmptySet[A](head: A, tail: MySet[A]) extends MySet[A] {
  override def contains(elem: A): Boolean = elem == head || tail.contains(elem)

  override def map[B](f: A => B): MySet[B] = tail.map(f) + f(head)

  override def flatMap[B](f: A => MySet[B]): MySet[B] = tail.flatMap(f) ++ f(head)

  override def filter(predicate: A => Boolean): MySet[A] = {
    val filteredTail = filter(tail)
    if (predicate(head)) filteredTail + head
    else filteredTail
  }

  override def foreach(f: A => Unit): Unit = {
    f(head)
    tail.foreach(f)
  }

  override def +(elem: A): MySet[A] = {
    if (this.contains(elem)) this
    else new NonEmptySet[A](elem, this)
  }

  override def ++(anotherSet: MySet[A]): MySet[A] = tail ++ anotherSet + head

  override def -(elem: A): MySet[A] = {
    if (head == elem) tail
    else tail - elem + head
  }

  override def --(anotherSet: MySet[A]): MySet[A] = filter(x => !anotherSet(x))

  override def &(anotherSet: MySet[A]): MySet[A] = filter(anotherSet)

  override def unary_! : MySet[A] = new PropertyBasedSet[A](x => !this.contains(x))
}

object MySet {

  def apply[A](values: A*): MySet[A] = {
    @tailrec
    def buildSet(valSeq: Seq[A], acc: MySet[A]): MySet[A] = {
      if (valSeq.isEmpty) acc
      else buildSet(valSeq.tail, acc + valSeq.head)
    }

    buildSet(values.toSeq, new EmptySet[A])
  }

}

object MySetPlayground extends App {
  val s = MySet(1, 2, 3, 4)
  s foreach println

  s + 5 ++ MySet(-1, -2) + 3 foreach println

  val s2 = s + 5 ++ MySet(-1, -2) + 4 + 5
  s2 flatMap (x => MySet(x, 10 * x))
  s2 foreach println


  val negative = !s //  all naturals not eq to 1,2,3,4
  println(negative(2)) // false
  println(negative(5)) // true

  val negativeEven = negative.filter(_%2 == 0)
  println(negativeEven(5))  // false
  println(negativeEven(6)) // true


  // Functional Seq - partial function from int to A (index to the element), throws exception otherwise
  val numbers = List(1,2,4)
  numbers(2) // apply

  // Functional Map - partial function from A(keys) to B(values)
  val phoneNumbers = Map(1 -> "asd", 2 -> "dfsdf")
  phoneNumbers(2) // apply

}
