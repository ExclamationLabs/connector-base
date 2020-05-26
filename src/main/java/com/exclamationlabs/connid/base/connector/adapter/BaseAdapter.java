package com.exclamationlabs.connid.base.connector.adapter;

import com.exclamationlabs.connid.base.connector.driver.Driver;
import com.exclamationlabs.connid.base.connector.model.GroupIdentityModel;
import com.exclamationlabs.connid.base.connector.model.IdentityModel;
import com.exclamationlabs.connid.base.connector.model.UserIdentityModel;
import org.apache.commons.lang3.StringUtils;
import org.identityconnectors.framework.common.exceptions.InvalidAttributeValueException;
import org.identityconnectors.framework.common.objects.*;

import java.util.Optional;
import java.util.Set;

/**
 * Base attribute class describing composition of an Adapter.
 * Since the Adapter is the glue between the Connector (which communicates
 * with Midpoint) and the Driver (which communicates with an external IAM data source),
 * it has to hold a reference to the Driver so it can communicate with it.
 *
 * Do not subclass this type, instead subclass BaseGroupsAdapter or BaseUsersAdapter.
 */
public abstract class BaseAdapter<U extends UserIdentityModel, G extends GroupIdentityModel> {

    private Driver<U,G> driver;

    protected abstract ObjectClass getType();

    /**
     * Service a request from IAM system to create the type on the destination system.
     * @param attributes Attributes from IAM system that map to data elements needed
     *                   by the destination system for type creation.
     * @return new unique identifier for newly created type
     */
    public abstract Uid create(Set<Attribute> attributes);

    /**
     * Service a request from IAM system to update the type on the destination system.
     * @param uid Unique identifier for the data item being updated.
     * @param attributes Attributes from IAM system that map to data elements supported
     *                   by the destination system to update the type.
     * @return unique identifier applicable to the type that was just updated
     */
    public abstract Uid update(Uid uid, Set<Attribute> attributes);

    /**
     * Service a request from IAM system to delete the type on the destination system.
     * @param uid Unique identifier for the data item to be deleted.
     */
    public abstract void delete(Uid uid);

    /**
     * Service a request from IAM to get one, some, or all items of a data type from the destination system.
     * @param query Query string to help identify which item(s) need to be retrieved.
     * @param resultsHandler ConnId ResultsHandler object used to send result data back to IAM system.
     */
    public abstract void get(String query, ResultsHandler resultsHandler);

    public final void setDriver(Driver<U,G> component) {
        driver = component;
    }

    public final Driver<U,G> getDriver() {
        return driver;
    }


    protected final ConnectorObjectBuilder getConnectorObjectBuilder(IdentityModel identity) {
        return new ConnectorObjectBuilder()
                .setObjectClass(getType())
                .setUid(identity.getIdentityIdValue())
                .setName(identity.getIdentityNameValue());
    }

    protected final <T> T getSingleAttributeValue(Class<T> returnType, Set<Attribute> attributes, Enum<?> enumValue) {
        if (attributes == null) {
            return null;
        }
        Optional<Attribute> correctAttribute =
                attributes.stream().filter(current -> current.getName().equals(enumValue.toString())).findFirst();
        Object value = correctAttribute.map(this::readAttributeValue).orElse(null);
        if (value == null) {
            return null;
        } else {
            if (returnType != value.getClass()) {
                throw new InvalidAttributeValueException("Invalid data type for attribute " + enumValue.name() + "; received " +
                        value.getClass().getName() + ", expected " + returnType.getName());
            } else {
                return (T) value; // Have to cast, last resort
            }
        }
    }

    protected final Object readAttributeValue(Attribute input) {
        if (input != null && input.getValue() != null && input.getValue().get(0) != null) {
            return input.getValue().get(0);
        } else {
            return null;
        }
    }

    protected final boolean queryAllRecords(String query) {
        return (query == null || StringUtils.isBlank(query) || StringUtils.equalsIgnoreCase(query, "ALL"));
    }
}
