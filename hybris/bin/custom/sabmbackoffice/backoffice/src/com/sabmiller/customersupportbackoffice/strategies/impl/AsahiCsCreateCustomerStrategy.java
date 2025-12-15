/**
 *
 */
package com.sabmiller.customersupportbackoffice.strategies.impl;

import de.hybris.platform.commerceservices.customer.DuplicateUidException;

import com.sabmiller.cockpitng.customersupportbackoffice.data.AsahiCsCreateB2BCustomerForm;
/**
 * @author EG588BU
 *
 */
public interface AsahiCsCreateCustomerStrategy
{
	public interface CsCreateCustomerStrategy {
	   void createCustomer(AsahiCsCreateB2BCustomerForm var1) throws DuplicateUidException;
	}
}
