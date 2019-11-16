package de.diedavids.cuba.defaultvalues.web.screens.entityattributedefaultvalue.edit.dialogs;


import com.haulmont.chile.core.datatypes.Datatype;
import com.haulmont.chile.core.model.MetaProperty;
import com.haulmont.cuba.core.entity.Entity;
import com.haulmont.cuba.core.global.Metadata;
import com.haulmont.cuba.gui.Dialogs;
import com.haulmont.cuba.gui.UiComponents;
import com.haulmont.cuba.gui.app.core.inputdialog.InputDialog;
import com.haulmont.cuba.gui.app.core.inputdialog.InputParameter;
import com.haulmont.cuba.gui.components.Field;
import com.haulmont.cuba.gui.components.HasOrientation;
import com.haulmont.cuba.gui.components.RadioButtonGroup;
import com.haulmont.cuba.gui.screen.FrameOwner;
import com.haulmont.cuba.gui.screen.MessageBundle;
import de.diedavids.cuba.defaultvalues.dynamicvalue.DynamicValueProviders;
import de.diedavids.cuba.defaultvalues.entity.EntityAttributeDefaultValue;
import de.diedavids.cuba.defaultvalues.entity.EntityAttributeDefaultValueType;
import de.diedavids.cuba.entitysoftreference.EntitySoftReferenceDatatype;
import de.diedavids.cuba.metadataextensions.EntityDialogs;

import java.util.function.Consumer;

import static com.haulmont.cuba.gui.app.core.inputdialog.InputDialog.INPUT_DIALOG_CANCEL_ACTION;
import static com.haulmont.cuba.gui.app.core.inputdialog.InputDialog.INPUT_DIALOG_OK_ACTION;
import static de.diedavids.cuba.metadataextensions.EntityAttributeInputParameter.entityAttributeParameter;

public class DefaultValueTypeSelectorDialogBuilder implements DefaultValueTypeDialogBuilder<EntityAttributeDefaultValueType> {

    private final MessageBundle messageBundle;
    private final Dialogs dialogs;
    private final UiComponents uiComponents;
    private final DynamicValueProviders dynamicValueProviders;

    public DefaultValueTypeSelectorDialogBuilder(
            MessageBundle messageBundle,
            Dialogs dialogs,
            UiComponents uiComponents,
            DynamicValueProviders dynamicValueProviders
    ) {

        this.messageBundle = messageBundle;
        this.dialogs = dialogs;
        this.uiComponents = uiComponents;
        this.dynamicValueProviders = dynamicValueProviders;
    }


    @Override
    public InputDialog createDialog(
            EntityAttributeDefaultValue entityAttributeDefaultValue,
            FrameOwner frameOwner,
            Consumer<EntityAttributeDefaultValueType> afterOkHandler,
            Runnable afterCancelHandler
    ) {
        return dialogs.createInputDialog(frameOwner)
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
                            EntityAttributeDefaultValueType entityAttributeDefaultValueType = closeEvent.getValue("entityAttributeDefaultValueType");
                            entityAttributeDefaultValue.setType(entityAttributeDefaultValueType);
                            afterOkHandler.accept(entityAttributeDefaultValueType);
                        }
                        else if (closeEvent.getCloseAction().equals(INPUT_DIALOG_CANCEL_ACTION)) {
                            afterCancelHandler.run();
                        }
                    }
                }).build();
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

}
