/*
 * Copyright 2010 Sanjiv Sahayam
 * Licensed under the Apache License, Version 2.0
 */
package ssahayam

import org.scalatest.mock.MockitoSugar
import org.scalatest.FunSuite
import org.scalatest.matchers.ShouldMatchers
import org.mockito.Mockito._
import org.openqa.selenium.remote.RemoteWebDriver
import org.openqa.selenium.By
import org.openqa.selenium.WebElement

//These tests are ugly. Make them more "functional".
final class SeleniumSupportSuite extends FunSuite with ShouldMatchers with MockitoSugar with SeleniumSupport {

  val xpath = "abc"

  import xpathy._
  test("getElement(driver) should return None if there are no elements for a specified path") {
    val mockDriver = mock[RemoteWebDriver]

    when(mockDriver.findElement(By.xpath(xpath))).thenThrow(new RuntimeException("element not found"))
    getElement(mockDriver, xpath) should equal (None)
    verify(mockDriver).findElement(By.xpath(xpath))
  }

  test("getElement(driver) should return Some(element) if there are elements for a specified path") {
    val mockDriver = mock[RemoteWebDriver]
    val mockElement = mock[WebElement]

    when(mockDriver.findElement(By.xpath(xpath))).thenReturn(mockElement)
    getElement(mockDriver, xpath) should equal (Some(mockElement))
    verify(mockDriver).findElement(By.xpath(xpath))
  }

  test("getElement(element) should return None if there are no elements for a specified path") {
    val mockElement = mock[WebElement]

    when(mockElement.findElement(By.xpath(xpath))).thenThrow(new RuntimeException("element not found"))
    getElement(mockElement, xpath) should equal (None)
    verify(mockElement).findElement(By.xpath(xpath))
  }

  test("getElement(element) should return Some(elemenet) if there are elements for a specified path") {
    val mockElement = mock[WebElement]
    val mockResult = mock[WebElement]

    when(mockElement.findElement(By.xpath(xpath))).thenReturn(mockResult)
    getElement(mockElement, xpath) should equal (Some(mockResult))
    verify(mockElement).findElement(By.xpath(xpath))
  }

  test("getElements(driver) should return Nil if there are no elements for a specified path") {
    val mockDriver = mock[RemoteWebDriver]

    when(mockDriver.findElements(By.xpath(xpath))).thenThrow(new RuntimeException("No elements found"))
    getElements(mockDriver, xpath) should equal (Seq[WebElement]())
    verify(mockDriver).findElements(By.xpath(xpath))
  }

  test("getElements(driver) should return the matched elements for a specified path") {
    val mockDriver = mock[RemoteWebDriver]
    val mockElement1 = mock[WebElement]
    val mockElement2 = mock[WebElement]

    import java.util.Arrays._
    when(mockDriver.findElements(By.xpath(xpath))).thenReturn(asList(mockElement1, mockElement2))
    val iterator = getElements(mockDriver, xpath).elements
    iterator.next should equal (mockElement1)
    iterator.next should equal (mockElement2)
    verify(mockDriver).findElements(By.xpath(xpath))
  }

  test("getElements(element) should return an empty Seq if there are no elements for a specified path") {
    val mockElement = mock[WebElement]

    when(mockElement.findElements(By.xpath(xpath))).thenThrow(new RuntimeException("No elements found"))
    getElements(mockElement, xpath) should equal (Seq.empty)
    verify(mockElement).findElements(By.xpath(xpath))
  }

  test("getElements(element) should return the matched elements for a specified path") {
    val mockParentElement = mock[WebElement]
    val mockElement1 = mock[WebElement]
    val mockElement2 = mock[WebElement]

    import java.util.Arrays._
    when(mockParentElement.findElements(By.xpath(xpath))).thenReturn(asList(mockElement1, mockElement2))
    val iterator = getElements(mockParentElement, xpath).elements
    iterator.next should equal (mockElement1)
    iterator.next should equal (mockElement2)
    verify(mockParentElement).findElements(By.xpath(xpath))
  }

  val defaultText = "N/A"
  test("getText (with defaults) should return 'N/A' with it is passed a None element") {
    getText(None, xpath) should equal (defaultText)
  }

  test("getText should return the supplied default with it is passed a None element") {
    getText(None, xpath)("something") should equal ("something")
  }

  test("getText should return the default text if the element supplied does not have the path specified") {
    val mockElement = mock[WebElement]

    when(mockElement.findElement(By.xpath(xpath))).thenThrow(new RuntimeException("Element not found"))
    getText(Some(mockElement), xpath) should equal (defaultText)
    verify(mockElement).findElement(By.xpath(xpath))
  }
  test("getText should return Some(text) if the element supplied has the path specified") {
    val mockParentElement = mock[WebElement]
    val mockElement = mock[WebElement]
    val text = "Texty"

    when(mockParentElement.findElement(By.xpath(xpath))).thenReturn(mockElement)
    when(mockElement.getText).thenReturn(text)
    getText(Some(mockParentElement), xpath) should equal (text)
    verify(mockParentElement).findElement(By.xpath(xpath))
    verify(mockElement).getText
  }

  test("getText should return the default if the element supplied fails on getText") {
    val mockParentElement = mock[WebElement]
    val mockElement = mock[WebElement]

    when(mockParentElement.findElement(By.xpath(xpath))).thenReturn(mockElement)
    when(mockElement.getText).thenThrow(new RuntimeException("No text for you"))
    getText(Some(mockParentElement), xpath) should equal (defaultText)
    verify(mockParentElement).findElement(By.xpath(xpath))
    verify(mockElement).getText
  }

}