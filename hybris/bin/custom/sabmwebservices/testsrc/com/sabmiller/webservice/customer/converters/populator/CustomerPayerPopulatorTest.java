/**
 *
 */
package com.sabmiller.webservice.customer.converters.populator;

import de.hybris.bootstrap.annotations.UnitTest;
import de.hybris.platform.b2bacceleratorfacades.order.data.B2BUnitData;

import java.util.ArrayList;
import java.util.List;

import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.mockito.InjectMocks;
import org.mockito.Mockito;
import org.mockito.MockitoAnnotations;

import com.sabmiller.webservice.customer.Customer;
import com.sabmiller.webservice.customer.Customer.CustomerRelationship;
import com.sabmiller.webservice.customer.Customer.CustomerRelationship.ID;
import com.sabmiller.webservice.customer.constants.CustomerImportConstants;


/**
 * @author Ranjith.Karuvachery
 *
 */
@UnitTest
public class CustomerPayerPopulatorTest
{


	@InjectMocks
	private final CustomerPayerPopulator customerPayerPopulator = new CustomerPayerPopulator();

	@Before
	public void setUp()
	{
		MockitoAnnotations.initMocks(this);
	}

	@Test
	public void testPopulate()
	{
		final Customer customer = Mockito.mock(Customer.class);
		final List<CustomerRelationship> relationships = new ArrayList<Customer.CustomerRelationship>();
		final CustomerRelationship customerRelationship = new CustomerRelationship();
		customerRelationship.setPartnerCode(CustomerImportConstants.PAYER.getCode());
		final Customer.CustomerRelationship.ID id = new ID();
		id.setValue("ABC");
		customerRelationship.setID(id);
		relationships.add(customerRelationship);
		Mockito.when(customer.getCustomerRelationship()).thenReturn(relationships);

		final B2BUnitData b2bUnitData = new B2BUnitData();
		customerPayerPopulator.populate(customer, b2bUnitData);

		Assert.assertEquals("ABC", b2bUnitData.getPayerId());
	}

}
