package com.apb.integration.util;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import org.joda.time.format.DateTimeFormat;
import org.joda.time.format.DateTimeFormatter;
import java.util.Calendar;
import java.util.Date;

import org.apache.commons.lang3.StringUtils;
import org.joda.time.DateTime;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class ApbAddressTimeUtil 
{
	private static final Logger LOG = LoggerFactory.getLogger(ApbAddressTimeUtil.class);
	
	private final static String TODAYS_DATE_INPUT_FORMAT = "yyyy-MMM-dd";
	private final static String CUTTOFF_ONLY_TIME_FORMAT = "hh:mm a ZZZ";  //MJ 2702
	private final static String CUTTOFF_TIME_FORMAT = TODAYS_DATE_INPUT_FORMAT + " " + CUTTOFF_ONLY_TIME_FORMAT; //MJ 2702

	/**
	 * This method is used to get delivery date object.
	 * 
	 * @param str
	 * @return Date
	 */
	public static Date getDeliveryTimeDateObject(String str){
		Date date = null; 
		SimpleDateFormat sdf = new SimpleDateFormat("HH:mm:ss");
		try {
			date = sdf.parse(str + "UTC+0");
		} catch (ParseException e) {
			LOG.error("Could not parse date", e);
		}
		return date;
	}
	
	/**
	 * This method is used to get Delivery time as string in format: HH:mm:ss
	 * 
	 * @param date
	 * @return String
	 */
	public static String getDeliveryTimeStringFull(Date date)
	{
		if(null != date)
		{
			String timeStr = null;
			SimpleDateFormat sdf2 = new SimpleDateFormat("HH:mm:ss");
			timeStr = sdf2.format(date);
			return timeStr;
		}
		return StringUtils.EMPTY;
	}

	/**
	 * This method is used to get Delivery time as string in format: HH:mm
	 * 
	 * @param date
	 * @return String
	 */
	public static String getDeliveryTimeHrsMinute(Date date)
	{
		if(null != date)
		{
			String timeStr = null;
			SimpleDateFormat sdf2 = new SimpleDateFormat("HH:mm");
			timeStr = sdf2.format(date);
			return timeStr;
		}
		return StringUtils.EMPTY;
	}
	
	/**
	 * This method is used to get cut off date in date object from cut off date set at warehouse level.
	 * 
	 * @param cutOfftimeStr
	 * @return DateTime
	 * @throws ParseException 
	 */
	public static DateTime getCutOffDate(String cutOfftimeStr, String timeFormat) throws ParseException
	{
		DateTime date = null;
		try
		{
			String todaysDate = getTodaysDate();
			String todaysCutOfftime = todaysDate + " " + cutOfftimeStr;			
			
			DateTimeFormatter sdf = null;
			if (timeFormat != null){
				sdf = DateTimeFormat.forPattern(TODAYS_DATE_INPUT_FORMAT + " " + timeFormat);
			} else {
				sdf = DateTimeFormat.forPattern(CUTTOFF_TIME_FORMAT);
			}
			date = sdf.parseDateTime(todaysCutOfftime);
		}
		catch(Exception e)
		{
			LOG.error("Unable to parse cut off time to date. Using Default 12:30 PM MEL time. To Fix check configurations. Exception = " 
							+ e + "; Configured Time Format (Optional) = " + timeFormat 
							+ "; Default Format = " + CUTTOFF_TIME_FORMAT 
							+ "; Cut Off time = " + cutOfftimeStr);
			DateTimeFormatter sdf = DateTimeFormat.forPattern(CUTTOFF_TIME_FORMAT);
			try {
				date = sdf.parseDateTime(getTodaysDate() + " 12:30 PM Australia/Melbourne" );
			} catch (ParseException exception) {
				LOG.error("Not able to fetch today's date ", exception);
			}		}
		return date;
	}
	
	/**
	 * This method is used to get current date.
	 * 
	 * @return String
	 * @throws ParseException 
	 */
	private static String getTodaysDate() throws ParseException{
		SimpleDateFormat sdf = new SimpleDateFormat(TODAYS_DATE_INPUT_FORMAT);
		return sdf.format(Calendar.getInstance().getTime());
	}
}