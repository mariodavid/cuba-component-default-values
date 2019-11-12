package de.diedavids.cuba.defaultvalues.web.screens.entityattributedefaultvalue;

import com.google.common.base.Strings;
import com.haulmont.chile.core.datatypes.Datatype;
import com.haulmont.chile.core.model.MetaClass;
import com.haulmont.chile.core.model.MetaProperty;
import com.haulmont.chile.core.model.Range;
import com.haulmont.cuba.core.entity.Entity;
import com.haulmont.cuba.core.global.*;
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
import de.diedavids.cuba.defaultvalues.web.screens.entityattributedefaultvalue.typedelegate.DefaultValueTypeEditDelegate;
import de.diedavids.cuba.defaultvalues.web.screens.entityattributedefaultvalue.typedelegate.ScriptEditDelegate;
import de.diedavids.cuba.defaultvalues.web.screens.entityattributedefaultvalue.typedelegate.SessionAttributeEditDelegate;
import de.diedavids.cuba.defaultvalues.web.screens.entityattributedefaultvalue.typedelegate.StaticValueEditDelegate;
import de.diedavids.cuba.entitysoftreference.EntitySoftReferenceDatatype;
import de.diedavids.cuba.metadataextensions.EntityDialogs;
import de.diedavids.cuba.metadataextensions.dataprovider.EntityDataProvider;
import de.diedavids.cuba.metadataextensions.entity.MetaClassEntity;
import org.springframework.util.StringUtils;

import javax.inject.Inject;
import java.text.ParseException;
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
    @Inject
    protected EntityStates entityStates;
    @Inject
    protected DataManager dataManager;
    @Inject
    protected Messages messages;
    @Inject
    protected EntityLoadInfoBuilder entityLoadInfoBuilder;


    protected DefaultValueTypeEditDelegate staticValueEditDelegate;
    protected DefaultValueTypeEditDelegate sessionAttributeEditDelegate;
    protected DefaultValueTypeEditDelegate scriptEditDelegate;

    @Subscribe
    protected void onInit(InitEvent event) {
        staticValueEditDelegate = new StaticValueEditDelegate(
                metadata,
                messageBundle,
                entityDialogs
        );

        sessionAttributeEditDelegate = new SessionAttributeEditDelegate(
                metadata,
                dialogs,
                messageBundle,
                uiComponents,
                messageTools,
                sessionAttributeService
        );

        scriptEditDelegate = new ScriptEditDelegate(
                metadata,
                messageBundle,
                uiComponents,
                dialogs
        );
    }

    
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

    @Subscribe(target = Target.DATA_CONTEXT)
    protected void onPreCommit(DataContext.PreCommitEvent event) {

        addNonEmptyValues();
        removeEmptyValues();
    }

    private void addNonEmptyValues() {
        entityAttributeDefaultValuesDc.getItems().stream()
                .filter(defaultValueConfiguration ->
                        defaultValueConfiguration.getValue() != null &&
                                defaultValueConfiguration.getType() != null
                )
                .forEach(defaultValueConfiguration -> dataContext.merge(defaultValueConfiguration));
    }

    private void removeEmptyValues() {

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
        entityAttributeDefaultValue.setEntity(getEntityMetaClass());
        entityAttributeDefaultValue.setEntityAttribute(metaProperty);

        return entityAttributeDefaultValue;
    }

    @Install(to = "entityAttributeDefaultValuesTable.entityAttribute", subject = "formatter")
    protected String defaultValuesTableMetaPropertyFormatter(MetaProperty metaProperty) {
        return messageTools.getPropertyCaption(metaProperty);
    }

    private void staticDefaultValueDialog(EntityAttributeDefaultValue entityAttributeDefaultValue) {
        staticValueEditDelegate.openDialog(
                entityAttributeDefaultValue,
                this,
                this::resetEmptyDefaultValues
        );
    }

    private void sessionAttributeDefaultValueDialog(EntityAttributeDefaultValue entityAttributeDefaultValue) {
        sessionAttributeEditDelegate.openDialog(
                entityAttributeDefaultValue,
                this,
                this::resetEmptyDefaultValues
        );

    }


    private void scriptDefaultValueDialog(EntityAttributeDefaultValue entityAttributeDefaultValue) {

        scriptEditDelegate.openDialog(
                entityAttributeDefaultValue,
                this,
                this::resetEmptyDefaultValues
        );
    }


    private void resetEmptyDefaultValues() {

        entityAttributeDefaultValuesDc.getItems()
                .stream()
                .filter(entityAttributeDefaultValue -> StringUtils.isEmpty(entityAttributeDefaultValue.getValue()))
                .forEach(this::removeDefaultValue);
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



    @Install(to = "entityAttributeDefaultValuesTable.value", subject = "columnGenerator")
    protected Component entityAttributeDefaultValuesTableValueColumnGenerator(EntityAttributeDefaultValue entityAttributeDefaultValue) {
        Label field = uiComponents.create(Label.class);
        if (entityAttributeDefaultValue.getType() != null) {

            switch (entityAttributeDefaultValue.getType()) {
                case SESSION_ATTRIBUTE:
                    field.setValue(getSessionAttributeValue(entityAttributeDefaultValue));
                    break;
                case SCRIPT:
                    field.setValue("...");
                    break;
                case STATIC_VALUE:
                    field.setValue(getStaticValue(entityAttributeDefaultValue));
                    break;
            }

        }
        return field;
    }

    private String getSessionAttributeValue(EntityAttributeDefaultValue entityAttributeDefaultValue) {
        if (entityAttributeDefaultValue != null) {
            return entityAttributeDefaultValue.getValue().replaceAll("\\:session\\$", "");
        }
        else {
            return null;
        }

    }

    private String getStaticValue(EntityAttributeDefaultValue entityAttributeDefaultValue) {

        Range range = entityAttributeDefaultValue.getEntityAttribute().getRange();



        if (range.isClass()) {
            Entity reference = convertToEntityAttribute(entityAttributeDefaultValue.getValue());
            if (reference != null) {
                return metadata.getTools().getInstanceName(reference);
            }
            else {
                return null;
            }
        }
        else if (range.isEnum()) {

            try {
                Datatype datatype = determineEntityAttributeDatatype(entityAttributeDefaultValue.getEntityAttribute());
                Object defaultValue = datatype.parse(entityAttributeDefaultValue.getValue());
                return messages.getMessage((Enum) defaultValue);
            } catch (ParseException e) {
                e.printStackTrace();
            }
        }
        else {
            return entityAttributeDefaultValue.getValue();
        }

        return null;
    }


    public Entity convertToEntityAttribute(String value) {


        if (Strings.isNullOrEmpty(value))
            return null;

        EntityLoadInfo entityLoadInfo = entityLoadInfoBuilder.parse(value);

        Entity entity = null;

        if (entityLoadInfo != null) {
            entity = loadEntity(entityLoadInfo);
        }

        return entity;
    }

    private Entity loadEntity(EntityLoadInfo entityLoadInfo) {
        return dataManager.load(
                getLoadContextForForEntityLoadInfo(
                        entityLoadInfo.getMetaClass(),
                        entityLoadInfo.getId()
                )
        );
    }

    protected LoadContext getLoadContextForForEntityLoadInfo(MetaClass metaClass, Object entityId) {
        LoadContext loadContext = LoadContext.create(metaClass.getJavaClass());
        loadContext
                .setId(entityId);
        return loadContext;
    }

}