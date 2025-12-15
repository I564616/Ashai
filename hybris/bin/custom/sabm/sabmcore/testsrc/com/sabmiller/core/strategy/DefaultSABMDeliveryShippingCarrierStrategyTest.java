/**
 *
 */
package com.sabmiller.core.strategy;

import static org.mockito.BDDMockito.given;

import de.hybris.bootstrap.annotations.UnitTest;
import de.hybris.platform.commerceservices.order.CommerceCartCalculationStrategy;
import de.hybris.platform.commerceservices.order.CommerceDeliveryModeValidationStrategy;
import de.hybris.platform.commerceservices.service.data.CommerceCheckoutParameter;
import de.hybris.platform.core.model.order.AbstractOrderModel;
import de.hybris.platform.core.model.order.CartModel;
import de.hybris.platform.core.model.user.UserModel;
import de.hybris.platform.servicelayer.model.ModelService;

import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.MockitoAnnotations;

import com.sabmiller.core.model.ShippingCarrierModel;
import com.sabmiller.core.strategy.impl.DefaultSABMDeliveryShippingCarrierStrategy;


/**
 * @author yaopeng
 *
 */
@UnitTest
public class DefaultSABMDeliveryShippingCarrierStrategyTest
{
	@Mock
	private ModelService modelService;

	private DefaultSABMDeliveryShippingCarrierStrategy sabmDeliveryShippingCarrierStrategy;
	@Mock
	private CommerceCartCalculationStrategy commerceCartCalculationStrategy;
	@Mock
	private CommerceDeliveryModeValidationStrategy commerceDeliveryModeValidationStrategy;

	@Before
	public void setUp()
	{
		MockitoAnnotations.initMocks(this);
		sabmDeliveryShippingCarrierStrategy = new DefaultSABMDeliveryShippingCarrierStrategy();
		sabmDeliveryShippingCarrierStrategy.setModelService(modelService);
	}

	@SuppressWarnings("boxing")
	@Test
	public void testGetSelectableDeliveryModesForOrder() throws Exception
	{
		final AbstractOrderModel mockOrder = Mockito.mock(AbstractOrderModel.class);
		given(mockOrder.getDeliveryInstructions()).willReturn("abc");

		final CartModel cartModel = Mockito.mock(CartModel.class);

		final UserModel user = Mockito.mock(UserModel.class);

		final CommerceCheckoutParameter parameter = Mockito.mock(CommerceCheckoutParameter.class);

		final ShippingCarrierModel shippModel = Mockito.mock(ShippingCarrierModel.class);

		given(shippModel.getCarrierCode()).willReturn("Hdl");
		given(shippModel.getCustomerOwned()).willReturn(true);
		given(shippModel.getCarrierDescription()).willReturn("hdl");

		given(cartModel.getUser()).willReturn(user);
		given(cartModel.getDeliveryShippingCarrier()).willReturn(shippModel);
		given(parameter.getCart()).willReturn(cartModel);
		given(parameter.getShippingCarrier()).willReturn(shippModel);
		Assert.assertEquals(true, sabmDeliveryShippingCarrierStrategy.setShippingCarrier(parameter));
	}



}
