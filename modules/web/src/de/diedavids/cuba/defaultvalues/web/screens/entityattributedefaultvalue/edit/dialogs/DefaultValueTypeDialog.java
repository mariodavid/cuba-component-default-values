package de.diedavids.cuba.defaultvalues.web.screens.entityattributedefaultvalue.edit.dialogs;

import com.haulmont.cuba.gui.screen.FrameOwner;
import de.diedavids.cuba.defaultvalues.entity.EntityAttributeDefaultValue;

public interface DefaultValueTypeDialog {


    void openDialog(
            EntityAttributeDefaultValue entityAttributeDefaultValue,
            FrameOwner frameOwner,
            Runnable afterCancelHandler
    );

}
