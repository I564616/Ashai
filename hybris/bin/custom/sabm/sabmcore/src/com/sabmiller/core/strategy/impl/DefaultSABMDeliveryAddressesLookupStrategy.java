/**
 *
 */
package com.sabmiller.core.strategy.impl;

import de.hybris.platform.b2b.model.B2BCustomerModel;
import de.hybris.platform.b2b.model.B2BUnitModel;
import de.hybris.platform.commerceservices.strategies.impl.DefaultDeliveryAddressesLookupStrategy;
import de.hybris.platform.core.model.order.AbstractOrderModel;
import de.hybris.platform.core.model.user.AddressModel;
import de.hybris.platform.core.model.user.CustomerModel;
import de.hybris.platform.core.model.user.UserModel;
import de.hybris.platform.store.services.BaseStoreService;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.List;
import java.util.stream.Collectors;

import jakarta.annotation.Resource;

import org.apache.commons.collections4.CollectionUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.apb.core.util.AsahiSiteUtil;
import com.sabmiller.core.b2b.services.SabmB2BUnitService;
import com.sabmiller.core.enums.AddressType;
import com.sabmiller.core.model.AsahiB2BUnitModel;


/**
 * @author yaopeng
 *
 */
public class DefaultSABMDeliveryAddressesLookupStrategy extends DefaultDeliveryAddressesLookupStrategy
{
	/** The Constant LOG. */
	protected static final Logger LOG = LoggerFactory.getLogger(DefaultSABMDeliveryAddressesLookupStrategy.class);

	/** The unit service. */
	private SabmB2BUnitService b2bUnitService;

	@Resource
	private AsahiSiteUtil asahiSiteUtil;

	@Resource(name = "baseStoreService")
	protected BaseStoreService baseStoreService;

	/*
	 * (non-Javadoc)
	 *
	 * @see de.hybris.platform.commerceservices.strategies.impl.DefaultDeliveryAddressesLookupStrategy#
	 * getDeliveryAddressesForOrder(de.hybris.platform.core.model.order.AbstractOrderModel, boolean)
	 */
	@Override
	public List<AddressModel> getDeliveryAddressesForOrder(final AbstractOrderModel abstractOrder,
			final boolean visibleAddressesOnly)
	{
		if (!asahiSiteUtil.isCub())
		{
			if (null != abstractOrder.getUser())
			{
				final List<AddressModel> addresses = getB2BUnitAddressesForUser(abstractOrder.getUser(), visibleAddressesOnly);
				if (CollectionUtils.isNotEmpty(addresses))
				{
					return addresses;
				}
			}
			return Collections.emptyList();
		}

		final List<AddressModel> addressesForOrder = new ArrayList<AddressModel>();
		if (abstractOrder != null)
		{
			final UserModel user = abstractOrder.getUser();
			if (user instanceof CustomerModel)
			{
				if (visibleAddressesOnly)
				{
					/*
					 * final List<AddressModel> addresslist = getCustomerAccountService()
					 * .getAddressBookDeliveryEntries((CustomerModel) user); if (null != addresslist &&
					 * !addresslist.isEmpty()) { addressesForOrder.addAll(addresslist); } // if The DeliveryAddress not
					 * exists in User, then getAddress from b2bUnit else {
					 */
						addressesForOrder.addAll(getB2bUnitAddress(abstractOrder));
					//}
				}
				else
				{
					/*
					 * final List<AddressModel> addresslist =
					 * getCustomerAccountService().getAllAddressEntries((CustomerModel) user); if (null != addresslist &&
					 * !addresslist.isEmpty()) { addressesForOrder.addAll(addresslist); } // if The DeliveryAddress not
					 * exists in User, then getAddress from b2bUnit else {
					 */
						addressesForOrder.addAll(getB2bUnitAddress(abstractOrder));
					//}
				}
				// If the user had no addresses, check the order for an address in case it's a guest checkout.
				if (getCheckoutCustomerStrategy().isAnonymousCheckout() && addressesForOrder.isEmpty()
						&& abstractOrder.getDeliveryAddress() != null)
				{
					addressesForOrder.add(abstractOrder.getDeliveryAddress());
				}
			}
		}
		return addressesForOrder;
	}

	private List<AddressModel> getB2BUnitAddressesForUser(final UserModel user, final boolean visibleAddressesOnly)
	{
		final List<AddressModel> addresses = new ArrayList<>();
		if (null != user && user instanceof B2BCustomerModel)
		{
			final B2BCustomerModel customer = (B2BCustomerModel) user;
			final AsahiB2BUnitModel b2bUnit = (AsahiB2BUnitModel) customer.getDefaultB2BUnit();
			if(this.asahiSiteUtil.isApb() && null!=b2bUnit && CollectionUtils.isNotEmpty(b2bUnit.getAddresses())){
				addresses.addAll(b2bUnit.getAddresses().stream().filter(address -> null != address.getAddressType() && !address.getAddressType().equals(AddressType.INVOICE)).collect(Collectors.toList()));
			}
			if(this.asahiSiteUtil.isSga() &&  null!=b2bUnit && CollectionUtils.isNotEmpty(b2bUnit.getShipToAccounts())){

				for(final AsahiB2BUnitModel b2bUnitModel : b2bUnit.getShipToAccounts()){
					if(CollectionUtils.isNotEmpty(b2bUnitModel.getAddresses())){
						addresses.add((AddressModel) ((List)b2bUnitModel.getAddresses()).get(0));
					}
				}
			}
		}
		return addresses.isEmpty() ? Collections.emptyList() : visibleAddressesOnly ? addresses.stream().filter(address -> address.getVisibleInAddressBook()).collect(Collectors.toList()) : addresses;
	}

	/**
	 * The DeliveryAddress not exists in User, then getAddress from b2bUnit
	 *
	 * @param abstractOrder
	 * @return Collection
	 */
	public Collection<AddressModel> getB2bUnitAddress(final AbstractOrderModel abstractOrder)
	{
		final UserModel user = abstractOrder.getUser();

		if (user instanceof B2BCustomerModel)
		{
			final B2BUnitModel b2bUnitModel = b2bUnitService.getParent((B2BCustomerModel) user);

			final List<AddressModel> addressModels = new ArrayList<AddressModel>();
			if (null != b2bUnitModel && null != b2bUnitModel.getAddresses())
			{
				for (final AddressModel address : b2bUnitModel.getAddresses())
				{
					if (Boolean.TRUE.equals(address.getShippingAddress()) && Boolean.TRUE.equals(address.getVisibleInAddressBook()))
					{
						addressModels.add(address);
					}
				}
				return addressModels;
			}
		}

		LOG.warn("Unable to find the b2bUnitAddress from Order: {}", abstractOrder);

		return Collections.<AddressModel> emptyList();
	}


	/**
	 * @return the b2bUnitService
	 */
	public SabmB2BUnitService getB2bUnitService()
	{
		return b2bUnitService;
	}

	/**
	 * @param b2bUnitService
	 *           the b2bUnitService to set
	 */
	public void setB2bUnitService(final SabmB2BUnitService b2bUnitService)
	{
		this.b2bUnitService = b2bUnitService;
	}

}
