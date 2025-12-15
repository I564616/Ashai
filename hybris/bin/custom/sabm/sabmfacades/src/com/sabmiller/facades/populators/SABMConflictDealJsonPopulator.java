/**
 *
 */
package com.sabmiller.facades.populators;

import de.hybris.platform.converters.Populator;
import de.hybris.platform.servicelayer.dto.converter.ConversionException;

import java.util.List;

import jakarta.annotation.Resource;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.util.Assert;

import com.sabmiller.core.model.DealModel;
import com.sabmiller.facades.deal.data.ConflictDealJson;
import com.sabmiller.facades.deal.data.DealJson;


/**
 *
 */
public class SABMConflictDealJsonPopulator implements Populator<List<DealModel>, ConflictDealJson>
{
	/** The Constant LOG. */
	private static final Logger LOG = LoggerFactory.getLogger(SABMConflictDealJsonPopulator.class);

	@Resource(name = "dealTitlePopulator")
	private Populator<List<DealModel>, DealJson> dealTitlePopulator;

	@Resource(name = "dealProductPopulator")
	private SABMDealProductPopulator dealProductPopulator;

	/**
	 * convert the conflict deal
	 *
	 * @param source
	 * @param target
	 */
	@Override
	public void populate(final List<DealModel> source, final ConflictDealJson target) throws ConversionException
	{
		Assert.notNull(source, "Parameter source cannot be null.");
		Assert.notNull(target, "Parameter target cannot be null.");

		LOG.debug("Populating conflict deal: [{}]", source);

		target.setCode(source.get(0).getCode());

		final DealJson dealJson = new DealJson();
		dealTitlePopulator.populate(source, dealJson);
		target.setTitle(dealJson.getTitle());

		dealProductPopulator.populate(source, dealJson); // for GTM
		target.setRanges(dealJson.getRanges()); // for GTM
	}

}
