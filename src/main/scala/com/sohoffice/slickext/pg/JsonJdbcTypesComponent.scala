package com.sohoffice.slickext.pg

import java.io.StringReader
import java.nio.charset.StandardCharsets
import java.sql.{Clob, PreparedStatement, ResultSet, Types}

import org.postgresql.util.PGobject
import org.slf4j.LoggerFactory
import play.api.libs.json.{JsValue, Json}
import slick.SlickException
import slick.ast.Type
import slick.jdbc.{JdbcProfile, JdbcType, JdbcTypesComponent}

import scala.io.Source

trait JsonJdbcTypesComponent extends JdbcTypesComponent {
  self: JdbcProfile =>

  class JsonDriverJdbcType extends DriverJdbcType[JsValue] {
    override def sqlType: Int = java.sql.Types.OTHER

    override def setValue(v: JsValue, p: PreparedStatement, idx: Int): Unit = {
      p.getParameterMetaData.getParameterType(idx) match {
        case Types.CLOB =>
          val r = new StringReader(Json.asciiStringify(v))
          try {
            p.setClob(idx, r)
          } finally {
            r.close()
          }
        case _ =>
          val po = new PGobject()
          po.setType("jsonb")
          po.setValue(v.toString())
          p.setObject(idx, po)
      }
    }

    override def getValue(r: ResultSet, idx: Int): JsValue = {
      r.getObject(idx) match {
        case x: PGobject if x.getType == "jsonb" =>
          Json.parse(x.getValue)
        case x: PGobject =>
          logger.warn(s"PGobject with type: ${x.getType}")
          Json.parse(x.getValue)
        case x: Clob =>
          val source = Source.fromInputStream(x.getAsciiStream, StandardCharsets.US_ASCII.name())
          Json.parse(source.mkString)
        case null =>
          null
        case x =>
          throw new SlickException(s"Not a json parseable data type: ${x.getClass}")
      }
    }

    override def updateValue(v: JsValue, r: ResultSet, idx: Int): Unit = {
      val po = new PGobject()
      po.setType("jsonb")
      po.setValue(v.toString())
      r.updateObject(idx, po)
    }

    override def hasLiteralForm: Boolean = false
  }

  private val _jsonDriverJdbcType = new JsonDriverJdbcType

  trait JsonJdbcTypes {
    val jsonJdbcType = _jsonDriverJdbcType
  }

  trait JsonImplicitColumnTypes {
    implicit val jsonJdbcType = _jsonDriverJdbcType
  }

  override def jdbcTypeFor(t: Type) = {
    ((t.structural match {
      case JsonTypes.jsValueType => _jsonDriverJdbcType
      case _ =>
        super.jdbcTypeFor(t)
    }): JdbcType[_]).asInstanceOf[JdbcType[Any]]
  }

  private val logger = LoggerFactory.getLogger(this.getClass)
}
