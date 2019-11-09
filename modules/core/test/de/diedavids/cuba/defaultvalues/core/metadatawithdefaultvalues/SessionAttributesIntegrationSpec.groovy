package de.diedavids.cuba.defaultvalues.core.metadatawithdefaultvalues

import com.haulmont.cuba.security.entity.User
import de.diedavids.cuba.defaultvalues.core.DefaultValuesIntegrationSpec
import de.diedavids.cuba.defaultvalues.entity.EntityAttributeDefaultValueType
import de.diedavids.cuba.defaultvalues.entity.example.mlb.MlbTeam
import de.diedavids.cuba.defaultvalues.entity.example.mlb.State

class SessionAttributesIntegrationSpec extends DefaultValuesIntegrationSpec {


    void "a configured session variable default value for an entity is automatically applied to a newly created instance"() {

        given:
        def userSession = userSessionSource.getUserSession()


        userSession.setAttribute("defaultEmailForCurrentUser", "bart@simpsons.com")

        configuration = sessionDefaultValue(
                'sec$User',
                'email',
                ':session$defaultEmailForCurrentUser'
        )

        when:
        User sut = metadata.create(User)

        then:
        sut.email == "bart@simpsons.com"
    }

    void "when the session attribute is missing in the session nothing is assigneds"() {

        given:
        userSessionSource
                .getUserSession()
                .setAttribute("defaultEmailForCurrentUser", "bart@simpsons.com")

        and:
        configuration = sessionDefaultValue(
                'sec$User',
                'email',
                ':session$notExistingSessionAttribute'
        )

        when:
        User sut = metadata.create(User)

        then:
        !sut.email
    }


    void "the session attribute type needs to be set correctly in order to fetch the value from the user session"() {

        given:
        def userSession = userSessionSource.getUserSession()


        userSession.setAttribute("defaultEmailForCurrentUser", "bart@simpsons.com")


        and:
        configuration = staticDefaultValue(
                'sec$User',
                'email',
                ':session$defaultEmailForCurrentUser'
        )

        assert configuration.type == EntityAttributeDefaultValueType.STATIC_VALUE

        when:
        User sut = metadata.create(User)

        then:
        sut.email == ':session$defaultEmailForCurrentUser'
    }

}