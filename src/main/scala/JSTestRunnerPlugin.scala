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

  case class NamedDriver(name:String, f: () => RemoteWebDriver)

  lazy val testJs = task{ runTestScripts } describedAs ("Runs Qunit javascript tests")

  //IO context.
  def runTestScripts: Option[String] = {
    log.info("Running scripts from: " + testScriptPath.getPaths.mkString)
    None
//    for {
//      nd <- driverSeq
//      driver = nd.f.apply
//      url <- getUrls
//    } {
//      runSafely {
//        log.info("Loading browser: " + nd.name)
//
//        log.info("Running tests on: " + url)
//        getPage(driver)(url)
//        close(driver)
//        print(new JSRunner(driver))
//      }.toLeftOption
//    }
  }

  def print(runner:JSRunner) {
    runner.summary
  }

  def getPage(driver: RemoteWebDriver)(url:String) { driver.get(url)  }

  def getUrls: Seq[String] = scriptFileSet.getPaths.map("file://" + _).toSeq

  //def getErrors(errors:Seq[Option[String]]): Option[String] =  stringToOption(errors flatten map (_.trim) reduceString)

  def stringToOption(str:String): Option[String] = if (str.isEmpty) None else Some(str)

  def reduceString(strings:Seq[String], sep:String): String =  strings mkString (sep)

//  def runBrowser: (RemoteWebDriver) => Option[String] = {
//    driver => {
//      val result = jstRunner.loadFiles(log, scriptFileSet.getPaths.toSeq, driver)
//      close(driver)
//      result
//    }
//  }

  def close(driver: RemoteWebDriver) { if (quitOnExit) driver.quit }
}
