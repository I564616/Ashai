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

import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;

import jakarta.annotation.Resource;

import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;import org.slf4j.LoggerFactory;
import org.springframework.validation.Errors;
import org.springframework.validation.ValidationUtils;
import org.springframework.validation.Validator;

import com.apb.core.service.config.AsahiConfigurationService;

import de.hybris.platform.commercewebservicescommons.dto.order.OrderWsDTO;


/**
 * Validates instances of {@link OrderWsDTO}.
 * 
 * @author Kuldeep.Singh1
 * 
 */
public class AsahiOrderWsDTOValidator implements Validator
{
	// Creates logger
	final Logger logger = LoggerFactory.getLogger(AsahiOrderWsDTOValidator.class);

	/** The Constant FIELD_REQUIRED_MESSAGE_ID. */
	private static final String FIELD_REQUIRED_MESSAGE_ID = "field.required";

	/** The Constant DATE_FORMAT_LENGTH. */
	private static final String DATE_FORMAT_LENGTH = "site.date.format.length.validation.apb";

	private static final String FIELD_DATE_FORMAT_MESSAGE_ID = "field.date.format";

	private static final String ORDER_VALIDATION_ATTRIBUTES = "validate.order.attributes.apb";

	/** The asahi configuration service. */
	@Resource(name = "asahiConfigurationService")
	private AsahiConfigurationService asahiConfigurationService;

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
		final OrderWsDTO order = (OrderWsDTO) target;

		logger.debug("Validating order with code: " + order.getSalesOrderId());

		//Picking date format regex from config/property file
		final String dateFormatLength = this.asahiConfigurationService.getString(DATE_FORMAT_LENGTH,
				"[0-9]{2,2}-[0-9]{2,2}+-[0-9]{4,4}+");

		
		SimpleDateFormat dateTimeFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss Z");
		

		
		//Picking attributes which has to be validated from config/property file
		final String attributesToBeValidated = this.asahiConfigurationService
				.getString(ORDER_VALIDATION_ATTRIBUTES, "salesOrderId");

		final String[] attributeList = attributesToBeValidated.split(",");

		for (int i = 0; i < attributeList.length; i++)
		{
			ValidationUtils.rejectIfEmptyOrWhitespace(errors, attributeList[i], FIELD_REQUIRED_MESSAGE_ID);
		}

		String scheduleDeliveryDate = order.getScheduleDeliveryDate();
		
		/*if (StringUtils.isNotEmpty(scheduleDeliveryDate) && !scheduleDeliveryDate.matches(dateFormatLength))
		{
			errors.rejectValue("scheduleDeliveryDate", "Please enter valid date");
		}
		
		String scheduleShippingDate = order.getScheduleShippingDate();
		if (StringUtils.isNotEmpty(scheduleShippingDate) && !scheduleShippingDate.matches(dateFormatLength))
		{
			errors.rejectValue("scheduleShippingDate", FIELD_REQUIRED_MESSAGE_ID);
		}*/
		
		
		if (null != order.getBackendCreatedDate() )
		{
			try
			{
				dateTimeFormat.parse(order.getBackendCreatedDate()); // Create a new Date object
			}
			catch (final ParseException e)
			{
				errors.rejectValue("backendCreatedDate", FIELD_REQUIRED_MESSAGE_ID);
			}
			
			
		}
		if (null != order.getTimePickListDate() )
		{
			try
			{
				dateTimeFormat.parse(order.getTimePickListDate()); // Create a new Date object
			}
			catch (final ParseException e)
			{
				errors.rejectValue("timePickListDate", FIELD_REQUIRED_MESSAGE_ID);
			}
		
		}
		if (null != order.getInvoiceCreatedDate())
		{
			try
			{
				dateTimeFormat.parse(order.getInvoiceCreatedDate()); // Create a new Date object
			}
			catch (final ParseException e)
			{
				errors.rejectValue("invoiceCreatedDate", FIELD_REQUIRED_MESSAGE_ID);
			}
			
		}

		logger.debug("order with code: " + order.getSalesOrderId() + " is validated");
	}

	@Override
	public boolean supports(final Class clazz)
	{
		return OrderWsDTO.class.isAssignableFrom(clazz);
	}
}
