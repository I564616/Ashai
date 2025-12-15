package com.apb.core.email.impl;

import de.hybris.platform.acceleratorservices.urlresolver.impl.DefaultSiteBaseUrlResolutionService;
import de.hybris.platform.basecommerce.model.site.BaseSiteModel;
import de.hybris.platform.store.services.BaseStoreService;
import jakarta.annotation.Resource;

import com.apb.core.util.AsahiSiteUtil;
import com.sabmiller.core.constants.SabmCoreConstants;


/**
 * @author C5252631
 *
 *         ApbSiteBaseUrlResolutionService implementation of ({@link DefaultSiteBaseUrlResolutionService}
 *
 *         Override method for removing local host and set current site url
 *
 */
public class ApbSiteBaseUrlResolutionService extends DefaultSiteBaseUrlResolutionService
{
	@Resource(name = "baseStoreService")
	protected BaseStoreService baseStoreService;
	
	@Resource
	private AsahiSiteUtil asahiSiteUtil;
	
	@Override
	protected String getDefaultMediaUrlForSite(@SuppressWarnings("unused") final BaseSiteModel site, final boolean secure) // NOSONAR
	{
		if(!asahiSiteUtil.isCub())
		{
			if (secure)
   		{
   			return cleanupUrl(lookupConfig("website." + site.getUid() + (".https")));
   		}
   		else
   		{
   			return cleanupUrl(lookupConfig("website." + site.getUid() + (".http")));
   		}
		}
		
   		return super.getDefaultMediaUrlForSite(site, secure);
		
	}
}
