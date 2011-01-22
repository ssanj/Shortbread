/*
 * Copyright 2010 Sanjiv Sahayam
 * Licensed under the Apache License, Version 2.0
 */
package ssahayam

import org.openqa.selenium.remote.RemoteWebDriver
import java.util.concurrent.TimeUnit

trait DriverConfig extends PluginSupport {

  type Timeout = (Long, TimeUnit)

  val profile:String
  val pageTimeout:Timeout
  val scriptTimeout:Timeout
  def driver:NamedDriver

  //Although each of the timeout functions may throw exception, since they run in an IO context they will be handled appropriately
  def setPageTimeout: (RemoteWebDriver) => Unit = { _.manage.timeouts.implicitlyWait(pageTimeout._1, pageTimeout._2) }

  def setScriptTimeout: (RemoteWebDriver) => Unit = { _.manage.timeouts.setScriptTimeout(scriptTimeout._1, scriptTimeout._2) }
}

//f() is a side-effecting function that launches a browser/driver.
case class NamedDriver(name:String, f: () => RemoteWebDriver)

object DefaultFoxConfig extends DriverConfig {

  import java.util.concurrent.TimeUnit._
  import org.openqa.selenium.firefox.FirefoxDriver
  import org.openqa.selenium.firefox.internal.ProfilesIni

  override lazy val profile = "default"
  override lazy val pageTimeout = (10L, SECONDS)
  override lazy val scriptTimeout = (20L, SECONDS)

  override def driver = NamedDriver("Firefox", () => {
    val ffd = new FirefoxDriver(new ProfilesIni().getProfile(profile))
    setPageTimeout(ffd)
    setScriptTimeout(ffd)
    ffd
  })
}

object DefaultChromeConfig extends DriverConfig {
  import java.util.concurrent.TimeUnit._
  override lazy val profile = "default"
  override lazy val pageTimeout = (10L, SECONDS)
  override lazy val scriptTimeout = (20L, SECONDS)

  override def driver = NamedDriver("Chrome", () =>
    {
      import org.openqa.selenium.chrome.ChromeDriver
      val chrome = new ChromeDriver
      setPageTimeout(chrome)
      setScriptTimeout(chrome)
      chrome
  })

}

