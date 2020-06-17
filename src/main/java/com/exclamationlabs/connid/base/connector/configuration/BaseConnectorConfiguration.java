/*
    Copyright 2020 Exclamation Labs

    Licensed under the Apache License, Version 2.0 (the "License");
    you may not use this file except in compliance with the License.
    You may obtain a copy of the License at

        http://www.apache.org/licenses/LICENSE-2.0

    Unless required by applicable law or agreed to in writing, software
    distributed under the License is distributed on an "AS IS" BASIS,
    WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
    See the License for the specific language governing permissions and
    limitations under the License.
*/

package com.exclamationlabs.connid.base.connector.configuration;

import com.exclamationlabs.connid.base.connector.authenticator.model.OAuth2AccessTokenContainer;
import org.identityconnectors.common.logging.Log;
import org.identityconnectors.framework.common.exceptions.ConfigurationException;
import org.identityconnectors.framework.common.objects.ConnectorMessages;

import java.io.FileReader;
import java.io.IOException;
import java.util.HashSet;
import java.util.Map;
import java.util.Properties;
import java.util.Set;
import java.util.Arrays;
import java.util.Collection;
import java.util.Objects;
import java.util.stream.Collectors;


/**
 * All configuration classes in the Base connector framework need to subclass this
 * abstract type.  This is an overlay for ConnId's configuration class, which
 * can be confusing and uninviting to use, and we needed something more streamlined.
 */
public abstract class BaseConnectorConfiguration implements ConnectorConfiguration {
    private static final Log LOG = Log.getLog(BaseConnectorConfiguration.class);

    private boolean validated;
    private ConnectorMessages connectorMessages;

    private String midPointConfigurationFilePath;

    private String credentialAccessToken;

    /**
     * Map to hold extra JWT claim data to be posted to the claim.  Can be null if not used.
     */
    private Map<String, String> extraJWTClaimData;

    /**
     * If OAuth2 authentication is used for this connector, this object
     * is used to hold additional OAuth2 response information, which
     * could be needed for the implementation.
     */
    private OAuth2AccessTokenContainer oauth2Information;

    private Properties connectorProperties;

    private Set<ConnectorProperty> requiredPropertyNames;

    private String qualifiedConfigurationName;

    public BaseConnectorConfiguration() {
        LOG.info("Empty required properties configuration");
        initializeRequiredPropertyNames();
    }

    public BaseConnectorConfiguration(String configurationName) {
        LOG.info("Configuration with location {0} specified", configurationName);
        qualifiedConfigurationName = configurationName;
        initializeRequiredPropertyNames();
        if (System.getenv(qualifiedConfigurationName) != null) {
            setMidPointConfigurationFilePath(System.getenv(qualifiedConfigurationName));
        } else {
            setMidPointConfigurationFilePath("src/test/resources/" +
                    qualifiedConfigurationName + ".properties");
        }
    }

    @SafeVarargs
    public final void setRequiredPropertyNames(Set<ConnectorProperty>... sets) {
        LOG.info("Required properties configuration, received {0}", Arrays.toString(sets));
        if (sets != null) {
            Set<ConnectorProperty> moreProperties = Arrays.stream(sets)
                    .filter(Objects::nonNull)
                    .flatMap(Collection::stream)
                    .collect(Collectors.toSet());
            requiredPropertyNames.addAll(moreProperties);
        }
        LOG.info("Required property names loaded for connector, count {0}, values {1}",
                requiredPropertyNames.size(), requiredPropertyNames);
    }

    /**
     * Override this method and return a path to configuration file
     * if the configuration for this Connector is file-based (most will be).
     * @return file path/name (relative or absolute) for Connector configuration file
     */
    abstract public String getConfigurationFilePath();

    @SuppressWarnings("unused")
    public void setConfigurationFilePath(String path) {
        LOG.info("setting configuration file path {0} for {1}", path, getName());
        setMidPointConfigurationFilePath(path);
    }

    @Override
    public String getName() {
        return this.getClass().getSimpleName();
    }

    public void setValidated() {
        validated = true;
    }

    @Override
    public boolean isValidated() {
        return validated;
    }

    @Override
    public void setup() throws ConfigurationException {
        LOG.info("invoking configuration setup for {0}", getClass().getSimpleName());
        if (getConfigurationFilePath() != null) {
            LOG.info("using configuration path {0}", getConfigurationFilePath());
            connectorProperties = new Properties();
            try {
                connectorProperties.load(new FileReader(getConfigurationFilePath()));
                LOG.info("configuration properties loaded for for {0}", getClass().getSimpleName());
            } catch (IOException ex) {
                throw new ConfigurationException("Failed to read configuration file from location " +
                        getConfigurationFilePath(), ex);
            }
        }

    }

    public String getProperty(ConnectorProperty propertyIn) {
        if (connectorProperties != null) {
            return connectorProperties.getProperty(propertyIn.name());
        }
        return null;
    }

    public String getPropertyFile(ConnectorProperty propertyIn) {
        if (qualifiedConfigurationName != null) {
            String fullIdentifier = qualifiedConfigurationName + "__" +
                    propertyIn.name();
            if (System.getenv(fullIdentifier) != null) {
                return System.getenv(fullIdentifier);
            }
        }
        return getProperty(propertyIn);
    }

    /**
     * For testing/stubbing
     * @param testProperties Populated Properties object w/ test
     *                       properties needed
     */
    protected void setConnectorProperties(Properties testProperties) {
        connectorProperties = testProperties;
    }

    @Override
    public void validateConfiguration() throws ConfigurationException {
        for (ConnectorProperty current : requiredPropertyNames) {
            if (getProperty(current) == null) {
                throw new ConfigurationException("Connector configuration is missing required property: " +
                        current.name());
            }
        }
        if ("Y".equalsIgnoreCase(getProperty(
                ConnectorProperty.CONNECTOR_BASE_CONFIGURATION_ACTIVE))) {
            setValidated();
            LOG.info("All {0} configuration properties successfully validated for {1}", requiredPropertyNames.size(),
                    getName());
        } else {
            throw new ConfigurationException("Connector configuration is currently disabled for " +
                    getName());
        }
    }

    public String getCredentialAccessToken() {
        return credentialAccessToken;
    }

    public void setCredentialAccessToken(String credentialAccessToken) {
        this.credentialAccessToken = credentialAccessToken;
    }

    @Override
    public ConnectorMessages getConnectorMessages() {
        return connectorMessages;
    }

    @Override
    public void setConnectorMessages(ConnectorMessages messages) {
        connectorMessages = messages;
    }

    public String getMidPointConfigurationFilePath() {
        LOG.info("getting getMidPointConfigurationFilePath {0}", midPointConfigurationFilePath);
        return midPointConfigurationFilePath;
    }

    public void setMidPointConfigurationFilePath(String inputPath) {
        if (inputPath != null) {
            LOG.info("setting getMidPointConfigurationFilePath, old {0}, new {1}",
                    midPointConfigurationFilePath, inputPath);

            this.midPointConfigurationFilePath = inputPath;
        }

    }

    public OAuth2AccessTokenContainer getOauth2Information() {
        return oauth2Information;
    }

    public void setOauth2Information(OAuth2AccessTokenContainer oauth2Information) {
        this.oauth2Information = oauth2Information;
    }

    public Map<String, String> getExtraJWTClaimData() {
        return extraJWTClaimData;
    }

    public void setExtraJWTClaimData(Map<String, String> extraJWTClaimData) {
        this.extraJWTClaimData = extraJWTClaimData;
    }

    public Set<ConnectorProperty> getRequiredPropertyNames() {
        return requiredPropertyNames;
    }

    private void initializeRequiredPropertyNames() {
        requiredPropertyNames = new HashSet<>();
        requiredPropertyNames.add(ConnectorProperty.CONNECTOR_BASE_CONFIGURATION_ACTIVE);
    }
}
