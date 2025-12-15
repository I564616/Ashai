/**
 *
 */
package com.sabmiller.core.util;

import de.hybris.platform.util.Config;

import javax.xml.datatype.DatatypeConfigurationException;
import javax.xml.datatype.DatatypeConstants;
import javax.xml.datatype.DatatypeFactory;
import javax.xml.datatype.XMLGregorianCalendar;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.GregorianCalendar;
import java.util.List;
import java.util.Objects;
import java.util.Set;
import java.util.TimeZone;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.apache.commons.collections4.CollectionUtils;
import org.apache.commons.lang3.StringUtils;
import org.apache.commons.lang3.time.DateUtils;
import org.joda.time.DateTime;
import org.joda.time.DateTimeComparator;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.sabmiller.core.constants.SabmCoreConstants;


/**
 * @author joshua.a.antony
 *
 */
public class SabmDateUtils
{
	private static final Logger LOG = LoggerFactory.getLogger(SabmDateUtils.class);

	public static final String DATE_PATTERN = "dd/MM/yyyy hh:mm a";

	public static Date getCurrentDate(final String format)
	{
		try
		{
			final Date today = new Date();
			final SimpleDateFormat sdf = new SimpleDateFormat(format);
			return sdf.parse(sdf.format(today));
		}
		catch (final ParseException e)
		{
			LOG.error("Error occured during date conversion. Returning null ", e);
			return null;
		}
	}

	public static String getTimeInTimeZone(TimeZone timeZone,Date date,String datePattern)
	{
		SimpleDateFormat sdf = new SimpleDateFormat(datePattern);
		sdf.setTimeZone(timeZone);
		return sdf.format(date);
	}

	public static Date getDate(final String dateStr, final String format) throws ParseException
	{
		if (StringUtils.isNotBlank(dateStr))
		{
			final SimpleDateFormat sdf = new SimpleDateFormat(format);
			sdf.setLenient(false);
			return sdf.parse(dateStr);
		}
		return null;
	}

	/*
	  the defaultDateTimeFormat will indictae whether to use SimpleDateFormat
	  default dd/MM/yyyy hh:mm a:  == e.g 24/10/2018 12:00 PM
	  not default : d'" + dayNumberSuffix + "' MMMM yyyy 'at' hh:mm a"  ===e.g 24th October 2018 at 01:29 PM
	 */
    public static String extractDateString(Calendar storeCal, Date date, TimeZone zone, boolean defaultDateTimeFormat )
	{
		if (Objects.isNull(storeCal))
		{
			storeCal = Calendar.getInstance();
			storeCal.setTime(date);
		}
		String dayNumberSuffix = SabmDateUtils.getDayNumberSuffix(storeCal.get(Calendar.DAY_OF_MONTH));
		final SimpleDateFormat newDateAndTime = new SimpleDateFormat(" d'" + dayNumberSuffix + "' MMMM yyyy 'at' hh:mm a");

		final SimpleDateFormat defaultDateAndTime = new SimpleDateFormat("dd/MM/yyyy hh:mm a");

		/*
		  this is to fix order cut off , deal notification, order delivered cronjob parseException issue,  the data format has to be same as SabmDateUtils.DATE_PATTERN which is below:
		  	public static final String DATE_PATTERN = "dd/MM/yyyy hh:mm a";
		 */

        if (Objects.nonNull(zone)) {
			defaultDateAndTime.setTimeZone(zone);
			newDateAndTime.setTimeZone(zone);
        }
		String result = "";

        if(defaultDateTimeFormat) {
			result = defaultDateAndTime.format(storeCal.getTime());
		}
		else{
			result = newDateAndTime.format(storeCal.getTime());
		}

		StringBuilder stringBuilder = new StringBuilder(result);
		//stringBuilder.append("at ").append(time.format(storeCal.getTime()));
		return stringBuilder.toString();
	}

	public static String getDayNumberSuffix(int day) {
		if (day >= 11 && day <= 13) {
			return "th";
		}
		switch (day % 10) {
			case 1:
				return "st";
			case 2:
				return "nd";
			case 3:
				return "rd";
			default:
				return "th";
		}
	}

	public static Date convert(final String format, final Date date)
	{
		try
		{
			final SimpleDateFormat sdf = new SimpleDateFormat(format);
			return sdf.parse(sdf.format(date));
		}
		catch (final ParseException e)
		{
			LOG.error("Error occured during date conversion. Returning null ", e);
			return null;
		}
	}

	public static XMLGregorianCalendar getGregorianCalendar(final Date date)
	{
		final GregorianCalendar c = new GregorianCalendar();
		c.setTime(date);
		return gregorianCalendar(c);
	}

	public static String getSiteFormattedDateFromSapDate(final String dateString) throws ParseException
	{

		if (StringUtils.isNotBlank(dateString))
		{
			final Date date = getDate(dateString, SabmCoreConstants.DELIVERY_DATE_PATTERN);

			final SimpleDateFormat sdf = new SimpleDateFormat(Config.getString("sabm.site.date.pattern", "dd/MM/yyyy"));
			return sdf.format(date);
		}

		return "";
	}

	public static XMLGregorianCalendar getGregorianCalendar(final String format, final Date date)
	{
		if (StringUtils.isNotBlank(format) && date != null)
		{
			final GregorianCalendar c = new GregorianCalendar();
			c.setTime(convert(format, date));
			return gregorianCalendar(c);
		}
		return null;
	}

	private static XMLGregorianCalendar gregorianCalendar(final GregorianCalendar c)
	{
		c.set(Calendar.HOUR_OF_DAY, 0);
		c.set(Calendar.MINUTE, 0);
		c.set(Calendar.SECOND, 0);
		c.set(Calendar.MILLISECOND, 0);
		try
		{
			final XMLGregorianCalendar xmlGregorianCalendar = DatatypeFactory.newInstance().newXMLGregorianCalendar(c);
			xmlGregorianCalendar.setTime(0, 0, 0);
			xmlGregorianCalendar.setTimezone(DatatypeConstants.FIELD_UNDEFINED);
			return xmlGregorianCalendar;
		}
		catch (final DatatypeConfigurationException e)
		{
			LOG.error("Error occured during date conversion. Returning null ", e);
			return null;
		}
	}

	public static String convertSimpleDateFormat(final String simpleDateFormatToBe, final String simpleDateFormat, final String date)
			throws ParseException {

		final SimpleDateFormat sdf = new SimpleDateFormat(simpleDateFormat);

		Date currentDate = sdf.parse(date);


		final SimpleDateFormat sdfTobbe = new SimpleDateFormat(simpleDateFormatToBe);


		return sdfTobbe.format(currentDate);

	}

	public static Date convert(final String originalText, final String[] regexes, final String[] datePatterns)
	{
		for (final String eachRegex : regexes)
		{
			final Pattern pattern = Pattern.compile(eachRegex);
			final Matcher matcher = pattern.matcher(originalText);
			while (matcher.find())
			{
				final String dateStr = matcher.group();

				for (final String datePattern : datePatterns)
				{
					if (dateStr.length() == datePattern.length())
					{
						try
						{
							final Date date = getDate(dateStr, datePattern);
							return date;
						}
						catch (final ParseException e)
						{
							LOG.error("Intentionally swallowing exception!");
						}
					}
				}
			}
		}
		return null;
	}

	public static Date toDate(final XMLGregorianCalendar calendar)
	{
		if (calendar == null)
		{
			return null;
		}
		calendar.setTime(0, 0, 0);
		calendar.setTimezone(DatatypeConstants.FIELD_UNDEFINED);
		return calendar.toGregorianCalendar().getTime();
	}

	public static String toString(final XMLGregorianCalendar calendar, final String pattern)
	{
		return toString(toDate(calendar), pattern);
	}

	public static String toString(final Date d, final String pattern)
	{
		return d != null ? new SimpleDateFormat(pattern).format(d) : null;
	}

	public static String toFormattedString(final XMLGregorianCalendar calendar)
	{
		final Date d = toDate(calendar);
		return d != null ? new SimpleDateFormat("dd/MM/yyyy").format(d) : null;
	}

	public static String toFormattedString(final Date d)
	{
		return d != null ? new SimpleDateFormat("dd/MM/yyyy").format(d) : null;
	}


	public static Date plusOneDay(final Date d)
	{
		return new DateTime(d).plusDays(1).toDate();
	}

	public static Date minusDays(final Date d,final int days)
	{
		return new DateTime(d).minusDays(days).toDate();
	}

	public static Date minusOneDay(final Date d)
	{
		return new DateTime(d).minusDays(1).toDate();
	}

	public static Date plusMinutes(final Date d, final int minutes){
		return new DateTime(d).plusMinutes(minutes).toDate();
	}

	public static Date minusMinutes(final Date d , final int minutes){
		return new DateTime(d).minusMinutes(minutes).toDate();
	}

	public static boolean afterOrEqual(final Date d1, final Date d2)
	{
		return d1.compareTo(d2) >= 0;
	}

	public static boolean beforeOrEqual(final Date d1, final Date d2)
	{
		return d1.compareTo(d2) <= 0;
	}

	public static boolean sameDay(final Date d1, final Date d2)
	{
		if (d1 != null && d2 != null)
		{
			return DateTimeComparator.getDateOnlyInstance().compare(d1, d2) == 0;
		}
		return false;
	}


	public static Date getOnlyDate(final Date date)
	{

		final Calendar cal = Calendar.getInstance();
		cal.setTime(date);
		cal.set(Calendar.HOUR_OF_DAY, 0);
		cal.set(Calendar.MINUTE, 0);
		cal.set(Calendar.SECOND, 0);
		cal.set(Calendar.MILLISECOND, 0);
		return cal.getTime();
	}

	public static Date getOnlyDate(final String date) throws ParseException
	{
		return DateUtils.truncate(getDate(date, DATE_PATTERN), Calendar.DATE);
	}

	public static String getFormattedDate(final Date date)
	{
		final Calendar cal = Calendar.getInstance();
		cal.setTime(date);
		final int day = cal.get(Calendar.DATE);

		switch (day)
		{
			case 1:
			case 21:
			case 31:
				return new SimpleDateFormat("d'st of' MMMM yyyy").format(date);

			case 2:
			case 22:
				return new SimpleDateFormat("d'nd of' MMMM yyyy").format(date);

			case 3:
			case 23:
				return new SimpleDateFormat("d'rd of' MMMM yyyy").format(date);

			default:
				return new SimpleDateFormat("d'th of' MMMM yyyy").format(date);
		}
	}

	/**
	 * <p>Checks if the first date is after the second date ignoring time.</p>
	 * @param date1 the first date, not altered, not null
	 * @param date2 the second date, not altered, not null
	 * @return true if the first date day is after the second date day.
	 * @throws IllegalArgumentException if the date is <code>null</code>
	 */
	public static boolean isAfterDay(Date date1, Date date2) {
		if (date1 == null || date2 == null) {
			throw new IllegalArgumentException("The dates must not be null");
		}
		Calendar cal1 = Calendar.getInstance();
		cal1.setTime(date1);
		Calendar cal2 = Calendar.getInstance();
		cal2.setTime(date2);
		return isAfterDay(cal1, cal2);
	}
	/**
	 * <p>Checks if the first calendar date is after the second calendar date ignoring time.</p>
	 * @param cal1 the first calendar, not altered, not null.
	 * @param cal2 the second calendar, not altered, not null.
	 * @return true if cal1 date is after cal2 date ignoring time.
	 * @throws IllegalArgumentException if either of the calendars are <code>null</code>
	 */
	public static boolean isAfterDay(Calendar cal1, Calendar cal2) {
		if (cal1 == null || cal2 == null) {
			throw new IllegalArgumentException("The dates must not be null");
		}
		if (cal1.get(Calendar.ERA) < cal2.get(Calendar.ERA)) return false;
		if (cal1.get(Calendar.ERA) > cal2.get(Calendar.ERA)) return true;
		if (cal1.get(Calendar.YEAR) < cal2.get(Calendar.YEAR)) return false;
		if (cal1.get(Calendar.YEAR) > cal2.get(Calendar.YEAR)) return true;
		return cal1.get(Calendar.DAY_OF_YEAR) > cal2.get(Calendar.DAY_OF_YEAR);
	}

	/**
	 * <p>Checks if the first date is before the second date ignoring time.</p>
	 * @param date1 the first date, not altered, not null
	 * @param date2 the second date, not altered, not null
	 * @return true if the first date day is before the second date day.
	 * @throws IllegalArgumentException if the date is <code>null</code>
	 */
	public static boolean isBeforeDay(Date date1, Date date2) {
		if (date1 == null || date2 == null) {
			throw new IllegalArgumentException("The dates must not be null");
		}
		Calendar cal1 = Calendar.getInstance();
		cal1.setTime(date1);
		Calendar cal2 = Calendar.getInstance();
		cal2.setTime(date2);
		return isBeforeDay(cal1, cal2);
	}

	/**
	 * <p>Checks if the first calendar date is before the second calendar date ignoring time.</p>
	 * @param cal1 the first calendar, not altered, not null.
	 * @param cal2 the second calendar, not altered, not null.
	 * @return true if cal1 date is before cal2 date ignoring time.
	 * @throws IllegalArgumentException if either of the calendars are <code>null</code>
	 */
	public static boolean isBeforeDay(Calendar cal1, Calendar cal2) {
		if (cal1 == null || cal2 == null) {
			throw new IllegalArgumentException("The dates must not be null");
		}
		if (cal1.get(Calendar.ERA) < cal2.get(Calendar.ERA)) return true;
		if (cal1.get(Calendar.ERA) > cal2.get(Calendar.ERA)) return false;
		if (cal1.get(Calendar.YEAR) < cal2.get(Calendar.YEAR)) return true;
		if (cal1.get(Calendar.YEAR) > cal2.get(Calendar.YEAR)) return false;
		return cal1.get(Calendar.DAY_OF_YEAR) < cal2.get(Calendar.DAY_OF_YEAR);
	}

    public static List<Long> getLongDates(final Set<Date> dates) {
        final List<Long> timestamps = new ArrayList<>();
        if (CollectionUtils.isNotEmpty(dates)) {
            for (final Date date : dates) {
                timestamps.add(date.getTime());
            }
        }
        return timestamps;
    }
	/*
	  method to round time to next 15 minutes increment
	  e.g.
	  53 to 07	--> 00
      08 to 22	--> 15
      23 to 37	--> 30
      38 to 52	--> 45
	 */

	public static Date roundDateToNearestQuarterHour(Date date){

		Calendar calendar = Calendar.getInstance();
		calendar.setTime(date);

		int unroundedMinutes = calendar.get(Calendar.MINUTE);
		int mod = unroundedMinutes % 15;
		calendar.add(Calendar.MINUTE, mod < 8 ? -mod : (15-mod));

		return calendar.getTime();
	}

	/** Transform ISO 8601 string to Calendar. */
	public static Calendar toCalendar(final String iso8601string)
			throws ParseException {
		Calendar calendar = GregorianCalendar.getInstance();
		String s = iso8601string.replace("Z", "+00:00");
		try {
			s = s.substring(0, 22) + s.substring(23);  // to get rid of the ":"
		} catch (IndexOutOfBoundsException e) {
			throw new ParseException("Invalid length", 0);
		}
		Date date = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss").parse(s);
		calendar.setTime(date);
		return calendar;
	}


}
