/**
 *
 */
package com.sabmiller.core.b2b.services;

import de.hybris.platform.b2b.model.B2BUnitModel;
import de.hybris.platform.store.BaseStoreModel;

import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;

import com.sabmiller.facades.delivery.data.DeliveryModePackTypeDeliveryDatesData;


/**
 * The Interface SABMDeliveryDateCutOffService.
 */
public interface SABMDeliveryDateCutOffService
{

	/**
	 * Checks if is cutoff time exceeded.
	 *
	 * @param b2bUnitModel
	 *           the b2b unit model
	 * @return true, if is cut off time exceeded
	 */
	boolean isCutOffTimeExceeded(B2BUnitModel b2bUnitModel);

	/**
	 * Checks if the cut off time has exceeded for current date and the b2b unit in session.
	 *
	 * @return true, if is cut off time exceeded
	 */
	boolean isCutOffTimeExceeded();

	/**
	 * Checks if is valid delivery date.
	 *
	 * @param checkDate
	 *           the check date
	 * @return true, if is valid delivery date
	 */
	boolean isValidDeliveryDate(Date checkDate);

	/**
	 * Checks if is valid delivery date.
	 *
	 * @param b2bUnitModel
	 *           the b2b unit model
	 * @param checkDate
	 *           the check date
	 * @return true, if is valid delivery date
	 */
	boolean isValidDeliveryDate(B2BUnitModel b2bUnitModel, Date checkDate);

	/**
	 * Gets the session customer next available day delivery. It's calculated using plant unloadingPoint and holiday
	 * calendar.
	 *
	 * @return the next available delivery date. If the calculation is not possible for missing data, it returns
	 *         tomorrow.
	 */
	Date getSafeNextAvailableDeliveryDate();


	/**
	 * @param b2bunit
	 * @return the next available delivery date. If the calculation is not possible for missing data, it returns
	 *         tomorrow.
	 */
	Date getSafeNextAvailableDeliveryDate(B2BUnitModel b2bunit);

	/**
	 * Disabled calendar dates of the session customer, calculated using plant unloadingPoints and holiday calendar.
	 *
	 * @return the list of disabled dates
	 */
	Set<Date> disabledCalendarDates();


	/**
	 * Disabled calendar dates of the customer, calculated using plant unloadingPoints and holiday calendar.
	 *
	 * @param b2bUnitModel
	 *           the b2b unit model
	 * @return the list of available dates
	 */
	Set<Date> disabledCalendarDates(B2BUnitModel b2bUnitModel);

	/**
	 * Enabled calendar dates of the session customer, calculated using plant unloadingPoints and holiday calendar.
	 *
	 * @return the list of enabled dates
	 */
	Set<Date> enabledCalendarDates();

	/**
	 * Enabled calendar dates of the customer, calculated using plant unloadingPoints and holiday calendar.
	 *
	 * @param b2bUnitModel
	 *           the b2b unit model
	 * @return the list of enabled dates
	 */
	Set<Date> enabledCalendarDates(B2BUnitModel b2bUnitModel);

	/**
	 * @param b2bUnitModel
	 * @param deliveryDate
	 * @return
	 */
	String getCutOffTime(B2BUnitModel b2bUnitModel, Date deliveryDate);

	HashMap<String, String> getCutOffTimeforCalendarToDisplay(B2BUnitModel b2bUnitModel, Date deliveryDate);

	String getServerTimeInBaseStoreTimeZone(final B2BUnitModel b2bUnitModel,final boolean defaultDateTimeFormat);

	/**
	 * @param b2bUnit
	 * @return
	 */
	List<DeliveryModePackTypeDeliveryDatesData> getDeliveryModePackTypeDeliveryDatesData(B2BUnitModel b2bUnit,
			final boolean enabled);

	Map<String, Object> getDeliveryDatePackType(final B2BUnitModel b2bUnit, final Date deliveryDate);

    /**
	 * @param b2bUnit
	 * @param baseStore
	 * @return
	 */
	Date getplantcutoffstoreTime(B2BUnitModel b2bUnit, BaseStoreModel baseStore);




}
