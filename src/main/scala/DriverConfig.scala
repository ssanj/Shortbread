/*
 * Copyright 2010 Sanjiv Sahayam
 * Licensed under the Apache License, Version 2.0
 */

package shortbread

import org.openqa.selenium.remote.RemoteWebDriver
import java.util.concurrent.TimeUnit
import java.util.concurrent.TimeUnit._
import DriverConfig._

trait DriverConfig {

  lazy val pageTimeout:Option[Timeout] = None   //use the default
  lazy val scriptTimeout:Option[Timeout] = None //use the default
  def webDriver:NamedDriver

  def setPageTimeout: (RemoteWebDriver) => Unit = {
    driver => setTimeout(pageTimeout)(t => driver.manage.timeouts.implicitlyWait(t._1, t._2))
  }

  def setScriptTimeout: (RemoteWebDriver) => Unit = {
    driver => setTimeout(scriptTimeout)(t => driver.manage.timeouts.setScriptTimeout(t._1, t._2))
  }
}

trait DefaultConfig extends DriverConfig {

  override lazy val scriptTimeout:Option[Timeout] = Some(5L, SECONDS)

  def init: (RemoteWebDriver => RemoteWebDriver) => (() => RemoteWebDriver) => (() => RemoteWebDriver) = f => c => () => f(c.apply)

  def withTimeouts: (() => RemoteWebDriver) => (() => RemoteWebDriver) =
    init { driver =>
      setPageTimeout(driver)
      setScriptTimeout(driver)
      driver
    }
}

object DriverConfig extends PluginSupport {

  type Timeout = (Long, TimeUnit)

  def setTimeout(timeout:Option[Timeout])(f:(Timeout) => Unit) { runSafelyWithOptionReturnError(timeout.map(f)) }
}

//f() is a side-effecting function that launches a browser/driver.
case class NamedDriver(name:String, f: () => RemoteWebDriver)