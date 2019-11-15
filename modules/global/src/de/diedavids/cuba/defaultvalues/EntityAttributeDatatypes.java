package de.diedavids.cuba.defaultvalues;

import com.haulmont.chile.core.datatypes.Datatype;
import com.haulmont.chile.core.datatypes.DatatypeRegistry;
import com.haulmont.chile.core.model.MetaProperty;
import org.springframework.stereotype.Component;

import javax.inject.Inject;

@Component(EntityAttributeDatatypes.NAME)
public class EntityAttributeDatatypes {
    public static final String NAME = "ddcdv_EntityAttributeDatatypes";

    @Inject
    protected DatatypeRegistry datatypeRegistry;

    public Datatype getEntityAttributeDatatype(MetaProperty metaProperty) {
        if (metaProperty.getRange().isDatatype()) {
            return metaProperty.getRange().asDatatype();
        } else if (metaProperty.getRange().isEnum()) {
            return metaProperty.getRange().asEnumeration();
        } else if (metaProperty.getRange().isClass()) {
            return datatypeRegistry.get("EntitySoftReference");
        } else {
            return null;
        }
    }
}