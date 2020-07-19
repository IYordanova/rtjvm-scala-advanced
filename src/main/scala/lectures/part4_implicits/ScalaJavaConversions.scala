package lectures.part4_implicits

import java.{util => ju}

object ScalaJavaConversions extends App {

  import collection.JavaConverters._
  val javaSet: ju.Set[Int] = new ju.HashSet[Int]()
  (1 to 5).foreach(javaSet.add)
  println(javaSet)

  val scalaSet = javaSet.asScala


  import collection.mutable._
  val numbersBuffer = ArrayBuffer[Int](1,2,3)
  val juNumbersBuffer = numbersBuffer.asJava
  println(juNumbersBuffer.asScala eq numbersBuffer) // converting back returns exactly the same reference

  val numbersList = List(1,2,3)
  val juNumbersList = numbersList.asJava
  val backToScala = juNumbersList.asScala
  println(juNumbersList eq numbersList) // not same - immutable in scala to java list back to immutable
  println(backToScala == numbersList) // deep compare - equals

//  juNumbersList.add(4) // exception! it's immutable

  class ToScala[T](value: => T) {
    def asScala: T = value
  }

  implicit def asScalaOptional[T](o: ju.Optional[T]): ToScala[Option[T]] = new ToScala[Option[T]](
    if(o.isPresent) Some(o.get())
    else None
  )

  val juOptional: ju.Optional[Int] = ju.Optional.of(1)
  val scalaOption = juOptional.asScala
  println(scalaOption)

}
