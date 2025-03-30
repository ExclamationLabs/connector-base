package com.exclamationlabs.connid.base.edition.neo.util;

import java.util.HashMap;
import java.util.Map;

public class ConnectivityTester {

  private ConnectivityTester() {}

  private static Map<TestPoint, String> testPointMap = new HashMap<>();

  public static void reset() {
    testPointMap.clear();
  }

  public static String getPoint(TestPoint testPoint) {
    return testPointMap.get(testPoint);
  }

  public static void setPoint(TestPoint testPoint, String value) {
    testPointMap.put(testPoint, value);
  }
}
