package lectures.part4_implicits

object ImplicitsIntro extends App {

  val pair = "Daniel" -> "222"
  val intPair = 1 -> 2 // implicit class ArrowAssoc


  case class Person(name: String) {
    def greet = s"Hi, my name is $name"
  }
  implicit def fromStringToPerson(str: String): Person = Person(str)
  println("peter".greet) // compiler looks for anything that has greet methofd
  //  equivalent to
  println(fromStringToPerson("peter").greet)

  def increment(x: Int)(implicit amount: Int) = x + amount
  implicit val defaultAmount = 10
  increment(2)  // not same as default args, the compiler is looking and adding the arg


}
