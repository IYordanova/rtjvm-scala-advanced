package lectures.part5

object Reflection extends App {

  case class Person(name: String) {
    def sayMyName(): Unit = println(s"Hi, my name is $name")
  }

  // 0 - import
  import scala.reflect.runtime.{universe => ru}

  // 1 - mirror
  val m = ru.runtimeMirror(getClass.getClassLoader)

  // 2 - create class object
  val clazz = m.staticClass("lectures.part5.Reflection.Person")  // class symbol/description

  // 3 - create a reflected mirror
  val cm = m.reflectClass(clazz)   // class mirror, can access methods and fields

  // 4 - get constructor
  val constr = clazz.primaryConstructor.asMethod // also a symbol/ description

  // 5 - constructor mirror
  val constrMirror = cm.reflectConstructor(constr)

  // 6 - invoke the constructor
  val instance = constrMirror.apply("John")

  println(instance)

  // ----------------------

  val p = Person("Mary") // gettig this as serialized obj
  val methodName = "sayMyName" // computed somewhere else
  //=>
  val reflected = m.reflect(p)
  val methodSymbol = ru.typeOf[Person].decl(ru.TermName(methodName)).asMethod
  val method = reflected.reflectMethod(methodSymbol)

  method.apply()

  // --------------------------------

  // type erasure
  val numbers = List(1,2,3)
  numbers match {
    case listOfStrings: List[String] => println("strings") // types are erased and this is printed!!!!!
    case listOfInts: List[Int] => println("ints")
  }

  def processList(list: List[Int]): Int  = 2
//  def processList(list: List[String]): Int  = 2 //  doesn't compile, types erased

  // Type Tags

  import ru._

  val ttag = typeTag[Person]
  println(ttag.tpe)

  class MyMap[K, V]

  def getTypeArgs[T](value: T)(implicit typeTag: TypeTag[T]) =
    typeTag.tpe match {
      case TypeRef(_, _, typeArgs) => typeArgs
      case _ => List()
    }


  val myMap = new MyMap[Int, String]
  val typeArgs = getTypeArgs(myMap)
  println(typeArgs)


  def isSubtype[A, B](implicit ttagA: TypeTag[A], ttagB: TypeTag[B]): Boolean = {
    ttagA.tpe <:< ttagB.tpe
  }

  class Animal
  class Dog extends Animal

  println(isSubtype[Dog, Animal]) // the TypeTags are created on compile type before the types are erased

}
