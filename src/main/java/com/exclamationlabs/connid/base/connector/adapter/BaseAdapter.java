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

package com.exclamationlabs.connid.base.connector.adapter;

import com.exclamationlabs.connid.base.connector.BaseConnector;
import com.exclamationlabs.connid.base.connector.attribute.ConnectorAttribute;
import com.exclamationlabs.connid.base.connector.configuration.ConnectorConfiguration;
import com.exclamationlabs.connid.base.connector.configuration.DefaultConnectorConfiguration;
import com.exclamationlabs.connid.base.connector.configuration.basetypes.ResultsConfiguration;
import com.exclamationlabs.connid.base.connector.driver.Driver;
import com.exclamationlabs.connid.base.connector.model.IdentityModel;
import com.exclamationlabs.connid.base.connector.results.ResultsFilter;
import com.exclamationlabs.connid.base.connector.results.ResultsPaginator;
import com.exclamationlabs.connid.base.connector.util.OperationOptionsDataFinder;
import org.apache.commons.lang3.BooleanUtils;
import org.apache.commons.lang3.StringUtils;
import org.identityconnectors.common.logging.Log;
import org.identityconnectors.framework.common.objects.*;

import java.util.Collections;
import java.util.HashSet;
import java.util.Set;

/**
 * Base attribute class describing composition of an Adapter.
 * Since the Adapter is the glue between the Connector (which communicates
 * with Midpoint) and the Driver (which communicates with an external IAM data source),
 * it has to hold a reference to the Driver so it can communicate with it.
 *
 * Adapter subclasses should specify the generic as a specific identity object
 * type, which implements IdentityModel.
 */
public abstract class BaseAdapter<T extends IdentityModel, U extends ConnectorConfiguration> {

    private static final Log LOG = Log.getLog(BaseAdapter.class);

    private Driver<U> driver;

    protected U configuration;

    /**
     * Return the ObjectClass type associated with this adapter.
     * This could be ConnId provided ObjectClass.ACCOUNT (for users) or
     * ObjectClass.GROUP (for groups), or a custom type ( new ObjectClass("Something") )     *
     * @return ObjectClass pertaining to this adapter.
     */
    public abstract ObjectClass getType();

    /**
     * Return the IdentityModel class reference.
     * @return Class reference for the IdentityModel implementation pertaining to this
     * adapter.
     */
    public abstract Class<T> getIdentityModelClass();

    /**
     * Return a list of the connector attributes belonging to this adapter.
     * Adapter subclasses should construct a list of ConnectorAttribute objects,
     * using desired name, type and definition information, by using the
     * ConnectorAttribute() constructor.
     * @return Set of ConnectorAttributes associated with this IdentityModel and Adapter.
     */
    public abstract Set<ConnectorAttribute> getConnectorAttributes();

    /**
     * Given an input IdentityModel, build the list of ConnId Attributes to
     * be received to Midpoint.
     * @param model IdentityModel implementation holding object data.
     * @return Set of ConnId Attribute objects holding name and value information to
     * be received by Midpoint.
     */
    protected abstract Set<Attribute> constructAttributes(T model);

    /**
     * Given a set of Attribute information received from Midpoint, build
     * a new IdentityModel with fields populated based on the received attributes
     * and return it.
     *
     * NOTE: Static helper AdapterValueTypeConverter methods should be
     * used to help read the data inside an Attribute object for each field
     * and handle type juggling, to try to keep this complexity out of Adapter implementations.
     *
     * @param attributes Name/Value Attribute information received from Midpoint.
     * @param isCreate True if this invocation applies to new object creation, false
     *                 if object update is applicable.
     * @return IdentityModel with fields populated from input attributes.
     */
    protected abstract T constructModel(Set<Attribute> attributes, boolean isCreate);

    /**
     * Service a request from IAM system to create the type on the destination system.
     * @param attributes Attributes from IAM system that map to data elements needed
     *                   by the destination system for type creation.
     * @return new unique identifier for newly created type
     */
    public final Uid create(Set<Attribute> attributes) {
        T model = constructModel(attributes, true);
        String newId = getDriver().create(getIdentityModelClass(), model);
        return new Uid(newId);
    }

    /**
     * Service a request from IAM system to update the type on the destination system.
     * @param uid Unique identifier for the data item being updated.
     * @param attributes Attributes from IAM system that map to data elements supported
     *                   by the destination system to update the type.
     * @return unique identifier applicable to the type that was just updated
     */
    public final Uid update(Uid uid, Set<Attribute> attributes) {
        T model = constructModel(attributes, false);
        getDriver().update(getIdentityModelClass(), uid.getUidValue(), model);
        return uid;
    }

    /**
     * Service a request from IAM system to delete the type on the destination system.
     * @param uid Unique identifier for the data item to be deleted.
     */
    public final void delete(Uid uid) {
        getDriver().delete(getIdentityModelClass(), uid.getUidValue());
    }

    /**
     * Service a request from IAM to get one, some, or all items of a data type from the destination system.
     * @param queryIdentifier Query string to help identify which item(s) need to be retrieved.
     * @param resultsHandler ConnId ResultsHandler object used to send result data back to IAM system.
     * @param options OperationOptions object received by connector.  Not currently used but may be supported in
     *                the future for paging or other purposes.
     */
    public void get(String queryIdentifier, ResultsHandler resultsHandler, OperationOptions options, boolean hasEnhancedFiltering) {
        if (queryIdentifierContainsFilter(queryIdentifier)) {

            if (StringUtils.equalsIgnoreCase(
                    StringUtils.substringBefore(queryIdentifier, BaseConnector.FILTER_SEPARATOR),
                    Uid.NAME)) {
                // Simple Filter by UID happened - query for single record
                LOG.info("Exact uid match for {0} requested by filter for type {1}", queryIdentifier,
                        getIdentityModelClass().getSimpleName());
                IdentityModel singleItem = getDriver().getOne(getIdentityModelClass(),
                        StringUtils.substringAfter(queryIdentifier, BaseConnector.FILTER_SEPARATOR), options.getOptions());
                if (singleItem != null) {
                    passSetToResultsHandler(resultsHandler, Collections.singleton(singleItem), false);
                }
            } else {
                if (hasEnhancedFiltering) {
                    ResultsFilter resultsFilter = new ResultsFilter(StringUtils.substringBefore(queryIdentifier, BaseConnector.FILTER_SEPARATOR),
                            StringUtils.substringAfter(queryIdentifier, BaseConnector.FILTER_SEPARATOR));
                    LOG.info("Perform getAll using filter {0} for type {1}", resultsFilter,
                            getIdentityModelClass().getSimpleName());
                    executeGetAll(resultsFilter, resultsHandler, options);

                } else {
                    LOG.info("Filtering present but not supported.  Perform getAll unfiltered for type {0}",
                            getIdentityModelClass().getSimpleName());
                    executeGetAll(new ResultsFilter(), resultsHandler, options);
                }
            }
        } else {
            if (queryAllRecords(queryIdentifier)) {
                LOG.info("Filter absent.  Perform getAll unfiltered for type {0}",
                        getIdentityModelClass().getSimpleName());
                executeGetAll(new ResultsFilter(), resultsHandler, options);
            } else {
                // Query for single item
                LOG.info("Exact uid match for {0} requested by caller for type {1}", queryIdentifier,
                        getIdentityModelClass().getSimpleName());
                IdentityModel singleItem = getDriver().getOne(getIdentityModelClass(), queryIdentifier, options.getOptions());
                if (singleItem != null) {
                    passSetToResultsHandler(resultsHandler, Collections.singleton(singleItem), false);
                }
            }
        }

    }

    protected void executeGetAll(ResultsFilter resultsFilter, ResultsHandler resultsHandler, OperationOptions options) {
        ResultsConfiguration resultsConfiguration;
        if (getConfiguration() instanceof ResultsConfiguration) {
            resultsConfiguration = (ResultsConfiguration) getConfiguration();
        } else {
            resultsConfiguration = new DefaultResultsConfiguration();
        }

        boolean hasPagingOptions = OperationOptionsDataFinder.hasPagingOptions(options.getOptions());
        boolean importAll = (!hasPagingOptions) && (!resultsFilter.hasFilter());
        boolean deep = BooleanUtils.isTrue(resultsConfiguration.getDeepGet()) ||
                (importAll && BooleanUtils.isTrue(resultsConfiguration.getDeepImport()));
        ResultsPaginator paginator;
        if (BooleanUtils.isNotTrue(resultsConfiguration.getPagination())) {
            paginator = new ResultsPaginator();
        } else {
            if (hasPagingOptions) {
                // Get partial results using paging data from OperationOptions
                paginator = new ResultsPaginator(OperationOptionsDataFinder.getPageSize(options.getOptions()),
                        OperationOptionsDataFinder.getPageResultsOffset(options.getOptions()));

            } else {
                if (resultsConfiguration.getImportBatchSize() != null) {
                    // importAll with batch size
                    paginator = new ResultsPaginator(resultsConfiguration.getImportBatchSize(), 1);
                } else {
                    // importAll
                    paginator = new ResultsPaginator();
                }
            }
        }

        if (importAll && paginator.hasPagination()) {
            LOG.info("Starting batch import using pagination: {0}, deep: {1} for type {2}",
                    paginator, deep, getIdentityModelClass().getSimpleName());
            executeBatchImport(resultsHandler, paginator, deep);
        } else {
            LOG.info("Starting non-batch getAll using pagination: {0}, deep: {1} for type {2}",
                    paginator, deep, getIdentityModelClass().getSimpleName());
            // get a single page of results, or get/import all results in one shot
            Set<IdentityModel> dataSet = getDriver().getAll(getIdentityModelClass(), resultsFilter, paginator, null);
            passSetToResultsHandler(resultsHandler, dataSet, deep);
        }

    }

    protected void executeBatchImport(ResultsHandler resultsHandler, ResultsPaginator paginator, boolean deep) {

        while (!paginator.getNoMoreResults()) {
            LOG.info("Processing next batch using pagination {0} for type {1}, deep={2}",
                    paginator, getIdentityModelClass().getSimpleName(), deep);
            Set<IdentityModel> dataSet = getDriver().getAll(getIdentityModelClass(), new ResultsFilter(), paginator, null);
            if (dataSet == null || dataSet.isEmpty()) {
                LOG.info("Returned result set is empty. Considering batch import done for {0}",
                        getIdentityModelClass().getSimpleName());
                paginator.setNoMoreResults(true);
            } else {
                passSetToResultsHandler(resultsHandler, dataSet, deep);
                if (!paginator.getNoMoreResults()) {
                    paginator.setCurrentOffset(paginator.getCurrentOffset() + dataSet.size());
                    paginator.setCurrentPageNumber(paginator.getCurrentPageNumber() + 1);
                }
            }
        }

    }

    @SuppressWarnings({"unchecked"})
    protected void passSetToResultsHandler(ResultsHandler resultsHandler, Set<IdentityModel> dataSet, boolean deep) {
        if (deep) {
            int passCount = 0;
            for (IdentityModel current : dataSet) {
                LOG.info("For getAll/import, requesting deep item from driver with uid {0} for type {1}",
                        current.getIdentityIdValue(), getIdentityModelClass().getSimpleName());
                IdentityModel fullModel = getDriver().getOne(getIdentityModelClass(), current.getIdentityIdValue(), null);
                if (fullModel != null) {
                    resultsHandler.handle(constructConnectorObject((T) fullModel));
                    passCount++;
                }
            }
            LOG.info("Passed {0} deep items to result handler for type {1}",
                    passCount, getIdentityModelClass().getSimpleName());
        } else {
            int passCount = 0;
            for (IdentityModel item : dataSet) {
                if (item != null) {
                    resultsHandler.handle(constructConnectorObject((T) item));
                    passCount++;
                }
            }
            LOG.info("Passed {0} shallow items to result handler for type {1}",
                    passCount, getIdentityModelClass().getSimpleName());
        }
    }


    private boolean queryIdentifierContainsFilter(String query) {
        return query != null && StringUtils.contains(query, BaseConnector.FILTER_SEPARATOR);
    }

    public final void setDriver(Driver<U> component) {
        driver = component;
    }

    public final Driver<U> getDriver() {
        return driver;
    }

    public U getConfiguration() {
        return configuration;
    }

    public void setConfiguration(U configurationInput) {
        configuration = configurationInput;
    }

    /**
     * This utility method can be used within adapter constructModel() method
     * in order to construct a list of identifiers for assignment to another object type
     * @param attributes Set of attributes from Midpoint
     * @param attributeName Enum value pertain to the list of object identifiers for another
     *                      type.  This enum type (as string) is expected to possibly be
     *                      present in @param attributes.
     * @return List of string identifiers for another object type.
     */
    protected Set<String> readAssignments(Set<Attribute> attributes,
                                           Enum<?> attributeName) {
        Set<?> data = AdapterValueTypeConverter.getMultipleAttributeValue(
                Set.class, attributes, attributeName);

        Set<String> ids = new HashSet<>();
        if (data != null) {
            data.forEach(item -> ids.add(item.toString()));
        }

        if (!ids.isEmpty()) {
            return ids;
        } else {
            return null;
        }
    }

    protected final ConnectorObject constructConnectorObject(T model) {
        ConnectorObjectBuilder builder = getConnectorObjectBuilder(model);
        Set<Attribute> connectorAttributes = constructAttributes(model);
        for (Attribute current : connectorAttributes) {
            builder.addAttribute(AttributeBuilder.build(current.getName(),
                    current.getValue()));
        }
        return builder.build();
    }

    protected final ConnectorObjectBuilder getConnectorObjectBuilder(IdentityModel identity) {
        return new ConnectorObjectBuilder()
                .setObjectClass(getType())
                .setUid(identity.getIdentityIdValue())
                .setName(identity.getIdentityNameValue());
    }

    protected final boolean queryAllRecords(String query) {
        return (query == null || StringUtils.isBlank(query) || StringUtils.equalsIgnoreCase(query, "ALL"));
    }

    protected static class DefaultResultsConfiguration extends DefaultConnectorConfiguration implements ResultsConfiguration {

        @Override
        public Boolean getDeepGet() {
            return false;
        }

        @Override
        public void setDeepGet(Boolean input) {
        }

        @Override
        public Boolean getDeepImport() {
            return false;
        }

        @Override
        public void setDeepImport(Boolean input) {
        }

        @Override
        public Integer getImportBatchSize() {
            return null;
        }

        @Override
        public void setImportBatchSize(Integer input) {
        }

        @Override
        public Boolean getPagination() {
            return false;
        }

        @Override
        public void setPagination(Boolean input) {
        }
    }

}
