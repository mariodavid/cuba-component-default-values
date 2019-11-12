package de.diedavids.cuba.defaultvalues.entity;

import com.haulmont.chile.core.annotations.NamePattern;
import de.diedavids.cuba.metadataextensions.entity.EntityAttributeAwareStandardEntity;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Lob;
import javax.persistence.Table;
import javax.validation.constraints.NotNull;

@NamePattern("%s: %s|entityAttribute,value")
@Table(name = "DDCDV_ENTITY_ATTRIBUTE_DEFAULT_VALUE")
@Entity(name = "ddcdv_EntityAttributeDefaultValue")
public class EntityAttributeDefaultValue extends EntityAttributeAwareStandardEntity {
    private static final long serialVersionUID = -2613764553714339761L;

    @Lob
    @Column(name = "VALUE_")
    protected String value;

    @NotNull
    @Column(name = "TYPE_", nullable = false)
    protected String type;

    public EntityAttributeDefaultValueType getType() {
        return type == null ? null : EntityAttributeDefaultValueType.fromId(type);
    }

    public void setType(EntityAttributeDefaultValueType type) {
        this.type = type == null ? null : type.getId();
    }

    public String getValue() {
        return value;
    }

    public void setValue(String value) {
        this.value = value;
    }
}