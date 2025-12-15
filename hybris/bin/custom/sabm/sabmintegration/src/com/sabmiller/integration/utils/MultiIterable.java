package com.sabmiller.integration.utils;

import java.util.Collection;
import java.util.Iterator;


/**
 * The Class MultiIterable.
 *
 * @param <T>
 *           the generic type
 */
public class MultiIterable<T> implements Iterable<T>
{

	/** The iterables. */
	private final Collection<? extends Iterable<T>> iterables;

	/**
	 * Instantiates a new multi iterable.
	 *
	 * @param iterables
	 *           the iterables
	 */
	public MultiIterable(final Collection<? extends Iterable<T>> iterables)
	{
		this.iterables = iterables;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see java.lang.Iterable#iterator()
	 */
	@Override
	public Iterator<T> iterator()
	{
		return new MultiIterator<T>(iterables);
	}

}