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
    val config = new TestDriverConfig(mockDriver)

    config.pageTimeout should equal (None)
    config.scriptTimeout should equal (None)

    val webDriver = config.webDriver
    webDriver.name should equal ("with defaults driver")
    webDriver.f.apply should be theSameInstanceAs (mockDriver)
    verifyZeroInteractions(mockDriver)
  }

  test("setTimeout should use default values for timeouts if not supplied") {
    val mockDriver = mock[RemoteWebDriver]
    val config = new TestDriverConfig(mockDriver)
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

  test("withTimeouts should not initialize the driver unless the namedDriver function is invoked") {
    val mockDriver = mock[RemoteWebDriver]
    val config = new TestDriverDefaultConfigWithTimeouts(mockDriver)
    config.webDriver //namedDriver is retrieved but not invoked
    verifyZeroInteractions(mockDriver)
  }

  test("withTimeouts should initialize the driver when the namedDriver function is invoked") {
    import org.openqa.selenium.WebDriver._
    import java.util.concurrent.TimeUnit._

    val mockDriver = mock[RemoteWebDriver]
    val mockTimeouts = mock[Timeouts]
    val mockTimeoutsUpdated = mock[Timeouts]
    val mockOptions = mock[Options]

    when(mockDriver.manage).thenReturn(mockOptions)
    when(mockOptions.timeouts).thenReturn(mockTimeouts)

    val config = new TestDriverDefaultConfigWithTimeouts(mockDriver)
    val nd = config.webDriver
    val driver = nd.f()

    driver should be theSameInstanceAs (mockDriver)

    verify(mockDriver, atMost(2)).manage
    verify(mockOptions, atMost(2)).timeouts
    verify(mockTimeouts).implicitlyWait(2L, SECONDS)
    verify(mockTimeouts).setScriptTimeout(300L, MILLISECONDS)
  }

  final class TestDriverConfig(mockDriver:RemoteWebDriver) extends DriverConfig {
    def webDriver = new NamedDriver("with defaults driver", () => mockDriver)
  }

  final class TestDriverDefaultConfigWithTimeouts(mockDriver:RemoteWebDriver) extends DefaultConfig {

    import java.util.concurrent.TimeUnit._

    override lazy val pageTimeout:Option[Timeout] = Some(2L, SECONDS)
    override lazy val scriptTimeout:Option[Timeout] = Some(300L, MILLISECONDS)

    def webDriver = new NamedDriver("with defaults driver", withTimeouts(() => mockDriver))
  }
}