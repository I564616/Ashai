package com.apb.facades.populators;

import de.hybris.platform.converters.Populator;
import de.hybris.platform.servicelayer.dto.converter.ConversionException;

import org.apache.commons.lang3.StringUtils;
import org.springframework.util.Assert;

import com.apb.core.model.AsahiEmployeeModel;
import com.apb.facades.contactust.data.AsahiContactUsSaleRepData;


/**
 *
 */
public class AsahiContactUsSalesRepPopulator implements Populator<AsahiEmployeeModel, AsahiContactUsSaleRepData>
{

	@Override
	public void populate(final AsahiEmployeeModel source, final AsahiContactUsSaleRepData target) throws ConversionException
	{
		Assert.notNull(source, "Parameter source cannot be null.");
		Assert.notNull(target, "Parameter target cannot be null.");

		if (StringUtils.isNotEmpty(source.getName()))
		{
			target.setName(source.getName());
		}
		if (StringUtils.isNotEmpty(source.getSalesRepEmail()))
		{
			target.setEmailAddress(source.getSalesRepEmail());
		}
		if (null != source.getActiveSalesRep())
		{
			target.setActiveSalesRep(source.getActiveSalesRep().booleanValue());
		}
		if (null != source.getShowSalesRep())
		{
			target.setShowSalesRep(source.getShowSalesRep().booleanValue());
		}
		target.setContactNumber(source.getPhone());
	}

}
