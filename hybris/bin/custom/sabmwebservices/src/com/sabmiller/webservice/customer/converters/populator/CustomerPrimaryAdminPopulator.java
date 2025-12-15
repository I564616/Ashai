/**
 *
 */
package com.sabmiller.webservice.customer.converters.populator;

import de.hybris.platform.b2bacceleratorfacades.order.data.B2BUnitData;
import de.hybris.platform.commercefacades.user.data.CustomerData;
import de.hybris.platform.commerceservices.strategies.CustomerNameStrategy;
import de.hybris.platform.converters.Populator;

import jakarta.annotation.Resource;

import org.apache.log4j.Logger;

import com.sabmiller.webservice.customer.Customer;
import com.sabmiller.webservice.customer.Customer.ContactPersons;
import com.sabmiller.webservice.customer.constants.CustomerImportConstants;


/**
 * @author joshua.a.antony
 *
 */
public class CustomerPrimaryAdminPopulator implements Populator<Customer, B2BUnitData>
{

	private static final Logger LOG = Logger.getLogger(CustomerPrimaryAdminPopulator.class);

	@Resource(name = "customerNameStrategy")
	private CustomerNameStrategy customerNameStrategy;

	/*
	 * (non-Javadoc)
	 *
	 * @see
	 * com.sabmiller.webservice.b2bunit.strategies.PrimaryAdminStrategy#derivePrimaryAdmin(com.sabmiller.webservice.customer
	 * .Customer)
	 */
	@Override
	public void populate(final Customer customer, final B2BUnitData b2bUnitData)
	{

		if (isPrimaryAdminRequest(b2bUnitData) && contactPersonExist(customer))
		{
			LOG.debug("This is a ZADP Customer. Trying to locate Primary Admin");
			for (final ContactPersons contactPerson : customer.getContactPersons())
			{
				if (contactPerson.getAddress() != null
						&& CustomerImportConstants.PRIMARY_ADMIN_DEPT_ID.getCode().equals(contactPerson.getAddress().getDepartmentID()))
				{
					LOG.info("Found Primary Admin.");
					final CustomerData customerData = new CustomerData();
					customerData.setEmail(contactPerson.getAddress().getEMAIL());
					customerData.setName(customerNameStrategy.getName(contactPerson.getAddress().getPersonName().getGivenName(),
							contactPerson.getAddress().getPersonName().getFamilyName()));
					customerData.setFirstName(contactPerson.getAddress().getPersonName().getGivenName());
					customerData.setLastName(contactPerson.getAddress().getPersonName().getFamilyName());
					b2bUnitData.setPrimaryAdmin(customerData);
					break;
				}
			}

			if (b2bUnitData.getPrimaryAdmin() == null)
			{
				LOG.error("Unable to locate primary admin in the request ::: Most probably a buggy request!");//TODO : Joshua => Do we need to send any emails?
			}
		}
	}

	protected boolean contactPersonExist(final Customer customer)
	{
		return customer.getContactPersons() != null && !customer.getContactPersons().isEmpty();
	}

	protected boolean isPrimaryAdminRequest(final B2BUnitData b2bUnitData)
	{
		return CustomerImportConstants.TOP_LEVEL_CUSTOMER.getCode().equals(b2bUnitData.getAccountGroup());
	}
}
