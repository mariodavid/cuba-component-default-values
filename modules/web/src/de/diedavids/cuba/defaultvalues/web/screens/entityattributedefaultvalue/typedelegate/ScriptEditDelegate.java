package de.diedavids.cuba.defaultvalues.web.screens.entityattributedefaultvalue.typedelegate;


import com.haulmont.chile.core.datatypes.Datatype;
import com.haulmont.chile.core.model.MetaProperty;
import com.haulmont.cuba.core.entity.Entity;
import com.haulmont.cuba.core.global.Metadata;
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
import de.diedavids.cuba.entitysoftreference.EntitySoftReferenceDatatype;
import de.diedavids.cuba.metadataextensions.EntityDialogs;

import java.util.function.Consumer;

import static com.haulmont.cuba.gui.app.core.inputdialog.InputDialog.INPUT_DIALOG_OK_ACTION;
import static de.diedavids.cuba.metadataextensions.EntityAttributeInputParameter.entityAttributeParameter;

public class ScriptEditDelegate implements DefaultValueTypeEditDelegate {

    private final Metadata metadata;
    private final MessageBundle messageBundle;
    private final UiComponents uiComponents;
    private final Dialogs dialogs;

    public ScriptEditDelegate(Metadata metadata, MessageBundle messageBundle, UiComponents uiComponents, Dialogs dialogs) {

        this.metadata = metadata;
        this.messageBundle = messageBundle;
        this.uiComponents = uiComponents;
        this.dialogs = dialogs;
    }

    @Override
    public void openDialog(
            EntityAttributeDefaultValue entityAttributeDefaultValue,
            FrameOwner frameOwner,
            Runnable afterCancelHandler
    ) {
        dialogs.createInputDialog(frameOwner)
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
                })
                .show();
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
