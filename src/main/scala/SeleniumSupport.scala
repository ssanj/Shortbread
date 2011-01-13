package ssahayam

/*
 * Copyright 2010 Sanjiv Sahayam
 * Licensed under the Apache License, Version 2.0
 */
trait SeleniumSupport extends PluginSupport {
  object xpathy {
    import org.openqa.selenium.{WebElement, By}
    import org.openqa.selenium.remote.RemoteWebDriver
    import scala.collection.jcl.BufferWrapper

    implicit def javaList2Seq[T](javaList: java.util.List[T]) : Seq[T] = new BufferWrapper[T]() { def underlying = javaList }

    implicit val defaultString:String = "N/A"

    //We can refactor this
    // (WebElement) => String => String
    def getText(element: WebElement, xpath: String)(implicit default:String): String = {
      getElement(element, xpath) match {
        case Some(value) => value.getText
        case None => default
      }
    }

    //(WebElement) => Option[WebElement]
    //(Option[WebElement) => String => String
    def getText(element:Option[WebElement], xpath: String)(implicit default:String): String = {
      element match {
        case Some(value) => getText(value, xpath)(default)
        case None => default
      }
    }

    def getElement(element: WebElement, xpath: String): Option[WebElement] =  runSafelyOption(element.findElement(By.xpath(xpath)))// return Option

    def getElement(driver: RemoteWebDriver, xpath: String): Option[WebElement] = runSafelyOption(driver.findElement(By.xpath(xpath))) //return Option

    def getElements(element: WebElement, xpath: String): Seq[WebElement] =  runSafelySeq(element.findElements(By.xpath(xpath)))

    def getElements(driver: RemoteWebDriver, xpath: String): Seq[WebElement] =  runSafelySeq(driver.findElements(By.xpath(xpath)))

    private def runSafelySeq(f: => Seq[WebElement]): Seq[WebElement] =  runSafelyWithDefault[Seq[WebElement]](f)(Seq[WebElement]())

    private def runSafelyOption(f: => WebElement): Option[WebElement] =  runSafelyWithDefault[Option[WebElement]](Some(f))(None)
  }

}