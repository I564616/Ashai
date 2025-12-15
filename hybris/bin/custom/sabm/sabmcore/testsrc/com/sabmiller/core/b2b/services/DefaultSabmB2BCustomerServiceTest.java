/**
 *
 */
package com.sabmiller.core.b2b.services;

import static org.mockito.BDDMockito.given;

import de.hybris.bootstrap.annotations.UnitTest;
import de.hybris.platform.b2b.model.B2BCustomerModel;
import de.hybris.platform.b2b.model.B2BUnitModel;
import de.hybris.platform.core.model.security.PrincipalGroupModel;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.MockitoAnnotations;

import com.sabmiller.core.b2b.dao.SabmB2BCustomerDao;
import com.sabmiller.core.b2b.services.impl.DefaultSabmB2BCustomerServiceImpl;


/**
 * DefaultSabmB2BCustomerServiceTest
 */
@UnitTest
public class DefaultSabmB2BCustomerServiceTest
{

	@Mock
	private SabmB2BCustomerDao sabmB2BCustomerDao;


	@InjectMocks
	private final DefaultSabmB2BCustomerServiceImpl sabmB2BCustomerService = new DefaultSabmB2BCustomerServiceImpl();;

	@Before
	public void setUp()
	{
		MockitoAnnotations.initMocks(this);
	}


	@Test
	public void testGetUsersByGroups()
	{
		final List<String> andUids = new ArrayList<>();
		andUids.add("b2badmingroup");
		andUids.add("b2bassistantgroup");

		final List<String> orUids = new ArrayList<>();
		orUids.add("testUid1");
		final B2BUnitModel prinmodel = Mockito.mock(B2BUnitModel.class);
		given(prinmodel.getUid()).willReturn("testUid1");
		final Set<PrincipalGroupModel> groups = new HashSet<PrincipalGroupModel>();
		groups.add(prinmodel);

		final B2BCustomerModel b2bCustomer = Mockito.mock(B2BCustomerModel.class);
		given(b2bCustomer.getGroups()).willReturn(groups);
		final List<B2BCustomerModel> b2bCustomers = new ArrayList<>();
		final B2BCustomerModel b2bCustomer1 = Mockito.mock(B2BCustomerModel.class);
		final B2BCustomerModel b2bCustomer2 = Mockito.mock(B2BCustomerModel.class);
		b2bCustomers.add(b2bCustomer1);
		b2bCustomers.add(b2bCustomer2);
		given(sabmB2BCustomerDao.getCustomerByUnits(orUids, andUids)).willReturn(b2bCustomers);


		final List<B2BCustomerModel> model = sabmB2BCustomerService.getUsersByGroups(b2bCustomer);
		Assert.assertEquals(2, model.size());

	}

}
