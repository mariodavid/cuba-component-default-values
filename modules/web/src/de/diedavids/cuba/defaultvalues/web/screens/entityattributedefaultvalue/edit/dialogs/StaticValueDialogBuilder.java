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

public class StaticValueDialogBuilder implements DefaultValueTypeDialogBuilder<Object> {

    private final Metadata metadata;
    private final MessageBundle messageBundle;
    private final EntityDialogs entityDialogs;

    public StaticValueDialogBuilder(
            Metadata metadata,
            MessageBundle messageBundle,
            EntityDialogs entityDialogs
    ) {

        this.metadata = metadata;
        this.messageBundle = messageBundle;
        this.entityDialogs = entityDialogs;
    }

    @Override
    public InputDialog createDialog(
            EntityAttributeDefaultValue entityAttributeDefaultValue,
            FrameOwner frameOwner,
            Consumer<Object> afterOkHandler,
            Runnable afterCancelHandler
    ) {
        Class<Entity> entityClass = entityAttributeDefaultValue.getEntity().getJavaClass();

        Entity entity = metadata.create(entityClass);

        return entityDialogs.createEntityInputDialog(frameOwner, entityClass)
                .withEntity(entity)
                .withCaption(messageBundle.getMessage("staticDefaultValueCaption"))
                .withParameter(
                        entityAttributeParameter(entityClass, entityAttributeName(entityAttributeDefaultValue))
                                .withRequired(true)
                                .withAutoBinding(true)
                )
                .withCloseListener(new Consumer<InputDialog.InputDialogCloseEvent>() {
                    @Override
                    public void accept(InputDialog.InputDialogCloseEvent closeEvent) {
                        if (closeEvent.getCloseAction().equals(INPUT_DIALOG_OK_ACTION)) {
                            setStaticDefaultValue(entity, entityAttributeDefaultValue);
                            afterOkHandler.accept(closeEvent.getValue(entityAttributeName(entityAttributeDefaultValue)));
                        } else {
                            afterCancelHandler.run();
                        }
                    }
                }).build();
    }

    private String entityAttributeName(EntityAttributeDefaultValue entityAttributeDefaultValue) {
        return entityAttributeDefaultValue.getEntityAttribute().getName();
    }

    private void setStaticDefaultValue(Entity entity, EntityAttributeDefaultValue entityAttributeDefaultValue) {

        Object defaultValue = entity.getValue(entityAttributeName(entityAttributeDefaultValue));
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
