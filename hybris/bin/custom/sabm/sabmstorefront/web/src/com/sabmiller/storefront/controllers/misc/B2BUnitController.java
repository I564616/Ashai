/*
 * [y] hybris Platform
 *
 * Copyright (c) 2000-2015 hybris AG
 * All rights reserved.
 *
 * This software is the confidential and proprietary information of hybris
 * ("Confidential Information"). You shall not disclose such Confidential
 * Information and shall use it only in accordance with the terms of the
 * license agreement you entered into with hybris.
 *
 *
 */
package com.sabmiller.storefront.controllers.misc;

import de.hybris.platform.acceleratorstorefrontcommons.annotations.RequireHardLogIn;
import de.hybris.platform.acceleratorstorefrontcommons.controllers.AbstractController;
import de.hybris.platform.b2b.services.B2BOrderService;
import de.hybris.platform.b2b.company.B2BCommerceUnitService;
import de.hybris.platform.core.model.order.OrderModel;
import de.hybris.platform.ordersplitting.model.ConsignmentModel;
import de.hybris.platform.servicelayer.user.UserService;

import jakarta.annotation.Resource;

import java.math.BigDecimal;
import java.text.SimpleDateFormat;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.stream.Collectors;

import org.springframework.context.annotation.Scope;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;

import com.sabmiller.core.b2b.services.SABMDeliveryDateCutOffService;
import com.sabmiller.core.notification.service.NotificationService;
import com.sabmiller.facades.customer.SABMCustomerFacade;
import com.sabmiller.facades.delivery.data.B2bDeliveryDatesConfig;
import com.sabmiller.facades.delivery.data.DeliveryModePackTypeDeliveryDatesData;
import com.sabmiller.facades.merchant.suite.data.SABMBankDetailsData;
import com.sabmiller.facades.merchant.suite.data.SABMCreditCardTransactionData;
import com.sabmiller.merchantsuiteservices.exception.SABMMerchantSuiteConfigurationException;
import com.sabmiller.merchantsuiteservices.exception.SABMMerchantSuiteMissingBankDetailsException;
import com.sabmiller.merchantsuiteservices.exception.SABMMerchantSuiteTokenAPIException;
import com.sabmiller.merchantsuiteservices.exception.SABMMerchantSuiteTokenException;
import com.sabmiller.merchantsuiteservices.facade.impl.SABMMerchantSuitePaymentFacadeImpl;
import com.sabmiller.merchantsuiteservices.service.SABMMerchantSuitePaymentService;

/**
 * Controller for B2BUnit specific functionality which is not specific to a certain page.
 *
 * @author joshua.a.antony
 */
@Controller
@Scope("tenant")
public class B2BUnitController extends AbstractController
{

	@Resource(name = "customerFacade")
	private SABMCustomerFacade customerFacade;


	@Resource(name = "sabmDeliveryDateCutOffService")
	private SABMDeliveryDateCutOffService deliveryDateCutOffService;

	@Resource(name = "b2bCommerceUnitService")
	private B2BCommerceUnitService b2bCommerceUnitService;
	
	@Resource(name="notificationService")
	private NotificationService notificationService;
	
	@Resource(name="b2bOrderService")
	private B2BOrderService b2bOrderService;

	@Resource
	private UserService userService;
	@Resource
	private SABMMerchantSuitePaymentService sabmMerchantSuitePaymentService;

	@Resource
	SABMMerchantSuitePaymentFacadeImpl sabmMerchantSuitePaymentFacade;

	/**
	 * Checks if the deals are still being refreshed. On login, SAP Deals service is conditionally invoked. The deal call
	 * status is set to IN_PROGRESS in the B2BUnit before SAP invocation and the status is reset to either DONE or ERROR
	 * after the deals have been persisted in Hybris.If the status is still IN_PROGRESS, then loading image needs to be
	 * displayed to the user in Deals and PDP pages and this method is used for the same (ajax call every 5 min or so)
	 */
	@GetMapping(value = "/b2bunit/checkDealsRefreshStatus", produces = "application/json")
	public ResponseEntity<String> checkDealsRefreshStatus(final Model model)
	{
		final HttpHeaders httpHeaders = new HttpHeaders();
		httpHeaders.setContentType(MediaType.APPLICATION_JSON);
		return new ResponseEntity<String>("{\"status\": \"" + customerFacade.isDealRefreshInProgress() + "\"}", httpHeaders,
				HttpStatus.OK);
	}

	/**
	 * Checks if the CUP(Customer Unit Price) are still being refreshed. On login, SAP Pricing service is conditionally
	 * invoked. The CUP call status is set to IN_PROGRESS in the B2BUnit before SAP invocation and the status is reset to
	 * either DONE or ERROR after the deals have been persisted in Hybris.If the status is still IN_PROGRESS, then
	 * loading image needs to be displayed to the user in PLP and PDP pages and this method is used for the same (ajax
	 * call every 5 min or so)
	 */
	@GetMapping(value = "/b2bunit/checkCupRefreshStatus", produces = "application/json")
	public ResponseEntity<String> checkCupRefreshStatus(final Model model)
	{
		final HttpHeaders httpHeaders = new HttpHeaders();
		httpHeaders.setContentType(MediaType.APPLICATION_JSON);
		return new ResponseEntity<String>("{\"status\": \"" + customerFacade.isCupRefreshInProgress() + "\"}", httpHeaders,
				HttpStatus.OK);
	}

	/**
	 * Checks if the CUP(Customer Unit Price) or Deals are still being refreshed. On login, SAP Pricing and Deals service
	 * is conditionally invoked. The CUP and Deals call status is set to IN_PROGRESS in the B2BUnit before SAP invocation
	 * and the status is reset to either DONE or ERROR after the deals have been persisted in Hybris.If the status is
	 * still IN_PROGRESS, then loading image needs to be displayed to the user and this method is used for the same (ajax
	 * call every 5 min or so)
	 */
	@GetMapping(value = "/b2bunit/checkCupDealsRefreshStatus", produces = "application/json")
	public ResponseEntity<String> checkCupDealsRefreshStatus(final Model model)
	{
		final HttpHeaders httpHeaders = new HttpHeaders();
		httpHeaders.setContentType(MediaType.APPLICATION_JSON);
		return new ResponseEntity<String>("{\"status\": \""
				+ (customerFacade.isDealRefreshInProgress()) + "\"}", httpHeaders,
				HttpStatus.OK);
	}


	
	@GetMapping(value = "/b2bunit/getdeliverydates",produces = "application/json")
	@RequireHardLogIn
	 
	public @ResponseBody Object  getDeliveryDates()
	{
		B2bDeliveryDatesConfig deliveryDatesConfig = customerFacade.getDeliveryDatesConfig(b2bCommerceUnitService.getParentUnit());
		List<DeliveryModePackTypeDeliveryDatesData> data = deliveryDatesConfig.getDeliveryDatesData();
				
		
		Map<Integer, Object> mapDates= new HashMap<>();
		final SimpleDateFormat simpleDateFormat = new SimpleDateFormat("EEE, dd/MM/yyyy");
		for(DeliveryModePackTypeDeliveryDatesData record: data){

			Map<String, Object> mapValues= new HashMap<>();
			List<Long> dates = record.getDateList();
		   List<String> dateStrings = dates.stream().map(date -> simpleDateFormat.format(date)).collect(Collectors.toList());

			mapValues.put("mode", record.getMode());
			mapValues.put("packType", record.getPackType());
			mapValues.put("dateList", dateStrings);

			mapDates.put(data.indexOf(record), mapValues);
		}

		Map<String,Object> mapData= new HashMap<>();
		mapData.put("deliveryDatesData", mapDates);
		mapData.put("cubArrangedEnabled", deliveryDatesConfig.getCubArrangedEnabled());
		mapData.put("customerArrangedEnabled", deliveryDatesConfig.getCustomerArrangedEnabled());
		mapData.put("customerOwned", deliveryDatesConfig.getCustomerOwned());
		mapData.put("selectedCarrier", deliveryDatesConfig.getSelectedCarrier());
		mapData.put("shippingCarriers", deliveryDatesConfig.getShippingCarriers());

		return mapData;
		
	}
	
	@GetMapping(value = "/b2bunit/sendUnableToDeliverEmailOrSms",produces = "application/json")
	@RequireHardLogIn
	public @ResponseBody void  sendUnableToDeliverEmailOrSms(@RequestParam("code") String  code)
	{
		Set<ConsignmentModel> consignments = b2bOrderService.getOrderForCode(code).getConsignments();
		ConsignmentModel consignmentModel =  consignments.iterator().next();
		notificationService.sendOrderUnableToDeliverNotification(consignmentModel);
	}
	
	@GetMapping(value = "/b2bunit/sendNextInQueueEmailOrSms",produces = "application/json")
	@RequireHardLogIn
	public @ResponseBody void  sendNextInQueueEmailOrSms(@RequestParam("code") String  code)
	{
		Set<ConsignmentModel> consignments = b2bOrderService.getOrderForCode(code).getConsignments();
		ConsignmentModel consignmentModel =  consignments.iterator().next();
		notificationService.sendOrderNextInQueueDeliveryNotification(consignmentModel);
	}


	@GetMapping(value = "/b2bunit/sendETANotificationEmailOrSms",produces = "application/json")
	@RequireHardLogIn
	public @ResponseBody void  sendETANotificationEmailOrSms(@RequestParam("code") String  code)
	{
		Set<ConsignmentModel> consignments = b2bOrderService.getOrderForCode(code).getConsignments();
		ConsignmentModel consignmentModel =  consignments.iterator().next();
		notificationService.sendOrderETANotification(consignmentModel);
	}

	@GetMapping(value = "/b2bunit/sendDeliveredNotificationEmailOrSms",produces = "application/json")
	@RequireHardLogIn
	public @ResponseBody void  sendOrderDeliveredNotificationEmailOrSms(@RequestParam("code") String  code)
	{
		Set<ConsignmentModel> consignments = b2bOrderService.getOrderForCode(code).getConsignments();
		ConsignmentModel consignmentModel =  consignments.iterator().next();
		notificationService.sendOrderDeliveredNotification(consignmentModel);
	}

}
