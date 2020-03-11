package de.diedavids.cuba.defaultvalues;

import com.haulmont.cuba.core.entity.Entity;
import com.haulmont.cuba.core.sys.MetadataImpl;

import javax.inject.Inject;

public class MetadataWithDefaultValuesSupport extends MetadataImpl {

    @Inject
    DefaultValueBinding defaultValueBinding;

    @Override
    protected <T extends Entity> T __create(Class<T> entityClass) {
        T entityInstance = super.__create(entityClass);
        initDefaultValues(entityClass, entityInstance);
        return entityInstance;
    }

    private <T extends Entity> void initDefaultValues(Class<T> entityClass, T entityInstance) {
        defaultValueBinding.bindDefaultValues(entityClass, entityInstance);
    }
}
