package de.diedavids.cuba.defaultvalues.web.screens.entityattributedefaultvalue.edit.dialogs


import com.haulmont.cuba.gui.app.core.inputdialog.InputDialog
import com.haulmont.cuba.gui.components.Component
import com.haulmont.cuba.gui.components.Form
import com.haulmont.cuba.gui.screen.Screen
import com.haulmont.cuba.gui.util.OperationResult
import com.haulmont.cuba.security.entity.User
import de.diedavids.cuba.defaultvalues.entity.EntityAttributeDefaultValue
import de.diedavids.cuba.defaultvalues.web.WebIntegrationSpec

abstract class DefaultValueBuilderSpec extends WebIntegrationSpec {

    DefaultValueTypeDialogBuilder sut



    protected Screen showScreen(EntityAttributeDefaultValue entityAttributeDefaultValue) {
        sut.createDialog(
                entityAttributeDefaultValue,
                mainWindow(),
                {},
                {}
        ).show()
    }
    protected InputDialog showDialog(EntityAttributeDefaultValue entityAttributeDefaultValue) {
        def inputDialog = sut.createDialog(
                entityAttributeDefaultValue,
                mainWindow(),
                {},
                {}
        )
        inputDialog.show()

        inputDialog
    }

    protected OperationResult cancel(Screen inputDialog) {
        inputDialog.close(InputDialog.INPUT_DIALOG_CANCEL_ACTION)
    }


    protected OperationResult close(Screen inputDialog) {
        inputDialog.close(InputDialog.INPUT_DIALOG_OK_ACTION)
    }

    EntityAttributeDefaultValue userDefaultValue(String entityAttribute) {
        EntityAttributeDefaultValue entityAttributeDefaultValue = new EntityAttributeDefaultValue()
        def userMetaClass = metadata.getClass(User)
        entityAttributeDefaultValue.entity = userMetaClass
        entityAttributeDefaultValue.entityAttribute = userMetaClass.getProperty(entityAttribute)

        return entityAttributeDefaultValue
    }

    protected  <T extends Component> T inputComponent(Screen inputDialog, String inputComponentId, Class<T> targetClass) {
        Form form = inputDialog.getWindow().getComponent("form") as Form
        (T) form.getComponent(inputComponentId)
    }

}
