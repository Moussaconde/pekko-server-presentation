package org.openapitools.server.fr.actors

import org.apache.pekko.actor.typed.{ActorRef, Behavior}
import org.apache.pekko.actor.typed.scaladsl.{AbstractBehavior, ActorContext, Behaviors}
import org.apache.pekko.actor.typed.Behavior

import org.apache.pekko.actor.typed.ActorSystem
import scala.concurrent.Future
import org.apache.pekko.http.scaladsl.model.HttpRequest
import org.apache.pekko.http.scaladsl.model.HttpResponse
import org.apache.pekko.http.scaladsl.Http
import org.apache.pekko.http.scaladsl.unmarshalling.Unmarshal
import play.api.libs.json._



object WeatherActor {
  sealed trait Command
    case class GetWeather(city: String, replyTo:ActorRef[String]) extends Command
    

    case class Weather(description: String)
    case class Main(temp: Double)
    case class WeatherResponse(weather: Seq[Weather], main: Main)

    implicit val weatherReads = Json.reads[Weather]
    implicit val mainReads = Json.reads[Main]
    implicit val weatherResponseReads = Json.reads[WeatherResponse]

    
    def apply(): Behavior[Command] =
    Behaviors.setup(context => new WeatherActor(context))
}

class WeatherActor(context: ActorContext[WeatherActor.Command]) extends AbstractBehavior[WeatherActor.Command](context) {
  context.log.info("Hello WeatherActor !")
  import WeatherActor._

override def onMessage(msg: Command): Behavior[Command] =
    msg match {
      case GetWeather(city, replyTo) =>
        context.log.info("GetWeather: " + city)
        getWeatherFrom(city, replyTo)(context.system)
        this
    }

  def getWeatherFrom(city: String, replyTo: ActorRef[String])(implicit system: ActorSystem[_]): Future[Unit] = {
  val apiKey = "cef6e31d67ff7c961d670ac8c514b551"
  val apiUrl = s"http://api.openweathermap.org/data/2.5/weather?q=$city&appid=$apiKey"
  implicit val executionContext = system.executionContext

  val responseFuture: Future[HttpResponse] = Http().singleRequest(HttpRequest(uri = apiUrl))
  responseFuture.flatMap { response =>
    Unmarshal(response.entity).to[String].map { weatherJson =>
      val weatherResponse = Json.parse(weatherJson).as[WeatherResponse]
      val description = weatherResponse.weather.head.description
      val temperatureInKelvin = weatherResponse.main.temp
      val temperatureInCelsius = temperatureInKelvin - 273.15
      replyTo ! s"The weather in Paris is $description with a temperature of $temperatureInCelsius"
  }
}
}}
