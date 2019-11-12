package de.diedavids.cuba.defaultvalues;

import com.haulmont.chile.core.datatypes.Datatype;
import com.haulmont.chile.core.model.MetaClass;
import com.haulmont.chile.core.model.MetaProperty;
import com.haulmont.cuba.core.entity.Entity;
import com.haulmont.cuba.core.global.DataManager;
import com.haulmont.cuba.core.global.Metadata;
import com.haulmont.cuba.core.global.Scripting;
import com.haulmont.cuba.core.global.UserSessionSource;
import de.diedavids.cuba.defaultvalues.entity.EntityAttributeDefaultValue;
import de.diedavids.cuba.entitysoftreference.EntitySoftReferenceDatatype;
import groovy.lang.Binding;
import org.codehaus.groovy.runtime.GStringImpl;
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
    protected UserSessionSource userSessionSource;

    @Inject
    protected Logger log;

    @Inject
    protected Scripting scripting;

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


        Object configuredDefaultValue = getConfiguredDefaultValue(entityAttributeDefaultValue, datatype);
        try {
            entityInstance.setValue(entityAttribute.getName(), configuredDefaultValue);
        }
        catch (ClassCastException e) {
            log.error("Returned value from Script evaluation cannot be assigned to attribute. Error message: {}",
                    e.getMessage()
            );
            log.debug("Error details:", e);
        }

    }

    private Object getConfiguredDefaultValue(EntityAttributeDefaultValue entityAttributeDefaultValue, Datatype<Object> datatype) {

        switch (entityAttributeDefaultValue.getType()) {
            case SESSION_ATTRIBUTE:
                return getSessionAttributeValue(entityAttributeDefaultValue, datatype);
            case SCRIPT:
                return getScriptAttributeValue(entityAttributeDefaultValue, datatype);
            default:
                return getStaticAttributeValue(entityAttributeDefaultValue, datatype);
        }
    }

    private Object getStaticAttributeValue(EntityAttributeDefaultValue entityAttributeDefaultValue, Datatype<Object> datatype) {
        return formatFromDatatypeToObject(
                entityAttributeDefaultValue.getValue(),
                datatype
        );
    }

    private Object getScriptAttributeValue(EntityAttributeDefaultValue entityAttributeDefaultValue, Datatype<Object> datatype) {

        try {
            Object result = scripting.evaluateGroovy(
                    entityAttributeDefaultValue.getValue(),
                    new Binding()
            );

            if (result instanceof GStringImpl) {
                return result.toString();
            }
            else {
                return result;
            }
        }
        catch (Exception e) {
            log.error("Error while evaluating default value from: '{}' for EntityAttributeDefaultValue: {}. Error message: {}",
                    entityAttributeDefaultValue.getValue(),
                    entityAttributeDefaultValue,
                    e.getMessage()
            );
            log.debug("Error details:", e);
            return null;
        }
    }

    private Object getSessionAttributeValue(EntityAttributeDefaultValue entityAttributeDefaultValue, Datatype<Object> datatype) {
        if (entityAttributeDefaultValue.getValue().startsWith(":session$")) {
            String sessionAttributeValue = userSessionSource.getUserSession().getAttribute(
                    entityAttributeDefaultValue.getValue().replace(":session$", "")
            );
            return formatFromDatatypeToObject(sessionAttributeValue, datatype);
        } else {
            return null;
        }
    }

    private Object formatFromDatatypeToObject(String defaultValue, Datatype<Object> datatype) {
        try {
            return datatype.parse(defaultValue);
        } catch (ParseException e) {
            e.printStackTrace();
        }
        return false;
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
