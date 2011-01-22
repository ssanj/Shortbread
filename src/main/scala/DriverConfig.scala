/*
 * Copyright 2010 Sanjiv Sahayam
 * Licensed under the Apache License, Version 2.0
 */
package ssahayam

import org.openqa.selenium.remote.RemoteWebDriver
import java.util.concurrent.TimeUnit
import java.util.concurrent.TimeUnit._

trait DriverConfig extends PluginSupport {

  type Timeout = (Long, TimeUnit)

  val profile:String
  val pageTimeout:Timeout
  val scriptTimeout:Timeout
  def driver:NamedDriver

  //Although each of the timeout functions may throw exception, since they run in an IO context they will be handled appropriately
  def setPageTimeout: (RemoteWebDriver) => Unit = { setTimeout(_)(pf)(pageTimeout) }

  def setScriptTimeout: (RemoteWebDriver) => Unit = { setTimeout(_)(sf)(scriptTimeout) }

  def pf(driver:RemoteWebDriver, t:Timeout): Unit = driver.manage.timeouts.implicitlyWait(t._1, t._2)

  def sf(driver:RemoteWebDriver, t:Timeout): Unit = driver.manage.timeouts.setScriptTimeout(t._1, t._2)

  def setTimeout(driver:RemoteWebDriver): ((RemoteWebDriver, Timeout) => Unit) => Timeout => Unit = f => t => f(driver, t) 
}

//f() is a side-effecting function that launches a browser/driver.
case class NamedDriver(name:String, f: () => RemoteWebDriver)

object DefaultFoxConfig extends DriverConfig {

  override lazy val profile = "default"
  override lazy val pageTimeout = (5L, SECONDS)
  override lazy val scriptTimeout = (5L, SECONDS)

  override def driver = NamedDriver("Firefox", () => {
    import org.openqa.selenium.firefox.FirefoxDriver
    import org.openqa.selenium.firefox.internal.ProfilesIni
  
    val ffd = new FirefoxDriver(new ProfilesIni().getProfile(profile))
    setPageTimeout(ffd)
    setScriptTimeout(ffd)
    ffd
  })
}

object DefaultChromeConfig extends DriverConfig {

  override lazy val profile = "default"
  override lazy val pageTimeout = (5L, SECONDS)
  override lazy val scriptTimeout = (5L, SECONDS)

  override def driver = NamedDriver("Chrome", () => {
     import org.openqa.selenium.chrome.ChromeDriver
 
     val chrome = new ChromeDriver
     setPageTimeout(chrome)
     setScriptTimeout(chrome)
     chrome
  })
}

