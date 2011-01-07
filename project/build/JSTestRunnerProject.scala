package ssahayam
import sbt._

class JSTestRunnerProject(info:ProjectInfo) extends PluginProject(info) {
   lazy val seleniumVersion = "2.0b1"
   lazy val seleniumFoxy = "org.seleniumhq.selenium" % "selenium-firefox-driver" % seleniumVersion withSources()
   lazy val seleniumChrome = "org.seleniumhq.selenium" % "selenium-chrome-driver" % seleniumVersion withSources()
}

