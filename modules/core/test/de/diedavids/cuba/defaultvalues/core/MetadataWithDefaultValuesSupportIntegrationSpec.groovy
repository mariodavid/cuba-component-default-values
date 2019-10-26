package de.diedavids.cuba.defaultvalues.core

import com.haulmont.cuba.core.global.AppBeans
import com.haulmont.cuba.core.global.DataManager
import com.haulmont.cuba.core.global.Metadata
import com.haulmont.cuba.security.entity.User
import de.diedavids.cuba.defaultvalues.DdcdvTestContainer
import de.diedavids.cuba.defaultvalues.entity.DefaultValueConfiguration
import de.diedavids.cuba.defaultvalues.entity.example.mlb.MlbTeam
import de.diedavids.cuba.defaultvalues.entity.example.mlb.State
import de.diedavids.cuba.defaultvalues.service.DefaultValuesConfigurationService
import org.junit.ClassRule
import spock.lang.Shared
import spock.lang.Specification

class MetadataWithDefaultValuesSupportIntegrationSpec extends Specification {


    @Shared
    @ClassRule
    public DdcdvTestContainer cont = DdcdvTestContainer.Common.INSTANCE

    private Metadata metadata;
    private DataManager dataManager;
    private DefaultValuesConfigurationService defaultValuesConfigurationService
    private DefaultValueConfiguration configuration

    void setup() {
        dataManager = AppBeans.get(DataManager.class);
        metadata = AppBeans.get(Metadata.class);
        defaultValuesConfigurationService = AppBeans.get(DefaultValuesConfigurationService.class);
    }

    void cleanup() {
        cont.deleteRecord(configuration)
    }

    void "a configured default value for an entity is automatically applied to a newly created instance"() {

        given:
        configuration = defaultValueConfiguration(
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
                    setState(State.CO);
                }
            }

        }
        */

        and: "the default value overrides an attribute configured in a @PostConstruct method"
        configuration = defaultValueConfiguration(
                'ddcdv$MlbTeam',
                'state',
                'AZ'
        )

        when:
        MlbTeam sut = metadata.create(MlbTeam)

        then:
        sut.state == State.AZ

    }

    private DefaultValueConfiguration defaultValueConfiguration(String entity, String entityAttribute, String value) {
        DefaultValueConfiguration configuration = metadata.create(DefaultValueConfiguration.class)

        configuration.setEntity(entity)
        configuration.setEntityAttribute(entityAttribute)
        configuration.setValue(value)

        dataManager.commit(configuration)
    }

}