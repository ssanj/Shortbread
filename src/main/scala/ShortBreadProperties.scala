/*
 * Copyright 2010 Sanjiv Sahayam
 * Licensed under the Apache License, Version 2.0
 */
package shortbread

import sbt.{DefaultWebProject, PathFinder, Path}
import DefaultDrivers._

trait ShortBreadProperties { this:DefaultWebProject =>

  def scriptDirectoryName = "scripts"

  def testScriptPath: Path = sourceDirectoryName / testDirectoryName / webappDirectoryName / scriptDirectoryName

  //files that contain the javascript tests
  def scriptFiles = "*.html"

  def scriptFileSet: PathFinder = testScriptPath ** scriptFiles

  //should the browsers exit at the end of the tests
  def exitOnCompletion = true

  //web drivers to use.
  def driverSeq:Seq[NamedDriver] = Seq(DefaultFoxConfig.webDriver, DefaultChromeConfig.webDriver)

  lazy val shob = task{ runTestScripts } describedAs ("Runs shortbread plugin for javascript tests")

  def runTestScripts: Option[String]

  def getUrls: Seq[String] = scriptFileSet.getPaths.map("file://" + _).toSeq
}