package de.diedavids.cuba.defaultvalues.web.screens.entityattributedefaultvalue.edit.typedelegate;


import com.google.common.base.Strings;
import com.haulmont.chile.core.datatypes.Datatype;
import com.haulmont.chile.core.model.MetaClass;
import com.haulmont.chile.core.model.MetaProperty;
import com.haulmont.chile.core.model.Range;
import com.haulmont.cuba.core.entity.Entity;
import com.haulmont.cuba.core.global.*;
import com.haulmont.cuba.gui.app.core.inputdialog.InputDialog;
import com.haulmont.cuba.gui.screen.FrameOwner;
import com.haulmont.cuba.gui.screen.MessageBundle;
import de.diedavids.cuba.defaultvalues.entity.EntityAttributeDefaultValue;
import de.diedavids.cuba.defaultvalues.entity.EntityAttributeDefaultValueType;
import de.diedavids.cuba.entitysoftreference.EntitySoftReferenceDatatype;
import de.diedavids.cuba.metadataextensions.EntityDialogs;

import java.text.ParseException;
import java.util.function.Consumer;

import static com.haulmont.cuba.gui.app.core.inputdialog.InputDialog.INPUT_DIALOG_OK_ACTION;
import static de.diedavids.cuba.metadataextensions.EntityAttributeInputParameter.entityAttributeParameter;

public class StaticValueEditDelegate implements DefaultValueTypeEditDelegate {

    private final Metadata metadata;
    private final MessageBundle messageBundle;
    private final EntityDialogs entityDialogs;
    private final Messages messages;
    private final DataManager dataManager;
    private final EntityLoadInfoBuilder entityLoadInfoBuilder;

    public StaticValueEditDelegate(
            Metadata metadata,
            MessageBundle messageBundle,
            EntityDialogs entityDialogs,
            Messages messages,
            DataManager dataManager,
            EntityLoadInfoBuilder entityLoadInfoBuilder
    ) {

        this.metadata = metadata;
        this.messageBundle = messageBundle;
        this.entityDialogs = entityDialogs;
        this.messages = messages;
        this.dataManager = dataManager;
        this.entityLoadInfoBuilder = entityLoadInfoBuilder;
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
                //.withCaption(messageBundle.getMessage("staticDefaultValueCaption"))
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

        // MetaProperty property = getEntityMetaClass().getProperty(entityAttributeDefaultValue.getEntityAttribute().getName());
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


    @Override
    public String getUiValue(EntityAttributeDefaultValue entityAttributeDefaultValue) {
        Range range = entityAttributeDefaultValue.getEntityAttribute().getRange();


        if (range.isClass()) {
            Entity reference = convertToEntityAttribute(entityAttributeDefaultValue.getValue());
            if (reference != null) {
                return metadata.getTools().getInstanceName(reference);
            }
            else {
                return null;
            }
        }
        else if (range.isEnum()) {

            try {
                Datatype datatype = determineEntityAttributeDatatype(entityAttributeDefaultValue.getEntityAttribute());
                Object defaultValue = datatype.parse(entityAttributeDefaultValue.getValue());
                return messages.getMessage((Enum) defaultValue);
            } catch (ParseException e) {
                e.printStackTrace();
            }
        }
        else {
            return entityAttributeDefaultValue.getValue();
        }

        return null;
    }


    public Entity convertToEntityAttribute(String value) {


        if (Strings.isNullOrEmpty(value))
            return null;

        EntityLoadInfo entityLoadInfo = entityLoadInfoBuilder.parse(value);

        Entity entity = null;

        if (entityLoadInfo != null) {
            entity = loadEntity(entityLoadInfo);
        }

        return entity;
    }

    private Entity loadEntity(EntityLoadInfo entityLoadInfo) {
        return dataManager.load(
                getLoadContextForForEntityLoadInfo(
                        entityLoadInfo.getMetaClass(),
                        entityLoadInfo.getId()
                )
        );
    }

    protected LoadContext getLoadContextForForEntityLoadInfo(MetaClass metaClass, Object entityId) {
        LoadContext loadContext = LoadContext.create(metaClass.getJavaClass());
        loadContext
                .setId(entityId);
        return loadContext;
    }

}
