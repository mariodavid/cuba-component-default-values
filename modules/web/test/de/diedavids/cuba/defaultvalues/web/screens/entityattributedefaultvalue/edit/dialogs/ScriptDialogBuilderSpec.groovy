package de.diedavids.cuba.defaultvalues.web.screens.entityattributedefaultvalue.edit.dialogs

import com.haulmont.cuba.gui.UiComponents
import com.haulmont.cuba.gui.components.SourceCodeEditor
import com.haulmont.cuba.gui.screen.MessageBundle
import com.haulmont.cuba.gui.screen.Screen
import com.haulmont.cuba.security.entity.User
import de.diedavids.cuba.defaultvalues.entity.EntityAttributeDefaultValue
import de.diedavids.cuba.defaultvalues.entity.EntityAttributeDefaultValueType

class ScriptDialogBuilderSpec extends DefaultValueBuilderSpec {


    def setup() {
        sut = new ScriptDialogBuilder(
                Mock(MessageBundle),
                environment.container.getBean(UiComponents),
                environment.getDialogs()
        )
    }


    def "createDialog creates a source code editor with the value of the entity attribute default value"() {

        given:
        EntityAttributeDefaultValue entityAttributeDefaultValue = userDefaultValue('login')
        entityAttributeDefaultValue.value = "return 'username'"

        when:
        Screen inputDialog = showScreen(entityAttributeDefaultValue)

        and:
        SourceCodeEditor loginScriptField = scriptInputComponent(inputDialog)

        then:
        loginScriptField.value == "return 'username'"
    }


    def "createDialog takes the value of the source code editor and binds it into the default value entity"() {

        given:
        EntityAttributeDefaultValue entityAttributeDefaultValue = userDefaultValue('login')
        entityAttributeDefaultValue.value = "return 'foo'"


        and:
        Screen inputDialog = showScreen(entityAttributeDefaultValue)

        and:
        SourceCodeEditor loginScriptField = scriptInputComponent(inputDialog)

        when:
        loginScriptField.value = "return 'bar'"

        and:
        close(inputDialog)

        then:
        entityAttributeDefaultValue.value == "return 'bar'"
        entityAttributeDefaultValue.type == EntityAttributeDefaultValueType.SCRIPT

    }


    def "createDialog creates an input dialog that runs the after cancel handler when the dialog is cancelled"() {

        given:
        def called = false

        and:
        EntityAttributeDefaultValue entityAttributeDefaultValue = userDefaultValue("login")


        when:
        Screen inputDialog = sut.createDialog(
                entityAttributeDefaultValue,
                mainWindow(),
                {},
                new Runnable() {
                    @Override
                    void run() {
                        called = true
                    }
                }
        ).show()

        and:
        cancel(inputDialog)

        then:
        called
    }

    private SourceCodeEditor scriptInputComponent(Screen inputDialog) {
        inputDialog.getWindow().getComponent("script") as SourceCodeEditor
    }


    EntityAttributeDefaultValue userDefaultValue(String entityAttribute) {
        EntityAttributeDefaultValue entityAttributeDefaultValue = new EntityAttributeDefaultValue()
        def userMetaClass = metadata.getClass(User)
        entityAttributeDefaultValue.entity = userMetaClass
        entityAttributeDefaultValue.entityAttribute = userMetaClass.getProperty(entityAttribute)

        return entityAttributeDefaultValue
    }

}
