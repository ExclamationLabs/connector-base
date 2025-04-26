/*
    Copyright 2025 Exclamation Labs

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

package com.exclamationlabs.connid.base.edition.neo;

import com.exclamationlabs.connid.base.connector.authenticator.Authenticator;
import com.exclamationlabs.connid.base.connector.configuration.ConnectorConfiguration;
import com.exclamationlabs.connid.base.connector.filter.DefaultFilterTranslator;
import com.exclamationlabs.connid.base.connector.logging.Logger;
import com.exclamationlabs.connid.base.edition.neo.annotation.connector.ReadOnly;
import com.exclamationlabs.connid.base.edition.neo.internal.BaseConnectorTypeFactory;
import com.exclamationlabs.connid.base.edition.neo.internal.schema.BaseSchemaBuilder;
import com.exclamationlabs.connid.base.edition.neo.internal.search.FullAccessHandler;
import com.exclamationlabs.connid.base.edition.neo.internal.search.GetHandler;
import com.exclamationlabs.connid.base.edition.neo.internal.search.GetType;
import com.exclamationlabs.connid.base.edition.neo.model.IdentityModel;
import java.util.*;
import org.identityconnectors.framework.api.operations.GetApiOp;
import org.identityconnectors.framework.api.operations.SearchApiOp;
import org.identityconnectors.framework.common.exceptions.ConfigurationException;
import org.identityconnectors.framework.common.exceptions.ConnectorException;
import org.identityconnectors.framework.common.objects.*;
import org.identityconnectors.framework.common.objects.filter.Filter;
import org.identityconnectors.framework.common.objects.filter.FilterTranslator;
import org.identityconnectors.framework.spi.Configuration;
import org.identityconnectors.framework.spi.PoolableConnector;
import org.identityconnectors.framework.spi.operations.*;

/**
 * Abstract base class for defining Identity Access Management connectors. In order to with MidPoint
 * or ConnId system, this class MUST be annotated with
 * org.identityconnectors.framework.spi.ConnectorClass, supplying a displayNameKey and
 * configurationClass. Example:
 *
 * <p>{@literal @}ConnectorClass(displayNameKey = "test.display", configurationClass =
 * StubConfiguration.class)
 *
 * <p>In most cases, you should subclass one of these abstract classes instead of this one, based on
 * your connector's need: BaseFullAccessConnector - full create/read/update/delete access to the
 * destination system BaseReadOnlyConnector - read-only access to the destination system
 * BaseWriteOnlyConnector - write-only access to the destination system
 *
 * <p>The constructor for your concrete class should also call these setters... MANDATORY:
 * setDriver(); setAdapters();
 *
 * <p>OPTIONAL: setAuthenticator(); setConnectorSchemaBuilder(); setEnhancedFiltering();
 * setFilterAttributes();
 */
public abstract class BaseConnector<T extends ConnectorConfiguration>
    implements PoolableConnector,
        SchemaOp,
        TestOp,
        DeleteOp,
        CreateOp,
        UpdateDeltaOp,
        SearchOp<Filter>,
        SearchApiOp,
        GetApiOp {

  private final BaseConnectorTypeFactory<T> typeFactory;

  protected T configuration;
  protected Class<T> configurationType;

  protected BaseSchemaBuilder<T> schemaBuilder;
  protected Schema schema;

  public BaseConnector(Class<T> configurationType) {
    this(configurationType, false);
  }

  public BaseConnector(Class<T> configurationType, boolean commonsLogging) {
    Logger.setCommonsLogging(commonsLogging);
    this.configurationType = configurationType;
    typeFactory = new BaseConnectorTypeFactory<>(this.getClass(), this.configurationType);
    Logger.trace(
        this,
        String.format(
            "Connector %s instantiated with configuration class type %s",
            this.getClass().getSimpleName(), configurationType.getSimpleName()));
    schemaBuilder = new BaseSchemaBuilder<>();
  }

  /**
   * MidPoint calls this method to initialize a connector on startup.
   *
   * @param configuration Configuration concrete class (Midpoint determines this by looking at
   *     configurationClass of {@literal @}ConnectorClass annotation on your concrete connector
   *     class)
   */
  @Override
  @SuppressWarnings("unchecked")
  public void init(Configuration configuration) throws ConfigurationException {
    if (!(configurationType.isInstance(configuration))) {
      throw new ConfigurationException(
          String.format(
              "Invalid configuration object supplied to connector init.  Configuration object %s is not an instance of %s",
              configuration.getClass().getName(), configurationType.getName()));
    }
    this.configuration = (T) configuration;
    typeFactory.init();
    var examine = typeFactory.getIdentityModelAccessMap();

    Authenticator<T> authenticator = typeFactory.getAuthenticator();
    Logger.debug(
        this,
        String.format(
            "All type validated for connector %s.  Will now attempt to authenticate using %s",
            this.getName(), authenticator.getClass().getSimpleName()));
    this.configuration.setCurrentToken(authenticator.authenticate(this.configuration));

    Logger.info(
        this,
        String.format(
            "Connector %s successfully authenticated using %s",
            this.getName(), authenticator.getClass().getSimpleName()));
  }

  /**
   * Identifies if this connector is able to read items on the destination system.
   *
   * @return true if read on the destination system is allowed, false if it is prohibited.
   */
  protected boolean readEnabled() {
    return true;
  }

  /**
   * Identifies whether or not this connector is able to update items on the destination system.
   *
   * @return true if update on the destination system is allowed, false if it is prohibited.
   */
  protected boolean updateEnabled() {
    return !this.getClass().isAnnotationPresent(ReadOnly.class);
  }

  /**
   * Identifies whether or not this connector is able to delete items on the destination system.
   *
   * @return true if delete on the destination system is allowed, false if it is prohibited.
   */
  protected boolean deleteEnabled() {
    return !this.getClass().isAnnotationPresent(ReadOnly.class);
  }

  /**
   * Identifies whether or not this connector is able to create items on the destination system.
   *
   * @return true if create on the destination system is allowed, false if it is prohibited.
   */
  protected boolean createEnabled() {
    return !this.getClass().isAnnotationPresent(ReadOnly.class);
  }

  protected boolean isReadOnly() {
    return readEnabled() && !updateEnabled() && !deleteEnabled() && !createEnabled();
  }

  protected boolean isWriteOnly() {
    return !readEnabled() && updateEnabled() && deleteEnabled() && createEnabled();
  }

  protected boolean isCrud() {
    return readEnabled() && updateEnabled() && deleteEnabled() && createEnabled();
  }

  @Override
  public void checkAlive() {
    test();
  }

  /** Required for ConnId Connector interface */
  @Override
  public Configuration getConfiguration() {
    return configuration;
  }

  @Override
  public void dispose() {
    typeFactory.getDriver().close();
  }

  @Override
  public Schema schema() {
    if (schema == null) {
      schema = schemaBuilder.build(this, configuration, typeFactory);
    }
    return schema;
  }

  public String getName() {
    return getClass().getSimpleName();
  }

  @Override
  public Uid create(
      final ObjectClass objectClass,
      final Set<Attribute> attributes,
      final OperationOptions operationOptions) {
    Class<? extends IdentityModel> modelType = typeFactory.getIdentityModel(objectClass);
    var handler = new FullAccessHandler<T>();
    var identityModelAccess = typeFactory.getIdentityModelAccessMap().get(objectClass);
    return handler.create(
        configuration,
        modelType,
        typeFactory.getDriver(),
        typeFactory.getInvocator(modelType),
        attributes,
        identityModelAccess);
  }

  @Override
  public Set<AttributeDelta> updateDelta(
      final ObjectClass objectClass,
      final Uid uid,
      final Set<AttributeDelta> attributeModifications,
      final OperationOptions operationOptions) {
    Class<? extends IdentityModel> modelType = typeFactory.getIdentityModel(objectClass);
    var handler = new FullAccessHandler<T>();
    var identityModelAccess = typeFactory.getIdentityModelAccessMap().get(objectClass);
    handler.update(
        configuration,
        modelType,
        typeFactory.getDriver(),
        typeFactory.getInvocator(modelType),
        uid,
        attributeModifications,
        identityModelAccess);
    return Collections.emptySet();
  }

  @Override
  public void delete(
      final ObjectClass objectClass, final Uid uid, final OperationOptions operationOptions) {
    Class<? extends IdentityModel> modelType = typeFactory.getIdentityModel(objectClass);
    var handler = new FullAccessHandler<T>();
    handler.delete(
        configuration,
        modelType,
        typeFactory.getDriver(),
        typeFactory.getInvocator(modelType),
        uid);
  }

  @Override
  public FilterTranslator<Filter> createFilterTranslator(
      ObjectClass objectClass, OperationOptions operationOptions) {
    // TODO: revisit approach
    return new DefaultFilterTranslator();
  }

  @Override
  public ConnectorObject getObject(
      ObjectClass objectClass, Uid uid, OperationOptions operationOptions) {
    var modelType = typeFactory.getIdentityModel(objectClass);
    var handler = new GetHandler<T>();
    var identityModelAccess = typeFactory.getIdentityModelAccessMap().get(objectClass);
    return handler.get(
        GetType.GET_OBJECT,
        configuration,
        modelType,
        typeFactory.getDriver(),
        typeFactory.getInvocator(modelType),
        objectClass,
        null,
        identityModelAccess,
        null,
        operationOptions);
  }

  @Override
  public void executeQuery(
      final ObjectClass objectClass,
      final Filter queryFilter,
      final ResultsHandler resultsHandler,
      final OperationOptions operationOptions) {
    var modelType = typeFactory.getIdentityModel(objectClass);
    var handler = new GetHandler<T>();
    var identityModelAccess = typeFactory.getIdentityModelAccessMap().get(objectClass);
    handler.get(
        GetType.EXECUTE_QUERY,
        configuration,
        modelType,
        typeFactory.getDriver(),
        typeFactory.getInvocator(modelType),
        objectClass,
        queryFilter,
        identityModelAccess,
        resultsHandler,
        operationOptions);
  }

  @Override
  public SearchResult search(
      final ObjectClass objectClass,
      final Filter filter,
      final ResultsHandler handler,
      final OperationOptions options) {
    executeQuery(objectClass, filter, handler, options);
    return new SearchResult();
  }

  public String getConstruction() {
    return typeFactory.getConstruction();
  }

  /** Standard connector test method per ConnId interface. */
  @Override
  public void test() {
    Logger.debug(
        this,
        String.format(
            "Test: Connector Configuration for connector %s: %s",
            this.getClass().getSimpleName(), configuration.write()));
    typeFactory.getDriver().test(configuration);
  }

  /**
   * Full connector test method. Get a full page of results, possibly relating to multiple object
   * classes.
   */
  public void fullConnectionTest() {
    Logger.debug(
        this,
        String.format(
            "Full connection test: Connector Configuration for connector %s: %s",
            this.getClass().getSimpleName(), configuration.write()));
    typeFactory.getDriver().fullConnectionTest(configuration);
  }

  /**
   * Method to manually invoke the connector and assure that we can successfully obtain a small
   * number of records from the destination API, without any authentication or general integration
   * errors occurring.
   *
   * @param objectClass - The object class for quick lookup search.
   * @return null if the look-up is successful, if look-up fails return the error message.
   */
  public String quickTest(final ObjectClass objectClass) {
    try {
      // TODO: get first 3 records
      return null;
    } catch (ConnectorException ce) {
      Logger.warn(this, "Quick test for connector failed", ce);
      return ce.getMessage();
    }
  }
}
