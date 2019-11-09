package de.diedavids.cuba.defaultvalues.core.metadatawithdefaultvalues

import com.haulmont.cuba.security.entity.User
import de.diedavids.cuba.defaultvalues.core.DefaultValuesIntegrationSpec
import de.diedavids.cuba.defaultvalues.entity.example.mlb.MlbTeam
import de.diedavids.cuba.defaultvalues.entity.example.mlb.State

class StaticValuesIntegrationSpec extends DefaultValuesIntegrationSpec {


    void "a configured static default value for an entity is automatically applied to a newly created instance"() {

        given:
        configuration = staticDefaultValue(
                'sec$User',
                'firstName',
                'mario'
        )


        when:
        User sut = metadata.create(User)

        then:
        sut.firstName == "mario"
    }

    void "default values are executed after @PostConstruct annotated methods"() {

        given: "the entity has a post construct method"

        /*
        @see {de.diedavids.cuba.defaultvalues.entity.example.mlb.MlbTeam}

        class MlbTeam {

            @PostConstruct
            protected void initState() {
                if (getState() != null) {
                    setState(State.CO)
                }
            }

        }
        */

        and: "the default value overrides an attribute configured in a @PostConstruct method"

        configuration = staticDefaultValue(
                'ddcdv$MlbTeam',
                'state',
                'AZ'
        )

        when:
        MlbTeam sut = metadata.create(MlbTeam)

        then:
        sut.state == State.AZ

    }



}