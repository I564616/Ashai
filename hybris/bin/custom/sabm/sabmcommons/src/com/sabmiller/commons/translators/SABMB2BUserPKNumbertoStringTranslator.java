/**
 *
 */
package com.sabmiller.commons.translators;

import de.hybris.platform.impex.jalo.ImpExException;
import de.hybris.platform.jalo.Item;

import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;


/**
 * @author ross.hengjun.zhu
 *
 */
public class SABMB2BUserPKNumbertoStringTranslator extends SABMAbstracB2BCustomerTranslator
{
	/** The Constant LOG. */
	private static final Logger LOG = LoggerFactory.getLogger(SABMB2BUserPKNumbertoStringTranslator.class);

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
		String result = StringUtils.EMPTY;

		//Checking if everything is ok to perform the attribute translation
		if (isValidAndSetup(item) && getCustomer().getDefaultB2BUnit() != null && getCustomer().getPk() != null)
		{

			result = "=\"" + getCustomer().getPk() + "\"";
		}

		LOG.debug("Result translation for item [{}] is [{}]", item, result);

		return result;
	}
}
