package org.openapitools.server.api

import org.apache.pekko.http.scaladsl.server.Directives._
import org.apache.pekko.http.scaladsl.server.Route
import org.apache.pekko.http.scaladsl.model.StatusCodes
import org.apache.pekko.http.scaladsl.marshalling.ToEntityMarshaller
import org.apache.pekko.http.scaladsl.unmarshalling.FromEntityUnmarshaller
import org.apache.pekko.http.scaladsl.unmarshalling.FromStringUnmarshaller
import org.openapitools.server.AkkaHttpHelper._
import org.openapitools.server.model.GetWeatherGet200Response


class GetWeatherApi(
    getWeatherService: GetWeatherApiService,
    getWeatherMarshaller: GetWeatherApiMarshaller
) {

  
  import getWeatherMarshaller._

  lazy val route: Route =
    path("getWeather") { 
      get {  
            getWeatherService.getWeatherGet()
      }
    }
}


trait GetWeatherApiService {

  def getWeatherGet200(responseGetWeatherGet200Response: GetWeatherGet200Response)(implicit toEntityMarshallerGetWeatherGet200Response: ToEntityMarshaller[GetWeatherGet200Response]): Route =
    complete((200, responseGetWeatherGet200Response))
  /**
   * Code: 200, Message: Données météorologiques récupérées avec succès, DataType: GetWeatherGet200Response
   */
  def getWeatherGet()
      (implicit toEntityMarshallerGetWeatherGet200Response: ToEntityMarshaller[GetWeatherGet200Response]): Route

}

trait GetWeatherApiMarshaller {


  implicit def toEntityMarshallerGetWeatherGet200Response: ToEntityMarshaller[GetWeatherGet200Response]

}

