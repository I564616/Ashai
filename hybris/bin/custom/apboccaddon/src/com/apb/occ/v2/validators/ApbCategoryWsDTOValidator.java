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

import de.hybris.platform.commercewebservicescommons.dto.product.CategoryWsDTO;
import de.hybris.platform.commercewebservicescommons.dto.product.ProductWsDTO;

import org.slf4j.Logger;import org.slf4j.LoggerFactory;
import org.springframework.validation.Errors;
import org.springframework.validation.ValidationUtils;
import org.springframework.validation.Validator;


/**
 * Validates instances of {@link CategoryWsDTO}.
 * 
 * @author Kuldeep.Singh1
 * 
 */
public class ApbCategoryWsDTOValidator implements Validator
{
	// Creates logger
	final Logger logger = LoggerFactory.getLogger(ApbCategoryWsDTOValidator.class);
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
		final CategoryWsDTO category = (CategoryWsDTO) target;

		logger.debug("Validating category with code: " + category.getCode());

		ValidationUtils.rejectIfEmptyOrWhitespace(errors, "code", FIELD_REQUIRED_MESSAGE_ID);

		logger.debug("category with code: " + category.getCode() + " is validated");
	}

	@Override
	public boolean supports(final Class clazz)
	{
		return ProductWsDTO.class.isAssignableFrom(clazz);
	}
}
