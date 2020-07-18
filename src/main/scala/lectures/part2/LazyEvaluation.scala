package lectures.part2

object LazyEvaluation extends App {

  // lazy values are evaluated ones, but only when used
  lazy val x: Int = throw new RuntimeException

  lazy val y: Int = {
    println("sf")
    42
  }

  println(y) // prints sf
  println(y) // only returns the value, does not print, doesn't evaluate the val


  // with side effects
  def sideEffectCondition: Boolean = {
    println("Boo")
    true
  }

  def simpleCondition: Boolean = false

  lazy val lazyCondition = sideEffectCondition

  println(if (simpleCondition && lazyCondition) "yes" else "no") // not printing Boo, first term is false, so lazyCondition never eval

  // in conjunction with call by name
  def byNameMethod(n: => Int): Int = n + n + n + 1

  def retrieveMagicValue = {
    // long computation or side effect
    Thread.sleep(1000)
    println("waiting")
    42
  }

  println(byNameMethod(retrieveMagicValue)) // waiting 3 times, re-evaluates 3 times
  // to avoid use:
  def byNameMethodBetter(n: => Int): Int = {
    lazy val t = n;
    t + t + t + 1
  }

  println(byNameMethodBetter(retrieveMagicValue)) // waits only ones - call by need

  // filtering with lazy vals
  def lessThan30(i: Int): Boolean = {
    println(s"$i is less than 30?")
    i < 30
  }

  def greaterThan20(i: Int): Boolean = {
    println(s"$i is greater than 20?")
    i > 20
  }

  val numbers = List(1, 25, 40, 5, 23)
  val lt30 = numbers.filter(lessThan30)
  val gt20 = lt30.filter(greaterThan20)
  println(gt20) // executes the filter on all numbers

  val lt30_lazy = numbers.withFilter(lessThan30) // uses lazy values under the hood
  val gt20_lazy = lt30_lazy.withFilter(greaterThan20) // =>
  println(gt20_lazy) // one var string representation , not evaluated
  gt20_lazy.foreach(println) // evaluates the numbers by need basis less, gt one by one, not all less, all greater

  // for comprehensions use withFilter with guards
  for {
    a <- List(1, 2, 3) if a % 2 == 0
  } yield a + 1
  // is equivalent to
  List(1,2,3).withFilter(_ % 2 == 0).map(_ + 1)


}
