/**
 *
 */
package com.sabmiller.core.businessenquiry.impl;

import de.hybris.platform.core.model.user.CustomerModel;
import de.hybris.platform.servicelayer.config.ConfigurationService;
import de.hybris.platform.servicelayer.event.EventService;
import de.hybris.platform.servicelayer.i18n.CommonI18NService;
import de.hybris.platform.servicelayer.user.UserService;
import de.hybris.platform.site.BaseSiteService;
import de.hybris.platform.store.services.BaseStoreService;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.Map;
import java.util.Optional;
import jakarta.annotation.Resource;
import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;
import java.util.HashMap;

import jakarta.annotation.Resource;

import org.apache.commons.lang3.StringUtils;
import org.springframework.http.MediaType;

import com.sabmiller.core.businessenquiry.BusinessEnquiryService;
import com.sabmiller.core.constants.SabmCoreConstants;
import com.sabmiller.core.event.BusinessEnquiryEmailEvent;
import com.sabmiller.facades.businessenquiry.data.AbstractBusinessEnquiryData;
import com.sabmiller.facades.businessenquiry.data.SabmKegIssueData;

import com.sabmiller.sfmc.service.SabmSFMCService;
import com.sabmiller.sfmc.pojo.SFCompositeResponse;


/**
 *
 */
public class BusinessEnquiryServiceImpl implements BusinessEnquiryService
{
	private EventService eventService;
	private BaseSiteService baseSiteService;
	private UserService userService;
	private CommonI18NService commonI18NService;
	private BaseStoreService baseStoreService;
	private ConfigurationService configurationService;

	@Resource(name = "sabmSFMCService")
	private SabmSFMCService sabmSFMCService;

	@Override
	public boolean sendEmail(final AbstractBusinessEnquiryData enquiry)
	{
		getEventService().publishEvent(initializeEvent(new BusinessEnquiryEmailEvent(), enquiry));
		return false;
	}

	protected BusinessEnquiryEmailEvent initializeEvent(final BusinessEnquiryEmailEvent event,
			final AbstractBusinessEnquiryData enquiry)
	{
		event.setRequestType(enquiry.getRequestType());
		event.setEnquiry(enquiry);
		event.setToEmails(retrieveToEmails(enquiry.getRequestType()));
		event.setCcEmails(retrieveCcEmails(enquiry));

		event.setSite(getBaseSiteService().getBaseSiteForUID("sabmStore"));
		event.setCustomer((CustomerModel) getUserService().getCurrentUser());
		event.setLanguage(getCommonI18NService().getLanguage("en"));
		event.setCurrency(getCommonI18NService().getCurrency("AUD"));
		event.setBaseStore(getBaseStoreService().getBaseStoreForUid("sabmStore"));

		return event;
	}

	private List<String> retrieveToEmails(final String requestType)
	{
		final List<String> toEmails = new ArrayList<String>();

		if (requestType.equals(SabmCoreConstants.BUSINESS_ENQUIRY_PICKUP))
		{
			toEmails.add(getConfigurationService().getConfiguration().getString(
					SabmCoreConstants.BUSINESS_ENQUIRY_TO_EMAIL_PROP_PREFIX + requestType,
					SabmCoreConstants.BUSINESS_ENQUIRY_KEG_PICKUP_TO_EMAIL_DEFAULT));
		}
		else
		{
			// SABMC-630: retrieve to email address defined per enquiry type
			toEmails.add(getConfigurationService().getConfiguration().getString(
					SabmCoreConstants.BUSINESS_ENQUIRY_TO_EMAIL_PROP_PREFIX + requestType,
					SabmCoreConstants.BUSINESS_ENQUIRY_TO_EMAIL_DEFAULT));

		}
		return toEmails;
	}

	private List<String> retrieveCcEmails(final AbstractBusinessEnquiryData enquiry)
	{
		final List<String> ccEmails = new ArrayList<String>();

		// SABMC-630: User is part of CC list
		if (baseSiteService.getCurrentBaseSite() != null
				&& SabmCoreConstants.CUB_STORE.equalsIgnoreCase(baseSiteService.getCurrentBaseSite().getUid()))
		{
			if(enquiry.getPhoneNumber()!=null && enquiry.getPreferredContactMethod().equalsIgnoreCase("Phone"))
			{
			  ccEmails.add(getUserService().getCurrentUser().getUid());
			}
		} else{
			ccEmails.add(getUserService().getCurrentUser().getUid());
		}
		if (Objects.nonNull(enquiry) && StringUtils.isNotEmpty(enquiry.getEmailAddress()))
		{
			ccEmails.add(enquiry.getEmailAddress());
		}
		return ccEmails;
	}

	/**
	 * @return the eventService
	 */
	public EventService getEventService()
	{
		return eventService;
	}

	/**
	 * @param eventService
	 *           the eventService to set
	 */
	public void setEventService(final EventService eventService)
	{
		this.eventService = eventService;
	}

	/**
	 * @return the baseSiteService
	 */
	public BaseSiteService getBaseSiteService()
	{
		return baseSiteService;
	}

	/**
	 * @param baseSiteService
	 *           the baseSiteService to set
	 */
	public void setBaseSiteService(final BaseSiteService baseSiteService)
	{
		this.baseSiteService = baseSiteService;
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
	 * @return the commonI18NService
	 */
	public CommonI18NService getCommonI18NService()
	{
		return commonI18NService;
	}

	/**
	 * @param commonI18NService
	 *           the commonI18NService to set
	 */
	public void setCommonI18NService(final CommonI18NService commonI18NService)
	{
		this.commonI18NService = commonI18NService;
	}

	/**
	 * @return the baseStoreService
	 */
	public BaseStoreService getBaseStoreService()
	{
		return baseStoreService;
	}

	/**
	 * @param baseStoreService
	 *           the baseStoreService to set
	 */
	public void setBaseStoreService(final BaseStoreService baseStoreService)
	{
		this.baseStoreService = baseStoreService;
	}

	/**
	 * @return the configurationService
	 */
	public ConfigurationService getConfigurationService()
	{
		return configurationService;
	}

	/**
	 * @param configurationService
	 *           the configurationService to set
	 */
	public void setConfigurationService(final ConfigurationService configurationService)
	{
		this.configurationService = configurationService;
	}

	/**
	 * HybrisSVOC WebToCase Implementation
	 * @sabmKegIssueData Data entered by user in the WebToCase form
	 */
	public SFCompositeResponse createKegIssueWithSalesforce(SabmKegIssueData sabmKegIssueData)
	{
		return sabmSFMCService.sendKegIssueRequest(sabmKegIssueData);
	}

}
