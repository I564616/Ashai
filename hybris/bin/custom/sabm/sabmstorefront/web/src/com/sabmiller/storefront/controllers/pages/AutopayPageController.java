package com.sabmiller.storefront.controllers.pages;

import de.hybris.platform.acceleratorstorefrontcommons.annotations.RequireHardLogIn;
import de.hybris.platform.acceleratorstorefrontcommons.breadcrumb.Breadcrumb;
import de.hybris.platform.acceleratorstorefrontcommons.breadcrumb.ResourceBreadcrumbBuilder;
import de.hybris.platform.acceleratorstorefrontcommons.controllers.pages.AbstractPageController;
import de.hybris.platform.cms2.exceptions.CMSItemNotFoundException;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.List;

import jakarta.annotation.Resource;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.RequestMapping;

import com.sabmiller.commons.constants.SabmcommonsConstants;
import com.sabmiller.core.constants.SabmCoreConstants;
import com.sabmiller.facades.util.SabmFeatureUtil;
import com.sabmiller.storefront.controllers.pages.SabmAbstractPageController;
import com.apb.core.util.AsahiCoreUtil;

/**
 * Created by philip.c.a.ferma on 5/29/18.
 */

@Controller
@Scope("tenant")
@RequestMapping(value = "/autopay")
public class AutopayPageController extends SabmAbstractPageController {

	private static final Logger LOG = LoggerFactory.getLogger(AutopayPageController.class);
	
    private static final String AUTOPAY_LANDING_PAGE = "AutopayLandingPage";
    private static final String REDIRECT_TO_HOME_PAGE = REDIRECT_PREFIX + ROOT;
    
    @Resource(name = "simpleBreadcrumbBuilder")
 	 private ResourceBreadcrumbBuilder accountBreadcrumbBuilder;
    
    @Resource(name = "sabmFeatureUtil")
    private SabmFeatureUtil sabmFeatureUtil;

    @Resource
    private AsahiCoreUtil asahiCoreUtil;

    @GetMapping("/landing")
    @RequireHardLogIn
    public String landing(final Model model) throws CMSItemNotFoundException {
   	 
   	 if (this.isAutoPayEnabled()) {
   		 List<Breadcrumb> breadcrumbs = accountBreadcrumbBuilder.getBreadcrumbs("text.autopay.header");
   		 model.addAttribute("breadcrumbs", breadcrumbs);
   			
   		 storeCmsPageInModel(model, getContentPageForLabelOrId(AUTOPAY_LANDING_PAGE));
   		 setUpMetaDataForContentPage(model, getContentPageForLabelOrId(AUTOPAY_LANDING_PAGE));
          return getViewForPage(model);
   	 }
   	 return REDIRECT_TO_HOME_PAGE;
   	 
    }

    @GetMapping("/download-form")
    @RequireHardLogIn
    public void downloadForm(HttpServletRequest request, HttpServletResponse response) throws IOException {
   	 
       final String downloadDirectory = request.getServletContext().getRealPath(SabmCoreConstants.DOWNLOAD_PATH);
       final Path file = Path.of(downloadDirectory, SabmCoreConstants.AUTOPAY_SIGNUP_FORM_FILENAME);
       
       if (Files.exists(file)) {
           response.setContentType("application/pdf");
           response.addHeader("Content-Disposition", "attachment; filename=" + file.getFileName().toString());
           try {
         	  Files.copy(file, response.getOutputStream());
              response.getOutputStream().flush();
           }
           catch (IOException e) {
         	  LOG.error("IOException occurred while downloading the AutoPay form.", e);
           }
       }
       
    }
    
    /**
		This is the flag which determines whether AutoPay Advantage feature should be displayed or not.
     */
    @ModelAttribute("isAutoPayEnabled")
    public boolean isAutoPayEnabled() {
   	 return sabmFeatureUtil.isFeatureEnabled(SabmcommonsConstants.AUTOPAY) && !asahiCoreUtil.isNAPUser();
    }
}
