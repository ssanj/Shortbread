/*
 * Copyright 2010 Sanjiv Sahayam
 * Licensed under the Apache License, Version 2.0
 */
package ssahayam

import org.openqa.selenium.remote.RemoteWebDriver

object jstRunner {

  def loadFiles(log: sbt.Logger, files:Seq[String], driver:RemoteWebDriver): Option[String] = {
      import jsTestExecutor._
      val init:Either[String, Unit] = Right()
      files.foldLeft(init)((a:Either[String, Unit], b:String) => a.right.flatMap(_ => execute(driver, b, log))).left.toOption
  }
}

object jsTestExecutor {

  def execute(driver:RemoteWebDriver, file:String, log:sbt.Logger): Either[String, Unit] = {
    import org.openqa.selenium.By
    import java.io.File

    val physicalFile = new File(file)

    def runTests:Either[String, Unit] = {
      val uri = "file://" + file
      log.info("Running tests in " + physicalFile.getName)
      driver.get(uri)
      val testResult = driver.findElement(By.xpath("//p[@id='qunit-testresult']"))
      val failed = testResult.findElement(By.xpath("span[@class='failed']")).getText
      val passed = testResult.findElement(By.xpath("span[@class='passed']")).getText
      val total = testResult.findElement(By.xpath("span[@class='total']")).getText
      val testSummary = "total: " + total + ", passed: " + passed + ", failed: " + failed
      if (failed != "0") {
        log.error(testSummary)
        Left("There were test failures")
      } else {
        log.info(testSummary)
        Right()
      }
    }

   if (physicalFile.canRead) runTests else Left("Can't read supplied file: " + physicalFile.getAbsolutePath)
  }
 }