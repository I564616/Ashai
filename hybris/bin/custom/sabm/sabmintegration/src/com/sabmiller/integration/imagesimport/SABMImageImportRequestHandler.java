/**
 *
 */
package com.sabmiller.integration.imagesimport;

import com.sabmiller.integration.imagesimport.pojo.SkuVantageResponseItem;
import com.sabmiller.integration.restclient.commons.SABMIntegrationException;


/**
 * The Interface SABMImageImportRequestHandler.
 */
public interface SABMImageImportRequestHandler
{

	/**
	 * Send a get request to sku vantage for a single product ean.
	 *
	 * @param product
	 *           the product
	 * @return an array of SkuVantageResponseItem
	 * @throws SABMIntegrationException
	 *            the SABM integration exception
	 */
	SkuVantageResponseItem[] sendGetRequestSingleProduct(String product) throws SABMIntegrationException;
}
