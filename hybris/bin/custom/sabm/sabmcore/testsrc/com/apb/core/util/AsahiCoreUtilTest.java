/**
 *
 */
package com.apb.core.util;

import static org.junit.Assert.assertEquals;
import static org.mockito.Mockito.when;

import de.hybris.bootstrap.annotations.UnitTest;
import de.hybris.platform.core.model.security.PrincipalGroupModel;
import de.hybris.platform.core.model.user.UserModel;
import de.hybris.platform.servicelayer.session.SessionService;
import de.hybris.platform.servicelayer.user.UserService;

import java.util.Collections;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.Spy;
import org.mockito.junit.MockitoJUnitRunner;

import com.apb.core.service.config.AsahiConfigurationService;
import com.apb.core.services.ApbCustomerAccountService;


/**
 * @author EG588BU
 *
 */
@UnitTest
@RunWith(MockitoJUnitRunner.class)
public class AsahiCoreUtilTest
{
	@Spy
	@InjectMocks
	private final AsahiCoreUtil asahiCoreUtil = new AsahiCoreUtil();

	@Mock
	private AsahiSiteUtil asahiSiteUtil;

	@Mock
	private UserModel userModel;

	@Mock
	private UserService userService;

	@Mock
	private PrincipalGroupModel userGroup;

	@Mock
	private AsahiConfigurationService asahiConfigurationService;

	@Mock
	private SessionService sessionService;

	@Mock
	private ApbCustomerAccountService customerAccountService;

	@Test
	public void notNAPUserForSiteTest()
	{
		when(asahiSiteUtil.isSga()).thenReturn(false);
		when(asahiSiteUtil.isCub()).thenReturn(false);
		assertEquals(false, asahiCoreUtil.isNAPUserForSite());
	}

	@Test
	public void isNAPUserForSiteTest()
	{
		when(asahiSiteUtil.isSga()).thenReturn(true);
		when(userService.getCurrentUser()).thenReturn(userModel);
		when(userModel.getGroups()).thenReturn(Collections.singleton(userGroup));
		when(userGroup.getUid()).thenReturn("b2bNAPGroup");
		assertEquals(true, asahiCoreUtil.isNAPUserForSite());
	}

	@Test
	public void isNotNAPUser()
	{
		when(userService.getCurrentUser()).thenReturn(userModel);
		when(userModel.getGroups()).thenReturn(Collections.singleton(userGroup));
		when(userGroup.getUid()).thenReturn("groupID");
		assertEquals(false, asahiCoreUtil.isNAPUser());
	}

	@Test
	public void isCloseToCreditBlockFalseTest()
	{
		when(asahiConfigurationService.getInt("credit.close.block.start.percentage.sga", 80)).thenReturn(80);
		assertEquals(false, asahiCoreUtil.isCloseToCreditBlock(60.0));
	}

	@Test
	public void isCloseToCreditBlockTrueTest()
	{
		when(asahiConfigurationService.getInt("credit.close.block.start.percentage.sga", 80)).thenReturn(80);
		assertEquals(true, asahiCoreUtil.isCloseToCreditBlock(90.0));
	}

	@Test
	public void setCreditInfoInSessionTest()
	{
		when(asahiConfigurationService.getInt("onaccount.disabled.percentage.sga", 100)).thenReturn(100);
		asahiCoreUtil.setCreditInfoInSession(100.0, 100.0, 101.0, true);
		Mockito.verify(sessionService).setAttribute("isCloseToCreditBlock", true);
		Mockito.verify(sessionService).setAttribute("isOnAccountDisabled", true);
		Mockito.verify(sessionService).setAttribute("deltaToLimit", 100.0);
		Mockito.verify(sessionService).setAttribute("creditLimit", 100.0);

	}

	@Test
	public void removeCreditInfoInSessionTest()
	{
		asahiCoreUtil.removeCreditInfoInSession();
		Mockito.verify(sessionService).removeAttribute("isCloseToCreditBlock");
		Mockito.verify(sessionService).removeAttribute("isOnAccountDisabled");
		Mockito.verify(sessionService).removeAttribute("deltaToLimit");
		Mockito.verify(sessionService).removeAttribute("creditLimit");
	}

	@Test
	public void testCheckIfUserExists()
	{
		Mockito.when(userModel.getUid()).thenReturn("testuser@test.com");
		Mockito.when(customerAccountService.getUserByUid(Mockito.anyString())).thenReturn(userModel);
		final UserModel user = asahiCoreUtil.checkIfUserExists("testuser@test.com");
		assertEquals("testuser@test.com", user.getUid());
	}

}
