package de.diedavids.cuba.defaultvalues.entity;

import com.haulmont.chile.core.annotations.NamePattern;
import de.diedavids.cuba.metadataextensions.entity.EntityAttributeAwareStandardEntity;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Table;

@NamePattern("%s: %s|entityAttribute,value")
@Table(name = "DDCDV_ENTITY_ATTRIBUTE_DEFAULT_VALUE")
@Entity(name = "ddcdv_EntityAttributeDefaultValue")
public class EntityAttributeDefaultValue extends EntityAttributeAwareStandardEntity {
    private static final long serialVersionUID = -2613764553714339761L;

    @Column(name = "VALUE_")
    protected String value;

    public String getValue() {
        return value;
    }

    public void setValue(String value) {
        this.value = value;
    }
}