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

import de.hybris.platform.apboccaddon.dto.sam.AsahiSAMStatementWsDTO;


/**
 * Validates instances of {@link AsahiSAMStatementWsDTO}.
 * 
 * @author Kuldeep.Singh1
 * 
 */
public class AsahiSAMStatementWsDTOValidator implements Validator
{
	// Creates logger
	final Logger logger = LoggerFactory.getLogger(AsahiSAMStatementWsDTOValidator.class);
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
		final AsahiSAMStatementWsDTO statement = (AsahiSAMStatementWsDTO) target;

		logger.debug("Validating Statement with Number: " + statement.getStatementNumber());

		ValidationUtils.rejectIfEmptyOrWhitespace(errors, "statementNumber", FIELD_REQUIRED_MESSAGE_ID);

		logger.debug("Statement with Number: " + statement.getStatementNumber() + " is validated");
	}

	@Override
	public boolean supports(final Class clazz)
	{
		return AsahiSAMStatementWsDTO.class.isAssignableFrom(clazz);
	}
}
