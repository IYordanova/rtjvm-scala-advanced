package lectures.part4_implicits

object PimpMyLibrary extends App {

  implicit class RichInt(value: Int) { // must take only 1 argument
    def isEven: Boolean = value % 2 == 0

    def sqrt: Double = Math.sqrt(value)

    def times(function: () => Unit): Unit = {
      def timesAux(n: Int): Unit = {
        if (n <= 0) ()
        else {
          function()
          timesAux(n - 1)
        }
      }

      timesAux(value)
    }

    def *[T](list: List[T]): List[T] = {
      def concatenate(n: Int): List[T] = {
        if (n <= 0) List()
        else concatenate(n - 1) ++ list
      }

      concatenate(value)
    }
  }

  // Type - enrichment
  24.isEven // compiler search for anything implicit that can wrap an int and has isEven method

  import scala.concurrent.duration._

  2.seconds

  implicit class RichString(string: String) {
    def asInt: Int = Integer.valueOf(string)

    def encrypt(cypherDistance: Int): String = string.map(c => (c + cypherDistance).asInstanceOf[Char])
  }

  println("3".asInt + 2)
  println("John".encrypt(2))

  3.times(() => println("cdsvcs "))

  implicit def stringToInt(string: String): Int = Integer.valueOf(string)

  println("6" / 2)


  class RichAltInt(value: Int)

  implicit def enrich(value: Int): RichAltInt = new RichAltInt(value)


  // DANGER !!!
  implicit def intToBoolean(i: Int): Boolean = i == 1
  val condition = if(3) "OK" else "wrong"
  println(condition)

}
