package com.exclamationlabs.connid.base.zoom.adapter;

import com.exclamationlabs.connid.base.connector.adapter.BaseGroupsAdapter;
import com.exclamationlabs.connid.base.zoom.model.ZoomGroup;
import com.exclamationlabs.connid.base.zoom.model.ZoomUser;
import org.identityconnectors.framework.common.objects.Attribute;
import org.identityconnectors.framework.common.objects.AttributeBuilder;
import org.identityconnectors.framework.common.objects.ConnectorObject;

import static com.exclamationlabs.connid.base.zoom.attribute.ZoomGroupAttribute.*;

import java.util.Set;

public class ZoomGroupsAdapter extends BaseGroupsAdapter<ZoomUser, ZoomGroup> {


    @Override
    protected ZoomGroup constructGroup(Set<Attribute> attributes, boolean creation) {
        ZoomGroup group = new ZoomGroup();
        group.setId(getIdentityIdAttributeValue(attributes));
        group.setName(getIdentityNameAttributeValue(attributes));

        group.setName(getSingleAttributeValue(String.class, attributes, GROUP_NAME));
        return group;
    }

    @Override
    protected ConnectorObject constructConnectorObject(ZoomGroup group) {
        return getConnectorObjectBuilder(group)
                .addAttribute(AttributeBuilder.build(GROUP_ID.name(), group.getId()))
                .addAttribute(AttributeBuilder.build(GROUP_NAME.name(), group.getName()))
                .addAttribute(AttributeBuilder.build(TOTAL_MEMBERS.name(), group.getTotalMembers()))
                .build();
    }
}
