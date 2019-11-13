package de.diedavids.cuba.defaultvalues.web.screens.entityattributedefaultvalue


import com.haulmont.cuba.gui.components.Table
import com.haulmont.cuba.gui.model.CollectionContainer
import de.diedavids.cuba.defaultvalues.web.WebIntegrationSpec
import de.diedavids.cuba.metadataextensions.entity.MetaClassEntity

class EntityAttributeDefaultValueBrowseSpec extends WebIntegrationSpec {

    def setup() {
        mainWindow()
    }

    def "all persistent MetaClasses are loaded in the collection data container"() {

        given:
        def expectedMetaClassesSize = metadata.tools.allPersistentMetaClasses.size()

        when:
        EntityAttributeDefaultValueBrowse sut = screens()
                .create(EntityAttributeDefaultValueBrowse)
                .show()

        CollectionContainer<MetaClassEntity> container = collectionContainer(sut, "metaClassEntitiesDc")

        then:
        container.items.size() == expectedMetaClassesSize
    }


    def "all persistent MetaClasses are rendered in the Table"() {

        given:
        def expectedMetaClassesSize = metadata.tools.allPersistentMetaClasses.size()

        EntityAttributeDefaultValueBrowse sut = screens()
                .create(EntityAttributeDefaultValueBrowse)
                .show()

        CollectionContainer<MetaClassEntity> container = collectionContainer(sut, "metaClassEntitiesDc")

        and:
        Table<MetaClassEntity> metaClassEntitiesTable = metaClassEntityTable(sut)
        metaClassEntitiesTable.multiSelect = true

        when:
        metaClassEntitiesTable.setSelected(container.getItems())

        then:
        metaClassEntitiesTable.selected.size() == expectedMetaClassesSize
    }

    private Table<MetaClassEntity> metaClassEntityTable(EntityAttributeDefaultValueBrowse sut) {
        (Table<MetaClassEntity>) sut.window.getComponent("metaClassEntitiesTable")
    }

}
