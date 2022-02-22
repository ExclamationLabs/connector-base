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
import com.exclamationlabs.connid.base.connector.configuration.basetypes.ServiceConfiguration;
import com.exclamationlabs.connid.base.connector.driver.Driver;
import com.exclamationlabs.connid.base.connector.model.IdentityModel;
import com.exclamationlabs.connid.base.connector.results.ResultsFilter;
import com.exclamationlabs.connid.base.connector.results.ResultsPaginator;
import com.exclamationlabs.connid.base.connector.util.OperationOptionsDataFinder;
import org.apache.commons.lang3.BooleanUtils;
import org.apache.commons.lang3.StringUtils;
import org.identityconnectors.common.logging.Log;
import org.identityconnectors.framework.common.exceptions.AlreadyExistsException;
import org.identityconnectors.framework.common.exceptions.ConnectorException;
import org.identityconnectors.framework.common.objects.*;

import java.util.Collections;
import java.util.HashSet;
import java.util.List;
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

    protected Set<String> multiValueAttributeNames;

    public BaseAdapter() {
        multiValueAttributeNames = new HashSet<>();
        Set<ConnectorAttribute> attributes = getConnectorAttributes();
        for (ConnectorAttribute attribute : attributes) {
            if (attribute.getFlags().contains(AttributeInfo.Flags.MULTIVALUED)) {
                multiValueAttributeNames.add(attribute.getName());
            }
        }
    }

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
     * @param addedMultiValueAttributes For updateDelta, this may contain
     *                                  multi-valued attributes that were just added via Midpoint.
     *                                  Null for create, Empty set if none applicable found for UpdateDelta
     * @param removedMultiValueAttributes For updateDelta, this may contain
     *                                  multi-valued attributes that were just removed via Midpoint.
     *                                  Null for create, Empty set if none applicable found for UpdateDelta
     * @param isCreate True if this invocation applies to new object creation, false
     *                 if object update is applicable.
     * @return IdentityModel with fields populated from input attributes.
     */
    protected abstract T constructModel(Set<Attribute> attributes,
                                        Set<Attribute> addedMultiValueAttributes,
                                        Set<Attribute> removedMultiValueAttributes,
                                        boolean isCreate);

    /**
     * Service a request from IAM system to create the type on the destination system.
     * @param attributes Attributes from IAM system that map to data elements needed
     *                   by the destination system for type creation.
     * @return new unique identifier for newly created type
     */
    public final Uid create(Set<Attribute> attributes) {
        T model = constructModel(attributes, null, null,true);
        String newId;

        try {
            newId = getDriver().create(getIdentityModelClass(), model);
        } catch (AlreadyExistsException aee) {
            if (supportsDuplicateErrorReturnsId()) {
                try {
                    IdentityModel duplicateModel = getDriver().getOneByName(getIdentityModelClass(), model.getIdentityNameValue());
                    if (duplicateModel != null && duplicateModel.getIdentityIdValue() != null) {
                        newId = duplicateModel.getIdentityIdValue();
                    } else {
                        throw new AlreadyExistsException("Driver/invocator could not obtain existing" +
                                " record for duplicate creation lookup");
                    }
                } catch (UnsupportedOperationException uoe) {
                    throw new AlreadyExistsException("Driver/invocator does not" +
                            " support duplicate creation lookup", uoe);
                } catch (ConnectorException ce) {
                    throw new AlreadyExistsException("Unexpected error occurred while attempting " +
                            "duplicate creation lookup", ce);
                }
            } else {
                throw aee;
            }
        }

        return new Uid(newId);
    }

    /**
     * Service a request from IAM system to update the type on the destination system.
     * @param uid Unique identifier for the data item being updated.
     * @param attributes Attributes from IAM system that map to data elements supported
     *                   by the destination system to update the type.
     * @return unique identifier applicable to the type that was just updated
     */
    public final Set<AttributeDelta> updateDelta(Uid uid, Set<AttributeDelta> attributes) {
        ConsolidatedValues consolidated = consolidateAttributeValues(attributes);
        T model = constructModel(consolidated.modifiedValues,
                consolidated.addedMultiValues, consolidated.removedMultiValues
                , false);
        getDriver().update(getIdentityModelClass(), uid.getUidValue(), model);
        return new HashSet<>();
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
        if (configuration instanceof ResultsConfiguration) {
            resultsConfiguration = (ResultsConfiguration) configuration;
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
        paginator.setAllowPartialAttributeValues(
                OperationOptionsDataFinder.getAllowPartialAttributeValues(options.getOptions()));

        if (importAll && paginator.hasPagination()) {
            LOG.info("Starting batch import using pagination: {0}, deep: {1} for type {2}",
                    paginator, deep, getIdentityModelClass().getSimpleName());
            executeBatchImport(resultsHandler, paginator, deep);
        } else {
            LOG.ok("Starting non-batch getAll using pagination: {0}, deep: {1} for type {2}",
                    paginator, deep, getIdentityModelClass().getSimpleName());
            // get a single page of results, or get/import all results in one shot
            Set<IdentityModel> dataSet = getDriver().getAll(getIdentityModelClass(), resultsFilter, paginator, null);
            passSetToResultsHandler(resultsHandler, dataSet, deep);
        }

    }

    protected void executeBatchImport(ResultsHandler resultsHandler, ResultsPaginator paginator, boolean deep) {

        while (!paginator.getNoMoreResults()) {
            LOG.ok("Processing next batch using pagination {0} for type {1}, deep={2}",
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
                LOG.ok("For getAll/import, requesting deep item from driver with uid {0} for type {1}",
                        current.getIdentityIdValue(), getIdentityModelClass().getSimpleName());
                IdentityModel fullModel = getDriver().getOne(getIdentityModelClass(), current.getIdentityIdValue(), null);
                if (fullModel != null) {
                    resultsHandler.handle(constructConnectorObject((T) fullModel));
                    passCount++;
                }
            }
            LOG.ok("Passed {0} deep items to result handler for type {1}",
                    passCount, getIdentityModelClass().getSimpleName());
        } else {
            int passCount = 0;
            for (IdentityModel item : dataSet) {
                if (item != null) {
                    resultsHandler.handle(constructConnectorObject((T) item));
                    passCount++;
                }
            }
            LOG.ok("Passed {0} shallow items to result handler for type {1}",
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

    private boolean supportsDuplicateErrorReturnsId() {
        if (configuration instanceof ServiceConfiguration) {
            ServiceConfiguration serviceConfiguration = (ServiceConfiguration) configuration;
            return BooleanUtils.isTrue(serviceConfiguration.getDuplicateErrorReturnsId());
        }

        return false;
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

    private ConsolidatedValues consolidateAttributeValues(Set<AttributeDelta> delta) {
        Set<Attribute> modifiedSet = new HashSet<>();
        Set<Attribute> addedSet = new HashSet<>();
        Set<Attribute> removedSet = new HashSet<>();

        LOG.ok("Consolidate {0} delta values", delta.size());
        for (AttributeDelta current : delta) {
            boolean multiValuedAttribute = multiValueAttributeNames.contains(current.getName());
            List<Object> addValues = current.getValuesToAdd();
            List<Object> modifiedValues = current.getValuesToReplace();
            List<Object> removedValues = current.getValuesToRemove();

            if (modifiedValues != null) {
                modifiedSet.add(AttributeBuilder.build(current.getName(), modifiedValues));
                LOG.ok("ModifiedValues not null. Added {0} to modified set.  New size {1}", current.getName(), modifiedSet.size());
            } else {
                if (addValues != null) {
                    if (multiValuedAttribute) {
                        addedSet.add(AttributeBuilder.build(current.getName(), addValues));
                        LOG.ok("MultiValuedAttribute. Added {0} to added set.  New size {1}", current.getName(), addedSet.size());
                    } else {
                        modifiedSet.add(AttributeBuilder.build(current.getName(), addValues));
                        LOG.ok("Not MultiValuedAttribute. Added {0} to modified set.  New size {1}", current.getName(), modifiedSet.size());
                    }
                }

                if (removedValues != null) {
                    if (multiValuedAttribute) {
                        removedSet.add(AttributeBuilder.build(current.getName(), removedValues));
                        LOG.ok("Removed values present and multiValuedAttribute. Added {0} to removed set.  New size {1}", current.getName(), removedSet.size());
                    } else {
                        modifiedSet.add(AttributeBuilder.build(current.getName(), Collections.emptyList()));
                        LOG.ok("Removed values present and not multiValuedAttribute. Added {0} to modified set.  New size {1}", current.getName(), modifiedSet.size());
                    }
                }

            }
        }
        ConsolidatedValues responseValues = new ConsolidatedValues(modifiedSet, addedSet, removedSet);
        LOG.ok("Consolidated values result: {0}", responseValues.toString());
        return responseValues;
    }

    private static final class ConsolidatedValues {

        final Set<Attribute> modifiedValues;
        final Set<Attribute> addedMultiValues;
        final Set<Attribute> removedMultiValues;

        public ConsolidatedValues(Set<Attribute> modified, Set<Attribute> added,
                                  Set<Attribute> removed) {
            modifiedValues = modified;
            addedMultiValues = added;
            removedMultiValues = removed;
        }

        @Override
        public String toString() {
            StringBuilder builder = new StringBuilder();
            builder.append("addedMultiValues: [");
            for (Attribute current : addedMultiValues) {
                builder.append(current.toString());
            }
            builder.append("],");
            builder.append("modifiedValues: [");
            for (Attribute current : modifiedValues) {
                builder.append(current.toString());
            }
            builder.append("],");
            builder.append("removedMultiValues: [");
            for (Attribute current : removedMultiValues) {
                builder.append(current.toString());
            }
            builder.append("]");
            return builder.toString();
        }
    }

}
