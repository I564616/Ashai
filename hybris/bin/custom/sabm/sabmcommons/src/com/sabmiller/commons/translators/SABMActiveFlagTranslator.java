/**
 *
 */
package com.sabmiller.commons.translators;

import de.hybris.platform.impex.jalo.ImpExException;
import de.hybris.platform.jalo.Item;

import org.apache.commons.lang3.BooleanUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.sabmiller.commons.utils.SABMModelUtils;


/**
 * @author GQ485VQ
 *
 */
public class SABMActiveFlagTranslator extends SABMAbstracB2BCustomerTranslator
{
	private static final Logger LOG = LoggerFactory.getLogger(SABMActiveFlagTranslator.class);

	@Override
	public String performExport(final Item item) throws ImpExException
	{
		String result = Boolean.FALSE.toString();

		//Checking if everything is ok to perform the attribute translation
		if (isValidAndSetup(item))
		{
			result = BooleanUtils.toStringTrueFalse(SABMModelUtils.isCustomerActiveForCUB(getCustomer()));
		}

		LOG.debug("Result translation for item [{}] is [{}]", item, result);

		return result;
	}
}
