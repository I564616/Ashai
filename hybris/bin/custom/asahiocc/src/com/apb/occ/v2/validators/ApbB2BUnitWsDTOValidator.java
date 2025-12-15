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

import de.hybris.platform.asahiocc.dto.b2bunit.AbpB2BUnitWsDTO;
import de.hybris.platform.commercewebservicescommons.dto.product.ProductWsDTO;

import org.slf4j.Logger;import org.slf4j.LoggerFactory;
import org.springframework.validation.Errors;
import org.springframework.validation.ValidationUtils;
import org.springframework.validation.Validator;


/**
 * Validates instances of {@link AbpB2BUnitWsDTO}.
 * 
 */
public class ApbB2BUnitWsDTOValidator implements Validator
{
	// Creates logger
	final Logger logger = LoggerFactory.getLogger(ApbB2BUnitWsDTOValidator.class);
	private static final String FIELD_REQUIRED_MESSAGE_ID = "field.required";
	private static final String FIELD_REQUIRED_AND_NOT_TOO_LONG_MESSAGE_ID = "field.notToLong";
	private static final int MAX_PURPOSE_CODE_LENGTH = 10;
	private static final int MAX_SALES_REP_NAME_LENGTH = 30;

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
		final AbpB2BUnitWsDTO abpB2BUnit = (AbpB2BUnitWsDTO) target;
		logger.debug("Validating Customer with Abn Number: " + abpB2BUnit.getAbnNumber());

		ValidationUtils.rejectIfEmptyOrWhitespace(errors, "uid", FIELD_REQUIRED_MESSAGE_ID);
		ValidationUtils.rejectIfEmptyOrWhitespace(errors, "name", FIELD_REQUIRED_MESSAGE_ID);
		ValidationUtils.rejectIfEmptyOrWhitespace(errors, "accountNum", FIELD_REQUIRED_MESSAGE_ID);
		//ValidationUtils.rejectIfEmptyOrWhitespace(errors, "tradingName", FIELD_REQUIRED_MESSAGE_ID);

		if (null != abpB2BUnit.getPurposeCode() && abpB2BUnit.getPurposeCode().length() > MAX_PURPOSE_CODE_LENGTH)
		{
			errors.rejectValue("purposeCode", FIELD_REQUIRED_AND_NOT_TOO_LONG_MESSAGE_ID, new String[]
			{ String.valueOf(MAX_PURPOSE_CODE_LENGTH) }, null);
		}
		if (null != abpB2BUnit.getSalesRepName() && abpB2BUnit.getSalesRepName().length() > MAX_SALES_REP_NAME_LENGTH)
		{
			errors.rejectValue("salesRepName", FIELD_REQUIRED_AND_NOT_TOO_LONG_MESSAGE_ID, new String[]
			{ String.valueOf(MAX_SALES_REP_NAME_LENGTH) }, null);
		}

		logger.debug("Customer with Abn Number: " + abpB2BUnit.getAbnNumber() + " is validated");
	}

	@Override
	public boolean supports(final Class clazz)
	{
		return ProductWsDTO.class.isAssignableFrom(clazz);
	}
}
