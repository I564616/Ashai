package com.sabmiller.integration.provider;

/**
 * The Interface DataProvider.
 *
 * @param <K>
 *           the key type
 * @param <T>
 *           the generic type
 */
public interface DataProvider<K, T>
{

	/**
	 * Gets the data.
	 *
	 * @param dataSource
	 *           the data source
	 * @return the data
	 * @throws IllegalArgumentException
	 *            the illegal argument exception
	 */
	K getData(T dataSource) throws IllegalArgumentException;
}
