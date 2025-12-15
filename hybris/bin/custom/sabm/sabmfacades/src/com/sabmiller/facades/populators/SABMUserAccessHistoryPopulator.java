/**
 *
 */
package com.sabmiller.facades.populators;

import de.hybris.platform.converters.Populator;
import de.hybris.platform.servicelayer.dto.converter.ConversionException;

import org.springframework.util.Assert;

import com.sabmiller.core.model.SABMUserAccessHistoryModel;
import com.sabmiller.facades.customer.SABMUserAccessHistoryData;


/**
 * The Class SABMUserAccessHistoryPopulator.
 *
 */
public class SABMUserAccessHistoryPopulator implements Populator<SABMUserAccessHistoryData, SABMUserAccessHistoryModel>
{
	/*
	 * (non-Javadoc)
	 *
	 * @see de.hybris.platform.converters.Populator#populate(java.lang.Object, java.lang.Object)
	 */
	@Override
	public void populate(final SABMUserAccessHistoryData source, final SABMUserAccessHistoryModel target)
			throws ConversionException
	{
		Assert.notNull(source, "Parameter source cannot be null.");
		Assert.notNull(target, "Parameter target cannot be null.");
		target.setUid(source.getUid());
		target.setPublicIPAddress(source.getPublicIPAddress());
		target.setRememberMeEnabled(source.getRememberMeEnabled());
		target.setUserAgent(source.getUserAgent());
	}

}
