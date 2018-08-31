package com.sohoffice.slickext.pg

import com.sohoffice.slickext.AbstractSpec

class PointSpec extends AbstractSpec {

  it should "unapplyLatLng" in {
    Point.unapplyLatLng("0,0") should ===(Some(Point(0, 0)))
    Point.unapplyLatLng("123.123,15.15") should ===(Some(Point(15.15, 123.123)))
    Point.unapplyLatLng("0,a") shouldBe 'empty
    Point.unapplyLatLng("") shouldBe 'empty
    Point.unapplyLatLng("0,1,2") shouldBe 'empty
  }
}
