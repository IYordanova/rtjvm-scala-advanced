package lectures.part4_implicits

object OrganizingImplicits extends App {

  //scala looks for this in the **scala.Predef** package
  println(List(1, 2, 5, 4, 3, 2).sorted) // sorted takes an implicit ordering value

  implicit val reverseOrdering: Ordering[Int] = Ordering.fromLessThan(_ > _)

  //  implicit val normalOrdering: Ordering[Int] = Ordering.fromLessThan(_ > _)

  /*
    Implicits:
     - val/var
     - object
     - accessor methods - defs with no parentheses
   */

  case class Person(name: String, age: Int)

  val persons = List(
    Person("Steve", 30),
    Person("Amy", 23),
    Person("John", 66)
  )

  //  implicit val personAplhaOrdering: Ordering[Person] = Ordering.fromLessThan((a, b) => a.name.compareTo(b.name) < 0)
  //  println(persons.sorted) // error if not ordering defined implicitly

  /*
    Implicits scope:
      - normal / local scope
      - imported scope
      - the companion objects of all types involved in the method signatures, example - for sorteed:
              - List
              - Ordering
              - all types involved (a,b - Person)
   */

  /*
       for multiple implicit values:
       - the good implicit should be in the companion object
       - the other ones separately packaged, to be imported and used explicitly by the user:
   */

  object AlphabeticNameOrdering {
    implicit val personNameOrdering: Ordering[Person] = Ordering.fromLessThan((a, b) => a.name.compareTo(b.name) < 0)
  }

  object AgeOrdering {
    implicit val personAgeOrdering: Ordering[Person] = Ordering.fromLessThan((a, b) => a.age < b.age)
  }

  import AlphabeticNameOrdering.personNameOrdering

  println(persons.sorted)


  case class Purchase(nUnits: Int, unitPrice: Double)

  object Purchase {
    implicit val totalPriceOrdering: Ordering[Purchase] = Ordering.fromLessThan(
      (a, b) => a.nUnits * a.unitPrice < b.nUnits * b.unitPrice)
  }

  object UnitCountOrdering {
    implicit val unitCountOrdering: Ordering[Purchase] = Ordering.fromLessThan((a, b) => a.nUnits < b.nUnits)
  }

  object UnitPriceOrdering {
    implicit val unitPriceOrdering: Ordering[Purchase] = Ordering.fromLessThan((a, b) => a.unitPrice < b.unitPrice)
  }

}
