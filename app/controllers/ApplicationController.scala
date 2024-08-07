package controllers

import models.DataModel.dataForm
import models.{APIError, DataModel}
import services.RepositoryService
import play.api.libs.json._
import play.api.mvc._
import play.filters.csrf.CSRF
import repositories.DataRepository
import services.LibraryService

import javax.inject._
import scala.concurrent.impl.Promise
import scala.concurrent.{ExecutionContext, Future}

@Singleton
class ApplicationController @Inject()(val controllerComponents: ControllerComponents, val dataRepository: DataRepository, val service: LibraryService, val repoService: RepositoryService)(implicit val ec: ExecutionContext) extends BaseController with play.api.i18n.I18nSupport {

  def index(name: Option[String] = None): Action[AnyContent] = Action.async { implicit request: Request[AnyContent] =>
    repoService.getBooks(name).map {
      case Right(items) => Ok {Json.toJson(items)}
      case Left(error) => Status(error.httpResponseStatus)(Json.obj("error" -> error.reason))
    }.recover {
      case ex: Exception => InternalServerError(Json.obj("error" -> s"An error occurred: ${ex.getMessage}"))
    }
  }

  def create: Action[JsValue] = Action.async(parse.json) { implicit request =>
    request.body.validate[DataModel] match {
      case JsSuccess(dataModel, _) =>
        repoService.createBook(dataModel).map {
          case Right(book) => Created{Json.toJson(book)}
          case Left(error) => Status(error.httpResponseStatus)(Json.obj("error" -> error.reason))
        }.recover {
          case ex: Exception => InternalServerError(Json.obj("error" -> s"An error occurred while saving the book: ${ex.getMessage}"))
        }
      case JsError(_) => Future.successful(BadRequest("Invalid JSON"))
    }
  }

  def read(id: String): Action[AnyContent] = Action.async { implicit request: Request[AnyContent] =>
    repoService.getBookById(id).map {
      case Right(book) => Ok {Json.toJson(book)}
      case Left(error) => Status(error.httpResponseStatus)(Json.obj("error" -> error.reason))
    }.recover {
      case ex: Exception => InternalServerError(Json.obj("error" -> s"An error occurred while retrieving the book: ${ex.getMessage}"))
    }
  }

  def update(id: String): Action[JsValue] = Action.async(parse.json) { implicit request =>
    request.body.validate[DataModel] match {
      case JsSuccess(dataModel, _) =>
        repoService.updateBookById(id, dataModel).flatMap {
          case Right(_) =>
          repoService.getBookById(id).map {
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

      repoService.updateBookByFieldName(id, fieldName, newValue).map {
        case Right(_) => NoContent
        case Left(error) => Status(error.httpResponseStatus)(error.reason)
      }
    } else {
      Future.successful(BadRequest("Invalid request format. Expected 'fieldName' and 'newValue' fields."))
    }
  }

  def delete(id: String): Action[AnyContent] = Action.async { implicit request: Request[AnyContent] =>
    repoService.deleteBookById(id).map {
      case Right(_) => NoContent
      case Left(error) => Status(error.httpResponseStatus)(Json.obj("error" -> error.reason))
    }.recover {
      case ex: Exception =>InternalServerError(Json.obj("error" -> s"An error occurred while deleting the book: ${ex.getMessage}"))
    }
  }

//  def getGoogleBook(search: String, term: String): Action[AnyContent] = Action.async { implicit request =>
//    service.getGoogleBook(search = search, term = term).value.map {
//      case Right(book) => Ok{Json.toJson(book)}
//      case Left(error) => InternalServerError(Json.toJson(s"Error: $error"))
//    }
//  }

  def getGoogleBookByIsbn(isbn: String): Action[AnyContent] = Action.async { implicit request =>
    service.getGoogleBookByIsbn(isbn).value.flatMap {
      case Right(book) =>
        service.storeBookToMongoDb(book).map {
          case Right(storedBook) => Ok(Json.toJson(storedBook))
          case Left(error) => Status(error.httpResponseStatus)(error.reason)
        }
      case Left(error) => Future.successful(InternalServerError(Json.toJson(s"Error: $error")))
    }.recover {
      case ex: Exception => InternalServerError(Json.toJson(s"Error: ${ex.getMessage}"))
    }
  }

  def example(id: String): Action[AnyContent] = Action.async { implicit result =>
    repoService.getBookById(id).map {
      case Right(book) => Ok(views.html.example(book))
      case Left(error) => NotFound("Book not found.")
    }.recover {
      case ex: Exception => InternalServerError(s"Error: ${ex.getMessage}")
    }
  }

  def addBook(): Action[AnyContent] = Action { implicit request =>
    Ok(views.html.addABook(DataModel.dataForm))
  }

  def accessToken(implicit request: Request[_]) = {
    CSRF.getToken
  }

  def addBookForm(): Action[AnyContent] = Action.async { implicit request =>
    accessToken
    dataForm.bindFromRequest().fold(
      formWithErrors => {
        Future.successful(BadRequest(views.html.addABook(formWithErrors)))
      },
      formData => {
        dataRepository.create(formData).map {
          case Right(_) => {
            Thread sleep(4000)
            Redirect(routes.ApplicationController.example(formData._id)).flashing("success" -> "Book added successfully")
          }
          case Left(error) => InternalServerError(Json.toJson(s"Error: $error"))
        }
      }
    )
  }

}
