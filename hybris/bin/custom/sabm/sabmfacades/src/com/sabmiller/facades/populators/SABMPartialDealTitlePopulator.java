/**
 *
 */
package com.sabmiller.facades.populators;

import de.hybris.platform.converters.Populator;
import de.hybris.platform.servicelayer.dto.converter.ConversionException;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.Objects;

import org.apache.commons.collections4.CollectionUtils;
import org.apache.commons.collections4.MapUtils;
import org.apache.commons.lang3.BooleanUtils;
import org.apache.commons.lang3.StringUtils;
import org.apache.commons.lang3.math.NumberUtils;
import org.assertj.core.util.Arrays;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.google.common.collect.Maps;
import com.sabmiller.core.comparators.DealScaleComparator;
import com.sabmiller.core.deals.services.response.PartialDealQualificationResponse.PartialAvailability;
import com.sabmiller.core.model.AbstractDealBenefitModel;
import com.sabmiller.core.model.AbstractDealConditionModel;
import com.sabmiller.core.model.ComplexDealConditionModel;
import com.sabmiller.core.model.DealModel;
import com.sabmiller.core.model.DealScaleModel;
import com.sabmiller.core.model.DiscountDealBenefitModel;
import com.sabmiller.core.model.FreeGoodsDealBenefitModel;
import com.sabmiller.core.model.ProductDealConditionModel;
import com.sabmiller.facades.deal.data.DealJson;


/**
 * SABMPartialDealTitlePopulator.
 */
public class SABMPartialDealTitlePopulator extends SABMAbstractDealPopulator implements Populator<PartialAvailability, DealJson>
{

	/** The Constant LOG. */
	private static final Logger LOG = LoggerFactory.getLogger(SABMPartialDealTitlePopulator.class);

	/*
	 * (non-Javadoc)
	 *
	 * @see de.hybris.platform.converters.Populator#populate(java.lang.Object, java.lang.Object)
	 */
	@Override
	public void populate(final PartialAvailability source, final DealJson target) throws ConversionException
	{
		final Locale locale = getI18nService().getCurrentLocale();

		//Map of parameter passed from the conditions to the benefits.
		final Map<String, String> mapValues = new HashMap<>();
		final String dealConditionTitle = populateDealConditionTitle(source, locale, mapValues, target);
		String dealBenefitTitle = StringUtils.EMPTY;
		if (!isMultiScale(source.getDeal()))
		{
			final List<DealModel> arrayList = new ArrayList<>();
			arrayList.add(source.getDeal());
			dealBenefitTitle = populateDealBenefitTitle(arrayList, locale, mapValues, false);
		}

		final String titleSuffix = populateLimitedSuffix(source.getDeal(), locale, mapValues, false);

		final String title = StringUtils.replace(
				StringUtils.join(Arrays.array(dealConditionTitle, dealBenefitTitle, titleSuffix), StringUtils.EMPTY), HTML_SPACE,
				NORMAL_SPACE);

		LOG.debug("PQD title for [{}] is [{}]", source, title);

		target.setTitle(title);
	}

	/**
	 * Populate deal condition title.
	 *
	 * @param source
	 *           the source
	 * @param locale
	 *           the locale
	 * @param mapValues
	 *           the map values
	 * @return the string
	 */
	protected String populateDealConditionTitle(final PartialAvailability source, final Locale locale,
			final Map<String, String> mapValues, final DealJson dealJson)
	{
		final boolean rangeDeal = isMultiRange(source.getDeal().getConditionGroup().getDealConditions());
		final boolean multiScale = isMultiScale(source.getDeal());
		mapValues.put(MAP_IS_RANGE_DEAL, BooleanUtils.toStringTrueFalse(rangeDeal));
		if (multiScale)
		{
			return populateMultiScaleDealConditionTitle(source, locale, mapValues, dealJson);
		}
		else if (rangeDeal)
		{
			return populateRangeDealConditionTitle(source, locale, mapValues);
		}
		else
		{
			return populateSimpleDealConditionTitle(source, locale, mapValues, dealJson);
		}
	}

	/**
	 * Populate multi scale deal condition title.
	 *
	 * @param partial
	 *           the partial
	 * @param locale
	 *           the locale
	 * @param mapValues
	 *           the map values
	 * @return the string
	 */
	protected String populateMultiScaleDealConditionTitle(final PartialAvailability partial, final Locale locale,
			final Map<String, String> mapValues, final DealJson dealJson)
	{
		final StringBuilder builder = new StringBuilder();
		final Map<String, Map<String, String>> mapScaleFree = Maps.newHashMap();
		final Map<String, Map<String, String>> mapScaleDisc = Maps.newHashMap();
		final List<Map<String, String>> argList = new ArrayList<>();

		populateConditionMap(partial, null, null, mapValues, argList);

		final List<DealScaleModel> dealScales = new ArrayList<>(partial.getDeal().getConditionGroup().getDealScales());

		Collections.sort(dealScales, DealScaleComparator.INSTANCE);

		for (final DealScaleModel scale : dealScales)
		{
			if (NumberUtils.isNumber(mapValues.get(MAP_KEY_SCALE_QTY))
					&& Integer.valueOf(mapValues.get(MAP_KEY_SCALE_QTY)).compareTo(scale.getFrom()) > 0)
			{
				continue;
			}

			if (partial.getScale() == 0 || Objects.equals(scale.getFrom(), partial.getScale()))
			{
				mapValues.put(MAP_KEY_SCALE_QTY, "0");
				mapValues.put(MAP_KEY_SCALE_DEAL, Integer.toString(partial.getScale()));
				for (final AbstractDealBenefitModel benefit : partial.getDeal().getConditionGroup().getDealBenefits())
				{
					if (StringUtils.equalsIgnoreCase(benefit.getScale(), scale.getScale()))
					{
						if (benefit instanceof FreeGoodsDealBenefitModel)
						{
							final Map<String, String> mapFree = Maps.newHashMap();
							populateFreeGoodMap((FreeGoodsDealBenefitModel) benefit, mapFree, mapValues);
							mapScaleFree.put(((FreeGoodsDealBenefitModel) benefit).getScale(), mapFree);
						}
						else if (benefit instanceof DiscountDealBenefitModel)
						{
							final Map<String, String> mapDisc = Maps.newHashMap();
							populateDiscountMap((DiscountDealBenefitModel) benefit, mapDisc, mapValues);
							mapScaleDisc.put(((DiscountDealBenefitModel) benefit).getScale(), mapDisc);
						}

						break;
					}
				}


				if (CollectionUtils.isEmpty(argList) || (MapUtils.isEmpty(mapScaleDisc) && MapUtils.isEmpty(mapScaleFree)))
				{
					throw new ConversionException("Impossible to generate scale deal.");
				}
				String qty = null;

				if (partial.getScale() == 0)
				{
					qty = "0";
				}
				else
				{
					qty = Integer.toString(partial.getScale() - Integer.valueOf(mapValues.get(MAP_KEY_SCALE_QTY)));
				}

				builder.append(getMultiScaleDealConditionTitle(partial, locale, mapValues, dealJson));

				buildBenefitTitle(mapScaleDisc.get(scale.getScale()), mapScaleFree.get(scale.getScale()), mapValues, builder, locale,
						false);

				break;
			}
		}

		return builder.toString();
	}

	protected String getMultiScaleDealConditionTitle(final PartialAvailability partial, final Locale locale,
			final Map<String, String> mapValues, final DealJson dealJson)
	{
		//if the deal not a range deal then invoke old function
		if (BooleanUtils.toBoolean(mapValues.get(MAP_IS_RANGE_DEAL)))
		{
			return populateRangeDealConditionTitle(partial, locale, mapValues);
		}
		return populateSimpleDealConditionTitle(partial, locale, mapValues, dealJson);
	}

	/**
	 * Populate range deal condition title.
	 *
	 * @param partial
	 *           the partial
	 * @param locale
	 *           the locale
	 * @param mapValues
	 *           the map values
	 * @return the string
	 */
	protected String populateRangeDealConditionTitle(final PartialAvailability partial, final Locale locale,
			final Map<String, String> mapValues)
	{
		final StringBuilder builder = new StringBuilder();

		final List<Map<String, String>> argList = new ArrayList<>();
		final List<String> excepts = new ArrayList<>();

		populateConditionMap(partial, excepts, null, mapValues, argList);

		int count = 0;
		final boolean isAcross = isAcross(partial.getDeal().getConditionGroup());

		for (final Map<String, String> map : argList)
		{
			addCommaConjunction(count, argList, locale, builder);
			if (isAcross)
			{
				if (count == 0)
				{
					if (isMultiScale(partial.getDeal()) && NumberUtils.isNumber(mapValues.get(MAP_KEY_SCALE_DEAL))
							&& NumberUtils.isNumber(mapValues.get(MAP_KEY_SCALE_QTY)))
					{
						final Integer qty = Integer.valueOf(mapValues.get(MAP_KEY_SCALE_DEAL))
								- Integer.valueOf(mapValues.get(MAP_KEY_SCALE_QTY));

						builder.append(getMessageSource().getMessage("text.partial.deal.add.more.range.across",
								Arrays.array(qty.toString(), map.get(MAP_KEY_UOM), map.get(MAP_KEY_RANGE)), locale));
					}
					else
					{
						builder.append(getMessageSource().getMessage("text.partial.deal.add.more.range.across",
								Arrays.array(map.get(MAP_KEY_QTY), map.get(MAP_KEY_UOM), map.get(MAP_KEY_RANGE)), locale));
					}
				}
				else
				{
					builder.append(getMessageSource().getMessage("text.deal.title.single.product",
							Arrays.array(map.get(MAP_KEY_RANGE)), locale));
				}

				if (count == argList.size() - 1)
				{
					builder.append(getMessageSource().getMessage("text.deal.title.ranges", null, locale));
				}
			}
			else
			{
				if (count == 0)
				{
					builder.append(getMessageSource().getMessage("text.partial.deal.title.range.uom.product",
							Arrays.array(map.get(MAP_KEY_QTY), map.get(MAP_KEY_UOM), map.get(MAP_KEY_RANGE)), locale));
				}
				else
				{
					builder.append(getMessageSource().getMessage("text.partial.deal.title.range.single.product",
							Arrays.array(map.get(MAP_KEY_QTY), map.get(MAP_KEY_UOM), map.get(MAP_KEY_RANGE)), locale));
				}
			}

			count++;
		}

		if (CollectionUtils.isNotEmpty(excepts))
		{
			builder.append(getMessageSource().getMessage("text.deal.title.except", null, locale));
			count = 0;
			for (final String except : excepts)
			{
				addCommaConjunction(count, excepts, locale, builder);

				builder.append(getMessageSource().getMessage("text.deal.title.single.product", Arrays.array(except), locale));

				count++;
			}
		}

		return builder.toString();
	}

	/**
	 * Populate simple deal condition title.
	 *
	 * @param partial
	 *           the partial
	 * @param locale
	 *           the locale
	 * @param mapValues
	 *           the map values
	 * @return the string
	 */
	protected String populateSimpleDealConditionTitle(final PartialAvailability partial, final Locale locale,
			final Map<String, String> mapValues, final DealJson dealJson)
	{
		final StringBuilder builder = new StringBuilder();

		final List<Map<String, String>> argList = new ArrayList<>();
		populateConditionMap(partial, null, null, mapValues, argList);

		int count = 0;

		//To determine if there is only one
		if (argList.size() == 1)
		{
			mapValues.put(MAP_KEY_ONLY_PROD, MAP_VALUE_TRUE);
		}

		for (final Map<String, String> map : argList)
		{
			addCommaConjunction(count, argList, locale, builder);

			if (isAcross(partial.getDeal().getConditionGroup()))
			{
				final boolean includingMinimum = NumberUtils.isNumber(map.get(MAP_KEY_QTY))
						&& Integer.valueOf(map.get(MAP_KEY_QTY)) > 0 && BooleanUtils.toBoolean(map.get(MAP_KEY_MANDATORY));
				if (count == 0)
				{
					if (includingMinimum)
					{
						builder
								.append(getMessageSource().getMessage("text.partial.deal.title.including",
										Arrays.array(Integer.toString(getAcrossQty(partial.getDeal(), dealJson, mapValues)),
												map.get(MAP_KEY_UOM), map.get(MAP_KEY_PROD), map.get(MAP_KEY_QTY), map.get(MAP_KEY_UOM)),
										locale));
					}
					else
					{
						builder.append(getMessageSource().getMessage("text.partial.deal.title.across",
								Arrays.array(Integer.toString(getAcrossQty(partial.getDeal(), dealJson, mapValues)), map.get(MAP_KEY_UOM),
										map.get(MAP_KEY_PROD)),
								locale));
					}
				}
				else
				{
					if (includingMinimum)
					{
						builder.append(getMessageSource().getMessage("text.partial.deal.title.uom.product.minimnum",
								Arrays.array(map.get(MAP_KEY_PROD), map.get(MAP_KEY_QTY), map.get(MAP_KEY_UOM)), locale));
					}
					else
					{
						builder.append(getMessageSource().getMessage("text.deal.title.single.product",
								Arrays.array(map.get(MAP_KEY_PROD)), locale));
					}
				}
			}
			else
			{
				if (count == 0)
				{
					builder.append(getMessageSource().getMessage("text.partial.deal.add.more",
							Arrays.array(map.get(MAP_KEY_QTY), map.get(MAP_KEY_UOM), map.get(MAP_KEY_PROD)), locale));
				}
				else
				{
					builder.append(getMessageSource().getMessage("text.partial.deal.more.single.product",
							Arrays.array(map.get(MAP_KEY_QTY), map.get(MAP_KEY_UOM), map.get(MAP_KEY_PROD)), locale));
				}
			}
			count++;
		}

		return builder.toString();
	}

	/**
	 * Populate condition map.
	 *
	 * @param partial
	 *           the partial
	 * @param excepts
	 *           the excepts
	 * @param map
	 *           the map
	 * @param mapValues
	 *           the map values
	 * @param argList
	 *           the arg list
	 */
	protected void populateConditionMap(final PartialAvailability partial, final List<String> excepts,
			final Map<String, String> map, final Map<String, String> mapValues, final List<Map<String, String>> argList)
	{
		populateExceptsList(partial.getDeal(), excepts);

		for (final AbstractDealConditionModel condition : partial.getDeal().getConditionGroup().getDealConditions())
		{
			if (BooleanUtils.isNotTrue(condition.getExclude()))
			{
				mapValues.put(MAP_KEY_PARTIAL_QTY, Long.toString(partial.getRequiredQtyWithGivenDealCondition(condition)));
				/**
				 * Modified the below condition logic as per incident
				 * "INC0343881 : Cart erroring when Great Northern bulk deal is added to cart" Fix.
				 */
				if (getDealsService().isManualScaleProportionByEachDeal(partial.getDeal().getConditionGroup().getDealBenefits()))
				{
					mapValues.put(MAP_KEY_SCALE_QTY, Long.toString(partial.getAvailableQtyWithGivenDealCondition(condition)));
				}
				else
				{
					if (NumberUtils.isNumber(mapValues.get(MAP_KEY_SCALE_QTY)) && Long.valueOf(mapValues.get(MAP_KEY_SCALE_QTY)) > 0)
					{
						mapValues.put(MAP_KEY_SCALE_QTY, Long.toString(Long.valueOf(mapValues.get(MAP_KEY_SCALE_QTY))
								+ partial.getAvailableQtyWithGivenDealCondition(condition)));
					}
					else
					{
						mapValues.put(MAP_KEY_SCALE_QTY, Long.toString(partial.getAvailableQtyWithGivenDealCondition(condition)));
					}
				}
				if (!getDealsService().isManualScaleProportionByEachDeal(partial.getDeal().getConditionGroup().getDealBenefits()))
				{
					mapValues.put(MAP_KEY_RATIO, Integer.toString(partial.getRatio()));
				}
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
	 * Gets the across qty.
	 *
	 * @param dealJson
	 *           the deal json
	 * @return the across qty
	 */
	protected int getAcrossQty(final DealModel deal, final DealJson dealJson, final Map<String, String> mapValues)
	{
		if (isMultiScale(deal) && NumberUtils.isNumber(mapValues.get(MAP_KEY_SCALE_QTY))
				&& NumberUtils.isNumber(mapValues.get(MAP_KEY_SCALE_DEAL))
				&& Integer.valueOf(mapValues.get(MAP_KEY_SCALE_DEAL)) > Integer.valueOf(mapValues.get(MAP_KEY_SCALE_QTY)))
		{
			return Integer.valueOf(mapValues.get(MAP_KEY_SCALE_DEAL)) - Integer.valueOf(mapValues.get(MAP_KEY_SCALE_QTY));
		}
		if (dealJson != null && CollectionUtils.isNotEmpty(dealJson.getRanges()) && dealJson.getRanges().get(0).getMinQty() != null
				&& dealJson.getRanges().get(0).getMinQty() > 0)
		{
			return dealJson.getRanges().get(0).getMinQty();
		}

		if (dealJson != null && CollectionUtils.isNotEmpty(dealJson.getRanges()) && (dealJson.getRanges().size() == 1))
		{
			final int ratio = deal.getConditionGroup().getDealScales().get(0).getFrom();
			return ratio * Integer.valueOf(mapValues.get(MAP_KEY_RATIO)) - Integer.valueOf(mapValues.get(MAP_KEY_SCALE_QTY));
		}


		final List<DealScaleModel> dealScales = new ArrayList<>(deal.getConditionGroup().getDealScales());

		if (CollectionUtils.isNotEmpty(dealScales))
		{
			Collections.sort(dealScales, DealScaleComparator.INSTANCE);
			return dealScales.get(0).getFrom() == null ? 0 : dealScales.get(0).getFrom();
		}

		return 0;
	}
}
