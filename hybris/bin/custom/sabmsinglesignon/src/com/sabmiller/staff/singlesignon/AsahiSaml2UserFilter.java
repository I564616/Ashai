/**
 *
 */
package com.sabmiller.staff.singlesignon;

import de.hybris.platform.samlsinglesignon.SAMLService;
import de.hybris.platform.samlsinglesignon.SSOUserService;
import de.hybris.platform.samlsinglesignon.SamlLoginService;
import de.hybris.platform.servicelayer.i18n.CommonI18NService;

import java.io.IOException;
import de.hybris.platform.util.Config;
import org.springframework.security.access.AccessDeniedException;
import java.util.List;

import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpSession;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.apache.commons.lang3.StringUtils;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.saml2.provider.service.authentication.AbstractSaml2AuthenticationRequest;
import org.springframework.security.saml2.provider.service.authentication.DefaultSaml2AuthenticatedPrincipal;
import org.springframework.security.saml2.provider.service.web.HttpSessionSaml2AuthenticationRequestRepository;
import org.springframework.web.filter.OncePerRequestFilter;



/**
 * @author Saumya.Mittal1
 *
 */
public class AsahiSaml2UserFilter extends OncePerRequestFilter
{

	/**
	 * @param userService
	 * @param commonI18NService
	 * @param samlService
	 * @param samlLoginService
	 */

	private static final Logger LOGGER = LoggerFactory.getLogger(AsahiSaml2UserFilter.class);
	private static final String SSO_USERGROUP_KEY = "sso.usergroup.attribute.key";
	private final SSOUserService userService;
	private final CommonI18NService commonI18NService;
	private final SAMLService samlService;
	private final SamlLoginService samlLoginService;

	private static final String DEFAULT_SAML2_AUTHN_REQUEST_ATTR_NAME = HttpSessionSaml2AuthenticationRequestRepository.class
			.getName().concat(".SAML2_AUTHN_REQUEST");


	public AsahiSaml2UserFilter(final SSOUserService userService, final CommonI18NService commonI18NService,
			final SAMLService samlService, final SamlLoginService samlLoginService)
	{
		this.userService = userService;
		this.commonI18NService = commonI18NService;
		this.samlService = samlService;
		this.samlLoginService = samlLoginService;

	}

	@Override
	public void doFilterInternal(final HttpServletRequest request, final HttpServletResponse response, final FilterChain chain)
			throws IOException, ServletException
	{

		final Authentication auth = SecurityContextHolder.getContext().getAuthentication();
		final Object principalObject = auth.getPrincipal();

		if (auth != null && principalObject instanceof DefaultSaml2AuthenticatedPrincipal)
		{
			//final SAMLCredential credential = (SAMLCredential) credentialsObject;
			final DefaultSaml2AuthenticatedPrincipal principal = (DefaultSaml2AuthenticatedPrincipal) principalObject;
			String relayState = StringUtils.EMPTY;
			final HttpSession httpSession = request.getSession(false);
			if (httpSession != null) {
				final AbstractSaml2AuthenticationRequest authRequest = (AbstractSaml2AuthenticationRequest) httpSession.getAttribute(DEFAULT_SAML2_AUTHN_REQUEST_ATTR_NAME);
				relayState = null != authRequest ? authRequest.getRelayState() : relayState;
			}
			if (StringUtils.isBlank(relayState))
			{
				final List roles = this.samlService.getCustomAttributes(principal,
						Config.getString("sso.usergroup.attribute.key", "usergroup"));
				try
				{
					this.samlLoginService.storeLoginToken(response,
							this.userService.getOrCreateSSOUser(this.samlService.getUserId(principal),
									this.samlService.getUserName(principal), roles),
							this.samlService.getLanguage(principal, request, this.commonI18NService));

				}
				catch (final Exception var8)
				{

					LOGGER.error("Can't map user properly", var8);
					throw new AccessDeniedException("Can't map user properly", var8);
				}
			}
		}
		chain.doFilter(request, response);
	}

}