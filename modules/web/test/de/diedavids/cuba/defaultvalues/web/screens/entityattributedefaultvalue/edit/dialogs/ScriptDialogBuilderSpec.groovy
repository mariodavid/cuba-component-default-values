package de.diedavids.cuba.defaultvalues.web.screens.entityattributedefaultvalue.edit.dialogs


import com.haulmont.cuba.gui.UiComponents
import com.haulmont.cuba.gui.app.core.inputdialog.InputDialog
import com.haulmont.cuba.gui.screen.MessageBundle
import com.haulmont.cuba.security.entity.User
import de.diedavids.cuba.defaultvalues.entity.EntityAttributeDefaultValue
import de.diedavids.cuba.defaultvalues.web.WebIntegrationSpec

class ScriptDialogBuilderSpec extends WebIntegrationSpec {

    ScriptDialogBuilder sut


    def setup() {
        sut = new ScriptDialogBuilder(
                Mock(MessageBundle),
                environment.container.getBean(UiComponents),
                environment.getDialogs()
        )
    }


    def "createDialog creates one parameter of the inputDialog for the default value"() {

        given:
        EntityAttributeDefaultValue entityAttributeDefaultValue = userDefaultValue('login')
        entityAttributeDefaultValue.value = "return 'username'"

        when:
        InputDialog inputDialog = sut.createDialog(
                entityAttributeDefaultValue,
                mainWindow(),
                {} as Runnable
        )

        and:
        def scriptField = inputDialog.parameters.get(0).getField().get()

        then:
        inputDialog.parameters.size() == 1

        and:
        scriptField.getValue() == "return 'username'"

    }

    def "createDialog defines one parameter with the key 'script'"() {

        given:
        EntityAttributeDefaultValue entityAttributeDefaultValue = userDefaultValue('login')

        when:
        InputDialog inputDialog = sut.createDialog(
                entityAttributeDefaultValue,
                mainWindow(),
                {} as Runnable
        )

        inputDialog.show()

        then:
        inputDialog.parameters[0].id == 'script'
    }

    def "createDialog creates an input dialog that runs the after cancel handler when the dialog is cancelled"() {

        given:
        def called = false

        and:
        EntityAttributeDefaultValue entityAttributeDefaultValue = userDefaultValue("login")


        when:
        InputDialog inputDialog = sut.createDialog(
                entityAttributeDefaultValue,
                mainWindow(),
                new Runnable() {
                    @Override
                    void run() {
                        called = true
                    }
                }
        )

        inputDialog.show()

        and:
        inputDialog.close(InputDialog.INPUT_DIALOG_CANCEL_ACTION)

        then:
        called
    }

    EntityAttributeDefaultValue userDefaultValue(String entityAttribute) {
        EntityAttributeDefaultValue entityAttributeDefaultValue = new EntityAttributeDefaultValue()
        def userMetaClass = metadata.getClass(User)
        entityAttributeDefaultValue.entity = userMetaClass
        entityAttributeDefaultValue.entityAttribute = userMetaClass.getProperty(entityAttribute)

        return entityAttributeDefaultValue;
    }

}
