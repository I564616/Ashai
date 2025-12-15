/**
 *
 */
package com.sabmiller.core.b2b.services;

import de.hybris.bootstrap.annotations.UnitTest;
import de.hybris.platform.b2b.model.B2BCustomerModel;
import de.hybris.platform.core.model.security.PrincipalGroupModel;
import de.hybris.platform.servicelayer.internal.dao.GenericDao;
import de.hybris.platform.servicelayer.model.ModelService;

import java.util.HashSet;
import java.util.Set;

import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.MockitoAnnotations;

import com.apb.core.util.AsahiSiteUtil;
import com.sabmiller.core.b2b.services.impl.DefaultSabmB2BCustomerServiceImpl;
import com.sabmiller.core.model.AsahiB2BUnitModel;
import com.sabmiller.core.model.SABMNotificationModel;


/**
 *
 */
@UnitTest
public class DefaultSabmB2BCustomerServiceImplTest
{

	@InjectMocks
	private final DefaultSabmB2BCustomerServiceImpl defaultSabmB2BCustomerServiceImpl = new DefaultSabmB2BCustomerServiceImpl();

	@Mock
	private ModelService modelService;

	@Mock
	private SabmB2BUnitService b2bUnitService;

	@Mock
	private GenericDao<SABMNotificationModel> sabmNotificationDao;

	@Mock
	private AsahiSiteUtil asahiSiteUtil;

	@Before
	public void setUp()
	{
		MockitoAnnotations.initMocks(this);
	}



	@Test
	public void testIsRegistrationAllowedSga()
	{
		Mockito.when(asahiSiteUtil.isSga()).thenReturn(Boolean.TRUE);
		Mockito.when(asahiSiteUtil.isApb()).thenReturn(Boolean.FALSE);
		Mockito.when(asahiSiteUtil.isCub()).thenReturn(Boolean.FALSE);
		final String newUid = "user@test.com";
		final B2BCustomerModel customer = new B2BCustomerModel();
		customer.setUid(newUid);
		final Set<PrincipalGroupModel> groups = new HashSet<PrincipalGroupModel>();
		final AsahiB2BUnitModel group1 = new AsahiB2BUnitModel();
		group1.setUid("apbgroup");
		group1.setCompanyCode("apb");
		groups.add(group1);
		customer.setGroups(groups);
		final boolean result = defaultSabmB2BCustomerServiceImpl.isRegistrationAllowed(customer, "sgagroup");
		Assert.assertEquals(Boolean.TRUE, result);
	}

	@Test
	public void testIsRegistrationAllowedApb()
	{
		Mockito.when(asahiSiteUtil.isSga()).thenReturn(Boolean.FALSE);
		Mockito.when(asahiSiteUtil.isApb()).thenReturn(Boolean.TRUE);
		Mockito.when(asahiSiteUtil.isCub()).thenReturn(Boolean.FALSE);
		final String newUid = "user@test.com";
		final B2BCustomerModel customer = new B2BCustomerModel();
		customer.setUid(newUid);
		final Set<PrincipalGroupModel> groups = new HashSet<PrincipalGroupModel>();
		final AsahiB2BUnitModel group1 = new AsahiB2BUnitModel();
		group1.setUid("sgagroup");
		group1.setCompanyCode("sga");
		groups.add(group1);
		customer.setGroups(groups);
		final boolean result = defaultSabmB2BCustomerServiceImpl.isRegistrationAllowed(customer, "apbgroup");
		Assert.assertEquals(Boolean.TRUE, result);
	}

	@Test
	public void testIsRegistrationAllowedCub()
	{
		Mockito.when(asahiSiteUtil.isSga()).thenReturn(Boolean.FALSE);
		Mockito.when(asahiSiteUtil.isApb()).thenReturn(Boolean.FALSE);
		Mockito.when(asahiSiteUtil.isCub()).thenReturn(Boolean.TRUE);
		final String newUid = "user@test.com";
		final B2BCustomerModel customer = new B2BCustomerModel();
		customer.setUid(newUid);
		final Set<PrincipalGroupModel> groups = new HashSet<PrincipalGroupModel>();
		final AsahiB2BUnitModel group1 = new AsahiB2BUnitModel();
		group1.setUid("apbgroup");
		group1.setCompanyCode("apb");
		groups.add(group1);
		customer.setGroups(groups);
		final boolean result = defaultSabmB2BCustomerServiceImpl.isRegistrationAllowed(customer, "apbgroup");
		Assert.assertEquals(Boolean.TRUE, result);
	}

	@Test
	public void testCheckIfUserRegisteredForOtherSitesSga()
	{
		Mockito.when(asahiSiteUtil.isSga()).thenReturn(Boolean.TRUE);
		Mockito.when(asahiSiteUtil.isApb()).thenReturn(Boolean.FALSE);
		Mockito.when(asahiSiteUtil.isCub()).thenReturn(Boolean.FALSE);
		final String newUid = "user@test.com";
		final B2BCustomerModel customer = new B2BCustomerModel();
		customer.setUid(newUid);
		final Set<PrincipalGroupModel> groups = new HashSet<PrincipalGroupModel>();
		final AsahiB2BUnitModel group1 = new AsahiB2BUnitModel();
		group1.setUid("apbgroup");
		group1.setCompanyUid("apb");
		groups.add(group1);
		customer.setGroups(groups);
		final boolean result = defaultSabmB2BCustomerServiceImpl.checkIfUserRegisteredForOtherSites(customer, Boolean.TRUE);
		Assert.assertEquals(Boolean.TRUE, result);
	}

	@Test
	public void testCheckIfUserRegisteredForOtherSitesApb()
	{
		Mockito.when(asahiSiteUtil.isSga()).thenReturn(Boolean.FALSE);
		Mockito.when(asahiSiteUtil.isApb()).thenReturn(Boolean.TRUE);
		Mockito.when(asahiSiteUtil.isCub()).thenReturn(Boolean.FALSE);
		final String newUid = "user@test.com";
		final B2BCustomerModel customer = new B2BCustomerModel();
		customer.setUid(newUid);
		final Set<PrincipalGroupModel> groups = new HashSet<PrincipalGroupModel>();
		final AsahiB2BUnitModel group1 = new AsahiB2BUnitModel();
		group1.setUid("sgagroup");
		group1.setCompanyUid("sga");
		groups.add(group1);
		customer.setGroups(groups);
		final boolean result = defaultSabmB2BCustomerServiceImpl.checkIfUserRegisteredForOtherSites(customer, Boolean.TRUE);
		Assert.assertEquals(Boolean.TRUE, result);
	}

	@Test
	public void testCheckIfUserRegisteredForOtherSitesCub()
	{
		Mockito.when(asahiSiteUtil.isSga()).thenReturn(Boolean.FALSE);
		Mockito.when(asahiSiteUtil.isApb()).thenReturn(Boolean.FALSE);
		Mockito.when(asahiSiteUtil.isCub()).thenReturn(Boolean.TRUE);
		final String newUid = "user@test.com";
		final B2BCustomerModel customer = new B2BCustomerModel();
		customer.setUid(newUid);
		final Set<PrincipalGroupModel> groups = new HashSet<PrincipalGroupModel>();
		final AsahiB2BUnitModel group1 = new AsahiB2BUnitModel();
		group1.setUid("apbgroup");
		group1.setCompanyUid("apb");
		groups.add(group1);
		customer.setGroups(groups);
		final boolean result = defaultSabmB2BCustomerServiceImpl.checkIfUserRegisteredForOtherSites(customer, Boolean.TRUE);
		Assert.assertEquals(Boolean.TRUE, result);
	}
}
