/**
 *
 */
package com.sabmiller.core.strategy;

import static org.mockito.BDDMockito.given;

import de.hybris.platform.europe1.model.PriceRowModel;

import java.math.BigDecimal;

import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.MockitoAnnotations;

import com.sabmiller.core.deals.strategies.DefaultSABMDiscountPerUnitCalculationStrategy;
import com.sabmiller.core.model.SABMAlcoholVariantProductEANModel;
import com.sabmiller.core.product.SabmPriceRowService;
import com.sabmiller.core.product.SabmProductService;
import de.hybris.platform.servicelayer.session.SessionService;

/**
 *
 */
public class DefaultSABMDiscountPerUnitCalculationStrategyTest
{
	@InjectMocks
	DefaultSABMDiscountPerUnitCalculationStrategy strategy = new DefaultSABMDiscountPerUnitCalculationStrategy();

	@Mock
	private SabmPriceRowService priceRowService;

	@Mock
	private SabmProductService productService;

	@Mock
	private SessionService sessionService;

	@Before
	public void setUp()
	{
		MockitoAnnotations.initMocks(this);
	}

	@Test
	public void testcalculateDiscountPerUnit()
	{
		//final SABMAlcoholVariantProductMaterialModel matProduct = Mockito.mock(SABMAlcoholVariantProductMaterialModel.class);
		final SABMAlcoholVariantProductEANModel baseProduct = Mockito.mock(SABMAlcoholVariantProductEANModel.class);
		final PriceRowModel priceRow = Mockito.mock(PriceRowModel.class);
		given(priceRow.getBasePrice()).willReturn(100.00);
		given(priceRow.getPrice()).willReturn(100.00);
		//given(matProduct.getBaseProduct()).willReturn(baseProduct);
		given(priceRowService.getPriceRowByProduct(baseProduct)).willReturn(priceRow);
		final BigDecimal result = strategy.calculateDiscountPerUnit(baseProduct, 50.00);

		Assert.assertEquals(result.scale(), 0);
	}
}
