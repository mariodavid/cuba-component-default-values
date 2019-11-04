package de.diedavids.cuba.defaultvalues.entity;

import com.haulmont.chile.core.annotations.MetaProperty;
import com.haulmont.chile.core.model.MetaClass;
import com.haulmont.cuba.core.entity.StandardEntity;
import de.diedavids.cuba.defaultvalues.metadata.MetaClassConverter;

import javax.persistence.Column;
import javax.persistence.Convert;
import javax.persistence.Entity;
import javax.persistence.Table;
import javax.validation.constraints.NotNull;

@Table(name = "DDCDV_DEFAULT_VALUE_CONFIGURATION")
@Entity(name = "ddcdv_DefaultValueConfiguration")
public class DefaultValueConfiguration extends EntityAttributeAwareStandardEntity {
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