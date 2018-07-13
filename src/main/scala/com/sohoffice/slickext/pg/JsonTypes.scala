package com.sohoffice.slickext.pg

import play.api.libs.json.JsValue
import slick.ast
import slick.ast.{BaseTypedType, ScalaType}

import scala.reflect.ClassTag

class JsonScalaType[T](implicit val classTag: ClassTag[T]) extends ScalaType[T] with BaseTypedType[T] {
  override def nullable: Boolean = true

  override def ordered: Boolean = false

  override def scalaOrderingFor(ord: ast.Ordering) = new Ordering[T] {
    override def compare(x: T, y: T): Int = 0
  }
}

object JsonTypes {
  implicit val jsValueType = new JsonScalaType[JsValue]()
}
