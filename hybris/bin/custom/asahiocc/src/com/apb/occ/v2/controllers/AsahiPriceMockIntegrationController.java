/**
 *
 */
package com.apb.occ.v2.controllers;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.annotation.Resource;

import org.apache.commons.collections.CollectionUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.RestController;

import com.apb.core.util.ApbXSSEncoderUtil;
import com.apb.integration.data.ApbPriceData;
import com.apb.integration.price.dto.AsahiAccountPriceRequest;
import com.apb.integration.price.dto.AsahiPriceRequest;
import com.apb.integration.price.dto.AsahiProductPriceRequest;
import com.apb.integration.price.service.ApbProductOfflinePriceService;


/**
 * @author Kuldeep.Singh1
 *
 */
@RestController
@RequestMapping(value = "/**/price")
public class AsahiPriceMockIntegrationController
{
	// Creates logger
	final Logger logger = LoggerFactory.getLogger(AsahiPriceMockIntegrationController.class);

	/** The apb product offline price service. */
	@Resource
	private ApbProductOfflinePriceService apbProductOfflinePriceService;

	/**
	 * Gets the mock price.
	 *
	 * @param priceRequest
	 *           the price request
	 * @return the mock price
	 */
	@ResponseBody
	@RequestMapping(value = "/getPrice", method = RequestMethod.POST, consumes =
	{ MediaType.APPLICATION_JSON_VALUE, MediaType.APPLICATION_XML_VALUE })
	public ApbPriceData getPrice(@RequestBody final AsahiPriceRequest priceRequest)
	{
		logger.debug("Calling Price Mock Service with request---" + ApbXSSEncoderUtil.encodeValue(priceRequest.toString()));
		final List<AsahiAccountPriceRequest> productPriceList = priceRequest.getPriceRequest();
		if (CollectionUtils.isNotEmpty(productPriceList))
		{
			final AsahiAccountPriceRequest productPrice = productPriceList.get(0);
			if (null != productPrice.getAccountNum())
			{
				final Map<String, Map<String, Long>> productMap = new HashMap<>();
				final Map<String, Map<String, Long>> bonusStatusMap = new HashMap<>();
				
				if (null != productPrice.getProducts() && CollectionUtils.isNotEmpty(productPrice.getProducts()))
				{
					final List<AsahiProductPriceRequest> products = productPrice.getProducts();

					for (final AsahiProductPriceRequest productPriceRequest : products)
					{
						if(productPriceRequest.isBonus()) {
							final Map<String,Long> lineAndQty = new HashMap<>();
							lineAndQty.put(productPriceRequest.getLineNbr(),productPriceRequest.getQuantity());
							bonusStatusMap.put(productPriceRequest.getProductId(), lineAndQty);
						} else {
							final Map<String,Long> lineAndQty = new HashMap<>();
							lineAndQty.put(productPriceRequest.getLineNbr(),productPriceRequest.getQuantity());
							productMap.put(productPriceRequest.getProductId(), lineAndQty);
						}
					}
				}
				return this.apbProductOfflinePriceService.getPricesForProducts(productMap, bonusStatusMap, productPrice.getAccountNum());
			}
		}
		return new ApbPriceData();
	}
}
