package com.sohoffice.slickext.pg

import java.sql.Connection

import org.postgis.PGgeometry
import org.postgresql.PGConnection
import slick.jdbc.DatabaseUrlDataSource

class GeoDatabaseUrlDataSource extends DatabaseUrlDataSource {
  private var schema: Option[String] = None
  private var postgisSchema: Option[String] = None

  override def getConnection(): Connection = {
    val c = super.getConnection()
    val cls = Class.forName("org.postgis.PGgeometry").asInstanceOf[Class[PGgeometry]]
    val pgconn = c.asInstanceOf[PGConnection]
    pgconn.addDataType("geography", cls)
    logger.trace(s"Added geography data type to pg connection: $c")

    if(schema.isDefined) {
      c.setSchema(schema.get)
    }
    if(postgisSchema.isDefined) {
      pgconn.addDataType(s"${postgisSchema.get}.geography", cls)
      logger.trace(s"Added "+s"${postgisSchema.get}.geography"+" data type to pg connection.")
      pgconn.addDataType(s""""${postgisSchema.get}"."geography"""", cls)
      logger.trace(s"Added "+s""""${postgisSchema.get}"."geography""""+" data type to pg connection.")
    }

    c
  }

  /** Where the normal database objects was accessed.
    *
    * @param s
    */
  def setSchema(s: String) = this.schema = Option(s).filter(_.nonEmpty)

  /** Where the postgis extension was installed.
    *
    * @param s
    */
  def setPostgisSchema(s: String) = this.postgisSchema = Option(s).filter(_.nonEmpty)
}
