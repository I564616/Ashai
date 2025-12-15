/**
 *
 */
package com.sabmiller.facades.populators;

import static org.junit.Assert.assertNotNull;
import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.mock;

import de.hybris.platform.commercefacades.order.data.OrderEntryData;
import de.hybris.platform.core.PK;
import de.hybris.platform.core.model.order.AbstractOrderEntryModel;
import de.hybris.platform.core.model.product.UnitModel;

import java.util.ArrayList;
import java.util.List;

import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import com.apb.core.util.AsahiSiteUtil;
import com.sabmiller.core.model.ProductUOMMappingModel;
import com.sabmiller.core.model.SABMAlcoholVariantProductEANModel;


/**
 * SABMOrderEntryUnitSwitchPopulatorTest
 *
 * @author xiaowu.a.zhang
 * @data 2015-11-18
 *
 */
public class SABMOrderEntryUnitSwitchPopulatorTest
{
	@Mock
	private AsahiSiteUtil asahiSiteUtil;
	@InjectMocks
	private SABMOrderEntryUnitSwitchPopulator sabmOrderEntryUnitSwitchPopulator;

	@Before
	public void setUp()
	{
		MockitoAnnotations.initMocks(this);

		//sabmOrderEntryUnitSwitchPopulator = new SABMOrderEntryUnitSwitchPopulator();
	}

	@Test
	public void testPopulator()
	{
		final AbstractOrderEntryModel orderEntryModel = mock(AbstractOrderEntryModel.class);

		final List<ProductUOMMappingModel> uomMappingList = new ArrayList<ProductUOMMappingModel>();
		final ProductUOMMappingModel uOMMapping = mock(ProductUOMMappingModel.class);
		final SABMAlcoholVariantProductEANModel sabvem = mock(SABMAlcoholVariantProductEANModel.class);
		final UnitModel unitInSource = mock(UnitModel.class);
		final UnitModel unitFromInProduct = mock(UnitModel.class);
		final UnitModel unitToInProduct = mock(UnitModel.class);
		final Double qtyConversion = Double.valueOf(9.0);
		final Long quantity = Long.valueOf(18);
		final PK pk1 = PK.parse("12387");
		final PK pk2 = PK.parse("22387");

		given(asahiSiteUtil.isCub()).willReturn(true);
		given(unitInSource.getPk()).willReturn(pk1);
		given(unitFromInProduct.getPk()).willReturn(pk2);
		given(unitToInProduct.getPk()).willReturn(pk1);
		given(unitFromInProduct.getName()).willReturn("unitFromInProduct.getName");
		given(uOMMapping.getFromUnit()).willReturn(unitFromInProduct);
		given(uOMMapping.getToUnit()).willReturn(unitToInProduct);
		given(uOMMapping.getQtyConversion()).willReturn(qtyConversion);
		uomMappingList.add(uOMMapping);
		given(sabvem.getUnit()).willReturn(unitInSource);
		given(sabvem.getUomMappings()).willReturn(uomMappingList);
		given(orderEntryModel.getProduct()).willReturn(sabvem);
		given(orderEntryModel.getUnit()).willReturn(unitInSource);
		given(orderEntryModel.getQuantity()).willReturn(quantity);

		final OrderEntryData orderEntryData = new OrderEntryData();
		orderEntryData.setQuantity(quantity);
		sabmOrderEntryUnitSwitchPopulator.populate(orderEntryModel, orderEntryData);
		assertNotNull(orderEntryData);

	}

}
