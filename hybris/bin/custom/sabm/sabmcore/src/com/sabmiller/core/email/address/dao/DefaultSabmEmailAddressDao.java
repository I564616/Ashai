/**
 *
 */
package com.sabmiller.core.email.address.dao;

import de.hybris.platform.acceleratorservices.email.dao.impl.DefaultEmailAddressDao;
import de.hybris.platform.acceleratorservices.model.email.EmailAddressModel;
import de.hybris.platform.core.model.ItemModel;
import de.hybris.platform.servicelayer.search.SearchResult;
import de.hybris.platform.servicelayer.util.ServicesUtil;

import java.util.HashMap;
import java.util.Map;

import org.apache.commons.collections4.CollectionUtils;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import jakarta.annotation.Resource;
import com.apb.core.util.AsahiSiteUtil;


/**
 *
 */
public class DefaultSabmEmailAddressDao extends DefaultEmailAddressDao
{
	private static final Logger LOG = LoggerFactory.getLogger(DefaultSabmEmailAddressDao.class);
	
	@Resource
	private AsahiSiteUtil asahiSiteUtil;

	@Override
	public EmailAddressModel findEmailAddressByEmailAndDisplayName(final String emailAddress, final String displayName)
	{
		
		if(asahiSiteUtil.isCub())
		{
   		ServicesUtil.validateParameterNotNull(emailAddress, "emailAddress must not be null");
   
   		final Map<String, Object> params = new HashMap<String, Object>();
   
   		params.put(EmailAddressModel.EMAILADDRESS, emailAddress);
   		if (StringUtils.isNotEmpty(displayName))
   		{
   			params.put(EmailAddressModel.DISPLAYNAME, displayName);
   		}
   
   		LOG.debug("Searching for emailAddress [{}] displayName [{}]", emailAddress, displayName);
   
   		final StringBuffer query = new StringBuffer("SELECT {" + ItemModel.PK + "} FROM  {" + EmailAddressModel._TYPECODE
   				+ "} WHERE {" + EmailAddressModel.EMAILADDRESS + "} = ?" + EmailAddressModel.EMAILADDRESS);
   		if (StringUtils.isNotEmpty(displayName))
   		{
   			query.append(" AND {" + EmailAddressModel.DISPLAYNAME + "} = ?" + EmailAddressModel.DISPLAYNAME);
   		}
   
   
   		final SearchResult<EmailAddressModel> results = getFlexibleSearchService().<EmailAddressModel> search(query.toString(),
   				params);
   
   		if (LOG.isDebugEnabled())
   		{
   			LOG.debug("Results: {}", (results == null ? "null" : String.valueOf(results.getCount())));
   		}
   
   		return CollectionUtils.isEmpty(results.getResult()) ? null : results.getResult().iterator().next();
		}
		else
		{
			return super.findEmailAddressByEmailAndDisplayName(emailAddress,displayName);
		}
	}
}
