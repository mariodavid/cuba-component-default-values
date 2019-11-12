package de.diedavids.cuba.defaultvalues.web.screens.entityattributedefaultvalue

import com.haulmont.chile.core.model.MetaClass
import com.haulmont.chile.core.model.MetaProperty
import com.haulmont.cuba.core.global.AppBeans
import com.haulmont.cuba.core.global.Metadata
import com.haulmont.cuba.gui.Screens
import com.haulmont.cuba.gui.app.core.inputdialog.InputDialog
import com.haulmont.cuba.gui.components.Table
import com.haulmont.cuba.gui.model.CollectionContainer
import com.haulmont.cuba.gui.screen.OpenMode
import com.haulmont.cuba.gui.screen.Screen
import com.haulmont.cuba.security.entity.Group
import com.haulmont.cuba.security.entity.User
import com.haulmont.cuba.web.app.main.MainScreen
import com.haulmont.cuba.web.gui.components.table.TableDataContainer
import com.haulmont.cuba.web.testsupport.TestUiEnvironment
import de.diedavids.cuba.defaultvalues.DefaultValuesWebTestContainer
import de.diedavids.cuba.defaultvalues.entity.EntityAttributeDefaultValue
import de.diedavids.cuba.metadataextensions.EntityAttributeInputParameter
import de.diedavids.cuba.metadataextensions.EntityDialogs
import org.junit.Rule
import spock.lang.Specification

class EntityAttributeDefaultValueEditSpec extends Specification {

    @Rule
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


    def "the default values of all parameters for the input dialog"() {

        given:
        def group = metadata.create(Group)

        Screen mainWindow = screens()
                .create(MainScreen.class, OpenMode.ROOT)
                .show()
        when:
        EntityAttributeDefaultValueBrowse sut = screens()
                .create(EntityAttributeDefaultValueBrowse)
                .show()

        def componets = sut.window.components
        CollectionContainer container = sut.window.getComponent("metaClassEntitiesDc")

        then:
        componets.size() == 12
        container.getItems().size() == 5

    }

    private Screens screens() {
        environment.getScreens()
    }

}
