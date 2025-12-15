/**
 *
 */
package com.sabmiller.facades.populators;

import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

import de.hybris.bootstrap.annotations.UnitTest;
import de.hybris.platform.category.model.CategoryModel;
import de.hybris.platform.commerceservices.product.CommerceProductService;
import de.hybris.platform.core.model.product.ProductModel;
import de.hybris.platform.servicelayer.dto.converter.Converter;
import de.hybris.platform.servicelayer.session.SessionService;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.Map;

import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import com.google.common.collect.Lists;
import com.google.common.collect.Maps;
import com.sabmiller.core.constants.SabmCoreConstants;
import com.sabmiller.core.deals.services.DealsService;
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
import com.sabmiller.facades.deal.data.DealBaseProductJson;
import com.sabmiller.facades.deal.data.DealFreeProductJson;
import com.sabmiller.facades.deal.data.DealJson;


/**
 *
 */
@UnitTest
public class SABMDealProductPopulatorTest
{

	@InjectMocks
	private final SABMDealProductPopulator sabmDealProductPopulator = new SABMDealProductPopulator();

	@Mock
	private SabmProductService productService;

	@Mock
	private Converter<ProductModel, DealFreeProductJson> dealFreeProductJsonConverter;

	@Mock
	private Converter<ProductModel, DealBaseProductJson> dealBaseProductJsonConverter;

	@Mock
	private CommerceProductService commerceProductService;

	@Mock
	private DealsService dealsService;
	
	@Mock
	private SessionService sessionService;

	@Before
	public void setUp()
	{
		MockitoAnnotations.initMocks(this);

	}

	@Test
	/**
	 * @author ross.hengjun.zhu
	 *
	 *         Test the benefits populator only for the new attribute "proportionalFreeGood" of DealProductJson
	 */
	public void testPopulateBenefitsOnlyForAttributeProportionalFreeGood()
	{
		final List<DealModel> deals = new ArrayList<DealModel>();
		final DealJson target = new DealJson();

		final DealConditionGroupModel condition1 = mock(DealConditionGroupModel.class);
		final List<AbstractDealBenefitModel> benefits1 = new ArrayList<AbstractDealBenefitModel>();

		final DealModel deal1 = mock(DealModel.class);
		given(deal1.getConditionGroup()).willReturn(condition1);
		when(condition1.getDealBenefits()).thenReturn(benefits1);
		deals.add(deal1);

		final FreeGoodsDealBenefitModel benefit1 = mock(FreeGoodsDealBenefitModel.class);
		given(benefit1.getProductCode()).willReturn("product1");
		given(benefit1.getProportionalFreeGood()).willReturn(null);
		final FreeGoodsDealBenefitModel benefit2 = mock(FreeGoodsDealBenefitModel.class);
		given(benefit2.getProportionalFreeGood()).willReturn(Boolean.valueOf(true));
		given(benefit2.getProductCode()).willReturn("product2");
		benefits1.add(benefit1);
		benefits1.add(benefit2);


		final ProductModel product1 = mock(ProductModel.class);
		product1.setCode("product1");
		when(productService.getProductForCode("product1")).thenReturn(product1);
		when(productService.getProductForCodeSafe("product1")).thenReturn(product1);
		final ProductModel product2 = mock(ProductModel.class);
		product1.setCode("product2");
		when(productService.getProductForCode("product2")).thenReturn(product2);
		when(productService.getProductForCodeSafe("product2")).thenReturn(product2);

		final DealFreeProductJson json1 = new DealFreeProductJson();
		final DealFreeProductJson json2 = new DealFreeProductJson();
		when(dealFreeProductJsonConverter.convert(product1)).thenReturn(json1);
		when(dealFreeProductJsonConverter.convert(product2)).thenReturn(json2);

		sabmDealProductPopulator.populateBenefits(deals, target);

		Assert.assertTrue(!json1.getProportionalFreeGood());
		Assert.assertTrue(json2.getProportionalFreeGood());
	}

	@Test
	public void testPopulate()
	{
		final DealModel freeProductDeal = mock(DealModel.class);
		final DealModel selectFreeProductDeal1 = mock(DealModel.class);
		final DealModel selectFreeProductDeal2 = mock(DealModel.class);
		final DealModel acrossFreeProductDeal = mock(DealModel.class);
		final DealModel rangesProductDeal = mock(DealModel.class);
		final DealModel acrossRangesProductDeal = mock(DealModel.class);
		final DealModel noAcrossRangesProductDeal = mock(DealModel.class);
		final DealModel noScasesProductDeal = mock(DealModel.class);
		final String productCode = "mockProductCode";
		final String brand = "mockBrand";
		final String dealCode = "mockDealCode";
		final String triggerHash = "mockTriggerHash";
		final String complexBrand = "testLevel2";

		final SABMAlcoholVariantProductMaterialModel materialProduct = mock(SABMAlcoholVariantProductMaterialModel.class);
		final SABMAlcoholVariantProductEANModel eanProduct = mock(SABMAlcoholVariantProductEANModel.class);
		final SABMAlcoholProductModel baseProduct = mock(SABMAlcoholProductModel.class);
		given(baseProduct.getBrand()).willReturn(brand);
		given(eanProduct.getBaseProduct()).willReturn(baseProduct);
		given(materialProduct.getBaseProduct()).willReturn(eanProduct);
		final DealBaseProductJson dealBaseProductJson = new DealBaseProductJson();
		final DealFreeProductJson dealFreeProductJson = new DealFreeProductJson();
		given(productService.getProductForCode(productCode)).willReturn(materialProduct);
		given(productService.getProductForCodeSafe(productCode)).willReturn(materialProduct);
		given(dealFreeProductJsonConverter.convert(materialProduct)).willReturn(dealFreeProductJson);
		given(dealBaseProductJsonConverter.convert(materialProduct)).willReturn(dealBaseProductJson);

		final List<SABMAlcoholVariantProductMaterialModel> productModels = new ArrayList<>();
		productModels.add(materialProduct);
		given(productService.getProductByLevel2(complexBrand)).willReturn(productModels);


		final DealConditionGroupModel dealConditionGroupModel1 = mock(DealConditionGroupModel.class);
		final List<AbstractDealConditionModel> dealConditionModels1 = new ArrayList<>();
		final ProductDealConditionModel productDealConditionModel1 = mock(ProductDealConditionModel.class);
		given(productDealConditionModel1.getProductCode()).willReturn(productCode);
		given(productDealConditionModel1.getMinQty()).willReturn(Integer.valueOf(10));
		final ProductDealConditionModel productDealConditionModel2 = mock(ProductDealConditionModel.class);
		given(productDealConditionModel2.getProductCode()).willReturn(productCode);
		given(productDealConditionModel2.getMinQty()).willReturn(Integer.valueOf(5));
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
		given(freeProductDeal.getCode()).willReturn(dealCode);
		given(freeProductDeal.getConditionGroup()).willReturn(dealConditionGroupModel1);

		final DealJson freeDealJson = new DealJson();
		sabmDealProductPopulator.populate(Lists.newArrayList(freeProductDeal), freeDealJson);
		Assert.assertEquals(2, freeDealJson.getFreeProducts().size());
		//Assert.assertEquals(2, freeDealJson.getRanges().get(0).getBaseProducts().size());
		//Assert.assertEquals(Integer.valueOf(5), freeDealJson.getRanges().get(0).getBaseProducts().get(1).getQty());

		final DealConditionGroupModel dealConditionGroupModel3 = mock(DealConditionGroupModel.class);
		final List<AbstractDealConditionModel> dealConditionModels3 = new ArrayList<>();
		final ProductDealConditionModel productDealConditionModel5 = mock(ProductDealConditionModel.class);
		given(productDealConditionModel5.getProductCode()).willReturn(productCode);
		given(productDealConditionModel5.getMinQty()).willReturn(Integer.valueOf(10));
		final ProductDealConditionModel productDealConditionModel6 = mock(ProductDealConditionModel.class);
		given(productDealConditionModel6.getProductCode()).willReturn(productCode);
		given(productDealConditionModel6.getMinQty()).willReturn(Integer.valueOf(5));
		dealConditionModels3.add(productDealConditionModel5);
		dealConditionModels3.add(productDealConditionModel6);
		final List<AbstractDealBenefitModel> dealBenefitModels3 = new ArrayList<>();
		final FreeGoodsDealBenefitModel freeGoodsDealBenefitModel5 = mock(FreeGoodsDealBenefitModel.class);
		given(freeGoodsDealBenefitModel5.getProductCode()).willReturn(productCode);
		given(freeGoodsDealBenefitModel5.getQuantity()).willReturn(Integer.valueOf(5));
		dealBenefitModels3.add(freeGoodsDealBenefitModel5);
		given(dealConditionGroupModel3.getDealConditions()).willReturn(dealConditionModels3);
		given(dealConditionGroupModel3.getDealBenefits()).willReturn(dealBenefitModels3);
		given(selectFreeProductDeal1.getCode()).willReturn(dealCode);
		given(selectFreeProductDeal1.getConditionGroup()).willReturn(dealConditionGroupModel3);
		given(selectFreeProductDeal1.getTriggerHash()).willReturn(triggerHash);

		final DealConditionGroupModel dealConditionGroupModel8 = mock(DealConditionGroupModel.class);
		final List<AbstractDealBenefitModel> dealBenefitModels8 = new ArrayList<>();
		final FreeGoodsDealBenefitModel freeGoodsDealBenefitModel6 = mock(FreeGoodsDealBenefitModel.class);
		given(freeGoodsDealBenefitModel6.getQuantity()).willReturn(Integer.valueOf(15));
		given(freeGoodsDealBenefitModel6.getProductCode()).willReturn(productCode);
		dealBenefitModels8.add(freeGoodsDealBenefitModel6);
		given(dealConditionGroupModel8.getDealConditions()).willReturn(dealConditionModels3);
		given(dealConditionGroupModel8.getDealBenefits()).willReturn(dealBenefitModels8);
		given(selectFreeProductDeal2.getCode()).willReturn(dealCode);
		given(selectFreeProductDeal2.getConditionGroup()).willReturn(dealConditionGroupModel8);
		given(selectFreeProductDeal2.getTriggerHash()).willReturn(triggerHash);


		final DealJson selectFreeProductDealJson = new DealJson();
		sabmDealProductPopulator.populate(Lists.newArrayList(selectFreeProductDeal1, selectFreeProductDeal2),
				selectFreeProductDealJson);
		Assert.assertEquals(2, selectFreeProductDealJson.getSelectableProducts().size());

		final DealConditionGroupModel dealConditionGroupModel2 = mock(DealConditionGroupModel.class);
		final List<AbstractDealConditionModel> dealConditionModels2 = new ArrayList<>();
		final ProductDealConditionModel productDealConditionModel3 = mock(ProductDealConditionModel.class);
		given(productDealConditionModel3.getProductCode()).willReturn(productCode);
		given(productDealConditionModel3.getMinQty()).willReturn(Integer.valueOf(10));
		final ProductDealConditionModel productDealConditionModel4 = mock(ProductDealConditionModel.class);
		given(productDealConditionModel4.getProductCode()).willReturn(productCode);
		given(productDealConditionModel4.getMinQty()).willReturn(Integer.valueOf(5));
		dealConditionModels2.add(productDealConditionModel3);
		dealConditionModels2.add(productDealConditionModel4);
		final List<AbstractDealBenefitModel> dealBenefitModels2 = new ArrayList<>();
		final FreeGoodsDealBenefitModel freeGoodsDealBenefitModel3 = mock(FreeGoodsDealBenefitModel.class);
		final FreeGoodsDealBenefitModel freeGoodsDealBenefitModel4 = mock(FreeGoodsDealBenefitModel.class);
		given(freeGoodsDealBenefitModel3.getProductCode()).willReturn(productCode);
		given(freeGoodsDealBenefitModel3.getQuantity()).willReturn(Integer.valueOf(5));
		given(freeGoodsDealBenefitModel4.getQuantity()).willReturn(Integer.valueOf(15));
		given(freeGoodsDealBenefitModel4.getProductCode()).willReturn(productCode);
		dealBenefitModels2.add(freeGoodsDealBenefitModel3);
		dealBenefitModels2.add(freeGoodsDealBenefitModel4);
		given(dealConditionGroupModel2.getDealConditions()).willReturn(dealConditionModels2);
		given(dealConditionGroupModel2.getDealBenefits()).willReturn(dealBenefitModels2);
		given(dealConditionGroupModel2.getMultipleScales()).willReturn(Boolean.TRUE);
		final List<Integer> scales = new ArrayList<>();
		scales.add(Integer.valueOf(7));
		scales.add(Integer.valueOf(8));
		given(dealConditionGroupModel2.getScales()).willReturn(scales);

		given(acrossFreeProductDeal.getCode()).willReturn(dealCode);
		given(acrossFreeProductDeal.getDealType()).willReturn(DealTypeEnum.COMPLEX);
		given(acrossFreeProductDeal.getConditionGroup()).willReturn(dealConditionGroupModel2);
		final DealJson accrossFreeDealJson = new DealJson();
		sabmDealProductPopulator.populate(Lists.newArrayList(acrossFreeProductDeal), accrossFreeDealJson);
		Assert.assertEquals(2, accrossFreeDealJson.getFreeProducts().size());
	
		final DealConditionGroupModel dealConditionGroupModel4 = mock(DealConditionGroupModel.class);
		final List<AbstractDealConditionModel> dealConditionModels4 = new ArrayList<>();
		final ProductDealConditionModel productDealConditionModel7 = mock(ProductDealConditionModel.class);
		given(productDealConditionModel7.getProductCode()).willReturn(productCode);
		given(productDealConditionModel7.getMinQty()).willReturn(Integer.valueOf(10));
		final ComplexDealConditionModel complexDealConditionModel1 = mock(ComplexDealConditionModel.class);
		given(complexDealConditionModel1.getBrand()).willReturn(complexBrand);
		given(complexDealConditionModel1.getEmptyType()).willReturn("testType");
		given(complexDealConditionModel1.getQuantity()).willReturn(Integer.valueOf(11));
		dealConditionModels4.add(productDealConditionModel7);
		dealConditionModels4.add(complexDealConditionModel1);
		final List<AbstractDealBenefitModel> dealBenefitModels4 = new ArrayList<>();
		final FreeGoodsDealBenefitModel freeGoodsDealBenefitModel7 = mock(FreeGoodsDealBenefitModel.class);
		given(freeGoodsDealBenefitModel7.getProductCode()).willReturn(productCode);
		given(freeGoodsDealBenefitModel7.getQuantity()).willReturn(Integer.valueOf(5));
		dealBenefitModels4.add(freeGoodsDealBenefitModel7);
		given(dealConditionGroupModel4.getDealConditions()).willReturn(dealConditionModels4);
		given(dealConditionGroupModel4.getDealBenefits()).willReturn(dealBenefitModels4);
		given(dealConditionGroupModel4.getMultipleScales()).willReturn(Boolean.TRUE);
		final List<Integer> scales4 = new ArrayList<>();
		scales4.add(Integer.valueOf(7));
		given(dealConditionGroupModel4.getScales()).willReturn(scales4);

		given(rangesProductDeal.getCode()).willReturn(dealCode);
		given(rangesProductDeal.getDealType()).willReturn(DealTypeEnum.COMPLEX);
		given(rangesProductDeal.getConditionGroup()).willReturn(dealConditionGroupModel4);
		final DealJson rangeFreeDealJson = new DealJson();
		//sabmDealProductPopulator.populate(Lists.newArrayList(rangesProductDeal), rangeFreeDealJson);
		/*
		 * Assert.assertEquals(1, rangeFreeDealJson.getFreeProducts().size());
		 * Assert.assertTrue(rangeFreeDealJson.getFreeProducts().get(0).getQty().containsKey(Integer.valueOf(7)));
		 * Assert.assertEquals(2, rangeFreeDealJson.getRanges().get(0).getBaseProducts().size()); Assert.assertEquals(1,
		 * rangeFreeDealJson.getRanges().size());
		 */

		final DealConditionGroupModel dealConditionGroupModel5 = mock(DealConditionGroupModel.class);
		final List<AbstractDealConditionModel> dealConditionModels5 = new ArrayList<>();
		final ProductDealConditionModel productDealConditionModel8 = mock(ProductDealConditionModel.class);
		given(productDealConditionModel8.getProductCode()).willReturn(productCode);
		given(productDealConditionModel8.getMinQty()).willReturn(Integer.valueOf(10));
		final ComplexDealConditionModel complexDealConditionModel2 = mock(ComplexDealConditionModel.class);
		given(complexDealConditionModel2.getBrand()).willReturn(complexBrand);
		dealConditionModels5.add(productDealConditionModel8);
		//dealConditionModels5.add(complexDealConditionModel2);
		dealConditionModels5.add(complexDealConditionModel1);
		final List<AbstractDealBenefitModel> dealBenefitModels5 = new ArrayList<>();
		final FreeGoodsDealBenefitModel freeGoodsDealBenefitModel9 = mock(FreeGoodsDealBenefitModel.class);
		final FreeGoodsDealBenefitModel freeGoodsDealBenefitModel10 = mock(FreeGoodsDealBenefitModel.class);
		given(freeGoodsDealBenefitModel9.getProductCode()).willReturn(productCode);
		given(freeGoodsDealBenefitModel9.getQuantity()).willReturn(Integer.valueOf(5));
		given(freeGoodsDealBenefitModel10.getQuantity()).willReturn(Integer.valueOf(15));
		given(freeGoodsDealBenefitModel10.getProductCode()).willReturn(productCode);
		dealBenefitModels5.add(freeGoodsDealBenefitModel9);
		dealBenefitModels5.add(freeGoodsDealBenefitModel10);
		given(dealConditionGroupModel5.getDealConditions()).willReturn(dealConditionModels5);
		given(dealConditionGroupModel5.getDealBenefits()).willReturn(dealBenefitModels5);
		given(dealConditionGroupModel5.getMultipleScales()).willReturn(Boolean.TRUE);
		final List<Integer> scales5 = new ArrayList<>();
		scales5.add(Integer.valueOf(9));
		scales5.add(Integer.valueOf(8));
		final List<SABMAlcoholVariantProductMaterialModel> materials = new ArrayList<>();
		final SABMAlcoholVariantProductMaterialModel materialProduct2 = mock(SABMAlcoholVariantProductMaterialModel.class);
		final SABMAlcoholVariantProductEANModel eanProduct2 = mock(SABMAlcoholVariantProductEANModel.class);
		final SABMAlcoholProductModel alcoholProduct2 = mock(SABMAlcoholProductModel.class);
		final Collection<CategoryModel> categories2 = new ArrayList<>();
		final CategoryModel category2 = mock(CategoryModel.class);
		given(category2.getName()).willReturn("Category2");
		categories2.add(category2);
		given(commerceProductService.getSuperCategoriesExceptClassificationClassesForProduct(alcoholProduct2))
				.willReturn(categories2);
		given(eanProduct2.getBaseProduct()).willReturn(alcoholProduct2);
		given(eanProduct2.getPurchasable()).willReturn(Boolean.valueOf(true));
		given(materialProduct2.getBaseProduct()).willReturn(eanProduct2);
		materials.add(materialProduct2);
		given(dealBaseProductJsonConverter.convert(materialProduct2)).willReturn(new DealBaseProductJson());
		final SABMAlcoholVariantProductMaterialModel materialProduct3 = mock(SABMAlcoholVariantProductMaterialModel.class);
		final SABMAlcoholVariantProductEANModel eanProduct3 = mock(SABMAlcoholVariantProductEANModel.class);
		final SABMAlcoholProductModel alcoholProduct3 = mock(SABMAlcoholProductModel.class);
		final Collection<CategoryModel> categories3 = new ArrayList<>();
		final CategoryModel category3 = mock(CategoryModel.class);
		given(category3.getName()).willReturn("Category3");
		categories2.add(category3);
		given(commerceProductService.getSuperCategoriesExceptClassificationClassesForProduct(alcoholProduct3))
				.willReturn(categories3);
		given(eanProduct3.getBaseProduct()).willReturn(alcoholProduct3);
		given(eanProduct3.getPurchasable()).willReturn(Boolean.valueOf(true));
		given(materialProduct3.getBaseProduct()).willReturn(eanProduct3);
		materials.add(materialProduct3);
		given(dealConditionGroupModel5.getScales()).willReturn(scales5);
		given(acrossRangesProductDeal.getCode()).willReturn(dealCode);
		given(acrossRangesProductDeal.getDealType()).willReturn(DealTypeEnum.COMPLEX);
		given(acrossRangesProductDeal.getConditionGroup()).willReturn(dealConditionGroupModel5);
		given(complexDealConditionModel1.getLine()).willReturn("level1");
		given(complexDealConditionModel1.getVariety()).willReturn("level3");
		given(complexDealConditionModel1.getEmpties()).willReturn("level4");
		given(complexDealConditionModel1.getPresentation()).willReturn("level6");
		given(productService.getProductByHierarchy("level1", complexBrand, "level3", "level4", "testType", "level6"))
		.willReturn(materials);
		given(dealBaseProductJsonConverter.convert(materialProduct3)).willReturn(new DealBaseProductJson());
		final DealJson acrossRangeFreeDealJson = new DealJson();
		sabmDealProductPopulator.populate(Lists.newArrayList(acrossRangesProductDeal), acrossRangeFreeDealJson);
		Assert.assertEquals(2, acrossRangeFreeDealJson.getFreeProducts().size());
	}

	@Test
	public void testBuildCategories()
	{
		final DealModel mockedDeal = mock(DealModel.class);
		final List<AbstractDealConditionModel> conditions = new ArrayList<>();

		final ProductDealConditionModel productDealCondition = mock(ProductDealConditionModel.class);
		final SABMAlcoholVariantProductMaterialModel materialProduct1 = mock(SABMAlcoholVariantProductMaterialModel.class);
		final SABMAlcoholVariantProductEANModel eanProduct1 = mock(SABMAlcoholVariantProductEANModel.class);
		final SABMAlcoholProductModel alcoholProduct1 = mock(SABMAlcoholProductModel.class);
		final Collection<CategoryModel> categories1 = new ArrayList<>();
		final CategoryModel category1 = mock(CategoryModel.class);
		given(category1.getName()).willReturn("Category1");
		categories1.add(category1);
		given(commerceProductService.getSuperCategoriesExceptClassificationClassesForProduct(alcoholProduct1))
				.willReturn(categories1);
		given(eanProduct1.getBaseProduct()).willReturn(alcoholProduct1);
		given(eanProduct1.getPurchasable()).willReturn(Boolean.valueOf(true));
		given(materialProduct1.getBaseProduct()).willReturn(eanProduct1);
		given(productDealCondition.getProductCode()).willReturn("materialProduct1");
		given(productService.getProductForCode("materialProduct1")).willReturn(materialProduct1);
		given(productService.getProductForCodeSafe("materialProduct1")).willReturn(materialProduct1);
		given(dealBaseProductJsonConverter.convert(materialProduct1)).willReturn(new DealBaseProductJson());
		conditions.add(productDealCondition);

		final ComplexDealConditionModel complexDealCondition = mock(ComplexDealConditionModel.class);
		final List<SABMAlcoholVariantProductMaterialModel> materials = new ArrayList<>();
		final SABMAlcoholVariantProductMaterialModel materialProduct2 = mock(SABMAlcoholVariantProductMaterialModel.class);
		final SABMAlcoholVariantProductEANModel eanProduct2 = mock(SABMAlcoholVariantProductEANModel.class);
		final SABMAlcoholProductModel alcoholProduct2 = mock(SABMAlcoholProductModel.class);
		final Collection<CategoryModel> categories2 = new ArrayList<>();
		final CategoryModel category2 = mock(CategoryModel.class);
		given(category2.getName()).willReturn("Category2");
		categories1.add(category2);
		given(commerceProductService.getSuperCategoriesExceptClassificationClassesForProduct(alcoholProduct2))
				.willReturn(categories2);
		given(eanProduct2.getBaseProduct()).willReturn(alcoholProduct2);
		given(eanProduct2.getPurchasable()).willReturn(Boolean.valueOf(true));
		given(materialProduct2.getBaseProduct()).willReturn(eanProduct2);
		materials.add(materialProduct2);
		given(dealBaseProductJsonConverter.convert(materialProduct2)).willReturn(new DealBaseProductJson());
		final SABMAlcoholVariantProductMaterialModel materialProduct3 = mock(SABMAlcoholVariantProductMaterialModel.class);
		final SABMAlcoholVariantProductEANModel eanProduct3 = mock(SABMAlcoholVariantProductEANModel.class);
		final SABMAlcoholProductModel alcoholProduct3 = mock(SABMAlcoholProductModel.class);
		final Collection<CategoryModel> categories3 = new ArrayList<>();
		final CategoryModel category3 = mock(CategoryModel.class);
		given(category3.getName()).willReturn("Category3");
		categories1.add(category3);
		given(commerceProductService.getSuperCategoriesExceptClassificationClassesForProduct(alcoholProduct3))
				.willReturn(categories3);
		given(eanProduct3.getBaseProduct()).willReturn(alcoholProduct3);
		given(eanProduct3.getPurchasable()).willReturn(Boolean.valueOf(true));
		given(materialProduct3.getBaseProduct()).willReturn(eanProduct3);
		materials.add(materialProduct3);
		given(dealBaseProductJsonConverter.convert(materialProduct3)).willReturn(new DealBaseProductJson());
		given(complexDealCondition.getLine()).willReturn("level1");
		given(complexDealCondition.getBrand()).willReturn("level2");
		given(complexDealCondition.getVariety()).willReturn("level3");
		given(complexDealCondition.getEmpties()).willReturn("level4");
		given(complexDealCondition.getEmptyType()).willReturn("level5");
		given(complexDealCondition.getPresentation()).willReturn("level6");
		given(productService.getProductByHierarchy("level1", "level2", "level3", "level4", "level5", "level6"))
				.willReturn(materials);
		conditions.add(complexDealCondition);

		final DealConditionGroupModel conditionGroup = mock(DealConditionGroupModel.class);
		given(conditionGroup.getDealConditions()).willReturn(conditions);
		given(mockedDeal.getConditionGroup()).willReturn(conditionGroup);

		given(sessionService.getAttribute(SabmCoreConstants.SESSION_ATTR_DELIVERY_DATE_PACKTYPE)).willReturn(null);
		final DealJson dealJson = new DealJson();
		sabmDealProductPopulator.populate(Lists.newArrayList(mockedDeal), dealJson);
		Assert.assertEquals(3, dealJson.getCategories().size());
		Assert.assertTrue(dealJson.getCategories().contains("Category3"));
		Assert.assertTrue(dealJson.getCategories().contains("Category2"));
		Assert.assertTrue(dealJson.getCategories().contains("Category1"));

	}

	/**
	 * test by SABMC-786
	 */
	@Test
	public void testPopulateBenefits()
	{
		final List<DealModel> deals = new ArrayList<DealModel>();
		final DealJson target = new DealJson();

		final DealConditionGroupModel condition1 = mock(DealConditionGroupModel.class);
		final List<AbstractDealBenefitModel> benefits1 = new ArrayList<AbstractDealBenefitModel>();

		final DealModel deal1 = mock(DealModel.class);
		given(deal1.getConditionGroup()).willReturn(condition1);
		when(condition1.getDealBenefits()).thenReturn(benefits1);

		final List<Integer> qtylist = new ArrayList<Integer>();
		qtylist.add(10);
		qtylist.add(20);
		when(condition1.getScales()).thenReturn(qtylist);

		final List<DealScaleModel> dealScale = new ArrayList<DealScaleModel>();

		final DealScaleModel dealScale1 = mock(DealScaleModel.class);


		final DealScaleModel dealScale2 = mock(DealScaleModel.class);
		given(dealScale1.getFrom()).willReturn(10);
		given(dealScale1.getScale()).willReturn("001");
		given(dealScale1.getTo()).willReturn(19);
		when(condition1.getDealScales()).thenReturn(dealScale);
		given(dealScale2.getFrom()).willReturn(20);
		given(dealScale2.getScale()).willReturn("002");
		given(dealScale2.getTo()).willReturn(99);

		dealScale.add(dealScale1);
		dealScale.add(dealScale2);

		deals.add(deal1);

		final DiscountDealBenefitModel benefit1 = mock(DiscountDealBenefitModel.class);
		given(benefit1.getAmount()).willReturn(1d);
		given(benefit1.getProportionalAmount()).willReturn(Boolean.valueOf(false));
		given(benefit1.getProportionalFreeGood()).willReturn(Boolean.valueOf(false));
		given(benefit1.getScale()).willReturn("001");
		final FreeGoodsDealBenefitModel benefit2 = mock(FreeGoodsDealBenefitModel.class);
		given(benefit2.getProportionalFreeGood()).willReturn(Boolean.valueOf(false));
		given(benefit2.getProductCode()).willReturn("product2");
		given(benefit2.getQuantity()).willReturn(1);
		given(benefit2.getScale()).willReturn("002");
		benefits1.add(benefit1);
		benefits1.add(benefit2);


		final ProductModel product1 = mock(ProductModel.class);
		product1.setCode("product1");
		when(productService.getProductForCode("product1")).thenReturn(product1);
		when(productService.getProductForCodeSafe("product1")).thenReturn(product1);
		final ProductModel product2 = mock(ProductModel.class);
		product1.setCode("product2");
		when(productService.getProductForCode("product2")).thenReturn(product2);
		when(productService.getProductForCodeSafe("product2")).thenReturn(product2);

		final DealFreeProductJson json1 = new DealFreeProductJson();
		final DealFreeProductJson json2 = new DealFreeProductJson();
		when(dealFreeProductJsonConverter.convert(product1)).thenReturn(json1);
		when(dealFreeProductJsonConverter.convert(product2)).thenReturn(json2);

		sabmDealProductPopulator.populateBenefits(deals, target);
		Assert.assertEquals(1, target.getFreeProducts().size());
		final Map<Integer, Integer> map = Maps.newHashMap();
		map.put(20, 1);
		map.put(10, 0);
		Assert.assertEquals(map, target.getFreeProducts().get(0).getQty());

	}


}
