package com.apb.facades.populators;

import de.hybris.platform.commercefacades.user.data.AddressData;
import de.hybris.platform.commercefacades.user.data.CountryData;
import de.hybris.platform.commercefacades.user.data.RegionData;
import de.hybris.platform.converters.Populator;
import de.hybris.platform.servicelayer.dto.converter.ConversionException;

import java.util.LinkedList;
import java.util.List;

import org.apache.commons.collections4.CollectionUtils;
import org.apache.commons.lang3.StringUtils;
import org.springframework.util.Assert;

import com.apb.core.model.ApbKegReturnEmailModel;
import com.apb.core.model.KegReturnSizeModel;
import com.apb.facades.kegreturn.data.ApbKegReturnData;
import com.apb.facades.kegreturn.data.KegSizeData;


/**
 * ApbKegReturnEmailPopulator implementation of {@link Populator}
 *
 * Convert source(ApbKegReturnEmailModel) to target(ApbKegReturnData)
 *
 */
public class ApbKegReturnEmailPopulator implements Populator<ApbKegReturnEmailModel, ApbKegReturnData>
{
	@Override
	public void populate(final ApbKegReturnEmailModel source, final ApbKegReturnData target) throws ConversionException
	{
		Assert.notNull(source, "Parameter source cannot be null.");
		Assert.notNull(target, "Parameter target cannot be null.");

		target.setAccountNumber(source.getAccountNumber() != null ? source.getAccountNumber() : "");
		target.setAccountName(source.getAccountName() != null ? source.getAccountName() : "");
		target.setContactName(source.getContactName() != null ? source.getContactName() : "");
		target.setContactNumber(source.getContactNumber() != null ? source.getContactNumber() : "");
		target.setEmailAddress(source.getEmailAddress() != null ? source.getEmailAddress() : "");
		target.setRequestRefNumber(source.getReferenceNumber());
		target.setKegComments(source.getKegComments() != null ? source.getKegComments() : "");
		setAddressData(source, target);
		setKegSizeData(source, target);
	}

	/**
	 * set Keg Size
	 *
	 * @param source
	 * @param target
	 */
	private void setKegSizeData(final ApbKegReturnEmailModel source, final ApbKegReturnData target)
	{
		final List<KegSizeData> kegSizeDataList = new LinkedList<>();
		if (CollectionUtils.isNotEmpty(source.getKegReturnSizeList()))
		{
			for (final KegReturnSizeModel kegReturnSizeModel2 : source.getKegReturnSizeList())
			{
				final KegSizeData kegSizeData = new KegSizeData();
				kegSizeData.setKegSize(kegReturnSizeModel2.getKegSize());
				kegSizeData.setKegQuantity(kegReturnSizeModel2.getKegQuantity());
				kegSizeDataList.add(kegSizeData);
			}
		}
		target.setKegSize(kegSizeDataList);
	}

	/**
	 * set Address Data
	 *
	 * @param source
	 */
	private void setAddressData(final ApbKegReturnEmailModel source, final ApbKegReturnData target)
	{
		final AddressData addressData = new AddressData();
		if (source.getPickupAddress() != null)
		{
			addressData.setCompanyName(source.getPickupAddress().getCompany() != null ? source.getPickupAddress().getCompany() : "");
			if (source.getPickupAddress().getTitle() != null)
			{
				addressData.setTitle(
						source.getPickupAddress().getTitle().getName() != null ? source.getPickupAddress().getTitle().getName() : "");
			}
			else
			{
				addressData.setTitle("");
			}
			addressData
					.setFirstName(source.getPickupAddress().getFirstname() != null ? source.getPickupAddress().getFirstname() : "");
			addressData.setLastName(source.getPickupAddress().getLastname() != null ? source.getPickupAddress().getLastname() : "");
			addressData.setLine1(source.getPickupAddress().getLine1() != null ? source.getPickupAddress().getLine1() : "");
			addressData.setLine2(source.getPickupAddress().getLine2() != null ? source.getPickupAddress().getLine2() : "");
			addressData.setTown(source.getPickupAddress().getTown() != null ? source.getPickupAddress().getTown() : "");

			final RegionData regionData = new RegionData();
			if (source.getPickupAddress().getRegion() != null)
			{
				regionData.setIsocode(source.getPickupAddress().getRegion().getIsocode());
				regionData.setName(
						source.getPickupAddress().getRegion().getName() != null ? source.getPickupAddress().getRegion().getName() : "");
				addressData.setRegion(regionData);
			}
			else
			{
				regionData.setName(StringUtils.EMPTY);
				regionData.setIsocode(StringUtils.EMPTY);
				addressData.setRegion(regionData);
			}
			addressData
					.setPostalCode(source.getPickupAddress().getPostalcode() != null ? source.getPickupAddress().getPostalcode() : "");
			target.setPickupAddressData(addressData);

			final CountryData countryData = new CountryData();
			if (source.getPickupAddress().getCountry() != null)
			{
				countryData.setName(source.getPickupAddress().getCountry().getName() != null
						? source.getPickupAddress().getCountry().getName() : "");
				addressData.setCountry(countryData);
			}
			else
			{
				countryData.setName(StringUtils.EMPTY);
				addressData.setCountry(countryData);
			}
		}
	}
}
