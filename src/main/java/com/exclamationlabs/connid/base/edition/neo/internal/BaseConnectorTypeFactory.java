package com.exclamationlabs.connid.base.edition.neo.internal;

import com.exclamationlabs.connid.base.connector.authenticator.Authenticator;
import com.exclamationlabs.connid.base.connector.configuration.ConnectorConfiguration;
import com.exclamationlabs.connid.base.connector.logging.Logger;
import com.exclamationlabs.connid.base.edition.neo.annotation.model.ModelObjectClass;
import com.exclamationlabs.connid.base.edition.neo.driver.Driver;
import com.exclamationlabs.connid.base.edition.neo.driver.FaultProcessor;
import com.exclamationlabs.connid.base.edition.neo.driver.Invocator;
import com.exclamationlabs.connid.base.edition.neo.model.IdentityModel;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import io.github.classgraph.ClassGraph;
import io.github.classgraph.ScanResult;
import java.lang.reflect.Modifier;
import java.lang.reflect.ParameterizedType;
import java.lang.reflect.Type;
import java.util.*;
import java.util.stream.Collectors;
import lombok.AccessLevel;
import lombok.Getter;
import org.apache.commons.lang3.StringUtils;
import org.identityconnectors.framework.common.exceptions.ConfigurationException;

@Getter
public final class BaseConnectorTypeFactory<T extends ConnectorConfiguration> {

  private Driver<T> driver;
  private Authenticator<T> authenticator;
  private FaultProcessor driverFaultProcessor;

  private final Set<Class<? extends IdentityModel>> modelClassList;

  @Getter(AccessLevel.NONE)
  private final Map<Class<IdentityModel>, Invocator<T, ?, ?>> invocatorMap;

  @Getter(AccessLevel.NONE)
  private final Map<Class<Invocator<?, ?, ?>>, FaultProcessor> invocatorFaultProcessorMap;

  private final Class<?> implementationClass;
  private final Class<T> configurationType;

  @Getter(AccessLevel.NONE)
  private final ObjectMapper objectMapper;

  public BaseConnectorTypeFactory(Class<?> implementationClass, Class<T> configurationType) {
    this.configurationType = configurationType;
    this.implementationClass = implementationClass;
    modelClassList = new HashSet<>();
    invocatorMap = new HashMap<>();
    invocatorFaultProcessorMap = new HashMap<>();
    objectMapper = new ObjectMapper();
  }

  public void init() throws ConfigurationException {
    loadDriver(); // exactly 1 required
    loadModelClasses(); // at least 1 required
    loadAuthenticator(); // per connector - optional
    loadInvocators(); // per model - optional
    loadDriverFaultProcessor(); // per driver - optional
  }

  @SuppressWarnings("unchecked")
  private void loadDriver() throws ConfigurationException {
    try (ScanResult scanResult =
        new ClassGraph()
            .enableAllInfo()
            .acceptPackages(implementationClass.getPackageName())
            .scan()) {

      List<Class<?>> driverClassList =
          scanResult.getClassesImplementing(Driver.class.getName()).loadClasses().stream()
              .filter(driverClass -> !Modifier.isAbstract(driverClass.getModifiers()))
              .collect(Collectors.toList());
      if (driverClassList.isEmpty()) {
        throw new ConfigurationException(
            String.format(
                "Driver not found for connector %s within package %s",
                implementationClass.getSimpleName(), implementationClass.getPackageName()));
      }
      if (driverClassList.size() > 1) {
        throw new ConfigurationException(
            String.format(
                "Multiple drivers found for connector %s within package %s.  Only one driver is allowed for base connector implementation.",
                implementationClass.getSimpleName(), implementationClass.getPackageName()));
      }
      Class<?> driverClass = driverClassList.get(0);
      if (isMissingAssignableTypeArgument(driverClass, configurationType)) {
        throw new ConfigurationException(
            String.format(
                "Driver %s does not have expected configuration %s in its type definition.",
                driverClass.getSimpleName(), configurationType.getName()));
      }
      driver = (Driver<T>) driverClass.getConstructor().newInstance();
    } catch (ReflectiveOperationException e) {
      throw new ConfigurationException(
          "Unexpected reflection or instantiation issue with Driver implementation", e);
    }
  }

  @SuppressWarnings("unchecked")
  private void loadModelClasses() throws ConfigurationException {
    try (ScanResult scanResult =
        new ClassGraph()
            .enableAllInfo()
            .acceptPackages(implementationClass.getPackageName())
            .scan()) {

      List<Class<?>> modelClassList =
          scanResult.getClassesImplementing(IdentityModel.class.getName()).loadClasses().stream()
              .filter(driverClass -> !Modifier.isAbstract(driverClass.getModifiers()))
              .collect(Collectors.toList());
      if (modelClassList.isEmpty()) {
        throw new ConfigurationException(
            String.format(
                "No models found for connector %s within package %s",
                implementationClass.getSimpleName(), implementationClass.getPackageName()));
      }
      List<Class<? extends IdentityModel>> identityModelClassList = new ArrayList<>();
      for (var currentClass : modelClassList) {
        var currentModelClass = (Class<? extends IdentityModel>) currentClass;
        if (currentModelClass.isAnnotationPresent(ModelObjectClass.class)
            && StringUtils.isNotBlank(
                currentModelClass.getAnnotation(ModelObjectClass.class).value())) {
          identityModelClassList.add(currentModelClass);
        } else {
          throw new ConfigurationException(
              String.format(
                  "Model class %s does not have a ModelObjectClass annotation, or the value is blank.",
                  currentModelClass.getSimpleName()));
        }
      }
      this.modelClassList.addAll(identityModelClassList);
    }
  }

  @SuppressWarnings("unchecked")
  private void loadAuthenticator() throws ConfigurationException {
    try (ScanResult scanResult =
        new ClassGraph()
            .enableAllInfo()
            .acceptPackages(implementationClass.getPackageName())
            .scan()) {

      List<Class<?>> authenticatorClassList =
          scanResult.getClassesImplementing(Authenticator.class.getName()).loadClasses();
      if (authenticatorClassList.size() > 1) {
        throw new ConfigurationException(
            String.format(
                "Multiple authenticators found for connector %s within package %s.  Only one is allowed for base connector implementation.",
                implementationClass.getSimpleName(), implementationClass.getPackageName()));
      }
      if (authenticatorClassList.isEmpty()) {
        this.authenticator = configuration -> "NA";
      } else {
        Class<?> authenticatorClass = authenticatorClassList.get(0);
        if (isMissingAssignableTypeArgument(authenticatorClass, configurationType)) {
          throw new ConfigurationException(
              String.format(
                  "Authenticator class %s does not have expected configuration type %s",
                  authenticatorClass.getSimpleName(), configurationType.getName()));
        }
        authenticator = (Authenticator<T>) authenticatorClass.getConstructor().newInstance();
      }
    } catch (ReflectiveOperationException e) {
      throw new ConfigurationException(
          "Unexpected reflection or instantiation issue with Authenticator implementation", e);
    }
  }

  @SuppressWarnings("unchecked")
  private void loadInvocators() throws ConfigurationException {
    try (ScanResult scanResult =
        new ClassGraph()
            .enableAllInfo()
            .acceptPackages(implementationClass.getPackageName())
            .scan()) {
      List<Class<?>> allInvocatorClasses =
          scanResult.getClassesImplementing(Invocator.class.getName()).loadClasses();
      for (Class<?> currentModelClass : modelClassList) {
        Class<IdentityModel> modelClass = (Class<IdentityModel>) currentModelClass;
        for (Class<?> invocatorClass : allInvocatorClasses) {
          if (invocatorHasAssignableTypeArguments(
              invocatorClass, configurationType, driver.getClass(), modelClass)) {
            invocatorMap.put(
                modelClass, (Invocator<T, ?, ?>) invocatorClass.getConstructor().newInstance());
          }
        }
      }
    } catch (ReflectiveOperationException e) {
      throw new ConfigurationException(
          "Unexpected reflection or instantiation issue with Invocator implementation", e);
    }
  }

  @SuppressWarnings("unchecked")
  private void loadDriverFaultProcessor() throws ConfigurationException {
    try (ScanResult scanResult =
        new ClassGraph()
            .enableAllInfo()
            .acceptPackages(implementationClass.getPackageName())
            .scan()) {

      List<Class<?>> faultProcessorClassList =
          scanResult.getClassesImplementing(FaultProcessor.class.getName()).loadClasses();
      for (Class<?> currentClass : faultProcessorClassList) {
        Class<FaultProcessor> faultProcessorClass = (Class<FaultProcessor>) currentClass;

        FaultProcessor processor = faultProcessorClass.getConstructor().newInstance();

        if (processor.forInvocator() != null) {
          invocatorFaultProcessorMap.put(processor.forInvocator(), processor);
        } else {
          driverFaultProcessor = processor;
        }
      }
    } catch (ReflectiveOperationException e) {
      throw new ConfigurationException(
          "Unexpected reflection or instantiation issue with FaultProcessor implementation", e);
    }
  }

  private static boolean isMissingAssignableTypeArgument(Class<?> holdingClass, Class<?> heldType) {

    return Arrays.stream(holdingClass.getGenericInterfaces())
        .filter(type -> type instanceof ParameterizedType)
        .map(type -> (ParameterizedType) type)
        .noneMatch(
            paramType -> {
              Type[] actualTypeArguments = paramType.getActualTypeArguments();
              return actualTypeArguments.length > 0 && actualTypeArguments[0].equals(heldType);
            });
  }

  private boolean invocatorHasAssignableTypeArguments(
      Class<?> holdingClass,
      Class<T> type1,
      Class<? extends Driver> type2,
      Class<IdentityModel> type3) {
    return Arrays.stream(holdingClass.getGenericInterfaces())
        .filter(type -> type instanceof ParameterizedType)
        .map(type -> (ParameterizedType) type)
        .allMatch(
            paramType -> {
              Type[] actualTypeArguments = paramType.getActualTypeArguments();
              return actualTypeArguments.length == 3
                  && actualTypeArguments[0].equals(type1)
                  && actualTypeArguments[1].equals(type2)
                  && actualTypeArguments[2].equals(type3);
            });
  }

  public Invocator<T, ?, ?> getInvocator(Class<IdentityModel> modelClass) {
    return invocatorMap.get(modelClass);
  }

  public FaultProcessor getInvocatorFaultProcessor(Class<Invocator<T, ?, ?>> invocatorClass) {
    return invocatorFaultProcessorMap.get(invocatorClass);
  }

  public String getConstruction() {
    Map<String, Object> output = new LinkedHashMap<>();
    output.put("Driver", driver.getClass().getSimpleName());
    output.put(
        "Authenticator", authenticator != null ? authenticator.getClass().getSimpleName() : "None");

    output.put(
        "Models", modelClassList.stream().map(Class::getSimpleName).collect(Collectors.toList()));
    output.put(
        "Invocators",
        invocatorMap.keySet().stream().map(Class::getSimpleName).collect(Collectors.toList()));
    output.put(
        "DriverFaultProcessor",
        driverFaultProcessor != null ? driverFaultProcessor.getClass().getSimpleName() : "None");
    output.put(
        "InvocatorFaultProcessors",
        invocatorFaultProcessorMap.values().stream()
            .map(inv -> inv.getClass().getSimpleName())
            .collect(Collectors.toList()));

    try {
      return objectMapper.writeValueAsString(output);
    } catch (JsonProcessingException e) {
      final var MSG = "Error converting connector construction to JSON";
      Logger.warn(this, MSG, e);
      return MSG;
    }
  }
}
