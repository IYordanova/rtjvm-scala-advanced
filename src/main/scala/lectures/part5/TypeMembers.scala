package lectures.part5

object TypeMembers extends App {

  class Animal

  class Cat extends Animal

  class Dog extends Animal

  class AnimalCollection {
    type AnimalType // abstract type member
    type BoundedAnimal <: Animal
    type SuperBoundedAnimal >: Dog <: Animal // multiple type bounds
    type AnimalC = Cat // aliases
  }


  val ac = new AnimalCollection
  val dog: ac.AnimalType = ??? // cannot construct it, no constructor
  //  val cat: ac.BoundedAnimal = new Cat // doesn't compile
  val pup: ac.SuperBoundedAnimal = new Dog // OK
  val cat: ac.AnimalC = new Cat // OK

  type CatAlias = Cat
  val anotherCat: CatAlias = new Cat


  // alternative to generics:
  trait MyList {
    type T
    def add(element: T): MyList
  }

  class NonEmptyList(value: Int) extends  MyList {
    override type T = Int
    override def add(element:Int): MyList = ???
  }

  // .type
  type CatsType = cat.type


  // enforce type to be applicable to some types only
  trait MList {
    type A
    def head: A
    def tail: MList
  }

  // make this not compile
  class CustomList(hd: String, tl: CustomList) extends MList {
    override type A = String
    def head = hd
    def tail = tl
  }

  // enforce this only
  class IntList(hd: Int, tl: IntList) extends MList {
    override type A = Int
    def head = hd
    def tail = tl
  }

  trait ApplicableToNumbers {
    type A <: Number
  }

  // does not compile
//  class CustomList2(hd: String, tl: CustomList2) extends MList with ApplicableToNumbers {
//    override type A = String
//    def head = hd
//    def tail = tl
//  }

//  class IntList2(hd: Int, tl: IntList) extends MList with ApplicableToNumbers {
//    override type A = Int
//    def head = hd
//    def tail = tl
//  }


}
