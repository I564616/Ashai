/**
 *
 */
package com.sabmiller.core.strategy.impl;

import static de.hybris.platform.servicelayer.util.ServicesUtil.validateParameterNotNull;

import de.hybris.platform.b2b.model.B2BUnitModel;
import de.hybris.platform.b2b.company.B2BCommerceUnitService;
import de.hybris.platform.commerceservices.delivery.DeliveryService;
import de.hybris.platform.commerceservices.order.impl.DefaultCommerceDeliveryAddressStrategy;
import de.hybris.platform.commerceservices.service.data.CommerceCheckoutParameter;
import de.hybris.platform.core.model.order.CartModel;
import de.hybris.platform.core.model.user.AddressModel;
import de.hybris.platform.core.model.user.UserModel;
import de.hybris.platform.store.services.BaseStoreService;
import com.apb.core.util.AsahiSiteUtil;

import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import jakarta.annotation.Resource;

import com.sabmiller.core.constants.SabmCoreConstants;


/**
 * DefaultSABMCommerceDeliveryAddressStrategy
 *
 * @author yaopeng
 *
 */
public class DefaultSABMCommerceDeliveryAddressStrategy extends DefaultCommerceDeliveryAddressStrategy
{
	/** The Constant LOG. */
	protected static final Logger LOG = LoggerFactory.getLogger(DefaultSABMCommerceDeliveryAddressStrategy.class);

	/** The unit service. */
	private B2BCommerceUnitService b2bCommerceUnitService;
	
	@Resource(name="deliveryService")
	private DeliveryService sabmDeliveryService;
	
	@Resource
	private AsahiSiteUtil asahiSiteUtil;
	

	@Override
	public boolean storeDeliveryAddress(final CommerceCheckoutParameter parameter)
	{
		final CartModel cartModel = parameter.getCart();
		final AddressModel addressModel = parameter.getAddress();
		final boolean flagAsDeliveryAddress = parameter.isIsDeliveryAddress();
		
		if(!asahiSiteUtil.isCub())
		{
			validateParameterNotNull(cartModel, "Cart model cannot be null");
			getModelService().refresh(cartModel);

			final UserModel user = cartModel.getUser();
			getModelService().refresh(user);

			cartModel.setDeliveryAddress(addressModel);
			cartModel.setDeliveryInstruction(addressModel.getEclDeliveryInstruction());

			// Check that the address model belongs to the same user as the cart
			if (isValidDeliveryAddress(cartModel, addressModel))
			{
				getModelService().save(cartModel);

				if (addressModel != null && flagAsDeliveryAddress && !Boolean.TRUE.equals(addressModel.getShippingAddress()))
				{
					// Flag the address as a delivery address
					addressModel.setShippingAddress(Boolean.TRUE);
					getModelService().save(addressModel);
				}
				//getCommerceCartCalculationStrategy().calculateCart(cartModel);
				// verify if the current delivery mode is still valid for this address
				//getCommerceDeliveryModeValidationStrategy().validateDeliveryMode(parameter);
				getModelService().refresh(cartModel);

				return true;
			}

			return false;
		}
		
		else
		{

		validateParameterNotNull(cartModel, "Cart model cannot be null");
		cartModel.setDeliveryAddress(addressModel);

		// Check that the address model belongs to the same user as the cart
		if (isValidDeliveryAddress(cartModel, addressModel))
		{
			getModelService().save(cartModel);

			if (addressModel != null && flagAsDeliveryAddress && !Boolean.TRUE.equals(addressModel.getShippingAddress()))
			{
				// Flag the address as a delivery address
				addressModel.setShippingAddress(Boolean.TRUE);
				getModelService().save(addressModel);
			}
			// verify if the current delivery mode is still valid for this address
			getCommerceDeliveryModeValidationStrategy().validateDeliveryMode(parameter);
			getModelService().refresh(cartModel);

			return true;
		}

		return false;
		}
	}

	/*
	 * ValidDeliveryAddress add b2bUnit validate of cartModel
	 *
	 * @see
	 * de.hybris.platform.commerceservices.order.impl.DefaultCommerceDeliveryAddressStrategy#isValidDeliveryAddress(de.
	 * hybris.platform.core.model.order.CartModel, de.hybris.platform.core.model.user.AddressModel)
	 */
	@Override
	protected boolean isValidDeliveryAddress(final CartModel cartModel, final AddressModel addressModel)
	{
		if(!asahiSiteUtil.isCub())
		{
			return super.isValidDeliveryAddress(cartModel, addressModel);
		}
		else
		{
		if (addressModel != null)
		{
			final List<AddressModel> supportedAddresses = getDeliveryService().getSupportedDeliveryAddressesForOrder(cartModel,
					false);
			if (supportedAddresses != null && supportedAddresses.contains(addressModel))
			{
				return true;
			}
			final B2BUnitModel b2bUnitModel = b2bCommerceUnitService.getParentUnit();
			LOG.info("ValidDeliveryAddress from b2bUnit the CarModel: {}", cartModel);

			if (null != b2bUnitModel && null != b2bUnitModel.getShippingAddresses())
			{
				return b2bUnitModel.getShippingAddresses().contains(addressModel);
			}
		}
		else
		{
			return true;
		}
		LOG.warn("Can't correct the deliveryAddress  the CarModel: {}", cartModel);
		return false;
		}
	}

	/**
	 * @return the b2bCommerceUnitService
	 */
	public B2BCommerceUnitService getB2bCommerceUnitService()
	{
		return b2bCommerceUnitService;
	}

	/**
	 * @param b2bCommerceUnitService
	 *           the b2bCommerceUnitService to set
	 */
	public void setB2bCommerceUnitService(final B2BCommerceUnitService b2bCommerceUnitService)
	{
		this.b2bCommerceUnitService = b2bCommerceUnitService;
	}
	
}
