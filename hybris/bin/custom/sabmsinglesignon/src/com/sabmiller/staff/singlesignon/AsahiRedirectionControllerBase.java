/**
 *
 */
package com.sabmiller.staff.singlesignon;

import de.hybris.platform.samlsinglesignon.RedirectionControllerBase;
import de.hybris.platform.util.Utilities;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.sabmiller.staff.singlesignon.constants.SabmsinglesignonConstants;


/**
 *
 */
public class AsahiRedirectionControllerBase extends RedirectionControllerBase
{

	private static final Logger LOG = LoggerFactory.getLogger(AsahiRedirectionControllerBase.class);

	@Override
	public String redirect(final HttpServletResponse response, final HttpServletRequest request)
	{
		return "redirect:" + getRedirectUrl(response, request);
	}

	@Override
	public String getRedirectUrl(final HttpServletResponse response, final HttpServletRequest request)
	{

		LOG.info("RelayState value :" + response.getHeader("isALBStaffPortalLogin"));

		final String referenceURL = StringUtils.substringAfter(request.getServletPath(), "/saml/");
		LOG.info("####Reference URL" + referenceURL);

		try
		{
			final String redirectURL = referenceURL.contains(SabmsinglesignonConstants.ASAHI_STAFF_PORTAL)
					? Utilities.getConfig().getString("sso.redirect.url.alb", "https://localhost:9002/")
					: Utilities.getConfig().getString("sso.redirect.url", "https://localhost:9002/");

			return redirectURL + referenceURL;


		}
		catch (final IllegalStateException var5)
		{

			if (LOG.isDebugEnabled())
			{

				LOG.debug(var5.getMessage(), var5);

			}

			return "/error";
		}
	}


}
