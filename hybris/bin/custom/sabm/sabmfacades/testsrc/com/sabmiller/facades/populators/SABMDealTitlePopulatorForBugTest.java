/**
 *
 */
package com.sabmiller.facades.populators;

import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.mock;

import de.hybris.platform.core.model.c2l.CurrencyModel;
import de.hybris.platform.core.model.product.UnitModel;
import de.hybris.platform.servicelayer.i18n.CommonI18NService;
import de.hybris.platform.servicelayer.i18n.I18NService;

import java.math.BigDecimal;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import org.assertj.core.util.Arrays;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.context.MessageSource;

import com.google.common.collect.Lists;
import com.sabmiller.core.deals.services.DealsService;
import com.sabmiller.core.deals.strategies.SABMDiscountPerUnitCalculationStrategy;
import com.sabmiller.core.enums.DealTypeEnum;
import com.sabmiller.core.model.AbstractDealBenefitModel;
import com.sabmiller.core.model.AbstractDealConditionModel;
import com.sabmiller.core.model.ComplexDealConditionModel;
import com.sabmiller.core.model.DealConditionGroupModel;
import com.sabmiller.core.model.DealModel;
import com.sabmiller.core.model.DealScaleModel;
import com.sabmiller.core.model.DiscountDealBenefitModel;
import com.sabmiller.core.model.FreeGoodsDealBenefitModel;
import com.sabmiller.core.model.ProductDealConditionModel;
import com.sabmiller.core.model.SABMAlcoholProductModel;
import com.sabmiller.core.model.SABMAlcoholVariantProductEANModel;
import com.sabmiller.core.model.SABMAlcoholVariantProductMaterialModel;
import com.sabmiller.core.product.SabmProductService;
import com.sabmiller.facades.deal.data.DealJson;

import org.apache.commons.lang3.StringUtils;


/**
 * SABMDealTitlePopulatorForBugTest
 */
public class SABMDealTitlePopulatorForBugTest
{

	@InjectMocks
	private final SABMDealTitlePopulator sabmDealTitlePopulator = new SABMDealTitlePopulator();

	@Mock
	private SabmProductService productService;

	@Mock
	private I18NService i18nService;

	@Mock
	private CommonI18NService commonI18NService;

	@Mock
	private MessageSource messageSource;

	@Mock
	private DealsService dealsService;

	@Mock
	private SABMDiscountPerUnitCalculationStrategy discountPerUnitCalculationStrategy;

	@Before
	public void setUp()
	{
		MockitoAnnotations.initMocks(this);
	}

	/**
	 * buy 1 case product name 1*20 boote to receive 1 case
	 *
	 */
	@Test
	public void testTitlePopulate1()
	{
		final Locale locale = i18nService.getCurrentLocale();

		given(sabmDealTitlePopulator.getMessageSource().getMessage("text.deal.title.buy", null, locale)).willReturn("buy");


		final DealModel complexDeal = mock(DealModel.class);
		given(complexDeal.getDealType()).willReturn(DealTypeEnum.COMPLEX);
		final String productCode = "productTestCode";
		final String brand = "testBrand";
		final String sellingName = "product name";
		final String packConfiguration = "1*20 boote";


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
		given(productService.getProductForCodeSafe(productCode)).willReturn(materialProduct);

		final DealConditionGroupModel dealConditionGroupModel1 = mock(DealConditionGroupModel.class);
		final List<AbstractDealConditionModel> dealConditionModels1 = new ArrayList<>();
		final ProductDealConditionModel productDealConditionModel1 = mock(ProductDealConditionModel.class);
		given(productDealConditionModel1.getProductCode()).willReturn(productCode);
		given(productDealConditionModel1.getQuantity()).willReturn(Integer.valueOf(10));
		given(productDealConditionModel1.getUnit()).willReturn(unitmodel);
		given(productDealConditionModel1.getMinQty()).willReturn(1);
		dealConditionModels1.add(productDealConditionModel1);
		final List<AbstractDealBenefitModel> dealBenefitModels1 = new ArrayList<>();
		final FreeGoodsDealBenefitModel freeGoodsDealBenefitModel1 = mock(FreeGoodsDealBenefitModel.class);
		given(freeGoodsDealBenefitModel1.getProductCode()).willReturn(productCode);
		given(freeGoodsDealBenefitModel1.getQuantity()).willReturn(Integer.valueOf(1));
		given(freeGoodsDealBenefitModel1.getUnit()).willReturn(unitmodel);
		dealBenefitModels1.add(freeGoodsDealBenefitModel1);

		given(sabmDealTitlePopulator.getMessageSource().getMessage("text.deal.title.uom.product",
				Arrays.array("1", "case", "product name 1*20 boote"), locale)).willReturn(" 1 case product name 1*20 boote");

		given(sabmDealTitlePopulator.getMessageSource().getMessage("text.deal.title.receive", null, locale))
				.willReturn(" to receive");

		given(sabmDealTitlePopulator.getMessageSource().getMessage("text.deal.title.bonus.uom",
				Arrays.array("1", "case", "product name 1*20 boote"), locale)).willReturn(" 1 case");

		given(dealConditionGroupModel1.getDealConditions()).willReturn(dealConditionModels1);
		given(dealConditionGroupModel1.getDealBenefits()).willReturn(dealBenefitModels1);

		given(complexDeal.getConditionGroup()).willReturn(dealConditionGroupModel1);

		final DealJson dealJson = new DealJson();
		sabmDealTitlePopulator.populate(Lists.newArrayList(complexDeal), dealJson);

		Assert.assertEquals(true, StringUtils.isNotEmpty(dealJson.getTitle()));
	}

	/**
	 * buy 1 case product name 1*20 boote to receive 1% and 1 case
	 *
	 */
	@Test
	public void testTitlePopulate2()
	{
		final Locale locale = i18nService.getCurrentLocale();

		final CurrencyModel currency = mock(CurrencyModel.class);
		given(currency.getSymbol()).willReturn("ISO");
		given(commonI18NService.getCurrentCurrency()).willReturn(currency);



		given(sabmDealTitlePopulator.getMessageSource().getMessage("text.deal.title.buy", null, locale)).willReturn("buy");


		final DealModel complexDeal = mock(DealModel.class);
		given(complexDeal.getDealType()).willReturn(DealTypeEnum.COMPLEX);
		final String productCode = "productTestCode";
		final String brand = "testBrand";
		final String sellingName = "product name";
		final String packConfiguration = "1*20 boote";


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
		given(productService.getProductForCodeSafe(productCode)).willReturn(materialProduct);

		final DealConditionGroupModel dealConditionGroupModel1 = mock(DealConditionGroupModel.class);
		final List<AbstractDealConditionModel> dealConditionModels1 = new ArrayList<>();
		final ProductDealConditionModel productDealConditionModel1 = mock(ProductDealConditionModel.class);
		given(productDealConditionModel1.getProductCode()).willReturn(productCode);
		given(productDealConditionModel1.getQuantity()).willReturn(Integer.valueOf(10));
		given(productDealConditionModel1.getUnit()).willReturn(unitmodel);
		given(productDealConditionModel1.getMinQty()).willReturn(1);
		dealConditionModels1.add(productDealConditionModel1);
		final List<AbstractDealBenefitModel> dealBenefitModels1 = new ArrayList<>();
		final FreeGoodsDealBenefitModel freeGoodsDealBenefitModel1 = mock(FreeGoodsDealBenefitModel.class);
		given(freeGoodsDealBenefitModel1.getProductCode()).willReturn(productCode);
		given(freeGoodsDealBenefitModel1.getQuantity()).willReturn(Integer.valueOf(1));
		given(freeGoodsDealBenefitModel1.getUnit()).willReturn(unitmodel);

		final DiscountDealBenefitModel discountDealBenefitModel = mock(DiscountDealBenefitModel.class);
		given(discountDealBenefitModel.getCurrency()).willReturn(Boolean.valueOf(false));
		given(discountDealBenefitModel.getAmount()).willReturn(Double.valueOf(1));
		given(discountDealBenefitModel.getUnit()).willReturn(unitmodel);
		dealBenefitModels1.add(discountDealBenefitModel);
		dealBenefitModels1.add(freeGoodsDealBenefitModel1);


		given(sabmDealTitlePopulator.getMessageSource().getMessage("text.deal.title.uom.product",
				Arrays.array("1", "case", "product name 1*20 boote"), locale)).willReturn(" 1 case product name 1*20 boote");

		given(sabmDealTitlePopulator.getMessageSource().getMessage("text.deal.title.receive", null, locale))
				.willReturn(" to receive ");

		given(sabmDealTitlePopulator.getMessageSource().getMessage("text.deal.title.bonus.uom",
				Arrays.array("1", "case", "product name 1*20 boote"), locale)).willReturn(" 1 case");


		given(sabmDealTitlePopulator.getMessageSource().getMessage("text.deal.title.conjunction", null, locale))
				.willReturn(" and ");

		given(sabmDealTitlePopulator.getMessageSource().getMessage("text.deal.title.disc", Arrays.array("ISO" + "1", "case"),
				locale)).willReturn(" 1% ");

		given(discountPerUnitCalculationStrategy.calculateDiscountPerUnit("productTestCode", 1d)).willReturn(BigDecimal.valueOf(1));

		given(dealConditionGroupModel1.getDealConditions()).willReturn(dealConditionModels1);
		given(dealConditionGroupModel1.getDealBenefits()).willReturn(dealBenefitModels1);

		given(complexDeal.getConditionGroup()).willReturn(dealConditionGroupModel1);

		final DealJson dealJson = new DealJson();
		sabmDealTitlePopulator.populate(Lists.newArrayList(complexDeal), dealJson);

		Assert.assertEquals(true, StringUtils.isNotEmpty(dealJson.getTitle()));

	}

	/**
	 * ,up to the total discount value of $150
	 *
	 * up to a maximum of 100 cases
	 */
	@Test
	public void testPopulateMaxTimesTitleSuffix()
	{
		final CurrencyModel currency = mock(CurrencyModel.class);
		final DealModel limitedDeal1 = mock(DealModel.class);
		final Locale locale = i18nService.getCurrentLocale();
		final Map<String, String> mapValues = new HashMap<>();

		mapValues.put("uom", "case");
		mapValues.put("uoms", "cases");
		given(currency.getSymbol()).willReturn("$");
		given(commonI18NService.getCurrentCurrency()).willReturn(currency);

		final DealConditionGroupModel conditionGroupModel = mock(DealConditionGroupModel.class);
		given(limitedDeal1.getConditionGroup()).willReturn(conditionGroupModel);
		given(limitedDeal1.getDealType()).willReturn(DealTypeEnum.LIMITED);
		given(limitedDeal1.getMaxConditionValue()).willReturn(Double.valueOf(150));
		given(limitedDeal1.getMaxConditionBaseValue()).willReturn(Double.valueOf(0));
		given(limitedDeal1.getUsedConditionBaseValue()).willReturn(Double.valueOf(0));
		given(limitedDeal1.getUsedConditionValue()).willReturn(Double.valueOf(0));

		given(sabmDealTitlePopulator.getMessageSource().getMessage("text.deal.title.suffix.limited.discount",
				Arrays.array("$" + 150), locale)).willReturn(",up to the total discount value of $150");

		//	final String result1 = sabmDealTitlePopulator.populateTitleSuffix(limitedDeal1, locale, mapValues);
		//	Assert.assertEquals(",up to the total discount value of $150", result1);

		final DealModel limitedDeal2 = mock(DealModel.class);

		given(limitedDeal2.getConditionGroup()).willReturn(conditionGroupModel);
		given(limitedDeal2.getDealType()).willReturn(DealTypeEnum.LIMITED);
		given(limitedDeal2.getMaxConditionValue()).willReturn(Double.valueOf(0));
		given(limitedDeal2.getMaxConditionBaseValue()).willReturn(Double.valueOf(150));
		given(limitedDeal2.getUsedConditionBaseValue()).willReturn(Double.valueOf(0));
		given(limitedDeal2.getUsedConditionValue()).willReturn(Double.valueOf(0));

		given(sabmDealTitlePopulator.getMessageSource().getMessage("text.deal.title.suffix.limited", Arrays.array("150", "cases"),
				locale)).willReturn("up to a maximum of 100 cases");

		//	final String result2 = sabmDealTitlePopulator.populateTitleSuffix(limitedDeal2, locale, mapValues);
		//	Assert.assertEquals("up to a maximum of 100 cases", result2);
	}


	@Test
	public void testPopulateMaxTimesTitleSuffix1()
	{
		final CurrencyModel currency = mock(CurrencyModel.class);
		final List<DealModel> deals = new ArrayList<>();
		final DealModel discountDeal = mock(DealModel.class);
		deals.add(discountDeal);
		final Locale locale = i18nService.getCurrentLocale();
		final Map<String, String> mapValues = new HashMap<>();

		mapValues.put("uom", "case");
		mapValues.put("uoms", "cases");
		given(currency.getSymbol()).willReturn("$");
		given(commonI18NService.getCurrentCurrency()).willReturn(currency);

		final DealConditionGroupModel conditionGroupModel = mock(DealConditionGroupModel.class);
		given(discountDeal.getConditionGroup()).willReturn(conditionGroupModel);
		given(discountDeal.getDealType()).willReturn(DealTypeEnum.DISCOUNT);



		final String result1 = sabmDealTitlePopulator.populateTitleSuffix(deals, locale, mapValues);
		Assert.assertEquals("", result1);


	}


	@Test
	public void testMultiScaleDealConditionTitle()
	{
		final Locale locale = i18nService.getCurrentLocale();

		given(sabmDealTitlePopulator.getMessageSource().getMessage("text.deal.title.buy", null, locale)).willReturn("buy");


		final DealModel complexDeal = mock(DealModel.class);
		given(complexDeal.getDealType()).willReturn(DealTypeEnum.COMPLEX);
		final String productCode = "productTestCode";
		final String brand = "testBrand";
		final String brand1 = "test brand 2";
		final String sellingName = "product name";
		final String packConfiguration = "1*20 boote";


		final SABMAlcoholVariantProductEANModel eanProduct = mock(SABMAlcoholVariantProductEANModel.class);
		final SABMAlcoholProductModel baseProduct = mock(SABMAlcoholProductModel.class);
		given(baseProduct.getBrand()).willReturn(brand);
		given(eanProduct.getBaseProduct()).willReturn(baseProduct);
		given(eanProduct.getBaseProduct()).willReturn(eanProduct);
		given(eanProduct.getSellingName()).willReturn(sellingName);
		given(eanProduct.getPackConfiguration()).willReturn(packConfiguration);
		given(eanProduct.getMinOrderQuantity()).willReturn(2);

		final SABMAlcoholProductModel baseProduct1 = mock(SABMAlcoholProductModel.class);
		given(baseProduct1.getBrand()).willReturn(brand1);


		given(productService.getSABMAlcoholProduct("123")).willReturn(baseProduct);

		given(productService.getSABMAlcoholProduct("567")).willReturn(baseProduct1);



		final UnitModel unitmodel = mock(UnitModel.class);
		given(unitmodel.getName()).willReturn("case");
		given(unitmodel.getPluralName()).willReturn("cases");
		given(eanProduct.getUnit()).willReturn(unitmodel);

		final SABMAlcoholVariantProductMaterialModel materialProduct = mock(SABMAlcoholVariantProductMaterialModel.class);

		given(materialProduct.getBaseProduct()).willReturn(eanProduct);
		given(productService.getProductForCode(productCode)).willReturn(materialProduct);


		final DealConditionGroupModel dealConditionGroupModel1 = mock(DealConditionGroupModel.class);
		final DealScaleModel dealScale1 = mock(DealScaleModel.class);
		given(dealScale1.getFrom()).willReturn(10);
		given(dealScale1.getTo()).willReturn(19);
		given(dealScale1.getScale()).willReturn("001");
		final DealScaleModel dealScale2 = mock(DealScaleModel.class);
		given(dealScale2.getFrom()).willReturn(20);
		given(dealScale2.getTo()).willReturn(29);
		given(dealScale2.getScale()).willReturn("002");

		final List<DealScaleModel> dealScaleModelList = new ArrayList<>();

		dealScaleModelList.add(dealScale1);
		dealScaleModelList.add(dealScale2);
		given(dealConditionGroupModel1.getDealScales()).willReturn(dealScaleModelList);

		final List<AbstractDealConditionModel> dealConditionModels1 = new ArrayList<>();
		final ComplexDealConditionModel complexDealConditionModel1 = mock(ComplexDealConditionModel.class);
		given(complexDealConditionModel1.getBrand()).willReturn("123");

		given(complexDealConditionModel1.getQuantity()).willReturn(Integer.valueOf(10));
		given(complexDealConditionModel1.getUnit()).willReturn(unitmodel);
		given(complexDealConditionModel1.getQuantity()).willReturn(1);
		dealConditionModels1.add(complexDealConditionModel1);

		final ComplexDealConditionModel complexDealConditionModel2 = mock(ComplexDealConditionModel.class);
		given(complexDealConditionModel2.getBrand()).willReturn("567");

		given(complexDealConditionModel2.getQuantity()).willReturn(Integer.valueOf(10));
		given(complexDealConditionModel2.getUnit()).willReturn(unitmodel);
		dealConditionModels1.add(complexDealConditionModel2);

		//	final ProductModel productModel = mock(ProductModel.class);

		given(productService.getProductForCodeSafe(productCode)).willReturn(baseProduct);

		final List<AbstractDealBenefitModel> dealBenefitModels1 = new ArrayList<>();
		final FreeGoodsDealBenefitModel freeGoodsDealBenefitModel1 = mock(FreeGoodsDealBenefitModel.class);
		given(freeGoodsDealBenefitModel1.getDealConditionGroup()).willReturn(dealConditionGroupModel1);
		given(freeGoodsDealBenefitModel1.getProductCode()).willReturn(productCode);
		given(freeGoodsDealBenefitModel1.getQuantity()).willReturn(Integer.valueOf(1));
		given(freeGoodsDealBenefitModel1.getUnit()).willReturn(unitmodel);
		given(freeGoodsDealBenefitModel1.getScale()).willReturn("001");
		dealBenefitModels1.add(freeGoodsDealBenefitModel1);

		final FreeGoodsDealBenefitModel freeGoodsDealBenefitModel2 = mock(FreeGoodsDealBenefitModel.class);
		given(freeGoodsDealBenefitModel2.getProductCode()).willReturn(productCode);
		given(freeGoodsDealBenefitModel2.getQuantity()).willReturn(Integer.valueOf(3));
		given(freeGoodsDealBenefitModel2.getUnit()).willReturn(unitmodel);
		given(freeGoodsDealBenefitModel2.getScale()).willReturn("002");
		given(freeGoodsDealBenefitModel2.getDealConditionGroup()).willReturn(dealConditionGroupModel1);
		dealBenefitModels1.add(freeGoodsDealBenefitModel2);



		given(dealConditionGroupModel1.getDealConditions()).willReturn(dealConditionModels1);
		given(dealConditionGroupModel1.getDealBenefits()).willReturn(dealBenefitModels1);

		given(complexDeal.getConditionGroup()).willReturn(dealConditionGroupModel1);

		final DealJson dealJson = new DealJson();
		sabmDealTitlePopulator.populate(Lists.newArrayList(complexDeal), dealJson);

		Assert.assertEquals(true, StringUtils.isNotEmpty(dealJson.getTitle()));
		System.out.println(dealJson.getTitle());
		//	Assert.assertEquals("buy 1 case product name 1*20 boote to receive 1 case", dealJson.getTitle());


	}

	@Test
	public void testMultiScaleDealConditionTitle1()
	{
		final Locale locale = i18nService.getCurrentLocale();

		final Calendar calendar = Calendar.getInstance();
		final SimpleDateFormat sdf = new SimpleDateFormat("MM-dd-yyyy");
		Date date = null;

		try
		{
			date = sdf.parse("06-02-2016");
		}
		catch (final ParseException e)
		{
			e.printStackTrace();
		}

		calendar.setTime(date);
		given(sabmDealTitlePopulator.getMessageSource().getMessage("text.deal.title.buy", null, locale)).willReturn("buy");


		final DealModel complexDeal = mock(DealModel.class);
		given(complexDeal.getDealType()).willReturn(DealTypeEnum.COMPLEX);
		given(complexDeal.getTriggerHash()).willReturn("123456");



		given(complexDeal.getValidFrom()).willReturn(calendar.getTime());
		given(complexDeal.getValidTo()).willReturn(calendar.getTime());
		final String productCode = "productTestCode";
		final String productCode2 = "productTestCode2";
		final String brand = "testBrand";
		final String brand1 = "test brand 2";
		final String sellingName = "product name";
		final String packConfiguration = "1*20 boote";
		final String sellingName2 = "product name2";
		final String packConfiguration2 = "2*20 boote";

		final SABMAlcoholVariantProductEANModel eanProduct = mock(SABMAlcoholVariantProductEANModel.class);
		final SABMAlcoholVariantProductEANModel eanProduct2 = mock(SABMAlcoholVariantProductEANModel.class);
		final SABMAlcoholProductModel baseProduct = mock(SABMAlcoholProductModel.class);
		final SABMAlcoholProductModel baseProduct2 = mock(SABMAlcoholProductModel.class);
		given(eanProduct2.getBaseProduct()).willReturn(baseProduct2);
		given(eanProduct2.getBaseProduct()).willReturn(eanProduct2);
		given(eanProduct2.getSellingName()).willReturn(sellingName2);
		given(eanProduct2.getPackConfiguration()).willReturn(packConfiguration2);

		given(baseProduct.getBrand()).willReturn(brand);
		given(eanProduct.getBaseProduct()).willReturn(baseProduct);
		given(eanProduct.getBaseProduct()).willReturn(eanProduct);
		given(eanProduct.getSellingName()).willReturn(sellingName);
		given(eanProduct.getPackConfiguration()).willReturn(packConfiguration);
		given(eanProduct.getMinOrderQuantity()).willReturn(2);

		final SABMAlcoholProductModel baseProduct1 = mock(SABMAlcoholProductModel.class);
		given(baseProduct1.getBrand()).willReturn(brand1);


		given(productService.getSABMAlcoholProduct("123")).willReturn(baseProduct);

		given(productService.getSABMAlcoholProduct("567")).willReturn(baseProduct1);



		final UnitModel unitmodel = mock(UnitModel.class);
		given(unitmodel.getName()).willReturn("case");
		given(unitmodel.getPluralName()).willReturn("cases");
		given(eanProduct.getUnit()).willReturn(unitmodel);

		final SABMAlcoholVariantProductMaterialModel materialProduct = mock(SABMAlcoholVariantProductMaterialModel.class);

		given(materialProduct.getBaseProduct()).willReturn(eanProduct);
		given(productService.getProductForCode(productCode)).willReturn(materialProduct);


		final SABMAlcoholVariantProductMaterialModel materialProduct2 = mock(SABMAlcoholVariantProductMaterialModel.class);

		given(materialProduct2.getBaseProduct()).willReturn(eanProduct2);
		given(productService.getProductForCode(productCode2)).willReturn(materialProduct2);

		final DealConditionGroupModel dealConditionGroupModel1 = mock(DealConditionGroupModel.class);
		final DealScaleModel dealScale1 = mock(DealScaleModel.class);
		given(dealScale1.getFrom()).willReturn(10);
		given(dealScale1.getTo()).willReturn(19);
		given(dealScale1.getScale()).willReturn("001");
		final DealScaleModel dealScale2 = mock(DealScaleModel.class);
		given(dealScale2.getFrom()).willReturn(20);
		given(dealScale2.getTo()).willReturn(29);
		given(dealScale2.getScale()).willReturn("002");

		final List<DealScaleModel> dealScaleModelList = new ArrayList<>();

		dealScaleModelList.add(dealScale1);
		dealScaleModelList.add(dealScale2);
		given(dealConditionGroupModel1.getDealScales()).willReturn(dealScaleModelList);

		final List<AbstractDealConditionModel> dealConditionModels1 = new ArrayList<>();
		final ComplexDealConditionModel complexDealConditionModel1 = mock(ComplexDealConditionModel.class);
		given(complexDealConditionModel1.getBrand()).willReturn("123");

		given(complexDealConditionModel1.getQuantity()).willReturn(Integer.valueOf(10));
		given(complexDealConditionModel1.getUnit()).willReturn(unitmodel);
		given(complexDealConditionModel1.getQuantity()).willReturn(1);
		dealConditionModels1.add(complexDealConditionModel1);

		final ComplexDealConditionModel complexDealConditionModel2 = mock(ComplexDealConditionModel.class);

		given(complexDealConditionModel2.getBrand()).willReturn("567");

		given(complexDealConditionModel2.getQuantity()).willReturn(Integer.valueOf(10));
		given(complexDealConditionModel2.getUnit()).willReturn(unitmodel);
		dealConditionModels1.add(complexDealConditionModel2);

		//	final ProductModel productModel = mock(ProductModel.class);

		given(productService.getProductForCodeSafe(productCode)).willReturn(baseProduct);

		given(productService.getProductForCodeSafe(productCode2)).willReturn(baseProduct2);

		final List<AbstractDealBenefitModel> dealBenefitModels1 = new ArrayList<>();
		final FreeGoodsDealBenefitModel freeGoodsDealBenefitModel1 = mock(FreeGoodsDealBenefitModel.class);
		given(freeGoodsDealBenefitModel1.getDealConditionGroup()).willReturn(dealConditionGroupModel1);
		given(freeGoodsDealBenefitModel1.getProductCode()).willReturn(productCode);
		given(freeGoodsDealBenefitModel1.getQuantity()).willReturn(Integer.valueOf(1));
		given(freeGoodsDealBenefitModel1.getUnit()).willReturn(unitmodel);
		given(freeGoodsDealBenefitModel1.getScale()).willReturn("001");
		given(freeGoodsDealBenefitModel1.getProportionalAmount()).willReturn(false);
		given(freeGoodsDealBenefitModel1.getProportionalFreeGood()).willReturn(false);
		dealBenefitModels1.add(freeGoodsDealBenefitModel1);

		final FreeGoodsDealBenefitModel freeGoodsDealBenefitModel2 = mock(FreeGoodsDealBenefitModel.class);
		given(freeGoodsDealBenefitModel2.getProductCode()).willReturn(productCode);
		given(freeGoodsDealBenefitModel2.getQuantity()).willReturn(Integer.valueOf(3));
		given(freeGoodsDealBenefitModel2.getUnit()).willReturn(unitmodel);
		given(freeGoodsDealBenefitModel2.getScale()).willReturn("002");
		given(freeGoodsDealBenefitModel2.getDealConditionGroup()).willReturn(dealConditionGroupModel1);
		given(freeGoodsDealBenefitModel2.getProportionalAmount()).willReturn(false);
		given(freeGoodsDealBenefitModel2.getProportionalFreeGood()).willReturn(false);
		dealBenefitModels1.add(freeGoodsDealBenefitModel2);



		given(dealConditionGroupModel1.getDealConditions()).willReturn(dealConditionModels1);
		given(dealConditionGroupModel1.getDealBenefits()).willReturn(dealBenefitModels1);

		given(complexDeal.getConditionGroup()).willReturn(dealConditionGroupModel1);


		final DealModel complexDeal2 = mock(DealModel.class);
		given(complexDeal2.getDealType()).willReturn(DealTypeEnum.COMPLEX);
		given(complexDeal2.getTriggerHash()).willReturn("123456");
		given(complexDeal2.getValidFrom()).willReturn(calendar.getTime());
		given(complexDeal2.getValidTo()).willReturn(calendar.getTime());
		final DealConditionGroupModel dealConditionGroupModel2 = mock(DealConditionGroupModel.class);
		given(dealConditionGroupModel2.getDealConditions()).willReturn(dealConditionModels1);

		final List<AbstractDealBenefitModel> dealBenefitModels2 = new ArrayList<>();
		final FreeGoodsDealBenefitModel freeGoodsDealBenefitModel3 = mock(FreeGoodsDealBenefitModel.class);
		given(freeGoodsDealBenefitModel3.getDealConditionGroup()).willReturn(dealConditionGroupModel1);
		given(freeGoodsDealBenefitModel3.getProductCode()).willReturn(productCode2);
		given(freeGoodsDealBenefitModel3.getQuantity()).willReturn(Integer.valueOf(1));
		given(freeGoodsDealBenefitModel3.getUnit()).willReturn(unitmodel);
		given(freeGoodsDealBenefitModel3.getScale()).willReturn("001");
		given(freeGoodsDealBenefitModel3.getProportionalAmount()).willReturn(false);
		given(freeGoodsDealBenefitModel3.getProportionalFreeGood()).willReturn(false);
		dealBenefitModels2.add(freeGoodsDealBenefitModel3);

		final FreeGoodsDealBenefitModel freeGoodsDealBenefitModel4 = mock(FreeGoodsDealBenefitModel.class);
		given(freeGoodsDealBenefitModel4.getProductCode()).willReturn(productCode2);
		given(freeGoodsDealBenefitModel4.getQuantity()).willReturn(Integer.valueOf(3));
		given(freeGoodsDealBenefitModel4.getUnit()).willReturn(unitmodel);
		given(freeGoodsDealBenefitModel4.getScale()).willReturn("002");
		given(freeGoodsDealBenefitModel4.getDealConditionGroup()).willReturn(dealConditionGroupModel1);
		given(freeGoodsDealBenefitModel4.getProportionalAmount()).willReturn(false);
		given(freeGoodsDealBenefitModel4.getProportionalFreeGood()).willReturn(false);
		dealBenefitModels2.add(freeGoodsDealBenefitModel4);

		given(dealConditionGroupModel2.getDealBenefits()).willReturn(dealBenefitModels1);

		given(complexDeal2.getConditionGroup()).willReturn(dealConditionGroupModel2);

		final List<DealModel> deals = new ArrayList<>();
		deals.add(complexDeal);
		deals.add(complexDeal2);
		final DealJson dealJson = new DealJson();
		sabmDealTitlePopulator.populate(deals, dealJson);

		Assert.assertEquals(true, StringUtils.isNotEmpty(dealJson.getTitle()));
		System.out.println(dealJson.getTitle());
		//	Assert.assertEquals("buy 1 case product name 1*20 boote to receive 1 case", dealJson.getTitle());


	}
}
