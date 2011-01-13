package ssahayam
/*
 * Copyright 2010 Sanjiv Sahayam
 * Licensed under the Apache License, Version 2.0
 */
trait PluginSupport {

  def leftToOption[T](f: Either[String, T]): Option[String] = f.left.toOption

  implicit def eitherToLeft[T](e:Either[String, T]): Lefty[T] = Lefty(e)

  case class Lefty[T](e:Either[String, T]) {
    def toLeftOption: Option[String] = e.left.toOption
  }

  def runSafely[T,R](f:R => T): R => Either[String, T] = {
    driver =>
      try {
        Right(f(driver))
      } catch {
        case error => { Left(error.getMessage) }
      }
  }
}