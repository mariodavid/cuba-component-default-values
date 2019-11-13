package de.diedavids.cuba.defaultvalues.web.screens.entityattributedefaultvalue.edit.columngenerator;


import de.diedavids.cuba.defaultvalues.entity.EntityAttributeDefaultValue;

public class ScriptColumnGenerator implements DefaultValueTypeColumnGenerator {

    @Override
    public String getUiValue(EntityAttributeDefaultValue entityAttributeDefaultValue) {
        return "...";
    }
}
