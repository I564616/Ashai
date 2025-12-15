/**
 *
 */
package com.sabmiller.facades.populators;

import de.hybris.platform.converters.Populator;
import de.hybris.platform.servicelayer.dto.converter.ConversionException;
import de.hybris.platform.servicelayer.i18n.CommonI18NService;

import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;

import jakarta.annotation.Resource;

import org.apache.commons.lang3.StringUtils;
import org.assertj.core.util.Arrays;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.sabmiller.core.model.DealModel;
import com.sabmiller.facades.deal.data.DealJson;


/**
 * SABMDealTitlePopulator.
 */
public class SABMDealTitlePopulator extends SABMAbstractDealPopulator implements Populator<List<DealModel>, DealJson>
{
	/** The Constant LOG. */
	private static final Logger LOG = LoggerFactory.getLogger(SABMDealTitlePopulator.class);

	@Resource(name = "commonI18NService")
	private CommonI18NService commonI18NService;

	/** The Constant SINGLECASE. */
	private static final Integer SINGLEUNIT = 5;

	/** The Constant MULTCASE. */
	private static final Integer MULTUNIT = 6;

	/*
	 * (non-Javadoc)
	 *
	 * @see com.sabmiller.facades.populators.SABMDealProductPopulator#populate(java.util.List,
	 * com.sabmiller.facades.deal.data.DealJson)
	 */
	@Override
	public void populate(final List<DealModel> source, final DealJson target) throws ConversionException
	{
		if (source.get(0) == null || source.get(0).getConditionGroup() == null)
		{
			LOG.warn("Deal: [{}] is without conditionGroup!", source.get(0));
			return;
		}
		final Locale locale = getI18nService().getCurrentLocale();

		//Map of parameter passed from the conditions to the benefits.
		final Map<String, String> mapValues = new HashMap<>();
		final String dealConditionTitle = populateDealConditionTitle(source, locale, mapValues);
		String dealBenefitTitle = StringUtils.EMPTY;

		if (!isMultiScale(source.get(0)))
		{
			dealBenefitTitle = populateDealBenefitTitle(source, locale, mapValues, true);
		}

		//SABMC-844  generate suffix of deal title like (Maximum of <number of preconditions> deals per order)
		final String titleSuffix = populateTitleSuffix(source, locale, mapValues);

		final String title = StringUtils.replace(
				StringUtils.join(Arrays.array(dealConditionTitle, dealBenefitTitle, titleSuffix), StringUtils.EMPTY), HTML_SPACE,
				NORMAL_SPACE);

		target.setTitle(title);
		populateSingleMultiUnitBadge(target);
	}


	/**
	 * populate Single MultiCase Badge
	 *
	 * @param source
	 * @param target
	 */
	private void populateSingleMultiUnitBadge(final DealJson target)
	{
		if (StringUtils.startsWithIgnoreCase(target.getTitle(), "Buy <b>1 case</b>")
				|| StringUtils.startsWithIgnoreCase(target.getTitle(), "Buy <b>1 keg</b>"))
		{
			setBadges(target, SINGLEUNIT);
		}
		else
		{
			setBadges(target, MULTUNIT);
		}
	}
}
