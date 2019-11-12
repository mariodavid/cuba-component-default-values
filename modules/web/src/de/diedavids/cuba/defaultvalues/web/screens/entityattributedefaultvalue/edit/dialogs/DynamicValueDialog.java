package de.diedavids.cuba.defaultvalues.web.screens.entityattributedefaultvalue.edit.dialogs;


import com.haulmont.cuba.core.global.MessageTools;
import com.haulmont.cuba.core.global.Messages;
import com.haulmont.cuba.gui.Dialogs;
import com.haulmont.cuba.gui.UiComponents;
import com.haulmont.cuba.gui.app.core.inputdialog.InputDialog;
import com.haulmont.cuba.gui.app.core.inputdialog.InputParameter;
import com.haulmont.cuba.gui.components.HasOrientation;
import com.haulmont.cuba.gui.components.RadioButtonGroup;
import com.haulmont.cuba.gui.screen.FrameOwner;
import com.haulmont.cuba.gui.screen.MessageBundle;
import de.diedavids.cuba.defaultvalues.dynamicvalue.DynamicValueProviders;
import de.diedavids.cuba.defaultvalues.entity.EntityAttributeDefaultValue;
import de.diedavids.cuba.defaultvalues.service.SessionAttributeService;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.function.Consumer;

import static com.haulmont.cuba.gui.app.core.inputdialog.InputDialog.INPUT_DIALOG_OK_ACTION;

public class DynamicValueDialog implements DefaultValueTypeDialog {


    private final Dialogs dialogs;
    private final MessageBundle messageBundle;
    private final UiComponents uiComponents;
    private final Messages messages;
    private final DynamicValueProviders dynamicValueProviders;

    public DynamicValueDialog(
            Dialogs dialogs,
            MessageBundle messageBundle,
            UiComponents uiComponents,
            Messages messages,
            DynamicValueProviders dynamicValueProviders
    ) {

        this.dialogs = dialogs;
        this.messageBundle = messageBundle;
        this.uiComponents = uiComponents;
        this.messages = messages;
        this.dynamicValueProviders = dynamicValueProviders;
    }

    @Override
    public void openDialog(
            EntityAttributeDefaultValue entityAttributeDefaultValue,
            FrameOwner frameOwner,
            Runnable afterCancelHandler
    ) {

        dialogs.createInputDialog(frameOwner)
                .withCaption(messageBundle.getMessage("dynamicDefaultValueCaption"))
                .withParameter(
                        InputParameter.parameter(
                                "dynamicValueProvider"
                        )
                                .withField(() -> {
                                    RadioButtonGroup radioButtonGroup = uiComponents.create(RadioButtonGroup.class);
                                    radioButtonGroup.setWidthFull();
                                    radioButtonGroup.setRequired(true);
                                    radioButtonGroup.setOrientation(HasOrientation.Orientation.VERTICAL);
                                    radioButtonGroup.setOptionsMap(dynamicDefaultValueOptions(entityAttributeDefaultValue));
                                    radioButtonGroup.setValue(entityAttributeDefaultValue.getValue());
                                    return radioButtonGroup;
                                })
                )
                .withCloseListener(new Consumer<InputDialog.InputDialogCloseEvent>() {
                    @Override
                    public void accept(InputDialog.InputDialogCloseEvent closeEvent) {
                        if (closeEvent.getCloseAction().equals(INPUT_DIALOG_OK_ACTION)) {
                            entityAttributeDefaultValue.setValue(
                                    closeEvent.getValue("dynamicValueProvider")
                            );
                        }
                    }
                })
                .show();
    }

    private Map<String, String> dynamicDefaultValueOptions(EntityAttributeDefaultValue entityAttributeDefaultValue) {
        List<String> availableDynamicValueProvider = dynamicValueProviders.getProvidersFor(entityAttributeDefaultValue.getEntityAttribute());

        Map<String, String> options = new HashMap<>();
        availableDynamicValueProvider
                .forEach(dynamicValueProviderName ->
                        options.put(messages.getMainMessage("dynamicValueProvider." + dynamicValueProviderName), dynamicValueProviderName)
                );
        return options;
    }
}
