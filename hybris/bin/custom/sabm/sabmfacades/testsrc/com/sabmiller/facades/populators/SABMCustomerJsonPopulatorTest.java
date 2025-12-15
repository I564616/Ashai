/**
 *
 */
package com.sabmiller.facades.populators;

import static org.mockito.BDDMockito.given;

import de.hybris.bootstrap.annotations.UnitTest;
import de.hybris.platform.b2b.model.B2BCustomerModel;
import de.hybris.platform.core.model.security.PrincipalGroupModel;
import de.hybris.platform.core.model.user.UserGroupModel;
import de.hybris.platform.core.model.user.UserModel;
import de.hybris.platform.servicelayer.user.UserService;

import java.util.List;
import java.util.Set;

import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import com.google.common.collect.Lists;
import com.google.common.collect.Sets;
import com.sabmiller.core.b2b.services.SabmB2BCustomerService;
import com.sabmiller.facades.b2bunit.SabmB2BCommerceUnitFacade;
import com.sabmiller.facades.customer.CustomerJson;


/**
 * @author xue.zeng
 *
 */
@UnitTest
public class SABMCustomerJsonPopulatorTest
{
	@InjectMocks
	private SABMCustomerJsonPopulator customerJsonPopulator;

	@Mock
	private SabmB2BCommerceUnitFacade b2bCommerceUnitFacade;
	@Mock
	private SabmB2BCustomerService sabmB2BCustomerService;
	@Mock
	private UserService userService;

	@Before
	public void setUp()
	{
		MockitoAnnotations.initMocks(this);
	}

	@Test
	public void testPopulate()
	{
		final String uid = "adam.gilchrist@testsample123.com";
		final B2BCustomerModel b2bCustomer = new B2BCustomerModel();
		b2bCustomer.setEmail(uid);
		b2bCustomer.setUid(uid);
		final Set<PrincipalGroupModel> roleModels = Sets.newConcurrentHashSet();
		final UserGroupModel userGroup1 = new UserGroupModel();
		userGroup1.setUid("b2bordercustomer");
		roleModels.add(userGroup1);

		final UserGroupModel userGroup2 = new UserGroupModel();
		userGroup2.setUid("b2binvoicecustomer");
		roleModels.add(userGroup2);

		final UserGroupModel userGroup3 = new UserGroupModel();
		userGroup3.setUid("b2bassistantgroup");
		roleModels.add(userGroup3);
		b2bCustomer.setGroups(roleModels);

		given(b2bCommerceUnitFacade.isCurrentB2BUnitExistOfUid(uid)).willReturn(true);

		final List<B2BCustomerModel> adminCustomers = Lists.newArrayList();
		final B2BCustomerModel adminB2bCustomer = new B2BCustomerModel();
		adminB2bCustomer.setName("Mark Rivers");
		adminB2bCustomer.setEmail("mark.rivers@testsample123.com");
		adminB2bCustomer.setUid("mark.rivers@testsample123.com");
		adminCustomers.add(adminB2bCustomer);
		given(sabmB2BCustomerService.getUsersByGroups(b2bCustomer)).willReturn(adminCustomers);
		given(userService.getCurrentUser()).willReturn((UserModel) adminB2bCustomer);

		final CustomerJson customerJson = new CustomerJson();
		customerJsonPopulator.populate(b2bCustomer, customerJson);

		Assert.assertEquals(customerJson.getEmail(), uid);
		Assert.assertEquals(customerJson.getAdmins().size(), 1);
		Assert.assertEquals(customerJson.getPermissions().isPa(), true);
	}
}
