package models

import play.api.http.Status

sealed abstract class APIError (
                               val httpResponseStatus: Int,
                               val reason: String
                               )

object APIError {

  final case class BadAPIResponse(upstreamStatus: Int, upstreamMessage: String)
  extends APIError(
    Status.INTERNAL_SERVER_ERROR,
    s"Received an error from upstream: Status = $upstreamStatus, Reason = $upstreamMessage"
  )

}