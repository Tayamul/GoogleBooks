package services

import cats.data.EitherT
import connectors.LibraryConnector
import models.{APIError, Book, DataModel}
import repositories.DataRepository

import javax.inject._
import scala.concurrent.{ExecutionContext, Future}

@Singleton
class LibraryService @Inject()(connector: LibraryConnector, dataRepository: DataRepository) {

//  def getGoogleBook(urlOverride: Option[String] = None, search: String, term: String)(implicit ec: ExecutionContext): EitherT[Future, APIError, Book] =
//    connector.get[Book](urlOverride.getOrElse(s"https://www.googleapis.com/books/v1/volumes?q=$search%$term"))

  def getGoogleBookByIsbn(isbn: String)(implicit ec: ExecutionContext): EitherT[Future, APIError, DataModel] = {
    connector.getBookByIsbn(isbn)
  }

  /**
    @param definiton convert Book data to a valid DataModel type
    @param method def convertDataType(book: Book): DataModel ={
    DataModel(
      _id = book.title,
      name = book.title,
      description = book.description,
      pageCount = book.pageCount
    )
  }
  */

  def storeBookToMongoDb(book: DataModel): Future[Either[APIError, DataModel]] = {
    dataRepository.create(book)
  }

}
