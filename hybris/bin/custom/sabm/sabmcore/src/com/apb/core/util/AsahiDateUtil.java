package com.apb.core.util;

import java.util.Collections;
import java.util.Comparator;
import java.util.Date;
import java.util.List;

import jakarta.annotation.Resource;

import org.joda.time.Days;
import org.joda.time.LocalDate;

import com.apb.core.service.config.AsahiConfigurationService;


/**
 * Class to be used where ever site related date values needs to be fetched.
 */
public class AsahiDateUtil
{

	/** The asahi configuration service. */
	@Resource(name = "asahiConfigurationService")
	private AsahiConfigurationService asahiConfigurationService;

	/**
	 * @param modDates
	 *           This method sorts the dates in increasing order
	 */
	public void sortDates(final List<Date> dates)
	{
		Collections.sort(dates, new Comparator<Date>()
		{
			@Override
			public int compare(final Date date1, final Date date2)
			{
				return date1.compareTo(date2);
			}
		});
	}

	/**
	 * The Method will validate the requested pay access date with the current time stamp.
	 *
	 * @param date
	 * @return true/false
	 */
	public Boolean validateDate(final Date date)
	{

		final double delta = new Date().getTime() - date.getTime();
		if ((delta / (3600 * 1000)) > asahiConfigurationService.getInt("sam.access.expired.email.timeout", 72))
		{
			return Boolean.FALSE;
		}
		return Boolean.TRUE;
	}

	public String getDifferenceInDays(final Date startDate, final Date endDate)
	{
		final LocalDate d1 = new LocalDate(startDate);
		final LocalDate d2 = new LocalDate(new Date(endDate.getTime()));
		int daysRemaining = Days.daysBetween(d1, d2).getDays() + 1;
		if (daysRemaining < 0)
		{
			daysRemaining = 0;
		}
		return String.valueOf(daysRemaining);
	}

}
