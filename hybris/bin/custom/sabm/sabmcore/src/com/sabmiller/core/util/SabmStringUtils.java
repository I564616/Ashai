/**
 *
 */
package com.sabmiller.core.util;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.apache.commons.lang3.StringUtils;
import org.apache.log4j.Logger;

import com.sabmiller.facades.deal.data.DealJson;


/**
 * CUB String utility
 *
 * @author joshua.a.antony
 */
public class SabmStringUtils {

    public static final String COUNTRY_CODE = "61";

    private SabmStringUtils() {
        //To Prevent instantiation
    }

    /**
     * Assumes that s is not null and is a valid double value
     */
    public static long doubleStringToLongPrimitive(final String s) {
        return (long) Double.parseDouble(s);
    }

    private static final Logger LOG = Logger.getLogger(SabmStringUtils.class);

    /**
     * Assumes that s is not null and is a valid double value
     */
    public static Long doubleStringToLongWrapper(final String s) {
        return Long.valueOf(doubleStringToLongPrimitive(s));
    }

    public static String findByPattern(final String s, final String... regex) {
        for (final String eachPattern : regex) {
            final Pattern pattern = Pattern.compile(eachPattern);
            final Matcher matcher = pattern.matcher(s);
            while (matcher.find()) {
                return matcher.group();
            }
        }
        return s;
    }

    public static Double toNullSafeDouble(final String s) {
        if (StringUtils.isNotBlank(s)) {
            return Double.parseDouble(stripLeadingZeroes(s).replaceAll(",", "."));
        }
        return null;
    }

    public static double toDouble(final String s) {
        return Double.parseDouble(stripLeadingZeroes(s).replaceAll(",", "."));
    }

    public static int toInt(final String s) {
        return (int) toDouble(s);
    }

    public static String stripLeadingZeroes(final String s) {
        return StringUtils.isEmpty(s) ? StringUtils.EMPTY : s.replaceFirst("^0+(?!$)", "");
    }


	/*
     6 digits  863298 -> 0000863298
     7 digits  1415418 -> 0001415418
	 */

    public static String addLeadingZeroes(final String s) {

        if (s.length() == 6) {
            return "0000" + s;
        }

        if (s.length() == 7) {
            return "000" + s;
        }
        return s;
    }

    public static Double sanitizeDoubleStringFromSAP(final String s) {
        Double discountPerItem = null;
        try {
            discountPerItem = Double.valueOf(StringUtils.remove(s, '-'));

        }
        catch (final NumberFormatException e){
            LOG.error(e.getMessage());
        }

        return discountPerItem;
    }


    public static boolean equalsIgnoreNull(final String str1, final String str2) {
        return (StringUtils.isBlank(str1) && StringUtils.isBlank(str2)) ? true : StringUtils.isBlank(str1) ? false : str1.equals(str2);

    }

    public static String trimToEmpty(final String s) {

        if (StringUtils.isBlank(s) || StringUtils.equalsIgnoreCase(StringUtils.trimToEmpty(s), "null")) {
            return "";
        }
        return StringUtils.trimToEmpty(s);
    }

    /**
     * Method used for sending SMS via SFMC, it only accept : coutryCode + 430012616 .e.g 61430012616
     * @param mobileNumber
     * @return
     */
    public static String convertToInternationalMobileNumber(final String mobileNumber) {

        String mobileNumber_internationalFormat = null;
        if(StringUtils.isNotEmpty(mobileNumber) && StringUtils.startsWith(mobileNumber,"0")){
            mobileNumber_internationalFormat = StringUtils.removeStart(mobileNumber,"0");
            mobileNumber_internationalFormat = COUNTRY_CODE + mobileNumber_internationalFormat;
        }

        return mobileNumber_internationalFormat;

    }

    public static List<String> splitStringAndReturnList(final String string , final String regex)
    {
        return new ArrayList<String>(Arrays.asList(string.split(regex)));
    }

	public static void getSortedDealTitles(final List<String> dealTitles)
	{


		Collections.sort(dealTitles, new Comparator<String>()
		{

			public int compare(final String o1, final String o2)
			{
				return extractQty(o1) - extractQty(o2);
			}

			int extractQty(final String s)
			{
				final String titleWithOnlyQty = s.substring(0, s.indexOf("of")).replaceAll("\\D", "");
				return titleWithOnlyQty.isEmpty() ? 0 : Integer.parseInt(titleWithOnlyQty);
			}

		});
	}

	public static void getSortedListDealJson(final List<DealJson> deals)
	{


		Collections.sort(deals, new Comparator<DealJson>()
		{

			public int compare(final DealJson o1, final DealJson o2)
			{
				return extractQty(o1) - extractQty(o2);
			}

			int extractQty(final DealJson s)
			{
				final String titleWithOnlyQty = s.getTitle().substring(0, s.getTitle().indexOf(" of ")).replaceAll("\\D", "");
				return titleWithOnlyQty.isEmpty() ? 0 : Integer.parseInt(titleWithOnlyQty);
			}

		});
	}

}
