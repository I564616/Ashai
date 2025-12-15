package com.sabmiller.storefront.controllers.cms;

import com.sabm.core.model.cms.components.SABMInformationBoxComponentModel;
import com.sabmiller.storefront.controllers.ControllerConstants;
import org.apache.commons.lang3.StringUtils;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.RequestMapping;

import jakarta.servlet.http.HttpServletRequest;

/**
 * Created by philip.c.a.ferma on 4/12/18.
 */

@Controller("SABMInformationBoxComponentController")
@Scope("tenant")
@RequestMapping(value = ControllerConstants.Actions.Cms.SABMInformationBoxComponent)
public class SABMInformationBoxComponentController extends AbstractCMSComponentController<SABMInformationBoxComponentModel> {

    private final String SABMInformationBoxComponent_TAB = "_tab";
    private final String SABMInformationBoxComponent_SMALLBOX = "_smallbox";

    @Override
    protected void fillModel(final HttpServletRequest request, final Model model, final SABMInformationBoxComponentModel component) {
        //do nothing
    }

    @Override
    protected String getView(final SABMInformationBoxComponentModel component) {
        if (component != null) {
            if (StringUtils.contains(component.getUid(), SABMInformationBoxComponent_TAB)) {
                return ControllerConstants.Views.Pages.GenericComponents.SABMInformationBoxComponent_TAB;
            } else if (StringUtils.contains(component.getUid(), SABMInformationBoxComponent_SMALLBOX)) {
                return ControllerConstants.Views.Pages.GenericComponents.SABMInformationBoxComponent_SMALLBOX;
            }
        }
        //this is the commonly used
        return ControllerConstants.Views.Pages.GenericComponents.SABMInformationBoxComponent;
    }


}
