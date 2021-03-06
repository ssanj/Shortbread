/*
 * Copyright 2010 Sanjiv Sahayam
 * Licensed under the Apache License, Version 2.0
 */

package shortbread

import org.scalatest.matchers.ShouldMatchers
import org.scalatest.FunSuite

final class PluginSupportSuite extends FunSuite with ShouldMatchers with PluginSupport {

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

  test("runSafelyWithResource should handle open failure") {
    runSafelyWithResource[String, Unit, Unit](s => s + s)(throw new RuntimeException("Open fail"))(x => "") should equal (Some("Open fail"))
  }

  //what a crappy way to test this. Is there a better way without side-effects?
  test("runSafelyWithResource should close the resource even if the function fails") {
    import scala.collection.mutable.ArrayBuffer
    val errors = new ArrayBuffer[String]
    errors.size should equal (0)
    runSafelyWithResource[String, Unit, Unit](s => throw new RuntimeException("function fail"))("open")(x => errors += "close") should equal (Some("function fail"))
    errors.contains("close") should equal (true)
  }

  test("runSafelyWithResource should handle close failure") {
    runSafelyWithResource[String, Unit, Unit](s => s + s)("open")(x => throw new RuntimeException("close fail")) should equal (Some("close fail"))
  }

  test("runSafelyWithResource should handle return the function fail even if the close fails also") {
    runSafelyWithResource[Int, Unit, Unit](n => throw new RuntimeException("function fail")){42}(x => throw new RuntimeException("final error")) should equal (Some("function fail"))
  }

  test("runSafelyWithOptionReturnError should return None on success") {
    runSafelyWithOptionReturnError("success") should equal (None)
  }

  test("runSafelyWithOptionReturnError should return Some(error) on failure") {
    runSafelyWithOptionReturnError(throw new RuntimeException("error")) should equal (Some("error"))
  }

  test("runSafelyWithOptionReturnResult should return result on success") {
    runSafelyWithOptionReturnResult("success") should equal (Some("success"))
  }

  test("runSafelyWithOptionReturnResult should return None on failure") {
    runSafelyWithOptionReturnResult(throw new RuntimeException("error")) should equal (None)
  }

  test("runSafelyWithDefault should return result on success") {
    runSafelyWithDefault("5".toInt)(_ => 0) should equal (5)
  }

  test("runSafelyWithDefault should return default on failure") {
    runSafelyWithDefault[Int]("five".toInt)(_ => 0) should equal (0)
  }

  test("getErrors should return None when there are no errors") {
    getErrors(Seq.empty)(_ + ":" +  _) should equal (None)
  }

  test("getErrors should concatenate errors when there are errors") {
    getErrors(Seq(Some("error1"), Some("error2"), None, Some("error3")))(_ + ":" + _) should equal (Some("error1:error2:error3"))
  }

  test("getErrors should handle a single error") {
    getErrors(Seq(Some("error1")))(_ + ":" + _) should equal (Some("error1"))
  }

  test("stringAdd should add 2 Strings with a separator") {
    stringAdd("<>")("one", "two") should equal ("one<>two")
  }

  test("stringToOption should convert an empty String to None") {
    stringToOption("") should equal (None)
  }

  test("stringToOption should convert a String value x to Some(x)") {
    stringToOption("some value") should equal (Some("some value"))
  }

  test("getStringOrDefault should return the supplied function succeeds") {
    getStringOrDefault("abc")("default") should equal ("abc")
  }

  test("getStringOrDefault should return the default String is the function fails") {
    getStringOrDefault(throw new RuntimeException("boom!"))("default") should equal ("default")
  }

  class SomeDriver

  object SomeDriverInstance extends SomeDriver
}