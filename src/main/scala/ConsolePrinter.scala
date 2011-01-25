/*
 * Copyright 2010 Sanjiv Sahayam
 * Licensed under the Apache License, Version 2.0
 */

package shortbread

import sbt.Project

//Side-effecting console activity.
trait ConsolePrinter extends PluginSupport with SideEffects { this:Project =>

  import org.openqa.selenium.remote.RemoteWebDriver
  import sbt.Path

  def info(message:String) { log.info(message) }

  def newLine { log.info("") }

  def error(message:String) { log.error(message) }

  def printResults(summary:TestSummary) {
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
    newLine
  }

  def getSummary(driver: RemoteWebDriver): TestSummary = new TestSummary(driver)

  def printDriver(driverName:String) {
    info("Using " + driverName + " driver >>>")
    newLine
  }

  def printScriptLocation(path:Path) {
    info("Running scripts from: " + path.getPaths.mkString(getLineSeparator))
  }

  def printFailure(failure: JSModuleFailure) {
    error("Module -> " + failure.moduleName)
    error("TestCase -> " + failure.testName)
    for {
      test <- failure.failedTests
    } {
      error("Test -> " + test.message)
      error("Expected: " + test.expected + ", Received: " + test.received)
      error("Source -> " + test.source)
    }
  }
}