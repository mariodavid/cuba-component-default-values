package de.diedavids.cuba.defaultvalues.core


import com.haulmont.cuba.core.global.AppBeans
import com.haulmont.cuba.core.global.DataManager
import com.haulmont.cuba.core.global.EntityLoadInfoBuilder
import com.haulmont.cuba.core.global.Metadata
import de.diedavids.cuba.defaultvalues.DdcdvTestContainer
import de.diedavids.cuba.defaultvalues.entity.EntityAttributeDefaultValue
import de.diedavids.cuba.defaultvalues.entity.EntityAttributeDefaultValueType
import de.diedavids.cuba.defaultvalues.entity.example.mlb.MlbPlayer
import de.diedavids.cuba.defaultvalues.entity.example.mlb.MlbTeam
import de.diedavids.cuba.defaultvalues.entity.example.mlb.State
import org.junit.ClassRule
import spock.lang.Shared
import spock.lang.Specification

import java.time.LocalDate

class DefaultValuesDatatypesIntegrationSpec extends DefaultValuesIntegrationSpec {

    private EntityLoadInfoBuilder entityLoadInfoBuilder

    void setup()  {
        entityLoadInfoBuilder = AppBeans.get(EntityLoadInfoBuilder)
    }

    void "a String default value can be configured"() {

        given:
        configuration = staticDefaultValue(
                'ddcdv$MlbTeam',
                'name',
                'Boston Braves'
        )


        when:
        MlbTeam sut = metadata.create(MlbTeam)

        then:
        sut.name == "Boston Braves"
    }


    void "an Integer default value can be configured"() {

        given:
        configuration = staticDefaultValue(
                'ddcdv$MlbTeam',
                'telephone',
                '12313'
        )


        when:
        MlbTeam sut = metadata.create(MlbTeam)

        then:
        sut.telephone == 12313

    }


    void "a Big Decimal default value can be configured"() {

        given:
        configuration = staticDefaultValue(
                'ddcdv$MlbPlayer',
                'annualSalary',
                '12313'
        )


        when:
        MlbPlayer sut = metadata.create(MlbPlayer)

        then:
        sut.annualSalary == 12313.0

    }



    void "a Date default value can be configured"() {

        given:
        configuration = staticDefaultValue(
                'ddcdv$MlbPlayer',
                'birthday',
                '2019-01-01'
        )


        when:
        MlbPlayer sut = metadata.create(MlbPlayer)

        then:
        sut.birthday == LocalDate.of(2019,1,1).toDate()

    }


    void "a boolean default value can be configured"() {

        given:
        configuration = staticDefaultValue(
                'ddcdv$MlbPlayer',
                'leftHanded',
                'true'
        )


        when:
        MlbPlayer sut = metadata.create(MlbPlayer)

        then:
        sut.leftHanded

    }


    void "an Enum default value can be configured"() {

        given:
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

    void "an Entity Reference value can be configured"() {

        given:
        def team = metadata.create(MlbTeam)

        team.name = "Boston Braves"
        team.code = "BSN"

        def mlbTeam = dataManager.commit(team)

        def entityReference = entityLoadInfoBuilder.create(team).toString()

        configuration = staticDefaultValue(
                'ddcdv$MlbPlayer',
                'team',
                entityReference
        )

        when:
        MlbPlayer sut = metadata.create(MlbPlayer)

        then:
        sut.team == mlbTeam
    }


}