package com.exclamationlabs.connid.base.connector.logging;

import org.apache.commons.lang3.StringUtils;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

public class Logger {

  private static boolean commonsLogging = false;

  private Logger() {}

  public static void setCommonsLogging(boolean input) {
    commonsLogging = input;
  }

  public static void log(Object loggingClass, LogLevel level, String message) {
    if (commonsLogging) {
      logCommons(loggingClass, level, message);
    } else {
      logConnId(loggingClass, level, message);
    }
  }

  public static void log(Object loggingClass, LogLevel level, String message, Throwable throwable) {
    if (commonsLogging) {
      logCommonsWithThrowable(loggingClass, level, message, throwable);
    } else {
      logConnIdWithThrowable(loggingClass, level, message, throwable);
    }
  }

  private static void logCommonsWithThrowable(
      Object loggingClass, LogLevel level, String message, Throwable throwable) {
    final Log log = LogFactory.getLog(loggingClass.getClass());
    switch (level) {
      case TRACE:
        log.trace(message, throwable);
        break;
      case DEBUG:
        log.debug(message, throwable);
        break;
      case WARN:
        log.warn(message, throwable);
        break;
      case ERROR:
        log.error(message, throwable);
        break;
      case FATAL:
        log.fatal(message, throwable);
        break;
      default:
        log.info(message, throwable);
        break;
    }
  }

  private static void logCommons(Object loggingClass, LogLevel level, String message) {
    final Log log = LogFactory.getLog(loggingClass.getClass());
    switch (level) {
      case TRACE:
        log.trace(message);
        break;
      case DEBUG:
        log.debug(message);
        break;
      case WARN:
        log.warn(message);
        break;
      case ERROR:
        log.error(message);
        break;
      case FATAL:
        log.fatal(message);
        break;
      default:
        log.info(message);
        break;
    }
  }

  private static void logConnIdWithThrowable(
      Object loggingClass, LogLevel level, String message, Throwable throwable) {
    org.identityconnectors.common.logging.Log.Level connIdLevel = determineConnIdLogLevel(level);

    org.identityconnectors.common.logging.Log connIdLogger =
        org.identityconnectors.common.logging.Log.getLog(loggingClass.getClass());

    connIdLogger.log(connIdLevel, throwable, connIdCleanMessage(message));
  }

  private static void logConnId(Object loggingClass, LogLevel level, String message) {

    org.identityconnectors.common.logging.Log connIdLogger =
        org.identityconnectors.common.logging.Log.getLog(loggingClass.getClass());
    switch (level) {
      case TRACE:
      case DEBUG:
        connIdLogger.ok(connIdCleanMessage(message));
        break;
      case WARN:
        connIdLogger.warn(connIdCleanMessage(message));
        break;
      case ERROR:
      case FATAL:
        connIdLogger.error(connIdCleanMessage(message));
        break;
      default:
        connIdLogger.info(connIdCleanMessage(message));
        break;
    }
  }

  private static String connIdCleanMessage(String message) {
    return StringUtils.replace(StringUtils.replace(message, "{", "["), "}", "]");
  }

  private static org.identityconnectors.common.logging.Log.Level determineConnIdLogLevel(
      LogLevel level) {
    org.identityconnectors.common.logging.Log.Level connIdLevel =
        org.identityconnectors.common.logging.Log.Level.INFO;
    switch (level) {
      case TRACE:
      case DEBUG:
        connIdLevel = org.identityconnectors.common.logging.Log.Level.OK;
        break;

      case WARN:
        connIdLevel = org.identityconnectors.common.logging.Log.Level.WARN;
        break;
      case ERROR:
      case FATAL:
        connIdLevel = org.identityconnectors.common.logging.Log.Level.ERROR;
        break;

      default:
        break;
    }
    return connIdLevel;
  }

  public static void debug(Object loggingClass, String message) {
    log(loggingClass, LogLevel.DEBUG, message);
  }

  public static void trace(Object loggingClass, String message) {
    log(loggingClass, LogLevel.TRACE, message);
  }

  public static void info(Object loggingClass, String message) {
    log(loggingClass, LogLevel.INFO, message);
  }

  public static void warn(Object loggingClass, String message) {
    log(loggingClass, LogLevel.WARN, message);
  }

  public static void error(Object loggingClass, String message) {
    log(loggingClass, LogLevel.ERROR, message);
  }

  public static void fatal(Object loggingClass, String message) {
    log(loggingClass, LogLevel.FATAL, message);
  }

  public static void debug(Object loggingClass, String message, Throwable throwable) {
    log(loggingClass, LogLevel.DEBUG, message, throwable);
  }

  public static void trace(Object loggingClass, String message, Throwable throwable) {
    log(loggingClass, LogLevel.TRACE, message, throwable);
  }

  public static void info(Object loggingClass, String message, Throwable throwable) {
    log(loggingClass, LogLevel.INFO, message, throwable);
  }

  public static void warn(Object loggingClass, String message, Throwable throwable) {
    log(loggingClass, LogLevel.WARN, message, throwable);
  }

  public static void error(Object loggingClass, String message, Throwable throwable) {
    log(loggingClass, LogLevel.ERROR, message, throwable);
  }

  public static void fatal(Object loggingClass, String message, Throwable throwable) {
    log(loggingClass, LogLevel.FATAL, message, throwable);
  }
}
