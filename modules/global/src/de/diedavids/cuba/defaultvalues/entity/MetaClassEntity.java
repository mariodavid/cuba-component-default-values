package de.diedavids.cuba.defaultvalues.entity;

import com.haulmont.chile.core.annotations.MetaClass;
import com.haulmont.chile.core.annotations.MetaProperty;
import com.haulmont.chile.core.annotations.NamePattern;
import com.haulmont.cuba.core.entity.BaseUuidEntity;

@NamePattern("%s|name")
@MetaClass(name = "ddcdv_MetaClassEntity")
public class MetaClassEntity extends BaseUuidEntity {
    private static final long serialVersionUID = 7143382196961171961L;

    @MetaProperty
    protected String name;

    @MetaProperty
    protected String description;

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }
}