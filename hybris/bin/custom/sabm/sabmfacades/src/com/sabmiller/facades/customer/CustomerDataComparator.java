/**
 *
 */
package com.sabmiller.facades.customer;

import de.hybris.platform.commercefacades.user.data.CustomerData;

import java.util.Comparator;


/**
 *
 */
public class CustomerDataComparator implements Comparator<CustomerData>
{

	/*
	 * (non-Javadoc)
	 *
	 * @see java.util.Comparator#compare(java.lang.Object, java.lang.Object)
	 */
	@Override
	public int compare(final CustomerData o1, final CustomerData o2)
	{
		if (o1.isActive() != o2.isActive() && o1.isActive())
		{
			return -1;
		}

		if (o1.isActive() != o2.isActive() && o2.isActive())
		{
			return 1;
		}

		if (o1.isActive() == o2.isActive())
		{
			final String o1FirstName = o1.getFirstName() != null ? o1.getFirstName() : "";
			final String o2FirstName = o2.getFirstName() != null ? o2.getFirstName() : "";
			

			if (o1FirstName.compareToIgnoreCase(o2FirstName) > 0)
			{
				return 1;
			}
			else if (o1FirstName.compareToIgnoreCase(o2FirstName) < 0)
			{
				return -1;
			}



		}

		return 0;
	}

}
