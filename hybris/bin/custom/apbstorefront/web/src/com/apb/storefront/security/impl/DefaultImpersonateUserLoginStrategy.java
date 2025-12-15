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
package com.apb.storefront.security.impl;

import jakarta.annotation.Resource;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
//import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.web.authentication.WebAuthenticationDetails;
import org.springframework.util.Assert;

import com.apb.storefront.security.GUIDAuthenticationSuccessHandler;
import com.apb.storefront.security.ImpersonateAuthenticationProvider;
import com.apb.storefront.security.ImpersonateUserLoginStrategy;
import com.sabmiller.core.b2b.services.SabmB2BUnitService;
import com.sabmiller.core.constants.SabmCoreConstants;
import com.sabmiller.core.model.BDECustomerModel;

import de.hybris.platform.acceleratorstorefrontcommons.security.GUIDCookieStrategy;
import de.hybris.platform.commercefacades.customer.CustomerFacade;
import de.hybris.platform.core.model.user.UserModel;
import de.hybris.platform.servicelayer.session.SessionService;
import de.hybris.platform.servicelayer.user.UserService;


/**
 * Default implementation of {@link ImpersonateUserLoginStrategy}
 */
public class DefaultImpersonateUserLoginStrategy implements ImpersonateUserLoginStrategy
{
	private static final Logger LOG = LoggerFactory.getLogger(DefaultImpersonateUserLoginStrategy.class.getName());


	@Resource(name="impersonateAuthenticationProvider")
	private ImpersonateAuthenticationProvider impersonateAuthenticationProvider;

	@Resource(name = "customerFacade")
	private CustomerFacade customerFacade;

	@Resource(name = "guidCookieStrategy")
	private GUIDCookieStrategy guidCookieStrategy;
	
	@Resource(name = "loginGuidAuthenticationSuccessHandler")
	private GUIDAuthenticationSuccessHandler loginGuidAuthenticationSuccessHandler;

	@Resource(name = "userService")
	private UserService userService;

	/** The session service. */
	@Resource(name = "sessionService")
	private SessionService sessionService;
	public static final String SESSION_CART_PARAMETER_NAME = "cart";
	
	@Resource(name = "b2bUnitService")
	private SabmB2BUnitService b2bUnitService;


	@Override
	public boolean loginAsBDECustomer(final String username, final HttpServletRequest request,
			final HttpServletResponse response)
	{
		Assert.hasText(username, "The field [username] cannot be empty when impersonate a user");

		final UserModel impersonatedUser = userService.getUserForUID(username);

		if (impersonatedUser instanceof BDECustomerModel)
		{
			final UsernamePasswordAuthenticationToken token = new UsernamePasswordAuthenticationToken(username, null);
			token.setDetails(new WebAuthenticationDetails(request));
			try
			{
				if (LOG.isDebugEnabled())
				{
					LOG.info("Try to impersonate user:" + username);
				}
				
				final Authentication authentication = impersonateAuthenticationProvider.authenticate(token);

				if (null != authentication)
				{			
					sessionService.setAttribute(SabmCoreConstants.SESSION_ATTR_B2B_UNIT,((BDECustomerModel) impersonatedUser).getDefaultB2BUnit());
					SecurityContextHolder.getContext().setAuthentication(authentication);
					customerFacade.loginSuccess();
					guidCookieStrategy.setCookie(request, response);
					return true;
				}
			}
			catch (final Exception e)
			{
				LOG.error("Failure during ImpersonateUser", e);
			}
		}
		return false;
	}

}
