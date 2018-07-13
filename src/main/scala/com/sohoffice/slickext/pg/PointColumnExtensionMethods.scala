package com.sohoffice.slickext.pg

import PointColumnExtensionMethods.Operators
import slick.ast.Library.{JdbcFunction, SqlFunction}
import slick.ast.ScalaBaseType._
import slick.ast._
import slick.lifted._

final class PointColumnExtensionMethods[P1](val c: Rep[P1]) extends AnyVal with ExtensionMethods[Point, P1] {
  import GeoTypes._
  protected[this] implicit def b1Type = implicitly[TypedType[Point]]

  def distance[R](s: Rep[Point])(implicit om: o#to[Double, R], ges: GeoExtensionSchema) = {
    // we don't have a ScalaBaseType for Point, so we've to specify GeoTypes.pointType explicitly
    ges match {
      case GeoExtensionSchema(Some(schema)) =>
        om.column(Operators.distanceS(schema), n, s.toNode)
      case _ =>
        om.column(Operators.distance, n, s.toNode)
    }
  }

  def distanceWithin[R](s: Rep[Point], distanceInUnit: Rep[Double])(implicit om: o#to[Boolean, R], ges: GeoExtensionSchema) = {
    val x = ges match {
      case GeoExtensionSchema(Some(schema)) =>
        om.column(Operators.distanceS(schema), n, s.toNode)
      case _ =>
        om.column(Operators.distance, n, s.toNode)
    }
    om.column(Library.<=, x.toNode, distanceInUnit.toNode)
  }

}

object PointColumnExtensionMethods {
  object Operators {
    val distance = new JdbcFunction("ST_Distance")
    def distanceS(schema: String) = new JdbcFunction(s"$schema.ST_Distance")
  }

  trait PointColumnExtensionMethodConversions {
    implicit def pointColumnExtensionMethods(c: Rep[Point]): PointColumnExtensionMethods[Point] = new PointColumnExtensionMethods[Point](c)
    implicit def pointOptionColumnExtensionMethods(c: Rep[Option[Point]]): PointColumnExtensionMethods[Option[Point]] = new PointColumnExtensionMethods[Option[Point]](c)
  }
}
