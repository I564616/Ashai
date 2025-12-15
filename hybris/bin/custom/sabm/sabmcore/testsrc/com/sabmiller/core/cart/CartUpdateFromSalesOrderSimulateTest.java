/**
 *
 */
package com.sabmiller.core.cart;


import de.hybris.bootstrap.annotations.IntegrationTest;
import de.hybris.platform.b2b.model.B2BCustomerModel;
import de.hybris.platform.b2b.model.B2BUnitModel;
import de.hybris.platform.catalog.CatalogVersionService;
import de.hybris.platform.catalog.impl.AbstractCatalogTest;
import de.hybris.platform.catalog.model.CatalogVersionModel;
import de.hybris.platform.commercefacades.order.data.CartData;
import de.hybris.platform.core.model.c2l.CurrencyModel;
import de.hybris.platform.core.model.c2l.LanguageModel;
import de.hybris.platform.core.model.order.AbstractOrderEntryModel;
import de.hybris.platform.core.model.order.AbstractOrderModel;
import de.hybris.platform.core.model.order.CartEntryModel;
import de.hybris.platform.core.model.order.CartModel;
import de.hybris.platform.core.model.product.UnitModel;
import de.hybris.platform.product.ProductService;
import de.hybris.platform.product.UnitService;
import de.hybris.platform.servicelayer.dto.converter.Converter;
import de.hybris.platform.servicelayer.exceptions.UnknownIdentifierException;
import de.hybris.platform.servicelayer.i18n.CommonI18NService;
import de.hybris.platform.servicelayer.model.ModelService;
import de.hybris.platform.servicelayer.user.UserService;
import de.hybris.platform.util.TaxValue;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Date;
import java.util.List;
import java.util.Locale;

import jakarta.annotation.Resource;

import org.junit.Before;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.MockitoAnnotations;

import com.sabmiller.core.cart.service.SABMCalculationService;
import com.sabmiller.core.cart.service.helper.SalesOrderSimulateCartSyncHelper;
import com.sabmiller.core.cart.service.impl.DefaultSABMCartService;
import com.sabmiller.core.enums.AlcoholCategoryAttribute;
import com.sabmiller.core.model.SABMAlcoholProductModel;
import com.sabmiller.integration.facade.ErrorEventFacade;
import com.sabmiller.integration.sap.ordersimulate.SalesOrderSimulateRequestHandler;
import com.sabmiller.integration.sap.ordersimulate.request.SalesOrderSimulateRequest;
import com.sabmiller.integration.sap.ordersimulate.response.SalesOrderSimulateResponse;
import com.sabmiller.integration.sap.ordersimulate.response.SalesOrderSimulateResponse.SalesOrderResHeder;
import com.sabmiller.integration.sap.ordersimulate.response.SalesOrderSimulateResponse.SalesOrderResItem;
import com.sabmiller.integration.sap.ordersimulate.response.SalesOrderSimulateResponse.SalesOrderResItem.SalesOrderFreeGoods;
import com.sabmiller.integration.sap.ordersimulate.response.SalesOrderSimulateResponse.SalesOrderResItem.SalesOrderItemCondition;
import com.sabmiller.integration.sap.ordersimulate.response.SalesOrderSimulateResponse.SalesOrderResItem.SalesOrderItemCondition.SalesOrderItemScales;
import com.sabmiller.integration.sap.ordersimulate.response.SalesOrderSimulateResponse.SalesOrderResItem.SalesOrderItemScheduling;


/**
 *
 */
@IntegrationTest
public class CartUpdateFromSalesOrderSimulateTest extends AbstractCatalogTest

{
	@Resource
	private DefaultSABMCartService cartService;

	@Resource
	private Converter<CartData, SalesOrderSimulateRequest> salesOrderSimulateRequestConverter;

	@Mock
	private SalesOrderSimulateRequestHandler salesOrderSimulateRestHandler;

	@Mock
	private ProductService productService;

	@Resource
	private SABMCalculationService calculationService;

	@Resource
	UserService userService;


	@Mock
	private UnitService unitService;


	@Mock
	LanguageModel languageModel;

	@Resource
	protected CatalogVersionService catalogVersionService;

	@Resource
	private ModelService modelService;

	@Resource
	CommonI18NService commonI18NService;

	@Resource
	private ErrorEventFacade errorEventFacade;

	@Resource
	private Converter<AbstractOrderModel, CartData> cartConverter;


	CatalogVersionModel catalogVersion;

	private SABMAlcoholProductModel productP1;
	private SABMAlcoholProductModel productP2;
	UnitModel unit;
	CurrencyModel aud;

	CartModel cartModel;
	B2BCustomerModel user;


	@Override
	@Before
	public void setUp() throws Exception
	{
		super.setUp();
		MockitoAnnotations.initMocks(this);
		user = modelService.create(B2BCustomerModel.class);
		user.setName("Test");
		user.setUid("dummy@dummy.com");
		final B2BUnitModel b2bUnit = modelService.create(B2BUnitModel.class);
		b2bUnit.setName("b2b");
		b2bUnit.setUid("b2bunit");
		b2bUnit.setLocName("b2bunit");
		modelService.save(b2bUnit);
		user.setDefaultB2BUnit(b2bUnit);

		modelService.save(user);
		userService.setCurrentUser(user);
		try
		{
			aud = commonI18NService.getCurrency("AUD");
		}
		catch (final UnknownIdentifierException e)
		{
			aud = modelService.create(CurrencyModel.class);
			aud.setIsocode("AUD");
			aud.setDigits(Integer.valueOf(2));
			modelService.save(aud);
		}

		final CatalogVersionModel ver = catalogVersionService.getCatalogVersion(TEST_CATALOG_1, SPRING_VERSION);
		unit = new UnitModel();
		unit.setCode("CAS");
		unit.setUnitType("case");
		unit.setConversion(Double.valueOf(1.0));

		catalogVersionService.setSessionCatalogVersions(Arrays.asList(new CatalogVersionModel[]
		{ ver }));
		modelService.saveAll(ver, unit);

		Mockito.when(unitService.getUnitForCode("CAS")).thenReturn(unit);
		final SalesOrderSimulateCartSyncHelper cartSyncHelper = new SalesOrderSimulateCartSyncHelper();
		productP1 = modelService.create(SABMAlcoholProductModel.class);
		productP1.setCode("P1");
		productP1.setName("Carlton dry", Locale.ENGLISH);
		productP1.setCatalogVersion(ver);
		productP1.setBrand("CARLTON1");
		productP1.setAbv("12.4");
		productP1.setUnit(unit);

		productP1.setCategoryAttribute(AlcoholCategoryAttribute.BEER);
		productP1.setCategoryVariety("v1");
		productP2 = modelService.create(SABMAlcoholProductModel.class);
		productP2.setCode("P2");
		productP2.setName("Carlton cold", Locale.ENGLISH);
		productP2.setCatalogVersion(ver);
		productP2.setCategoryAttribute(AlcoholCategoryAttribute.BEER);
		productP2.setCategoryVariety("v1");
		productP2.setBrand("CARLTON");
		productP2.setAbv("12.4");
		productP2.setUnit(unit);
		Mockito.when(productService.getProductForCode("P1")).thenReturn(productP1);
		Mockito.when(productService.getProductForCode("P2")).thenReturn(productP1);

		modelService.saveAll(productP1, productP2);

		cartSyncHelper.setProductService(productService);
		cartSyncHelper.setModelService(modelService);
		cartSyncHelper.setCommonI18NService(commonI18NService);
		//		cartService.setSalesOrderSimulateRestHandler(salesOrderSimulateRestHandler);
		cartSyncHelper.setUnitService(unitService);
		cartSyncHelper.setErrorEventFacade(errorEventFacade);
		//cartService.setCartConverter(cartConverter);
		//		cartService.setCartSyncHelper(cartSyncHelper);
		cartModel = modelService.create(CartModel.class);
		cartModel.setCurrency(aud);
		cartModel.setDate(new Date());
		cartModel.setUser(userService.getCurrentUser());

		final AbstractOrderEntryModel entry = modelService.create(CartEntryModel.class);
		final List<AbstractOrderEntryModel> entries = new ArrayList<AbstractOrderEntryModel>();
		entry.setProduct(productP1);
		entry.setEntryNumber(Integer.valueOf(1));
		entry.setQuantity(Long.valueOf("20"));
		entry.setUnit(unit);
		entry.setTaxValues(new ArrayList<TaxValue>());
		entry.setOrder(cartModel);
		entries.add(entry);
		cartModel.setEntries(entries);
		modelService.save(cartModel);
		modelService.refresh(cartModel);

	}


	//	@Test
	//	public void testEntryTotals() throws SalesOrderSimulateCartUpdateException, CartThresholdExceededException
	//	{
	//		//Given cartmodel and response for one cart item
	//
	//		final SalesOrderSimulateResponse response = getSalesOrderSimulateResponse(); //response
	//
	//		try
	//		{
	//
	//			Mockito.when(salesOrderSimulateRestHandler.sendPostRequest(Mockito.any(SalesOrderSimulateRequest.class))).thenReturn(
	//					response);
	//		}
	//		catch (final SABMIntegrationException e)
	//		{
	//			e.printStackTrace();
	//		}
	//
	//		// When I call updateCartAfterSalesOrderSimulate
	//		final Set<String> messageList = cartService.updateCartAfterSalesOrderSimulate(cartModel);
	//		Assert.assertNotNull(messageList);
	//
	//		//Then verify entry subtotals are as expected in cartModel
	//
	//		for (final AbstractOrderEntryModel entryItem : cartModel.getEntries())
	//		{
	//
	//			if (entryItem != null)
	//			{
	//				//free goods
	//
	//				if (entryItem.getIsFreeGood() != null && entryItem.getIsFreeGood().booleanValue())
	//				{
	//					Assert.assertEquals(03, entryItem.getQuantity().longValue());
	//				}
	//				else
	//				{
	//					//Before discount unit price
	//					Assert.assertEquals(Double.valueOf(10), entryItem.getBasePrice());
	//
	//
	//					//Total entry price after discount
	//					Assert.assertEquals(Double.valueOf(1980), entryItem.getTotalPrice());
	//
	//					//Item total gst
	//					if (entryItem.getTaxValues() != null)
	//					{
	//						for (final TaxValue taxValue : entryItem.getTaxValues())
	//						{
	//							if (SabmCoreConstants.GST == taxValue.getCode())
	//							{
	//								Assert.assertEquals(20, taxValue.getValue(), 0);
	//							}
	//						}
	//					}
	//
	//					//Item total wet
	//					if (entryItem.getTaxValues() != null)
	//					{
	//						for (final TaxValue taxValue : entryItem.getTaxValues())
	//						{
	//							if (SabmCoreConstants.WET == taxValue.getCode())
	//							{
	//								Assert.assertEquals(40, taxValue.getValue(), 0);
	//							}
	//						}
	//					}
	//
	//					//Item total Delivery cost
	//					if (entryItem.getDeliveryCost() != null)
	//					{
	//						Assert.assertEquals(10, entryItem.getDeliveryCost().doubleValue(), 0);
	//					}
	//					//
	//					Assert.assertNotNull("DiscountInfo null", entryItem.getOfferInfo());
	//					Assert.assertNotNull("DiscountInfo null", entryItem.getOfferInfo().get(0).getScaleAmount());
	//
	//				}
	//			}
	//		}
	//	}


	//	@Test
	//	public void testCartTotals()
	//	{
	//
	//		CartModel cartModel1 = modelService.create(CartModel.class);
	//		cartModel.setCurrency(aud);
	//		cartModel.setDate(new Date());
	//		cartModel.setUser(userService.getCurrentUser());
	//		final DefaultSABMCalculationService calService = new DefaultSABMCalculationService();
	//
	//		final B2BUnitModel b2bUnit = new B2BUnitModel();
	//
	//
	//		b2bUnit.setSalesOrgId("orgId");
	//		final SalesDataModel salesData = new SalesDataModel();
	//		salesData.setDistributionChannel("distributionChannel");
	//		salesData.setDivision("D1");
	//		b2bUnit.setSalesData(salesData);
	//		cartModel1.setUnit(b2bUnit);
	//
	//		cartService.removeSessionCart();
	//		cartModel1 = cartService.getSessionCart();
	//		cartModel1.setCurrency(aud);
	//		cartModel1.setDate(new Date());
	//		cartModel1.setUser(userService.getCurrentUser());
	//
	//		final AbstractOrderEntryModel entry1 = modelService.create(CartEntryModel.class);
	//		final List<AbstractOrderEntryModel> entries = new ArrayList<AbstractOrderEntryModel>();
	//		entry1.setProduct(productP1);
	//		entry1.setEntryNumber(Integer.valueOf(1));
	//		entry1.setQuantity(Long.valueOf("20"));
	//		entry1.setUnit(unit);
	//
	//		entry1.setOrder(cartModel1);
	//		entry1.setTotalPrice(Double.valueOf(100));
	//
	//
	//		final TaxValue gstTaxValue1 = new TaxValue(SabmCoreConstants.GST, 20, true, "AUD");
	//		final TaxValue wetTaxValue1 = new TaxValue(SabmCoreConstants.WET, 10, true, "AUD");
	//		final Collection<TaxValue> entryTaxValues1 = new ArrayList<TaxValue>();
	//
	//		entryTaxValues1.add(gstTaxValue1);
	//		entryTaxValues1.add(wetTaxValue1);
	//		entry1.setTaxValues(entryTaxValues1);
	//		entry1.setDeliveryCost(Double.valueOf(10));
	//		final List<DiscountValue> discounts1 = new ArrayList<DiscountValue>();
	//
	//		discounts1.add(new DiscountValue(SabmCoreConstants.ENTRY_TOTAL_DISCOUNT, -10, true, "AUD"));
	//		entry1.setDiscountValues(discounts1);
	//
	//		entries.add(entry1);
	//
	//		final AbstractOrderEntryModel entry2 = modelService.create(CartEntryModel.class);
	//		entry2.setProduct(productP2);
	//		entry1.setEntryNumber(Integer.valueOf(2));
	//		entry2.setQuantity(Long.valueOf("20"));
	//		entry2.setUnit(unit);
	//		entry2.setTaxValues(new ArrayList<TaxValue>());
	//		entry2.setTotalPrice(Double.valueOf(100));
	//		final TaxValue gstTaxValue2 = new TaxValue(SabmCoreConstants.GST, 10, true, "AUD");
	//		final TaxValue wetTaxValue2 = new TaxValue(SabmCoreConstants.WET, 10, true, "AUD");
	//		final Collection<TaxValue> entryTaxValues2 = new ArrayList<TaxValue>();
	//		entryTaxValues2.add(gstTaxValue2);
	//		entryTaxValues2.add(wetTaxValue2);
	//		entry2.setTaxValues(entryTaxValues2);
	//		entry2.setDeliveryCost(Double.valueOf(15));
	//		final List<DiscountValue> discounts2 = new ArrayList<DiscountValue>();
	//		discounts2.add(new DiscountValue(SabmCoreConstants.ENTRY_TOTAL_DISCOUNT, -20, true, "AUD"));
	//		entry2.setDiscountValues(discounts2);
	//		entry2.setOrder(cartModel1);
	//		entries.add(entry2);
	//
	//
	//		cartModel1.setEntries(entries);
	//		modelService.save(cartModel1);
	//		modelService.refresh(cartModel1);
	//
	//
	//		// Given cartModel with entry totals updates as above
	//
	//		// when cal service invoked
	////		calService.calculateTotals(cartModel1);
	//
	//		//Then cart totals gets updates
	//
	//		Assert.assertEquals(Double.valueOf(200), cartModel1.getNetAmount());
	//
	//		Assert.assertEquals(Double.valueOf(-30), cartModel1.getTotalDiscounts());
	//
	//		for (final TaxValue tax : cartModel1.getTotalTaxValues())
	//		{
	//			if (StringUtils.equalsIgnoreCase(tax.getCode(), SabmCoreConstants.GST))
	//			{
	//				Assert.assertEquals(Double.valueOf(30), Double.valueOf(tax.getValue()));
	//			}
	//			if (StringUtils.equalsIgnoreCase(tax.getCode(), SabmCoreConstants.WET))
	//			{
	//				Assert.assertEquals(Double.valueOf(20), Double.valueOf(tax.getValue()));
	//			}
	//
	//		}
	//		Assert.assertEquals("Cart Total delivery cost", Double.valueOf(25), cartModel1.getDeliveryCost());
	//		Assert.assertEquals("Cart SubTotal", Double.valueOf(200 + 20 + 25), cartModel1.getSubtotal()); // totalPrice(after discount)+wet+delivery
	//		Assert.assertEquals("Cart Total price", Double.valueOf(200 + 20 + 25 + 30), cartModel1.getTotalPrice());
	//
	//	}

	private SalesOrderSimulateResponse getSalesOrderSimulateResponse()
	{

		final SalesOrderSimulateResponse response = new SalesOrderSimulateResponse();
		final SalesOrderResHeder resHeader = new SalesOrderResHeder();
		resHeader.setRequestedDeliveryDate("20151220");
		response.setSalesOrderResHeder(resHeader);

		final SalesOrderResItem resItem1 = new SalesOrderResItem();
		resItem1.setMaterialNumber("P1");
		resItem1.setMaterialEntered("P1");
		resItem1.setMaterialQuantity("20");
		resItem1.setLineNumber("001");
		resItem1.setUnitOfMeasure("CAS");
		final List<SalesOrderItemCondition> item1PriceConditions = new ArrayList<SalesOrderItemCondition>();
		final SalesOrderItemCondition item1PriceCondition = new SalesOrderResItem.SalesOrderItemCondition();

		item1PriceCondition.setConditionType("PR00");
		item1PriceCondition.setCalculationType("B");
		item1PriceCondition.setConditionPricingUnit("1");
		item1PriceCondition.setConditionAmount("10.00");
		item1PriceCondition.setTotalCondValue("2000.00");
		item1PriceCondition.setConditionUOM("CAS");
		final SalesOrderItemScheduling item1Scale1 = new SalesOrderResItem.SalesOrderItemScheduling();
		item1Scale1.setConfirmedQty("20");
		item1Scale1.setItemNumber("10");
		item1PriceConditions.add(item1PriceCondition);

		// Discount condition
		final SalesOrderItemCondition item1PriceCondition2 = new SalesOrderResItem.SalesOrderItemCondition();
		item1PriceCondition2.setConditionType("YDA0");
		item1PriceCondition2.setCalculationType("C");
		item1PriceCondition2.setConditionPricingUnit("1");
		item1PriceCondition2.setConditionAmount("-1.00");
		item1PriceCondition2.setTotalCondValue("-20.00");
		item1PriceCondition2.setConditionUOM("CAS");
		item1PriceConditions.add(item1PriceCondition2);
		final SalesOrderItemScales scale = new SalesOrderItemScales();
		scale.setAmount("10");
		scale.setCalculationType("C");
		scale.setScaleQuantity("10");
		item1PriceCondition2.getSalesOrderItemScales().add(scale);

		// gst condition
		final SalesOrderItemCondition item1PriceCondition3 = new SalesOrderResItem.SalesOrderItemCondition();
		item1PriceCondition3.setConditionType("MWST");
		item1PriceCondition3.setCalculationType("A");
		item1PriceCondition3.setConditionPricingUnit("1");
		item1PriceCondition3.setConditionAmount("1.00");
		item1PriceCondition3.setTotalCondValue("20.00");
		item1PriceCondition3.setConditionUOM("CAS");
		item1PriceConditions.add(item1PriceCondition3);



		//wet condition
		final SalesOrderItemCondition item1PriceCondition4 = new SalesOrderResItem.SalesOrderItemCondition();
		item1PriceCondition4.setConditionType("Z9W0");
		item1PriceCondition4.setCalculationType("A");
		item1PriceCondition4.setConditionPricingUnit("1");
		item1PriceCondition4.setConditionAmount("2.00");
		item1PriceCondition4.setTotalCondValue("40.00");
		item1PriceCondition4.setConditionUOM("CAS");
		item1PriceConditions.add(item1PriceCondition4);

		//Delivery Cost
		final SalesOrderItemCondition item1PriceCondition5 = new SalesOrderResItem.SalesOrderItemCondition();
		item1PriceCondition5.setConditionType("Z3F1");
		item1PriceCondition5.setCalculationType("C");
		item1PriceCondition5.setConditionPricingUnit("1");
		item1PriceCondition5.setConditionAmount("0.50");
		item1PriceCondition5.setTotalCondValue("10.00");
		item1PriceCondition5.setConditionUOM("CAS");
		item1PriceConditions.add(item1PriceCondition5);

		resItem1.getSalesOrderItemCondition().addAll(item1PriceConditions);
		response.getSalesOrderResItem().add(resItem1);

		//freegood

		final SalesOrderResItem freeGood1 = new SalesOrderResItem();

		freeGood1.setMaterialNumber("P2");
		freeGood1.setMaterialEntered("P2");
		freeGood1.setMaterialQuantity("3");
		freeGood1.setFreeGoodsFlag("X");
		freeGood1.setUnitOfMeasure("CAS");
		freeGood1.setSalesItemRelFreeGoods("001");
		final List<SalesOrderFreeGoods> saleGoodsConditions = new ArrayList<SalesOrderFreeGoods>();
		final SalesOrderFreeGoods freeGoodsCondition = new SalesOrderFreeGoods();
		freeGoodsCondition.setFreeGoodsQty("03");
		freeGoodsCondition.setMinimumQty("10");
		//freegood scale
		saleGoodsConditions.add(freeGoodsCondition);
		freeGood1.getSalesOrderFreeGoods().addAll(saleGoodsConditions);

		final SalesOrderItemCondition itemFreeGoodPriceCond = new SalesOrderResItem.SalesOrderItemCondition();
		itemFreeGoodPriceCond.setConditionType("PR00");
		itemFreeGoodPriceCond.setCalculationType("C");
		itemFreeGoodPriceCond.setConditionPricingUnit("1");
		itemFreeGoodPriceCond.setConditionAmount("10.00");
		itemFreeGoodPriceCond.setConditionUOM("CAS");
		itemFreeGoodPriceCond.setTotalCondValue("03");
		freeGood1.getSalesOrderItemCondition().add(itemFreeGoodPriceCond);

		response.getSalesOrderResItem().add(freeGood1);

		return response;
	}

}
