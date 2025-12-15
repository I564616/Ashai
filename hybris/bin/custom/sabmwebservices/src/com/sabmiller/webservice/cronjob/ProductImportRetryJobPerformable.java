/**
 *
 */
package com.sabmiller.webservice.cronjob;

import de.hybris.platform.util.Config;


/**
 * @author joshua.a.antony
 *
 */
public class ProductImportRetryJobPerformable extends AbstractImportRetryJobPerformable
{

	/*
	 * (non-Javadoc)
	 * 
	 * @see com.sabmiller.webservice.cronjob.AbstractImportRetryJobPerformable#getUrl()
	 */
	@Override
	public String getUrl()
	{
		return Config.getString("services.import.product.url", null);
	}

}
