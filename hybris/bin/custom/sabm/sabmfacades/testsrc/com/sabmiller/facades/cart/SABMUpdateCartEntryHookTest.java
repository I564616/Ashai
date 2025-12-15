/**
 *
 */
package com.sabmiller.facades.cart;


import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.mock;

import de.hybris.bootstrap.annotations.UnitTest;
import de.hybris.platform.commerceservices.order.CommerceCartModification;
import de.hybris.platform.commerceservices.order.CommerceCartModificationException;
import de.hybris.platform.commerceservices.service.data.CommerceCartParameter;
import de.hybris.platform.core.model.order.CartEntryModel;
import de.hybris.platform.core.model.order.CartModel;
import de.hybris.platform.order.CartService;
import de.hybris.platform.servicelayer.model.ModelService;

import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import com.apb.core.util.AsahiSiteUtil;
import com.sabmiller.facades.order.hook.impl.SABMUpdateCartEntryHook;


/**
 * @author tom.minwen.wang
 *
 */

@UnitTest
public class SABMUpdateCartEntryHookTest
{
	@Mock
	ModelService modelService;
	@Mock
	CartService cartService;
	@Mock
	private AsahiSiteUtil asahiSiteUtil;

	@InjectMocks
	private SABMUpdateCartEntryHook sabmUpdateCartEntryHook;

	@Before
	public void setUp()
	{
		MockitoAnnotations.initMocks(this);
	}

	@Test
	public void testSabCommerceAddToCart() throws CommerceCartModificationException
	{
		final CartModel sessionCart = mock(CartModel.class);
		given(cartService.getSessionCart()).willReturn(sessionCart);
		given(sessionCart.getCalculated()).willReturn(Boolean.TRUE);
		modelService.refresh(sessionCart);
		modelService.save(sessionCart);
		Assert.assertEquals(Boolean.TRUE, sessionCart.getCalculated());
		Assert.assertNotNull("cart not null", sessionCart);
	}

	@Test
	public void testAfterUpdateCartEntry()
	{
		final CommerceCartParameter cartParameter = mock(CommerceCartParameter.class);
		final CommerceCartModification result = mock(CommerceCartModification.class);
		final CartModel sessionCart = mock(CartModel.class);
		final CartEntryModel cartEntry = new CartEntryModel();
		given(cartParameter.getCart()).willReturn(sessionCart);
		given(cartParameter.getEntryNumber()).willReturn(1L);
		given(cartParameter.getQuantity()).willReturn(2L);
		given(asahiSiteUtil.isCub()).willReturn(true);
		given(cartService.getEntryForNumber(sessionCart, 1)).willReturn(cartEntry);
		sabmUpdateCartEntryHook.afterUpdateCartEntry(cartParameter, result);
		Assert.assertEquals(true, cartEntry.getIsChange());
	}
}
