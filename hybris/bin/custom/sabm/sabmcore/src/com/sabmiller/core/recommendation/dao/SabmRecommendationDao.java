/**
 *
 */
package com.sabmiller.core.recommendation.dao;

import de.hybris.platform.b2b.model.B2BUnitModel;
import de.hybris.platform.core.model.product.ProductModel;
import de.hybris.platform.core.servicelayer.data.SearchPageData;

import java.util.Date;
import java.util.List;

import com.sabmiller.core.enums.SmartRecommendationType;
import com.sabmiller.core.model.AsahiCatalogProductMappingModel;
import com.sabmiller.core.model.SABMRecommendationModel;
import com.sabmiller.core.model.SmartRecommendationModel;


/**
 * @author raul.b.abatol.jr
 *
 */
public interface SabmRecommendationDao
{

	public List<SABMRecommendationModel> getRecommendations(B2BUnitModel b2bUnit);

	public List<SABMRecommendationModel> getRecommendationsByID(B2BUnitModel b2bUnit, String id);

	public List<SABMRecommendationModel> getRecommendationsByProductID(B2BUnitModel b2bUnit, String productID);

	public List<SABMRecommendationModel> getRecommendationsByDealID(B2BUnitModel b2bUnit, String dealID);

	/**
	 * @param expiryDate
	 * @return
	 */
	public List<SABMRecommendationModel> getEligibleRecommendationsForExpiry(Date expiryDate);

	public List<SABMRecommendationModel> getAllRecommendations();

	public SmartRecommendationModel getSmartRecommendation(final ProductModel product, final SmartRecommendationType type);

	List<SmartRecommendationModel> getAllSmartRecommendations();

	/**
	 * @param selectedB2BUnit
	 * @param searchData
	 * @param sortCode
	 * @return
	 */
	public SearchPageData<SABMRecommendationModel> getPagedRecommendationsByB2BUnit(B2BUnitModel selectedB2BUnit,
			SearchPageData searchData, String sortCode);
			
			
	public List<AsahiCatalogProductMappingModel> getProductMappingBasedOnCatalogId(List<String> catalogIds);

}
