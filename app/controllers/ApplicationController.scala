package controllers

import models.DataModel
import play.api.libs.json.Json
import play.api.mvc.{Action, AnyContent, BaseController, ControllerComponents, Request}
import repositories.DataRepository

import javax.inject._
import scala.concurrent.{ExecutionContext, Future}

@Singleton
class ApplicationController @Inject()(val controllerComponents: ControllerComponents, val dataRepository: DataRepository, implicit val ec: ExecutionContext) extends BaseController {

  def index(): Action[AnyContent] = Action.async { implicit request: Request[AnyContent] =>
    dataRepository.index().map {
      case Right(item: Seq[DataModel]) => Ok {Json.toJson(item)}
      case Left(error) => Status(error)(Json.toJson("Unable to find any books"))
    }
  }

  def create = TODO

  def read(id: String): Action[AnyContent] = Action.async { implicit request: Request[AnyContent] =>
    dataRepository.read(id).map {
      case item => Ok { Json.toJson(item) }
      case _ => NotFound (Json.toJson("Data not found"))
    }
  }

  def update(id: String) = TODO

  def delete(id: String) = TODO
}
