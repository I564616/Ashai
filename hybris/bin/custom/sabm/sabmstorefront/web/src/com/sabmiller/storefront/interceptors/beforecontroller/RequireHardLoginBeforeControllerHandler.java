/*
 * [y] hybris Platform
 *
 * Copyright (c) 2000-2015 hybris AG
 * All rights reserved.
 *
 * This software is the confidential and proprietary information of hybris
 * ("Confidential Information"). You shall not disclose such Confidential
 * Information and shall use it only in accordance with the terms of the
 * license agreement you entered into with hybris.
 *
 *
 */
package com.sabmiller.storefront.interceptors.beforecontroller;

import de.hybris.platform.acceleratorstorefrontcommons.annotations.RequireHardLogIn;
import de.hybris.platform.acceleratorstorefrontcommons.constants.WebConstants;
import de.hybris.platform.order.CartService;
import de.hybris.platform.servicelayer.session.SessionService;
import de.hybris.platform.servicelayer.user.UserService;

import java.lang.annotation.Annotation;

import jakarta.servlet.http.Cookie;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

import org.apache.commons.lang3.StringUtils;
import org.apache.log4j.Logger;
import org.springframework.core.annotation.AnnotationUtils;
import org.springframework.security.web.RedirectStrategy;
import org.springframework.web.method.HandlerMethod;
import org.springframework.web.util.CookieGenerator;

import com.sabmiller.core.model.BDECustomerModel;
import com.sabmiller.storefront.annotations.DeniedToBDE;
import de.hybris.platform.acceleratorstorefrontcommons.interceptors.BeforeControllerHandler;



/**
 */
public class RequireHardLoginBeforeControllerHandler implements BeforeControllerHandler
{
	private static final Logger LOG = Logger.getLogger(RequireHardLoginBeforeControllerHandler.class);

	public static final String SECURE_GUID_SESSION_KEY = "acceleratorSecureGUID";

	private String loginUrl;
	private String loginAndCheckoutUrl;
	private RedirectStrategy redirectStrategy;
	private CookieGenerator cookieGenerator;
	private UserService userService;
	private SessionService sessionService;
	private CartService cartService;

	protected String getLoginUrl()
	{
		return loginUrl;
	}

	public void setLoginUrl(final String loginUrl)
	{
		this.loginUrl = loginUrl;
	}

	protected RedirectStrategy getRedirectStrategy()
	{
		return redirectStrategy;
	}

	public void setRedirectStrategy(final RedirectStrategy redirectStrategy)
	{
		this.redirectStrategy = redirectStrategy;
	}

	protected CookieGenerator getCookieGenerator()
	{
		return cookieGenerator;
	}

	public void setCookieGenerator(final CookieGenerator cookieGenerator)
	{
		this.cookieGenerator = cookieGenerator;
	}

	protected UserService getUserService()
	{
		return userService;
	}

	public void setUserService(final UserService userService)
	{
		this.userService = userService;
	}

	public String getLoginAndCheckoutUrl()
	{
		return loginAndCheckoutUrl;
	}

	public void setLoginAndCheckoutUrl(final String loginAndCheckoutUrl)
	{
		this.loginAndCheckoutUrl = loginAndCheckoutUrl;
	}

	protected SessionService getSessionService()
	{
		return sessionService;
	}

	public void setSessionService(final SessionService sessionService)
	{
		this.sessionService = sessionService;
	}

	public CartService getCartService()
	{
		return cartService;
	}

	public void setCartService(final CartService cartService)
	{
		this.cartService = cartService;
	}

	@Override
	public boolean beforeController(final HttpServletRequest request, final HttpServletResponse response,
			final HandlerMethod handler) throws Exception
	{
		// We only care if the request is secure
		if (request.isSecure())
		{
			// redirect to Homepage for BDE view only mode
			final DeniedToBDE DeniedToBDE = findAnnotation(handler, DeniedToBDE.class);
			if(DeniedToBDE !=null && (getUserService().getCurrentUser() instanceof BDECustomerModel)){
				LOG.warn("This page is disabled for BDE view only mode" + ", redirecting");
				getRedirectStrategy().sendRedirect(request, response, "/");
				return false;
			}				
			// Check if the handler has our annotation
			final RequireHardLogIn annotation = findAnnotation(handler, RequireHardLogIn.class);
			if (annotation != null)
			{
				final String guid = (String) request.getSession().getAttribute(SECURE_GUID_SESSION_KEY);
				boolean redirect = true;

				if ((!getUserService().isAnonymousUser(getUserService().getCurrentUser()))
						&& checkForGUIDCookie(request, response, guid))
				{
					redirect = false;									
				}

				if (redirect)
				{
					LOG.warn((guid == null ? "missing secure token in session" : "no matching guid cookie") + ", redirecting");
					getRedirectStrategy().sendRedirect(request, response, getRedirectUrl(request));
					return false;
				}
			}
		}

		return true;
	}


	protected boolean checkForGUIDCookie(final HttpServletRequest request, final HttpServletResponse response, final String guid)
	{
		if (guid != null && request.getCookies() != null)
		{
			final String guidCookieName = getCookieGenerator().getCookieName();
			if (guidCookieName != null)
			{
				for (final Cookie cookie : request.getCookies())
				{
					if (guidCookieName.equals(cookie.getName()))
					{
						if (guid.equals(cookie.getValue()))
						{
							return true;
						}
						else
						{
							LOG.info("Found secure cookie with invalid value. expected [" + guid + "] actual [" + cookie.getValue()
									+ "]. removing.");
							getCookieGenerator().removeCookie(response);
						}
					}
				}
			}
		}

		return false;

	}

	protected boolean checkForAnonymousCheckout()
	{
		if (Boolean.TRUE.equals(getSessionService().getAttribute(WebConstants.ANONYMOUS_CHECKOUT)))
		{
			if (getSessionService().getAttribute(WebConstants.ANONYMOUS_CHECKOUT_GUID) == null)
			{
				getSessionService().setAttribute(WebConstants.ANONYMOUS_CHECKOUT_GUID,
						StringUtils.substringBefore(getCartService().getSessionCart().getUser().getUid(), "|"));
			}
			return true;
		}
		return false;
	}

	protected String getRedirectUrl(final HttpServletRequest request)
	{
			return getLoginUrl();
	}

	protected <T extends Annotation> T findAnnotation(final HandlerMethod handlerMethod, final Class<T> annotationType)
	{
		// Search for method level annotation
		final T annotation = handlerMethod.getMethodAnnotation(annotationType);
		if (annotation != null)
		{
			return annotation;
		}

		// Search for class level annotation
		return AnnotationUtils.findAnnotation(handlerMethod.getBeanType(), annotationType);
	}
}
