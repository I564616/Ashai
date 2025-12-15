package com.apb.facades.populators;

import de.hybris.platform.commercefacades.user.data.AddressData;
import de.hybris.platform.converters.Populator;
import de.hybris.platform.core.model.user.AddressModel;
import de.hybris.platform.servicelayer.dto.converter.ConversionException;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Date;
import java.util.LinkedList;
import java.util.List;
import java.util.stream.Collectors;

import jakarta.annotation.Resource;

import org.apache.commons.collections4.CollectionUtils;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;import org.slf4j.LoggerFactory;import org.springframework.util.Assert;

import com.sabmiller.core.enums.AddressType;
import com.sabmiller.core.model.AsahiB2BUnitModel;
import com.apb.core.util.ApbAddressTimeUtil;
import com.apb.core.util.AsahiSiteUtil;
import com.apb.facades.checkout.data.B2BUnitDeliveryAddressData;
import com.apb.facades.user.data.ApbCompanyData;


/**
 * The Class ApbCompanyDetailsPopulator.
 *
 * Surendra Sharma
 */
public class ApbCompanyDetailsPopulator implements Populator<AsahiB2BUnitModel, ApbCompanyData>
{
	private static final Logger LOG = LoggerFactory.getLogger(ApbCompanyDetailsPopulator.class);
	/** The asahi site util. */
	@Resource
	private AsahiSiteUtil asahiSiteUtil;

	public void populate(final AsahiB2BUnitModel source, final ApbCompanyData target) throws ConversionException
	{
		Assert.notNull(source, "Parameter source cannot be null.");
		Assert.notNull(target, "Parameter target cannot be null.");
		target.setAccountNumber(source.getAccountNum());
		target.setAcccountName(source.getName());
		target.setAbn(source.getAbnNumber());
		target.setLiquorLicense(source.getLiquorLicensenumber());
		target.setCompanyEmailAddress(source.getEmailAddress());
		target.setTradingName(source.getLocname());

		/* company billing address */
		target.setCompanyBillingAddress(setAddress(source.getAddresses(), target));

		target.setCompanyPhone(source.getPhone());
		target.setCompanyMobilePhone(source.getCellularPhone());
		target.setCompanyFax(source.getTeleFax());

		List<AddressModel> addresses = new ArrayList<>();
		if(this.asahiSiteUtil.isApb() && CollectionUtils.isNotEmpty(source.getAddresses())){
			addresses.addAll(source.getAddresses());
		}
		if(this.asahiSiteUtil.isSga() && null!=source && CollectionUtils.isNotEmpty(source.getShipToAccounts())){
			AsahiB2BUnitModel shipTo = source.getShipToAccounts().get(0);
				if(null!=shipTo && CollectionUtils.isNotEmpty(shipTo.getAddresses())){
					addresses.add((AddressModel) ((List)shipTo.getAddresses()).get(0));
				}
		}
		
		/* Company delivery Address */
		if (CollectionUtils.isNotEmpty(addresses))
		{
			final List<B2BUnitDeliveryAddressData> b2bUnitDeliveryAddressDataList = new LinkedList<B2BUnitDeliveryAddressData>();
			final List<AddressData> addressDataList = new LinkedList<AddressData>();
			B2BUnitDeliveryAddressData b2bUnitDeliveryAddressData = null;
			for (final AddressModel addressModel : addresses)
			{
				if (addressModel.getAddressType() != null)
				{
					final AddressType addressType = addressModel.getAddressType();

					if (addressModel.getVisibleInAddressBook()
							&& !addressType.getCode().equalsIgnoreCase(AddressType.INVOICE.toString()))
					{
						b2bUnitDeliveryAddressData = new B2BUnitDeliveryAddressData();
						final AddressData addressData = new AddressData();
						addressData.setDeliveryInstruction(addressModel.getEclDeliveryInstruction());
						addressData.setDeliveryCalendar(addressModel.getDeliveryCalendar());
						addressData.setAddressId(addressModel.getAddressRecordid());
						addressData.setDeliveryAddress(setAddressNonBillingAddress(addressModel, target));

						String fromDate = ApbAddressTimeUtil.getDeliveryTimeHrsMinute(addressModel.getEclDeliveryTimeslotFrom());
						if (StringUtils.isNotEmpty(fromDate))
						{
							String[] times = fromDate.split(":");
							addressData.setDeliveryTimeFrameFromMM(Integer.parseInt(times[1]));
							addressData.setDeliveryTimeFrameFromHH(Integer.parseInt(times[0]));	
						}
						String toDate = ApbAddressTimeUtil.getDeliveryTimeHrsMinute(addressModel.getEclDeliveryTimesLotto());
						if (StringUtils.isNotEmpty(toDate))
						{
							String[] times = toDate.split(":");
							addressData.setDeliveryTimeFrameToMM(Integer.parseInt(times[1]));
							addressData.setDeliveryTimeFrameToHH(Integer.parseInt(times[0]));
						}
						addressData.setRemoveRequestAddress(addressModel.getRemoveRequestAddress());
						addressData.setChangeRequestAddress(addressModel.getChangeRequestAddress());
						addressData.setDefaultAddress(addressModel.getDefaultAddress());
						if (addressDataList.size() >= 0 && addressModel.getDefaultAddress())
						{
							LOG.debug("Default delivery address set on 0 position in list:");
							addressDataList.add(0, addressData);
						}
						if (addressDataList.contains(addressData))
						{
							LOG.warn("Duplicate address remove in list " + addressModel.getDefaultAddress());
						}
						else
						{
							addressDataList.add(addressData);
						}
						b2bUnitDeliveryAddressData.setDeliveryAddresses(addressDataList);
					}
				}
			}
			b2bUnitDeliveryAddressDataList.add(b2bUnitDeliveryAddressData);
			target.setDeliveryAddresses(b2bUnitDeliveryAddressDataList);
		}
	}

	/**
	 * Address comes in delivery address section only
	 *
	 * @param addressModel
	 * @param target
	 * @return
	 */
	private String setAddressNonBillingAddress(final AddressModel addressModel, final ApbCompanyData target)
	{
		String customAaddress = StringUtils.EMPTY;
		if (addressModel != null)
		{
			customAaddress = setAddressData(addressModel);
		}
		return customAaddress;
	}

	/**
	 * @param source
	 * @param target
	 */
	protected String setAddress(final Collection<AddressModel> address, final ApbCompanyData target)
	{

		String customAaddress = StringUtils.EMPTY;
		if (CollectionUtils.isNotEmpty(address))
		{
			for (final AddressModel addressModel : address)
			{
				if (addressModel != null && addressModel.getAddressType() != null
						&& addressModel.getAddressType().getCode().equalsIgnoreCase(AddressType.INVOICE.toString()))
				{
					customAaddress = setAddressData(addressModel);
				}
			}
		}
		return customAaddress;
	}

	/**
	 * @param addressModel
	 * @return
	 */
	private String setAddressData(final AddressModel addressModel)
	{
		String customAaddress;
		final String streetNumber = addressModel.getStreetnumber() == null ? StringUtils.EMPTY
				: addressModel.getStreetnumber();
		final String streetName = addressModel.getStreetname() == null ? StringUtils.EMPTY : addressModel.getStreetname();
		final String town = addressModel.getTown() == null ? StringUtils.EMPTY : addressModel.getTown();
		final String postalcode = addressModel.getPostalcode() == null ? StringUtils.EMPTY : addressModel.getPostalcode();
		String countryName = "";
		if (addressModel.getCountry() != null)
		{
			countryName = addressModel.getCountry().getName() == null ? StringUtils.EMPTY
					: addressModel.getCountry().getName();
		}
		if (StringUtils.isNotEmpty(streetNumber))
		{
			customAaddress = streetNumber + ", " + streetName + ", " + town + ", " + postalcode + ", " + countryName;
		}
		else
		{
			customAaddress = streetName + ", " + town + ", " + postalcode + ", " + countryName;
		}
		return customAaddress;
	}
}
