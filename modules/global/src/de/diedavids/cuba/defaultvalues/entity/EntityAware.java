package de.diedavids.cuba.defaultvalues.entity;

import com.haulmont.chile.core.model.MetaClass;

public interface EntityAware {

    MetaClass getEntity();

    void setEntity(MetaClass entity);
}
