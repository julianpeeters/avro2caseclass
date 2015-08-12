package models

// Play form model/schemas avro datafile model
case class AvroRecordSchema(
  schema: Option[String], 
  format: Option[String], 
  privacy: Boolean)

// avro model can't extend SpecificRecordBase and still be Salat serializable
// but we can use part of a SpecificRecord def, the schema, and use it in the generic api
object AvroRecordSchema {
  val SCHEMA$ = new org.apache.avro.Schema.Parser().parse("{\"type\":\"record\",\"name\":\"AvroRecordSchema\",\"namespace\":\"com.julianpeeters.avro2caseclass.db\",\"fields\":[{\"name\":\"schema\",\"type\":[\"string\",\"null\"]},{\"name\":\"format\",\"type\":[\"string\",\"null\"]},{\"name\":\"privacy\",\"type\":\"boolean\"}]}")
}