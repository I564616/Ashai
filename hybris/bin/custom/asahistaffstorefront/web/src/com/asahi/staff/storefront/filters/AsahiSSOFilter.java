package com.asahi.staff.storefront.filters;

import de.hybris.platform.acceleratorstorefrontcommons.security.GUIDCookieStrategy;
import de.hybris.platform.jalo.JaloSession;
import de.hybris.platform.jalo.user.CookieBasedLoginToken;
import de.hybris.platform.jalo.user.LoginToken;
import de.hybris.platform.jalo.user.User;
import de.hybris.platform.jalo.user.UserManager;
import de.hybris.platform.servicelayer.config.ConfigurationService;

import java.io.IOException;

import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.Cookie;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

import org.apache.log4j.Logger;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.web.authentication.WebAuthenticationDetails;
import org.springframework.web.filter.OncePerRequestFilter;
import org.springframework.web.util.WebUtils;

import com.sabmiller.staff.singlesignon.constants.SabmsinglesignonConstants;


/**
 * Created by wei.yang.ng on 27/05/2016.
 */
public class AsahiSSOFilter extends OncePerRequestFilter
{
	private static final Logger LOG = Logger.getLogger(AsahiSSOFilter.class);

	private GUIDCookieStrategy guidCookieStrategy;
	private UserDetailsService userDetailsService;
	private ConfigurationService configurationService;

	@Override
	protected void doFilterInternal(final HttpServletRequest request, final HttpServletResponse response,
			final FilterChain filterChain) throws ServletException, IOException
	{
		LOG.info("Logging in user using SAML cookie" + getCookieName());
		final Cookie cookie = WebUtils.getCookie(request, getCookieName());
		LOG.info("Logging in user using SAML cookie" + cookie);
		if (null != cookie)
		{
			final LoginToken token = new CookieBasedLoginToken(cookie);
			LOG.info("AsahiSSOFilter token : " + token);
			LOG.info("AsahiSSOFilter username : " + token.getUser().getUid());
			loginUser(token.getUser().getUid(), request, response);
		}

		filterChain.doFilter(request, response);
	}

	private String getCookieName()
	{
		return configurationService.getConfiguration().getString(SabmsinglesignonConstants.SSO_COOKIE_NAME,
				SabmsinglesignonConstants.SSO_DEFAULT_COOKIE_NAME);
	}

	private void loginUser(final String userName, final HttpServletRequest request, final HttpServletResponse response)
	{
		final UserDetails userDetails = getUserDetailsService().loadUserByUsername(userName);
		LOG.info("AsahiSSOFilter userDetails : " + userDetails);
		final UsernamePasswordAuthenticationToken token = new UsernamePasswordAuthenticationToken(userName,
				userDetails.getAuthorities());
		LOG.info("AsahiSSOFilter token : " + token);
		token.setDetails(new WebAuthenticationDetails(request));
		final User user = UserManager.getInstance().getUserByLogin(userName);
		LOG.info("AsahiSSOFilter user : " + user);
		JaloSession.getCurrentSession().setUser(user);
		SecurityContextHolder.getContext().setAuthentication(token);
		LOG.info("AsahiSSOFilter setAuthentication : " + token);
		getGuidCookieStrategy().setCookie(request, response);
		LOG.info("AsahiSSOFilter setCookie : " + token);
		eraseSamlCookie(response);
		LOG.info("AsahiSSOFilter eraseSamlCookie : " + token);
	}

	private void eraseSamlCookie(final HttpServletResponse response)
	{

		final Cookie cookie = new Cookie(getCookieName(), "");
		cookie.setMaxAge(0);
		cookie.setPath("/");
		response.addCookie(cookie);

	}

	public GUIDCookieStrategy getGuidCookieStrategy()
	{
		return guidCookieStrategy;
	}

	public void setGuidCookieStrategy(final GUIDCookieStrategy guidCookieStrategy)
	{
		this.guidCookieStrategy = guidCookieStrategy;
	}

	public UserDetailsService getUserDetailsService()
	{
		return userDetailsService;
	}

	public void setUserDetailsService(final UserDetailsService userDetailsService)
	{
		this.userDetailsService = userDetailsService;
	}

	protected ConfigurationService getConfigurationService()
	{
		return configurationService;
	}

	public void setConfigurationService(final ConfigurationService configurationService)
	{
		this.configurationService = configurationService;
	}
}
