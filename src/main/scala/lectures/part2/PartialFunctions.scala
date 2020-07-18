package lectures.part2

object PartialFunctions extends App {

  // defined on the whole Int domain
  val aFunction = (x: Int) => x + 1 // Function1[Int, Int] === Int => Int

  // can limit like : (not...great)
  class FunctionNotApplicableException extends RuntimeException

  val aFussyFunction = (x: Int) => {
    if (x == 1) 42
    else if (x == 2) 56
    else if (x == 5) 999
    else throw new FunctionNotApplicableException
  }
  // will also throw exception if not matching - a proper function
  val aNicerFussyFunction = (x: Int) => x match {
    case 1 => 42
    case 2 => 56
    case 5 => 999
  }
  // partial function value - eq to the above - also throws PM exception
  val aPartialFunction: PartialFunction[Int, Int] = {
    case 1 => 42
    case 2 => 56
    case 5 => 999
  }
  println(aPartialFunction(2))

  // PF utilities
  println(aPartialFunction.isDefinedAt(67)) // if the pf can be run with the arg
  // list to total function - returning optionals
  val lifted = aPartialFunction.lift
  println(lifted(2)) // Some(56)
  println(lifted(2134)) // None

  // chaining
  val aChain = aPartialFunction.orElse[Int, Int] {
    case 45 => 67
  }
  println(aChain(2)) // 56
  println(aChain(45)) // 67 - from the chained second partial function

  // PR extend normal function
  val aTotalFunction: Int => Int = {
    case 1 => 99
  }
  // higher order functions accept partial functions as well
  val aMappedList = List(1, 2, 3).map {
    case 1 => 32
    case 2 => 78
    case 3 => 342
  }
  println(aMappedList)

  /*
      Note: Partial Functions can only have 1 parameter type !!!!!!
   */

  // construct own partial function
  val aManualFussyFunction = new PartialFunction[Int, Int] {
    override def apply(x: Int): Int = x match {
      case 1 => 213
      case 2 => 21
      case 3 => 32
    }

    override def isDefinedAt(x: Int): Boolean = x == 1 || x == 2 || x == 3
  }
  val chatbot: PartialFunction[String, String] = {
    case "hello" => "Hi"
    case "goodbye" => "Bye"
    case _ => "don't understand that"
  }
  scala.io.Source.stdin.getLines().map(chatbot).foreach(println)

}
