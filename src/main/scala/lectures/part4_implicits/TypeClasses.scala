package lectures.part4_implicits

import java.util.Date

object TypeClasses extends App {

  case class User(name: String, age: Int, email: String)

  val john = User("John", 32, "john@gmail.com")

  trait HtmlSerializer[T] {
    def serialize(value: T): String
  }

  implicit object UserSerializer extends HtmlSerializer[User] {
    override def serialize(value: User): String = s"<div>${value.name} (${value.age}) years old <a hred=${value.email}/></div>"
  }

  implicit object IntSerializer extends HtmlSerializer[Int] {
    override def serialize(value: Int): String = s"<div>$value</div>"
  }

  object DateSerializer extends HtmlSerializer[Date] {
    override def serialize(value: Date): String = s"<div>${value.toString}></div>"
  }

  object HtmlSerializer {
    def serialize[T](value: T)(implicit serializer: HtmlSerializer[T]): String = serializer.serialize(value)
  }

  println(HtmlSerializer.serialize(43)(IntSerializer))
  println(HtmlSerializer.serialize(43))
  println(HtmlSerializer.serialize(john)) // john is of type user, there is a local implicit obj defined


  // 3
  implicit class HTMLEnrichment[T](value: T) {
    def toHTML(implicit serializer: HtmlSerializer[T]): String = serializer.serialize(value)
  }

  println(john.toHTML)

  // 4 context bounds
  def htmlBoilerplate[T](content: T)(implicit serializer: HtmlSerializer[T]): String = {
    "<html><body>" + content.toHTML(serializer) + "</body></html>"
  }

  // binds this to the implicit local serializer
  def htmlSugar[T: HtmlSerializer](content: T): String = {
    "<html><body>" + content.toHTML + "</body></html>"
  }


  // 5 implicitly
  case class Permissions(mask: String)
  implicit val defaultPermissions: Permissions = Permissions("0744")


  // in other part of the code:
  val standardPermissions = implicitly[Permissions]

}
