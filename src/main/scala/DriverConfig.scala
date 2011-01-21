/*
 * Copyright 2010 Sanjiv Sahayam
 * Licensed under the Apache License, Version 2.0
 */
package ssahayam

import org.openqa.selenium.remote.RemoteWebDriver
import java.util.concurrent.TimeUnit

trait DriverConfig extends TimeoutSupport {

  val profile:String
  val implicitTimeout:Tuple2[Long, TimeUnit]
  val scriptTimeout:Tuple2[Long, TimeUnit]
  def driver:NamedDriver

  def setPageTimeout(rd:RemoteWebDriver) { setTimeout(rd)((d, t) => d.manage.timeouts.implicitlyWait(t._1, t._2))(implicitTimeout) }

  def setScriptTimeout(rd:RemoteWebDriver) { setTimeout(rd)((d, t) => d.manage.timeouts.setScriptTimeout(t._1, t._2))(scriptTimeout) }
}

trait TimeoutSupport {

  def setTimeout(driver:RemoteWebDriver)(t:(RemoteWebDriver, Tuple2[Long, TimeUnit]) => Unit): (Tuple2[Long, TimeUnit]) => Unit = {
    timeout => t(driver, timeout)
  }
}

//f() is a side-effecting function that launches a browser/driver.
case class NamedDriver(name:String, f: () => RemoteWebDriver)

object DefaultFoxConfig extends DriverConfig with TimeoutSupport {

  import java.util.concurrent.TimeUnit._
  import org.openqa.selenium.firefox.FirefoxDriver
  import org.openqa.selenium.firefox.internal.ProfilesIni

  override lazy val profile = "default"
  override lazy val implicitTimeout = (10L, SECONDS)
  override lazy val scriptTimeout = (20L, SECONDS)

  override def driver = NamedDriver("Firefox", () => {
    val ffd = new FirefoxDriver(new ProfilesIni().getProfile(profile))
    setPageTimeout(ffd)
    setScriptTimeout(ffd)
    ffd
  })
}

object DefaultChromeConfig extends DriverConfig with TimeoutSupport {
  import java.util.concurrent.TimeUnit._
  override lazy val profile = "default"
  override lazy val implicitTimeout = (10L, SECONDS)
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

