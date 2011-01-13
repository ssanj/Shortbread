/*
 * Copyright 2010 Sanjiv Sahayam
 * Licensed under the Apache License, Version 2.0
 */
package ssahayam

import org.scalatest.matchers.ShouldMatchers
import org.scalatest.FunSuite

final class PluginSupportWithRunWithinSuite extends FunSuite with ShouldMatchers with PluginSupport {

  test("runSafely should handle Success") {
    runSafely {
      (driver:SomeDriver) =>  22
    }(SomeDriverInstance) should equal (Right(22))
  }

  test("runSafely should handle Failure") {
    runSafely {
      (_:SomeDriver) =>  throw new RuntimeException("error")
    }(SomeDriverInstance) should equal (Left("error"))
  }

  test("leftToOption should convert a Left(String) to a Some(String)") {
    Left("error").toLeftOption should equal (Some("error"))
  }

  test("leftToOption should convert a Right(x) to a None") {
    Right(24).toLeftOption should equal (None)
  }

  class SomeDriver

  object SomeDriverInstance extends SomeDriver
}