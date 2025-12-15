/**
 *
 */
package com.sabmiller.core.b2b.services;

import static org.junit.Assert.assertNotNull;
import static org.mockito.Mockito.when;

import de.hybris.bootstrap.annotations.UnitTest;
import de.hybris.platform.b2b.dao.B2BUnitDao;
import de.hybris.platform.b2b.model.B2BCustomerModel;
import de.hybris.platform.b2b.model.B2BUnitModel;
import de.hybris.platform.core.model.security.PrincipalGroupModel;
import de.hybris.platform.core.model.security.PrincipalModel;
import de.hybris.platform.servicelayer.user.daos.UserDao;
import de.hybris.platform.servicelayer.user.impl.DefaultUserService;

import java.util.HashSet;
import java.util.List;
import java.util.Set;

import org.junit.Before;
import org.junit.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.MockitoAnnotations;


/**
 * @author ross.hengjun.zhu
 *
 */
@UnitTest
public class DefaultSabmB2BUnitServiceForSABMC438UnitTest
{
	@Mock
	private DefaultUserService userService;

	@Mock
	private B2BUnitDao b2bUnitDao;
	@Mock
	private UserDao userDao;

	private B2BUnitModel zadpUnit;
	private B2BUnitModel zalbUnit1;
	private B2BUnitModel zalbUnit2;

	private B2BCustomerModel zadpUser1;
	private B2BCustomerModel zadpUser2;
	private B2BCustomerModel zalbUser1;
	private B2BCustomerModel zalbUser2;


	@InjectMocks
	private DefaultSabmB2BUnitService b2bUnitService;

	@Before
	public void setUp() throws Exception
	{
		MockitoAnnotations.initMocks(this);
		//
		//		userService.setUserDao(userDao);
		//		b2bUnitService.setB2bUnitDao(b2bUnitDao);

		zadpUnit = Mockito.mock(B2BUnitModel.class);
		zalbUnit1 = Mockito.mock(B2BUnitModel.class);
		zalbUnit2 = Mockito.mock(B2BUnitModel.class);

		zadpUser1 = Mockito.mock(B2BCustomerModel.class);
		zadpUser2 = Mockito.mock(B2BCustomerModel.class);
		zalbUser1 = Mockito.mock(B2BCustomerModel.class);
		zalbUser2 = Mockito.mock(B2BCustomerModel.class);

		/*
		 * Init business units
		 */
		when(zadpUnit.getAccountGroup()).thenReturn("ZADP");
		when(zadpUnit.getUid()).thenReturn("Austrilia");

		zalbUnit1 = Mockito.mock(B2BUnitModel.class);
		when(zalbUnit1.getAccountGroup()).thenReturn("ZALB");
		when(zalbUnit1.getUid()).thenReturn("Melbourne");

		zalbUnit2 = Mockito.mock(B2BUnitModel.class);
		when(zalbUnit2.getAccountGroup()).thenReturn("ZALB");
		when(zalbUnit2.getUid()).thenReturn("Brisbane");

		/*
		 * Init users
		 */
		when(zadpUser1.getUid()).thenReturn("boss");
		when(zalbUser1.getUid()).thenReturn("employee1");
		when(zalbUser2.getUid()).thenReturn("employee2");

		/*
		 * Init relationship between business units, groups and users
		 */
		final Set<PrincipalModel> zadpUnitMembers = new HashSet<PrincipalModel>();
		zadpUnitMembers.add(zalbUnit1);
		zadpUnitMembers.add(zalbUnit2);
		zadpUnitMembers.add(zadpUser1);
		when(zadpUnit.getMembers()).thenReturn(zadpUnitMembers);

		final Set<PrincipalGroupModel> zadpUserGroups = new HashSet<PrincipalGroupModel>();
		zadpUserGroups.add(zadpUnit);
		when(zadpUser1.getGroups()).thenReturn(zadpUserGroups);
		when(zadpUser2.getGroups()).thenReturn(zadpUserGroups);

		final Set<PrincipalModel> zalpUnitMembers1 = new HashSet<PrincipalModel>();
		zalpUnitMembers1.add(zalbUser1);
		zalpUnitMembers1.add(zalbUser2);
		zalpUnitMembers1.add(zadpUser2);
		when(zalbUnit1.getMembers()).thenReturn(zalpUnitMembers1);

		final Set<PrincipalModel> zalpUnitMembers2 = new HashSet<PrincipalModel>();
		zalpUnitMembers2.add(zalbUser2);
		when(zalbUnit2.getMembers()).thenReturn(zalpUnitMembers2);

		final Set<PrincipalGroupModel> zalbUserGroups1 = new HashSet<PrincipalGroupModel>();
		zalbUserGroups1.add(zalbUnit1);
		final Set<PrincipalGroupModel> zalbUserGroups2 = new HashSet<PrincipalGroupModel>();
		zalbUserGroups2.add(zalbUnit2);
		when(zalbUser1.getGroups()).thenReturn(zalbUserGroups1);
		when(zalbUser2.getGroups()).thenReturn(zalbUserGroups2);
		when(zadpUser2.getGroups()).thenReturn(zadpUserGroups);
	}

	@Test
	public void testGetNoneZADPUsersWithSpecifiedBusinessUnit()
	{
		final List<B2BCustomerModel> result1 = b2bUnitService.getNoneZADPUsersWithSpecifiedBusinessUnit(zadpUnit, zadpUser1);
		assertNotNull(result1);

		final List<B2BCustomerModel> result2 = b2bUnitService.getNoneZADPUsersWithSpecifiedBusinessUnit(zalbUnit1, zalbUser1);
		assertNotNull(result2);

	}
}