/*
 * Copyright 2010 Sanjiv Sahayam
 * Licensed under the Apache License, Version 2.0
 */

package ssahayam

import sbt.Project

trait ConsolePrinter extends PluginSupport { this:Project =>

  import org.openqa.selenium.remote.RemoteWebDriver
  import sbt.Path

  def info(message:String) { log.info(message) }

  def error(message:String) { log.error(message) }

  private def printResults(summary:TestSummary) {
    info("Running -> " + summary.url)
    for {
      failures <- summary.getFailures
      failure <- failures
    } {
      printFailure(failure)
    }
    printSummary(summary)
  }

  private def printSummary(summary:TestSummary) {
    info("total: " + summary.total + ", passed: " + summary.passed + ", failed: " + summary.failed)
    info("")
  }

  def getSummary(driver: RemoteWebDriver): TestSummary = new TestSummary(driver)

  def printTestResults(driver:RemoteWebDriver) { printResults(getSummary(driver)) }

  def printDriver(driverName:String) {
    info("Using " + driverName + " driver >>>")
    info("")
  }

  def printScriptLocation(path:Path) {
    info("Running scripts from: ")
    info(path.getPaths.mkString(getLineSeparator))
  }

  def printFailure(failure: JSModuleFailure) {
    error(failure.moduleName + " - " + failure.testName)
    for {
      test <- failure.failedTests
    } {
      error("")
      error("Test -> " + test.message)
      error("Expected: " + test.expected + ", Received: " + test.received)
      error("Source -> " + test.source)
    }
  }
}