/**
 *
 */
package com.sabmiller.facades.populators;

import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.mock;

import de.hybris.bootstrap.annotations.UnitTest;
import de.hybris.platform.core.Registry;
import de.hybris.platform.core.model.product.UnitModel;
import de.hybris.platform.servicelayer.i18n.I18NService;

import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

//import org.apache.catalina.core.ApplicationContext;
import org.apache.commons.lang3.StringUtils;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockedStatic;
import org.mockito.Mockito;
import org.mockito.MockitoAnnotations;
import org.springframework.context.ApplicationContext;
import org.springframework.context.MessageSource;
import org.springframework.test.util.ReflectionTestUtils;

import com.google.common.collect.Lists;
import com.sabmiller.core.deals.services.DealsService;
import com.sabmiller.core.enums.DealTypeEnum;
import com.sabmiller.core.model.AbstractDealBenefitModel;
import com.sabmiller.core.model.AbstractDealConditionModel;
import com.sabmiller.core.model.ComplexDealConditionModel;
import com.sabmiller.core.model.DealConditionGroupModel;
import com.sabmiller.core.model.DealModel;
import com.sabmiller.core.model.FreeGoodsDealBenefitModel;
import com.sabmiller.core.model.ProductDealConditionModel;
import com.sabmiller.core.model.SABMAlcoholProductModel;
import com.sabmiller.core.model.SABMAlcoholVariantProductEANModel;
import com.sabmiller.core.model.SABMAlcoholVariantProductMaterialModel;
import com.sabmiller.core.product.SabmProductService;
import com.sabmiller.facades.deal.data.DealJson;


/**
 * SABMDealTitlePopulatorTest
 */
@UnitTest
public class SABMDealTitlePopulatorTest
{
	@InjectMocks
	private final SABMDealTitlePopulator sabmDealTitlePopulator = new SABMDealTitlePopulator();

	@Mock
	private SabmProductService productService;

	@Mock
	private DealsService dealsService;

	@Mock
	private I18NService i18nService;

	@Mock
	MessageSource messageSource;

	final Locale locale = Locale.ENGLISH;

	@Before
	public void setUp()
	{
		MockitoAnnotations.initMocks(this);
		final ApplicationContext applicationContext = mock(ApplicationContext.class);
		ReflectionTestUtils.setField(sabmDealTitlePopulator, "productService", productService);
		try (MockedStatic<Registry> utilities = Mockito.mockStatic(Registry.class))
		{
			utilities.when(Registry::getApplicationContext).thenReturn(applicationContext);
			Mockito.when(applicationContext.getBean("messageSource", MessageSource.class)).thenReturn(mock(MessageSource.class));
		}

		Mockito.when(i18nService.getCurrentLocale()).thenReturn(locale);
	}

	/**
	 * Buy a minimum of 10 cases of Carlton Mid 4x6 375ml bottles
	 *
	 */
	@Test
	public void testTitlePopulate1()
	{
		final DealModel noComplexDeal = mock(DealModel.class);
		given(noComplexDeal.getDealType()).willReturn(DealTypeEnum.DISCOUNT);

		final String productCode = "productTestCode";
		final String brand = "testBrand";
		final String sellingName = "product name";
		final String packConfiguration = "1*20 boote";

		//PowerMockito.mockStatic(ApplicationContext.class);

		final SABMAlcoholVariantProductEANModel eanProduct = mock(SABMAlcoholVariantProductEANModel.class);
		final SABMAlcoholProductModel baseProduct = mock(SABMAlcoholProductModel.class);
		given(baseProduct.getBrand()).willReturn(brand);
		given(eanProduct.getBaseProduct()).willReturn(baseProduct);
		given(eanProduct.getBaseProduct()).willReturn(eanProduct);
		given(eanProduct.getSellingName()).willReturn(sellingName);
		given(eanProduct.getPackConfiguration()).willReturn(packConfiguration);
		given(eanProduct.getMinOrderQuantity()).willReturn(2);


		final UnitModel unitmodel = mock(UnitModel.class);
		given(unitmodel.getName()).willReturn("case");
		given(unitmodel.getPluralName()).willReturn("cases");
		given(eanProduct.getUnit()).willReturn(unitmodel);

		final SABMAlcoholVariantProductMaterialModel materialProduct = mock(SABMAlcoholVariantProductMaterialModel.class);

		given(materialProduct.getBaseProduct()).willReturn(eanProduct);
		given(productService.getProductForCode(productCode)).willReturn(materialProduct);
		Mockito.when(productService.getProductForCodeSafe(productCode)).thenReturn(materialProduct);

		final DealConditionGroupModel dealConditionGroupModel1 = mock(DealConditionGroupModel.class);
		final List<AbstractDealConditionModel> dealConditionModels1 = new ArrayList<>();
		final ProductDealConditionModel productDealConditionModel1 = mock(ProductDealConditionModel.class);
		given(productDealConditionModel1.getProductCode()).willReturn(productCode);
		given(productDealConditionModel1.getQuantity()).willReturn(Integer.valueOf(10));
		given(productDealConditionModel1.getMinQty()).willReturn(1);
		final ProductDealConditionModel productDealConditionModel2 = mock(ProductDealConditionModel.class);
		given(productDealConditionModel2.getProductCode()).willReturn(productCode);
		given(productDealConditionModel2.getQuantity()).willReturn(Integer.valueOf(5));
		given(productDealConditionModel2.getMinQty()).willReturn(2);
		dealConditionModels1.add(productDealConditionModel1);
		dealConditionModels1.add(productDealConditionModel2);
		final List<AbstractDealBenefitModel> dealBenefitModels1 = new ArrayList<>();
		final FreeGoodsDealBenefitModel freeGoodsDealBenefitModel1 = mock(FreeGoodsDealBenefitModel.class);
		final FreeGoodsDealBenefitModel freeGoodsDealBenefitModel2 = mock(FreeGoodsDealBenefitModel.class);
		given(freeGoodsDealBenefitModel1.getProductCode()).willReturn(productCode);
		given(freeGoodsDealBenefitModel1.getQuantity()).willReturn(Integer.valueOf(5));
		given(freeGoodsDealBenefitModel2.getQuantity()).willReturn(Integer.valueOf(15));
		given(freeGoodsDealBenefitModel2.getProductCode()).willReturn(productCode);
		dealBenefitModels1.add(freeGoodsDealBenefitModel1);
		dealBenefitModels1.add(freeGoodsDealBenefitModel2);
		given(dealConditionGroupModel1.getDealConditions()).willReturn(dealConditionModels1);
		given(dealConditionGroupModel1.getDealBenefits()).willReturn(dealBenefitModels1);

		given(noComplexDeal.getConditionGroup()).willReturn(dealConditionGroupModel1);
		given(dealsService.isMultiRange(dealConditionModels1)).willReturn(false);
	    //given(registry.getApplicationContext()).willReturn((org.springframework.context.ApplicationContext) mock(ApplicationContext.class));

		Mockito.when(messageSource.getMessage("text.deal.title.buy", null, locale)).thenReturn("Buy&ensp;");
		final DealJson dealJson = new DealJson();
		sabmDealTitlePopulator.populate(Lists.newArrayList(noComplexDeal), dealJson);
		Assert.assertEquals(true, StringUtils.isNotEmpty(dealJson.getTitle()));
	}


	/**
	 *
	 * Buy a minimum of 10 cases of any products in the Matilda Bay range and 8 cases in the Miller
	 * range(isAcrossCondition is false)
	 */
	@Test
	public void testTitlePopulate2()
	{
		// Buy a minimum of 10 cases of Carlton Mid 4x6 375ml bottles
		final DealModel noComplexDeal = mock(DealModel.class);
		given(noComplexDeal.getDealType()).willReturn(DealTypeEnum.COMPLEX);
		final String productCode = "productTestCode";
		final String brand = "testBrand";
		final String sellingName = "product name";
		final String packConfiguration = "1*20 boote";


		final SABMAlcoholVariantProductEANModel eanProduct = mock(SABMAlcoholVariantProductEANModel.class);
		final SABMAlcoholProductModel baseProduct = mock(SABMAlcoholProductModel.class);
		given(baseProduct.getBrand()).willReturn(brand);
		given(baseProduct.getLevel2()).willReturn("A45");
		given(eanProduct.getBaseProduct()).willReturn(baseProduct);
		given(eanProduct.getBaseProduct()).willReturn(eanProduct);
		given(eanProduct.getSellingName()).willReturn(sellingName);
		given(eanProduct.getPackConfiguration()).willReturn(packConfiguration);
		given(eanProduct.getMinOrderQuantity()).willReturn(2);

		final UnitModel unitmodel = mock(UnitModel.class);
		given(unitmodel.getName()).willReturn("case");
		given(unitmodel.getPluralName()).willReturn("cases");
		given(eanProduct.getUnit()).willReturn(unitmodel);

		final SABMAlcoholVariantProductMaterialModel materialProduct = mock(SABMAlcoholVariantProductMaterialModel.class);

		given(materialProduct.getBaseProduct()).willReturn(eanProduct);
		given(productService.getProductForCode(productCode)).willReturn(materialProduct);
		Mockito.when(productService.getProductForCodeSafe(productCode)).thenReturn(materialProduct);

		final DealConditionGroupModel dealConditionGroupModel1 = mock(DealConditionGroupModel.class);
		final List<AbstractDealConditionModel> dealConditionModels1 = new ArrayList<>();
		final ComplexDealConditionModel complexDealConditions = mock(ComplexDealConditionModel.class);
		given(complexDealConditions.getQuantity()).willReturn(Integer.valueOf(10));
		given(complexDealConditions.getBrand()).willReturn("A45");
		given(complexDealConditions.getUnit()).willReturn(unitmodel);

		dealConditionModels1.add(complexDealConditions);

		final List<AbstractDealBenefitModel> dealBenefitModels1 = new ArrayList<>();
		final FreeGoodsDealBenefitModel freeGoodsDealBenefitModel1 = mock(FreeGoodsDealBenefitModel.class);
		final FreeGoodsDealBenefitModel freeGoodsDealBenefitModel2 = mock(FreeGoodsDealBenefitModel.class);
		given(freeGoodsDealBenefitModel1.getProductCode()).willReturn(productCode);
		given(freeGoodsDealBenefitModel1.getQuantity()).willReturn(Integer.valueOf(5));
		given(freeGoodsDealBenefitModel2.getQuantity()).willReturn(Integer.valueOf(15));
		given(freeGoodsDealBenefitModel2.getProductCode()).willReturn(productCode);
		dealBenefitModels1.add(freeGoodsDealBenefitModel1);
		dealBenefitModels1.add(freeGoodsDealBenefitModel2);
		given(dealConditionGroupModel1.getDealConditions()).willReturn(dealConditionModels1);
		given(dealConditionGroupModel1.getDealBenefits()).willReturn(dealBenefitModels1);
		given(dealConditionGroupModel1.getMultipleScales()).willReturn(false);
		given(noComplexDeal.getConditionGroup()).willReturn(dealConditionGroupModel1);
		given(dealsService.isMultiRange(dealConditionModels1)).willReturn(false);
		final DealJson dealJson = new DealJson();
		final Locale locale = Locale.ENGLISH;
		Mockito.when(messageSource.getMessage("text.deal.title.buy", null, locale)).thenReturn("Buy&ensp;");
		sabmDealTitlePopulator.populate(Lists.newArrayList(noComplexDeal), dealJson);
		Assert.assertEquals(true, StringUtils.isNotEmpty(dealJson.getTitle()));
	}

	/**
	 *
	 * Buy a minimum of 10 cases across Carlton Mid 4x6 375ml bottles and Carlton Draught 4x6x375ml bottles
	 * (isAcrossCondition is true)
	 */
	@Test
	public void testTitlePopulate3()
	{
		// Buy a minimum of 10 cases of Carlton Mid 4x6 375ml bottles
		final DealModel noComplexDeal = mock(DealModel.class);
		given(noComplexDeal.getDealType()).willReturn(DealTypeEnum.COMPLEX);
		final String productCode = "productTestCode";
		final String brand = "testBrand";
		final String sellingName = "product name";
		final String packConfiguration = "1*20 boote";


		final SABMAlcoholVariantProductEANModel eanProduct = mock(SABMAlcoholVariantProductEANModel.class);
		final SABMAlcoholProductModel baseProduct = mock(SABMAlcoholProductModel.class);
		given(baseProduct.getBrand()).willReturn(brand);
		given(baseProduct.getLevel2()).willReturn("A45");
		given(eanProduct.getBaseProduct()).willReturn(baseProduct);
		given(eanProduct.getBaseProduct()).willReturn(eanProduct);
		given(eanProduct.getSellingName()).willReturn(sellingName);
		given(eanProduct.getPackConfiguration()).willReturn(packConfiguration);
		given(eanProduct.getMinOrderQuantity()).willReturn(2);

		final UnitModel unitmodel = mock(UnitModel.class);
		given(unitmodel.getName()).willReturn("case");
		given(unitmodel.getPluralName()).willReturn("cases");
		given(eanProduct.getUnit()).willReturn(unitmodel);

		final SABMAlcoholVariantProductMaterialModel materialProduct = mock(SABMAlcoholVariantProductMaterialModel.class);

		given(materialProduct.getBaseProduct()).willReturn(eanProduct);
		given(productService.getProductForCode(productCode)).willReturn(materialProduct);
		Mockito.when(productService.getProductForCodeSafe(productCode)).thenReturn(materialProduct);

		final DealConditionGroupModel dealConditionGroupModel1 = mock(DealConditionGroupModel.class);
		final List<AbstractDealConditionModel> dealConditionModels1 = new ArrayList<>();
		final ComplexDealConditionModel complexDealConditions = mock(ComplexDealConditionModel.class);
		given(complexDealConditions.getQuantity()).willReturn(Integer.valueOf(10));
		given(complexDealConditions.getBrand()).willReturn("A45");
		given(complexDealConditions.getUnit()).willReturn(unitmodel);

		dealConditionModels1.add(complexDealConditions);

		final List<AbstractDealBenefitModel> dealBenefitModels1 = new ArrayList<>();
		final FreeGoodsDealBenefitModel freeGoodsDealBenefitModel1 = mock(FreeGoodsDealBenefitModel.class);
		final FreeGoodsDealBenefitModel freeGoodsDealBenefitModel2 = mock(FreeGoodsDealBenefitModel.class);
		given(freeGoodsDealBenefitModel1.getProductCode()).willReturn(productCode);
		given(freeGoodsDealBenefitModel1.getQuantity()).willReturn(Integer.valueOf(5));
		given(freeGoodsDealBenefitModel2.getQuantity()).willReturn(Integer.valueOf(15));
		given(freeGoodsDealBenefitModel2.getProductCode()).willReturn(productCode);
		dealBenefitModels1.add(freeGoodsDealBenefitModel1);
		dealBenefitModels1.add(freeGoodsDealBenefitModel2);
		given(dealConditionGroupModel1.getDealConditions()).willReturn(dealConditionModels1);
		given(dealConditionGroupModel1.getDealBenefits()).willReturn(dealBenefitModels1);
		given(dealConditionGroupModel1.getMultipleScales()).willReturn(true);
		given(noComplexDeal.getConditionGroup()).willReturn(dealConditionGroupModel1);
		given(dealsService.isMultiRange(dealConditionModels1)).willReturn(false);
		final DealJson dealJson = new DealJson();
		final Locale locale = Locale.ENGLISH;
		Mockito.when(messageSource.getMessage("text.deal.title.buy", null, locale)).thenReturn("Buy&ensp;");
		sabmDealTitlePopulator.populate(Lists.newArrayList(noComplexDeal), dealJson);

		System.out.println(dealJson.getTitle());
		Assert.assertEquals(true, StringUtils.isNotEmpty(dealJson.getTitle()));
	}


	/**
	 *
	 * Buy a minimum of 10 cases of Carlton Mid 4x6 375ml bottles to receive (outcome) or Buy a minimum of 20 cases of
	 * Carlton Mid 4x6 375ml bottles to receive (outcome)
	 */
	@Test
	public void testTitlePopulate4()
	{
		// Buy a minimum of 10 cases of Carlton Mid 4x6 375ml bottles
		final DealModel noComplexDeal = mock(DealModel.class);
		given(noComplexDeal.getDealType()).willReturn(DealTypeEnum.COMPLEX);
		final String productCode = "productTestCode";
		final String brand = "testBrand";
		final String sellingName = "product name";
		final String packConfiguration = "1*20 boote";


		final SABMAlcoholVariantProductEANModel eanProduct = mock(SABMAlcoholVariantProductEANModel.class);
		final SABMAlcoholProductModel baseProduct = mock(SABMAlcoholProductModel.class);
		given(baseProduct.getBrand()).willReturn(brand);
		given(baseProduct.getLevel2()).willReturn("A45");
		given(eanProduct.getBaseProduct()).willReturn(baseProduct);
		given(eanProduct.getBaseProduct()).willReturn(eanProduct);
		given(eanProduct.getSellingName()).willReturn(sellingName);
		given(eanProduct.getPackConfiguration()).willReturn(packConfiguration);
		given(eanProduct.getMinOrderQuantity()).willReturn(2);

		final UnitModel unitmodel = mock(UnitModel.class);
		given(unitmodel.getName()).willReturn("case");
		given(unitmodel.getPluralName()).willReturn("cases");
		given(eanProduct.getUnit()).willReturn(unitmodel);

		final SABMAlcoholVariantProductMaterialModel materialProduct = mock(SABMAlcoholVariantProductMaterialModel.class);

		given(materialProduct.getBaseProduct()).willReturn(eanProduct);
		given(productService.getProductForCode(productCode)).willReturn(materialProduct);
		Mockito.when(productService.getProductForCodeSafe(productCode)).thenReturn(materialProduct);

		final DealConditionGroupModel dealConditionGroupModel1 = mock(DealConditionGroupModel.class);
		final List<AbstractDealConditionModel> dealConditionModels1 = new ArrayList<>();
		final ComplexDealConditionModel complexDealConditions = mock(ComplexDealConditionModel.class);
		given(complexDealConditions.getQuantity()).willReturn(Integer.valueOf(10));
		given(complexDealConditions.getBrand()).willReturn("A45");
		given(complexDealConditions.getUnit()).willReturn(unitmodel);

		dealConditionModels1.add(complexDealConditions);

		final List<AbstractDealBenefitModel> dealBenefitModels1 = new ArrayList<>();
		final FreeGoodsDealBenefitModel freeGoodsDealBenefitModel1 = mock(FreeGoodsDealBenefitModel.class);
		final FreeGoodsDealBenefitModel freeGoodsDealBenefitModel2 = mock(FreeGoodsDealBenefitModel.class);
		given(freeGoodsDealBenefitModel1.getProductCode()).willReturn(productCode);
		given(freeGoodsDealBenefitModel1.getQuantity()).willReturn(Integer.valueOf(5));
		given(freeGoodsDealBenefitModel2.getQuantity()).willReturn(Integer.valueOf(15));
		given(freeGoodsDealBenefitModel2.getProductCode()).willReturn(productCode);
		dealBenefitModels1.add(freeGoodsDealBenefitModel1);
		dealBenefitModels1.add(freeGoodsDealBenefitModel2);
		given(dealConditionGroupModel1.getDealConditions()).willReturn(dealConditionModels1);
		given(dealConditionGroupModel1.getDealBenefits()).willReturn(dealBenefitModels1);
		given(dealConditionGroupModel1.getMultipleScales()).willReturn(true);
		final List<Integer> scales = new ArrayList<Integer>();
		scales.add(1);
		scales.add(2);
		given(dealConditionGroupModel1.getScales()).willReturn(scales);
		given(noComplexDeal.getConditionGroup()).willReturn(dealConditionGroupModel1);
		given(dealsService.isMultiRange(dealConditionModels1)).willReturn(false);
		final DealJson dealJson = new DealJson();
		final Locale locale = Locale.ENGLISH;
		Mockito.when(messageSource.getMessage("text.deal.title.buy", null, locale)).thenReturn("Buy&ensp;");
		sabmDealTitlePopulator.populate(Lists.newArrayList(noComplexDeal), dealJson);
		Assert.assertEquals(true, StringUtils.isNotEmpty(dealJson.getTitle()));
	}

	/**
	 *
	 * Buy a minimum of 10 cases of any products across the Matilda Bay and Miller ranges except Lazy Yak 4x6 375ml
	 * bottles or Buy a minimum of 20 cases of any products across the Matilda Bay and Miller ranges except Lazy Yak 4x6
	 * 375ml bottles
	 */
	@Test
	public void testTitlePopulate5()
	{
		// Buy a minimum of 10 cases of Carlton Mid 4x6 375ml bottles
		final DealModel noComplexDeal = mock(DealModel.class);
		given(noComplexDeal.getDealType()).willReturn(DealTypeEnum.COMPLEX);
		final String productCode = "productTestCode";
		final String brand = "testBrand";
		final String sellingName = "product name";
		final String packConfiguration = "1*20 boote";


		final SABMAlcoholVariantProductEANModel eanProduct = mock(SABMAlcoholVariantProductEANModel.class);
		final SABMAlcoholProductModel baseProduct = mock(SABMAlcoholProductModel.class);
		given(baseProduct.getBrand()).willReturn(brand);
		given(baseProduct.getLevel2()).willReturn("A45");
		given(eanProduct.getBaseProduct()).willReturn(baseProduct);
		given(eanProduct.getBaseProduct()).willReturn(eanProduct);
		given(eanProduct.getSellingName()).willReturn(sellingName);
		given(eanProduct.getPackConfiguration()).willReturn(packConfiguration);
		given(eanProduct.getMinOrderQuantity()).willReturn(2);

		final UnitModel unitmodel = mock(UnitModel.class);
		given(unitmodel.getName()).willReturn("case");
		given(unitmodel.getPluralName()).willReturn("cases");
		given(eanProduct.getUnit()).willReturn(unitmodel);

		final SABMAlcoholVariantProductMaterialModel materialProduct = mock(SABMAlcoholVariantProductMaterialModel.class);

		given(materialProduct.getBaseProduct()).willReturn(eanProduct);
		given(productService.getProductForCode(productCode)).willReturn(materialProduct);
		Mockito.when(productService.getProductForCodeSafe(productCode)).thenReturn(materialProduct);

		final ProductDealConditionModel productDealConditionModel2 = mock(ProductDealConditionModel.class);
		given(productDealConditionModel2.getProductCode()).willReturn(productCode);
		given(productDealConditionModel2.getQuantity()).willReturn(Integer.valueOf(5));
		given(productDealConditionModel2.getMinQty()).willReturn(2);
		given(productDealConditionModel2.getExclude()).willReturn(true);
		final DealConditionGroupModel dealConditionGroupModel1 = mock(DealConditionGroupModel.class);
		final List<AbstractDealConditionModel> dealConditionModels1 = new ArrayList<>();
		final ComplexDealConditionModel complexDealConditions = mock(ComplexDealConditionModel.class);
		given(complexDealConditions.getQuantity()).willReturn(Integer.valueOf(10));
		given(complexDealConditions.getBrand()).willReturn("A45");
		given(complexDealConditions.getUnit()).willReturn(unitmodel);

		dealConditionModels1.add(complexDealConditions);

		final List<AbstractDealBenefitModel> dealBenefitModels1 = new ArrayList<>();
		final FreeGoodsDealBenefitModel freeGoodsDealBenefitModel1 = mock(FreeGoodsDealBenefitModel.class);
		final FreeGoodsDealBenefitModel freeGoodsDealBenefitModel2 = mock(FreeGoodsDealBenefitModel.class);
		given(freeGoodsDealBenefitModel1.getProductCode()).willReturn(productCode);
		given(freeGoodsDealBenefitModel1.getQuantity()).willReturn(Integer.valueOf(5));
		given(freeGoodsDealBenefitModel2.getQuantity()).willReturn(Integer.valueOf(15));
		given(freeGoodsDealBenefitModel2.getProductCode()).willReturn(productCode);
		dealBenefitModels1.add(freeGoodsDealBenefitModel1);
		dealBenefitModels1.add(freeGoodsDealBenefitModel2);
		given(dealConditionGroupModel1.getDealConditions()).willReturn(dealConditionModels1);
		given(dealConditionGroupModel1.getDealBenefits()).willReturn(dealBenefitModels1);
		given(dealConditionGroupModel1.getMultipleScales()).willReturn(true);
		final List<Integer> scales = new ArrayList<Integer>();
		scales.add(1);
		scales.add(2);
		given(dealConditionGroupModel1.getScales()).willReturn(scales);
		given(noComplexDeal.getConditionGroup()).willReturn(dealConditionGroupModel1);
		given(dealsService.isMultiRange(dealConditionModels1)).willReturn(false);
		final DealJson dealJson = new DealJson();
		final Locale locale = Locale.ENGLISH;
		Mockito.when(messageSource.getMessage("text.deal.title.buy", null, locale)).thenReturn("Buy&ensp;");
		sabmDealTitlePopulator.populate(Lists.newArrayList(noComplexDeal), dealJson);
		Assert.assertEquals(true, StringUtils.isNotEmpty(dealJson.getTitle()));
	}

	/**
	 *
	 * Buy a minimum of 10 cases of any products across the Matilda Bay, Miller and Carlton ranges
	 *
	 */
	@Test
	public void testTitlePopulate6()
	{
		// Buy a minimum of 10 cases of Carlton Mid 4x6 375ml bottles
		final DealModel noComplexDeal = mock(DealModel.class);
		given(noComplexDeal.getDealType()).willReturn(DealTypeEnum.COMPLEX);
		final String productCode = "productTestCode";
		final String brand = "testBrand";
		final String sellingName = "product name";
		final String packConfiguration = "1*20 boote";


		final SABMAlcoholVariantProductEANModel eanProduct = mock(SABMAlcoholVariantProductEANModel.class);
		final SABMAlcoholProductModel baseProduct = mock(SABMAlcoholProductModel.class);
		given(baseProduct.getBrand()).willReturn(brand);
		given(baseProduct.getLevel2()).willReturn("A45");
		given(eanProduct.getBaseProduct()).willReturn(baseProduct);
		given(eanProduct.getBaseProduct()).willReturn(eanProduct);
		given(eanProduct.getSellingName()).willReturn(sellingName);
		given(eanProduct.getPackConfiguration()).willReturn(packConfiguration);
		given(eanProduct.getMinOrderQuantity()).willReturn(2);

		final UnitModel unitmodel = mock(UnitModel.class);
		given(unitmodel.getName()).willReturn("case");
		given(unitmodel.getPluralName()).willReturn("cases");
		given(eanProduct.getUnit()).willReturn(unitmodel);

		final SABMAlcoholVariantProductMaterialModel materialProduct = mock(SABMAlcoholVariantProductMaterialModel.class);

		given(materialProduct.getBaseProduct()).willReturn(eanProduct);
		given(productService.getProductForCode(productCode)).willReturn(materialProduct);
		Mockito.when(productService.getProductForCodeSafe(productCode)).thenReturn(materialProduct);

		final DealConditionGroupModel dealConditionGroupModel1 = mock(DealConditionGroupModel.class);
		final List<AbstractDealConditionModel> dealConditionModels1 = new ArrayList<>();
		final ComplexDealConditionModel complexDealConditions = mock(ComplexDealConditionModel.class);
		given(complexDealConditions.getQuantity()).willReturn(Integer.valueOf(10));
		given(complexDealConditions.getBrand()).willReturn("A45");
		given(complexDealConditions.getUnit()).willReturn(unitmodel);

		dealConditionModels1.add(complexDealConditions);

		final List<AbstractDealBenefitModel> dealBenefitModels1 = new ArrayList<>();
		final FreeGoodsDealBenefitModel freeGoodsDealBenefitModel1 = mock(FreeGoodsDealBenefitModel.class);
		final FreeGoodsDealBenefitModel freeGoodsDealBenefitModel2 = mock(FreeGoodsDealBenefitModel.class);
		given(freeGoodsDealBenefitModel1.getProductCode()).willReturn(productCode);
		given(freeGoodsDealBenefitModel1.getQuantity()).willReturn(Integer.valueOf(5));
		given(freeGoodsDealBenefitModel2.getQuantity()).willReturn(Integer.valueOf(15));
		given(freeGoodsDealBenefitModel2.getProductCode()).willReturn(productCode);
		dealBenefitModels1.add(freeGoodsDealBenefitModel1);
		dealBenefitModels1.add(freeGoodsDealBenefitModel2);
		given(dealConditionGroupModel1.getDealConditions()).willReturn(dealConditionModels1);
		given(dealConditionGroupModel1.getDealBenefits()).willReturn(dealBenefitModels1);
		given(dealConditionGroupModel1.getMultipleScales()).willReturn(true);
		final List<Integer> scales = new ArrayList<Integer>();
		scales.add(1);
		given(dealConditionGroupModel1.getScales()).willReturn(scales);
		given(noComplexDeal.getConditionGroup()).willReturn(dealConditionGroupModel1);
		given(dealsService.isMultiRange(dealConditionModels1)).willReturn(false);
		final DealJson dealJson = new DealJson();
		Mockito.when(messageSource.getMessage("text.deal.title.buy", null, locale)).thenReturn("Buy&ensp;");
		sabmDealTitlePopulator.populate(Lists.newArrayList(noComplexDeal), dealJson);
		Assert.assertEquals(true, StringUtils.isNotEmpty(dealJson.getTitle()));
	}

	/**
	 *
	 * to receive either 1 free case of Carlton Dry 4x6x355ml bottles or 1 free case of Lazy Yak 4x6 375ml bottles
	 */
	@Test
	public void testTitlePopulate7()
	{
		// Buy a minimum of 10 cases of Carlton Mid 4x6 375ml bottles
		final DealModel noComplexDeal = mock(DealModel.class);
		given(noComplexDeal.getDealType()).willReturn(DealTypeEnum.COMPLEX);
		given(noComplexDeal.getTriggerHash()).willReturn("test123");
		final String productCode = "productTestCode";
		final String brand = "testBrand";
		final String sellingName = "product name";
		final String packConfiguration = "1*20 boote";


		final SABMAlcoholVariantProductEANModel eanProduct = mock(SABMAlcoholVariantProductEANModel.class);
		final SABMAlcoholProductModel baseProduct = mock(SABMAlcoholProductModel.class);
		given(baseProduct.getBrand()).willReturn(brand);
		given(baseProduct.getLevel2()).willReturn("A45");
		given(eanProduct.getBaseProduct()).willReturn(baseProduct);
		given(eanProduct.getBaseProduct()).willReturn(eanProduct);
		given(eanProduct.getSellingName()).willReturn(sellingName);
		given(eanProduct.getPackConfiguration()).willReturn(packConfiguration);
		given(eanProduct.getMinOrderQuantity()).willReturn(2);

		final UnitModel unitmodel = mock(UnitModel.class);
		given(unitmodel.getName()).willReturn("case");
		given(unitmodel.getPluralName()).willReturn("cases");
		given(eanProduct.getUnit()).willReturn(unitmodel);

		final SABMAlcoholVariantProductMaterialModel materialProduct = mock(SABMAlcoholVariantProductMaterialModel.class);

		given(materialProduct.getBaseProduct()).willReturn(eanProduct);
		given(productService.getProductForCode(productCode)).willReturn(materialProduct);
		Mockito.when(productService.getProductForCodeSafe(productCode)).thenReturn(materialProduct);

		final DealConditionGroupModel dealConditionGroupModel1 = mock(DealConditionGroupModel.class);
		final List<AbstractDealConditionModel> dealConditionModels1 = new ArrayList<>();
		final ComplexDealConditionModel complexDealConditions = mock(ComplexDealConditionModel.class);
		given(complexDealConditions.getQuantity()).willReturn(Integer.valueOf(10));
		given(complexDealConditions.getBrand()).willReturn("A45");
		given(complexDealConditions.getUnit()).willReturn(unitmodel);

		dealConditionModels1.add(complexDealConditions);

		final List<AbstractDealBenefitModel> dealBenefitModels1 = new ArrayList<>();
		final FreeGoodsDealBenefitModel freeGoodsDealBenefitModel1 = mock(FreeGoodsDealBenefitModel.class);
		final FreeGoodsDealBenefitModel freeGoodsDealBenefitModel2 = mock(FreeGoodsDealBenefitModel.class);
		given(freeGoodsDealBenefitModel1.getProductCode()).willReturn(productCode);
		given(freeGoodsDealBenefitModel1.getQuantity()).willReturn(Integer.valueOf(5));
		given(freeGoodsDealBenefitModel2.getQuantity()).willReturn(Integer.valueOf(15));
		given(freeGoodsDealBenefitModel2.getProductCode()).willReturn(productCode);
		dealBenefitModels1.add(freeGoodsDealBenefitModel1);
		dealBenefitModels1.add(freeGoodsDealBenefitModel2);
		given(dealConditionGroupModel1.getDealConditions()).willReturn(dealConditionModels1);
		given(dealConditionGroupModel1.getDealBenefits()).willReturn(dealBenefitModels1);
		given(dealConditionGroupModel1.getMultipleScales()).willReturn(false);
		given(noComplexDeal.getConditionGroup()).willReturn(dealConditionGroupModel1);
		given(dealsService.isMultiRange(dealConditionModels1)).willReturn(false);
		final DealJson dealJson = new DealJson();
		Mockito.when(messageSource.getMessage("text.deal.title.buy", null, locale)).thenReturn("Buy&ensp;");
		sabmDealTitlePopulator.populate(Lists.newArrayList(noComplexDeal), dealJson);
		Assert.assertEquals(true, StringUtils.isNotEmpty(dealJson.getTitle()));
	}

	/**
	 * Buy a minimum of 10 cases of any products in the Matilda Bay range and 8 cases in the Miller range and a product
	 *
	 */
	@Test
	public void testTitlePopulate8()
	{
		// Buy a minimum of 10 cases of Carlton Mid 4x6 375ml bottles
		final DealModel noComplexDeal = mock(DealModel.class);
		given(noComplexDeal.getDealType()).willReturn(DealTypeEnum.COMPLEX);
		given(noComplexDeal.getTriggerHash()).willReturn("test123");
		final String productCode = "productTestCode";
		final String brand = "testBrand";
		final String sellingName = "product name";
		final String packConfiguration = "1*20 boote";


		final SABMAlcoholVariantProductEANModel eanProduct = mock(SABMAlcoholVariantProductEANModel.class);
		final SABMAlcoholProductModel baseProduct = mock(SABMAlcoholProductModel.class);
		given(baseProduct.getBrand()).willReturn(brand);
		given(baseProduct.getLevel2()).willReturn("A45");
		given(eanProduct.getBaseProduct()).willReturn(baseProduct);
		given(eanProduct.getBaseProduct()).willReturn(eanProduct);
		given(eanProduct.getSellingName()).willReturn(sellingName);
		given(eanProduct.getPackConfiguration()).willReturn(packConfiguration);
		given(eanProduct.getMinOrderQuantity()).willReturn(2);

		final UnitModel unitmodel = mock(UnitModel.class);
		given(unitmodel.getName()).willReturn("case");
		given(unitmodel.getPluralName()).willReturn("cases");
		given(eanProduct.getUnit()).willReturn(unitmodel);

		final SABMAlcoholVariantProductMaterialModel materialProduct = mock(SABMAlcoholVariantProductMaterialModel.class);

		given(materialProduct.getBaseProduct()).willReturn(eanProduct);
		given(productService.getProductForCode(productCode)).willReturn(materialProduct);
		Mockito.when(productService.getProductForCodeSafe(productCode)).thenReturn(materialProduct);

		final DealConditionGroupModel dealConditionGroupModel1 = mock(DealConditionGroupModel.class);
		final List<AbstractDealConditionModel> dealConditionModels1 = new ArrayList<>();
		final ComplexDealConditionModel complexDealConditions = mock(ComplexDealConditionModel.class);
		given(complexDealConditions.getQuantity()).willReturn(Integer.valueOf(10));
		given(complexDealConditions.getBrand()).willReturn("A45");
		given(complexDealConditions.getUnit()).willReturn(unitmodel);
		final ProductDealConditionModel productDealConditionModel2 = mock(ProductDealConditionModel.class);
		given(productDealConditionModel2.getProductCode()).willReturn(productCode);
		given(productDealConditionModel2.getQuantity()).willReturn(Integer.valueOf(5));
		given(productDealConditionModel2.getMinQty()).willReturn(2);
		dealConditionModels1.add(productDealConditionModel2);
		dealConditionModels1.add(complexDealConditions);

		final List<AbstractDealBenefitModel> dealBenefitModels1 = new ArrayList<>();
		final FreeGoodsDealBenefitModel freeGoodsDealBenefitModel1 = mock(FreeGoodsDealBenefitModel.class);
		final FreeGoodsDealBenefitModel freeGoodsDealBenefitModel2 = mock(FreeGoodsDealBenefitModel.class);
		given(freeGoodsDealBenefitModel1.getProductCode()).willReturn(productCode);
		given(freeGoodsDealBenefitModel1.getQuantity()).willReturn(Integer.valueOf(5));
		given(freeGoodsDealBenefitModel2.getQuantity()).willReturn(Integer.valueOf(15));
		given(freeGoodsDealBenefitModel2.getProductCode()).willReturn(productCode);
		dealBenefitModels1.add(freeGoodsDealBenefitModel1);
		dealBenefitModels1.add(freeGoodsDealBenefitModel2);
		given(dealConditionGroupModel1.getDealConditions()).willReturn(dealConditionModels1);
		given(dealConditionGroupModel1.getDealBenefits()).willReturn(dealBenefitModels1);
		given(dealConditionGroupModel1.getMultipleScales()).willReturn(false);
		given(noComplexDeal.getConditionGroup()).willReturn(dealConditionGroupModel1);
		given(dealsService.isMultiRange(dealConditionModels1)).willReturn(false);
		final DealJson dealJson = new DealJson();
		Mockito.when(messageSource.getMessage("text.deal.title.buy", null, locale)).thenReturn("Buy&ensp;");
		sabmDealTitlePopulator.populate(Lists.newArrayList(noComplexDeal), dealJson);
		Assert.assertEquals(true, StringUtils.isNotEmpty(dealJson.getTitle()));
	}

	/**
	 * Buy a minimum of 10 cases of any products in the Matilda Bay range and 8 cases in the Miller range
	 *
	 */
	@Test
	public void testTitlePopulate9()
	{
		// Buy a minimum of 10 cases of Carlton Mid 4x6 375ml bottles
		final DealModel noComplexDeal = mock(DealModel.class);
		given(noComplexDeal.getDealType()).willReturn(DealTypeEnum.COMPLEX);
		given(noComplexDeal.getTriggerHash()).willReturn("test123");
		final String productCode = "productTestCode";
		final String brand = "testBrand";
		final String sellingName = "product name";
		final String packConfiguration = "1*20 boote";


		final SABMAlcoholVariantProductEANModel eanProduct = mock(SABMAlcoholVariantProductEANModel.class);
		final SABMAlcoholProductModel baseProduct = mock(SABMAlcoholProductModel.class);
		given(baseProduct.getBrand()).willReturn(brand);
		given(baseProduct.getLevel2()).willReturn("A45");
		given(eanProduct.getBaseProduct()).willReturn(baseProduct);
		given(eanProduct.getBaseProduct()).willReturn(eanProduct);
		given(eanProduct.getSellingName()).willReturn(sellingName);
		given(eanProduct.getPackConfiguration()).willReturn(packConfiguration);
		given(eanProduct.getMinOrderQuantity()).willReturn(2);

		final UnitModel unitmodel = mock(UnitModel.class);
		given(unitmodel.getName()).willReturn("case");
		given(unitmodel.getPluralName()).willReturn("cases");
		given(eanProduct.getUnit()).willReturn(unitmodel);

		final SABMAlcoholVariantProductMaterialModel materialProduct = mock(SABMAlcoholVariantProductMaterialModel.class);

		given(materialProduct.getBaseProduct()).willReturn(eanProduct);
		given(productService.getProductForCode(productCode)).willReturn(materialProduct);
		Mockito.when(productService.getProductForCodeSafe(productCode)).thenReturn(materialProduct);

		final DealConditionGroupModel dealConditionGroupModel1 = mock(DealConditionGroupModel.class);
		final List<AbstractDealConditionModel> dealConditionModels1 = new ArrayList<>();
		final ComplexDealConditionModel complexDealConditions = mock(ComplexDealConditionModel.class);
		given(complexDealConditions.getQuantity()).willReturn(Integer.valueOf(10));
		given(complexDealConditions.getBrand()).willReturn("A45");
		given(complexDealConditions.getUnit()).willReturn(unitmodel);
		given(complexDealConditions.getEmpties()).willReturn("O98");
		dealConditionModels1.add(complexDealConditions);

		final List<AbstractDealBenefitModel> dealBenefitModels1 = new ArrayList<>();
		final FreeGoodsDealBenefitModel freeGoodsDealBenefitModel1 = mock(FreeGoodsDealBenefitModel.class);
		final FreeGoodsDealBenefitModel freeGoodsDealBenefitModel2 = mock(FreeGoodsDealBenefitModel.class);
		given(freeGoodsDealBenefitModel1.getProductCode()).willReturn(productCode);
		given(freeGoodsDealBenefitModel1.getQuantity()).willReturn(Integer.valueOf(5));
		given(freeGoodsDealBenefitModel2.getQuantity()).willReturn(Integer.valueOf(15));
		given(freeGoodsDealBenefitModel2.getProductCode()).willReturn(productCode);
		dealBenefitModels1.add(freeGoodsDealBenefitModel1);
		dealBenefitModels1.add(freeGoodsDealBenefitModel2);
		given(dealConditionGroupModel1.getDealConditions()).willReturn(dealConditionModels1);
		given(dealConditionGroupModel1.getDealBenefits()).willReturn(dealBenefitModels1);
		given(dealConditionGroupModel1.getMultipleScales()).willReturn(false);
		given(noComplexDeal.getConditionGroup()).willReturn(dealConditionGroupModel1);
		given(dealsService.isMultiRange(dealConditionModels1)).willReturn(false);
		final DealJson dealJson = new DealJson();
		Mockito.when(messageSource.getMessage("text.deal.title.buy", null, locale)).thenReturn("Buy&ensp;");
		sabmDealTitlePopulator.populate(Lists.newArrayList(noComplexDeal), dealJson);
		Assert.assertEquals(true, StringUtils.isNotEmpty(dealJson.getTitle()));
	}
}
