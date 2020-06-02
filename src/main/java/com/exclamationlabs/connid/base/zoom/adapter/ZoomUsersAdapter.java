package com.exclamationlabs.connid.base.zoom.adapter;

import com.exclamationlabs.connid.base.connector.adapter.BaseUsersAdapter;
import com.exclamationlabs.connid.base.zoom.model.ZoomGroup;
import com.exclamationlabs.connid.base.zoom.model.ZoomUser;
import org.identityconnectors.framework.common.objects.Attribute;
import org.identityconnectors.framework.common.objects.AttributeBuilder;
import org.identityconnectors.framework.common.objects.ConnectorObject;

import java.util.Set;

import static com.exclamationlabs.connid.base.zoom.attribute.ZoomUserAttribute.*;

public class ZoomUsersAdapter extends BaseUsersAdapter<ZoomUser, ZoomGroup> {
    @Override
    protected ZoomUser constructUser(Set<Attribute> attributes, boolean creation) {
        ZoomUser user = new ZoomUser();
        user.setId(getIdentityIdAttributeValue(attributes));
        user.setEmail(getIdentityNameAttributeValue(attributes));

        user.setFirstName(getSingleAttributeValue(String.class, attributes, FIRST_NAME));
        user.setLastName(getSingleAttributeValue(String.class, attributes, LAST_NAME));
        user.setEmail(getSingleAttributeValue(String.class, attributes, EMAIL));
        user.setLanguage(getSingleAttributeValue(String.class, attributes, LANGUAGE));
        user.setTimezone(getSingleAttributeValue(String.class, attributes, TIME_ZONE));
        user.setStatus(getSingleAttributeValue(String.class, attributes, STATUS));
        user.setType(getSingleAttributeValue(Integer.class, attributes, TYPE));
        return user;
    }

    @Override
    protected ConnectorObject constructConnectorObject(ZoomUser user) {
        return getConnectorObjectBuilder(user)
                .addAttribute(AttributeBuilder.build(USER_ID.name(), user.getId()))
                .addAttribute(AttributeBuilder.build(EMAIL.name(), user.getEmail()))
                .addAttribute(AttributeBuilder.build(FIRST_NAME.name(), user.getFirstName()))
                .addAttribute(AttributeBuilder.build(LAST_NAME.name(), user.getLastName()))
                .addAttribute(AttributeBuilder.build(LANGUAGE.name(), user.getLanguage()))
                .addAttribute(AttributeBuilder.build(TIME_ZONE.name(), user.getTimezone()))
                .addAttribute(AttributeBuilder.build(STATUS.name(), user.getStatus()))
                .addAttribute(AttributeBuilder.build(TYPE.name(), user.getType()))
                .addAttribute(AttributeBuilder.build(PHONE_NUMBER.name(), user.getPhoneNumber()))
                .addAttribute(AttributeBuilder.build(CREATED_AT.name(), user.getCreatedAt()))
                .addAttribute(AttributeBuilder.build(LAST_LOGIN_TIME.name(), user.getLastLoginTime()))
                .addAttribute(AttributeBuilder.build(VERIFIED.name(), user.getVerified()))
                .addAttribute(AttributeBuilder.build(PERSONAL_MEETING_ID.name(), user.getPersonalMeetingId()))
                .addAttribute(AttributeBuilder.build(GROUP_IDS.name(), user.getGroupIds()))
                .build();
    }
}
