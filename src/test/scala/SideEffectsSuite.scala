/*
 * Copyright 2010 Sanjiv Sahayam
 * Licensed under the Apache License, Version 2.0
 */
package shortbread

import org.scalatest.FunSuite
import org.scalatest.matchers.ShouldMatchers
import org.scalatest.mock.MockitoSugar
import org.mockito.Mockito._

final class SideEffectsSuite extends FunSuite with ShouldMatchers with SideEffects with MockitoSugar {

  import org.openqa.selenium.remote.RemoteWebDriver

  test("Closing a RemoteWebDriver should quit if exit=true") {
    val mockDriver = mock[RemoteWebDriver]
    close(true)(mockDriver)
    verify(mockDriver).quit
  }

  test("Closing a RemoteWebDriver should not quit if exist=false") {
    val mockDriver = mock[RemoteWebDriver]
    close(false)(mockDriver)
    verifyZeroInteractions(mockDriver)
  }

  test("open should return a NamedDriver's RemoteWebDriver") {
    val mockDriver = mock[RemoteWebDriver]
    val nd = NamedDriver("blah", () => mockDriver)
    open(nd) should be theSameInstanceAs (mockDriver)
    verifyZeroInteractions(mockDriver)
  }

  test("loadPage should load the supplied url with the supplied RemoteWebDriver") {
    val mockDriver = mock[RemoteWebDriver]
    val url = "blah"
    loadPage(url)(mockDriver)
    verify(mockDriver).get(url)
  }

  test("getLineSeparator should return the system line separator") {
    getLineSeparator(":") should equal (":")
  }

  test("getLineSeparator should return a default separator if the function supplied fails") {
    getLineSeparator(throw new RuntimeException("Could not read property")) should equal ("\n")
  }

  test("stringsOnNewLines should separate Strings supplied by line separator") {
    stringsOnNewLines("One", "Two") should equal ("One" + separator + "Two")
  }

  test("failOnTestError should throw a JavaScriptTestFailedException when there are errors") {
    intercept[JavaScriptTestFailedException] { failOnTestError(true) }
  }

  test("failOnTestError should not throw a JavaScriptTestFailedException when there are no errors") {
    failOnTestError(false)
  }

  test("JavaScriptTestFailedException should have a default error message") {
    val ex = new JavaScriptTestFailedException
    ex.getMessage should equal ("There were test failures")
  }
}