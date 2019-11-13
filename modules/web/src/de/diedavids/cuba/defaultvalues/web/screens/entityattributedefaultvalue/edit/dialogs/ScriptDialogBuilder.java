package de.diedavids.cuba.defaultvalues.web.screens.entityattributedefaultvalue.edit.dialogs;


import com.haulmont.cuba.gui.Dialogs;
import com.haulmont.cuba.gui.UiComponents;
import com.haulmont.cuba.gui.app.core.inputdialog.InputDialog;
import com.haulmont.cuba.gui.app.core.inputdialog.InputParameter;
import com.haulmont.cuba.gui.components.Field;
import com.haulmont.cuba.gui.components.SourceCodeEditor;
import com.haulmont.cuba.gui.screen.FrameOwner;
import com.haulmont.cuba.gui.screen.MessageBundle;
import de.diedavids.cuba.defaultvalues.entity.EntityAttributeDefaultValue;
import de.diedavids.cuba.defaultvalues.entity.EntityAttributeDefaultValueType;

import java.util.function.Consumer;

import static com.haulmont.cuba.gui.app.core.inputdialog.InputDialog.INPUT_DIALOG_OK_ACTION;

public class ScriptDialogBuilder implements DefaultValueTypeDialogBuilder {

    private final MessageBundle messageBundle;
    private final UiComponents uiComponents;
    private final Dialogs dialogs;

    public ScriptDialogBuilder(
            MessageBundle messageBundle,
            UiComponents uiComponents,
            Dialogs dialogs
    ) {
        this.messageBundle = messageBundle;
        this.uiComponents = uiComponents;
        this.dialogs = dialogs;
    }

    @Override
    public InputDialog createDialog(EntityAttributeDefaultValue entityAttributeDefaultValue, FrameOwner frameOwner, Runnable afterCancelHandler) {
        return dialogs.createInputDialog(frameOwner)
                .withCaption(
                        messageBundle.getMessage("setScriptDefaultValue")
                )
                .withWidth("60%")
                .withParameter(
                        InputParameter.stringParameter("script")
                                .withField(() -> scriptField(entityAttributeDefaultValue))
                )
                .withCloseListener(new Consumer<InputDialog.InputDialogCloseEvent>() {
                    @Override
                    public void accept(InputDialog.InputDialogCloseEvent closeEvent) {
                        if (closeEvent.getCloseAction().equals(INPUT_DIALOG_OK_ACTION)) {
                            setScriptAttributeDefaultValue(
                                    entityAttributeDefaultValue,
                                    closeEvent.getValue("script")
                            );
                        } else {
                            afterCancelHandler.run();
                        }
                    }
                }).build();

    }

    private void setScriptAttributeDefaultValue(EntityAttributeDefaultValue entityAttributeDefaultValue, String scriptValue) {
        entityAttributeDefaultValue.setValue(scriptValue);
        entityAttributeDefaultValue.setType(EntityAttributeDefaultValueType.SCRIPT);
    }


    private Field scriptField(EntityAttributeDefaultValue entityAttributeDefaultValue) {
        SourceCodeEditor field = uiComponents.create(SourceCodeEditor.class);
        field.setMode(SourceCodeEditor.Mode.Groovy);
        field.setWidthFull();
        field.setHeight("400px");
        field.setRequired(true);
        field.setValue(entityAttributeDefaultValue.getValue());

        return field;
    }

}
