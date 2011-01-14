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

  def runSafelyWithEither[T](f: => T): Either[String, T] = runSafelyWithDefault[Either[String, T]](Right(f))(e => Left(e.getMessage))

  def runSafelyWithOption[T](f: => T): Option[String] = runSafelyWithDefault[Option[String]]{f; None}(e => Some(e.getMessage))

  def runSafelyWithDefault[T](f: => T)(default:(Exception) => T): T = {
    try {
      f
    }  catch {
      case e:Exception => default(e)
    }
  }
}