package com.exclamationlabs.connid.base.connector.configuration;

import org.identityconnectors.common.logging.Log;
import org.identityconnectors.framework.common.exceptions.ConfigurationException;

import javax.validation.*;
import java.util.Set;

public class ConfigurationValidator {

    private static final Log LOG = Log.getLog(ConfigurationValidator.class);

    private ConfigurationValidator() {}

    public static void validate(ConnectorConfiguration configuration) throws ConfigurationException {
        if (configuration.getActive()) {
            LOG.info("Validating configuration rules for configuration {0} ...",
                    configuration.getClass().getSimpleName()  );
            ValidatorFactory factory = Validation.buildDefaultValidatorFactory();
            Validator validator = factory.getValidator();

            Set<ConstraintViolation<ConnectorConfiguration>> violations =
                    validator.validate(configuration);
            if (!violations.isEmpty()) {
                throw new ConfigurationException("Validation of Connector configuration " +
                        configuration.getClass().getSimpleName() + " failed", new ConstraintViolationException(violations));
            }
        } else {
            LOG.info("Skipping validation, validation not active for {0}, name:{1} source:{2}" +
                    configuration.getClass().getSimpleName(), configuration.getName(),
                    configuration.getSource());
            throw new ConfigurationException("Skipped integration test");
        }
    }
}
