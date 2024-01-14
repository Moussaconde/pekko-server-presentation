package org.openapitools.server.api

import org.apache.pekko.http.scaladsl.server.Directives._
import org.apache.pekko.http.scaladsl.server.Route
import org.apache.pekko.http.scaladsl.model.StatusCodes
import org.apache.pekko.http.scaladsl.marshalling.ToEntityMarshaller
import org.apache.pekko.http.scaladsl.unmarshalling.FromEntityUnmarshaller
import org.apache.pekko.http.scaladsl.unmarshalling.FromStringUnmarshaller
import org.openapitools.server.AkkaHttpHelper._
import org.openapitools.server.model.GoBackPostRequest


class GoBackApi(
    goBackService: GoBackApiService,
    goBackMarshaller: GoBackApiMarshaller
) {

  
  import goBackMarshaller._

  lazy val route: Route =
    path("goBack") { 
      post {  
            entity(as[GoBackPostRequest]){ goBackPostRequest =>
              goBackService.goBackPost(goBackPostRequest = goBackPostRequest)
            }
      }
    }
}


trait GoBackApiService {

  def goBackPost200: Route =
    complete((200, "Retour effectué avec succès"))
  /**
   * Code: 200, Message: Retour effectué avec succès
   */
  def goBackPost(goBackPostRequest: GoBackPostRequest): Route

}

trait GoBackApiMarshaller {
  implicit def fromEntityUnmarshallerGoBackPostRequest: FromEntityUnmarshaller[GoBackPostRequest]



}

