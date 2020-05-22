package com.exclamationlabs.connid.base.connector.adapter;

import com.exclamationlabs.connid.base.connector.model.GroupIdentityModel;
import com.exclamationlabs.connid.base.connector.model.UserIdentityModel;

/**
 * Base users adapter that needs to be subclassed in order to map a specific user model
 * data type to ConnId attributes and vice versa.  This extends BaseAdapter
 * so that the Adapter has composition and can reference the driver.
 */
public abstract class BaseUsersAdapter<U extends UserIdentityModel, G extends GroupIdentityModel>
        extends BaseAdapter<U,G> implements UsersAdapter<U,G> {

    @Override
    public boolean groupAdditionControlledByUpdate() {
        return false;
    }

    @Override
    public boolean groupRemovalControlledByUpdate() {
        return false;
    }
}
