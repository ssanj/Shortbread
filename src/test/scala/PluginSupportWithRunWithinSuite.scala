/*
 * Copyright 2010 Sanjiv Sahayam
 * Licensed under the Apache License, Version 2.0
 */
package ssahayam

import org.scalatest.matchers.ShouldMatchers
import org.scalatest.FunSuite

final class PluginSupportWithRunWithinSuite extends FunSuite with ShouldMatchers with PluginSupport {

  test("runSafelyWithEither should handle Success") {
    runSafelyWithEither(22) should equal (Right(22))
  }

  test("runSafelyWithEither should handle Failure") {
    runSafelyWithEither(throw new RuntimeException("error")) should equal (Left("error"))
  }

  test("leftToOption should convert a Left(String) to a Some(String)") {
    Left("error").toLeftOption should equal (Some("error"))
  }

  test("leftToOption should convert a Right(x) to a None") {
    Right(24).toLeftOption should equal (None)
  }

  test("runSafelyWithResource should handle Success") {
    runSafelyWithResource[String, Unit, Unit](s => s + s)("success")(x => "") should equal (None)
  }

  test("runSafelyWithResource should handle open Failure") {
    runSafelyWithResource[String, Unit, Unit](s => s + s)(throw new RuntimeException("Open Error"))(x => "") should equal (Some("Open Error"))
  }

  test("runSafelyWithResource should handle function Failure") {
    runSafelyWithResource[String, Unit, Unit](s => throw new RuntimeException("function error"))("failure")(x => "") should equal (Some("function error"))
  }

  test("runSafelyWithResource should handle close Failure") {
    runSafelyWithResource[String, Unit, Unit](s => s + s)("failure")(x => throw new RuntimeException("close error")) should equal (Some("close error"))
  }

  test("runSafelyWithResource should handle return the function error even if the close fails") {
    runSafelyWithResource[Int, Unit, Unit](n => throw new RuntimeException("function error")){42}(x => throw new RuntimeException("final error")) should equal (Some("function error"))
  }

  class SomeDriver

  object SomeDriverInstance extends SomeDriver
}