/*
 * Copyright 2010 Sanjiv Sahayam
 * Licensed under the Apache License, Version 2.0
 */

package ssahayam

import sbt.Project

trait ConsolePrinter extends PluginSupport { this:Project =>

  import org.openqa.selenium.remote.RemoteWebDriver

  def printResults(summary:TestSummary) {
    for {
      failures <- summary.getFailures
      failure <- failures
    } {
      printFailure(failure)
    }
    printSummary(summary)
  }

  def printSummary(summary:TestSummary) {
    log.info("total: " + summary.total + ", passed: " + summary.passed + ", failed: " + summary.failed)
  }

  def getSummary(driver: RemoteWebDriver): TestSummary = new TestSummary(driver)

  def printFailure(failure: JSModuleFailure) {
    log.error(failure.moduleName + " - " + failure.testName)
    for {
      test <- failure.failedTests
    } {
      log.error("\t" + test.message)
      log.error("\t\tExpected: " + test.expected + ", Received: " + test.received)
      log.error("\t\tSource -> " + test.source)
      log.info("")
    }
  }

  implicit def getErrors(errors:Seq[Option[String]]): Option[String] =  {
    errors.filter(_.isDefined) match {
      case Nil => None
      case x::xs => errors.foldLeft(x)(addOption[String](_, _))
    }
  }

  def stringAdd(sep:String)(str1:String, str2:String): String = str1 + sep + str2

  implicit def stringsOnNewLines: (String, String) => String = stringAdd(getLineSeparator)

  def getLineSeparator: String = runSafelyWithDefault(System.getProperty("line.separator"))(_ => "\n")

  def addOption[T](op1:Option[T], op2:Option[T])(implicit f:(T, T) => T): Option[T] = {
    (op1, op2) match {
      case (None, None) => None
      case (None, Some(v)) => Some(v)
      case (Some(v), None) => Some(v)
      case (Some(v1), Some(v2)) => Some(f(v1, v2))
    }
  }


  def stringToOption(str:String): Option[String] = if (str.isEmpty) None else Some(str)

  def reduceString(strings:Seq[String], sep:String): String =  strings mkString (sep)

}