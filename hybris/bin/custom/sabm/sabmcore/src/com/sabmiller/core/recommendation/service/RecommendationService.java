package com.sabmiller.core.recommendation.service;

import de.hybris.platform.catalog.model.CatalogVersionModel;
import de.hybris.platform.commercefacades.product.data.ProductData;
import de.hybris.platform.core.model.product.ProductModel;
import de.hybris.platform.core.model.product.UnitModel;
import de.hybris.platform.core.servicelayer.data.SearchPageData;

import java.nio.file.NoSuchFileException;
import java.util.List;
import java.util.Map;
import java.util.Optional;

import com.sabmiller.core.enums.RecommendationStatus;
import com.sabmiller.core.enums.SmartRecommendationType;
import com.sabmiller.core.model.SABMRecommendationModel;
import com.sabmiller.core.model.StagingSABMRecommendationModel;
import com.sabmiller.facades.deal.data.DealBaseProductJson;


/**
 * Created by raul.b.abatol.jr on 06/06/2017.
 */
public interface RecommendationService
{

	/**
	 * This will get all recommendations with recommended status.
	 *
	 * @return recommendationList the retrieved recommendation list
	 */
	public List<SABMRecommendationModel> getRecommendations();

	List<SABMRecommendationModel> getDisplayableRecommendations();
	/**
	 * This will get all recommendations given the recommendation ID.
	 *
	 * @param id
	 *           the recommendation ID
	 * @return recommendationList the retrieved recommendation list
	 */
	public List<SABMRecommendationModel> getRecommendationsByID(String id);

	/**
	 * This will get all recommendations given the productID
	 *
	 * @param productID
	 *           the product ID
	 * @return recommendationList the retrieved recommendation list
	 */
	public List<SABMRecommendationModel> getRecommendationsByProductID(String productID);

	/**
	 * This will get all recommendations given the Deal ID
	 *
	 * @param dealID
	 *           the deal ID
	 * @return recommendationList the retrieved recommendation list
	 */
	public SABMRecommendationModel getRecommendationsByDealID(String dealID);


	/**
	 * This will save the given product, qty and unit as recommendation
	 *
	 * @param productID
	 *           the product ID
	 * @param quantity
	 *           the quatity
	 * @param uom
	 *           the unit model
	 */
	public void saveProductAsRecommendation(String productID, Integer quantity, UnitModel uom);

	/**
	 * this will save the given deal as recommendation
	 *
	 * @param dealID
	 *           the deal ID
	 *
	 */
	public SABMRecommendationModel saveDealAsRecommendation(String dealID, List<DealBaseProductJson> dealProductsList,
			boolean isUpdateRecommendation);


	/**
	 * This will update the given recommendation with the given quantity and unit.
	 *
	 * @param recommendationID
	 *           the recommendation
	 * @param quantity
	 *           the quantity
	 *
	 */
	public void updateProductRecommendation(String recommendationID, Integer quantity, UnitModel uom);

	public void updateDealProductRecommendation(final SABMRecommendationModel dealRecommendation, final String productID,
			final Integer quantity);

	public void updateRecommendation(String recommendationID, RecommendationStatus status);

	/**
	 * This will delete the given recommendation
	 *
	 * @param recommendationID
	 *           the recommendation
	 */
	public void deleteRecommendationByID(String recommendationID);

	/**
	 * this will convert the given quantity to the basic unit
	 *
	 * @param productID
	 *           the product
	 * @param unit
	 *           the unit of the product
	 * @param quantity
	 *           the quantity of the product
	 *
	 */
	public Map<String, Object> getRecommendedQuantity(String productID, UnitModel unit, Integer quantity);

	public void updateRecommendationStatus(SABMRecommendationModel recommendation, RecommendationStatus status);

	public void updateRecommendationsForBulkUpload(final StagingSABMRecommendationModel stagingRecommendationModel,
			final boolean isBannerInfoAvailable) throws Exception;

	/**
	 * Retrieves file from Azure storage containing recommendations and saves it to Hybris
	 * */
	boolean retrieveAndSaveRecommendations(final CatalogVersionModel catalogVersion) throws NoSuchFileException;

	/**
	 * Retrieves file from Azure storage containing recommendations and saves it to Hybris
	 * */
	boolean retrieveAndSaveRecommendationsV2(final CatalogVersionModel catalogVersion) throws NoSuchFileException;

	/**
	 * Retrieves file from Azure storage containing recommendation groupings and saves it to Hybris B2BUnit
	 * */
	boolean retrieveAndSaveRecommendationGroup() throws NoSuchFileException;

	/**
	 *
	 * @return
	 */
	Map<SmartRecommendationType, Optional<ProductModel>> calculateSmartRecommendations();

	Map<String,String> getAllProductRecommendationsInCart();

	Map<SmartRecommendationType, ProductData> getSgaProductRecommendations();
	/**
	 * @param productCode
	 * @param quantity
	 */
	public void saveRepRecommendedProducts(String productCode, Integer quantity);

	/**
	 * @param productCode
	 */
	public void deleteRecommendationByProductId(String productCode);

	/**
	 * @param productCode
	 * @param quantity
	 */
	void updateProductRecommendation(String productCode, Integer quantity);

	/**
	 *
	 */
	public void deleteAllRecommendations();

	/**
	 * @param searchData
	 * @param sortCode
	 * @return
	 */
	SearchPageData<SABMRecommendationModel> getPageableRecommendations(SearchPageData searchData, String sortCode);


}
