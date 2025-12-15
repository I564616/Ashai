/*
 * [y] hybris Platform
 *
 * Copyright (c) 2017 SAP SE or an SAP affiliate company.  All rights reserved.
 *
 * This software is the confidential and proprietary information of SAP
 * ("Confidential Information"). You shall not disclose such Confidential
 * Information and shall use it only in accordance with the terms of the
 * license agreement you entered into with SAP.
 */
package com.apb.storefront.controllers.integration;


import de.hybris.platform.acceleratorservices.payment.PaymentService;

import jakarta.annotation.Resource;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpSession;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.PostMapping;


/**
 * Controller to handle merchant callbacks from a subscription provider
 */
@Controller
public class MerchantCallbackController extends BaseIntegrationController
{
	@Resource(name = "acceleratorPaymentService")
	private PaymentService acceleratorPaymentService;


	@PostMapping("/integration/merchant_callback")
	public void process(final HttpServletRequest request, final HttpServletResponse response)
	{
		initializeSiteFromRequest(request);

		try
		{
			acceleratorPaymentService.handleCreateSubscriptionCallback(getParameterMap(request));
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
