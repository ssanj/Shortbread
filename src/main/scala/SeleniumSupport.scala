package ssahayam

/*
 * Copyright 2010 Sanjiv Sahayam
 * Licensed under the Apache License, Version 2.0
 */
trait SeleniumSupport {
  object xpathy {
    import org.openqa.selenium.{WebElement, By}
    import org.openqa.selenium.remote.RemoteWebDriver
    import scala.collection.jcl.BufferWrapper

    implicit def javaList2Seq[T](javaList: java.util.List[T]) : Seq[T] = new BufferWrapper[T]() { def underlying = javaList }

    implicit val defaultString:String = "N/A"

    def getText(element: WebElement, xpath: String)(implicit default:String): String = {
      try {
        getElement(element, xpath).getText
      } catch {
        case _ => default
      }
    }

    def getElement(element: WebElement, xpath: String): WebElement = element.findElement(By.xpath(xpath))

    def getElement(driver: RemoteWebDriver, xpath: String): WebElement = driver.findElement(By.xpath(xpath))

    def getElements(element: WebElement, xpath: String): Seq[WebElement] = element.findElements(By.xpath(xpath))

    def getElements(driver: RemoteWebDriver, xpath: String): Seq[WebElement] = driver.findElements(By.xpath(xpath))

  }

}