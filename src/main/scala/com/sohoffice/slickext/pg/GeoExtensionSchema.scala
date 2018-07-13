package com.sohoffice.slickext.pg

/** Register this class in implicit scope when GeoExtensionMethods are to be used.
  *
  * Specify the schema param, if postgis was installed in another schema. Otherwise use None.
  *
  * @param schema
  */
case class GeoExtensionSchema(
  schema: Option[String] = None
)
