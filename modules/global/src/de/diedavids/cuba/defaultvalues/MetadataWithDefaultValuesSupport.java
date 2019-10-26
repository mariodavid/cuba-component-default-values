package de.diedavids.cuba.defaultvalues;

import com.haulmont.cuba.core.entity.Entity;
import com.haulmont.cuba.core.sys.MetadataImpl;

import javax.inject.Inject;

public class MetadataWithDefaultValuesSupport extends MetadataImpl {

    @Inject
    DefaultValueBinding defaultValueBinding;

    @Override
    protected <T> T __create(Class<T> entityClass) {
        T entityInstance = super.__create(entityClass);
        initDefaultValues((Class<Entity>) entityClass, (Entity) entityInstance);
        return entityInstance;
    }

    private <U extends Entity> void initDefaultValues(Class<U> entityClass, U entityInstance) {
        defaultValueBinding.bindDefaultValues(entityClass, entityInstance);
    }
}
