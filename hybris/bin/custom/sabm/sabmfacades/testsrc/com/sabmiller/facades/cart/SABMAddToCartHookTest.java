/**
 *
 */
package com.sabmiller.facades.cart;

import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.mock;

import de.hybris.platform.commerceservices.order.CommerceCartModificationException;
import de.hybris.platform.core.model.order.CartModel;
import de.hybris.platform.order.CartService;
import de.hybris.platform.servicelayer.model.ModelService;

import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import com.sabmiller.facades.order.hook.impl.SABMAddToCartHook;


/**
 * @author tom.minwen.wang
 *
 */
public class SABMAddToCartHookTest
{

	@Mock
	ModelService modelService;
	@Mock
	CartService cartService;

	private SABMAddToCartHook sabmAddToCartHook;

	@Before
	public void setUp()
	{
		MockitoAnnotations.initMocks(this);
		sabmAddToCartHook = new SABMAddToCartHook();
		sabmAddToCartHook.setModelService(modelService);
	}

	@Test
	public void testSabCommerceAddToCart() throws CommerceCartModificationException
	{
		final CartModel sessionCart = mock(CartModel.class);
		given(cartService.getSessionCart()).willReturn(sessionCart);
		given(sessionCart.getCalculated()).willReturn(Boolean.TRUE);
		modelService.save(sessionCart);
		Assert.assertEquals(Boolean.TRUE, sessionCart.getCalculated());
		Assert.assertNotNull("cart not null", sessionCart);
	}
}
