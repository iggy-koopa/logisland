package com.hurence.logisland.parser.base

import com.hurence.logisland.record.Field

/**
  * Created by gregoire on 13/04/16.
  */
abstract class BaseLogParserTest extends BaseUnitTest{
    protected def testAnEventField(apacheEventField: Field, name: String, eventFieldType: String, value: Object) = {
        apacheEventField.getName shouldBe name
        apacheEventField.getType shouldBe eventFieldType
        eventFieldType match {
            case "string" => apacheEventField.getRawValue.asInstanceOf[String] shouldBe value.asInstanceOf[String]
            case "long" => apacheEventField.getRawValue.asInstanceOf[Long] shouldBe value.asInstanceOf[Long]
            case "int" => apacheEventField.getRawValue.asInstanceOf[Int] shouldBe value.asInstanceOf[Int]
            case "double" => apacheEventField.getRawValue.asInstanceOf[Double] shouldBe value.asInstanceOf[Double]
            case "float" => apacheEventField.getRawValue.asInstanceOf[Float] shouldBe value.asInstanceOf[Float]
            case "boolean" => apacheEventField.getRawValue.asInstanceOf[Boolean] shouldBe value.asInstanceOf[Boolean]
            case x => throw new Error(s"this type '$x' is not recognised yet in 'testAnEventField' function")
        }

    }
}
