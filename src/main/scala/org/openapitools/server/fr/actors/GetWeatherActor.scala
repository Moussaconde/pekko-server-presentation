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

object WeatherActor {
  sealed trait Command
    case class GetWeather(city: String, replyTo:ActorRef[String]) extends Command
  def apply(): Behavior[Command] =
    Behaviors.setup(context => new WeatherActor(context))
}

class WeatherActor(context: ActorContext[WeatherActor.Command]) extends AbstractBehavior[WeatherActor.Command](context) {
  context.log.info("Hello WeatherActor !")
  import WeatherActor._

override def onMessage(msg: Command): Behavior[Command] =
    msg match {
      case getWeather: GetWeather =>
        context.log.info("GetWeather: " + getWeather)
        getWeatherFrom(getWeather.city)(context.system)
        this
    }

 def getWeatherFrom(city: String)(implicit system: ActorSystem[_]): Future[String] = {
  val apiKey = "cef6e31d67ff7c961d670ac8c514b551"
  val apiUrl = s"http://api.openweathermap.org/data/2.5/weather?q=$city&appid=$apiKey"
implicit val executionContext = system.executionContext

  val responseFuture: Future[HttpResponse] = Http().singleRequest(HttpRequest(uri = apiUrl))
  responseFuture.flatMap { response =>
    Unmarshal(response.entity).to[String]
  }
}}