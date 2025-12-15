package com.apb.core.checkout.service.impl;

import de.hybris.platform.b2b.model.B2BCustomerModel;
import de.hybris.platform.b2b.model.B2BUnitModel;
import de.hybris.platform.b2bacceleratorfacades.order.data.B2BPaymentTypeData;
import de.hybris.platform.cms2.servicelayer.services.CMSSiteService;
import de.hybris.platform.commerceservices.delivery.DeliveryService;
import de.hybris.platform.core.model.c2l.RegionModel;
import de.hybris.platform.core.model.order.CartModel;
import de.hybris.platform.core.model.user.AddressModel;
import de.hybris.platform.core.model.user.EmployeeModel;
import de.hybris.platform.core.model.user.UserModel;
import de.hybris.platform.order.CartService;
import de.hybris.platform.ordersplitting.WarehouseService;
import de.hybris.platform.ordersplitting.model.WarehouseModel;
import de.hybris.platform.servicelayer.internal.dao.GenericDao;
import de.hybris.platform.servicelayer.model.ModelService;
import de.hybris.platform.servicelayer.session.SessionService;
import de.hybris.platform.servicelayer.user.UserService;
import de.hybris.platform.storelocator.model.PointOfServiceModel;

import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.time.Instant;
import java.time.ZoneId;
import java.time.ZoneOffset;
import java.time.ZonedDateTime;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.Comparator;
import java.util.Date;
import java.util.HashSet;
import java.util.List;
import java.util.Optional;
import java.util.Set;
import java.util.stream.Collectors;

import jakarta.annotation.Resource;

import org.apache.commons.collections4.CollectionUtils;
import org.apache.commons.lang3.BooleanUtils;
import org.apache.commons.lang3.StringUtils;
import org.joda.time.DateTime;
import org.joda.time.DateTimeComparator;
import org.joda.time.DateTimeConstants;
import org.joda.time.DateTimeZone;
import org.joda.time.format.DateTimeFormat;
import org.joda.time.format.DateTimeFormatter;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.apb.core.checkout.dao.ApbCheckoutDao;
import com.apb.core.checkout.service.ApbCheckoutService;
import com.apb.core.constants.ApbCoreConstants;
import com.apb.core.model.CutOffDeliveryDateModel;
import com.apb.core.service.config.AsahiConfigurationService;
import com.apb.core.util.ApbAddressTimeUtil;
import com.apb.core.util.AsahiDateUtil;
import com.apb.core.util.AsahiSiteUtil;
import com.apb.facades.delivery.data.DeliveryInfoData;
import com.apb.integration.data.AsahiProductInfo;
import com.apb.service.b2bunit.ApbB2BUnitService;
import com.sabmiller.core.model.AsahiB2BUnitModel;
import com.sabmiller.core.model.BDECustomerModel;
import com.sabmiller.core.model.HolidayModel;
import com.sabmiller.facades.bdeordering.BdeOrderDetailsForm;

/**
 * @author Ashish.Monga
 *
 */

public class ApbCheckoutServiceImpl implements ApbCheckoutService
{

	private static final Logger LOGGER = LoggerFactory.getLogger(ApbCheckoutServiceImpl.class);
	private static final String CONVERSIONDATEFORMAT = "yyyyMMdd HH:mm:ss.SSSSSS Z";
	private static final String ZONEID_AWST = "Australia/Perth";
	private static final String ZONEID_ACWST = "Australia/Eucla";
	private static final String ZONEID_ACST = "Australia/Adelaide";
	private static final String ZONEID_AEST = "Australia/Sydney";
	private static final String ZONEID_LHST = "Australia/Lord_Howe";
	private static final String DUMMYSECONDS = "00.000";
	private static final String DUMMYDATE = "20121130";
	private static final String CHECKOUT_DEFERRED_DELIVERY_DATES_DAYS = "checkout.deferred.delivery.dates.days.apb";
	private static final String DEFAULT_WAREHOUSE_CODE = "default";
	private static final String DEFAULT_CUT_OFF_TIME = "12:30 PM AEST";
	private static final String TIMEZONE_DISPLAY_VAL = "apb.cutoff.message.timezone.";
	private static final String DEVICE_TYPE_STAFF_PORTAL = "StaffPortal";
	private static final String TIMEZONE_ZONE_ID = "apb.cutoff.time.zone.id.";
	private static final String CUT_OFF_TIME_FORMAT = "apb.cutoff.time.format";

	public static final String PAYMENTMODE_ACCOUNT = "ACCOUNT";
	public static final String PAYMENTMODE_DELIVERY = "DELIVERY";

	@Resource(name = "userService")
	private UserService userService;

	@Resource(name = "asahiConfigurationService")
	private AsahiConfigurationService asahiConfigurationService;

	@Resource(name = "apbCheckoutDao")
	private ApbCheckoutDao apbCheckoutDao;

	@Resource(name = "sessionService")
	private SessionService sessionService;

	@Resource(name = "b2bDeliveryService")
	private DeliveryService b2bDeliveryService;

	@Resource
	private CartService cartService;

	@Resource
	private WarehouseService warehouseService;

	@Resource(name = "apbB2BUnitService")
	private ApbB2BUnitService apbB2BUnitService;

	@Resource(name = "asahiSiteUtil")
	private AsahiSiteUtil asahiSiteUtil;

	@Resource(name = "asahiDateUtil")
	private AsahiDateUtil asahiDateUtil;

	@Resource
	private CMSSiteService cmsSiteService;

	@Resource(name = "employeeDao")
	private GenericDao<EmployeeModel> employeeDao;

	@Resource
	private ModelService modelService;

	/*
	 * (non-Javadoc)
	 *
	 * @see main method - returns delivery Info data
	 */
	@Override
	public DeliveryInfoData getDeliveryInfo(final String addressRecordId)
	{

		DeliveryInfoData deliveryData = null;

		// Populating Customer B2BUnits

		final UserModel user = this.userService.getCurrentUser();
		B2BCustomerModel customer = null ;
		try {
		customer = (B2BCustomerModel) user;
		}catch(final ClassCastException classCastExp) {
			LOGGER.error("Can not cast from "+ user + " to B2bCustomer. User name is  "+ ((user != null )? user.getName():" NULL " ), classCastExp);
			return null;
		}
		final B2BUnitModel b2bUnit = customer.getDefaultB2BUnit();
		RegionModel customerRegion = null;
		Optional<AddressModel> selectedAddress = null;
		if (null != b2bUnit)
		{
			final CartModel cartModel = cartService.getSessionCart();
			final List<AddressModel> addresses = b2bDeliveryService.getSupportedDeliveryAddressesForOrder(cartModel, true);
			if (CollectionUtils.isNotEmpty(addresses))
			{
				if (null != addressRecordId)
				{
					selectedAddress = addresses.stream().filter(address -> address.getAddressRecordid().equals(addressRecordId))
							.findFirst();
				}
				else
				{
					// select default address
					selectedAddress = addresses.stream()
							.filter(address -> null != address.getDefaultAddress() && address.getDefaultAddress().equals(Boolean.TRUE))
							.findFirst();
				}
				if (selectedAddress.isPresent())
				{
					customerRegion = selectedAddress.get().getRegion();
				}
				else
				{
					customerRegion = addresses.get(0).getRegion();
					if(asahiSiteUtil.isSga()){
						selectedAddress = addresses.stream().findFirst();
					}
				}

			}
		}

		if (!asahiSiteUtil.isSga())
		{

			final DateTime userLocalTime = getUserLocalTime();

			final WarehouseModel warehouseModel = getWarehouseForB2BUnit(getCurrentB2BUnit());

			final RegionModel regionModel = getRegionFromWarehouse(warehouseModel);

			final DateTime warehouseCutOffTime = getCutOffTime(null,warehouseModel, regionModel);
			deliveryData = getDeliveryInfo(warehouseModel, regionModel);

			// setting deferred delivery options
			if (null != warehouseCutOffTime && null != customerRegion)
			{
				final DateTime warehouseCurrentDTime = this.getWarehouseCurrentDateTime(new DateTime(), warehouseCutOffTime);
				final DateTime cutOffTime = warehouseCutOffTime.withDate(warehouseCurrentDTime.toLocalDate());
				deliveryData.setDeferredDeliveryOptions(this.createDeferredDeliveryOptions(cutOffTime, regionModel, customerRegion));
			}
		}
		else
		{

			deliveryData = getDeliveryOptionsForCustomer(selectedAddress, customerRegion);
		}

		return deliveryData;
	}
	private DateTime getCutOffTimeInLocal(final DateTime cutOffTime, final DateTime userLocalTime)
	{
		final DateTimeZone localTimeZone = userLocalTime.getZone();
		return cutOffTime.withZone(localTimeZone);
	}
	/**
	 * @param customerRegion
	 * @param selectedAddress
	 * @return the method gets delivery options for the customer
	 */
	private DeliveryInfoData getDeliveryOptionsForCustomer(final Optional<AddressModel> selectedAddress,
			final RegionModel customerRegion)
	{

		final DeliveryInfoData deliveryInfoData = new DeliveryInfoData();

		final DateTime cutOffDateTime = getCutOffTimeForRegion(customerRegion);

		final List<CutOffDeliveryDateModel> cutOffDeliveryDates = getCutOffDeliveryDates(selectedAddress);

		final DateTime regionCurrentDateTime = getCurrentDateTimeInRegion(cutOffDateTime);

		final List<CutOffDeliveryDateModel> eligibleCutOffDeliveryDates = getEligibleCutOffDeliveryDates(cutOffDeliveryDates,
				regionCurrentDateTime);

		setCutOffCriteria(cutOffDateTime, regionCurrentDateTime, eligibleCutOffDeliveryDates, deliveryInfoData);

		setDeliveryDates(eligibleCutOffDeliveryDates, deliveryInfoData);

		return deliveryInfoData;

	}
	private DateTime getCurrentDateTimeInRegion(final DateTime cutOffTime)
	{
		if (null != cutOffTime)
		{
			final DateTimeZone regionTimeZone = cutOffTime.getZone();
			return new DateTime().withZone(regionTimeZone);
		}
		return null;
	}

	/**
	 * @param eligibleCutOffDeliveryDates
	 * @param deliveryInfoData
	 *           This method sets the delivery dates in DeliveryInfoData.
	 */
	private void setDeliveryDates(final List<CutOffDeliveryDateModel> eligibleCutOffDeliveryDates,
			final DeliveryInfoData deliveryInfoData)
	{
		final Boolean isBeforeCutOff = deliveryInfoData.getBeforeCutOff();
		if (null != isBeforeCutOff && !isBeforeCutOff && CollectionUtils.isNotEmpty(eligibleCutOffDeliveryDates))
		{
			eligibleCutOffDeliveryDates.remove(0);
		}

		final List<String> dateList = new ArrayList<>();

		for (final CutOffDeliveryDateModel cutOffDeliveryDateModel : eligibleCutOffDeliveryDates)
		{
			final DateFormat dateFormat = new SimpleDateFormat(ApbCoreConstants.DEFER_DELIVERY_DATEPATTERN);
			dateList.add(dateFormat.format(cutOffDeliveryDateModel.getDeliveryDate()));
		}

		deliveryInfoData.setDeferredDeliveryOptions(dateList);
	}


	/**
	 * @param cutOffDateTime
	 * @param regionCurrentDateTime
	 * @param eligibleCutOffDeliveryDates
	 * @param deliveryInfoData
	 *           This method sets cut off criteria in DeliveryInfoData.
	 */
	private void setCutOffCriteria(final DateTime cutOffDateTime, final DateTime regionCurrentDateTime,
			final List<CutOffDeliveryDateModel> eligibleCutOffDeliveryDates, final DeliveryInfoData deliveryInfoData)
	{

		deliveryInfoData.setDisableDeferredDelivery(Boolean.FALSE);
		if (!regionCurrentDateAvailableInCutOffDates(eligibleCutOffDeliveryDates, regionCurrentDateTime))
		{
			deliveryInfoData.setBeforeCutOff(Boolean.TRUE);
		}
		else
		{
			if (null != cutOffDateTime && null != regionCurrentDateTime)
			{
				if (isRegionTimeAfterCutOff(regionCurrentDateTime, cutOffDateTime))
				{
					deliveryInfoData.setBeforeCutOff(Boolean.FALSE);
				}
				else
				{
					deliveryInfoData.setBeforeCutOff(Boolean.TRUE);
				}
			}
		}

	}


	private boolean isRegionTimeAfterCutOff(final DateTime regionCurrentTime, final DateTime cutOffDateTime)
	{
		final DateTimeComparator comparator = DateTimeComparator.getTimeOnlyInstance();
		final int comp = comparator.compare(regionCurrentTime, cutOffDateTime);
		if (comp > 0)
		{
			return true;
		}
		return false;
	}


	/**
	 * @param selectedAddress
	 * @return This method gets cut off delivery dates for the address.
	 */
	private List<CutOffDeliveryDateModel> getCutOffDeliveryDates(final Optional<AddressModel> selectedAddress)
	{
		List<CutOffDeliveryDateModel> cutOffDeliveryDateModels = Collections.emptyList();
		if (null != selectedAddress && selectedAddress.isPresent())
		{
			cutOffDeliveryDateModels = selectedAddress.get().getCutOffDeliveryDates();
			final List<CutOffDeliveryDateModel> eligibleDates = new ArrayList<CutOffDeliveryDateModel>(cutOffDeliveryDateModels);
			this.sortCutOffDeliveryDates(eligibleDates);
			return eligibleDates;
		}
		return cutOffDeliveryDateModels;
	}




	/**
	 * @param cutOffDeliveryDateModels
	 * @param regionCurrentDateTime
	 * @return This method evaluates if region current date is available in Cut Off Dates List
	 */
	private boolean regionCurrentDateAvailableInCutOffDates(final List<CutOffDeliveryDateModel> cutOffDeliveryDateModels,
			final DateTime regionCurrentDateTime)
	{
		if (null != regionCurrentDateTime)
		{
			for (final CutOffDeliveryDateModel cutOffDeliveryDateModel : cutOffDeliveryDateModels)
			{

				final int comp = new DateTime(cutOffDeliveryDateModel.getCutOffDate()).toLocalDate()
						.compareTo(regionCurrentDateTime.toLocalDate());
				if (comp == 0)
				{
					return true;
				}
			}
		}
		return false;
	}


	/**
	 * @param cutOffDeliveryDateModels
	 *           This method sorts cut off delivery dates on the basis of cut off dates.
	 */
	private void sortCutOffDeliveryDates(final List<CutOffDeliveryDateModel> cutOffDeliveryDateModels)
	{

		Collections.sort(cutOffDeliveryDateModels, new Comparator<CutOffDeliveryDateModel>()
		{
			@Override
			public int compare(final CutOffDeliveryDateModel cutOffDeliveryDate1, final CutOffDeliveryDateModel cutOffDeliveryDate2)
			{

				return compare(cutOffDeliveryDate1.getCutOffDate(), cutOffDeliveryDate2.getCutOffDate());
			}

			private int compare(final Date cutOffDate1, final Date cutOffDate2)
			{
				return cutOffDate1.compareTo(cutOffDate2);
			}
		});
	}


	/**
	 * @param cutOffDeliveryDates
	 * @param regionCurrentDateTime
	 * @return This method removes the past dates from deliveryDates List and returns the sorted eligible dates
	 */
	private List<CutOffDeliveryDateModel> getEligibleCutOffDeliveryDates(final List<CutOffDeliveryDateModel> cutOffDeliveryDates,
			final DateTime regionCurrentDateTime)
	{

		List<CutOffDeliveryDateModel> validCutOffDeliveryDates = Collections.emptyList();
		if (null != regionCurrentDateTime)
		{
			validCutOffDeliveryDates = new ArrayList<CutOffDeliveryDateModel>();
			for (final CutOffDeliveryDateModel cutOffDeliveryDateModel : cutOffDeliveryDates)
			{
				if (isValidDeliveryDate(new DateTime(cutOffDeliveryDateModel.getCutOffDate()), regionCurrentDateTime))
				{
					validCutOffDeliveryDates.add(cutOffDeliveryDateModel);
				}
			}
		}
		return validCutOffDeliveryDates;
	}


	/**
	 * @param regionCurrentDateTime
	 * @param cutOffDateTime
	 * @return checks if the cut off date is equal or after the current date
	 */
	private boolean isValidDeliveryDate(final DateTime cutOffDateTime, final DateTime regionCurrentDateTime)
	{
		final int comp = cutOffDateTime.toLocalDate().compareTo(regionCurrentDateTime.toLocalDate());
		if (comp >= 0)
		{
			return true;
		}
		return false;
	}



	/**
	 * @param customerRegion
	 * @return this method gets the Cut Off Time for the Customer Region
	 */
	private DateTime getCutOffTimeForRegion(final RegionModel customerRegion)
	{
		String cutOffTimeStr = StringUtils.EMPTY;
		DateTime zoneCutOffTime = null;
		if (null != customerRegion)
		{
			final String custCutOffTimeStr = customerRegion.getCutOffTimeSga();
			if (StringUtils.isNotEmpty(custCutOffTimeStr))
			{
				cutOffTimeStr = custCutOffTimeStr;
			}

			zoneCutOffTime = getZoneCutOffTime(cutOffTimeStr);
		}
		return zoneCutOffTime;
	}

	// Deprecated: Modified implementation as part of fix for ACP-2702 as discussed
	@Deprecated
	private DateTime getUserLocalTime()
	{
		DateTime userDateTime = null;
		final String userTimeOffsetSec = sessionService.getAttribute("asahiUserTimeOffset");
		if (StringUtils.isNotEmpty(userTimeOffsetSec))
		{
			try
			{
				String timeOffset = "";
				if (userTimeOffsetSec.contains("-"))
				{
					timeOffset = userTimeOffsetSec.replace("-", "+");
				}
				else
				{
					timeOffset = userTimeOffsetSec.replace("+", "-");
				}
				final int timeOffsetInt = Integer.parseInt(timeOffset);
				userDateTime = new DateTime(DateTimeZone.forOffsetMillis(timeOffsetInt));
			}
			catch (final NumberFormatException e)
			{
				LOGGER.error("NumberFormatException for Offset time Calculation" + e.getMessage());
			}
		}
		return userDateTime;
	}

	/**
	 * Creates the deferred delivery options.
	 *
	 * @param cutOffTime
	 *           the cut off time
	 * @param region
	 *           the region
	 * @param customerRegion
	 *           the customer region
	 * @return the list
	 */
	private List<String> createDeferredDeliveryOptions(final DateTime cutOffTime, final RegionModel region,
			final RegionModel customerRegion)
	{

		final List<String> deferredDeliveryDates = new ArrayList<String>();
		DateTime fisrtCutOffDate = cutOffTime;
		// Logic to determine first deferred delivery date
		// Before CutOff Time
		if (cutOffTime.isAfterNow())
		{
			if (cutOffTime.getDayOfWeek() == DateTimeConstants.SATURDAY || cutOffTime.getDayOfWeek() == DateTimeConstants.SUNDAY
					|| (cutOffTime.getDayOfWeek() == DateTimeConstants.MONDAY && (this.checkIsHoliday(region.getIsocode(), cutOffTime)
							|| this.checkIsHoliday(customerRegion.getIsocode(), cutOffTime)))
					|| (cutOffTime.getDayOfWeek() == DateTimeConstants.TUESDAY && (this.checkIsHoliday(region.getIsocode(), cutOffTime)
							|| this.checkIsHoliday(customerRegion.getIsocode(), cutOffTime)))
					|| (cutOffTime.getDayOfWeek() == DateTimeConstants.WEDNESDAY
							&& (this.checkIsHoliday(region.getIsocode(), cutOffTime)
									|| this.checkIsHoliday(customerRegion.getIsocode(), cutOffTime)))
					|| (cutOffTime.getDayOfWeek() == DateTimeConstants.THURSDAY
							&& (this.checkIsHoliday(region.getIsocode(), cutOffTime)
									|| this.checkIsHoliday(customerRegion.getIsocode(), cutOffTime)))
					|| (cutOffTime.getDayOfWeek() == DateTimeConstants.FRIDAY && (this.checkIsHoliday(region.getIsocode(), cutOffTime)
							|| this.checkIsHoliday(customerRegion.getIsocode(), cutOffTime))))
			{
				fisrtCutOffDate = this.calculateFirstDate(cutOffTime, region, customerRegion, 3);
			}
			else
			{
				fisrtCutOffDate = this.calculateFirstDate(cutOffTime, region, customerRegion, 2);
			}
		}
		else
		{
			// After CutOff Time
			fisrtCutOffDate = this.calculateFirstDate(cutOffTime, region, customerRegion, 3);
		}
		// setting first Deferred Delivery Date
		this.setDeferredDeliveryDate(fisrtCutOffDate, deferredDeliveryDates);

		// populating the deferred delivery option till
		// deferredDeliveryOptionsDays
		return this.populateDeferredDeliveryOptionsBasedOnCutOffTime(fisrtCutOffDate, region, deferredDeliveryDates,
				customerRegion);
	}

	/**
	 * Calculate first date.
	 *
	 * @param cutOffTime
	 *           the cut off time
	 * @param region
	 *           the region
	 * @param customerRegion
	 * @param n
	 *           the n
	 * @return the date time
	 */
	private DateTime calculateFirstDate(DateTime cutOffTime, final RegionModel region, final RegionModel customerRegion,
			final int n)
	{
		int daysCount = 0;
		int j = 0;

		if (this.checkIsWeekend(cutOffTime) || (null != region && (this.checkIsHoliday(region.getIsocode(), cutOffTime)
				|| this.checkIsHoliday(customerRegion.getIsocode(), cutOffTime))))
		{
			j = n - 1;
		}
		else
		{
			j = n;
		}

		while (daysCount < n + 1)
		{
			if (this.checkIsWeekend(cutOffTime) || (null != region && (this.checkIsHoliday(region.getIsocode(), cutOffTime)
					|| this.checkIsHoliday(customerRegion.getIsocode(), cutOffTime))))
			{
			}
			else
			{
				daysCount++;
			}
			if (daysCount <= j)
			{
				cutOffTime = cutOffTime.plusDays(1);
			}
		}
		return cutOffTime;
	}

	/**
	 * Sets the deferred delivery date. the list contains list of dates in format: dd/MM/yyyy, as String
	 *
	 * @param cutOffTime
	 *           the cut off time
	 * @param deferredDeliveryDates
	 *           the deferred delivery dates
	 */
	private void setDeferredDeliveryDate(final DateTime cutOffTime, final List<String> deferredDeliveryDates)
	{
		final DateTimeFormatter deliveryDateFormatter = DateTimeFormat.forPattern("dd/MM/yyyy");
		deferredDeliveryDates.add(deliveryDateFormatter.print(cutOffTime));
	}

	/**
	 * Populate deferred delivery options based on cut off time.
	 *
	 * @param cutOffTime
	 *           the cut off time
	 * @param region
	 *           the region
	 * @param deferredDeliveryDates
	 *           the deferred delivery dates
	 * @param deliveryDate
	 * @param deferredDeliveryOptionsDays
	 *           the deferred delivery options days
	 * @return the list
	 */
	private List<String> populateDeferredDeliveryOptionsBasedOnCutOffTime(DateTime fisrtCutOffDate, final RegionModel region,
			final List<String> deferredDeliveryDates, final RegionModel customerRegion)
	{

		int optionDays = 0;
		final String deliveryDays = this.asahiConfigurationService.getString(CHECKOUT_DEFERRED_DELIVERY_DATES_DAYS, " ");

		if (null != deliveryDays)
		{
			optionDays = Integer.parseInt(deliveryDays);
		}
		else
		{
			optionDays = fisrtCutOffDate.dayOfMonth().getMaximumValue();
		}

		for (int i = 2; i <= optionDays; i++)
		{
			fisrtCutOffDate = fisrtCutOffDate.plusDays(1);
			if (null != region && (this.checkIsWeekend(fisrtCutOffDate) || this.checkIsHoliday(region.getIsocode(), fisrtCutOffDate)
					|| this.checkIsHoliday(customerRegion.getIsocode(), fisrtCutOffDate)))
			{
				continue;
			}
			else
			{
				this.setDeferredDeliveryDate(fisrtCutOffDate, deferredDeliveryDates);
			}
		}
		return deferredDeliveryDates;
	}

	/**
	 * @param warehouseModel
	 * @param warehouseCutOffTime
	 * @param regionModel
	 * @param sysDateTime
	 * @param userLocalTime
	 * @return
	 * @see sets delivery info data based on cut off time.
	 */
	private DeliveryInfoData getDeliveryInfo(final WarehouseModel warehouseModel, final RegionModel regionModel)
	{
		// comparison between the two time instances will be made and
		// appropriate params will be set.
		final DeliveryInfoData deliveryInfoData = new DeliveryInfoData();
		// Modified implementation as part of fix for ACP-2702 as discussed
		//final DateTime sysDateTime = new DateTime();

		final DateTime warehouseCutOffTime = getCutOffTime(deliveryInfoData, warehouseModel, regionModel);

		if (null != warehouseModel && null != warehouseCutOffTime)
		{

			deliveryInfoData.setDisableDeferredDelivery(warehouseModel.isDisableDeferredDelivery());
			// getWarehouse current time and date
			//final DateTime warehouseCurrentDTime = getWarehouseCurrentDateTime(sysDateTime, warehouseCutOffTime);

			//final DateTime warehouseCutOffTimeInLocal = getWarehouseCutOffTimeInLocal(warehouseCutOffTime, userLocalTime);

			//deliveryInfoData.setCutOffDate(getLocalCutOffTime(warehouseCutOffTimeInLocal));

			evaluateDeliveryInfoData(regionModel, deliveryInfoData, warehouseCutOffTime);
		}
		return deliveryInfoData;
	}

	// Deprecated: Modified implementation as part of fix for ACP-2702 as discussed
	@Deprecated
	private String getLocalCutOffTime(final DateTime warehouseCutOffTimeInLocal)
	{

		final int hour = warehouseCutOffTimeInLocal.getHourOfDay();
		final int minute = warehouseCutOffTimeInLocal.getMinuteOfHour();

		final String time12 = getTime12(String.valueOf(hour) + ":" + String.valueOf(minute));

		String userTimeZone = sessionService.getAttribute("asahiUserTimeZone");
		if (StringUtils.isNotEmpty(userTimeZone))
		{
			userTimeZone = userTimeZone.replace(".", " ");
			return time12.concat(" (").concat(userTimeZone).concat(")");
		}

		return time12;

	}

	// Deprecated: Modified implementation as part of fix for ACP-2702 as discussed
	@Deprecated
	private DateTime getWarehouseCutOffTimeInLocal(final DateTime warehouseCutOffTime, final DateTime userLocalTime)
	{
		final DateTimeZone localTimeZone = userLocalTime.getZone();
		return warehouseCutOffTime.withZone(localTimeZone);
	}

	/**
	 * @param regionModel
	 * @param deliveryInfoData
	 * @param utcCutOffDateTime
	 * @param warehouseCurrentDTime
	 * @param warehouseCutOffTimeInLocal
	 * @param userLocalTime
	 * @see evaluates delivery info parameters for before cut off and after cut off
	 */
	private void evaluateDeliveryInfoData(final RegionModel regionModel, final DeliveryInfoData deliveryInfoData,
			final DateTime warehouseCurrentDTime)
	{

		final String fromStandardDelDays = this.asahiConfigurationService.getString("delivery.timeframe.standard.fromDay.apb", "1");
		final String toStandardDelDays = this.asahiConfigurationService.getString("delivery.timeframe.standard.toDay.apb", "2");
		final DateTime sysDateTime = new DateTime();
		if (null != regionModel
				&& (checkIsWeekend(warehouseCurrentDTime) || checkIsHoliday(regionModel.getIsocode(), warehouseCurrentDTime)))
		{
			setAfterCutOffParameters(deliveryInfoData, fromStandardDelDays, toStandardDelDays);
		}
		else if (sysDateTime.isBefore(warehouseCurrentDTime))
		{
			setBeforeCutOffParameters(deliveryInfoData, fromStandardDelDays, toStandardDelDays);
		}
		else
		{
			setAfterCutOffParameters(deliveryInfoData, fromStandardDelDays, toStandardDelDays);
		}
	}

	// Deprecated: Modified implementation as part of fix for ACP-2702 as discussed
	@Deprecated
	private boolean isCutOffAfterCurrentTime(final DateTime warehouseCutOffTimeInLocal, final DateTime userLocalTime)
	{

		final DateTimeComparator comparator = DateTimeComparator.getTimeOnlyInstance();
		final int comp = comparator.compare(warehouseCutOffTimeInLocal, userLocalTime);
		if (comp > 0)
		{
			return true;
		}
		return false;
	}

	/**
	 * @param sysDateTime
	 * @param warehouseCutOffTime
	 * @see gets warehouse current date and time
	 */
	private DateTime getWarehouseCurrentDateTime(final DateTime sysDateTime, final DateTime warehouseCutOffTime)
	{
		final DateTimeZone warehouseTimeZone = warehouseCutOffTime.getZone();
		return sysDateTime.withZone(warehouseTimeZone);
	}

	/**
	 * @param deliveryInfoData
	 * @param fromStandardDelDays
	 * @param toStandardDelDays
	 * @see set params for before cut off delivery
	 */
	private void setBeforeCutOffParameters(final DeliveryInfoData deliveryInfoData, final String fromStandardDelDays,
			final String toStandardDelDays)
	{

		deliveryInfoData.setDeliveryCutOff("BeforeCutOff");
		deliveryInfoData.setDeliveryDays(fromStandardDelDays);
		deliveryInfoData.setMaxDeliveryDays(toStandardDelDays);
	}

	/**
	 * @param deliveryInfoData
	 * @param fromStandardDelDays
	 * @param toStandardDelDays
	 * @see set params for after cut off delivery
	 */
	private void setAfterCutOffParameters(final DeliveryInfoData deliveryInfoData, final String fromStandardDelDays,
			final String toStandardDelDays)
	{

		deliveryInfoData.setDeliveryCutOff("AfterCutOff");
		deliveryInfoData.setDeliveryDays(getNextDayString(fromStandardDelDays));
		deliveryInfoData.setMaxDeliveryDays(getNextDayString(toStandardDelDays));
	}

	/**
	 * @param delDays
	 * @see get Next Day
	 */
	private String getNextDayString(final String delDays)
	{

		try
		{
			final int delDaysInt = Integer.parseInt(delDays);
			return String.valueOf(delDaysInt + 1);
		}
		catch (final NumberFormatException e)
		{
			LOGGER.error("NumberFormatException for Next Day Calculation" + e.getMessage());
		}
		return StringUtils.EMPTY;

	}

	/**
	 * @param regionCode
	 * @param warehouseCurrentDTime
	 *           see check if the current date for warehouse region is holiday
	 */
	private boolean checkIsHoliday(final String regionCode, final DateTime warehouseCurrentDTime)
	{

		final List<HolidayModel> holidayModels = apbCheckoutDao.getHolidayModelForRegionDate(regionCode, warehouseCurrentDTime);

		if (checkIsWeekend(warehouseCurrentDTime) ||CollectionUtils.isNotEmpty(holidayModels))
		{
			return true;
		}
		return false;
	}

	/**
	 * @param dateTime
	 * @return
	 * @see check if the current day for warehouse region is weekend
	 */
	private boolean checkIsWeekend(final DateTime dateTime)
	{
		final String dayOfWeek = Integer.toString(dateTime.getDayOfWeek());
		final String weekendDays = this.asahiConfigurationService.getString("warehouse.weekend.days.apb", " ");
		if (weekendDays.contains(dayOfWeek))
		{
			return true;
		}
		return false;
	}

	/**
	 * @param b2bUnitModel
	 * @return
	 * @see returns associated warehouse for the B2BUnit
	 */
	private WarehouseModel getWarehouseForB2BUnit(final B2BUnitModel b2bUnitModel)
	{
		WarehouseModel warehouseModel = null;
		if (null != b2bUnitModel && b2bUnitModel instanceof AsahiB2BUnitModel)
		{
			warehouseModel = ((AsahiB2BUnitModel) b2bUnitModel).getWarehouse();
		}
		if(null == warehouseModel)
		{
			warehouseModel = warehouseService.getWarehouseForCode(DEFAULT_WAREHOUSE_CODE);
			LOGGER.info("No warehouse found for Customer: " + (null != b2bUnitModel && null != b2bUnitModel.getUid() ? b2bUnitModel.getUid() : "") + ". Using default warehouse values. Cut off time for default warehouse is: " + warehouseModel.getCutOffTime());
		}
		return warehouseModel;
	}

	/**
	 * @param warehouseModel
	 * @return
	 * @see get region from warehouse
	 */
	private RegionModel getRegionFromWarehouse(final WarehouseModel warehouseModel)
	{
		RegionModel regionModel = null;
		if (warehouseModel != null)
		{
			final Collection<PointOfServiceModel> pointOfServiceModels = warehouseModel.getPointsOfService();
			if (CollectionUtils.isNotEmpty(pointOfServiceModels))
			{
				final PointOfServiceModel pointOfServiceModel = pointOfServiceModels.iterator().next();
				regionModel = getRegionFromPOS(pointOfServiceModel);
			}
		}
		return regionModel;
	}

	/**
	 * @param pointOfServiceModel
	 * @return
	 * @see gets region from warehouse's associated POS
	 */
	private RegionModel getRegionFromPOS(final PointOfServiceModel pointOfServiceModel)
	{
		RegionModel regionModel = null;
		if (null != pointOfServiceModel)
		{
			final AddressModel addressModel = pointOfServiceModel.getAddress();
			if (null != addressModel)
			{
				regionModel = addressModel.getRegion();
			}
		}
		return regionModel;
	}

	/**
	 * @param warehouseModel
	 * @param regionModel
	 * @return
	 * @see gets cut off time from warehouse or region
	 */
	private DateTime getCutOffTime(final DeliveryInfoData deliveryInfoData, final WarehouseModel warehouseModel, final RegionModel regionModel)
	{
		String cutOffTimeStr = StringUtils.EMPTY;
		DateTime zoneCutOffTime = null;

		if (null != warehouseModel)
		{
			final String whCutOffTimeStr = warehouseModel.getCutOffTime();
			if (StringUtils.isNotEmpty(whCutOffTimeStr))
			{
				cutOffTimeStr = whCutOffTimeStr;
			}
			else if (null != regionModel)
			{
				cutOffTimeStr = regionModel.getCutOffTime();
			}
			else if(StringUtils.isEmpty(cutOffTimeStr))
			{
				final WarehouseModel warehouseForCutOff = warehouseService.getWarehouseForCode(DEFAULT_WAREHOUSE_CODE);
				if(null != warehouseForCutOff.getCutOffTime())
				{
					cutOffTimeStr = warehouseForCutOff.getCutOffTime();
					LOGGER.info("No cut off time found for warehouse with code: " + warehouseModel.getCode() + ". Setting cut off time from default warehouse. Cut off time used: " + cutOffTimeStr);
				}
				else
				{
					cutOffTimeStr = DEFAULT_CUT_OFF_TIME;
					LOGGER.info("No cut off time found for warehouse with code: " + warehouseModel.getCode() + ". Setting cut off time as: " + cutOffTimeStr);
				}

			}
			if(null != deliveryInfoData)
			{
				final String cutOffTimeToDisplay = cutOffTimeStr.substring(0, cutOffTimeStr.lastIndexOf(' '));
				final String timezone = getTimeZoneToDisplay(cutOffTimeStr);
				deliveryInfoData.setCutOffDate(cutOffTimeToDisplay + " " + timezone);
			}

			final String cutOffTimeFormat = asahiConfigurationService.getString(CUT_OFF_TIME_FORMAT, null);
			final String zonedCutOffSTimeStr = getZonedCutOffTimeStr(cutOffTimeStr);
			zoneCutOffTime = ApbAddressTimeUtil.getCutOffDate(zonedCutOffSTimeStr, cutOffTimeFormat);
		}
		return zoneCutOffTime;
	}

	/**
	 * @return
	 * @see gets current B2BUnit for customer
	 */
	private B2BUnitModel getCurrentB2BUnit()
	{
		final UserModel user = this.userService.getCurrentUser();
		B2BUnitModel b2bUnit = null;
		if (null != user && user instanceof B2BCustomerModel)
		{
			final B2BCustomerModel customer = (B2BCustomerModel) user;
			b2bUnit = customer.getDefaultB2BUnit();
		}
		return b2bUnit;
	}
	/*
	 * (non-Javadoc)
	 *
	 * @see
	 * com.apb.core.checkout.service.ApbCheckoutService#getPaymentTypesForCustomer(com.sabmiller.core.model.AsahiB2BUnitModel,
	 * java.util.List) call to service to validate the SGA payment type.. if payment term IS C00C, then only i am allowed
	 * to show Account Payment Type
	 */
	@Override
	public List<B2BPaymentTypeData> getPaymentTypesForCustomer(final AsahiB2BUnitModel asahiB2BUnitModel,
			final List<B2BPaymentTypeData> paymentTypes)
	{

		final List<B2BPaymentTypeData> modPaymentTypes = new ArrayList<B2BPaymentTypeData>();

		if (CollectionUtils.isNotEmpty(paymentTypes) && null != asahiB2BUnitModel)
		{
			final String paymentTerm = asahiB2BUnitModel.getPaymentTerm();

			populatePaymentTypesForPmtTerm(paymentTypes, modPaymentTypes, paymentTerm);

			return modPaymentTypes;
		}
		return paymentTypes;
	}


	/**
	 * @param paymentTypes
	 * @param modPaymentTypes
	 * @param paymentTerm
	 *           The method populates payment types considering the payment term.
	 */
	private void populatePaymentTypesForPmtTerm(final List<B2BPaymentTypeData> paymentTypes,
			final List<B2BPaymentTypeData> modPaymentTypes, final String paymentTerm)
	{
		for (final B2BPaymentTypeData b2bPaymentTypeData : paymentTypes)
		{
			if (StringUtils.isEmpty(paymentTerm)
					|| asahiConfigurationService.getString("checkout.payment.terms.sga", "C00C").contains(paymentTerm))
			{
				addPaymentTypesForInvalidPmtTerm(modPaymentTypes, b2bPaymentTypeData);
			}
			else
			{
				addPaymentTypesForValidPmtTerm(modPaymentTypes, b2bPaymentTypeData);
			}
		}
	}


	/**
	 * @param modPaymentTypes
	 * @param b2bPaymentTypeData
	 *           The method populates payment types for the valid payment term.
	 */
	private void addPaymentTypesForValidPmtTerm(final List<B2BPaymentTypeData> modPaymentTypes,
			final B2BPaymentTypeData b2bPaymentTypeData)
	{
		if (!PAYMENTMODE_DELIVERY.equals(b2bPaymentTypeData.getCode()))
		{
			final Boolean userDeviceType = sessionService.getAttribute(ApbCoreConstants.IS_ON_ACCOUNT_DISABLED);

			if(!Boolean.TRUE.equals(userDeviceType) || !PAYMENTMODE_ACCOUNT.equals(b2bPaymentTypeData.getCode()))
			{
				modPaymentTypes.add(b2bPaymentTypeData);
			}

		}
	}


	/**
	 * @param modPaymentTypes
	 * @param b2bPaymentTypeData
	 *           The method populates payment types for the invalid payment term.
	 */
	private void addPaymentTypesForInvalidPmtTerm(final List<B2BPaymentTypeData> modPaymentTypes,
			final B2BPaymentTypeData b2bPaymentTypeData)
	{
		if ((!PAYMENTMODE_ACCOUNT.equals(b2bPaymentTypeData.getCode())) && (!PAYMENTMODE_DELIVERY.equals(b2bPaymentTypeData.getCode())))
		{
			modPaymentTypes.add(b2bPaymentTypeData);
		}
	}

	/*
	 * <p>This method will set device type from session to cart model</p>
	 *
	 * @param cartModel
	 *
	 * @see com.apb.core.checkout.service.ApbCheckoutService#setDeviceType(de.hybris.platform.core.model.order.CartModel)
	 */
	@Override
	public void setDeviceType(final CartModel cartModel)
	{
		if (asahiSiteUtil.isBDECustomer())
		{
			cartModel.setDeviceType(DEVICE_TYPE_STAFF_PORTAL);
		}
		else
		{
			final String userDeviceType = sessionService.getAttribute("userDeviceType");

			if (StringUtils.isNotEmpty(userDeviceType))
			{
				cartModel.setDeviceType(userDeviceType);
			}
		}
	}


	/*
	 * <p> This method will set the SGA specific custom fields in cart model </p>
	 *
	 * @param cartModel
	 *
	 * @see com.apb.core.checkout.service.ApbCheckoutService#setSgaSpecificFields(de.hybris.platform.core.model.order.
	 * CartModel)
	 */
	@Override
	public void setCustomFields(final CartModel cartModel)
	{
		final B2BUnitModel defaultB2bUnit = getCurrentB2BUnit();

		//Set the current site as company code.
		cartModel.setCompanyCode(cmsSiteService.getCurrentSite().getUid());

		//Set the customer order type from default b2b unit.
		if (null != defaultB2bUnit && defaultB2bUnit instanceof AsahiB2BUnitModel)
		{
			cartModel.setCustOrderType(((AsahiB2BUnitModel) defaultB2bUnit).getCustOrderType());
		}

	}

	@Override
	public List<AsahiProductInfo> getProductDetailsFromCart(final boolean updateCart, final long formQty, final String code) {
		return apbCheckoutDao.getProductDetailsFromCart(updateCart,formQty,code);
	}

	/* (non-Javadoc)
	 * @see com.apb.core.checkout.service.ApbCheckoutService#isDeliveryDateInValid(java.lang.String, java.lang.String)
	 * This method evaluates for the fringe case delivery scenario while placing the order.
	 */
	@Override
	public boolean isDeliveryDateInValid(final String recordId, final String reqDeliveryDate) {
		final DeliveryInfoData deliveryInfoData = this.getDeliveryInfo(recordId);
		if(null != deliveryInfoData && !deliveryInfoData.getBeforeCutOff() && StringUtils.isNotEmpty(reqDeliveryDate)){
			String deliveryDateDDMMYYYY= StringUtils.EMPTY;
			try {
			final Date delDate = (new SimpleDateFormat(ApbCoreConstants.DATE_PATTERN_DDMMMYYYY)).parse(reqDeliveryDate);
			deliveryDateDDMMYYYY= (new SimpleDateFormat(ApbCoreConstants.DEFER_DELIVERY_DATEPATTERN)).format(delDate);
				}
			catch (final ParseException e) {
				LOGGER.error("Parse Exception caught in converting date pattern" + e.getMessage());
			}
			final List<String> deliveryDates = deliveryInfoData.getDeferredDeliveryOptions();
			if(CollectionUtils.isNotEmpty(deliveryDates) && !deliveryDates.contains(deliveryDateDDMMYYYY)){
					return true;
				}
		}
		return false;
	}
	private String getZonedCutOffTimeStr(final String cutOffTimeStr)
	{
		final String timeZone = cutOffTimeStr.substring(cutOffTimeStr.lastIndexOf(' ') + 1);
		String zoneId = asahiConfigurationService.getString(TIMEZONE_ZONE_ID + timeZone, null);
		if (StringUtils.isEmpty(zoneId )){
			zoneId = "Australia/Melbourne";
			LOGGER.error("Cut off time Zone ID config missing for " + timeZone + ". Please check configurations. Using Default : " + zoneId); // Tell that which config is missing.
		}
		return  cutOffTimeStr.replace(timeZone, zoneId);
	}

	private String getTimeZoneToDisplay(final String cutOffTimeStr)
	{
		final String timeZone = cutOffTimeStr.substring(cutOffTimeStr.lastIndexOf(' ') + 1);
		return asahiConfigurationService.getString(TIMEZONE_DISPLAY_VAL + timeZone, timeZone);
	}

	/**
	 * @param cutOffTimeStr
	 * @return
	 * @see gets local cut off time for warehouse
	 */
	private DateTime getZoneCutOffTime(final String cutOffTimeStr)
	{
		if (StringUtils.isNotEmpty(cutOffTimeStr))
		{
			final String cutOffDateTimeFormat = CONVERSIONDATEFORMAT;
			final String cutOffTimeStrm = getCutOffDateInFormat(cutOffTimeStr);
			final String defaultCutOffTimeStrm = "20121130 12:30:00.000 +1100";

			final DateTimeFormatter formatter = DateTimeFormat.forPattern(cutOffDateTimeFormat);

			try
			{
				return formatter.withOffsetParsed().parseDateTime(cutOffTimeStrm);
			}
			catch (final IllegalArgumentException e)
			{
				LOGGER.error("IllegalArgumentException caught in getZoneCutOffTime." + e.getMessage(), e);
				LOGGER.info("Format for cut off date: " + cutOffTimeStrm + " is not correct. Setting default cut off time of 12:30 PM");
				return formatter.withOffsetParsed().parseDateTime(defaultCutOffTimeStrm);
			}
		}
		return null;
	}

	/**
	 * @param cutOffTimeStr
	 * @return
	 * @see frames cut off date time in yyyyMMdd HH:mm:ss.SSSSSS Z format for processing
	 */
	private String getCutOffDateInFormat(final String cutOffTimeStr)
	{

		String cutOffDateTime = StringUtils.EMPTY;

		if (cutOffTimeStr.length() >= 10)
		{
			final String time24 = getTime24(cutOffTimeStr.substring(0, 8));
			final String hhmmssSSS = time24 + ":" + DUMMYSECONDS;
			final String cutOffTimeStr2 = hhmmssSSS + " " + getTimeZoneString(cutOffTimeStr);
			cutOffDateTime = DUMMYDATE + " " + cutOffTimeStr2;
		}
		return cutOffDateTime;
	}

	/**
	 * @param time12
	 * @return
	 * @see gets time in 24 hour format
	 */
	private String getTime24(final String time12)
	{

		try
		{
			final SimpleDateFormat inFormat = new SimpleDateFormat("hh:mm aa");
			final SimpleDateFormat outFormat = new SimpleDateFormat("HH:mm");
			return outFormat.format(inFormat.parse(time12));
		}
		catch (final ParseException e)
		{
			LOGGER.error("Parse Exception caught in converting time to 24 hour format" + e.getMessage());
		}
		return StringUtils.EMPTY;
	}

	private String getTime12(final String time24)
	{

		try
		{
			final SimpleDateFormat inFormat = new SimpleDateFormat("HH:mm");
			final SimpleDateFormat outFormat = new SimpleDateFormat("hh:mm aa");
			return outFormat.format(inFormat.parse(time24));
		}
		catch (final ParseException e)
		{
			LOGGER.error("Parse Exception caught in converting time to 12 hour format" + e.getMessage());
		}
		return StringUtils.EMPTY;
	}

	/**
	 * @param cutOffTimeStr
	 * @return
	 * @see gets offset corresponding to the timezone
	 */
	private String getTimeZoneString(final String cutOffTimeStr)
	{

		String zoneId = StringUtils.EMPTY;

		if (cutOffTimeStr.contains("AWST"))
		{
			zoneId = ZONEID_AWST;
		}
		else if (cutOffTimeStr.contains("ACWST"))
		{
			zoneId = ZONEID_ACWST;
		}
		else if (cutOffTimeStr.contains("ACST"))
		{
			zoneId = ZONEID_ACST;
		}
		else if (cutOffTimeStr.contains("AEST"))
		{
			zoneId = ZONEID_AEST;
		}
		else if (cutOffTimeStr.contains("LHST"))
		{
			zoneId = ZONEID_LHST;
		}
		return getOffsetForZoneId(zoneId);
	}

	/**
	 * @param zoneIdStr
	 * @return
	 * @see returns offset for zoneID
	 */
	private String getOffsetForZoneId(final String zoneIdStr)
	{
		if (StringUtils.isNotEmpty(zoneIdStr))
		{
			final Instant now = Instant.now();
			final ZoneId zoneId = ZoneId.of(zoneIdStr);
			if (null != zoneId)
			{
				final ZonedDateTime zonedDateTime = ZonedDateTime.ofInstant(now, zoneId);
				if (null != zonedDateTime)
				{
					return getOffsetStringForZone(zonedDateTime.getOffset());
				}
			}
		}
		return StringUtils.EMPTY;
	}

	/**
	 * @param offset
	 * @return
	 * @see returnsOffset string for zone
	 */
	private String getOffsetStringForZone(final ZoneOffset zoneOffset)
	{
		return zoneOffset.toString().replace(":", "");
	}

	@Override
	public B2BUnitModel getB2BUnitForUid(final String b2bUnit)
	{
		return this.apbCheckoutDao.getB2BUnitForUid(b2bUnit);
	}
	@Override
	public Set<String> getCustomerEmailIds(){
		final Set<String> customerEmailIds = new HashSet<String>();
		final B2BUnitModel b2bUnitModel = cartService.getSessionCart().getUnit();
		if(b2bUnitModel instanceof AsahiB2BUnitModel && CollectionUtils.isNotEmpty(b2bUnitModel.getMembers())) {
			final AsahiB2BUnitModel asahiB2BUnit = (AsahiB2BUnitModel) b2bUnitModel;
			customerEmailIds.addAll(b2bUnitModel.getMembers().stream()
					.filter(member -> member instanceof B2BCustomerModel && !(member instanceof BDECustomerModel)
							&& BooleanUtils.isTrue(((B2BCustomerModel) member).getActive())
							&& (CollectionUtils.isEmpty(asahiB2BUnit.getDisabledUser())
									|| !asahiB2BUnit.getDisabledUser().contains(member.getUid())))
					.collect(Collectors.toList()).stream().map(member -> member.getUid()).collect(Collectors.toSet()));
		}
		final CartModel cartModel = cartService.getSessionCart();
		if( null != cartModel) {
			customerEmailIds.addAll(cartModel.getBdeOrderCustomerEmails());
		}
		return customerEmailIds;
	}

	@Override
	public EmployeeModel searchBDEByName(final String name) {
		final List<EmployeeModel> userList = employeeDao.find(Collections.singletonMap("name", name));

		return userList.isEmpty() ? null : (EmployeeModel) userList.get(0);
	}

	@Override
	public void saveBDEOrderDetails(final BdeOrderDetailsForm bdeCheckoutForm) {

		final CartModel cartModel = cartService.getSessionCart();
		final List<String> customerEmailIds = new ArrayList<>();
		final List<String> userEmailIds = new ArrayList<>();
		String customerFirstName = null;
		if(null != bdeCheckoutForm.getCustomers()) {

			customerEmailIds.addAll(bdeCheckoutForm.getCustomers().stream().map(customer->customer.getEmail()).collect(Collectors.toList()));
		}
		if(null != bdeCheckoutForm.getUsers()) {

			userEmailIds.addAll(bdeCheckoutForm.getUsers().stream().map(user->user.getEmail()).collect(Collectors.toList()));
			final UserModel userModel= userService.getUserForUID(userEmailIds.get(0));
			customerFirstName = userModel != null? userModel.getName():null;
		}
		cartModel.setBdeOrderCustomerFirstName(customerFirstName);
		cartModel.setBdeOrder(Boolean.TRUE);
		cartModel.setBdeOrderCustomerEmails(customerEmailIds);
		cartModel.setBdeOrderUserEmails(userEmailIds);
		cartModel.setBdeOrderEmailText(bdeCheckoutForm.getEmailText());
		modelService.save(cartModel);
		modelService.refresh(cartModel);

	}
}