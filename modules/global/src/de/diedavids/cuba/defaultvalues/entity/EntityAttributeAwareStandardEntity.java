package de.diedavids.cuba.defaultvalues.entity;

import com.haulmont.chile.core.annotations.MetaProperty;
import com.haulmont.cuba.core.entity.StandardEntity;
import de.diedavids.cuba.metadataextensions.converter.MetaPropertyConverter;

import javax.persistence.Column;
import javax.persistence.Convert;
import javax.persistence.MappedSuperclass;

@MappedSuperclass
public class EntityAttributeAwareStandardEntity extends EntityAwareStandardEntity implements EntityAttributeAware {
    private static final long serialVersionUID = 1095944424907519563L;


    @Convert(converter = MetaPropertyConverter.class)
    @MetaProperty(datatype = "MetaProperty")
    @Column(name = "ENTITY_ATTRIBUTE")
    protected com.haulmont.chile.core.model.MetaProperty entityAttribute;

    @Override
    public com.haulmont.chile.core.model.MetaProperty getEntityAttribute() {
        return entityAttribute;
    }

    @Override
    public void setEntityAttribute(com.haulmont.chile.core.model.MetaProperty entityAttribute) {
        this.entityAttribute = entityAttribute;
    }

}