/**
 *
 */
package com.sabmiller.integration.validation;

import org.apache.commons.validator.ValidatorException;


/**
 * The Interface Validator.
 *
 * @param <T>
 *           the generic type
 */
public interface Validator<T>
{

	/**
	 * Validate.
	 *
	 * @param obj
	 *           the obj
	 * @throws ValidatorException
	 *            the validator exception
	 */
	void validate(T obj) throws ValidatorException;
}
