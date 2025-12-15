/**
 *
 */
package com.sabmiller.commons.translators;

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

import com.sabmiller.core.model.AsahiB2BUnitModel;


/**
 * @author bmuchu
 *
 */
public class ALBDisabledB2Bunits extends SABMAbstracB2BCustomerTranslator
{
	private static final Logger LOG = LoggerFactory.getLogger(ALBDisabledB2Bunits.class);

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
				if (group.getClass().equals(AsahiB2BUnitModel.class))
				{
					final AsahiB2BUnitModel businessUnit = (AsahiB2BUnitModel) group;
					final Collection<String> disabledUsers = businessUnit.getCubDisabledUsers();

					for (final String disabledUser : disabledUsers)
					{
						if (disabledUser.equalsIgnoreCase(getCustomer().getUid()))
						{
							userDisabled = true;
							break;
						}
					}

					if (userDisabled && StringUtils.isNotEmpty(businessUnit.getCompanyCode())
							&& businessUnit.getCompanyCode().equalsIgnoreCase("sga"))
					{
						b2bUnitsList.add(group.getUid());
					}
				}
			}
			result = "=\"" + StringUtils.join(b2bUnitsList, ",") + "\"";
		}

		LOG.debug("Result of ALB Disabled B2Bunits translation for item [{}] is [{}]", item, result);

		return result;
	}

}
