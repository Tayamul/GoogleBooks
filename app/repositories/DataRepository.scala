package repositories

import models.{APIError, DataModel}
import org.mongodb.scala.bson.conversions.Bson
import org.mongodb.scala.model.Filters.empty
import org.mongodb.scala.model._
import org.mongodb.scala.{MongoException, result}
import play.api.mvc.Results.Status
import uk.gov.hmrc.mongo.MongoComponent
import uk.gov.hmrc.mongo.play.json.PlayMongoRepository

import javax.inject.{Inject, Singleton}
import scala.concurrent.{ExecutionContext, Future}

@Singleton
class DataRepository @Inject()(
                                mongoComponent: MongoComponent
                              )(implicit ec: ExecutionContext) extends PlayMongoRepository[DataModel](
  collectionName = "dataModels",
  mongoComponent = mongoComponent,
  domainFormat = DataModel.formats,
  indexes = Seq(IndexModel(
    Indexes.ascending("_id")
  )),
  replaceIndexes = false
) {

  def index(): Future[Either[APIError.BadAPIResponse, Seq[DataModel]]]  =
    collection.find().toFuture().map{
      case books: Seq[DataModel] => Right(books)
      case _ => Left(APIError.BadAPIResponse(404, "Books cannot be found"))
    }

  def create(book: DataModel): Future[Either[APIError, DataModel]] =
    collection
      .insertOne(book)
      .toFuture()
      .map(_ => Right(book))
      .recover {
        case _: MongoException => Left(APIError.BadAPIResponse(500, "Could not connect to the database."))
        case _: IllegalArgumentException => Left(APIError.BadAPIResponse(400, "Bad request."))
        case _ => Left(APIError.BadAPIResponse(500, "An unknown error occurred."))
      }

  private def byID(id: String): Bson =
    Filters.and(
      Filters.equal("_id", id)
    )

  def read(id: String): Future[Either[APIError, DataModel]] =
    collection.find(byID(id)).headOption flatMap {
      case Some(data) => Right(data)
      case None => Left(APIError.BadAPIResponse(404, s"No book found with id: $id"))
    }.recover {
      case _: MongoException => Left(APIError.BadAPIResponse(500, "Could not connect to the database."))
      case _ => Left(APIError.BadAPIResponse(500, "An unknown error occurred."))
    }

  def update(id: String, book: DataModel): Future[Either[APIError, result.UpdateResult]] =
    collection.replaceOne(
      filter = byID(id),
      replacement = book,
      options = new ReplaceOptions().upsert(true) // 'true' means to insert the document if it doesn't exist
    ).toFuture().map { updateResult =>
      if (updateResult.getMatchedCount == 0) Left(APIError.BadAPIResponse(404, s"No book found with id: $id"))
      else Right(updateResult)
    }.recover {
      case _: MongoException => Left(APIError.BadAPIResponse(500, "Could not connect to the database."))
      case _: IllegalArgumentException => Left(APIError.BadAPIResponse(400, "Bad request."))
      case _ => Left(APIError.BadAPIResponse(500, "An unknown error occurred."))
    }

  def delete(id: String): Future[Either[APIError, result.DeleteResult]] =
    collection.deleteOne(
      filter = byID(id)
    ).toFuture().map { deleteResult =>
      if (deleteResult.getDeletedCount == 0) Left(APIError.BadAPIResponse(404, s"No book found with id: $id"))
      else Right((deleteResult))
    }.recover {
      case _: MongoException => Left(APIError.BadAPIResponse(500, "Could not connect to the database."))
      case _ => Left(APIError.BadAPIResponse(500, "An unknown error occurred."))
    }

  def deleteAll(): Future[Either[APIError, Unit]] = collection.deleteMany(empty()).toFuture().map {deleteResult =>
    if (deleteResult.getDeletedCount == 0) Left(APIError.BadAPIResponse(404, "No books found to delete."))
    else Right(())
  }.recover {
    case _: MongoException => Left(APIError.BadAPIResponse(500, "Could not connect to the database."))
    case _: IllegalArgumentException => Left(APIError.BadAPIResponse(400, "Bad request."))
    case _ => Left(APIError.BadAPIResponse(500, "An unknown error occurred."))} //Hint: needed for tests

}