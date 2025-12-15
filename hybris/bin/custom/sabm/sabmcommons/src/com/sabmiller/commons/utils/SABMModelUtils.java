/**
 * 
 */
package com.sabmiller.commons.utils;

import de.hybris.platform.b2b.model.B2BCustomerModel;
import de.hybris.platform.b2b.model.B2BUnitModel;
import de.hybris.platform.core.model.security.PrincipalGroupModel;

import java.util.Collection;

import org.apache.commons.collections4.CollectionUtils;
import org.apache.commons.lang3.BooleanUtils;

/**
 * @author GQ485VQ
 *
 */
public class SABMModelUtils
{
	private static final String CUB_STORE = "sabmStore";
	private static final String DELETEDCUSTOMERGROUP = "deletedcustomergroup";
	
	/**
	 * @return true / false based on customer present in disabledUsers list
	 */
	public static boolean isCustomerActiveForCUB(B2BCustomerModel customer)
	{
		boolean isActive = true;
		if(BooleanUtils.isTrue(customer.getActive()))
		{
		for(PrincipalGroupModel unit:customer.getGroups())
		{
			if(unit instanceof B2BUnitModel && ((B2BUnitModel) unit).getCompanyUid() != null 
					&& ((B2BUnitModel) unit).getCompanyUid().equals(CUB_STORE))
			{
				if(DELETEDCUSTOMERGROUP.equalsIgnoreCase(((B2BUnitModel) unit).getUid()))
				{
					return false;
				}
				if(((B2BUnitModel) unit).getCubDisabledUsers() != null)
				{
					Collection<String> disabledUsers= ((B2BUnitModel) unit).getCubDisabledUsers();
					if(CollectionUtils.isNotEmpty(disabledUsers) && disabledUsers.contains(customer.getUid()))
					{
						isActive = false;
					}
					else
					{
						return true;
					}
				}
			}
		}
		}
		else {
			isActive = false;
		}
		return isActive;
	}
}
