/**
 *
 */
package com.apb.core.event;

/**
 * @author GQ485VQ
 *
 */
import de.hybris.platform.acceleratorservices.site.AbstractAcceleratorSiteEventListener;
import de.hybris.platform.basecommerce.model.site.BaseSiteModel;
import de.hybris.platform.commerceservices.enums.SiteChannel;
import de.hybris.platform.core.model.user.UserModel;
import de.hybris.platform.processengine.BusinessProcessService;
import de.hybris.platform.servicelayer.model.ModelService;
import de.hybris.platform.servicelayer.util.ServicesUtil;

import java.util.Arrays;

import jakarta.annotation.Resource;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.apb.core.services.ApbCustomerAccountService;
import com.sabm.core.model.AsahiDealChangeEmailProcessModel;


/**
 * Listener for order confirmation events.
 */
public class AsahiDealsChangeEventListener extends AbstractAcceleratorSiteEventListener<AsahiDealsChangeEvent>
{

	private ModelService modelService;
	private BusinessProcessService businessProcessService;
	private static final Logger LOG = LoggerFactory.getLogger(AsahiDealsChangeEventListener.class);
	@Resource(name = "customerAccountService")
	private ApbCustomerAccountService customerAccountService;

	protected BusinessProcessService getBusinessProcessService()
	{
		return businessProcessService;
	}

	public void setBusinessProcessService(final BusinessProcessService businessProcessService)
	{
		this.businessProcessService = businessProcessService;
	}

	protected ModelService getModelService()
	{
		return modelService;
	}

	public void setModelService(final ModelService modelService)
	{
		this.modelService = modelService;
	}

	/** The Method will create the process for Order Confirmation.
	 * @param asahiOrderPlacedEvent
	 */
	@Override
	protected void onSiteEvent(final AsahiDealsChangeEvent  asahiDealsChangeEvent)
	{

		asahiDealsChangeEvent.getCustomerEmailIds().forEach(customer -> {
			final AsahiDealChangeEmailProcessModel asahiDealChangeEmailProcessModel = (AsahiDealChangeEmailProcessModel) getBusinessProcessService()
					.createProcess("asahiDealsChangeNotificationEmailProcess-repdeals-" + System.currentTimeMillis(),
							"asahiDealsChangeNotificationEmailProcess");
			try
			{
				final UserModel userModel = customerAccountService.getUserByUid(customer);
				if (null != userModel)
				{
					asahiDealChangeEmailProcessModel.setUserDisplayName(userModel.getName());
				}
			}
			catch (final Exception exception)

			{
				LOG.info("user doesnt exist " + customer);
			}
			asahiDealChangeEmailProcessModel.setToEmails(Arrays.asList(customer));
			asahiDealChangeEmailProcessModel.setActivatedDeals(asahiDealsChangeEvent.getActivatedDeals());
			asahiDealChangeEmailProcessModel.setRemovedDeals(asahiDealsChangeEvent.getRemovedDeals());
			asahiDealChangeEmailProcessModel.setAdditionalDealDetails(asahiDealsChangeEvent.getAdditionalDealDetails());
			asahiDealChangeEmailProcessModel.setSite(asahiDealsChangeEvent.getSite());
			asahiDealChangeEmailProcessModel.setStore(asahiDealsChangeEvent.getBaseStore());
			asahiDealChangeEmailProcessModel.setLanguage(asahiDealsChangeEvent.getLanguage());
			asahiDealChangeEmailProcessModel.setCurrency(asahiDealsChangeEvent.getCurrency());
			getModelService().save(asahiDealChangeEmailProcessModel);
			getBusinessProcessService().startProcess(asahiDealChangeEmailProcessModel);
		});
	}

	@Override
	protected SiteChannel getSiteChannelForEvent(final AsahiDealsChangeEvent event)
	{
		final BaseSiteModel site = event.getSite();
		ServicesUtil.validateParameterNotNullStandardMessage("event.order.site", site);
		return site.getChannel();
	}
}
