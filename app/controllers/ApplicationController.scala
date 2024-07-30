package controllers

import models.DataModel
import play.api.libs.json.{JsError, JsSuccess, JsValue, Json}
import play.api.mvc.{Action, AnyContent, BaseController, ControllerComponents, Request}
import repositories.DataRepository

import javax.inject._
import scala.concurrent.{ExecutionContext, Future}

@Singleton
class ApplicationController @Inject()(val controllerComponents: ControllerComponents, val dataRepository: DataRepository, implicit val ec: ExecutionContext) extends BaseController {

  def index(): Action[AnyContent] = Action.async { implicit request: Request[AnyContent] =>
    dataRepository.index().map {
      case Right(item: Seq[DataModel]) => Ok {Json.toJson(item)}
      case Left(error) => Status(error)(Json.toJson("Unable to find any books."))
    }
  }

  def create: Action[JsValue] = Action.async(parse.json) { implicit request =>
    request.body.validate[DataModel] match {
      case JsSuccess(dataModel, _) =>
        dataRepository.create(dataModel).map {createdModel =>
          if (createdModel == dataModel)
            Created(Json.toJson(createdModel))
          else
            InternalServerError("Failed to store the book information.")
        }.recover {
          case ex: Exception => InternalServerError(s"An error occurred while saving the book: ${ex.getMessage}")
        }
      case JsError(_) => Future(BadRequest)
    }
  }

  def read(id: String): Action[AnyContent] = Action.async { implicit request: Request[AnyContent] =>
    dataRepository.read(id).map {
      case item => Ok { Json.toJson(item) }
      case _ => NotFound (Json.toJson("Data not found"))
    }
  }

  def update(id: String) = TODO

  def delete(id: String): Action[AnyContent] = Action.async { implicit request: Request[AnyContent] =>
    dataRepository.delete(id).map {
      case item => Accepted
      case _ => NotFound (Json.toJson("Could not find the book in the database"))
    }
  }
}
