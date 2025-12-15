/**
 *
 */
package com.sabmiller.facades.populators;

import de.hybris.platform.servicelayer.dto.converter.ConversionException;
import de.hybris.platform.servicelayer.dto.converter.Converter;
import de.hybris.platform.servicelayer.user.UserService;

import java.util.Arrays;
import java.util.List;

import jakarta.annotation.Resource;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.sabmiller.core.deals.services.DealsService;
import com.sabmiller.core.model.DealModel;
import com.sabmiller.core.model.SABMRecommendationDPModel;
import com.sabmiller.core.model.SABMRecommendationModel;
import com.sabmiller.facades.deal.data.DealBaseProductJson;
import com.sabmiller.facades.deal.data.DealJson;
import com.sabmiller.facades.deal.data.DealRangeJson;
import com.sabmiller.facades.deal.data.RecommendationDealJson;


/**
 * The Class SABMDealJsonPopulator.
 */
public class SABMRecommendationDealJsonPopulator
{
	/** The Constant LOG. */
	private static final Logger LOG = LoggerFactory.getLogger(SABMRecommendationDealJsonPopulator.class);

	@Resource(name = "dealJsonConverter")
	private Converter<List<DealModel>, DealJson> dealJsonConverter;

	@Resource(name = "dealsService")
	private DealsService dealsService;

	@Resource(name = "userService")
	private UserService userService;


	/*
	 * (non-Javadoc)
	 *
	 * @see de.hybris.platform.converters.Populator#populate(java.lang.Object, java.lang.Object)
	 */
	public void populate(final SABMRecommendationModel source, final RecommendationDealJson target) throws ConversionException
	{

		final DealModel dealModel = dealsService.getDeal(source.getDealCode());

		dealJsonConverter.convert(Arrays.asList(dealModel), target);

		target.setRecommendationId(source.getPk().getLongValueAsString());
		target.setRecommendationType(source.getRecommendationType());
		target.setRecommendedBy(source.getRecommendedBy());

		for (final DealRangeJson dealRangeJson : target.getRanges())
		{
			for (final DealBaseProductJson dealBaseProductJson : dealRangeJson.getBaseProducts())
			{
				for (final SABMRecommendationDPModel dealProduct : source.getDealProducts())
				{
					if (dealProduct.getProductCode().equals(dealBaseProductJson.getProductCode()))
					{
						dealBaseProductJson.setQty(dealProduct.getQty());
						dealBaseProductJson.setUomS(dealProduct.getUnit().getName());
						dealBaseProductJson.setUomP(dealProduct.getUnit().getPluralName());
						dealBaseProductJson.setUomCode(dealProduct.getUnit().getCode());
						break;
					}
				}
			}
		}


	}



}
