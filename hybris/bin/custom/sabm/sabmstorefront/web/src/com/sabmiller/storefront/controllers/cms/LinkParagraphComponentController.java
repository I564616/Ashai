package com.sabmiller.storefront.controllers.cms;

import com.sabmiller.commons.constants.SabmcommonsConstants;
import com.sabmiller.facades.util.SabmFeatureUtil;
import com.sabmiller.storefront.controllers.ControllerConstants;
import de.hybris.platform.cms2.model.contents.components.AbstractCMSComponentModel;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.RequestMapping;

import jakarta.annotation.Resource;
import jakarta.servlet.http.HttpServletRequest;

/**
 * Created by zhuo.a.jiang on 29/01/2018.
 */

/**
 * Controller for CMS LinkParagraphComponentController.
 */
@Controller("LinkParagraphComponentController")
@Scope("tenant")
@RequestMapping(value = ControllerConstants.Actions.Cms.LinkParagraphComponent)
public class LinkParagraphComponentController extends DefaultCMSComponentController {

    private static final Logger LOG = LoggerFactory.getLogger(LinkParagraphComponentController.class);


    @Resource(name = "sabmFeatureUtil")
    private SabmFeatureUtil sabmFeatureUtil;

    @Override
    protected void fillModel(final HttpServletRequest request, final Model model, final AbstractCMSComponentModel component) {
        super.fillModel(request, model, component);

        boolean isInvoiceDiscrepancyEnabled = sabmFeatureUtil.isFeatureEnabled(SabmcommonsConstants.INVOICEDISCREPANY);

        model.addAttribute("isInvoiceDiscrepancyEnabled", isInvoiceDiscrepancyEnabled);
        LOG.debug("isInvoiceDiscrepancyEnabled for LinkParagraphComponent [{}]", isInvoiceDiscrepancyEnabled);

    }

}
