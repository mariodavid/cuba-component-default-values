package de.diedavids.cuba.defaultvalues.web.screens.entityattributedefaultvalue.edit.dialogs;


import com.haulmont.chile.core.datatypes.Datatype;
import com.haulmont.chile.core.model.MetaProperty;
import com.haulmont.cuba.core.entity.Entity;
import com.haulmont.cuba.core.global.Metadata;
import com.haulmont.cuba.gui.app.core.inputdialog.InputDialog;
import com.haulmont.cuba.gui.screen.FrameOwner;
import com.haulmont.cuba.gui.screen.MessageBundle;
import de.diedavids.cuba.defaultvalues.entity.EntityAttributeDefaultValue;
import de.diedavids.cuba.defaultvalues.entity.EntityAttributeDefaultValueType;
import de.diedavids.cuba.entitysoftreference.EntitySoftReferenceDatatype;
import de.diedavids.cuba.metadataextensions.EntityDialogs;

import java.util.function.Consumer;

import static com.haulmont.cuba.gui.app.core.inputdialog.InputDialog.INPUT_DIALOG_OK_ACTION;
import static de.diedavids.cuba.metadataextensions.EntityAttributeInputParameter.entityAttributeParameter;

public class StaticValueDialog implements DefaultValueTypeDialog {

    private final Metadata metadata;
    private final MessageBundle messageBundle;
    private final EntityDialogs entityDialogs;

    public StaticValueDialog(
            Metadata metadata,
            MessageBundle messageBundle,
            EntityDialogs entityDialogs
    ) {

        this.metadata = metadata;
        this.messageBundle = messageBundle;
        this.entityDialogs = entityDialogs;
    }

    @Override
    public void openDialog(
            EntityAttributeDefaultValue entityAttributeDefaultValue,
            FrameOwner frameOwner,
            Runnable afterCancelHandler
    ) {
        Class<Entity> entityClass = entityAttributeDefaultValue.getEntity().getJavaClass();

        Entity entity = metadata.create(entityClass);

        entityDialogs.createEntityInputDialog(frameOwner, entityClass)
                .withEntity(entity)
                .withCaption(messageBundle.getMessage("staticDefaultValueCaption"))
                .withParameter(
                        entityAttributeParameter(entityClass, entityAttributeDefaultValue.getEntityAttribute().getName())
                                .withRequired(true)
                                .withAutoBinding(true)
                )
                .withCloseListener(new Consumer<InputDialog.InputDialogCloseEvent>() {
                    @Override
                    public void accept(InputDialog.InputDialogCloseEvent closeEvent) {
                        if (closeEvent.getCloseAction().equals(INPUT_DIALOG_OK_ACTION)) {
                            setStaticDefaultValue(entity, entityAttributeDefaultValue);
                        } else {
                            afterCancelHandler.run();
                        }
                    }
                })
                .show();
    }

    private void setStaticDefaultValue(Entity entity, EntityAttributeDefaultValue entityAttributeDefaultValue) {

        // MetaProperty property = getEntityMetaClass().getProperty(entityAttributeDefaultValue.getEntityAttribute().getCode());
        Object defaultValue = entity.getValue(entityAttributeDefaultValue.getEntityAttribute().getName());
        //Datatype datatype = determineEntityAttributeDatatype(property);
        Datatype datatype = determineEntityAttributeDatatype(entityAttributeDefaultValue.getEntityAttribute());
        String formattedValue = datatype.format(defaultValue);
        entityAttributeDefaultValue.setValue(formattedValue);
        entityAttributeDefaultValue.setType(EntityAttributeDefaultValueType.STATIC_VALUE);
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


}
