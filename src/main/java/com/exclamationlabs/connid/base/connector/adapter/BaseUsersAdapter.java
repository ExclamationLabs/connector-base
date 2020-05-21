package com.exclamationlabs.connid.base.connector.adapter;

import com.exclamationlabs.connid.base.connector.model.GroupIdentityModel;
import com.exclamationlabs.connid.base.connector.model.UserIdentityModel;

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
