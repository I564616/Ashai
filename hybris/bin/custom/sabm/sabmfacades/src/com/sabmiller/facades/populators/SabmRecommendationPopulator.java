package com.sabmiller.facades.populators;

import com.sabmiller.core.util.UOMUtils;
import de.hybris.platform.commercefacades.product.ProductFacade;
import de.hybris.platform.commercefacades.product.ProductOption;
import de.hybris.platform.converters.Populator;
import de.hybris.platform.core.model.product.UnitModel;
import de.hybris.platform.servicelayer.dto.converter.ConversionException;

import java.util.Arrays;

import jakarta.annotation.Resource;

import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.sabmiller.core.deals.services.DealsService;
import com.sabmiller.core.enums.RecommendationType;
import com.sabmiller.core.model.SABMRecommendationModel;
import com.sabmiller.facades.deal.data.RecommendationDealJson;
import com.sabmiller.facades.product.data.UomData;
import com.sabmiller.facades.recommendation.data.RecommendationData;
import com.sabmiller.facades.recommendation.data.RecommendationProductData;


/**
 * Created by raul.b.abatol.jr on 07/06/2017.
 */
public class SabmRecommendationPopulator implements Populator<SABMRecommendationModel, RecommendationData>
{
	/** The Constant LOG. */
	private static final Logger LOG = LoggerFactory.getLogger(SabmRecommendationPopulator.class);

	@Resource(name = "accProductFacade")
	private ProductFacade productFacade;

	SABMRecommendationDealJsonPopulator sabmRecommendationDealJsonPopulator;



	private DealsService dealsService;

	@Override
	public void populate(final SABMRecommendationModel sabmRecommendationModel, final RecommendationData recommendationData)
			throws ConversionException
	{
		recommendationData.setRecommendationId(sabmRecommendationModel.getPk().getLongValueAsString());
		recommendationData.setRecommendationType(sabmRecommendationModel.getRecommendationType());
		recommendationData.setRecommendedBy(sabmRecommendationModel.getRecommendedBy());
		if (RecommendationType.PRODUCT.equals(sabmRecommendationModel.getRecommendationType()))
		{
			final RecommendationProductData productData = createRecommendationProductData(sabmRecommendationModel.getProductCode(),
					sabmRecommendationModel.getQty(), sabmRecommendationModel.getUnit());

			recommendationData.setProduct(productData);
		}
		else
		{
			if (StringUtils.isNotEmpty(sabmRecommendationModel.getDealCode())
					&& dealsService.getDeal(sabmRecommendationModel.getDealCode()) != null)
			{
				final RecommendationDealJson recommendationDealJson = getDealJson(sabmRecommendationModel);

				recommendationData.setRecommendationDealJson(recommendationDealJson);
			}
			else
			{
				LOG.error("no deal found that is recommended with deal number:", sabmRecommendationModel.getDealCode());
			}
		}
	}

	/**
	 * This method will create a product recommendation data that contains the following: product, quantity, and unit.
	 *
	 * @param productID
	 *           the productID
	 * @param qty
	 *           the quantity
	 * @param unit
	 *           the unit of the product
	 *
	 * @return RecommendationProductData the product recommendation data
	 *
	 */
	private RecommendationProductData createRecommendationProductData(final String productID, final Integer qty,
			final UnitModel unit)
	{
		final RecommendationProductData productData = new RecommendationProductData();
		if (StringUtils.isNotEmpty(productID))
		{
			productData.setProduct(productFacade.getProductForCodeAndOptions(productID,
					Arrays.asList(ProductOption.BASIC, ProductOption.CATEGORIES)));
		}

		productData.setQuantity(qty);
		productData.setMinQuantity(1);
		if (unit != null)
		{
			productData.setUnit(UOMUtils.convertUom(unit));
		}
		return productData;
	}

	/**
	 * this will get the dealJson object base from the deal code given
	 *
	 * @param sabmRecommendationModel
	 *           the deal code
	 * @return dealJson the retrieved dealJson
	 *
	 */
	public RecommendationDealJson getDealJson(final SABMRecommendationModel sabmRecommendationModel)
	{



		final RecommendationDealJson recommendationDealJson = new RecommendationDealJson();
		sabmRecommendationDealJsonPopulator.populate(sabmRecommendationModel, recommendationDealJson);
		return recommendationDealJson;
	}


	public DealsService getDealsService()
	{
		return dealsService;
	}

	public void setDealsService(final DealsService dealsService)
	{
		this.dealsService = dealsService;
	}

	/**
	 * @return the sabmRecommendationDealJsonPopulator
	 */
	public SABMRecommendationDealJsonPopulator getSabmRecommendationDealJsonPopulator()
	{
		return sabmRecommendationDealJsonPopulator;
	}

	/**
	 * @param sabmRecommendationDealJsonPopulator
	 *           the sabmRecommendationDealJsonPopulator to set
	 */
	public void setSabmRecommendationDealJsonPopulator(
			final SABMRecommendationDealJsonPopulator sabmRecommendationDealJsonPopulator)
	{
		this.sabmRecommendationDealJsonPopulator = sabmRecommendationDealJsonPopulator;
	}

}
