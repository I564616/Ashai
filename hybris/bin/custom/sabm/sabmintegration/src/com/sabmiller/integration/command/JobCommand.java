package com.sabmiller.integration.command;

/**
 * The Interface JobCommand provide only one method to implement "execute" used to perform the job itself.
 *
 * @param <T>
 *           the generic type
 */
public interface JobCommand<T>
{

	/**
	 * The Execute method is the entry point for a job command. Generic data input and throw exception in case of error.
	 *
	 * @param data
	 *           the data
	 * @return true, if successful
	 * @throws Exception
	 *            the exception
	 */
	boolean execute(T data) throws Exception;
}
