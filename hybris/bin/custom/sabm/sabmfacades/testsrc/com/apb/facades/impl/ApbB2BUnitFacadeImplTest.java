/**
 *
 */
package com.apb.facades.impl;

import static org.junit.Assert.assertTrue;
import static org.mockito.Mockito.when;

import de.hybris.bootstrap.annotations.UnitTest;
import de.hybris.platform.b2b.model.B2BUnitModel;
import de.hybris.platform.b2bcommercefacades.company.data.B2BUnitData;
import de.hybris.platform.core.model.security.PrincipalGroupModel;
import de.hybris.platform.core.model.user.CustomerModel;
import de.hybris.platform.servicelayer.dto.converter.Converter;
import de.hybris.platform.servicelayer.user.UserService;

import java.util.HashSet;
import java.util.Set;

import org.junit.Before;
import org.junit.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.MockitoAnnotations;

import com.apb.facades.b2bunit.impl.ApbB2BUnitFacadeImpl;
import com.sabmiller.core.model.AsahiB2BUnitModel;

/***
 * 
 * @author Ranjith.Karuvachery
 *
 */


@UnitTest
public class ApbB2BUnitFacadeImplTest
{

	@InjectMocks
	private ApbB2BUnitFacadeImpl apbB2BUnitFacadeImpl;

	@Mock
	private UserService userService;

	@Mock
	private Converter<B2BUnitModel, B2BUnitData> b2bUnitConverter;


	@Before
	public void setUp()
	{
		MockitoAnnotations.initMocks(this);
	}

	
	@Test
	public void testGetB2BUnitsByCustomer() {
		String userId = "abc@gmail.com";
		final CustomerModel customer = Mockito.mock(CustomerModel.class);
		final Set<PrincipalGroupModel> principalGroups = new HashSet<PrincipalGroupModel>();
		B2BUnitData b2bUnitData = Mockito.mock(B2BUnitData.class);
		AsahiB2BUnitModel asahiB2BUnitModel = Mockito.mock(AsahiB2BUnitModel.class);
		principalGroups.add(asahiB2BUnitModel);
		when(userService.getUserForUID(userId)).thenReturn(customer);
		when(asahiB2BUnitModel.getActive()).thenReturn(false);
		when(customer.getGroups()).thenReturn(principalGroups);
		when(b2bUnitConverter.convert(asahiB2BUnitModel)).thenReturn(b2bUnitData);
		
		assertTrue(apbB2BUnitFacadeImpl.getB2BUnitsByCustomer(userId).isEmpty());
	}
}
