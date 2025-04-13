package com.exclamationlabs.connid.base.edition.neo.internal;

import com.exclamationlabs.connid.base.edition.neo.model.IdentityModel;
import lombok.Getter;
import lombok.Setter;

import java.util.HashMap;
import java.util.Map;

@Getter
@Setter
public class IdentityModelAccess {
    private Class<? extends IdentityModel> identityModelClass;
    private Map<String, FieldAccessInfo> fieldAccessInfoMap;

    public IdentityModelAccess() {
        fieldAccessInfoMap = new HashMap<>();
    }
}
