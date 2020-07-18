package lectures.part1

import scala.util.Try

object DarkSugars extends App {

  // 1. - methods with single param
  def singleArgMethod(arg: Int): String = s"$arg little ducks..."

  val description = singleArgMethod {
    // some code...
    42
  }

  val aTryInstance = Try { // apply method for Try
  }

  List(1, 2, 3).map { x =>
    x + 1
  }


  // 2. - single abstract method pattern
  trait Action {
    def act(x: Int): Int
  }

  val anInstance: Action = (x: Int) => x + 1
  val aThread = new Thread(() => println("this is a thread"))

  abstract class AnAbstractType {
    def implemented: Int = 23
    def f(a: Int): Unit
  }

  val anAbstractInstance: AnAbstractType = (a: Int) => println("sweet")


  // 3. - :: and #:: methods  - last char decides the associativity of a method
  // on whichever side the : is - that' s the association side
  val prependedList = 2 :: List(3, 4)
  1 :: 2 :: 3 :: List(4, 5)

  class MyStream[T] {
    def -->:(value: T): MyStream[T] = this // actual implementation
  }

  val myStream = 1 -->: 2 -->: 3 -->: new MyStream[Int]


  // 4. - multi-word method naming
  class TeenGirl(name: String) {
    def `and then said`(gossip: String) = println(s"$name said $gossip")
  }

  val lilly = new TeenGirl("Lilly")
  lilly `and then said` "Scala is so cool"


  // 5. - infix types
  class Composite[A, B]

  val composite: Composite[Int, String] = ???
  // eq to:
  val composite2: Int Composite String = ???

  class -->[A, B]

  val towards: Int --> String = ???


  // 6. - update method
  val anArray = Array(1, 2, 3)
  anArray(2) = 7
  // is eq to
  anArray.update(2, 7)


  // 7. - setters for mutable containers
  class Mutable {
    private var internalMember: Int = 0
    def member = internalMember // "getter"
    def member_=(value: Int): Unit = { // value
      internalMember = value
    }
  }
  val aMutableContainer = new Mutable
  aMutableContainer.member = 42
  // is eq to:
  aMutableContainer.member_=(42)

}
