/*
 * Copyright 2010 Sanjiv Sahayam
 * Licensed under the Apache License, Version 2.0
 */

package ssahayam
import sbt._

trait JSTestRunnerPlugin extends Project with MavenStyleWebScalaPaths {
   lazy val scriptPath = testSourcePath / webappDirectoryName / "scripts"
   def testFileFinder:PathFinder = scriptPath ** "*.html" //kept this dynamic so that it can automatically pick up new files.
   def testSet:scala.collection.Set[String] = testFileFinder.getPaths
   //default exlcudes?

   lazy val jstest = task { testSet.map(new JSTRunner(_, log)); None }
}
