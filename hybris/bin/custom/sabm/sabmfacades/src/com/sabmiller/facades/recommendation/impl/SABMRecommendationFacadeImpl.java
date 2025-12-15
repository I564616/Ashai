package com.sabmiller.facades.recommendation.impl;

import de.hybris.platform.b2b.model.B2BUnitModel;
import de.hybris.platform.basecommerce.enums.StockLevelStatus;
import de.hybris.platform.commercefacades.product.ProductOption;
import de.hybris.platform.commercefacades.product.data.ProductData;
import de.hybris.platform.converters.ConfigurablePopulator;
import de.hybris.platform.core.model.product.ProductModel;
import de.hybris.platform.core.model.product.UnitModel;
import de.hybris.platform.product.ProductService;
import de.hybris.platform.product.UnitService;
import de.hybris.platform.servicelayer.dto.converter.Converter;
import de.hybris.platform.servicelayer.exceptions.AmbiguousIdentifierException;
import de.hybris.platform.servicelayer.exceptions.UnknownIdentifierException;
import de.hybris.platform.servicelayer.session.SessionService;
import de.hybris.platform.servicelayer.user.UserService;

import java.util.AbstractMap;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.stream.Collectors;

import jakarta.annotation.Resource;

import org.apache.commons.collections4.CollectionUtils;
import org.apache.commons.collections4.ListUtils;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.cache.annotation.CacheEvict;

import com.sabmiller.core.b2b.services.SABMProductExclusionService;
import com.sabmiller.core.b2b.services.SabmB2BUnitService;
import com.sabmiller.core.constants.SabmCoreConstants;
import com.sabmiller.core.deals.services.DealsServiceImpl;
import com.sabmiller.core.enums.RecommendationGroupType;
import com.sabmiller.core.enums.RecommendationStatus;
import com.sabmiller.core.enums.RecommendationType;
import com.sabmiller.core.enums.SmartRecommendationType;
import com.sabmiller.core.model.BDECustomerModel;
import com.sabmiller.core.model.DealModel;
import com.sabmiller.core.model.ProductExclusionModel;
import com.sabmiller.core.model.SABMRecommendationDPModel;
import com.sabmiller.core.model.SABMRecommendationModel;
import com.sabmiller.core.recommendation.service.RecommendationService;
import com.sabmiller.facades.deal.data.DealBaseProductJson;
import com.sabmiller.facades.deal.data.DealRangeJson;
import com.sabmiller.facades.populators.SabmRecommendationPopulator;
import com.sabmiller.facades.recommendation.SABMRecommendationFacade;
import com.sabmiller.facades.recommendation.data.RecommendationData;


/**
 * Created by evariz.d.paragoso on 6/6/17.
 */
public class SABMRecommendationFacadeImpl implements SABMRecommendationFacade
{
	/** The Constant LOG. */
	private static final Logger LOG = LoggerFactory.getLogger(SABMRecommendationFacadeImpl.class);

	protected static final List<ProductOption> SMART_RECOMMENDATIONS_PRODUCT_OPTIONS = Arrays.asList(ProductOption.BASIC, ProductOption.PRICE, ProductOption.CATEGORIES);


	@Resource(name = "recommendationService")
	RecommendationService recommendationService;

	@Resource(name = "recommendationPopulator")
	private SabmRecommendationPopulator recommendationPopulator;


	@Resource(name = "unitService")
	UnitService unitService;

	@Resource(name = "productService")
	ProductService productService;

	/** The product exclusion service. */
	@Resource(name = "sabmProductExclusionService")
	private SABMProductExclusionService productExclusionService;


	@Resource(name = "defaultDealsService")
	private DealsServiceImpl dealsService;


	@Resource(name = "sessionService")
	private SessionService sessionService;


	@Resource(name = "userService")
	private UserService userService;

	@Resource(name = "productConverter")
	private Converter<ProductModel,ProductData> productConverter;

	@Resource(name = "productVariantConfiguredPopulator")
	private ConfigurablePopulator<ProductModel, ProductData, ProductOption> productVariantConfiguredPopulator;

	@Resource(name = "sabmB2BUnitService")
	private SabmB2BUnitService sabmB2BUnitService;

	@Override
	//	Commented for Cache issues on Recommendation component
	//	@Cacheable(value = "recommendationCache", key = "T(com.sabmiller.cache.impl.SabmCacheKeyGenerator).generateKey(false,true,false,'recommendations')")
	public List<RecommendationData> getRecommendations()
	{
		final List<RecommendationData> reccommendationsData = new ArrayList<RecommendationData>();
		final List<SABMRecommendationModel> recommendations = recommendationService.getRecommendations();
		for (final SABMRecommendationModel recommendationModel : recommendations)
		{
			final RecommendationData recommendationData = new RecommendationData();
			try
			{
				recommendationPopulator.populate(recommendationModel, recommendationData);
				recommendationData.setIsInDeliveryPackType(isRecommendationForDelivery(recommendationData));
				if (recommendationData.getRecommendationType().equals(RecommendationType.DEAL))
				{
					if (recommendationData.getRecommendationDealJson() != null)
					{
						recommendationData.getRecommendationDealJson()
								.setIsInDeliveryPackType(isRecommendationForDelivery(recommendationData));
					}
				}
				if (isRecommendationToBeDisplayed(recommendationData))
				{
					reccommendationsData.add(recommendationData);
				}
			}
			catch (final Exception e)
			{
				LOG.error("Unable to fetch recommendation since product or deal may not exist any more", e);
			}


		}

		return reccommendationsData;
	}

	@Override
	public int getTotalRecommendations()
	{
		return recommendationService.getDisplayableRecommendations().size();
	}

	@Override
	@CacheEvict(value = "recommendationCache", key = "T(com.sabmiller.cache.impl.SabmCacheKeyGenerator).generateKey(false,true,false,'recommendations')")
	public void saveProductAsRecommendation(final String productID, final Integer quantity, final String uom)
	{
		UnitModel unitm = null;
		try
		{
			try
			{
				unitm = unitService.getUnitForCode(StringUtils.upperCase(uom));
			}
			catch (final UnknownIdentifierException | AmbiguousIdentifierException e)
			{
				LOG.warn("Unit with code " + uom + " not found! ");
			}

			final Map<String, Object> qtyMap = recommendationService.getRecommendedQuantity(productID, unitm, quantity);
			if (quantity != null)
			{
				recommendationService.saveProductAsRecommendation(productID,
						(Integer) qtyMap.get(SabmCoreConstants.RECOMMENDATION_QTY),
						(UnitModel) qtyMap.get(SabmCoreConstants.RECOMMENDATION_UNIT));
			}
		}
		catch (AmbiguousIdentifierException | UnknownIdentifierException | IllegalArgumentException e)
		{
			LOG.debug(e.getMessage(), e);
			LOG.warn("Error fetching product with code: " + productID);
		}

	}

	@Override
	@CacheEvict(value = "recommendationCache", key = "T(com.sabmiller.cache.impl.SabmCacheKeyGenerator).generateKey(false,true,false,'recommendations')")
	public SABMRecommendationModel saveDealAsRecommendation(final String dealID, final List<DealBaseProductJson> dealProductsList)
	{
		return recommendationService.saveDealAsRecommendation(dealID, dealProductsList, true);


	}

	@Override
	@CacheEvict(value = "recommendationCache", key = "T(com.sabmiller.cache.impl.SabmCacheKeyGenerator).generateKey(false,true,false,'recommendations')")
	public SABMRecommendationModel addDealAsRecommendation(final String dealID, final List<DealBaseProductJson> dealProductsList)
	{
		return recommendationService.saveDealAsRecommendation(dealID, dealProductsList, false);


	}


	@Override
	public SABMRecommendationModel getRecommendationByID(final String recommendationID)
	{
		final List<SABMRecommendationModel> recommendations = recommendationService.getRecommendationsByID(recommendationID);
		if (CollectionUtils.isNotEmpty(recommendations))
		{
			return recommendations.get(0);
		}
		else
		{
			LOG.debug("No recommendation record found for id {}", recommendationID);
			return null;
		}
	}

	@Override
	@CacheEvict(value = "recommendationCache", key = "T(com.sabmiller.cache.impl.SabmCacheKeyGenerator).generateKey(false,true,false,'recommendations')")
	public Boolean updateProductRecommendation(final String recommendationID, final Integer quantity, final String uom)
	{
		UnitModel unitm = null;
		try
		{
			unitm = unitService.getUnitForCode(StringUtils.upperCase(uom));
		}
		catch (final UnknownIdentifierException | AmbiguousIdentifierException e)
		{
			LOG.warn("Unit with code " + uom + " not found! " + e, e);
		}

		recommendationService.updateProductRecommendation(recommendationID, quantity, unitm);

		return true;
	}



	@Override
	@CacheEvict(value = "recommendationCache", key = "T(com.sabmiller.cache.impl.SabmCacheKeyGenerator).generateKey(false,true,false,'recommendations')")
	public void updateRecommendation(final String recommendationID, final RecommendationStatus status)
	{
		recommendationService.updateRecommendation(recommendationID, status);
	}

	@Override
	@CacheEvict(value = "recommendationCache", key = "T(com.sabmiller.cache.impl.SabmCacheKeyGenerator).generateKey(false,true,false,'recommendations')")
	public void deleteRecommendationByID(final String recommendationID)
	{
		recommendationService.deleteRecommendationByID(recommendationID);
	}

	@Override
	public void checkProductForRecommendation(final String productId, final Integer qty, final String uom)
	{
		final List<SABMRecommendationModel> recommendations = recommendationService.getRecommendationsByProductID(productId);

		if (CollectionUtils.isNotEmpty(recommendations))
		{
			final SABMRecommendationModel recommendation = recommendations.get(0);
			if (qty > 0)
			{
				final Map<String, Object> qtyMap = this.getRecommendedQuantity(productId, qty, uom);
				final Integer recommendedQty = (Integer) qtyMap.get(SabmCoreConstants.RECOMMENDATION_QTY);
				final Map<String, Object> qtyMapRecommendation = this.getRecommendedQuantity(recommendation.getProductCode(),
						recommendation.getQty(), recommendation.getUnit().getCode());
				final Integer recommendedQtyCurrent = (Integer) qtyMapRecommendation.get(SabmCoreConstants.RECOMMENDATION_QTY);
				if (recommendedQtyCurrent <= recommendedQty)
				{
					recommendationService.updateRecommendationStatus(recommendation, RecommendationStatus.ACCEPTED);
				}
			}
		}
	}

	@Override
	public Boolean checkDealForRecommendation(final SABMRecommendationModel dealRecommendation, final String productId,
			final Integer qty, final String uom)
	{
		Boolean isChecked = false;
		if (dealRecommendation != null)
		{
			final List<SABMRecommendationDPModel> dealProducts = dealRecommendation.getDealProducts();

			if (CollectionUtils.isNotEmpty(dealProducts))
			{
				for (final SABMRecommendationDPModel recommendation : dealProducts)
				{
					if (qty > 0)
					{
						if (recommendation.getProductCode().equals(productId))
						{
							final Map<String, Object> qtyMap = this.getRecommendedQuantity(productId, qty, uom);
							final Integer recommendedQty = (Integer) qtyMap.get(SabmCoreConstants.RECOMMENDATION_QTY);
							final Map<String, Object> qtyMapRecommendation = this.getRecommendedQuantity(recommendation.getProductCode(),
									recommendation.getQty(), recommendation.getUnit().getCode());
							final Integer recommendedQtyCurrent = (Integer) qtyMapRecommendation
									.get(SabmCoreConstants.RECOMMENDATION_QTY);
							if (recommendedQtyCurrent <= recommendedQty)
							{
								isChecked = true;
							}
							break;
						}
					}
				}
			}
		}

		return isChecked;
	}

	/**
	 * this will convert the given quantity to the basic unit
	 *
	 * @param productID
	 *           the product
	 * @param uom
	 *           the unit of the product
	 * @param quantity
	 *           the quantity of the product
	 *
	 */
	public Map<String, Object> getRecommendedQuantity(final String productID, final Integer quantity, final String uom)
	{
		UnitModel unitm = null;
		try
		{
			try
			{
				unitm = unitService.getUnitForCode(StringUtils.upperCase(uom));
			}
			catch (final UnknownIdentifierException | AmbiguousIdentifierException e)
			{
				LOG.warn("Unit with code " + uom + " not found! " + e, e);
			}


			final Map<String, Object> qtyMap = recommendationService.getRecommendedQuantity(productID, unitm, quantity);
			return qtyMap;

		}
		catch (AmbiguousIdentifierException | UnknownIdentifierException | IllegalArgumentException e)
		{
			LOG.debug(e.getMessage(), e);
			LOG.warn("Error fetching product with code: " + productID);
			return Collections.EMPTY_MAP;
		}

	}


	public SABMRecommendationModel getRecommendationByDealID(final String dealCode)
	{
		return recommendationService.getRecommendationsByDealID(dealCode);
	}

	@Override
	@CacheEvict(value = "recommendationCache", key = "T(com.sabmiller.cache.impl.SabmCacheKeyGenerator).generateKey(false,true,false,'recommendations')")
	public void updateRecommendationStatus(final SABMRecommendationModel recommendation, final RecommendationStatus status)
	{
		recommendationService.updateRecommendationStatus(recommendation, status);
	}

	public Boolean isRecommendationToBeDisplayed(final RecommendationData recommendationData)
	{
		Boolean isRecommendationToBeDisplayed = true;
		if (recommendationData.getRecommendationType().equals(RecommendationType.PRODUCT))
		{
			final List<ProductExclusionModel> productExList = productExclusionService.findProductEx();
			if (recommendationData.getProduct() == null)
			{
				isRecommendationToBeDisplayed = false;
			}
			else if (recommendationData.getProduct().getProduct().getCubStockStatus() != null
					&& recommendationData.getProduct().getProduct().getCubStockStatus().equals(StockLevelStatus.OUTOFSTOCK)
					&& !(userService.getCurrentUser() instanceof BDECustomerModel))
			{
				isRecommendationToBeDisplayed = false;
			}
			if (CollectionUtils.isNotEmpty(productExList))
			{
				for (final ProductExclusionModel excProduct : productExList)
				{
					final String excCode = excProduct.getProduct();
					if (recommendationData.getProduct().getProduct().getCode().equals(excCode))
					{
						isRecommendationToBeDisplayed = false;
					}
				}
			}
		}
		else
		{
			if (recommendationData != null && recommendationData.getRecommendationDealJson() != null)
			{
				final DealModel deal = dealsService.getDeal(recommendationData.getRecommendationDealJson().getCode());
				if (dealsService.isValidityPeriod(sessionService.getAttribute(SabmCoreConstants.SESSION_ATTR_DELIVERY_DATE), deal,
						true))
				{
					isRecommendationToBeDisplayed = dealsService.isValidDeal(deal);
				}
				else
				{
					isRecommendationToBeDisplayed = false;
				}
			}
			else
			{
				isRecommendationToBeDisplayed = false;
			}
			if (!(userService.getCurrentUser() instanceof BDECustomerModel))
			{
				if (recommendationData != null && recommendationData.getRecommendationDealJson() != null)
				{
					for (final DealRangeJson rangeJson : recommendationData.getRecommendationDealJson().getRanges())
					{
						for (final DealBaseProductJson baseProduct : rangeJson.getBaseProducts())
						{
							if (baseProduct.getCubStockStatus() != null
									&& baseProduct.getCubStockStatus().equals(StockLevelStatus.OUTOFSTOCK))
							{
								if (rangeJson.getBaseProducts().size() == 1)
								{
									isRecommendationToBeDisplayed = false;
									break;
								}
							}
						}
					}
				}
			}
		}
		return isRecommendationToBeDisplayed;
	}

	public Boolean isRecommendationForDelivery(final RecommendationData recommendation)
	{

		if (userService.getCurrentUser() instanceof BDECustomerModel)
		{
			return true;
		}
		final String deliveryDatePackType = sessionService.getAttribute(SabmCoreConstants.SESSION_ATTR_DELIVERY_DATE_PACKTYPE);
		LOG.debug("Deliver Date Pack Type: " + deliveryDatePackType);
		if (deliveryDatePackType != null)
		{
			if (recommendation.getRecommendationType() == RecommendationType.DEAL)
			{
				if (recommendation.getRecommendationDealJson() != null)
				{
					for (final DealRangeJson dealRanges : ListUtils
							.emptyIfNull(recommendation.getRecommendationDealJson().getRanges()))
					{
						for (final DealBaseProductJson productJson : dealRanges.getBaseProducts())
						{
							String packType = "KEG";
							if (!productJson.getUomS().toUpperCase().equals("KEG"))
							{
								packType = "PACK";
							}

							if (deliveryDatePackType.indexOf(packType) == -1)
							{
								return false;
							}
						}
					}
				}

			}
			else
			{
				String packType = "KEG";
				if (recommendation.getProduct() != null && recommendation.getProduct().getUnit() != null
						&& !recommendation.getProduct().getUnit().getCode().toUpperCase().equals("KEG"))
				{
					packType = "PACK";
				}

				if (deliveryDatePackType.indexOf(packType) == -1)
				{
					return false;
				}
			}
		}
		else
		{
			return false;
		}

		return true;
	}

	@Override
	public Map<SmartRecommendationType, Optional<ProductData>> calculateSmartRecommendations() {
		final Map<SmartRecommendationType,Optional<ProductModel>>smartRecommendationModelMap = recommendationService.calculateSmartRecommendations();
		return smartRecommendationModelMap.entrySet().stream().map((e)-> new AbstractMap.SimpleEntry<>(e.getKey(), convertProduct(e.getValue()))).collect(Collectors.toMap(Map.Entry::getKey,Map.Entry::getValue));
	}

	@Override
	public Map<String,String> getAllProductRecommendationsInCart(){
		try {
			return recommendationService.getAllProductRecommendationsInCart();
		}catch (final Throwable e){
			LOG.error("An error occured calculating the recommendation",e);
		}

		return Collections.emptyMap();
	}

	@Override
	public String getCurrentSmartRecommendationGroup() {
		final B2BUnitModel b2BUnit = sabmB2BUnitService.getB2BUnitInCurrentSession();
		if (null != b2BUnit && null != b2BUnit.getRecommendationGroup()) {
			return b2BUnit.getRecommendationGroup().getCode();
		}
		return RecommendationGroupType.A.getCode();
	}

	protected Optional<ProductData> convertProduct(final Optional<ProductModel> product) {

		if(!product.isPresent()){
			return Optional.empty();
		}

		final ProductModel p = product.get();
		final ProductData productData = getProductConverter().convert(p);
		getProductVariantConfiguredPopulator().populate(p,productData,SMART_RECOMMENDATIONS_PRODUCT_OPTIONS);
		return Optional.of(productData);
	}

    public Converter<ProductModel, ProductData> getProductConverter() {
        return productConverter;
    }

    public ConfigurablePopulator<ProductModel, ProductData, ProductOption> getProductVariantConfiguredPopulator() {
        return productVariantConfiguredPopulator;
    }

	@Override
	public List<RecommendationData> getAsahiProductRecommendations()
	{
		// YTODO Auto-generated method stub
		return null;
	}
}
