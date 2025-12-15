///**
// *
// */
//package com.sabmiller.webservice.customer.converters.populator;
//
//import static org.junit.Assert.assertTrue;
//
//import de.hybris.platform.b2bacceleratorfacades.order.data.B2BUnitData;
//
//import org.junit.Test;
//
//import com.sabmiller.webservice.customer.Customer;
//import com.sabmiller.webservice.customer.Customer.CustomerRelationship;
//
//
///**
// * @author joshua.a.antony
// *
// */
//public class CustomerAddressPopulatorTest
//{
//
//
//	@Test
//	public void testWithIncorrectRelationships()
//	{
//		final Customer customer = new Customer();
//		customer.getCustomerRelationship().add(create("SH1", "123", true));
//		customer.getCustomerRelationship().add(create("SP", "123", true));
//		customer.getCustomerRelationship().add(create("BO", "123", true));
//
//		final B2BUnitData b2bUnitData = new B2BUnitData();
//		final CustomerAddressPopulator populator = new CustomerAddressPopulator();
//		populator.populate(customer, b2bUnitData);
//
//		assertTrue(b2bUnitData.getAddresses().isEmpty());
//
//	}
//
//	protected CustomerRelationship create(final String partnerFunction, final String partnerNumber, final boolean isDefault)
//	{
//		final CustomerRelationship cr = new CustomerRelationship();
//		cr.setPartnerFunction("SH1");
//		cr.setPartnerNumber("123");
//		cr.setDefaultPartner(isDefault);
//		return cr;
//	}
//
//
//}
