package shortbread

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

    def getText(element: Option[WebElement], xpath: String)(implicit default:String): String = {
      element.flatMap(getElement(_, xpath)).flatMap(value => runSafelyWithOptionReturnResult(value.getText)).getOrElse(default)
    }

    def getElement(element: WebElement, xpath: String): Option[WebElement] =  runSafelyOption(element.findElement(By.xpath(xpath)))// return Option

    def getElement(driver: RemoteWebDriver, xpath: String): Option[WebElement] = runSafelyOption(driver.findElement(By.xpath(xpath))) //return Option

    def getElements(element: WebElement, xpath: String): Seq[WebElement] =  runSafelySeq(element.findElements(By.xpath(xpath)))

    def getElements(driver: RemoteWebDriver, xpath: String): Seq[WebElement] =  runSafelySeq(driver.findElements(By.xpath(xpath)))

    def getBrowserUrl(driver: RemoteWebDriver): String = runSafelyWithDefault(driver.getCurrentUrl)(_ => defaultString)

    private def runSafelySeq(f: => Seq[WebElement]): Seq[WebElement] =  runSafelyWithDefault[Seq[WebElement]](f)(_ => Seq[WebElement]())

    private def runSafelyOption(f: => WebElement): Option[WebElement] = runSafelyWithDefault[Option[WebElement]](Some(f))(_ => None)
  }

}