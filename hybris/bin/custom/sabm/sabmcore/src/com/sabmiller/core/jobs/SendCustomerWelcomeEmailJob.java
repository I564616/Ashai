package com.sabmiller.core.jobs;

import de.hybris.platform.commerceservices.customer.CustomerAccountService;
import de.hybris.platform.commerceservices.event.AbstractCommerceUserEvent;
import de.hybris.platform.commerceservices.security.SecureToken;
import de.hybris.platform.commerceservices.security.SecureTokenService;
import de.hybris.platform.core.model.user.CustomerModel;
import de.hybris.platform.cronjob.enums.CronJobResult;
import de.hybris.platform.cronjob.enums.CronJobStatus;
import de.hybris.platform.cronjob.model.CronJobModel;
import de.hybris.platform.processengine.BusinessProcessService;
import de.hybris.platform.servicelayer.cronjob.AbstractJobPerformable;
import de.hybris.platform.servicelayer.cronjob.PerformResult;
import de.hybris.platform.servicelayer.event.EventService;
import de.hybris.platform.servicelayer.i18n.CommonI18NService;
import de.hybris.platform.servicelayer.model.ModelService;
import de.hybris.platform.servicelayer.search.FlexibleSearchQuery;
import de.hybris.platform.servicelayer.search.SearchResult;
import de.hybris.platform.site.BaseSiteService;
import de.hybris.platform.store.services.BaseStoreService;

import java.util.Date;
import java.util.List;

import jakarta.annotation.Resource;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.sabmiller.core.b2b.services.SabmB2BUnitService;
import com.sabmiller.core.event.WelcomeEmailEvent;


public class SendCustomerWelcomeEmailJob extends AbstractJobPerformable<CronJobModel>
{
	@SuppressWarnings("unused")
	private static final Logger LOG = LoggerFactory.getLogger(SendCustomerWelcomeEmailJob.class);

	private BusinessProcessService businessProcessService;
	private EventService eventService;
	private BaseStoreService baseStoreService;
	private BaseSiteService baseSiteService;
	private CommonI18NService commonI18NService;
	private long tokenValiditySeconds;
	private SecureTokenService secureTokenService;
	private CustomerAccountService customerAccountService;
	private ModelService modelService;

	@Resource(name = "b2bUnitService")
	private SabmB2BUnitService b2bUnitService;

	protected BusinessProcessService getBusinessProcessService()
	{
		return businessProcessService;
	}

	public void setBusinessProcessService(final BusinessProcessService businessProcessService)
	{
		this.businessProcessService = businessProcessService;
	}

	@Override
	public PerformResult perform(final CronJobModel cronJob)
	{

		for (final CustomerModel customerModel : getAllCustomerToSendWelcomeEmail())
		{
			final long timeStamp = getTokenValiditySeconds() > 0L ? getTokenValiditySeconds() : 31536000000L;
			final SecureToken data = new SecureToken(customerModel.getUid(), System.currentTimeMillis() + timeStamp);
			final String token = getSecureTokenService().encryptData(data);
			customerModel.setToken(token);
			customerModel.setWelcomeEmailStatus(true);
			customerModel.setWelcomeEmailSentDate(new Date());
			modelService.save(customerModel);
			// update the status for the customer's b2bunit
			b2bUnitService.updateB2BUnitStatus(customerModel, Boolean.TRUE, Boolean.FALSE);
			getEventService().publishEvent(initializeEvent(new WelcomeEmailEvent(customerModel.getToken()), customerModel));
		}
		LOG.info("SendCustomerWelcomeEmailJob performed");
		return new PerformResult(CronJobResult.SUCCESS, CronJobStatus.FINISHED);
	}

	protected List<CustomerModel> getAllCustomerToSendWelcomeEmail()
	{
		final String query = "select {cus.PK} " + "from {Customer AS cus} "
				+ "WHERE {cus.onboardWithWelcomeEmail} = ?onboardWithWelcomeEmail and {cus.welcomeEmailStatus} = ?welcomeEmailStatus";

		final FlexibleSearchQuery searchQuery = new FlexibleSearchQuery(query);
		searchQuery.addQueryParameter("onboardWithWelcomeEmail", Boolean.TRUE);
		searchQuery.addQueryParameter("welcomeEmailStatus", Boolean.FALSE);
		final SearchResult<CustomerModel> processes = flexibleSearchService.search(searchQuery);
		return processes.getResult();
	}


	protected AbstractCommerceUserEvent initializeEvent(final AbstractCommerceUserEvent event, final CustomerModel customerModel)
	{
		event.setBaseStore(getBaseStoreService().getBaseStoreForUid("sabmStore"));
		event.setSite(getBaseSiteService().getBaseSiteForUID("sabmStore"));
		event.setCustomer(customerModel);
		event.setLanguage(getCommonI18NService().getLanguage("en"));
		event.setCurrency(getCommonI18NService().getCurrency("AUD"));
		return event;
	}

	/**
	 * @return the secureTokenService
	 */
	protected SecureTokenService getSecureTokenService()
	{
		return secureTokenService;
	}

	/**
	 * @param secureTokenService
	 *           the secureTokenService to set
	 */
	public void setSecureTokenService(final SecureTokenService secureTokenService)
	{
		this.secureTokenService = secureTokenService;
	}

	/**
	 * @return the tokenValiditySeconds
	 */
	protected long getTokenValiditySeconds()
	{
		return tokenValiditySeconds;
	}

	/**
	 * @param tokenValiditySeconds
	 *           the tokenValiditySeconds to set
	 */
	public void setTokenValiditySeconds(final long tokenValiditySeconds)
	{
		if (tokenValiditySeconds < 0)
		{
			throw new IllegalArgumentException("tokenValiditySeconds has to be >= 0");
		}
		this.tokenValiditySeconds = tokenValiditySeconds;
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
	 * @return the baseSiteService
	 */
	public BaseSiteService getBaseSiteService()
	{
		return baseSiteService;
	}

	/**
	 * @return the commonI18NService
	 */
	public CommonI18NService getCommonI18NService()
	{
		return commonI18NService;
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
	 * @param commonI18NService
	 *           the commonI18NService to set
	 */
	public void setCommonI18NService(final CommonI18NService commonI18NService)
	{
		this.commonI18NService = commonI18NService;
	}

	/**
	 * @return the customerAccountService
	 */
	public CustomerAccountService getCustomerAccountService()
	{
		return customerAccountService;
	}

	/**
	 * @param customerAccountService
	 *           the customerAccountService to set
	 */
	public void setCustomerAccountService(final CustomerAccountService customerAccountService)
	{
		this.customerAccountService = customerAccountService;
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
	@Override
	public void setModelService(final ModelService modelService)
	{
		this.modelService = modelService;
	}
}