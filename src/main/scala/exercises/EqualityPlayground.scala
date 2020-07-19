package exercises

import lectures.part4_implicits.TypeClasses.User

object EqualityPlayground extends App {

  trait Equal[T] {
    def apply(a: T, b: T): Boolean
  }
  object Equal {
    def apply[T](a: T, b: T)(implicit instance: Equal[T]) = instance.apply(a, b)
  }

  implicit object NameEquality extends Equal[User] {
    override def apply(a: User, b: User): Boolean = a.name == b.name
  }

  object FullEquality extends Equal[User] {
    override def apply(a: User, b: User): Boolean = a.name == b.name &&  a.email == b.email
  }

  val john = User("John", 32, "john@gmail.com")

  val anotherJohn = User("John", 22, "anotherJohn@gmail.com")
  println(Equal(john, anotherJohn)) // AD-HOC polymorphism


  implicit class TypeSafeEqual[T](value:T) {
    def === (other:T)(implicit equalizer: Equal[T]): Boolean = equalizer.apply(value, other)
    def !== (other:T)(implicit equalizer: Equal[T]): Boolean = !equalizer.apply(value, other)
  }

  /*
        TYPE SAFE!
      - converted to john.===(anotherJohn)
      - try wrapping john with something that has === method
      - the second argument is implicit - find it locally
   */
  println(john === anotherJohn)

}
