package de.diedavids.cuba.defaultvalues;


import com.haulmont.cuba.core.entity.Entity;

public interface DefaultValueBinding {

    String NAME = "ddcdv_DefaultValueBinding";

    /**
     * binds the default values for an entity instance
     *
     * @param entityClass the entity class of the instance
     * @param entityInstance the entity instance itself
     * @param <T> the type of the Entity
     * @return the entity instance with the default values
     */
    <T extends Entity> T bindDefaultValues(Class<T> entityClass, T entityInstance);
}