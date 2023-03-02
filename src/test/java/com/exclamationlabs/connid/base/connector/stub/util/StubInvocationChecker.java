package com.exclamationlabs.connid.base.connector.stub.util;

public class StubInvocationChecker {

  private static String methodInvoked;
  private static Object methodParameter1;
  private static Object methodParameter2;
  private static boolean initializeInvoked = false;

  private StubInvocationChecker() {}

  public static void reset() {
    setInitializeInvoked(false);
    setMethodInvoked(null);
    setMethodParameter1(null);
    setMethodParameter2(null);
  }

  public static String getMethodInvoked() {
    return methodInvoked;
  }

  public static void setMethodInvoked(String input) {
    methodInvoked = input;
  }

  public static Object getMethodParameter1() {
    return methodParameter1;
  }

  public static void setMethodParameter1(Object input) {
    methodParameter1 = input;
  }

  public static Object getMethodParameter2() {
    return methodParameter2;
  }

  public static void setMethodParameter2(Object input) {
    methodParameter2 = input;
  }

  public static boolean isInitializeInvoked() {
    return initializeInvoked;
  }

  public static void setInitializeInvoked(boolean input) {
    initializeInvoked = input;
  }
}
