package com.sabmiller.core.order.impl;

import de.hybris.platform.b2b.model.B2BCustomerModel;
import de.hybris.platform.b2b.model.B2BUnitModel;
import de.hybris.platform.b2b.company.B2BCommerceUnitService;
import de.hybris.platform.commerceservices.order.CommercePlaceOrderStrategy;
import de.hybris.platform.commerceservices.order.impl.DefaultCommerceCheckoutService;
import de.hybris.platform.core.model.order.CartModel;
import de.hybris.platform.core.model.user.AddressModel;
import de.hybris.platform.payment.model.PaymentTransactionEntryModel;
import de.hybris.platform.payment.model.PaymentTransactionModel;
import de.hybris.platform.util.Config;

import java.util.Calendar;
import java.util.HashSet;
import java.util.Set;

import jakarta.annotation.Resource;

import org.apache.commons.collections4.SetUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.apb.core.place.order.strategy.AsahiCommercePlaceOrderStrategy;
import com.sabmiller.core.constants.SabmCoreConstants;
import com.sabmiller.core.model.DeliveryDefaultAddressModel;
import com.sabmiller.core.order.SabmCommerceCheckoutService;


public class SabmCommerceCheckoutServiceImpl extends DefaultCommerceCheckoutService implements SabmCommerceCheckoutService
{
	private static final Logger LOG = LoggerFactory.getLogger(SabmCommerceCheckoutServiceImpl.class.getName());

	@Resource(name = "b2bCommerceUnitService")
	private B2BCommerceUnitService b2bCommerceUnitService;
	
	@Resource(name="asahiCommercePlaceOrderStrategy")
	private AsahiCommercePlaceOrderStrategy asahiCommercePlaceOrderStrategy;

	public void clearPreviousPaymentAttempts(final CartModel cartModel)
	{
		cartModel.setPaymentInfo(null);
		getModelService().save(cartModel);

		for (final PaymentTransactionModel transactionModel : cartModel.getPaymentTransactions())
		{
			for (final PaymentTransactionEntryModel entryModel : transactionModel.getEntries())
			{
				entryModel.setTransactionStatus("Voided-new attempt");
			}
			getModelService().saveAll(transactionModel.getEntries());
		}
	}


	@Override
	public void startCheckoutCountdown(final CartModel cartModel)
	{
		final Calendar cal = Calendar.getInstance();
		cal.add(Calendar.MINUTE, Config.getInt("sabm.checkout.countdown", 5));

		cartModel.setCheckoutCountdown(cal.getTime());
		getModelService().save(cartModel);
	}


	/*
	 * (non-Javadoc)
	 *
	 * @see com.sabmiller.core.order.SabmCommerceCheckoutService#updateDefaultAddress(de.hybris.platform.core.model.user.
	 * AddressModel, de.hybris.platform.b2b.model.B2BCustomerModel)
	 */
	@Override
	public void updateDefaultAddress(final AddressModel addressModel, final B2BCustomerModel b2bCustomer)
	{
		final B2BUnitModel b2bUnit = b2bCommerceUnitService.getParentUnit();
		if (null != b2bUnit)
		{
			final Set<DeliveryDefaultAddressModel> defaultAddresses = new HashSet<DeliveryDefaultAddressModel>(
					SetUtils.emptyIfNull(b2bCustomer.getDefaultAddresses()));
			boolean isSetB2BUnitAddress = false;
			for (final DeliveryDefaultAddressModel deliveryDefaultAddress : defaultAddresses)
			{
				if (b2bUnit.equals(deliveryDefaultAddress.getB2bUnit()))
				{
					deliveryDefaultAddress.setAddress(addressModel);
					getModelService().save(deliveryDefaultAddress);
					isSetB2BUnitAddress = true;
					break;
				}
			}
			// The default address for the current B2BUnit is not set.
			if (!isSetB2BUnitAddress)
			{
				final DeliveryDefaultAddressModel newAddress = getModelService().create(DeliveryDefaultAddressModel.class);
				newAddress.setB2bUnit(b2bUnit);
				newAddress.setAddress(addressModel);
				getModelService().save(newAddress);
				defaultAddresses.add(newAddress);
			}
			b2bCustomer.setDefaultAddresses(defaultAddresses);
			getModelService().save(b2bCustomer);
		}
		else
		{
			LOG.error("Does not exist B2BUnit in the current user [{}] Session", b2bCustomer);
		}
	}
	
}
