package com.exclamationlabs.connid.base.edition.neo.stub.rest;

import com.exclamationlabs.connid.base.edition.neo.driver.rest.RestBehavior;
import com.exclamationlabs.connid.base.edition.neo.stub.configuration.RestTestConfiguration;

public class RestTestBehavior implements RestBehavior<RestTestConfiguration> {
    @Override
    public String getName() {
        return "";
    }

    @Override
    public boolean usesBearerAuthentication() {
        return false;
    }

    @Override
    public boolean supportsReauthentication() {
        return false;
    }

    @Override
    public String getBaseServiceUrl(RestTestConfiguration configuration) {
        return "https://somewhere.net/";
    }
}
