package org.openapitools.server.model


/**
 * @param temperature  for example: ''null''
 * @param description  for example: ''null''
*/
final case class GetWeatherGet200Response (
  temperature: Option[Double] = None,
  description: Option[String] = None
)

