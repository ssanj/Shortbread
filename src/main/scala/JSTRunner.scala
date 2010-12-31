/*
 * Copyright 2010 Sanjiv Sahayam
 * Licensed under the Apache License, Version 2.0
 */
package ssahayam

import org.openqa.selenium.{By}
import org.openqa.selenium.firefox.FirefoxDriver
import sbt.Logger

class JSTRunner(testFile:String, log: sbt.Logger) {
  val driver = new FirefoxDriver();
  runWith(lookupStatus)(driver.quit)

  def lookupStatus() {
    driver.get(testFile)
    val testResult = driver.findElement(By.xpath("//p[@id='qunit-testresult']"))
    val failed = testResult.findElement(By.xpath("span[@class='failed']")).getText
    val passed = testResult.findElement(By.xpath("span[@class='passed']")).getText
    val total = testResult.findElement(By.xpath("span[@class='total']")).getText
    log.info(testFile + " -> total: " + total + ", passed: " + passed + ", failed: " + failed)
  }

  def runWith(f1: => Any)(f2: => Any) {
    try {
      f1
    } catch {
      case _ =>
    } finally {
      f2
    }
  }

}