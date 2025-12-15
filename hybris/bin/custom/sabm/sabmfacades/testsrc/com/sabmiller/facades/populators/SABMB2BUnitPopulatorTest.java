/**
 *
 */
package com.sabmiller.facades.populators;

import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.mock;

import de.hybris.bootstrap.annotations.UnitTest;
import de.hybris.platform.b2b.model.B2BUnitModel;
import de.hybris.platform.b2bacceleratorfacades.order.data.B2BUnitData;
import de.hybris.platform.commercefacades.user.data.AddressData;
import de.hybris.platform.converters.impl.AbstractPopulatingConverter;
import de.hybris.platform.core.PK;
import de.hybris.platform.core.model.order.CartModel;
import de.hybris.platform.core.model.user.AddressModel;
import de.hybris.platform.order.CartService;

import java.util.Arrays;
import java.util.Collections;
import java.util.List;

import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.MockitoAnnotations;

import com.apb.core.util.AsahiSiteUtil;
import com.sabmiller.core.b2b.services.SabmB2BUnitService;
import com.sabmiller.core.model.ShippingCarrierModel;


/**
 * SABMB2BUnitPopulatorTest
 *
 * @author yaopeng
 *
 */
@UnitTest
public class SABMB2BUnitPopulatorTest
{

	@Mock
	private AbstractPopulatingConverter<AddressModel, AddressData> addressConverter;
	@Mock
	private AsahiSiteUtil asahiSiteUtil;
	@InjectMocks
	private SABMB2BUnitPopulator sabmB2BUnitPopulator;

	@Mock
	private B2BUnitModel b2bUnitModel;
	@Mock
	private ShippingCarrierModel shippCarrierModel;
	@Mock
	private CartModel cartModel;
	@Mock
	private CartService cartService;
	@Mock
	private AddressModel address;
	@Mock
	private SabmB2BUnitService b2bUnitService;
	@Mock
	private B2BUnitData b2bUnitData;
	@Before
	public void setUp()
	{
		MockitoAnnotations.initMocks(this);
		sabmB2BUnitPopulator.setAddressConverter(addressConverter);
	}

	@Test
	public void testPopulator()
	{
		final B2BUnitModel b2bUnit = mock(B2BUnitModel.class);
		final PK pk = PK.parse("1234567");
		given(b2bUnit.getPk()).willReturn(pk);
		given(b2bUnit.getUid()).willReturn("test@test.com");
		final ShippingCarrierModel carrierModel = mock(ShippingCarrierModel.class);
		final PK pk1 = PK.parse("12345678");
		given(carrierModel.getPk()).willReturn(pk1);
		given(carrierModel.getCarrierCode()).willReturn("Hdl");
		given(carrierModel.getCarrierDescription()).willReturn("hdl");
		given(carrierModel.getCustomerOwned()).willReturn(Boolean.TRUE);
		given(asahiSiteUtil.isCub()).willReturn(true);

		final ShippingCarrierModel carrierModel1 = mock(ShippingCarrierModel.class);
		final PK pk2 = PK.parse("123456789");
		given(carrierModel1.getPk()).willReturn(pk2);
		given(carrierModel1.getCarrierCode()).willReturn("Hdl");
		given(carrierModel1.getCarrierDescription()).willReturn("hdl");
		given(carrierModel1.getCustomerOwned()).willReturn(Boolean.TRUE);


		final List<ShippingCarrierModel> shipplist = Arrays.asList(carrierModel, carrierModel1);
		given(b2bUnit.getShippingCarriers()).willReturn(shipplist);
		final B2BUnitData b2bUnitData = new B2BUnitData();

		sabmB2BUnitPopulator.populate(b2bUnit, b2bUnitData);

		Assert.assertEquals("Hdl", b2bUnitData.getShippingCarriers().get(0).getCode());

	}

	@Test
	public void setShippingCarriersTest()
	{
		given(cartService.getSessionCart()).willReturn(cartModel);
		given(cartModel.getDeliveryAddress()).willReturn(address);
		given(address.getPartnerNumber()).willReturn("partnerNumber");
		given(b2bUnitService.getUnitForUid("partnerNumber")).willReturn(b2bUnitModel);
		given(b2bUnitModel.getShippingCarriers()).willReturn(Collections.singletonList(shippCarrierModel));
		sabmB2BUnitPopulator.setShippingCarriers(b2bUnitModel, b2bUnitData);
		Mockito.verify(b2bUnitData).setShippingCarriers(Mockito.any());
	}

}
