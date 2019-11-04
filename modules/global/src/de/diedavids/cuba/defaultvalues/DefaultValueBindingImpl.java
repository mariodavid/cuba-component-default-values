package de.diedavids.cuba.defaultvalues;

import com.haulmont.chile.core.datatypes.Datatype;
import com.haulmont.chile.core.datatypes.Enumeration;
import com.haulmont.chile.core.model.MetaClass;
import com.haulmont.chile.core.model.MetaProperty;
import com.haulmont.cuba.core.entity.Entity;
import com.haulmont.cuba.core.global.DataManager;
import com.haulmont.cuba.core.global.Metadata;
import de.diedavids.cuba.defaultvalues.entity.DefaultValueConfiguration;
import de.diedavids.cuba.defaultvalues.service.DefaultValuesConfigurationService;
import de.diedavids.cuba.entitysoftreference.EntitySoftReferenceDatatype;
import org.springframework.stereotype.Component;

import javax.inject.Inject;
import java.text.ParseException;
import java.util.List;

@Component(DefaultValueBinding.NAME)
public class DefaultValueBindingImpl implements DefaultValueBinding {


    @Inject
    DefaultValuesConfigurationService defaultValuesConfigurationService;

    @Inject
    protected DataManager dataManager;
    @Inject
    protected Metadata metadata;

    @Override
    public <T extends Entity> T bindDefaultValues(Class<T> entityClass, T entityInstance) {


        MetaClass metaClass = metadata.getClass(entityClass);

        List<DefaultValueConfiguration> defaultValueConfigurationList = getDefaultConfigurations(metaClass);

        defaultValueConfigurationList
                .forEach(defaultValueConfiguration -> bindDefaultValue(entityInstance, metaClass, defaultValueConfiguration)
                );

        return entityInstance;
    }

    private List<DefaultValueConfiguration> getDefaultConfigurations(MetaClass metaClass) {

        return dataManager.load(DefaultValueConfiguration.class)
                .query("select e from ddcdv_DefaultValueConfiguration e where e.entity = :entity")
        .parameter("entity", metaClass)
        .list();
    }

    private <T extends Entity> void bindDefaultValue(T entityInstance, MetaClass metaClass, DefaultValueConfiguration defaultValueConfiguration) {
        MetaProperty property = defaultValueConfiguration.getEntityAttribute();

        if (property.getRange().isDatatype()) {
            bindDatatypeDefaultValue(
                    entityInstance,
                    defaultValueConfiguration,
                    property,
                    property.getRange().asDatatype()
            );
        }
        else if (property.getRange().isEnum()) {
            bindDatatypeDefaultValue(
                    entityInstance,
                    defaultValueConfiguration,
                    property,
                    property.getRange().asEnumeration()
            );
        }
        else if (property.getRange().isClass()) {
            bindEntityDefaultValue(
                    entityInstance,
                    defaultValueConfiguration,
                    property
            );
        }

    }

    private <T extends Entity> void bindDatatypeDefaultValue(
            T entityInstance,
            DefaultValueConfiguration defaultValueConfiguration,
            MetaProperty entityAttribute,
            Datatype<Object> datatype
    ) {

        try {
            Object result = datatype.parse(defaultValueConfiguration.getValue());
            entityInstance.setValue(entityAttribute.getName(), result);
        } catch (ParseException e) {
            e.printStackTrace();
        }
    }
    private <T extends Entity> void bindEntityDefaultValue(
            T entityInstance,
            DefaultValueConfiguration defaultValueConfiguration,
            MetaProperty entityAttribute
    ) {

        try {
            Datatype datatype = new EntitySoftReferenceDatatype();
            Object result = datatype.parse(defaultValueConfiguration.getValue());
            entityInstance.setValue(entityAttribute.getName(), result);
        } catch (ParseException e) {
            e.printStackTrace();
        }
    }
}
