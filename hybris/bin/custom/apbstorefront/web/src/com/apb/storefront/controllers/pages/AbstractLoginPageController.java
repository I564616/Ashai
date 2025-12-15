
package com.apb.storefront.controllers.pages;

import de.hybris.platform.acceleratorstorefrontcommons.breadcrumb.Breadcrumb;
import de.hybris.platform.acceleratorstorefrontcommons.controllers.ThirdPartyConstants;
import de.hybris.platform.acceleratorstorefrontcommons.controllers.pages.AbstractRegisterPageController;
import de.hybris.platform.acceleratorstorefrontcommons.controllers.util.GlobalMessages;
import de.hybris.platform.acceleratorstorefrontcommons.forms.GuestForm;
import de.hybris.platform.acceleratorstorefrontcommons.forms.RegisterForm;
import de.hybris.platform.cms2.exceptions.CMSItemNotFoundException;
import de.hybris.platform.cms2.model.pages.ContentPageModel;
import de.hybris.platform.servicelayer.config.ConfigurationService;
import de.hybris.platform.util.Config;

import java.util.Collections;

import jakarta.annotation.Resource;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpSession;

import org.springframework.ui.Model;

import com.apb.core.util.AsahiSiteUtil;
import com.apb.storefront.forms.ApbLoginForm;


/**
 * APB Specific base class for login page controllers
 */
public abstract class AbstractLoginPageController extends AbstractRegisterPageController
{
	
	protected static final String SPRING_SECURITY_LAST_USERNAME = "SPRING_SECURITY_LAST_USERNAME";
	
	@Resource
	private AsahiSiteUtil asahiSiteUtil;
	
    @Resource(name = "configurationService")
    private ConfigurationService configurationService;

	protected String getDefaultLoginPage(final boolean loginError, final HttpSession session, final Model model, final HttpServletRequest request)
			throws CMSItemNotFoundException
	{
		final ApbLoginForm loginForm = new ApbLoginForm();
		model.addAttribute("loginForm", loginForm);
		model.addAttribute(new RegisterForm());
		model.addAttribute(new GuestForm());

		final String username = (String) session.getAttribute(SPRING_SECURITY_LAST_USERNAME);
		if (username != null)
		{
			session.removeAttribute(SPRING_SECURITY_LAST_USERNAME);
		}

		loginForm.setJ_username(username);
		storeCmsPageInModel(model, getCmsPage());
		setUpMetaDataForContentPage(model, (ContentPageModel) getCmsPage());
		if(((ContentPageModel) getCmsPage()).getBackgroundImage() != null)
		{
			model.addAttribute("media", ((ContentPageModel) getCmsPage()).getBackgroundImage().getURL());
		}
		
		 if("prod".equalsIgnoreCase(configurationService.getConfiguration().getString("envType", "dev"))){
			model.addAttribute(ThirdPartyConstants.SeoRobots.META_ROBOTS, ThirdPartyConstants.SeoRobots.INDEX_FOLLOW);
		 }
		 else {
			model.addAttribute(ThirdPartyConstants.SeoRobots.META_ROBOTS, ThirdPartyConstants.SeoRobots.NOINDEX_NOFOLLOW);
		}

		final Breadcrumb loginBreadcrumbEntry = new Breadcrumb("#",
				getMessageSource().getMessage("header.link.login.apb", null, "header.link.login.apb", getI18nService().getCurrentLocale()),
				null);
		model.addAttribute("breadcrumbs", Collections.singletonList(loginBreadcrumbEntry));

		if (loginError)
		{
			model.addAttribute("loginError", Boolean.valueOf(loginError));
			
			if(asahiSiteUtil.isSga())
			{
				GlobalMessages.addErrorMessage(model, "sga.login.error.account.not.found.title");
			}else{
				GlobalMessages.addErrorMessage(model, "login.error.account.not.found.title");
			}
		}
		

		return getView();
	}

	
}