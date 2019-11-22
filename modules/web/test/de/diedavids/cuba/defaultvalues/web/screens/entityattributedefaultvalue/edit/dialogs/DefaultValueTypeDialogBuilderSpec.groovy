package de.diedavids.cuba.defaultvalues.web.screens.entityattributedefaultvalue.edit.dialogs


import com.haulmont.cuba.gui.UiComponents
import com.haulmont.cuba.gui.components.RadioButtonGroup
import com.haulmont.cuba.gui.screen.MessageBundle
import com.haulmont.cuba.gui.screen.Screen
import com.haulmont.cuba.security.entity.User
import de.diedavids.cuba.defaultvalues.dynamicvalue.DynamicValueProviders
import de.diedavids.cuba.defaultvalues.entity.EntityAttributeDefaultValue
import de.diedavids.cuba.defaultvalues.entity.EntityAttributeDefaultValueType
import de.diedavids.cuba.defaultvalues.service.SessionAttributeService

class DefaultValueTypeDialogBuilderSpec extends DefaultValueBuilderSpec {

    DynamicValueProviders dynamicValueProviders


    def setup() {
        dynamicValueProviders = Mock(DynamicValueProviders)

        sut = new DefaultValueTypeSelectorDialogBuilder(
                environment.getDialogs(),
                Mock(MessageBundle),
                environment.container.getBean(UiComponents),
                dynamicValueProviders,
                Mock(SessionAttributeService)
        )
    }


    def "createDialog creates a radio group with all possible types"() {

        given:
        EntityAttributeDefaultValue entityAttributeDefaultValue = userDefaultValue('email')
        entityAttributeDefaultValue.value = 'default@default.com'

        when:
        Screen inputDialog = showScreen(entityAttributeDefaultValue)

        and:
        RadioButtonGroup<String> typeField = defaultValueTypeInputComponent(inputDialog)

        then:
        typeField.options.options.count() == EntityAttributeDefaultValueType.values().length
    }

    def "createDialog deactivates dynamic value option if no options are available for this datatype"() {

        given:
        EntityAttributeDefaultValue entityAttributeDefaultValue = userDefaultValue('email')
        entityAttributeDefaultValue.value = 'default@default.com'

        and:
        dynamicValuesAvailable([])

        when:
        Screen inputDialog = showScreen(entityAttributeDefaultValue)

        and:
        RadioButtonGroup<String> typeField = defaultValueTypeInputComponent(inputDialog)
        com.vaadin.ui.RadioButtonGroup unwrappedTypeField = typeField.unwrap(com.vaadin.ui.RadioButtonGroup.class)

        typeField.value = EntityAttributeDefaultValueType.DYNAMIC_VALUE

        then:
        unwrappedTypeField.itemEnabledProvider instanceof DefaultValueTypeSelectorItemEnabledProvider
    }

    def "createDialog creates a radio group which is required"() {
        given:
        EntityAttributeDefaultValue entityAttributeDefaultValue = userDefaultValue('createTs')
        entityAttributeDefaultValue.value = 'tomorrowDateProvider'

        when:
        Screen inputDialog = showScreen(entityAttributeDefaultValue)

        and:
        RadioButtonGroup<String> typeField = defaultValueTypeInputComponent(inputDialog)

        then:
        typeField.required
    }


    def "createDialog takes the value of the radio group and binds it to the configuration entity"() {

        given:
        EntityAttributeDefaultValue entityAttributeDefaultValue = userDefaultValue('createTs')
        entityAttributeDefaultValue.type = EntityAttributeDefaultValueType.STATIC_VALUE

        and:
        Screen inputDialog = showScreen(entityAttributeDefaultValue)

        and:
        RadioButtonGroup<String> typeField = defaultValueTypeInputComponent(inputDialog)

        when:
        typeField.value = EntityAttributeDefaultValueType.SCRIPT

        and:
        close(inputDialog)

        then:
        entityAttributeDefaultValue.type == EntityAttributeDefaultValueType.SCRIPT

    }



    private void dynamicValuesAvailable(List<String> dynamicValueProviders) {
        this.dynamicValueProviders.getProvidersFor(_) >> dynamicValueProviders
    }

    private RadioButtonGroup<String> defaultValueTypeInputComponent(Screen inputDialog) {
        inputDialog.getWindow().getComponent("entityAttributeDefaultValueType") as RadioButtonGroup<String>
    }


    EntityAttributeDefaultValue userDefaultValue(String entityAttribute) {
        EntityAttributeDefaultValue entityAttributeDefaultValue = new EntityAttributeDefaultValue()
        def userMetaClass = metadata.getClass(User)
        entityAttributeDefaultValue.entity = userMetaClass
        entityAttributeDefaultValue.entityAttribute = userMetaClass.getProperty(entityAttribute)

        return entityAttributeDefaultValue;
    }

}
