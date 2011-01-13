/*
 * Copyright 2010 Sanjiv Sahayam
 * Licensed under the Apache License, Version 2.0
 */

package ssahayam

import sbt._

trait JSTestRunnerPlugin extends DefaultWebProject with PluginSupport {

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

  case class NamedDriver(name:String, f:() => RemoteWebDriver)

  lazy val testJs = task{ runTestScripts } describedAs ("Runs Qunit javascript tests")

  def runTestScripts: Option[String] = {
    log.info("Running scripts from: " + testScriptPath.getPaths.mkString)

    driverSeq.map {
      nd => log.info("Running tests on: " + nd.name)
      runSafely(runBrowser)(nd.f.apply).toLeftOption
    } filter(_.isDefined) firstOption match {
      case Some(error) => error
      case _ => None
    }
  }

  def runBrowser: (RemoteWebDriver) => Option[String] = {
    driver => {
      val result = jstRunner.loadFiles(log, scriptFileSet.getPaths.toSeq, driver)
      close(driver)
      result
    }
  }

  def close(driver: RemoteWebDriver) { if (quitOnExit) driver.quit }
}
