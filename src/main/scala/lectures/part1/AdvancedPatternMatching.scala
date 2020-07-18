package lectures.part1

object AdvancedPatternMatching extends App {

  val numbers = List(1)

  val description = numbers match {
    case head :: Nil => "the only element is head"
    case _ => // do nothing
  }
  println(description)

  /*
     - constants
     - tuples
     - wildcards
     - case classes
     - some magic like above
     - custom type
   */

  class Person(val name: String, val age: Int) // let's say you cannot make it case class

  object Person {
    // used by the compiler to decompose the obj for pattern matching
    def unapply(person: Person): Option[(String, Int)] =
      Some((person.name, person.age))

    def unapply(age: Int): Option[String] =
      Some(if (age < 21) "minor" else "major")
  }

  val bob = new Person("Bob", 25)
  val greeting = bob match {
    case Person(n, a) => s"Hi, my name is $n and I am $a years old"
  }
  println(greeting)

  val legalStatus = bob.age match { // bob.age is the arg to the unapply method
    case Person(status) => s"My legal status is $status" // status is the returned val of the unapply
  }
  println(legalStatus)


  val n: Int = 9
  val property = n match {
    case x if x < 10 => "single digit"
    case x if x % 2 == 0 => "an even number"
    case _ => "no property"
  }
  println(property)

  object even {
    def unapply(arg: Int): Boolean = arg % 2 == 0
  }

  object singleDigit {
    def unapply(arg: Int): Boolean = arg > -10 && arg < 10
  }

  val property2 = n match {
    case singleDigit() => "single digit"
    case even() => "an even number"
    case _ => "no property"
  }
  println(property2)


  // infix pattern
  case class Or[A, B](a: A, b: B)

  val either = Or(2, "two")
  val des = either match {
    case number Or string => s"$number is writen as $string"
  }
  println(des)

  // decomposing sequences
  val vararg = numbers match {
    case List(1, _*) => "starting with one"
  }

  abstract class SomeList[+A] {
    def head: A = ???
    def tail: SomeList[A] = ???
  }
  case object Empty extends SomeList[Nothing]
  case class Con[+A](override val head: A, override val tail: SomeList[A]) extends SomeList[A]

  object SomeList {
    def unapplySeq[A](list: SomeList[A]): Option[Seq[A]] = {
      if(list == Empty) Some(Seq.empty)
      else unapplySeq(list.tail).map(list.head +: _)
    }
  }

  val someList: SomeList[Int] = Con(1, Con(2, Con(3, Empty)))
  val decomposed = someList match {
    case SomeList(1, 2, _*) => "starting with 1,2"
    case _ => "something else"
  }
  println(decomposed)

  // custom return types for unapply and unapplySeq
  abstract class Wrapper[T] {
    def isEmpty: Boolean
    def get: T
  }

  object PersonWrapper {
    def unapply(person: Person): Wrapper[String] = new Wrapper[String] {
      override def isEmpty: Boolean = false
      override def get: String = person.name
    }
  }

  println(bob match {
    case PersonWrapper(n) => s"This person's name is $n"
    case _ => "alien"
  })

}
