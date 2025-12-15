/**
 *
 */
package com.sabmiller.core.deals.services;

import com.sabmiller.core.deals.vo.DealsResponse;
import com.sabmiller.core.deals.vo.DealsResponse.DealItem;
import com.sabmiller.core.util.SabmDateUtils;
import com.sabmiller.core.util.SabmStringUtils;
import de.hybris.platform.util.Config;
import org.apache.commons.collections4.ListUtils;
import org.apache.commons.collections4.MapUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.*;


/**
 * Deals Priority Implementation. This makes use of inner class {@link DealCalendar} to a large extent in order to merge
 * overlapping deals
 *
 * @author joshua.a.antony
 */
public class DefaultDealsPriorityService implements DealsPriorityService
{
	private static final Logger LOG = LoggerFactory.getLogger(DefaultDealsPriorityService.class);


	/**
	 * For Overlapping deals, use priority to determine the correct deal to be displayed. This might involve shuffling
	 * the deal validity ranges. This method first fetches all the overlapping deals, sorts them by Priority and then
	 * invokes adjustDates() to perform the actual shuffle/removal/adding of deals.
	 */
	@Override
	public void mergeOverlappingDeals(final DealsResponse discountResponse)
	{
		final boolean isEnabled = Config.getBoolean("deals.priority.enabled", true);
		if (isEnabled)
		{
			final Map<String, List<DealItem>> map = fetchOverlappingRecords(discountResponse.getItems());

			for (final List<DealItem> i : MapUtils.invertMap(map).keySet())
			{
				discountResponse.getItems().removeAll(i);//Remove all the overlapping deals from the original response as these will be added back after merge
			}

			for (final Map.Entry<String, List<DealItem>> entry : map.entrySet())
			{
				final List<DealItem> items = entry.getValue();
				sortByPriority(items);
				adjustDates(items);
				discountResponse.getItems().addAll(items); //Added the merged overlapping deal items back
			}
		}
		else
		{
			LOG.warn("Deals Priority Implementation is Disabled. Please turn deals.priority.enabled flag on to enable it");
		}
	}

	/**
	 * Adjust the from and to dates (deal validity) for the items based on priority. If there are overlapping deals, then
	 * the higher priority deals takes precidence over the overlapping period.
	 *
	 * Example : Deal 1 => Jan1-Jan15, Priority 1 Deal 2 => Jan5-Jan20, Priority 2 Deal 3 => Dec15-Feb15, Priority 3 In
	 * this case, the adjusted/reshuffled date would be Deal1 => Jan1-Jan15, Deal 2 => Jan16-Jan20, Deal3 => Dec15-Dec31
	 * and Jan21-Feb15
	 *
	 */
	protected void adjustDates(final List<DealItem> items)
	{
		LOG.debug("In adjustDates(). Material :{} , Total Items : {} ", items.get(0).getMaterial(), items.size());

		final List<DealItem> splitDeals = new ArrayList<DealItem>();
		final List<DealItem> invalidDeals = new ArrayList<DealItem>();
		final DealCalendar dealCalendar = new DealCalendar();
		for (final DealItem eachItem : items)
		{
			dealCalendar.logCalendarStatus();

			final Date from = SabmDateUtils.toDate(eachItem.getValidFrom());
			final Date to = SabmDateUtils.toDate(eachItem.getValidTo());

			LOG.debug("Material {} . Invoking dealCalendar.bookSlots() to book slots for {} - {} ", eachItem.getMaterial(),
					SabmDateUtils.toFormattedString(from), SabmDateUtils.toFormattedString(to));

			final DealCalendarResponse calendarResponse = dealCalendar.bookSlots(from, to);

			if (AvailabilityStatus.NOT_AVAILABLE.equals(calendarResponse.availabilityStatus))
			{
				invalidDeals.add(eachItem);
			}
			else if (AvailabilityStatus.PARTIAL.equals(calendarResponse.availabilityStatus))
			{
				final Map<Date, Date> splitDates = calendarResponse.splitDates;
				if (splitDates != null && !splitDates.isEmpty())
				{
					invalidDeals.add(eachItem);
					splitDeals.addAll(splitDeals(splitDates, eachItem));
				}
				else
				{
					eachItem.setValidFrom(SabmDateUtils.getGregorianCalendar(calendarResponse.from));
					eachItem.setValidTo(SabmDateUtils.getGregorianCalendar(calendarResponse.to));
				}
			}
		}

		//Remove all the invalid deals (that are overwritten by a higher priority one)
		items.removeAll(invalidDeals);

		//Add all the split deals
		items.addAll(splitDeals);

		LOG.debug("Total Invalid Deals = {}, Split Deals = {}, Total Items {} ", invalidDeals.size(), splitDeals.size(),
				items.size());
	}

	/**
	 * Split each {@link DealItem} into multiple deals based on the splitDates map. For each entry in splitDates, a new
	 * items is created (which is clone of the passed item). Thus, this method returns deal item that are exactly similar
	 * with different validFrom and validTo
	 */
	private List<DealItem> splitDeals(final Map<Date, Date> splitDates, final DealItem item)
	{
		final List<DealItem> splitDeals = new ArrayList<DealItem>();
		for (final Map.Entry<Date, Date> entry : splitDates.entrySet())
		{
			final DealItem newItem = clone(item);
			newItem.setValidFrom(SabmDateUtils.getGregorianCalendar(entry.getKey()));
			newItem.setValidTo(SabmDateUtils.getGregorianCalendar(entry.getValue()));
			splitDeals.add(newItem);
		}
		return splitDeals;
	}


	private void sortByPriority(final List<DealItem> items)
	{
		Collections.sort(items, new Comparator<DealItem>()
		{
			@Override
			public int compare(final DealItem o1, final DealItem o2)
			{
				final String o1Priority = SabmStringUtils.stripLeadingZeroes(o1.getPriority());
				final String o2Priority = SabmStringUtils.stripLeadingZeroes(o2.getPriority());

				return Integer.valueOf(o1Priority).compareTo(Integer.valueOf(o2Priority)); //Per SAP, Priority will never by empty
			}
		});
	}


	/**
	 * Returns map of the trigger points and the items against the trigger points.The trigger point for deal are
	 * Material, UOM and Quantity. Thus, if SAP returns multiple deals with the same trigger point, we might have to
	 * re-adjust the dates by priority(sent from SAP)
	 */
	@Override
	public Map<String, List<DealItem>> fetchOverlappingRecords(final List<DealItem> items)
	{
		final Map<String, List<DealItem>> map = new HashMap<String, List<DealItem>>();
		for (final DealItem discountItem : ListUtils.emptyIfNull(items))
		{
			final String trigger = discountItem.getMaterial() + discountItem.getMinimumQuantity() + discountItem.getUnitOfMeasure();
			if (map.containsKey(trigger))
			{
				map.get(trigger).add(discountItem);
			}
			else
			{
				final List<DealItem> discountItems = new ArrayList<DealItem>();
				discountItems.add(discountItem);
				map.put(trigger, discountItems);
			}
		}

		if (LOG.isDebugEnabled())
		{
			logOverlappingRecord(map);
		}

		return map;
	}

	private void logOverlappingRecord(final Map<String, List<DealItem>> map)
	{
		LOG.debug("Logging overlapping records...");
		for (final Map.Entry<String, List<DealItem>> entry : map.entrySet())
		{
			LOG.debug("The trigger piont (key) is : {}. Value : {} ", entry.getKey(), entry.getValue());
		}
	}


	/**
	 * Represents a Calendar having entries of the deal ranges ( from and to date). This is used during the Deal Priority
	 * processing, whereby- every time a valid deal is detected, the same is add to the calendar. Thus, the Calendar can
	 * look up for the available dates for subsequent deals and also provides a snapshot of the currently used dates.
	 *
	 * The calendar uses an internal {@link TreeMap} to keep the deals sorted by the From Date of the deal. This makes it
	 * easier to perform the algorithm for merging overlapping deals.
	 *
	 * @author joshua.a.antony
	 */
	static class DealCalendar
	{
		private final Map<Date, Date> selectedDates = new TreeMap<Date, Date>();

		/**
		 * Blocks the calendar for these dates. Any future request for these dates will be ignored.
		 */
		private void blockCalendar(final Date from, final Date to)
		{
			LOG.debug("Blocking the Deal Calendar {}-{}", SabmDateUtils.toFormattedString(from), SabmDateUtils.toFormattedString(to));
			logCalendarStatus();

			selectedDates.put(from, to);
		}

		/**
		 * The concept is similar to functionality of booking an appointment with doctors/company. This method first
		 * checks if the entire date range (from & to) is available, if yes - adds it to the calendar and blocks it.If
		 * entire date range is not available, it checks for partial date range whereby the from,to might have to be
		 * adjusted. In addition to adjusting the from-to, if there are other slots booked in between these date ranges,
		 * then the date range is split into number of slots and then allocated.
		 */
		private DealCalendarResponse bookSlots(final Date from, final Date to)
		{
			LOG.debug("In bookSlots(). Checking if From {} and To {} needs to be adjusted ", SabmDateUtils.toFormattedString(from),
					SabmDateUtils.toFormattedString(to));

			if (available(from, to))
			{
				LOG.debug(
						"There is a vacant spot available for date range of {} - {} - i.e there are absolutely no overlapping deals anywhere in this date range.",
						SabmDateUtils.toFormattedString(from), SabmDateUtils.toFormattedString(to));

				blockCalendar(from, to);
				return new DealCalendarResponse(AvailabilityStatus.AVAILABLE);
			}
			if (notAvailable(from, to))
			{
				LOG.debug(
						"All the dates between from {} and to (inclusive) {} are taken by some other deals. Ignore the current deal item and move on",
						SabmDateUtils.toFormattedString(from), SabmDateUtils.toFormattedString(to));
				return new DealCalendarResponse(AvailabilityStatus.NOT_AVAILABLE);
			}

			LOG.debug(
					"There are other deals overlapping with this date range {} - {} => Partial Availability. Might have to adjust the from/to dates along with splitting deal",
					SabmDateUtils.toFormattedString(from), SabmDateUtils.toFormattedString(to));

			Date newFrom = from;
			Date newTo = to;
			final DealCalendarResponse response = new DealCalendarResponse(AvailabilityStatus.PARTIAL);
			if (alreadyTaken(from))
			{
				newFrom = nextAvailableFrom(from);
				LOG.debug("From Date is already taken by other deals. Adjust the from date of this deal to {} ",
						SabmDateUtils.toFormattedString(newFrom));
			}
			if (alreadyTaken(to))
			{
				newTo = nextAvailableTo(to);
				LOG.debug("'To Date' is already taken by other deals. Adjusting the to date of this deal to {} ",
						SabmDateUtils.toFormattedString(newTo));
			}
			if (anythingInBetween(newFrom, newTo))
			{
				logCalendarStatus();
				LOG.debug(
						"At this point, the from/to date have been adjusted to {}-{}. Howeer, there are some slots in between these dates that have been already taken. "
								+ "Hence, splitting this into multiple slots(deals)", SabmDateUtils.toFormattedString(newFrom),
						SabmDateUtils.toFormattedString(newTo));

				response.splitDates = availableSlots(newFrom, newTo);
				for (final Date fromDate : response.splitDates.keySet())
				{
					final Date toDate = response.splitDates.get(fromDate);
					blockCalendar(fromDate, toDate);

					LOG.debug("After Split. From Date {} To Date {} ", SabmDateUtils.toFormattedString(fromDate),
							SabmDateUtils.toFormattedString(toDate));

				}
			}
			else
			{
				blockCalendar(newFrom, newTo);
			}

			response.from = newFrom;
			response.to = newTo;

			return response;
		}

		/**
		 * This assumes that there is an overlap with the passed date. Thus, it looks for existing deal whose from date is
		 * before the passed in date and to date is after the passed in date, i.e basically looks for any other dates that
		 * overlap with the from date. 1 day is added to the returned overlapped date's 'to-date'. Example: if 2016/01/31
		 * is passed to this method and there is a deal between 2016/01/15 and 2016/02/10, then this method would return
		 * 2016/02/11 as the next available from date.
		 */
		private Date nextAvailableFrom(final Date from)
		{
			Date fromDate = from;
			Date d1;
			while ((d1 = findOverlappingDate(fromDate)) != null)
			{
				fromDate = SabmDateUtils.plusOneDay(selectedDates.get(d1));
				LOG.debug("In nextAvailableFrom() - Added 1 day. Verify if this new FROM date {} is available ",
						SabmDateUtils.toFormattedString(fromDate));
			}
			return fromDate;
		}


		/**
		 * Example: if 2016/01/31 is passed to this method and there is a deal between 2016/01/15 and 2016/02/10, then
		 * this method would return 2016/01/14 as the next available to date.
		 */
		private Date nextAvailableTo(final Date to)
		{
			Date toDate = to;
			Date d1;
			while ((d1 = findOverlappingDate(toDate)) != null)
			{
				toDate = SabmDateUtils.minusOneDay(d1);
				LOG.debug("In nextAvailableTo() - Subtracted 1 day. Verify if this new TO date {} is available ",
						SabmDateUtils.toFormattedString(toDate));
			}
			return toDate;
		}

		/**
		 * Fetches all the vacant dates available in between the from and the to date. Example: if from=2016/01/20 , to =
		 * 2016/02/10 and there is an existing deal (calendar is blocked) between 2016/01/25 - 2016/01/31, this method
		 * would return multiple deals => 2016/01/20 - 2016/01/24 and 2016/02/01 - 2016/02/20 as the vacant dates.
		 */
		private Map<Date, Date> availableSlots(final Date from, final Date to)
		{
			Date newFrom = from;
			final Map<Date, Date> availableInBetweenDates = new TreeMap<Date, Date>();
			for (final Date selectedFromDate : inBetweenBookedSlots(from, to).keySet())
			{
				final Date toDate = selectedDates.get(selectedFromDate);
				if (SabmDateUtils.beforeOrEqual(selectedFromDate, newFrom) && SabmDateUtils.afterOrEqual(toDate, newFrom))
				{
					newFrom = SabmDateUtils.plusOneDay(toDate);
					continue;
				}
				final Date newTo = SabmDateUtils.minusOneDay(selectedFromDate);
				availableInBetweenDates.put(newFrom, newTo);
				newFrom = SabmDateUtils.plusOneDay(toDate);
			}

			//This is to handle the final slot. The iteration above would not take care of that!
			if (SabmDateUtils.afterOrEqual(to, newFrom))
			{
				availableInBetweenDates.put(newFrom, to);
			}
			return availableInBetweenDates;
		}

		/**
		 * Check if the deal can reuse the entire from and to date range. This method checks if 1)The from date is not
		 * already taken 2) To date is not already taken 3) There are no deals lying between from and to dates
		 */
		private boolean available(final Date from, final Date to)
		{
			return !alreadyTaken(from) && !alreadyTaken(to) && !anythingInBetween(from, to);

		}

		/**
		 * Check if there are any spots(dates) available between from and to dates.This method checks 1)The from date is
		 * already taken 2)To date is already taken and 3) There are not other blocked dates between the from and to
		 */
		private boolean notAvailable(final Date from, final Date to)
		{
			return alreadyTaken(from) && alreadyTaken(to) && !isThereAnyFreeDatesInBetween(from, to);
		}

		/**
		 * Check if there are already deals in between the from and to date,
		 */
		private boolean anythingInBetween(final Date from, final Date to)
		{
			return !inBetweenBookedSlots(from, to).isEmpty();
		}


		/**
		 * Fetch all the dates that have been already blocked between the from and the to date.
		 */
		private Map<Date, Date> inBetweenBookedSlots(final Date from, final Date to)
		{
			final Map<Date, Date> inBetweenDates = new TreeMap<>();
			for (final Map.Entry<Date, Date> entry : selectedDates.entrySet())
			{
				if (entry.getKey().after(from) && entry.getValue().before(to))
				{
					inBetweenDates.put(entry.getKey(), entry.getValue());
				}
			}
			return inBetweenDates;
		}

		boolean isThereAnyFreeDatesInBetween(final Date from, final Date to)
		{
			Date start = from;
			Date d1;
			while ((d1 = findOverlappingDate(start)) != null)
			{
				start = SabmDateUtils.plusOneDay(selectedDates.get(d1));
			}
			return start.before(to);
		}

		boolean alreadyTaken(final Date date)
		{
			return findOverlappingDate(date) != null;
		}

		Date findOverlappingDate(final Date date)
		{
			for (final Map.Entry<Date, Date> entry : selectedDates.entrySet())
			{
				if (SabmDateUtils.beforeOrEqual(entry.getKey(), date) && SabmDateUtils.afterOrEqual(entry.getValue(), date))
				{
					return entry.getKey();
				}
			}
			return null;
		}

		void logCalendarStatus()
		{
			if (LOG.isDebugEnabled())
			{
				LOG.debug("Current Calendar Slots =>>>>>");
				for (final Map.Entry<Date, Date> entry : selectedDates.entrySet())
				{
					LOG.debug("{} - {} ", entry.getKey(), entry.getValue());
				}
			}
		}

	}

	private enum AvailabilityStatus
	{
		AVAILABLE, NOT_AVAILABLE, PARTIAL;
	}


	private static class DealCalendarResponse
	{
		private Date from, to;
		private final AvailabilityStatus availabilityStatus;
		private Map<Date, Date> splitDates;

		public DealCalendarResponse(final AvailabilityStatus availabilityStatus)
		{
			this.availabilityStatus = availabilityStatus;
		}
	}

	private DealItem clone(final DealItem item)
	{
		final DealItem newItem = new DealItem();
		newItem.setAmount(item.getAmount());
		newItem.setCalcType(item.getCalcType());
		newItem.setConditionType(item.getConditionType());
		newItem.setMaterial(item.getMaterial());
		newItem.setMinimumQuantity(item.getMinimumQuantity());
		newItem.setPriority(item.getPriority());
		newItem.setSaleUnit(item.getSaleUnit());
		newItem.setUnit(item.getUnit());
		newItem.setUnitOfMeasure(item.getUnitOfMeasure());
		newItem.setUnitOfMeasure2(item.getUnitOfMeasure2());
		return newItem;
	}


}
