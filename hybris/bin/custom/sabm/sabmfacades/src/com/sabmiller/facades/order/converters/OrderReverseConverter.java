/**
 *
 */
package com.sabmiller.facades.order.converters;

import de.hybris.platform.b2b.model.B2BUnitModel;
import de.hybris.platform.commercefacades.order.data.OrderData;
import de.hybris.platform.commerceservices.enums.SalesApplication;
import de.hybris.platform.converters.Populator;
import de.hybris.platform.core.model.order.OrderModel;
import de.hybris.platform.core.model.user.AddressModel;
import de.hybris.platform.core.model.user.UserModel;
import de.hybris.platform.servicelayer.dto.converter.ConversionException;
import de.hybris.platform.servicelayer.dto.converter.Converter;
import de.hybris.platform.servicelayer.model.ModelService;
import de.hybris.platform.servicelayer.user.UserService;

import java.util.Date;

import jakarta.annotation.Resource;

import org.apache.commons.collections4.CollectionUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.sabmiller.core.b2b.services.SabmB2BUnitService;
import com.sabmiller.core.order.SabmB2BOrderService;


/**
 * @author joshua.a.antony
 *
 */

public class OrderReverseConverter implements Converter<OrderData, OrderModel>
{
	private static final Logger LOG = LoggerFactory.getLogger(OrderReverseConverter.class.getName());

	@Resource(name = "modelService")
	private ModelService modelService;

	@Resource(name = "b2bOrderService")
	private SabmB2BOrderService b2bOrderService;

	@Resource(name = "b2bUnitService")
	private SabmB2BUnitService b2bUnitService;

	@Resource(name = "orderEntryReversePopulator")
	private Populator<OrderData, OrderModel> orderEntryReversePopulator;

	@Resource(name = "orderBasicReversePopulator")
	private Populator<OrderData, OrderModel> orderBasicReversePopulator;

	@Resource(name = "userService")
	private UserService userService;


	@Override
	public OrderModel convert(final OrderData orderData) throws ConversionException
	{
		return convert(orderData, findOrCreateOrder(orderData.getSapSalesOrderNumber()));
	}



	@Override
	public OrderModel convert(final OrderData source, final OrderModel target) throws ConversionException
	{
		if (LOG.isDebugEnabled())
		{
			LOG.debug("Converting from OrderModel to OrderData.");
		}
		final B2BUnitModel b2bUnitModel = b2bUnitService.getUnitForUid(source.getSoldTo());
		target.setUnit(b2bUnitModel);

		orderBasicReversePopulator.populate(source, target);
		orderEntryReversePopulator.populate(source, target);

		/**
		 * Only if this is a newly created order, set the address and primary admin. If order is already existing, these
		 * attributes need not be set as SAP does not send across these values in the Sales Order Update service
		 */
		if (modelService.isNew(target))
		{
			if (LOG.isDebugEnabled())
			{
				LOG.debug("Order does not exist in Hybris system! This is newly created");
			}

			target.setDeliveryAddress(deriveAddress(source.getShipTo(), b2bUnitModel));

			final UserModel primaryAdmin = b2bUnitService.findPrimaryAdmin(b2bUnitModel.getPayerId());
			target.setUser(primaryAdmin != null ? primaryAdmin : userService.getAnonymousUser());
			if (target.getDate() == null)
			{
				target.setDate(new Date());
			}

			if (primaryAdmin == null)
			{
				LOG.warn(
						"No primary admin found for ZALB : " + b2bUnitModel.getUid() + " , hence setting the user to anonymous user");
			}

			target.setSalesApplication(SalesApplication.SAP);
		}

		modelService.save(target);
		modelService.saveAll(target.getEntries());

		return target;
	}

	private AddressModel deriveAddress(final String shipToId, final B2BUnitModel b2bUnitModel)
	{
		final String b2bUnitId = b2bUnitModel.getUid();
		for (final AddressModel address : CollectionUtils.emptyIfNull(b2bUnitModel.getAddresses()))
		{
			if (address.getPartnerNumber() != null && address.getPartnerNumber().equals(shipToId))
			{
				if (LOG.isDebugEnabled())
				{
					LOG.debug("Found address for shipto {} in B2bUnit {} ", shipToId, b2bUnitId);
				}
				return address;
			}
		}
		final AddressModel defaultShiptoAddress = b2bUnitModel.getDefaultShipTo();
		if (defaultShiptoAddress != null)
		{
			if (LOG.isDebugEnabled())
			{
				LOG.debug("No address found matching shipto. Returning the default shipto {} for the B2BUnit {} ", shipToId,
						b2bUnitId);
			}
			return defaultShiptoAddress;
		}

		if (b2bUnitModel.getAddresses() != null && !b2bUnitModel.getAddresses().isEmpty())
		{
			if (LOG.isDebugEnabled())
			{
				LOG.debug(
						"No address found matching shipto. Also, default ship to does not exist. Returning one of the address from the B2BUnit {} ",
						b2bUnitId);
			}
			return b2bUnitModel.getAddresses().iterator().next();
		}

		LOG.warn("There are no addresses tied to B2BUnit {}, returning null . Data issue in the application??? ", b2bUnitId);
		return null;
	}

	private OrderModel findOrCreateOrder(final String sapSalesOrderNumber)
	{
		if (LOG.isDebugEnabled())
		{
			LOG.debug("Looking up for order with sapSalesOrderNumber : " + sapSalesOrderNumber
					+ ". If the order does not exist, a new OrderModel will be created");
		}
		final OrderModel orderModel = b2bOrderService.getOrderBySapSalesOrderNumber(sapSalesOrderNumber);
		return orderModel != null ? orderModel : modelService.<OrderModel> create(OrderModel.class);
	}
}
