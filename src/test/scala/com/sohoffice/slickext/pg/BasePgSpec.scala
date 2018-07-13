package com.sohoffice.slickext.pg

import java.security.SecureRandom
import java.sql.{Connection, DriverManager}

import com.sohoffice.slickext.AbstractSpec
import com.typesafe.config.{Config, ConfigFactory}
import org.scalatest.BeforeAndAfterAll
import org.slf4j.LoggerFactory
import slick.basic.DatabaseConfig
import slick.jdbc.JdbcProfile

class BasePgSpec extends AbstractSpec with BeforeAndAfterAll {
  private var config: Config = _
  private var schema: String = _

  implicit val geoSchema = GeoExtensionSchema(Some("public"))

  override protected def beforeAll(): Unit = {
    import scala.collection.JavaConverters._
    schema = "t" + Math.abs(SecureRandom.getInstanceStrong.nextInt()).toString
    config = {
      val rawConfig = ConfigFactory.load()
      val config2 = ConfigFactory.parseMap(Map(
        // "slick.dbs.default.db.url" -> (config.getString("slick.dbs.default.db.url") + s"?currentSchema=$schema"),
        "slick.dbs.default.db.properties.schema" -> schema
      ).asJava)
      config2.withFallback(rawConfig)
        .resolve()
    }

    if (!this.getClass.equals(classOf[BasePgSpec])) {
      // this class itself is not a spec, we don't need to create schema for it.
      val conn = getAdminConnection(config)
      try {
        conn.prepareStatement(s"CREATE SCHEMA $schema").execute()
        logger.debug(s"Schema $schema created.")
      } catch {
        case t: Throwable =>
          logger.error(s"Error creating schema $schema", t)
      } finally {
        conn.close()
      }
    }
  }

  override protected def afterAll(): Unit = {
    // if test.db.drop is not defined or is set to true
    val conn = getAdminConnection(config)
    try {
      val sql = s"DROP SCHEMA IF EXISTS $schema CASCADE"
      logger.debug(s"afterAll execute sql: $sql")
      conn.prepareStatement(sql).execute()
    } catch {
      case t: Throwable =>
        logger.error(s"Error dropping schema $schema.", t)
    } finally {
      conn.close()
    }
  }

  private def getAdminConnection(config: Config) = {
    Class.forName(config.getString("test.db.driver"))
    DriverManager.getConnection(
      config.getString("test.db.url"),
      config.getString("test.db.user"),
      config.getString("test.db.password"))
  }

  def withConnection(func: Connection => Unit) = {
    val conn = db.source.createConnection()

    try {
      func(conn)
    } finally {
      if (conn != null) {
        conn.close()
      }
    }
  }

  lazy val databaseConfig: DatabaseConfig[JdbcProfile] = DatabaseConfig.forConfig[JdbcProfile]("slick.dbs.default", config)
  lazy val db = databaseConfig.db

  val logger = LoggerFactory.getLogger(this.getClass)
}
