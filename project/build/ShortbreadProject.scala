package shortbread
import sbt._

class ShortbreadProject(info:ProjectInfo) extends PluginProject(info) {
   lazy val seleniumVersion = "2.0b1"
   lazy val seleniumFoxy = "org.seleniumhq.selenium" % "selenium-firefox-driver" % seleniumVersion % "compile->default" withSources()
   lazy val seleniumChrome = "org.seleniumhq.selenium" % "selenium-chrome-driver" % seleniumVersion  % "compile->default" withSources()
   lazy val mockito = "org.mockito" % "mockito-all" %  "1.8.5" % "test->default" withSources()
   lazy val scalatest = "org.scalatest" % "scalatest" % "1.1" % "test->default" withSources()
}

