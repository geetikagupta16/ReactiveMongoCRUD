package models

import reactivemongo.bson.{BSONDocument, BSONDocumentReader, BSONObjectID}

case class Person(id: BSONObjectID, name: String, designation: String)

object Person {
  implicit object PersonReader extends BSONDocumentReader[Person] {
    def read(doc: BSONDocument): Person = {
      val id: BSONObjectID = doc.getAs[BSONObjectID]("_id").get
      val name = doc.getAs[String]("name").get
      val designation = doc.getAs[String]("designation").get

      Person(id, name, designation)
    }
  }
}
