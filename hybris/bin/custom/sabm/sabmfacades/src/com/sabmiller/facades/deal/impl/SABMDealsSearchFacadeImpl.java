/**
 *
 */
package com.sabmiller.facades.deal.impl;

import de.hybris.platform.b2b.company.B2BCommerceUnitService;
import de.hybris.platform.b2b.model.B2BUnitModel;
import de.hybris.platform.commercefacades.product.ProductFacade;
import de.hybris.platform.commercefacades.product.ProductOption;
import de.hybris.platform.commercefacades.product.data.CategoryData;
import de.hybris.platform.commercefacades.search.data.SearchStateData;
import de.hybris.platform.commerceservices.search.facetdata.FacetData;
import de.hybris.platform.commerceservices.search.facetdata.FacetValueData;
import de.hybris.platform.converters.Converters;
import de.hybris.platform.converters.Populator;
import de.hybris.platform.core.model.order.CartModel;
import de.hybris.platform.core.model.user.EmployeeModel;
import de.hybris.platform.core.model.user.UserModel;
import de.hybris.platform.servicelayer.dto.converter.ConversionException;
import de.hybris.platform.servicelayer.dto.converter.Converter;
import de.hybris.platform.servicelayer.session.SessionService;

import java.io.UnsupportedEncodingException;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Calendar;
import java.util.Collections;
import java.util.Comparator;
import java.util.Date;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.stream.Collectors;

import jakarta.annotation.Resource;

import org.apache.commons.collections4.CollectionUtils;
import org.apache.commons.lang3.BooleanUtils;
import org.apache.commons.lang3.StringUtils;
import org.apache.commons.lang3.time.DateFormatUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;

import com.apb.facades.deal.data.AsahiDealData;
import com.google.common.collect.Lists;
import com.google.common.collect.Maps;
import com.sabmiller.core.b2b.services.SabmB2BCustomerService;
import com.sabmiller.core.b2b.services.SabmB2BUnitService;
import com.sabmiller.core.cart.service.SABMCartService;
import com.sabmiller.core.comparators.DealBrandMinQtyComparator;
import com.sabmiller.core.constants.SabmCoreConstants;
import com.sabmiller.core.deals.services.DealsCacheService;
import com.sabmiller.core.deals.services.response.PartialDealQualificationResponse;
import com.sabmiller.core.enums.DealConditionStatus;
import com.sabmiller.core.model.AsahiB2BUnitModel;
import com.sabmiller.core.model.AsahiDealModel;
import com.sabmiller.core.model.DealModel;
import com.sabmiller.facades.cart.SABMCartFacade;
import com.sabmiller.facades.customer.SABMCustomerFacade;
import com.sabmiller.facades.deal.DealPageData;
import com.sabmiller.facades.deal.SABMDealsSearchFacade;
import com.sabmiller.facades.deal.data.CartDealsJson;
import com.sabmiller.facades.deal.data.DealConditionData;
import com.sabmiller.facades.deal.data.DealData;
import com.sabmiller.facades.deal.data.DealJson;
import com.sabmiller.facades.deal.data.PartiallyQualifiedDealsJson;
import com.sabmiller.facades.deal.repdriven.data.RepDrivenDealConditionData;
import com.sabmiller.facades.util.FacetComparator;


public class SABMDealsSearchFacadeImpl extends SABMDealsFacadeImpl implements SABMDealsSearchFacade
{
	private static final Logger LOG = LoggerFactory.getLogger(SABMDealsSearchFacadeImpl.class.getName());
	protected static final List<ProductOption> ASAHI_DEAL_PRODUCT_OPTIONS = Arrays.asList(ProductOption.BASIC,
			ProductOption.PRICE);
	private static final Long ALB_DEAL_LIMIT = 5L;
	@Resource(name = "b2bCommerceUnitService")
	private B2BCommerceUnitService b2bCommerceUnitService;

	@Resource(name = "dealJsonConverter")
	private Converter<List<DealModel>, DealJson> dealJsonConverter;

	/**
	 * Converts a Partially Available Deal into a Deal Json.
	 */
	@Resource(name = "partiallyQualifiedDealsJsonConverter")
	private Converter<PartialDealQualificationResponse, PartiallyQualifiedDealsJson> pqdJsonConverter;

	@Resource(name = "sabmB2BCustomerService")
	private SabmB2BCustomerService sabmB2BCustomerService;

	@Resource(name = "b2bUnitService")
	private SabmB2BUnitService b2bUnitService;

	@Resource(name = "dealTitlePopulator")
	private Populator<List<DealModel>, DealJson> dealTitlePopulator;

	@Resource(name = "cartService")
	private SABMCartService sabmCartService;

	@Resource(name = "sessionService")
	private SessionService sessionService;

	@Resource(name = "sabmCartFacade")
	private SABMCartFacade cartFacade;

	@Resource(name = "customerFacade")
	private SABMCustomerFacade customerFacade;

	@Resource(name = "dealsCacheService")
	private DealsCacheService dealsCacheService;

	@Resource(name = "asahiDealDataConverter")
	private Converter<AsahiDealModel, AsahiDealData> asahiDealDataConverter;
	@Resource(name = "productVariantFacade")
	private ProductFacade productFacade;

	@Value(value = "${complex.deal.code.pre.fix:000000}")
	private String complexDealCodePreFix;

	private static final String CONDITIONTYPE_PRODUCTCONDITION = "PRODUCTCONDITION";
	protected static final String DEAL_CATEGORY_LABEL = "category";
	protected static final String DEAL_BRAND_LABEL = "brand";

	protected static final String CATEGORY_CODE = "c";
	protected static final String BRAND_CODE = "b";
	protected static final String LEFT_BRACKETS = "&nbsp;(";
	protected static final String RIGHT_CODE = ")";
	protected static final String DEAL_CODE_SPLIT = ",";


	/**
	 * Constructs a cart deals data which contains information about the following: 1. Partially Qualified Deals 2.
	 * Conflicting deals 3. Free goods selection.
	 *
	 * @return the list of Partially Qualified Deals.
	 */
	@Override
	public CartDealsJson getCartDealsData()
	{
		final B2BUnitModel unitModel = b2bCommerceUnitService.getParentUnit();
		final CartModel cartModel = sabmCartService.getSessionCart();

		final CartDealsJson cartDealsJson = new CartDealsJson();

		final List<DealModel> fullyQualifiedDeals = dealConditionService
				.findFullyQualifiedDeals(dealsService.getValidatedComplexDeals(unitModel), cartModel);

		for (final DealModel dealModel : CollectionUtils.emptyIfNull(fullyQualifiedDeals))
		{
			cartFacade.addApplyDealToCart(dealModel, DealConditionStatus.AUTOMATIC);
		}

		// add the conflict deals to the wrapper CartDealsJson
		cartFacade.findConflictingDeals(cartDealsJson);

		if (CollectionUtils.isEmpty(cartDealsJson.getConflict()) && CollectionUtils.isEmpty(cartDealsJson.getFree()))
		{
			final PartialDealQualificationResponse pqdResponse = dealConditionService.findPartiallyQualifiedDeals(
					dealsService.getDeals(unitModel, new Date(), forNextPeriodDate(new Date())), cartModel, false);

			try
			{
				final PartiallyQualifiedDealsJson pqdJson = pqdJsonConverter.convert(pqdResponse);
				cartDealsJson.setPartial(pqdJson);
			}
			catch (final ConversionException e)
			{
				LOG.debug("Error converting deal", e);
			}
		}
		return cartDealsJson;
	}

	@Override
	public List<DealJson> searchDeals()
	{
		return this.searchDeals(sessionService.getAttribute(SabmCoreConstants.SESSION_ATTR_DELIVERY_DATE), Boolean.FALSE);
	}

	@Override
	public List<DealJson> searchDeals(final boolean onlyValidDelivery)
	{
		return this.searchDeals(sessionService.getAttribute(SabmCoreConstants.SESSION_ATTR_DELIVERY_DATE), onlyValidDelivery);
	}

	@Override
	public List<DealJson> searchDeals(final Date deliveryDate, final Boolean judgeValidPeriod)
	{

		return dealsService.searchDeals(deliveryDate, judgeValidPeriod);
	}


	@Override
	public List<DealJson> searchDeals(final B2BUnitModel b2BUnitModel, final Date deliveryDate)
	{
		return this.searchDeals(deliveryDate, Boolean.TRUE, b2BUnitModel);
	}

	@Override
	public List<DealJson> searchDeals(final Date deliveryDate, final Boolean judgeValidPeriod, final B2BUnitModel unitModel)
	{
		final List<DealJson> dealsJson = new ArrayList<>();
		if (unitModel != null)
		{
			sessionService.setAttribute(SabmCoreConstants.SESSION_SELECT_B2BUNIT_UID_DATA, unitModel.getUid());
		}
		final List<DealModel> deals = dealsService.getDeals(unitModel, new Date(), forNextPeriodDate(new Date()));

		// To determine the valid deals
		final List<DealModel> dealsFiltered = dealsService.getValidationDeals(deliveryDate, deals, judgeValidPeriod);

		if (CollectionUtils.isNotEmpty(dealsFiltered))
		{
			final List<List<DealModel>> composedDeals = dealsService.composeComplexFreeProducts(dealsFiltered);

			for (final List<DealModel> dealList : composedDeals)
			{
				try
				{
					final DealJson dealJson = dealJsonConverter.convert(dealList);
					dealsJson.add(dealJson);
				}
				catch (final ConversionException e)
				{
					LOG.warn("Unable to convert deal: " + dealList.get(0), e);
				}
			}
		}
		return dealsJson;
	}




	/**
	 * Sets the last chance on deals.
	 *
	 * @return Map<String, List<String>> The changed deals titles in the map
	 *
	 */
	@Override
	public Map<String, List<String>> getChangedDealsTitleForCurrentUser(final List<RepDrivenDealConditionData> changedDealsData)
	{
		final List<String> activatedDealsTitle = new ArrayList<String>();
		final List<String> deactivatedDealsTitle = new ArrayList<String>();

		// find the activated deals and deactivated deals by the condition status
		for (final RepDrivenDealConditionData changedDeals : CollectionUtils.emptyIfNull(changedDealsData))
		{
			if (changedDeals.isStatus() && StringUtils.isNotEmpty(changedDeals.getDealConditionNumber()))
			{
				final DealModel activedDealModel = dealsService.getDeal(changedDeals.getDealConditionNumber());
				if (activedDealModel != null)
				{
					activatedDealsTitle.add(getDealTitle(activedDealModel));
				}
			}
			if (!changedDeals.isStatus() && StringUtils.isNotEmpty(changedDeals.getDealConditionNumber()))
			{
				final DealModel deactivedDealModel = dealsService.getDeal(changedDeals.getDealConditionNumber());
				if (deactivedDealModel != null)
				{
					deactivatedDealsTitle.add(getDealTitle(deactivedDealModel));
				}
			}
		}

		final Map<String, List<String>> changedDealsTitle = Maps.newHashMap();

		if (CollectionUtils.isNotEmpty(activatedDealsTitle))
		{
			changedDealsTitle.put(SabmCoreConstants.ACTIVATED_DEAL_KEY, activatedDealsTitle);
		}

		if (CollectionUtils.isNotEmpty(deactivatedDealsTitle))
		{
			changedDealsTitle.put(SabmCoreConstants.DEACTIVATED_DEAL_KEY, deactivatedDealsTitle);
		}

		return changedDealsTitle;
	}

	/**
	 * Sets the last chance on deal.
	 *
	 * @param dealModel
	 * @return String The List of title
	 *
	 */
	protected String getDealTitle(final DealModel dealModel)
	{

		final DealJson dealJson = new DealJson();
		final List<DealModel> deals = new ArrayList<>();
		deals.add(dealModel);
		dealTitlePopulator.populate(deals, dealJson);

		//if the deal code is empty, will not add it to the title
		if (StringUtils.isNotEmpty(dealModel.getCode()))
		{
			if (dealModel.getCode().startsWith(complexDealCodePreFix))
			{
				return dealJson.getTitle() + LEFT_BRACKETS + dealModel.getCode().replace(complexDealCodePreFix, "") + RIGHT_CODE;
			}
			LOG.debug("The Deal{} code is less then 7 numbers, will not split it", dealModel.getCode());
			return dealJson.getTitle() + LEFT_BRACKETS + dealModel.getCode() + RIGHT_CODE;
		}
		return dealJson.getTitle();
	}

	/**
	 * Create the empty DealPageData
	 *
	 * @return DealPageData
	 */
	protected DealPageData createDealPageData()
	{
		final DealPageData dealPageData = new DealPageData();
		dealPageData.setDeals(Lists.<DealData> newArrayList());
		dealPageData.setFacets(Lists.<FacetData> newArrayList());

		return dealPageData;
	}

	/**
	 * Generate result data and build all facets based on the deals being returned
	 *
	 * @param deals
	 *           deal list have filters
	 * @param facetDeals
	 *           full deal list
	 * @param brands
	 *           List of brands currently selected
	 * @param categoryCode
	 *           List of categories currently selected
	 * @return DealPageData deal page data containing deals and facets
	 */
	protected DealPageData buildResultDataAndBuildFacets(final List<DealData> deals, final List<DealData> facetDeals,
			final List<String> brands, final List<String> categoryCode)
	{

		final DealPageData dealPageData = createDealPageData();
		Collections.sort(deals, DealBrandMinQtyComparator.INSTANCE);
		dealPageData.setDeals(deals);

		final FacetData<SearchStateData> categoryFacetData = new FacetData<>();
		final FacetData<SearchStateData> brandFacetData = new FacetData<>();

		categoryFacetData.setValues(Lists.<FacetValueData<SearchStateData>> newArrayList());
		brandFacetData.setValues(Lists.<FacetValueData<SearchStateData>> newArrayList());


		final Map<String, FacetValueData<SearchStateData>> categoryFacetMap = Maps.newHashMap();
		final Map<String, FacetValueData<SearchStateData>> brandFacetMap = Maps.newHashMap();

		for (final DealData deal : facetDeals)
		{
			//build facets no count
			createFacets(brands, categoryCode, categoryFacetData, brandFacetData, categoryFacetMap, brandFacetMap, deal);
		}

		// If the user had selected the brand and not selected the category--> Build the brand by the base deals and build the category by facetDeals
		// If the user had selected the category and not selected the brand--> Build the brand by the facetDeals and build the category by base deals
		// If the user (selected the brand and category) or (not selected both brand and category) -->Build the brand and category by the base deals
		if (CollectionUtils.isNotEmpty(brands) && CollectionUtils.isEmpty(categoryCode))
		{
			buildFacetsByBrand(deals, facetDeals, categoryFacetMap, brandFacetMap);
		}
		else if (CollectionUtils.isEmpty(brands) && CollectionUtils.isNotEmpty(categoryCode))
		{
			buildFacetsByCategory(deals, facetDeals, categoryFacetMap, brandFacetMap);
		}
		else
		{
			for (final DealData deal : deals)
			{
				//build the count
				buildCategoryFacets(categoryFacetMap, deal);
				//build the count
				buildBrandFacets(brandFacetMap, deal);
			}
		}

		//sort values - first by selected then alpha
		final Comparator<FacetValueData<SearchStateData>> comparator = new FacetComparator();
		categoryFacetData.getValues().sort(comparator);
		brandFacetData.getValues().sort(comparator);

		//Create the facet and add it to the facet list
		createFacet(dealPageData, categoryFacetData, CATEGORY_CODE, DEAL_CATEGORY_LABEL);
		createFacet(dealPageData, brandFacetData, BRAND_CODE, DEAL_BRAND_LABEL);

		return dealPageData;
	}

	/**
	 * Fill in basic details for a facet
	 *
	 * @param dealPageData
	 *           Deals data - contains all deals and facets info
	 * @param facetData
	 *           the facet
	 * @param code
	 *           facet code
	 * @param name
	 *           facet name
	 */
	protected void createFacet(final DealPageData dealPageData, final FacetData<SearchStateData> facetData, final String code,
			final String name)
	{
		if (CollectionUtils.isNotEmpty(facetData.getValues()))
		{
			facetData.setCategory(true);
			facetData.setCode(code);
			facetData.setMultiSelect(true);
			facetData.setName(name);
			facetData.setVisible(true);
			dealPageData.getFacets().add(facetData);
		}
	}

	/**
	 * Build facets for a particular deal
	 *
	 * @param brand
	 *           List of brands currently selected
	 * @param categoryCode
	 *           List of categories currently selected
	 * @param categoryFacetData
	 *           Category facet data
	 * @param brandFacetData
	 *           Brand facet data
	 * @param categoryFacetMap
	 *           Map to track facet count - how many occurrences of a category
	 * @param brandFacetMap
	 *           Map to track facet count - how many occurrences of a brand
	 * @param deal
	 *           a deal
	 */
	protected void createFacets(final List<String> brand, final List<String> categoryCode,
			final FacetData<SearchStateData> categoryFacetData, final FacetData<SearchStateData> brandFacetData,
			final Map<String, FacetValueData<SearchStateData>> categoryFacetMap,
			final Map<String, FacetValueData<SearchStateData>> brandFacetMap, final DealData deal)
	{

		if (deal.getDealConditionGroupData() != null
				&& CollectionUtils.isNotEmpty(deal.getDealConditionGroupData().getDealConditions()))
		{
			for (final DealConditionData data : deal.getDealConditionGroupData().getDealConditions())
			{
				// just build the facet which the deal's condition is ProductDealCondition
				if (CONDITIONTYPE_PRODUCTCONDITION.equals(data.getConditionType()))
				{
					//generate category "facets"
					createCategoryFacets(categoryCode, categoryFacetData, categoryFacetMap, data);

					//generate brand "facets"
					createBrandFacets(brand, brandFacetData, brandFacetMap, data);
				}
			}
		}
	}

	/**
	 * Build facets for a particular deal by the filter result brand
	 *
	 * @param deals
	 *
	 * @param facetDeals
	 *
	 * @param categoryFacetMap
	 *           Map to track facet count - how many occurrences of a category
	 * @param brandFacetMap
	 *           Map to track facet count - how many occurrences of a brand
	 */
	protected void buildFacetsByBrand(final List<DealData> deals, final List<DealData> facetDeals,
			final Map<String, FacetValueData<SearchStateData>> categoryFacetMap,
			final Map<String, FacetValueData<SearchStateData>> brandFacetMap)
	{
		for (final DealData deal : deals)
		{
			//build the count
			buildCategoryFacets(categoryFacetMap, deal);
		}
		for (final DealData deal : facetDeals)
		{
			//build the count
			buildBrandFacets(brandFacetMap, deal);
		}
	}

	/**
	 * Build facets for a particular deal by the falter result category
	 *
	 * @param deals
	 *
	 * @param facetDeals
	 *
	 * @param categoryFacetMap
	 *           Map to track facet count - how many occurrences of a category
	 * @param brandFacetMap
	 *           Map to track facet count - how many occurrences of a brand
	 */
	protected void buildFacetsByCategory(final List<DealData> deals, final List<DealData> facetDeals,
			final Map<String, FacetValueData<SearchStateData>> categoryFacetMap,
			final Map<String, FacetValueData<SearchStateData>> brandFacetMap)
	{
		for (final DealData deal : deals)
		{
			//build the count
			buildBrandFacets(brandFacetMap, deal);
		}
		for (final DealData deal : facetDeals)
		{
			//build the count
			buildCategoryFacets(categoryFacetMap, deal);
		}
	}

	/**
	 * Build facets for a particular deal
	 *
	 * Category facet data
	 *
	 * @param categoryFacetMap
	 *           Map to track facet count - how many occurrences of a category
	 * @param deal
	 *           a deal
	 */
	protected void buildCategoryFacets(final Map<String, FacetValueData<SearchStateData>> categoryFacetMap, final DealData deal)
	{

		if (deal.getDealConditionGroupData() != null
				&& CollectionUtils.isNotEmpty(deal.getDealConditionGroupData().getDealConditions()))
		{
			for (final DealConditionData data : deal.getDealConditionGroupData().getDealConditions())
			{
				// just build the facet which the deal's condition is ProductDealCondition
				if (CONDITIONTYPE_PRODUCTCONDITION.equals(data.getConditionType()))
				{
					//generate category "facets"
					generateCategoryFacets(categoryFacetMap, data);
				}
			}
		}
	}

	/**
	 * Build facets for a particular deal
	 *
	 * @param brandFacetMap
	 *           Map to track facet count - how many occurrences of a brand
	 * @param deal
	 *           a deal
	 */
	protected void buildBrandFacets(final Map<String, FacetValueData<SearchStateData>> brandFacetMap, final DealData deal)
	{

		if (deal.getDealConditionGroupData() != null
				&& CollectionUtils.isNotEmpty(deal.getDealConditionGroupData().getDealConditions()))
		{
			for (final DealConditionData data : deal.getDealConditionGroupData().getDealConditions())
			{
				// just build the facet which the deal's condition is ProductDealCondition
				if (CONDITIONTYPE_PRODUCTCONDITION.equals(data.getConditionType()))
				{
					//generate brand "facets"
					generateBrandFacets(brandFacetMap, data);
				}
			}
		}
	}

	/**
	 * Generate category facets based on result set - note that this is a refinement and it will not have all categories
	 * at all times. If one is selected, then only those that descent from it will be available for further refinement.
	 *
	 * @param brands
	 *           List of brands currently selected
	 * @param brandFacetData
	 *           Brand facet data
	 * @param brandFacetMap
	 *           Map to track facet count - how many occurrences of a brand
	 * @param conditionData
	 *           a result to be evaluated and added to the facets
	 */
	protected void createBrandFacets(final List<String> brands, final FacetData<SearchStateData> brandFacetData,
			final Map<String, FacetValueData<SearchStateData>> brandFacetMap, final DealConditionData conditionData)
	{

		// If the condition have no product or product have no brand , skip this deal.
		// Check if the brand have been add to the Map, if no-->create it
		if (conditionData.getProduct() != null && StringUtils.isNotEmpty(conditionData.getProduct().getBrand())
				&& !brandFacetMap.containsKey(conditionData.getProduct().getBrand()))
		{
			createBrandFacets(conditionData, brands, brandFacetData, brandFacetMap);

		}
	}

	/**
	 * Generate category facets based on result set - note that this is a refinement and it will not have all categories
	 * at all times. If one is selected, then only those that descent from it will be available for further refinement.
	 *
	 * @param brandFacetMap
	 *           Map to track facet count - how many occurrences of a brand
	 * @param conditionData
	 *           a result to be evaluated and added to the facets
	 */
	protected void generateBrandFacets(final Map<String, FacetValueData<SearchStateData>> brandFacetMap,
			final DealConditionData conditionData)
	{

		// If the condition have no product or product have no brand , skip this deal.
		// Check if the brand have been add to the Map,if yes--> add the count.
		if (conditionData.getProduct() != null && StringUtils.isNotEmpty(conditionData.getProduct().getBrand())
				&& brandFacetMap.containsKey(conditionData.getProduct().getBrand()))
		{
			brandFacetMap.get(conditionData.getProduct().getBrand())
					.setCount(brandFacetMap.get(conditionData.getProduct().getBrand()).getCount() + 1);
		}
	}

	/**
	 * Create a new FacetValueData
	 *
	 * @param brands
	 *           List of brands currently selected
	 * @param brandFacetData
	 *           Brand facet data
	 * @param brandFacetMap
	 *           Map to track facet count - how many occurrences of a brand
	 * @param conditionData
	 *           a result to be evaluated and added to the facets
	 */
	protected void createBrandFacets(final DealConditionData conditionData, final List<String> brands,
			final FacetData<SearchStateData> brandFacetData, final Map<String, FacetValueData<SearchStateData>> brandFacetMap)
	{
		try
		{
			final FacetValueData<SearchStateData> valueData = new FacetValueData<>();
			valueData.setName(conditionData.getProduct().getBrand());
			valueData.setCode(java.net.URLEncoder.encode(conditionData.getProduct().getBrand(), ENCODING));
			valueData.setCount(0);
			valueData.setSelected(CollectionUtils.isNotEmpty(brands) && brands.contains(valueData.getCode()));

			brandFacetData.getValues().add(valueData);
			brandFacetMap.put(conditionData.getProduct().getBrand(), valueData);
		}
		catch (final UnsupportedEncodingException e)
		{
			LOG.error("Error encoding brand facet value {}", conditionData.getProduct().getBrand(), e);
		}
	}

	/**
	 * Generate category facets based on result set - note that this is a refinement and it will not have all categories
	 * at all times. If one is selected, then only those that descent from it will be available for further refinement.
	 *
	 * @param categoryCodes
	 *           List of category code currently selected
	 * @param categoryFacetData
	 *           Category facet data
	 * @param categoryFacetMap
	 *           Map to track facet count - how many occurrences of a category
	 * @param conditionData
	 *           a result to be evaluated and added to the facets
	 */
	protected void createCategoryFacets(final List<String> categoryCodes, final FacetData<SearchStateData> categoryFacetData,
			final Map<String, FacetValueData<SearchStateData>> categoryFacetMap, final DealConditionData conditionData)
	{

		// If the condition have no product or product have no brand , skip this deal.
		if (conditionData.getProduct() != null && CollectionUtils.isNotEmpty(conditionData.getProduct().getCategories()))
		{
			for (final CategoryData categoryData : conditionData.getProduct().getCategories())
			{
				// Check if the brand have been add to the Map, if no-->create it
				if (!categoryFacetMap.containsKey(categoryData.getCode()))
				{
					createCategoryFacet(categoryCodes, categoryFacetData, categoryFacetMap, conditionData, categoryData);
				}
			}
		}
	}

	/**
	 * Generate category facets based on result set - note that this is a refinement and it will not have all categories
	 * at all times. If one is selected, then only those that descent from it will be available for further refinement.
	 *
	 * @param categoryFacetMap
	 *           Map to track facet count - how many occurrences of a category
	 * @param conditionData
	 *           a result to be evaluated and added to the facets
	 */
	protected void generateCategoryFacets(final Map<String, FacetValueData<SearchStateData>> categoryFacetMap,
			final DealConditionData conditionData)
	{

		// If the condition have no product or product have no brand , skip this deal.
		if (conditionData.getProduct() != null && CollectionUtils.isNotEmpty(conditionData.getProduct().getCategories()))
		{
			for (final CategoryData categoryData : conditionData.getProduct().getCategories())
			{
				// Check if the brand have been add to the Map, if no-->create it, if yes--> add the count.
				if (categoryFacetMap.containsKey(categoryData.getCode()))
				{
					categoryFacetMap.get(categoryData.getCode()).setCount(categoryFacetMap.get(categoryData.getCode()).getCount() + 1);
				}
			}
		}
	}

	/**
	 *
	 * Create new Category Facet
	 *
	 * @param categoryCodes
	 *           List of category code currently selected
	 * @param categoryFacetData
	 *           Category facet data
	 * @param categoryFacetMap
	 *           Map to track facet count - how many occurrences of a category
	 * @param conditionData
	 *           a result to be evaluated and added to the facets
	 * @param categoryData
	 *           the category need to be build to the facet
	 */
	protected void createCategoryFacet(final List<String> categoryCodes, final FacetData<SearchStateData> categoryFacetData,
			final Map<String, FacetValueData<SearchStateData>> categoryFacetMap, final DealConditionData conditionData,
			final CategoryData categoryData)
	{
		try
		{
			final FacetValueData<SearchStateData> valueData = new FacetValueData<>();
			valueData.setName(categoryData.getName());
			valueData.setCode(java.net.URLEncoder.encode(categoryData.getCode(), ENCODING));
			valueData.setCount(0);
			valueData.setSelected(CollectionUtils.isNotEmpty(categoryCodes) && categoryCodes.contains(valueData.getCode()));

			categoryFacetData.getValues().add(valueData);
			categoryFacetMap.put(categoryData.getCode(), valueData);
		}
		catch (final UnsupportedEncodingException e)
		{
			LOG.error("Error encoding brand facet value {}", conditionData.getProduct().getBrand(), e);
		}
	}

	/**
	 * Convert the date String to Date
	 *
	 * @param date
	 * @param format
	 * @return Date
	 */
	@Override
	public Date convertDate(final String date, final String format)
	{
		if (StringUtils.isNotBlank(date))
		{
			final SimpleDateFormat dateFormat = new SimpleDateFormat(format);
			try
			{
				return dateFormat.parse(date);
			}
			catch (final ParseException e)
			{
				LOG.error("Error converting date [{}]", date);
			}
		}
		return null;
	}

	/**
	 * Convert the date between different format
	 *
	 * @param dateString
	 * @param formatFrom
	 * @param formatTo
	 * @return String
	 */
	@Override
	public String convertDateFormat(final String dateString, final String formatFrom, final String formatTo)
	{
		final Date date = convertDate(dateString, formatFrom);
		if (date != null)
		{
			return DateFormatUtils.format(date, formatTo);
		}

		return null;
	}

	/**
	 * Check the date is correct date format
	 *
	 * @param date
	 * @return boolean
	 */
	@Override
	public boolean validDate(final String date)
	{
		if (StringUtils.isNotBlank(date))
		{
			final SimpleDateFormat format = new SimpleDateFormat(DATE_SAFE_FORMAT);
			try
			{
				format.parse(date);
				return true;
			}
			catch (final ParseException e)
			{
				LOG.debug("Error converting date [{}]", date);
			}
		}
		return false;
	}


	@Override
	public boolean sendConfirmEnableDealEmail(final String b2bUnitId, final List<RepDrivenDealConditionData> changedDeals,
			final String behaviourRequirements, final Boolean sendToMe, final List<String> toEmails)
	{
		final Map<String, List<String>> changedDealsTitle = getChangedDealsTitleForCurrentUser(changedDeals);

		final List<String> activatedDealTitles = new ArrayList<>();
		final List<String> deactivatedDealTitles = new ArrayList<>();

		final UserModel fromUser = userService.getCurrentUser();
		if (BooleanUtils.isTrue(sendToMe))
		{
			toEmails.add(fromUser.getUid());
		}

		if (CollectionUtils.isEmpty(toEmails))
		{
			return false;
		}
		if (changedDealsTitle != null)
		{
			if (changedDealsTitle.containsKey(SabmCoreConstants.ACTIVATED_DEAL_KEY))
			{
				activatedDealTitles.addAll(changedDealsTitle.get(SabmCoreConstants.ACTIVATED_DEAL_KEY));
			}
			if (changedDealsTitle.containsKey(SabmCoreConstants.DEACTIVATED_DEAL_KEY))
			{
				deactivatedDealTitles.addAll(changedDealsTitle.get(SabmCoreConstants.DEACTIVATED_DEAL_KEY));
			}
		}
		else
		{
			return false;
		}

		if (CollectionUtils.isEmpty(activatedDealTitles) && CollectionUtils.isEmpty(deactivatedDealTitles))
		{
			return false;
		}

		final B2BUnitModel b2bUnit = b2bUnitService.getUnitForUid(b2bUnitId);
		String b2bUnitStatus = StringUtils.EMPTY;
		if (b2bUnit != null && b2bUnit.getB2BUnitStatus() != null)
		{
			b2bUnitStatus = b2bUnit.getB2BUnitStatus().getCode();
		}

		dealsService.sendConfirmEnabledDealsEmail(behaviourRequirements, activatedDealTitles, deactivatedDealTitles, fromUser,
				toEmails, Collections.<String> emptyList(), b2bUnit, b2bUnitStatus);

		return true;
	}

	/*
	 * (non-Javadoc)
	 *
	 * @see com.sabmiller.facades.deal.SABMDealsSearchFacade#getCustomers(java.lang.String)
	 */
	@Override
	public List<String> getOtherSelectCustomers(final B2BUnitModel b2bnit)
	{
		final EmployeeModel employee = (EmployeeModel) userService.getCurrentUser();
		// get other select customer
		return sabmB2BCustomerService.getOtherCustomerByUnitAndGroups(b2bnit, employee.getUid());
	}

	/*
	 * (non-Javadoc)
	 *
	 * @see com.sabmiller.facades.deal.SABMDealsSearchFacade#getSpecificDeals(de.hybris.platform.b2b.model.B2BUnitModel,
	 * boolean)
	 */
	@Override
	public List<DealJson> getSpecificDeals(final B2BUnitModel b2bUnit, final boolean inStore)
	{
		final List<DealModel> deals = dealsService.getSpecificDeals(b2bUnit, inStore);
		//final List<List<DealModel>> dealsList = new ArrayList<>();
		final List<DealJson> dealsJson = new ArrayList<>();
		for (final DealModel dealModel : CollectionUtils.emptyIfNull(deals))
		{
			final List<DealModel> newDeals = new ArrayList<>();
			newDeals.add(dealModel);
			//dealsList.add(newDeals);
			try
			{
				final DealJson dealJson = dealJsonConverter.convert(newDeals);
				dealsJson.add(dealJson);
			}
			catch (final ConversionException e)
			{
				LOG.error("Unable to convert deal for BDE Portal : " + newDeals.get(0), e);
			}
		}
		return dealsJson;
		//return Converters.convertAll(dealsList, dealJsonConverter);
	}

	/*
	 * (non-Javadoc)
	 *
	 * @see com.sabmiller.facades.deal.SABMDealsSearchFacade#hasUpcomingDeals()
	 */
	@Override
	public boolean hasUpcomingDeals()
	{
		final Date currentDeliveryDate = (Date) sessionService.getAttribute(SabmCoreConstants.SESSION_ATTR_DELIVERY_DATE);
		final Calendar currentDeliveryCalendar = Calendar.getInstance();
		currentDeliveryCalendar.setTime(currentDeliveryDate);

		final Set<Date> enabledCalendarDates = customerFacade.enabledCalendarDates();
		for (final Date enabledDate : enabledCalendarDates)
		{
			final Calendar enabledDeliveryCalendar = Calendar.getInstance();
			enabledDeliveryCalendar.setTime(enabledDate);

			if (currentDeliveryCalendar.get(Calendar.DAY_OF_YEAR) != enabledDeliveryCalendar.get(Calendar.DAY_OF_YEAR))
			{
				final List<DealJson> upcomingDeals = searchDeals(enabledDate, Boolean.TRUE);
				if (upcomingDeals.size() > 0)
				{
					return true;
				}
			}
		}

		return false;
	}

	/*
	 * (non-Javadoc)
	 *
	 * @see com.sabmiller.facades.deal.SABMDealsSearchFacade#getDealsForProduct(java.lang.String)
	 */
	@Override
	public List<String> getDealsForProduct(final String productCode)
	{
		// YTODO Auto-generated method stub
		return dealsCacheService.getDealTitlesForProduct(productCode);
	}

	@Override
	public List<AsahiDealData> getSGASpecificDeals(final AsahiB2BUnitModel b2bUnit)
	{
		final List<AsahiDealModel> deals = dealsService.getSGASpecificDeals(b2bUnit);
		final List<AsahiDealData> asahiDealDatas = Converters.convertAll(deals, asahiDealDataConverter);
		populateDealsStatus(asahiDealDatas, b2bUnit);
		return asahiDealDatas;
	}

	/**
	 * @param asahiDealDatas
	 * @param b2bUnit
	 */
	private void populateDealsStatus(final List<AsahiDealData> asahiDealDatas, final AsahiB2BUnitModel b2bUnit)
	{
		if (org.apache.commons.collections4.CollectionUtils.isNotEmpty(asahiDealDatas) && b2bUnit != null
				&& org.apache.commons.collections4.CollectionUtils.isNotEmpty(b2bUnit.getAsahiDeals()))
		{
			final Set<String> asahiDealModels = b2bUnit.getAsahiDeals().stream().map(deal -> deal.getCode())
					.collect(Collectors.toSet());
			asahiDealDatas.forEach(dealData -> {
				if (asahiDealModels.contains(dealData.getCode()))
				{
					dealData.setActive(true);
				}
			});
		}

	}

	@Override
	public void saveAsahiRepDealChange(final String customerAccount, final List<String> dealsToActivate,
			final List<String> dealsToRemove, final List<String> customerEmails, final String dealsDetails)
	{

		final B2BUnitModel b2bUnit = customerFacade.getB2BUnitForId(customerAccount);
		if (b2bUnit instanceof AsahiB2BUnitModel)
		{
			final AsahiB2BUnitModel b2bUnitModel = (AsahiB2BUnitModel) b2bUnit;
			dealsService.saveAsahiRepDealChange(b2bUnitModel, dealsToActivate, dealsToRemove, customerEmails, dealsDetails);
		}
	}

	@Override
	public List<String> getCustomerEmails(final AsahiB2BUnitModel b2bUnit)
	{
		return dealsService.getCustomerEmails(b2bUnit);
	}

	@Override
	public List<String> getSGADealsTitleForProductAndUnit(final String code, final B2BUnitModel b2bUnitModel)
	{
		if (b2bUnitModel instanceof AsahiB2BUnitModel
				&& org.apache.commons.collections4.CollectionUtils.isNotEmpty(((AsahiB2BUnitModel) b2bUnitModel).getAsahiDeals()))
		{
			final List<AsahiDealModel> deals = dealsService.getSGADealsForProductAndUnit(code, (AsahiB2BUnitModel) b2bUnitModel);
			final List<AsahiDealData> asahiDealDatas = Converters.convertAll(deals, asahiDealDataConverter);
			if (org.apache.commons.collections4.CollectionUtils.isNotEmpty(asahiDealDatas))
			{
				return asahiDealDatas.stream().map(dealData -> dealData.getTitle()).collect(Collectors.toList());
			}
		}
		return null;
	}

	@Override
	public List<AsahiDealData> getCustomerSpecificDeals(final AsahiB2BUnitModel b2bUnit){
		List<AsahiDealData> asahiDealDatas = new ArrayList<AsahiDealData>();
		if(org.apache.commons.collections4.CollectionUtils.isNotEmpty(b2bUnit.getAsahiDeals())) {
			List<AsahiDealModel> validDeals = dealsService.getCustomerSpecificDeals(b2bUnit);
			Collections.shuffle(validDeals);
			validDeals = validDeals.stream().limit(ALB_DEAL_LIMIT).collect(Collectors.toList());
			asahiDealDatas = Converters.convertAll(validDeals, asahiDealDataConverter);
		}
		return asahiDealDatas;
	}

	@Override
	public List<AsahiDealData> getSGADealsDataForProductAndUnit(final String code, final B2BUnitModel b2bUnitModel)
	{
		if (b2bUnitModel instanceof AsahiB2BUnitModel
				&& CollectionUtils.isNotEmpty(((AsahiB2BUnitModel) b2bUnitModel).getAsahiDeals()))
		{
			final List<AsahiDealModel> deals = dealsService.getSGADealsForProductAndUnit(code, (AsahiB2BUnitModel) b2bUnitModel);
			final List<AsahiDealData> asahiDealDatas = Converters.convertAll(deals, asahiDealDataConverter);
			return asahiDealDatas;
		}
		return Collections.emptyList();
	}
}
