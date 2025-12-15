package com.apb.controller.security;

import de.hybris.platform.b2b.model.B2BCustomerModel;
import de.hybris.platform.b2b.model.B2BUnitModel;
import de.hybris.platform.b2bacceleratoraddon.security.B2BAcceleratorAuthenticationProvider;
import de.hybris.platform.cms2.servicelayer.services.CMSSiteService;
import de.hybris.platform.core.model.security.PrincipalGroupModel;
import de.hybris.platform.core.model.user.UserModel;
import de.hybris.platform.servicelayer.exceptions.UnknownIdentifierException;
import de.hybris.platform.servicelayer.session.SessionService;

import java.util.List;
import java.util.Map;

import jakarta.annotation.Resource;

import org.apache.commons.lang3.StringUtils;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.authentication.DisabledException;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.AuthenticationException;

import com.sabmiller.core.model.AsahiB2BUnitModel;
import com.apb.core.util.AsahiSiteUtil;
import com.apb.service.b2bunit.ApbB2BUnitService;
import com.apb.storefront.constant.ApbStoreFrontContants;

import org.apache.commons.collections4.CollectionUtils;


/**
 * Authentication provider for the spring security
 */
public class ApbB2BAcceleratorAuthenticationProvider extends B2BAcceleratorAuthenticationProvider
{
	private String targetBeanName;

	private ApbB2BUnitService apbB2BUnitService;

	@Resource
	private SessionService sessionService;

	@Resource
	private AsahiSiteUtil asahiSiteUtil;

	@Resource
	private CMSSiteService cmsSiteService;

	@Override
	public Authentication authenticate(final Authentication authentication) throws AuthenticationException
	{
		final String username = (authentication.getPrincipal() == null) ? "NONE_PROVIDED" : authentication.getName();
		UserModel userModel = null;

		// throw BadCredentialsException if user does not exist
		try
		{
			userModel = getUserService().getUserForUID(StringUtils.lowerCase(username));
		}
		catch (final UnknownIdentifierException e)
		{
			throw new BadCredentialsException(messages.getMessage(CORE_AUTHENTICATION_PROVIDER_BAD_CREDENTIALS, BAD_CREDENTIALS), e);
		}
		if (!checkB2BUnitActiveForUser(userModel))
		{
			throw new DisabledException("B2B Unit for user is inactive therefore user: " + username + " cannot login");
		}

		if (userModel instanceof B2BCustomerModel)
		{
			final B2BCustomerModel custModel = (B2BCustomerModel) userModel;

			final B2BUnitModel defaultB2bUnit = (B2BUnitModel) custModel.getDefaultB2BUnit();
			final Map<String,List<AsahiB2BUnitModel>> activeUnits = apbB2BUnitService.getUserActiveB2BUnits(userModel.getUid());
			final String currentSite = asahiSiteUtil.getCurrentSite().getUid();

			if (CollectionUtils.isEmpty(activeUnits.get(currentSite)))
			{
				/*
				 * if no active b2bunit associated with user, don't let him login
				 */
				throw new DisabledException("No B2BUnit is assigned to the user : " + username);
			}
			
			sessionService.setAttribute(ApbStoreFrontContants.IS_DEFAULT_UNIT_BELONGS_TO_CURRENT_SITE,Boolean.TRUE);
			if (defaultB2bUnit instanceof AsahiB2BUnitModel )
			{				
				/*
				 * SCP-2047 : If default unit is disabled, make next one as default
				 */
				final AsahiB2BUnitModel b2bunitModel = (AsahiB2BUnitModel) defaultB2bUnit;
				if (b2bunitModel.getDisabledUser().contains(userModel.getUid())) {
					
					this.sessionService.setAttribute(ApbStoreFrontContants.DEFAULT_UNIT_DISABLED_FLAG, Boolean.TRUE);
					custModel.setDefaultB2BUnit((B2BUnitModel) activeUnits.get(currentSite).get(0));
					getModelService().save(custModel);
					getModelService().refresh(custModel);
					return super.authenticate(authentication);
				}
			} if(!currentSite.equalsIgnoreCase(defaultB2bUnit.getCompanyUid())) {
				sessionService.setAttribute(ApbStoreFrontContants.IS_DEFAULT_UNIT_BELONGS_TO_CURRENT_SITE,Boolean.FALSE);
				if(activeUnits.get(currentSite).size() >= 1) {
					custModel.setDefaultB2BUnit((B2BUnitModel) activeUnits.get(currentSite).get(0));
	
					getModelService().save(custModel);
					getModelService().refresh(custModel);
				}
			}
		}

		return super.authenticate(authentication);
	}

	/**
	 * check if b2bunit is active
	 *
	 * @param user
	 * @return
	 */
	private boolean checkB2BUnitActiveForUser(final UserModel user)
	{
		if (null != user && user instanceof B2BCustomerModel)
		{
			final B2BUnitModel b2bUnit = ((B2BCustomerModel) user).getDefaultB2BUnit();
			return null != b2bUnit ? b2bUnit.getActive() : false;
		}
		return false;
	}
	/**
	 * @return string
	 */
	public String getTargetBeanName()
	{
		return targetBeanName;
	}

	/**
	 * @param targetBeanName
	 */
	public void setTargetBeanName(final String targetBeanName)
	{
		this.targetBeanName = targetBeanName;
	}

	/**
	 * @return unit
	 */
	public ApbB2BUnitService getApbB2BUnitService()
	{
		return apbB2BUnitService;
	}

	/**
	 * @param apbB2BUnitService
	 */
	public void setApbB2BUnitService(final ApbB2BUnitService apbB2BUnitService)
	{
		this.apbB2BUnitService = apbB2BUnitService;
	}


}
