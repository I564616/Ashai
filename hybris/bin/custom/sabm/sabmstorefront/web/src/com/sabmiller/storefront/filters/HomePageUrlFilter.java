/**
 *
 */
package com.sabmiller.storefront.filters;

import java.io.IOException;

import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

import org.springframework.security.core.Authentication;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.web.DefaultRedirectStrategy;
import org.springframework.security.web.RedirectStrategy;
import org.springframework.web.filter.OncePerRequestFilter;


/**
 * Redirect pay only user to your business page when visiting homepage
 *
 * @author bonnie
 *
 */
public class HomePageUrlFilter extends OncePerRequestFilter
{
	public static final String ROLE_B2BINVOICECUSTOMER = "ROLE_B2BINVOICECUSTOMER";
	public static final String ROLE_B2BORDERCUSTOMER = "ROLE_B2BORDERCUSTOMER";
	public static final String ROLE_ANONYMOUS = "ROLE_ANONYMOUS";
	public static final String HOME = "";
	private final RedirectStrategy redirectStrategy = new DefaultRedirectStrategy();
	private String homePageUrlForPayOnlyUser;
	private String loginPage;


	/*
	 * (non-Javadoc)
	 *
	 * @see org.springframework.web.filter.OncePerRequestFilter#doFilterInternal(javax.servlet.http.HttpServletRequest,
	 * javax.servlet.http.HttpServletResponse, javax.servlet.FilterChain)
	 */
	@Override
	protected void doFilterInternal(final HttpServletRequest paramHttpServletRequest,
			final HttpServletResponse paramHttpServletResponse, final FilterChain paramFilterChain)
					throws ServletException, IOException
	{
		//if home page redirect to your business page
		if (HOME.equals(paramHttpServletRequest.getServletPath()))
		{
			final Authentication auth = getAuth();

			if (hasRole(ROLE_ANONYMOUS, auth))
			{
				redirectStrategy.sendRedirect(paramHttpServletRequest, paramHttpServletResponse, loginPage);
				return;
			}

			if (hasRole(ROLE_B2BORDERCUSTOMER, auth))
			{
				paramFilterChain.doFilter(paramHttpServletRequest, paramHttpServletResponse);
				return;
			}

			if (hasRole(ROLE_B2BINVOICECUSTOMER, auth))
			{
				redirectStrategy.sendRedirect(paramHttpServletRequest, paramHttpServletResponse, homePageUrlForPayOnlyUser);
				return;
			}
		}
		paramFilterChain.doFilter(paramHttpServletRequest, paramHttpServletResponse);
	}

	/**
	 * @param homePageUrlForPayOnlyUser
	 *           the homePageUrlForPayOnlyUser to set
	 */
	public void setHomePageUrlForPayOnlyUser(final String homePageUrlForPayOnlyUser)
	{
		this.homePageUrlForPayOnlyUser = homePageUrlForPayOnlyUser;
	}



	/**
	 * @param loginPage
	 *           the loginPage to set
	 */
	public void setLoginPage(final String loginPage)
	{
		this.loginPage = loginPage;
	}

	protected Authentication getAuth()
	{
		return SecurityContextHolder.getContext().getAuthentication();
	}

	protected boolean hasRole(final String role, final Authentication auth)
	{
		for (final GrantedAuthority ga : auth.getAuthorities())
		{
			if (ga.getAuthority().equals(role))
			{
				return true;
			}
		}
		return false;
	}

}
