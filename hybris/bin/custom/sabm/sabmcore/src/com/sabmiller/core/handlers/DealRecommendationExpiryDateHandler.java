/**
 *
 */
package com.sabmiller.core.handlers;

import de.hybris.platform.servicelayer.model.attribute.DynamicAttributeHandler;

import java.util.Date;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.sabmiller.core.deals.services.DealsService;
import com.sabmiller.core.enums.RecommendationType;
import com.sabmiller.core.model.DealModel;
import com.sabmiller.core.model.SABMRecommendationModel;


/**
 * @author Siddarth
 *
 */
public class DealRecommendationExpiryDateHandler implements DynamicAttributeHandler<Date, SABMRecommendationModel>
{
	private static final Logger LOG = LoggerFactory.getLogger(DealRecommendationExpiryDateHandler.class);

	private DealsService dealsService;

	public void setDealsService(final DealsService dealsService)
	{
		this.dealsService = dealsService;
	}

	/*
	 * (non-Javadoc)
	 *
	 * @see
	 * de.hybris.platform.servicelayer.model.attribute.DynamicAttributeHandler#set(de.hybris.platform.servicelayer.model.
	 * AbstractItemModel, java.lang.Object)
	 */
	@Override
	public void set(final SABMRecommendationModel recommendationModel, final Date dealExpiryDate)
	{
		throw new UnsupportedOperationException("Set of dynamic attribute 'dealExpiryDate' of SABMRecommendation is disabled!");
	}

	/*
	 * (non-Javadoc)
	 *
	 * @see
	 * de.hybris.platform.servicelayer.model.attribute.DynamicAttributeHandler#get(de.hybris.platform.servicelayer.model.
	 * AbstractItemModel)
	 */
	@Override
	public Date get(final SABMRecommendationModel recommendationModel)
	{
		if (recommendationModel.getRecommendationType().equals(RecommendationType.DEAL))
		{
			try
			{
				final DealModel deal = dealsService.getDeal(recommendationModel.getDealCode());
				return deal.getValidTo();
			}
			catch (final Exception e)
			{
				LOG.debug("Unable to find deal with deal code: " + recommendationModel.getDealCode());
			}
		}
		return null;
	}
}