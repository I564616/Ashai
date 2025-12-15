/**
 *
 */
package com.asahi.staff.storefront.controllers.pages;

import de.hybris.platform.acceleratorstorefrontcommons.annotations.RequireHardLogIn;

import jakarta.annotation.Resource;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.validation.Valid;

import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;

import com.asahi.staff.storefront.form.ImpersonateCustomerForm;
import com.sabmiller.core.model.BDECustomerModel;
import com.sabmiller.facades.customer.SABMCustomerFacade;


/**
 * @author Saumya.Mittal1
 *
 */
@Controller
@Scope("tenant")
@RequestMapping(value = "/viewOnly")
public class ViewOnlyPageController extends AbstractAsahiStaffPageController
{
	private static final Logger LOG = LoggerFactory.getLogger(ViewOnlyPageController.class);

	@Resource(name = "customerFacade")
	private SABMCustomerFacade customerFacade;

	private static final String BDELOGIN = "bdelogin";
	private static final String ALB_BASEURL = "/storefront/sga/en/AUD/";
	private static final String TOKEN = "token";

	@ResponseBody
	@PostMapping
	@RequireHardLogIn
	public String viewAsBdeCustomer(@Valid
	final ImpersonateCustomerForm impersonateCustomerForm, final HttpServletRequest request, final HttpServletResponse response)
	{

		if (StringUtils.isNotBlank(impersonateCustomerForm.getUid()) && StringUtils.isNotBlank(impersonateCustomerForm.getEmail()))
		{
			try
			{
				final BDECustomerModel bdeCustomer = customerFacade.getOrCreateBDECustomer(impersonateCustomerForm.getUid(),
						impersonateCustomerForm.getEmail());
				if (null != bdeCustomer)
				{
					final String newSecureToken = customerFacade.getNewSecureToken(bdeCustomer.getUid());
					if (StringUtils.isNotBlank(newSecureToken))
					{
						final StringBuilder builder = new StringBuilder();
						builder.append(ALB_BASEURL);
						builder.append(BDELOGIN);
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
				LOG.error("Exception in changeImpersonateCustomer method " + e);
			}
		}
		return REDIRECT_PREFIX + ROOT;
	}
}