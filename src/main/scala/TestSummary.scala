/*
 * Copyright 2010 Sanjiv Sahayam
 * Licensed under the Apache License, Version 2.0
 */

package ssahayam

import org.openqa.selenium.remote.RemoteWebDriver
import org.openqa.selenium.WebElement

class JSTestFailure(element: WebElement) extends SeleniumSupport {

  import xpathy._

  lazy val message = getText(element, "span[@class='test-message']")
  lazy val errorBody = getElement(element, "table/tbody")
  lazy val expected = getText(errorBody, "tr[@class='test-expected']/td/pre")
  lazy val received = getText(errorBody, "tr[@class='test-actual']/td/pre")
  lazy val source = getText(errorBody, "tr[@class='test-source']/td/pre")
}

class JSModuleFailure(element: WebElement) extends SeleniumSupport {

  import xpathy._

  lazy val moduleName = getText(element, "strong/span[@class='module-name']")("Default Module")
  lazy val testName = getText(element, "strong/span[@class='test-name']")
  lazy val failedTests: Seq[JSTestFailure] = (for (failedTest <- getElements(element, "ol/li[@class='fail']")) yield (new JSTestFailure(failedTest))).toSeq
}

class TestSummary(driver: RemoteWebDriver) extends SeleniumSupport {

  import xpathy._

  lazy val testResult = getElement(driver, "//p[@id='qunit-testresult']")
  lazy val failed = getText(testResult, "span[@class='failed']")
  lazy val passed = getText(testResult, "span[@class='passed']")
  lazy val total = getText(testResult, "span[@class='total']")

  lazy val hasFailures = failed != '0'

  lazy val getFailures: Option[Seq[JSModuleFailure]] = if (hasFailures) Some(getModuleFailures) else None

  val getModuleFailures: Seq[JSModuleFailure] = {
    import xpathy._
    (for (failedModule <- getElements(driver, "//ol[@id='qunit-tests']/li[@class='fail']")) yield (new JSModuleFailure(failedModule))).toSeq
  }
}
