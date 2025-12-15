/**
 *
 */
package com.sabmiller.core.b2b.services;

import static org.mockito.BDDMockito.given;

import de.hybris.bootstrap.annotations.UnitTest;
import de.hybris.platform.b2b.model.B2BCustomerModel;
import de.hybris.platform.b2b.model.B2BUnitModel;
import de.hybris.platform.core.model.security.PrincipalGroupModel;
import de.hybris.platform.core.model.security.PrincipalModel;
import de.hybris.platform.core.model.user.AddressModel;
import de.hybris.platform.servicelayer.model.ModelService;
import de.hybris.platform.servicelayer.user.UserService;

import java.util.Collection;
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

import com.google.common.collect.Lists;
import com.sabmiller.core.enums.B2BUnitStatus;


/**
 * DefaultSabmB2BUnitServiceUnitTest
 */
@UnitTest
public class DefaultSabmB2BUnitServiceUnitTest
{

	@InjectMocks
	private final DefaultSabmB2BUnitService b2bUnitService = new DefaultSabmB2BUnitService();

	@Mock
	private UserService userService;
	@Mock
	protected ModelService modelService;

	@Before
	public void setUp()
	{
		MockitoAnnotations.initMocks(this);
		//b2bUnitService.setModelService(modelService);
	}


	@Test
	public void testFindCustomerTopLevelUnit()
	{
		final B2BUnitModel prinmodel2 = Mockito.mock(B2BUnitModel.class);
		given(prinmodel2.getUid()).willReturn("testUid1");
		given(prinmodel2.getAccountGroup()).willReturn("ZADP");


		final B2BUnitModel prinmodel = Mockito.mock(B2BUnitModel.class);
		given(prinmodel.getUid()).willReturn("testUid2");
		given(prinmodel.getAccountGroup()).willReturn("ZALB");

		final Set<PrincipalGroupModel> groups = new HashSet<PrincipalGroupModel>();
		groups.add(prinmodel);

		final Set<PrincipalGroupModel> groups2 = new HashSet<PrincipalGroupModel>();
		groups2.add(prinmodel2);

		final B2BCustomerModel b2bCustomer = Mockito.mock(B2BCustomerModel.class);
		given(b2bCustomer.getGroups()).willReturn(groups);
		given(prinmodel.getGroups()).willReturn(groups2);

		final List<B2BUnitModel> model = b2bUnitService.findCustomerTopLevelUnit(b2bCustomer);
		Assert.assertEquals(1, model.size());
		Assert.assertEquals("testUid1", model.get(0).getUid());

		final B2BCustomerModel b2bCustomer2 = Mockito.mock(B2BCustomerModel.class);
		given(b2bCustomer2.getGroups()).willReturn(groups2);

		final List<B2BUnitModel> model2 = b2bUnitService.findCustomerTopLevelUnit(b2bCustomer2);
		Assert.assertEquals(1, model2.size());
		Assert.assertEquals("testUid1", model2.get(0).getUid());
	}


	@Test
	public void testGetContactAddressFormB2BUnit()
	{
		final B2BUnitModel b2bUnit = new B2BUnitModel();
		final AddressModel contactAddress = new AddressModel();
		b2bUnit.setContactAddress(contactAddress);
		final AddressModel testContactAddress = b2bUnitService.getContactAddressFormB2BUnit(b2bUnit);
		Assert.assertEquals(contactAddress, testContactAddress);

		contactAddress.setContactAddress(Boolean.TRUE);
		final Collection<AddressModel> addresses = Lists.newArrayList();
		addresses.add(contactAddress);
		b2bUnit.setContactAddress(null);
		b2bUnit.setAddresses(addresses);
		final AddressModel testContactAddress2 = b2bUnitService.getContactAddressFormB2BUnit(b2bUnit);
		Assert.assertEquals(contactAddress, testContactAddress2);

		final B2BUnitModel mockB2BUnit = Mockito.mock(B2BUnitModel.class);
		given(mockB2BUnit.getContactAddress()).willReturn(null);
		given(mockB2BUnit.getAddresses()).willReturn(null);
		final AddressModel testContactAddress3 = b2bUnitService.getContactAddressFormB2BUnit(mockB2BUnit);
		Assert.assertEquals(null, testContactAddress3);
	}

	@Test
	public void testGetPrimaryAdminStatus()
	{
		//active
		final B2BUnitModel mockB2BUnit = Mockito.mock(B2BUnitModel.class);
		given(mockB2BUnit.getUid()).willReturn("10009");
		final B2BCustomerModel principal1 = Mockito.mock(B2BCustomerModel.class);
		given(principal1.getPrimaryAdmin()).willReturn(Boolean.FALSE);
		final B2BCustomerModel principal2 = Mockito.mock(B2BCustomerModel.class);
		given(principal2.getPrimaryAdmin()).willReturn(Boolean.FALSE);
		final B2BCustomerModel principal3 = Mockito.mock(B2BCustomerModel.class);
		given(principal3.getPrimaryAdmin()).willReturn(Boolean.TRUE);
		given(principal3.getActive()).willReturn(Boolean.TRUE);
		given(principal3.getEncodedPassword()).willReturn("password");

		final Set<PrincipalModel> member = new HashSet<PrincipalModel>();
		member.add(principal1);
		member.add(principal2);
		member.add(principal3);
		given(mockB2BUnit.getMembers()).willReturn(member);
		given(b2bUnitService.getUnitForUid("10009")).willReturn(mockB2BUnit);


		//invited
		final B2BUnitModel mockB2BUnit1 = Mockito.mock(B2BUnitModel.class);
		given(mockB2BUnit1.getUid()).willReturn("10010");
		final B2BCustomerModel principal5 = Mockito.mock(B2BCustomerModel.class);
		given(principal5.getPrimaryAdmin()).willReturn(Boolean.FALSE);
		final B2BCustomerModel principal6 = Mockito.mock(B2BCustomerModel.class);
		given(principal6.getPrimaryAdmin()).willReturn(Boolean.FALSE);
		final B2BCustomerModel principal7 = Mockito.mock(B2BCustomerModel.class);
		given(principal7.getPrimaryAdmin()).willReturn(Boolean.TRUE);
		given(principal7.getActive()).willReturn(Boolean.TRUE);
		given(principal7.getEncodedPassword()).willReturn("");

		final Set<PrincipalModel> member1 = new HashSet<PrincipalModel>();
		member1.add(principal5);
		member1.add(principal6);
		member1.add(principal7);
		given(mockB2BUnit1.getMembers()).willReturn(member1);
		given(b2bUnitService.getUnitForUid("10010")).willReturn(mockB2BUnit1);

		//inactive
		final B2BUnitModel mockB2BUnit2 = Mockito.mock(B2BUnitModel.class);
		given(mockB2BUnit1.getUid()).willReturn("10011");
		final B2BCustomerModel principal8 = Mockito.mock(B2BCustomerModel.class);
		given(principal8.getPrimaryAdmin()).willReturn(Boolean.FALSE);
		final B2BCustomerModel principal9 = Mockito.mock(B2BCustomerModel.class);
		given(principal9.getPrimaryAdmin()).willReturn(Boolean.FALSE);


		final Set<PrincipalModel> member2 = new HashSet<PrincipalModel>();
		member1.add(principal8);
		member1.add(principal9);

		given(mockB2BUnit2.getMembers()).willReturn(member2);
		given(b2bUnitService.getUnitForUid("10011")).willReturn(mockB2BUnit2);



		final B2BUnitModel mockB2BUnit3 = Mockito.mock(B2BUnitModel.class);
		given(mockB2BUnit3.getUid()).willReturn("10012");
		final B2BCustomerModel principal10 = Mockito.mock(B2BCustomerModel.class);
		given(principal10.getPrimaryAdmin()).willReturn(Boolean.TRUE);
		given(principal10.getActive()).willReturn(Boolean.FALSE);
		given(principal10.getEncodedPassword()).willReturn("password");

		final Set<PrincipalModel> member3 = new HashSet<PrincipalModel>();
		member3.add(principal1);
		member3.add(principal2);
		member3.add(principal10);
		given(mockB2BUnit3.getMembers()).willReturn(member3);
		given(b2bUnitService.getUnitForUid("10012")).willReturn(mockB2BUnit3);



		Assert.assertEquals("active", b2bUnitService.findPrimaryAdminStatus("10009"));

		Assert.assertEquals("invited", b2bUnitService.findPrimaryAdminStatus("10010"));

		Assert.assertEquals("inactive", b2bUnitService.findPrimaryAdminStatus("10011"));

		Assert.assertEquals("inactive", b2bUnitService.findPrimaryAdminStatus("10012"));

		Assert.assertEquals("inactive", b2bUnitService.findPrimaryAdminStatus(""));
	}


	@Test
	public void testB2BUnitStatus()
	{
		//active
		final B2BUnitModel mockB2BUnit = Mockito.mock(B2BUnitModel.class);
		given(mockB2BUnit.getUid()).willReturn("10009");
		final B2BCustomerModel principal1 = Mockito.mock(B2BCustomerModel.class);
		final B2BCustomerModel principal2 = Mockito.mock(B2BCustomerModel.class);
		final B2BCustomerModel principal3 = Mockito.mock(B2BCustomerModel.class);
		given(principal3.getActive()).willReturn(Boolean.TRUE);
		given(principal3.getEncodedPassword()).willReturn("password");

		final Set<PrincipalModel> member = new HashSet<PrincipalModel>();
		member.add(principal1);
		member.add(principal2);
		member.add(principal3);
		given(mockB2BUnit.getMembers()).willReturn(member);
		given(b2bUnitService.getUnitForUid("10009")).willReturn(mockB2BUnit);


		//invited
		final B2BUnitModel mockB2BUnit1 = Mockito.mock(B2BUnitModel.class);
		given(mockB2BUnit1.getUid()).willReturn("10010");
		final B2BCustomerModel principal5 = Mockito.mock(B2BCustomerModel.class);
		final B2BCustomerModel principal6 = Mockito.mock(B2BCustomerModel.class);
		final B2BCustomerModel principal7 = Mockito.mock(B2BCustomerModel.class);
		given(principal7.getActive()).willReturn(Boolean.TRUE);
		given(principal7.getEncodedPassword()).willReturn("");

		final Set<PrincipalModel> member1 = new HashSet<PrincipalModel>();
		member1.add(principal5);
		member1.add(principal6);
		member1.add(principal7);
		given(mockB2BUnit1.getMembers()).willReturn(member1);
		given(b2bUnitService.getUnitForUid("10010")).willReturn(mockB2BUnit1);

		//inactive
		final B2BUnitModel mockB2BUnit2 = Mockito.mock(B2BUnitModel.class);
		given(mockB2BUnit2.getB2BUnitStatus()).willReturn(B2BUnitStatus.INVITED);
		given(mockB2BUnit1.getUid()).willReturn("10011");
		final B2BCustomerModel principal8 = Mockito.mock(B2BCustomerModel.class);

		final B2BCustomerModel principal9 = Mockito.mock(B2BCustomerModel.class);



		final Set<PrincipalModel> member2 = new HashSet<PrincipalModel>();
		member1.add(principal8);
		member1.add(principal9);

		given(mockB2BUnit2.getMembers()).willReturn(member2);
		given(b2bUnitService.getUnitForUid("10011")).willReturn(mockB2BUnit2);
		Assert.assertEquals("invited", b2bUnitService.findB2BUnitStatus("10011"));


		final B2BUnitModel mockB2BUnit3 = Mockito.mock(B2BUnitModel.class);
		given(mockB2BUnit3.getB2BUnitStatus()).willReturn(B2BUnitStatus.ACTIVE);
		given(mockB2BUnit3.getUid()).willReturn("10012");
		final B2BCustomerModel principal10 = Mockito.mock(B2BCustomerModel.class);
		given(principal10.getActive()).willReturn(Boolean.TRUE);
		given(principal10.getEncodedPassword()).willReturn("");
		given(principal10.getWelcomeEmailStatus()).willReturn(Boolean.TRUE);
		final Set<PrincipalModel> member3 = new HashSet<PrincipalModel>();
		member3.add(principal1);
		member3.add(principal2);
		member3.add(principal10);
		given(mockB2BUnit3.getMembers()).willReturn(member3);
		given(b2bUnitService.getUnitForUid("10012")).willReturn(mockB2BUnit3);

		Assert.assertEquals("active", b2bUnitService.findB2BUnitStatus("10012"));
	}

}
