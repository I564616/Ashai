/**
 *
 */
package com.sabmiller.core.cart;

import static org.mockito.BDDMockito.given;

import de.hybris.bootstrap.annotations.UnitTest;
import de.hybris.platform.b2b.model.B2BUnitModel;
import de.hybris.platform.b2b.company.B2BCommerceUnitService;
import de.hybris.platform.commerceservices.service.data.CommerceCheckoutParameter;
import de.hybris.platform.core.model.order.AbstractOrderEntryModel;
import de.hybris.platform.core.model.order.AbstractOrderModel;
import de.hybris.platform.core.model.order.CartModel;
import de.hybris.platform.servicelayer.dto.converter.ConversionException;
import de.hybris.platform.servicelayer.model.ModelService;
import de.hybris.platform.servicelayer.session.SessionService;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Map;

import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.MockitoAnnotations;

import com.sabmiller.core.cart.service.impl.DefaultSABMCartService;
import com.sabmiller.core.constants.SabmCoreConstants;
import com.sabmiller.core.deals.services.DealsService;
import com.sabmiller.core.enums.DealConditionStatus;
import com.sabmiller.core.enums.DealTypeEnum;
import com.sabmiller.core.model.AbstractDealConditionModel;
import com.sabmiller.core.model.CartDealConditionModel;
import com.sabmiller.core.model.ComplexDealConditionModel;
import com.sabmiller.core.model.DealConditionGroupModel;
import com.sabmiller.core.model.DealModel;
import com.sabmiller.core.model.ProductDealConditionModel;
import com.sabmiller.core.model.SABMAlcoholVariantProductMaterialModel;
import com.sabmiller.core.model.ShippingCarrierModel;
import com.sabmiller.core.product.SabmProductService;
import com.sabmiller.core.strategy.SABMDeliveryShippingCarrierStrategy;


/**
 * SABMDefaultCartServiceTest
 *
 * @author yaopeng
 *
 */
@UnitTest
public class SABMDefaultCartServiceTest
{
	@InjectMocks
	private final DefaultSABMCartService sabmDefaultCartService = new DefaultSABMCartService();

	@Mock
	private ModelService modelService;
	@Mock
	private SABMDeliveryShippingCarrierStrategy sabmDeliveryShippingCarrierStrategy;
	@Mock
	private SabmProductService productService;
	@Mock
	private B2BCommerceUnitService b2bCommerceUnitService;
	@Mock
	private SessionService sessionService;
	@Mock
	private DealsService dealsService;

	private SABMAlcoholVariantProductMaterialModel mockP1, mockP2, mockP3, mockP4, mockP5, mockP6, mockP7, mockP8;

	private DealModel deal1, deal2, deal3, deal4, deal5;


	@Before
	public void setUp()
	{
		MockitoAnnotations.initMocks(this);
		sabmDefaultCartService.setModelService(modelService);
		sabmDefaultCartService.setSabmDeliveryShippingCarrierStrategy(sabmDeliveryShippingCarrierStrategy);
		sabmDefaultCartService.setSessionService(sessionService);
	}

	@Test
	public void testSaveDeliveryInstructions()
	{
		final AbstractOrderModel mockOrder = Mockito.mock(AbstractOrderModel.class);
		given(mockOrder.getDeliveryInstructions()).willReturn("abc");
		final String deliveryInstructions = "abc";
		sabmDefaultCartService.saveDeliveryInstructions(deliveryInstructions, mockOrder);
		Assert.assertEquals("abc", mockOrder.getDeliveryInstructions());
	}

	@SuppressWarnings("boxing")
	@Test
	public void testSetShippingCarrier()
	{
		final AbstractOrderModel mockOrder = Mockito.mock(AbstractOrderModel.class);
		given(mockOrder.getDeliveryInstructions()).willReturn("abc");

		final CommerceCheckoutParameter parameter = Mockito.mock(CommerceCheckoutParameter.class);

		final ShippingCarrierModel shippModel = Mockito.mock(ShippingCarrierModel.class);

		given(shippModel.getCarrierCode()).willReturn("Hdl");
		given(shippModel.getCustomerOwned()).willReturn(true);
		given(shippModel.getCarrierDescription()).willReturn("hdl");

		given(parameter.getShippingCarrier()).willReturn(shippModel);
		Mockito.when(sabmDefaultCartService.setShippingCarrier(parameter)).thenReturn(true);
		Assert.assertEquals(true, sabmDefaultCartService.setShippingCarrier(parameter));
	}

	@Test
	public void testGetEntryApplyDeal() throws Exception
	{
		mockProducts(); //mock the products
		mockBrandProducts(); // mock the product with brand
		mockDeals(); //mock the deals
		mockUnit(); //mock the unit
		final CartModel cartModel = mockCartModel();

		final Map<Integer, List<DealModel>> entryDealMap = sabmDefaultCartService.getEntryApplyDeal(cartModel, false);

		Assert.assertNotNull(entryDealMap);
		Assert.assertEquals(true, entryDealMap.containsKey(0));
		Assert.assertEquals(1, entryDealMap.get(0).size());
	}

	private void mockUnit()
	{
		final B2BUnitModel unitModel = Mockito.mock(B2BUnitModel.class);

		final List<DealModel> dealModels = new ArrayList<>();
		dealModels.add(deal1);
		dealModels.add(deal2);
		dealModels.add(deal3);
		dealModels.add(deal4);
		dealModels.add(deal5);
		given(unitModel.getComplexDeals()).willReturn(new HashSet<>(dealModels));
		given(b2bCommerceUnitService.getParentUnit()).willReturn(unitModel);

		given(dealsService.filterOnlineDeals(dealModels)).willReturn(dealModels);
		given(dealsService.getDealsByRepDrivenStatus(dealModels)).willReturn(dealModels);
	}

	private CartModel mockCartModel()
	{
		final CartModel cartModel = Mockito.mock(CartModel.class);
		final List<CartDealConditionModel> conditions = mockDealConditions();
		given(cartModel.getComplexDealConditions()).willReturn(conditions);

		final List<AbstractOrderEntryModel> entries = mockOrderEntries();
		given(cartModel.getEntries()).willReturn(entries);
		return cartModel;
	}

	private List<AbstractOrderEntryModel> mockOrderEntries()
	{
		final List<AbstractOrderEntryModel> entries = new ArrayList<>();

		final AbstractOrderEntryModel entry1 = Mockito.mock(AbstractOrderEntryModel.class);
		given(entry1.getIsFreeGood()).willReturn(Boolean.valueOf(false));
		given(entry1.getEntryNumber()).willReturn(0);
		given(entry1.getProduct()).willReturn(mockP1);
		entries.add(entry1);

		final AbstractOrderEntryModel entry2 = Mockito.mock(AbstractOrderEntryModel.class);
		given(entry2.getIsFreeGood()).willReturn(Boolean.valueOf(true));
		given(entry2.getEntryNumber()).willReturn(1);
		given(entry2.getProduct()).willReturn(mockP1);
		entries.add(entry2);

		final AbstractOrderEntryModel entry3 = Mockito.mock(AbstractOrderEntryModel.class);
		given(entry3.getIsFreeGood()).willReturn(Boolean.valueOf(false));
		given(entry3.getEntryNumber()).willReturn(2);
		given(entry3.getProduct()).willReturn(mockP2);
		entries.add(entry3);

		final AbstractOrderEntryModel entry4 = Mockito.mock(AbstractOrderEntryModel.class);
		given(entry4.getIsFreeGood()).willReturn(Boolean.valueOf(false));
		given(entry4.getEntryNumber()).willReturn(3);
		given(entry4.getProduct()).willReturn(mockP3);
		entries.add(entry4);

		final AbstractOrderEntryModel entry5 = Mockito.mock(AbstractOrderEntryModel.class);
		given(entry5.getIsFreeGood()).willReturn(Boolean.valueOf(true));
		given(entry5.getEntryNumber()).willReturn(4);
		given(entry5.getProduct()).willReturn(mockP4);
		entries.add(entry5);

		return entries;
	}

	private void mockProducts()
	{
		mockP1 = mockProduct("product1");
		mockP2 = mockProduct("product2");
		mockP3 = mockProduct("product3");
		mockP4 = mockProduct("product4");
		mockP5 = mockProduct("product5");
		mockP6 = mockProduct("product6");
		mockP7 = mockProduct("product7");
		mockP8 = mockProduct("product8");

	}

	private void mockBrandProducts()
	{

		final List<SABMAlcoholVariantProductMaterialModel> range1 = new ArrayList<>();
		range1.add(mockP1);
		range1.add(mockP8);
		range1.add(mockP3);

		final List<SABMAlcoholVariantProductMaterialModel> range2 = new ArrayList<>();
		range2.add(mockP6);
		range2.add(mockP4);
		range2.add(mockP5);

		final List<SABMAlcoholVariantProductMaterialModel> range3 = new ArrayList<>();
		range3.add(mockP7);
		range3.add(mockP2);
		range3.add(mockP3);

		given(productService.getProductByHierarchy(null, "brand1", null, null, null, null)).willReturn(range1);
		given(productService.getProductByHierarchy(null, "brand2", null, null, null, null)).willReturn(range2);
		given(productService.getProductByHierarchy(null, "brand3", null, null, null, null)).willReturn(range3);
	}

	private SABMAlcoholVariantProductMaterialModel mockProduct(final String productCode)
	{
		final SABMAlcoholVariantProductMaterialModel p1 = Mockito.mock(SABMAlcoholVariantProductMaterialModel.class);
		Mockito.when(p1.getCode()).thenReturn(productCode);

		Mockito.when(productService.getProductForCode(productCode)).thenReturn(p1);

		return p1;
	}

	private List<CartDealConditionModel> mockDealConditions()
	{
		final List<CartDealConditionModel> conditions = new ArrayList<>();
		conditions.add(mockDealCondition(DealConditionStatus.AUTOMATIC, deal1));
		conditions.add(mockDealCondition(DealConditionStatus.MANUAL, deal2));
		conditions.add(mockDealCondition(DealConditionStatus.MANUAL_CONFLICT, deal3));
		conditions.add(mockDealCondition(DealConditionStatus.REJECTED, deal2));
		return conditions;
	}

	private CartDealConditionModel mockDealCondition(final DealConditionStatus status, final DealModel deal)
	{
		final CartDealConditionModel cartDealConditionModel = Mockito.mock(CartDealConditionModel.class);
		given(cartDealConditionModel.getStatus()).willReturn(status);
		given(cartDealConditionModel.getDeal()).willReturn(deal);

		return cartDealConditionModel;
	}

	private void mockDeals()
	{
		//The complex deal with brand1 and product1 and have triggerHash
		deal1 = mockDeal("code1", "brand1", "product1", Boolean.valueOf(false), DealTypeEnum.COMPLEX, "triggerHash1");
		//The bogof deal with brand2 and product2 and no triggerHash
		deal2 = mockDeal("code2", "brand2", "product2", Boolean.valueOf(false), DealTypeEnum.BOGOF, null);
		//The COMPLEX deal with brand3 and exclude product3 have no triggerHash
		deal3 = mockDeal("code3", "brand3", "product3", Boolean.valueOf(true), DealTypeEnum.COMPLEX, null);
		//The deal similar with the deal1
		deal4 = mockDeal("code1", "brand1", "product1", Boolean.valueOf(false), DealTypeEnum.COMPLEX, "triggerHash1");
		//The deal with brand2 and product3 have triggerHash2
		deal5 = mockDeal("code1", "brand2", "product3", Boolean.valueOf(false), DealTypeEnum.COMPLEX, "triggerHash2");
	}

	private DealModel mockDeal(final String dealCode, final String brand, final String productCode, final Boolean exclude,
			final DealTypeEnum dealType, final String triggerHash)
	{
		final DealModel dealModel = Mockito.mock(DealModel.class);
		given(dealModel.getCode()).willReturn(dealCode);
		final DealConditionGroupModel dealConditionGroup = mockDealConditionGroup(brand, productCode, exclude);
		given(dealModel.getConditionGroup()).willReturn(dealConditionGroup);
		given(dealModel.getDealType()).willReturn(dealType);
		given(dealModel.getTriggerHash()).willReturn(triggerHash);
		given(dealModel.getStatus()).willReturn(SabmCoreConstants.DEAL_ONLINE_STATUS);
		return dealModel;
	}

	private DealConditionGroupModel mockDealConditionGroup(final String brand, final String productCode, final Boolean exclude)
	{
		final List<AbstractDealConditionModel> conditions = new ArrayList<>();

		final ProductDealConditionModel productDealConditionModel = Mockito.mock(ProductDealConditionModel.class);
		given(productDealConditionModel.getProductCode()).willReturn(productCode);
		given(productDealConditionModel.getExclude()).willReturn(exclude);
		conditions.add(productDealConditionModel);

		final ComplexDealConditionModel complexDealConditionModel = Mockito.mock(ComplexDealConditionModel.class);
		given(complexDealConditionModel.getBrand()).willReturn(brand);
		conditions.add(complexDealConditionModel);

		final DealConditionGroupModel dealConditionGroup = Mockito.mock(DealConditionGroupModel.class);
		given(dealConditionGroup.getDealConditions()).willReturn(conditions);
		return dealConditionGroup;
	}

	@Test(expected = ConversionException.class)
	public void testCanNotFindProductByCode()
	{
		mockBrandProducts();
		mockDeals(); //mock the deals
		mockUnit(); //mock the unit
		final CartModel cartModel = mockCartModel();
		sabmDefaultCartService.getEntryApplyDeal(cartModel, false);
	}

	@Test(expected = ConversionException.class)
	public void testCanNotFindProductByBrand()
	{
		mockProducts();
		mockDeals(); //mock the deals
		mockUnit(); //mock the unit
		final CartModel cartModel = mockCartModel();
		sabmDefaultCartService.getEntryApplyDeal(cartModel, false);
	}

}
