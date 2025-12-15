package com.sabmiller.storefront.controllers.cms;

import com.sabm.core.model.cms.components.SABMHeroBannerComponentModel;
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

@Controller("SABMHeroBannerComponentController")
@Scope("tenant")
@RequestMapping(value = ControllerConstants.Actions.Cms.SABMHeroBannerComponent)
public class SABMHeroBannerComponentController extends AbstractCMSComponentController<SABMHeroBannerComponentModel> {

    private final String SABMHeroBannerComponent_BACKGROUNDIMAGE = "_backgroundimage";

    @Override
    protected void fillModel(final HttpServletRequest request, final Model model, final SABMHeroBannerComponentModel component) {
        //do nothing
    }

    @Override
    protected String getView(final SABMHeroBannerComponentModel component) {

        if (component != null) {
            if (StringUtils.contains(component.getUid(), SABMHeroBannerComponent_BACKGROUNDIMAGE)) {
                return ControllerConstants.Views.Pages.GenericComponents.SABMHeroBannerComponent_BACKGROUNDIMAGE;
            }
        }
        //this is the commonly used
        return ControllerConstants.Views.Pages.GenericComponents.SABMHeroBannerComponent;
    }


}
