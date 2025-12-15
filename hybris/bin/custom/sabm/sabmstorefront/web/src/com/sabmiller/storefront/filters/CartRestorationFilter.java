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
package com.sabmiller.storefront.filters;

import de.hybris.platform.acceleratorstorefrontcommons.constants.WebConstants;
import de.hybris.platform.b2b.model.B2BCustomerModel;
import de.hybris.platform.commercefacades.order.CartFacade;
import de.hybris.platform.commerceservices.order.CommerceCartRestorationException;
import de.hybris.platform.core.model.user.UserModel;
import de.hybris.platform.order.CartService;
import de.hybris.platform.servicelayer.session.SessionService;
import de.hybris.platform.servicelayer.user.UserService;
import de.hybris.platform.site.BaseSiteService;

import java.io.IOException;

import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.Cookie;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.web.filter.OncePerRequestFilter;

import com.sabmiller.storefront.security.cookie.CartRestoreCookieGenerator;


/**
 * Filter that the restores the user's cart. This is a spring configured filter that is executed by the
 * PlatformFilterChain.
 */
public class CartRestorationFilter extends OncePerRequestFilter
{

	private static final Logger LOG = LoggerFactory.getLogger(CartRestorationFilter.class);

	private CartRestoreCookieGenerator cartRestoreCookieGenerator;
	private CartService cartService;
	private CartFacade cartFacade;
	private BaseSiteService baseSiteService;
	private UserService userService;
	private SessionService sessionService;

	@Override
	public void doFilterInternal(final HttpServletRequest request, final HttpServletResponse response,
			final FilterChain filterChain) throws IOException, ServletException
	{
		UserModel userModel = getUserService().getCurrentUser();
		if (getUserService().isAnonymousUser(userModel))
		{
			if (getCartService().hasSessionCart()
					&& getBaseSiteService().getCurrentBaseSite().equals(
							getBaseSiteService().getBaseSiteForUID(getCartService().getSessionCart().getSite().getUid())))
			{
				final String guid = getCartService().getSessionCart().getGuid();

				if (!StringUtils.isEmpty(guid))
				{
					getCartRestoreCookieGenerator().addCookie(response, guid);
				}
			}
			else if (request.getSession().isNew()
					|| (getCartService().hasSessionCart() && !getBaseSiteService().getCurrentBaseSite().equals(
							getBaseSiteService().getBaseSiteForUID(getCartService().getSessionCart().getSite().getUid()))))
			{
				String cartGuid = null;

				if (request.getCookies() != null)
				{
					final String anonymousCartCookieName = getCartRestoreCookieGenerator().getCookieName();

					for (final Cookie cookie : request.getCookies())
					{
						if (anonymousCartCookieName.equals(cookie.getName()))
						{
							cartGuid = cookie.getValue();
							break;
						}
					}
				}

				if (!StringUtils.isEmpty(cartGuid))
				{
					getSessionService().setAttribute(WebConstants.CART_RESTORATION_SHOW_MESSAGE, Boolean.TRUE);
					try
					{
						getSessionService().setAttribute(WebConstants.CART_RESTORATION, getCartFacade().restoreSavedCart(cartGuid));
					}
					catch (final CommerceCartRestorationException e)
					{
						getSessionService().setAttribute(WebConstants.CART_RESTORATION_ERROR_STATUS,
								WebConstants.CART_RESTORATION_ERROR_STATUS);
					}
				}
			}

		}
		else
		{
			LOG.info("Not an anonymous user. Check if restoration is required");
			if ((userModel instanceof B2BCustomerModel && !getCartService().hasSessionCart() && getSessionService().getAttribute(WebConstants.CART_RESTORATION) == null)
					|| (getCartService().hasSessionCart() && !getBaseSiteService().getCurrentBaseSite().equals(
							getBaseSiteService().getBaseSiteForUID(getCartService().getSessionCart().getSite().getUid()))))
			{
				LOG.info("Cart restoration!!!");

				getSessionService().setAttribute(WebConstants.CART_RESTORATION_SHOW_MESSAGE, Boolean.TRUE);
				try
				{
					getSessionService().setAttribute(WebConstants.CART_RESTORATION, getCartFacade().restoreSavedCart(null));
				}
				catch (final CommerceCartRestorationException e)
				{
					getSessionService().setAttribute(WebConstants.CART_RESTORATION, WebConstants.CART_RESTORATION_ERROR_STATUS);
				}
			}
		}

		filterChain.doFilter(request, response);
	}

	protected SessionService getSessionService()
	{
		return sessionService;
	}

	public void setSessionService(final SessionService sessionService)
	{
		this.sessionService = sessionService;
	}

	protected CartRestoreCookieGenerator getCartRestoreCookieGenerator()
	{
		return cartRestoreCookieGenerator;
	}

	public void setCartRestoreCookieGenerator(final CartRestoreCookieGenerator cartRestoreCookieGenerator)
	{
		this.cartRestoreCookieGenerator = cartRestoreCookieGenerator;
	}

	protected CartFacade getCartFacade()
	{
		return cartFacade;
	}

	public void setCartFacade(final CartFacade cartFacade)
	{
		this.cartFacade = cartFacade;
	}

	protected BaseSiteService getBaseSiteService()
	{
		return baseSiteService;
	}

	public void setBaseSiteService(final BaseSiteService baseSiteService)
	{
		this.baseSiteService = baseSiteService;
	}

	protected UserService getUserService()
	{
		return userService;
	}

	public void setUserService(final UserService userService)
	{
		this.userService = userService;
	}

	protected CartService getCartService()
	{
		return cartService;
	}

	public void setCartService(final CartService cartService)
	{
		this.cartService = cartService;
	}
}
