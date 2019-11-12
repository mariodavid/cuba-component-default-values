package de.diedavids.cuba.defaultvalues.web.screens.entityattributedefaultvalue.typedelegate;

import com.haulmont.cuba.gui.screen.FrameOwner;
import de.diedavids.cuba.defaultvalues.entity.EntityAttributeDefaultValue;

public interface DefaultValueTypeEditDelegate {


    void openDialog(
            EntityAttributeDefaultValue entityAttributeDefaultValue,
            FrameOwner frameOwner,
            Runnable afterCancelHandler
    );
}
