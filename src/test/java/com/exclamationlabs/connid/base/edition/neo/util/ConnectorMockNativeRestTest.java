package com.exclamationlabs.connid.base.edition.neo.util;

import static java.net.HttpURLConnection.HTTP_OK;
import static org.junit.jupiter.api.Assertions.fail;
import static org.mockito.ArgumentMatchers.any;

import com.exclamationlabs.connid.base.connector.logging.Logger;
import java.io.IOException;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.util.Map;
import org.mockito.Mock;
import org.mockito.Mockito;

public abstract class ConnectorMockNativeRestTest {

  @Mock protected HttpClient stubClient;

  @Mock protected HttpResponse<String> stubResponse;

  protected void prepareMockResponse(Map<String, String> responseHeaders, String... responseData) {
    try {
      Mockito.when(stubResponse.statusCode()).thenReturn(HTTP_OK);
      Mockito.when(stubResponse.body()).thenReturn(responseData[0]);
      Mockito.when(stubClient.send(any(HttpRequest.class), any(HttpResponse.BodyHandler.class)))
          .thenReturn(stubResponse);
    } catch (IOException | InterruptedException ioe) {
      handleFailure("IOException occurred during Mock rest execution for empty response", ioe);
    }
  }

  private static void handleFailure(String message, Throwable throwable) {
    Logger.error(ConnectorMockNativeRestTest.class, message, throwable);
    fail(message);
  }
}
