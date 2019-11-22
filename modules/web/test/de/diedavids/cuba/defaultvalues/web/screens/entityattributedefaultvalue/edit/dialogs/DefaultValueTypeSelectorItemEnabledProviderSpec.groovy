package de.diedavids.cuba.defaultvalues.web.screens.entityattributedefaultvalue.edit.dialogs

import com.haulmont.cuba.security.entity.User
import de.diedavids.cuba.defaultvalues.dynamicvalue.DynamicValueProviders
import de.diedavids.cuba.defaultvalues.entity.EntityAttributeDefaultValue
import de.diedavids.cuba.defaultvalues.entity.EntityAttributeDefaultValueType
import de.diedavids.cuba.defaultvalues.service.SessionAttributeService
import de.diedavids.cuba.defaultvalues.web.WebIntegrationSpec

import static de.diedavids.cuba.defaultvalues.entity.EntityAttributeDefaultValueType.*

class DefaultValueTypeSelectorItemEnabledProviderSpec extends WebIntegrationSpec {
    DynamicValueProviders dynamicValueProviders
    SessionAttributeService sessionAttributeService
    DefaultValueTypeSelectorItemEnabledProvider sut


    def setup() {
        dynamicValueProviders = Mock(DynamicValueProviders)
        sessionAttributeService = Mock(SessionAttributeService)

        sut = new DefaultValueTypeSelectorItemEnabledProvider(
                userDefaultValue("login"),
                dynamicValueProviders,
                sessionAttributeService
        )
    }

    def "when no dynamic value providers are available the option is not available"() {

        given:
        dynamicValuesAvailable([])

        expect:
        !optionAvailable(DYNAMIC_VALUE)
    }

    def "when one dynamic value providers are available the option is available"() {

        given:
        dynamicValuesAvailable(['myDynamicValueProvider1'])

        expect:
        optionAvailable(DYNAMIC_VALUE)
    }

    def "when no session attributes are available the option is not available"() {

        given:
        sessionAttributesAvailable([])
        expect:
        !optionAvailable(SESSION_ATTRIBUTE)
    }

    def "when one session attribute is available the option is available"() {

        given:
        sessionAttributesAvailable(['defaultGroup'])
        expect:
        optionAvailable(SESSION_ATTRIBUTE)
    }

    private boolean optionAvailable(EntityAttributeDefaultValueType type) {
        sut.test(type)
    }

    EntityAttributeDefaultValue userDefaultValue(String entityAttribute) {
        EntityAttributeDefaultValue entityAttributeDefaultValue = new EntityAttributeDefaultValue()
        def userMetaClass = metadata.getClass(User)
        entityAttributeDefaultValue.entity = userMetaClass
        entityAttributeDefaultValue.entityAttribute = userMetaClass.getProperty(entityAttribute)

        return entityAttributeDefaultValue
    }

    private void dynamicValuesAvailable(List<String> dynamicValueProviders) {
        this.dynamicValueProviders.getProvidersFor(_) >> dynamicValueProviders
    }
    private void sessionAttributesAvailable(List<String> sessionAttributes) {
        sessionAttributeService.getAvailableSessionAttributes() >> sessionAttributes
    }

}
