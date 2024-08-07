package models

import play.api.libs.json.{Json, OFormat}
import play.api.data._
import play.api.data.Forms._


case class DataModel(
                      _id: String,
                     name: String,
                     description: Option[String],
                     pageCount: Option[Int]
                    )

object DataModel {
  // this allows for easily transforming the model to and from JSON
  implicit val formats: OFormat[DataModel] = Json.format[DataModel]

  val dataForm: Form[DataModel] = Form(
    mapping(
      "_id" -> text,
      "name" -> text,
      "description" -> optional(text),
      "pageCount" -> optional(number)
    )(DataModel.apply)(DataModel.unapply)
  )

}