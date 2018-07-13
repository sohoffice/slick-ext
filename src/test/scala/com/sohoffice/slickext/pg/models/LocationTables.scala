package com.sohoffice.slickext.pg.models

import com.sohoffice.slickext.pg.Point
import slick.lifted
import com.sohoffice.slickext.pg.TestProfile.api._
import com.sohoffice.slickext.pg.models.LocationModels.LocationModel
import slick.lifted.ProvenShape

object LocationTables {

  type LocationRow = (Long, String, Point, Option[Point])

  class LocationTable(tag: Tag)
    extends Table[LocationRow](tag, "locations") {

    def id: Rep[Long] = column[Long]("id", O.PrimaryKey, O.AutoInc)
    def name: Rep[String] = column[String]("name", O.Length(40))
    def location: Rep[Point] = column[Point]("location")
    def optionLocation: Rep[Option[Point]] = column[Option[Point]]("option_location")

    def model = (id, name, location, optionLocation) <> (LocationModel.tupled, LocationModel.unapply)

    // Every table needs a * projection with the same type as the table's type parameter
    def * : ProvenShape[LocationRow] = (id, name, location, optionLocation)
  }
  lazy val locationQuery: TableQuery[LocationTable] = lifted.TableQuery[LocationTable]

}
