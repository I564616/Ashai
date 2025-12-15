/**
 *
 */
package com.sabmiller.core.util;

import de.hybris.platform.servicelayer.config.ConfigurationService;

import java.math.BigDecimal;
import java.text.DecimalFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;

import org.apache.commons.lang3.StringUtils;
import org.apache.commons.lang3.math.NumberUtils;
import org.apache.commons.text.WordUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;


/**
 * The Class SABMFormatterUtils.
 */
public class SABMFormatterUtils
{

	private ConfigurationService configurationService;

	/** The Constant LOG. */
	private static final Logger LOG = LoggerFactory.getLogger(SABMFormatterUtils.class);

	/** The Constant SYMBOL_COMMA. */
	public static final String SYMBOL_COMMA = ",";

	/** The Constant SYMBOL_DOT. */
	public static final String SYMBOL_DOT = ".";

	/** The Constant SYMBOL_MINUS. */
	public static final String SYMBOL_MINUS = "-";




	/**
	 * update the value with add a comma as a separator for dimensions over 1000 e.g. convert 1000.00 to 1,000 Related
	 * Attribute as following: -Weight -Length -Height -Width
	 *
	 * @param source
	 *           the source
	 * @return String
	 */
	public String formatDimension(final String source)
	{

		String formattedValue = StringUtils.trim(source);

		if (StringUtils.isNotBlank(formattedValue))
		{
			formattedValue = getCommaNumberFormat(formattedValue);
			formattedValue = removeTrailingZeros(formattedValue);

		}
		return formattedValue;
	}


	/*
	 * checks if the the input is number and covert to comma formatted number(#,###.00) Eg: 2222.000 to 2,222.00
	 */

	/**
	 * Gets the comma number format.
	 *
	 * @param number
	 *           the number
	 * @return the comma number format
	 */
	public String getCommaNumberFormat(final String number)
	{

		if (StringUtils.isNotBlank(number))
		{

			final DecimalFormat decimalFormatter = new DecimalFormat(getRegex("comma.number.format"));
			try
			{
				return decimalFormatter.format(Double.parseDouble(number));
			}
			catch (final NumberFormatException ex)
			{
				LOG.warn("number format exception while converting to comma seperated string", ex);
			}
		}
		return number;


	}

	/**
	 * update the value formatter to title case letters. e.g. VICTORIA BITTER->Victoria Bitter Related attribute as
	 * following: -Category -Sub Category - Brand - Package Type - size - Unit of Measurement - Unit
	 *
	 * @param values
	 *           the values
	 * @return String
	 */
	public String toTitleCase(final String values)
	{

		return WordUtils.capitalizeFully(StringUtils.trim(values));
	}




	/**
	 * if the "," exist in the values, replace it with "." e.g. 4,60% -> 4.60% Related attribute as following: -ABV
	 *
	 * @param values
	 *           the values
	 * @return String
	 */
	public String formatABV(final String values)
	{
		if (!StringUtils.isBlank(values))
		{
			//invoke replace method replace the "," to "."
			return StringUtils.replace(values, SYMBOL_COMMA, SYMBOL_DOT);
		}
		else
		{
			return values;
		}

	}


	/**
	 * Any leading "0" as the first character will be removed e.g. 01X02 -> 1x2 Related attribute as following:
	 * -PackageConfiguration
	 *
	 * @param values
	 *           the values
	 * @return String
	 */
	public String formatPackageConfiguration(final String values)
	{
		if (StringUtils.isNotBlank(values))
		{

			return removeLeadingZeros(values);
		}
		return values;
	}


	/**
	 * Removes the leading zeros.
	 *
	 * @param source
	 *           the source
	 * @return the string
	 */
	public String removeLeadingZeros(final String source)
	{

		if (!StringUtils.isBlank(source))
		{
			final String reg = getRegex("leadingzeros.format.regx");
			return source.replaceAll(reg, "$1");
		}
		return source;
	}


	public String formatSKU(final String source)
	{

		if (!StringUtils.isBlank(source))
		{
			final String reg = getRegex("sku.zeros.format.regx");
			return source.replaceAll(reg, "");
		}
		return source;
	}

	/**
	 * Removes the trailing zeros.
	 *
	 * @param source
	 *           the source
	 * @return the string
	 */
	public String removeTrailingZeros(final String source)
	{

		if (!StringUtils.isBlank(source))
		{
			final String reg = getRegex("trailingzeros.format.regx");
			return source.replaceAll(reg, "$1");
		}
		return source;
	}

	/**
	 * Parses the date.
	 *
	 * @param dateToParse
	 *           the date to parse
	 * @param pattern
	 *           the pattern
	 * @return the date
	 */
	public Date parseDate(final String dateToParse, final String pattern)
	{
		if (StringUtils.isNotEmpty(dateToParse))
		{
			try
			{
				final SimpleDateFormat dateFormat = new SimpleDateFormat(pattern);
				return dateFormat.parse(dateToParse);
			}
			catch (final ParseException e)
			{
				LOG.error("Unable to parse date: " + dateToParse, e);
			}
		}

		return null;
	}

	/**
	 * Parses the sap number checking if is negative with the "-" in the end.
	 *
	 * @param number
	 *           the number
	 * @return the big decimal representation of the string or null if conversion failed.
	 */
	public BigDecimal parseSAPNumber(final String number)
	{
		BigDecimal parsedNumber = null;

		if (StringUtils.isNotEmpty(number))
		{
			String value = number.trim();
			if (StringUtils.endsWith(value, SYMBOL_MINUS))
			{
				value = StringUtils.removeEnd(value, SYMBOL_MINUS);
				value = SYMBOL_MINUS + value;
			}

			if (NumberUtils.isNumber(value))
			{
				try
				{
					parsedNumber = new BigDecimal(value);
				}
				catch (final NumberFormatException e)
				{
					LOG.warn("Unable to parse number: " + number, e);
				}
			}
		}

		return parsedNumber;
	}



	/**
	 * get regex value from project.properties
	 *
	 * @param regex
	 *           the regex
	 * @return String
	 */
	public String getRegex(final String regex)
	{
		final String value = configurationService.getConfiguration().getString(regex, "");
		return StringUtils.trimToEmpty(value);
	}


	/**
	 * @return the configurationService
	 */
	public ConfigurationService getConfigurationService()
	{
		return configurationService;
	}


	/**
	 * @param configurationService
	 *           the configurationService to set
	 */
	public void setConfigurationService(final ConfigurationService configurationService)
	{
		this.configurationService = configurationService;
	}
}
