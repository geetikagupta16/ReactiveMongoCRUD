import scala.concurrent.ExecutionContext.Implicits.global
import scala.concurrent.Future

import models.Person
import reactivemongo.api.collections.GenericQueryBuilder
import reactivemongo.api.collections.bson.BSONCollection
import reactivemongo.api.commands.{MultiBulkWriteResult, UpdateWriteResult, WriteResult}
import reactivemongo.api.{DB, MongoConnection, MongoDriver}
import reactivemongo.bson.BSONDocument

class CrudService {

  //Connection URI for MongoDb mongodb://host:port/dbName
  val mongoUri = "mongodb://localhost:27017/test"
  val driver = new MongoDriver

  def createConnection(): Future[(DB, MongoConnection)] = {

    for {
      uri <- Future.fromTry(MongoConnection.parseURI(mongoUri))
      con: MongoConnection = driver.connection(uri)
      dn <- Future(uri.db.get)
      db <- con.database(dn)
    } yield {
      (db, con)
    }
  }

  def insertDocument(col: BSONCollection,
      docFirst: BSONDocument,
      docSecond: BSONDocument,
      docThird: BSONDocument): Future[MultiBulkWriteResult] = {
    col.bulkInsert(ordered = false)(docFirst, docSecond, docThird)
  }

  def updateDocument(col: BSONCollection,
      selector: BSONDocument,
      modifier: BSONDocument): Future[UpdateWriteResult] = {
    col.update(selector, modifier)
  }

  def removeDocument(col: BSONCollection, doc: BSONDocument): Future[WriteResult] = {
    col.remove(doc)
  }

  def readDocument(col: BSONCollection,
      doc: BSONDocument): Future[List[Person]] = {
    val readRes: GenericQueryBuilder[col.pack.type] = col.find(doc)
    readRes.cursor[Person]().collect[List]()
  }

  def dropCollection(col: BSONCollection): Future[Unit] = {
    col.drop()
  }

  def closeConnection(connection: MongoConnection) = {
    connection.close()
  }
}
