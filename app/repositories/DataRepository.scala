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

  def read(id: String): Future[DataModel] =
    collection.find(byID(id)).headOption flatMap {
      case Some(data) =>
        Future(data)
    }

  def update(id: String, book: DataModel): Future[result.UpdateResult] =
    collection.replaceOne(
      filter = byID(id),
      replacement = book,
      options = new ReplaceOptions().upsert(true) //What happens when we set this to false?
    ).toFuture()

  def delete(id: String): Future[result.DeleteResult] =
    collection.deleteOne(
      filter = byID(id)
    ).toFuture()

  def deleteAll(): Future[Unit] = collection.deleteMany(empty()).toFuture().map(_ => ()) //Hint: needed for tests

}