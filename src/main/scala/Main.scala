import java.util.UUID

import akka.actor.{ActorRef, ActorSystem}
import akka.http.scaladsl.Http
import akka.http.scaladsl.model._
import akka.http.scaladsl.model.headers._
import akka.http.scaladsl.model.HttpMethods._
import akka.http.scaladsl.model.sse.ServerSentEvent
import akka.http.scaladsl.server.Directives._
import akka.stream.ActorMaterializer
import akka.stream.scaladsl.{BroadcastHub, Keep, Source}
import com.theunic.orderprocessing.domainmodel.Order
import com.theunic.orderprocessing.infrastructure.remoting._
import spray.json._

import scala.concurrent.{Await, ExecutionContextExecutor, Future}
import scala.concurrent.duration._
import scala.io.StdIn
import ch.megard.akka.http.cors.scaladsl.CorsDirectives._

object Main extends App {
  implicit val system: ActorSystem = ActorSystem("restaurant-orders")
  implicit val materializer: ActorMaterializer = ActorMaterializer()
  implicit val executionContext: ExecutionContextExecutor = system.dispatcher

  var orders: Map[UUID, ActorRef] = Map()

  import com.theunic.orderprocessing.infrastructure.serialization.JSON.JsonSupport._
  import akka.http.scaladsl.marshalling.sse.EventStreamMarshalling._

  val (sseActor, sseSource) =
    Source.actorRef[DTO.Order](10, akka.stream.OverflowStrategy.dropTail)
      .map(orderDto => ServerSentEvent(orderDto.toJson.compactPrint))
      .keepAlive(1.second, () => ServerSentEvent.heartbeat)
      .toMat(BroadcastHub.sink[ServerSentEvent])(Keep.both)
      .run()

  val routes =
    options {
      complete(
        HttpResponse(StatusCodes.OK)
          .withHeaders(
            `Access-Control-Allow-Methods`(OPTIONS, POST, PUT, GET, DELETE),
            `Access-Control-Allow-Origin`.*,
            `Access-Control-Allow-Headers`("Content-Type", "X-Requested-With")
          )
      )
    } ~
    handleRejections(corsRejectionHandler) {
      cors() {
        pathPrefix("api") {
          pathPrefix("orders") {
            path(JavaUUID) { id =>
              put {
                orders += (id -> system.actorOf(Order.props(id)))
                sseActor ! DTO.Order(id)
                complete(StatusCodes.Created -> DTO.Order(id))
              }
            } ~
              pathEnd {
                complete(
                  orders
                    .keys
                    .map(id => DTO.Order(id))
                )
              }
          } ~
            path("events") {
              get {
                complete(sseSource)
              }
            }
        }
      }
    } ~
    (get & pathPrefix("app")) {
      (pathEndOrSingleSlash & redirectToTrailingSlashIfMissing(StatusCodes.TemporaryRedirect)) {
        getFromResource("web/src/index.html")
      } ~ {
        getFromResourceDirectory("web")
      }
    }

  val f = for {
    bindingFuture <- Http().bindAndHandle(routes, "0.0.0.0", 3000)
    waitOnFuture  <- Future.never
  } yield waitOnFuture

  sys.addShutdownHook {
    system.terminate()
  }

  Await.ready(f, Duration.Inf)
}
