package de.diedavids.cuba.defaultvalues.web.screens.entityattributedefaultvalue.edit.dialogs


import com.haulmont.chile.core.datatypes.impl.StringDatatype
import com.haulmont.cuba.core.global.AppBeans
import com.haulmont.cuba.gui.app.core.inputdialog.InputDialog
import com.haulmont.cuba.gui.components.TextField
import com.haulmont.cuba.gui.screen.MessageBundle
import com.haulmont.cuba.gui.screen.Screen
import com.haulmont.cuba.security.entity.User
import de.diedavids.cuba.defaultvalues.entity.EntityAttributeDefaultValue
import de.diedavids.cuba.metadataextensions.EntityDialogs

class StaticValueBuilderSpec extends DefaultValueBuilderSpec {


    def setup() {

        EntityDialogs entityDialogs = AppBeans.get(EntityDialogs)

        sut = new StaticValueDialogBuilder(
                metadata,
                Mock(MessageBundle),
                entityDialogs
        )
    }


    def "createDialog creates one parameter of the inputDialog with the correct datatype"() {

        given:
        EntityAttributeDefaultValue entityAttributeDefaultValue = userDefaultValue('login')

        when:
        Screen inputDialog = showScreen(entityAttributeDefaultValue)

        and:
        TextField loginField = inputComponent(inputDialog, "login", TextField)

        then:
        loginField.datatype instanceof StringDatatype

    }

    def "createDialog creates one parameter of the inputDialog with the correct value binding"() {

        given:
        EntityAttributeDefaultValue entityAttributeDefaultValue = userDefaultValue('login')

        entityAttributeDefaultValue.value = "foo"

        and:
        Screen inputDialog = showScreen(entityAttributeDefaultValue)


        TextField loginField = inputComponent(inputDialog, "login", TextField)

        when:
        loginField.value = "bar"

        and:
        close(inputDialog)

        then:
        entityAttributeDefaultValue.value == "bar"

    }

    def "createDialog defines one parameter with the key name of the meta property"() {

        given:
        EntityAttributeDefaultValue entityAttributeDefaultValue = userDefaultValue('login')

        when:
        InputDialog inputDialog = showDialog(entityAttributeDefaultValue)

        then:
        inputDialog.parameters[0].id == 'login'
    }

    def "createDialog creates an input dialog that runs the after cancel handler when the dialog is cancelled"() {

        given:
        def inputDialogWasCancelled = false

        and:
        EntityAttributeDefaultValue entityAttributeDefaultValue = userDefaultValue("login")


        when:
        InputDialog inputDialog = sut.createDialog(
                entityAttributeDefaultValue,
                mainWindow(),
                {},
                new Runnable() {
                    @Override
                    void run() {
                        inputDialogWasCancelled = true
                    }
                }
        )

        inputDialog.show()

        and:
        cancel(inputDialog)

        then:
        inputDialogWasCancelled
    }


    EntityAttributeDefaultValue userDefaultValue(String entityAttribute) {
        EntityAttributeDefaultValue entityAttributeDefaultValue = new EntityAttributeDefaultValue()
        def userMetaClass = metadata.getClass(User)
        entityAttributeDefaultValue.entity = userMetaClass
        entityAttributeDefaultValue.entityAttribute = userMetaClass.getProperty(entityAttribute)

        return entityAttributeDefaultValue
    }

}
