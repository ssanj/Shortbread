/*
 * Copyright 2010 Sanjiv Sahayam
 * Licensed under the Apache License, Version 2.0
 */
package ssahayam

import org.openqa.selenium.remote.RemoteWebDriver
import java.util.concurrent.TimeUnit

//TODO: 1. MOve this into an IO package
trait DriverConfig extends PluginSupport {

  type Timeout = (Long, TimeUnit)

  val profile:String
  val pageTimeout:Option[Timeout]
  val scriptTimeout:Option[Timeout]
  val useDefaultTimeout:Option[Timeout] = None
  def webDriver:NamedDriver

  def setPageTimeout: (RemoteWebDriver) => Unit = {
    driver => setTimeout(pageTimeout)(t => driver.manage.timeouts.implicitlyWait(t._1, t._2))
  }

  def setScriptTimeout: (RemoteWebDriver) => Unit = {
    driver => setTimeout(scriptTimeout)(t => driver.manage.timeouts.setScriptTimeout(t._1, t._2))
  }

  def setTimeout(timeout:Option[Timeout])(f:(Timeout) => Unit) { runSafelyWithOptionReturnError(timeout.map(f)) }
}

//f() is a side-effecting function that launches a browser/driver.
case class NamedDriver(name:String, f: () => RemoteWebDriver)

object DefaultFoxConfig extends DriverConfig {

  import java.util.concurrent.TimeUnit._
  import org.openqa.selenium.firefox.FirefoxDriver
  import org.openqa.selenium.firefox.internal.ProfilesIni

  override lazy val profile = "default"
  override lazy val pageTimeout = useDefaultTimeout
  override lazy val scriptTimeout = Some(5L, SECONDS)

  override def webDriver = NamedDriver("Firefox", () => {
    val ffd = new FirefoxDriver(new ProfilesIni().getProfile(profile))
    setPageTimeout(ffd)
    setScriptTimeout(ffd)
    ffd
  })
}

object DefaultChromeConfig extends DriverConfig {
  import java.util.concurrent.TimeUnit._
  override lazy val profile = "default"
  override lazy val pageTimeout = useDefaultTimeout
  override lazy val scriptTimeout = Some(5L, SECONDS)

  override def webDriver = NamedDriver("Chrome", () =>
    {
      import org.openqa.selenium.chrome.ChromeDriver
      val chrome = new ChromeDriver
      setPageTimeout(chrome)
      setScriptTimeout(chrome)
      chrome
  })

}