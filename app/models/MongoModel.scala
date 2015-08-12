package models

import play.api.Play.current
import play.api.PlayException

import se.radley.plugin.salat._

import com.novus.salat._
import com.novus.salat.dao._
import com.novus.salat.Context

import com.mongodb.casbah.commons.Imports._
import mongoContext._

// Salat model includes ObjectId for storing in Mongo
case class MongoModel(_id: ObjectId = new ObjectId, recordSchema: AvroRecordSchema)

// Salat model companion DAO
object MongoModel extends ModelCompanion[MongoModel, ObjectId] {
  val collection = mongoCollection("public")
  val collPrivate = mongoCollection("private")
  val dao = new SalatDAO[MongoModel, ObjectId](collection = collection) {}
  val daoPrivate = new SalatDAO[MongoModel, ObjectId](collection = collPrivate) {}

  def all: List[MongoModel] = dao.find(MongoDBObject.empty).toList

  def create(recordSchema: AvroRecordSchema): Option[ObjectId] = {
    val publicDatum = recordSchema.privacy match {
      case true => Obfuscator.obfuscate(recordSchema)
      case false => recordSchema
    }
    dao.insert(MongoModel(recordSchema = publicDatum))
    daoPrivate.insert(MongoModel(recordSchema = recordSchema))
  }

  def delete(id: String) {
  	dao.remove(MongoDBObject("_id" -> new ObjectId(id)))
    daoPrivate.remove(MongoDBObject("_id" -> new ObjectId(id)))
  }

}

