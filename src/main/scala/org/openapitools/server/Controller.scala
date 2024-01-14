package org.openapitools.server

import org.apache.pekko.http.scaladsl.Http
import org.apache.pekko.http.scaladsl.server.Route
import org.openapitools.server.api.GetWeatherApi
import org.openapitools.server.api.GoBackApi
import org.openapitools.server.api.SubmitDocumentApi
import org.openapitools.server.api.TurnPageApi

import org.apache.pekko.http.scaladsl.server.Directives._
import org.apache.pekko.actor.ActorSystem
import org.apache.pekko.stream.ActorMaterializer

class Controller(getWeather: GetWeatherApi, goBack: GoBackApi, submitDocument: SubmitDocumentApi, turnPage: TurnPageApi)(implicit system: ActorSystem, materializer: ActorMaterializer) {

    lazy val routes: Route = getWeather.route ~ goBack.route ~ submitDocument.route ~ turnPage.route 

    Http().bindAndHandle(routes, "0.0.0.0", 9000)
}