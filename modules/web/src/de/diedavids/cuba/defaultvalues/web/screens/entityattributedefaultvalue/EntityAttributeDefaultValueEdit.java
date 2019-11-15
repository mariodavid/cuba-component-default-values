package de.diedavids.cuba.defaultvalues.web.screens.entityattributedefaultvalue;

import com.haulmont.chile.core.model.MetaClass;
import com.haulmont.chile.core.model.MetaProperty;
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
import de.diedavids.cuba.defaultvalues.dynamicvalue.DynamicValueProviders;
import de.diedavids.cuba.defaultvalues.entity.EntityAttributeDefaultValue;
import de.diedavids.cuba.defaultvalues.entity.EntityAttributeDefaultValueType;
import de.diedavids.cuba.defaultvalues.service.SessionAttributeService;
import de.diedavids.cuba.defaultvalues.web.screens.entityattributedefaultvalue.edit.EditDataContextDelegate;
import de.diedavids.cuba.defaultvalues.web.screens.entityattributedefaultvalue.edit.columngenerator.DynamicValueColumnGenerator;
import de.diedavids.cuba.defaultvalues.web.screens.entityattributedefaultvalue.edit.columngenerator.ScriptColumnGenerator;
import de.diedavids.cuba.defaultvalues.web.screens.entityattributedefaultvalue.edit.columngenerator.SessionAttributeColumnGenerator;
import de.diedavids.cuba.defaultvalues.web.screens.entityattributedefaultvalue.edit.columngenerator.StaticValueColumnGenerator;
import de.diedavids.cuba.defaultvalues.web.screens.entityattributedefaultvalue.edit.dialogs.*;
import de.diedavids.cuba.metadataextensions.EntityDialogs;
import de.diedavids.cuba.metadataextensions.dataprovider.EntityDataProvider;
import de.diedavids.cuba.metadataextensions.entity.MetaClassEntity;
import org.springframework.util.StringUtils;

import javax.inject.Inject;
import java.util.function.Consumer;

import static com.haulmont.cuba.gui.app.core.inputdialog.InputDialog.INPUT_DIALOG_OK_ACTION;

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

    @Inject
    protected DynamicValueProviders dynamicValueProviders;

    private DefaultValueTypeDialogBuilder staticValueDialog;
    private DefaultValueTypeDialogBuilder sessionAttributeDialog;
    private DefaultValueTypeDialogBuilder scriptDialog;
    private DefaultValueTypeDialogBuilder dynamicValueDialog;


    private EditDataContextDelegate dataContextDelegate;

    @Subscribe
    protected void onInit(InitEvent event) {


        staticValueDialog = new StaticValueDialogBuilder(
                metadata,
                messageBundle,
                entityDialogs
        );

        dynamicValueDialog = new DynamicValueDialogBuilder(
                dialogs,
                messageBundle,
                uiComponents,
                messages,
                dynamicValueProviders
        );

        sessionAttributeDialog = new SessionAttributeDialogBuilder(
                dialogs,
                messageBundle,
                uiComponents,
                messageTools,
                sessionAttributeService
        );

        scriptDialog = new ScriptDialogBuilder(
                messageBundle,
                uiComponents,
                dialogs
        );
    }


    @Subscribe
    protected void onBeforeShow(BeforeShowEvent event) {
        MetaClass entityMetaClass = metadata.getClass(getEditedEntity().getName());

        entityAttributeDefaultValueDl.setParameter("entity", entityMetaClass);
        getScreenData().loadAll();

        dataContextDelegate = new EditDataContextDelegate(
                dataContext,
                entityAttributeDefaultValuesDc,
                entityStates,
                metadata,
                entityDataProvider,
                entityMetaClass
        );

        dataContextDelegate.onBeforeShow();

    }

    @Subscribe(target = Target.DATA_CONTEXT)
    protected void onPreCommit(DataContext.PreCommitEvent event) {
        dataContextDelegate.preCommit();
    }


    @Install(to = "entityAttributeDefaultValuesTable.entityAttribute", subject = "formatter")
    protected String defaultValuesTableMetaPropertyFormatter(MetaProperty metaProperty) {
        return messageTools.getPropertyCaption(metaProperty);
    }

    private void staticDefaultValueDialog(EntityAttributeDefaultValue entityAttributeDefaultValue) {
        staticValueDialog.createDialog(
                entityAttributeDefaultValue,
                this,
                this::resetEmptyDefaultValues
        ).show();
    }

    private void dynamicDefaultValueDialog(EntityAttributeDefaultValue entityAttributeDefaultValue) {
        dynamicValueDialog.createDialog(
                entityAttributeDefaultValue,
                this,
                this::resetEmptyDefaultValues
        ).show();
    }

    private void sessionAttributeDefaultValueDialog(EntityAttributeDefaultValue entityAttributeDefaultValue) {
        sessionAttributeDialog.createDialog(
                entityAttributeDefaultValue,
                this,
                this::resetEmptyDefaultValues
        ).show();
    }

    private void scriptDefaultValueDialog(EntityAttributeDefaultValue entityAttributeDefaultValue) {
        scriptDialog.createDialog(
                entityAttributeDefaultValue,
                this,
                this::resetEmptyDefaultValues
        ).show();
    }

    private void resetEmptyDefaultValues() {
        entityAttributeDefaultValuesDc.getItems()
                .stream()
                .filter(entityAttributeDefaultValue -> StringUtils.isEmpty(entityAttributeDefaultValue.getValue()))
                .forEach(this::removeDefaultValue);
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
                    .withWidth("300px")
                    .withParameter(
                            InputParameter.parameter(
                                    "entityAttributeDefaultValueType"
                            )
                                    .withField(() -> defaultValueTypeField(entityAttributeDefaultValue))
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

    private Field defaultValueTypeField(EntityAttributeDefaultValue entityAttributeDefaultValue) {
        RadioButtonGroup radioButtonGroup = uiComponents.create(RadioButtonGroup.class);
        radioButtonGroup.setWidthFull();
        radioButtonGroup.setRequired(true);
        radioButtonGroup.setOrientation(HasOrientation.Orientation.VERTICAL);
        radioButtonGroup.setOptionsEnum(EntityAttributeDefaultValueType.class);
        radioButtonGroup.setValue(EntityAttributeDefaultValueType.STATIC_VALUE);
        radioButtonGroup.setContextHelpText(messageBundle.getMessage("selectDefaultValueTypeHelp"));
        radioButtonGroup.setContextHelpTextHtmlEnabled(true);

        configureOptionEnableProvider(entityAttributeDefaultValue, radioButtonGroup);

        return radioButtonGroup;
    }

    private void configureOptionEnableProvider(EntityAttributeDefaultValue entityAttributeDefaultValue, RadioButtonGroup radioButtonGroup) {
        com.vaadin.ui.RadioButtonGroup unwrap = radioButtonGroup.unwrap(com.vaadin.ui.RadioButtonGroup.class);

        unwrap.setItemEnabledProvider(o -> {
            EntityAttributeDefaultValueType type = (EntityAttributeDefaultValueType) o;

            if (
                    type.equals(EntityAttributeDefaultValueType.DYNAMIC_VALUE) &&
                            noDynamicValueProvidersAvailable(entityAttributeDefaultValue)
            ) {
                return false;
            }

            return true;
        });
    }

    private boolean noDynamicValueProvidersAvailable(EntityAttributeDefaultValue entityAttributeDefaultValue) {
        return dynamicValueProviders.getProvidersFor(entityAttributeDefaultValue.getEntityAttribute()).size() == 0;
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
                case DYNAMIC_VALUE:
                    dynamicDefaultValueDialog(entityAttributeDefaultValue);
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
                    field.setValue(new SessionAttributeColumnGenerator().getUiValue(entityAttributeDefaultValue));
                    break;
                case SCRIPT:
                    field.setValue(new ScriptColumnGenerator().getUiValue(entityAttributeDefaultValue));
                    break;
                case DYNAMIC_VALUE:
                    field.setValue(new DynamicValueColumnGenerator(messages).getUiValue(entityAttributeDefaultValue));
                    break;
                case STATIC_VALUE:
                    field.setValue(new StaticValueColumnGenerator(
                            metadata,
                            messages,
                            entityLoadInfoBuilder,
                            dataManager
                    ).getUiValue(entityAttributeDefaultValue));
                    break;
            }
        }
        return field;
    }
}