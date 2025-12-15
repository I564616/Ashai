/**
 *
 */
package com.sabmiller.core.b2b.services.impl;

import de.hybris.platform.acceleratorservices.config.SiteConfigService;
import de.hybris.platform.b2b.company.B2BCommerceUnitService;
import de.hybris.platform.b2b.model.B2BUnitModel;
import de.hybris.platform.core.model.order.CartModel;
import de.hybris.platform.servicelayer.internal.dao.GenericDao;
import de.hybris.platform.servicelayer.session.SessionService;
import de.hybris.platform.store.BaseStoreModel;
import de.hybris.platform.store.services.BaseStoreService;
import de.hybris.platform.util.Config;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Collections;
import java.util.Date;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.Set;
import java.util.TimeZone;
import java.util.stream.Collectors;

import jakarta.annotation.Resource;

import org.apache.commons.collections4.CollectionUtils;
import org.apache.commons.collections4.ListUtils;
import org.apache.commons.collections4.MapUtils;
import org.apache.commons.collections4.SetUtils;
import org.apache.commons.lang3.BooleanUtils;
import org.apache.commons.lang3.StringUtils;
import org.apache.commons.lang3.time.DateUtils;
import org.joda.time.DateTimeComparator;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.cache.annotation.Cacheable;

import com.sabmiller.core.b2b.services.SABMDeliveryDateCutOffService;
import com.sabmiller.core.b2b.services.SabmB2BUnitService;
import com.sabmiller.core.cart.service.SABMCartService;
import com.sabmiller.core.constants.SabmCoreConstants;
import com.sabmiller.core.enums.DeliveryModeType;
import com.sabmiller.core.enums.PackType;
import com.sabmiller.core.model.AdditionalDeliveryDayModel;
import com.sabmiller.core.model.PlantCutOffModel;
import com.sabmiller.core.model.PlantDeliveryDayModel;
import com.sabmiller.core.model.PlantModel;
import com.sabmiller.core.model.PublicHolidayModel;
import com.sabmiller.core.model.SabmDeliveryModeMappingModel;
import com.sabmiller.core.model.ShippingCarrierModel;
import com.sabmiller.core.model.UnloadingPointModel;
import com.sabmiller.core.util.SabmDateUtils;
import com.sabmiller.facades.delivery.data.DeliveryModePackTypeDeliveryDatesData;


/**
 * The Class DefaultSABMDeliveryDateCutOffService.
 */
public class DefaultSABMDeliveryDateCutOffService implements SABMDeliveryDateCutOffService
{

	/** The Constant LOG. */
	private static final Logger LOG = LoggerFactory.getLogger(DefaultSABMDeliveryDateCutOffService.class);

	/** The b2b commerce unit service. */
	@Resource(name = "b2bCommerceUnitService")
	private B2BCommerceUnitService b2bCommerceUnitService;

	@Resource(name = "baseStoreService")
	private BaseStoreService baseStoreService;

	@Resource(name = "cartService")
	private SABMCartService sabmCartService;

	@Resource(name = "siteConfigService")
	private SiteConfigService siteConfigService;

	@Resource(name = "sessionService")
	private SessionService sessionService;

	@Resource(name = "sabmDeliveryMethodDao")
	private GenericDao<SabmDeliveryModeMappingModel> genericDao;

	@Resource(name = "b2bUnitService")
	private SabmB2BUnitService b2bUnitService;


	/** The parser. */
	private final SimpleDateFormat parser = new SimpleDateFormat(Config.getString("plant.cutoff.time.pattern", "HHmm"));

	/** The server time delta. */
	@Value(value = "${server.cutoff.time.delta:600000}")
	private long serverTimeDelta;

	/** The max day check. */
	@Value(value = "${max.further.day.check:30}")
	private int maxDayCheck;

	/** The max day check. */
	@Value(value = "${max.further.day.calendar:14}")
	private int maxDayCalendar;

	/** The caldendar delivery day map. */
	@Resource(name = "caldendarDeliveryDayMap")
	private Map<Integer, Integer> caldendarDeliveryDayMap;

	private final int DAYS_IN_YEAR = 365;

	private final String CODES_PATTERN = "([BULK]|[PACK])+-\\w+-\\d+";

	private final String AT = "AT";

	/**
	 * Checks if is cut off time exceeded.
	 *
	 * @return true, if is cut off time exceeded
	 */
	@Override
	public boolean isCutOffTimeExceeded()
	{
		return isCutOffTimeExceeded(b2bCommerceUnitService.getParentUnit());
	}

	/*
	 * (non-Javadoc)
	 *
	 * @see com.sabmiller.core.b2b.services.SABMDeliveryDateCutOffService#isCutOffTimeExceeded(java.lang.String)
	 */
	@Override
	public boolean isCutOffTimeExceeded(final B2BUnitModel b2bUnitModel)
	{
		if (b2bUnitModel == null)
		{
			LOG.error("Unable to check cutoff time for null b2bUnit");
			return true;
		}

		final TimeZone serverTimeZone = Calendar.getInstance().getTimeZone();

		final Calendar todayCal = Calendar.getInstance();
		final Date now = new Date();
		final long serverTimeMillis = now.getTime() - serverTimeZone.getOffset(now.getTime()) - serverTimeDelta;

		todayCal.setTimeInMillis(serverTimeMillis);

		final PlantModel plant = b2bUnitModel.getPlant();

		if (plant == null)
		{
			LOG.error("No Plant found for b2bUnit {}", b2bUnitModel);
			return true;
		}

		final PlantCutOffModel plantCutOffForTZ = getPlantCutOffByDayOfWeek(b2bUnitModel, todayCal.get(Calendar.DAY_OF_WEEK));
		//If there is no plant cutoff for the specific day of week, return true (CutOff Time Exceeded)
		if (plantCutOffForTZ == null)
		{
			LOG.warn("Missing PlantCutOffModel for Plant: {} and dayOfWeek: {}", plant, todayCal.get(Calendar.DAY_OF_WEEK));
			return true;
		}

		TimeZone storeTimeZone = null;
		if (plantCutOffForTZ.getTimeZone() == null)
		{
			storeTimeZone = TimeZone.getDefault();
		}
		else
		{
			final String AestTimeZone =Config.getString("plant.cutoff.timeZones.toStoreTimeZone", "");
			if(StringUtils.isNotEmpty(AestTimeZone) && AestTimeZone.contains(plantCutOffForTZ.getTimeZone().getCode())){
				final BaseStoreModel baseStoreTimeZone = baseStoreService.getBaseStoreForUid("sabmStore");
				//Getting BaseStore timezone
				if (baseStoreTimeZone != null && baseStoreTimeZone.getTimeZone() != null)
				{
					storeTimeZone = TimeZone.getTimeZone(baseStoreTimeZone.getTimeZone().getCode());
				}
			}else{
				storeTimeZone = TimeZone.getTimeZone(plantCutOffForTZ.getTimeZone().getCode());
			}
		}

		final Calendar storeCal = Calendar.getInstance();
		storeCal.setTimeInMillis(todayCal.getTimeInMillis() + storeTimeZone.getOffset(now.getTime()));

		LOG.debug("Server time in base store timezone: [{}]", storeCal.getTime());

		final int dayOfWeek = storeCal.get(Calendar.DAY_OF_WEEK);

		final PlantCutOffModel plantCutOff = getPlantCutOffByDayOfWeek(b2bUnitModel, dayOfWeek);

		//If there is no plant cutoff for the specific day of week, return true (CutOff Time Exceeded)
		if (plantCutOff == null)
		{
			LOG.warn("Missing PlantCutOffModel for Plant: {} and dayOfWeek: {}", plant, dayOfWeek);
			return true;
		}

		if (StringUtils.isEmpty(plantCutOff.getCutOffTime()))
		{
			LOG.warn("Missing cutOffTime for Plant: {} and dayOfWeek: {}", plant, dayOfWeek);
			return true;
		}

		try
		{
			TimeZone plantTimeZone = null;
			//If no timezone set in the plantcutoff, return the default GMT
			if (plantCutOff.getTimeZone() == null)
			{
				plantTimeZone = TimeZone.getDefault();
			}
			else
			{
				final String AestTimeZone =Config.getString("plant.cutoff.timeZones.toStoreTimeZone", "");
				if(StringUtils.isNotEmpty(AestTimeZone) && AestTimeZone.contains(plantCutOff.getTimeZone().getCode())){
					final BaseStoreModel baseStoreTimeZone = baseStoreService.getBaseStoreForUid("sabmStore");
					//Getting BaseStore timezone
					if (baseStoreTimeZone != null && baseStoreTimeZone.getTimeZone() != null)
					{
						plantTimeZone = TimeZone.getTimeZone(baseStoreTimeZone.getTimeZone().getCode());
					}
				}else{
					plantTimeZone = TimeZone.getTimeZone(plantCutOff.getTimeZone().getCode());
				}
			}

			final Date cutoffDate = parser.parse(plantCutOff.getCutOffTime());

			final Calendar plantCutoffCal = getPlantCutoffCal(cutoffDate, storeCal.getTime(), plantTimeZone);

			//If server time is before plantCutofftime means that it's valid. Return false because CutOff Time is not Exceeded
			final boolean isValidCutOff = plantCutoffCal.after(todayCal);

			LOG.debug("cut off time in cutoffTimeExceed: [{}]", plantCutoffCal.getTime());
			LOG.debug("cutOffTimeExceeded: [{}], for b2bUnit: [{}] and date: [{}]", !isValidCutOff, b2bUnitModel,
					todayCal.getTime());

			return !isValidCutOff;
		}
		catch (final ParseException e)
		{
			LOG.warn("Error parsing cutoff time for model: " + plantCutOff, e);
			return true;
		}
	}

	/**
	 * Checks if is valid delivery date.
	 *
	 * @param checkDate
	 *           the check date
	 * @return true, if is valid delivery date
	 */
	@Override
	public boolean isValidDeliveryDate(final Date checkDate)
	{
		final B2BUnitModel b2bUnit = b2bCommerceUnitService.getParentUnit();

		return isValidDeliveryDate(b2bUnit, checkDate);
	}

	/**
	 * Checks if is valid delivery date.
	 *
	 * @param b2bUnit
	 *           the b2b unit
	 * @param checkDate
	 *           the check date
	 * @return true, if is valid delivery date
	 */
	@Override
	public boolean isValidDeliveryDate(final B2BUnitModel b2bUnit, final Date checkDate)
	{
		if (checkDate == null)
		{
			LOG.error("Null date is not valid for delivery! (Customer [{}])", b2bUnit);
			return false;
		}

		if (b2bUnit == null)
		{
			LOG.error("Impossible to check delivery date for a null customer!");
			return false;
		}

		final Set<Date> enabledCalendarDates = enabledCalendarDates(b2bUnit);

		if (CollectionUtils.isEmpty(enabledCalendarDates))
		{
			LOG.error("There are no available delivery date for the customer [{}]", b2bUnit);
			return false;
		}

		//Truncating the time part of the date
		final Date truncatedDate = DateUtils.truncate(checkDate, Calendar.DATE);

		LOG.debug("The date: [{}] is valid: [{}] for the b2bUnit: [{}]", checkDate, enabledCalendarDates.contains(truncatedDate),
				b2bUnit);

		return enabledCalendarDates.contains(truncatedDate);
	}


	@Override
	public Date getSafeNextAvailableDeliveryDate()
	{
		final B2BUnitModel b2bUnit = b2bCommerceUnitService.getParentUnit();
		return getSafeNextAvailableDeliveryDate(b2bUnit);
	}

	/**
	 * Gets the safe next available delivery date.
	 *
	 * @return the safe next available delivery date
	 */
	/*
	 * (non-Javadoc)
	 *
	 * @see com.sabmiller.core.b2b.services.SABMDeliveryDateCutOffService#getSafeNextAvailableDeliveryDate()
	 */
	@Override
	public Date getSafeNextAvailableDeliveryDate(final B2BUnitModel b2bunit)
	{
		Date deliveryDay = null;
		final Set<Date> enabledCalendarDates = enabledCalendarDates(b2bunit);

		if (CollectionUtils.isNotEmpty(enabledCalendarDates))
		{
			final List<Date> sortedList = new ArrayList<>(enabledCalendarDates);
			Collections.sort(sortedList);

			deliveryDay = sortedList.get(0);
		}
		else
		{
			final Calendar cal = Calendar.getInstance();
			cal.setTime(new Date());
			cal.add(Calendar.DAY_OF_YEAR, 1);

			deliveryDay = cal.getTime();
		}

		return SabmDateUtils.getOnlyDate(deliveryDay);
	}

	/**
	 * Gets the plant cut off lead by day of week.
	 *
	 * @param b2bUnit
	 *           the b2b unit
	 * @param dayOfWeek
	 *           the day of week
	 * @return the plant cut off lead by day of week
	 */
	protected int getPlantCutOffLeadByDayOfWeek(final B2BUnitModel b2bUnit, final int dayOfWeek)
	{
		int leadTime = 0;

		final PlantCutOffModel plantCutOff = getPlantCutOffByDayOfWeek(b2bUnit, dayOfWeek);

		if (plantCutOff != null && plantCutOff.getLeadTime() != null)
		{
			leadTime = plantCutOff.getLeadTime();
		}

		return leadTime;
	}

	/**
	 * Gets the plant cut off lead by day of week.
	 *
	 * @param dayOfWeek
	 *           the day of week
	 * @return the plant cut off lead by day of week
	 */
	protected int getPlantCutOffLeadByDayOfWeek(final int dayOfWeek)
	{
		return getPlantCutOffLeadByDayOfWeek(b2bCommerceUnitService.getParentUnit(), dayOfWeek);
	}


	/**
	 * Disabled calendar dates.
	 *
	 * @return the sets the
	 */
	/*
	 * (non-Javadoc)
	 *
	 * @see com.sabmiller.core.b2b.services.SABMDeliveryDateCutOffService#availableCalendarDates()
	 */
	@Override
	public Set<Date> disabledCalendarDates()
	{
		final B2BUnitModel b2bUnit = b2bCommerceUnitService.getParentUnit();

		return disabledCalendarDates(b2bUnit);
	}

	/**
	 * Disabled calendar dates.
	 *
	 * @param b2bUnit
	 *           the b2b unit
	 * @return the sets the
	 */
	/*
	 * (non-Javadoc)
	 *
	 * @see
	 * com.sabmiller.core.b2b.services.SABMDeliveryDateCutOffService#disabledCalendarDates(de.hybris.platform.b2b.model.
	 * B2BUnitModel)
	 */
	@Override
	public Set<Date> disabledCalendarDates(final B2BUnitModel b2bUnit)
	{
		return calculateCalendarDeliveryDates(b2bUnit, false);
	}

	/**
	 * Enabled calendar dates.
	 *
	 * @return the sets the
	 */
	/*
	 * (non-Javadoc)
	 *
	 * @see com.sabmiller.core.b2b.services.SABMDeliveryDateCutOffService#enabledCalendarDates()
	 */
	@Override
	public Set<Date> enabledCalendarDates()
	{

		B2BUnitModel b2bUnit = null;

		try
		{
			b2bUnit = b2bCommerceUnitService.getParentUnit();
		}
		catch (final Exception e)
		{
			LOG.error("unable to get the b2bunit");
		}
		if (b2bUnit == null)
		{
			b2bUnit = sessionService.getAttribute(SabmCoreConstants.SESSION_ATTR_B2B_UNIT);
		}

		return enabledCalendarDates(b2bUnit);
	}


	/**
	 * Enabled calendar dates.
	 *
	 * @param b2bUnit
	 *           the b2b unit
	 * @return the sets the
	 */
	/*
	 * (non-Javadoc)
	 *
	 * @see
	 * com.sabmiller.core.b2b.services.SABMDeliveryDateCutOffService#enabledCalendarDates(de.hybris.platform.b2b.model.
	 * B2BUnitModel)
	 */
	@Override
	@Cacheable(value = "calendarDates", key = "T(com.sabmiller.cache.impl.SabmCacheKeyGenerator).generateKey(false,false,false,'dates',#b2bUnit.uid)")
	public Set<Date> enabledCalendarDates(final B2BUnitModel b2bUnit)
	{
		return calculateCalendarDeliveryDates(b2bUnit, true);
	}

	/**
	 * Calculate calendar delivery dates.
	 *
	 * @param b2bUnit
	 *           the b2b unit
	 * @param enabled
	 *           if true, the method returns the enabled dates, otherwise the disabled ones.
	 * @return the list
	 */
	protected Set<Date> calculateCalendarDeliveryDates(B2BUnitModel b2bUnit, final boolean enabled)
	{

		if (b2bUnit == null)
		{
			LOG.error("Unable to get available calendar days for a null b2bUnit");
			return Collections.emptySet();
		}
		final B2BUnitModel b2bUnitForAlternativeAddress = getUnitBasedForAlternativeAddress();
		b2bUnit = null != b2bUnitForAlternativeAddress ? b2bUnitForAlternativeAddress : b2bUnit;

		final DeliveryModeType mode = getDefaultDeliveryModeType(b2bUnit);
		final List<DeliveryModePackTypeDeliveryDatesData> deliveryDatesData = this.getDeliveryModePackTypeDeliveryDatesData(b2bUnit,
				enabled);

		List<Long> dates = deliveryDatesData.stream().filter(data -> mode.equals(data.getMode()))
				.map(DeliveryModePackTypeDeliveryDatesData::getDateList).flatMap(List::stream).collect(Collectors.toList());

		//fallback if no dates for the specified delivery mode - get dates from the other delivery mode
		if (dates.isEmpty())
		{
			dates = deliveryDatesData.stream().map(DeliveryModePackTypeDeliveryDatesData::getDateList).flatMap(List::stream)
					.collect(Collectors.toList());
		}

		final Set<Date> timestamps = new HashSet<>();

		if (CollectionUtils.isNotEmpty(dates))
		{
			for (final Long date : dates)
			{
				timestamps.add(new Date(date));
			}
		}
		return timestamps;

	}

	private B2BUnitModel getUnitBasedForAlternativeAddress()
	{
		B2BUnitModel b2bUnitModel = null;
		if (sabmCartService.hasSessionCart())
		{
			final CartModel cartModel = sabmCartService.getSessionCart();
			if (null != cartModel && null != cartModel.getDeliveryAddress()
					&& null != cartModel.getDeliveryAddress().getPartnerNumber())
			{
				b2bUnitModel = b2bUnitService.getUnitForUid(cartModel.getDeliveryAddress().getPartnerNumber());
			}
		}
		return b2bUnitModel;
	}

	private Set<Date> getUnloadingPointsDates(final B2BUnitModel b2bUnit, final boolean enabled,
			final UnloadingPointModel unloadingPoint)
	{
		final Set<Date> dateList = new HashSet<>();
		if (unloadingPoint != null && MapUtils.isNotEmpty(unloadingPoint.getMap()))
		{
			final Map<String, Set<String>> map = unloadingPoint.getMap();

			final BaseStoreModel baseStore = baseStoreService.getBaseStoreForUid("sabmStore");


			TimeZone storeTimeZone = null;

			//Getting BaseStore timezone
			if (baseStore != null && baseStore.getTimeZone() != null)
			{
				storeTimeZone = TimeZone.getTimeZone(baseStore.getTimeZone().getCode());
			}
			final Date now = new Date();
			Date storeTime = null;

			if (storeTimeZone != null)
			{
				final TimeZone serverTimeZone = Calendar.getInstance().getTimeZone();
				storeTime = new Date(
						now.getTime() - serverTimeZone.getOffset(now.getTime()) + storeTimeZone.getOffset(now.getTime()));

				LOG.debug("Server time in base store timezone: [{}]", storeTime);
			}
			else
			{
				storeTime = now;
			}

			//############ Changes as per Incident: "INC0520806-Customer - 851813 Order had incorrect RDD" Fix ###########
			final Date plantcutoffstoreTime = getplantcutoffstoreTime(b2bUnit, baseStore);
			if (plantcutoffstoreTime != null)
			{
				storeTime = plantcutoffstoreTime;
			}
			//############ Ended Incident: "INC0520806" fix ##################################

			final Calendar dayDelivery = Calendar.getInstance();
			dayDelivery.setTime(DateUtils.truncate(storeTime, Calendar.DATE));
			final int todayDayOfYear = dayDelivery.get(Calendar.DAY_OF_YEAR);
			final int todayYear = dayDelivery.get(Calendar.YEAR);


			List<PublicHolidayModel> publicHolidays = null;
			List<PlantDeliveryDayModel> deliveryDays = null;
			final Set<Date> additionalDeliveryDates = getAdditinalDeliveryDates(b2bUnit);
			final Set<Date> excludedDeliveryDates = CollectionUtils.isNotEmpty(b2bUnit.getExcludedDeliveryDates())? b2bUnit.getExcludedDeliveryDates().stream()
					.filter(date -> null != date && null != date.getCalendarDay())
					.map(date -> DateUtils.truncate(date.getCalendarDay(), Calendar.DATE)).collect(Collectors.toSet())
					: Collections.emptySet();
			if (b2bUnit.getPlant() != null)
			{
				deliveryDays = b2bUnit.getPlant().getDeliveryDays();

				if (b2bUnit.getPlant().getHolidayCalendar() != null)
				{
					publicHolidays = b2bUnit.getPlant().getHolidayCalendar().getPublicHolidays();
				}
			}

			if (!enabled)
			{
				dateList.add(dayDelivery.getTime());
				if (CollectionUtils.isNotEmpty(excludedDeliveryDates))
				{
					dateList.addAll(excludedDeliveryDates);
				}
			}

			if (isCutOffTimeExceeded(b2bUnit))
			{
				dayDelivery.add(Calendar.DAY_OF_YEAR, 1);
			}

			final int leadTime = getPlantCutOffLeadByDayOfWeek(b2bUnit, dayDelivery.get(Calendar.DAY_OF_WEEK));
			for (int i = 0; i < leadTime; i++)
			{
				if (!enabled)
				{
					dateList.add(dayDelivery.getTime());
				}

				dayDelivery.add(Calendar.DAY_OF_YEAR, 1);
			}

			LOG.debug("Checking date [{}] compliant for customer [{}]", dayDelivery, b2bUnit);

			while (dayDelivery.get(Calendar.DAY_OF_YEAR) + ((dayDelivery.get(Calendar.YEAR) - todayYear) * DAYS_IN_YEAR)
					- todayDayOfYear <= maxDayCalendar)
			{
				LOG.debug("Check if day delivery ");
				if (!checkDateCompliant(map, deliveryDays, publicHolidays, dayDelivery, additionalDeliveryDates) ^ enabled)
				{
					LOG.debug("checkDateCompliant", map, additionalDeliveryDates);
					dateList.add(dayDelivery.getTime());
				}

				dayDelivery.add(Calendar.DAY_OF_YEAR, 1);
			}
			if (enabled && CollectionUtils.isNotEmpty(dateList) && CollectionUtils.isNotEmpty(excludedDeliveryDates))
			{
				dateList.removeAll(excludedDeliveryDates);
			}
		}
		else
		{
			LOG.error("Unable to get available calendar days for b2bUnit: {} with null unloading point", b2bUnit);
		}
		return dateList;
	}

	/**
	 * This will check and return boolean if the date is excluded
	 *
	 * @param date
	 * @param excludedDeliveryDates
	 * @return boolean true if the dates are same else false.
	 */
	protected boolean isSameDate(final Date date, final Set<Date> excludedDeliveryDates )
	{
		final DateTimeComparator dateTimeComparator = DateTimeComparator.getDateOnlyInstance();
		final Iterator<Date> iterator = excludedDeliveryDates.iterator();
		if (iterator.hasNext())
		{
			if (0 == dateTimeComparator.compare(date, iterator.next()))
			{
				return true;
			}
		}
		return false;
	}

	/**
	 * Processes the b2b unloading points and converts to DeliveryModePackTypeDeliveryDatesData objects based on its
	 * codes
	 *
	 * @param b2bUnit
	 * @return List of DeliveryModePackTypeDeliveryDatesData (CubArranged:Pack and Keg, CustomerArranged: Pack and Keg)
	 */
	@Override
	public List<DeliveryModePackTypeDeliveryDatesData> getDeliveryModePackTypeDeliveryDatesData(final B2BUnitModel b2bUnit,
			final boolean enabled)
	{
		final List<DeliveryModePackTypeDeliveryDatesData> deliveryModePackTypeDeliveryDatesData = new ArrayList<>();

		final Map<Integer, DeliveryModeType> deliveryModeMapping = getDeliveryModeMappings();

		for (final UnloadingPointModel unloadingPoint : ListUtils.emptyIfNull(b2bUnit.getUnloadingPoints()))
		{
			if (unloadingPoint != null)
			{
				if (StringUtils.isNotEmpty(unloadingPoint.getCode()) && unloadingPoint.getCode().matches(CODES_PATTERN))
				{
					//Derive the Delivery Mode from unloading point code format : PACKTYPE-DAYS-MODE
					final String[] codes = unloadingPoint.getCode().split("-");

					final int mode = Integer.parseInt(codes[2]);
					final DeliveryModeType method = deliveryModeMapping.get(mode);
					if (method != null)
					{
						final PackType packType = codes[0].equals(PackType.PACK.getCode()) ? PackType.PACK : PackType.KEG;

						final DeliveryModePackTypeDeliveryDatesData deliveryModePackTypeRecord = getDeliveryModePackTypeDeliveryDateRecord(
								method, packType, deliveryModePackTypeDeliveryDatesData);

						final Set<Date> unloadingPointDates = getUnloadingPointsDates(b2bUnit, enabled, unloadingPoint);
						LOG.debug("code: {} unloadingPointDates: {}", unloadingPoint.getCode(), unloadingPointDates);

						final List<Long> existingDateList = deliveryModePackTypeRecord.getDateList();

						if (existingDateList != null)
						{
							existingDateList.addAll(getDeliveryDates(unloadingPointDates));
						}
						else
						{
							deliveryModePackTypeRecord.setDateList(getDeliveryDates(unloadingPointDates));
						}
					}
				}
				else
				{
					LOG.error("Unable to get available calendar days for b2bUnit: {} with incorrect unloading point code {}", b2bUnit,
							unloadingPoint.getCode());
				}
			}
		}

		//process pack&keg dateList
		for (final DeliveryModeType mode : DeliveryModeType.values())
		{
			getKegAndPackDatesData(mode, deliveryModePackTypeDeliveryDatesData);
		}

		return deliveryModePackTypeDeliveryDatesData;
	}


	public List<Long> getDeliveryDates(final Set<Date> dates)
	{
		final List<Long> timestamps = new ArrayList<>();

		if (CollectionUtils.isNotEmpty(dates))
		{
			for (final Date date : dates)
			{
				timestamps.add(date.getTime());
			}
		}

		return timestamps;
	}

	/**
	 * Create data record for Pack&Keg types based on merge of pack dates and keg dates
	 *
	 * @param mode
	 * @param deliveryModePackTypeDeliveryDatesData
	 */
	private void getKegAndPackDatesData(final DeliveryModeType mode,
			final List<DeliveryModePackTypeDeliveryDatesData> deliveryModePackTypeDeliveryDatesData)
	{
		final List<DeliveryModePackTypeDeliveryDatesData> deliveryDatesData = deliveryModePackTypeDeliveryDatesData.stream()
				.filter(data -> mode.equals(data.getMode())).collect(Collectors.toList());

		if (CollectionUtils.size(deliveryDatesData) == 2)
		{
			final List<Long> packOnlyDates = deliveryDatesData.get(0).getDateList();
			final List<Long> kegOnlyDates = deliveryDatesData.get(1).getDateList();

			//Merge dates from pack dates and keg dates
			final List<Long> packKegDates = new ArrayList<>(packOnlyDates);
			packKegDates.retainAll(kegOnlyDates);

			//Initialize Pack_Keg and add to overall data
			final DeliveryModePackTypeDeliveryDatesData packKegData = new DeliveryModePackTypeDeliveryDatesData();
			packKegData.setMode(mode);
			packKegData.setPackType(PackType.PACK_KEG);
			packKegData.setDateList(packKegDates);
			deliveryModePackTypeDeliveryDatesData.add(packKegData);

			//Clean PACK Only and KEG Only Dates
			packOnlyDates.removeAll(packKegDates);
			kegOnlyDates.removeAll(packKegDates);
		}
	}


	private Map<Integer, DeliveryModeType> getDeliveryModeMappings()
	{
		final Map<Integer, DeliveryModeType> deliveryModeMappings = new HashMap<>();
		final List<SabmDeliveryModeMappingModel> mappingModels = genericDao.find();

		for (final SabmDeliveryModeMappingModel methodMapping : ListUtils.emptyIfNull(mappingModels))
		{
			deliveryModeMappings.put(methodMapping.getMode(), methodMapping.getMethod());
		}
		return deliveryModeMappings;

	}

	private DeliveryModePackTypeDeliveryDatesData getDeliveryModePackTypeDeliveryDateRecord(final DeliveryModeType method,
			final PackType packType, final List<DeliveryModePackTypeDeliveryDatesData> deliveryDates)
	{
		DeliveryModePackTypeDeliveryDatesData dataRecord = null;
		if (CollectionUtils.isNotEmpty(deliveryDates))
		{
			for (final DeliveryModePackTypeDeliveryDatesData deliveryData : deliveryDates)
			{
				if (StringUtils.equals(method.getCode(), deliveryData.getMode().getCode())
						&& StringUtils.equals(packType.getCode(), deliveryData.getPackType().getCode()))
				{
					dataRecord = deliveryData;
				}
			}

		}
		if (dataRecord == null)
		{
			dataRecord = new DeliveryModePackTypeDeliveryDatesData();
			dataRecord.setMode(method);
			dataRecord.setPackType(packType);
			deliveryDates.add(dataRecord);
		}
		return dataRecord;
	}

	@Override
	public Map getDeliveryDatePackType(B2BUnitModel b2bUnit, final Date deliveryDate)
	{
		final B2BUnitModel b2bUnitForAlternativeAddress = getUnitBasedForAlternativeAddress();
		b2bUnit = null != b2bUnitForAlternativeAddress ? b2bUnitForAlternativeAddress : b2bUnit;

		final Map<String, Object> retObj = new HashMap<>();

		final DeliveryModeType deliveryModeType = getDefaultDeliveryModeType(b2bUnit);

		retObj.put(DeliveryModeType._TYPECODE, deliveryModeType);

		final List<DeliveryModePackTypeDeliveryDatesData> data = this.getDeliveryModePackTypeDeliveryDatesData(b2bUnit, true);

		List<String> packTypeCode = data.stream().filter(dt -> deliveryModeType.equals(dt.getMode()))
				.filter(dt -> dt.getDateList().contains(deliveryDate.getTime()))
				.map(DeliveryModePackTypeDeliveryDatesData::getPackType).map(PackType::getCode).collect(Collectors.toList());

		//fallback if no dates for the specified delivery mode - get the packtype from date of the other delivery mode
		if (CollectionUtils.isEmpty(packTypeCode))
		{
			packTypeCode = data.stream().filter(dt -> dt.getDateList().contains(deliveryDate.getTime()))
					.map(DeliveryModePackTypeDeliveryDatesData::getPackType).map(PackType::getCode).collect(Collectors.toList());
		}

		if (CollectionUtils.isNotEmpty(packTypeCode))
		{
			retObj.put(PackType._TYPECODE, packTypeCode.get(0));
		}

		return retObj;
	}

	private DeliveryModeType getDefaultDeliveryModeType(final B2BUnitModel b2bUnit)
	{
		Boolean customerOwned = false;

		if (sabmCartService.hasSessionCart() && CollectionUtils.isNotEmpty(sabmCartService.getSessionCart().getEntries()))
		{
			final CartModel cart = sabmCartService.getSessionCart();
			customerOwned = cart.getDeliveryMode().getCode()
					.equals(siteConfigService.getString(SabmCoreConstants.CART_DELIVERY_CUSTOMERARRANGED, ""));
		}
		else
		{
			final ShippingCarrierModel defaultCarrier = b2bUnit.getDefaultCarrier();
			if (defaultCarrier != null)
			{
				customerOwned = defaultCarrier.getCustomerOwned();
			}
			else
			{
				LOG.warn("default carrier for b2bUnit: {} is null!", b2bUnit);
			}
		}

		return BooleanUtils.isTrue(customerOwned) ? DeliveryModeType.CUSTOMER_DELIVERY : DeliveryModeType.CUB_DELIVERY;
	}

	/**
	 * Gets the additinal delivery dates.
	 *
	 * @param b2bUnit
	 *           the b2b unit
	 * @return the additinal delivery dates
	 */
	protected Set<Date> getAdditinalDeliveryDates(final B2BUnitModel b2bUnit)
	{
		if (b2bUnit == null)
		{
			return SetUtils.emptySet();
		}

		final Set<Date> dateSet = new HashSet<>();

		for (final AdditionalDeliveryDayModel deliveryDay : SetUtils.emptyIfNull(b2bUnit.getAdditionalDeliveryDates()))
		{
			dateSet.add(deliveryDay.getCalendarDay());
		}
		return dateSet;
	}

	/**
	 * Gets the plant cutoff cal.
	 *
	 * @param cutoffHoursMinutes
	 *           the cutoff hours minutes
	 * @param serverDate
	 *           the server date
	 * @param timeZone
	 *           the time zone
	 * @return the plant cutoff cal
	 */
	protected Calendar getPlantCutoffCal(final Date cutoffHoursMinutes, final Date serverDate, final TimeZone timeZone)
	{
		final Calendar hoursMinCal = Calendar.getInstance();
		hoursMinCal.setTime(cutoffHoursMinutes);

		final Calendar plantCutoffCal = Calendar.getInstance();
		plantCutoffCal.setTime(serverDate);
		plantCutoffCal.set(Calendar.HOUR_OF_DAY, hoursMinCal.get(Calendar.HOUR_OF_DAY));
		plantCutoffCal.set(Calendar.MINUTE, hoursMinCal.get(Calendar.MINUTE));
		plantCutoffCal.set(Calendar.SECOND, 0);
		plantCutoffCal.set(Calendar.MILLISECOND, 0);

		plantCutoffCal.setTimeInMillis(plantCutoffCal.getTimeInMillis() - timeZone.getOffset(plantCutoffCal.getTimeInMillis()));

		return plantCutoffCal;
	}

	public String getServerTimeInBaseStoreTimeZone(final B2BUnitModel b2bUnitModel,final boolean defaultDateTimeFormat) {
		if (b2bUnitModel == null) {
			LOG.error("Unable to server time in base store timezone for null b2bUnit");
			return null;
		}

		final Date now = new Date();
		final Calendar todayCal = getServerTime(now);

		final PlantModel plant = b2bUnitModel.getPlant();
		if (plant == null) {
			LOG.error("No Plant found for b2bUnit {}", b2bUnitModel);
			return null;
		}

		try {
			TimeZone storeTimeZone = getPlantTimeZone(b2bUnitModel, todayCal);
			final Calendar storeCal = Calendar.getInstance();
			if (storeTimeZone == null) {
				storeTimeZone = TimeZone.getDefault();
			}
			storeCal.setTimeInMillis(todayCal.getTimeInMillis() + storeTimeZone.getOffset(now.getTime()));
			LOG.debug("Server time in base store timezone: [{}]", storeCal.getTime());
			return SabmDateUtils.extractDateString(storeCal, null, null,defaultDateTimeFormat);
		} catch (final Exception e) {
			LOG.warn("Error parsing server time in base store timezone: ", e);
			return null;
		}

	}

	/**
	 * Match holiday.
	 *
	 * @param publicHolidays
	 *           the public holidays
	 * @param deliveryDate
	 *           the delivery date
	 * @return true, if successful
	 */
	protected boolean matchHoliday(final List<PublicHolidayModel> publicHolidays, final Calendar deliveryDate)
	{
		if(CollectionUtils.isEmpty(publicHolidays))
		{
			return false;
		}

		return publicHolidays.stream()
				.map(PublicHolidayModel::getHolidayDate) // map to get holidayDate
				.filter(Objects::nonNull) // filter nonnull to be safe
				.filter((date) -> DateUtils.isSameDay(date, deliveryDate.getTime()))// verify if same day as deliveryDate
				.findFirst() // find first to found
				.isPresent(); // return if present
	}

	/**
	 * Gets the plant cut off by day of week.
	 *
	 * @param b2bUnit
	 *           the b2b unit
	 * @param dayOfWeek
	 *           the day of week
	 * @return the plant cut off by day of week
	 */
	protected PlantCutOffModel getPlantCutOffByDayOfWeek(final B2BUnitModel b2bUnit, final Integer dayOfWeek)
	{
		if (b2bUnit != null && b2bUnit.getPlant() != null && dayOfWeek != null)
		{
			final String shippingCondition = b2bUnit.getDefaultCarrier() != null ? b2bUnit.getDefaultCarrier().getShippingCondition()
					: null;
			for (final PlantCutOffModel plantCutOff : ListUtils.emptyIfNull(b2bUnit.getPlant().getCutOffs()))
			{
				if (Objects.equals(plantCutOff.getDayOfWeek(), caldendarDeliveryDayMap.get(dayOfWeek))
						&& (shippingCondition == null
								|| StringUtils.equalsIgnoreCase(shippingCondition, plantCutOff.getShippingCondition())))
				{
					return plantCutOff;
				}
			}
		}

		return null;
	}


	@Override
	public String getCutOffTime(final B2BUnitModel b2bUnitModel, final Date deliveryDate)
	{
		if (b2bUnitModel == null)
		{
			LOG.error("Unable to check cutoff time for null b2bUnit");
			return null;
		}

		final Date truncatedDeliveryDate = DateUtils.truncate(deliveryDate, Calendar.DATE);

		LOG.debug("truncatedDeliveryDate:: {}", truncatedDeliveryDate);
		
		final Calendar deliveryDateCal = Calendar.getInstance();
		deliveryDateCal.setTime(truncatedDeliveryDate);

		final PlantModel plant = b2bUnitModel.getPlant();

		if (plant == null)
		{
			LOG.error("No Plant found for b2bUnit {}", b2bUnitModel);
			return null;
		}
		
		final PlantCutOffModel plantCutOff = getPlantCutOffByDayOfWeek(b2bUnitModel, deliveryDateCal.get(Calendar.DAY_OF_WEEK));

		try
		{
			TimeZone plantTimeZone = null;
			//If no timezone set in the plantcutoff, return the default GMT
			if (plantCutOff == null || plantCutOff.getTimeZone() == null)
			{
				plantTimeZone = TimeZone.getDefault();
			}
			else
			{
				final String AestTimeZone =Config.getString("plant.cutoff.timeZones.toStoreTimeZone", "");
				if(StringUtils.isNotEmpty(AestTimeZone) && AestTimeZone.contains(plantCutOff.getTimeZone().getCode())){
					final BaseStoreModel baseStoreTimeZone = baseStoreService.getBaseStoreForUid("sabmStore");
					
					//Getting BaseStore timezone
					if (baseStoreTimeZone != null && baseStoreTimeZone.getTimeZone() != null)
					{
						plantTimeZone = TimeZone.getTimeZone(baseStoreTimeZone.getTimeZone().getCode());
						
					}
				}else{
					plantTimeZone = TimeZone.getTimeZone(plantCutOff.getTimeZone().getCode());
					
				}
			}

			//deliveryDateCal.setTimeZone(plantTimeZone);

			final Date cutoffDate = parser.parse(plantCutOff.getCutOffTime());

			LOG.debug("plantcutoff [{}] , cutoffDate:: {}", plantCutOff.getCutOffTime(), cutoffDate);

			
			final Calendar hoursMinCal = Calendar.getInstance();

			
			hoursMinCal.setTime(cutoffDate);

			final Calendar plantCutoffCal = Calendar.getInstance(plantTimeZone);
			plantCutoffCal.set(deliveryDateCal.get(Calendar.YEAR), deliveryDateCal.get(Calendar.MONTH),
					deliveryDateCal.get(Calendar.DATE));
			
			plantCutoffCal.set(Calendar.HOUR_OF_DAY, hoursMinCal.get(Calendar.HOUR_OF_DAY));
			plantCutoffCal.set(Calendar.MINUTE, hoursMinCal.get(Calendar.MINUTE));
			plantCutoffCal.set(Calendar.SECOND, 0);
			plantCutoffCal.set(Calendar.MILLISECOND, 0);

			LOG.debug("plantCutoffCal [{}] ", plantCutoffCal);
			LOG.debug("timezone [{}] ", plantTimeZone);
			
			if (plantCutOff.getLeadTime() > 0)
			{
				plantCutoffCal.add(Calendar.DATE, -plantCutOff.getLeadTime());
			}

			LOG.debug("lead time {},plantCutoffCal [{}] ", plantCutOff.getLeadTime(), plantCutoffCal);


			//plantCutoffCal.setTimeInMillis(plantCutoffCal.getTimeInMillis() - serverTimeDelta);
			//No need to substract serverDelaTime as Business reduce 10 min in cutoff configuration for customer in Backoffice
			plantCutoffCal.setTimeInMillis(plantCutoffCal.getTimeInMillis());

			final SimpleDateFormat df = new SimpleDateFormat("dd/MM/yyyy hh:mm a");
			df.setTimeZone(plantTimeZone);
			return df.format(plantCutoffCal.getTime());
		}
		catch (final ParseException e)
		{
			LOG.warn("Error parsing cutoff time for model: " + plantCutOff, e);
			return null;
		}
	}

	/**
	 * Check date compliant.
	 *
	 * @param daysMap
	 *         the days map
	 * @param deliveryDays
	 *         the delivery days
	 * @param publicHolidays
	 *         the public holidays
	 * @param deliveryDate
	 *         the delivery date
	 * @return true, if successful
	 */
	protected boolean checkDateCompliant(final Map<String, Set<String>> daysMap, final List<PlantDeliveryDayModel> deliveryDays,
			final List<PublicHolidayModel> publicHolidays, final Calendar deliveryDate, final Set<Date> additionalDeliveryDates) {
		if (additionalDeliveryDates.contains(deliveryDate.getTime()) && !matchHoliday(publicHolidays, deliveryDate)) {
			return true;

		}
		if (MapUtils.isNotEmpty(daysMap)
				&& daysMap.containsKey(String.valueOf(caldendarDeliveryDayMap.get(deliveryDate.get(Calendar.DAY_OF_WEEK))))
				&& CollectionUtils.isNotEmpty(deliveryDays)) {
			for (final PlantDeliveryDayModel deliveryDay : deliveryDays) {
				if (deliveryDay != null && deliveryDay.getDayOfWeek() != null
						&& deliveryDay.getDayOfWeek().equals(caldendarDeliveryDayMap.get(deliveryDate.get(Calendar.DAY_OF_WEEK)))) {
					return !matchHoliday(publicHolidays, deliveryDate);
				}
			}
		}

		return false;
	}



	protected Calendar getServerTime(final Date now)
	{
		final TimeZone serverTimeZone = Calendar.getInstance().getTimeZone();

		final Calendar todayCal = Calendar.getInstance();
		final long serverTimeMillis = now.getTime() - serverTimeZone.getOffset(now.getTime());

		todayCal.setTimeInMillis(serverTimeMillis);

		return todayCal;
	}

	protected TimeZone getPlantTimeZone(final B2BUnitModel b2bUnitModel, final Calendar theCalendar)
	{
		final int dayOfWeek = theCalendar.get(Calendar.DAY_OF_WEEK);
		final PlantCutOffModel plantCutOffForTZ = getPlantCutOffByDayOfWeek(b2bUnitModel, dayOfWeek);

		//If there is no plant cutoff for the specific day of week, return true (CutOff Time Exceeded)
		if (plantCutOffForTZ == null)
		{
			LOG.error("Missing PlantCutOffModel for Plant: {} and dayOfWeek: {} for B2BUnit: {}", b2bUnitModel.getPlant(), dayOfWeek,
					b2bUnitModel);
			return null;
		}

		if(plantCutOffForTZ.getTimeZone() == null){
			return TimeZone.getDefault();
		}else{
			final String AestTimeZone =Config.getString("plant.cutoff.timeZones.toStoreTimeZone", "");
			if(StringUtils.isNotEmpty(AestTimeZone) && AestTimeZone.contains(plantCutOffForTZ.getTimeZone().getCode())){
				final BaseStoreModel baseStoreTimeZone = baseStoreService.getBaseStoreForUid("sabmStore");
				//Getting BaseStore timezone
				if (baseStoreTimeZone != null && baseStoreTimeZone.getTimeZone() != null)
				{
					return TimeZone.getTimeZone(baseStoreTimeZone.getTimeZone().getCode());
				}
			}else{
					return TimeZone.getTimeZone(plantCutOffForTZ.getTimeZone().getCode());
			}
		}

		return null;
	}

	/**
	 * Getting the store time which is same as calculated in isCutOffTimeExceeded(final B2BUnitModel b2bUnitModel) method
	 *
	 * @param b2bUnit
	 *           , baseStore
	 * @return plantcutoffstoreTime as date
	 */
	@Override
	public Date getplantcutoffstoreTime(final B2BUnitModel b2bUnit, final BaseStoreModel baseStore)
	{
		final TimeZone serverTimeZone = Calendar.getInstance().getTimeZone();
		final Date now = new Date();
		final Calendar todayCal = Calendar.getInstance();
		final long serverTimeMillis = now.getTime() - serverTimeZone.getOffset(now.getTime()) - serverTimeDelta;
		todayCal.setTimeInMillis(serverTimeMillis);

		final PlantCutOffModel plantCutOffForTZ = getPlantCutOffByDayOfWeek(b2bUnit, todayCal.get(Calendar.DAY_OF_WEEK));
		if (baseStore != null && baseStore.getTimeZone() != null && plantCutOffForTZ != null
				&& plantCutOffForTZ.getTimeZone() != null
				&& !baseStore.getTimeZone().getCode().equals(plantCutOffForTZ.getTimeZone().getCode()))
		{
			TimeZone plantcutoffTimeZone = null;
			final String AestTimeZone =Config.getString("plant.cutoff.timeZones.toStoreTimeZone", "");
			if(StringUtils.isNotEmpty(AestTimeZone) && AestTimeZone.contains(plantCutOffForTZ.getTimeZone().getCode())){
				final BaseStoreModel baseStoreTimeZone = baseStoreService.getBaseStoreForUid("sabmStore");
				//Getting BaseStore timezone
				if (baseStoreTimeZone != null && baseStoreTimeZone.getTimeZone() != null)
				{
					plantcutoffTimeZone = TimeZone.getTimeZone(baseStoreTimeZone.getTimeZone().getCode());
				}
			}else{
				plantcutoffTimeZone = TimeZone.getTimeZone(plantCutOffForTZ.getTimeZone().getCode());
			}

			if (plantcutoffTimeZone != null)
			{
				final Calendar plantcutoffstoreCal = Calendar.getInstance();
				plantcutoffstoreCal.setTimeInMillis(todayCal.getTimeInMillis() + plantcutoffTimeZone.getOffset(now.getTime()));
				LOG.debug("Server time in plant cutoff timezone: [{}]", plantcutoffstoreCal.getTime());
				return plantcutoffstoreCal.getTime();

			}
		}
		return null;
	}

	/* (non-Javadoc)
    * @see com.sabmiller.core.b2b.services.SABMDeliveryDateCutOffService#getCutOffTimeforCalendarToDisplay(de.hybris.platform.b2b.model.B2BUnitModel, java.util.Date)
    */
   @Override
	public HashMap<String, String> getCutOffTimeforCalendarToDisplay(final B2BUnitModel b2bUnitModel, final Date deliveryDate)
   {

       if (b2bUnitModel == null)
       {
           LOG.error("Unable to check cutoff time for null b2bUnit");
           return null;
       }
		 HashMap<String, String> returnMap = null;
       final Date truncatedDeliveryDate = DateUtils.truncate(deliveryDate, Calendar.DATE);
       LOG.debug("truncatedDeliveryDate:: {}", truncatedDeliveryDate);
       final Calendar deliveryDateCal = Calendar.getInstance();
       deliveryDateCal.setTime(truncatedDeliveryDate);

       final PlantModel plant = b2bUnitModel.getPlant();
       if (plant == null)
       {
           LOG.error("No Plant found for b2bUnit {}", b2bUnitModel);
           return null;
       }

       final Calendar cutOffDateCal = Calendar.getInstance();
     cutOffDateCal.setTime(truncatedDeliveryDate);
     cutOffDateCal.add(Calendar.DAY_OF_YEAR, -1);
     PlantCutOffModel plantCutOff = null;
     for(int i=1;i<=7;i++){
         final Calendar tempCal = Calendar.getInstance();
         tempCal.setTime(cutOffDateCal.getTime());
         plantCutOff = getPlantCutOffByDayOfWeek(b2bUnitModel, cutOffDateCal.get(Calendar.DAY_OF_WEEK));
         if(plantCutOff != null && plantCutOff.getLeadTime() != null){
             tempCal.add(Calendar.DAY_OF_YEAR, plantCutOff.getLeadTime());
             if(!tempCal.after(deliveryDateCal)){
                 break;
             }
             cutOffDateCal.add(Calendar.DAY_OF_YEAR, -1);
         }
     }
       try
       {
           TimeZone plantTimeZone = null;
           Date cutoffDate = null;
           //If no timezone set in the plantcutoff, return the default GMT
           if (plantCutOff == null || plantCutOff.getTimeZone() == null)
           {
               if (plantCutOff == null){
                   cutoffDate = parser.parse("0000");
               }
               plantTimeZone = TimeZone.getDefault();
           }
           else
           {
               final String AestTimeZone =Config.getString("plant.cutoff.timeZones.toStoreTimeZone", "");
               if(StringUtils.isNotEmpty(AestTimeZone) && AestTimeZone.contains(plantCutOff.getTimeZone().getCode())){
                   final BaseStoreModel baseStoreTimeZone = baseStoreService.getBaseStoreForUid("sabmStore");
                   //Getting BaseStore timezone
                   if (baseStoreTimeZone != null && baseStoreTimeZone.getTimeZone() != null)
                   {
                       plantTimeZone = TimeZone.getTimeZone(baseStoreTimeZone.getTimeZone().getCode());
                   }
               }else{
                   plantTimeZone = TimeZone.getTimeZone(plantCutOff.getTimeZone().getCode());
               }
               cutoffDate = parser.parse(plantCutOff.getCutOffTime());
               LOG.debug("plantcutoff [{}] , cutoffDate:: {}", plantCutOff.getCutOffTime(), cutoffDate);
           }
			  //Fix for INC0136590
           //cutOffDateCal.setTimeZone(plantTimeZone);
           final Calendar hoursMinCal = Calendar.getInstance();
           hoursMinCal.setTime(cutoffDate);
           final Calendar plantCutoffCal = Calendar.getInstance(plantTimeZone);
           plantCutoffCal.set(cutOffDateCal.get(Calendar.YEAR), cutOffDateCal.get(Calendar.MONTH),
         		  cutOffDateCal.get(Calendar.DATE));
           plantCutoffCal.set(Calendar.HOUR_OF_DAY, hoursMinCal.get(Calendar.HOUR_OF_DAY));
           plantCutoffCal.set(Calendar.MINUTE, hoursMinCal.get(Calendar.MINUTE));
           plantCutoffCal.set(Calendar.SECOND, 0);
           plantCutoffCal.set(Calendar.MILLISECOND, 0);
           LOG.debug("plantCutoffCal [{}] ", plantCutoffCal);
           LOG.debug("timezone [{}] ", plantTimeZone);
           LOG.debug("lead time {},plantCutoffCal [{}] ", plantCutOff.getLeadTime(), plantCutoffCal);
           plantCutoffCal.setTimeInMillis(plantCutoffCal.getTimeInMillis());
           final SimpleDateFormat df = new SimpleDateFormat("dd/MM/yyyy hh:mm a");
           df.setTimeZone(plantTimeZone);
			  returnMap = new HashMap<String, String>();
			  returnMap.put(SabmCoreConstants.CUTOFFTIME, df.format(plantCutoffCal.getTime()));

			  if (null != plantCutOff.getTimeZone())
			  {
				  returnMap.put(SabmCoreConstants.PLANT_CUTOFF_TIMEZONE, plantCutOff.getTimeZone().getCode());

			  }
			  return returnMap;
       }
       catch (final ParseException e)
       {
           LOG.warn("Error parsing cutoff time for model: " + plantCutOff, e);
           return null;
       }
   }
}
