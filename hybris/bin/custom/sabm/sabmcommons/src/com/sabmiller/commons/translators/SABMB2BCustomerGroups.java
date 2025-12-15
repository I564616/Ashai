package com.sabmiller.commons.translators;

import de.hybris.platform.core.model.security.PrincipalGroupModel;
import de.hybris.platform.core.model.user.UserGroupModel;
import de.hybris.platform.impex.jalo.ImpExException;
import de.hybris.platform.jalo.Item;

import java.util.ArrayList;
import java.util.List;

import org.apache.commons.collections4.CollectionUtils;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;


/**
 * Translator of list of user groups for B2BCustomer export.
 */
public class SABMB2BCustomerGroups extends SABMAbstracB2BCustomerTranslator
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
			final List<String> userGroupList = new ArrayList<>();

			for (final PrincipalGroupModel group : getCustomer().getGroups())
			{
				//Filtering only the real UserGroup (removing the B2BUnits)
				if (group.getClass().equals(UserGroupModel.class))
				{
					userGroupList.add(group.getUid());
				}
			}

			result = StringUtils.join(userGroupList, ",");
		}

		LOG.debug("Result translation for item [{}] is [{}]", item, result);

		return result;
	}
}
