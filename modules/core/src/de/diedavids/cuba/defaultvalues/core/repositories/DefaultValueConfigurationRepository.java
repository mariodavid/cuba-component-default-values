package de.diedavids.cuba.defaultvalues.core.repositories;

import com.haulmont.addons.cuba.jpa.repositories.config.CubaJpaRepository;
import com.haulmont.addons.cuba.jpa.repositories.config.CubaView;
import de.diedavids.cuba.defaultvalues.entity.DefaultValueConfiguration;

import java.util.List;
import java.util.UUID;

public interface DefaultValueConfigurationRepository extends CubaJpaRepository<DefaultValueConfiguration, UUID> {

    @CubaView("_base")
    List<DefaultValueConfiguration> findByEntityAndEntityAttribute(String entity, String entityAttribute);


    @CubaView("_base")
    List<DefaultValueConfiguration> findByEntity(String entity);
}
