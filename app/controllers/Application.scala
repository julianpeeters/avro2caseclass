package controllers

import play.api._
import play.api.mvc._
import play.api.data._
import play.api.data.Forms._

import avrohugger.Generator

case class ClassDefAndForm(form: Form[String], classDef:String)

object Application extends Controller {

  val taskForm = Form (
    "json" -> nonEmptyText
  )

  val defaultView = Ok(views.html.index(taskForm, List(ClassDefAndForm(taskForm, "")), ""))

  def index = Action {
    defaultView
  }

  def generate = Action { implicit request =>
    taskForm.bindFromRequest.fold(
      errors => defaultView,
      json  => {
        val generator = new Generator
        val classDefs = generator.fromString(json.replace("\n", "")).reverse
        val filledForms = classDefs.map(cd => taskForm.fill(cd))
        val zipped = filledForms.zip(classDefs)
        val classDefsAndForms = zipped.map(cdaf => new ClassDefAndForm(cdaf._1, cdaf._2))
        Ok(views.html.index(taskForm.fill(json.replace("\n", "")), classDefsAndForms, "json"))
      }
    )

  }
  
}