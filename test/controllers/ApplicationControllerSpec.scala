package controllers

import baseSpec.BaseSpecWithApplication
import models.DataModel
import play.api.test.FakeRequest
import play.api.http.Status
import play.api.libs.json.{JsValue, Json}
import play.api.mvc.{ControllerComponents, Result}
import play.api.test.Helpers._
import repositories.DataRepository

import javax.inject.Inject
import scala.concurrent.{ExecutionContext, Future}

class ApplicationControllerSpec extends BaseSpecWithApplication {

  val TestApplicationController = new ApplicationController(component, repository, service, repoService)(executionContext)

  private val dataModel: DataModel = DataModel(
    "abcd",
    "test name",
    "test description",
    100
  )

  private val updateDataModel: DataModel = DataModel(
    "abcd",
    "updated test name",
    "updated test description",
    150
  )

  "ApplicationController .index()" should {

    "retrieve a collection of all books in the database" in {

      beforeEach()


      val request: FakeRequest[JsValue] = buildPost("/api").withBody[JsValue](Json.toJson(dataModel))
      val createdResult: Future[Result] = TestApplicationController.create()(request)

      status(createdResult) shouldBe Status.CREATED

      val result = TestApplicationController.index()(FakeRequest())
      status(result) shouldBe Status.OK

      afterEach()

    }


  }

  "ApplicationController .create()" should {

    "create a book in the database" in {

      beforeEach()

      val request: FakeRequest[JsValue] = buildPost("/api").withBody[JsValue](Json.toJson(dataModel))
      val createdResult: Future[Result] = TestApplicationController.create()(request)

      status(createdResult) shouldBe Status.CREATED

      afterEach()

    }
  }

  "ApplicationController .read()" should {

    "find a book in the database by id" in {

      beforeEach()

      val request: FakeRequest[JsValue] = buildPost("/api").withBody[JsValue](Json.toJson(dataModel))
      val createdResult: Future[Result] = TestApplicationController.create()(request)

      status(createdResult) shouldBe Status.CREATED

      val readResult: Future[Result] = TestApplicationController.read(dataModel._id)(FakeRequest())

      status(readResult) shouldBe Status.OK
      contentAsJson(readResult).as[JsValue] shouldBe Json.toJson(dataModel)

      afterEach()

    }
  }

  "ApplicationController .update()" should {

    "update a book's field in the database by its id" in {


    beforeEach()

    val request: FakeRequest[JsValue] = buildPost("/api").withBody[JsValue](Json.toJson(dataModel))
      val createdResult: Future[Result] = TestApplicationController.create()(request)

      status(createdResult) shouldBe Status.CREATED

      val updateRequest: FakeRequest[JsValue] = buildPut("/api/${dataModel._id}").withBody[JsValue](Json.toJson(updateDataModel))
      val updateResult: Future[Result] = TestApplicationController.update(dataModel._id)(updateRequest)

      status(updateResult) shouldBe Status.ACCEPTED

    afterEach()

    }
  }

  "ApplicationController .delete()" should {

    "delete a book in the database by its id" in {

    beforeEach()

    val request: FakeRequest[JsValue] = buildPost("/api").withBody[JsValue](Json.toJson(dataModel))
    val createdRequest: Future[Result] = TestApplicationController.create()(request)

    status(createdRequest) shouldBe Status.CREATED

    val deleteRequest: Future[Result] = TestApplicationController.delete(dataModel._id)(FakeRequest())

    status(deleteRequest) shouldBe Status.NO_CONTENT

    afterEach()

    }
  }

  override def beforeEach(): Unit = await(repository.deleteAll())
  override def afterEach(): Unit = await(repository.deleteAll())
}
