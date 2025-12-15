/**
 *
 */
package com.sabmiller.webservice.customer.converters.populator;

import de.hybris.platform.b2bacceleratorfacades.order.data.B2BUnitData;
import de.hybris.platform.commercefacades.user.data.AddressData;
import de.hybris.platform.commercefacades.user.data.CountryData;
import de.hybris.platform.commercefacades.user.data.RegionData;
import de.hybris.platform.converters.Populator;
import de.hybris.platform.servicelayer.dto.converter.ConversionException;

import java.util.ArrayList;
import java.util.List;

import org.apache.commons.collections4.ListUtils;

import com.sabmiller.webservice.customer.Customer;
import com.sabmiller.webservice.customer.Customer.AddressInformation.Address.PhysicalAddress;
import com.sabmiller.webservice.customer.Customer.AlternativeDeliveryAddresses;
import com.sabmiller.webservice.customer.Customer.CustomerRelationship;
import com.sabmiller.webservice.customer.constants.CustomerImportConstants;
import com.sabmiller.webservice.importer.DataImportValidationException;


/**
 * @author joshua.a.antony
 *
 */
public class CustomerAddressPopulator implements Populator<Customer, B2BUnitData>
{

	/*
	 * (non-Javadoc)
	 * 
	 * @see de.hybris.platform.converters.Populator#populate(java.lang.Object, java.lang.Object)
	 */
	@Override
	public void populate(final Customer source, final B2BUnitData target) throws ConversionException
	{
		final List<AddressData> addresses = new ArrayList<AddressData>();
		addresses.add(getPhysicalAddress(source));
		if (source.getAlternativeDeliveryAddresses() != null)
		{
			addresses.addAll(getDeliveryAddresses(source));
		}
		target.setAddresses(addresses);

		if (addresses.isEmpty())
		{
			throw new DataImportValidationException("Ship to unvailable in the request. It is mandatory in Hybris !!!");
		}
	}

	protected List<AddressData> getDeliveryAddresses(final Customer customer)
	{
		final List<AddressData> deliveryAddresses = new ArrayList<AddressData>();
		for (final String shipToId : deriveAddressIds(customer))
		{
			for (final AlternativeDeliveryAddresses eachAddress : ListUtils.emptyIfNull(customer.getAlternativeDeliveryAddresses()))
			{
				if (eachAddress != null && shipToId.equals(eachAddress.getShipToID()) && eachAddress.getAddress() != null
						&& eachAddress.getAddress().getPhysicalAddress() != null)
				{
					final com.sabmiller.webservice.customer.Customer.AlternativeDeliveryAddresses.Address.PhysicalAddress wsAddress = eachAddress
							.getAddress().getPhysicalAddress();
					final AddressData addressData = new AddressData();
					addressData.setShippingAddress(true);
					addressData.setCountry(getCountryData(wsAddress.getCountryKey()));
					addressData.setRegion(getRegionData(wsAddress.getRegionCode(), null, wsAddress.getCountryKey()));
					addressData.setPostalCode(wsAddress.getStreetPostalCode());
					addressData.setTown(wsAddress.getCityName());
					addressData.setLine1(wsAddress.getStreetName());
					addressData.setLine2(wsAddress.getHouseID());
					addressData.setVisibleInAddressBook(true);
					addressData.setPartnerFunction(CustomerImportConstants.SHIP_TO.getCode());
					addressData.setPartnerNumber(shipToId);
					deliveryAddresses.add(addressData);
				}
			}
		}
		return deliveryAddresses;
	}

	protected AddressData getPhysicalAddress(final Customer customer)
	{
		final PhysicalAddress wsAddress = customer.getAddressInformation().getAddress().getPhysicalAddress();
		final AddressData addressData = new AddressData();
		addressData.setContactAddress(true);
		addressData.setCountry(getCountryData(wsAddress.getCountryCode()));
		addressData.setRegion(getRegionData(wsAddress.getRegionCode().getValue(), wsAddress.getRegionName(),
				wsAddress.getCountryCode()));
		addressData.setPostalCode(wsAddress.getStreetPostalCode());
		addressData.setTown(wsAddress.getCityName());
		addressData.setLine1(wsAddress.getStreetName());
		addressData.setLine2(wsAddress.getHouseID());
		addressData.setVisibleInAddressBook(true);
		return addressData;
	}

	protected CountryData getCountryData(final String countryCode)
	{
		final CountryData countryData = new CountryData();
		countryData.setIsocode(countryCode);
		return countryData;
	}

	protected RegionData getRegionData(final String regionCode, final String regionName, final String countryCode)
	{
		final RegionData regionData = new RegionData();
		regionData.setIsocode(regionCode);
		regionData.setCountryIso(countryCode);
		regionData.setName(regionName);
		return regionData;
	}

	protected List<String> deriveAddressIds(final Customer customer)
	{
		final List<String> addressIds = new ArrayList<String>();
		for (final CustomerRelationship cr : customer.getCustomerRelationship())
		{
			if (CustomerImportConstants.SHIP_TO.getCode().equals(cr.getPartnerCode()))
			{
				addressIds.add(cr.getID().getValue());
			}
		}
		return addressIds;
	}
}
