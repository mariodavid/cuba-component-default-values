package de.diedavids.cuba.defaultvalues.core.metadatawithdefaultvalues


import de.diedavids.cuba.defaultvalues.core.DefaultValuesIntegrationSpec
import de.diedavids.cuba.defaultvalues.entity.example.sales.Order

import java.time.LocalDate

class ScriptsIntegrationSpec extends DefaultValuesIntegrationSpec {


    void "a script default value is evaluated and assigned to the value"() {

        given:

        configuration = scriptDefaultValue(
                'ddcdv$Order',
                'orderDate',
                """
import java.time.LocalDate
return LocalDate.now()
"""
        )

        when:
        Order sut = metadata.create(Order)

        then:
        sut.orderDate == LocalDate.now()
    }


    void "an invalid script return type will not be assigned"() {

        given:

        configuration = scriptDefaultValue(
                'ddcdv$Order',
                'orderDate',
                """
return new Date()
"""
        )

        when:
        Order sut = metadata.create(Order)

        then:
        !sut.orderDate
    }


    void "an exception within the script execution assignes no value"() {

        given:

        configuration = scriptDefaultValue(
                'ddcdv$Order',
                'orderDate',
                """
throw new RuntimeException('did not work')
"""
        )

        when:
        Order sut = metadata.create(Order)

        then:
        !sut.orderDate
    }

}