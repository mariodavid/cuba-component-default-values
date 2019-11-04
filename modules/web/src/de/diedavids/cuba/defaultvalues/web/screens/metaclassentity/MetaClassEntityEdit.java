package de.diedavids.cuba.defaultvalues.web.screens.metaclassentity;

import com.haulmont.chile.core.datatypes.Datatype;
import com.haulmont.chile.core.model.MetaClass;
import com.haulmont.chile.core.model.MetaProperty;
import com.haulmont.cuba.core.entity.Entity;
import com.haulmont.cuba.core.global.MessageTools;
import com.haulmont.cuba.core.global.Metadata;
import com.haulmont.cuba.gui.app.core.inputdialog.InputDialog;
import com.haulmont.cuba.gui.components.Action;
import com.haulmont.cuba.gui.components.Table;
import com.haulmont.cuba.gui.model.CollectionContainer;
import com.haulmont.cuba.gui.model.CollectionLoader;
import com.haulmont.cuba.gui.model.DataContext;
import com.haulmont.cuba.gui.screen.*;
import de.diedavids.cuba.defaultvalues.entity.DefaultValueConfiguration;
import de.diedavids.cuba.defaultvalues.entity.MetaClassEntity;
import de.diedavids.cuba.entitysoftreference.EntitySoftReferenceDatatype;
import de.diedavids.cuba.metadataextensions.MetadataDialogs;
import de.diedavids.cuba.metadataextensions.web.MetadataDataProvider;
import org.springframework.util.StringUtils;

import javax.inject.Inject;
import java.util.Comparator;
import java.util.List;
import java.util.function.Consumer;
import java.util.stream.Collectors;

import static com.haulmont.cuba.gui.app.core.inputdialog.InputDialog.INPUT_DIALOG_OK_ACTION;
import static de.diedavids.cuba.metadataextensions.MetaPropertyInputParameter.metaPropertyParameter;

@UiController("ddcdv_MetaClassEntity.edit")
@UiDescriptor("meta-class-entity-edit.xml")
@EditedEntityContainer("metaClassEntityDc")
public class MetaClassEntityEdit extends StandardEditor<MetaClassEntity> {

    @Inject
    protected MessageBundle messageBundle;
    @Inject
    protected MessageTools messageTools;
    @Inject
    protected Metadata metadata;
    @Inject
    protected MetadataDataProvider metadataDataProvider;
    @Inject
    protected MetadataDialogs metadataDialogs;
    @Inject
    protected DataContext dataContext;

    @Inject
    protected CollectionLoader<DefaultValueConfiguration> defaultValueConfigurationsDl;
    @Inject
    protected CollectionContainer<DefaultValueConfiguration> defaultValueConfigurationsDc;
    @Inject
    protected Table<DefaultValueConfiguration> defaultValuesTable;


    @Subscribe
    protected void onBeforeShow(BeforeShowEvent event) {
        defaultValueConfigurationsDl.setParameter("entity", getEntityMetaClass());
        getScreenData().loadAll();

        createTemporaryDefaultValueConfigurationForAllMetaProperties();

    }

    private void createTemporaryDefaultValueConfigurationForAllMetaProperties() {
        List<DefaultValueConfiguration> existingDefaultValues = defaultValueConfigurationsDc.getItems();

        List<DefaultValueConfiguration> defaultValueConfigurationForMissingMetaProperties =
                createDefaultValueConfigurationForMissingMetaProperties(existingDefaultValues);

        defaultValueConfigurationsDc.getMutableItems().addAll(
                defaultValueConfigurationForMissingMetaProperties
        );


        defaultValueConfigurationsDc.getMutableItems().sort(
                Comparator.comparing(defaultValueConfiguration -> defaultValueConfiguration.getEntityAttribute().getName())
        );


    }

    private List<DefaultValueConfiguration> createDefaultValueConfigurationForMissingMetaProperties(List<DefaultValueConfiguration> existingDefaultValues) {
        return metadataDataProvider.getBusinessMetaProperties(getEntityMetaClass()).stream()
                .filter(metaProperty ->
                    !isPartOfExistingDefaultValues(existingDefaultValues, metaProperty)
                )
                .map(this::createDefaultValueConfiguration)
                .collect(Collectors.toList());
    }

    @Subscribe(target = Target.DATA_CONTEXT)
    protected void onPreCommit(DataContext.PreCommitEvent event) {
        addNonEmptyValues();
        removeEmptyValues();
    }

    private void addNonEmptyValues() {
        defaultValueConfigurationsDc.getItems().stream()
                .filter(defaultValueConfiguration -> defaultValueConfiguration.getValue() != null)
                .forEach(defaultValueConfiguration -> dataContext.merge(defaultValueConfiguration));
    }

    private void removeEmptyValues() {
        defaultValueConfigurationsDc.getItems().stream()
                .filter(defaultValueConfiguration -> StringUtils.isEmpty(defaultValueConfiguration.getValue()))
                .forEach(defaultValueConfiguration -> dataContext.remove(defaultValueConfiguration));
    }


    private boolean isPartOfExistingDefaultValues(List<DefaultValueConfiguration> existingDefaultValues, MetaProperty metaProperty) {
        return existingDefaultValues.stream()
                .anyMatch(defaultValueConfiguration -> defaultValueConfiguration.getEntityAttribute().equals(metaProperty));
    }

    private DefaultValueConfiguration createDefaultValueConfiguration(MetaProperty metaProperty) {
        DefaultValueConfiguration defaultValueConfiguration = metadata.create(DefaultValueConfiguration.class);
        defaultValueConfiguration.setEntity(getEntityMetaClass());
        defaultValueConfiguration.setEntityAttribute(metaProperty);

        return defaultValueConfiguration;
    }

    @Install(to = "defaultValuesTable.entityAttribute", subject = "formatter")
    protected String defaultValuesTableMetaPropertyFormatter(MetaProperty metaProperty) {
        return messageTools.getPropertyCaption(metaProperty);
    }

    @Subscribe("defaultValuesTable.setDefaultValue")
    protected void onDefaultValuesTableSetDefaultValue(Action.ActionPerformedEvent event) {

        DefaultValueConfiguration defaultValueConfiguration = defaultValuesTable.getSingleSelected();

        Class<Entity> entityClass = defaultValueConfiguration.getEntity().getJavaClass();

        Entity entity = metadata.create(entityClass);

        metadataDialogs.createMetadataInputDialog(this, entityClass)
                .withEntity(entity)
                .withCaption(messageBundle.getMessage("setValueCaption"))
                .withParameter(
                        metaPropertyParameter(entityClass, defaultValueConfiguration.getEntityAttribute().getName())
                            .withAutoBinding(true)
                )
                .withCloseListener(new Consumer<InputDialog.InputDialogCloseEvent>() {
                    @Override
                    public void accept(InputDialog.InputDialogCloseEvent closeEvent) {
                        if (closeEvent.getCloseAction().equals(INPUT_DIALOG_OK_ACTION)) {
                            setDefaultValue(entity, defaultValueConfiguration);
                        }
                    }
                })
                .show();

    }

    private void setDefaultValue(Entity entity, DefaultValueConfiguration defaultValueConfiguration) {
        MetaProperty property = getEntityMetaClass().getProperty(defaultValueConfiguration.getEntityAttribute().getName());
        Object defaultValue = entity.getValue(defaultValueConfiguration.getEntityAttribute().getName());
        Datatype datatype = determineEntityAttributeDatatype(property);
        defaultValueConfiguration.setValue(datatype.format(defaultValue));
    }


    private MetaClass getEntityMetaClass() {
        return metadata.getClass(getEditedEntity().getName());
    }

    private Datatype determineEntityAttributeDatatype(MetaProperty metaProperty) {
        if (metaProperty.getRange().isDatatype()) {
            return metaProperty.getRange().asDatatype();
        } else if (metaProperty.getRange().isEnum()) {
            return metaProperty.getRange().asEnumeration();
        } else if (metaProperty.getRange().isClass()) {
            return new EntitySoftReferenceDatatype();
        }
        else {
            return null;
        }
    }

    @Subscribe("defaultValuesTable.removeDefaultValue")
    protected void onDefaultValuesTableRemoveDefaultValue(Action.ActionPerformedEvent event) {
        defaultValuesTable.getSingleSelected()
                .setValue(null);
    }
}