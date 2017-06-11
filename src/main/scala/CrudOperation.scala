import scala.concurrent.ExecutionContext.Implicits.global
import scala.util.{Failure, Success}

import reactivemongo.api.MongoConnection
import reactivemongo.api.collections.bson.BSONCollection
import reactivemongo.bson.BSONDocument


object CrudOperation extends App {

  val crudService = new CrudService
  val connectDb = crudService.createConnection()
  connectDb.onComplete {
    case Failure(e) => e.printStackTrace()
    case Success(res) => println("Connection established successfully !!")
      val (db, connection) = res
      val bSONCollection = new BSONCollection(db,
        "testCollection",
        db.failoverStrategy,
        db.defaultReadPreference)
      insert(crudService, bSONCollection)
      val selectQuery = BSONDocument("designation" -> "Tester")
      Thread.sleep(100)
      print(crudService, bSONCollection, selectQuery)
      val selector = BSONDocument("name" -> "Jake")

      val modifier = BSONDocument(
        "$set" -> BSONDocument(
          "designation" -> "Software Engineer"))
      update(crudService, bSONCollection, selector, modifier)
      Thread.sleep(100)

      print(crudService, bSONCollection, selector)

      Thread.sleep(100)

      remove(crudService, bSONCollection, selector)
      val select = BSONDocument("designation" -> "Software Engineer")
      Thread.sleep(100)

      print(crudService, bSONCollection, select)
      Thread.sleep(100)
      dropCollection(connection, bSONCollection)
  }

  def insert(crudService: CrudService, bSONCollection: BSONCollection) = {
    val insertQueryFirst = BSONDocument("name" -> "Alex",
      "designation" -> "Software Engineer")
    val insertQuerySecond = BSONDocument("name" -> "Jake",
      "designation" -> "Tester")
    val insertQueryThird = BSONDocument("name" -> "Rachel",
      "designation" -> "Tester")

    //To insert a document
    crudService
      .insertDocument(bSONCollection, insertQueryFirst, insertQuerySecond, insertQueryThird)
      .onComplete {
        case Failure(e) => e.printStackTrace()
        case Success(writeResult) =>
          println(s"successfully inserted document with result: $writeResult")
      }
  }

  def print(crudService: CrudService, bSONCollection: BSONCollection, selectQuery: BSONDocument) = {
    crudService.readDocument(bSONCollection, selectQuery).onComplete {
      case Failure(e) => e.printStackTrace()
      case Success(persons) =>
        println("\n\nPerson Details :")
        persons
          .map(person => println(
            "Id: " + person.id + "\tName: " + person.name + "\tDesignation: " + person.designation))
    }
  }

  def update(crudService: CrudService,
      bSONCollection: BSONCollection,
      selector: BSONDocument,
      modifier: BSONDocument) = {

    crudService.updateDocument(bSONCollection, selector, modifier).onComplete {
      case Failure(e) => e.printStackTrace()
      case Success(updateRes) =>
        println(s"successfully updated document with result: $updateRes")
    }
  }

  def dropCollection(connection: MongoConnection, bSONCollection: BSONCollection) = {
    crudService.dropCollection(bSONCollection).onComplete {
      case Failure(e) => e.printStackTrace()
      case Success(res) => println("Collection dropped successfully !!")
    }
    crudService.closeConnection(connection)
    println("Connection closed !!")
  }

  def remove(crudService: CrudService,
      bSONCollection: BSONCollection,
      bSONDocument: BSONDocument) = {
    crudService.removeDocument(bSONCollection, bSONDocument).onComplete {
      case Failure(e) => e.printStackTrace()
      case Success(updateRes) =>
        println(s"successfully removed document with result: $updateRes")
    }
  }
}

