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

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import jakarta.annotation.Resource;

import org.apache.commons.collections4.CollectionUtils;
import org.apache.commons.lang3.StringUtils;
import org.apache.log4j.Logger;
import org.springframework.security.authentication.AbstractAuthenticationToken;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.authentication.LockedException;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;

import com.apb.service.b2bunit.ApbB2BUnitService;
import com.sabmiller.core.model.AsahiB2BUnitModel;
import com.sabmiller.facades.customer.SABMCustomerFacade;

import de.hybris.platform.acceleratorstorefrontcommons.security.AbstractAcceleratorAuthenticationProvider;
import de.hybris.platform.acceleratorstorefrontcommons.security.BruteForceAttackCounter;
import de.hybris.platform.b2b.model.B2BCustomerModel;
import de.hybris.platform.b2b.model.B2BUnitModel;
import de.hybris.platform.core.Constants;
import de.hybris.platform.core.model.user.EmployeeModel;
import de.hybris.platform.core.model.user.UserModel;
import de.hybris.platform.servicelayer.exceptions.UnknownIdentifierException;

/**
 * Derived authentication provider supporting additional authentication checks. See
 * {@link de.hybris.platform.spring.security.RejectUserPreAuthenticationChecks}.
 *
 * <ul>
 * <li>prevent login without password for users created via CSCockpit</li>
 * <li>prevent login as user in group admingroup</li>
 * </ul>
 *
 * any login as admin disables SearchRestrictions and therefore no page can be viewed correctly
 */
public class AcceleratorAuthenticationProvider extends AbstractAcceleratorAuthenticationProvider
{
	private static final Logger LOG = Logger.getLogger(AcceleratorAuthenticationProvider.class);
	
	@Resource
	private ApbB2BUnitService apbB2BUnitService;
	
	@Resource(name = "b2bCustomerFacade")
	protected SABMCustomerFacade customerFacade;
	
	private BruteForceAttackCounter bruteForceAttackCounter;
	
	private static final String ROLE_ADMIN_GROUP = "ROLE_" + Constants.USER.ADMIN_USERGROUP.toUpperCase();

	private GrantedAuthority adminAuthority = new SimpleGrantedAuthority(ROLE_ADMIN_GROUP);
	
	private long unlockLimitSecond;

	/**
	 * @see de.hybris.platform.acceleratorstorefrontcommons.security.AbstractAcceleratorAuthenticationProvider#additionalAuthenticationChecks(org.springframework.security.core.userdetails.UserDetails,
	 *      org.springframework.security.authentication.AbstractAuthenticationToken)
	 */
	@Override
	protected void additionalAuthenticationChecks(final UserDetails details, final AbstractAuthenticationToken authentication)
			throws AuthenticationException
	{
		super.additionalAuthenticationChecks(details, authentication);

		// Check if the user is in role admingroup
		if (getAdminAuthority() != null && details.getAuthorities().contains(getAdminAuthority()))
		{
			throw new LockedException("Login attempt as " + Constants.USER.ADMIN_USERGROUP + " is rejected");
		}		
	}
	
	/**
	 * @param authentication
	 */
	protected boolean preAuthentication(final Authentication authentication)
	{
		final String username = (authentication.getPrincipal() == null) ? "NONE_PROVIDED" : authentication.getName();
		
		try
		{
			// According to the username for the userModel

			final UserModel userModel = getUserService().getUserForUID(username.toLowerCase());
			if (userModel instanceof B2BCustomerModel)
			{
				
				List<B2BUnitModel> sgaUnits = new ArrayList<>();
				final B2BCustomerModel b2bcustomerModel = (B2BCustomerModel) userModel;
				b2bcustomerModel.getGroups().forEach(
						group -> {
							if ((group instanceof AsahiB2BUnitModel))
							{
								sgaUnits.add((B2BUnitModel)group);
							}
						});
				
				if(CollectionUtils.isEmpty(sgaUnits))
				{
					return false;
				}
				
				
				
				//check the user is active or allow to login. if not, return null to avoid login.
				if (!b2bcustomerModel.getActive() || !customerFacade.canUserLogin(b2bcustomerModel))
				{
					return false;
				}
				//check if the user is locked and login time is more than locking time.
				if (!isCustomerLocked(b2bcustomerModel))
				{
					return false;
				}
			}
			/*
			 * if the user is employ invoke the checkEmployeeUser method to check.
			 */
			if (userModel instanceof EmployeeModel)
			{
				checkEmployeeUser(username, userModel);
			}
		}
		catch (final UnknownIdentifierException e)
		{
			LOG.warn("Brute force attack attempt for non existing user name " + username);
		}
		
		//super.authenticate(authentication);
		return true;
	}
	
	/**
	 * Check whether the customer is locked or the lock time is less than login time.
	 *
	 * @param b2bcustomerModel
	 */
	private boolean isCustomerLocked(final B2BCustomerModel b2bcustomerModel)
	{
		// YTODO Auto-generated method stub
		if (b2bcustomerModel.isLoginDisabled()
				&& (b2bcustomerModel.getLoginDisabledTime() != null && (getUnlockLimitSecond() > 0L)))
		{
			/*
			 * Judge the unlock time, the time must be greater than zero. According the login time and user account locking
			 * time, determine whether can be unlocked
			 */
			final long delta = new Date().getTime() - b2bcustomerModel.getLoginDisabledTime().getTime();
			if (delta / 1000 > getUnlockLimitSecond())
			{
				//update userModel's loginDisabled  is false
				b2bcustomerModel.setLoginDisabled(false);
				b2bcustomerModel.setLoginDisabledTime(null);
				getModelService().save(b2bcustomerModel);
				getModelService().refresh(b2bcustomerModel);
				bruteForceAttackCounter.resetUserCounter(b2bcustomerModel.getUid());
				return true;
			}
			return false;
		}
		return true;
	}

	/**
	 * Check emplyee login user
	 *
	 * @param username
	 * @param userModel
	 */
	private void checkEmployeeUser(final String username, final UserModel userModel)
	{
		if (!customerFacade.isEmployeeUser(userModel))
		{
			throw new BadCredentialsException(messages.getMessage("CoreAuthenticationProvider.badCredentials", "Bad credentials"));
		}

		if (getBruteForceAttackCounter().isAttack(username))
		{
			try
			{
				userModel.setLoginDisabled(true);
				getModelService().save(userModel);
				bruteForceAttackCounter.resetUserCounter(userModel.getUid());
			}
			catch (final UnknownIdentifierException e)
			{
				LOG.warn("Brute force attack attempt for non existing user name " + username);
			}

			throw new BadCredentialsException(messages.getMessage("CoreAuthenticationProvider.badCredentials", "Bad credentials"));
		}
	}
	

	public void setAdminGroup(final String adminGroup)
	{
		if (StringUtils.isBlank(adminGroup))
		{
			adminAuthority = null;
		}
		else
		{
			adminAuthority = new SimpleGrantedAuthority(adminGroup);
		}
	}
	

	protected GrantedAuthority getAdminAuthority()
	{
		return adminAuthority;
	}



	/**
	 * @return service
	 */
	public ApbB2BUnitService getApbB2BUnitService()
	{
		return apbB2BUnitService;
	}

	/**
	 * @param apbB2BUnitService
	 */
	public void setApbB2BUnitService(ApbB2BUnitService apbB2BUnitService)
	{
		this.apbB2BUnitService = apbB2BUnitService;
	}


	protected BruteForceAttackCounter getBruteForceAttackCounter()
	{
		return bruteForceAttackCounter;
	}

	public void setBruteForceAttackCounter(final BruteForceAttackCounter bruteForceAttackCounter)
	{
		this.bruteForceAttackCounter = bruteForceAttackCounter;
	}


	public long getUnlockLimitSecond()
	{
		return unlockLimitSecond;
	}

	public void setUnlockLimitSecond(final long unlockLimitSecond)
	{
		this.unlockLimitSecond = unlockLimitSecond;
	}
}
