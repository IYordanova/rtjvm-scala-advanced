package lectures.part5

object FBoundedPolyMorphism extends App {


  // Solution 1:
  //  trait Animal {
  //    def breed: List[Animal]
  //  }
  //
  //  class Cat extends Animal {
  //    override def breed: List[Cat] = ???
  //  }
  //
  //  class Dog extends Animal {
  //    override def breed: List[Dog] = ??? // but can also be a list of cats => error-prone
  //  }


  // Solution 2: FBP
  //  trait Animal[A <: Animal[A]] { // recursive type - F- bounded polymorphism
  //    def breed: List[Animal[A]]
  //  }
  //
  //  class Cat extends Animal[Cat] { // it won't compile otherwise
  //    override def breed: List[Animal[Cat]] = ???
  //  }
  //
  //  class Dog extends Animal[Dog] {
  //    override def breed: List[Animal[Dog]] = ???
  //  }
  //
  //  class Crocodile extends Animal[Dog] {
  //    override def breed: List[Animal[Dog]] = ???  // still not enforced all the way
  //  }


  // Solution 3: FBP + self types
  //  trait Animal[A <: Animal[A]] { self: A =>
  //    def breed: List[Animal[A]]
  //  }
  //
  //  class Cat extends Animal[Cat] { // it won't compile otherwise
  //    override def breed: List[Animal[Cat]] = ???
  //  }
  //
  //  class Dog extends Animal[Dog] {
  //    override def breed: List[Animal[Dog]] = ??? // but can also be a list of cats => error-prone
  //  }
  //
  //  class Crocodile extends Animal[Dog] {
  //    override def breed: List[Animal[Dog]] = ???  // not compiling in this solution
  //  }
  //
  //  trait Fish extends Animal[Fish]
  //  class Shark extends Fish {
  //    override def breed: List[Animal[Fish]] = ???
  //  }
  //  class Cod extends Fish {
  //    override def breed: List[Animal[Fish]] = ???  // bringing it down one level - fails again
  //  }


  // Solution 4:
  //  trait Animal
  //  // A - type class description
  //  trait CanBreed[A] {
  //    def breed(a: A): List[A]
  //  }
  //
  //  class Dog extends Animal
  //  object Dog {
  //    // B - type instance
  //    implicit object DogsCanBreed extends CanBreed[Dog] {
  //      override def breed(a: Dog): List[Dog] = ???
  //    }
  //  }
  //
  //  // C - to convert into something that has access to the type class instance
  //  implicit class CanBreedOps[A](animal: A) {
  //    def breed(implicit canBreed: CanBreed[A]): List[A] = canBreed.breed(animal)
  //  }
  //
  //  val dog = new Dog
  //  dog.breed
  //
  //
  //  class Cat extends Animal
  //  object Cat {
  //    implicit object CatsCanBreed extends CanBreed[Dog] {  // so if we make an error
  //      override def breed(a: Dog): List[Dog] = ???
  //    }
  //  }
  //
  //  val cat = new Cat
  ////  cat.breed // error gets detected by the compiler


  // Solution 5:
  trait Animal[A] {
    def breed(a: A): List[A]
  }
  class Dog
  object Dog {
    implicit object DogAnimal extends Animal[Dog] {  // so if we make an error
      override def breed(a: Dog): List[Dog] = ???
    }
  }

  implicit class AnimalOps[A](animal: A) {
    def breed(implicit animalType: Animal[A]): List[A] = animalType.breed(animal)
  }

  val dog = new Dog
  dog.breed
}
