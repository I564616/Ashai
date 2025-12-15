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
package com.sabmiller.storefront.security;

import de.hybris.platform.acceleratorservices.uiexperience.UiExperienceService;
import de.hybris.platform.acceleratorstorefrontcommons.constants.WebConstants;
import de.hybris.platform.b2b.model.B2BCustomerModel;
import de.hybris.platform.b2b.company.B2BCommerceUnitService;
import de.hybris.platform.commercefacades.order.CartFacade;
import de.hybris.platform.commerceservices.enums.UiExperienceLevel;
import de.hybris.platform.commerceservices.order.CommerceCartMergingException;
import de.hybris.platform.commerceservices.order.CommerceCartRestorationException;
import de.hybris.platform.commerceservices.security.SecureToken;
import de.hybris.platform.commerceservices.security.SecureTokenService;
import de.hybris.platform.core.model.security.PrincipalGroupModel;
import de.hybris.platform.core.model.user.EmployeeModel;
import de.hybris.platform.core.model.user.UserModel;
import de.hybris.platform.servicelayer.session.SessionService;
import de.hybris.platform.servicelayer.user.UserService;
import de.hybris.platform.site.BaseSiteService;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Date;
import java.util.List;
import java.util.Map;
import java.util.Set;

import jakarta.annotation.Resource;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.Cookie;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import com.sabmiller.core.model.AsahiB2BUnitModel;

import org.apache.commons.collections4.CollectionUtils;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.security.core.Authentication;
import org.springframework.security.web.authentication.SavedRequestAwareAuthenticationSuccessHandler;
import org.springframework.security.web.savedrequest.HttpSessionRequestCache;
import org.springframework.security.web.savedrequest.RequestCache;
import org.springframework.security.web.savedrequest.SavedRequest;

import com.sabmiller.core.b2b.services.SABMDeliveryDateCutOffService;
import com.sabmiller.core.constants.SabmCoreConstants;
import com.sabmiller.facades.customer.SABMCustomerFacade;
import com.sabmiller.facades.customer.SABMUserAccessHistoryData;
import com.sabmiller.storefront.controllers.SABMWebConstants;
import de.hybris.platform.acceleratorstorefrontcommons.security.BruteForceAttackCounter;


/**
 * Success handler initializing user settings, restoring or merging the cart and ensuring the cart is handled correctly.
 * Cart restoration is stored in the session since the request coming in is that to j_spring_security_check and will be
 * redirected
 */
public class StorefrontAuthenticationSuccessHandler extends SavedRequestAwareAuthenticationSuccessHandler
{
	@Resource(name = "customerFacade")
	private SABMCustomerFacade customerFacade;
	private UiExperienceService uiExperienceService;
	private CartFacade cartFacade;
	private SessionService sessionService;
	private BruteForceAttackCounter bruteForceAttackCounter;
	private Map<UiExperienceLevel, Boolean> forceDefaultTargetForUiExperienceLevel;
	private List<String> restrictedPages;
	private List<String> listRedirectUrlsForceDefaultTarget;
	@Resource(name = "userService")
	private UserService userService;

	@Resource(name = "baseSiteService")
	private BaseSiteService baseSiteService;

	@Resource(name = "b2bCommerceUnitService")
	private B2BCommerceUnitService b2bCommerceUnitService;

	@Resource(name = "sabmDeliveryDateCutOffService")
	private SABMDeliveryDateCutOffService sabmDeliveryDateCutOffService;
	
	@Resource(name = "secureTokenService")
	private SecureTokenService secureTokenService;

	
	private static String CHECKOUT_URL = "/checkout";
	private static String CART_URL = "/cart";
	private static String CART_MERGED = "cartMerged";

	private static final Logger LOG = LoggerFactory.getLogger(StorefrontAuthenticationSuccessHandler.class);

	@Override
	public void onAuthenticationSuccess(final HttpServletRequest request, final HttpServletResponse response,
			final Authentication authentication) throws IOException, ServletException
	{

		getCustomerFacade().loginSuccess();

		//log user access history after login successfully
		final SABMUserAccessHistoryData accessHistory = setUserAccessHistoryData(request);
		getCustomerFacade().createUserAccessHistory(accessHistory);
		// If the user selects RememberMe, set the user name to cookie
		final String cookName = StringUtils.deleteWhitespace(baseSiteService.getCurrentBaseSite().getUid())
				+ SabmCoreConstants.COOKIE_REMEMBERME;
		
		final long timeStamp =new Date().getTime();
		final SecureToken data = new SecureToken(accessHistory.getUid(), timeStamp);
		final String token = secureTokenService.encryptData(data);
		
		if (accessHistory.getRememberMeEnabled())
		{
			final Cookie cookie = new Cookie(cookName, accessHistory.getUid());
			cookie.setHttpOnly(true);
			cookie.setPath("/");
			cookie.setValue(token);
			cookie.setSecure(true);
			response.addCookie(cookie);
		}
		else
		{
			final Cookie cookie = new Cookie(cookName, null);
			cookie.setMaxAge(0);
			response.addCookie(cookie);
		}

		request.setAttribute(CART_MERGED, Boolean.FALSE);
		UserModel userModel = userService.getCurrentUser();

		if (userModel instanceof B2BCustomerModel) {
			if (!getCartFacade().hasEntries())
			{
				getSessionService().setAttribute(WebConstants.CART_RESTORATION_SHOW_MESSAGE, Boolean.TRUE);
				try
				{
					getSessionService().setAttribute(WebConstants.CART_RESTORATION, getCartFacade().restoreSavedCart(null));
				}
				catch (final Exception e )
				{
					getSessionService().setAttribute(WebConstants.CART_RESTORATION_ERROR_STATUS,
							WebConstants.CART_RESTORATION_ERROR_STATUS);
				}
			}
			else
			{
				final String sessionCartGuid = getCartFacade().getSessionCartGuid();
				final String mostRecentSavedCartGuid = getMostRecentSavedCartGuid(sessionCartGuid);
				if (StringUtils.isNotEmpty(mostRecentSavedCartGuid))
				{
					getSessionService().setAttribute(WebConstants.CART_RESTORATION_SHOW_MESSAGE, Boolean.TRUE);
					try
					{
						getSessionService().setAttribute(WebConstants.CART_RESTORATION,
								getCartFacade().restoreCartAndMerge(mostRecentSavedCartGuid, sessionCartGuid));
						request.setAttribute(CART_MERGED, Boolean.TRUE);
					}
					catch (final CommerceCartRestorationException e)
					{
						getSessionService().setAttribute(WebConstants.CART_RESTORATION_ERROR_STATUS,
								WebConstants.CART_RESTORATION_ERROR_STATUS);
					}
					catch (final CommerceCartMergingException e)
					{
						LOG.error("User saved cart could not be merged");
					}
				}
			}
		}

		customerFacade.getNextDeliveryDateAndUpdateSession();

		getBruteForceAttackCounter().resetUserCounter(getCustomerFacade().getCurrentCustomerUid());
		
		/*
		 * SABMC-1435 Check if the customer has only "create and edit user" permission, then the default page is "/your-business"
		 */
		if (userModel instanceof B2BCustomerModel) {
			Set<PrincipalGroupModel> groups = userModel.getGroups();
			List<String> belongingGroupIds = new ArrayList<String>();
			for (PrincipalGroupModel group : groups)
			{
				if(group instanceof AsahiB2BUnitModel)
				{
					continue;
				}
				else
				{
					belongingGroupIds.add(group.getUid());
				}
			}
			
			boolean hasOnlyRole = CustomerRoleChecker.hasOnlyRole(belongingGroupIds, CustomerRoleChecker.ROLE_ASSISTANT);
			if (hasOnlyRole)
			{
				getRedirectStrategy().sendRedirect(request, response, SABMWebConstants.ASSISTANT_DEFAULT_PAGE_URL);
			} else {
				super.onAuthenticationSuccess(request, response, authentication);
			}
		} else if (userModel instanceof EmployeeModel){
			if (customerFacade.isEmployeeUser(userModel)) {
				getRedirectStrategy().sendRedirect(request, response, SABMWebConstants.EMPOYEE_USER_SEARCH_URL);
			}
		}
	}

	/**
	 *
	 * @param request
	 * @return SABMUserAccessHistoryData
	 */
	protected SABMUserAccessHistoryData setUserAccessHistoryData(final HttpServletRequest request)
	{
		final SABMUserAccessHistoryData sabmUserAccessHistoryData = new SABMUserAccessHistoryData();
		sabmUserAccessHistoryData.setUid(getCustomerFacade().getCurrentCustomerUid());
		sabmUserAccessHistoryData.setPublicIPAddress(request.getRemoteAddr());
		sabmUserAccessHistoryData.setUserAgent(request.getHeader("User-Agent"));
		sabmUserAccessHistoryData.setRememberMeEnabled(rememberMeRequested(request));
		return sabmUserAccessHistoryData;
	}

	protected boolean rememberMeRequested(final HttpServletRequest request)
	{
		final String paramValue = request.getParameter("_spring_security_remember_me");

		if ((paramValue != null) && (((paramValue.equalsIgnoreCase("true")) || (paramValue.equalsIgnoreCase("on"))
				|| (paramValue.equalsIgnoreCase("yes")) || (paramValue.equals("1")))))
		{
			return true;
		}
		return false;
	}

	protected List<String> getRestrictedPages()
	{
		return restrictedPages;
	}

	public void setRestrictedPages(final List<String> restrictedPages)
	{
		this.restrictedPages = restrictedPages;
	}

	protected CartFacade getCartFacade()
	{
		return cartFacade;
	}

	public void setCartFacade(final CartFacade cartFacade)
	{
		this.cartFacade = cartFacade;
	}

	protected SessionService getSessionService()
	{
		return sessionService;
	}

	public void setSessionService(final SessionService sessionService)
	{
		this.sessionService = sessionService;
	}

	/*
	 * @see org.springframework.security.web.authentication.AbstractAuthenticationTargetUrlRequestHandler#
	 * isAlwaysUseDefaultTargetUrl()
	 */
	@Override
	protected boolean isAlwaysUseDefaultTargetUrl()
	{
		final UiExperienceLevel uiExperienceLevel = getUiExperienceService().getUiExperienceLevel();
		if (getForceDefaultTargetForUiExperienceLevel().containsKey(uiExperienceLevel))
		{
			return Boolean.TRUE.equals(getForceDefaultTargetForUiExperienceLevel().get(uiExperienceLevel));
		}
		else
		{
			return false;
		}
	}

	@Override
	protected String determineTargetUrl(final HttpServletRequest request, final HttpServletResponse response)
	{
		String targetUrl = super.determineTargetUrl(request, response);
		if (CollectionUtils.isNotEmpty(getRestrictedPages()))
		{
			for (final String restrictedPage : getRestrictedPages())
			{
				// When logging in from a restricted page, return user to homepage.
				if (targetUrl.contains(restrictedPage))
				{
					targetUrl = super.getDefaultTargetUrl();
				}
			}
		}
		/*
		 * If the cart has been merged and the user logging in through checkout, redirect to the cart page to display the
		 * new cart
		 */
		if (StringUtils.equals(targetUrl, CHECKOUT_URL) && ((Boolean) request.getAttribute(CART_MERGED)).booleanValue())
		{
			targetUrl = CART_URL;
		}

		return targetUrl;
	}

	/**
	 * Determine the most recent saved cart of a user for the site that is not the current session cart. The current
	 * session cart is already owned by the user and for the merging functionality to work correctly the most recently
	 * saved cart must be determined. getMostRecentCartGuidForUser(excludedCartsGuid) returns the cart guid which is
	 * ordered by modified time and is not the session cart.
	 *
	 * @param currentCartGuid
	 * @return most recently saved cart guid
	 */
	protected String getMostRecentSavedCartGuid(final String currentCartGuid)
	{
		return getCartFacade().getMostRecentCartGuidForUser(Arrays.asList(currentCartGuid));
	}

	protected Map<UiExperienceLevel, Boolean> getForceDefaultTargetForUiExperienceLevel()
	{
		return forceDefaultTargetForUiExperienceLevel;
	}

	public void setForceDefaultTargetForUiExperienceLevel(
			final Map<UiExperienceLevel, Boolean> forceDefaultTargetForUiExperienceLevel)
	{
		this.forceDefaultTargetForUiExperienceLevel = forceDefaultTargetForUiExperienceLevel;
	}

	protected BruteForceAttackCounter getBruteForceAttackCounter()
	{
		return bruteForceAttackCounter;
	}

	public void setBruteForceAttackCounter(final BruteForceAttackCounter bruteForceAttackCounter)
	{
		this.bruteForceAttackCounter = bruteForceAttackCounter;
	}

	protected UiExperienceService getUiExperienceService()
	{
		return uiExperienceService;
	}

	public void setUiExperienceService(final UiExperienceService uiExperienceService)
	{
		this.uiExperienceService = uiExperienceService;
	}

	protected List<String> getListRedirectUrlsForceDefaultTarget()
	{
		return listRedirectUrlsForceDefaultTarget;
	}

	public void setListRedirectUrlsForceDefaultTarget(final List<String> listRedirectUrlsForceDefaultTarget)
	{
		this.listRedirectUrlsForceDefaultTarget = listRedirectUrlsForceDefaultTarget;
	}

	public void setCustomerFacade(final SABMCustomerFacade customerFacade)
	{
		this.customerFacade = customerFacade;
	}

	public SABMCustomerFacade getCustomerFacade()
	{
		return customerFacade;
	}

}
