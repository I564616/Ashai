package com.sabmiller.widgets.baseeditorarea;

import com.sabmiller.model.EnablePortalUserModel;
import com.sabmiller.services.EnableUserService;
import com.hybris.backoffice.widgets.notificationarea.event.NotificationEvent.Level;
import com.hybris.cockpitng.annotations.SocketEvent;
import com.hybris.cockpitng.annotations.ViewEvent;
import com.hybris.cockpitng.common.model.ObjectWithComponentContext;
import com.hybris.cockpitng.components.Widgetslot;
import com.hybris.cockpitng.dataaccess.facades.object.exceptions.ObjectNotFoundException;
import com.hybris.cockpitng.dataaccess.facades.type.DataType;
import com.hybris.cockpitng.dataaccess.facades.type.TypeFacade;
import com.hybris.cockpitng.dataaccess.facades.type.exceptions.TypeNotFoundException;
import com.hybris.cockpitng.util.UITools;
import com.hybris.cockpitng.widgets.baseeditorarea.DefaultEditorAreaController;
import com.hybris.cockpitng.widgets.editorarea.renderer.EditorAreaRendererUtils;
import org.apache.commons.lang3.BooleanUtils;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.zkoss.zk.ui.Component;
import org.zkoss.zk.ui.event.Event;
import org.zkoss.zk.ui.event.Events;
import org.zkoss.zk.ui.select.annotation.Wire;
import org.zkoss.zk.ui.select.annotation.WireVariable;
import org.zkoss.zul.Button;
import org.zkoss.zul.Label;
import org.zkoss.zul.Messagebox;

import jakarta.annotation.Resource;
import java.util.Collections;
import java.util.Map;

public class ApbDefaultEditorAreaController extends DefaultEditorAreaController {

    private static final Logger LOG = LoggerFactory.getLogger(ApbDefaultEditorAreaController.class);

    @WireVariable
    private EnableUserService enableUserService;

    @Wire
    private Button enableButton;

    @Resource
    private TypeFacade typeFacade;

    @Override
    public void preInitialize(Component comp) {
        super.preInitialize(comp);
    }

    @SocketEvent(
            socketId = "inputObject"
    )
    public void setObject(Object inputData) {
        super.setObject(inputData);
        if (null != inputData)
            this.triggerEnableUserView(typeFacade.getType(inputData));
    }

    @SocketEvent(
            socketId = "enableUserType"
    )
    public void triggerEnableUserView(String type) {
        if (type.equalsIgnoreCase("EnablePortalUser")) {
            resetButtonsVisibility(false);
            this.getModel().addObserver("valueChanged", () -> {
                enableButton.setDisabled(BooleanUtils.isNotTrue((Boolean) this.getValue("valueChanged", Boolean.class)));
            });
        } else
            resetButtonsVisibility(true);
    }

    @ViewEvent(componentID = "enableButton", eventName = Events.ON_CLICK)
    public void enableUser() {
        final Object object = this.getCurrentObject();
        if (object instanceof EnablePortalUserModel) {
            Messagebox.show(getLabel("apb.enable.user.proceed"), "Enable User", new Messagebox.Button[]{Messagebox.Button.CANCEL, Messagebox.Button.YES}, null, "z-messagebox-icon z-messagebox-question", null, (clickEvent) -> {
                if (Messagebox.Button.YES.equals(clickEvent.getButton())) {
                    enableUserService.perform((EnablePortalUserModel) object);
                    super.cancelObjectModification();
                }
            }, (Map) null);
        } else {
            Messagebox.show(getLabel("apb.enable.user.not.possible.info"), "Enable User Not Possible", 0, Messagebox.ERROR);
        }
    }

    private void resetButtonsVisibility(boolean b) {
        getSaveButton().setVisible(b);
        getCancelButton().setVisible(b);
        enableButton.setVisible(!b);
    }
}
