package de.diedavids.cuba.defaultvalues.web.screens.entityattributedefaultvalue;

import com.haulmont.chile.core.datatypes.Datatype;
import com.haulmont.chile.core.model.MetaClass;
import com.haulmont.chile.core.model.MetaProperty;
import com.haulmont.cuba.core.entity.Entity;
import com.haulmont.cuba.core.global.MessageTools;
import com.haulmont.cuba.core.global.Metadata;
import com.haulmont.cuba.gui.Dialogs;
import com.haulmont.cuba.gui.Notifications;
import com.haulmont.cuba.gui.UiComponents;
import com.haulmont.cuba.gui.app.core.inputdialog.InputDialog;
import com.haulmont.cuba.gui.app.core.inputdialog.InputParameter;
import com.haulmont.cuba.gui.components.*;
import com.haulmont.cuba.gui.model.CollectionContainer;
import com.haulmont.cuba.gui.model.CollectionLoader;
import com.haulmont.cuba.gui.model.DataContext;
import com.haulmont.cuba.gui.screen.*;
import de.diedavids.cuba.defaultvalues.entity.EntityAttributeDefaultValue;
import de.diedavids.cuba.defaultvalues.entity.EntityAttributeDefaultValueType;
import de.diedavids.cuba.defaultvalues.service.SessionAttributeService;
import de.diedavids.cuba.entitysoftreference.EntitySoftReferenceDatatype;
import de.diedavids.cuba.metadataextensions.EntityDialogs;
import de.diedavids.cuba.metadataextensions.dataprovider.EntityDataProvider;
import de.diedavids.cuba.metadataextensions.entity.MetaClassEntity;
import org.springframework.util.StringUtils;

import javax.inject.Inject;
import java.util.Comparator;
import java.util.List;
import java.util.function.Consumer;
import java.util.stream.Collectors;

import static com.haulmont.cuba.gui.app.core.inputdialog.InputDialog.INPUT_DIALOG_OK_ACTION;
import static de.diedavids.cuba.metadataextensions.EntityAttributeInputParameter.entityAttributeParameter;

@UiController("ddcdv_EntityAttributeDefaultValue.edit")
@UiDescriptor("entity-attribute-default-value-edit.xml")
@EditedEntityContainer("metaClassEntityDc")
public class EntityAttributeDefaultValueEdit extends StandardEditor<MetaClassEntity> {

    @Inject
    protected MessageBundle messageBundle;
    @Inject
    protected MessageTools messageTools;
    @Inject
    protected Metadata metadata;
    @Inject
    protected EntityDataProvider entityDataProvider;
    @Inject
    protected EntityDialogs entityDialogs;
    @Inject
    protected DataContext dataContext;

    @Inject
    protected CollectionLoader<EntityAttributeDefaultValue> entityAttributeDefaultValueDl;
    @Inject
    protected CollectionContainer<EntityAttributeDefaultValue> entityAttributeDefaultValuesDc;
    @Inject
    protected Table<EntityAttributeDefaultValue> entityAttributeDefaultValuesTable;
    @Inject
    protected Dialogs dialogs;
    @Inject
    protected UiComponents uiComponents;
    @Inject
    protected SessionAttributeService sessionAttributeService;
    @Inject
    protected Notifications notifications;


    @Subscribe
    protected void onBeforeShow(BeforeShowEvent event) {
        entityAttributeDefaultValueDl.setParameter("entity", getEntityMetaClass());
        getScreenData().loadAll();

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
        MetaClass entityMetaClass = getEntityMetaClass();
        return entityDataProvider.businessEntityAttributes(entityMetaClass).stream()
                .filter(metaProperty -> !isToManyReference(metaProperty))
                .filter(metaProperty ->
                        !isPartOfExistingDefaultValues(existingDefaultValues, metaProperty)
                )
                .map(this::createDefaultValueConfiguration)
                .collect(Collectors.toList());
    }

    private boolean isToManyReference(MetaProperty metaProperty) {
        return metaProperty.getRange().getCardinality().isMany();
    }

    @Subscribe(target = Target.DATA_CONTEXT)
    protected void onPreCommit(DataContext.PreCommitEvent event) {
        addNonEmptyValues();
        removeEmptyValues();
    }

    private void addNonEmptyValues() {
        entityAttributeDefaultValuesDc.getItems().stream()
                .filter(defaultValueConfiguration -> defaultValueConfiguration.getValue() != null)
                .forEach(defaultValueConfiguration -> dataContext.merge(defaultValueConfiguration));
    }

    private void removeEmptyValues() {
        entityAttributeDefaultValuesDc.getItems().stream()
                .filter(defaultValueConfiguration -> StringUtils.isEmpty(defaultValueConfiguration.getValue()))
                .forEach(defaultValueConfiguration -> dataContext.remove(defaultValueConfiguration));
    }


    private boolean isPartOfExistingDefaultValues(List<EntityAttributeDefaultValue> existingDefaultValues, MetaProperty metaProperty) {
        return existingDefaultValues.stream()
                .anyMatch(defaultValueConfiguration -> defaultValueConfiguration.getEntityAttribute().equals(metaProperty));
    }

    private EntityAttributeDefaultValue createDefaultValueConfiguration(MetaProperty metaProperty) {
        EntityAttributeDefaultValue entityAttributeDefaultValue = metadata.create(EntityAttributeDefaultValue.class);
        entityAttributeDefaultValue.setEntity(getEntityMetaClass());
        entityAttributeDefaultValue.setEntityAttribute(metaProperty);

        return entityAttributeDefaultValue;
    }

    @Install(to = "entityAttributeDefaultValuesTable.entityAttribute", subject = "formatter")
    protected String defaultValuesTableMetaPropertyFormatter(MetaProperty metaProperty) {
        return messageTools.getPropertyCaption(metaProperty);
    }

    private void staticDefaultValueDialog(EntityAttributeDefaultValue entityAttributeDefaultValue) {
        Class<Entity> entityClass = entityAttributeDefaultValue.getEntity().getJavaClass();

        Entity entity = metadata.create(entityClass);

        entityDialogs.createEntityInputDialog(this, entityClass)
                .withEntity(entity)
                .withCaption(messageBundle.getMessage("staticDefaultValueCaption"))
                .withParameter(
                        entityAttributeParameter(entityClass, entityAttributeDefaultValue.getEntityAttribute().getName())
                                .withRequired(true)
                                .withAutoBinding(true)
                )
                .withCloseListener(new Consumer<InputDialog.InputDialogCloseEvent>() {
                    @Override
                    public void accept(InputDialog.InputDialogCloseEvent closeEvent) {
                        if (closeEvent.getCloseAction().equals(INPUT_DIALOG_OK_ACTION)) {
                            setStaticDefaultValue(entity, entityAttributeDefaultValue);
                        }
                        else {
                            resetEmptyDefaultValues();
                        }
                    }
                })
                .show();
    }

    private void sessionAttributeDefaultValueDialog(EntityAttributeDefaultValue entityAttributeDefaultValue) {
        dialogs.createInputDialog(this)
                .withCaption(
                        messageBundle.getMessage("setSessionValueCaption")
                )
                .withParameter(
                        InputParameter.stringParameter("sessionAttribute")
                                .withField(() -> sessionAttributeLookupField(entityAttributeDefaultValue))
                )
                .withCloseListener(new Consumer<InputDialog.InputDialogCloseEvent>() {
                    @Override
                    public void accept(InputDialog.InputDialogCloseEvent closeEvent) {
                        if (closeEvent.getCloseAction().equals(INPUT_DIALOG_OK_ACTION)) {
                            setSessionAttributeDefaultValue(
                                    entityAttributeDefaultValue,
                                    closeEvent.getValue("sessionAttribute")
                            );
                        } else {
                            resetEmptyDefaultValues();
                        }
                    }
                })
                .show();
    }

    private void resetEmptyDefaultValues() {
        entityAttributeDefaultValuesDc.getItems()
                .stream()
                .filter(entityAttributeDefaultValue -> StringUtils.isEmpty(entityAttributeDefaultValue.getValue()))
                .forEach(this::removeDefaultValue);
    }

    private Field sessionAttributeLookupField(EntityAttributeDefaultValue entityAttributeDefaultValue) {
        LookupField lookupField = uiComponents.create(LookupField.class);
        lookupField.setWidthFull();
        lookupField.setRequired(true);
        lookupField.setCaption(
                messageTools.getPropertyCaption(entityAttributeDefaultValue.getEntityAttribute())
        );

        lookupField.setOptionsMap(
                sessionAttributeService
                        .getAvailableSessionAttributes()
                        .stream()
                        .collect(Collectors.toMap(x -> x, x -> ":session$" + x))
        );
        lookupField.setValue(entityAttributeDefaultValue.getValue());

        return lookupField;
    }

    private void setSessionAttributeDefaultValue(EntityAttributeDefaultValue entityAttributeDefaultValue, String sessionAttributeName) {
        entityAttributeDefaultValue.setValue(sessionAttributeName);
        entityAttributeDefaultValue.setType(EntityAttributeDefaultValueType.SESSION_ATTRIBUTE);
    }

    private void setStaticDefaultValue(Entity entity, EntityAttributeDefaultValue entityAttributeDefaultValue) {
        MetaProperty property = getEntityMetaClass().getProperty(entityAttributeDefaultValue.getEntityAttribute().getName());
        Object defaultValue = entity.getValue(entityAttributeDefaultValue.getEntityAttribute().getName());
        Datatype datatype = determineEntityAttributeDatatype(property);
        String formattedValue = datatype.format(defaultValue);
        entityAttributeDefaultValue.setValue(formattedValue);
        entityAttributeDefaultValue.setType(EntityAttributeDefaultValueType.STATIC_VALUE);
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
        } else {
            return null;
        }
    }

    @Subscribe("entityAttributeDefaultValuesTable.removeDefaultValue")
    protected void onDefaultValuesTableRemoveDefaultValue(Action.ActionPerformedEvent event) {
        removeDefaultValue(entityAttributeDefaultValuesTable.getSingleSelected());
    }

    private void removeDefaultValue(EntityAttributeDefaultValue entityAttributeDefaultValue) {
        if (entityAttributeDefaultValue != null) {
            entityAttributeDefaultValue.setValue(null);
            entityAttributeDefaultValue.setType(null);
        }
    }

    @Subscribe("entityAttributeDefaultValuesTable.changeDefaultValue")
    protected void onEntityAttributeDefaultValuesTableChangeDefaultValue(Action.ActionPerformedEvent event) {
        EntityAttributeDefaultValue entityAttributeDefaultValue = entityAttributeDefaultValuesTable.getSingleSelected();

        if (entityAttributeDefaultValue == null || entityAttributeDefaultValue.getType() == null) {
            dialogs.createInputDialog(this)
                    .withCaption(messageBundle.getMessage("selectDefaultValueTypeCaption"))
                    .withParameter(
                            InputParameter.parameter(
                                    "entityAttributeDefaultValueType"
                            )
                                    .withField(() -> {
                                        RadioButtonGroup radioButtonGroup = uiComponents.create(RadioButtonGroup.class);
                                        radioButtonGroup.setWidthFull();
                                        radioButtonGroup.setRequired(true);
                                        radioButtonGroup.setOrientation(HasOrientation.Orientation.HORIZONTAL);
                                        radioButtonGroup.setOptionsEnum(EntityAttributeDefaultValueType.class);
                                        radioButtonGroup.setValue(EntityAttributeDefaultValueType.STATIC_VALUE);
                                        return radioButtonGroup;
                                    })
                    )
                    .withCloseListener(new Consumer<InputDialog.InputDialogCloseEvent>() {
                        @Override
                        public void accept(InputDialog.InputDialogCloseEvent closeEvent) {
                            if (closeEvent.getCloseAction().equals(INPUT_DIALOG_OK_ACTION)) {
                                entityAttributeDefaultValue.setType(
                                        closeEvent.getValue("entityAttributeDefaultValueType")
                                );
                                entityAttributeDefaultValueDialog(entityAttributeDefaultValue);
                            }
                        }
                    })
                    .show();
        } else {
            entityAttributeDefaultValueDialog(entityAttributeDefaultValue);
        }

    }

    private void entityAttributeDefaultValueDialog(EntityAttributeDefaultValue entityAttributeDefaultValue) {
        if (entityAttributeDefaultValue == null) {
            notifications.create(Notifications.NotificationType.WARNING)
                    .withCaption(messageBundle.getMessage("selectEntityAttributeFirst"))
                    .show();
        } else {
            switch (entityAttributeDefaultValue.getType()) {
                case SESSION_ATTRIBUTE:
                    sessionAttributeDefaultValueDialog(entityAttributeDefaultValue);
                    break;
                case STATIC_VALUE:
                    staticDefaultValueDialog(entityAttributeDefaultValue);
                    break;
                case SCRIPT:
                    scriptDefaultValueDialog(entityAttributeDefaultValue);
                    break;
            }
        }

    }

    private void scriptDefaultValueDialog(EntityAttributeDefaultValue entityAttributeDefaultValue) {
        notifications.create(Notifications.NotificationType.WARNING)
                .withCaption(messageBundle.getMessage("notSupportedYet"))
                .show();
        resetEmptyDefaultValues();
    }

}