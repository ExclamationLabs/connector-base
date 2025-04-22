package com.exclamationlabs.connid.base.edition.neo.util;

import java.net.http.HttpClient;
import java.util.HashMap;
import java.util.Map;

public class ConnectivityTester {

  private ConnectivityTester() {}

  private static Map<TestPoint, String> testPointMap = new HashMap<>();
  private static HttpClient mockHttpClient;

  public static void reset() {
    testPointMap.clear();
    ConnectivityTester.mockHttpClient = null;
  }

  public static String getPoint(TestPoint testPoint) {
    return testPointMap.get(testPoint);
  }

  public static void setPoint(TestPoint testPoint, String value) {
    testPointMap.put(testPoint, value);
  }

  public static void setMockClient(HttpClient mockClient) {
    ConnectivityTester.mockHttpClient = mockClient;
  }

  public static HttpClient getMockClient() {
    return ConnectivityTester.mockHttpClient;
  }
}
