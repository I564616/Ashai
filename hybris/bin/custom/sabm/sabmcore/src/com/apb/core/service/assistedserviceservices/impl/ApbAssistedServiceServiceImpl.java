package com.apb.core.service.assistedserviceservices.impl;

import de.hybris.platform.assistedserviceservices.impl.DefaultAssistedServiceService;
import de.hybris.platform.b2b.model.B2BCustomerModel;
import de.hybris.platform.servicelayer.user.UserConstants;
import de.hybris.platform.store.services.BaseStoreService;

import jakarta.annotation.Resource;

import org.apache.commons.lang3.StringUtils;

import com.apb.core.util.AsahiSiteUtil;
import com.sabmiller.core.constants.SabmCoreConstants;


/**
 * ApbAssistedServiceServiceImpl implementation of {@link ApbAssistedServiceServiceImpl}
 */
public class ApbAssistedServiceServiceImpl extends DefaultAssistedServiceService
{
	
	@Resource(name = "baseStoreService")
	protected BaseStoreService baseStoreService;
	
	@Resource
	private AsahiSiteUtil asahiSiteUtil;
	
	/**
	 * Override this method for enabled customer return
	 */
	@Override
	protected StringBuilder getCustomerSearchQuery(final String searchCriteria)
	{
		if(!asahiSiteUtil.isCub())
		{
   		final StringBuilder builder = new StringBuilder();
   		builder.append("SELECT ");
   		builder.append("{p:" + B2BCustomerModel.PK + "} ");
   		builder.append("FROM {" + B2BCustomerModel._TYPECODE + " AS p} ");
   		builder.append("WHERE NOT {" + B2BCustomerModel.UID + "}='" + UserConstants.ANONYMOUS_CUSTOMER_UID + "' ");
   		builder.append("AND {p:" + B2BCustomerModel.ACTIVE + "}=" + 1 + " ");
   		if (!StringUtils.isBlank(searchCriteria))
   		{
   			builder.append("AND (LOWER({p:" + B2BCustomerModel.UID + "}) LIKE CONCAT(?username, '%') ");
   			builder.append("OR LOWER({p:name}) LIKE CONCAT('%', CONCAT(?username, '%'))) ");
   		}
   		return builder;
		}
		else
		{
			return super.getCustomerSearchQuery(searchCriteria);
		}
	}

}
