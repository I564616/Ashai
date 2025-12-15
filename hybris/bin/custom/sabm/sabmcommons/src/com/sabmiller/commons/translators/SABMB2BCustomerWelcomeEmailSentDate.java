package com.sabmiller.commons.translators;

import de.hybris.platform.impex.jalo.ImpExException;
import de.hybris.platform.jalo.Item;

import java.text.DateFormat;
import java.text.SimpleDateFormat;

import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;


/**
 * Translator of list of user groups for B2BCustomer export.
 */
public class SABMB2BCustomerWelcomeEmailSentDate extends SABMAbstracB2BCustomerTranslator
{

	/** The Constant LOG. */
	private static final Logger LOG = LoggerFactory.getLogger(SABMB2BCustomerWelcomeEmailSentDate.class);

	/**
	 * Performs translation of user groups for customer export.
	 *
	 * @param item
	 *           of B2BCustomer Jalo.
	 * @return translated list of user groups as a String.
	 */
	@Override
	public String performExport(final Item item) throws ImpExException
	{
		String returnDate = StringUtils.EMPTY;
		//Checking if everything is ok to perform the attribute translation
		if (isValidAndSetup(item))
		{
			final DateFormat df = new SimpleDateFormat("dd/MM/yyyy");
			if (null != getCustomer().getWelcomeEmailSentDate())
			{
				returnDate = df.format(getCustomer().getWelcomeEmailSentDate());
			}
		}

		LOG.debug("Result translation for item [{}] is [{}]", item, returnDate);

		return returnDate;
	}
}
