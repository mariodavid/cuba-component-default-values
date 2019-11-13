package de.diedavids.cuba.defaultvalues.web.screens.entityattributedefaultvalue.edit.dialogs;


import com.haulmont.cuba.core.global.MessageTools;
import com.haulmont.cuba.gui.Dialogs;
import com.haulmont.cuba.gui.UiComponents;
import com.haulmont.cuba.gui.app.core.inputdialog.InputDialog;
import com.haulmont.cuba.gui.app.core.inputdialog.InputParameter;
import com.haulmont.cuba.gui.components.Field;
import com.haulmont.cuba.gui.components.LookupField;
import com.haulmont.cuba.gui.screen.FrameOwner;
import com.haulmont.cuba.gui.screen.MessageBundle;
import de.diedavids.cuba.defaultvalues.entity.EntityAttributeDefaultValue;
import de.diedavids.cuba.defaultvalues.entity.EntityAttributeDefaultValueType;
import de.diedavids.cuba.defaultvalues.service.SessionAttributeService;

import java.util.function.Consumer;
import java.util.stream.Collectors;

import static com.haulmont.cuba.gui.app.core.inputdialog.InputDialog.INPUT_DIALOG_OK_ACTION;

public class SessionAttributeDialogBuilder implements DefaultValueTypeDialogBuilder {


    private final Dialogs dialogs;
    private final MessageBundle messageBundle;
    private final UiComponents uiComponents;
    private final MessageTools messageTools;
    private final SessionAttributeService sessionAttributeService;

    public SessionAttributeDialogBuilder(
            Dialogs dialogs,
            MessageBundle messageBundle,
            UiComponents uiComponents,
            MessageTools messageTools,
            SessionAttributeService sessionAttributeService
    ) {

        this.dialogs = dialogs;
        this.messageBundle = messageBundle;
        this.uiComponents = uiComponents;
        this.messageTools = messageTools;
        this.sessionAttributeService = sessionAttributeService;
    }

    @Override
    public InputDialog createDialog(EntityAttributeDefaultValue entityAttributeDefaultValue, FrameOwner frameOwner, Runnable afterCancelHandler) {
        return dialogs.createInputDialog(frameOwner)
                .withCaption(
                        messageBundle.getMessage("setSessionValueCaption")
                )
                .withParameter(
                        InputParameter.stringParameter("sessionAttribute")
                                .withField(() -> sessionAttributeLookupField(entityAttributeDefaultValue))
                )
                .withCloseListener(new Consumer<InputDialog.InputDialogCloseEvent>() {
                    @Override
                    public void accept(InputDialog.InputDialogCloseEvent closeEvent) {
                        if (closeEvent.getCloseAction().equals(INPUT_DIALOG_OK_ACTION)) {
                            setSessionAttributeDefaultValue(
                                    entityAttributeDefaultValue,
                                    closeEvent.getValue("sessionAttribute")
                            );
                        } else {
                            afterCancelHandler.run();
                        }
                    }
                }).build();
    }

    private Field sessionAttributeLookupField(EntityAttributeDefaultValue entityAttributeDefaultValue) {
        LookupField lookupField = uiComponents.create(LookupField.class);
        lookupField.setWidthFull();
        lookupField.setRequired(true);
        lookupField.setCaption(
                messageTools.getPropertyCaption(entityAttributeDefaultValue.getEntityAttribute())
        );

        lookupField.setOptionsMap(
                sessionAttributeService
                        .getAvailableSessionAttributes()
                        .stream()
                        .collect(Collectors.toMap(x -> x, x -> ":session$" + x))
        );
        lookupField.setValue(entityAttributeDefaultValue.getValue());

        return lookupField;
    }

    private void setSessionAttributeDefaultValue(EntityAttributeDefaultValue entityAttributeDefaultValue, String sessionAttributeName) {
        entityAttributeDefaultValue.setValue(sessionAttributeName);
        entityAttributeDefaultValue.setType(EntityAttributeDefaultValueType.SESSION_ATTRIBUTE);
    }

}
