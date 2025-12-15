
package com.sabmiller.integration.simulator;

import de.hybris.platform.commercefacades.order.data.CartData;
import de.hybris.platform.core.model.order.CartModel;
import de.hybris.platform.order.CartService;
import de.hybris.platform.servicelayer.dto.converter.Converter;

import jakarta.annotation.Resource;

import org.springframework.http.MediaType;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;

import com.sabmiller.integration.restclient.commons.SABMIntegrationException;
import com.sabmiller.integration.sap.ordercreate.SalesOrderCreateRequestHandler;
import com.sabmiller.integration.sap.ordercreate.request.SalesOrderCreateRequest;
import com.sabmiller.integration.sap.ordercreate.response.SalesOrderCreateResponse;


@Controller
@RequestMapping(value = "/sap")
public class SapSalesOrderCreateInterfaceRestSimulator
{

	public static SalesOrderCreateResponse staticSalesOrderResponse;

	@Resource
	private SalesOrderCreateRequestHandler salesOrderCreateRestHandler;

	@Resource
	private CartService cartService;

	@Resource
	private Converter<CartModel, CartData> cartConverter;

	@Resource
	private Converter<CartData, SalesOrderCreateRequest> salesOrderCreateRequestConverter;




	@GetMapping(value = "/getSalesOrderCreateRequest", produces = MediaType.APPLICATION_ATOM_XML_VALUE)
	@ResponseBody
	public SalesOrderCreateRequest getRequest()
	{

		final CartData cartData = cartConverter.convert(cartService.getSessionCart());
		return salesOrderCreateRequestConverter.convert(cartData);
	}


	@PostMapping(value = "/getSalesOrderCreateResponse", produces = MediaType.APPLICATION_ATOM_XML_VALUE)
	@ResponseBody
	public SalesOrderCreateResponse getResponse() throws SABMIntegrationException
	{
		final CartData cartData = cartConverter.convert(cartService.getSessionCart());
		return salesOrderCreateRestHandler.sendPostRequest(salesOrderCreateRequestConverter.convert(cartData));
	}

	@PostMapping(value = "/updateSalesOrderCreateResponse", produces = MediaType.APPLICATION_ATOM_XML_VALUE)
	@ResponseBody
	public void updateSalesOrderCreateResponse(@RequestBody final SalesOrderCreateResponse res)
	{
		staticSalesOrderResponse = res;
	}

	@PostMapping(value = "/SalesOrderCreate", produces = MediaType.APPLICATION_XML_VALUE)
	@ResponseBody
	public SalesOrderCreateResponse salesOrderSimulateTest(@RequestBody final SalesOrderCreateRequest request,
			@RequestParam(defaultValue = "false", required = false) final boolean useUploadedResponse)
	{

		if (useUploadedResponse)
		{
			return staticSalesOrderResponse;
		}
		final SalesOrderCreateResponse response = new SalesOrderCreateResponse();

		return response;
	}


}
