package controllers

import baseSpec.BaseSpecWithApplication
import models.DataModel
import play.api.test.FakeRequest
import play.api.http.Status
import play.api.libs.json.{JsValue, Json}
import play.api.mvc.Result
import play.api.test.Helpers._
import repositories.DataRepository

import scala.concurrent.{ExecutionContext, Future}

class ApplicationControllerSpec extends BaseSpecWithApplication {

  val TestApplicationController = new ApplicationController(
    component,
    repository,
    executionContext
  )

  private val dataModel: DataModel = DataModel(
    "abcd",
    "test name",
    "test description",
    100
  )

  "ApplicationController .index()" should {

    val result = TestApplicationController.index()(FakeRequest())

    "return TODO" in {
      status(result) shouldBe Status.OK
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

  }

  "ApplicationController .update()" should {

  }

  "ApplicationController .delete()" should {

  }

  "test name" should {
    "do something" in {
      beforeEach
      afterEach
    }
  }
  override def beforeEach(): Unit = await(repository.deleteAll())
  override def afterEach(): Unit = await(repository.deleteAll())
}
