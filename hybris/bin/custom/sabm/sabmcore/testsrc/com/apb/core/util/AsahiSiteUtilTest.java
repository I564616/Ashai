/**
 *
 */
package com.apb.core.util;

import static org.junit.Assert.assertEquals;
import static org.mockito.Mockito.when;

import de.hybris.bootstrap.annotations.UnitTest;
import de.hybris.platform.core.model.user.UserModel;
import de.hybris.platform.servicelayer.user.UserService;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Spy;
import org.mockito.junit.MockitoJUnitRunner;

import com.sabmiller.core.model.BDECustomerModel;


/**
 * @author EG588BU
 *
 */
@UnitTest
@RunWith(MockitoJUnitRunner.class)
public class AsahiSiteUtilTest
{

	@Spy
	@InjectMocks
	private final AsahiSiteUtil asahiSiteUtil = new AsahiSiteUtil();

	@Mock
	private UserService userService;

	@Mock
	private BDECustomerModel bdeCustomer;

	@Mock
	private UserModel user;

	@Test
	public void isBDECustomerTest()
	{
		when(userService.getCurrentUser()).thenReturn(bdeCustomer);
		assertEquals(true, asahiSiteUtil.isBDECustomer());
	}

	@Test
	public void isNotBDECustomerTest()
	{
		when(userService.getCurrentUser()).thenReturn(user);
		assertEquals(false, asahiSiteUtil.isBDECustomer());

	}
}
