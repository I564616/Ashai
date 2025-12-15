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
package com.apb.storefront.controllers.misc;


import jakarta.annotation.Resource;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;

import com.apb.storefront.security.evaluator.impl.RequireHardLoginEvaluator;


/**
 * Controller for checking user's authentication status
 */
@Controller
@RequestMapping("/authentication")
public class AuthenticationController
{
	public static final String AUTHENTICATED = "authenticated";

	@Resource(name = "requireHardLoginEvaluator")
	private RequireHardLoginEvaluator requireHardLoginEvaluator;

	@GetMapping("/status")
	public ResponseEntity<String> status(final HttpServletRequest request, final HttpServletResponse response)
	{
		if (!requireHardLoginEvaluator.evaluate(request, response))
		{
			return new ResponseEntity<String>(AUTHENTICATED, HttpStatus.OK);
		}

		return new ResponseEntity<String>(HttpStatus.UNAUTHORIZED);
	}
}
