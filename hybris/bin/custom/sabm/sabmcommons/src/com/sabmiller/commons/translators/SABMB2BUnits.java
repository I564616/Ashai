/**
 *
 */
package com.sabmiller.commons.translators;

import de.hybris.platform.b2b.model.B2BUnitModel;
/**
 * @author s.reddi.sekhar.reddy
 *
 */
import de.hybris.platform.core.model.security.PrincipalGroupModel;
import de.hybris.platform.impex.jalo.ImpExException;
import de.hybris.platform.jalo.Item;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

import org.apache.commons.collections4.CollectionUtils;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.sabmiller.commons.constants.SabmcommonsConstants;


/**
 * Translator of list of user groups for B2BCustomer export.
 */
public class SABMB2BUnits extends SABMAbstracB2BCustomerTranslator
{
	/** The Constant LOG. */
	private static final Logger LOG = LoggerFactory.getLogger(SABMB2BCustomerGroups.class);

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
		if (isValidAndSetup(item) && CollectionUtils.isNotEmpty(getCustomer().getGroups()))
		{
			final List<String> b2bUnitsList = new ArrayList<>();

			for (final PrincipalGroupModel group : getCustomer().getGroups())
			{
				boolean userDisabled = false;
				//Filtering only the real B2BUnits (removing the UserGroup)
				if (group.getClass().equals(B2BUnitModel.class))
				{
					final B2BUnitModel businessUnit = (B2BUnitModel) group;
					final Collection<String> disabledUsers = businessUnit.getCubDisabledUsers();
							for(final String disabledUser : disabledUsers){
								if (disabledUser.equalsIgnoreCase(getCustomer().getUid())){
										userDisabled = true;
										break;
								}
							}
					//Filtering only ZALB B2BUnits (removing the ZADP)
					if (SabmcommonsConstants.ZALB.equals(businessUnit.getAccountGroup()) && !userDisabled)
					{
						b2bUnitsList.add(group.getUid());
					}
				}
			}
			result = "=\"" + StringUtils.join(b2bUnitsList, ",") + "\"";
		}

		LOG.debug("Result translation for item [{}] is [{}]", item, result);

		return result;
	}
}

