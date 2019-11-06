package de.diedavids.cuba.defaultvalues.service;

import de.diedavids.cuba.defaultvalues.core.repositories.DefaultValueConfigurationRepository;
import de.diedavids.cuba.defaultvalues.entity.EntityAttributeDefaultValue;
import org.springframework.stereotype.Service;

import javax.inject.Inject;
import java.util.List;

@Service(DefaultValuesConfigurationService.NAME)
public class DefaultValuesConfigurationServiceBean implements DefaultValuesConfigurationService {


    @Inject
    DefaultValueConfigurationRepository repository;

    @Override
    public List<EntityAttributeDefaultValue> findByEntityAndEntityAttribute(String entity, String entityAttribute) {
        return repository.findByEntityAndEntityAttribute(entity, entityAttribute);
    }

    @Override
    public List<EntityAttributeDefaultValue> findByEntity(String entity) {
        return repository.findByEntity(entity);
    }
}