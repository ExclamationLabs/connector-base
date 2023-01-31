package com.exclamationlabs.connid.base.connector.configuration;

import com.exclamationlabs.connid.base.connector.logging.Logger;
import java.util.Set;
import javax.validation.*;
import org.identityconnectors.framework.common.exceptions.ConfigurationException;

public class ConfigurationValidator {

  private ConfigurationValidator() {}

  public static void validate(ConnectorConfiguration configuration) throws ConfigurationException {
    if (configuration.getActive()) {
      Logger.debug(
          ConfigurationValidator.class,
          String.format(
              "Validating configuration rules for configuration %s ...",
              configuration.getClass().getSimpleName()));
      ValidatorFactory factory = Validation.buildDefaultValidatorFactory();
      Validator validator = factory.getValidator();

      Set<ConstraintViolation<ConnectorConfiguration>> violations =
          validator.validate(configuration);
      if (!violations.isEmpty()) {
        throw new ConfigurationException(
            "Validation of Connector configuration "
                + configuration.getClass().getSimpleName()
                + " failed",
            new ConstraintViolationException(violations));
      }
    } else {
      Logger.debug(
          ConfigurationValidator.class,
          String.format(
              "Skipping validation, validation not active for %s, name:%s source:%s",
              configuration.getClass().getSimpleName(),
              configuration.getName(),
              configuration.getSource()));
      throw new ConfigurationException("Skipped integration test");
    }
  }
}
