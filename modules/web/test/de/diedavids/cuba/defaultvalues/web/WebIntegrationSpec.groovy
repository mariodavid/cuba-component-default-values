package de.diedavids.cuba.defaultvalues.web

import com.haulmont.cuba.core.global.AppBeans
import com.haulmont.cuba.core.global.Metadata
import com.haulmont.cuba.gui.Screens
import com.haulmont.cuba.gui.model.CollectionContainer
import com.haulmont.cuba.gui.screen.OpenMode
import com.haulmont.cuba.gui.screen.Screen
import com.haulmont.cuba.gui.screen.UiControllerUtils
import com.haulmont.cuba.web.app.main.MainScreen
import com.haulmont.cuba.web.testsupport.TestUiEnvironment
import de.diedavids.cuba.defaultvalues.DefaultValuesWebTestContainer
import org.junit.ClassRule
import spock.lang.Shared
import spock.lang.Specification

abstract class WebIntegrationSpec extends Specification {


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


    protected CollectionContainer collectionContainer(Screen screen, String collectionContainerId) {
        UiControllerUtils.getScreenData(screen).getContainer(collectionContainerId)
    }

    protected Screens screens() {
        environment.getScreens()
    }


    protected Screen mainWindow() {
        screens()
                .create(MainScreen.class, OpenMode.ROOT)
                .show()
    }

}
