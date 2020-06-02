package com.exclamationlabs.connid.base.connector.configuration;

import org.identityconnectors.common.logging.Log;
import org.identityconnectors.framework.common.exceptions.ConfigurationException;
import org.identityconnectors.framework.common.objects.ConnectorMessages;

import java.io.FileReader;
import java.io.IOException;
import java.util.Arrays;
import java.util.HashSet;
import java.util.Properties;
import java.util.Set;

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

    private Properties connectorProperties;

    private final Set<ConnectorProperty> requiredPropertyNames;

    public BaseConnectorConfiguration() {
        LOG.info("Empty required properties configuration");
        requiredPropertyNames = new HashSet<>();
    }

    @SafeVarargs
    public BaseConnectorConfiguration(Set<ConnectorProperty>... sets) {
        LOG.info("Required properties configuration, received {0}", Arrays.toString(sets));
        requiredPropertyNames = new HashSet<>();
        if (sets != null) {
            for (Set<ConnectorProperty> currentSet : sets) {
                requiredPropertyNames.addAll(currentSet);
            }
        }
        LOG.info("Required properties configuration loaded count {0}", requiredPropertyNames.size());
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

    protected void setValidated() {
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

    @Override
    public void validateConfiguration() throws ConfigurationException {
        setValidated();
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
}
