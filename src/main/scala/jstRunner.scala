/*
 * Copyright 2010 Sanjiv Sahayam
 * Licensed under the Apache License, Version 2.0
 */
package ssahayam

import org.openqa.selenium.{WebDriver, By}

object jstRunner {

  def loadFiles(log: sbt.Logger, files:Seq[String], driver:WebDriver): Option[String] = {
      import jsTestExecutor._
      val init:Either[String, Unit] = Right()
      files.foldLeft(init)((a:Either[String, Unit], b:String) => a.right.flatMap(_ => execute(driver, b, log))).left.toOption
  }
}

object jsTestExecutor {
  def execute(driver:WebDriver, file:String, log:sbt.Logger): Either[String, Unit] = {
    val uri = "file://" + file
    driver.get(uri)
    log.info("loaded -> " + uri)
    val testResult = driver.findElement(By.xpath("//p[@id='qunit-testresult']"))
    val failed = testResult.findElement(By.xpath("span[@class='failed']")).getText
    val passed = testResult.findElement(By.xpath("span[@class='passed']")).getText
    val total = testResult.findElement(By.xpath("span[@class='total']")).getText
    log.info(file + " -> total: " + total + ", passed: " + passed + ", failed: " + failed)
    if (failed != "0") Left("There were test failures") else Right()
  }
}