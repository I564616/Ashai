/**
 *
 */
package com.sabmiller.core.deals.strategies;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

import de.hybris.platform.servicelayer.session.SessionService;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.Date;
import java.util.List;

import org.apache.commons.lang3.time.DateUtils;
import org.apache.log4j.Level;
import org.apache.log4j.LogManager;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import com.sabmiller.core.enums.DealTypeEnum;
import com.sabmiller.core.model.AbstractDealBenefitModel;
import com.sabmiller.core.model.AbstractDealConditionModel;
import com.sabmiller.core.model.ComplexDealConditionModel;
import com.sabmiller.core.model.DealConditionGroupModel;
import com.sabmiller.core.model.DealModel;
import com.sabmiller.core.model.DiscountDealBenefitModel;
import com.sabmiller.core.model.FreeGoodsDealBenefitModel;
import com.sabmiller.core.model.ProductDealConditionModel;
import com.sabmiller.core.model.SABMAlcoholVariantProductEANModel;
import com.sabmiller.core.model.SABMAlcoholVariantProductMaterialModel;
import com.sabmiller.core.product.SabmPriceRowService;
import com.sabmiller.core.product.SabmProductService;


/**
 *
 */
public class DefaultSABMDealValidationStrategy2Test
{
	@InjectMocks
	private final SABMDealValidationStrategy dealValidationStrategy = new DefaultSABMDealValidationStrategy();;

	@Mock
	private SessionService sessionService;

	@Mock
	private SabmProductService productService;

	@Mock
	private SabmPriceRowService priceRowService;

	@Mock
	private SABMDiscountPerUnitCalculationStrategy discountPerUnitCalculationStrategy;


	@Before
	public void setUp()
	{
		MockitoAnnotations.initMocks(this);
		LogManager.getLogger(DefaultSABMDealValidationStrategy.class).setLevel(Level.DEBUG);

	}


	/**
	 * normal deal. should pass the validate.
	 */
	@Test
	public void testValidateSuccess()
	{
		final DealModel deal = mockComplexDeal();
		// mock the products.
		final SABMAlcoholVariantProductMaterialModel prod = new SABMAlcoholVariantProductMaterialModel();
		prod.setCode("matVariant1");

		final SABMAlcoholVariantProductEANModel eanprod = mock(SABMAlcoholVariantProductEANModel.class);
		given(eanprod.getCode()).willReturn("eanVariant1");
		given(eanprod.getPurchasable()).willReturn(true);
		prod.setBaseProduct(eanprod);

		final List<SABMAlcoholVariantProductMaterialModel> materials = new ArrayList<>();
		materials.add(prod);
		when(productService.getProductByHierarchy(any(), any(), any(), any(), any(), any()))
				.thenReturn(materials);

		final SABMAlcoholVariantProductMaterialModel freeprod = new SABMAlcoholVariantProductMaterialModel();
		freeprod.setCode("freeGoods");
		when(productService.getProductForCodeSafe("freeGoods")).thenReturn(freeprod);

		given(productService.findExcludedProduct(deal.getConditionGroup().getDealConditions())).willReturn(Collections.emptyList());

		Assert.assertTrue(dealValidationStrategy.validateDeal(deal, null));
	}

	/**
	 * invalid complex deal.
	 *
	 * for complex deal condition
	 *
	 * cannot find valid base products based on the brand defined in deal
	 */
	@Test
	public void testValidate1()
	{
		final DealModel deal = mockComplexDeal();
		// mock the products.
		when(productService.getProductByHierarchy(anyString(), anyString(), anyString(), anyString(), anyString(), anyString()))
				.thenReturn(Collections.emptyList());

		final SABMAlcoholVariantProductMaterialModel freeprod = new SABMAlcoholVariantProductMaterialModel();
		freeprod.setCode("freeGoods");
		when(productService.getProductForCodeSafe("freeGoods")).thenReturn(freeprod);

		given(productService.findExcludedProduct(deal.getConditionGroup().getDealConditions())).willReturn(Collections.emptyList());

		Assert.assertFalse(dealValidationStrategy.validateDeal(deal, null));
	}

	/**
	 * valid complex deal.
	 *
	 * for complex deal condition
	 *
	 * the exclude condition list is not empty. after exclusion, some products left
	 */
	@Test
	public void testValidate2()
	{
		final DealModel deal = mockComplexDeal_valid2();

		final SABMAlcoholVariantProductMaterialModel prod = new SABMAlcoholVariantProductMaterialModel();
		prod.setCode("matVariant1");

		final SABMAlcoholVariantProductEANModel eanprod = mock(SABMAlcoholVariantProductEANModel.class);
		given(eanprod.getCode()).willReturn("eanVariant1");
		given(eanprod.getPurchasable()).willReturn(true);
		prod.setBaseProduct(eanprod);

		final List<SABMAlcoholVariantProductMaterialModel> materials = new ArrayList<>();
		materials.add(prod);
		when(productService.getProductByHierarchy(any(), any(), any(), any(), any(), any()))
				.thenReturn(materials);

		final SABMAlcoholVariantProductMaterialModel freeprod = new SABMAlcoholVariantProductMaterialModel();
		freeprod.setCode("freeGoods");
		when(productService.getProductForCodeSafe("freeGoods")).thenReturn(freeprod);

		final SABMAlcoholVariantProductMaterialModel prodex = new SABMAlcoholVariantProductMaterialModel();
		prodex.setCode("matVariant_ex1");
		given(productService.findExcludedProduct(deal.getConditionGroup().getDealConditions())).willReturn(Arrays.asList(prodex));

		Assert.assertTrue(dealValidationStrategy.validateDeal(deal, null));
	}

	/**
	 * invalid complex deal.
	 *
	 * for complex deal condition
	 *
	 * the exclude condition list is not empty. after exclusion, NO products left
	 */
	@Test
	public void testValidate3()
	{
		final DealModel deal = mockComplexDeal_valid2();

		final SABMAlcoholVariantProductMaterialModel prod = new SABMAlcoholVariantProductMaterialModel();
		prod.setCode("matVariant1");
		final List<SABMAlcoholVariantProductMaterialModel> materials = new ArrayList<>();
		materials.add(prod);
		when(productService.getProductByHierarchy(anyString(), anyString(), anyString(), anyString(), anyString(), anyString()))
				.thenReturn(materials);

		final SABMAlcoholVariantProductMaterialModel freeprod = new SABMAlcoholVariantProductMaterialModel();
		freeprod.setCode("freeGoods");
		when(productService.getProductForCodeSafe("freeGoods")).thenReturn(freeprod);

		given(productService.findExcludedProduct(deal.getConditionGroup().getDealConditions())).willReturn(Arrays.asList(prod));

		Assert.assertFalse(dealValidationStrategy.validateDeal(deal, null));
	}

	/**
	 * valid complex deal.
	 *
	 * for product deal condition
	 *
	 */
	@Test
	public void testValidate4()
	{
		final DealModel deal = mockComplexDeal_prod();

		final SABMAlcoholVariantProductMaterialModel prod = new SABMAlcoholVariantProductMaterialModel();
		prod.setCode("matVariant1");
		final List<SABMAlcoholVariantProductMaterialModel> materials = new ArrayList<>();
		materials.add(prod);
		when(productService.getProductByHierarchy(anyString(), anyString(), anyString(), anyString(), anyString(), anyString()))
				.thenReturn(materials);

		final SABMAlcoholVariantProductMaterialModel freeprod = new SABMAlcoholVariantProductMaterialModel();
		freeprod.setCode("freeGoods");
		when(productService.getProductForCodeSafe("freeGoods")).thenReturn(freeprod);
		when(productService.getProductForCodeSafe("matVariant1")).thenReturn(prod);

		given(productService.findExcludedProduct(deal.getConditionGroup().getDealConditions())).willReturn(Collections.emptyList());

		Assert.assertFalse(dealValidationStrategy.validateDeal(deal, null));
	}

	/**
	 * invalid complex deal.
	 *
	 * for product deal condition
	 *
	 * the base product is invalid
	 */
	@Test
	public void testValidate5()
	{
		final DealModel deal = mockComplexDeal_prod();

		final SABMAlcoholVariantProductMaterialModel prod = new SABMAlcoholVariantProductMaterialModel();
		prod.setCode("matVariant1");
		final List<SABMAlcoholVariantProductMaterialModel> materials = new ArrayList<>();
		materials.add(prod);
		when(productService.getProductByHierarchy(anyString(), anyString(), anyString(), anyString(), anyString(), anyString()))
				.thenReturn(materials);

		final SABMAlcoholVariantProductMaterialModel freeprod = new SABMAlcoholVariantProductMaterialModel();
		freeprod.setCode("freeGoods");
		when(productService.getProductForCodeSafe("freeGoods")).thenReturn(freeprod);
		when(productService.getProductForCodeSafe("matVariant1")).thenReturn(null);

		given(productService.findExcludedProduct(deal.getConditionGroup().getDealConditions())).willReturn(Collections.emptyList());

		Assert.assertFalse(dealValidationStrategy.validateDeal(deal, null));
	}


	/**
	 * invalid complex deal.
	 *
	 * validate the free goods
	 *
	 * the free good is invalid
	 */
	@Test
	public void testValidate6()
	{
		final DealModel deal = mockComplexDeal();

		final SABMAlcoholVariantProductMaterialModel prod = new SABMAlcoholVariantProductMaterialModel();
		prod.setCode("matVariant1");

		final SABMAlcoholVariantProductEANModel eanprod = mock(SABMAlcoholVariantProductEANModel.class);
		given(eanprod.getCode()).willReturn("eanVariant1");
		given(eanprod.getPurchasable()).willReturn(true);
		prod.setBaseProduct(eanprod);

		final List<SABMAlcoholVariantProductMaterialModel> materials = new ArrayList<>();
		materials.add(prod);
		when(productService.getProductByHierarchy(anyString(), anyString(), anyString(), anyString(), anyString(), anyString()))
				.thenReturn(materials);

		final SABMAlcoholVariantProductMaterialModel freeprod = new SABMAlcoholVariantProductMaterialModel();
		freeprod.setCode("freeGoods");
		when(productService.getProductForCodeSafe("freeGoods")).thenReturn(null);
		when(productService.getProductForCodeSafe("matVariant1")).thenReturn(prod);

		given(productService.findExcludedProduct(deal.getConditionGroup().getDealConditions())).willReturn(Collections.emptyList());

		Assert.assertFalse(dealValidationStrategy.validateDeal(deal, null));
	}

	/**
	 * valid limited deal.
	 *
	 */
	@Test
	public void testValidate7()
	{
		final DealModel deal = mockLimitedDeal();

		final SABMAlcoholVariantProductMaterialModel prod = new SABMAlcoholVariantProductMaterialModel();
		prod.setCode("matVariant1");

		when(productService.getProductForCodeSafe("matVariant1")).thenReturn(prod);

		Assert.assertFalse(dealValidationStrategy.validateDeal(deal, null));
	}

	/**
	 * invalid limited deal.
	 *
	 * the base product is invalid
	 */
	@Test
	public void testValidate8()
	{
		final DealModel deal = mockLimitedDeal();

		final SABMAlcoholVariantProductMaterialModel prod = new SABMAlcoholVariantProductMaterialModel();
		prod.setCode("matVariant1");

		when(productService.getProductForCodeSafe("matVariant1")).thenReturn(null);


		Assert.assertFalse(dealValidationStrategy.validateDeal(deal, null));
	}

	/**
	 * invalid complex deal.
	 *
	 * validate the purchasable
	 *
	 * the ean product is not purchasable
	 */
	@Test
	public void testValidate9()
	{
		final DealModel deal = mockComplexDeal();

		final SABMAlcoholVariantProductMaterialModel prod = new SABMAlcoholVariantProductMaterialModel();
		prod.setCode("matVariant1");

		final SABMAlcoholVariantProductEANModel eanprod = mock(SABMAlcoholVariantProductEANModel.class);
		given(eanprod.getCode()).willReturn("eanVariant1");
		given(eanprod.getPurchasable()).willReturn(false);
		prod.setBaseProduct(eanprod);

		final List<SABMAlcoholVariantProductMaterialModel> materials = new ArrayList<>();
		materials.add(prod);
		when(productService.getProductByHierarchy(anyString(), anyString(), anyString(), anyString(), anyString(), anyString()))
				.thenReturn(materials);

		final SABMAlcoholVariantProductMaterialModel freeprod = new SABMAlcoholVariantProductMaterialModel();
		freeprod.setCode("freeGoods");
		when(productService.getProductForCodeSafe("freeGoods")).thenReturn(freeprod);
		when(productService.getProductForCodeSafe("matVariant1")).thenReturn(prod);

		given(productService.findExcludedProduct(deal.getConditionGroup().getDealConditions())).willReturn(Collections.emptyList());

		Assert.assertFalse(dealValidationStrategy.validateDeal(deal, null));
	}

	private DealModel mockComplexDeal_valid2()
	{
		// mock the deal
		final DealModel dm = mockComplexDeal();

		// add an exclude condition.
		final ComplexDealConditionModel cdcm = new ComplexDealConditionModel();
		cdcm.setExclude(true);
		dm.getConditionGroup().getDealConditions().add(cdcm);

		return dm;
	}


	private DealModel mockComplexDeal()
	{
		// mock the deal
		final DealModel dm = new DealModel();
		dm.setDealType(DealTypeEnum.COMPLEX);
		dm.setValidTo(DateUtils.addDays(new Date(), 5));
		dm.setValidFrom(DateUtils.addDays(new Date(), -5));
		final DealConditionGroupModel dcgm = new DealConditionGroupModel();
		dm.setConditionGroup(dcgm);

		final List<AbstractDealConditionModel> dclist = new ArrayList<>();
		dcgm.setDealConditions(dclist);

		final ComplexDealConditionModel cdcm = new ComplexDealConditionModel();
		dclist.add(cdcm);

		final FreeGoodsDealBenefitModel fgdb = new FreeGoodsDealBenefitModel();
		fgdb.setQuantity(1);
		fgdb.setProductCode("freeGoods");
		final List<AbstractDealBenefitModel> dblist = new ArrayList<>();
		dblist.add(fgdb);
		dcgm.setDealBenefits(dblist);

		return dm;
	}

	private DealModel mockComplexDeal_prod()
	{
		// mock the deal
		final DealModel dm = new DealModel();
		dm.setDealType(DealTypeEnum.COMPLEX);
		dm.setValidTo(DateUtils.addDays(new Date(), 5));
		dm.setValidFrom(DateUtils.addDays(new Date(), -5));

		final DealConditionGroupModel dcgm = new DealConditionGroupModel();
		dm.setConditionGroup(dcgm);

		final List<AbstractDealConditionModel> dclist = new ArrayList<>();
		dcgm.setDealConditions(dclist);

		final ProductDealConditionModel cdcm = new ProductDealConditionModel();
		cdcm.setProductCode("matVariant1");
		dclist.add(cdcm);

		final FreeGoodsDealBenefitModel fgdb = new FreeGoodsDealBenefitModel();
		fgdb.setQuantity(1);
		fgdb.setProductCode("freeGoods");
		final List<AbstractDealBenefitModel> dblist = new ArrayList<>();
		dblist.add(fgdb);
		dcgm.setDealBenefits(dblist);

		return dm;
	}

	private DealModel mockLimitedDeal()
	{
		// mock the deal
		final DealModel dm = new DealModel();
		dm.setDealType(DealTypeEnum.LIMITED);
		dm.setMaxConditionBaseValue(1000d);

		dm.setValidTo(DateUtils.addDays(new Date(), 5));
		dm.setValidFrom(DateUtils.addDays(new Date(), -5));
		final DealConditionGroupModel dcgm = new DealConditionGroupModel();
		dm.setConditionGroup(dcgm);

		final List<AbstractDealConditionModel> dclist = new ArrayList<>();
		dcgm.setDealConditions(dclist);

		final ProductDealConditionModel cdcm = new ProductDealConditionModel();
		cdcm.setProductCode("matVariant1");
		dclist.add(cdcm);

		final DiscountDealBenefitModel fgdb = new DiscountDealBenefitModel();
		fgdb.setAmount(-10d);
		fgdb.setCurrency(true);
		final List<AbstractDealBenefitModel> dblist = new ArrayList<>();
		dblist.add(fgdb);
		dcgm.setDealBenefits(dblist);

		return dm;
	}

}
