package com.sabmiller.commons.translators;

import de.hybris.platform.impex.jalo.ImpExException;
import de.hybris.platform.jalo.Item;

import org.apache.commons.lang3.BooleanUtils;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;


/**
 * Translator of password exists for B2BCustomer export.
 */
public class SABMB2BCustomerPasswordIsSet extends SABMAbstracB2BCustomerTranslator
{

	/** The Constant LOG. */
	private static final Logger LOG = LoggerFactory.getLogger(SABMB2BCustomerPasswordIsSet.class);

	/**
	 * Performs translation of password exists attribute for B2BCustomer export.
	 *
	 * @param item
	 *           of B2BCustomer Jalo.
	 * @return boolean to string with password exists value.
	 */
	@Override
	public String performExport(final Item item) throws ImpExException
	{
		String result = Boolean.FALSE.toString();

		//Checking if everything is ok to perform the attribute translation
		if (isValidAndSetup(item))
		{
			result = BooleanUtils.toStringTrueFalse(StringUtils.isNotBlank(getCustomer().getEncodedPassword()));
		}

		LOG.debug("Result translation for item [{}] is [{}]", item, result);

		return result;
	}
}
