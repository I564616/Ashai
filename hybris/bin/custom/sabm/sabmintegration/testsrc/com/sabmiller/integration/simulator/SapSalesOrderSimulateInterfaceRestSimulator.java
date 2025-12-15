
package com.sabmiller.integration.simulator;

import de.hybris.platform.commercefacades.order.data.CartData;
import de.hybris.platform.core.model.order.CartModel;
import de.hybris.platform.order.CartService;
import de.hybris.platform.servicelayer.dto.converter.Converter;

import java.util.ArrayList;
import java.util.List;

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
import com.sabmiller.integration.sap.ordersimulate.SalesOrderSimulateRequestHandler;
import com.sabmiller.integration.sap.ordersimulate.request.SalesOrderSimulateRequest;
import com.sabmiller.integration.sap.ordersimulate.response.SalesOrderSimulateResponse;
import com.sabmiller.integration.sap.ordersimulate.response.SalesOrderSimulateResponse.SalesOrderResHeder;
import com.sabmiller.integration.sap.ordersimulate.response.SalesOrderSimulateResponse.SalesOrderResItem;
import com.sabmiller.integration.sap.ordersimulate.response.SalesOrderSimulateResponse.SalesOrderResItem.SalesOrderItemScheduling;


@Controller
@RequestMapping(value = "/sap")
public class SapSalesOrderSimulateInterfaceRestSimulator
{

	public static SalesOrderSimulateResponse staticSalesOrderResponse;

	@Resource
	private SalesOrderSimulateRequestHandler salesOrderSimulateRestHandler;

	@Resource
	private CartService cartService;

	@Resource
	private Converter<CartModel, CartData> cartConverter;

	@Resource
	private Converter<CartData, SalesOrderSimulateRequest> salesOrderSimulateRequestConverter;




	@GetMapping(value = "/getSalesOrderSimulateRequest", produces = MediaType.APPLICATION_ATOM_XML_VALUE)
	@ResponseBody
	public SalesOrderSimulateRequest getRequest()
	{

		final CartData cartData = cartConverter.convert(cartService.getSessionCart());
		return salesOrderSimulateRequestConverter.convert(cartData);
	}


	@PostMapping(value = "/getSalesOrderSimulateResponse", produces = MediaType.APPLICATION_ATOM_XML_VALUE)
	@ResponseBody
	public SalesOrderSimulateResponse getResponse() throws SABMIntegrationException
	{
		final CartData cartData = cartConverter.convert(cartService.getSessionCart());
		return salesOrderSimulateRestHandler.sendPostRequest(salesOrderSimulateRequestConverter.convert(cartData));
	}

	@PostMapping(value = "/updateSalesOrderSimulateResponse", produces = MediaType.APPLICATION_ATOM_XML_VALUE)
	@ResponseBody
	public void updateSalesOrderSimulateResponse(@RequestBody final SalesOrderSimulateResponse res)
	{
		staticSalesOrderResponse = res;
	}

	@PostMapping(value = "/salesOrderSimulate", produces = MediaType.APPLICATION_XML_VALUE)
	@ResponseBody
	public SalesOrderSimulateResponse salesOrderSimulateTest(@RequestBody final SalesOrderSimulateRequest request,
			@RequestParam(required = false, defaultValue = "false") final boolean useUploadedResponse)
	{

		if (useUploadedResponse)
		{
			return staticSalesOrderResponse;
		}
		final SalesOrderSimulateResponse response = new SalesOrderSimulateResponse();


		final SalesOrderResHeder resHeader = new SalesOrderResHeder();
		resHeader.setSoldTo(request.getSalesOrderReqPartner().getSoldTo());
		resHeader.setShipTo(request.getSalesOrderReqPartner().getShipTo());

		resHeader.setRequestedDeliveryDate("06-06-2016");
		resHeader.setCurrency("AUD");


		final SalesOrderResItem item1 = new SalesOrderResItem();
		item1.setLineNumber("001");
		item1.setMaterialEntered("eanVariant04");
		item1.setMaterialNumber("eanVariant04");
		item1.setMaterialQuantity("3");
		item1.setUnitOfMeasure("case");
		final SalesOrderItemScheduling scale1 = new SalesOrderItemScheduling();
		scale1.setConfirmedQty("3");
		scale1.setRequestedQty("10");
		scale1.setItemNumber("001");
		item1.getSalesOrderItemScheduling().add(scale1);
		final SalesOrderResItem item2 = new SalesOrderResItem();


		item2.setFreeGoodsFlag("false");
		item2.setLineNumber("line2");
		item2.setMaterialEntered("eanVariant05");
		item2.setMaterialNumber("eanVariant06");
		item2.setMaterialQuantity("0");
		item2.setMaterialSubstitution("");
		item2.setUnitOfMeasure("case");
		final SalesOrderItemScheduling scale2 = new SalesOrderItemScheduling();
		scale2.setConfirmedQty("0");
		scale2.setRequestedQty("1");
		scale2.setItemNumber("001");
		item2.getSalesOrderItemScheduling().add(scale2);


		final List<SalesOrderSimulateResponse.SalesOrderResItem> salesOrderResItemList = new ArrayList<SalesOrderSimulateResponse.SalesOrderResItem>();
		salesOrderResItemList.add(item1);
		salesOrderResItemList.add(item2);

		response.setSalesOrderResHeder(resHeader);
		response.getSalesOrderResItem().addAll(salesOrderResItemList);

		return response;
	}


}
