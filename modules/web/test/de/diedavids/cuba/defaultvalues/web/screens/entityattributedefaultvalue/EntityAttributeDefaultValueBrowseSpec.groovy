package de.diedavids.cuba.defaultvalues.web.screens.entityattributedefaultvalue

import com.haulmont.cuba.core.global.AppBeans
import com.haulmont.cuba.core.global.Metadata
import com.haulmont.cuba.gui.Screens
import com.haulmont.cuba.gui.components.Table
import com.haulmont.cuba.gui.model.CollectionContainer
import com.haulmont.cuba.gui.screen.Screen
import com.haulmont.cuba.gui.screen.UiControllerUtils
import com.haulmont.cuba.web.testsupport.TestUiEnvironment
import de.diedavids.cuba.defaultvalues.DefaultValuesWebTestContainer
import de.diedavids.cuba.metadataextensions.entity.MetaClassEntity
import org.junit.ClassRule
import spock.lang.Shared
import spock.lang.Specification

class EntityAttributeDefaultValueBrowseSpec extends Specification {

    @Shared
    @ClassRule
    TestUiEnvironment environment =
            new TestUiEnvironment(DefaultValuesWebTestContainer.Common.INSTANCE)
                    .withScreenPackages(
                            "com.haulmont.cuba.web.app.main",
                            "com.haulmont.cuba.gui.app.core.inputdialog",
                            "de.diedavids.cuba.metadataextensions",
                            "de.diedavids.cuba.defaultvalues"
                    )
                    .withUserLogin("admin")

    Metadata metadata

    void setup() {
        metadata = AppBeans.get(Metadata.class);
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

    private CollectionContainer collectionContainer(Screen screen, String collectionContainerId) {
        UiControllerUtils.getScreenData(screen).getContainer(collectionContainerId)
    }

    private Screens screens() {
        environment.getScreens()
    }

}
