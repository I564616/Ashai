package com.apb.core.services;

import de.hybris.platform.b2b.model.B2BCustomerModel;
import de.hybris.platform.core.model.user.UserModel;
import de.hybris.platform.jalo.JaloItemNotFoundException;
import de.hybris.platform.jalo.JaloSession;
import de.hybris.platform.jalo.security.PrincipalGroup;
import de.hybris.platform.jalo.user.User;
import de.hybris.platform.jalo.user.UserManager;
import de.hybris.platform.servicelayer.user.UserService;
import de.hybris.platform.spring.security.CoreUserDetails;
import de.hybris.platform.spring.security.CoreUserDetailsService;
import de.hybris.platform.store.services.BaseStoreService;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Iterator;
import java.util.Set;
import com.sabmiller.core.constants.SabmCoreConstants;

import jakarta.annotation.Resource;

import org.apache.commons.collections4.CollectionUtils;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UsernameNotFoundException;

import com.apb.core.constants.ApbCoreConstants;
import com.sabmiller.core.model.AsahiB2BUnitModel;
import com.apb.core.util.AsahiSiteUtil;

/**
 * Core user details service to be used during spring security login details provider
 */
public class AsahiCoreUserDetailsService extends CoreUserDetailsService
{
	@Resource
	private UserService userService;
	
	@Resource(name = "baseStoreService")
	protected BaseStoreService baseStoreService;
	
	@Resource
	private AsahiSiteUtil asahiSiteUtil;
	
	private String rolePrefix = "ROLE_";


	@Override
	public CoreUserDetails loadUserByUsername(String username) {
		
		if(!asahiSiteUtil.isCub())
		{
   		if (username == null) {
   			return null;
   		} else {
   			User user;
   			try {
   				user = UserManager.getInstance().getUserByLogin(username.toLowerCase());
   			} catch (JaloItemNotFoundException arg5) {
   				throw new UsernameNotFoundException("User not found!");
   			}
   
   			boolean enabled = this.isNotAnonymousOrAnonymousLoginIsAllowed(user) && !user.isLoginDisabledAsPrimitive();
   			String password = user.getEncodedPassword(JaloSession.getCurrentSession().getSessionContext());
   			if (password == null) {
   				password = "";
   			}
   
   			final CoreUserDetails userDetails = new CoreUserDetails(user.getLogin(), password, enabled, true, true, true,
   					this.getAuthorities(user),
   					JaloSession.getCurrentSession().getSessionContext().getLanguage().getIsoCode());
   			return userDetails;
   		}
		}
		else
		{
			return super.loadUserByUsername(username);
		}
	}
	
	/**
	 * getting user's current associated authorities
	 * @param user
	 * @return list
	 */
	private Collection<GrantedAuthority> getAuthorities(User user) {
		final Set groups = user.getGroups();
		final ArrayList authorities = new ArrayList(groups.size());
		
		final UserModel userModel = userService.getUser(user.getUid());
		if(userModel instanceof B2BCustomerModel) {
			final B2BCustomerModel b2bCust = (B2BCustomerModel) userService.getUser(user.getUid());
			/*
			 * Adding admin group to current user if associated unit allows
			 */
			if(CollectionUtils.isNotEmpty(b2bCust.getAdminUnits()) && 
					(null!=b2bCust.getAdminUnits().stream().filter(unit -> 
						unit.getUid().equalsIgnoreCase(b2bCust.getDefaultB2BUnit().getUid())).findFirst().orElse(null))) {
				authorities.add(new SimpleGrantedAuthority(rolePrefix + ApbCoreConstants.B2B_ADMIN_GROUP));
			}		
		}
		Iterator itr = groups.iterator();
		while (itr.hasNext()) {
			final PrincipalGroup group = (PrincipalGroup) itr.next();
			authorities.add(new SimpleGrantedAuthority(rolePrefix + group.getUID().toUpperCase()));
			Iterator arg6 = group.getAllGroups().iterator();

			while (arg6.hasNext()) {
				final PrincipalGroup gr = (PrincipalGroup) arg6.next();
				authorities.add(new SimpleGrantedAuthority(rolePrefix + gr.getUID().toUpperCase()));
			}
		}
		return authorities;
	}
}
