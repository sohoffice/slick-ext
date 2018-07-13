package com.sohoffice.slickext.pg

import com.sohoffice.slickext.pg.PointColumnExtensionMethods.PointColumnExtensionMethodConversions
import slick.jdbc.PostgresProfile

trait TestProfile extends PostgresProfile with GeoJdbcTypesComponent with JsonJdbcTypesComponent {
  override val columnTypes: JdbcTypes = new JdbcTypes

  override val api = new API with GeoImplicitColumnTypes with PointColumnExtensionMethodConversions

  class JdbcTypes extends super.JdbcTypes with GeoJdbcTypes with JsonJdbcTypes
}

object TestProfile extends TestProfile