package de.diedavids.cuba.defaultvalues.entity;

import com.haulmont.chile.core.model.MetaProperty;

public interface EntityAttributeAware extends EntityAware {

    MetaProperty getEntityAttribute();

    void setEntityAttribute(MetaProperty entityAttribute);

}
