package de.diedavids.cuba.defaultvalues.service;

import de.diedavids.cuba.defaultvalues.entity.EntityAttributeDefaultValue;

import java.util.List;

public interface DefaultValuesConfigurationService {
    String NAME = "ddcdv_DefaultValuesConfigurationService";


    List<EntityAttributeDefaultValue> findByEntityAndEntityAttribute(String entity, String entityAttribute);

    List<EntityAttributeDefaultValue> findByEntity(String entity);

}