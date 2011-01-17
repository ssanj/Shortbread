/*
 * Copyright 2010 Sanjiv Sahayam
 * Licensed under the Apache License, Version 2.0
 */

package ssahayam

import sbt.Project

trait ConsolePrinter extends PluginSupport { this:Project =>

  import org.openqa.selenium.remote.RemoteWebDriver
  import sbt.Path

  private def printResults(summary:TestSummary) {
    for {
      failures <- summary.getFailures
      failure <- failures
    } {
      printFailure(failure)
    }
    printSummary(summary)
  }

  private def printSummary(summary:TestSummary) {
    log.info("total: " + summary.total + ", passed: " + summary.passed + ", failed: " + summary.failed)
  }

  def getSummary(driver: RemoteWebDriver): TestSummary = new TestSummary(driver)

  def printTestResults(driver:RemoteWebDriver) { printResults(getSummary(driver)) }

  def printScriptLocation(path:Path) {
    log.info("Running scripts from: ")
    log.info(path.getPaths.mkString(getLineSeparator))
  }

  def printFailure(failure: JSModuleFailure) {
    log.error(failure.moduleName + " - " + failure.testName)
    for {
      test <- failure.failedTests
    } {
      log.error(test.message)
      log.error("Expected: " + test.expected + ", Received: " + test.received)
      log.error("Source -> " + test.source)
      log.info("")
    }
  }
}