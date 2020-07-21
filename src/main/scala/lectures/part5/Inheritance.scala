package lectures.part5

object Inheritance extends App {

  // convenience
  trait Writer[T] {
    def write(value: T): Unit
  }

  trait Closeable {
    def close(status: Int): Unit
  }

  trait GenericStream[T] {
    def foreach(f: T => Unit): Unit
  }

  def processStream[T](stream: GenericStream[T] with Writer[T] with Closeable) : Unit = {
    stream.foreach(println)
    stream.close(0)
  }

  // diamond problem
  trait Animal {
    def name: String
  }
  trait Lion extends Animal {
    override def name: String = "lion"
  }
  trait Tiger extends Animal {
    override def name: String = "tiger"
  }
  class Mutant extends Lion with Tiger
  val m = new Mutant
  println(m.name)  // prints tiger, because compiler picks the last override always!!!


  // the super problem + type linearization
  trait Cold {
    def print = println("cold")
  }
  trait Green extends Cold {
    override def print = {
      println("green")
      super.print
    }
  }
  trait Blue extends Cold {
    override def print = {
      println("blue")
      super.print
    }
  }

  class Red {
    def print = println("red")
  }

  class White extends Red with Green with Blue {
    override def print: Unit = {
      println("white")
      super.print
    }
  }

  val color = new White
  color.print // prints white -> blue -> green -> cold,
  // because super is interpreted as the type on the left of the current one in the linearization below:
  // AnyRef with <Red> with <Cold> with <Green> with <Blue> with <White>


}
