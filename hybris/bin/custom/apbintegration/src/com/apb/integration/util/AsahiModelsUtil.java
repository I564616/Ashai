package com.apb.integration.util;

import de.hybris.platform.b2b.model.B2BCustomerModel;
import de.hybris.platform.core.model.security.PrincipalGroupModel;

import java.util.Collection;

import org.apache.commons.collections4.CollectionUtils;
import org.apache.commons.lang3.BooleanUtils;

import com.sabmiller.core.model.AsahiB2BUnitModel;

public class AsahiModelsUtil {
	
public static final String SGA_STORE = "sga";
	
	/**
	 * @return true / false based on customer present in disabledUsers list
	 */
	public static boolean isCustomerActiveForAsahiAccount(B2BCustomerModel customer)
	{
		boolean isActive = true;
		if(BooleanUtils.isTrue(customer.getActive()))
		{
		for(PrincipalGroupModel unit:customer.getGroups())
		{
			if(unit instanceof AsahiB2BUnitModel && ((AsahiB2BUnitModel) unit).getCompanyCode() != null 
					&& ((AsahiB2BUnitModel) unit).getCompanyCode().equals(SGA_STORE))
			{
				if(((AsahiB2BUnitModel) unit).getDisabledUser() != null)
				{
					Collection<String> disabledUsers= ((AsahiB2BUnitModel) unit).getDisabledUser();
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
		else
		{
			isActive = false;
		}
		return isActive;
	}

}
