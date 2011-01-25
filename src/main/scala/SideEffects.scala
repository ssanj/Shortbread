package shortbread

trait SideEffects extends PluginSupport {

  import org.openqa.selenium.remote.RemoteWebDriver

  class JavaScriptTestFailedException extends RuntimeException("There were test failures")

  def close(exit:Boolean)(driver: RemoteWebDriver) { if (exit) driver.quit }

  def open(nd: NamedDriver): RemoteWebDriver = nd.f.apply

  //Side-effecting function that loads a url in browser/driver
  def loadPage(url:String)(driver: RemoteWebDriver) { driver.get(url)  }

  def failOnTestError(summary:TestSummary) {
    if (summary.hasFailures) throw new JavaScriptTestFailedException else {}
  }

  //TODO - test
  implicit def stringsOnNewLines: (String, String) => String = stringAdd(getLineSeparator)

  //TODO - test
  val getLineSeparator: String = runSafelyWithDefault(System.getProperty("line.separator"))(_ => "\n")
}