package de.diedavids.cuba.defaultvalues.core.repositories;

import com.haulmont.addons.cuba.jpa.repositories.config.CubaJpaRepository;
import com.haulmont.addons.cuba.jpa.repositories.config.CubaView;
import de.diedavids.cuba.defaultvalues.entity.EntityAttributeDefaultValue;

import java.util.List;
import java.util.UUID;

public interface DefaultValueConfigurationRepository extends CubaJpaRepository<EntityAttributeDefaultValue, UUID> {

    @CubaView("_base")
    List<EntityAttributeDefaultValue> findByEntityAndEntityAttribute(String entity, String entityAttribute);


    @CubaView("_base")
    List<EntityAttributeDefaultValue> findByEntity(String entity);
}
