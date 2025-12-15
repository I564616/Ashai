/**
 *
 */
package com.sabmiller.staff.storefront.controllers.pages;

import de.hybris.platform.acceleratorstorefrontcommons.annotations.RequireHardLogIn;
import de.hybris.platform.acceleratorstorefrontcommons.breadcrumb.ResourceBreadcrumbBuilder;
import de.hybris.platform.acceleratorstorefrontcommons.constants.WebConstants;
import de.hybris.platform.cms2.exceptions.CMSItemNotFoundException;
import de.hybris.platform.cms2.model.pages.ContentPageModel;
import de.hybris.platform.servicelayer.i18n.I18NService;

import java.io.IOException;

import jakarta.annotation.Resource;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.validation.Valid;

import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.context.MessageSource;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;

import com.sabmiller.core.model.BDECustomerModel;
import com.sabmiller.facades.customer.SABMCustomerFacade;
import com.sabmiller.staff.storefront.form.ImpersonateCustomerForm;


/**
 * @author dale.bryan.a.mercado
 *
 */
@Controller
@Scope("tenant")
@RequestMapping(value = "/viewOnly")
public class ViewOnlyPageController extends AbstractStaffPageController
{
	private static final Logger LOG = LoggerFactory.getLogger(ViewOnlyPageController.class);

	@Resource(name = "customerFacade")
	private SABMCustomerFacade customerFacade;

	@Resource(name = "i18nService")
	private I18NService i18nService;

	@Resource(name = "messageSource")
	private MessageSource messageSource;


	@Resource(name = "simpleBreadcrumbBuilder")
	private ResourceBreadcrumbBuilder resourceBreadcrumbBuilder;

	private static final String BDEVIEW = "bdeview";
	private static final String TOKEN = "token";
	private static final String ERROR_CMS_PAGE = "notFound";

	@ResponseBody
	@PostMapping
	@RequireHardLogIn
	public String viewAsBdeCustomer(final Model model, @Valid
	final ImpersonateCustomerForm impersonateCustomerForm, final HttpServletRequest request, final HttpServletResponse response)
			throws IOException, CMSItemNotFoundException
	{

		if (StringUtils.isNotBlank(impersonateCustomerForm.getUid()) && StringUtils.isNotBlank(impersonateCustomerForm.getEmail()))
		{
			try
			{
				final BDECustomerModel bdeCustomer = customerFacade.getOrCreateBDECustomer(impersonateCustomerForm.getUid(),impersonateCustomerForm.getEmail());
				if (null != bdeCustomer)
				{
					final String newSecureToken = customerFacade.getNewSecureToken(bdeCustomer.getUid());
					if (StringUtils.isNotBlank(newSecureToken))
					{
						final StringBuilder builder = new StringBuilder();
						builder.append("/");
						builder.append(BDEVIEW);
						builder.append("?");
						builder.append(TOKEN + "=");
						builder.append(newSecureToken.toString());
						if (StringUtils.isNotBlank(impersonateCustomerForm.getLandingPage()))
						{
							builder.append("&landingPage=" + impersonateCustomerForm.getLandingPage());
						}
						final String tokenRegex = builder.toString().replaceAll("\\+", "%2B");
						final String encodedRedirectUrl = response.encodeRedirectURL(tokenRegex);
						response.sendRedirect(encodedRedirectUrl);
					}
				}
			}
			catch (final Exception e)
			{
				final ContentPageModel errorPage = getContentPageForLabelOrId(ERROR_CMS_PAGE);
				storeCmsPageInModel(model, errorPage);
				setUpMetaDataForContentPage(model, errorPage);
				model.addAttribute(WebConstants.MODEL_KEY_ADDITIONAL_BREADCRUMB,
						resourceBreadcrumbBuilder.getBreadcrumbs("breadcrumb.not.found"));
				response.setStatus(HttpServletResponse.SC_NOT_FOUND);
				response.sendRedirect(getViewForPage(model) + "?errormessage=true");
				LOG.error("Exception in changeImpersonateCustomer method " + e);
				return REDIRECT_PREFIX + getViewForPage(model);
			}
		}
		return REDIRECT_PREFIX + ROOT;
	}

}
