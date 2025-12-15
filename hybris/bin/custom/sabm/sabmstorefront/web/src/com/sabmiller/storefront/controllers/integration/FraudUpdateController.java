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
package com.sabmiller.storefront.controllers.integration;

import de.hybris.platform.acceleratorservices.payment.PaymentService;

import jakarta.annotation.Resource;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpSession;

import org.springframework.stereotype.Controller;
import org.springframework.util.MultiValueMap;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;


/**
 * Generic endpoint to receive fraud updates.
 */
@Controller
public class FraudUpdateController extends BaseIntegrationController
{
	@Resource(name = "acceleratorPaymentService")
	private PaymentService acceleratorPaymentService;

	@PostMapping("/integration/order_review_callback")
	public void process(@RequestBody final MultiValueMap<String, String> bodyParameterMap, final HttpServletRequest request,
			final HttpServletResponse response) throws Exception
	{
		initializeSiteFromRequest(request);

		try
		{
			acceleratorPaymentService.handleFraudUpdateCallback(bodyParameterMap.toSingleValueMap());
		}
		finally
		{
			//Kill this session at the end of the request processing in order to reduce the server overhead, otherwise
			//this session will hang around until it's timed out.
			final HttpSession session = request.getSession(false);
			if (session != null)
			{
				session.invalidate();
			}
		}
		response.setStatus(HttpServletResponse.SC_OK);
	}
}
