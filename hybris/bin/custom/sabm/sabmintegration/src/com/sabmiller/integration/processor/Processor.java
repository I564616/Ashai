package com.sabmiller.integration.processor;

import com.sabmiller.integration.processor.exception.ProcessorException;


/**
 * The Interface Processor.
 *
 * @param <S>
 *           the generic type
 * @param <T>
 *           the generic type
 */
public interface Processor<S, T>
{

	/**
	 * Process.
	 *
	 * @param source
	 *           the source
	 * @return the t
	 * @throws ProcessorException
	 *            the processor exception
	 */
	T process(S source) throws ProcessorException;
}
