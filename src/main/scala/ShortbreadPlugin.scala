/*
 * Copyright 2010 Sanjiv Sahayam
 * Licensed under the Apache License, Version 2.0
 */

package shortbread

import sbt._
import org.openqa.selenium.remote.RemoteWebDriver
import scala.Option
import DefaultDrivers._

trait ShortbreadPlugin extends DefaultWebProject with PluginSupport with ConsolePrinter {

  def scriptDirectoryName = "scripts"

  //def testScriptPath: Path = testSourcePath / scriptDirectoryName
  def testScriptPath: Path = "src" / "test" / "webapp" / scriptDirectoryName

  def scriptFiles = "*.html"

  def scriptFileSet: PathFinder = testScriptPath ** scriptFiles

  def quitOnExit = false

  def driverSeq:Seq[NamedDriver] = Seq(DefaultFoxConfig.webDriver, DefaultChromeConfig.webDriver)

  lazy val shob = task{ runTestScripts } describedAs ("Runs shortbread plugin for javascript tests")

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
     }}{open(nd)}{close(quitOnExit)})
  }

  def getUrls: Seq[String] = scriptFileSet.getPaths.map("file://" + _).toSeq
}
