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
import de.hybris.platform.core.model.order.OrderModel;
import de.hybris.platform.core.model.user.UserModel;
import de.hybris.platform.orderprocessing.model.OrderProcessModel;
import de.hybris.platform.processengine.BusinessProcessService;
import de.hybris.platform.servicelayer.exceptions.UnknownIdentifierException;
import de.hybris.platform.servicelayer.model.ModelService;
import de.hybris.platform.servicelayer.search.FlexibleSearchService;
import de.hybris.platform.servicelayer.user.UserService;
import de.hybris.platform.servicelayer.util.ServicesUtil;

import java.util.Arrays;
import java.util.Set;
import java.util.stream.Collectors;

import jakarta.annotation.Resource;

import org.apache.commons.lang3.BooleanUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.apb.core.services.ApbCustomerAccountService;


/**
 * Listener for order confirmation events.
 */
public class AsahiOrderConfirmationEventListener extends AbstractAcceleratorSiteEventListener<AsahiOrderPlacedEvent>
{

	private ModelService modelService;
	private BusinessProcessService businessProcessService;
	private static final Logger LOG = LoggerFactory.getLogger(AsahiOrderConfirmationEventListener.class);
	@Resource
	private UserService userService;
	@Resource
	private FlexibleSearchService flexibleSearchService;
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
	protected void onSiteEvent(final AsahiOrderPlacedEvent asahiOrderPlacedEvent)
	{
		final OrderModel orderModel = asahiOrderPlacedEvent.getOrderModel();
		if (orderModel != null && BooleanUtils.isTrue(orderModel.getBdeOrder()))
		{
			triggerBDEOrderConfirmationEmail(orderModel);
		}
		else
		{
			final OrderProcessModel orderProcessModel = (OrderProcessModel) getBusinessProcessService().createProcess(
					"asahiOrderConfirmationEmailProcess-" + orderModel.getCode() + "-" + System.currentTimeMillis(),
					"asahiOrderConfirmationEmailProcess");
			orderProcessModel.setOrder(orderModel);
			getModelService().save(orderProcessModel);
			getBusinessProcessService().startProcess(orderProcessModel);
		}
	}

	protected void triggerBDEOrderConfirmationEmail(final OrderModel orderModel)
	{
		final Set<String> uniqueEmailIds = orderModel.getBdeOrderUserEmails().stream().collect(Collectors.toSet());
		uniqueEmailIds.forEach(user -> {

			final OrderProcessModel bdeOrderUserEmailProcessModel = (OrderProcessModel) getBusinessProcessService().createProcess(
					"asahiOrderConfirmationEmailProcess-" + orderModel.getCode() + "-bdeusers-" + System.currentTimeMillis(),
					"asahiOrderConfirmationEmailProcess");
			bdeOrderUserEmailProcessModel.setOrder(orderModel);
			bdeOrderUserEmailProcessModel.setToEmails(Arrays.asList(user));
			bdeOrderUserEmailProcessModel.setBdeOrderEmailGroup("bdeusers");
			try {
				final UserModel userModel = userService.getUserForUID(user);
				bdeOrderUserEmailProcessModel.setBdeOrderCustomerFirstName(userModel.getName());
			}
			catch(final UnknownIdentifierException exception) {
				LOG.info("user doesnt exist " + user);
			}
			getModelService().save(bdeOrderUserEmailProcessModel);
			getBusinessProcessService().startProcess(bdeOrderUserEmailProcessModel);
		});

		orderModel.getBdeOrderCustomerEmails().forEach(customer -> {
			final OrderProcessModel bdeOrderCustomerEmailProcessModel = (OrderProcessModel) getBusinessProcessService()
					.createProcess(
							"asahiOrderConfirmationEmailProcess-" + orderModel.getCode() + "-bdecustomers-" + System.currentTimeMillis(),
							"asahiOrderConfirmationEmailProcess");
			bdeOrderCustomerEmailProcessModel.setOrder(orderModel);
			bdeOrderCustomerEmailProcessModel.setToEmails(Arrays.asList(customer));
			bdeOrderCustomerEmailProcessModel.setBdeOrderEmailGroup("customers");
			try {
				final UserModel userModel = customerAccountService.getUserByUid(customer);
				bdeOrderCustomerEmailProcessModel.setBdeOrderCustomerFirstName(userModel.getName());
			}
			catch(final Exception exception) {
				LOG.info("user doesnt exist " + customer);
			}

			getModelService().save(bdeOrderCustomerEmailProcessModel);
			getBusinessProcessService().startProcess(bdeOrderCustomerEmailProcessModel);
		});

	}

	/** The Method will set the site channel for the event
	 * @param asahiOrderPlacedEvent
	 */
	@Override
	protected SiteChannel getSiteChannelForEvent(final AsahiOrderPlacedEvent asahiOrderPlacedEvent)
	{
		final OrderModel order = asahiOrderPlacedEvent.getOrderModel();
		ServicesUtil.validateParameterNotNullStandardMessage("event.order", order);
		final BaseSiteModel site = order.getSite();
		ServicesUtil.validateParameterNotNullStandardMessage("event.order.site", site);
		return site.getChannel();
	}
}
