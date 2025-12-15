/**
 *
 */
package com.sabmiller.core.strategy;

import static org.mockito.BDDMockito.given;

import de.hybris.bootstrap.annotations.UnitTest;
import de.hybris.platform.commerceservices.delivery.dao.CountryZoneDeliveryModeDao;
import de.hybris.platform.core.model.order.AbstractOrderModel;
import de.hybris.platform.core.model.order.delivery.DeliveryModeModel;

import java.util.ArrayList;
import java.util.List;

import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.MockitoAnnotations;

import com.sabmiller.core.strategy.impl.SABMDeliveryModeLookupStrategy;


/**
 * SABMDeliveryModeLookupStrategyTest
 *
 * @author yaopeng
 *
 */
@UnitTest
public class SABMDeliveryModeLookupStrategyTest
{

	private SABMDeliveryModeLookupStrategy sabmDeliveryModeLookupStrategy;
	@Mock
	private CountryZoneDeliveryModeDao countryZoneDeliveryModeDao;

	@Before
	public void setUp()
	{
		MockitoAnnotations.initMocks(this);
		sabmDeliveryModeLookupStrategy = new SABMDeliveryModeLookupStrategy();
		sabmDeliveryModeLookupStrategy.setCountryZoneDeliveryModeDao(countryZoneDeliveryModeDao);
	}

	@SuppressWarnings("boxing")
	@Test
	public void testGetSelectableDeliveryModesForOrder()
	{
		final AbstractOrderModel mockOrder = Mockito.mock(AbstractOrderModel.class);

		final DeliveryModeModel deliveryModel = Mockito.mock(DeliveryModeModel.class);
		given(deliveryModel.getCode()).willReturn("cub");
		final List<DeliveryModeModel> addlist = Mockito.mock(ArrayList.class);
		given(addlist.get(0)).willReturn(deliveryModel);
		given(mockOrder.getDeliveryMode()).willReturn(deliveryModel);

		given(sabmDeliveryModeLookupStrategy.getSelectableDeliveryModesForOrder(mockOrder)).willReturn(addlist);

		Assert.assertEquals("cub", addlist.get(0).getCode());
	}


}
