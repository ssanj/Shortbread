/*
 * Copyright 2010 Sanjiv Sahayam
 * Licensed under the Apache License, Version 2.0
 */

package ssahayam

import sbt._

trait JSTestRunnerPlugin extends DefaultWebProject with PluginSupport with ConsolePrinter {

  import org.openqa.selenium.remote.RemoteWebDriver
  import org.openqa.selenium.firefox.internal.ProfilesIni
  import org.openqa.selenium.firefox.FirefoxDriver
  import org.openqa.selenium.chrome.ChromeDriver
  import scala.Option

  def scriptDirectoryName = "scripts"

  //def testScriptPath: Path = testSourcePath / scriptDirectoryName
  def testScriptPath: Path = "src" / "test" / "webapp" / scriptDirectoryName

  def scriptFiles = "*.html"

  def scriptFileSet: PathFinder = testScriptPath ** scriptFiles

  def quitOnExit = false

  def driverSeq:Seq[NamedDriver] = Seq(firefoxDriver, chromeDriver)

  lazy val foxyProfile = "default"

  def firefoxDriver = NamedDriver("Firefox", () => new FirefoxDriver(new ProfilesIni().getProfile(foxyProfile)))

  def chromeDriver = NamedDriver("Chrome", () => new ChromeDriver)

  case class NamedDriver(name:String, f: () => RemoteWebDriver)

  lazy val testJs = task{ runTestScripts } describedAs ("Runs Qunit javascript tests")

  //IO context.
  def runTestScripts: Option[String] = {
    log.info("Running scripts from: " + testScriptPath.getPaths.mkString)

    val pages:Seq[(RemoteWebDriver) => Unit] = getUrls map (getPage(_))
    driverSeq.flatMap(nd => pages.map(page => runSafelyWithResource[RemoteWebDriver, Unit, Unit]{
     driver => {
       page(driver)
       printSummary(populateResults(driver))
     }}{nd.f.apply}{close}))
  }

  def populateResults(driver: RemoteWebDriver): TestSummary = new TestSummary(driver)

  def getPage(url:String)(driver: RemoteWebDriver) { driver.get(url)  }

  def getUrls: Seq[String] = scriptFileSet.getPaths.map("file://" + _).toSeq

  def close(driver: RemoteWebDriver) { if (quitOnExit) driver.quit }
}
