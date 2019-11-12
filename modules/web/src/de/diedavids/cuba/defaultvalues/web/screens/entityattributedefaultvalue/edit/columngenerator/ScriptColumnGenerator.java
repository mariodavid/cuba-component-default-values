package de.diedavids.cuba.defaultvalues.web.screens.entityattributedefaultvalue.edit.columngenerator;


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
import de.diedavids.cuba.defaultvalues.web.screens.entityattributedefaultvalue.edit.dialogs.DefaultValueTypeDialog;

import java.util.function.Consumer;

import static com.haulmont.cuba.gui.app.core.inputdialog.InputDialog.INPUT_DIALOG_OK_ACTION;

public class ScriptColumnGenerator implements DefaultValueTypeColumnGenerator {

    @Override
    public String getUiValue(EntityAttributeDefaultValue entityAttributeDefaultValue) {
        return "...";
    }
}
