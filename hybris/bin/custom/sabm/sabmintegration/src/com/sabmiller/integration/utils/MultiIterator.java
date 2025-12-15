package com.sabmiller.integration.utils;

import java.util.Collection;
import java.util.Iterator;
import java.util.NoSuchElementException;


/**
 * The Class MultiIterator.
 *
 * @param <T>
 *           the generic type
 */
public class MultiIterator<T> implements Iterator<T>
{

	/** The itor. */
	private final Iterator<? extends Iterable<T>> itor;

	/** The current. */
	private Iterator<T> current;

	/**
	 * Instantiates a new multi iterator.
	 *
	 * @param iterables
	 *           the iterables
	 */
	public MultiIterator(final Collection<? extends Iterable<T>> iterables)
	{
		if (iterables == null)
		{
			throw new IllegalArgumentException("Null collection!");
		}
		itor = iterables.iterator();
		if (itor.hasNext())
		{
			current = nextIterator();
		}
	}

	/**
	 * Next iterator.
	 *
	 * @return the iterator
	 */
	private Iterator<T> nextIterator()
	{
		return itor.next().iterator();
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see java.util.Iterator#hasNext()
	 */
	@Override
	public boolean hasNext()
	{
		if (current != null && current.hasNext())
		{
			return true;
		}
		else
		{
			while (itor.hasNext())
			{
				current = nextIterator();
				if (current.hasNext())
				{
					return true;
				}
			}
			return false;
		}
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see java.util.Iterator#next()
	 */
	@Override
	public T next()
	{
		if (current.hasNext())
		{
			return current.next();
		}
		else
		{
			while (itor.hasNext())
			{
				current = nextIterator();
				if (current.hasNext())
				{
					current.next();
				}
			}
			throw new NoSuchElementException();
		}
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see java.util.Iterator#remove()
	 */
	@Override
	public void remove()
	{
		throw new UnsupportedOperationException("Remove not supported for this kind of iterator");
	}
}
