/**
 *
 */
package com.apb.occ.v2.controllers;

import de.hybris.platform.core.model.product.ProductModel;
import de.hybris.platform.ordersplitting.WarehouseService;
import de.hybris.platform.ordersplitting.model.StockLevelModel;
import de.hybris.platform.product.ProductService;
import de.hybris.platform.stock.StockService;

import java.util.ArrayList;
import java.util.List;

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
import com.apb.core.service.config.AsahiConfigurationService;
import com.apb.integration.stock.dto.AsahiStockOnHandReq;
import com.apb.integration.stock.dto.AsahiStockOnHandRequest;
import com.apb.integration.stock.dto.AsahiStockOnHandRes;
import com.apb.integration.stock.dto.AsahiStockOnHandResponse;
import com.apb.integration.stock.dto.AsahiStockProductResponse;


/**
 * The Class AsahiStockMockIntegrationController.
 *
 * @author Kuldeep.Singh1
 */
@RestController
@RequestMapping(value = "/**/stock")
public class AsahiStockMockIntegrationController
{

	/** The logger. */
	final Logger logger = LoggerFactory.getLogger(AsahiStockMockIntegrationController.class);

	/** The Constant STOCK_MOCK_SERVICE_MOCK_SOH. */
	public static final String STOCK_MOCK_SERVICE_MOCK_SOH = "integration.stock.mock.service.mockSOHValue";

	/** The product service. */
	@Resource(name = "productService")
	private ProductService productService;

	/** The warehouse service. */
	@Resource(name = "warehouseService")
	private WarehouseService warehouseService;

	/** The stock service. */
	@Resource(name = "stockService")
	private StockService stockService;

	/** The asahi configuration service. */
	@Resource(name = "asahiConfigurationService")
	private AsahiConfigurationService asahiConfigurationService;

	/**
	 * Gets the mock price.
	 *
	 * @param stockRequest
	 *           the stock request
	 * @return the mock price
	 */
	@ResponseBody
	@RequestMapping(value = "/getStock", method = RequestMethod.POST, consumes =
	{ MediaType.APPLICATION_JSON_VALUE, MediaType.APPLICATION_XML_VALUE })
	public AsahiStockOnHandRes getStock(@RequestBody final AsahiStockOnHandReq stockRequest)
	{
		logger.debug("Calling Stock Mock Service with request---" + ApbXSSEncoderUtil.encodeValue(stockRequest.toString()));
		final AsahiStockOnHandRes response = new AsahiStockOnHandRes();
		final List<com.apb.integration.stock.dto.AsahiStockOnHandResponse> stockList = new ArrayList<AsahiStockOnHandResponse>();

		if (CollectionUtils.isNotEmpty(stockRequest.getStockOnHandRequest()))
		{
			for (final AsahiStockOnHandRequest stockOnHandRequest : stockRequest.getStockOnHandRequest())
			{
				if (CollectionUtils.isNotEmpty(stockOnHandRequest.getProducts()))
				{
					final com.apb.integration.stock.dto.AsahiStockOnHandResponse stockOnHand = new AsahiStockOnHandResponse();
					stockOnHand.setWarehouse(stockOnHandRequest.getWarehouse());
					final List<AsahiStockProductResponse> productList = new ArrayList<AsahiStockProductResponse>();
					for (final String productId : stockOnHandRequest.getProducts())
					{
						final AsahiStockProductResponse productRes = new AsahiStockProductResponse();

						String stock = "0";
						try
						{
							final ProductModel product = this.productService.getProductForCode(productId);
							final StockLevelModel stockLevel = this.stockService.getStockLevel(product,
									this.warehouseService.getWarehouseForCode(stockOnHandRequest.getWarehouse()));
							stock = String.valueOf(stockLevel.getAvailable());

							productRes.setProductId(productId);
							productRes.setAvailablePhysical(stock);
							productList.add(productRes);

						}
						catch (final Exception ex)
						{
							logger.debug("Error has occured while getting Mock Stock");
							productRes.setProductId(productId);
							productRes.setAvailablePhysical(this.asahiConfigurationService.getString(STOCK_MOCK_SERVICE_MOCK_SOH, "0"));
							productList.add(productRes);
						}
					}
					stockOnHand.setProducts(productList);
					stockList.add(stockOnHand);
				}
			}
		}
		response.setStockOnHandResponse(stockList);
		logger.debug("Calling Stock Mock Service with request---");

		return response;
	}
}
