package com.exclamationlabs.connid.base.connector.util;

import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.HttpStatus;
import org.apache.http.StatusLine;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpRequestBase;
import org.mockito.Mock;
import org.mockito.Mockito;

import java.io.ByteArrayInputStream;
import java.io.IOException;

import static org.mockito.ArgumentMatchers.any;

/**
 * Subclass this abstract in order to add Mock REST support
 * for testing connectors that use RESTful drivers which
 * subclass BaseRestDriver.
 */
public abstract class ConnectorMockRestTest {

    @Mock
    protected HttpClient stubClient;

    @Mock
    protected HttpResponse stubResponse;

    @Mock
    protected HttpEntity stubResponseEntity;

    @Mock
    protected StatusLine stubStatusLine;

    protected void prepareMockResponse(String responseData) {
        try {
            Mockito.when(stubResponseEntity.getContent()).thenReturn(new ByteArrayInputStream(responseData.getBytes()));
            Mockito.when(stubResponse.getEntity()).thenReturn(stubResponseEntity);
            Mockito.when(stubResponse.getStatusLine()).thenReturn(stubStatusLine);
            Mockito.when(stubStatusLine.getStatusCode()).thenReturn(HttpStatus.SC_OK);
            Mockito.when(stubClient.execute(any(HttpRequestBase.class))).thenReturn(stubResponse);
        } catch(IOException ioe) {
            throw new RuntimeException("IO Exception occurred during Mock rest execution " +
                    "(populated response)", ioe);
        }

    }

    protected void prepareMockResponseEmpty() {
        try {
            Mockito.when(stubResponse.getStatusLine()).thenReturn(stubStatusLine);
            Mockito.when(stubStatusLine.getStatusCode()).thenReturn(HttpStatus.SC_OK);
            Mockito.when(stubClient.execute(any(HttpRequestBase.class))).thenReturn(stubResponse);
        } catch(IOException ioe) {
            throw new RuntimeException(
                    "IO Exception occurred during Mock rest execution (empty response", ioe);
        }

    }
}
