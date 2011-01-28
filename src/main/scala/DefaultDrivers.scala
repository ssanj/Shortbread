/*
 * Copyright 2010 Sanjiv Sahayam
 * Licensed under the Apache License, Version 2.0
 */

package shortbread
import DriverConfig._
import java.util.concurrent.TimeUnit._

object DefaultDrivers {

  object DefaultChromeConfig extends DriverConfig {

    override lazy val profile = "default"
    override lazy val scriptTimeout:Option[Timeout] = Some(5L, SECONDS)

    override def webDriver = NamedDriver("Chrome", () => {
        import org.openqa.selenium.chrome.ChromeDriver
        val chrome = new ChromeDriver
        setPageTimeout(chrome)
        setScriptTimeout(chrome)
        chrome
    })
  }

  object DefaultFoxConfig extends DriverConfig {

    override lazy val profile = "default"
    override lazy val scriptTimeout:Option[Timeout] = Some(5L, SECONDS)

    override def webDriver = NamedDriver("Firefox", () => {
      import org.openqa.selenium.firefox.FirefoxDriver
      import org.openqa.selenium.firefox.internal.ProfilesIni

      val ffd = new FirefoxDriver(new ProfilesIni().getProfile(profile))
      setPageTimeout(ffd)
      setScriptTimeout(ffd)
      ffd
    })
  }

  object DefaultIEConfig extends DriverConfig {
    import org.openqa.selenium.ie.InternetExplorerDriver

    override lazy val profile = "default"
    override def webDriver = NamedDriver("InternetExplorer", () => {
      val ied = new InternetExplorerDriver()
      setPageTimeout(ied)
      setScriptTimeout(ied)
      ied
    })
  }
}
