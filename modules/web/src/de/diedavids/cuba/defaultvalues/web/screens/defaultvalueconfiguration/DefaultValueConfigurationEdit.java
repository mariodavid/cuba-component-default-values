package de.diedavids.cuba.defaultvalues.web.screens.defaultvalueconfiguration;

import com.haulmont.chile.core.datatypes.Datatype;
import com.haulmont.chile.core.datatypes.impl.StringDatatype;
import com.haulmont.chile.core.model.MetaClass;
import com.haulmont.chile.core.model.MetaProperty;
import com.haulmont.chile.core.model.Range;
import com.haulmont.cuba.core.global.Messages;
import com.haulmont.cuba.core.global.Metadata;
import com.haulmont.cuba.gui.Dialogs;
import com.haulmont.cuba.gui.Notifications;
import com.haulmont.cuba.gui.UiComponents;
import com.haulmont.cuba.gui.app.core.inputdialog.InputParameter;
import com.haulmont.cuba.gui.components.Form;
import com.haulmont.cuba.gui.components.HasValue;
import com.haulmont.cuba.gui.components.LookupField;
import com.haulmont.cuba.gui.screen.*;
import de.diedavids.cuba.defaultvalues.EntitySoftReferenceDatatype;
import de.diedavids.cuba.defaultvalues.entity.DefaultValueConfiguration;
import de.diedavids.cuba.defaultvalues.metadata.MetadataDataProvider;

import javax.inject.Inject;

import java.util.function.Consumer;

import static com.haulmont.cuba.gui.app.core.inputdialog.InputDialog.INPUT_DIALOG_OK_ACTION;

@UiController("ddcdv_DefaultValueConfiguration.edit")
@UiDescriptor("default-value-configuration-edit.xml")
@EditedEntityContainer("defaultValueConfigurationDc")
@LoadDataBeforeShow
public class DefaultValueConfigurationEdit extends StandardEditor<DefaultValueConfiguration> {

    @Inject
    protected LookupField<MetaClass> entityField;

    @Inject
    protected LookupField<String> entityAttributeField;
    @Inject
    protected UiComponents uiComponents;
    @Inject
    protected Dialogs dialogs;
    @Inject
    protected Metadata metadata;
    @Inject
    protected Messages messages;
    @Inject
    protected MessageBundle messageBundle;
    @Inject
    protected Notifications notifications;

    @Inject
    MetadataDataProvider metadataDataProvider;

    @Inject
    protected Form form;

    @Subscribe
    protected void onInit(InitEvent event) {
        entityField.setOptionsMap(
                metadataDataProvider.getEntitiesLookupFieldOptions()
        );
    }

    @Subscribe("entityField")
    protected void onEntityFieldValueChange(HasValue.ValueChangeEvent<MetaClass> event) {

        entityAttributeField.setValue(null);

        if (event.getValue() != null) {
            entityAttributeField.setOptionsMap(
                    metadataDataProvider.getAllAttributesLookupFieldOptions(event.getValue())
            );
        }
        else {
            entityAttributeField.setEditable(false);
        }
    }

    @Subscribe("entityAttributeField")
    protected void onEntityAttributeFieldValueChange(HasValue.ValueChangeEvent<String> event) {

        InputParameter result;
        MetaClass metaClass = entityField.getValue();

        MetaProperty property = metaClass.getProperty(event.getValue());
        Range propertyRange = property.getRange();

        if (propertyRange.isDatatype()) {
            Datatype<Object> datatype = propertyRange.asDatatype();
            result = InputParameter.parameter("value")
                    .withCaption(messageBundle.getMessage("defaultValueCaption"))
                    .withDatatype(datatype);

            requestDefaultValue(result, datatype);
        }
        else if (propertyRange.isEnum()) {
            Datatype<Object> datatype = propertyRange.asEnumeration();
            result = InputParameter.parameter("value")
                    .withCaption(messageBundle.getMessage("defaultValueCaption"))
                    .withEnumClass(datatype.getJavaClass());

            requestDefaultValue(result, datatype);
        }
        else if (propertyRange.isClass()) {
            result = InputParameter.parameter("value")
                    .withCaption(messageBundle.getMessage("defaultValueCaption"))
                    .withEntityClass(propertyRange.asClass().getJavaClass());

            requestDefaultValue(result, new EntitySoftReferenceDatatype());
        }
        else {
            result = InputParameter
                    .stringParameter("value")
                    .withCaption(messageBundle.getMessage("defaultValueCaption"));

            requestDefaultValue(result, new StringDatatype());
        }


    }

    private void requestDefaultValue(
            InputParameter inputParameter,
            Datatype datatype
    ) {
        dialogs.createInputDialog(this)
                .withParameter(inputParameter)
                .withCaption(messageBundle.getMessage("setValueCaption"))
                .withCloseListener(closeEvent -> {
                    if (closeEvent.getCloseAction().equals(INPUT_DIALOG_OK_ACTION)) {
                        Object providedDefaultValue = closeEvent.getValue("value");
                        getEditedEntity().setValue(datatype.format(providedDefaultValue));
                    }
                })
                .show();
    }


}