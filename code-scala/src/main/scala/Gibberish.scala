import akka.actor.ActorSystem
import akka.stream.ActorMaterializer
import play.api.libs.ws.ahc.StandaloneAhcWSClient
import play.api.libs.ws.StandaloneWSClient

import scala.concurrent.{Await, Future}
import scala.concurrent.ExecutionContext.Implicits.global
import scala.concurrent.duration._

object Gibberish extends App {

  val randomNumUrl = "http://randnum.herokuapp.com"
  val randomWordUrl = "http://random-word.herokuapp.com/"


  def ws[T](f: StandaloneWSClient => Future[T]): Future[T] = {
    implicit val system = ActorSystem()
    implicit val materializer = ActorMaterializer()

    val wsClient = StandaloneAhcWSClient()

    val future = f(wsClient)
    future.onComplete { t =>
      wsClient.close()
      system.terminate()
    }

    future
  }

  def await[T](f: => Future[T]): T = Await.result(f, 1.minute)

}
