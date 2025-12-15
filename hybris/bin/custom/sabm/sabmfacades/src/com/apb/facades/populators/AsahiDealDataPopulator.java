/**
 *
 */
package com.apb.facades.populators;

import de.hybris.platform.converters.Populator;
import de.hybris.platform.servicelayer.dto.converter.ConversionException;

import java.util.Calendar;
import java.util.Date;

import jakarta.annotation.Resource;

import org.apache.commons.lang3.time.DateUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.util.Assert;

import com.apb.core.util.AsahiDateUtil;
import com.apb.facades.deal.data.AsahiDealData;
import com.sabmiller.core.model.AsahiDealModel;


/**
 * The Class SABMDealJsonPopulator.
 */
public class AsahiDealDataPopulator implements Populator<AsahiDealModel, AsahiDealData>
{
	/** The Constant LOG. */
	private static final Logger LOG = LoggerFactory.getLogger(AsahiDealDataPopulator.class);


	@Resource
	private AsahiDateUtil asahiDateUtil;

	/**
	 *
	 */
	@Override
	public void populate(final AsahiDealModel source, final AsahiDealData target) throws ConversionException
	{
		Assert.notNull(source, "Parameter source cannot be null.");
		Assert.notNull(target, "Parameter target cannot be null.");
		LOG.debug("Populating deal: [{}]", source);

		final AsahiDealModel deal = source;
		if (deal.getValidFrom() != null)
		{
			target.setValidFrom(DateUtils.truncate(deal.getValidFrom(), Calendar.DATE));
		}

		if (deal.getValidTo() != null)
		{
			target.setValidTo(DateUtils.truncate(deal.getValidTo(), Calendar.DATE));
		}
		target.setExpiryDaysRemaining(asahiDateUtil.getDifferenceInDays(new Date(), source.getValidTo()));
		target.setCode(deal.getCode());
		target.setActive(false);
	}
}
