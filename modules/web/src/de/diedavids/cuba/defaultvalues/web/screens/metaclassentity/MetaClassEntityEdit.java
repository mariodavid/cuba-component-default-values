package de.diedavids.cuba.defaultvalues.web.screens.metaclassentity;

import com.haulmont.chile.core.datatypes.Datatype;
import com.haulmont.chile.core.datatypes.impl.StringDatatype;
import com.haulmont.chile.core.model.MetaClass;
import com.haulmont.chile.core.model.MetaProperty;
import com.haulmont.chile.core.model.Range;
import com.haulmont.cuba.core.global.MessageTools;
import com.haulmont.cuba.core.global.Metadata;
import com.haulmont.cuba.gui.Dialogs;
import com.haulmont.cuba.gui.app.core.inputdialog.InputParameter;
import com.haulmont.cuba.gui.components.Action;
import com.haulmont.cuba.gui.components.Table;
import com.haulmont.cuba.gui.model.CollectionContainer;
import com.haulmont.cuba.gui.model.CollectionLoader;
import com.haulmont.cuba.gui.model.DataContext;
import com.haulmont.cuba.gui.screen.*;
import de.diedavids.cuba.defaultvalues.entity.DefaultValueConfiguration;
import de.diedavids.cuba.defaultvalues.entity.MetaClassEntity;
import de.diedavids.cuba.defaultvalues.metadata.MetadataDataProvider;
import de.diedavids.cuba.entitysoftreference.EntitySoftReferenceDatatype;
import org.springframework.util.StringUtils;

import javax.inject.Inject;
import java.text.ParseException;
import java.util.List;

import static com.haulmont.cuba.gui.app.core.inputdialog.InputDialog.INPUT_DIALOG_OK_ACTION;

@UiController("ddcdv_MetaClassEntity.edit")
@UiDescriptor("meta-class-entity-edit.xml")
@EditedEntityContainer("metaClassEntityDc")
public class MetaClassEntityEdit extends StandardEditor<MetaClassEntity> {


    @Inject
    protected Dialogs dialogs;
    @Inject
    protected MessageBundle messageBundle;
    @Inject
    protected MessageTools messageTools;
    @Inject
    protected Metadata metadata;
    @Inject
    protected MetadataDataProvider metadataDataProvider;

    @Inject
    protected CollectionLoader<DefaultValueConfiguration> defaultValueConfigurationsDl;
    @Inject
    protected CollectionContainer<DefaultValueConfiguration> defaultValueConfigurationsDc;
    @Inject
    protected Table<DefaultValueConfiguration> defaultValuesTable;
    @Inject
    protected DataContext dataContext;


    @Subscribe
    protected void onBeforeShow(BeforeShowEvent event) {
        defaultValueConfigurationsDl.setParameter("entity", getEntityMetaClass());
        getScreenData().loadAll();


        List<DefaultValueConfiguration> existingDefaultValues = defaultValueConfigurationsDc.getItems();

        MetaClass metaClass = getEntityMetaClass();

        metadataDataProvider.getBusinessMetaProperties(metaClass).stream()
                .filter(metaProperty ->
                    !isPartOfExistingMetaProperties(existingDefaultValues, metaProperty)
                )
                .map(this::createDefaultValueConfiguration)
                .forEach(defaultValueConfiguration ->
                        defaultValueConfigurationsDc.getMutableItems().add(defaultValueConfiguration)
                );

    }

    @Subscribe(target = Target.DATA_CONTEXT)
    protected void onPreCommit(DataContext.PreCommitEvent event) {
        defaultValueConfigurationsDc.getItems().stream()
                .filter(defaultValueConfiguration -> defaultValueConfiguration.getValue() != null)
                .forEach(defaultValueConfiguration -> dataContext.merge(defaultValueConfiguration));
    }
    
    
    


    private boolean isPartOfExistingMetaProperties(List<DefaultValueConfiguration> existingDefaultValues, MetaProperty metaProperty) {
        return existingDefaultValues.stream()
                .anyMatch(defaultValueConfiguration -> defaultValueConfiguration.getEntityAttribute().equals(metaProperty.getName()));
    }

    private DefaultValueConfiguration createDefaultValueConfiguration(MetaProperty metaProperty) {
        DefaultValueConfiguration defaultValueConfiguration = metadata.create(DefaultValueConfiguration.class);
        defaultValueConfiguration.setEntity(getEntityMetaClass());
        defaultValueConfiguration.setEntityAttribute(metaProperty.getName());

        return defaultValueConfiguration;
    }




    @Install(to = "defaultValuesTable.entityAttribute", subject = "formatter")
    protected String defaultValuesTableMetaPropertyFormatter(String metaProperty) {
        return messageTools.getPropertyCaption(getEntityMetaClass().getProperty(metaProperty));
    }



    private MetaClass getEntityMetaClass() {
        return metadata.getClass(getEditedEntity().getName());
    }

    @Subscribe("defaultValuesTable.setDefaultValue")
    protected void onDefaultValuesTableSetDefaultValue(Action.ActionPerformedEvent event) {

        DefaultValueConfiguration defaultValueConfiguration = defaultValuesTable.getSingleSelected();

        MetaProperty property = getEntityMetaClass().getProperty(defaultValueConfiguration.getEntityAttribute());
        Range propertyRange = property.getRange();
        InputParameter result = null;

        if (propertyRange.isDatatype()) {
            Datatype<Object> datatype = propertyRange.asDatatype();
            result = inputParameter(property)
                    .withDatatype(datatype);

            setDefaultValueIfRequired(
                    result,
                    datatype,
                    defaultValueConfiguration.getValue()
            );

            requestDefaultValue(result, datatype, defaultValueConfiguration);
        }
        else if (propertyRange.isEnum()) {
            Datatype<Object> datatype = propertyRange.asEnumeration();
            result = inputParameter(property)
                    .withEnumClass(datatype.getJavaClass());

            setDefaultValueIfRequired(
                    result,
                    datatype,
                    defaultValueConfiguration.getValue()
            );

            requestDefaultValue(result, datatype, defaultValueConfiguration);
        }
        else if (propertyRange.isClass()) {
            EntitySoftReferenceDatatype datatype = new EntitySoftReferenceDatatype();

            result = inputParameter(property)
                    .withEntityClass(propertyRange.asClass().getJavaClass());

            setDefaultValueIfRequired(
                    result,
                    datatype,
                    defaultValueConfiguration.getValue()
            );

            requestDefaultValue(result, datatype, defaultValueConfiguration);
        }
        else {
            StringDatatype datatype = new StringDatatype();
            result = inputParameter(property)
                    .withDatatype(datatype);

            setDefaultValueIfRequired(
                    result,
                    datatype,
                    defaultValueConfiguration.getValue()
            );

            requestDefaultValue(result, datatype, defaultValueConfiguration);
        }
    }

    private void setDefaultValueIfRequired(InputParameter result, Datatype datatype, String value) {
        if (!StringUtils.isEmpty(value)) {
            try {
                result.withDefaultValue(datatype.parse(value));
            } catch (ParseException e) {
                e.printStackTrace();
            }
        }
    }

    private InputParameter inputParameter(MetaProperty property) {
        return InputParameter.parameter("value")
                .withCaption(messageTools.getPropertyCaption(property));
    }

    private void requestDefaultValue(
            InputParameter inputParameter,
            Datatype datatype,
            DefaultValueConfiguration defaultValueConfiguration
    ) {
        dialogs.createInputDialog(this)
                .withParameter(inputParameter)
                .withCaption(messageBundle.getMessage("setValueCaption"))
                .withCloseListener(closeEvent -> {
                    if (closeEvent.getCloseAction().equals(INPUT_DIALOG_OK_ACTION)) {
                        Object providedDefaultValue = closeEvent.getValue("value");
                        defaultValueConfiguration.setValue(datatype.format(providedDefaultValue));
                    }
                })
                .show();
    }

}