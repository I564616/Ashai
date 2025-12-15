/**
 *
 */
package com.sabmiller.facades.populators;

import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.mock;

import de.hybris.bootstrap.annotations.UnitTest;
import de.hybris.platform.commercefacades.order.data.OrderData;
import de.hybris.platform.core.PK;
import de.hybris.platform.core.model.order.OrderModel;

import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import com.sabmiller.core.model.ShippingCarrierModel;


/**
 * SABMAbstractOrderPopulatorTest
 *
 * @author yaopeng
 *
 */
@UnitTest
public class SABMAbstractOrderPopulatorTest
{

	@Mock
	private SABMAbstractOrderPopulator sabmAbstractOrderPopulator;

	@Before
	public void setUp()
	{
		MockitoAnnotations.initMocks(this);

		sabmAbstractOrderPopulator = new SABMAbstractOrderPopulator();

	}

	@Test
	public void testPopulator()
	{
		final OrderModel orderm = mock(OrderModel.class);
		final PK pk = PK.parse("1234567");
		given(orderm.getPk()).willReturn(pk);
		given(orderm.getDeliveryInstructions()).willReturn("instructions");
		final ShippingCarrierModel carrierModel = mock(ShippingCarrierModel.class);
		final PK pk1 = PK.parse("12345678");
		given(carrierModel.getPk()).willReturn(pk1);
		given(carrierModel.getCarrierCode()).willReturn("Hdl");
		given(carrierModel.getCarrierDescription()).willReturn("hdl");
		given(carrierModel.getCustomerOwned()).willReturn(Boolean.TRUE);
		final OrderData orderdata = new OrderData();
		sabmAbstractOrderPopulator.setShippingCarrier(carrierModel, orderdata);

		Assert.assertEquals("Hdl", orderdata.getDeliveryShippingCarrier().getCode());

	}
}
