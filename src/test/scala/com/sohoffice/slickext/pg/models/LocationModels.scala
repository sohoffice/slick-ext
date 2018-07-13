package com.sohoffice.slickext.pg.models

import com.sohoffice.slickext.pg.Point
import com.sohoffice.slickext.pg.TestProfile.api._

object LocationModels {

  case class LocationModel(
    id: Long,
    name: String,
    location: Point,
    optionLocation: Option[Point]
  )
  case class LiftedLocationModel(
    id: Rep[Long],
    name: Rep[String],
    location: Rep[Point],
    optionLocation: Rep[Option[Point]]
  )
  implicit object LocationModelShape extends CaseClassShape(LiftedLocationModel.tupled, LocationModel.tupled)

}
