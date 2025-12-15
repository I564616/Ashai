/**
 *
 */
package com.apb.facades.populators;

import de.hybris.platform.b2b.model.B2BCustomerModel;
import de.hybris.platform.commercefacades.user.data.CustomerData;
import de.hybris.platform.commerceservices.strategies.CustomerNameStrategy;
import de.hybris.platform.enumeration.EnumerationService;
import de.hybris.platform.servicelayer.user.UserService;

import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.MockitoAnnotations;

import com.apb.core.util.AsahiSiteUtil;
import com.apb.facades.product.data.AsahiRoleData;
import com.sabmiller.core.enums.AsahiRole;


/**
 * @author Saumya.Mittal1
 *
 */
public class AsahiCustomerReversePopulatorTest
{


	@InjectMocks
	private final AsahiCustomerReversePopulator customerReversePopulator = new AsahiCustomerReversePopulator();;


	@Mock
	private EnumerationService enumerationService;

	@Mock
	private UserService userService;

	@Mock
	private AsahiSiteUtil asahiSiteUtil;

	@Mock
	private CustomerNameStrategy customerNameStrategy;

	@Before
	public void setUp()
	{
		MockitoAnnotations.initMocks(this);
		customerReversePopulator.setUserService(userService);
		customerReversePopulator.setCustomerNameStrategy(customerNameStrategy);
		Mockito.when(customerNameStrategy.getName(Mockito.anyString(), Mockito.anyString())).thenReturn("Test");
		Mockito.when(asahiSiteUtil.isCub()).thenReturn(Boolean.FALSE);
	}

	@Test
	public void testPopulate()
	{
		final CustomerData customer = new CustomerData();
		customer.setUid("testuser@test.com");
		customer.setEmail("testuser@test.com");
		customer.setName("Test User");
		customer.setFirstName("Test");
		customer.setLastName("User");
		customer.setContactNumber("0000000");
		final AsahiRoleData role = new AsahiRoleData();
		role.setCode(AsahiRole.OWNER.getCode());
		role.setName(AsahiRole.OWNER.toString());
		customer.setAsahiRole(role);

		Mockito.when(enumerationService.getEnumerationValue(AsahiRole.class, AsahiRole.OWNER.getCode()))
				.thenReturn(AsahiRole.OWNER);
		final B2BCustomerModel customerModel = new B2BCustomerModel();
		customerReversePopulator.populate(customer, customerModel);
		Assert.assertEquals("testuser@test.com", customerModel.getEmail());
	}
}
