package com.apb.facades.product;

import de.hybris.platform.commercefacades.product.ProductOption;
import de.hybris.platform.commercefacades.product.data.ProductData;
import de.hybris.platform.core.servicelayer.data.SearchPageData;
import de.hybris.platform.servicelayer.exceptions.AmbiguousIdentifierException;
import de.hybris.platform.servicelayer.exceptions.UnknownIdentifierException;
import de.hybris.platform.servicelayer.session.SessionService;
import de.hybris.platform.servicelayer.user.UserService;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Map;

import jakarta.annotation.Resource;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.apb.core.constants.ApbCoreConstants;
import com.apb.integration.data.AsahiProductInfo;
import com.sabmiller.core.enums.RecommendationType;
import com.sabmiller.core.enums.SmartRecommendationType;
import com.sabmiller.core.model.SABMRecommendationModel;
import com.sabmiller.core.recommendation.service.RecommendationService;
import com.sabmiller.facades.populators.SabmRecommendationPopulator;
import com.sabmiller.facades.recommendation.data.RecommendationData;


public class AsahiRecommendationFacadeImpl implements AsahiRecommendationFacade
{
	/** The Constant LOG. */
	private static final Logger LOG = LoggerFactory.getLogger(AsahiRecommendationFacadeImpl.class);

	protected static final List<ProductOption> SMART_RECOMMENDATIONS_PRODUCT_OPTIONS = Arrays.asList(ProductOption.BASIC, ProductOption.PRICE, ProductOption.CATEGORIES);


	@Resource(name = "recommendationService")
	RecommendationService recommendationService;

	@Resource(name = "recommendationPopulator")
	private SabmRecommendationPopulator recommendationPopulator;

	@Resource(name = "sessionService")
	private SessionService sessionService;

	@Resource(name = "userService")
	private UserService userService;



	@Override
	public SearchPageData<RecommendationData> getAsahiProductRecommendations(final SearchPageData searchData,
			final String sortCode)
	{
		final SearchPageData<RecommendationData> recommendationData = new SearchPageData<RecommendationData>();
		final SearchPageData<SABMRecommendationModel> recommendations = recommendationService
				.getPageableRecommendations(searchData, sortCode);
		final Map<String, AsahiProductInfo> inclusionList = sessionService.getAttribute(ApbCoreConstants.CUSTOMER_SESSION_INCLUSION_LIST);
		final List<RecommendationData> recommendationsList = new ArrayList<RecommendationData>();
		for (final SABMRecommendationModel recommendationModel : recommendations.getResults())
		{
			final RecommendationData recommendation = new RecommendationData();
			try
			{
				recommendationPopulator.populate(recommendationModel, recommendation);
				recommendation.getProduct().setQuantity(recommendationModel.getQty());
				if (isRecommendedProductToBeDisplayed(recommendation, inclusionList))
				{
					recommendationsList.add(recommendation);
				}

			}
			catch (final Exception e)
			{
				LOG.error("Unable to fetch recommendation since product or deal may not exist any more", e);
			}


		}
		recommendationData.setResults(recommendationsList);
		recommendationData.setPagination(recommendations.getPagination());
		return recommendationData;
	}


	/**
	 * @param recommendationData
	 * @param inclusionList
	 */
	private boolean isRecommendedProductToBeDisplayed(final RecommendationData recommendationData,
			final Map<String, AsahiProductInfo> inclusionList)
	{
		boolean displayProduct = true;
		final String productCode= recommendationData.getProduct().getProduct().getCode();
		if (recommendationData.getRecommendationType().equals(RecommendationType.PRODUCT))
		{
			AsahiProductInfo productData = null;

			if (null != inclusionList && !inclusionList.isEmpty())
			{
				productData = inclusionList.entrySet().stream().filter(entry -> entry.getKey().equalsIgnoreCase(productCode))
						.map(Map.Entry::getValue).findFirst().orElse(null);
			}

			if (null == productData)
			{
				displayProduct = false;
			}

		}
		return displayProduct;

	}

	/**
	 * Save product recommendations.
	 *
	 * @param productCode
	 *           the product code
	 * @param quantity
	 *           the quantity
	 */
	public void saveProductRecommendations(final String productCode, final Integer quantity)
	{

		try
		{

			if (quantity != null)
			{
				recommendationService.saveRepRecommendedProducts(productCode, quantity);
			}
		}
		catch (AmbiguousIdentifierException | UnknownIdentifierException | IllegalArgumentException e)
		{
			LOG.debug(e.getMessage(), e);
			LOG.warn("Error fetching product with code: " + productCode);
		}

	}


	/**
	 * Update product recommendation.
	 *
	 * @param productCode
	 *           the product code
	 * @param quantity
	 *           the quantity
	 * @return true, if successful
	 */
	@Override
	public boolean updateProductRecommendation(final String productCode, final Integer quantity)
	{
		try
		{
			recommendationService.updateProductRecommendation(productCode, quantity);
			return true;
		}
		catch (final Exception ex)
		{
			LOG.error("Error updating recommendation for product: " + productCode);
			return false;
		}

	}


	@Override
	public Integer getTotalRepRecommendedProducts()
	{
		final List<SABMRecommendationModel> recommendations = recommendationService.getRecommendations();
		return recommendations.size();
	}


	@Override
	public boolean deleteRecommendationByProductId(final String productCode)
	{
		try
		{
			recommendationService.deleteRecommendationByProductId(productCode);
			return true;
		}
		catch (final Exception ex)
		{
			LOG.error("Error updating recommendation for product: " + productCode);
			return false;
		}
	}


	@Override
	public boolean deleteAllRecommendations()
	{
		try
		{
			recommendationService.deleteAllRecommendations();
			return true;
		}
		catch (final Exception ex)
		{
			LOG.error("Error occured while removing all recommendations");
			return false;
		}

	}


	@Override
	public Map<SmartRecommendationType, ProductData> getSgaProductRecommendations()
	{

		return recommendationService.getSgaProductRecommendations();

	}

}
