package de.diedavids.cuba.defaultvalues.web.screens.entityattributedefaultvalue.edit.columngenerator;


import com.google.common.base.Strings;
import com.haulmont.chile.core.datatypes.Datatype;
import com.haulmont.chile.core.model.MetaClass;
import com.haulmont.chile.core.model.MetaProperty;
import com.haulmont.chile.core.model.Range;
import com.haulmont.cuba.core.entity.Entity;
import com.haulmont.cuba.core.global.*;
import de.diedavids.cuba.defaultvalues.entity.EntityAttributeDefaultValue;
import de.diedavids.cuba.entitysoftreference.EntitySoftReferenceDatatype;

import java.text.ParseException;

public class StaticValueColumnGenerator implements DefaultValueTypeColumnGenerator {

    private final Metadata metadata;
    private final Messages messages;
    private final EntityLoadInfoBuilder entityLoadInfoBuilder;
    private final DataManager dataManager;

    public StaticValueColumnGenerator(Metadata metadata, Messages messages, EntityLoadInfoBuilder entityLoadInfoBuilder, DataManager dataManager) {

        this.metadata = metadata;
        this.messages = messages;
        this.entityLoadInfoBuilder = entityLoadInfoBuilder;
        this.dataManager = dataManager;
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
                return messages.getMessage((Enum) defaultValue);
            } catch (ParseException e) {
                e.printStackTrace();
            }
        }
        else {
            return entityAttributeDefaultValue.getValue();
        }

        return null;
    }


    public Entity convertToEntityAttribute(String value) {


        if (Strings.isNullOrEmpty(value))
            return null;

        EntityLoadInfo entityLoadInfo = entityLoadInfoBuilder.parse(value);

        Entity entity = null;

        if (entityLoadInfo != null) {
            entity = loadEntity(entityLoadInfo);
        }

        return entity;
    }

    private Entity loadEntity(EntityLoadInfo entityLoadInfo) {
        return dataManager.load(
                getLoadContextForForEntityLoadInfo(
                        entityLoadInfo.getMetaClass(),
                        entityLoadInfo.getId()
                )
        );
    }

    protected LoadContext getLoadContextForForEntityLoadInfo(MetaClass metaClass, Object entityId) {
        LoadContext loadContext = LoadContext.create(metaClass.getJavaClass());
        loadContext
                .setId(entityId);
        return loadContext;
    }

    private Datatype determineEntityAttributeDatatype(MetaProperty metaProperty) {
        if (metaProperty.getRange().isDatatype()) {
            return metaProperty.getRange().asDatatype();
        } else if (metaProperty.getRange().isEnum()) {
            return metaProperty.getRange().asEnumeration();
        } else if (metaProperty.getRange().isClass()) {
            return new EntitySoftReferenceDatatype();
        } else {
            return null;
        }
    }


}
