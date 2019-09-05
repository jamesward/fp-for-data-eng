import org.apache.flink.streaming.api.scala._
import play.api.libs.json.{JsValue, Json}

object FlinkExample extends App {

  val url = "ws://stackoverflow-to-ws.default.35.193.208.0.xip.io/questions"

  val env = StreamExecutionEnvironment.createLocalEnvironment()

  val sourceFunction = new WebSocketSourceFunction(url)

  val stream = env.addSource(sourceFunction)

  val total = (totals: Map[String, Int], q: JsValue) => {
    val newTotals = (q \ "tags").as[Seq[String]].map { tag =>
      val total = totals.getOrElse(tag, 0) + 1
      (tag, total)
    }.toMap

    totals ++ newTotals
  }

  stream.print()

  env.execute()

}
