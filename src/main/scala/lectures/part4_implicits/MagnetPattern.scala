package lectures.part4_implicits

import scala.concurrent.Future
import scala.concurrent.ExecutionContext.Implicits.global

object MagnetPattern extends App {

  /*
      Aims to solve problems created by method overloading
      - type erasure
      - lifting doesn't work for all overloads  i.e val receiveFV = receive._ ???
      - code duplication
      - default args and type inferrence
   */
  class P2PRequest

  class P2PResponse

  class Serializer[T]

  trait Actor {
    def receive(statusCode: Int): Int

    def receive(request: P2PRequest): Int

    def receive(response: P2PResponse): Int

    def receive[T: Serializer](message: T): Int

    def receive[T: Serializer](message: T, statusCode: Int): Int

    def receive(future: Future[P2PRequest]): Int
  }


  // pattern

  trait MessageMagnet[Result] {
    def apply(): Result
  }

  def receive[R](magnet: MessageMagnet[R]): R = magnet()


  implicit class FromP2PRequest(request: P2PRequest) extends MessageMagnet[Int] {
    def apply(): Int = {
      println("handling P2PRequest")
      1
    }
  }

  implicit class FromP2PResponse(response: P2PResponse) extends MessageMagnet[Int] {
    def apply(): Int = {
      println("handling P2PResponse")
      2
    }
  }

  receive(new P2PRequest)
  receive(new P2PResponse)

  implicit class FromP2PRequestFuture(request: Future[P2PRequest]) extends MessageMagnet[Int] {
    def apply(): Int = {
      println("handling P2PRequest Future")
      3
    }
  }

  implicit class FromP2PResponseFuture(response: Future[P2PResponse]) extends MessageMagnet[Int] {
    def apply(): Int = {
      println("handling P2PResponse Future")
      4
    }
  }

  receive(Future(new P2PRequest))
  receive(Future(new P2PResponse))


  //
  trait MathLib {
    def add1(x: Int) = x + 1

    def add1(s: String) = s.toInt + 1
  }

  trait AddMagnet {
    def apply(): Int
  }

  def add1(magnet: AddMagnet): Int = magnet()

  implicit class AddInt(x: Int) extends AddMagnet {
    override def apply(): Int = x + 1
  }

  implicit class AddString(s: String) extends AddMagnet {
    override def apply(): Int = s.toInt + 1
  }

  val addFV = add1 _ // OK because the result of the magnet is always Int, known, as opposed to type var
  println(addFV(1))
  println(addFV("2"))


  // doesn't work with by name parameters

  class Handler {
    def handle(s: => String) = {
      println(s)
      println(s)
    }
  }

  trait HandleMagnet {
    def apply(): Unit
  }

  def handle(magnet: HandleMagnet) = magnet()

  implicit class StringHandle(s: => String) extends HandleMagnet {
    override def apply(): Unit = {
      println(s)
      println(s)
    }
  }

  def sideEffectMethod(): String = {
    println("sdf")
    "magnet"
  }

  //  handle(sideEffectMethod())
  handle {
    println("sdf") // this is only printed once as a side effect on var eval
    "magnet" // this is only converted to the magnet class
  }

}
