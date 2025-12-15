/**
 *
 */
package com.sabmiller.core.customer.service.impl;

import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

import de.hybris.bootstrap.annotations.UnitTest;
import de.hybris.platform.b2b.model.B2BUnitModel;
import de.hybris.platform.servicelayer.config.ConfigurationService;
import de.hybris.platform.servicelayer.session.SessionService;
import de.hybris.platform.servicelayer.user.UserService;

import org.apache.commons.configuration2.Configuration;
import org.junit.Before;
import org.junit.Test;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.MockitoAnnotations;
import org.mockito.InjectMocks;

import com.sabmiller.core.b2b.services.SabmB2BUnitService;
import de.hybris.platform.b2b.company.B2BCommerceUnitService;

/**
 * @author john.dale.f.menoso
 *
 */
@UnitTest
public class SABMUserFlagServiceTest
{
	private static final String B2BUnit_HYBRI = "HYBRI";
	private static final String B2BUnit_ONLCA = "ONLCA";
	private static final String B2BUnit_NULL = null;

	@Mock
	private UserService userService;

	@Mock
	private B2BUnitModel b2bUnit;

	@Mock
	private ConfigurationService configurationService;

	@Mock
	private Configuration config;

	@InjectMocks
	private SABMUserFlagServiceImpl userFlagService = new SABMUserFlagServiceImpl();

	@Mock
	private SabmB2BUnitService b2bUnitService;

	@Mock
	private SessionService sessionService;

	@Mock
	private B2BCommerceUnitService b2bCommerceUnitService;

	@Before
	public void setUp() throws Exception
	{
		MockitoAnnotations.initMocks(this);


		/*userFlagService.setB2bUnitService(b2bUnitService);*/
	}


	@Test
	public void testB2BUnitNullFlagHYBRI()
	{
		Mockito.when(configurationService.getConfiguration()).thenReturn(config);
		Mockito.when(sessionService.getAttribute(Mockito.anyString())).thenReturn(null);

		Mockito.when(config.getString(Mockito.anyString(), Mockito.anyString())).thenReturn(B2BUnit_ONLCA);
		Mockito.when(b2bUnit.getCustomerFlag()).thenReturn(B2BUnit_HYBRI);
		final Boolean result = userFlagService.isCashOnlyCustomer();
		assertFalse(result);
	}

	@Test
	public void testB2BUnitFlagONLCA()
	{
		Mockito.when(configurationService.getConfiguration()).thenReturn(config);

		b2bUnit = Mockito.mock(B2BUnitModel.class);
		b2bUnit.setPayerId("12345");
		final B2BUnitModel parentb2bUnit = Mockito.mock(B2BUnitModel.class);
		Mockito.when(sessionService.getAttribute(Mockito.anyString())).thenReturn(b2bUnit);

		Mockito.when(b2bUnitService.findTopLevelB2BUnit(b2bUnit.getPayerId())).thenReturn(parentb2bUnit);
		Mockito.when(config.getString(Mockito.anyString(), Mockito.anyString())).thenReturn(B2BUnit_ONLCA);
		Mockito.when(b2bUnit.getCustomerFlag()).thenReturn(B2BUnit_ONLCA);

		final Boolean result = userFlagService.isCashOnlyCustomer();
		assertFalse(result);
	}

	@Test
	public void testParentB2BUnitFlagONLCA()
	{
		Mockito.when(configurationService.getConfiguration()).thenReturn(config);

		b2bUnit = Mockito.mock(B2BUnitModel.class);
		b2bUnit.setPayerId("12345");
		final B2BUnitModel parentb2bUnit = Mockito.mock(B2BUnitModel.class);
		Mockito.when(b2bUnit.getPayerId()).thenReturn("12345");
		Mockito.when(sessionService.getAttribute(Mockito.anyString())).thenReturn(b2bUnit);
		Mockito.when(b2bUnitService.findTopLevelB2BUnit(b2bUnit.getPayerId())).thenReturn(parentb2bUnit);
		Mockito.when(config.getString(Mockito.anyString(), Mockito.anyString())).thenReturn(B2BUnit_ONLCA);
		Mockito.when(parentb2bUnit.getCustomerFlag()).thenReturn(B2BUnit_ONLCA);

		final Boolean result = userFlagService.isCashOnlyCustomer();
		assertFalse(result);
	}

	@Test
	public void testB2BCustomerFlagEmpty()
	{
		Mockito.when(configurationService.getConfiguration()).thenReturn(config);
		Mockito.when(config.getString(Mockito.anyString(), Mockito.anyString())).thenReturn(B2BUnit_ONLCA);
		Mockito.when(b2bUnit.getCustomerFlag()).thenReturn(B2BUnit_NULL);
		final Boolean result = userFlagService.isCashOnlyCustomer();
		assertFalse(result);
	}

}
