package com.apb.storefront.controllers.cms;



import de.hybris.platform.servicelayer.config.ConfigurationService;
import de.hybris.platform.servicelayer.user.UserService;
import de.hybris.platform.core.model.user.UserModel;
import de.hybris.platform.acceleratorstorefrontcommons.controllers.cms.AbstractCMSComponentController;


import jakarta.annotation.Resource;
import jakarta.servlet.http.HttpServletRequest;

import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.RequestMapping;

import com.sabm.core.model.cms.components.LiveChatComponentModel;
import com.sabmiller.core.model.BDECustomerModel;
import com.apb.storefront.controllers.ControllerConstants;

/**
 * Created by himanshu.kumar on 17/04/2025.
 */


/**
 * Controller for LiveChat Component.
 */
@Controller("LiveChatComponentController")
@Scope("tenant")
@RequestMapping(value = ControllerConstants.Actions.Cms.LiveChatComponent)
public class LiveChatComponentController extends AbstractAcceleratorCMSComponentController<LiveChatComponentModel>{

    @Resource(name = "configurationService")
    private ConfigurationService configurationService;

    @Resource(name = "userService")
    private UserService userService;

    @Override
    protected void fillModel(final HttpServletRequest request, final Model model, final LiveChatComponentModel component) {

        final UserModel currentUser = userService.getCurrentUser();
        if (currentUser != null && currentUser instanceof BDECustomerModel) {
            model.addAttribute("isBDECustomer", true);
            BDECustomerModel bdeCustomerModel = (BDECustomerModel) currentUser;
            model.addAttribute("asahiStaffEmail", bdeCustomerModel.getEmail());
        }

        model.addAttribute("livechatAvailableFromHour", component.getFromHour());
        model.addAttribute("livechatAvailableFromMin", component.getFromMinute());
        model.addAttribute("livechatAvailableToHour", component.getToHour());
        model.addAttribute("livechatAvailableToMin", component.getToMinute());

        model.addAttribute("livechatMiawOrgId",configurationService.getConfiguration().getString("livechat.miaw.org.id"));
        model.addAttribute("livechatMiawDeploymentName",configurationService.getConfiguration().getString("livechat.miaw.deployment.name"));
        model.addAttribute("livechatMiawHostURL",configurationService.getConfiguration().getString("livechat.miaw.host.url"));
        model.addAttribute("livechatMiawScrtURL",configurationService.getConfiguration().getString("livechat.miaw.scrt.url"));
        model.addAttribute("livechatMiawJsFileURL",configurationService.getConfiguration().getString("livechat.miaw.js.file.url"));
        model.addAttribute("livechatMiawSiteNameALB",configurationService.getConfiguration().getString("livechat.miaw.site.name.alb"));


    }

}
