package de.diedavids.cuba.defaultvalues.web.screens.entityattributedefaultvalue.edit.dialogs

import com.haulmont.cuba.core.global.Messages
import com.haulmont.cuba.gui.UiComponents
import com.haulmont.cuba.gui.components.RadioButtonGroup
import com.haulmont.cuba.gui.screen.MessageBundle
import com.haulmont.cuba.gui.screen.Screen
import com.haulmont.cuba.security.entity.User
import de.diedavids.cuba.defaultvalues.dynamicvalue.DynamicValueProviders
import de.diedavids.cuba.defaultvalues.entity.EntityAttributeDefaultValue
import de.diedavids.cuba.defaultvalues.entity.EntityAttributeDefaultValueType

class DynamicValueDialogBuilderSpec extends DefaultValueBuilderSpec {

    DynamicValueProviders dynamicValueProviders


    def setup() {
        dynamicValueProviders = Mock(DynamicValueProviders)

        sut = new DynamicValueDialogBuilder(
                environment.getDialogs(),
                Mock(MessageBundle),
                environment.container.getBean(UiComponents),
                environment.container.getBean(Messages),
                dynamicValueProviders
        )
    }


    def "createDialog creates a radio group with all possible dynamic value options"() {

        given:
        EntityAttributeDefaultValue entityAttributeDefaultValue = userDefaultValue('createTs')
        entityAttributeDefaultValue.value = 'tomorrowDateProvider'

        and:
        dynamicValuesAvailable(['currentDateProvider', 'tomorrowDateProvider'])

        when:
        Screen inputDialog = showScreen(entityAttributeDefaultValue)

        and:
        RadioButtonGroup<String> dynamicValueProviderField = dynamicValueInputComponent(inputDialog)

        then:
        dynamicValueProviderField.options.options.count() == 2
    }

    def "createDialog creates a radio group and selects the attribute from the default value configuration"() {

        given:
        EntityAttributeDefaultValue entityAttributeDefaultValue = userDefaultValue('createTs')
        entityAttributeDefaultValue.value = 'tomorrowDateProvider'

        and:
        dynamicValuesAvailable(['currentDateProvider', 'tomorrowDateProvider'])

        when:
        Screen inputDialog = showScreen(entityAttributeDefaultValue)

        and:
        RadioButtonGroup<String> dynamicValueProviderField = dynamicValueInputComponent(inputDialog)

        then:
        dynamicValueProviderField.value == 'tomorrowDateProvider'
    }

    def "createDialog creates a radio group which is required"() {
        given:
        EntityAttributeDefaultValue entityAttributeDefaultValue = userDefaultValue('createTs')
        entityAttributeDefaultValue.value = 'tomorrowDateProvider'

        and:
        dynamicValuesAvailable(['currentDateProvider', 'tomorrowDateProvider'])

        when:
        Screen inputDialog = showScreen(entityAttributeDefaultValue)

        and:
        RadioButtonGroup<String> dynamicValueProviderField = dynamicValueInputComponent(inputDialog)

        then:
        dynamicValueProviderField.required
    }


    def "createDialog takes the value of the radio group and binds it to the configuration entity"() {

        given:
        EntityAttributeDefaultValue entityAttributeDefaultValue = userDefaultValue('createTs')
        entityAttributeDefaultValue.value = 'tomorrowDateProvider'

        and:
        dynamicValuesAvailable(['currentDateProvider', 'tomorrowDateProvider'])

        and:
        Screen inputDialog = showScreen(entityAttributeDefaultValue)

        and:
        RadioButtonGroup<String> dynamicValueProviderField = dynamicValueInputComponent(inputDialog)

        when:
        dynamicValueProviderField.value = 'currentDateProvider'

        and:
        close(inputDialog)

        then:
        entityAttributeDefaultValue.value == 'currentDateProvider'
        entityAttributeDefaultValue.type == EntityAttributeDefaultValueType.DYNAMIC_VALUE

    }

    private void dynamicValuesAvailable(List<String> dynamicValueProviders) {
        this.dynamicValueProviders.getProvidersFor(_) >> dynamicValueProviders
    }

    private RadioButtonGroup<String> dynamicValueInputComponent(Screen inputDialog) {
        inputDialog.getWindow().getComponent("dynamicValueProvider") as RadioButtonGroup<String>
    }


    EntityAttributeDefaultValue userDefaultValue(String entityAttribute) {
        EntityAttributeDefaultValue entityAttributeDefaultValue = new EntityAttributeDefaultValue()
        def userMetaClass = metadata.getClass(User)
        entityAttributeDefaultValue.entity = userMetaClass
        entityAttributeDefaultValue.entityAttribute = userMetaClass.getProperty(entityAttribute)

        return entityAttributeDefaultValue;
    }

}
