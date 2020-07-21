package lectures.part5

/*
   what it is?
   problem of type substitutions of generics
 */

object Variance extends App {

  trait Animal
  class Dog extends Animal
  class Cat extends Animal
  class Crocodile extends Animal

  class Cage[T]

  // covariance
  class CCage[+T]

  val ccage: CCage[Animal] = new CCage[Cat]

  // invariance
  class ICage[T]

  //  val icage: ICage[Animal] = new ICage[Cat]  // not allowed

  // contravariance
  class XCage[-T]

  val xcage: XCage[Cat] = new XCage[Animal] // valid!!!!


  class InvariantCage[T](val animal: T)

  class CovariantCage[+T](val animal: T)

  //  class ContravariantCage[-T](val animal: T) //  not allowed, cannot use covariant val (type T) in a covariant val position

  //  class CovariantVariableCage[+T](var animal: T) // not allowed, cannot pass T into contravariant var position

  //  class CovariantVariableCage[-T](var animal: T) // not allowed, cannot pass T into covariant var position

  class InvariantVariableCage[T](var animal: T) // OK

  //  trait AnotherCovariantCage[+T] {
  //    def addAnimal(animal: T) // CONTRAVARIANT POSITION - not allowed
  //  }

  trait AnotherContravariantCage[-T] {
    def addAnimal(animal: T) // CONTRAVARIANT POSITION - OK
  }


  // way to create a covariant collection:
  class Kitty extends Cat

  class MyList[+A] {
    def add[B >: A](element: B): MyList[B] = new MyList[B] // widening the type
  }

  val emptyList = new MyList[Kitty]
  val animals = emptyList.add(new Kitty)
  val moreAnimals = animals.add(new Cat) // returns list of cats
  val evenMoreAnimals = animals.add((new Dog)) // returns list of animals

  // method arguments are in contravariant position
  // return type are in covariant position

  class PetShop[-T] {
    def get[S <: T](isPuppy: Boolean, defaultAnimal: S): S = defaultAnimal
  }

  val shop: PetShop[Dog] = new PetShop[Animal]

  //  val evilCat = shop.get(true, new Cat) // illegal

  class TerraNova extends Dog

  val bigFurry = shop.get(true, new TerraNova)


  class Vehicle
  class Bike extends Vehicle
  class Car extends Vehicle
  class IList[T]

  class IParking[T](vehicles: List[T]) {
    def park(vehicle: T): IParking[T] = ???
    def impound(vehicles: List[T]): IParking[T] = ???
    def checkVehicles(conditions: String): List[T] = ???
    def flatMap[S](f: T => IParking[S]): IParking[S] = ???
  }

  class CParking[+T](vehicles: List[T]) {
    def park[S >: T](vehicle: S): CParking[S] = ???
    def impound[S >: T](vehicles: List[S]): CParking[S] = ???
    def checkVehicles(conditions: String): List[T] = ???
    def flatMap[S](f: T => CParking[S]): CParking[S] = ???
  }

  class XParking[-T](vehicles: List[T]) {
    def park(vehicle: T): XParking[T] = ???
    def impound(vehicles: List[T]): XParking[T] = ???
    def checkVehicles[S <: T](conditions: String): List[S] = ???
    def flatMap[R <: T, S](f: Function1[R, XParking[S]]): XParking[S] = ???
  }

  /*
      Rule of thumb:
      - covariance - when collection of things needed
      - contravariance - when collection of actions
   */


  class CParking2[+T](vehicles: IList[T]) {
    def park[S >: T](vehicle: S): CParking2[S] = ???
    def impound[S >: T](vehicles: IList[S]): CParking2[S] = ???
    def checkVehicles[S >: T](conditions: String): IList[S] = ???
  }

  class XParking2[-T](vehicles: IList[T]) {
    def park(vehicle: T): XParking2[T] = ???
    def impound[S <: T](vehicles: IList[S]): XParking2[S] = ???
    def checkVehicles[S <: T](conditions: String): IList[S] = ???
  }

}
