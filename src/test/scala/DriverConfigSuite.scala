/*
 * Copyright 2010 Sanjiv Sahayam
 * Licensed under the Apache License, Version 2.0
 */

package shortbread

import org.scalatest.mock.MockitoSugar
import org.scalatest.FunSuite
import org.scalatest.matchers.ShouldMatchers
import org.openqa.selenium.remote.RemoteWebDriver
import org.mockito.Mockito._
import DriverConfig._

final class DriverConfigSuite extends FunSuite with ShouldMatchers with MockitoSugar {

  import java.util.concurrent.TimeUnit
  val timeout:Timeout = (100L, TimeUnit.MILLISECONDS)

  test("DriverConfig should have default values for timeouts") {
    val mockDriver = mock[RemoteWebDriver]
    val config = new TestDriverConfigWithDefaults(mockDriver)

    config.pageTimeout should equal (None)
    config.scriptTimeout should equal (None)
    config.profile should equal ("with defaults profile")

    val webDriver = config.webDriver
    webDriver.name should equal ("with defaults driver")
    webDriver.f.apply should be theSameInstanceAs (mockDriver)
    verifyZeroInteractions(mockDriver)
  }

  test("setTimeout should use default values for timeouts if not supplied") {
    val mockDriver = mock[RemoteWebDriver]
    val config = new TestDriverConfigWithDefaults(mockDriver)
    setTimeout(None)(t => mockDriver.close)
    verifyZeroInteractions(mockDriver)
  }

  test("setTimeout should use default values for timeouts on error") {
    val mockDriver = mock[RemoteWebDriver]
    setTimeout(Some(timeout))(t => throw new RuntimeException("Could not find timeout"))
    verifyZeroInteractions(mockDriver)
  }

  test("setTimeout should use supplied values for timeouts if valid") {
    import org.openqa.selenium.WebDriver._
    val mockDriver = mock[RemoteWebDriver]
    val mockTimeouts = mock[Timeouts]
    val mockTimeoutsChanged = mock[Timeouts]
    val mockOptions = mock[Options]

    when(mockDriver.manage).thenReturn(mockOptions)
    when(mockOptions.timeouts).thenReturn(mockTimeouts)
    when(mockTimeouts.implicitlyWait(timeout._1, timeout._2)).thenReturn(mockTimeoutsChanged)

    setTimeout(Some(timeout))(t => mockDriver.manage.timeouts.implicitlyWait(t._1, t._2))

    verify(mockDriver).manage
    verify(mockOptions).timeouts
    verify(mockTimeouts).implicitlyWait(timeout._1, timeout._2)
  }

  final class TestDriverConfigWithDefaults(mockDriver:RemoteWebDriver) extends DriverConfig {
    override val profile:String = "with defaults profile"
    def webDriver = new NamedDriver("with defaults driver", () => mockDriver)
  }
}