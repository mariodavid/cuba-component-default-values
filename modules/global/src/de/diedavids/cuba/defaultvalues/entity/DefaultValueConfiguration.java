package de.diedavids.cuba.defaultvalues.entity;

import com.haulmont.cuba.core.entity.StandardEntity;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Table;
import javax.validation.constraints.NotNull;

@Table(name = "DDCDV_DEFAULT_VALUE_CONFIGURATION")
@Entity(name = "ddcdv_DefaultValueConfiguration")
public class DefaultValueConfiguration extends StandardEntity {
    private static final long serialVersionUID = -2613764553714339761L;

    @NotNull
    @Column(name = "ENTITY", nullable = false)
    protected String entity;

    @NotNull
    @Column(name = "ENTITY_ATTRIBUTE", nullable = false)
    protected String entityAttribute;

    @Column(name = "VALUE_")
    protected String value;

    public String getValue() {
        return value;
    }

    public void setValue(String value) {
        this.value = value;
    }

    public String getEntityAttribute() {
        return entityAttribute;
    }

    public void setEntityAttribute(String entityAttribute) {
        this.entityAttribute = entityAttribute;
    }

    public String getEntity() {
        return entity;
    }

    public void setEntity(String entity) {
        this.entity = entity;
    }
}