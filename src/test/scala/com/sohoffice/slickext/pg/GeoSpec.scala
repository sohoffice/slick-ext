package com.sohoffice.slickext.pg

import com.sohoffice.slickext.pg.models.LocationModels.LocationModel
import org.scalatest.BeforeAndAfterEach
import TestProfile.api._
import com.sohoffice.slickext.pg.models.LocationTables
import slick.jdbc.JdbcProfile
import slick.lifted
import slick.lifted.{ProvenShape, Rep}

class GeoSpec extends BasePgSpec with BeforeAndAfterEach {
  import com.sohoffice.slickext.pg.models.LocationTables._

  override protected def beforeEach(): Unit = {
    withConnection(conn => {
      try {
        val sql =
          """
            |CREATE TABLE IF NOT EXISTS locations (
            |  id SERIAL PRIMARY KEY,
            |  name VARCHAR(40),
            |  location public.GEOGRAPHY NOT NULL,
            |  option_location public.GEOGRAPHY
            |)""".stripMargin
        logger.info(s"Execute default sql: $sql")
        val prep = conn.prepareStatement(sql)
        prep.execute()

        val pstmt1 = conn.prepareStatement("INSERT INTO locations (name,location,option_location) VALUES (?,public.st_point(?,?),public.st_point(?,?))")
        pstmt1.setString(1, "A")
        pstmt1.setBigDecimal(2, taipei101.lon.bigDecimal)
        pstmt1.setBigDecimal(3, taipei101.lat.bigDecimal)
        pstmt1.setBigDecimal(4, taipei101.lon.bigDecimal)
        pstmt1.setBigDecimal(5, taipei101.lat.bigDecimal)
        pstmt1.executeUpdate()

        val pstmt2 = conn.prepareStatement("INSERT INTO locations (name,location) VALUES (?,public.st_point(?,?))")
        pstmt2.setString(1, "B")
        pstmt2.setBigDecimal(2, palaceMuseum.lon.bigDecimal)
        pstmt2.setBigDecimal(3, palaceMuseum.lat.bigDecimal)
        pstmt2.executeUpdate()
      } catch {
        case t: Throwable =>
          logger.error(s"default sql error ${t.getMessage}")
      }
      super.beforeEach()
    })
  }

  override protected def afterEach(): Unit = {
    super.afterEach()
    withConnection(conn => {
      try {
        val sql = "DELETE FROM locations"
        logger.info(s"Execute default sql: $sql")
        val prep = conn.prepareStatement(sql)
        prep.execute()
      } catch {
        case t: Throwable =>
          logger.info(s"default sql error ${t.getMessage}")
      }
    })
  }

  val taipei101 = Point(121.565000, 25.033611)
  val palaceMuseum = Point(121.548500, 25.102330)
  val daAnPark = Point(121.534696, 25.031593)

  it should "insert records" in {
    val q = locationQuery += (
      0, "C", daAnPark, Some(daAnPark)
    )
    db.run(q) flatMap { _ =>
      for {
        _ <- db.run(locationQuery.length.result) map { size =>
          size should ===(3)
        }
        _ <- db.run(locationQuery.filter(_.name === "C").map(_.model).result.headOption) map {
          case Some(rec) =>
            rec.location should === (daAnPark)
          case _ =>
            fail("location C not found")
        }
        as3 <- db.run(locationQuery.sortBy(_.name.asc).map(_.model).result) map { seq =>
          seq.map(_.name) should contain theSameElementsInOrderAs Seq("A", "B", "C")
          seq.map(_.location) should contain theSameElementsInOrderAs Seq(taipei101, palaceMuseum, daAnPark)
          seq.map(_.optionLocation) should contain theSameElementsInOrderAs Seq(Some(taipei101), None, Some(daAnPark))
        }
      } yield {
        as3
      }
    }
  }

  it should "sort by distance" in {
    val q = locationQuery
      .sortBy(_.location.distance(palaceMuseum).asc)
      .map(_.model)
    db.run(q.result) map { seq =>
      seq should have size 2
      seq.map(_.name) should contain theSameElementsInOrderAs Seq("B", "A")
    }
  }

  it should "filter by distanceWithin" in {
    val q = locationQuery
      .filter(_.location.distanceWithin(taipei101, 100d))
      .map(_.model)
    db.run(q.result) map { seq =>
      seq should have size 1
      seq.map(_.name).head should === ("A")
    }
  }

  it should "filter by optional distanceWithin" in {
    val q = locationQuery
      .filter(_.optionLocation.distanceWithin(taipei101, 100d))
      .map(_.model)
    db.run(q.result) map { seq =>
      seq should have size 1
      seq.map(_.name).head should === ("A")
    }
  }
}
