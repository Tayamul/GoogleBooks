package services

import models.{APIError, DataModel}
import org.mongodb.scala.result
import repositories.DataRepository

import javax.inject.Inject
import scala.concurrent.Future

class RepositoryService @Inject()(val dataRepository: DataRepository) {

  def getBooks(name: Option[String]): Future[Either[APIError.BadAPIResponse, Seq[DataModel]]] = {
    dataRepository.index(name)
  }

  def createBook(book: DataModel): Future[Either[APIError, DataModel]] = {
    dataRepository.create(book)
  }

  def getBookById(id: String): Future[Either[APIError, DataModel]] = {
    dataRepository.read(id)
  }

  def updateBookById(id: String, book: DataModel): Future[Either[APIError, result.UpdateResult]] = {
    dataRepository.update(id, book)
  }

  def updateBookByFieldName(id: String, fieldName: String, newValue: String): Future[Either[APIError, result.UpdateResult]] = {
    dataRepository.updateField(id, fieldName, newValue)
  }

  def deleteBookById(id: String): Future[Either[APIError, result.DeleteResult]] = {
    dataRepository.delete(id)
  }

}
