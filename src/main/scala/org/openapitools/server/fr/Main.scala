package org.openapitools.server.fr

import org.apache.pekko
import pekko.actor.typed.scaladsl.Behaviors
import pekko.http.scaladsl.server.Route
import pekko.http.scaladsl.server.Directives._
import pekko.http.scaladsl.model.StatusCodes
import pekko.http.scaladsl.marshalling.ToEntityMarshaller
import pekko.http.scaladsl.unmarshalling.FromEntityUnmarshaller
import spray.json.DefaultJsonProtocol._
import spray.json.RootJsonFormat
import pekko.http.scaladsl.marshallers.sprayjson.SprayJsonSupport._
import scala.concurrent.{ExecutionContext, Future}
import org.openapitools.server.api.SubmitDocumentApiService
import org.openapitools.server.model.{Page}
import pekko.util.Timeout
import scala.concurrent.duration._
import org.apache.pekko.actor.typed.scaladsl.Behaviors
import org.openapitools.server.api.SubmitDocumentApi
import org.openapitools.server.api.SubmitDocumentApiMarshaller
import org.apache.pekko.http.scaladsl.server.Route
import org.apache.pekko.http.scaladsl.model.StatusCodes
import org.apache.pekko.http.scaladsl.marshalling.ToEntityMarshaller
import org.apache.pekko.http.scaladsl.unmarshalling.FromEntityUnmarshaller

import org.apache.pekko.actor.typed.ActorSystem
import org.apache.pekko.http.scaladsl.Http
import spray.json._
import scala.util.Failure
import scala.util.Success
import org.apache.pekko.actor.typed.ActorRef
import org.apache.pekko.actor.typed.ActorSystem
import org.apache.pekko.actor.typed.scaladsl.AskPattern._
import org.apache.pekko.util.Timeout
import scala.concurrent.duration._
import org.apache.pekko.actor.typed.scaladsl.Behaviors
import org.openapitools.server.api.TurnPageApiService
import org.openapitools.server.api.TurnPageApiMarshaller
import org.openapitools.server.model.TurnPagePostRequest
import org.openapitools.server.api.GoBackApi
import org.openapitools.server.api.GoBackApiService
import org.openapitools.server.api.GoBackApiMarshaller
import org.openapitools.server.model.GoBackPostRequest
import org.openapitools.server.api.TurnPageApi
import org.openapitools.server.api.GetWeatherApiService
import org.openapitools.server.api.GetWeatherApiMarshaller
import org.openapitools.server.model.GetWeatherGet200Response
import pekko.http.scaladsl.model._
import org.openapitools.server.fr.actors.PageActor
import org.openapitools.server.api
import org.openapitools.server.fr.actors.WeatherActor


object ServerPresentation {

  // Create an instance of the actor system
  val system: ActorSystem[Nothing] =
    ActorSystem(Behaviors.empty, "PresentationSystem")
  implicit val scheduler: pekko.actor.typed.Scheduler = system.scheduler
  implicit val executionContext = system.executionContext
// Create an instance of the PageActor
  val pageActor: ActorRef[PageActor.Command] =
    system.systemActorOf(PageActor(), "pageActor")


 val getWeatherActor: ActorRef[PageActor.Command] =
    system.systemActorOf(PageActor(), "getWeather")
  
val weatherActor: ActorRef[WeatherActor.Command] =
    system.systemActorOf(WeatherActor(), "weatherActor")


// Set a timeout for the ask pattern
  implicit val timeout: Timeout = 3.seconds


import org.apache.pekko.actor.typed.ActorSystem
import org.apache.pekko.http.scaladsl.Http
import org.apache.pekko.http.scaladsl.model._
import org.apache.pekko.http.scaladsl.unmarshalling.Unmarshal
import scala.concurrent.Future

// def getWeatherFrom(city: String)(implicit system: ActorSystem[_]): Future[String] = {
//   val apiKey = "cef6e31d67ff7c961d670ac8c514b551"
//   val apiUrl = s"http://api.openweathermap.org/data/2.5/weather?q=$city&appid=$apiKey"

//   val responseFuture: Future[HttpResponse] = Http().singleRequest(HttpRequest(uri = apiUrl))
//   responseFuture.flatMap { response =>
//     Unmarshal(response.entity).to[String]
//   }
// }






  /** ****************SubmitApi***************************
    */

  object SubmitApi extends SubmitDocumentApiService {
    override def submitDocumentPost(requestBody: Seq[Page]): Route = {
      val futureResponse = pageActor.ask(PageActor.SaveDocument(requestBody, _))
      onSuccess(futureResponse) { response =>
        complete(StatusCodes.OK, response)
      }
      
    }

  }

  object SubmitMarshaller extends SubmitDocumentApiMarshaller {
    implicit val pageFormat
        : RootJsonFormat[org.openapitools.server.model.Page] = jsonFormat1(
      Page.apply
    )
    override implicit def fromEntityUnmarshallerStringList
        : FromEntityUnmarshaller[Seq[Page]] = immSeqFormat(pageFormat)
  }


  object TurnApi extends TurnPageApiService {
    override def turnPagePost(
        turnPagePostRequest: TurnPagePostRequest
    ): Route = {
      val futureResponse =
        pageActor.ask(PageActor.Turn(turnPagePostRequest.pageNumber, _))
      onSuccess(futureResponse) { response =>
        complete(StatusCodes.OK, response)
      }
    }
  }

  object GoBackApi extends GoBackApiService {
    override def goBackPost(goBackPostRequest: GoBackPostRequest): Route = {
      val futureResponse =
        pageActor.ask(PageActor.Back(goBackPostRequest.steps, _))
      onSuccess(futureResponse) { response =>
        complete(StatusCodes.OK, response)
      }
    }
  }

  object TurnMarshaller extends TurnPageApiMarshaller {
    implicit val turnPageRequestFormat: RootJsonFormat[TurnPagePostRequest] =
      jsonFormat1(TurnPagePostRequest)
    override implicit def fromEntityUnmarshallerTurnPagePostRequest
        : FromEntityUnmarshaller[TurnPagePostRequest] = turnPageRequestFormat
  }

  object GoBackMarshaller extends GoBackApiMarshaller {
    implicit val goBackPageRequestFormat: RootJsonFormat[GoBackPostRequest] =
      jsonFormat1(GoBackPostRequest)
    override implicit def fromEntityUnmarshallerGoBackPostRequest
        : FromEntityUnmarshaller[GoBackPostRequest] = goBackPageRequestFormat
  }

  /** ****************GetWeatherApi***************************
    */
  // object GetWeatherApi extends GetWeatherApiService {
  //   override def getWeatherGet()(implicit
  //       toEntityMarshallerGetWeatherGet200Response: ToEntityMarshaller[
  //         GetWeatherGet200Response
  //       ]
  //   ): Route = {
  //     implicit val system: pekko.actor.typed.ActorSystem[Nothing] = system

  //     getWeatherFrom("Paris")(system).onComplete { 
  //       case Success(weather) => complete(StatusCodes.OK, weather)
  //       case Failure(ex) => complete(StatusCodes.InternalServerError, ex.getMessage)
  //     }
  //   }
  // }


object GetWeatherApi extends GetWeatherApiService {
  override def getWeatherGet()(implicit
      toEntityMarshallerGetWeatherGet200Response: ToEntityMarshaller[
        GetWeatherGet200Response
      ]
  ): Route = {
      val futureWeather = weatherActor.ask(WeatherActor.GetWeather("Paris", _))
      print("Mitshibishi")
    
      onComplete(futureWeather) { 
      case Success(weather) => complete(StatusCodes.OK, weather)
      case Failure(ex) => complete(StatusCodes.InternalServerError, ex.getMessage)
    }
        }
      }


  object GetWeatherMarshaller extends GetWeatherApiMarshaller {
    implicit val getWeatherGet200ResponseFormat
        : RootJsonFormat[GetWeatherGet200Response] =
      jsonFormat2(GetWeatherGet200Response)
    override implicit def toEntityMarshallerGetWeatherGet200Response
        : ToEntityMarshaller[GetWeatherGet200Response] =
      getWeatherGet200ResponseFormat
  }

  /** ****************Main***************************
    */
  def main(args: Array[String]): Unit = {
    implicit val system: pekko.actor.typed.ActorSystem[Nothing] =
      pekko.actor.typed.ActorSystem(Behaviors.empty, "PresentationSystem")
    implicit val executionContext = system.executionContext

    val submitApi = new SubmitDocumentApi(SubmitApi, SubmitMarshaller)
    val turnPageApi = new TurnPageApi(TurnApi, TurnMarshaller)
    val goBackApi = new GoBackApi(GoBackApi, GoBackMarshaller)
    val getWeatherApi = new api.GetWeatherApi(GetWeatherApi, GetWeatherMarshaller)


    val host = "localhost"
    val port = 8080
    println(s"\n\n****** API PRESENTATION *******\n")
    Http()
      .newServerAt("localhost", 8081)
      .bind(concat(submitApi.route, turnPageApi.route, goBackApi.route, getWeatherApi.route))
      .onComplete {
        case Success(binding) =>
          system.log.info(
            "Server online at http://{}:{}/",
            binding.localAddress.getHostString,
            binding.localAddress.getPort
          )
        case Failure(ex) =>
          system.log.error(
            "Failed to bind HTTP endpoint, terminating system",
            ex
          )
          system.terminate()
      }
  }

}

