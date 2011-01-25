package shortbread

trait SideEffects { this:PluginSupport =>

  import org.openqa.selenium.remote.RemoteWebDriver

  class JavaScriptTestFailedException extends RuntimeException("There were test failures")

  def close: Boolean => RemoteWebDriver => Unit = exit => driver => if (exit) driver.quit

  def open: NamedDriver => RemoteWebDriver = nd => nd.f.apply

  def loadPage: String => RemoteWebDriver => Unit = url => driver => driver.get(url)

  def failOnTestError: Boolean => Unit = {
    b => if (b) throw new JavaScriptTestFailedException else {}
  }

  implicit def stringsOnNewLines: (String, String) => String = stringAdd(separator)

  val separator: String = getLineSeparator(System.getProperty("line.separator"))

  def getLineSeparator: ( => String) => String = f => getStringOrDefault(f)("\n")
}