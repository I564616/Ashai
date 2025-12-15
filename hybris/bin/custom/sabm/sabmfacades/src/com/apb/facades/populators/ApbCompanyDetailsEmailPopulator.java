package com.apb.facades.populators;

import de.hybris.platform.commercefacades.user.data.AddressData;
import de.hybris.platform.converters.Populator;
import de.hybris.platform.servicelayer.dto.converter.ConversionException;

import java.util.LinkedList;
import java.util.List;

import org.apache.commons.collections4.CollectionUtils;
import org.apache.commons.lang3.StringUtils;

import com.apb.core.model.ApbCompanyDetailsEmailModel;
import com.apb.core.model.ApbDeliveryAddressModel;
import com.apb.facades.checkout.data.B2BUnitDeliveryAddressData;
import com.apb.facades.constants.ApbFacadesConstants;
import com.apb.facades.user.data.ApbCompanyData;


/**
 * The Class ApbCompanyDetailsPopulator.
 *
 * Surendra Sharma
 */
public class ApbCompanyDetailsEmailPopulator implements Populator<ApbCompanyDetailsEmailModel, ApbCompanyData>
{
	public void populate(final ApbCompanyDetailsEmailModel source, final ApbCompanyData target) throws ConversionException
	{
		target.setAccountNumber(source.getAccountNumber());
		target.setAcccountName(source.getAccountName());
		target.setAbn(source.getAbn());
		target.setLiquorLicense(source.getLiquorLicense());
		target.setCompanyEmailAddress(source.getCompanyEmailAddress());
		target.setTradingName(source.getTraidingName());
		target.setCompanyBillingAddress(source.getCompanyBillingAddress());
		target.setCompanyPhone(source.getCompanyPhone());
		target.setCompanyMobilePhone(source.getCompanyMobilePhone());
		target.setCompanyFax(source.getCompanyFax());
		target.setSameasInvoiceAddress(source.getSameAsAddress());
		setDeliveryAddress(source, target);
	}

	/**
	 * @param source
	 * @param target
	 */
	private void setDeliveryAddress(final ApbCompanyDetailsEmailModel source, final ApbCompanyData target)
	{
		/* Company delivery Address */
		if (CollectionUtils.isNotEmpty(source.getApbDeliveryAddressColl()))
		{
			final List<B2BUnitDeliveryAddressData> b2bUnitDeliveryAddressDataList = new LinkedList<B2BUnitDeliveryAddressData>();
			final List<ApbDeliveryAddressModel> apbDeliveryAddressModelList = source.getApbDeliveryAddressColl();
			final List<AddressData> addressDataList = new LinkedList<AddressData>();
			final B2BUnitDeliveryAddressData b2bUnitDeliveryAddressData = new B2BUnitDeliveryAddressData();

			for (final ApbDeliveryAddressModel apbDeliveryAddressModel : apbDeliveryAddressModelList)
			{
				final AddressData addressData = new AddressData();
				addressData.setDeliveryAddress(apbDeliveryAddressModel.getDeliveryAddress());
				addressData.setDeliveryInstruction(apbDeliveryAddressModel.getDeliveryInstruction());
				addressData.setDeliveryCalendar(apbDeliveryAddressModel.getDeliveryCalendar());
				if (StringUtils.isNotEmpty(apbDeliveryAddressModel.getAddressId()))
				{
					addressData.setAddressId(apbDeliveryAddressModel.getAddressId());
				}
				else
				{
					addressData.setAddressId(ApbFacadesConstants.NA);
				}
				addressData.setDeliveryTimeFrameFromMM(apbDeliveryAddressModel.getDeliveryTimeFrameFromMM());
				if (apbDeliveryAddressModel.getDeliveryTimeFrameFromMM() == null)
				{
					addressData.setDeliveryTimeFrameFromMM(0);
				}
				addressData.setDeliveryTimeFrameFromHH(apbDeliveryAddressModel.getDeliveryTimeFrameFromHH());
				if (apbDeliveryAddressModel.getDeliveryTimeFrameFromHH() == null)
				{
					addressData.setDeliveryTimeFrameFromHH(0);
				}
				addressData.setDeliveryTimeFrameToMM(apbDeliveryAddressModel.getDeliveryTimeFrameToMM());
				if (apbDeliveryAddressModel.getDeliveryTimeFrameToMM() == null)
				{
					addressData.setDeliveryTimeFrameToMM(0);
				}
				addressData.setDeliveryTimeFrameToHH(apbDeliveryAddressModel.getDeliveryTimeFrameToHH());
				if (apbDeliveryAddressModel.getDeliveryTimeFrameToHH() == null)
				{
					addressData.setDeliveryTimeFrameToHH(0);
				}
				addressData.setRemoveRequestAddress(apbDeliveryAddressModel.getRemoveRequestAddress());
				addressData.setChangeRequestAddress(apbDeliveryAddressModel.getChangeRequestAddress());
				addressDataList.add(addressData);
				b2bUnitDeliveryAddressData.setDeliveryAddresses(addressDataList);
			}
			b2bUnitDeliveryAddressDataList.add(b2bUnitDeliveryAddressData);
			target.setDeliveryAddresses(b2bUnitDeliveryAddressDataList);
		}
	}

}
