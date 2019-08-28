import com.google.cloud.bigquery.BigQueryOptions
import scalikejdbc._
import scalikejdbc.bigquery._

object BigQueryStackoverflow extends App {

  val bigQuery = BigQueryOptions.getDefaultInstance.getService

  val executor = new QueryExecutor(bigQuery, QueryConfig())

  case class PostQuestion(id: Int, title: String, favoriteCount: Int, viewCount: Int) {
    lazy val url = "https://stackoverflow.com/questions/" + id
  }

  object PostQuestion extends SQLSyntaxSupport[PostQuestion] {
    override def tableNameWithSchema = "`bigquery-public-data.stackoverflow.posts_questions`"
    override val columns = Seq("id", "title", "favorite_count", "view_count")

    val pq = syntax("pq")

    def apply(rs: WrappedResultSet): PostQuestion = apply(rs, pq.resultName)
    def apply(rs: WrappedResultSet, rn: ResultName[PostQuestion]): PostQuestion = new PostQuestion(
      id = rs.get[Int](rn.id),
      title = rs.get[String](rn.title),
      favoriteCount = rs.get[Option[Int]](rn.favoriteCount).getOrElse(0),
      viewCount = rs.get[Int](rn.viewCount)
    )
  }

  import PostQuestion._

  case class GitHubContentsJs(soId: Int, content: String)

  object GitHubContentsJs extends SQLSyntaxSupport[GitHubContentsJs] {
    override def tableNameWithSchema = "`fh-bigquery.github_extracts.contents_js`"
    override val columns = Seq("so_id", "content")

    val ghcjs = syntax("ghcjs")

    def apply(rs: WrappedResultSet): GitHubContentsJs = apply(rs, ghcjs.resultName)
    def apply(rs: WrappedResultSet, rn: ResultName[GitHubContentsJs]): GitHubContentsJs = new GitHubContentsJs(
      soId = rs.get[Int](rn.soId),
      content = rs.get[String](rn.content)
    )
  }

  import GitHubContentsJs._

  /*
  val queryStackOverflow =
    selectFrom(PostQuestion as pq)
      .orderBy(pq.viewCount).desc
      .limit(50)

  println(queryStackOverflow.statement.value)

  val responseStackOverflow = queryStackOverflow.map(PostQuestion(_)).list.run(executor)
  responseStackOverflow.result.foreach(println)
   */

  def regexp(column: SQLSyntax, r: String): SQLSyntax = {
    val regex = SQLSyntax.createUnsafely(s"r'$r'")
    sqls"REGEXP_EXTRACT($column, $regex)"
  }
  def cast(it: SQLSyntax, to: String): SQLSyntax = {
    val named = SQLSyntax.createUnsafely(to)
    sqls"CAST($it AS $named)"
  }

  implicit class RichSQLSyntax(self: SQLSyntax) {
    def named(name: String): SQLSyntax = {
      val to = SQLSyntax.createUnsafely(name)
      sqls"$self $to"
    }
  }

  implicit class RichSelectSQLBuilder[A](self: SelectSQLBuilder[A]) {
    def join[B](sqlBuilder: SQLBuilder[B], name: SQLSyntax): SelectSQLBuilder[A] = {
      //val named = SQLSyntax.createUnsafely()
      self.copy(sqls"${self.sql} JOIN (${sqlBuilder.sql}) $name")
    }
  }

  val queryGitHub =
    select(cast(regexp(ghcjs.content, "stackoverflow.com/questions/([0-9]+)/"), "INT64").named("si_on_ghcjs"), sqls.count.named("c_on_ghcjs"))
      .from(GitHubContentsJs as ghcjs)
      .where.like(ghcjs.content, "%stackoverflow.com/questions/%")
      .groupBy(sqls"si_on_ghcjs")
      .having(sqls"si_on_ghcjs>0")
      .orderBy(sqls"c_on_ghcjs").desc
      .limit(10)

  val query = bq {
    selectFrom(PostQuestion as pq).join(queryGitHub, sqls"ghcjs").on(pq.id, sqls"si_on_ghcjs").limit(10)
  }

  println(query.statement)

  val response = query.map(PostQuestion(_)).list.run(executor)
  response.result.foreach(println)

}
