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
import de.diedavids.cuba.defaultvalues.entity.EntityAttributeDefaultValue;
import de.diedavids.cuba.defaultvalues.entity.EntityAttributeDefaultValueType;
import de.diedavids.cuba.defaultvalues.service.SessionAttributeService;
import de.diedavids.cuba.defaultvalues.web.screens.entityattributedefaultvalue.edit.EditDataContextDelegate;
import de.diedavids.cuba.defaultvalues.web.screens.entityattributedefaultvalue.edit.typedelegate.DefaultValueTypeEditDelegate;
import de.diedavids.cuba.defaultvalues.web.screens.entityattributedefaultvalue.edit.typedelegate.ScriptEditDelegate;
import de.diedavids.cuba.defaultvalues.web.screens.entityattributedefaultvalue.edit.typedelegate.SessionAttributeEditDelegate;
import de.diedavids.cuba.defaultvalues.web.screens.entityattributedefaultvalue.edit.typedelegate.StaticValueEditDelegate;
import de.diedavids.cuba.metadataextensions.EntityDialogs;
import de.diedavids.cuba.metadataextensions.dataprovider.EntityDataProvider;
import de.diedavids.cuba.metadataextensions.entity.MetaClassEntity;
import org.springframework.util.StringUtils;

import javax.inject.Inject;
import java.util.function.Consumer;

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


    private DefaultValueTypeEditDelegate staticValueEditDelegate;
    private DefaultValueTypeEditDelegate sessionAttributeEditDelegate;
    private DefaultValueTypeEditDelegate scriptEditDelegate;

    private EditDataContextDelegate dataContextDelegate;

    @Subscribe
    protected void onInit(InitEvent event) {


        staticValueEditDelegate = new StaticValueEditDelegate(
                metadata,
                messageBundle,
                entityDialogs,
                messages,
                dataManager,
                entityLoadInfoBuilder
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
                    field.setValue(sessionAttributeEditDelegate.getUiValue(entityAttributeDefaultValue));
                    break;
                case SCRIPT:
                    field.setValue(scriptEditDelegate.getUiValue(entityAttributeDefaultValue));
                    break;
                case STATIC_VALUE:
                    field.setValue(staticValueEditDelegate.getUiValue(entityAttributeDefaultValue));
                    break;
            }

        }
        return field;
    }
}