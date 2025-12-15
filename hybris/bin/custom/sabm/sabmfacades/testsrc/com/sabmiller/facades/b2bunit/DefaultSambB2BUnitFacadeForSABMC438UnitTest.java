/**
 *
 */
package com.sabmiller.facades.b2bunit;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.mockito.Mockito.when;

import de.hybris.bootstrap.annotations.UnitTest;
import de.hybris.platform.b2b.model.B2BCustomerModel;
import de.hybris.platform.b2b.model.B2BUnitModel;
import de.hybris.platform.b2b.services.B2BCustomerService;
import de.hybris.platform.b2bacceleratorfacades.order.data.B2BUnitData;
import de.hybris.platform.b2b.company.B2BCommerceUnitService;
import de.hybris.platform.commercefacades.user.data.CustomerData;
import de.hybris.platform.core.model.c2l.RegionModel;
import de.hybris.platform.core.model.security.PrincipalGroupModel;
import de.hybris.platform.core.model.security.PrincipalModel;
import de.hybris.platform.core.model.user.AddressModel;
import de.hybris.platform.servicelayer.dto.converter.Converter;
import de.hybris.platform.servicelayer.user.UserService;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import org.junit.Before;
import org.junit.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import com.sabmiller.core.b2b.services.SabmB2BUnitService;
import com.sabmiller.core.constants.SabmCoreConstants;

import io.jsonwebtoken.lang.Collections;


/**
 * @author ross.hengjun.zhu
 *
 */
@UnitTest
public class DefaultSambB2BUnitFacadeForSABMC438UnitTest
{

	@Mock
	private B2BCommerceUnitService b2bCommerceUnitService;
	@Mock
	private SabmB2BUnitService b2bUnitService;
	@Mock
	private UserService userService;

	@Mock
	private Converter<B2BUnitModel, B2BUnitData> b2bUnitConverter;
	@Mock
	private Converter<B2BCustomerModel, CustomerData> b2BCustomerConverter;

	@Mock
	private B2BCustomerService b2bCustomerService;

	private AddressModel contactAddress;

	private B2BUnitModel zadpUnit;

	private B2BUnitModel zalbUnit;

	private B2BUnitData zadpUnitData;

	private B2BUnitData zalbUnitData;

	private B2BCustomerModel zadpUser;

	private B2BCustomerModel zalbUser;

	private CustomerData zadpUserData;

	private CustomerData zalbUserData;

	private ArrayList<B2BCustomerModel> noneZadpUsers;

	@InjectMocks
	private DefaultSabmB2BUnitFacade sabmB2bUnitFacade;

	@Before
	public void setUp()
	{
		MockitoAnnotations.initMocks(this);

		contactAddress = new AddressModel();
		final RegionModel region = new RegionModel();
		contactAddress.setTown("Melbourne");
		contactAddress.setPostalcode("3000");
		region.setIsocode("VIC");
		contactAddress.setRegion(region);

		zadpUnit = new B2BUnitModel();
		zadpUnit.setAccountGroup("ZADP");
		zadpUnit.setUid("Austrilia");
		zadpUnit.setActive(true);
		final Set<PrincipalModel> zadpMembers = new HashSet<PrincipalModel>();
		zadpMembers.add(zadpUser);
		zadpMembers.add(zalbUnit);
		zadpUnit.setMembers(zadpMembers);

		zadpUnitData = new B2BUnitData();

		zalbUnit = new B2BUnitModel();
		zalbUnit.setAccountGroup("ZALB");
		zalbUnit.setUid("Melbourne");
		zalbUnit.setName("Melbourne");
		zalbUnit.setContactAddress(contactAddress);
		zalbUnit.setActive(true);
		final Set<PrincipalModel> zalbMembers = new HashSet<PrincipalModel>();
		zalbMembers.add(zalbUser);
		zalbUnit.setMembers(zalbMembers);

		zadpUser = new B2BCustomerModel();
		zadpUser.setName("Boss");
		zadpUser.setUid("boss");
		final Set<PrincipalGroupModel> zadpUserGroups = new HashSet<PrincipalGroupModel>();
		zadpUserGroups.add(zadpUnit);
		zadpUser.setGroups(zadpUserGroups);
		zadpUser.setActive(true);

		zadpUserData = new CustomerData();
		zadpUserData.setUid(zadpUser.getUid());

		zalbUser = new B2BCustomerModel();
		zalbUser.setName("Employee");
		zalbUser.setUid("employee");
		final Set<PrincipalGroupModel> zalbUserGroups = new HashSet<PrincipalGroupModel>();
		zalbUserGroups.add(zalbUnit);
		zalbUser.setGroups(zalbUserGroups);
		zalbUser.setActive(true);

		zalbUserData = new CustomerData();
		zalbUserData.setUid(zalbUser.getUid());

		zalbUnitData = new B2BUnitData();
		final Collection<CustomerData> customers = new ArrayList<CustomerData>();
		customers.add(zalbUserData);
		zalbUnitData.setCustomers(customers);

		noneZadpUsers = new ArrayList<B2BCustomerModel>();
		noneZadpUsers.add(zalbUser);
	}

	@Test
	public void testGetNoneZADPUsers()
	{
		// Test login use zadp user
		when(b2bCommerceUnitService.getRootUnit()).thenReturn(zadpUnit);
		when(userService.getCurrentUser()).thenReturn(zadpUser);
		when(b2bUnitService.getNoneZADPUsersWithSpecifiedBusinessUnit(zadpUnit, zadpUser)).thenReturn(noneZadpUsers);
		when(b2BCustomerConverter.convert(zadpUser)).thenReturn(zadpUserData);
		when(b2BCustomerConverter.convert(zalbUser)).thenReturn(zalbUserData);
		assertNotNull(sabmB2bUnitFacade.getNoneZADPUsers());
		assertEquals(sabmB2bUnitFacade.getNoneZADPUsers().get(0).getUid(), zalbUser.getUid());

		final Collection<CustomerData> customers = new ArrayList<CustomerData>();
		customers.add(zalbUserData);
		final B2BUnitData unit = new B2BUnitData();
		unit.setCustomers(customers);

		// Test login use zalb user
		final List<B2BCustomerModel> customerExceptZadpUsers = new ArrayList<B2BCustomerModel>();
		customerExceptZadpUsers.add(zalbUser);

		when(userService.getCurrentUser()).thenReturn(zalbUser);
		when(b2bUnitService.getUnitForUid(zalbUnit.getUid())).thenReturn(zalbUnit);
		when(b2bUnitConverter.convert(zalbUnit)).thenReturn(zalbUnitData);
		when(b2bUnitService.getCustmoersExceptZADP(zalbUnit)).thenReturn(customerExceptZadpUsers);
		when(b2BCustomerConverter.convert(zalbUser)).thenReturn(zalbUserData);

		assertNotNull(sabmB2bUnitFacade.getNoneZADPUsers());
		assertEquals(sabmB2bUnitFacade.getNoneZADPUsers().get(0).getUid(), zalbUser.getUid());
	}

	@Test
	@SuppressWarnings(value =
	{ "rawtypes", "unchecked" })
	public void testGetEntireB2bUnits()
	{
		final Collection organization = new HashSet();
		organization.add(zalbUnit);
		zadpUser = new B2BCustomerModel();
		B2BUnitModel zadpUserGroup = new B2BUnitModel();
		zadpUserGroup.setAccountGroup(SabmCoreConstants.ZADP);
		zadpUser.setGroups(new HashSet<PrincipalGroupModel>(Arrays.asList(zadpUserGroup)));

		when(b2bCommerceUnitService.getOrganization()).thenReturn(organization);
		when(b2bUnitConverter.convert(zadpUnit)).thenReturn(zadpUnitData);
		when(b2bUnitConverter.convert(zalbUnit)).thenReturn(zalbUnitData);
		when(userService.getCurrentUser()).thenReturn(zadpUser);

		assertNotNull(sabmB2bUnitFacade.getEntireB2bUnits());
	}

	@Test
	public void testGetRootB2bUnit()
	{
		final B2BUnitModel rootUnit = new B2BUnitModel();
		rootUnit.setAccountGroup("ZADP");
		rootUnit.setUid("Austrilia");
		final Set<PrincipalModel> zadpMembers = new HashSet<PrincipalModel>();
		rootUnit.setMembers(zadpMembers);

		final B2BUnitData returnValue = new B2BUnitData();

		when(b2bCommerceUnitService.getRootUnit()).thenReturn(rootUnit);
		when(b2bUnitConverter.convert(rootUnit)).thenReturn(returnValue);

		assertNotNull(sabmB2bUnitFacade.getRootB2bUnit());
		assertEquals(sabmB2bUnitFacade.getRootB2bUnit(), returnValue);
	}
}
