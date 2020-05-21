package com.exclamationlabs.connid.base.connector.adapter;

import com.exclamationlabs.connid.base.connector.model.GroupIdentityModel;
import com.exclamationlabs.connid.base.connector.model.UserIdentityModel;

public abstract class BaseGroupsAdapter<U extends UserIdentityModel, G extends GroupIdentityModel>
        extends BaseAdapter<U,G> implements GroupsAdapter<U,G> {

}
