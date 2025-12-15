package com.sabmiller.storefront.controllers.cms;

import com.sabm.core.model.cms.components.SABMTextImageBannerComponentModel;
import com.sabmiller.storefront.controllers.ControllerConstants;
import org.apache.commons.lang3.StringUtils;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.RequestMapping;

import jakarta.servlet.http.HttpServletRequest;

/**
 * Created by philip.c.a.ferma on 4/13/18.
 */

@Controller("SABMTextImageBannerComponentController")
@Scope("tenant")
@RequestMapping(value = ControllerConstants.Actions.Cms.SABMTextImageBannerComponent)
public class SABMTextImageBannerComponentController extends AbstractCMSComponentController<SABMTextImageBannerComponentModel> {

    private final String THREE_COLUMNS_SABMTextImageBannerComponent = "three_columns_";
    private final String FOUR_COLUMNS_SABMTextImageBannerComponent = "four_columns_";
    private final String FIVE_COLUMNS_SABMTextImageBannerComponent = "five_columns_";

    @Override
    protected void fillModel(final HttpServletRequest request, final Model model, final SABMTextImageBannerComponentModel component) {
        //do nothing
    }

    @Override
    protected String getView(final SABMTextImageBannerComponentModel component) {


        if (component != null) {
            if (StringUtils.contains(component.getUid(), FOUR_COLUMNS_SABMTextImageBannerComponent)) {
                return ControllerConstants.Views.Pages.GenericComponents.FOUR_COLUMNS_SABMTextImageBannerComponent;
            } else if (StringUtils.contains(component.getUid(), FIVE_COLUMNS_SABMTextImageBannerComponent)) {
                return ControllerConstants.Views.Pages.GenericComponents.FIVE_COLUMNS_SABMTextImageBannerComponent;
            }
        }
        //this is the commonly used
        return ControllerConstants.Views.Pages.GenericComponents.THREE_COLUMNS_SABMTextImageBannerComponent;

    }

}

