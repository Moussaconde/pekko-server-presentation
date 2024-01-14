package org.openapitools.server.api

import org.apache.pekko.http.scaladsl.server.Directives._
import org.apache.pekko.http.scaladsl.server.Route
import org.apache.pekko.http.scaladsl.model.StatusCodes
import org.apache.pekko.http.scaladsl.marshalling.ToEntityMarshaller
import org.apache.pekko.http.scaladsl.unmarshalling.FromEntityUnmarshaller
import org.apache.pekko.http.scaladsl.unmarshalling.FromStringUnmarshaller
import org.openapitools.server.AkkaHttpHelper._
import org.openapitools.server.model.Page

class SubmitDocumentApi(
    submitDocumentService: SubmitDocumentApiService,
    submitDocumentMarshaller: SubmitDocumentApiMarshaller
) {

  
  import submitDocumentMarshaller._

  lazy val route: Route =
    path("submitDocument") { 
      post {  
            entity(as[Seq[Page]]){ requestBody =>
              submitDocumentService.submitDocumentPost(requestBody = requestBody)
            }
      }
    }
}


trait SubmitDocumentApiService {

  def submitDocumentPost200: Route =
    complete((200, "Retour effectué avec succès"))
  /**
   * Code: 200, Message: Retour effectué avec succès
   */
  def submitDocumentPost(requestBody: Seq[Page]): Route

}

trait SubmitDocumentApiMarshaller {
  implicit def fromEntityUnmarshallerStringList: FromEntityUnmarshaller[Seq[Page]]

}

