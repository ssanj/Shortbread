/*
 * Copyright 2010 Sanjiv Sahayam
 * Licensed under the Apache License, Version 2.0
 */

package ssahayam

import sbt._
import org.openqa.selenium.remote.RemoteWebDriver
import org.openqa.selenium.firefox.internal.ProfilesIni
import org.openqa.selenium.firefox.FirefoxDriver
import org.openqa.selenium.chrome.ChromeDriver
import scala.Option

//clean this code up. Maybe extra another trait for setting up the drivers etc.
trait JSTestRunnerPlugin extends DefaultWebProject with PluginSupport with ConsolePrinter {
  import FoxConfig._

  def scriptDirectoryName = "scripts"

  //def testScriptPath: Path = testSourcePath / scriptDirectoryName
  def testScriptPath: Path = "src" / "test" / "webapp" / scriptDirectoryName

  def scriptFiles = "*.html"

  def scriptFileSet: PathFinder = testScriptPath ** scriptFiles

  def quitOnExit = false

  def driverSeq:Seq[NamedDriver] = Seq(firefoxDriver, chromeDriver)

  object FoxConfig {

    import java.util.concurrent.TimeUnit
    import java.util.concurrent.TimeUnit._

    lazy val foxProfile = "default"
    lazy val timeout:Long = 10
    lazy val timeUnit:TimeUnit = SECONDS
  }

  def firefoxDriver = NamedDriver("Firefox", () => new FirefoxDriver(new ProfilesIni().getProfile(foxProfile)))

  def chromeDriver = NamedDriver("Chrome", () =>
    {
      val chrome = new ChromeDriver
      chrome.manage.timeouts.implicitlyWait(timeout, timeUnit)
      chrome
    })

  //f() is a side-effecting function that launches a browser/driver.
  case class NamedDriver(name:String, f: () => RemoteWebDriver)

  lazy val testJs = task{ runTestScripts } describedAs ("Runs javascript tests")

  //IO context where all things IO are run.
  def runTestScripts: Option[String] = {
    printScriptLocation(testScriptPath)

    val pages:Seq[(RemoteWebDriver) => Unit] = getUrls map (loadPage(_))
    driverSeq.map(nd => runSafelyWithResource[RemoteWebDriver, Unit, Unit]{
     driver => {
       printDriver(nd.name)
       pages.map { p =>
         p(driver)
         val summary = getSummary(driver)
         printResults(summary)
         failOnTestError(summary)
       }
     }}{open(nd)}{close})
  }

  //Side-effecting function that loads a url in browser/driver
  def loadPage(url:String)(driver: RemoteWebDriver) { driver.get(url)  }

  def getUrls: Seq[String] = scriptFileSet.getPaths.map("file://" + _).toSeq

  def close(driver: RemoteWebDriver) { if (quitOnExit) driver.quit }

  def open(nd: NamedDriver): RemoteWebDriver = nd.f.apply
}
