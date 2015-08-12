package controllers
import models._

import play.api._
import play.api.mvc._
import play.api.data._
import play.api.data.Forms._

import avrohugger.Generator
import avrohugger.format._

import org.apache.avro.generic.GenericData
import org.apache.avro.generic.GenericRecord
import org.apache.avro.generic.GenericDatumWriter
import org.apache.avro.file.DataFileWriter

import java.io.File

object Application extends Controller {

  def taskForm = Form (
    mapping(
      "schema" -> optional(text),
      "format" -> optional(text),    // output format, e.g. `SpecificRecords`
      "privacy" -> boolean // masks namespace, record, and field names
    )(AvroRecordSchema.apply)(AvroRecordSchema.unapply)
  ) 

  val defaultView = Ok(views.html.index(taskForm, List(taskForm), ""))
  val schemasView = Redirect(routes.Application.schemas)//Ok(views.html.schemas(MongoModel.all))

  def index = Action {
    defaultView
  }

  def schemas = Action {
    Ok(views.html.schemas(MongoModel.all))//schemasView
  }

  def generate = Action { implicit request =>
    taskForm.bindFromRequest.fold(
      errors => defaultView,
      recordSchema => recordSchema match {
        case AvroRecordSchema(Some(schemaStr), Some(format), maybePrivacy) => {
          // get the correct generator for a given format
          val generator = format match {
            case "standard" => new Generator(Standard)
            case "specific" => new Generator(SpecificRecord)
          }
          // top-level: RECORD as case class, ENUM as object/Java enum
          val topLevelDefs = generator.stringToStrings(schemaStr).reverse
          // keep the input form as it looked upon submission
          val filledTaskForm = taskForm.fill(recordSchema)
          // update the result form(s)
          val resultForms = topLevelDefs.map(cd => {
            taskForm.fill(AvroRecordSchema(Some(cd), Some(format), maybePrivacy))
          })
          
          MongoModel.create(recordSchema)
          Ok(views.html.index(filledTaskForm, resultForms, "schema"))
        }
        case _ => defaultView
      }
    )
  }

  def download() = Action {
    // Heroku files are not persisted, so write a new temp file each time
    val records = MongoModel.all.map(swid => swid.recordSchema)
    val dateFormat = new java.text.SimpleDateFormat("yyyyMMddhhmm'.avro'")
    val file = new File("avro2caseclass" + dateFormat.format(new java.util.Date()))
      file.deleteOnExit()
    // Salat needs a pure case class model (can't handle extending SpecificRecordBase)
    val schema = AvroRecordSchema.SCHEMA$
    val userDatumWriter = new GenericDatumWriter[GenericRecord](schema)
    val dataFileWriter = new DataFileWriter[GenericRecord](userDatumWriter)
    dataFileWriter.create(schema, file)
    records.foreach(record => {
      val r = new GenericData.Record(schema)
      r.put("schema", record.schema.getOrElse("null"))
      r.put("format", record.format.getOrElse("null"))
      r.put("privacy", record.privacy)

      dataFileWriter.append(r)
    })
    dataFileWriter.close()
    Ok.sendFile(file)
  }



  def delete(id: String) = Action {
    MongoModel.delete(id)
    Redirect(routes.Application.schemas())
  }
  
}