package de.diedavids.cuba.defaultvalues.entity;

import com.haulmont.chile.core.datatypes.impl.EnumClass;

import javax.annotation.Nullable;


public enum EntityAttributeDefaultValueType implements EnumClass<String> {

    STATIC_VALUE("STATIC_VALUE"),
    DYNAMIC_VALUE("DYNAMIC_VALUE"),
    SESSION_ATTRIBUTE("SESSION_ATTRIBUTE"),
    SCRIPT("SCRIPT");

    private String id;

    EntityAttributeDefaultValueType(String value) {
        this.id = value;
    }

    public String getId() {
        return id;
    }

    @Nullable
    public static EntityAttributeDefaultValueType fromId(String id) {
        for (EntityAttributeDefaultValueType at : EntityAttributeDefaultValueType.values()) {
            if (at.getId().equals(id)) {
                return at;
            }
        }
        return null;
    }
}