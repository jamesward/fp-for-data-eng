import akka.Done
import akka.actor.ActorSystem
import akka.http.scaladsl.Http
import akka.http.scaladsl.model.StatusCodes
import akka.http.scaladsl.model.ws.{Message, TextMessage, WebSocketRequest}
import akka.stream.ActorMaterializer
import akka.stream.scaladsl.{Flow, Keep, Sink, Source}
import org.apache.flink.streaming.api.functions.source.SourceFunction

import scala.concurrent.{Await, Future, Promise}
import scala.concurrent.duration.Duration

class WebSocketSourceFunction(url: String) extends SourceFunction[String] {

  @transient
  lazy implicit private val system = ActorSystem()

  @transient
  lazy implicit private val materializer = ActorMaterializer()

  override def run(ctx: SourceFunction.SourceContext[String]): Unit = {
    import system.dispatcher

    val sink: Sink[Message, Future[Done]] = Sink.foreach {
      case message: TextMessage.Strict =>
        ctx.collect(message.text)
      case message: Message =>
        println(s"Could not handle message: $message")
    }

    val flow: Flow[Message, Message, Promise[Option[Message]]] = Flow.fromSinkAndSourceMat(sink, Source.maybe[Message])(Keep.right)

    val (webSocketUpgradeResponseFuture, _) = {
      Http().singleWebSocketRequest(WebSocketRequest(url), flow)
    }

    webSocketUpgradeResponseFuture.foreach { webSocketUpgradeResponse =>
      if (webSocketUpgradeResponse.response.status != StatusCodes.SwitchingProtocols) {
        println(s"Could not connect to WebSocket: ${webSocketUpgradeResponse.response}")
        system.terminate()
        ctx.close()
      }
    }

    Await.result(system.whenTerminated, Duration.Inf)
  }

  override def cancel(): Unit = {
    Await.result(system.terminate(), Duration.Inf)
  }

}
