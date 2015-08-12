package models

object Obfuscator {

  def obfuscate(m: AvroRecordSchema): AvroRecordSchema = {
    var nameCounter = 0
    // Check if there's a name field, if there is, use it with it's value as new,
    // more specific regex, replacing all instances of that name, then recurse.
    def maskNames(schemaStr: String): String = {
      // match names, expect those that have been already masked
      val nameRegEx = "(?!.*avro2caseclassMaskedName.*?)\"name\": \"(.*?)\"".r
      nameRegEx.findFirstIn(schemaStr) match {
        case Some(x) => {
          nameCounter = nameCounter + 1
          val updatedCounter = nameCounter
          val currentNameRegEx = x.r
          val currentNameMask = "\"name\": \"avro2caseclassMaskedName" + updatedCounter + "\""
          val maskedSchemaStr = currentNameRegEx.replaceAllIn(schemaStr, currentNameMask)
          maskNames(maskedSchemaStr)
        }
        case None => schemaStr
      }
    }

    def maskNamespace(schemaStr: String): String = {
      val namespaceRegEx = "\"namespace\": \"(.*?)\"".r
      val namespaceMask = "\"namespace\": \"avro2caseclassMaskedNamespace\""
      namespaceRegEx.replaceAllIn(schemaStr, namespaceMask)
    }

    val masks = Seq(maskNamespace(_), maskNames(_))
    val maskedSchema = masks.foldLeft(m.schema)(_ map _)
    m.copy(schema = maskedSchema)
  }
  
}