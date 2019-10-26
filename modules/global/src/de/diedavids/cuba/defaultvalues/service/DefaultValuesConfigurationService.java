package de.diedavids.cuba.defaultvalues.service;

import de.diedavids.cuba.defaultvalues.entity.DefaultValueConfiguration;

import java.util.List;

public interface DefaultValuesConfigurationService {
    String NAME = "ddcdv_DefaultValuesConfigurationService";


    List<DefaultValueConfiguration> findByEntityAndEntityAttribute(String entity, String entityAttribute);

    List<DefaultValueConfiguration> findByEntity(String entity);

}