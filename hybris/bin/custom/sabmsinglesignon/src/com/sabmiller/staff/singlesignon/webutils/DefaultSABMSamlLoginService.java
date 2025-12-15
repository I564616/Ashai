/**
 *
 */
package com.sabmiller.staff.singlesignon.webutils;

import de.hybris.platform.core.model.user.UserModel;
import de.hybris.platform.jalo.user.UserManager;
import de.hybris.platform.persistence.security.EJBPasswordEncoderNotFoundException;
import de.hybris.platform.samlsinglesignon.DefaultSamlLoginService;
import de.hybris.platform.servicelayer.exceptions.SystemException;
import de.hybris.platform.util.Utilities;

import jakarta.servlet.http.HttpServletResponse;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;


/**
 * @author GQ485VQ
 *
 */
public class DefaultSABMSamlLoginService extends DefaultSamlLoginService
{
	protected static final Logger LOGGER = LoggerFactory.getLogger(DefaultSABMSamlLoginService.class);

	@Override
	public void storeLoginToken(final HttpServletResponse response, final UserModel user, final String languageIsoCode)
	{

		final String location = response.getHeader("Location");
		LOGGER.info("Location to Redirect:" + location);
		//		if (location.contains("backoffice"))
		//		{
		try
		{
			final String cookieMaxAgeStr = Utilities.getConfig().getString("sso.cookie.max.age", String.valueOf(1));
			//final int cookieMaxAge = NumberUtils.isCreatable(cookieMaxAgeStr) ? 60 : Integer.parseInt(cookieMaxAgeStr);
			final int cookieMaxAge = Integer.parseInt(cookieMaxAgeStr);
			LOGGER.info("Saml backoffice-cookieMaxAge>>" + cookieMaxAge);


			UserManager.getInstance().storeLoginTokenCookie(
					Utilities.getConfig().getString("sso.cookie.name", "samlPassThroughToken"), user.getUid(), languageIsoCode,
					(String) null, Utilities.getConfig().getString("sso.cookie.path", "/"),
					null != response.getHeader("isALBStaffPortalLogin")
							? Utilities.getConfig().getString("sso.cookie.domain.alb", null)
							: Utilities.getConfig().getString("sso.cookie.domain", null),
					true, cookieMaxAge, response);
		}
		catch (final EJBPasswordEncoderNotFoundException var6)
		{
			throw new SystemException(var6);
		}
		//		}
		//		else
		//		{
		//			super.storeLoginToken(response, user, languageIsoCode);
		//		}
	}
}
