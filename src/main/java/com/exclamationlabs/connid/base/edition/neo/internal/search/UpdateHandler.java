package com.exclamationlabs.connid.base.edition.neo.internal.search;

import com.exclamationlabs.connid.base.connector.configuration.ConnectorConfiguration;
import com.exclamationlabs.connid.base.connector.results.ResultsFilter;
import com.exclamationlabs.connid.base.connector.results.ResultsPaginator;
import com.exclamationlabs.connid.base.edition.neo.driver.Driver;
import com.exclamationlabs.connid.base.edition.neo.driver.FullAccessDriver;
import com.exclamationlabs.connid.base.edition.neo.driver.FullAccessInvocator;
import com.exclamationlabs.connid.base.edition.neo.driver.Invocator;
import com.exclamationlabs.connid.base.edition.neo.internal.model.ModelReader;
import com.exclamationlabs.connid.base.edition.neo.model.IdentityModel;
import org.identityconnectors.framework.common.exceptions.ConnectorException;
import org.identityconnectors.framework.common.objects.Attribute;
import org.identityconnectors.framework.common.objects.AttributeDelta;
import org.identityconnectors.framework.common.objects.ConnectorObject;
import org.identityconnectors.framework.common.objects.ConnectorObjectBuilder;
import org.identityconnectors.framework.common.objects.ObjectClass;
import org.identityconnectors.framework.common.objects.OperationOptions;
import org.identityconnectors.framework.common.objects.ResultsHandler;
import org.identityconnectors.framework.common.objects.Uid;
import org.identityconnectors.framework.common.objects.filter.EqualsFilter;
import org.identityconnectors.framework.common.objects.filter.Filter;

import java.util.Collections;
import java.util.Set;

public class UpdateHandler<T extends ConnectorConfiguration> {


    public Uid create(T configuration,
                                                              Class<? extends IdentityModel> identityModelClass,
                                                              Driver<T> driver,
                    Invocator<T, Driver<T>, ?> invocator, ObjectClass objectClass, OperationOptions operationOptions) {
        checkForFullAccess(identityModelClass, driver, invocator, "Create");

        return null;
    }

    public void update(T configuration, Class<? extends IdentityModel> identityModelClass, Driver<T> driver,
                       Invocator<T, Driver<T>, ?> invocator, Uid uid, Set<AttributeDelta> attributeModifications,
                       ObjectClass objectClass, OperationOptions operationOptions) {
        checkForFullAccess(identityModelClass, driver, invocator, "Update");
    }

    public void delete(T configuration, Class<? extends IdentityModel> identityModelClass, Driver<T> driver,
                       Invocator<T, Driver<T>, ?> invocator, Uid uid, ObjectClass objectClass, OperationOptions operationOptions) {
        checkForFullAccess(identityModelClass, driver, invocator, "Delete");
    }

    private void checkForFullAccess(final Class<? extends IdentityModel> identityModelClass,
                                    final Driver<T> driver, final Invocator<T, Driver<T>,?> invocator, final String operation) {
        if (invocator == null) {
            if (!(driver instanceof FullAccessDriver)) {
                throw new ConnectorException(
                        String.format("Driver %s does not support full access for %s to %s", driver.getClass().getSimpleName(), operation,
                                identityModelClass.getSimpleName()));
            }
        } else {
            if (!(invocator instanceof FullAccessInvocator)) {
                throw new ConnectorException(
                        String.format("Invocator %s does not support full access for %s to %s", invocator.getClass().getSimpleName(), operation,
                                identityModelClass.getSimpleName()));
            }
        }
    }


}
