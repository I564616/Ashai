/**
 *
 */
package com.apb.facades.impl;

import static org.mockito.Mockito.times;

import de.hybris.bootstrap.annotations.UnitTest;
import de.hybris.platform.b2b.model.B2BCustomerModel;
import de.hybris.platform.servicelayer.dto.converter.Converter;
import de.hybris.platform.servicelayer.user.UserService;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Calendar;
import java.util.List;

import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.MockitoAnnotations;

import com.apb.core.services.ApbCustomerAccountService;
import com.apb.facades.user.impl.ApbUserFacadeImpl;
import com.apb.service.b2bunit.ApbB2BUnitService;
import com.asahi.facades.planograms.PlanogramData;
import com.sabmiller.core.model.AsahiB2BUnitModel;
import com.sabmiller.core.model.PlanogramModel;
import com.sabmiller.core.order.dao.DefaultSabmOrderDao;



@UnitTest
public class ApbUserFacadeImplTest
{

	@InjectMocks
	private final ApbUserFacadeImpl userFacade = new ApbUserFacadeImpl();

	@Mock
	private UserService userService;

	@Mock
	DefaultSabmOrderDao orderDao;

	@Mock
	private ApbB2BUnitService apbB2BUnitService;

	@Mock
	private ApbCustomerAccountService apbCustomerAccountService;

	@Mock
	private Converter<PlanogramModel, PlanogramData> planogramConverter;


	@Before
	public void setUp()
	{
		MockitoAnnotations.initMocks(this);
		userFacade.setUserService(userService);
		userFacade.setPlanogramConverter(planogramConverter);

	}

	@Test
	public void testIsUserEligibleToReceiveWelcomeEmail1()
	{

		final B2BCustomerModel mockUser = Mockito.mock(B2BCustomerModel.class);
		final AsahiB2BUnitModel mockb2bUnit = Mockito.mock(AsahiB2BUnitModel.class);
		Mockito.when(userService.getUserForUID("test@test.com")).thenReturn(mockUser);
		Mockito.when(mockUser.getDefaultB2BUnit()).thenReturn(mockb2bUnit);
		Mockito.when(orderDao.fetchOnlineOrderCountBasedOnUserB2BUnitAndSite(mockUser, "sga	", mockb2bUnit)).thenReturn(0);
		final boolean result = userFacade.isUserEligibleToReceiveWelcomeEmail("test@test.com", "sga");
		Assert.assertEquals(Boolean.TRUE, result);
	}


	@Test
	public void testIsUserEligibleToReceiveWelcomeEmail2()
	{
		final B2BCustomerModel user1 = new B2BCustomerModel();
		final AsahiB2BUnitModel mockb2bUnit = Mockito.mock(AsahiB2BUnitModel.class);
		user1.setUid("test1@test.com");
		Mockito.when(userService.getUserForUID("test1@test.com")).thenReturn(user1);
		user1.setDefaultB2BUnit(mockb2bUnit);
		Mockito.when(orderDao.fetchOnlineOrderCountBasedOnUserB2BUnitAndSite(user1, "sga	", mockb2bUnit)).thenReturn(1);
		final Calendar calendar = Calendar.getInstance();
		calendar.add(Calendar.DATE, -40);
		user1.setLastLogin(calendar.getTime());
		final boolean result = userFacade.isUserEligibleToReceiveWelcomeEmail("test1@test.com", "sga");
		Assert.assertEquals(Boolean.TRUE, result);
	}

	@Test
	public void testIsUserEligibleToReceiveWelcomeEmail3()
	{
		final B2BCustomerModel user2 = new B2BCustomerModel();
		final AsahiB2BUnitModel mockb2bUnit = Mockito.mock(AsahiB2BUnitModel.class);
		user2.setUid("test2@test.com");
		Mockito.when(userService.getUserForUID("test2@test.com")).thenReturn(user2);
		user2.setDefaultB2BUnit(mockb2bUnit);
		Mockito.when(orderDao.fetchOnlineOrderCountBasedOnUserB2BUnitAndSite(Mockito.any(), Mockito.any(),
				Mockito.any())).thenReturn(1);
		final Calendar calendar = Calendar.getInstance();
		calendar.add(Calendar.DATE, -20);
		user2.setLastLogin(calendar.getTime());
		final boolean result = userFacade.isUserEligibleToReceiveWelcomeEmail("test2@test.com", "sga");
		Assert.assertEquals(Boolean.FALSE, result);
	}

	@Test
	public void testSendWelcomeEmail()
	{
		final B2BCustomerModel mockUser = Mockito.mock(B2BCustomerModel.class);
		Mockito.when(userService.getUserForUID("test@test.com")).thenReturn(mockUser);
		Mockito.when(apbCustomerAccountService.sendWelcomeEmail(mockUser)).thenReturn(true);
		final boolean result = userFacade.sendWelcomeEmail("test@test.com");
		Assert.assertEquals(Boolean.TRUE, result);
	}

	@Test
	public void testRemoveCustomerFromB2bUnit()
	{
		Mockito.when(apbCustomerAccountService.removeCustomerFromUnit(Mockito.any(), Mockito.any()))
				.thenReturn(Boolean.TRUE);
		final boolean result = userFacade.removeCustomerFromB2bUnit("test@test.com", new AsahiB2BUnitModel());
		Assert.assertEquals(Boolean.TRUE, result);
	}

	@Test
	public void testGetPlanogramsForB2BUnit()
	{
		final AsahiB2BUnitModel unit = Mockito.mock(AsahiB2BUnitModel.class);
		final PlanogramModel planogramModel = Mockito.mock(PlanogramModel.class);
		Mockito.when(unit.getPlanograms()).thenReturn(Arrays.asList(planogramModel));
		Mockito.when(apbB2BUnitService.getCurrentB2BUnit()).thenReturn(unit);
		final List<PlanogramData> planogramData = new ArrayList<PlanogramData>();
		final PlanogramData planogram = new PlanogramData();
		planogram.setCode("pcode");
		planogramData.add(planogram);

		final List<PlanogramData> data = userFacade.getPlanogramsForB2BUnit();
		Assert.assertNotNull(data);
		Assert.assertEquals(String.valueOf(Integer.valueOf(1)), String.valueOf(data.size()));

	}

	@Test
	public void testGetDefaultPlanogram()
	{
		final List<PlanogramData> planogramData = new ArrayList<PlanogramData>();
		final PlanogramData planogram = new PlanogramData();
		planogram.setCode("pcode");
		planogramData.add(planogram);
		final AsahiB2BUnitModel unit = Mockito.mock(AsahiB2BUnitModel.class);
		final PlanogramModel planogramModel = Mockito.mock(PlanogramModel.class);
		final List<PlanogramModel> planograms = Arrays.asList(planogramModel);
		Mockito.when(apbCustomerAccountService.getDefaultPlanograms(Mockito.any())).thenReturn(planograms);
		final List<String> catalogHierarchy = Arrays.asList("00009686565");
		Mockito.when(apbB2BUnitService.getCurrentB2BUnit()).thenReturn(unit);
		Mockito.when(unit.getCatalogHierarchy()).thenReturn(catalogHierarchy);

		final List<PlanogramData> data = userFacade.getDefaultPlanogram();
		Assert.assertNotNull(data);
		Assert.assertEquals(String.valueOf(Integer.valueOf(1)), String.valueOf(data.size()));

	}

	@Test
	public void testSavePlanogram()
	{
		Mockito.when(apbCustomerAccountService.savePlanogram(Mockito.any())).thenReturn(Boolean.TRUE);
		final boolean result = userFacade.savePlanogram(new PlanogramData());
		Assert.assertEquals(Boolean.TRUE, result);
	}

	@Test
	public void testRemovePlanogram()
	{
		userFacade.removePlanogram("pcode");
		Mockito.verify(apbCustomerAccountService, times(1)).removePlanogram(Mockito.any());
	}

	@Test
	public void testRemoveAllPlanograms()
	{
		userFacade.removeAllPlanograms();
		Mockito.verify(apbCustomerAccountService, times(1)).removeAllPlanogramsForCurrentB2BUnit();
	}
}
