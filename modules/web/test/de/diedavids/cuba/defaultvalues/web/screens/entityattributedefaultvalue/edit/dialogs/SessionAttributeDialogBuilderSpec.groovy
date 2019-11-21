package de.diedavids.cuba.defaultvalues.web.screens.entityattributedefaultvalue.edit.dialogs

import com.haulmont.cuba.core.global.Messages
import com.haulmont.cuba.gui.UiComponents
import com.haulmont.cuba.gui.components.LookupField
import com.haulmont.cuba.gui.components.SourceCodeEditor
import com.haulmont.cuba.gui.components.TextInputField
import com.haulmont.cuba.gui.screen.MessageBundle
import com.haulmont.cuba.gui.screen.Screen
import com.haulmont.cuba.security.entity.User
import de.diedavids.cuba.defaultvalues.entity.EntityAttributeDefaultValue
import de.diedavids.cuba.defaultvalues.entity.EntityAttributeDefaultValueType
import de.diedavids.cuba.defaultvalues.service.SessionAttributeService

class SessionAttributeDialogBuilderSpec extends DefaultValueBuilderSpec {

    SessionAttributeService sessionAttributeService


    def setup() {
        sessionAttributeService = Mock(SessionAttributeService)

        sut = new SessionAttributeDialogBuilder(
                environment.getDialogs(),
                Mock(MessageBundle),
                environment.container.getBean(UiComponents),
                environment.container.getBean(Messages).getTools(),
                sessionAttributeService
        )
    }


    def "createDialog creates a lookupField with all possible session attributes"() {

        given:
        EntityAttributeDefaultValue entityAttributeDefaultValue = userDefaultValue('email')
        entityAttributeDefaultValue.value = ':session$defaultEmailAddress'

        and:
        sessionAttributeService.getAvailableSessionAttributes() >> ['defaultGroup','defaultEmailAddress']

        when:
        Screen inputDialog = showScreen(entityAttributeDefaultValue)

        and:
        LookupField<String> emailSessionAttributeLookupField = inputDialog.getWindow().getComponent("sessionAttribute")

        then:
        emailSessionAttributeLookupField.options.options.count() == 2
    }

    def "createDialog creates a lookupField and selects the attribute from the default value configuration"() {

        given:
        EntityAttributeDefaultValue entityAttributeDefaultValue = userDefaultValue('email')
        entityAttributeDefaultValue.value = ':session$defaultEmailAddress'

        and:
        sessionAttributeService.getAvailableSessionAttributes() >> ['defaultGroup','defaultEmailAddress']

        when:
        Screen inputDialog = showScreen(entityAttributeDefaultValue)

        and:
        LookupField<String> emailSessionAttributeLookupField = inputDialog.getWindow().getComponent("sessionAttribute")

        then:
        emailSessionAttributeLookupField.value == ':session$defaultEmailAddress'
    }

    def "createDialog creates a lookupField which is required"() {

        given:
        EntityAttributeDefaultValue entityAttributeDefaultValue = userDefaultValue('email')
        entityAttributeDefaultValue.value = ':session$defaultEmailAddress'

        and:
        sessionAttributeService.getAvailableSessionAttributes() >> ['defaultGroup','defaultEmailAddress']

        when:
        Screen inputDialog = showScreen(entityAttributeDefaultValue)

        and:
        LookupField<String> emailSessionAttributeLookupField = inputDialog.getWindow().getComponent("sessionAttribute")

        then:
        emailSessionAttributeLookupField.required
    }



    def "createDialog takes the value of the Lookup Field and binds it to the configuration entity"() {


        given:
        EntityAttributeDefaultValue entityAttributeDefaultValue = userDefaultValue('email')
        entityAttributeDefaultValue.value = ':session$defaultEmailAddress'

        and:
        sessionAttributeService.getAvailableSessionAttributes() >> ['defaultGroup','defaultEmailAddress']

        and:
        Screen inputDialog = showScreen(entityAttributeDefaultValue)

        and:
        LookupField<String> emailSessionAttributeLookupField = inputDialog.getWindow().getComponent("sessionAttribute")

        when:
        emailSessionAttributeLookupField.value = ':session$defaultEmailAddress'

        and:
        close(inputDialog)

        then:
        entityAttributeDefaultValue.value == ':session$defaultEmailAddress'
        entityAttributeDefaultValue.type == EntityAttributeDefaultValueType.SESSION_ATTRIBUTE

    }

    EntityAttributeDefaultValue userDefaultValue(String entityAttribute) {
        EntityAttributeDefaultValue entityAttributeDefaultValue = new EntityAttributeDefaultValue()
        def userMetaClass = metadata.getClass(User)
        entityAttributeDefaultValue.entity = userMetaClass
        entityAttributeDefaultValue.entityAttribute = userMetaClass.getProperty(entityAttribute)

        return entityAttributeDefaultValue;
    }

}
