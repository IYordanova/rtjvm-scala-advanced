package lectures.part5

object StructuralTypes extends App {

  // 1. Structural types
  type JavaClosable = java.io.Closeable

  class HipsterClosable {
    def close(): Unit = println("svb")
  }

  // structural type
  type UnifiedClosable = {
    def close(): Unit
  }

  def closeQuietly(closable: UnifiedClosable): Unit = closable.close()

  closeQuietly(new JavaClosable {
    override def close(): Unit = ???
  })
  closeQuietly(new HipsterClosable)

  // 2. Type refinements - enriching a type
  type AdvancedCloseable = JavaClosable {
    def closeSilently(): Unit
  }

  class AdvancedJavaCloseable extends JavaClosable {
    override def close(): Unit = println("closes")
    def closeSilently(): Unit = println("closes silently")
  }

  def closeShh(advClosable: AdvancedCloseable): Unit = advClosable.closeSilently()

  closeShh(new AdvancedJavaCloseable)


  // 3. Using structural types as standalone types - define inline type ?!?!?
  def alternativeClose(closable: { def close(): Unit }): Unit = closable.close()

  // 4. Type-checking - duck typing mechanism
  type SoundMaker = {
    def makeSound() : Unit
  }

  class Dog {
    def makeSound(): Unit = println("bark")
  }

  class Car {
    def makeSound(): Unit = println("vroom")
  }
  // Dog and Car conform to the SoundMaker def - static duck typing
  val dog: SoundMaker = new Dog
  val car: SoundMaker = new Car

  // caveat: based on reflection => performance impact

  // ---------------------------------------
  trait CBL[+T] {
    def head: T
    def tail: CBL[T]
  }

  class Human {
    def head: Brain = new Brain
  }

  class Brain {
    override def toString: String = "Brain"
  }

  // compatible with Human and CBL!!
  def f[T](somethingWithAHead: { def head: T}): Unit = println(somethingWithAHead.head)

  case object CBNil extends CBL[Nothing] {
    override def head: Nothing = ???

    override def tail: CBL[Nothing] = ???
  }

  case class CBCons[T](override val head: T, override val tail: CBL[T]) extends CBL[T]

  f(CBCons(2, CBNil))
  f(new Human)  // compiler figures out T is Brain


  // ---------------------------------------

  object HeadEqualizer {
    type Headable[T] = { def head: T }
    def ===[T](a:Headable[T], b: Headable[T]): Boolean = a.head == b.head
  }

  val brainsList = CBCons(new Brain, CBNil)
  HeadEqualizer.===(brainsList, new Human) // OK
  // but also
  val stringsList = CBCons("brain", CBNil)
  HeadEqualizer.===(stringsList, new Human) // OK, but wrong - on reflection T is deleted - not type-safe



}
