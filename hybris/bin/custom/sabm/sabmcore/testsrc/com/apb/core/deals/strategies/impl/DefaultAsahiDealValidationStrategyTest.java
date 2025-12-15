/**
 *
 */
package com.apb.core.deals.strategies.impl;

import static org.junit.Assert.assertEquals;
import static org.mockito.Mockito.when;

import de.hybris.bootstrap.annotations.UnitTest;
import de.hybris.platform.catalog.enums.ArticleApprovalStatus;
import de.hybris.platform.core.model.product.ProductModel;

import java.util.Calendar;
import java.util.Date;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Spy;
import org.mockito.junit.MockitoJUnitRunner;

import com.apb.core.model.ApbProductModel;
import com.sabmiller.core.model.AsahiB2BUnitModel;
import com.sabmiller.core.model.AsahiDealModel;
import com.sabmiller.core.model.AsahiFreeGoodsDealBenefitModel;
import com.sabmiller.core.model.AsahiProductDealConditionModel;
import com.sabmiller.core.product.SabmPriceRowService;
import com.sabmiller.core.product.SabmProductService;


/**
 * @author EG588BU
 *
 */
@UnitTest
@RunWith(MockitoJUnitRunner.class)
public class DefaultAsahiDealValidationStrategyTest
{
	@Spy
	@InjectMocks
	private final DefaultAsahiDealValidationStrategy defaultAsahiDealValidationStrategy = new DefaultAsahiDealValidationStrategy();
	@Mock
	private SabmProductService productService;
	@Mock
	private SabmPriceRowService priceRowService;
	@Mock
	private AsahiDealModel deal;
	@Mock
	private AsahiProductDealConditionModel productDealCondition;
	@Mock
	private ApbProductModel apbProductModel;
	@Mock
	private AsahiFreeGoodsDealBenefitModel freeGoodsDealBenefit;
	@Mock
	private ApbProductModel benefitProduct;
	@Mock
	private ProductModel product;
	@Mock
	private AsahiB2BUnitModel b2bUnit;


	@Before
	public void setup()
	{
		final Calendar cal = Calendar.getInstance();
		cal.setTime(new Date());
		cal.add(Calendar.DATE, 10);
		final Date validTo = cal.getTime();
		when(deal.getValidTo()).thenReturn(validTo);
		when(deal.getDealCondition()).thenReturn(productDealCondition);
		when(productDealCondition.getProductCode()).thenReturn("conditionProductCode");
		when(productDealCondition.getQuantity()).thenReturn(10);
		when(productService.getProductForCodeSafe("conditionProductCode")).thenReturn(apbProductModel);
		when(apbProductModel.isActive()).thenReturn(true);
		when(apbProductModel.getApprovalStatus()).thenReturn(ArticleApprovalStatus.APPROVED);
		when(deal.getDealBenefit()).thenReturn(freeGoodsDealBenefit);
		when(freeGoodsDealBenefit.getProductCode()).thenReturn("benefitProductCode");
		when(freeGoodsDealBenefit.getQuantity()).thenReturn(123);
		when(productService.getProductForCodeSafe("benefitProductCode")).thenReturn(apbProductModel);
	}

	@Test
	public void validateDealTest1() {
		assertEquals(true, defaultAsahiDealValidationStrategy.validateDeal(deal));
	}

	@Test
	public void validateDealTest2()
	{

		assertEquals(true, defaultAsahiDealValidationStrategy.validateDeal(deal, b2bUnit));
	}

	@Test
	public void validateDealTest3()
	{
		when(freeGoodsDealBenefit.getQuantity()).thenReturn(0);
		assertEquals(false, defaultAsahiDealValidationStrategy.validateDeal(deal, b2bUnit));
	}

	@Test
	public void validateDealTest4()
	{
		when(productDealCondition.getQuantity()).thenReturn(0);
		assertEquals(false, defaultAsahiDealValidationStrategy.validateDeal(deal));
	}

	@Test
	public void validateDealTest5()
	{
		when(productService.getProductForCodeSafe("conditionProductCode")).thenReturn(product);
		assertEquals(false, defaultAsahiDealValidationStrategy.validateDeal(deal, b2bUnit));
	}

	@Test
	public void validateDealTest6()
	{
		when(productService.getProductForCodeSafe("benefitProductCode")).thenReturn(product);
		assertEquals(false, defaultAsahiDealValidationStrategy.validateDeal(deal));
	}
}
