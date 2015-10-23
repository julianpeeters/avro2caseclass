avro2caseclass
====================

Generate Scala case class definitions from Avro schemas.


Try it at: [https://avro2caseclass.herokuapp.com/](https://avro2caseclass.herokuapp.com/) (hobbyist account, so give 'er a sec to rev up)

![Screenshot](public/img/Screenshot.png)


### Use cases



Use [Scala case classes](http://docs.scala-lang.org/tutorials/tour/case-classes.html) to represent Avro records allows for clean integration with your Scala project, and get the many benefits that case classes offer.

Generate:

- Vanilla case classes (for use with [Scalavro](https://github.com/GenslerAppsPod/scalavro), [Salat-Avro](https://github.com/julianpeeters/salat-avro), [gfc-avro](https://github.com/gilt/gfc-avro), etc.)

- Case classes that implement `SpecificRecordBase` (for use with the Avro Specific API - Scalding, Spark, Avro, etc.).

- Case classes that implement `AvroSerializable` (for use with the Avro Specific API - Scalding, Spark, Avro, etc.).



### Formats


#### Output


Source code can be generated in two output formats:


- Standard: plain Scala case classes with `val` immutable fields. For use with scalavro, salat-avro, gfc-avro, and other Scala Avro seriliazation runtimes.


- SpecificRecord: Implements Avro's `SpecificRecord` with `var` mutable fields required for use with Apache Avro's `Specific` API. For use with Spark, Scalding, and other Apache Avro runtimes.


- Scavro: Implements Scavro's `AvroSerializable` Scala wrapper classes with `val` immutable fields required for use with Apache Avro's `Specific` API. For use with Spark, Scalding, and other Apache Avro runtimes.


#### Input


Inputs must be `String` representation fo the following formats:

- Avro Schema as found in `.avsc` files
- Avro Protocol as found in `.avpr` files
- Avro IDL as found in `.avdl` files
- Scala case class definition as found in `.scala` files


Avro2CaseClass will generate vanilla case class definitions, as well as case classes that implement `SpecificRecordBase` or Scavro's `AvroSerializable`.



#### Supported Datatypes


Supports generating case classes with arbitrary fields of the following datatypes: 


INT -> Int

LONG -> Long

FLOAT -> Float

DOUBLE -> Double

STRING -> String

BOOLEAN -> Boolean

NULL  -> 

MAP -> Map

ENUM -> `Standard`: scala.Enumeration, `Specific`: Java Enum

BYTES -> //TODO

FIXED -> //TODO

ARRAY -> List, `Scavro`: Array

UNION -> Option

RECORD -> case classes



### Warnings


1) Avro's Specific API currently relies on reflection that fails on Scala class because all fields are `private final`. Therefore, preempt Avro's reflection by passing a schema to SpecificDatumWriter's constructor. 


```scala
val schema = MyRecord.SCHEMA$ // or 'new MyRecord().getSchema'
val sdw = SpecificDatumWriter[MyRecord](schema)
```


2) Import statements are not supported when generating specific records from standard case class definitions. Use fully qualified type names when generating classes from multiple namespaces.


3) Scavro wrapper classes use the namespace provided by the schema, with 'model' appended so as not to conflict with the Java classes they wrap. Use `sbt-avrohugger` or `avrohugger` directly if one would like to customize package names during code generation.


4) The dataset associated with this project is subject to change until either:
* the project hits version `1.0.0`
* data is versioned in a data package manager such as dat


### Alternatives


Integrate Avro Scala code-generation into your project:
- [avrohugger](https://github.com/julianpeeters/avrohugger): Scala version of Avro SpecificCompiler and Avro-Tools
- [sbt-avrohugger](https://github.com/julianpeeters/sbt-avrohugger): Generate classes seamlessly at each compile, analogous to sbt-avro
- [avro-scala-macro-annotations](https://github.com/julianpeeters/avro-scala-macro-annotations): experimental feature, but allows for '[interactive](http://bit.ly/1TJ42IU)' code generation.


### Dataset

In the spirit of open data, input is collected and made available for download as an Avro datafile.

//TODO: dat data package manager integration


###Creating a Heroku app

Make sure that `mongo.default.uri` in `conf/appication.conf` is set to `${MONGOLAB_URI}`,
then run:

```
heroku create avro2caseclass
git push heroku master
heroku addons:create mongolab
heroku ps:scale web=1
```

### Development

To develop locally without setting up a MongoLab account, simply start a local MongoDB and point the `mongodb.default.uri` configuration key to it.

~~~bash
mkdir -p /tmp/avro2caseclass && mongod --dbpath /tmp/avro2caseclass --smallfiles
sbt run -Dmongodb.default.uri=mongodb://localhost:27017/avro2caseclass
~~~

### Credits

- based on [json2caseclass](http://json2caseclass.cleverapps.io/)
- play project based on Mironor's [example](https://github.com/Mironor/Play-2.0-Scala-MongoDb-Salat-exemple).
- depends on [avrohugger](https://github.com/julianpeeters/avrohugger)


#### Contributors:
- [Marius Soutier](https://github.com/mariussoutier)
