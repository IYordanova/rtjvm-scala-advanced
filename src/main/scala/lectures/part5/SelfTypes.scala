package lectures.part5

object SelfTypes extends App {

  // way of requiring a type to be mixed in

  trait Instrumentalist {
    def play(): Unit
  }

  // when concepts are not related and Singer extends Instrumentalist doesn't make sense
  trait Singer { self: Instrumentalist => // marker at language level to impl this as well
    def sing(): Unit
  }


  // Option 1:
  class LeadSinger extends Singer with Instrumentalist {
    override def sing(): Unit = ???
    override def play(): Unit = ???
  }

//  class Vocalist extends Singer {  // illegal!!
//    override def sing(): Unit = ???
//  }


  // Option 2:
  val jH = new Singer with Instrumentalist {
    override def sing(): Unit = ???
    override def play(): Unit = ???
  }

  // Option 3:
  class Guitarist extends Instrumentalist {
    override def play(): Unit = ???
  }

  val eC = new Guitarist with Singer {
    override def sing(): Unit = ???
  }


  class A
  class B extends A  // B is an A

  trait T
  trait S { self: T => } // S requires a T, not is a

  // cake pattern => "dependency injection", creates layers of abstraction

  trait Component
  trait DependentComponent { self: Component =>  } // instead of passing in the constructor like usually DI is implemented in JAVA

}
