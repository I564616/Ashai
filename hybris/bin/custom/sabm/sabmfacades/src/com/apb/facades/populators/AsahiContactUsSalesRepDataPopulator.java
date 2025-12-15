package com.apb.facades.populators;

import de.hybris.platform.converters.Populator;
import de.hybris.platform.servicelayer.dto.converter.ConversionException;

import org.apache.commons.lang3.StringUtils;
import org.springframework.util.Assert;

import com.apb.facades.contactust.data.ApbContactUsData;
import com.apb.facades.contactust.data.AsahiContactUsSaleRepData;


/**
 * Sales rep data populator
 */
public class AsahiContactUsSalesRepDataPopulator implements Populator<AsahiContactUsSaleRepData, ApbContactUsData>
{
	@Override
	public void populate(final AsahiContactUsSaleRepData source, final ApbContactUsData target) throws ConversionException
	{
		Assert.notNull(source, "Parameter source cannot be null.");
		Assert.notNull(target, "Parameter target cannot be null.");
		//source.
		final AsahiContactUsSaleRepData asahiContactUsSaleRepData = new AsahiContactUsSaleRepData();
		if (StringUtils.isNotEmpty(source.getName()))
		{
			asahiContactUsSaleRepData.setName(source.getName());
		}
		if (StringUtils.isNotEmpty(source.getEmailAddress()))
		{
			asahiContactUsSaleRepData.setEmailAddress(source.getEmailAddress());
		}
		asahiContactUsSaleRepData.setActiveSalesRep(source.isActiveSalesRep());
		asahiContactUsSaleRepData.setShowSalesRep(source.isShowSalesRep());
		asahiContactUsSaleRepData.setContactNumber(source.getContactNumber());
		target.setAsahiContactUsSaleRepData(asahiContactUsSaleRepData);
	}
}
