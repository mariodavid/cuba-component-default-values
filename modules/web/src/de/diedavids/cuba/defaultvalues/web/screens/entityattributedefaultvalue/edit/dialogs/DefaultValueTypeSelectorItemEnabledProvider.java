package de.diedavids.cuba.defaultvalues.web.screens.entityattributedefaultvalue.edit.dialogs;

import com.vaadin.server.SerializablePredicate;
import de.diedavids.cuba.defaultvalues.dynamicvalue.DynamicValueProviders;
import de.diedavids.cuba.defaultvalues.entity.EntityAttributeDefaultValue;
import de.diedavids.cuba.defaultvalues.entity.EntityAttributeDefaultValueType;
import de.diedavids.cuba.defaultvalues.service.SessionAttributeService;

import java.util.List;

public class DefaultValueTypeSelectorItemEnabledProvider implements SerializablePredicate<EntityAttributeDefaultValueType> {
    private final EntityAttributeDefaultValue entityAttributeDefaultValue;
    private final DynamicValueProviders dynamicValueProviders;
    private final SessionAttributeService sessionAttributeService;

    public DefaultValueTypeSelectorItemEnabledProvider(
            EntityAttributeDefaultValue entityAttributeDefaultValue,
            DynamicValueProviders dynamicValueProviders,
            SessionAttributeService sessionAttributeService
    ) {
        this.entityAttributeDefaultValue = entityAttributeDefaultValue;
        this.dynamicValueProviders = dynamicValueProviders;
        this.sessionAttributeService = sessionAttributeService;
    }

    @Override
    public boolean test(EntityAttributeDefaultValueType type) {

        switch (type) {
            case DYNAMIC_VALUE: return dynamicValueProvidersAvailable(entityAttributeDefaultValue);
            case SESSION_ATTRIBUTE: return sessionAttributesAvailable();
            default: return true;
        }
    }

    private boolean sessionAttributesAvailable() {
        List<String> availableSessionAttributes = sessionAttributeService.getAvailableSessionAttributes();
        return availableSessionAttributes != null && availableSessionAttributes.size() > 0;
    }


    private boolean dynamicValueProvidersAvailable(EntityAttributeDefaultValue entityAttributeDefaultValue) {
        return dynamicValueProviders.getProvidersFor(entityAttributeDefaultValue.getEntityAttribute()).size() > 0;
    }

}
