package de.diedavids.cuba.defaultvalues.core.metadatawithdefaultvalues


import com.haulmont.cuba.security.entity.UserRole
import de.diedavids.cuba.defaultvalues.core.DefaultValuesIntegrationSpec
import de.diedavids.cuba.defaultvalues.dynamicvalue.CurrentUserDynamicValueProvider
import de.diedavids.cuba.defaultvalues.dynamicvalue.TodayDateProvider
import de.diedavids.cuba.defaultvalues.entity.example.mlb.MlbPlayer

import java.time.LocalDate

class DynamicValueIntegrationSpec extends DefaultValuesIntegrationSpec {




    void "a configured dynamic value with simple type will be assigned to the entity attribute"() {

        given:
        configuration = dynamicDefaultValue(
                'ddcdv$MlbPlayer',
                'birthday',
                TodayDateProvider.CODE
        )

        when:
        MlbPlayer sut = metadata.create(MlbPlayer)

        then:
        sut.birthday
    }

    void "a configured dynamic value is evaluated and assigned to the entity attribute"() {

        given:
        configuration = dynamicDefaultValue(
                'sec$UserRole',
                'user',
                CurrentUserDynamicValueProvider.CODE
        )

        when:
        UserRole sut = metadata.create(UserRole)

        then:
        sut.user == userSessionSource.getUserSession().getCurrentOrSubstitutedUser()
    }

    void "a configured dynamic value with the wrong target type is not evaluated"() {

        given:
        configuration = dynamicDefaultValue(
                'sec$UserRole',
                'role',
                CurrentUserDynamicValueProvider.CODE
        )

        when:
        UserRole sut = metadata.create(UserRole)

        then:
        !sut.role
    }
}