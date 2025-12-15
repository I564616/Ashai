/**
 *
 */
package com.sabmiller.core.deals.services;

import de.hybris.platform.b2b.company.B2BCommerceUnitService;
import de.hybris.platform.b2b.model.B2BCustomerModel;
import de.hybris.platform.b2b.model.B2BUnitModel;
import de.hybris.platform.basecommerce.enums.StockLevelStatus;
import de.hybris.platform.catalog.CatalogVersionService;
import de.hybris.platform.catalog.enums.ArticleApprovalStatus;
import de.hybris.platform.catalog.jalo.CatalogManager;
import de.hybris.platform.converters.Populator;
import de.hybris.platform.core.Registry;
import de.hybris.platform.core.model.ItemModel;
import de.hybris.platform.core.model.order.AbstractOrderEntryModel;
import de.hybris.platform.core.model.order.CartModel;
import de.hybris.platform.core.model.product.ProductModel;
import de.hybris.platform.core.model.user.UserModel;
import de.hybris.platform.search.restriction.SearchRestrictionService;
import de.hybris.platform.servicelayer.dto.converter.ConversionException;
import de.hybris.platform.servicelayer.dto.converter.Converter;
import de.hybris.platform.servicelayer.event.EventService;
import de.hybris.platform.servicelayer.event.events.AbstractEvent;
import de.hybris.platform.servicelayer.exceptions.AmbiguousIdentifierException;
import de.hybris.platform.servicelayer.exceptions.UnknownIdentifierException;
import de.hybris.platform.servicelayer.i18n.CommonI18NService;
import de.hybris.platform.servicelayer.model.ModelService;
import de.hybris.platform.servicelayer.session.SessionService;
import de.hybris.platform.servicelayer.user.UserService;
import de.hybris.platform.site.BaseSiteService;
import de.hybris.platform.store.services.BaseStoreService;
import de.hybris.platform.tx.Transaction;
import de.hybris.platform.util.Config;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.Collection;
import java.util.Collections;
import java.util.Date;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.Optional;
import java.util.Set;
import java.util.concurrent.Callable;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;
import java.util.concurrent.TimeUnit;
import java.util.stream.Collectors;

import jakarta.annotation.Nonnull;
import jakarta.annotation.Resource;

import org.apache.commons.collections4.CollectionUtils;
import org.apache.commons.collections4.ListUtils;
import org.apache.commons.lang3.BooleanUtils;
import org.apache.commons.lang3.StringUtils;
import org.apache.commons.lang3.builder.ReflectionToStringBuilder;
import org.apache.commons.lang3.time.DateUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.cache.annotation.Cacheable;

import com.apb.core.deals.strategies.AsahiDealValidationStrategy;
import com.apb.core.event.AsahiDealsChangeEvent;
import com.apb.core.model.ApbProductModel;
import com.apb.product.strategy.AsahiInclusionExclusionProductStrategy;
import com.google.common.collect.Lists;
import com.google.common.collect.Maps;
import com.sabm.core.config.SabmConfigurationService;
import com.sabmiller.cache.impl.SABMLRUCache;
import com.sabmiller.core.b2b.dao.SearchB2BUnitQueryParam;
import com.sabmiller.core.b2b.services.SabmB2BUnitService;
import com.sabmiller.core.comparators.DealBenefitScaleComparator;
import com.sabmiller.core.comparators.DealScaleComparator;
import com.sabmiller.core.constants.SabmCoreConstants;
import com.sabmiller.core.deals.dao.DealsDao;
import com.sabmiller.core.deals.strategies.SABMDealValidationStrategy;
import com.sabmiller.core.deals.strategies.SABMProductUOMConversionStrategy;
import com.sabmiller.core.deals.vo.DealCodeGeneratorParam;
import com.sabmiller.core.deals.vo.DealsResponse;
import com.sabmiller.core.enums.DealConditionStatus;
import com.sabmiller.core.enums.DealTypeEnum;
import com.sabmiller.core.enums.RepDrivenDealStatus;
import com.sabmiller.core.enums.SapServiceCallStatus;
import com.sabmiller.core.event.ConfirmEnableDealEmailEvent;
import com.sabmiller.core.jobs.concurrency.MultiThreadJobFactory;
import com.sabmiller.core.model.AbstractDealBenefitModel;
import com.sabmiller.core.model.AbstractDealConditionModel;
import com.sabmiller.core.model.AsahiB2BUnitModel;
import com.sabmiller.core.model.AsahiDealModel;
import com.sabmiller.core.model.AsahiFreeGoodsDealBenefitModel;
import com.sabmiller.core.model.AsahiProductDealConditionModel;
import com.sabmiller.core.model.BDECustomerModel;
import com.sabmiller.core.model.CartDealConditionModel;
import com.sabmiller.core.model.ComplexDealConditionModel;
import com.sabmiller.core.model.DealAssigneeModel;
import com.sabmiller.core.model.DealConditionGroupModel;
import com.sabmiller.core.model.DealModel;
import com.sabmiller.core.model.DealScaleModel;
import com.sabmiller.core.model.DiscountDealBenefitModel;
import com.sabmiller.core.model.EntryOfferInfoModel;
import com.sabmiller.core.model.FreeGoodsDealBenefitModel;
import com.sabmiller.core.model.ProductDealConditionModel;
import com.sabmiller.core.model.SABMAlcoholVariantProductMaterialModel;
import com.sabmiller.core.product.SabmProductService;
import com.sabmiller.core.product.strategy.CatalogVersionDeterminationStrategy;
import com.sabmiller.core.stock.DefaultSabmCommerceStockService;
import com.sabmiller.core.util.SabmConverter;
import com.sabmiller.facades.deal.data.DealBaseProductJson;
import com.sabmiller.facades.deal.data.DealFreeProductJson;
import com.sabmiller.facades.deal.data.DealJson;
import com.sabmiller.facades.deal.data.DealRangeJson;
import com.sabmiller.integration.restclient.commons.SABMIntegrationException;
import com.sabmiller.integration.sap.cup.response.CustomerUnitPricingResponse;
import com.sabmiller.integration.sap.deals.BOGOFDealsRequestHandler;
import com.sabmiller.integration.sap.deals.DiscountDealsRequestHandler;
import com.sabmiller.integration.sap.deals.bogof.request.PricingBOGOFDealsRequest;
import com.sabmiller.integration.sap.deals.bogof.response.PricingBOGOFDealsResponse;
import com.sabmiller.integration.sap.deals.pricediscount.request.PricingDiscountConditionsRequest;
import com.sabmiller.integration.sap.deals.pricediscount.response.PricingDiscountConditionsResponse;


/**
 * The Class DealsServiceImpl.
 *
 * @author joshua.a.antony
 */
public class DealsServiceImpl implements DealsService
{

	/** The Constant LOG. */
	private static final Logger LOG = LoggerFactory.getLogger(DealsServiceImpl.class);


	/** The b2b unit service. */
	@Resource(name = "b2bUnitService")
	private SabmB2BUnitService b2bUnitService;

	/** The model service. */
	@Resource(name = "modelService")
	private ModelService modelService;

	/** The deals dao. */
	@Resource(name = "dealsDao")
	private DealsDao dealsDao;

	/** The bogof deals request handler. */
	@Resource(name = "bogofDealsRequestHandler")
	private BOGOFDealsRequestHandler bogofDealsRequestHandler;

	/** The discount deals request handler. */
	@Resource(name = "discountDealsRequestHandler")
	private DiscountDealsRequestHandler discountDealsRequestHandler;

	/** The bogof deal request converter. */
	@Resource(name = "bogofDealRequestConverter")
	private SabmConverter<B2BUnitModel, PricingBOGOFDealsRequest, Date> bogofDealRequestConverter;

	/** The bogof deal reverse converter. */
	@Resource(name = "bogofDealReverseConverter")
	private Converter<DealsResponse, List<DealModel>> bogofDealReverseConverter;

	/** The pricing discount deal request converter. */
	@Resource(name = "discountDealRequestConverter")
	private SabmConverter<B2BUnitModel, PricingDiscountConditionsRequest, Date> pricingDiscountDealRequestConverter;

	/** The once off deals request converter. */
	@Resource(name = "onceOffDealsRequestConverter")
	private SabmConverter<B2BUnitModel, PricingDiscountConditionsRequest, Date> onceOffDealsRequestConverter;

	/** The pricing discount deal reverse converter. */
	@Resource(name = "discountDealReverseConverter")
	private Converter<DealsResponse, List<DealModel>> pricingDiscountDealReverseConverter;

	/** The once off deals reverse converter. */
	@Resource(name = "onceOffDealsReverseConverter")
	private Converter<DealsResponse, List<DealModel>> onceOffDealsReverseConverter;

	/** The deals priority service. */
	@Resource(name = "dealsPriorityService")
	private DealsPriorityService dealsPriorityService;

	/** The user service. */
	@Resource(name = "userService")
	private UserService userService;

	/** The search restriction service. */
	@Resource(name = "searchRestrictionService")
	private SearchRestrictionService searchRestrictionService;

	/** The session service. */
	@Resource(name = "sessionService")
	private SessionService sessionService;

	/** The catalog version service. */
	@Resource(name = "catalogVersionService")
	private CatalogVersionService catalogVersionService;

	/** The catalog version determination strategy. */
	@Resource(name = "catalogVersionDeterminationStrategy")
	private CatalogVersionDeterminationStrategy catalogVersionDeterminationStrategy;

	/** The event service. */
	@Resource(name = "eventService")
	private EventService eventService;

	/** The base site service. */
	@Resource(name = "baseSiteService")
	private BaseSiteService baseSiteService;

	/** The common i18 n service. */
	@Resource(name = "commonI18NService")
	private CommonI18NService commonI18NService;

	/** The base store service. */
	@Resource(name = "baseStoreService")
	private BaseStoreService baseStoreService;

	/** The base store service. */
	@Resource(name = "productService")
	private SabmProductService productService;

	/** The dealValidationStrategy strategy. */
	@Resource(name = "dealValidationStrategy")
	private SABMDealValidationStrategy dealValidationStrategy;

	/** The product uom conversion strategy. */
	@Resource(name = "productUOMConversionStrategy")
	private SABMProductUOMConversionStrategy productUOMConversionStrategy;

	/** The b2b commerce unit service. */
	@Resource(name = "b2bCommerceUnitService")
	private B2BCommerceUnitService b2bCommerceUnitService;


	@Resource(name = "dealTitlePopulator")
	private Populator<List<DealModel>, DealJson> dealTitlePopulator;

	@Resource(name = "dealJsonConverter")
	private Converter<List<DealModel>, DealJson> dealJsonConverter;

	@Resource(name = "sabmConfigurationService")
	private SabmConfigurationService sabmConfigurationService;

	@Resource(name = "asahiDealValidationStrategy")
	private AsahiDealValidationStrategy asahiDealValidationStrategy;
	@Resource(name = "inclusionExclusionProductStrategy")
	private AsahiInclusionExclusionProductStrategy inclusionExclusionProductStrategy;
	@Resource
	private DefaultSabmCommerceStockService defaultSabmCommerceStockService;



	/** The lost deal check list. */
	@SuppressWarnings("rawtypes")
	private List<AbstractLostDealChecker> lostDealCheckList = new ArrayList<>();



	/**
	 * Gets the lost deal check list.
	 *
	 * @return the lostDealCheckList
	 */
	@SuppressWarnings("rawtypes")
	public List<AbstractLostDealChecker> getLostDealCheckList()
	{
		return lostDealCheckList;
	}


	/**
	 * Sets the lost deal check list.
	 *
	 * @param lostDealCheckList
	 *           the lostDealCheckList to set
	 */
	@SuppressWarnings("rawtypes")
	public void setLostDealCheckList(final List<AbstractLostDealChecker> lostDealCheckList)
	{
		this.lostDealCheckList = lostDealCheckList;
	}


	/*
	 * (non-Javadoc)
	 *
	 * @see com.sabmiller.core.deals.services.DealsService#getDeals(de.hybris.platform.b2b.model.B2BUnitModel,
	 * java.util.Date, java.util.Date)
	 */
	@Override
	public List<DealModel> getDeals(final B2BUnitModel b2bUnitModel, final Date fromDate, final Date toDate)
	{
		final List<DealModel> nonComplexDeals = dealsDao.getDeals(b2bUnitModel, fromDate, toDate);
		final Collection<DealModel> complexDeals = filterOnlineDeals(b2bUnitModel.getComplexDeals());

		final List<DealModel> deals = new ArrayList<DealModel>();
		deals.addAll(nonComplexDeals);
		deals.addAll(complexDeals);

		return CollectionUtils.isNotEmpty(deals) ? getDealsByRepDrivenStatus(deals) : Collections.emptyList();
	}

	/*
	 * (non-Javadoc)
	 *
	 * @see
	 * com.sabmiller.core.deals.services.DealsService#getValidatedComplexDeals(de.hybris.platform.b2b.model.B2BUnitModel)
	 */
	@Override
	public List<DealModel> getValidatedComplexDeals(final B2BUnitModel b2bUnitModel)
	{
		final List<DealModel> complexDeals = filterOnlineDeals(b2bUnitModel.getComplexDeals());

		final List<DealModel> validationDeals = getValidationDeals(complexDeals, true);

		return ListUtils.emptyIfNull(validationDeals);
	}

	/**
	 * for get the rep-driven deals.
	 *
	 * @param deals
	 *           the deals
	 * @return the result deals
	 */
	@Override
	public List<DealModel> getDealsByRepDrivenStatus(final List<DealModel> deals)
	{
		final List<DealModel> resultDeals = new ArrayList<>();
		for (final DealModel dealModel : CollectionUtils.emptyIfNull(deals))
		{
			if (dealModel != null)
			{
				if (BooleanUtils.isNotTrue(dealModel.getInStore()))
				{
					resultDeals.add(dealModel);
				}
				else if (dealModel.getRepDrivenDealStatus() != null
						&& RepDrivenDealStatus.UNLOCKED.equals(dealModel.getRepDrivenDealStatus().getStatus()))
				{
					resultDeals.add(dealModel);
				}
			}
		}
		LOG.info("After getDealsByRepDrivenStatus validation resultDeals : " +resultDeals);

		return resultDeals;
	}

	/*
	 * (non-Javadoc)
	 *
	 * @see com.sabmiller.core.deals.services.DealsService#getDealsForProduct(de.hybris.platform.b2b.model.B2BUnitModel,
	 * java.lang.String, java.util.Date, java.util.Date)
	 */
	@Override
	public List<DealModel> getDealsForProduct(final B2BUnitModel b2bUnitModel, final List<String> productCode, final Date fromDate,
			final Date toDate)
	{
		final List<DealModel> deals = Lists.newArrayList();
		final List<DealModel> nonComplexDeals = dealsDao.getDealsForProduct(b2bUnitModel, productCode, fromDate, toDate);
		deals.addAll(nonComplexDeals);

		final Collection<DealModel> complexAllDeals = filterOnlineDeals(b2bUnitModel.getComplexDeals());
		if (CollectionUtils.isNotEmpty(complexAllDeals))
		{
			deals.addAll(getComplexDealsByProduct(productCode, complexAllDeals));
		}

		return CollectionUtils.isNotEmpty(deals) ? getDealsByRepDrivenStatus(deals) : Collections.emptyList();
	}



	/**
	 * For the complex deal of the product.
	 *
	 * @param productCode
	 *           the product code
	 * @param complexAllDeals
	 *           the complex all deals
	 * @return the complex deals by product
	 */
	private List<DealModel> getComplexDealsByProduct(final List<String> productCode, final Collection<DealModel> complexAllDeals)
	{
		final List<DealModel> complexDeals = Lists.newArrayList();
		for (final DealModel dealModel : complexAllDeals)
		{
			final DealConditionGroupModel dealConditionGroup = dealModel.getConditionGroup();
			if (dealConditionGroup == null)
			{
				continue;
			}

			final List<AbstractDealConditionModel> dealConditions = dealConditionGroup.getDealConditions();

			if (isProductInFreeGoodDealBenefit(productCode, dealModel))
			{
				LOG.debug("Adding complex deal [{}] to list for products: [{}]", dealModel, productCode);
				complexDeals.add(dealModel);
			}
			else if (CollectionUtils.isNotEmpty(dealConditions) && isComplexDealConditionOfProduct(productCode, dealConditions))
			{
				complexDeals.add(dealModel);
			}
		}
		return complexDeals;
	}


	/**
	 * Checks if is product in free good deal benefit.
	 *
	 * @param productCode
	 *           the product code
	 * @param dealModel
	 *           the deal model
	 * @return true, if is product in free good deal benefit
	 */
	private boolean isProductInFreeGoodDealBenefit(final List<String> productCode, final DealModel dealModel)
	{
		LOG.debug("Deals service isProductInFreeGoodDealBenefit method");

		LOG.debug("Deals service isProductInFreeGoodDealBenefit method product list {}", productCode);
		if (dealModel.getConditionGroup().getDealBenefits() != null)
		{
			for (final AbstractDealBenefitModel benefit : dealModel.getConditionGroup().getDealBenefits())
			{
				if (benefit instanceof FreeGoodsDealBenefitModel)
				{
					final String product = ((FreeGoodsDealBenefitModel) benefit).getProductCode();
					LOG.debug("Deals service isProductInFreeGoodDealBenefit method deal product", product);
					if (productCode.contains(product))
					{
						LOG.debug("Deals service isProductInFreeGoodDealBenefit contains true");
						return true;
					}
				}
			}
		}
		return false;
	}


	/**
	 * The Complex deal conditions in the presence of product.
	 *
	 * @param productCode
	 *           the product code
	 * @param dealConditions
	 *           the deal conditions
	 * @return true, if is complex deal condition of product
	 */
	private boolean isComplexDealConditionOfProduct(final List<String> productCode,
			final List<AbstractDealConditionModel> dealConditions)
	{
		final List<ProductModel> excludedProduct = productService.findExcludedProduct(dealConditions);

		for (final AbstractDealConditionModel dealCondition : dealConditions)
		{
			if (dealCondition instanceof ComplexDealConditionModel)
			{
				final ComplexDealConditionModel complexDealCondition = (ComplexDealConditionModel) dealCondition;

				final List<SABMAlcoholVariantProductMaterialModel> materials = productService.getProductByHierarchy(
						complexDealCondition.getLine(), complexDealCondition.getBrand(), complexDealCondition.getVariety(),
						complexDealCondition.getEmpties(), complexDealCondition.getEmptyType(), complexDealCondition.getPresentation());

				if (CollectionUtils.isNotEmpty(materials))
				{
					final Collection<ProductModel> filteredMaterial = CollectionUtils.subtract(materials, excludedProduct);

					for (final ProductModel material : filteredMaterial)
					{
						if (productCode.contains(material.getCode()))
						{
							return true;
						}
					}
				}
			}
			else if (dealCondition instanceof ProductDealConditionModel)
			{
				final ProductDealConditionModel productDealCondition = (ProductDealConditionModel) dealCondition;
				if (productCode.contains(productDealCondition.getProductCode()))
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
	 * @see com.sabmiller.core.deals.services.DealsService#findDealProducts(java.util.List)
	 */
	public List<ProductModel> findDealProducts(final List<AbstractDealConditionModel> dealConditions)
	{
		final List<ProductModel> products = new ArrayList<>();

		if (CollectionUtils.isEmpty(dealConditions))
		{
			return products;
		}

		final List<ProductModel> excludedProducts = productService.findExcludedProduct(dealConditions);

		for (final AbstractDealConditionModel condition : dealConditions)
		{
			if (BooleanUtils.isNotTrue(condition.getExclude()))
			{
				if (condition instanceof ProductDealConditionModel)
				{
					final ProductModel product = productService
							.getProductForCodeSafe(((ProductDealConditionModel) condition).getProductCode());

					if (product != null)
					{
						products.add(product);
					}
				}
				else if (condition instanceof ComplexDealConditionModel)
				{
					final ComplexDealConditionModel complexCondition = (ComplexDealConditionModel) condition;

					final List<? extends ProductModel> materials = productService.getProductByHierarchy(complexCondition.getLine(),
							complexCondition.getBrand(), complexCondition.getVariety(), complexCondition.getEmpties(),
							complexCondition.getEmptyType(), complexCondition.getPresentation());

					if (CollectionUtils.isNotEmpty(materials))
					{
						products.addAll(materials);
					}
				}
			}
		}

		return ListUtils.subtract(products, excludedProducts);
	}

	/**
	 * Generates the code for the deal model based on the parameters passed. As SAP does not have a clear cut PK, we use
	 * the combination of these fields to generate the deal code. This code can then be used down the line to check the
	 * existance of the deal.
	 *
	 * @param param
	 *           the param
	 * @return the int
	 */
	@Override
	public int generateDealsCode(final DealCodeGeneratorParam param)
	{
		return (param.getSalesOrg() + param.getCustomerId() + param.getMaterial() + param.getValidFrom().toString()
				+ param.getValidTo().toString() + param.getMinQty() + param.getUom() + param.getDealType()).hashCode();
	}

	/*
	 * (non-Javadoc)
	 *
	 * @see com.sabmiller.core.deals.services.DealsService#generateTriggerHash(com.sabmiller.core.model.DealModel)
	 */
	@Override
	public int generateTriggerHash(final DealModel dealModel)
	{
		final List<String> triggers = new ArrayList<String>();

		for (final ProductDealConditionModel condition : getProductDealConditions(dealModel))
		{
			final String uom = BooleanUtils.toBoolean(condition.getExclude()) ? null : condition.getUnit().getCode();
			triggers.add(String.valueOf(condition.getExclude()) + String.valueOf(condition.getMandatory()) + condition.getMinQty()
					+ condition.getProductCode() + uom);
		}

		for (final ComplexDealConditionModel condition : getComplexDealConditions(dealModel))
		{
			final String uom = BooleanUtils.toBoolean(condition.getExclude()) ? null : condition.getUnit().getCode();
			triggers.add(condition.getBrand() + condition.getEmpties() + condition.getEmptyType() + condition.getLine()
					+ condition.getPresentation() + condition.getProductCode() + condition.getVariety() + condition.getQuantity()
					+ condition.getExclude() + condition.getMandatory() + uom);
		}
		Collections.sort(triggers);

		final StringBuilder triggerBuilder = new StringBuilder();
		for (final String eachTrigger : triggers)
		{
			triggerBuilder.append(eachTrigger);
		}
		return triggerBuilder.toString().hashCode();
	}


	/*
	 * (non-Javadoc)
	 *
	 * @see com.sabmiller.core.deals.services.DealsService#getDeal(java.lang.String)
	 */
	@Override
	public DealModel getDeal(final String dealCode)
	{
		return dealsDao.getDeal(dealCode);
	}

	/**
	 * Checks if is multi range.
	 *
	 * @param dealConditions
	 *           the deal conditions
	 * @return true, if is multi range
	 */
	public boolean isMultiRange(final List<AbstractDealConditionModel> dealConditions)
	{
		for (final AbstractDealConditionModel condition : dealConditions)
		{
			if (condition instanceof ComplexDealConditionModel
					&& StringUtils.isNotEmpty(((ComplexDealConditionModel) condition).getBrand())
					&& BooleanUtils.isNotTrue(condition.getExclude())
					&& (StringUtils.isEmpty(((ComplexDealConditionModel) condition).getEmpties())
					|| StringUtils.isEmpty(((ComplexDealConditionModel) condition).getEmptyType())
					|| StringUtils.isEmpty(((ComplexDealConditionModel) condition).getVariety())
					|| StringUtils.isEmpty(((ComplexDealConditionModel) condition).getPresentation())))
			{
				return true;
			}
		}
		return false;
	}

	/**
	 * Checks if is across.
	 *
	 * @param dealCondition
	 *           the deal condition
	 * @return true, if is across
	 */
	public boolean isAcross(final DealConditionGroupModel dealCondition)
	{
		if (CollectionUtils.isNotEmpty(dealCondition.getDealScales()))
		{
			final List<DealScaleModel> dealScales = new ArrayList<>(dealCondition.getDealScales());
			Collections.sort(dealScales, DealScaleComparator.INSTANCE);

			if (dealScales.get(0).getFrom() > 0)
			{
				int complexCounter = 0;
				int prodCounter = 0;
				for (final AbstractDealConditionModel condition : dealCondition.getDealConditions())
				{
					if (BooleanUtils.isNotTrue(condition.getExclude()))
					{
						if (condition instanceof ComplexDealConditionModel)
						{
							complexCounter++;
						}
						else if (condition instanceof ProductDealConditionModel && BooleanUtils.isNotTrue(condition.getExclude()))
						{
							prodCounter++;
						}
					}
				}

				return complexCounter > 1 || prodCounter > 1;
			}
		}
		return false;
	}

	/**
	 * Persist discount deals.
	 *
	 * @param b2bUnitModel
	 *           the b2b unit model
	 * @param deliveryDate
	 *           the delivery date
	 * @param dealsResponse
	 *           the deals response
	 */
	protected void persistDiscountDeals(final B2BUnitModel b2bUnitModel, final Date deliveryDate,
			final DealsResponse dealsResponse)
	{
		new DiscountDealsUpdator(b2bUnitModel, deliveryDate).update(dealsResponse);
	}


	/**
	 * Persist bogof deals.
	 *
	 * @param b2bUnitModel
	 *           the b2b unit model
	 * @param deliveryDate
	 *           the delivery date
	 * @param dealsResponse
	 *           the deals response
	 */
	protected void persistBogofDeals(final B2BUnitModel b2bUnitModel, final Date deliveryDate, final DealsResponse dealsResponse)
	{
	}

	/**
	 * Persist once off deals.
	 *
	 * @param b2bUnitModel
	 *           the b2b unit model
	 * @param deliveryDate
	 *           the delivery date
	 * @param dealsResponse
	 *           the deals response
	 */
	protected void persistOnceOffDeals(final B2BUnitModel b2bUnitModel, final Date deliveryDate, final DealsResponse dealsResponse)
	{
		new OnceOffDealsUpdator(b2bUnitModel, deliveryDate).update(dealsResponse);
	}


	/**
	 * If Deals refresh status is IN_PROGRESS, then set it back to DONE.
	 *
	 * @param b2bUnitModel
	 *           the b2b unit model
	 */
	private void resetDealsRefreshStatus(final B2BUnitModel b2bUnitModel)
	{
		modelService.refresh(b2bUnitModel);
		if (SapServiceCallStatus.IN_PROGRESS.equals(b2bUnitModel.getBogofCallStatus()))
		{
			b2bUnitModel.setBogofCallStatus(SapServiceCallStatus.ERROR);
		}
		if (SapServiceCallStatus.IN_PROGRESS.equals(b2bUnitModel.getOnceOffDealCallStatus()))
		{
			b2bUnitModel.setOnceOffDealCallStatus(SapServiceCallStatus.ERROR);
		}
		modelService.save(b2bUnitModel);
	}

	/*
	 * (non-Javadoc)
	 *
	 * @see com.sabmiller.core.deals.services.DealsService#refreshDeals(de.hybris.platform.b2b.model.B2BUnitModel)
	 */
	@Override
	public void refreshDeals(final B2BUnitModel b2bUnitModel) {
		if (!sabmConfigurationService.isEnablePricingBOGOFDeals()) {
			return;
		}

		final Date today = new Date();
		final String userId = userService.getCurrentUser().getUid();
		Executors.newCachedThreadPool().execute(()-> {

			onThreadExecution(userId);

			final Future<DealsResponse> bogofDealsFuture = invokeBogofService(b2bUnitModel, today, userId);
			final Future<DealsResponse> onceOffDealsFuture = invokeOnceOffDealsService(b2bUnitModel, today, userId);

			try {
				if (bogofDealsFuture != null) {
					persistBogofDeals(b2bUnitModel, today, bogofDealsFuture.get());
				}
			} catch (final Exception e) {
				LOG.error("Error occured while refreshing BOGOF deals from SAP ", e);
			}

			try {
				if (onceOffDealsFuture != null) {
					persistOnceOffDeals(b2bUnitModel, today, onceOffDealsFuture.get());
				}
			} catch (final Exception e) {
				LOG.error("Error occured while refreshing Ones Off deals from SAP ", e);
			}

			resetDealsRefreshStatus(b2bUnitModel);
			refreshDealCache();

		});

	}

	@Override
	public void importDeals(final B2BUnitModel b2BUnit, final Date deliveryDate, final CustomerUnitPricingResponse customerUnitPricingResponse) {
		final DealsResponse dealsResponse = new DealsResponse(customerUnitPricingResponse);
		dealsPriorityService.mergeOverlappingDeals(dealsResponse);
		persistDiscountDeals(b2BUnit,deliveryDate,dealsResponse);
		refreshDealCache();
	}


	/**
	 * Mark discount deal in progress.
	 *
	 * @param b2bUnitModel
	 *           the b2b unit model
	 */
	private void markDiscountDealInProgress(final B2BUnitModel b2bUnitModel)
	{
		final Transaction tx = Transaction.current();
		tx.begin();
		boolean success = true;
		try
		{
			final B2BUnitModel b2bUnitWithLock = modelService.getWithLock(modelService.getSource(b2bUnitModel));
			b2bUnitWithLock.setDiscountCallStatus(SapServiceCallStatus.IN_PROGRESS);
			modelService.save(b2bUnitWithLock);
		}
		catch (final Exception e)
		{
			LOG.error(
					"Exception occured while trying to set Discount deal status IN_PROGRESS. Swallowing this as the side effects are minimal");
			success = false;
		}
		finally
		{
			if (success)
			{
				tx.commit();
			}
			else
			{
				tx.rollback();
			}
		}
	}

	/**
	 * Mark once off deal in progress.
	 *
	 * @param b2bUnitModel
	 *           the b2b unit model
	 */
	private void markOnceOffDealInProgress(final B2BUnitModel b2bUnitModel)
	{
		final Transaction tx = Transaction.current();
		tx.begin();
		boolean success = true;
		try
		{
			final B2BUnitModel b2bUnitWithLock = modelService.getWithLock(modelService.getSource(b2bUnitModel));
			b2bUnitWithLock.setOnceOffDealCallStatus(SapServiceCallStatus.IN_PROGRESS);
			modelService.save(b2bUnitWithLock);
		}
		catch (final Exception e)
		{
			LOG.error(
					"Exception occured while trying to set Discount deal status IN_PROGRESS. Swallowing this as the side effects are minimal");
			success = false;
		}
		finally
		{
			if (success)
			{
				tx.commit();
			}
			else
			{
				tx.rollback();
			}
		}
		b2bUnitModel.setRefreshEntitiesLastUpdatedTime(new Date());
		modelService.save(b2bUnitModel);
	}

	/**
	 * Mark bogof deal in progress.
	 *
	 * @param b2bUnitModel
	 *           the b2b unit model
	 */
	private void markBogofDealInProgress(final B2BUnitModel b2bUnitModel)
	{
		final Transaction tx = Transaction.current();
		tx.begin();
		boolean success = true;
		try
		{
			final B2BUnitModel b2bUnitWithLock = modelService.getWithLock(modelService.getSource(b2bUnitModel));
			b2bUnitWithLock.setBogofCallStatus(SapServiceCallStatus.IN_PROGRESS);
			modelService.save(b2bUnitWithLock);
		}
		catch (final Exception e)
		{
			LOG.error(
					"Exception occured while trying to set Bogof status IN_PROGRESS. Swallowing this as the side effects are minimal");
			success = false;
		}
		finally
		{
			if (success)
			{
				tx.commit();
			}
			else
			{
				tx.rollback();
			}
		}
	}

	/**
	 * Invoke bogof service.
	 *
	 * @param b2bUnitModel
	 *           the b2b unit model
	 * @param deliveryDate
	 *           the delivery date
	 * @param userId
	 *           the user id
	 * @return the future
	 */
	protected Future<DealsResponse> invokeBogofService(final B2BUnitModel b2bUnitModel, final Date deliveryDate,
			final String userId)
	{
		if (b2bUnitService.isBOGOFDealsObsolete(b2bUnitModel, deliveryDate) && !SapServiceCallStatus.IN_PROGRESS.equals(b2bUnitModel.getBogofCallStatus()))
		{
			//markBogofDealInProgress(b2bUnitModel);
			b2bUnitModel.setBogofCallStatus(SapServiceCallStatus.IN_PROGRESS);
			modelService.save(b2bUnitModel);

			return Executors.newCachedThreadPool().submit(new Callable<DealsResponse>()
			{
				@Override
				public DealsResponse call() throws Exception
				{
					onThreadExecution(userId);
					return fetchBOGOFDeals(b2bUnitModel, deliveryDate);
				}
			});
		}
		LOG.debug("BOGOF deals are not obsolete. Not invoking the SAP service");
		return null;
	}

	/**
	 * Invoke once off deals service.
	 *
	 * @param b2bUnitModel
	 *           the b2b unit model
	 * @param deliveryDate
	 *           the delivery date
	 * @param userId
	 *           the user id
	 * @return the future
	 */
	protected Future<DealsResponse> invokeOnceOffDealsService(final B2BUnitModel b2bUnitModel, final Date deliveryDate,
			final String userId)
	{
		if (b2bUnitService.isOnceOffDealsObsolete(b2bUnitModel, deliveryDate) && !SapServiceCallStatus.IN_PROGRESS.equals(b2bUnitModel.getOnceOffDealCallStatus()))
		{
			//markOnceOffDealInProgress(b2bUnitModel);
			b2bUnitModel.setOnceOffDealCallStatus(SapServiceCallStatus.IN_PROGRESS);
			modelService.save(b2bUnitModel);

			return Executors.newCachedThreadPool().submit(new Callable<DealsResponse>()
			{
				@Override
				public DealsResponse call() throws Exception
				{
					onThreadExecution(userId);
					return fetchOnceOffDeals(b2bUnitModel, deliveryDate);
				}
			});
		}

		LOG.debug("One Off deals are not obsolete. Not invoking the SAP service");
		return null;
	}

	/*
	 * (non-Javadoc)
	 *
	 * @see com.sabmiller.core.deals.services.DealsService#refreshOneOffDeals(de.hybris.platform.b2b.model.B2BUnitModel)
	 */
	@Override
	public void refreshOneOffDeals(final B2BUnitModel b2bUnitModel)
	{
		final String userId = userService.getCurrentUser().getUid();
		final Date today = new Date();
		markOnceOffDealInProgress(b2bUnitModel);
		Executors.newCachedThreadPool().execute(new Runnable()
		{
			@Override
			public void run()
			{
				try
				{
					onThreadExecution(userId);
					persistOnceOffDeals(b2bUnitModel, today, fetchOnceOffDeals(b2bUnitModel, today));
					if (SapServiceCallStatus.IN_PROGRESS.equals(b2bUnitModel.getOnceOffDealCallStatus()))
					{
						b2bUnitModel.setOnceOffDealCallStatus(SapServiceCallStatus.ERROR);
					}
				}
				catch (final Exception e)
				{
					LOG.error("Exception occurred while refreshing once off deals ", e);
				}
			}
		});

	}


	/**
	 * On thread execution.
	 *
	 * @param userId
	 *           the user id
	 */
	private void onThreadExecution(final String userId)
	{
		Registry.activateMasterTenant();
		userService.setCurrentUser(userService.getUserForUID("integrationAdmin"));
		catalogVersionService.setSessionCatalogVersion(catalogVersionDeterminationStrategy.getCatalogId(),
				CatalogManager.ONLINE_VERSION);

		final String sessionAttrUserId = Config.getString("session.attr.user.invoking.sap.service", "CURRENT_USER_SAP_INVOCATION");
		sessionService.setAttribute(sessionAttrUserId, userId);
	}



	/**
	 * Fetch existing deals.
	 *
	 * @param b2bUnitModel
	 *           the b2b unit model
	 * @param dealType
	 *           the deal type
	 * @return the list
	 */
	private List<DealModel> fetchExistingDeals(final B2BUnitModel b2bUnitModel, final DealTypeEnum dealType)
	{
		return dealsDao.getDealsByType(b2bUnitModel, dealType);
	}


	/**
	 * Fetch bogof deals.
	 *
	 * @param b2bUnitModel
	 *           the b2b unit model
	 * @param deliveryDate
	 *           the delivery date
	 * @return the deals response
	 * @throws SABMIntegrationException
	 *            the SABM integration exception
	 * @throws ConversionException
	 *            the conversion exception
	 */
	protected DealsResponse fetchBOGOFDeals(final B2BUnitModel b2bUnitModel, final Date deliveryDate)
			throws SABMIntegrationException, ConversionException
	{
		LOG.debug("Invoking the SAP service to refresh the BOGOF deals for B2BUnit : {} , Delivery Date : {} ", b2bUnitModel,
				deliveryDate);

		//Invoke the SAP Web service to fetch all the BOGOF deals associated with the customer (B2BUnit)
		final PricingBOGOFDealsResponse response = bogofDealsRequestHandler
				.sendPostRequest(bogofDealRequestConverter.convert(b2bUnitModel, new PricingBOGOFDealsRequest(), deliveryDate));

		//Merge any overlapping deals
		final DealsResponse dealsResponse = new DealsResponse(response);
		dealsPriorityService.mergeOverlappingDeals(dealsResponse);

		return dealsResponse;
	}

	/**
	 * Fetch once off deals.
	 *
	 * @param b2bUnitModel
	 *           the b2b unit model
	 * @param deliveryDate
	 *           the delivery date
	 * @return the deals response
	 * @throws SABMIntegrationException
	 *            the SABM integration exception
	 * @throws ConversionException
	 *            the conversion exception
	 */
	protected DealsResponse fetchOnceOffDeals(final B2BUnitModel b2bUnitModel, final Date deliveryDate)
			throws SABMIntegrationException, ConversionException
	{
		LOG.debug("Invoking the SAP service to refresh the ONCE OFF (LIMITED) deals for customer {}, Delivery Date : {} ",
				b2bUnitModel, deliveryDate);

		//Invoke the SAP Web service to fetch all the Discount deals associated with the customer (B2BUnit)
		final PricingDiscountConditionsResponse response = discountDealsRequestHandler.sendPostRequest(
				onceOffDealsRequestConverter.convert(b2bUnitModel, new PricingDiscountConditionsRequest(), deliveryDate));

		//Merge any overlapping deals
		final DealsResponse dealsResponse = new DealsResponse(response);
		dealsPriorityService.mergeOverlappingDeals(dealsResponse);

		return dealsResponse;
	}



	/*
	 * (non-Javadoc)
	 *
	 * @see com.sabmiller.core.deals.services.DealsService#dealExistForProduct(de.hybris.platform.b2b.model.B2BUnitModel,
	 * java.lang.String, java.util.Date)
	 */
	@Override
	public List<List<DealModel>> dealExistForProduct(final B2BUnitModel b2bUnitModel, final String productCode, final Date fromDate)
	{
		// this session will be used in the RepDrivenDealStatusAttributeHandler to get the deal status
		if (b2bUnitModel != null)
		{
			sessionService.setAttribute(SabmCoreConstants.SESSION_SELECT_B2BUNIT_UID_DATA, b2bUnitModel.getUid());
		}
		List<List<DealModel>> composedDeals = new ArrayList<>();
		try
		{
			final ProductModel eanProduct = productService.getProductForCode(productCode);

			//To determine whether to exist deals by b2bunit,productCode,date
			if (eanProduct != null && CollectionUtils.isNotEmpty(eanProduct.getVariants()))
			{
				final List<DealModel> dealModels = getDealsForProduct(b2bUnitModel,
						productService.getProductsCode(eanProduct.getVariants()), fromDate, fromDate);
				// To determine the valid deals
				final List<DealModel> dealsFiltered = getValidationDeals(dealModels, Boolean.TRUE);

				composedDeals = composeComplexFreeProducts(dealsFiltered);
				LOG.debug("Composed Deals: {}" + composedDeals);
			}
		}
		catch (AmbiguousIdentifierException | UnknownIdentifierException | IllegalArgumentException e)
		{
			LOG.debug(e.getMessage(), e);
			LOG.warn("Error fetching product with code: " + productCode);
		}

		return composedDeals;
	}

	/*
	 * (non-Javadoc)
	 *
	 * @see com.sabmiller.core.deals.services.DealsService#dealBelongsToProduct(com.sabmiller.core.model.DealModel,
	 * com.sabmiller.core.model.SABMAlcoholVariantProductMaterialModel)
	 */
	@Override
	public boolean productBelongsToDeal(final DealModel deal, final SABMAlcoholVariantProductMaterialModel material)
	{
		if (material == null)
		{
			return Boolean.FALSE;
		}

		final ProductModel eanProduct = material.getBaseProduct();

		//To determine whether to exist deals by b2bunit,productCode,date
		if (eanProduct != null && CollectionUtils.isNotEmpty(eanProduct.getVariants()))
		{
			final Date deliveryDate = (Date) sessionService.getAttribute(SabmCoreConstants.SESSION_ATTR_DELIVERY_DATE);

			final List<DealModel> dealModels = getDealsForProduct(b2bCommerceUnitService.getParentUnit(),
					productService.getProductsCode(eanProduct.getVariants()), deliveryDate, deliveryDate);
			// To determine the valid deals
			final List<DealModel> dealsFiltered = getValidationDeals(dealModels, Boolean.TRUE);

			LOG.debug("dealsFiltered: {}" + dealsFiltered);

			return CollectionUtils.emptyIfNull(dealsFiltered).contains(deal);
		}

		return Boolean.FALSE;
	}

	/*
	 * (non-Javadoc)
	 *
	 * @see com.sabmiller.core.deals.services.DealsService#importComplexDeal(com.sabmiller.facades.complexdeals.data.
	 * ComplexDealData)
	 */
	@Override
	public void importComplexDeal(final DealModel dealModel, final ImportContext importContext)
	{
		normalizeAssignees(dealModel, importContext.getData());
		modelService.save(dealModel);

	}

	@Override
	public ImportContext createImportContext() {
	    return new DealsService.ImportContext(){
	    	B2bUnitByDealAssigneeProvider b2bUnitByDealAssigneeProvider = createB2bUnitByDealAssigneeProvider(b2bUnitService,1000,true);
            @Override
            public <T> T getData() {
              return (T) b2bUnitByDealAssigneeProvider;
            }
        };
	}

	/**
	 * Normalize assignees.
	 *
	 * @param dealModel
	 *           the deal model
	 */
	protected void normalizeAssignees(final DealModel dealModel,final B2bUnitByDealAssigneeProvider dealAssigneeProvider)
	{
		final List<B2BUnitModel> assignToList = new ArrayList<B2BUnitModel>();
		final List<B2BUnitModel> excludeFromList = new ArrayList<B2BUnitModel>();
		for (final DealAssigneeModel eachAssignee : dealModel.getAssignees())
		{
			final List<B2BUnitModel> orgs = dealAssigneeProvider.getB2bUnits(eachAssignee);

			if (LOG.isDebugEnabled())
			{
				LOG.debug("b2bUnitService.searchB2BUnit() on Assignee : {} returned : {} ",
						ReflectionToStringBuilder.toString(eachAssignee), orgs);
			}

			if (BooleanUtils.isTrue(eachAssignee.getExclude()))
			{
				excludeFromList.addAll(orgs);
			}
			else
			{
				assignToList.addAll(orgs);
			}

		}

		assignToList.removeAll(excludeFromList);

		dealModel.setOrganizations(new HashSet<>(assignToList));

		modelService.save(dealModel);

		LOG.debug("In normalizeAssignees(). Deal {} has been assigned to {} customers. Customers : {} ", dealModel.getCode(),
				assignToList.size(), assignToList);
	}


	@Override
	public void normalizeAssigneesDeals() {


		final Calendar yesterday = Calendar.getInstance();
		yesterday.add(Calendar.DATE, -1); // just to be safe due to time diff, get starting as yesterday no harm vs everything

		final List<DealModel> complexDeals = dealsDao.getComplexDealsToDate(DateUtils.truncate(yesterday.getTime(), Calendar.DATE)); // truncate so the hours minutes etc doesn't matter

		if (CollectionUtils.isEmpty(complexDeals)) {
			return;
		}

		final B2bUnitByDealAssigneeProvider b2bUnitByDealAssigneeProvider = createB2bUnitByDealAssigneeProvider(b2bUnitService, 5000, true);

		final ExecutorService executorService = Executors.newFixedThreadPool(8, new MultiThreadJobFactory());

		try {

			for (final DealModel dealModel : complexDeals) {
				executorService.submit(() -> normalizeAssignees(dealModel, b2bUnitByDealAssigneeProvider));
			}

		} finally {
			executorService.shutdown();
		}

		try {
			executorService.awaitTermination(30, TimeUnit.MINUTES);
		} catch (final InterruptedException e) {
			LOG.warn("Waiting for deal assignee normalisation interrupted. Forcing shutdown of executor service", e);
			executorService.shutdownNow();
		}

	}

	/**
	 * Creates the query param.
	 *
	 * @param model
	 *           the model
	 * @return the search b2 b unit query param
	 */
	private static SearchB2BUnitQueryParam createQueryParam(final DealAssigneeModel model)
	{
		return new SearchB2BUnitQueryParam.Builder().banner(model.getBanner()).customer(model.getB2bUnit())
				.customerGroup(model.getCustomerGroup()).distributionChannel(model.getDistributionChannel())
				.division(model.getDivision()).plant(model.getPlant()).priceGroup(model.getPriceGroup())
				.primaryBanner(model.getPrimaryBanner()).salesGroup(model.getSalesGroup()).salesOffice(model.getSalesOffice())
				.salesOrgId(model.getSalesOrg()).subBanner(model.getSubBanner()).subChannel(model.getSubChannel()).build();
	}


	/**
	 * Updates the Deals in the Hybris system. Relies on the appropriate converters to get the {@link DealModel}, after
	 * which the corresponding status in the {@link B2BUnitModel} are set
	 *
	 * @author joshua.a.antony
	 */
	abstract class DealsUpdator
	{

		/** The b2b unit model. */
		private final B2BUnitModel b2bUnitModel;

		/** The delivery date. */
		private final Date deliveryDate;

		/**
		 * Mark deals as refreshed.
		 *
		 * @param b2bUnitModel
		 *           the b2b unit model
		 * @param deliveryDate
		 *           the delivery date
		 */
		abstract void markDealsAsRefreshed(B2BUnitModel b2bUnitModel, Date deliveryDate);

		/**
		 * Gets the deal reverse converter.
		 *
		 * @return the deal reverse converter
		 */
		abstract Converter<DealsResponse, List<DealModel>> getDealReverseConverter();

		/**
		 * Sets the call status as done.
		 *
		 * @param b2bUnitModel
		 *           the new call status as done
		 */
		abstract void setCallStatusAsDone(B2BUnitModel b2bUnitModel);

		/**
		 * Sets the call status as error.
		 *
		 * @param b2bUnitModel
		 *           the new call status as error
		 */
		abstract void setCallStatusAsError(B2BUnitModel b2bUnitModel);

		/**
		 * Deal type.
		 *
		 * @return the deal type enum
		 */
		abstract DealTypeEnum dealType();

		/**
		 * Instantiates a new deals updator.
		 *
		 * @param b2bUnitModel
		 *           the b2b unit model
		 * @param deliveryDate
		 *           the delivery date
		 */
		public DealsUpdator(final B2BUnitModel b2bUnitModel, final Date deliveryDate)
		{
			this.b2bUnitModel = b2bUnitModel;
			this.deliveryDate = deliveryDate;
		}

		/**
		 * Update.
		 *
		 * @param dealsResponse
		 *           the deals response
		 */
		protected void update(final DealsResponse dealsResponse)
		{
			//final Transaction tx = Transaction.current();
			//tx.begin();
			boolean success = true;
			try
			{
				modelService.removeAll(fetchExistingDeals(b2bUnitModel, dealType()));
				modelService.refresh(b2bUnitModel);

				final List<DealModel> deals = getDealReverseConverter().convert(dealsResponse);

				for (final DealModel eachDeal : ListUtils.emptyIfNull(deals))
				{
					eachDeal.setB2bUnit(b2bUnitModel);
				}
				modelService.saveAll(deals);
				modelService.refresh(b2bUnitModel);

				LOG.debug("Got the {} deals from SAP for Customer {} . Marking the deals as refreshed", dealType(), b2bUnitModel);

				markDealsAsRefreshed(b2bUnitModel, deliveryDate);//Finally put an entry on B2BUnit denoting when that the deal was refreshed
				setCallStatusAsDone(b2bUnitModel);
				modelService.save(b2bUnitModel);
			}
			catch (final Exception e)
			{
				LOG.error("Exception occured while trying to refresh" + dealType() + " Deals ", e);

				success = false;
			}
			finally
			{
				/*
				 * if (success) { tx.commit(); } else { tx.rollback(); setCallStatusAsError(b2bUnitModel);
				 * modelService.save(b2bUnitModel); }
				 */
				if (!success)
				{
					setCallStatusAsError(b2bUnitModel);
					b2bUnitModel.setRefreshEntitiesLastUpdatedTime(new Date());

					modelService.save(b2bUnitModel);
				}
			}
		}
	}

	/**
	 * The Class BogofDealsUpdator.
	 */
	private class BogofDealsUpdator extends DealsUpdator
	{

		/**
		 * Instantiates a new bogof deals updator.
		 *
		 * @param b2bUnitModel
		 *           the b2b unit model
		 * @param deliveryDate
		 *           the delivery date
		 */
		public BogofDealsUpdator(final B2BUnitModel b2bUnitModel, final Date deliveryDate)
		{
			super(b2bUnitModel, deliveryDate);
		}

		/*
		 * (non-Javadoc)
		 *
		 * @see
		 * com.sabmiller.core.deals.services.DealsServiceImpl.DealsUpdator#markDealsAsRefreshed(de.hybris.platform.b2b.
		 * model.B2BUnitModel, java.util.Date)
		 */
		@Override
		void markDealsAsRefreshed(final B2BUnitModel b2bUnitModel, final Date deliveryDate)
		{
			b2bUnitService.markBOGOFDealsAsRefreshed(b2bUnitModel, deliveryDate);

		}

		/*
		 * (non-Javadoc)
		 *
		 * @see com.sabmiller.core.deals.services.DealsServiceImpl.DealsUpdator#getDealReverseConverter()
		 */
		@Override
		Converter<DealsResponse, List<DealModel>> getDealReverseConverter()
		{
			return bogofDealReverseConverter;
		}

		/*
		 * (non-Javadoc)
		 *
		 * @see
		 * com.sabmiller.core.deals.services.DealsServiceImpl.DealsUpdator#setCallStatusAsDone(de.hybris.platform.b2b.
		 * model.B2BUnitModel)
		 */
		@Override
		void setCallStatusAsDone(final B2BUnitModel b2bUnitModel)
		{
			b2bUnitModel.setBogofCallStatus(SapServiceCallStatus.DONE);
		}

		/*
		 * (non-Javadoc)
		 *
		 * @see
		 * com.sabmiller.core.deals.services.DealsServiceImpl.DealsUpdator#setCallStatusAsError(de.hybris.platform.b2b.
		 * model.B2BUnitModel)
		 */
		@Override
		void setCallStatusAsError(final B2BUnitModel b2bUnitModel)
		{
			b2bUnitModel.setBogofCallStatus(SapServiceCallStatus.ERROR);
		}

		/*
		 * (non-Javadoc)
		 *
		 * @see com.sabmiller.core.deals.services.DealsServiceImpl.DealsUpdator#dealType()
		 */
		@Override
		DealTypeEnum dealType()
		{
			return DealTypeEnum.BOGOF;
		}
	}

	/**
	 * The Class DiscountDealsUpdator.
	 */
	private class DiscountDealsUpdator extends DealsUpdator
	{

		/**
		 * Instantiates a new discount deals updator.
		 *
		 * @param b2bUnitModel
		 *           the b2b unit model
		 * @param deliveryDate
		 *           the delivery date
		 */
		public DiscountDealsUpdator(final B2BUnitModel b2bUnitModel, final Date deliveryDate)
		{
			super(b2bUnitModel, deliveryDate);
		}

		/*
		 * (non-Javadoc)
		 *
		 * @see
		 * com.sabmiller.core.deals.services.DealsServiceImpl.DealsUpdator#markDealsAsRefreshed(de.hybris.platform.b2b.
		 * model.B2BUnitModel, java.util.Date)
		 */
		@Override
		void markDealsAsRefreshed(final B2BUnitModel b2bUnitModel, final Date deliveryDate)
		{
			b2bUnitService.markDiscountDealsAsRefreshed(b2bUnitModel, deliveryDate);

		}

		/*
		 * (non-Javadoc)
		 *
		 * @see com.sabmiller.core.deals.services.DealsServiceImpl.DealsUpdator#getDealReverseConverter()
		 */
		@Override
		Converter<DealsResponse, List<DealModel>> getDealReverseConverter()
		{
			return pricingDiscountDealReverseConverter;
		}

		/*
		 * (non-Javadoc)
		 *
		 * @see
		 * com.sabmiller.core.deals.services.DealsServiceImpl.DealsUpdator#setCallStatusAsDone(de.hybris.platform.b2b.
		 * model.B2BUnitModel)
		 */
		@Override
		void setCallStatusAsDone(final B2BUnitModel b2bUnitModel)
		{
			b2bUnitModel.setDiscountCallStatus(SapServiceCallStatus.DONE);
		}

		/*
		 * (non-Javadoc)
		 *
		 * @see
		 * com.sabmiller.core.deals.services.DealsServiceImpl.DealsUpdator#setCallStatusAsError(de.hybris.platform.b2b.
		 * model.B2BUnitModel)
		 */
		@Override
		void setCallStatusAsError(final B2BUnitModel b2bUnitModel)
		{
			b2bUnitModel.setDiscountCallStatus(SapServiceCallStatus.ERROR);
		}

		/*
		 * (non-Javadoc)
		 *
		 * @see com.sabmiller.core.deals.services.DealsServiceImpl.DealsUpdator#dealType()
		 */
		@Override
		DealTypeEnum dealType()
		{
			return DealTypeEnum.DISCOUNT;
		}
	}

	/**
	 * The Class OnceOffDealsUpdator.
	 */
	private class OnceOffDealsUpdator extends DealsUpdator
	{

		/**
		 * Instantiates a new once off deals updator.
		 *
		 * @param b2bUnitModel
		 *           the b2b unit model
		 * @param deliveryDate
		 *           the delivery date
		 */
		public OnceOffDealsUpdator(final B2BUnitModel b2bUnitModel, final Date deliveryDate)
		{
			super(b2bUnitModel, deliveryDate);
		}

		/*
		 * (non-Javadoc)
		 *
		 * @see
		 * com.sabmiller.core.deals.services.DealsServiceImpl.DealsUpdator#markDealsAsRefreshed(de.hybris.platform.b2b.
		 * model.B2BUnitModel, java.util.Date)
		 */
		@Override
		void markDealsAsRefreshed(final B2BUnitModel b2bUnitModel, final Date deliveryDate)
		{
			b2bUnitService.markOnceOffDealsAsRefreshed(b2bUnitModel, deliveryDate);

		}

		/*
		 * (non-Javadoc)
		 *
		 * @see com.sabmiller.core.deals.services.DealsServiceImpl.DealsUpdator#getDealReverseConverter()
		 */
		@Override
		Converter<DealsResponse, List<DealModel>> getDealReverseConverter()
		{
			return onceOffDealsReverseConverter;
		}

		/*
		 * (non-Javadoc)
		 *
		 * @see
		 * com.sabmiller.core.deals.services.DealsServiceImpl.DealsUpdator#setCallStatusAsDone(de.hybris.platform.b2b.
		 * model.B2BUnitModel)
		 */
		@Override
		void setCallStatusAsDone(final B2BUnitModel b2bUnitModel)
		{
			b2bUnitModel.setOnceOffDealCallStatus(SapServiceCallStatus.DONE);
		}

		/*
		 * (non-Javadoc)
		 *
		 * @see
		 * com.sabmiller.core.deals.services.DealsServiceImpl.DealsUpdator#setCallStatusAsError(de.hybris.platform.b2b.
		 * model.B2BUnitModel)
		 */
		@Override
		void setCallStatusAsError(final B2BUnitModel b2bUnitModel)
		{
			b2bUnitModel.setOnceOffDealCallStatus(SapServiceCallStatus.ERROR);
		}

		/*
		 * (non-Javadoc)
		 *
		 * @see com.sabmiller.core.deals.services.DealsServiceImpl.DealsUpdator#dealType()
		 */
		@Override
		DealTypeEnum dealType()
		{
			return DealTypeEnum.LIMITED;
		}
	}

	/*
	 * (non-Javadoc)
	 *
	 * @see com.sabmiller.core.deals.services.DealsService#getValidationDeals(java.util.List, boolean)
	 */
	@Override
	public List<DealModel> getValidationDeals(final List<DealModel> deals, final boolean judgValidPeriod)
	{
		final Date deliveryDate = (Date) sessionService.getAttribute(SabmCoreConstants.SESSION_ATTR_DELIVERY_DATE);

		return this.getValidationDeals(deliveryDate, getDealsByRepDrivenStatus(deals), judgValidPeriod);
	}

	/**
	 * Get Validation qualified deals.
	 *
	 * @param deliveryDate
	 *           the delivery date
	 * @param deals
	 *           This is complex deals
	 * @param judgValidPeriod
	 *           the judg valid period
	 * @return List<DealModel> the result deals
	 */
	@Override
	public List<DealModel> getValidationDeals(final Date deliveryDate, final List<DealModel> deals, final boolean judgValidPeriod)
	{
		List<DealModel> dealsFiltered = null;

		LOG.debug("Deals before validation: {}", deals);
		final Set<DealModel> dealset = new HashSet<>();

		CollectionUtils.addAll(dealset, deals);

		if (CollectionUtils.isNotEmpty(dealset))
		{
			dealsFiltered = new ArrayList<>();
			for (final DealModel deal : dealset)
			{
				// first judgment the deal is ValidityPeriod  second validate the deal
				if (isValidityPeriod(deliveryDate, deal, judgValidPeriod) && dealValidationStrategy.validateDeal(deal))
				{
					dealsFiltered.add(deal);
				}
			}
		}
		LOG.debug("Deals after validation: {}", dealsFiltered);

		return ListUtils.emptyIfNull(dealsFiltered);
	}


	/**
	 * Check the valid period with specified deliveryDate.
	 *
	 * @param deliveryDate
	 *           the delivery date
	 * @param deal
	 *           the deal
	 * @param judgValidPeriod
	 *           the judg valid period
	 * @return true, if is validity period
	 */
	@Override
	public boolean isValidityPeriod(final Date deliveryDate, final DealModel deal, final boolean judgValidPeriod)
	{
		LOG.debug("Deals info: deliveryDate:{}, deal.getValidFrom():{}, deal.getValidTo():{}", deliveryDate.getTime(),
				deal.getValidFrom().getTime(), deal.getValidTo().getTime());

		if (!judgValidPeriod)
		{
			return Boolean.TRUE;
		}
		if (null == deal.getValidFrom() || null == deal.getValidTo())
		{
			return Boolean.FALSE;
		}
		if (deliveryDate.getTime() >= deal.getValidFrom().getTime() && deliveryDate.getTime() <= deal.getValidTo().getTime())
		{
			LOG.debug("Deals info isValidityPeriod true: deal{} ", deal);
			return Boolean.TRUE;
		}
		return Boolean.FALSE;
	}

	/**
	 * To compose the deals which have the same triggerHash.
	 *
	 * @param dealModels
	 *           the deal models
	 * @return List<DealModel>
	 */
	@Override
	public List<List<DealModel>> composeComplexFreeProducts(final List<DealModel> dealModels)
	{
		final List<List<DealModel>> deals = new ArrayList<>();

		if (CollectionUtils.isEmpty(dealModels))
		{
			return deals;
		}

		final Map<String, List<DealModel>> complexDealsMap = Maps.newHashMap();

		//Checking if deals have the same trigger
		for (final DealModel dealModel : dealModels)
		{
			if (DealTypeEnum.COMPLEX.equals(dealModel.getDealType()))
			{
				if (StringUtils.isEmpty(dealModel.getTriggerHash()))
				{
					addDealAsSingleList(dealModel, deals);

					LOG.debug("Complex deal has no triggerHash: {}", dealModel);
					continue;
				}

				// combine the trigger to distinguish deals.
				String trigger = dealModel.getTriggerHash() + dealModel.getValidFrom().getTime() + dealModel.getValidTo().getTime();
				if (CollectionUtils.isNotEmpty(dealModel.getConditionGroup().getDealBenefits()))
				{
					trigger += BooleanUtils.isTrue(dealModel.getConditionGroup().getDealBenefits().get(0).getProportionalAmount());
					trigger += BooleanUtils.isTrue(dealModel.getConditionGroup().getDealBenefits().get(0).getProportionalFreeGood());
				}

				if (complexDealsMap.containsKey(trigger))
				{
					if (isOnlySingleBogofDeal(dealModel))
					{
						LOG.debug("Complex deal has  OnlySingleBogofDeal [{}]", dealModel);
						LOG.debug("Complex deal -  trigger: [{}]", trigger);
						complexDealsMap.get(trigger).add(dealModel);
					}
					else
					{
						LOG.debug("Complex deal has NO OnlySingleBogofDeal [{}]", dealModel);
						addDealAsSingleList(dealModel, deals);
					}
				}
				else
				{
					final List<DealModel> triggerDeals = new ArrayList<>();
					triggerDeals.add(dealModel);
					complexDealsMap.put(trigger, triggerDeals);
					LOG.debug("Complex deal [{}] adding to trigger list [{}]", dealModel, trigger);
				}
			}
			else
			{
				LOG.debug("Deal is not complex type [{}]", dealModel);
				addDealAsSingleList(dealModel, deals);
			}
		}

		//If deals have the same trigger will be populated as a single DealJson
		for (final Map.Entry<String, List<DealModel>> mapEntry : complexDealsMap.entrySet())
		{
			LOG.debug("Deals having same trigger [{}]", mapEntry.getValue());
			deals.add(mapEntry.getValue());
		}

		return deals;
	}

	/**
	 * Adds the deal as single list.
	 *
	 * @param deal
	 *           the deal
	 * @param deals
	 *           the deals
	 */
	protected void addDealAsSingleList(final DealModel deal, final List<List<DealModel>> deals)
	{
		final List<DealModel> dealList = new ArrayList<>();
		dealList.add(deal);
		deals.add(dealList);
	}

	/**
	 * Checks if is only single bogof deal.
	 *
	 * @param deal
	 *           the deal
	 * @return true, if is only single bogof deal
	 */
	@Override
	public boolean isOnlySingleBogofDeal(final DealModel deal)
	{
		if (deal == null || deal.getConditionGroup() == null || deal.getConditionGroup().getDealBenefits() == null
				|| (deal.getConditionGroup().getDealBenefits().size() > 1
						&& CollectionUtils.isEmpty(deal.getConditionGroup().getDealScales()))
				|| (deal.getConditionGroup().getDealBenefits().size() > 1
						&& deal.getConditionGroup().getDealScales().size() != deal.getConditionGroup().getDealBenefits().size()))
		{
			return false;
		}

		for (final AbstractDealBenefitModel benefit : deal.getConditionGroup().getDealBenefits())
		{
			if (benefit instanceof DiscountDealBenefitModel)
			{
				return false;
			}
		}
		return true;
	}

	/**
	 * send the confirm email.
	 *
	 * @param behaviourRequirements
	 *           the behaviour requirements
	 * @param activatedDealTitles
	 *           the activated deal titles
	 * @param deactivatedDealTitles
	 *           the deactivated deal titles
	 * @param fromUser
	 *           the from User
	 * @param toEmails
	 *           the to emails
	 * @param ccEmails
	 *           the cc emails
	 * @param b2bUnit
	 *           the b2b unit
	 * @param primaryAdminStatus
	 *           the primary admin status
	 */
	@Override
	public void sendConfirmEnabledDealsEmail(final String behaviourRequirements, final List<String> activatedDealTitles,
			final List<String> deactivatedDealTitles, final UserModel fromUser, final List<String> toEmails,
			final List<String> ccEmails, final B2BUnitModel b2bUnit, final String primaryAdminStatus)
	{
		eventService.publishEvent(initializeEvent(new ConfirmEnableDealEmailEvent(behaviourRequirements, activatedDealTitles,
				deactivatedDealTitles, fromUser, toEmails, ccEmails, b2bUnit, primaryAdminStatus)));

	}

	/**
	 * To set the attributes to the event.
	 *
	 * @param event
	 *           the event
	 * @return ConfirmEnableDealEmailEvent
	 */
	protected ConfirmEnableDealEmailEvent initializeEvent(final ConfirmEnableDealEmailEvent event)
	{
		event.setBaseStore(baseStoreService.getBaseStoreForUid("sabmStore"));
		event.setSite(baseSiteService.getBaseSiteForUID("sabmStore"));
		event.setLanguage(commonI18NService.getLanguage("en"));
		event.setCurrency(commonI18NService.getCurrency("AUD"));
		return event;
	}


	/**
	 * Gets the complex deal conditions.
	 *
	 * @param dealModel
	 *           the deal model
	 * @return the complex deal conditions
	 */
	private List<ComplexDealConditionModel> getComplexDealConditions(final DealModel dealModel)
	{
		final List<ComplexDealConditionModel> dealConditions = new ArrayList<ComplexDealConditionModel>();
		for (final AbstractDealConditionModel eachCondition : dealModel.getConditionGroup().getDealConditions())
		{
			if (eachCondition instanceof ComplexDealConditionModel)
			{
				dealConditions.add((ComplexDealConditionModel) eachCondition);
			}
		}
		return dealConditions;
	}


	/**
	 * Gets the product deal conditions.
	 *
	 * @param dealModel
	 *           the deal model
	 * @return the product deal conditions
	 */
	private List<ProductDealConditionModel> getProductDealConditions(final DealModel dealModel)
	{
		final List<ProductDealConditionModel> dealConditions = new ArrayList<ProductDealConditionModel>();
		for (final AbstractDealConditionModel eachCondition : dealModel.getConditionGroup().getDealConditions())
		{
			if (eachCondition instanceof ProductDealConditionModel)
			{
				dealConditions.add((ProductDealConditionModel) eachCondition);
			}
		}
		return dealConditions;
	}

	/*
	 * (non-Javadoc)
	 *
	 * @see com.sabmiller.core.deals.services.DealsService#getSpecificDeals(de.hybris.platform.b2b.model.B2BUnitModel,
	 * boolean)
	 */
	@Override
	public List<DealModel> getSpecificDeals(final B2BUnitModel b2bUnit, final boolean inStore)
	{
		final List<DealModel> deals = Lists.newArrayList();
		final Set<DealModel> dealsSet = new HashSet<>();
		dealsSet.addAll(dealsDao.getNonComplexDeals(b2bUnit, inStore));
		dealsSet.addAll(b2bUnit.getComplexDeals());

		if (CollectionUtils.isNotEmpty(dealsSet))
		{
			for (final DealModel deal : dealsSet)
			{
				if (null != deal.getInStore() && deal.getInStore().booleanValue() == inStore
						&& dealValidationStrategy.validateDeal(deal, b2bUnit))
				{
					deals.add(deal);
				}
			}
		}
		return deals;
	}

	/**
	 * Filter online deals.
	 *
	 * @param deals
	 *           the deals
	 * @return the list
	 */
	@Override
	public List<DealModel> filterOnlineDeals(final Collection<DealModel> deals)
	{
		final List<DealModel> onlineDeals = new ArrayList<DealModel>();

		for (final DealModel eachDeal : deals)
		{
			if (SabmCoreConstants.DEAL_ONLINE_STATUS.equals(eachDeal.getStatus()))
			{
				onlineDeals.add(eachDeal);
			}
		}
		return onlineDeals;
	}


	/*
	 * (non-Javadoc)
	 *
	 * @see com.sabmiller.core.deals.services.DealsService#isLostDeall(de.hybris.platform.core.model.order.CartModel,
	 * java.lang.String, int)
	 */
	@Override
	public Map<String, List<ItemModel>> getLostDeal(final CartModel cart, final String entryNumber, final int quantity,
			final String uom)
	{
		final List<AbstractOrderEntryModel> entries = cart.getEntries();
		AbstractOrderEntryModel currentEntry = null;
		final List<ItemModel> complexDeal = new ArrayList<>();
		final List<ItemModel> discountDeal = new ArrayList<>();
		final List<ItemModel> limitedDeal = new ArrayList<>();
		final List<ItemModel> deleteDeal = new ArrayList<>();
		for (final AbstractOrderEntryModel abstractOrderEntryModel : entries)
		{
			if (abstractOrderEntryModel.getEntryNumber().equals(Integer.valueOf(entryNumber)))
			{
				currentEntry = abstractOrderEntryModel;
				break;
			}
		}
		if (currentEntry != null)
		{ //check complex deal
			final int realQuantity = productUOMConversionStrategy.convertQuantity(currentEntry.getProduct(), quantity, uom);
			checkLostComplexDeal(cart, realQuantity, currentEntry, complexDeal, deleteDeal);
			//check discount/bogof deal
			checkLostDiscountBOGOGDeal(cart, realQuantity, currentEntry, discountDeal);
			//Check Limited Deal
			checkLimitedDeal(cart, realQuantity, currentEntry, limitedDeal);
		}
		else
		{
			LOG.error("cannot find order entry for the entry number " + entryNumber + " in the cart " + cart.getCode());
		}

		final Map<String, List<ItemModel>> lostDeals = new HashMap<>();
		lostDeals.put("COMPLEX", complexDeal);
		lostDeals.put("DISCOUNT", discountDeal);
		lostDeals.put("DELETED", deleteDeal);
		lostDeals.put("LIMITED", limitedDeal);
		return lostDeals;
	}


	/**
	 * Check limited deal.
	 *
	 * @param cart
	 *           the cart
	 * @param quantity
	 *           the quantity
	 * @param currentEntry
	 *           the current entry
	 * @param limitedDeal
	 *           the limited deal
	 */
	private void checkLimitedDeal(final CartModel cart, final int quantity, final AbstractOrderEntryModel currentEntry,
			final List<ItemModel> limitedDeal)
	{
		final List<EntryOfferInfoModel> offerList = currentEntry.getOfferInfo();
		if (offerList != null)
		{
			for (final EntryOfferInfoModel entryOfferInfoModel : offerList)
			{
				if (SabmCoreConstants.OFFER_TYPE_LIMITED.equals(entryOfferInfoModel.getOfferType()))
				{

					final List<DealModel> dealList = getDeals(cart.getUnit(), new Date(), forNextPeriodDate(new Date()));
					final DealModel deal = findLimitedDealWithOfferInfo(entryOfferInfoModel, dealList, currentEntry);
					limitedDeal.addAll(getLostDeal(cart, quantity, currentEntry, deal));
				}
			}
		}

	}

	/**
	 * For next period date.
	 *
	 * @param date
	 *           the date
	 * @return the date
	 */
	private Date forNextPeriodDate(final Date date)
	{
		final Calendar cal = Calendar.getInstance();
		cal.setTime(date);
		cal.set(Calendar.MINUTE, 0);
		cal.set(Calendar.SECOND, 0);
		cal.set(Calendar.MILLISECOND, 0);
		cal.add(Calendar.DATE, Config.getInt("deal.valid.next.default.day", 14));
		return cal.getTime();
	}



	/**
	 * Check lost discount bogog deal.
	 *
	 * @param cart
	 *           the cart
	 * @param quantity
	 *           the quantity
	 * @param currentEntry
	 *           the current entry
	 * @param discountDeal
	 *           the discount deal
	 */
	private void checkLostDiscountBOGOGDeal(final CartModel cart, final int quantity, final AbstractOrderEntryModel currentEntry,
			final List<ItemModel> discountDeal)
	{
		final List<EntryOfferInfoModel> offerList = currentEntry.getOfferInfo();
		if (offerList != null)
		{
			for (final EntryOfferInfoModel entryOfferInfoModel : offerList)
			{
				if (SabmCoreConstants.OFFER_TYPE_DISCOUNT.equals(entryOfferInfoModel.getOfferType())
						|| SabmCoreConstants.OFFER_TYPE_FREEGOOD.equals(entryOfferInfoModel.getOfferType()))
				{
					discountDeal.addAll(getLostDeal(cart, quantity, currentEntry, entryOfferInfoModel));
				}
			}
		}
	}


	/**
	 * Check lost complex deal.
	 *
	 * @param cart
	 *           the cart
	 * @param quantity
	 *           the quantity
	 * @param currentEntry
	 *           the current entry
	 * @param complexDeal
	 *           the complex deal
	 * @param deleteDeal
	 *           the delete deal
	 */
	private void checkLostComplexDeal(final CartModel cart, final int quantity, final AbstractOrderEntryModel currentEntry,
			final List<ItemModel> complexDeal, final List<ItemModel> deleteDeal)
	{
		final List<CartDealConditionModel> cartDealConditions = cart.getComplexDealConditions();
		LOG.debug("cartDealConditions is null : {}", cartDealConditions == null);
		LOG.debug("Cart deal conditions size : {}", cartDealConditions == null ? "0" : String.valueOf(cartDealConditions.size()));
		for (final CartDealConditionModel cartDealConditionModel : cartDealConditions)
		{
			final DealModel deal = cartDealConditionModel.getDeal();
			if (deal != null && (DealConditionStatus.REJECTED.equals(cartDealConditionModel.getStatus())
					|| !DealTypeEnum.COMPLEX.equals(deal.getDealType())))
			{
				LOG.debug("this deal is not a complex deal : {}", deal.getCode());
				continue;
			}

			//LOG.debug("check complex deal : {}", deal.getCode());
			complexDeal.addAll(getLostDeal(cart, quantity, currentEntry, deal));
			deleteDeal.addAll(getDeleteDeal(cart, quantity, currentEntry, deal));
		}
	}


	/**
	 * Gets the lost deal.
	 *
	 * @param cart
	 *           the cart
	 * @param quantity
	 *           the quantity
	 * @param currentEntry
	 *           the current entry
	 * @param deal
	 *           the deal
	 * @return the lost deal
	 */
	@SuppressWarnings(
	{ "unchecked", "rawtypes" })
	private List<ItemModel> getLostDeal(final CartModel cart, final int quantity, final AbstractOrderEntryModel currentEntry,
			final ItemModel deal)
	{
		final List<ItemModel> lostDealList = new ArrayList<>();
		for (final AbstractLostDealChecker checker : lostDealCheckList)
		{

			if (checker.isThisDealType(deal))
			{
				LOG.debug("the checker is {}", checker.getClass().getSimpleName());
				if (checker.isLostDeal(deal, currentEntry, quantity, cart))
				{
					lostDealList.add(deal);
				}
			}
		}
		return lostDealList;
	}

	/**
	 * Gets the delete deal.
	 *
	 * @param cart
	 *           the cart
	 * @param quantity
	 *           the quantity
	 * @param currentEntry
	 *           the current entry
	 * @param deal
	 *           the deal
	 * @return the delete deal
	 */
	@SuppressWarnings(
	{ "rawtypes", "unchecked" })
	private List<ItemModel> getDeleteDeal(final CartModel cart, final int quantity, final AbstractOrderEntryModel currentEntry,
			final DealModel deal)
	{
		final List<ItemModel> dealList = new ArrayList<>();
		for (final AbstractLostDealChecker checker : lostDealCheckList)
		{

			if (checker.isThisDealType(deal))
			{
				LOG.debug("for deal {} , the checker is {}", deal, checker);
				if (checker.isDeleteDeal(deal, currentEntry, quantity, cart))
				{
					dealList.add(deal);
				}
			}
		}
		if (dealList.size() > 1)
		{
			LOG.warn("there are 2+ checkers for deal {}.", deal);
		}
		return dealList;
	}

	/**
	 * Checks that the following aspects of the deal is valid: 1. Checks that complex deal conditions are valid 2. Checks
	 * that product deal condition is valid. 3. Checks that the free good benefits of the deal are valid.
	 *
	 * @param deal
	 *           the deal
	 * @return true if useable, false otherwise.
	 */
	public boolean isValidDeal(final DealModel deal)
	{
		//useless so throw away...
		if (deal.getConditionGroup() == null)
		{
			return false;
		}

		final List<AbstractDealConditionModel> dealConditions = deal.getConditionGroup().getDealConditions();
		final List<AbstractDealBenefitModel> dealBenefits = deal.getConditionGroup().getDealBenefits();

		//All deals need to come with conditions and benefits, if not why bother???
		if (CollectionUtils.isEmpty(dealConditions) || CollectionUtils.isEmpty(dealBenefits))
		{
			return false;
		}

		final List<ProductModel> excluded = productService.findExcludedProduct(dealConditions);

		for (final AbstractDealConditionModel condition : dealConditions)
		{
			//No need to consider excluded conditions
			if (BooleanUtils.isTrue(condition.getExclude()))
			{
				continue;
			}

			if (condition instanceof ComplexDealConditionModel)
			{
				if (!isValidComplexDealCondition((ComplexDealConditionModel) condition, excluded))
				{
					return false;
				}
			}
			else if (condition instanceof ProductDealConditionModel)
			{
				if (!isValidProductDeal((ProductDealConditionModel) condition))
				{
					return false;
				}
			}
			else
			{
				return false;
			}
		}

		for (final AbstractDealBenefitModel benefit : dealBenefits)
		{
			if (benefit instanceof FreeGoodsDealBenefitModel)
			{
				if (!isValidFreeGoodDealBenefit((FreeGoodsDealBenefitModel) benefit, excluded))
				{
					return false;
				}
			}
		}

		return true;
	}

	/**
	 * Checks for deal condition validity: - Check that the deal's hierarchy assignment has associated product materials
	 * - Check that the product EAN associated with the condition is purchaseable. - Check that the materials under the
	 * deal's hierarchy assignment are not excluded.
	 *
	 * @param condition
	 *           deals conditions
	 * @param excluded
	 *           products within the deal conditions that should be excluded.
	 *
	 * @return true if the deal condition is valid, false otherwise.
	 */
	private boolean isValidComplexDealCondition(final ComplexDealConditionModel condition, final List<ProductModel> excluded)
	{
		final List<? extends ProductModel> materials = productService.getProductByHierarchy(condition.getLine(),
				condition.getBrand(), condition.getVariety(), condition.getEmpties(), condition.getEmptyType(),
				condition.getPresentation());

		if (CollectionUtils.isEmpty(materials))
		{
			LOG.error("There are no available materials associated with deal [{}]'s hierarchy. ", condition.getDealCode());
			return false;
		}

		//Filter out any known excluded products from the material list.
		Collection<ProductModel> filteredMaterial = CollectionUtils.subtract(materials, excluded);

		//Filter out any non-purchasable products...
		filteredMaterial = productService.filterNonPurchaseableProducts(filteredMaterial);

		if (CollectionUtils.isEmpty(filteredMaterial))
		{
			LOG.error("Excluded and non-purchasable products have been filtered, no valid products for deal [{}].",
					condition.getDealCode());
			return false;
		}

		return true;
	}

	/**
	 * Checks that the given benefit meets the following: 1. is associated with an existing product. 2. is not an
	 * excluded product.
	 *
	 * @param benefit
	 *           the benefit to check.
	 * @param excluded
	 *           the excluded
	 * @return true if the benefit is valid, false otherwise.
	 */
	private boolean isValidFreeGoodDealBenefit(final FreeGoodsDealBenefitModel benefit, final List<ProductModel> excluded)
	{
		final ProductModel product = productService.getProductFromCodeWithGivenCatalogVersion(
				catalogVersionDeterminationStrategy.onlineCatalogVersion(), benefit.getProductCode());

		if (product == null)
		{
			LOG.error("Free good for benefit in deal [{}] does not exist.", benefit.getDealCode());
			return false;
		}

		if (excluded.contains(product))
		{
			LOG.error("Free good for benefit in deal [{}] is an excluded product", benefit.getDealCode());
			return false;
		}

		return true;
	}


	@Override
	@Cacheable(value = "dealCache", key = "T(com.sabmiller.cache.impl.SabmCacheKeyGenerator).generateKey(true,true,true)")
	public List<DealJson> searchDeals(final Date deliveryDate, final Boolean judgeValidPeriod)
	{

		final B2BUnitModel unitModel = b2bCommerceUnitService.getParentUnit();

		if (unitModel != null)
		{
			sessionService.setAttribute(SabmCoreConstants.SESSION_SELECT_B2BUNIT_UID_DATA, unitModel.getUid());

		}

		final ArrayList<DealJson> dealsJson = new ArrayList<>();

		final List<DealModel> deals = getDeals(unitModel, new Date(), forNextPeriodDate(new Date()));

		// To determine the valid deals
		final List<DealModel> dealsFiltered = getValidationDeals(deliveryDate, deals, judgeValidPeriod);

		if (CollectionUtils.isNotEmpty(dealsFiltered))
		{
			final List<List<DealModel>> composedDeals = composeComplexFreeProducts(dealsFiltered);

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
		//Start : Loggers for deal issue 0000314859
		LOG.info("Deal Size in deal service impl=>" + dealsJson.size());
		//End : Loggers for deal issue 0000314859
		return dealsJson;
	}
	@Override
	public List<String> getDealsTitles(final List<List<DealModel>> composedDeals){
		final List<String> dealsTitle = new ArrayList<>();
		for (final List<DealModel> dealList : composedDeals)
		{
			try
			{
				final DealJson dealJson = new DealJson();
				dealTitlePopulator.populate(dealList,dealJson);
				String dealTitle = dealJson.getTitle();
				if (dealJson.getTitle() != null && dealJson.getTitle().length() > 200)
				{
					dealTitle = dealTitle.substring(0, 200).trim() + " ...";
				}
				dealsTitle.add(dealTitle);
			}
			catch (final ConversionException e)
			{
				LOG.warn("Unable to convert deal: " + dealList.get(0), e);
			}
		}

		return dealsTitle;
	}



	@Override
	@Cacheable(value = "dealProductSearch", key = "T(com.sabmiller.cache.impl.SabmCacheKeyGenerator).generateKey(false,false,false,#productCode,#dealJson.code)")
	public boolean isProductBelongsToDeal(final String productCode, final DealJson dealJson)
	{
		for (final DealRangeJson range : dealJson.getRanges())
		{
			for (final DealBaseProductJson baseProduct : range.getBaseProducts())
			{
				if (StringUtils.equals(productCode, baseProduct.getProductCode()))
				{
					return true;
				}
			}
		}
		for (final DealFreeProductJson freeProduct : dealJson.getFreeProducts())
		{
			if (StringUtils.equals(productCode, freeProduct.getCode()))
			{
				return true;
			}
		}
		return false;

	}


	/**
	 * Checks that the given product deal is useable. - checks that the product code attached to the deal exists.
	 *
	 * @param condition
	 *           the condition
	 * @return true if the products exist, false otherwise.
	 */
	private boolean isValidProductDeal(final ProductDealConditionModel condition)
	{

		final ProductModel product = productService.getProductFromCodeWithGivenCatalogVersion(
				catalogVersionDeterminationStrategy.onlineCatalogVersion(), condition.getProductCode());

		if (product == null)
		{
			LOG.error("Free good for benefit in deal [{}] does not exist.", condition.getDealCode());
			return false;
		}

		return true;
	}


	/*
	 * (non-Javadoc)
	 *
	 * @see com.sabmiller.core.deals.services.DealsService#findLimitedDealWithOfferInfo(com.sabmiller.core.model.
	 * EntryOfferInfoModel)
	 */
	@Override
	public DealModel findLimitedDealWithOfferInfo(final EntryOfferInfoModel offerInfo, final List<DealModel> dealList,
			final AbstractOrderEntryModel orderEntry)
	{
		return findLimitedDealWithOfferInfo(offerInfo.getOfferType(), dealList, orderEntry.getProduct().getCode());
	}

	/*
	 * (non-Javadoc)
	 *
	 * @see com.sabmiller.core.deals.services.DealsService#findLimitedDealWithOfferInfo(java.lang.String, java.util.List,
	 * java.lang.String)
	 */
	public DealModel findLimitedDealWithOfferInfo(final String offerInfoType, final List<DealModel> dealList,
			final String entryProductCode)
	{
		if (SabmCoreConstants.OFFER_TYPE_LIMITED.equals(offerInfoType))
		{
			for (final DealModel dealModel : dealList)
			{
				if (DealTypeEnum.LIMITED.equals(dealModel.getDealType()))
				{
					final DealConditionGroupModel conditionGroup = dealModel.getConditionGroup();
					final List<AbstractDealConditionModel> conditionList = conditionGroup.getDealConditions();
					final List<ProductModel> productList = new ArrayList<>();
					for (final AbstractDealConditionModel abstractDealConditionModel : conditionList)
					{
						if (abstractDealConditionModel instanceof ComplexDealConditionModel)
						{
							final ComplexDealConditionModel complexCondition = (ComplexDealConditionModel) abstractDealConditionModel;
							productList.addAll(productService.getProductByHierarchy(complexCondition.getLine(),
									complexCondition.getBrand(), complexCondition.getVariety(), complexCondition.getEmpties(),
									complexCondition.getEmptyType(), complexCondition.getPresentation()));
						}
						else
						{
							final ProductDealConditionModel productCondition = (ProductDealConditionModel) abstractDealConditionModel;
							productList.add(productService.getProductForCode(productCondition.getProductCode()));

						}
					}
					if (productList.contains(productService.getProductForCode(entryProductCode)))
					{
						return dealModel;
					}
				}
			}
		}
		return null;
	}

	/*
	 * (non-Javadoc)
	 *
	 * @see com.sabmiller.core.deals.services.DealsService#isDiscountDealExists(java.util.List)
	 */
	public boolean isDiscountDealExists(final List<DealModel> dealsList)
	{

		for (final DealModel deal : dealsList)
		{
			if (deal.getConditionGroup() != null && CollectionUtils.isNotEmpty(deal.getConditionGroup().getDealBenefits()))
			{

				for (final AbstractDealBenefitModel benefit : deal.getConditionGroup().getDealBenefits())
				{
					if (benefit instanceof DiscountDealBenefitModel)
					{
						return true;
					}
				}
			}
		}
		return false;
	}

	/**
	 * Checks if is manual scale proportion.
	 *
	 * @param deals
	 *           the DealModel list
	 * @return true, if is manual scale proportion
	 */
	public boolean isManualScaleProportion(final List<DealModel> deals)
	{
		boolean isProportion = true;
		for (final DealModel dealmodel : deals)
		{
			if (CollectionUtils
					.isNotEmpty(dealmodel.getConditionGroup().getDealBenefits().get(0).getDealConditionGroup().getDealScales()))
			{
				if (!isManualScaleProportionByEachDeal(dealmodel.getConditionGroup().getDealBenefits()))
				{
					isProportion = false;
				}
			}
		}
		return isProportion;
	}

	/**
	 * Checks if is manual scale proportion.
	 *
	 * @param deal
	 *           the DealModel list
	 * @return true, if is manual scale proportion
	 */
	public boolean isManualScaleProportion(final DealModel deal)
	{
		boolean isProportion = true;
		if (CollectionUtils
				.isNotEmpty(deal.getConditionGroup().getDealBenefits().get(0).getDealConditionGroup().getDealScales()))
		{
			if (!isManualScaleProportionByEachDeal(deal.getConditionGroup().getDealBenefits()))
			{
				isProportion = false;
			}
		}
		return isProportion;
	}


	/*
	 * (non-Javadoc)
	 *
	 * @see com.sabmiller.core.deals.services.DealsService#isManualScaleProportionByEachDeal(java.util.List)
	 */
	public boolean isManualScaleProportionByEachDeal(final List<AbstractDealBenefitModel> benefitList)
	{
		final List<DealScaleModel> dealScales = new ArrayList<>(benefitList.get(0).getDealConditionGroup().getDealScales());

		if (CollectionUtils.isEmpty(dealScales) || CollectionUtils.isEmpty(benefitList) || (dealScales.size() == 1))
		{
			return false;
		}

		int proportion = 0;
		int oldScale = 0;
		Collections.sort(dealScales, DealScaleComparator.INSTANCE);
		final List<AbstractDealBenefitModel> sortedBenefit = new ArrayList<>(benefitList);
		Collections.sort(sortedBenefit, DealBenefitScaleComparator.INSTANCE);

		final int firstScale = dealScales.get(0).getFrom();

		for (final AbstractDealBenefitModel benefit : sortedBenefit)
		{
			if (benefit instanceof FreeGoodsDealBenefitModel)
			{
				final FreeGoodsDealBenefitModel benefitModel = (FreeGoodsDealBenefitModel) benefit;
				final Integer scale = getScale(benefitModel.getDealConditionGroup().getDealScales(), benefitModel.getScale());

				if (proportion == 0)
				{
					proportion = scale / benefitModel.getQuantity();
				}
				else if (proportion != scale / benefitModel.getQuantity() || scale - oldScale != firstScale)
				{
					return false;
				}

				oldScale = scale;
			}
			else if (benefit instanceof DiscountDealBenefitModel)
			{
				return false;
			}
		}

		return true;
	}

	/*
	 * (non-Javadoc)
	 *
	 * @see com.sabmiller.core.deals.services.DealsService#getScale(java.util.List, java.lang.String)
	 */
	public Integer getScale(final List<DealScaleModel> dealScales, final String scale)
	{
		for (final DealScaleModel dealScaleModel : dealScales)
		{
			if (dealScaleModel.getScale().equals(scale))
			{
				return dealScaleModel.getFrom();
			}
		}
		if (CollectionUtils.isNotEmpty(dealScales))
		{
			LOG.error("Can not find Scales for dealconditiongroup {}", dealScales.get(0).getDealConditionGroup());
		}

		return 0;
	}

	/*
	 * (non-Javadoc)
	 *
	 * @see
	 * com.sabmiller.core.deals.services.DealsService#getValidatedComplexDeals(de.hybris.platform.b2b.model.B2BUnitModel)
	 */
	@Override
	public List<DealModel> getValidatedNonComplexDeals(final B2BUnitModel b2bUnitModel)
	{
		final List<DealModel> nonComplexDeals = dealsDao.getDeals(b2bUnitModel, new Date(), forNextPeriodDate(new Date()));

		List<DealModel> validationNonComplexDeals = new ArrayList<DealModel>();
		if (nonComplexDeals != null && nonComplexDeals.size() > 0)
		{
			validationNonComplexDeals = getValidationDeals(nonComplexDeals, true);
		}
		return ListUtils.emptyIfNull(validationNonComplexDeals);
	}

	/*
	 * (non-Javadoc)
	 *
	 * @see com.sabmiller.core.deals.services.DealsService#getAbstractDealCondition(java.lang.String)
	 */
	@Override
	public List<AbstractDealConditionModel> getAbstractDealCondition(final Date date, final int batchSize)
	{
		return dealsDao.getAbstractDealCondition(date, batchSize);

	}


	/*
	 * (non-Javadoc)
	 *
	 * @see com.sabmiller.core.deals.services.DealsService#getAbstractDealBenefit(java.lang.String)
	 */
	@Override
	public List<AbstractDealBenefitModel> getAbstractDealBenefit(final Date date, final int batchSize)
	{
		return dealsDao.getAbstractDealBenefit(date, batchSize);
	}


	/*
	 * (non-Javadoc)
	 *
	 * @see com.sabmiller.core.deals.services.DealsService#getDealsScales(java.lang.String)
	 */
	@Override
	public List<DealScaleModel> getDealsScales(final Date date, final int batchSize)
	{
		return dealsDao.getDealsScales(date, batchSize);
	}


	/*
	 * (non-Javadoc)
	 *
	 * @see com.sabmiller.core.deals.services.DealsService#getDealConditionGroup(java.lang.String)
	 */
	@Override
	public List<DealConditionGroupModel> getDealConditionGroup(final Date date, final int batchSize)
	{
		return dealsDao.getDealConditionGroup(date, batchSize);
	}

	@Override
	public List<DealConditionGroupModel> getDealConditionGroupForExpiredDeals(final Date date, final int batchSize)
	{
		return dealsDao.getDealConditionGroupForExpiredDeals(date,batchSize);
	}

	/*
	 * (non-Javadoc)
	 *
	 * @see com.sabmiller.core.deals.services.DealsService#getDealAssignee(java.lang.String)
	 */
	@Override
	public List<DealAssigneeModel> getDealAssignee(final Date date, final int batchSize)
	{
		return dealsDao.getDealAssignee(date, batchSize);
	}

	@Override
	public List<DealAssigneeModel> getDealAssigneeForExpiredDeals(final Date date, final int batch) {
		return dealsDao.getDealAssigneeForExpiredDeals(date,batch);
	}

	/*
	 * (non-Javadoc)
	 *
	 * @see com.sabmiller.core.deals.services.DealsService#getCartDealCondition(java.lang.String)
	 */
	@Override
	public List<CartDealConditionModel> getCartDealCondition(final Date date, final int batchSize)
	{
		return dealsDao.getCartDealCondition(date, batchSize);

	}

	@Override
	public List<CartDealConditionModel> getCartDealConditionForExpiredDeals(final Date date, final int batch) {
		return dealsDao.getCartDealConditionForExpiredDeals(date,batch);
	}

	/*
	 * (non-Javadoc)
	 *
	 * @see com.sabmiller.core.deals.services.DealsService#getDealsbeforethirtydays(java.lang.String)
	 */
	@Override
	public List<DealModel> getDealsbeforethirtydays(final Date date, final int batchSize)
	{
		return dealsDao.getDealsbeforethirtydays(date, batchSize);

	}

	@Override
	@CacheEvict(value = "dealCache", key = "T(com.sabmiller.cache.impl.SabmCacheKeyGenerator).generateKey(true,true,true)")
	public void refreshDealCache()
	{
		// YTODO Auto-generated method stub
		LOG.info("refreshDealCache");

	}

	/**
	 * Helper method to create a B2bUnitByDealsAssigneeProvider with simple db search
	 * @param sabmB2BUnitService
	 * @return
	 */
	public static B2bUnitByDealAssigneeProvider createB2bUnitByDealAssigneeProvider(final SabmB2BUnitService sabmB2BUnitService, final int cacheSize, final boolean enabledThreadSafety){

		Objects.requireNonNull(sabmB2BUnitService,"sabmB2BUnitService is required.");

		final SABMLRUCache<SearchB2BUnitQueryParam,List<B2BUnitModel>> b2bUnitDealAssigneeKeyMap = new SABMLRUCache<>(cacheSize);

		if(!enabledThreadSafety) {
            return d->b2bUnitDealAssigneeKeyMap.getValueOrLoad(createQueryParam(d), sabmB2BUnitService::searchB2BUnit);
        }

        final Object lock = new Object();

		return  d->{
		    synchronized (lock) {
		        return b2bUnitDealAssigneeKeyMap.getValueOrLoad(createQueryParam(d), sabmB2BUnitService::searchB2BUnit);
		    }
		};
	}

	@FunctionalInterface
	interface B2bUnitByDealAssigneeProvider{
		List<B2BUnitModel> getB2bUnits(final DealAssigneeModel dealAssignee);
	}

	@Override
	public List<AsahiDealModel> getSGASpecificDeals(final AsahiB2BUnitModel b2bUnit)
	{

		final List<AsahiDealModel> deals = Lists.newArrayList();
		final Set<AsahiDealModel> dealsSet = new HashSet<>();
		dealsSet.addAll(dealsDao.getSGASpecificDeals(b2bUnit.getCatalogHierarchy()));
		if (CollectionUtils.isNotEmpty(dealsSet))
		{
			for (final AsahiDealModel deal : dealsSet)
			{
				if (asahiDealValidationStrategy.validateDeal(deal))
				{
					deals.add(deal);
				}
			}
		}
		return deals;

	}


	@Override
	public void saveAsahiRepDealChange(final AsahiB2BUnitModel b2bUnitModel, final List<String> dealsToActivate, final List<String> dealsToRemove,
			final List<String> customerEmails, final String dealsDetails)
	{
		List<AsahiDealModel> finalActiveDeals = new ArrayList<AsahiDealModel>();

		final List<AsahiDealModel> existingActiveDeals = (List<AsahiDealModel>) b2bUnitModel.getAsahiDeals();
		if (org.apache.commons.collections4.CollectionUtils.isNotEmpty(existingActiveDeals)
				&& org.apache.commons.collections4.CollectionUtils.isNotEmpty(dealsToRemove))
		{
			finalActiveDeals = existingActiveDeals.stream()
					.filter(deal -> !(dealsToRemove.contains(deal.getCode()))).collect(Collectors.toList());
		}
		else if(org.apache.commons.collections4.CollectionUtils.isNotEmpty(existingActiveDeals))
		{
			finalActiveDeals.addAll(existingActiveDeals);
		}
		if (org.apache.commons.collections4.CollectionUtils.isNotEmpty(dealsToActivate))
		{
			final List<AsahiDealModel> dealstoActivateForCustomer = dealsDao.getSgaDealsForCode(dealsToActivate);
			finalActiveDeals.addAll(dealstoActivateForCustomer);
		}
		b2bUnitModel.setAsahiDeals(finalActiveDeals);
		modelService.save(b2bUnitModel);
		modelService.refresh(b2bUnitModel);
		if(org.apache.commons.collections4.CollectionUtils.isNotEmpty(customerEmails)) {
			final List<String> finalCustomerEmails = new ArrayList<String>();
			finalCustomerEmails.addAll(customerEmails);
			final String userId = userService.getCurrentUser().getUid();
			finalCustomerEmails.add(userId);
			sendAsahiDealsChangeEmail(dealsToActivate,dealsToRemove,finalCustomerEmails,dealsDetails);
		}
	}

	/**
	 * @param dealsToActivate
	 * @param dealsToRemove
	 * @param customerEmails
	 * @param dealsDetails
	 */
	private void sendAsahiDealsChangeEmail(final List<String> dealsToActivate, final List<String> dealsToRemove, final List<String> customerEmails,
			final String dealsDetails)
	{
		eventService.publishEvent(initializeEvent(new AsahiDealsChangeEvent(), dealsToActivate,dealsToRemove,customerEmails,dealsDetails));
	}

	/**
	 * @param asahiDealsChangeEvent
	 * @param asahiDealsChangeEvent
	 * @param orderModel
	 * @return
	 */
	private AbstractEvent initializeEvent(final AsahiDealsChangeEvent asahiDealsChangeEvent, final List<String> dealsToActivate, final List<String> dealsToRemove, final List<String> customerEmails,
			final String dealsDetails)
	{
		asahiDealsChangeEvent.setBaseStore(baseStoreService.getBaseStoreForUid("sga"));
		asahiDealsChangeEvent.setSite(baseSiteService.getBaseSiteForUID("sga"));
		asahiDealsChangeEvent.setLanguage(commonI18NService.getLanguage("en"));
		asahiDealsChangeEvent.setCurrency(commonI18NService.getCurrency("AUD"));
		asahiDealsChangeEvent.setActivatedDeals(dealsToActivate);
		asahiDealsChangeEvent.setAdditionalDealDetails(dealsDetails);
		asahiDealsChangeEvent.setRemovedDeals(dealsToRemove);
		asahiDealsChangeEvent.setCustomerEmailIds(customerEmails);
		return asahiDealsChangeEvent;
	}

	/***
	 * dealsCode
	 */
	@Override
	public List<AsahiDealModel> getSGADealsForCode(final List<String> dealsCode){
		return dealsDao.getSgaDealsForCode(dealsCode);
	}

	/***
	 *
	 * @param b2bUnit
	 * @return
	 */
	@Override
	public List<String> getCustomerEmails(final AsahiB2BUnitModel b2bUnit)
	{
		final Set<String> customerEmailIds = new HashSet<String>();
		if (CollectionUtils.isNotEmpty(b2bUnit.getMembers()))
		{
			customerEmailIds.addAll(b2bUnit.getMembers().stream()
					.filter(member -> member instanceof B2BCustomerModel && !(member instanceof BDECustomerModel)
							&& (BooleanUtils.isTrue(((B2BCustomerModel) member).getActive()))
							&& (CollectionUtils.isEmpty(b2bUnit.getDisabledUser())
									|| !b2bUnit.getDisabledUser().contains(member.getUid())))
					.collect(Collectors.toList()).stream().map(member -> member.getUid()).collect(Collectors.toSet()));
		}
		return new ArrayList<String>(customerEmailIds);
	}


	@Override
	public List<AsahiDealModel> getSGADealsForProductAndUnit(final String code, final AsahiB2BUnitModel b2bUnitModel)
	{
		final Set<AsahiDealModel> activeDeals = b2bUnitModel.getAsahiDeals().stream()
				.filter(deal -> asahiDealValidationStrategy.validateDealForCustomer(deal)).collect(Collectors.toSet());
		List<AsahiDealModel> validDeals = new ArrayList<AsahiDealModel>();
		if(org.apache.commons.collections4.CollectionUtils.isNotEmpty(activeDeals)) {
			validDeals = activeDeals.stream()
					.filter(deal -> ((AsahiProductDealConditionModel) deal.getDealCondition()).getProductCode().equals(code)
					&& isValidProductForAsahiDeals(((AsahiFreeGoodsDealBenefitModel) deal.getDealBenefit()).getProductCode()))
					.collect(Collectors.toList());
		}
		return validDeals;
	}


	@Override
	public List<AsahiDealModel> getCustomerSpecificDeals(final AsahiB2BUnitModel b2bUnit)
	{
		final Set<AsahiDealModel> activeDeals = b2bUnit.getAsahiDeals().stream()
				.filter(deal -> asahiDealValidationStrategy.validateDealForCustomer(deal)).collect(Collectors.toSet());
		List<AsahiDealModel> validDeals = new ArrayList<AsahiDealModel>();
		if (org.apache.commons.collections4.CollectionUtils.isNotEmpty(activeDeals))
		{
			validDeals = activeDeals.stream()
					.filter(deal -> isValidProductForAsahiDeals(
							((AsahiProductDealConditionModel) deal.getDealCondition()).getProductCode())
							&& isValidProductForAsahiDeals(((AsahiFreeGoodsDealBenefitModel) deal.getDealBenefit()).getProductCode()))
					.collect(Collectors.toList());
		}

		return validDeals;
	}

	protected boolean isValidProductForAsahiDeals(final String productCode)
	{
		final Optional<ProductModel> optProduct = getOptionalProductForCode(productCode);
		final ProductModel product = optProduct.orElse(null);
		if (!(product instanceof ApbProductModel))
		{
			return false;
		}
		final ApbProductModel apbProductModel = (ApbProductModel) product;

		if (this.inclusionExclusionProductStrategy.isProductIncluded(apbProductModel.getCode()) && apbProductModel.isActive()
				&& apbProductModel.getApprovalStatus().equals(ArticleApprovalStatus.APPROVED)
				&& !isOutOfStock(apbProductModel))
		{
			return true;
		}
		return false;
	}

	 /**
	  * @param apbProductModel
	  * @return
	  */
	 private boolean isOutOfStock(final ApbProductModel apbProductModel)
	 {
		 final StockLevelStatus status = defaultSabmCommerceStockService.getStockLevelForSGA(apbProductModel);
		 return StockLevelStatus.OUTOFSTOCK.equals(status);
	}

	 /**
	  * Just a helper method to get product code, optional, so this won't throw an Exception but will return empty
	  *
	  * @param code
	  * @return
	  */
	 protected Optional<ProductModel> getOptionalProductForCode(@Nonnull
	 final String code)
	 {
		 try
		 {
			 return Optional.of(productService.getProductForCodeSafe(code));
		 }
		 catch (final Exception e)
		 {
			 LOG.warn(String.format("Unable to retrieve product for code [%s]", code), e);
		 }

		 return Optional.empty();
	 }


}
