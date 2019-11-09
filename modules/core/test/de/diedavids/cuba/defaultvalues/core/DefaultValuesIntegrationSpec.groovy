package de.diedavids.cuba.defaultvalues.core

import com.haulmont.bali.db.QueryRunner
import com.haulmont.cuba.core.global.AppBeans
import com.haulmont.cuba.core.global.DataManager
import com.haulmont.cuba.core.global.Metadata
import com.haulmont.cuba.core.global.UserSessionSource
import com.haulmont.cuba.security.entity.User
import de.diedavids.cuba.defaultvalues.DdcdvTestContainer
import de.diedavids.cuba.defaultvalues.entity.EntityAttributeDefaultValue
import de.diedavids.cuba.defaultvalues.entity.EntityAttributeDefaultValueType
import de.diedavids.cuba.defaultvalues.entity.example.mlb.MlbTeam
import de.diedavids.cuba.defaultvalues.entity.example.mlb.State
import org.junit.ClassRule
import spock.lang.Shared
import spock.lang.Specification

import java.sql.SQLException

abstract class DefaultValuesIntegrationSpec extends Specification {


    @Shared
    @ClassRule
    public DdcdvTestContainer cont = DdcdvTestContainer.Common.INSTANCE

    protected Metadata metadata
    protected UserSessionSource userSessionSource
    protected DataManager dataManager
    protected EntityAttributeDefaultValue configuration

    void setup() {
        userSessionSource = AppBeans.get(UserSessionSource.NAME)
        dataManager = AppBeans.get(DataManager.NAME)
        metadata = cont.metadata()

        clearTable("DDCDV_ENTITY_ATTRIBUTE_DEFAULT_VALUE")
    }

    void cleanup() {
        cont.deleteRecord(configuration)
    }


    protected EntityAttributeDefaultValue defaultValueConfiguration(
            EntityAttributeDefaultValueType type,
            String entity,
            String entityAttribute,
            String value
    ) {
        EntityAttributeDefaultValue configuration = metadata.create(EntityAttributeDefaultValue.class)

        def targetMetaClass = metadata.getClass(entity)
        configuration.setType(type)
        configuration.setEntity(targetMetaClass)
        configuration.setEntityAttribute(targetMetaClass.getProperty(entityAttribute))
        configuration.setValue(value)

        dataManager.commit(configuration)
    }


    protected EntityAttributeDefaultValue staticDefaultValue(String entity, String entityAttribute, String value) {
        defaultValueConfiguration(
                EntityAttributeDefaultValueType.STATIC_VALUE,
                entity,
                entityAttribute,
                value
        )
    }


    protected EntityAttributeDefaultValue sessionDefaultValue(String entity, String entityAttribute, String value) {
        defaultValueConfiguration(
                EntityAttributeDefaultValueType.SESSION_ATTRIBUTE,
                entity,
                entityAttribute,
                value
        )
    }

    protected EntityAttributeDefaultValue scriptDefaultValue(String entity, String entityAttribute, String value) {
        defaultValueConfiguration(
                EntityAttributeDefaultValueType.SCRIPT,
                entity,
                entityAttribute,
                value
        )
    }


    protected void clearTable(String tableName) {
        String sql = "delete from $tableName"
        QueryRunner runner = new QueryRunner(cont.persistence().getDataSource());
        try {
            runner.update(sql);
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }


    }
}