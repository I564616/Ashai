package com.sabmiller.storefront.controllers.pages;

import de.hybris.platform.acceleratorstorefrontcommons.controllers.pages.AbstractPageController;
import de.hybris.platform.cms2.exceptions.CMSItemNotFoundException;

import jakarta.annotation.Resource;

import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.RequestMapping;

import com.sabmiller.commons.constants.SabmcommonsConstants;
import com.sabmiller.facades.util.SabmFeatureUtil;
import com.sabmiller.storefront.controllers.pages.SabmAbstractPageController;


/**
 * Created by philip.c.a.ferma on 4/11/18.
 */

@Controller
@Scope("tenant")
@RequestMapping(value = "/generic/components")
public class GenericComponentsPageController extends SabmAbstractPageController {

    private static final String GENERIC_HOMEPAGE_CMS_PAGE = "GenericComponentsHomePage";
    private static final String GENERIC_SIGNUP_SUCCESS_CMS_PAGE = "GenericComponentsConfirmationPage";
    private static final String REDIRECT_TO_HOME_PAGE = REDIRECT_PREFIX + ROOT;
    
    @Resource(name = "sabmFeatureUtil")
    private SabmFeatureUtil sabmFeatureUtil;
    
    @GetMapping("/landing")
    public String homepage(final Model model) throws CMSItemNotFoundException {
   	 if (this.isAutoPayEnabled()) {
   		 storeCmsPageInModel(model, getContentPageForLabelOrId(GENERIC_HOMEPAGE_CMS_PAGE));
          setUpMetaDataForContentPage(model, getContentPageForLabelOrId(GENERIC_HOMEPAGE_CMS_PAGE));
          return getViewForPage(model);
   	 }
       return REDIRECT_TO_HOME_PAGE;
    }

    @GetMapping("/confirmation")
    public String signupSuccess(final Model model) throws CMSItemNotFoundException {
        storeCmsPageInModel(model, getContentPageForLabelOrId(GENERIC_SIGNUP_SUCCESS_CMS_PAGE));
        setUpMetaDataForContentPage(model, getContentPageForLabelOrId(GENERIC_SIGNUP_SUCCESS_CMS_PAGE));
        return getViewForPage(model);
    }

    /**
		This is the flag which determines whether AutoPay Advantage feature should be displayed or not.
     */
    @ModelAttribute("isAutoPayEnabled")
    public boolean isAutoPayEnabled() {
   	 return sabmFeatureUtil.isFeatureEnabled(SabmcommonsConstants.AUTOPAY);
    }
}
