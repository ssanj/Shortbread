/*
 * Copyright 2010 Sanjiv Sahayam
 * Licensed under the Apache License, Version 2.0
 */

package shortbread

import sbt._
import org.openqa.selenium.remote.RemoteWebDriver
import scala.Option

trait ShortbreadPlugin extends ShortBreadProperties with PluginSupport with ConsolePrinter { this:DefaultWebProject =>

  //IO context where all things IO are run.
  override def runTestScripts: Option[String] = {
    printScriptLocation(testScriptPath)

    val pages:Seq[(RemoteWebDriver) => Unit] = getUrls map (loadPage(_))
    driverSeq.map(nd => runSafelyWithResource[RemoteWebDriver, Unit, Unit]{
     driver => {
       printDriver(nd.name)
       pages.map { p =>
         p(driver)
         val summary = getSummary(driver)
         printResults(summary)
         failOnTestError { summary.hasFailures }
       }
     }}{open(nd)}{close(exitOnCompletion)})
  }
}
