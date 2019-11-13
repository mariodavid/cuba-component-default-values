package de.diedavids.cuba.defaultvalues.web.screens.entityattributedefaultvalue.edit.dialogs;

import com.haulmont.cuba.gui.app.core.inputdialog.InputDialog;
import com.haulmont.cuba.gui.screen.FrameOwner;
import de.diedavids.cuba.defaultvalues.entity.EntityAttributeDefaultValue;

public interface DefaultValueTypeDialogBuilder {

    InputDialog createDialog(
            EntityAttributeDefaultValue entityAttributeDefaultValue,
            FrameOwner frameOwner,
            Runnable afterCancelHandler
    );

}
