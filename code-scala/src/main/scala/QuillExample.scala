import io.getquill.{H2JdbcContext, SnakeCase}

import scala.io.Source

object QuillExample extends App {

  val ctx = new H2JdbcContext(SnakeCase, "ctx")
  import ctx._

  val schema = Source.fromResource("setup.sql").mkString

  executeAction(schema)

  case class Person(id: Int, name: String, favoriteColor: String)
  case class Contact(id: Int, email: Option[String], personId: Int)

  val q = quote {
    ""
  }

  val result = run(q)

  result.foreach(println)

}
