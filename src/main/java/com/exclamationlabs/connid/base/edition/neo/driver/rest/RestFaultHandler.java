package com.exclamationlabs.connid.base.edition.neo.driver.rest;

import com.exclamationlabs.connid.base.connector.configuration.ConnectorConfiguration;
import org.identityconnectors.framework.common.exceptions.ConnectorException;

import java.net.http.HttpRequest;
import java.net.http.HttpResponse;

public interface RestFaultHandler<T extends ConnectorConfiguration> {

    void process(T configuration, HttpRequest request, HttpResponse<String> response) throws ConnectorException;

    boolean supportsNotFound();
}
