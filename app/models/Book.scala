package models

import play.api.libs.json.{Json, OFormat}

case class Identifier(`type`: String, identifier: String)
case class ImageLinks(smallThumbnail: Option[String], thumbnail: Option[String])

case class Book(
                 title: String,
                 authors: Seq[String],
                 publisher: Option[String],
                 publishedDate: Option[String],
                 description: Option[String],
                 pageCount: Option[Int],
                 industryIdentifiers: Seq[Identifier],
                 imageLinks: Option[ImageLinks],
                 previewLink: Option[String],
                 infoLink: Option[String]
               )


object Book {
  implicit val identifierFormat: OFormat[Identifier] = Json.format[Identifier]
  implicit val imageLinksFormat: OFormat[ImageLinks] = Json.format[ImageLinks]
  implicit val format: OFormat[Book] = Json.format[Book]
}