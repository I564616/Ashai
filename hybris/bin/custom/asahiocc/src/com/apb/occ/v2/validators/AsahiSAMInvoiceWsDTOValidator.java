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
package com.apb.occ.v2.validators;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.validation.Errors;
import org.springframework.validation.ValidationUtils;
import org.springframework.validation.Validator;

import de.hybris.platform.asahiocc.dto.sam.AsahiSAMInvoiceWsDTO;


/**
 * Validates instances of {@link AsahiSAMInvoiceWsDTO}.
 * 
 * @author Kuldeep.Singh1
 * 
 */
public class AsahiSAMInvoiceWsDTOValidator implements Validator
{
	// Creates logger
	final Logger logger = LoggerFactory.getLogger(AsahiSAMInvoiceWsDTOValidator.class);
	private static final String FIELD_REQUIRED_MESSAGE_ID = "field.required";

	/**
	 * Validate.
	 * 
	 * @param target
	 *           the target
	 * @param errors
	 *           the errors
	 */
	@Override
	public void validate(final Object target, final Errors errors)
	{
		final AsahiSAMInvoiceWsDTO invoice = (AsahiSAMInvoiceWsDTO) target;

		logger.debug("Validating Invoice with Document Number: " + invoice.getDocumentNumber());

		ValidationUtils.rejectIfEmptyOrWhitespace(errors, "documentNumber", FIELD_REQUIRED_MESSAGE_ID);

		logger.debug("Invoice with Document Number: " + invoice.getDocumentNumber() + " is validated");
	}

	@Override
	public boolean supports(final Class clazz)
	{
		return AsahiSAMInvoiceWsDTO.class.isAssignableFrom(clazz);
	}
}
