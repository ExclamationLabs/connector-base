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

import com.exclamationlabs.connid.base.connector.attribute.ConnectorAttribute;
import com.exclamationlabs.connid.base.connector.driver.Driver;
import com.exclamationlabs.connid.base.connector.model.IdentityModel;
import org.apache.commons.lang3.StringUtils;
import org.identityconnectors.framework.common.objects.*;

import java.util.*;

/**
 * Base attribute class describing composition of an Adapter.
 * Since the Adapter is the glue between the Connector (which communicates
 * with Midpoint) and the Driver (which communicates with an external IAM data source),
 * it has to hold a reference to the Driver so it can communicate with it.
 *
 * Adapter subclasses should specify the generic as a specific identity object
 * type, which implements IdentityModel.
 */
public abstract class BaseAdapter<T extends IdentityModel> {

    private Driver driver;

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
     * @return List of ConnectorAttributes associated with this IdentityModel and Adapter.
     */
    public abstract List<ConnectorAttribute> getConnectorAttributes();

    /**
     * Given an input IdentityModel, build the list of ConnId Attributes to
     * be received to Midpoint.
     * @param model IdentityModel implementation holding object data.
     * @return List of ConnId Attribute objects holding name and value information to
     * be received by Midpoint.
     */
    protected abstract List<Attribute> constructAttributes(T model);

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
    @SuppressWarnings({"unused", "unchecked"})

    public final void get(String queryIdentifier, ResultsHandler resultsHandler, OperationOptions options) {
        if (queryAllRecords(queryIdentifier)) {
            // query for all items
            List<IdentityModel> allItems = getDriver().getAll(getIdentityModelClass(), options.getOptions());
            for (IdentityModel item : allItems) {
                resultsHandler.handle(constructConnectorObject((T) item));
            }
        } else {
            // Query for single item
            IdentityModel singleItem = getDriver().getOne(getIdentityModelClass(), queryIdentifier, options.getOptions());
            if (singleItem != null) {
                resultsHandler.handle(constructConnectorObject((T) singleItem));
            }
        }
    }

    public final void setDriver(Driver component) {
        driver = component;
    }

    public final Driver getDriver() {
        return driver;
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
    protected List<String> readAssignments(Set<Attribute> attributes,
                                           Enum<?> attributeName) {
        List<?> data = AdapterValueTypeConverter.getMultipleAttributeValue(
                List.class, attributes, attributeName);

        List<String> ids = new ArrayList<>();
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
        List<Attribute> connectorAttributes = constructAttributes(model);
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

}
