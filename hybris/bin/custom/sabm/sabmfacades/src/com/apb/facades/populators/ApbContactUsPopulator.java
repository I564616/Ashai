package com.apb.facades.populators;

import de.hybris.platform.converters.Populator;
import de.hybris.platform.servicelayer.dto.converter.ConversionException;

import org.springframework.util.Assert;

import com.sabmiller.core.model.AsahiB2BUnitModel;
import com.apb.facades.contactust.data.ApbContactUsData;


/**
 * ApbContactUsPopulator implementation of {@link Populator}
 *
 * Convert source(AsahiB2BUnitModel) to target(ApbContactUsData)
 *
 */
public class ApbContactUsPopulator implements Populator<AsahiB2BUnitModel, ApbContactUsData>
{

	public void populate(final AsahiB2BUnitModel source, final ApbContactUsData target) throws ConversionException
	{
		Assert.notNull(source, "Parameter source cannot be null.");
		Assert.notNull(target, "Parameter target cannot be null.");

		target.setAccountNumber(source.getAccountNum());
		target.setCompanyName(source.getLocName());
		target.setSalesRepName(source.getSalesRepName());
		target.setSalesRepEmailID(source.getSalesRepEmailID());
		target.setSalesRepPhone(source.getSalesRepPhone());
	}
}
