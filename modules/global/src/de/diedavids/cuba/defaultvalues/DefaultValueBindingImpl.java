package de.diedavids.cuba.defaultvalues;

import com.haulmont.chile.core.datatypes.Datatype;
import com.haulmont.chile.core.model.MetaClass;
import com.haulmont.chile.core.model.MetaProperty;
import com.haulmont.cuba.core.entity.Entity;
import com.haulmont.cuba.core.global.*;
import de.diedavids.cuba.defaultvalues.dynamicvalue.DynamicValueProvider;
import de.diedavids.cuba.defaultvalues.entity.EntityAttributeDefaultValue;
import groovy.lang.Binding;
import org.codehaus.groovy.runtime.GStringImpl;
import org.slf4j.Logger;
import org.springframework.stereotype.Component;

import javax.inject.Inject;
import java.text.ParseException;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;

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

    @Inject
    protected List<DynamicValueProvider> dynamicValueProviders;

    @Inject
    protected EntityAttributeDatatypes entityAttributeDatatypes;

    @Inject
    protected TimeSource timeSource;

    @Inject
    protected BeanLocator beanLocator;

    @Override
    public <T extends Entity> T bindDefaultValues(Class<T> entityClass, T entityInstance) {

        MetaClass metaClass = metadata.getClass(entityClass);

        List<EntityAttributeDefaultValue> entityAttributeDefaultValueList = getDefaultConfigurations(metaClass);

        entityAttributeDefaultValueList
                .forEach(entityAttributeDefaultValue ->
                        bindDefaultValue(entityInstance, entityAttributeDefaultValue)
                );

        return entityInstance;
    }

    private List<EntityAttributeDefaultValue> getDefaultConfigurations(MetaClass metaClass) {

        return dataManager.load(EntityAttributeDefaultValue.class)
                .query("select e from ddcdv_EntityAttributeDefaultValue e where e.entity = :entity")
                .parameter("entity", metaClass)
                .list();
    }

    private <T extends Entity> void bindDefaultValue(T entityInstance, EntityAttributeDefaultValue entityAttributeDefaultValue) {
        MetaProperty property = entityAttributeDefaultValue.getEntityAttribute();

        bindDefaultValue(
                entityInstance,
                entityAttributeDefaultValue,
                property,
                entityAttributeDatatypes.getEntityAttributeDatatype(property)
        );

    }

    private <T extends Entity> void bindDefaultValue(
            T entityInstance,
            EntityAttributeDefaultValue entityAttributeDefaultValue,
            MetaProperty entityAttribute,
            Datatype<Object> datatype
    ) {

        try {
            Object result = getConfiguredDefaultValue(entityAttributeDefaultValue, datatype);
            entityInstance.setValue(entityAttribute.getName(), result);
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
                return getScriptDefaultValue(entityAttributeDefaultValue, datatype);
            case DYNAMIC_VALUE:
                return getDynamicDefaultValue(entityAttributeDefaultValue, datatype);
            default:
                return getStaticDefaultValue(entityAttributeDefaultValue, datatype);
        }
    }

    private Object getStaticDefaultValue(EntityAttributeDefaultValue entityAttributeDefaultValue, Datatype<Object> datatype) {
        return formatFromDatatypeToObject(
                entityAttributeDefaultValue.getValue(),
                datatype
        );
    }

    private Object getDynamicDefaultValue(EntityAttributeDefaultValue entityAttributeDefaultValue, Datatype<Object> datatype) {

        Optional<DynamicValueProvider> foundDynamicDefaultValue = dynamicValueProviders.stream()
                .filter(dynamicValueProvider -> dynamicValueProvider.getCode().equals(entityAttributeDefaultValue.getValue()))
                .findFirst();

        if (foundDynamicDefaultValue.isPresent()) {

            MetaProperty entityAttribute = entityAttributeDefaultValue.getEntityAttribute();
            DynamicValueProvider dynamicValueProvider = foundDynamicDefaultValue.get();

            if (dynamicValueProvider.getReturnType().isAssignableFrom(entityAttribute.getJavaType())) {
                return dynamicValueProvider.get();
            }
            else {
                log.error(
                        "Mismatch of configured types. Type of MetaProperty: '{}' is '{}', return type of '{}' is '{}'",
                        entityAttribute.toString(),
                        entityAttribute.getJavaType().getName(),
                        dynamicValueProvider.getCode(),
                        dynamicValueProvider.getReturnType()

                );
                return null;
            }
        }

        return null;
    }

    private Object getScriptDefaultValue(EntityAttributeDefaultValue entityAttributeDefaultValue, Datatype<Object> datatype) {

        try {
            Object result = scripting.evaluateGroovy(
                    entityAttributeDefaultValue.getValue(),
                    getScriptBinding()
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


    private Binding getScriptBinding() {

        Map<String, Object> bindingValues = new HashMap<>();

        bindingValues.put("beanLocator", beanLocator);
        bindingValues.put("dataManager", dataManager);
        bindingValues.put("timeSource", timeSource);
        bindingValues.put("metadata", metadata);

        return new Binding(bindingValues);
    }
    private Object formatFromDatatypeToObject(String defaultValue, Datatype<Object> datatype) {
        try {
            return datatype.parse(defaultValue);
        } catch (ParseException e) {
            log.error("Unable to parse default value: {} with datatype: {}. Error message: {}", defaultValue, datatype, e.getMessage());
            log.info("Error details:", e);
        }
        return false;
    }

}
