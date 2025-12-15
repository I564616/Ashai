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
package com.sabmiller.storefront.security.impl;

import de.hybris.platform.acceleratorstorefrontcommons.security.GUIDCookieStrategy;
import de.hybris.platform.b2b.model.B2BCustomerModel;
import de.hybris.platform.b2b.model.B2BUnitModel;
import de.hybris.platform.commercefacades.customer.CustomerFacade;
import de.hybris.platform.core.model.user.EmployeeModel;
import de.hybris.platform.core.model.user.UserModel;
import de.hybris.platform.servicelayer.session.SessionService;
import de.hybris.platform.servicelayer.user.UserService;

import jakarta.annotation.Resource;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
//import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.web.authentication.WebAuthenticationDetails;
import org.springframework.util.Assert;

import com.sabmiller.core.b2b.services.SabmB2BUnitService;
import com.sabmiller.core.constants.SabmCoreConstants;
import com.sabmiller.core.model.BDECustomerModel;
import com.sabmiller.storefront.security.GUIDAuthenticationSuccessHandler;
import com.sabmiller.storefront.security.ImpersonateUserLoginStrategy;
import com.sabmiller.storefront.security.ImpersonateAuthenticationProvider;


/**
 * Default implementation of {@link ImpersonateUserLoginStrategy}
 *
 * @author xiaowu.a.zhang
 * @date 07/06/2016
 *
 */
public class DefaultImpersonateUserLoginStrategy implements ImpersonateUserLoginStrategy
{
	private static final Logger LOG = LoggerFactory.getLogger(DefaultImpersonateUserLoginStrategy.class.getName());

	/*@Resource(name = "impersonateAuthenticationManager")
	private AuthenticationManager impersonateAuthenticationManager;*/

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

	/**
	 * Impersonate a user. SABMC-1101
	 *
	 * @param username
	 *           the user'name need to be impersonated
	 * @param request
	 *           the request
	 * @param response
	 *           the response
	 * @return if impersonate success return true. otherwise return false
	 */
	@Override
	public boolean loginAsCustomer(final String username, final String b2bUnitId, final HttpServletRequest request,
			final HttpServletResponse response)
	{
		Assert.hasText(username, "The field [username] cannot be empty when impersonate a user");

		final UserModel currentUser = userService.getCurrentUser();
		final UserModel impersonatedUser = userService.getUserForUID(username);

		// only the employee could impersonate other b2b customer.
		// only the B2B customer could be impersonated.
		if (currentUser instanceof EmployeeModel && impersonatedUser instanceof B2BCustomerModel)
		{
			final UsernamePasswordAuthenticationToken token = new UsernamePasswordAuthenticationToken(username, null);
			token.setDetails(new WebAuthenticationDetails(request));
			try
			{
				if (LOG.isDebugEnabled())
				{
					LOG.info("Try to impersonate user:" + username);
				}
				if (StringUtils.isNotBlank(b2bUnitId))
				{
					//Set the currently selected B2BUnit in the session
					final B2BUnitModel b2bUnitModel = b2bUnitService.getUnitForUid(b2bUnitId);
					if (null != b2bUnitModel)
					{
						sessionService.setAttribute(SabmCoreConstants.SESSION_ATTR_B2B_UNIT, b2bUnitModel);
					}
				}
				else
				{
					//Set the currently selected B2BUnit in the session					
					sessionService.setAttribute(SabmCoreConstants.SESSION_ATTR_B2B_UNIT,
							((B2BCustomerModel) impersonatedUser).getDefaultB2BUnit());
				}
				// get the authentication
				//final Authentication authentication = impersonateAuthenticationManager.authenticate(token);
				final Authentication authentication = impersonateAuthenticationProvider.authenticate(token);

				if (null != authentication)
				{
					
					// set the authentication to the context
					SecurityContextHolder.getContext().setAuthentication(authentication);
					// update the delivery date and the cart message.
					customerFacade.loginSuccess();
					// set the cookie
					guidCookieStrategy.setCookie(request, response);
					//set the current PA user to the session.
					sessionService.setAttribute(SabmCoreConstants.SESSION_ATTR_IMPERSONATE_PA, currentUser);
					return true;
				}
			}
			catch (final Exception e)
			{
				LOG.error("Failure during ImpersonateUser", e);
				sessionService.removeAttribute(SabmCoreConstants.SESSION_ATTR_B2B_UNIT);
			}
		}
		return false;
	}

	@Override
	public boolean loginAsEmployee(final HttpServletRequest request, final HttpServletResponse response, String uid)
	{
		UserModel impersonatedUser = userService.getUserForUID(uid);

		if (impersonatedUser instanceof EmployeeModel)
		{
			final UsernamePasswordAuthenticationToken token = new UsernamePasswordAuthenticationToken(impersonatedUser.getUid(),
					null);
			token.setDetails(new WebAuthenticationDetails(request));
			try
			{
				//final Authentication authentication = impersonateAuthenticationManager.authenticate(token);
				final Authentication authentication = impersonateAuthenticationProvider.authenticate(token);
				if (null != authentication)
				{
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
				//final Authentication authentication = impersonateAuthenticationManager.authenticate(token);
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
