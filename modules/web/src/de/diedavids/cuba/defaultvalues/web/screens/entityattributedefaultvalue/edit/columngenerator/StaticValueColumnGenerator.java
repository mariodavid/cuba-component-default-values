package de.diedavids.cuba.defaultvalues.web.screens.entityattributedefaultvalue.edit.columngenerator;


import com.haulmont.chile.core.datatypes.Datatype;
import com.haulmont.chile.core.datatypes.DatatypeRegistry;
import com.haulmont.chile.core.model.MetaProperty;
import com.haulmont.chile.core.model.Range;
import com.haulmont.cuba.core.entity.Entity;
import com.haulmont.cuba.core.global.Messages;
import com.haulmont.cuba.core.global.Metadata;
import de.diedavids.cuba.defaultvalues.EntityAttributeDatatypes;
import de.diedavids.cuba.defaultvalues.entity.EntityAttributeDefaultValue;

import java.text.ParseException;

public class StaticValueColumnGenerator implements DefaultValueTypeColumnGenerator {

    private final Metadata metadata;
    private final Messages messages;
    private final EntityAttributeDatatypes entityAttributeDatatypes;
    private final DatatypeRegistry datatypeRegistry;


    public StaticValueColumnGenerator(
            Metadata metadata,
            Messages messages,
            EntityAttributeDatatypes entityAttributeDatatypes,
            DatatypeRegistry datatypeRegistry
    ) {

        this.metadata = metadata;
        this.messages = messages;
        this.entityAttributeDatatypes = entityAttributeDatatypes;
        this.datatypeRegistry = datatypeRegistry;
    }

    @Override
    public String getUiValue(EntityAttributeDefaultValue entityAttributeDefaultValue) {
        Range range = entityAttributeDefaultValue.getEntityAttribute().getRange();


        if (range.isClass()) {
            Entity reference = convertToEntityAttribute(entityAttributeDefaultValue.getValue());
            if (reference != null) {
                return metadata.getTools().getInstanceName(reference);
            }
            else {
                return null;
            }
        }
        else if (range.isEnum()) {

            try {
                Datatype datatype = determineEntityAttributeDatatype(entityAttributeDefaultValue.getEntityAttribute());
                Object defaultValue = datatype.parse(entityAttributeDefaultValue.getValue());

                if (defaultValue != null)  {
                    return messages.getMessage((Enum) defaultValue);
                }
                else {
                    return null;
                }
            } catch (ParseException e) {
                e.printStackTrace();
            }
        }
        else {
            return entityAttributeDefaultValue.getValue();
        }

        return null;
    }


    private Entity convertToEntityAttribute(String value) {

        try {
            return (Entity) datatypeRegistry.get("EntitySoftReference").parse(value);
        } catch (ParseException e) {
            e.printStackTrace();
        }

        return null;
    }


    private Datatype determineEntityAttributeDatatype(MetaProperty metaProperty) {
        return entityAttributeDatatypes.getEntityAttributeDatatype(metaProperty);
    }


}
