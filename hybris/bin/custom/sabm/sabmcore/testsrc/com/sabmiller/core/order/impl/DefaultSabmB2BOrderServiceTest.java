package com.sabmiller.core.order.impl;

import static org.junit.Assert.assertEquals;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

import de.hybris.bootstrap.annotations.UnitTest;
import de.hybris.platform.core.model.product.UnitModel;

import java.util.Arrays;
import java.util.List;

import org.junit.Before;
import org.junit.Test;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import com.sabmiller.core.model.ProductUOMMappingModel;
import com.sabmiller.core.model.SABMAlcoholVariantProductEANModel;
import com.sabmiller.core.product.SabmUnitService;

import org.springframework.test.util.ReflectionTestUtils;

/**
 * Created by evariz.p.papellero on 8/21/17.
 */
@UnitTest
public class DefaultSabmB2BOrderServiceTest
{

	private DefaultSabmB2BOrderService testClass;

	@Mock
	private SabmUnitService unitService;


	@Before
	public void setUp()
	{
		MockitoAnnotations.initMocks(this);
		testClass = new DefaultSabmB2BOrderService();
	}

	@Test
	public void testGetFinalQty()
	{

		ReflectionTestUtils.setField(testClass, "unitService", unitService);
		List<Integer> qtyInput = Arrays.asList(0, 1, 5, 8, 9, 10, 15, 18, 89, 91, 105, 109, 189, 191, 555, 439, 344, 546, 2008);
		List<Integer> qtyExpected = Arrays.asList(0, 1, 5, 8, 10, 10, 15, 20, 90, 100, 105, 110, 190, 200, 560, 440, 350, 550,
				2008);
		final ProductUOMMappingModel mappingModel1 = mock(ProductUOMMappingModel.class);
		final ProductUOMMappingModel mappingModel2 = mock(ProductUOMMappingModel.class);

		final UnitModel mockUnit = mock(UnitModel.class);
		final SABMAlcoholVariantProductEANModel mockEanProduct = mock(SABMAlcoholVariantProductEANModel.class);

		final UnitModel layer = new UnitModel();
		layer.setCode("LAY");
		when(mappingModel1.getFromUnit()).thenReturn(layer);
		when(mappingModel1.getQtyConversion()).thenReturn(10d);

		final UnitModel pallet = new UnitModel();
		pallet.setCode("PAL");
		when(mappingModel2.getFromUnit()).thenReturn(pallet);
		when(mappingModel2.getQtyConversion()).thenReturn(100d);

		when(mockEanProduct.getUnit()).thenReturn(mockUnit);
		when(mockUnit.getCode()).thenReturn("CAS");
		when(mockEanProduct.getUomMappings()).thenReturn(Arrays.asList(mappingModel1, mappingModel2));

		when(unitService.isValid(any())).thenReturn(true);

		for (int i = 0; i < qtyInput.size(); i++)
		{
			final Integer result = testClass.calculateSuggestedQtyUom(qtyInput.get(i), mockEanProduct);
			assertEquals(qtyExpected.get(i), result);
		}
	}

}
