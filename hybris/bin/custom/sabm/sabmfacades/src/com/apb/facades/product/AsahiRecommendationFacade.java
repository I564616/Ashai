package com.apb.facades.product;

import de.hybris.platform.commercefacades.product.data.ProductData;
import de.hybris.platform.core.servicelayer.data.SearchPageData;

import java.util.Map;

import com.sabmiller.core.enums.SmartRecommendationType;
import com.sabmiller.facades.recommendation.data.RecommendationData;

public interface AsahiRecommendationFacade
{

	/**
	 * Save product recommendations.
	 *
	 * @param productID
	 *           the product ID
	 * @param quantity
	 *           the quantity
	 */
	void saveProductRecommendations(String productID, Integer quantity);

	/**
	 * Update recommendation.
	 *
	 * @param productID
	 *           the product ID
	 * @param quantity
	 *           the quantity
	 * @return
	 */
	public boolean updateProductRecommendation(String productID, Integer quantity);

	/**
	 * Gets the asahi product recommendations.
	 *
	 * @param searchData
	 * @param sortCode
	 *
	 * @return the asahi product recommendations
	 */
	public SearchPageData<RecommendationData> getAsahiProductRecommendations(SearchPageData searchData, String sortCode);

	/**
	 * Gets the total rep recommended products.
	 *
	 * @return the total rep recommended products
	 */
	public Integer getTotalRepRecommendedProducts();

	/**
	 * Delete recommendation by product id.
	 *
	 * @param recommendationIdToRemove
	 *           the recommendation id to remove
	 * @return true, if successful
	 */
	public boolean deleteRecommendationByProductId(String recommendationIdToRemove);

	/**
	 * Delete all recommendations.
	 *
	 * @return true, if successful
	 */
	public boolean deleteAllRecommendations();

	/**
	 * @return
	 */
	Map<SmartRecommendationType, ProductData> getSgaProductRecommendations();
}
