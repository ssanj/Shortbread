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

  /**
   * This function tries to safely run a resource than needs to be:
   * 1. Supplied
   * 2. Used
   * 3. Closed
   *
   * If the open function fails then no attempt is made to run f or close. The error is returned as Some(_).
   * If open succeeds and f fails then an attempt is made to run close. Whether close succeeds or fails, the error raised by f is returned as Some(_).
   * if open, f and close all succeed, None is returned.
   *
   * note: Usually open and close (and possibly f) are side-effecting functions. Given that, a failed open/f/close combination would cause
   * side-effects, although Exceptions will be transformed to Some(_).
   */
  def runSafelyWithResource[R, S, T](f:R => S)(open: => R)(close: R => T): Option[String] = {

    def functionFailed(resource:R)(error:String): Either[String, T] = { runSafelyWithEither[T](close(resource)); Left(error) }

    def functionPassed(resource:R)(result:S): Either[String, T] = runSafelyWithEither[T](close(resource))

    runSafelyWithEither[R](open).right.flatMap(resource => runSafelyWithEither[S](f(resource)).
            fold(functionFailed(resource), functionPassed(resource))).toLeftOption
  }
}