package controllers

import models.{APIError, DataModel}
//import play.api.libs.json.{JsError, JsSuccess, JsValue, Json}
import play.api.libs.json._
//import play.api.mvc.{Action, AnyContent, BaseController, ControllerComponents, Request}
import play.api.mvc._
import repositories.DataRepository
import services.LibraryService

import javax.inject._
import scala.concurrent.{ExecutionContext, Future}

@Singleton
class ApplicationController @Inject()(val controllerComponents: ControllerComponents, val dataRepository: DataRepository, val service: LibraryService)(implicit val ec: ExecutionContext) extends BaseController {

  def index(name: Option[String] = None): Action[AnyContent] = Action.async { implicit request: Request[AnyContent] =>
    dataRepository.index(name).map {
      case Right(items) => Ok {Json.toJson(items)}
      case Left(error) => Status(error.httpResponseStatus)(Json.obj("error" -> error.reason))
    }.recover {
      case ex: Exception => InternalServerError(Json.obj("error" -> s"An error occurred: ${ex.getMessage}"))
    }
  }

  def create: Action[JsValue] = Action.async(parse.json) { implicit request =>
    request.body.validate[DataModel] match {
      case JsSuccess(dataModel, _) =>
        dataRepository.create(dataModel).map {
          case Right(book) => Created{Json.toJson(book)}
          case Left(error) => Status(error.httpResponseStatus)(Json.obj("error" -> error.reason))
        }.recover {
          case ex: Exception => InternalServerError(Json.obj("error" -> s"An error occurred while saving the book: ${ex.getMessage}"))
        }
      case JsError(_) => Future.successful(BadRequest("Invalid JSON"))
    }
  }

  def read(id: String): Action[AnyContent] = Action.async { implicit request: Request[AnyContent] =>
    dataRepository.read(id).map {
      case Right(book) => Ok {Json.toJson(book)}
      case Left(error) => Status(error.httpResponseStatus)(Json.obj("error" -> error.reason))
    }.recover {
      case ex: Exception => InternalServerError(Json.obj("error" -> s"An error occurred while retrieving the book: ${ex.getMessage}"))
    }
  }

  def update(id: String): Action[JsValue] = Action.async(parse.json) { implicit request =>
    request.body.validate[DataModel] match {
      case JsSuccess(dataModel, _) =>
        dataRepository.update(id, dataModel).flatMap {
          case Right(_) =>
          dataRepository.read(id).map {
            case Right(updatedDataModel) => Accepted {Json.toJson(updatedDataModel)}
            case Left(error) => Status(error.httpResponseStatus)(Json.obj("error" -> error.reason))
          }.recover {
            case _: NoSuchElementException => NotFound(Json.obj("error" -> s"No book found with id: $id"))
            case ex: Exception => InternalServerError(Json.obj("error" -> s"An error occurred while updating the book: ${ex.getMessage}"))
          }
          case Left(error) => Future.successful(Status(error.httpResponseStatus)(Json.obj("error" -> error.reason)))
      }.recover {
          case ex: Exception => InternalServerError(Json.obj("error" -> s"An error occurred while updating the book: ${ex.getMessage}"))
        }
      case JsError(_) => Future.successful(BadRequest(Json.obj("error" -> "Invalid JSON")))
    }
  }

  def updateField(id: String): Action[JsValue] = Action.async(parse.json) { implicit request =>
    val json = request.body

    val fieldNamePath = __ \ "fieldName"
    val newValuePath = __ \ "newValue"

    val fieldNameResult = json.validate[String](fieldNamePath.read[String])
    val newValueResult = json.validate[String](newValuePath.read[String])

    if (fieldNameResult.isSuccess && newValueResult.isSuccess) {
      val fieldName = fieldNameResult.get
      val newValue = newValueResult.get

      dataRepository.updateField(id, fieldName, newValue).map {
        case Right(_) => NoContent
        case Left(error) => Status(error.httpResponseStatus)(error.reason)
      }
    } else {
      Future.successful(BadRequest("Invalid request format. Expected 'fieldName' and 'newValue' fields."))
    }
  }

  def delete(id: String): Action[AnyContent] = Action.async { implicit request: Request[AnyContent] =>
    dataRepository.delete(id).map {
      case Right(_) => NoContent
      case Left(error) => Status(error.httpResponseStatus)(Json.obj("error" -> error.reason))
    }.recover {
      case ex: Exception =>InternalServerError(Json.obj("error" -> s"An error occurred while deleting the book: ${ex.getMessage}"))
    }
  }

  def getGoogleBook(search: String, term: String): Action[AnyContent] = Action.async { implicit request =>
    service.getGoogleBook(search = search, term = term).value.map {
      case Right(book) => Ok{Json.toJson(book)}
      case Left(error) => InternalServerError(Json.toJson(s"Error: $error"))
    }
  }
}
