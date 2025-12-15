/**
 *
 */
package com.sabmiller.integration.validation.impl;


import de.hybris.platform.cronjob.model.CronJobModel;

import org.apache.commons.validator.ValidatorException;

import com.sabmiller.integration.validation.Validator;


/**
 * The Class NoJobValidator is used to skip the validation in jobs that don't require it.
 */
public class NoJobValidator implements Validator<CronJobModel>
{

	/*
	 * (non-Javadoc)
	 *
	 * @see com.sabmiller.integration.validation.Validator#validate(java.lang.Object)
	 */
	@Override
	public void validate(final CronJobModel obj) throws ValidatorException
	{
		//No validation.
	}

}
