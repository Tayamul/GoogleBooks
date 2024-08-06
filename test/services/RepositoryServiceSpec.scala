package services

import baseSpec.BaseSpec
import models.{APIError, DataModel}
import org.scalamock.scalatest.MockFactory
import org.scalatest.concurrent.ScalaFutures
import org.scalatestplus.play.guice.GuiceOneServerPerSuite
import play.api.libs.json.{JsValue, Json}
import play.api.test.Helpers.{await, defaultAwaitTimeout}
import repositories.{DataRepository, MockDataRepository}

import scala.concurrent.{ExecutionContext, Future}

class RepositoryServiceSpec extends BaseSpec with MockFactory with ScalaFutures with GuiceOneServerPerSuite {

  val mockDataRepository: MockDataRepository = mock[MockDataRepository]
  implicit val executionContext: ExecutionContext = app.injector.instanceOf[ExecutionContext]
  val testRepoService = new RepositoryService(mockDataRepository)

  val houseOfDragons: DataModel = DataModel("2", "House of Dragons", "The best book!!!", 100)

  "getBooks" should {

    "retrieve all books from the database" in {

//      beforeEach()

      (mockDataRepository.index(_: Option[String]))
        .expects(*)
        .returning(Future(Right(Seq(houseOfDragons))))
        .once()

      whenReady(testRepoService.getBooks(None)) { result =>
        result shouldBe Right(Seq(houseOfDragons))
      }

//      afterEach()

    }

    "returning an error" in {

//      beforeEach()

      (mockDataRepository.index(_: Option[String]))
        .expects(*)
        .returning(Future(Left(APIError.BadAPIResponse(404, "No books found."))))
        .once()

      whenReady(testRepoService.getBooks(None)) { result =>
        result shouldBe Left(APIError.BadAPIResponse(404, "No books found."))
      }

//      afterEach()

    }

  }

//  def beforeEach(): Unit = await(mockDataRepository.deleteAll())
//  def afterEach(): Unit = await(mockDataRepository.deleteAll())


}
