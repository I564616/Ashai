/**
 *
 */
package com.sabmiller.facades.populators;

import de.hybris.platform.b2b.model.B2BCustomerModel;
import de.hybris.platform.b2b.company.B2BCommerceUnitService;
import de.hybris.platform.converters.Populator;
import de.hybris.platform.core.model.order.AbstractOrderEntryModel;
import de.hybris.platform.core.model.order.CartModel;
import de.hybris.platform.servicelayer.dto.converter.ConversionException;
import de.hybris.platform.servicelayer.session.SessionService;
import de.hybris.platform.servicelayer.user.UserService;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Collections;
import java.util.Date;
import java.util.List;
import java.util.Map;
import java.util.Set;

import jakarta.annotation.Resource;

import org.apache.commons.collections4.CollectionUtils;
import org.apache.commons.lang3.BooleanUtils;
import org.apache.commons.lang3.math.NumberUtils;
import org.apache.commons.lang3.time.DateUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.util.Assert;

import com.google.common.collect.Maps;
import com.sabmiller.core.b2b.services.SABMDeliveryDateCutOffService;
import com.sabmiller.core.cart.service.SABMCartService;
import com.sabmiller.core.constants.SabmCoreConstants;
import com.sabmiller.core.deals.strategies.SABMDealValidationStrategy;
import com.sabmiller.core.enums.DealTypeEnum;
import com.sabmiller.core.enums.RepDrivenDealStatus;
import com.sabmiller.core.model.AbstractDealBenefitModel;
import com.sabmiller.core.model.AbstractDealConditionModel;
import com.sabmiller.core.model.DealModel;
import com.sabmiller.core.model.DiscountDealBenefitModel;
import com.sabmiller.core.model.FreeGoodsDealBenefitModel;
import com.sabmiller.core.model.ProductDealConditionModel;
import com.sabmiller.core.model.RepDrivenDealConditionStatusModel;
import com.sabmiller.core.model.SABMAlcoholVariantProductMaterialModel;
import com.sabmiller.facades.deal.data.DealJson;


/**
 * The Class SABMDealJsonPopulator.
 */
public class SABMDealJsonPopulator extends SABMAbstractDealPopulator implements Populator<List<DealModel>, DealJson>
{
	/** The Constant LOG. */
	private static final Logger LOG = LoggerFactory.getLogger(SABMDealJsonPopulator.class);

	/** The Constant LASTCHANCE. */
	private static final Integer LASTCHANCE = 0;

	/** The Constant LIMITEDOFFER. */
	private static final Integer LIMITEDOFFER = 2;

	/** The Constant ONLINEONLY. */
	private static final Integer ONLINEONLY = 3;

	/** The Constant AGREEDINSTORE. */
	private static final Integer AGREEDINSTORE = 1;

	/** The Constant NOWAVAILABLE. */
	private static final Integer NOWAVAILABLE = 4;


	private static final Integer BONUSOFFER = 7;

	private static final Integer DISCOUNTOFFER = 8;



	/** The delivery date cut off service. */
	@Resource(name = "sabmDeliveryDateCutOffService")
	private SABMDeliveryDateCutOffService deliveryDateCutOffService;

	/** The session service. */
	@Resource(name = "sessionService")
	private SessionService sessionService;

	/** The b2b commerce unit service. */
	@Resource(name = "b2bCommerceUnitService")
	private B2BCommerceUnitService b2bCommerceUnitService;

	/** The user service. */
	@Resource(name = "userService")
	private UserService userService;

	/** The cart service. */
	@Resource(name = "cartService")
	private SABMCartService cartService;

	@Resource(name = "dealValidationStrategy")
	private SABMDealValidationStrategy dealValidationStrategy;


	/*
	 * (non-Javadoc)
	 *
	 * @see de.hybris.platform.converters.Populator#populate(java.lang.Object, java.lang.Object)
	 */
	@Override
	public void populate(final List<DealModel> source, final DealJson target) throws ConversionException
	{
		Assert.notNull(source, "Parameter source cannot be null.");
		Assert.notNull(target, "Parameter target cannot be null.");
		Assert.notEmpty(source, "Parameter source cannot be empty.");

		LOG.debug("Populating deal: [{}]", source);

		final DealModel deal = source.get(0);
		if (deal.getValidFrom() != null)
		{
			target.setValidFrom(DateUtils.truncate(deal.getValidFrom(), Calendar.DATE).getTime());
		}

		if (deal.getValidTo() != null)
		{
			target.setValidTo(DateUtils.truncate(deal.getValidTo(), Calendar.DATE).getTime());
		}
		populateActive(deal, target);
		populateBadges(deal, target);
		populateLimitedInfo(deal, target);

		target.setCode(deal.getCode());
	}

	/**
	 * Convert Rep-Driven Deal state, when RepDrivenDealStatus.UNLOCKED then active=true
	 *
	 * @param deal
	 *           the deal
	 * @param target
	 *           the target
	 */
	protected void populateActive(final DealModel deal, final DealJson target)
	{
		final RepDrivenDealConditionStatusModel conditionStatus = deal.getRepDrivenDealStatus();
		if (null != conditionStatus && RepDrivenDealStatus.UNLOCKED.equals(conditionStatus.getStatus()))
		{
			target.setActive(Boolean.TRUE.booleanValue());
		}
	}

	/**
	 * Covert deals badges.
	 *
	 * @param source
	 *           the source
	 * @param target
	 *           the target
	 */
	protected void populateBadges(final DealModel source, final DealJson target)
	{
		//populate last chance badge, just the B2BCustomer need convert the last chance
		if (userService.getCurrentUser() instanceof B2BCustomerModel)
		{
			populateLastChanceBadge(source, target);
		}

		//populate agreed instore badge
		if (Boolean.TRUE.equals(source.getInStore()))
		{
			setBadges(target, AGREEDINSTORE);
		}

		//populate online only badge
		if ("B2B".equalsIgnoreCase(source.getCustomerPOType()))
		{
			setBadges(target, ONLINEONLY);
		}

		//populate limited offer badge
		if (DealTypeEnum.LIMITED.equals(source.getDealType()))
		{
			setBadges(target, LIMITEDOFFER);
		}

		populateNowAvailableBadge(source, target);

		if (source != null && source.getConditionGroup() != null && source.getConditionGroup().getDealBenefits() != null)
		{
			for (final AbstractDealBenefitModel benefit : source.getConditionGroup().getDealBenefits())
			{
				if (benefit instanceof FreeGoodsDealBenefitModel)
				{
					setBadges(target, BONUSOFFER);
				}
				if (benefit instanceof DiscountDealBenefitModel)
				{
					setBadges(target, DISCOUNTOFFER);
				}
			}
		}

	}



	/**
	 * populate now available badge
	 *
	 * @param source
	 * @param target
	 */
	private void populateNowAvailableBadge(final DealModel source, final DealJson target)
	{
		if (dealValidationStrategy.validateNowAvailableDeal(source))
		{
			setBadges(target, NOWAVAILABLE);
		}
	}

	/**
	 * Populate limited info.
	 *
	 * @param deal
	 *           the deal
	 * @param target
	 *           the target
	 */
	protected void populateLimitedInfo(final DealModel deal, final DealJson target)
	{
		if (deal != null && DealTypeEnum.LIMITED.equals(deal.getDealType()))
		{
			final Map<String, String> map = Maps.newHashMap();
			final Map<String, String> mapValues = Maps.newHashMap();
			final Map<String, String> mapDisc = Maps.newHashMap();

			for (final AbstractDealBenefitModel benefit : deal.getConditionGroup().getDealBenefits())
			{
				if (benefit instanceof DiscountDealBenefitModel)
				{
					populateConditionMap(deal, null, map, mapValues, null);
					populateDiscountMap((DiscountDealBenefitModel) benefit, mapDisc, mapValues);

					break;
				}
			}

			int conditionQty = 0;

			for (final AbstractDealConditionModel abstractCondition : deal.getConditionGroup().getDealConditions())
			{
				if (BooleanUtils.isNotTrue(abstractCondition.getExclude()))
				{
					if (abstractCondition instanceof ProductDealConditionModel)
					{
						final ProductDealConditionModel condition = (ProductDealConditionModel) abstractCondition;
						conditionQty = condition.getMinQty() != null ? condition.getMinQty()
								: condition.getQuantity() != null ? condition.getQuantity() : 0;
					}
					else
					{
						LOG.debug("A limited deal [{}] cannot be populated with hierarchy, only material code", deal);
					}
				}
			}

			int qty = 0;
			if (cartService.hasSessionCart())
			{
				final CartModel cart = cartService.getSessionCart();

				for (final AbstractOrderEntryModel entry : CollectionUtils.emptyIfNull(cart.getEntries()))
				{
					if (BooleanUtils.isNotTrue(entry.getIsFreeGood())
							&& getDealsService().productBelongsToDeal(deal, (SABMAlcoholVariantProductMaterialModel) entry.getProduct()))
					{
						qty += entry.getQuantity();
					}
				}
			}
			if (NumberUtils.isNumber(mapDisc.get(MAP_KEY_AMOUNT)))
			{
				if (isLimitedQuantityCurrency(deal))
				{
					if (conditionQty <= qty)
					{
						final BigDecimal amount = BigDecimal.valueOf(Double.valueOf(mapDisc.get(MAP_KEY_AMOUNT)))
								.multiply(BigDecimal.valueOf(qty));
						target.setRemainingValue(BigDecimal.valueOf(getMaxQuantity(deal)).subtract(amount).doubleValue());
					}
					else
					{
						target.setRemainingValue(BigDecimal.valueOf(getMaxQuantity(deal)).doubleValue());
					}
				}
				else
				{
					if (conditionQty <= qty)
					{
						target.setRemainingQty(Long.valueOf(getMaxQuantity(deal) - qty));
					}
					else
					{
						target.setRemainingQty(Long.valueOf(getMaxQuantity(deal)));
					}
				}
			}
		}
	}

	/**
	 * set last chance badge.
	 *
	 * @param source
	 *           the source
	 * @param target
	 *           the target
	 */
	protected void populateLastChanceBadge(final DealModel source, final DealJson target)
	{


		final Date deliveryDate = sessionService.getAttribute(SabmCoreConstants.SESSION_ATTR_DELIVERY_DATE);
		if (deliveryDate == null)
		{
			LOG.debug("Picked delivery date is null", b2bCommerceUnitService.getParentUnit());
			return;
		}

		boolean lastChance = false;

		final Calendar validTo = Calendar.getInstance();
		validTo.setTime(source.getValidTo());
		final Calendar pickedDeliveryDate = Calendar.getInstance();
		pickedDeliveryDate.setTime(deliveryDate);

		/*
		 * If the picked delivery date is the "valid to" date. Then this deal is the "Last Chance".
		 *
		 * Else It means:
		 *
		 * 1. The "valid to" date is out of range.
		 *
		 * 2. The "valid to" date is another enabled delivery date which isn't the "Last Chance"
		 *
		 * 3. The "valid to" date is in the range and it must in the disabled delivery dates.
		 */
		if (pickedDeliveryDate.get(Calendar.YEAR) == validTo.get(Calendar.YEAR)
				&& pickedDeliveryDate.get(Calendar.MONTH) == validTo.get(Calendar.MONTH)
				&& pickedDeliveryDate.get(Calendar.DAY_OF_MONTH) == validTo.get(Calendar.DAY_OF_MONTH))
		{
			lastChance = true;
		}
		else
		{
			// Get the nearest enabled delivery date
			final Calendar nearestEnabledDeliveryDate = getNearestEnabledDeliveryDate(validTo);
			// If there has a nearest enabled delivery date
			if (nearestEnabledDeliveryDate != null)
			{
				// If the nearest enabled delivery date is the picked delivery date
				if (nearestEnabledDeliveryDate.get(Calendar.YEAR) == pickedDeliveryDate.get(Calendar.YEAR)
						&& nearestEnabledDeliveryDate.get(Calendar.MONTH) == pickedDeliveryDate.get(Calendar.MONTH)
						&& nearestEnabledDeliveryDate.get(Calendar.DAY_OF_MONTH) == pickedDeliveryDate.get(Calendar.DAY_OF_MONTH))
				{
					lastChance = true;
				}
			}
		}
		if (lastChance)
		{
			setBadges(target, LASTCHANCE);
		}

	}


	/**
	 * Get the nearest enabled delivery date.
	 *
	 * @param validTo
	 *           the valid to
	 * @return The nearest enabled delivery date or null if hasn't.
	 */
	protected Calendar getNearestEnabledDeliveryDate(final Calendar validTo)
	{
		// Get enabled delivery dates.
		final Set<Date> enabledCalendarDates = deliveryDateCutOffService.enabledCalendarDates();

		if (CollectionUtils.isEmpty(enabledCalendarDates))
		{
			LOG.debug("Unable to set the LastChance flag with empty enabled Caledar Dates", b2bCommerceUnitService.getParentUnit());
			return null;
		}

		// Get disable delivery dates.
		final Set<Date> disabledCalendarDates = deliveryDateCutOffService.disabledCalendarDates();

		// Iterate all the disabled delivery dates, if the "valid to" is one of these disabled dates then the valid to is qualified.
		boolean qualified = false;
		for (final Date disabledDate : disabledCalendarDates)
		{
			final Calendar disabledCalendar = Calendar.getInstance();
			disabledCalendar.setTime(disabledDate);

			if (disabledCalendar.get(Calendar.YEAR) == validTo.get(Calendar.YEAR)
					&& disabledCalendar.get(Calendar.MONTH) == validTo.get(Calendar.MONTH)
					&& disabledCalendar.get(Calendar.DAY_OF_MONTH) == validTo.get(Calendar.DAY_OF_MONTH))
			{
				qualified = true;
				break;
			}
		}

		if (!qualified)
		{
			return null;
		}

		// If the "valid to" is qualified
		final List<Date> enabledDates = new ArrayList<>(enabledCalendarDates);
		Collections.sort(enabledDates);

		/*
		 * Try to find the nearest enabled date before the "valid to"
		 *
		 * There's one scenario we cannot find the nearest enabled day before "valid to": There has disabled delivery
		 * day(s) before the first enabled delivery date in current calendar range, and, the "valid to" i.e disabled
		 * delivery date is one of these days.
		 */
		for (int i = enabledDates.size() - 1; i >= 0; i--)
		{
			final Date enabledDate = enabledDates.get(i);
			if (enabledDate.before(validTo.getTime()))
			{
				final Calendar nearestEnabledDeliveryDate = Calendar.getInstance();
				nearestEnabledDeliveryDate.setTime(enabledDate);

				return nearestEnabledDeliveryDate;
			}
		}

		return null;
	}

}
