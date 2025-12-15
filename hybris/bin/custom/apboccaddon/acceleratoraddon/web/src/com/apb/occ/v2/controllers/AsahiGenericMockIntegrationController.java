/**
 *
 */
package com.apb.occ.v2.controllers;

import jakarta.annotation.Resource;
import jakarta.ws.rs.Produces;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.RestController;

import com.apb.core.util.ApbXSSEncoderUtil;
import com.apb.integration.order.dto.AsahiOrderRequest;
import com.apb.integration.service.config.AsahiConfigurationService;


/**
 * @author Kuldeep.Singh1
 *
 */
@RestController
@RequestMapping(value = "/{path:.*}/generic")
public class AsahiGenericMockIntegrationController
{
	// Creates logger
	final Logger logger = LoggerFactory.getLogger(AsahiGenericMockIntegrationController.class);

	/** The Constant CHECK_ORDER_ERROR_TEST_CASE. */
	private static final String CHECK_ORDER_ERROR_TEST_CASE = "service.order.error.test.case";

	/** The asahi configuration service. */
	@Resource(name = "asahiIconfigurationService")
	private AsahiConfigurationService asahiIconfigurationService;

	/**
	 * Gets the mock price.
	 *
	 * @param asahiOrderRequest
	 *           the asahi order request
	 * @return the mock price
	 */
	@Produces(MediaType.TEXT_PLAIN_VALUE)
	@ResponseBody
	@PostMapping(value = "/integrationService", consumes =
	{ MediaType.APPLICATION_JSON_VALUE, MediaType.APPLICATION_XML_VALUE })
	public ResponseEntity<String> genericIntegrationService(@RequestBody final AsahiOrderRequest asahiOrderRequest)
	{
		logger.debug("Calling Service with request---" + ApbXSSEncoderUtil.encodeValue(asahiOrderRequest.toString()));
		final ResponseEntity<String> response;
		if (this.asahiIconfigurationService.getBoolean(CHECK_ORDER_ERROR_TEST_CASE, false))
		{
			response = new ResponseEntity<String>(HttpStatus.BAD_REQUEST);
		}
		else
		{
			response = new ResponseEntity<String>(HttpStatus.OK);
		}
		return response;
	}
}
