/**
 *
 */
package com.sabmiller.facades.populators;

import de.hybris.platform.core.Registry;
import de.hybris.platform.core.model.c2l.CurrencyModel;
import de.hybris.platform.core.model.product.ProductModel;
import de.hybris.platform.core.model.product.UnitModel;
import de.hybris.platform.servicelayer.dto.converter.ConversionException;
import de.hybris.platform.servicelayer.i18n.CommonI18NService;
import de.hybris.platform.servicelayer.i18n.I18NService;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.HashSet;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.Set;

import jakarta.annotation.Resource;

import org.apache.commons.collections4.CollectionUtils;
import org.apache.commons.collections4.MapUtils;
import org.apache.commons.lang3.BooleanUtils;
import org.apache.commons.lang3.StringUtils;
import org.apache.commons.lang3.math.NumberUtils;
import org.assertj.core.util.Arrays;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.MessageSource;

import com.google.common.collect.Maps;
import com.sabmiller.core.comparators.DealScaleComparator;
import com.sabmiller.core.deals.services.DealsService;
import com.sabmiller.core.deals.strategies.SABMDiscountPerUnitCalculationStrategy;
import com.sabmiller.core.enums.DealTypeEnum;
import com.sabmiller.core.model.AbstractDealBenefitModel;
import com.sabmiller.core.model.AbstractDealConditionModel;
import com.sabmiller.core.model.ComplexDealConditionModel;
import com.sabmiller.core.model.DealConditionGroupModel;
import com.sabmiller.core.model.DealModel;
import com.sabmiller.core.model.DealScaleModel;
import com.sabmiller.core.model.DiscountDealBenefitModel;
import com.sabmiller.core.model.FreeGoodsDealBenefitModel;
import com.sabmiller.core.model.ProductDealConditionModel;
import com.sabmiller.core.model.SABMAlcoholProductModel;
import com.sabmiller.core.model.SABMAlcoholVariantProductEANModel;
import com.sabmiller.core.model.SABMAlcoholVariantProductMaterialModel;
import com.sabmiller.core.product.SabmProductService;
import com.sabmiller.facades.deal.data.DealJson;


/**
 * SABMAbstractDealPopulator.
 */
public abstract class SABMAbstractDealPopulator
{
	/** The Constant LOG. */
	private static final Logger LOG = LoggerFactory.getLogger(SABMAbstractDealPopulator.class);

	/** The Constant MAP_KEY_RANGE. */
	protected static final String MAP_KEY_RANGE = "range";

	/** The Constant MAP_KEY_AMOUNT. */
	protected static final String MAP_KEY_AMOUNT = "amount";

	/** The Constant MAP_KEY_UOM. */
	protected static final String MAP_KEY_UOM = "uom";

	/** The Constant MAP_KEY_UOMS. */
	protected static final String MAP_KEY_UOMS = "uoms";

	/** The Constant MAP_KEY_UOM. */
	protected static final String MAP_KEY_UOM_SINGULAR = "uomSingular";

	/** The Constant MAP_KEY_UOM. */
	protected static final String MAP_KEY_UOM_PLURAL = "uomPlural";

	/** The Constant MAP_KEY_QTY. */
	protected static final String MAP_KEY_QTY = "qty";

	/** The Constant MAP_KEY_PARTIAL_QTY. */
	protected static final String MAP_KEY_PARTIAL_QTY = "partialQty";

	/** The Constant MAP_KEY_SCALE_QTY. */
	protected static final String MAP_KEY_SCALE_QTY = "scaleQty";

	protected static final String MAP_KEY_SCALE_DEAL = "scaleDealQty";

	/** The Constant MAP_KEY_RATIO. */
	protected static final String MAP_KEY_RATIO = "ratio";

	/** The Constant MAP_KEY_PROD. */
	protected static final String MAP_KEY_PROD = "product";

	/** The Constant MAP_KEY_MANDATORY. */
	protected static final String MAP_KEY_MANDATORY = "mandatory";

	/** The Constant MAP_KEY_PROD_CODE. */
	protected static final String MAP_KEY_PROD_CODE = "productCode";

	/** The Constant MAP_KEY_SAME_PROD. */
	protected static final String MAP_KEY_ONLY_PROD = "onlyProduct";

	/** The Constant MAP_VALUE_TRUE. */
	protected static final String MAP_VALUE_TRUE = "true";

	/** The Constant HTML_SPACE. */
	protected static final String HTML_SPACE = "&ensp;";

	/** The Constant NORMAL_SPACE. */
	protected static final String NORMAL_SPACE = " ";

	/** The Constant MAP_IS_RANGE_DEAL. */
	protected static final String MAP_IS_RANGE_DEAL = "isRangeDeal";

	/** The Constant MAP_DEAL_SCALE_QTY. */
	protected static final String MAP_DEAL_SCALE_QTY = "dealScaleQty";

	/** The Constant MAP_DEAL_SCALE_NUM. */
	protected static final String MAP_DEAL_SCALE_NUM = "dealScaleNum";

	/** The deals service. */
	@Resource
	private DealsService dealsService;

	/** The i18n service. */
	@Resource
	private I18NService i18nService;

	/** The common i18 n service. */
	@Resource(name = "commonI18NService")
	private CommonI18NService commonI18NService;

	/** The discount per unit calculation strategy. */
	@Resource(name = "discountPerUnitCalculationStrategy")
	private SABMDiscountPerUnitCalculationStrategy discountPerUnitCalculationStrategy;

	/** The message source name. */
	@Value(value = "${message.source.name:messageSource}")
	private String messageSourceName;

	/** The product service. */
	@Resource(name = "productService")
	private SabmProductService productService;

	/** The message source. */
	private MessageSource messageSource;


	/**
	 * generate suffix of deal title like (Maximum of <number of preconditions> deals per order).
	 *
	 * @param dealModel
	 *           the deal model
	 * @param locale
	 *           the locale
	 * @param mapValues
	 *           the map values
	 * @return the string
	 */
	public String populateTitleSuffix(final List<DealModel> deals, final Locale locale, final Map<String, String> mapValues)
	{
		final List<AbstractDealBenefitModel> benefitList = deals.get(0).getConditionGroup().getDealBenefits();

		if (DealTypeEnum.LIMITED.equals(deals.get(0).getDealType()))
		{
			return populateLimitedSuffix(deals.get(0), locale, mapValues, true);
		}
		else if (DealTypeEnum.COMPLEX.equals(deals.get(0).getDealType()) && CollectionUtils.isNotEmpty(benefitList))
		{
			//if the deal is proportional, need to generate title suffix
			//else return empty.
			if (BooleanUtils.isNotTrue(benefitList.get(0).getProportionalAmount())
					&& BooleanUtils.isNotTrue(benefitList.get(0).getProportionalFreeGood()))
			{
				return generateTitleSuffix(deals, locale);
			}
		}

		return StringUtils.EMPTY;
	}

	/**
	 * Populate limited suffix.
	 *
	 * @param dealModel
	 *           the deal model
	 * @param locale
	 *           the locale
	 * @param mapValues
	 *           the map values
	 * @param useBold
	 *           the use bold
	 * @return the string
	 */
	protected String populateLimitedSuffix(final DealModel dealModel, final Locale locale, final Map<String, String> mapValues,
			final boolean useBold)
	{
		if (DealTypeEnum.LIMITED.equals(dealModel.getDealType()))
		{
			final BigDecimal roundAmount = discountPerUnitCalculationStrategy
					.roundAmount(BigDecimal.valueOf(getMaxBaseQuantity(dealModel)));

			if (isLimitedQuantityCurrency(dealModel))
			{

				final CurrencyModel currency = commonI18NService.getCurrentCurrency();
				return getMessageSource().getMessage("text.deal.title.suffix.limited.discount" + (useBold ? ".bold" : ""),
						Arrays.array(currency.getSymbol() + roundAmount.toString()), locale);
			}

			final String uom = roundAmount.doubleValue() > 1 ? mapValues.get(MAP_KEY_UOMS) : mapValues.get(MAP_KEY_UOM);
			return getMessageSource().getMessage("text.deal.title.suffix.limited" + (useBold ? ".bold" : ""),
					Arrays.array(roundAmount.toString(), uom), locale);
		}

		return StringUtils.EMPTY;
	}

	/**
	 * Generate title suffix.
	 *
	 * @param benefitList
	 *           the benefit list
	 * @param locale
	 *           the locale
	 * @return the string
	 */
	private String generateTitleSuffix(final List<DealModel> deals, final Locale locale)
	{
		if (deals.get(0).getConditionGroup().getDealBenefits().size() < 2)
		{
			return getMessageSource().getMessage("text.deal.title.suffix", Arrays.array("1 deal"), locale);
		}
		else if (isAllOfFreeGoodsBenefit(deals.get(0).getConditionGroup().getDealBenefits()))
		{
			//for free goods benefit
			return placeTitleSuffixForFreeGoods(deals, locale);
		}
		else if (isAllOfDiscountBenefit(deals.get(0).getConditionGroup().getDealBenefits()))
		{
			//for discount benefit
			return placeTitleSuffixForDiscount(deals.get(0).getConditionGroup().getDealBenefits(), locale);
		}
		else
		{
			return getMessageSource().getMessage("text.deal.title.suffix", Arrays.array("1 deal"), locale);
		}
	}

	/**
	 * Place title suffix for discount.
	 *
	 * @param benefitList
	 *           the benefit list
	 * @param locale
	 *           the locale
	 * @return the string
	 */
	private String placeTitleSuffixForDiscount(final List<AbstractDealBenefitModel> benefitList, final Locale locale)
	{
		String suffix = StringUtils.EMPTY;
		BigDecimal proportion = BigDecimal.ZERO;
		for (final AbstractDealBenefitModel abstractDealBenefitModel : benefitList)
		{
			final DiscountDealBenefitModel benefitModel = (DiscountDealBenefitModel) abstractDealBenefitModel;
			final Integer scale = getDealsService().getScale(benefitModel.getDealConditionGroup().getDealScales(),
					benefitModel.getScale());
			if (proportion.compareTo(BigDecimal.ZERO) <= 0)
			{
				proportion = BigDecimal.valueOf(scale).divide(BigDecimal.valueOf(benefitModel.getAmount()), 2,
						BigDecimal.ROUND_HALF_UP);
			}
			else
			{
				final BigDecimal tempProportion = BigDecimal.valueOf(scale).divide(BigDecimal.valueOf(benefitModel.getAmount()), 2,
						BigDecimal.ROUND_HALF_UP);
				if (proportion.compareTo(tempProportion) != 0)
				{
					suffix = getMessageSource().getMessage("text.deal.title.suffix", Arrays.array("1 deal"), locale);
					break;
				}
			}
		}
		if (suffix.equals(StringUtils.EMPTY))
		{
			final int dealApplyTimes = benefitList.size();
			suffix = getMessageSource().getMessage("text.deal.title.suffix", Arrays.array(dealApplyTimes + " deals"), locale);
		}
		return suffix;
	}

	/**
	 * Place title suffix for free goods.
	 *
	 * @param benefitList
	 *           the benefit list
	 * @param locale
	 *           the locale
	 * @return the string
	 */
	private String placeTitleSuffixForFreeGoods(final List<DealModel> deals, final Locale locale)
	{
		if (CollectionUtils.isEmpty(deals.get(0).getConditionGroup().getDealBenefits()))
		{
			LOG.warn("Unable to generate suffix for empty benefits");
			return StringUtils.EMPTY;
		}

		String suffix = null;

		if (getDealsService().isManualScaleProportion(deals))
		{
			final int dealApplyTimes = deals.get(0).getConditionGroup().getDealBenefits().get(0).getDealConditionGroup()
					.getDealScales().size();
			suffix = getMessageSource().getMessage("text.deal.title.suffix", Arrays.array(dealApplyTimes + " deals"), locale);
		}
		else
		{
			suffix = getMessageSource().getMessage("text.deal.title.suffix", Arrays.array("1 deal"), locale);
		}

		return suffix;
	}

	/**
	 * Checks if is all of free goods benefit.
	 *
	 * @param benefitList
	 *           the benefit list
	 * @return true, if is all of free goods benefit
	 */
	protected boolean isAllOfFreeGoodsBenefit(final List<AbstractDealBenefitModel> benefitList)
	{
		for (final AbstractDealBenefitModel abstractDealBenefitModel : benefitList)
		{
			if (abstractDealBenefitModel instanceof DiscountDealBenefitModel)
			{
				return false;
			}
		}
		return true;
	}

	/**
	 * Checks if is all of discount benefit.
	 *
	 * @param benefitList
	 *           the benefit list
	 * @return true, if is all of discount benefit
	 */
	private boolean isAllOfDiscountBenefit(final List<AbstractDealBenefitModel> benefitList)
	{
		for (final AbstractDealBenefitModel abstractDealBenefitModel : benefitList)
		{
			if (abstractDealBenefitModel instanceof FreeGoodsDealBenefitModel)
			{
				return false;
			}
		}
		return true;
	}

	/**
	 * Populate deal condition title.
	 *
	 * @param deal
	 *           the deal
	 * @param locale
	 *           the locale
	 * @param mapValues
	 *           the map values
	 * @return the string
	 */
	protected String populateDealConditionTitle(final List<DealModel> deals, final Locale locale,
			final Map<String, String> mapValues)
	{
		final boolean rangeDeal = isMultiRange(deals.get(0).getConditionGroup().getDealConditions());
		final boolean multiScale = isMultiScale(deals.get(0));
		LOG.debug("Populate Deal Condition Title for Deal Code：[{}] =======start=======", deals.get(0).getCode());
		// set the rangeDeal flag to map
		mapValues.put(MAP_IS_RANGE_DEAL, BooleanUtils.toStringTrueFalse(rangeDeal));

		if (multiScale)
		{
			return populateMultiScaleDealConditionTitle(deals, locale, mapValues);
		}
		else if (rangeDeal)
		{
			return populateRangeDealConditionTitle(deals.get(0), locale, mapValues);
		}
		else
		{
			return populateSimpleDealConditionTitle(deals.get(0), locale, mapValues);
		}
	}

	/**
	 * Populate multi scale deal condition title.
	 *
	 * @param deals
	 *           the deal
	 * @param locale
	 *           the locale
	 * @param mapValues
	 *           the map values
	 * @return the string
	 */
	protected String populateMultiScaleDealConditionTitle(final List<DealModel> deals, final Locale locale,
			final Map<String, String> mapValues)
	{
		final List<DealScaleModel> dealScales = new ArrayList<>(deals.get(0).getConditionGroup().getDealScales());

		if (CollectionUtils.isEmpty(dealScales))
		{
			throw new ConversionException("Impossible to generate scale deal title for deal:" + deals.get(0));
		}

		Collections.sort(dealScales, DealScaleComparator.INSTANCE);

		final StringBuilder builder = new StringBuilder("");
		//set the dealScales.from QTY to map
		mapValues.put(MAP_DEAL_SCALE_QTY, dealScales.get(0).getFrom().toString());
		mapValues.put(MAP_DEAL_SCALE_NUM, dealScales.get(0).getScale().toString());
		builder.append(getMultiScaleDealConditionTitle(deals.get(0), locale, mapValues));
		builder.append(populateDealBenefitTitle(deals, locale, mapValues, true));

		if (!getDealsService().isManualScaleProportion(deals))
		{
			for (int i = 1; i < dealScales.size(); i++)
			{
				builder.append(getMessageSource().getMessage("text.deal.title.or", null, locale));
				mapValues.put(MAP_DEAL_SCALE_QTY, dealScales.get(i).getFrom().toString());
				mapValues.put(MAP_DEAL_SCALE_NUM, dealScales.get(i).getScale().toString());
				builder.append(getMultiScaleDealConditionTitle(deals.get(0), locale, mapValues));
				builder.append(populateDealBenefitTitle(deals, locale, mapValues, true));
			}
		}
		return builder.toString();
	}

	/**
	 * get the MultiScale deal condition title , when the deal is also range deal then display rangeName.
	 *
	 * @param deal
	 *           the deal
	 * @param locale
	 *           the locale
	 * @param mapValues
	 *           the map values
	 * @return String
	 */
	protected String getMultiScaleDealConditionTitle(final DealModel deal, final Locale locale,
			final Map<String, String> mapValues)
	{
		//if the deal not a range deal then invoke old function
		if (BooleanUtils.toBoolean(mapValues.get(MAP_IS_RANGE_DEAL)))
		{
			return populateRangeDealConditionTitle(deal, locale, mapValues);
		}
		return populateSimpleDealConditionTitle(deal, locale, mapValues);
	}

	/**
	 * Populate range deal condition title.
	 *
	 * @param deal
	 *           the deal
	 * @param locale
	 *           the locale
	 * @param mapValues
	 *           the map values
	 * @return the string
	 */
	protected String populateRangeDealConditionTitle(final DealModel deal, final Locale locale,
			final Map<String, String> mapValues)
	{
		final StringBuilder builder = new StringBuilder(getMessageSource().getMessage("text.deal.title.buy", null, locale));
		final List<Map<String, String>> argList = new ArrayList<>();
		final List<String> excepts = new ArrayList<>();

		populateConditionMap(deal, excepts, null, mapValues, argList);

		int count = 0;

		for (final Map<String, String> map : argList)
		{
			addCommaConjunction(count, argList, locale, builder);
			final int acrossQty = getAcrossQty(deal.getConditionGroup());
			if (isAcross(deal.getConditionGroup()))
			{
				if (count == 0)
				{
					final String realQty = mapValues.get(MAP_DEAL_SCALE_QTY) != null ? mapValues.get(MAP_DEAL_SCALE_QTY)
							: Integer.toString(acrossQty);
					builder.append(getMessageSource().getMessage("text.deal.title.across.range.uom.product",
							Arrays.array(realQty,
									Integer.valueOf(realQty) > 1 ? map.get(MAP_KEY_UOM_PLURAL) : map.get(MAP_KEY_UOM_SINGULAR),
									map.get(MAP_KEY_RANGE)),
							locale));
				}
				else
				{
					builder.append(getMessageSource().getMessage("text.deal.title.single.product.bold",
							Arrays.array(map.get(MAP_KEY_RANGE)), locale));
				}

				if (count == argList.size() - 1)
				{
					builder.append(getMessageSource().getMessage("text.deal.title.ranges", null, locale));
				}
			}
			else
			{
				final String realQty = mapValues.get(MAP_DEAL_SCALE_QTY) != null ? mapValues.get(MAP_DEAL_SCALE_QTY)
						: acrossQty > 0 ? Integer.toString(acrossQty) : map.get(MAP_KEY_QTY);
				builder.append(getMessageSource().getMessage("text.deal.title.range.uom.product",
						Arrays.array(realQty,
								Integer.valueOf(realQty) > 1 ? map.get(MAP_KEY_UOM_PLURAL) : map.get(MAP_KEY_UOM_SINGULAR),
								map.get(MAP_KEY_RANGE)),
						locale));
			}

			count++;
		}

		count = 0;

		for (final String except : excepts)
		{
			if (count == 0)
			{
				builder.append(getMessageSource().getMessage("text.deal.title.except", null, locale));
			}
			addCommaConjunction(count, excepts, locale, builder);

			builder.append(getMessageSource().getMessage("text.deal.title.single.product.bold", Arrays.array(except), locale));

			count++;
		}

		return builder.toString();
	}

	/**
	 * Populate condition map.
	 *
	 * @param deal
	 *           the deal
	 * @param excepts
	 *           the excepts
	 * @param map
	 *           the map
	 * @param mapValues
	 *           the map values
	 * @param argList
	 *           the arg list
	 */
	protected void populateConditionMap(final DealModel deal, final List<String> excepts, final Map<String, String> map,
			final Map<String, String> mapValues, final List<Map<String, String>> argList)
	{
		populateExceptsList(deal, excepts);

		for (final AbstractDealConditionModel condition : deal.getConditionGroup().getDealConditions())
		{
			if (BooleanUtils.isNotTrue(condition.getExclude()))
			{
				if (condition instanceof ComplexDealConditionModel)
				{
					if (argList != null)
					{
						final Map<String, String> mapArg = Maps.newHashMap();
						populateComplexConditionMap((ComplexDealConditionModel) condition, mapArg, mapValues);
						argList.add(mapArg);
					}
					else
					{
						populateComplexConditionMap((ComplexDealConditionModel) condition, map, mapValues);
					}
				}
				else if (condition instanceof ProductDealConditionModel)
				{
					if (argList != null)
					{
						final Map<String, String> mapArg = Maps.newHashMap();
						populateProductConditionMap((ProductDealConditionModel) condition, mapArg, mapValues);
						argList.add(mapArg);
					}
					else
					{
						populateProductConditionMap((ProductDealConditionModel) condition, map, mapValues);
					}
				}
			}
		}
	}

	/**
	 * Populate excepts list.
	 *
	 * @param deal
	 *           the deal
	 * @param excepts
	 *           the excepts
	 */
	protected void populateExceptsList(final DealModel deal, final List<String> excepts)
	{
		if (deal == null || excepts == null)
		{
			return;
		}

		final List<ProductModel> findExcluded = getProductService()
				.findExcludedProduct(deal.getConditionGroup().getDealConditions());

		for (final ProductModel product : CollectionUtils.emptyIfNull(findExcluded))
		{
			final String productName = getProductName(product);
			if (excepts != null && !excepts.contains(productName))
			{
				excepts.add(productName);
			}
		}
	}

	/**
	 * Populate simple deal condition title.
	 *
	 * @param deal
	 *           the deal
	 * @param locale
	 *           the locale
	 * @param mapValues
	 *           the map values
	 * @return the string
	 */
	protected String populateSimpleDealConditionTitle(final DealModel deal, final Locale locale,
			final Map<String, String> mapValues)
	{
		final StringBuilder builder = new StringBuilder(getMessageSource().getMessage("text.deal.title.buy", null, locale));

		final List<Map<String, String>> argList = new ArrayList<>();
		populateConditionMap(deal, null, null, mapValues, argList);

		int count = 0;

		//To determine if there is only one
		if (argList.size() == 1)
		{
			mapValues.put(MAP_KEY_ONLY_PROD, MAP_VALUE_TRUE);
		}
		final List<Map<String, String>> minimumQtyCondition = new ArrayList<>();
		for (final Map<String, String> map : argList)
		{
			addCommaConjunction(count, argList, locale, builder);

			if (isAcross(deal.getConditionGroup()))
			{
				final boolean includingMinimum = NumberUtils.isNumber(map.get(MAP_KEY_QTY))
						&& Integer.valueOf(map.get(MAP_KEY_QTY)) > 0 && BooleanUtils.toBoolean(map.get(MAP_KEY_MANDATORY));
				if (count == 0)
				{
					final String realQty = mapValues.get(MAP_DEAL_SCALE_QTY) != null ? mapValues.get(MAP_DEAL_SCALE_QTY)
							: Integer.toString(getAcrossQty(deal.getConditionGroup()));
					final String uom = Integer.valueOf(realQty) > 1 ? map.get(MAP_KEY_UOM_PLURAL) : map.get(MAP_KEY_UOM_SINGULAR);
					if (includingMinimum)
					{
						minimumQtyCondition.add(map);
						//						builder.append(getMessageSource().getMessage("text.deal.title.including",
						//								Arrays.array(realQty, uom, map.get(MAP_KEY_RANGE)), locale));
						//
						//						builder.append(getMessageSource().getMessage("text.deal.title.uom.product",
						//								Arrays.array(map.get(MAP_KEY_QTY), map.get(MAP_KEY_UOM), map.get(MAP_KEY_PROD)), locale));
					}
					//else
					//{
					builder.append(getMessageSource().getMessage("text.deal.title.across.uom.product",
							Arrays.array(realQty, uom, map.get(MAP_KEY_PROD)), locale));
					//}
				}
				else
				{
					if (includingMinimum)
					{
						minimumQtyCondition.add(map);
						//						builder.append(getMessageSource().getMessage("text.deal.title.uom.product.minimnum",
						//								Arrays.array(map.get(MAP_KEY_QTY), map.get(MAP_KEY_UOM), map.get(MAP_KEY_PROD)), locale));
					}
					//else
					//{
					builder.append(getMessageSource().getMessage("text.deal.title.single.product.bold",
							Arrays.array(map.get(MAP_KEY_PROD)), locale));
					//}
				}
			}
			else
			{
				final String reqlQty = mapValues.get(MAP_DEAL_SCALE_QTY) != null ? mapValues.get(MAP_DEAL_SCALE_QTY)
						: map.get(MAP_KEY_QTY);
				final String uom = Integer.valueOf(reqlQty) > 1 ? map.get(MAP_KEY_UOM_PLURAL) : map.get(MAP_KEY_UOM_SINGULAR);
				builder.append(getMessageSource().getMessage("text.deal.title.uom.product",
						Arrays.array(reqlQty, uom, map.get(MAP_KEY_PROD)), locale));
			}
			count++;
		}
		if (minimumQtyCondition.size() <= 0)
		{
			return builder.toString();
		}

		count = 0;
		for (final Map<String, String> map : minimumQtyCondition)
		{
			addCommaConjunction(count, minimumQtyCondition, locale, builder);
			if (count == 0)
			{
				builder.append(getMessageSource().getMessage("text.deal.title.including.sub", null, locale));

				builder.append(getMessageSource().getMessage("text.deal.title.uom.product",
						Arrays.array(map.get(MAP_KEY_QTY), map.get(MAP_KEY_UOM), map.get(MAP_KEY_PROD)), locale));
			}
			else
			{
				builder.append(getMessageSource().getMessage("text.deal.title.uom.product.minimnum",
						Arrays.array(map.get(MAP_KEY_QTY), map.get(MAP_KEY_UOM), map.get(MAP_KEY_PROD)), locale));
			}
			count++;
		}

		return builder.toString();
	}

	/**
	 * Gets the unit name.
	 *
	 * @param qty
	 *           the qty
	 * @param unit
	 *           the unit
	 * @return the unit name
	 */
	protected String getUnitName(final int qty, final UnitModel unit)
	{
		return unit == null ? StringUtils.EMPTY : qty > 1 ? unit.getPluralName().toLowerCase() : unit.getName().toLowerCase();
	}

	/**
	 * Adds the a separator or a conjunction if the counter is greater than 0 and there are more than 1 item in the
	 * collection.
	 *
	 * @param count
	 *           the counter
	 * @param collection
	 *           the collection
	 * @param locale
	 *           the locale
	 * @param builder
	 *           the builder
	 */
	protected void addCommaConjunction(final int count, final Collection<? extends Object> collection, final Locale locale,
			final StringBuilder builder)
	{
		if (count > 0)
		{
			if (count <= collection.size() - 2)
			{
				builder.append(getMessageSource().getMessage("text.deal.title.comma", null, locale));
			}
			else if (count <= collection.size() - 1)
			{
				builder.append(getMessageSource().getMessage("text.deal.title.conjunction", null, locale));
			}
		}

	}

	/**
	 * Populate deal benefit title.
	 *
	 * @param deals
	 *           the deals
	 * @param locale
	 *           the locale
	 * @param mapValues
	 *           the map values
	 * @param useBold
	 *           the use bold
	 * @return the string
	 */
	protected String populateDealBenefitTitle(final List<DealModel> deals, final Locale locale,
			final Map<String, String> mapValues, final boolean useBold)
	{
		final StringBuilder builder = new StringBuilder();

		if (deals.size() > 1)
		{
			final List<Map<String, String>> argList = new ArrayList<>();

			for (final DealModel deal : deals)
			{
				forEachDeals(deal, mapValues, argList);
			}

			int count = 0;
			for (final Map<String, String> map : argList)
			{
				builder.append(
						getMessageSource().getMessage(count == 0 ? "text.deal.title.receive" : "text.deal.title.or", null, locale));

				builder.append(getMessageSource().getMessage("text.deal.title.bonus" + (useBold ? ".bold" : ""),
						Arrays.array(map.get(MAP_KEY_QTY), map.get(MAP_KEY_UOM), map.get(MAP_KEY_PROD)), locale));

				count++;
			}
		}
		else
		{
			final Map<String, String> mapFree = Maps.newHashMap();
			final Map<String, String> mapDisc = Maps.newHashMap();

			final DealModel deal = deals.get(0);

			for (final AbstractDealBenefitModel benefit : deal.getConditionGroup().getDealBenefits())
			{

				checkBenefit(benefit, mapValues, mapFree, mapDisc);
			}

			buildBenefitTitle(mapDisc, mapFree, mapValues, builder, locale, useBold);
		}

		return builder.toString();
	}

	/**
	 * when the deal is MultiScale then check each deal and build the benefit
	 *
	 * @param deal
	 * @param mapValues
	 * @param argList
	 */
	protected void forEachDeals(final DealModel deal, final Map<String, String> mapValues, final List<Map<String, String>> argList)
	{
		for (final AbstractDealBenefitModel benefit : deal.getConditionGroup().getDealBenefits())
		{
			//the scale num is not null then get the scale's benefit
			if (StringUtils.isNotEmpty(mapValues.get(MAP_DEAL_SCALE_NUM)))
			{
				if (benefit.getScale().equals(mapValues.get(MAP_DEAL_SCALE_NUM)) && benefit instanceof FreeGoodsDealBenefitModel)
				{
					final Map<String, String> map = Maps.newHashMap();
					populateFreeGoodMap((FreeGoodsDealBenefitModel) benefit, map, mapValues);
					argList.add(map);
					break;
				}
			}
			else
			{
				if (benefit instanceof FreeGoodsDealBenefitModel)
				{
					final Map<String, String> map = Maps.newHashMap();
					populateFreeGoodMap((FreeGoodsDealBenefitModel) benefit, map, mapValues);
					argList.add(map);
				}
			}
		}
	}

	/**
	 * check and build the benefit
	 *
	 * @param benefit
	 * @param mapValues
	 * @param mapFree
	 * @param mapDisc
	 */
	protected void checkBenefit(final AbstractDealBenefitModel benefit, final Map<String, String> mapValues,
			final Map<String, String> mapFree, final Map<String, String> mapDisc)
	{
		//the scale num is not null then get the scale's benefit
		if (StringUtils.isNotEmpty(mapValues.get(MAP_DEAL_SCALE_NUM)))
		{
			if (benefit.getScale().equals(mapValues.get(MAP_DEAL_SCALE_NUM)) && benefit instanceof FreeGoodsDealBenefitModel)
			{
				populateFreeGoodMap((FreeGoodsDealBenefitModel) benefit, mapFree, mapValues);
			}
			else if (benefit.getScale().equals(mapValues.get(MAP_DEAL_SCALE_NUM)) && benefit instanceof DiscountDealBenefitModel)
			{
				populateDiscountMap((DiscountDealBenefitModel) benefit, mapDisc, mapValues);
			}
		}
		else
		{
			if (benefit instanceof FreeGoodsDealBenefitModel)
			{
				populateFreeGoodMap((FreeGoodsDealBenefitModel) benefit, mapFree, mapValues);
			}
			else if (benefit instanceof DiscountDealBenefitModel)
			{
				populateDiscountMap((DiscountDealBenefitModel) benefit, mapDisc, mapValues);
			}
		}
	}

	/**
	 * Builds the benefit title.
	 *
	 * @param mapDisc
	 *           the map disc
	 * @param mapFree
	 *           the map free
	 * @param mapConditionBenefit
	 *           the map condition benefit
	 * @param builder
	 *           the builder
	 * @param locale
	 *           the locale
	 * @param useBold
	 *           the use bold
	 */
	protected void buildBenefitTitle(final Map<String, String> mapDisc, final Map<String, String> mapFree,
			final Map<String, String> mapConditionBenefit, final StringBuilder builder, final Locale locale, final boolean useBold)
	{
		if (MapUtils.isNotEmpty(mapDisc))
		{
			final CurrencyModel currency = commonI18NService.getCurrentCurrency();

			builder.append(getMessageSource().getMessage("text.deal.title.receive", null, locale));
			builder.append(getMessageSource().getMessage("text.deal.title.disc" + (useBold ? ".bold" : ""),
					Arrays.array(currency.getSymbol() + mapDisc.get(MAP_KEY_AMOUNT), mapConditionBenefit.get(MAP_KEY_UOM)), locale));

			if (MapUtils.isNotEmpty(mapFree))
			{
				builder.append(getMessageSource().getMessage("text.deal.title.conjunction", null, locale));
				builder.append(getMessageSource().getMessage(
						isSameFreeGood(mapFree, mapConditionBenefit) ? "text.deal.title.bonus.uom" + (useBold ? ".bold" : "")
								: "text.deal.title.bonus" + (useBold ? ".bold" : ""),
						Arrays.array(mapFree.get(MAP_KEY_QTY), mapFree.get(MAP_KEY_UOM), mapFree.get(MAP_KEY_PROD)), locale));
			}
		}
		else if (MapUtils.isNotEmpty(mapFree))
		{

			builder.append(getMessageSource().getMessage("text.deal.title.receive", null, locale));
			builder.append(getMessageSource().getMessage(
					isSameFreeGood(mapFree, mapConditionBenefit) ? "text.deal.title.bonus.uom" + (useBold ? ".bold" : "")
							: "text.deal.title.bonus" + (useBold ? ".bold" : ""),
					Arrays.array(mapFree.get(MAP_KEY_QTY), mapFree.get(MAP_KEY_UOM), mapFree.get(MAP_KEY_PROD)), locale));
		}
	}

	/**
	 * judgment the free good is the same condition good.
	 *
	 * @param mapFree
	 *           the mapFree
	 * @param mapConditionValues
	 *           the mapConditionValues
	 * @return true, if is same free good
	 */
	protected boolean isSameFreeGood(final Map<String, String> mapFree, final Map<String, String> mapConditionValues)
	{
		if (StringUtils.isNotEmpty(mapConditionValues.get(MAP_KEY_ONLY_PROD))
				&& mapFree.get(MAP_KEY_PROD_CODE).equals(mapConditionValues.get(MAP_KEY_PROD_CODE)))
		{
			return true;
		}
		return false;
	}

	/**
	 * Populate free good map.
	 *
	 * @param benefit
	 *           the benefit
	 * @param map
	 *           the map
	 */
	protected void populateFreeGoodMap(final FreeGoodsDealBenefitModel benefit, final Map<String, String> map,
			final Map<String, String> mapValue)
	{
		final String productName = getProductName(benefit.getProductCode());

		map.put(MAP_KEY_PROD, productName);
		Integer qty = benefit.getQuantity();

		if (mapValue.containsKey(MAP_KEY_RATIO) && NumberUtils.isNumber(mapValue.get(MAP_KEY_RATIO))
				&& Integer.valueOf(mapValue.get(MAP_KEY_RATIO)) > 0)
		{
			qty = qty * Integer.valueOf(mapValue.get(MAP_KEY_RATIO));
		}
		map.put(MAP_KEY_QTY, qty.toString());
		map.put(MAP_KEY_UOM, getUnitName(qty, benefit.getUnit()));
		map.put(MAP_KEY_PROD_CODE, benefit.getProductCode());
		LOG.debug("Populated freegood benefit [{}] in map [{}]", benefit, map);
	}

	/**
	 * Populate discount map.
	 *
	 * @param benefit
	 *           the benefit
	 * @param map
	 *           the map
	 * @param mapValues
	 *           the map values
	 */
	protected void populateDiscountMap(final DiscountDealBenefitModel benefit, final Map<String, String> map,
			final Map<String, String> mapValues)
	{
		if (benefit.getUnit() != null && benefit.getUnit().getName() != null)
		{
			map.put(MAP_KEY_UOM, benefit.getUnit().getName().toLowerCase());
		}
		else
		{
			map.put(MAP_KEY_UOM, StringUtils.EMPTY);
		}

		BigDecimal discountPerUnit = BigDecimal.ZERO;

		if (BooleanUtils.isTrue(benefit.getCurrency()))
		{
			if (benefit.getAmount() != null)
			{
				discountPerUnit = discountPerUnitCalculationStrategy.roundAmount(BigDecimal.valueOf(Math.abs(benefit.getAmount())));
			}
		}
		else
		{
			discountPerUnit = discountPerUnitCalculationStrategy.calculateDiscountPerUnit(mapValues.get(MAP_KEY_PROD_CODE),
					benefit.getAmount());
		}

		map.put(MAP_KEY_AMOUNT, discountPerUnit.toString());

		LOG.debug("Populated discount benefit [{}] in map [{}]", benefit, map);
	}

	/**
	 * Populate product condition map.
	 *
	 * @param condition
	 *           the condition
	 * @param map
	 *           the map
	 * @param mapConditionBenefit
	 *           the map condition benefit
	 */
	protected void populateProductConditionMap(final ProductDealConditionModel condition, final Map<String, String> map,
			final Map<String, String> mapConditionBenefit)
	{
		final String product = getProductName(condition.getProductCode());
		mapConditionBenefit.put(MAP_KEY_PROD_CODE, condition.getProductCode());
		map.put(MAP_KEY_PROD, product);
		map.put(MAP_KEY_MANDATORY, BooleanUtils.toStringTrueFalse(condition.getMandatory()));

		if (mapConditionBenefit.containsKey(MAP_KEY_PARTIAL_QTY)
				&& NumberUtils.isNumber(mapConditionBenefit.get(MAP_KEY_PARTIAL_QTY)))
		{
			final Integer partialQty = NumberUtils.toInt(mapConditionBenefit.get(MAP_KEY_PARTIAL_QTY)) < 0 ? 0
					: NumberUtils.toInt(mapConditionBenefit.get(MAP_KEY_PARTIAL_QTY));
			map.put(MAP_KEY_QTY, partialQty.toString());
			map.put(MAP_KEY_UOM, getUnitName(partialQty, condition.getUnit()));
		}
		else
		{
			final int qty = condition.getMinQty() != null ? condition.getMinQty()
					: condition.getQuantity() != null ? condition.getQuantity() : 0;

			map.put(MAP_KEY_QTY, Integer.toString(qty));
			map.put(MAP_KEY_UOM, getUnitName(qty, condition.getUnit()));
		}

		map.put(MAP_KEY_UOM_SINGULAR, getUnitName(1, condition.getUnit()));
		map.put(MAP_KEY_UOM_PLURAL, getUnitName(2, condition.getUnit()));
		mapConditionBenefit.put(MAP_KEY_UOM, getUnitName(1, condition.getUnit()));
		mapConditionBenefit.put(MAP_KEY_UOMS, getUnitName(2, condition.getUnit()));

		LOG.debug("Populated product condition [{}] in map [{}]", condition, map);

	}

	/**
	 * Populate complex condition map.
	 *
	 * @param condition
	 *           the condition
	 * @param map
	 *           the map
	 * @param mapConditionBenefit
	 *           the map condition benefit
	 */
	protected void populateComplexConditionMap(final ComplexDealConditionModel condition, final Map<String, String> map,
			final Map<String, String> mapConditionBenefit)
	{
		map.put(MAP_KEY_RANGE, getComplexBrand(condition.getBrand()));
		map.put(MAP_KEY_MANDATORY, BooleanUtils.toStringTrueFalse(condition.getMandatory()));

		final Integer qty = condition.getQuantity() == null ? 0 : condition.getQuantity();

		if (mapConditionBenefit.containsKey(MAP_KEY_PARTIAL_QTY)
				&& NumberUtils.isNumber(mapConditionBenefit.get(MAP_KEY_PARTIAL_QTY)))
		{
			final Integer partialQty = NumberUtils.toInt(mapConditionBenefit.get(MAP_KEY_PARTIAL_QTY)) < 0 ? 0
					: NumberUtils.toInt(mapConditionBenefit.get(MAP_KEY_PARTIAL_QTY));
			map.put(MAP_KEY_QTY, partialQty.toString());
			map.put(MAP_KEY_UOM, getUnitName(partialQty, condition.getUnit()));
		}
		else
		{
			map.put(MAP_KEY_QTY, qty.toString());
			map.put(MAP_KEY_UOM, getUnitName(qty, condition.getUnit()));
		}

		map.put(MAP_KEY_UOM_SINGULAR, getUnitName(1, condition.getUnit()));
		map.put(MAP_KEY_UOM_PLURAL, getUnitName(2, condition.getUnit()));
		mapConditionBenefit.put(MAP_KEY_UOM, getUnitName(1, condition.getUnit()));

		final List<SABMAlcoholVariantProductMaterialModel> materials = getProductService().getProductByHierarchy(
				condition.getLine(), condition.getBrand(), condition.getVariety(), condition.getEmpties(), condition.getEmptyType(),
				condition.getPresentation());

		if (CollectionUtils.isNotEmpty(materials))
		{
			mapConditionBenefit.put(MAP_KEY_PROD_CODE, materials.get(0).getCode());

			final Set<ProductModel> baseProducts = new HashSet<>();

			for (final SABMAlcoholVariantProductMaterialModel material : materials)
			{
				baseProducts.add(material.getBaseProduct());
			}


			map.put(MAP_KEY_PROD, getProductName(materials.get(0)));

		}

		LOG.debug("Populated complex condition [{}] in map [{}]", condition, map);
	}

	/**
	 * Gets the product name.
	 *
	 * @param productCode
	 *           the product code
	 * @return the product name
	 */
	protected String getProductName(final String productCode)
	{
		final ProductModel product = getProductService().getProductForCodeSafe(productCode);

		if (product == null)
		{
			throw new ConversionException("Null product in deal with code: " + productCode);
		}

		return getProductName(product);
	}

	/**
	 * Gets the product name.
	 *
	 * @param product
	 *           the product
	 * @return the product name
	 */
	protected String getProductName(final ProductModel product)
	{
		if (product instanceof SABMAlcoholVariantProductMaterialModel
				&& ((SABMAlcoholVariantProductMaterialModel) product).getBaseProduct() instanceof SABMAlcoholVariantProductEANModel)
		{
			final ProductModel eanProduct = ((SABMAlcoholVariantProductMaterialModel) product).getBaseProduct();
			//Override productData name if sellingName is not empty
			if (StringUtils.isNotEmpty(eanProduct.getSellingName()) && StringUtils.isNotEmpty(eanProduct.getPackConfiguration()))
			{
				return StringUtils.join(Arrays.array(eanProduct.getSellingName(), "</b>" + eanProduct.getPackConfiguration() + "<b>"),
						" ");
			}
			return eanProduct.getName();
		}

		return StringUtils.EMPTY;
	}

	/**
	 * Gets the message source.
	 *
	 * @return the message source
	 */
	public MessageSource getMessageSource()
	{
		if (messageSource == null)
		{
			messageSource = Registry.getApplicationContext().getBean(messageSourceName, MessageSource.class);
		}

		return messageSource;
	}

	/**
	 * get brandName from product.
	 *
	 * @param brand
	 *           the brand
	 * @return String
	 */
	protected String getComplexBrand(final String brand)
	{
		if (StringUtils.isEmpty(brand))
		{
			return StringUtils.EMPTY;
		}

		final SABMAlcoholProductModel product = getProductService().getSABMAlcoholProduct(brand);
		return product == null ? StringUtils.EMPTY : StringUtils.trimToEmpty(product.getBrand());
	}

	/**
	 * Checks if is across.
	 *
	 * @param dealCondition
	 *           the deal condition
	 * @return true, if is across
	 */
	protected boolean isAcross(final DealConditionGroupModel dealCondition)
	{
		return dealsService.isAcross(dealCondition);
	}

	/**
	 * Gets the across qty.
	 *
	 * @param dealCondition
	 *           the deal condition
	 * @return the across qty
	 */
	protected int getAcrossQty(final DealConditionGroupModel dealCondition)
	{
		final List<DealScaleModel> dealScales = new ArrayList<>(dealCondition.getDealScales());

		if (CollectionUtils.isNotEmpty(dealScales))
		{
			Collections.sort(dealScales, DealScaleComparator.INSTANCE);
			return dealScales.get(0).getFrom() == null ? 0 : dealScales.get(0).getFrom();
		}

		return 0;
	}

	/**
	 * Checks if is multi scale.
	 *
	 * @param deal
	 *           the deal
	 * @return true, if is multi scale
	 */
	protected boolean isMultiScale(final DealModel deal)
	{
		return deal.getConditionGroup().getDealScales() != null && deal.getConditionGroup().getDealScales().size() > 1;
	}

	/**
	 * Checks if is multi range.
	 *
	 * @param dealConditions
	 *           the deal conditions
	 * @return true, if is multi range
	 */
	protected boolean isMultiRange(final List<AbstractDealConditionModel> dealConditions)
	{
		return dealsService.isMultiRange(dealConditions);
	}

	/**
	 * Gets the max quantity for LIMITED deal.
	 *
	 * @param deal
	 *           the deal
	 * @return the max quantity
	 */
	protected int getMaxQuantity(final DealModel deal)
	{
		if (deal != null && DealTypeEnum.LIMITED.equals(deal.getDealType()))
		{
			if (deal.getMaxConditionBaseValue() != null && deal.getMaxConditionBaseValue() > 0)
			{
				return (int) (deal.getMaxConditionBaseValue()
						- (deal.getUsedConditionBaseValue() == null ? 0 : deal.getUsedConditionBaseValue()));
			}
			else if (deal.getMaxConditionValue() != null && Math.abs(deal.getMaxConditionValue()) > 0)
			{
				return (int) (Math.abs(deal.getMaxConditionValue())
						- (deal.getUsedConditionValue() == null ? 0 : Math.abs(deal.getUsedConditionValue())));
			}
		}

		return 0;
	}

	/**
	 * Gets the max base quantity.
	 *
	 * @param deal
	 *           the deal
	 * @return the max base quantity
	 */
	protected double getMaxBaseQuantity(final DealModel deal)
	{
		if (deal != null && DealTypeEnum.LIMITED.equals(deal.getDealType()))
		{
			if (deal.getMaxConditionBaseValue() != null && deal.getMaxConditionBaseValue() > 0)
			{
				return deal.getMaxConditionBaseValue();
			}
			else if (deal.getMaxConditionValue() != null && Math.abs(deal.getMaxConditionValue()) > 0)
			{
				return Math.abs(deal.getMaxConditionValue());
			}
		}

		return 0;
	}

	/**
	 * Checks if is limited quantity currency.
	 *
	 * @param deal
	 *           the deal
	 * @return true, if is limited quantity currency
	 */
	protected boolean isLimitedQuantityCurrency(final DealModel deal)
	{
		if (deal != null && DealTypeEnum.LIMITED.equals(deal.getDealType()))
		{
			return deal.getMaxConditionValue() != null && Math.abs(deal.getMaxConditionValue()) > 0;
		}

		return false;
	}


	/**
	 * set the in store to badges. The value is 3 for in store display in the page Set deal badges.
	 *
	 * @param target
	 *           the target
	 * @param badge
	 *           the badge
	 */
	protected void setBadges(final DealJson target, final Integer badge)
	{
		if (target.getBadges() != null)
		{

			if (!target.getBadges().contains(badge))
			{
				target.getBadges().add(badge);

			}
		}
		else
		{
			final List<Integer> badges = new ArrayList<>();

			badges.add(badge);

			target.setBadges(badges);
		}
	}


	/**
	 * Gets the product service.
	 *
	 * @return the productService
	 */
	public SabmProductService getProductService()
	{
		return productService;
	}

	/**
	 * Gets the i18n service.
	 *
	 * @return the i18nService
	 */
	public I18NService getI18nService()
	{
		return i18nService;
	}

	/**
	 * Gets the common i18 n service.
	 *
	 * @return the commonI18NService
	 */
	public CommonI18NService getCommonI18NService()
	{
		return commonI18NService;
	}

	/**
	 * Gets the discount per unit calculation strategy.
	 *
	 * @return the discountPerUnitCalculationStrategy
	 */
	public SABMDiscountPerUnitCalculationStrategy getDiscountPerUnitCalculationStrategy()
	{
		return discountPerUnitCalculationStrategy;
	}

	/**
	 * Gets the deals service.
	 *
	 * @return the dealsService
	 */
	public DealsService getDealsService()
	{
		return dealsService;
	}

}
