package com.sabmiller.storefront.controllers.cms;

import com.sabm.core.model.cms.components.FAQComponentModel;
import com.sabmiller.facades.generic.components.SABMGenericComponentsFacade;
import com.sabmiller.storefront.controllers.ControllerConstants;

import org.apache.log4j.Logger;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.RequestMapping;

import jakarta.annotation.Resource;
import jakarta.servlet.http.HttpServletRequest;
/**
 * Created by philip.c.a.ferma on 5/23/18.
 */

@Controller("FAQComponentController")
@Scope("tenant")
@RequestMapping(value = ControllerConstants.Actions.Cms.FAQComponent)
public class FAQComponentController extends AbstractCMSComponentController<FAQComponentModel> {

    private static final Logger LOG = Logger.getLogger(FAQComponentController.class);

    @Resource(name = "genericComponentsFacade")
    private SABMGenericComponentsFacade sabmGenericComponentsFacade;

    @Override
    protected void fillModel(final HttpServletRequest request, final Model model, final FAQComponentModel component) {
        try {
            // convert object to json string
            model.addAttribute("faqsComponent", sabmGenericComponentsFacade.getFaqsComponentJsonFormat(component));
        } catch (Exception e) {
            LOG.error("Exception ", e);
        }

    }


}
