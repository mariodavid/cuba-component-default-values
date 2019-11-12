package de.diedavids.cuba.defaultvalues.web.screens.entityattributedefaultvalue.edit.columngenerator;


import com.haulmont.cuba.core.global.Messages;
import de.diedavids.cuba.defaultvalues.entity.EntityAttributeDefaultValue;

public class DynamicValueColumnGenerator implements DefaultValueTypeColumnGenerator {

    private final Messages messages;

    public DynamicValueColumnGenerator(Messages messages) {
        this.messages = messages;
    }

    @Override
    public String getUiValue(EntityAttributeDefaultValue entityAttributeDefaultValue) {
        if (entityAttributeDefaultValue.getValue() != null) {
            return messages.getMainMessage("dynamicValueProvider." + entityAttributeDefaultValue.getValue());
        }
        else {
            return null;
        }
    }
}
