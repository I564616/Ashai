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

import de.hybris.platform.core.Constants;
import de.hybris.platform.core.Registry;
import de.hybris.platform.jalo.JaloConnection;
import de.hybris.platform.jalo.JaloSession;
import de.hybris.platform.jalo.JaloInvalidParameterException;
import de.hybris.platform.jalo.user.User;
import de.hybris.platform.jalo.user.UserManager;
import de.hybris.platform.spring.security.CoreAuthenticationProvider;
import de.hybris.platform.spring.security.CoreUserDetails;

import java.util.Collections;

import jakarta.annotation.Resource;

import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.security.authentication.AbstractAuthenticationToken;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.authentication.LockedException;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UsernameNotFoundException;

import com.sabmiller.facades.customer.SABMCustomerFacade;


/**
 *
 * @author xiaowu.a.zhang
 * @date 07/06/2016
 *
 *       Derived authentication provider supporting additional authentication checks. See
 *       {@link de.hybris.platform.spring.security.RejectUserPreAuthenticationChecks}.
 *
 *       <ul>
 *       <li>prevent login as user in group admingroup</li>
 *       </ul>
 *
 *       any login as admin disables SearchRestrictions and therefore no page can be viewed correctly
 * 
 *       impersonate a user will skip the check of password. SABMC-1101
 *
 */
public class ImpersonateAuthenticationProvider extends CoreAuthenticationProvider
{
	private static final Logger LOG = LoggerFactory.getLogger(ImpersonateAuthenticationProvider.class.getName());

	private static final String ROLE_ADMIN_GROUP = "ROLE_" + Constants.USER.ADMIN_USERGROUP.toUpperCase();

	private GrantedAuthority adminAuthority = new SimpleGrantedAuthority(ROLE_ADMIN_GROUP);
	@Resource(name = "acceleratorAuthenticationProvider")
	protected AcceleratorAuthenticationProvider acceleratorAuthenticationProvider;
	
	@SuppressWarnings("boxing")
	@Override
	public Authentication authenticate(final Authentication authentication) throws AuthenticationException
	{
		if ((Registry.hasCurrentTenant()) && (JaloConnection.getInstance().isSystemInitialized()))
		{
			if(!acceleratorAuthenticationProvider.preAuthentication(authentication)){
				return null;
			}
			final String username = (authentication.getPrincipal() == null) ? "NONE_PROVIDED" : authentication.getName();

			UserDetails userDetails = null;

			try
			{
				userDetails = retrieveUser(username);
			}
			catch (final UsernameNotFoundException notFound)
			{
				throw new BadCredentialsException(
						this.messages.getMessage("CoreAuthenticationProvider.badCredentials", "Bad credentials"), notFound);
			}

			getPreAuthenticationChecks().check(userDetails);

			final User user = UserManager.getInstance().getUserByLogin(userDetails.getUsername());

			additionalAuthenticationChecks(userDetails, (AbstractAuthenticationToken) authentication);
			
			boolean employeeGroup=false;
			boolean paGroup=false;
			for(GrantedAuthority grantedAuthority : userDetails.getAuthorities())
			{
				if(grantedAuthority.getAuthority().equals("ROLE_EMPLOYEEGROUP"))
				{
					employeeGroup = true;					
				}
				if(grantedAuthority.getAuthority().equals("ROLE_PAGROUP"))
				{
					paGroup = true;					
				}
				if(employeeGroup && paGroup){
					break;
				}
			}
			if(paGroup && employeeGroup)
			{
				setUserForImpersonated(user);				
			}else
			{
				JaloSession.getCurrentSession().setUser(user);
			}
			

			if (LOG.isDebugEnabled())
			{
				LOG.info("Authenticate the user:" + username + ", set the user to session");
			}
			return createSuccessAuthentication(authentication, userDetails);
		}
		return createSuccessAuthentication(authentication, new CoreUserDetails("systemNotInitialized", "systemNotInitialized", true,
				false, true, true, Collections.emptyList(), null));

	}
	
	/**
	 * While change the Impersonate set user without cart
	 *      
	 */
	private void setUserForImpersonated(User user)
   {
       if (user == null)
       {
             throw new JaloInvalidParameterException("session user cannot be null", 0);
       }
      if (!user.equals(JaloSession.getCurrentSession().getUser()))
      {
         //User previous = user;            
         JaloSession.getCurrentSession().getSessionContext().setUser(user);        
      }
   }

	/**
	 * @see de.hybris.platform.spring.security.CoreAuthenticationProvider#additionalAuthenticationChecks(org.springframework.security.core.userdetails.UserDetails,
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

}
