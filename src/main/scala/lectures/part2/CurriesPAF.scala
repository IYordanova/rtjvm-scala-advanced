package lectures.part2

object CurriesPAF extends App {
  // curried functions - multiparam functon
  val superAdder: Int => Int => Int = x => y => x + y
  val add3 = superAdder(3) //
  println(add3(2)) // 5
  println(superAdder(3)(2)) //5


  def curriedAdder(x: Int)(y: Int): Int = x + y

  // lifting = ETA-EXPANSION - create functions out of methods
  val add4: Int => Int = curriedAdder(4) // type specified is required

  // partial function applications
  val add5 = curriedAdder(5) _ //  tells the compiler to do the ETA-expansion to Int => Int

  // EXERCISE:
  val simpleAddFunction = (x: Int, y: Int) => x + y

  def simpleAddMethod(x: Int, y: Int) = x + y

  def curriedAddMethod(x: Int)(y: Int) = x + y

  // define add7 Int => Int = y => 7 + y

  val add7_a = (y: Int) => simpleAddFunction(7, y)
  val add7_b = (y: Int) => simpleAddMethod(7, y)
  val add7_c = (y: Int) => curriedAddMethod(7)(y)
  val add7_d = simpleAddFunction.curried(7)
  val add7_e = curriedAddMethod(7) _ // PAF - partially applied function
  val add7_f = curriedAddMethod(7)(_) // PAF
  val add7_g = simpleAddMethod(7, _: Int) // PAF
  val add7_h = simpleAddFunction(7, _: Int)


  // underscores:
  def concatenator(a: String, b: String, c: String) = a + b + c

  val insertName = concatenator("hello, I'm ", _: String, ", how are you")
  println(insertName("John"))



  def curriedFormatter(s: String)(num: Double): String = s.format(num)
  val numbers = List(Math.PI, Math.E, 1, 9.8, 1.3e-12)
  val simpleFormat = curriedFormatter("%4.2f") _
  val seriousFormat = curriedFormatter("%8.6f") _
  val preciseFormat = curriedFormatter("%14.12f") _

  println(numbers.map(preciseFormat))



  def byName(n: => Int) = n + 1
  def byFunction(f: () => Int) = f() + 1

  def method: Int = 42
  def parenMethod(): Int = 42


  byName(23)
  byName(method) // is evaluated to the result
  byName(parenMethod())
  byName(parenMethod)  // equivalent to the above
//  byName(() => 24)  - by name parameter is not the same as a higher order function
  byName((() => 23)())


//  byFunction(23)      // not OK
//  byFunction(method)  // not OK
  byFunction(parenMethod)  // ETA expansion
  byFunction(() => 24)
  byFunction(parenMethod _)





}
