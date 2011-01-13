/*
 * Copyright 2010 Sanjiv Sahayam
 * Licensed under the Apache License, Version 2.0
 */

package ssahayam

import org.openqa.selenium.remote.RemoteWebDriver
import org.openqa.selenium.WebElement

class JSTestFailure(element: WebElement) extends SeleniumSupport {

  import xpathy._
  val message = getText(element, "span[@class='test-message']")
  val errorBody = getElement(element, "table/tbody")
  val expected = getText(errorBody, "tr[@class='test-expected']/td/pre")
  val received = getText(errorBody, "tr[@class='test-actual']/td/pre")
  val source = getText(errorBody, "tr[@class='test-source']/td/pre")
}

class JSModuleFailure(element: WebElement) extends SeleniumSupport {
  import xpathy._
  val moduleName = getText(element, "strong/span[@class='module-name']")("Default Module")
  val testName = getText(element, "strong/span[@class='test-name']")
  val failedTests: Seq[JSTestFailure] = (for (failedTest <- getElements(element, "ol/li[@class='fail']")) yield (new JSTestFailure(failedTest))).toSeq
}

class TestSummary(driver: RemoteWebDriver) extends SeleniumSupport {
  import xpathy._
  val testResult = getElement(driver, "//p[@id='qunit-testresult']")
  val failed = getText(testResult, "span[@class='failed']")
  val passed = getText(testResult, "span[@class='passed']")
  val total = getText(testResult, "span[@class='total']")

  def hasFailures = failed != '0'

  def getFailures: Option[Seq[JSModuleFailure]] = if (hasFailures) Some(getModuleFailures) else None

  private def getModuleFailures: Seq[JSModuleFailure] = {
    import xpathy._
    (for (failedModule <- getElements(driver, "//ol[@id='qunit-tests']/li[@class='fail']")) yield (new JSModuleFailure(failedModule))).toSeq
  }
}

  class JSRunner(driver: RemoteWebDriver, file:String) {
    driver.get(file)
    val summary = new TestSummary(driver)
    for {
      failures <- summary.getFailures
      failure <- failures
    } { print(failure) }
    print(summary)
  }

  object Runner {
  def print(summary:TestSummary) {
    println("total: " + summary.total + ", passed: " + summary.passed + ", failed: " + summary.failed)
  }

  def print(failure:JSModuleFailure) {
    println(failure.moduleName + " - " + failure.testName)
    for {
      test <- failure.failedTests
    } {
      println("\t" + test.message)
      println("\t\tExpected: " + test.expected + ", Received: " + test.received)
      println("\t\tSource -> " + test.source)
      println
    }
  }
}
