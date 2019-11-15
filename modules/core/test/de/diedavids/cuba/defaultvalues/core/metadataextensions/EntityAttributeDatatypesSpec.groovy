package de.diedavids.cuba.defaultvalues.core.metadataextensions

import com.haulmont.chile.core.datatypes.Datatype
import com.haulmont.chile.core.datatypes.DatatypeRegistry
import com.haulmont.chile.core.model.MetaClass
import com.haulmont.chile.core.model.MetaProperty
import com.haulmont.cuba.core.entity.Entity
import com.haulmont.cuba.core.entity.EntitySnapshot
import com.haulmont.cuba.core.global.AppBeans
import com.haulmont.cuba.security.entity.User
import de.diedavids.cuba.defaultvalues.EntityAttributeDatatypes
import de.diedavids.cuba.defaultvalues.core.DefaultValuesIntegrationSpec


class EntityAttributeDatatypesSpec extends DefaultValuesIntegrationSpec {

    EntityAttributeDatatypes sut
    DatatypeRegistry datatypeRegistry


    def setup() {
        sut = AppBeans.get(EntityAttributeDatatypes)
        datatypeRegistry = AppBeans.get(DatatypeRegistry)
    }

    def "getEntityAttributeDatatype returns String datatype for a String entity attribute"() {
        expect:
        sut.getEntityAttributeDatatype(metaProperty(User, "login")) == stringDatatype()
    }

    def "getEntityAttributeDatatype returns DateTime datatype for a DateTime entity attribute"() {

        expect:
        sut.getEntityAttributeDatatype(metaProperty(EntitySnapshot, "snapshotDate")) == dateTimeDatatype()
    }

    private MetaProperty metaProperty(Class<? extends Entity> entityClass, String attribute) {
        userMetaClass(entityClass).getProperty(attribute)
    }

    private Datatype stringDatatype() {
        datatypeRegistry.get("string")
    }
    private Datatype dateTimeDatatype() {
        datatypeRegistry.get("dateTime")
    }

    private MetaClass userMetaClass(Class<? extends Entity> entityClass) {
        metadata.getClass(entityClass)
    }
}
