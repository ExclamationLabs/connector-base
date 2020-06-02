package com.exclamationlabs.connid.base.zoom;

import com.exclamationlabs.connid.base.connector.BaseConnector;
import com.exclamationlabs.connid.base.connector.attribute.ConnectorAttributeMapBuilder;
import com.exclamationlabs.connid.base.connector.authenticator.JWTAuthenticator;
import com.exclamationlabs.connid.base.zoom.adapter.ZoomGroupsAdapter;
import com.exclamationlabs.connid.base.zoom.adapter.ZoomUsersAdapter;
import com.exclamationlabs.connid.base.zoom.attribute.ZoomGroupAttribute;
import com.exclamationlabs.connid.base.zoom.attribute.ZoomUserAttribute;
import com.exclamationlabs.connid.base.zoom.configuration.ZoomConfiguration;
import com.exclamationlabs.connid.base.zoom.driver.rest.ZoomDriver;
import com.exclamationlabs.connid.base.zoom.model.ZoomGroup;
import com.exclamationlabs.connid.base.zoom.model.ZoomUser;
import org.identityconnectors.framework.spi.ConnectorClass;

import static com.exclamationlabs.connid.base.connector.attribute.ConnectorAttributeDataType.INTEGER;
import static com.exclamationlabs.connid.base.zoom.attribute.ZoomGroupAttribute.*;
import static com.exclamationlabs.connid.base.zoom.attribute.ZoomUserAttribute.*;
import static com.exclamationlabs.connid.base.connector.attribute.ConnectorAttributeDataType.STRING;
import static org.identityconnectors.framework.common.objects.AttributeInfo.Flags.*;

@ConnectorClass(displayNameKey = "zoom.connector.display", configurationClass = ZoomConfiguration.class)
public class ZoomConnector extends BaseConnector<ZoomUser, ZoomGroup> {

    public ZoomConnector() {

        setAuthenticator(new JWTAuthenticator());
        setDriver(new ZoomDriver());
        setUsersAdapter(new ZoomUsersAdapter());
        setGroupsAdapter(new ZoomGroupsAdapter());
        setUserAttributes( new ConnectorAttributeMapBuilder<>(ZoomUserAttribute.class)
                .add(USER_ID, STRING, NOT_UPDATEABLE)
                .add(FIRST_NAME, STRING)
                .add(LAST_NAME, STRING)
                .add(EMAIL, STRING)
                .add(PASSWORD, STRING, NOT_UPDATEABLE)
                .add(LANGUAGE, STRING)
                .add(TIME_ZONE, STRING)
                .add(PHONE_NUMBER, STRING)
                .add(STATUS, STRING)
                .add(TYPE, INTEGER, NOT_UPDATEABLE)
                .add(CREATED_AT, STRING, NOT_UPDATEABLE)
                .add(LAST_LOGIN_TIME, STRING, NOT_UPDATEABLE)
                .add(VERIFIED, STRING, NOT_UPDATEABLE)
                .add(PERSONAL_MEETING_ID, STRING, NOT_UPDATEABLE)
                .add(GROUP_IDS, STRING, NOT_UPDATEABLE)
                .build());
        setGroupAttributes(new ConnectorAttributeMapBuilder<>(ZoomGroupAttribute.class)
                .add(GROUP_ID, STRING, NOT_UPDATEABLE)
                .add(GROUP_NAME, STRING, REQUIRED)
                .add(TOTAL_MEMBERS, INTEGER, NOT_UPDATEABLE)
                .build());
    }

}
