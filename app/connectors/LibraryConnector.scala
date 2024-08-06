package connectors

import cats.data.EitherT
import models.{APIError, Book, DataModel}
import play.api.libs.json.{JsError, JsObject, JsSuccess, OFormat}
import play.api.libs.ws.WSClient
import play.libs.ws.WSResponse

import javax.inject.Inject
import scala.concurrent.{ExecutionContext, Future}

class LibraryConnector @Inject()(ws: WSClient){

//  def get[Book](url: String)(implicit rds: OFormat[Book], ec: ExecutionContext): EitherT[Future, APIError, Book] = {
//    val request = ws.url(url)
//    val response = request.get()
//    EitherT {
//      response
//        .map {
//          result =>
//            if (result.status == 200) {
//              println("---------------result-----------", result)
//              val json = result.json
//              println("---------------json-----------", json)
//
//              json.validate[Book] match {
//                case JsSuccess(parsedResponse, _) => Right(parsedResponse)
//                case JsError(errors) => Left(APIError.BadAPIResponse(500, s"Error parsing JSON response. Message: ${errors}"))
//              }
//            } else {
//              Left(APIError.BadAPIResponse(result.status, result.statusText))
//            }
//        }.recover { case _: WSResponse =>
//          Left(APIError.BadAPIResponse(500, "Could not connect"))
//        }
//    }
//  }

  def getBookByIsbn(isbn: String)(implicit ec: ExecutionContext): EitherT[Future, APIError, DataModel] = {
    val url = s"https://www.googleapis.com/books/v1/volumes?q=isbn:$isbn"
    val request = ws.url(url)

    EitherT {
      request.get().map { result =>
        if (result.status == 200) {
          val json = result.json.as[JsObject]
          val items = (json \ "items").asOpt[Seq[JsObject]].getOrElse(Seq.empty)

          if (items.nonEmpty) {
            val item = items.head
            val id = (item \ "id").as[String]
            val volumeInfoJson = (item \ "volumeInfo").as[JsObject]

            volumeInfoJson.validate[Book] match {
              case JsSuccess(book, _) =>
                val dataModel: DataModel = DataModel(
                  _id = id,
                  name = book.title,
                  description = book.description,
                  pageCount = book.pageCount
                )
                Right(dataModel)
              case JsError(errors) =>
                Left(APIError.BadAPIResponse(500, s"Could not parse book data. Message: ${JsError.toJson(errors).toString()}"))
            }
          } else {
            Left(APIError.BadAPIResponse(404, "Book not found."))
          }
        } else {
          Left(APIError.BadAPIResponse(result.status, result.statusText))
        }
      }.recover {
        case ex: Exception =>
          Left(APIError.BadAPIResponse(500, s"Could not connect. Error: ${ex.getMessage}"))
      }
    }
  }

}
