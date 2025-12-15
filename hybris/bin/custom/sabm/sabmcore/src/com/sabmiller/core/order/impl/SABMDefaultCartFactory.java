/**
 *
 */
package com.sabmiller.core.order.impl;

import de.hybris.platform.b2b.model.B2BCustomerModel;
import de.hybris.platform.b2b.model.B2BUnitModel;
import de.hybris.platform.b2b.company.B2BCommerceUnitService;
import de.hybris.platform.commerceservices.delivery.DeliveryService;
import de.hybris.platform.commerceservices.order.impl.CommerceCartFactory;
import de.hybris.platform.core.model.order.CartModel;
import de.hybris.platform.core.model.order.delivery.DeliveryModeModel;
import de.hybris.platform.core.model.user.AddressModel;
import de.hybris.platform.core.model.user.UserModel;
import de.hybris.platform.servicelayer.session.SessionService;
import de.hybris.platform.store.services.BaseStoreService;
import de.hybris.platform.util.Config;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Set;

import jakarta.annotation.Resource;

import org.apache.commons.collections4.CollectionUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.apb.core.util.AsahiSiteUtil;
import com.sabmiller.core.b2b.services.SABMDeliveryDateCutOffService;
import com.sabmiller.core.constants.SabmCoreConstants;
import com.sabmiller.core.enums.OrderSimulationStatus;
import com.sabmiller.core.model.DeliveryDefaultAddressModel;


/**
 * SABMDefaultCartFactory.
 */
public class SABMDefaultCartFactory extends CommerceCartFactory
{

	/** The Constant LOG. */
	private static final Logger LOG = LoggerFactory.getLogger(SABMDefaultCartFactory.class);

	/** The delivery service. */
	private DeliveryService deliveryService;

	/** The b2b commerce unit service. */
	private B2BCommerceUnitService b2bCommerceUnitService;

	/** The delivery date cut off service. */
	@Resource(name = "sabmDeliveryDateCutOffService")
	private SABMDeliveryDateCutOffService deliveryDateCutOffService;

	/** The session service. */
	@Resource(name = "sessionService")
	private SessionService sessionService;
	
	@Resource(name = "baseStoreService")
	protected BaseStoreService baseStoreService;
	
	@Resource
	private AsahiSiteUtil asahiSiteUtil;


	/**
	 * Creates a new {@link CartModel} instance without persisting it.
	 *
	 * @return {@link CartModel} - a fully initialized, not persisted {@link CartModel} instance
	 */
	@Override
	protected CartModel createCartInternal()
	{
		if (!asahiSiteUtil.isCub())
		{
			return super.createCartInternal();
		}
		final CartModel cart = super.createCartInternal();
		final DeliveryModeModel deliveryModeModel = getDeliveryService()
				.getDeliveryModeForCode(Config.getString(SabmCoreConstants.DEFAULT_DELIVERY_MODE, ""));
		if (deliveryModeModel != null)
		{
			cart.setDeliveryMode(deliveryModeModel);
		}
		final B2BUnitModel b2bUnit = getB2bCommerceUnitService().getParentUnit();
		cart.setUnit(b2bUnit);

		// set the cart delivery address
		final UserModel user = cart.getUser();
		if (null != user && user instanceof B2BCustomerModel)
		{
			final B2BCustomerModel b2bCustomer = (B2BCustomerModel) user;
			updateDeliveryAddress(cart, b2bUnit, b2bCustomer);
		}


		if (b2bUnit.getDefaultCarrier() != null)
		{
			cart.setDeliveryShippingCarrier(b2bUnit.getDefaultCarrier());
		}
		cart.setOrderSimulationStatus(OrderSimulationStatus.NEED_CALCULATION);

		final Date sessionDate = sessionService.getAttribute(SabmCoreConstants.SESSION_ATTR_DELIVERY_DATE);

		cart.setRequestedDeliveryDate(
				sessionDate != null ? sessionDate : deliveryDateCutOffService.getSafeNextAvailableDeliveryDate());

		LOG.debug("In createCartInternal(). The B2BUnit Id is {} ", b2bUnit.getUid());

		return cart;
	}



	/**
	 * Update the cart delivery address.
	 *
	 * @param cart
	 *           the cart
	 * @param b2bUnit
	 *           the b2b unit
	 * @param b2bCustomer
	 *           the b2b customer
	 */
	private void updateDeliveryAddress(final CartModel cart, final B2BUnitModel b2bUnit, final B2BCustomerModel b2bCustomer)
	{
		final Set<DeliveryDefaultAddressModel> defaultAddress = b2bCustomer.getDefaultAddresses();
		if (CollectionUtils.isNotEmpty(defaultAddress))
		{
			for (final DeliveryDefaultAddressModel deliveryDefaultAddress : defaultAddress)
			{
				if (b2bUnit.equals(deliveryDefaultAddress.getB2bUnit()))
				{
					cart.setDeliveryAddress(deliveryDefaultAddress.getAddress());
				}
			}
		}

		if (null == cart.getDeliveryAddress())
		{
			final AddressModel defaultShipTo = b2bUnit.getDefaultShipTo();
			if (null != defaultShipTo)
			{
				cart.setDeliveryAddress(defaultShipTo);
			}
			else
			{
				final List<AddressModel> address = new ArrayList<AddressModel>();
				for (final AddressModel addressModel : b2bUnit.getAddresses())
				{
					if (Boolean.TRUE.equals(addressModel.getShippingAddress()))
					{
						address.add(addressModel);
					}
				}
				//final List<AddressModel> address = Lists.newArrayList(b2bUnit.getAddresses());
				cart.setDeliveryAddress(CollectionUtils.isNotEmpty(address) ? address.get(0) : null);
			}


		}


	}



	/**
	 * Gets the delivery service.
	 *
	 * @return the deliveryService
	 */
	public DeliveryService getDeliveryService()
	{
		return deliveryService;
	}

	/**
	 * Sets the delivery service.
	 *
	 * @param deliveryService
	 *           the deliveryService to set
	 */
	public void setDeliveryService(final DeliveryService deliveryService)
	{
		this.deliveryService = deliveryService;
	}




	/**
	 * Gets the b2b commerce unit service.
	 *
	 * @return the b2bCommerceUnitService
	 */
	public B2BCommerceUnitService getB2bCommerceUnitService()
	{
		return b2bCommerceUnitService;
	}




	/**
	 * Sets the b2b commerce unit service.
	 *
	 * @param b2bCommerceUnitService
	 *           the b2bCommerceUnitService to set
	 */
	public void setB2bCommerceUnitService(final B2BCommerceUnitService b2bCommerceUnitService)
	{
		this.b2bCommerceUnitService = b2bCommerceUnitService;
	}
}