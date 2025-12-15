package com.sabmiller.integration.processor;

import java.util.Collection;
import java.util.Map;

import org.apache.commons.collections4.CollectionUtils;
import org.apache.commons.collections4.MapUtils;
import org.apache.commons.lang3.StringUtils;

import com.sabmiller.integration.processor.exception.ExecutionException;
import com.sabmiller.integration.processor.exception.ProcessorException;
import com.sabmiller.integration.processor.exception.ValidatorException;


/**
 * The Class AbstractProcessor.
 *
 * @param <S>
 *           the generic type
 * @param <T>
 *           the generic type
 */
public abstract class AbstractProcessor<S, T> implements Processor<S, T>
{

	/*
	 * (non-Javadoc)
	 * 
	 * @see com.sabmiller.integration.processor.Processor#process(java.lang.Object)
	 */
	@Override
	public final T process(final S source) throws ProcessorException
	{
		validate(source);
		return execute(source);
	}

	/**
	 * Validate.
	 *
	 * @param source
	 *           the source
	 * @throws ValidatorException
	 *            the validator exception
	 */
	protected abstract void validate(S source) throws ValidatorException;

	/**
	 * Execute.
	 *
	 * @param source
	 *           the source
	 * @return the t
	 * @throws ExecutionException
	 *            the execution exception
	 */
	protected abstract T execute(S source) throws ExecutionException;

	/**
	 * Validate attribute.
	 *
	 * @param value
	 *           the value
	 * @param message
	 *           the message
	 * @throws ValidatorException
	 *            the validator exception
	 */
	protected void validateAttribute(final String value, final String message) throws ValidatorException
	{
		if (StringUtils.isEmpty(value))
		{
			throw new ValidatorException(message);
		}
	}

	/**
	 * Validate attribute.
	 *
	 * @param value
	 *           the value
	 * @param message
	 *           the message
	 * @throws ValidatorException
	 *            the validator exception
	 */
	protected void validateAttribute(final Collection<?> value, final String message) throws ValidatorException
	{
		if (CollectionUtils.isEmpty(value))
		{
			throw new ValidatorException(message);
		}
	}

	/**
	 * Validate attribute.
	 *
	 * @param value
	 *           the value
	 * @param message
	 *           the message
	 * @throws ValidatorException
	 *            the validator exception
	 */
	protected void validateAttribute(final Map<?, ?> value, final String message) throws ValidatorException
	{
		if (MapUtils.isEmpty(value))
		{
			throw new ValidatorException(message);
		}
	}

	/**
	 * Validate attribute.
	 *
	 * @param value
	 *           the value
	 * @param message
	 *           the message
	 * @throws ValidatorException
	 *            the validator exception
	 */
	protected void validateAttribute(final Object value, final String message) throws ValidatorException
	{
		if (value == null)
		{
			throw new ValidatorException(message);
		}
	}
}
