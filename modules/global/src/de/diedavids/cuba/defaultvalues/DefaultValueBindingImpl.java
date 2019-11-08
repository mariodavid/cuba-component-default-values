package de.diedavids.cuba.defaultvalues;

import com.haulmont.chile.core.datatypes.Datatype;
import com.haulmont.chile.core.model.MetaClass;
import com.haulmont.chile.core.model.MetaProperty;
import com.haulmont.cuba.core.entity.Entity;
import com.haulmont.cuba.core.global.DataManager;
import com.haulmont.cuba.core.global.Metadata;
import de.diedavids.cuba.defaultvalues.entity.EntityAttributeDefaultValue;
import de.diedavids.cuba.entitysoftreference.EntitySoftReferenceDatatype;
import org.slf4j.Logger;
import org.springframework.stereotype.Component;

import javax.inject.Inject;
import java.text.ParseException;
import java.util.List;

@Component(DefaultValueBinding.NAME)
public class DefaultValueBindingImpl implements DefaultValueBinding {

    @Inject
    protected DataManager dataManager;

    @Inject
    protected Metadata metadata;

    @Inject
    protected Logger log;

    @Override
    public <T extends Entity> T bindDefaultValues(Class<T> entityClass, T entityInstance) {

        MetaClass metaClass = metadata.getClass(entityClass);

        List<EntityAttributeDefaultValue> entityAttributeDefaultValueList = getDefaultConfigurations(metaClass);

        entityAttributeDefaultValueList
                .forEach(defaultValueConfiguration ->
                        bindDefaultValue(entityInstance, metaClass, defaultValueConfiguration)
                );

        return entityInstance;
    }

    private List<EntityAttributeDefaultValue> getDefaultConfigurations(MetaClass metaClass) {

        return dataManager.load(EntityAttributeDefaultValue.class)
                .query("select e from ddcdv_EntityAttributeDefaultValue e where e.entity = :entity")
                .parameter("entity", metaClass)
                .list();
    }

    private <T extends Entity> void bindDefaultValue(T entityInstance, MetaClass metaClass, EntityAttributeDefaultValue entityAttributeDefaultValue) {
        MetaProperty property = entityAttributeDefaultValue.getEntityAttribute();

        if (property.getRange().isDatatype()) {
            bindDatatypeDefaultValue(
                    entityInstance,
                    entityAttributeDefaultValue,
                    property,
                    property.getRange().asDatatype()
            );
        } else if (property.getRange().isEnum()) {
            bindDatatypeDefaultValue(
                    entityInstance,
                    entityAttributeDefaultValue,
                    property,
                    property.getRange().asEnumeration()
            );
        } else if (property.getRange().isClass()) {
            bindEntityDefaultValue(
                    entityInstance,
                    entityAttributeDefaultValue,
                    property
            );
        }

    }

    private <T extends Entity> void bindDatatypeDefaultValue(
            T entityInstance,
            EntityAttributeDefaultValue entityAttributeDefaultValue,
            MetaProperty entityAttribute,
            Datatype<Object> datatype
    ) {

        try {
            Object result = datatype.parse(entityAttributeDefaultValue.getValue());
            entityInstance.setValue(entityAttribute.getName(), result);
        } catch (ParseException e) {
            log.error("The default value from EntityAttributeDefaultValue: {} could not be assigned to attribute: {}. Error: {}", entityAttributeDefaultValue, entityAttribute, e.getMessage());
        }
    }

    private <T extends Entity> void bindEntityDefaultValue(
            T entityInstance,
            EntityAttributeDefaultValue entityAttributeDefaultValue,
            MetaProperty entityAttribute
    ) {

        try {
            Datatype datatype = new EntitySoftReferenceDatatype();
            Object result = datatype.parse(entityAttributeDefaultValue.getValue());
            entityInstance.setValue(entityAttribute.getName(), result);
        } catch (ParseException e) {
            log.error("The default value from EntityAttributeDefaultValue: {} could not be assigned to attribute: {}. Error: {}", entityAttributeDefaultValue, entityAttribute, e.getMessage());
        }
    }
}
