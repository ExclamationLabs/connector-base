package com.exclamationlabs.connid.base.connector.adapter;

import com.exclamationlabs.connid.base.connector.driver.Driver;
import com.exclamationlabs.connid.base.connector.model.GroupIdentityModel;
import com.exclamationlabs.connid.base.connector.model.UserIdentityModel;

/**
 * Base attribute class describing composition of an Adapter.
 * Since the Adapter is the glue between the Connector (which communicates
 * with Midpoint) and the Driver (which communicates with an external IAM data source),
 * it has to hold a reference to the Driver so it can communicate with it.
 *
 * Do not subclass this type, instead subclass BaseGroupsAdapter or BaseUsersAdapter.
 */
public abstract class BaseAdapter<U extends UserIdentityModel, G extends GroupIdentityModel>
        implements Adapter<U,G>  {

    private Driver<U,G> driver;

    public final void setDriver(Driver<U,G> component) {
        driver = component;
    }

    public final Driver<U,G> getDriver() {
        return driver;
    }
}
