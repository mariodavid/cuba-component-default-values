package de.diedavids.cuba.defaultvalues.web.screens.entityattributedefaultvalue.edit.columngenerator;


import de.diedavids.cuba.defaultvalues.entity.EntityAttributeDefaultValue;

public class SessionAttributeColumnGenerator implements DefaultValueTypeColumnGenerator {

    @Override
    public String getUiValue(EntityAttributeDefaultValue entityAttributeDefaultValue) {
        if (entityAttributeDefaultValue != null && entityAttributeDefaultValue.getValue() != null) {
            return entityAttributeDefaultValue.getValue().replaceAll("\\:session\\$", "");
        }
        else {
            return null;
        }
    }

}
