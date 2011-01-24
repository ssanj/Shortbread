package shortbread

trait SideEffects {

  import org.openqa.selenium.remote.RemoteWebDriver

  class JavaScriptTestFailedException extends RuntimeException("There were test failures")

  def close(exit:Boolean)(driver: RemoteWebDriver) { if (exit) driver.quit }

  def open(nd: NamedDriver): RemoteWebDriver = nd.f.apply

  //Side-effecting function that loads a url in browser/driver
  def loadPage(url:String)(driver: RemoteWebDriver) { driver.get(url)  }
}