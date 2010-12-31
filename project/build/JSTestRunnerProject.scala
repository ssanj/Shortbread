package ssahayam
import sbt._

class JSTestRunnerProject(info:ProjectInfo) extends PluginProject(info) {
   lazy val selenium = "org.seleniumhq.webdriver" % "webdriver-firefox" % "0.9.7376"
}

