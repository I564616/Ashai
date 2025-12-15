/**
 *
 */
package com.sabmiller.staff.singlesignon;

import de.hybris.platform.core.Registry;
import de.hybris.platform.samlsinglesignon.SAMLService;
import de.hybris.platform.samlsinglesignon.SSOUserService;
import de.hybris.platform.samlsinglesignon.SamlLoginService;
import de.hybris.platform.servicelayer.config.ConfigurationService;
import de.hybris.platform.servicelayer.i18n.CommonI18NService;

import java.io.IOException;
import java.util.Base64;
import java.util.Collection;
import java.util.List;

import jakarta.annotation.Resource;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

import org.apache.commons.collections4.CollectionUtils;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.security.core.Authentication;
//import org.springframework.security.saml.SAMLRelayStateSuccessHandler;
import org.springframework.security.saml2.provider.service.authentication.DefaultSaml2AuthenticatedPrincipal;
import org.springframework.security.saml2.provider.service.authentication.Saml2Authentication;
import org.springframework.security.saml2.provider.service.web.HttpSessionSaml2AuthenticationRequestRepository;
import org.springframework.security.web.authentication.SavedRequestAwareAuthenticationSuccessHandler;

import com.sabmiller.staff.singlesignon.constants.SabmsinglesignonConstants;
import org.springframework.security.saml2.provider.service.authentication.DefaultSaml2AuthenticatedPrincipal;
import org.springframework.security.saml2.provider.service.authentication.Saml2AuthenticatedPrincipal;



/**
 * @author Saumya.Mittal1
 *
 */
public class AsahiSAMLRelayStateSuccessHandler extends SavedRequestAwareAuthenticationSuccessHandler
{
	/**
	 * Class logger.
	 */
	protected static final Logger LOGGER = LoggerFactory.getLogger(AsahiSAMLRelayStateSuccessHandler.class);

	private static final String SSO_INFO_VALUE_FMT = "{\"user_id\" : \"%s\",\"user_name\" : \"%s\",\"roles\" : \"%s\",\"language\" : \"%s\"}";

	private static final String CUB_SSO_ERROR_PAGE = "/ssoError";

	private static final String DEFAULT_SAML2_AUTHN_REQUEST_ATTR_NAME = HttpSessionSaml2AuthenticationRequestRepository.class
			.getName().concat(".SAML2_AUTHN_REQUEST");

	@Resource
	private SAMLService samlService;

	private ConfigurationService configurationService;

	@Resource
	private SamlLoginService samlLoginService;

	@Resource
	private SSOUserService ssoUserService;

	@Resource
	private CommonI18NService commonI18NService;


	/**
	 * Implementation tries to load RelayString from the SAMLCredential authentication object and in case the state is
	 * present uses it as the target URL. In case the state is missing behaviour is the same as of the
	 * SavedRequestAwareAuthenticationSuccessHandler.
	 */
	@Override
	public void onAuthenticationSuccess(final HttpServletRequest request, final HttpServletResponse response,
			final Authentication authentication) throws ServletException, IOException
	{

		if (!Registry.hasCurrentTenant())
		{
			Registry.activateMasterTenant();
		}

		if (authentication instanceof Saml2Authentication
				&& authentication.getPrincipal() instanceof DefaultSaml2AuthenticatedPrincipal)
		{
			LOGGER.info("Inside AsahiSAMLRelayStateSuccessHandler##: ");
			final DefaultSaml2AuthenticatedPrincipal principal = (DefaultSaml2AuthenticatedPrincipal) authentication.getPrincipal();
			//final SAMLCredential samlCredential = (SAMLCredential) credentialsObject;
			final List<String> roles = this.samlService.getCustomAttributes(principal,
					getConfigurationService().getConfiguration().getString("sso.usergroup.attribute.key", "usergroup"));
			final String relayState = request.getParameter("RelayState");

			if (StringUtils.isNotBlank(relayState))
			{
				final StringBuilder builder = new StringBuilder();
				if (relayState.contains(SabmsinglesignonConstants.ASAHI_STAFF_PORTAL))
				{
					LOGGER.info("Redirecting to ALB Staff Portal..... ");
					final String redirectURL = getConfigurationService().getConfiguration()
							.getString("sso.alb.storefront.redirect.url");
					builder.append(redirectURL);
					builder.append("?");
					builder.append("token" + "=");
					final String encryptedValue = encryptInfo(principal);
					builder.append(encryptedValue);
					getRedirectStrategy().sendRedirect(request, response, builder.toString());
					return;
				}
			}


			else if (null != request.getRequestURL() && !request.getRequestURL().toString().contains("admin"))
			{
				if (CollectionUtils.isEmpty(roles)
						|| !hasRequiredUserAccessRole(roles, getConfigurationService().getConfiguration().getString("sso.cub.ad.role")))
				{
					getRedirectStrategy().sendRedirect(request, response,
							getConfigurationService().getConfiguration().getString("sso.cub.storefront.error.redirect.url"));
					return;
				}

				//Storing LoginToken for CUB Staff Portal as AsahiSaml2UserFilter is not being invoked
				LOGGER.info("Redirecting to CUB Staff Portal.....");
				redirectToRelyingParty(request, response, principal, roles,
						getConfigurationService().getConfiguration().getString("sso.cub.storefront.redirect.url"));

			}


			if (null != request.getRequestURL() && request.getRequestURL().toString().contains("admin"))
			{
				String redirectUrl = getConfigurationService().getConfiguration().getString("sso.admin.redirect.url");
				if (StringUtils.isNotBlank(relayState) && relayState.contains(SabmsinglesignonConstants.CMS_COCKPIT))
				{
					redirectUrl = getConfigurationService().getConfiguration().getString("sso.cmscockpit.redirect.url");
				}
				if (CollectionUtils.isEmpty(roles) || !hasRequiredUserAccessRole(roles,
						getConfigurationService().getConfiguration().getString("sso.admin.ad.role")))
				{
					LOGGER.error("User does not have sufficient privilege to access Backoffice");
					getRedirectStrategy().sendRedirect(request, response,redirectUrl);
					return;
				}

				//Storing Login Token BackOffice as AsahiSaml2UserFilter is not being invoked
				LOGGER.info("Redirecting to Backoffice.....");
				redirectToRelyingParty(request, response, principal, roles, redirectUrl);

				super.onAuthenticationSuccess(request, response, authentication);
			}
		}

	}


	/**
	 * Redirect to relying party.
	 *
	 * @param request
	 *           the request
	 * @param response
	 *           the response
	 * @param principal
	 *           the principal
	 * @param roles
	 *           the roles
	 * @param redirectURL
	 *           the redirect URL
	 * @throws IOException
	 *            Signals that an I/O exception has occurred.
	 */
	private void redirectToRelyingParty(final HttpServletRequest request, final HttpServletResponse response,
			final DefaultSaml2AuthenticatedPrincipal principal, final List<String> roles, final String redirectURL)
			throws IOException
	{
		try
		{
			this.samlLoginService.storeLoginToken(response,
					this.ssoUserService.getOrCreateSSOUser(this.samlService.getUserId(principal),
							this.samlService.getUserName(principal), roles),
					this.samlService.getLanguage(principal, request, this.commonI18NService));

		}
		catch (final Exception var8)
		{

			LOGGER.error("Can't map user properly", var8);
			throw new AccessDeniedException("Can't map user properly", var8);
		}

		getRedirectStrategy().sendRedirect(request, response, redirectURL);
		return;
	}

	/**
	 * @param authenticatedPrincipal
	 * @return
	 */
	private String encryptInfo(final Saml2AuthenticatedPrincipal authenticatedPrincipal)
	{
		final List<String> roles = this.samlService.getCustomAttributes(authenticatedPrincipal,
				getConfigurationService().getConfiguration().getString("sso.usergroup.attribute.key", "usergroup"));
		final String ssoInfo = String.format(SSO_INFO_VALUE_FMT, this.samlService.getUserId(authenticatedPrincipal),
				this.samlService.getUserName(authenticatedPrincipal),
				CollectionUtils.isNotEmpty(roles) ? String.join(",", roles) : StringUtils.EMPTY, "en");
		final Base64.Encoder encoder = Base64.getEncoder();
		return encoder.encodeToString(ssoInfo.getBytes());
	}

	/**
	 * @param roles
	 * @param application
	 * @return
	 */
	private boolean hasRequiredUserAccessRole(final Collection<String> roles, final String application)
	{
		final String adRole = roles.stream().filter(role -> role.equalsIgnoreCase(application)).findAny().orElse(null);
		if (StringUtils.isNotBlank(adRole))
		{
			return true;
		}
		return false;
	}

	/**
	 * @return the samlService
	 */
	public SAMLService getSamlService()
	{
		return samlService;
	}

	/**
	 * @param samlService
	 *           the samlService to set
	 */
	public void setSamlService(final SAMLService samlService)
	{
		this.samlService = samlService;
	}

	protected ConfigurationService getConfigurationService()
	{
		return configurationService;
	}

	public void setConfigurationService(final ConfigurationService configurationService)
	{
		this.configurationService = configurationService;
	}

	/**
	 * @return the samlLoginService
	 */
	public SamlLoginService getSamlLoginService()
	{
		return samlLoginService;
	}

	/**
	 * @return the commonI18NService
	 */
	public CommonI18NService getCommonI18NService()
	{
		return commonI18NService;
	}

	/**
	 * @param commonI18NService
	 *           the commonI18NService to set
	 */
	public void setCommonI18NService(final CommonI18NService commonI18NService)
	{
		this.commonI18NService = commonI18NService;
	}

	/**
	 * @return the ssoUserService
	 */
	public SSOUserService getSsoUserService()
	{
		return ssoUserService;
	}

	/**
	 * @param ssoUserService
	 *           the ssoUserService to set
	 */
	public void setSsoUserService(final SSOUserService ssoUserService)
	{
		this.ssoUserService = ssoUserService;
	}


}
