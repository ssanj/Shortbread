/*
 * Copyright 2010 Sanjiv Sahayam
 * Licensed under the Apache License, Version 2.0
 */

package ssahayam

import sbt._

trait JSTestRunnerPlugin extends DefaultWebProject {

  import org.openqa.selenium.remote.RemoteWebDriver
  import org.openqa.selenium.firefox.internal.ProfilesIni
  import org.openqa.selenium.firefox.FirefoxDriver
  import org.openqa.selenium.chrome.ChromeDriver

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
      runDriver(nd.f.apply)
    } filter(_.isDefined) firstOption match {
      case Some(error) => error
      case _ => None
    }
  }

  def runDriver(driver: RemoteWebDriver): Option[String] =  runWithin(jstRunner.loadFiles(log, scriptFileSet.getPaths.toSeq, _), driver)(close)

  def runWithin(f1: (RemoteWebDriver) => Option[String], driver: RemoteWebDriver)(f2: (RemoteWebDriver) => Unit): Option[String] = {
    val tests:Either[String, Option[String]] = runSafely(f1(driver))
    val cleanup = runSafely(f2(driver)) // close the driver on success or failure

    tests match {
      case Left(ex) => Some(ex)
      case Right(Some(err)) => Some(err)
      case Right(None) => cleanup match {
        case Left(ex) => Some(ex)
        case Right(_) => None
      }
    }
  }

  def runSafely[T](f: => T): Either[String, T] = {
    try {
      Right(f)
    } catch {
      case error => Left(error.getMessage)
    }
  }

  def close(driver: RemoteWebDriver) {
    if (quitOnExit) driver.quit
  }
}
