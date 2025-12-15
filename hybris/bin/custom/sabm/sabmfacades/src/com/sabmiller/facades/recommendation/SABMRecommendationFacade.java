package com.sabmiller.facades.recommendation;

import de.hybris.platform.commercefacades.product.data.ProductData;

import java.util.List;
import java.util.Map;
import java.util.Optional;

import com.sabmiller.core.enums.RecommendationStatus;
import com.sabmiller.core.enums.SmartRecommendationType;
import com.sabmiller.core.model.SABMRecommendationModel;
import com.sabmiller.facades.deal.data.DealBaseProductJson;
import com.sabmiller.facades.recommendation.data.RecommendationData;


/**
 * Created by evariz.d.paragoso on 6/6/17.
 */
public interface SABMRecommendationFacade
{
	/**
	 * Gets list of all recommendations of the current b2bunit
	 *
	 * @return recommendationList the retrieved recommendation list
	 */
	public List<RecommendationData> getRecommendations();

	/**
	 * Returns only the total size of recommendations as used for the header. a simplified version of {@link #getRecommendations()}
	 * @return
	 */
	int getTotalRecommendations();

	/**
	 * This will save the given product, qty and unit as recommendation
	 *
	 * @param productID
	 *           the product ID
	 * @param quantity
	 *           the quatity
	 * @param uom
	 *           the unit
	 */
	public void saveProductAsRecommendation(String productID, Integer quantity, String uom);

	public SABMRecommendationModel getRecommendationByID(final String recommendationID);


	public SABMRecommendationModel getRecommendationByDealID(final String dealCode);

	/**
	 * This will update the given recommendation with the given quantity and unit.
	 *
	 * @param recommendationID
	 *           the recommendation
	 * @param quantity
	 *           the quantity
	 * @param uom
	 *           the unit
	 *
	 */
	public Boolean updateProductRecommendation(String recommendationID, Integer quantity, String uom);

	public void updateRecommendation(String recommendationID, RecommendationStatus status);

	/**
	 * This will delete the given recommendation
	 *
	 * @param recommendationID
	 *           the recommendation
	 */
	public void deleteRecommendationByID(String recommendationID);


	/**
	 * this will save the given deal as recommendation
	 *
	 * @param dealID
	 *           the deal ID
	 *
	 */
	public SABMRecommendationModel saveDealAsRecommendation(String dealID, List<DealBaseProductJson> dealProductsList);

	/**
	 * This will check if a product with the qty and uom have a corresponding recommendation and sets it to accepted.
	 *
	 * @param productId
	 *           the id of the product
	 * @param qty
	 *           the quantity of the product
	 * @param uom
	 *           the unit of the product
	 */
	public void checkProductForRecommendation(String productId, Integer qty, String uom);

	/**
	 * This will check if a deal with the product, qty and uom have a corresponding recommendation and sets it to
	 * accepted.
	 *
	 * @param dealRecommendations
	 *           the deal to be checked
	 * @param productId
	 *           the id of the product
	 * @param qty
	 *           the quantity of the product
	 * @param uom
	 *           the unit of the product
	 * @return isChecked value is true if there is corresponding recommendation
	 */
	public Boolean checkDealForRecommendation(SABMRecommendationModel dealRecommendations, String productId, Integer qty,
			String uom);

	/**
	 * This will update the given recommendation into the given status.
	 *
	 * @param recommendation
	 *           the recommendation to be set
	 * @param status
	 *           the status to set
	 *
	 */
	public void updateRecommendationStatus(SABMRecommendationModel recommendation, RecommendationStatus status);

	/**
	 * @param dealID
	 * @param dealProductsList
	 * @return
	 */
	SABMRecommendationModel addDealAsRecommendation(String dealID, List<DealBaseProductJson> dealProductsList);

	/**
	 * Return a single product
	 * @return
	 */
	Map<SmartRecommendationType, Optional<ProductData>> calculateSmartRecommendations();

	Map<String,String> getAllProductRecommendationsInCart();

	String getCurrentSmartRecommendationGroup();

	/**
	 * @return
	 */
	public List<RecommendationData> getAsahiProductRecommendations();
}
