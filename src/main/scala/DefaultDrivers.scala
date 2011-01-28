/*
 * Copyright 2010 Sanjiv Sahayam
 * Licensed under the Apache License, Version 2.0
 */

package shortbread
import org.openqa.selenium.chrome.ChromeDriver
import org.openqa.selenium.firefox.FirefoxDriver
import org.openqa.selenium.firefox.internal.ProfilesIni
import org.openqa.selenium.ie.InternetExplorerDriver

object DefaultDrivers {

  object DefaultChromeConfig extends DefaultConfig {
    override def webDriver = NamedDriver("Chrome", withTimeouts(() => new ChromeDriver))
  }

  object DefaultFoxConfig extends DefaultConfig {

    val profile = "default"

    override def webDriver = NamedDriver("Firefox", withTimeouts(() => new FirefoxDriver(new ProfilesIni().getProfile(profile))))
  }

  object DefaultIEConfig extends DefaultConfig {
    override def webDriver = NamedDriver("InternetExplorer", withTimeouts(() => new InternetExplorerDriver()))
  }
}
