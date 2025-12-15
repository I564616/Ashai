/**
 *
 */
package com.sabmiller.core.deals.services;

import de.hybris.platform.servicelayer.session.SessionService;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import jakarta.annotation.Resource;

import org.apache.commons.collections4.CollectionUtils;

import com.sabmiller.core.constants.SabmCoreConstants;
import com.sabmiller.facades.deal.data.DealJson;


/**
 * The Class DealsServiceImpl.
 *
 * @author joshua.a.antony
 */
public class DealsCacheServiceImpl implements DealsCacheService
{

	/** The session service. */
	@Resource(name = "sessionService")
	private SessionService sessionService;


	@Resource(name = "dealsService")
	protected DealsService dealsService;



	@Override
	public final List<String> getDealTitlesForProduct(final String productCode)
	{
		if (productCode != null)
		{
			final Date deliveryDate = sessionService.getAttribute(SabmCoreConstants.SESSION_ATTR_DELIVERY_DATE);

			final List<DealJson> dealjson = dealsService.searchDeals(deliveryDate, Boolean.TRUE);

			if (CollectionUtils.size(dealjson) > 0)
			{
				final List<String> dealTitles = new ArrayList<>();

				for (final DealJson deal : dealjson)
				{
					if (dealsService.isProductBelongsToDeal(productCode, deal))
					{
						dealTitles.add(trimDealTitle(deal.getTitle()));
					}
				}
				return dealTitles;
			}
		}
		return null;

	}


	/**
	 * @param title
	 * @return
	 */
	private String trimDealTitle(final String title)
	{

		if (title != null && title.length() > 200)
		{
			return title.substring(0, 200).trim() + " ...";
		}
		return title;
	}




	/*
	 * (non-Javadoc)
	 *
	 * @see com.sabmiller.core.deals.services.DealsCacheService#getDealsFlag(java.lang.String)
	 */
	@Override
	public boolean getDealsFlag(final String productCode)
	{
		if (productCode != null)
		{
			final Date deliveryDate = sessionService.getAttribute(SabmCoreConstants.SESSION_ATTR_DELIVERY_DATE);

			final List<DealJson> dealjson = dealsService.searchDeals(deliveryDate, Boolean.TRUE);

			if (CollectionUtils.size(dealjson) > 0)
			{
				return true;
			}
		}
		return false;

	}

}
