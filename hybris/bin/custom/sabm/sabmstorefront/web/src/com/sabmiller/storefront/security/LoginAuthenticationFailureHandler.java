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

import de.hybris.platform.b2b.model.B2BCustomerModel;
import de.hybris.platform.b2b.model.B2BUnitModel;
import de.hybris.platform.core.model.user.UserModel;
import de.hybris.platform.servicelayer.exceptions.UnknownIdentifierException;
import de.hybris.platform.servicelayer.model.ModelService;
import de.hybris.platform.servicelayer.user.UserService;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import jakarta.annotation.Resource;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

import org.apache.commons.lang3.StringUtils;
import org.apache.commons.collections4.CollectionUtils;
import org.apache.log4j.Logger;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.web.authentication.SimpleUrlAuthenticationFailureHandler;
import org.springframework.security.web.authentication.session.SessionAuthenticationException;

import com.sabmiller.commons.enumerations.LoginStatus;
import com.sabmiller.core.model.AsahiB2BUnitModel;
import com.sabmiller.facades.customer.SABMCustomerFacade;
import com.sabmiller.storefront.controllers.SABMWebConstants;
import de.hybris.platform.acceleratorstorefrontcommons.security.BruteForceAttackCounter;


/**
 * LoginAuthenticationFailureHandler
 *
 * Modify by yaopeng for SAB-632 Login user errors after a specified number Lock the user
 *
 */
public class LoginAuthenticationFailureHandler extends SimpleUrlAuthenticationFailureHandler
{
	private static final Logger LOG = Logger.getLogger(LoginAuthenticationFailureHandler.class);
	private BruteForceAttackCounter bruteForceAttackCounter;
	// add by SAB-632
	private UserService userService;
	private ModelService modelService;

	@Resource(name = "b2bCustomerFacade")
	private SABMCustomerFacade customerFacade;


	/**
	 * Modify by yaopeng for SAB-632 add lock the user logic
	 *
	 * @param request
	 * @param response
	 * @param exception
	 * @throws IOException
	 * @throws ServletException
	 */
	@Override
	public void onAuthenticationFailure(final HttpServletRequest request, final HttpServletResponse response,
			final AuthenticationException exception) throws IOException, ServletException
	{
		final String userName = request.getParameter("j_username");
		Integer loginAttempts = (Integer) request.getSession().getAttribute(SABMWebConstants.LOGIN_ATTEMPTS);
		if (loginAttempts == null){
			request.getSession().setAttribute(SABMWebConstants.LOGIN_ATTEMPTS,
					1);

			if(StringUtils.isNotBlank(request.getParameter("targetUrl"))){
				super.setDefaultFailureUrl("/login?targetUrl="+StringUtils.replaceOnce(request.getParameter("targetUrl"),"?","&")+"&error=true");	
			}
		} else {
			loginAttempts = loginAttempts +1;
			request.getSession().setAttribute(SABMWebConstants.LOGIN_ATTEMPTS,
					loginAttempts);
		}
		
		
		
		
		if (exception instanceof SessionAuthenticationException)
		{
			request.getSession().invalidate();

			request.getSession().setAttribute(SABMWebConstants.SPRING_SECURITY_LOGINDISABLED_STATUS,
					LoginStatus.CONCURRENT_LOGINS_BLOCKED);

		}
		else
		{
			// Register brute attacks
			bruteForceAttackCounter.registerLoginFailure(userName);
			// add by SAB-632  start

			try
			{
				// According to the username for the userModel
				final UserModel userModel = getUserService().getUserForUID(StringUtils.lowerCase(userName));
				//Judgment for the isActive b2bcustomerModel
				if (userModel instanceof B2BCustomerModel)
				{
					final B2BCustomerModel b2bcustomerModel = (B2BCustomerModel) userModel;
					
					List<B2BUnitModel> cubUnits = new ArrayList<>();
					b2bcustomerModel.getGroups().forEach(
							group -> {
								if (group instanceof B2BUnitModel && !(group instanceof AsahiB2BUnitModel))
								{
									cubUnits.add((B2BUnitModel)group);
								}
							});
					
					if(!CollectionUtils.isEmpty(cubUnits))
					{
					
					final boolean isActive = b2bcustomerModel.getActive() && customerFacade.isCustomerActiveForCUB(b2bcustomerModel);

					if (isActive)
					{
						LoginStatus loginStatus = LoginStatus.UN_LOCK;
						//Judgment for the unlock userModel
						if (!b2bcustomerModel.isLoginDisabled() && (bruteForceAttackCounter.isAttack(userName)))
						{
							//If login error number has reached the conditions of the lock

							//update userModel's loginDisabled  is true
							b2bcustomerModel.setLoginDisabled(true);
							//save userModel's loginDisabledTime  is the current system time
							b2bcustomerModel.setLoginDisabledTime(new Date());
							getModelService().save(b2bcustomerModel);
							bruteForceAttackCounter.resetUserCounter(b2bcustomerModel.getUid());
							loginStatus = LoginStatus.IS_LOCK;

						}

						//Judgment for the lock userModel
						else if (b2bcustomerModel.isLoginDisabled())
						{
							loginStatus = LoginStatus.IS_LOCK;
						}

						else if (!customerFacade.canUserLogin(b2bcustomerModel))
						{
							loginStatus = customerFacade.getLoginStatus(b2bcustomerModel);
						}

						request.getSession().invalidate();

						// save the login user loginDisabledstatus
						request.getSession().setAttribute(SABMWebConstants.SPRING_SECURITY_LOGINDISABLED_STATUS, loginStatus);
					}
					else
					{
						request.getSession().invalidate();

						request.getSession().setAttribute(SABMWebConstants.ACCOUNT_ISDEACTIVATED,
								SABMWebConstants.ACCOUNT_LOGINDISABLED_STATUS_ISDEACTIVATED);

					}
					
				  }

				}

			}
			catch (final UnknownIdentifierException e)
			{
				LOG.warn("Brute force attack attempt for non existing user name " + StringUtils.lowerCase(userName), e);
			}
		}

		// add by SAB-632  end

		// Store the j_username in the session
		request.getSession().setAttribute("SPRING_SECURITY_LAST_USERNAME", userName);


		super.onAuthenticationFailure(request, response, exception);

	}





	protected BruteForceAttackCounter getBruteForceAttackCounter()
	{
		return bruteForceAttackCounter;
	}

	public void setBruteForceAttackCounter(final BruteForceAttackCounter bruteForceAttackCounter)
	{
		this.bruteForceAttackCounter = bruteForceAttackCounter;
	}



	/**
	 * @return the userService
	 */
	public UserService getUserService()
	{
		return userService;
	}



	/**
	 * @param userService
	 *           the userService to set
	 */
	public void setUserService(final UserService userService)
	{
		this.userService = userService;
	}



	/**
	 * @return the modelService
	 */
	public ModelService getModelService()
	{
		return modelService;
	}



	/**
	 * @param modelService
	 *           the modelService to set
	 */
	public void setModelService(final ModelService modelService)
	{
		this.modelService = modelService;
	}


}
