package lectures.part5

object PathDependentTypes extends App {

  class Outer {
    class Inner
    object InnerObject
    type InnerType

    def print(i: Inner) = println(i)
    def printGeneral(i: Outer#Inner) = println(i)
  }


  // classes, objects can be defined anywhere
  // types only not in methods (besides as aliases)
  def aMethod: Int = {
    class HelperClass // valid !
    type HelperType = String
    2
  }

  val o = new Outer
//  val inner = new Inner // only exists in the context of Outer
  val inner = new o.Inner // need outer INSTANCE to access Inner

  val oo = new Outer
//  val otherInner: oo.Inner = new o.Inner // different types because diff instances!!!!!!

  o.print(inner)
//  oo.print(inner) // diff type!!

  o.printGeneral(inner)
  oo.printGeneral(inner)

  trait ItemLike {
    type Key
  }

  trait Item[K] extends ItemLike {
    type Key = K
  }
  trait IntItem extends Item[Int]
  trait StringItem extends Item[String]

  def get[ItemType <: ItemLike](key: ItemType#Key): ItemType = ???

  get[IntItem](42)
  get[StringItem]("sf")
//  get[IntItem]("ad") //not OK

}
