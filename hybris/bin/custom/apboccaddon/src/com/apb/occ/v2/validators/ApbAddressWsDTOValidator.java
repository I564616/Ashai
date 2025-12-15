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

import de.hybris.platform.commercewebservicescommons.dto.user.AddressWsDTO;

import org.slf4j.Logger;import org.slf4j.LoggerFactory;
import org.springframework.validation.Errors;
import org.springframework.validation.ValidationUtils;
import org.springframework.validation.Validator;


/**
 * Validates instances of {@link AddressWsDTO}.
 * 
 */
public class ApbAddressWsDTOValidator implements Validator
{
	// Creates logger
	final Logger logger = LoggerFactory.getLogger(ApbAddressWsDTOValidator.class);

	/** The Constant FIELD_REQUIRED_MESSAGE_ID. */
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
		final AddressWsDTO address = (AddressWsDTO) target;
		logger.debug("Validating Address with recordId: " + address.getRecordId());

		ValidationUtils.rejectIfEmptyOrWhitespace(errors, "recordId", FIELD_REQUIRED_MESSAGE_ID);
		ValidationUtils.rejectIfEmptyOrWhitespace(errors, "customerRecId", FIELD_REQUIRED_MESSAGE_ID);

		logger.debug("Address with recordId:: " + address.getRecordId() + " is validated");
	}

	@Override
	public boolean supports(final Class clazz)
	{
		return AddressWsDTO.class.isAssignableFrom(clazz);
	}
}
