package de.diedavids.cuba.defaultvalues.web.screens.entityattributedefaultvalue.edit;

import com.haulmont.chile.core.model.MetaClass;
import com.haulmont.chile.core.model.MetaProperty;
import com.haulmont.cuba.core.global.EntityStates;
import com.haulmont.cuba.core.global.Metadata;
import com.haulmont.cuba.gui.model.CollectionContainer;
import com.haulmont.cuba.gui.model.DataContext;
import de.diedavids.cuba.defaultvalues.entity.EntityAttributeDefaultValue;
import de.diedavids.cuba.defaultvalues.entity.EntityAttributeDefaultValueType;
import de.diedavids.cuba.metadataextensions.dataprovider.EntityDataProvider;
import org.springframework.util.StringUtils;

import java.util.Comparator;
import java.util.List;
import java.util.stream.Collectors;

public class EditDataContextDelegate {


    private final DataContext dataContext;
    private final CollectionContainer<EntityAttributeDefaultValue> entityAttributeDefaultValuesDc;
    private final EntityStates entityStates;
    private final Metadata metadata;
    private final EntityDataProvider entityDataProvider;
    private final MetaClass entityMetaClass;

    public EditDataContextDelegate(
            DataContext dataContext,
            CollectionContainer<EntityAttributeDefaultValue> entityAttributeDefaultValuesDc,
            EntityStates entityStates,
            Metadata metadata,
            EntityDataProvider entityDataProvider,
            MetaClass entityMetaClass
    ) {

        this.dataContext = dataContext;
        this.entityAttributeDefaultValuesDc = entityAttributeDefaultValuesDc;
        this.entityStates = entityStates;
        this.metadata = metadata;
        this.entityDataProvider = entityDataProvider;
        this.entityMetaClass = entityMetaClass;
    }

    public void preCommit() {
        addNonEmptyValues(entityAttributeDefaultValuesDc);
        removeEmptyValues(entityAttributeDefaultValuesDc);
    }


    private void addNonEmptyValues(CollectionContainer<EntityAttributeDefaultValue> entityAttributeDefaultValuesDc) {
        entityAttributeDefaultValuesDc.getItems().stream()
                .filter(defaultValueConfiguration ->
                        defaultValueConfiguration.getValue() != null &&
                                defaultValueConfiguration.getType() != null
                )
                .forEach(defaultValueConfiguration -> dataContext.merge(defaultValueConfiguration));
    }

    private void removeEmptyValues(CollectionContainer<EntityAttributeDefaultValue> entityAttributeDefaultValuesDc) {

        /*
        avoid the following situation:
        when marking a default value to be removed for an already persisted (detached) entity attribute
        the type will be set to null, so that the UI have a clean slate (type selection can appear)
        when within the editor immediately a new value is assigned.

        However, when removing this item not, first *probably* an update statement is trying to be executed
        since the type attribute changed (before the remove call).

        However, since type is a mandatory attribute, this update fails.

        Therefore the type is blindly set to some time. This way, the remove operation succeeds.
         */
        entityAttributeDefaultValuesDc.getItems()
                .stream()
                .filter(this::isToBeDeleted)
                .forEach(defaultValueConfiguration -> defaultValueConfiguration.setType(EntityAttributeDefaultValueType.STATIC_VALUE));


        /*
        regular marking of items for removal
         */
        entityAttributeDefaultValuesDc.getItems()
                .stream()
                .filter(this::isEmpty)
                .forEach(defaultValueConfiguration -> dataContext.remove(defaultValueConfiguration));
    }

    private boolean isToBeDeleted(EntityAttributeDefaultValue entityAttributeDefaultValue) {
        return entityStates.isDetached(entityAttributeDefaultValue) &&
                isEmpty(entityAttributeDefaultValue);
    }

    private boolean isEmpty(EntityAttributeDefaultValue defaultValueConfiguration) {
        return StringUtils.isEmpty(defaultValueConfiguration.getValue());
    }


    private boolean isPartOfExistingDefaultValues(List<EntityAttributeDefaultValue> existingDefaultValues, MetaProperty metaProperty) {
        return existingDefaultValues.stream()
                .anyMatch(defaultValueConfiguration -> defaultValueConfiguration.getEntityAttribute().equals(metaProperty));
    }

    private EntityAttributeDefaultValue createDefaultValueConfiguration(MetaProperty metaProperty) {
        EntityAttributeDefaultValue entityAttributeDefaultValue = metadata.create(EntityAttributeDefaultValue.class);
        //entityAttributeDefaultValue.setEntity(getEntityMetaClass());
        entityAttributeDefaultValue.setEntity(metaProperty.getDomain());
        entityAttributeDefaultValue.setEntityAttribute(metaProperty);

        return entityAttributeDefaultValue;
    }

    public void onBeforeShow() {
        createTemporaryDefaultValueConfigurationForAllMetaProperties();
    }


    private void createTemporaryDefaultValueConfigurationForAllMetaProperties() {
        List<EntityAttributeDefaultValue> existingDefaultValues = entityAttributeDefaultValuesDc.getItems();

        List<EntityAttributeDefaultValue> entityAttributeDefaultValueForMissingMetaProperties =
                createDefaultValueConfigurationForMissingMetaProperties(existingDefaultValues);

        entityAttributeDefaultValuesDc.getMutableItems().addAll(
                entityAttributeDefaultValueForMissingMetaProperties
        );


        entityAttributeDefaultValuesDc.getMutableItems().sort(
                Comparator.comparing(defaultValueConfiguration -> defaultValueConfiguration.getEntityAttribute().getName())
        );
    }

    private List<EntityAttributeDefaultValue> createDefaultValueConfigurationForMissingMetaProperties(List<EntityAttributeDefaultValue> existingDefaultValues) {
        return entityDataProvider.businessEntityAttributes(entityMetaClass).stream()
                .filter(metaProperty -> !isToManyReference(metaProperty))
                .filter(metaProperty -> !isTransient(metaProperty))
                .filter(metaProperty ->
                        !isPartOfExistingDefaultValues(existingDefaultValues, metaProperty)
                )
                .map(this::createDefaultValueConfiguration)
                .collect(Collectors.toList());
    }

    private boolean isTransient(MetaProperty metaProperty) {
        return metadata.getTools().isNotPersistent(metaProperty);
    }

    private boolean isToManyReference(MetaProperty metaProperty) {
        return metaProperty.getRange().getCardinality().isMany();
    }

}
