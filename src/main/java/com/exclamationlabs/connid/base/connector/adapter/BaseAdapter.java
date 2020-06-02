package com.exclamationlabs.connid.base.connector.adapter;

import com.exclamationlabs.connid.base.connector.driver.Driver;
import com.exclamationlabs.connid.base.connector.model.GroupIdentityModel;
import com.exclamationlabs.connid.base.connector.model.IdentityModel;
import com.exclamationlabs.connid.base.connector.model.UserIdentityModel;
import org.apache.commons.lang3.StringUtils;
import org.identityconnectors.framework.common.exceptions.InvalidAttributeValueException;
import org.identityconnectors.framework.common.objects.*;

import java.math.BigDecimal;
import java.math.BigInteger;
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

    protected static String getIdentityNameAttributeValue(Set<Attribute> attributes) {
        return getIdentityFixedAttributeValue(attributes, Name.NAME);
    }

    protected static String getIdentityIdAttributeValue(Set<Attribute> attributes) {
        return getIdentityFixedAttributeValue(attributes, Uid.NAME);
    }

    protected static <T> T getSingleAttributeValue(Class<T> returnType, Set<Attribute> attributes, Enum<?> enumValue) {
        return getAttributeValue(returnType, attributes, enumValue.toString(), true);
    }

    protected static <T> T getAttributeValue(Class<T> returnType, Set<Attribute> attributes, String attributeName, boolean singleValue) {
        if (attributes == null) {
            return null;
        }
        Optional<Attribute> correctAttribute =
                attributes.stream().filter(current -> current.getName().equals(attributeName)).findFirst();
        Object value;
        if (singleValue) {
            value = correctAttribute.map(BaseAdapter::readSingleAttributeValue).orElse(null);
        } else {
            value = correctAttribute.map(BaseAdapter::readMultipleAttributeValue).orElse(null);
        }
        if (value == null) {
            return null;
        } else {
            if (value instanceof String) {
                return (T) convertStringToType(returnType, value.toString());
            }
            if (returnType != value.getClass()) {
                throw new InvalidAttributeValueException("Invalid data type for attribute " + attributeName + "; received " +
                        value.getClass().getName() + ", expected " + returnType.getName());
            }
            return (T) value; // Have to cast, last resort

        }
    }

    protected static <T> Object convertStringToType(Class<T> returnType, String value) {

        if (returnType == String.class) {
            return value;
        }
        try {
            if (returnType == Integer.class) {
                return Integer.parseInt(value);
            }
            if (returnType == BigDecimal.class) {
                return new BigDecimal(value);
            }
            if (returnType == BigInteger.class) {
                return new BigInteger(value);
            }
            if (returnType == Boolean.class) {
                return Boolean.parseBoolean(value);
            }
            if (returnType == Byte.class) {
                return Byte.parseByte(value);
            }
            if (returnType == Character.class) {
                if (value != null && (!value.isEmpty())) {
                    char[] characters = new char[1];
                    value.getChars(0, 1, characters, 0);
                    return characters[0];
                }
                throw new InvalidAttributeValueException("Empty/null string cannot convert to character type");
            }
            if (returnType == Double.class) {
                return Double.parseDouble(value);
            }
            if (returnType == Long.class) {
                return Long.parseLong(value);
            }
            if (returnType == Float.class) {
                return Float.parseFloat(value);
            }
        } catch (NumberFormatException nfe) {
            throw new InvalidAttributeValueException("Invalid type " + returnType + " cannot be parsed from string", nfe);
        }
        throw new InvalidAttributeValueException("Unexpected return type " + returnType);
    }

    protected static Object readMultipleAttributeValue(Attribute input) {
        return readAttributeValue(input, false);
    }

    protected static Object readSingleAttributeValue(Attribute input) {
        return readAttributeValue(input, true);
    }

    protected static Object readAttributeValue(Attribute input, boolean readSingleValue) {
        if (input != null && input.getValue() != null && input.getValue().get(0) != null) {
            return readSingleValue ? input.getValue().get(0) : input.getValue();
        } else {
            return null;
        }
    }

    protected final boolean queryAllRecords(String query) {
        return (query == null || StringUtils.isBlank(query) || StringUtils.equalsIgnoreCase(query, "ALL"));
    }

    private static String getIdentityFixedAttributeValue(Set<Attribute> attributes, String identitiyName) {
        if (attributes == null) {
            return null;
        }
        Optional<Attribute> correctAttribute =
                attributes.stream().filter(current -> current.getName().equals(identitiyName)).findFirst();
        Object value = correctAttribute.map(BaseAdapter::readSingleAttributeValue).orElse(null);
        if (value == null) {
            return null;
        } else {
            return value.toString();
        }
    }
}
