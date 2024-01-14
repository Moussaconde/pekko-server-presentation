package org.openapitools.server.api

import org.apache.pekko.http.scaladsl.server.Directives._
import org.apache.pekko.http.scaladsl.server.Route
import org.apache.pekko.http.scaladsl.model.StatusCodes
import org.apache.pekko.http.scaladsl.marshalling.ToEntityMarshaller
import org.apache.pekko.http.scaladsl.unmarshalling.FromEntityUnmarshaller
import org.apache.pekko.http.scaladsl.unmarshalling.FromStringUnmarshaller
import org.openapitools.server.AkkaHttpHelper._
import org.openapitools.server.model.TurnPagePostRequest


class TurnPageApi(
    turnPageService: TurnPageApiService,
    turnPageMarshaller: TurnPageApiMarshaller
) {

  
  import turnPageMarshaller._

  lazy val route: Route =
    path("turnPage") { 
      post {  
            entity(as[TurnPagePostRequest]){ turnPagePostRequest =>
              turnPageService.turnPagePost(turnPagePostRequest = turnPagePostRequest)
            }
      }
    }
}


trait TurnPageApiService {

  def turnPagePost200: Route =
    complete((200, "Page tournée avec succès"))
  /**
   * Code: 200, Message: Page tournée avec succès
   */
  def turnPagePost(turnPagePostRequest: TurnPagePostRequest): Route

}

trait TurnPageApiMarshaller {
  implicit def fromEntityUnmarshallerTurnPagePostRequest: FromEntityUnmarshaller[TurnPagePostRequest]

}

