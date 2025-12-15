/*
 * [y] hybris Platform
 *
 * Copyright (c) 2017 SAP SE or an SAP affiliate company.  All rights reserved.
 *
 * This software is the confidential and proprietary information of SAP
 * ("Confidential Information"). You shall not disclose such Confidential
 * Information and shall use it only in accordance with the terms of the
 * license agreement you entered into with SAP.
 */
package com.apb.storefront.security;

import de.hybris.platform.acceleratorstorefrontcommons.security.GUIDCookieStrategy;
import de.hybris.platform.assistedservicefacades.AssistedServiceFacade;
import de.hybris.platform.core.model.user.UserModel;
import de.hybris.platform.servicelayer.model.ModelService;
import de.hybris.platform.servicelayer.user.UserService;

import java.io.IOException;
import java.util.Date;

import jakarta.annotation.Resource;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

import org.springframework.security.core.Authentication;
import org.springframework.security.web.authentication.AuthenticationSuccessHandler;

import com.apb.core.util.AsahiCoreUtil;
import com.apb.core.util.AsahiSiteUtil;
import com.apb.service.b2bunit.ApbB2BUnitService;
import com.sabmiller.facades.cart.SABMCartFacade;
import com.sabmiller.facades.customer.SABMCustomerFacade;


/**
 * Default implementation of {@link AuthenticationSuccessHandler}
 */
public class GUIDAuthenticationSuccessHandler implements AuthenticationSuccessHandler
{
	private GUIDCookieStrategy guidCookieStrategy;
	private AuthenticationSuccessHandler authenticationSuccessHandler;
	
	@Resource(name = "userService")
	private UserService userService;

	@Resource(name = "modelService")
	private ModelService modelService;
	
	@Resource(name="asahiSiteUtil")
	private AsahiSiteUtil asahiSiteUtil;
	
	@Resource
	private AsahiCoreUtil asahiCoreUtil;
	
	@Resource
	private ApbB2BUnitService apbB2BUnitService;
	
	@Resource(name = "assistedServiceFacade")
	private AssistedServiceFacade assistedServiceFacade;
	
	@Resource(name = "cartFacade")
	private SABMCartFacade sabmCartFacade;
	
	@Resource(name = "b2bCustomerFacade")
	private SABMCustomerFacade sabmCustomerFacade;
	
	@Override
	public void onAuthenticationSuccess(final HttpServletRequest request, final HttpServletResponse response,
			final Authentication authentication) throws IOException, ServletException
	{
		request.getSession().setAttribute("isCustomerLoggedIn", Boolean.TRUE);
		getGuidCookieStrategy().setCookie(request, response);
		getAuthenticationSuccessHandler().onAuthenticationSuccess(request, response, authentication);
		setCustomerLastLogin();
		
		//Remove if the cart contains bonus product for general user...
		if (asahiSiteUtil.isApb() && !userService.isAnonymousUser(userService.getCurrentUser()) && 
				!assistedServiceFacade.isAssistedServiceModeLaunched() && sabmCartFacade.hasAnyBonusProduct())
		{
			sabmCartFacade.removeBonusProductFromCart();
		}
		
		//Send the Expiry email to the user if the pay access request got expire...
		//Update the Sam Access Model if the pay access request got expire...
		if(asahiSiteUtil.isSga()){
			sabmCustomerFacade.validatePayAccess(userService.getCurrentUser());
		}
	}

	protected GUIDCookieStrategy getGuidCookieStrategy()
	{
		return guidCookieStrategy;
	}

	/**
	 * @param guidCookieStrategy
	 *           the guidCookieStrategy to set
	 */
	public void setGuidCookieStrategy(final GUIDCookieStrategy guidCookieStrategy)
	{
		this.guidCookieStrategy = guidCookieStrategy;
	}

	protected AuthenticationSuccessHandler getAuthenticationSuccessHandler()
	{
		return authenticationSuccessHandler;
	}

	/**
	 * @param authenticationSuccessHandler
	 *           the authenticationSuccessHandler to set
	 */
	public void setAuthenticationSuccessHandler(final AuthenticationSuccessHandler authenticationSuccessHandler)
	{
		this.authenticationSuccessHandler = authenticationSuccessHandler;
	}
	
	private void setCustomerLastLogin(){
		
		if(asahiSiteUtil.isSga()){
			
		final UserModel userModel = userService.getCurrentUser();

		if (null != userModel)
			{
				userModel.setLastLogin(new Date());
				modelService.save(userModel);
			}
		}
	}
}
