/**
 *
 */
package com.sabmiller.facades.deal;

import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.times;

import de.hybris.bootstrap.annotations.UnitTest;
import de.hybris.platform.b2b.model.B2BCustomerModel;
import de.hybris.platform.b2b.model.B2BUnitModel;
import de.hybris.platform.b2bacceleratorservices.company.B2BCommerceUnitService;
import de.hybris.platform.commercefacades.product.data.CategoryData;
import de.hybris.platform.commercefacades.product.data.ProductData;
import de.hybris.platform.converters.Populator;
import de.hybris.platform.servicelayer.dto.converter.Converter;
import de.hybris.platform.servicelayer.session.SessionService;
import de.hybris.platform.servicelayer.user.UserService;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Map;

import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.MockitoAnnotations;
//import org.powermock.api.mockito.PowerMockito;
//import org.powermock.core.classloader.annotations.PrepareForTest;
//import org.powermock.modules.junit4.PowerMockRunner;

import com.apb.facades.deal.data.AsahiDealData;
import com.google.common.collect.Maps;
import com.sabmiller.core.b2b.services.SABMDeliveryDateCutOffService;
import com.sabmiller.core.b2b.services.SabmB2BUnitService;
import com.sabmiller.core.constants.SabmCoreConstants;
import com.sabmiller.core.deals.services.DealsService;
import com.sabmiller.core.model.AsahiB2BUnitModel;
import com.sabmiller.core.model.AsahiDealModel;
import com.sabmiller.core.model.DealModel;
import com.sabmiller.facades.customer.SABMCustomerFacade;
import com.sabmiller.facades.deal.data.DealConditionData;
import com.sabmiller.facades.deal.data.DealConditionGroupData;
import com.sabmiller.facades.deal.data.DealData;
import com.sabmiller.facades.deal.data.DealJson;
import com.sabmiller.facades.deal.impl.SABMDealsSearchFacadeImpl;
import com.sabmiller.facades.deal.repdriven.data.RepDrivenDealConditionData;



/**
 * @author xiaowu.a.zhang
 * @data 2015-12-18
 */
@UnitTest
//@RunWith(PowerMockRunner.class)
public class SABMDealsSearchFacadeTest
{
	protected static final String DEAL_CATEGORY_LABEL = "category";

	@Mock
	private DealsService dealsService;
	@Mock
	private Converter<DealModel, DealData> dealConverter;
	@Mock
	private List<String> brand;
	@Mock
	private List<String> category;
	@Mock
	private UserService userService;
	@Mock
	private B2BCustomerModel b2bCustomer;
	@Mock
	private B2BUnitModel b2bUnitModel;
	@Mock
	private SabmB2BUnitService b2bUnitService;
	@Mock
	private Converter<DealModel, DealJson> dealJsonConverter;
	@Mock
	private B2BCommerceUnitService b2bCommerceUnitService;
	@Mock
	private Populator<DealModel, DealJson> dealTitlePopulator;

	private SABMDeliveryDateCutOffService deliveryDateCutOffService;
	@Mock
	private SessionService sessionService;
	@Mock
	private SABMCustomerFacade customerFacade;

	@Mock
	private Converter<AsahiDealModel, AsahiDealData> asahiDealDataConverter;

	@Mock
	private AsahiB2BUnitModel asahiB2bUnitModel;

	@InjectMocks
	private final SABMDealsSearchFacadeImpl sabmDealsSearchFacadeImpl = new SABMDealsSearchFacadeImpl();


	@Before
	public void setUp()
	{
		MockitoAnnotations.initMocks(this);

		brand = Arrays.asList("brand1", "brand2", "brand3");
		category = Arrays.asList("category1", "category2", "category3");


		final List<DealModel> dealModels = new ArrayList<DealModel>();
		final DealModel dealModel1 = mock(DealModel.class);
		final DealModel dealModel2 = mock(DealModel.class);
		final DealModel dealModel3 = mock(DealModel.class);
		final DealModel dealModel4 = mock(DealModel.class);
		dealModels.add(dealModel1);
		dealModels.add(dealModel2);
		dealModels.add(dealModel3);
		dealModels.add(dealModel4);

		final DealData dealData1 = mock(DealData.class);
		final DealData dealData2 = mock(DealData.class);
		final DealData dealData3 = mock(DealData.class);
		final DealData dealData4 = mock(DealData.class);

		final DealConditionGroupData dealConditionGroupData1 = mock(DealConditionGroupData.class);
		final DealConditionGroupData dealConditionGroupData2 = mock(DealConditionGroupData.class);
		final DealConditionGroupData dealConditionGroupData3 = mock(DealConditionGroupData.class);
		final DealConditionGroupData dealConditionGroupData4 = mock(DealConditionGroupData.class);

		final DealConditionData dealConditionData1 = mock(DealConditionData.class);
		final DealConditionData dealConditionData2 = mock(DealConditionData.class);
		final DealConditionData dealConditionData3 = mock(DealConditionData.class);
		final DealConditionData dealConditionData4 = mock(DealConditionData.class);

		final ProductData productData1 = mock(ProductData.class);
		final ProductData productData2 = mock(ProductData.class);
		final ProductData productData3 = mock(ProductData.class);
		final ProductData productData4 = mock(ProductData.class);

		final CategoryData categoryData1 = mock(CategoryData.class);
		final CategoryData categoryData2 = mock(CategoryData.class);
		final CategoryData categoryData3 = mock(CategoryData.class);
		final CategoryData categoryData4 = mock(CategoryData.class);

		final List<CategoryData> categoryDatas1 = new ArrayList<CategoryData>();
		final List<CategoryData> categoryDatas2 = new ArrayList<CategoryData>();
		final List<CategoryData> categoryDatas3 = new ArrayList<CategoryData>();
		final List<CategoryData> categoryDatas4 = new ArrayList<CategoryData>();
		categoryDatas1.add(categoryData1);
		categoryDatas2.add(categoryData2);
		categoryDatas3.add(categoryData3);
		categoryDatas4.add(categoryData4);

		final List<DealConditionData> dealConditionDatas1 = new ArrayList<DealConditionData>();
		final List<DealConditionData> dealConditionDatas2 = new ArrayList<DealConditionData>();
		final List<DealConditionData> dealConditionDatas3 = new ArrayList<DealConditionData>();
		final List<DealConditionData> dealConditionDatas4 = new ArrayList<DealConditionData>();
		dealConditionDatas1.add(dealConditionData1);
		dealConditionDatas2.add(dealConditionData2);
		dealConditionDatas3.add(dealConditionData3);
		dealConditionDatas4.add(dealConditionData4);

		given(categoryData1.getCode()).willReturn("category1");
		given(categoryData2.getCode()).willReturn("category2");
		given(categoryData3.getCode()).willReturn("category2");
		given(categoryData4.getCode()).willReturn("category3");
		given(categoryData1.getName()).willReturn("categoryName1");
		given(categoryData2.getName()).willReturn("categoryName2");
		given(categoryData3.getName()).willReturn("categoryName2");
		given(categoryData4.getName()).willReturn("categoryName3");

		given(productData1.getCode()).willReturn("product1");
		given(productData2.getCode()).willReturn("product2");
		given(productData3.getCode()).willReturn("product3");
		given(productData4.getCode()).willReturn("product4");
		given(productData1.getCategories()).willReturn(categoryDatas1);
		given(productData2.getCategories()).willReturn(categoryDatas2);
		given(productData3.getCategories()).willReturn(categoryDatas3);
		given(productData4.getCategories()).willReturn(categoryDatas4);
		given(productData1.getBrand()).willReturn("brand1");
		given(productData2.getBrand()).willReturn("brand1");
		given(productData3.getBrand()).willReturn("brand2");
		given(productData4.getBrand()).willReturn("brand3");

		given(dealConditionData1.getConditionType()).willReturn("PRODUCTCONDITION");
		given(dealConditionData2.getConditionType()).willReturn("PRODUCTCONDITION");
		given(dealConditionData3.getConditionType()).willReturn("PRODUCTCONDITION");
		given(dealConditionData4.getConditionType()).willReturn("PRODUCTCONDITION");
		given(dealConditionData1.getProduct()).willReturn(productData1);
		given(dealConditionData2.getProduct()).willReturn(productData2);
		given(dealConditionData3.getProduct()).willReturn(productData3);
		given(dealConditionData4.getProduct()).willReturn(productData4);


		given(dealConditionGroupData1.getDealConditions()).willReturn(dealConditionDatas1);
		given(dealConditionGroupData2.getDealConditions()).willReturn(dealConditionDatas2);
		given(dealConditionGroupData3.getDealConditions()).willReturn(dealConditionDatas3);
		given(dealConditionGroupData4.getDealConditions()).willReturn(dealConditionDatas4);


		given(dealData1.getDealConditionGroupData()).willReturn(dealConditionGroupData1);
		given(dealData2.getDealConditionGroupData()).willReturn(dealConditionGroupData2);
		given(dealData3.getDealConditionGroupData()).willReturn(dealConditionGroupData3);
		given(dealData4.getDealConditionGroupData()).willReturn(dealConditionGroupData4);
		given(userService.getCurrentUser()).willReturn(b2bCustomer);
		given(b2bCustomer.getDefaultB2BUnit()).willReturn(b2bUnitModel);
		given(dealConverter.convert(dealModel1)).willReturn(dealData1);
		given(dealConverter.convert(dealModel2)).willReturn(dealData2);
		given(dealConverter.convert(dealModel3)).willReturn(dealData3);
		given(dealConverter.convert(dealModel4)).willReturn(dealData4);
	}

	@Test
	public void testGetChangedDealsTitleForCurrentUser()
	{

		final Map<String, List<DealModel>> changedDeals = Maps.newHashMap();

		final DealModel deal1 = mock(DealModel.class);
		final DealModel deal2 = mock(DealModel.class);
		final DealModel deal3 = mock(DealModel.class);
		final DealModel deal4 = mock(DealModel.class);

		given(deal1.getCode()).willReturn("TestCode1");
		given(deal2.getCode()).willReturn("TestCode2");
		given(deal3.getCode()).willReturn("TestCode3");
		given(deal4.getCode()).willReturn("TestCode4");

		final List<DealModel> activated = new ArrayList<>();
		final List<DealModel> deactivated = new ArrayList<>();

		activated.add(deal1);
		activated.add(deal2);
		deactivated.add(deal3);
		deactivated.add(deal4);

		changedDeals.put(SabmCoreConstants.ACTIVATED_DEAL_KEY, activated);
		changedDeals.put(SabmCoreConstants.DEACTIVATED_DEAL_KEY, deactivated);

		final DealJson dealJson1 = mock(DealJson.class);
		final DealJson dealJson2 = mock(DealJson.class);
		final DealJson dealJson3 = mock(DealJson.class);
		final DealJson dealJson4 = mock(DealJson.class);

		given(dealJson1.getTitle()).willReturn("TestTitle1");
		given(dealJson2.getTitle()).willReturn("TestTitle2");
		given(dealJson3.getTitle()).willReturn("TestTitle3");
		given(dealJson4.getTitle()).willReturn("TestTitle4");

		final List<DealJson> activatedJson = new ArrayList<DealJson>();
		final List<DealJson> deactivatedJson = new ArrayList<DealJson>();

		activatedJson.add(dealJson1);
		activatedJson.add(dealJson2);
		deactivatedJson.add(dealJson3);
		deactivatedJson.add(dealJson4);
		given(dealJsonConverter.convert(deal1)).willReturn(dealJson1);
		given(dealJsonConverter.convert(deal2)).willReturn(dealJson2);
		given(dealJsonConverter.convert(deal3)).willReturn(dealJson3);
		given(dealJsonConverter.convert(deal4)).willReturn(dealJson4);

		final List<RepDrivenDealConditionData> changedDealsData = new ArrayList<>();
		final RepDrivenDealConditionData conditionData1 = mock(RepDrivenDealConditionData.class);
		final RepDrivenDealConditionData conditionData2 = mock(RepDrivenDealConditionData.class);
		final RepDrivenDealConditionData conditionData3 = mock(RepDrivenDealConditionData.class);
		final RepDrivenDealConditionData conditionData4 = mock(RepDrivenDealConditionData.class);

		given(conditionData1.isStatus()).willReturn(true);
		given(conditionData1.getDealConditionNumber()).willReturn("TestCode1");
		given(conditionData2.isStatus()).willReturn(true);
		given(conditionData2.getDealConditionNumber()).willReturn("TestCode2");
		given(conditionData3.isStatus()).willReturn(false);
		given(conditionData3.getDealConditionNumber()).willReturn("TestCode3");
		given(conditionData4.isStatus()).willReturn(false);
		given(conditionData4.getDealConditionNumber()).willReturn("TestCode4");

		given(dealsService.getDeal("TestCode1")).willReturn(deal1);
		given(dealsService.getDeal("TestCode2")).willReturn(deal2);
		given(dealsService.getDeal("TestCode3")).willReturn(deal3);
		given(dealsService.getDeal("TestCode4")).willReturn(deal4);

		changedDealsData.add(conditionData1);
		changedDealsData.add(conditionData2);
		changedDealsData.add(conditionData3);
		changedDealsData.add(conditionData4);

		//final Map<String, List<String>> changedDealsTitle = sabmDealsSearchFacadeImpl
		//	.getChangedDealsTitleForCurrentUser(changedDealsData);

		//Assert.assertEquals(Boolean.TRUE, changedDealsTitle.containsKey(SabmCoreConstants.ACTIVATED_DEAL_KEY));
		//Assert.assertEquals(2, changedDealsTitle.get(SabmCoreConstants.ACTIVATED_DEAL_KEY).size());

		//Assert.assertEquals(2, changedDealsTitle.get(SabmCoreConstants.DEACTIVATED_DEAL_KEY).size());

		//Assert.assertEquals("null&nbsp;(de1)", changedDealsTitle.get(SabmCoreConstants.ACTIVATED_DEAL_KEY).get(0));
	}

	@Test
	public void testGetSpecificDeals()
	{
		final List<DealModel> dealModels = new ArrayList<DealModel>();
		final DealModel dealModel1 = mock(DealModel.class);
		dealModel1.setInStore(Boolean.TRUE);
		dealModels.add(dealModel1);


		final String uid = "ricky.ponting@testsample123.com";
		final boolean inStore = true;

		final B2BCustomerModel customer = new B2BCustomerModel();
		final B2BUnitModel b2bUnit = mock(B2BUnitModel.class);
		customer.setDefaultB2BUnit(b2bUnit);

		given(b2bCommerceUnitService.getCustomerForUid(uid)).willReturn(customer);
		given(dealsService.getSpecificDeals(b2bUnit, inStore)).willReturn(dealModels);

		//final List<DealJson> dealJsons = sabmDealsSearchFacadeImpl.getSpecificDeals(uid, inStore);

		//	Assert.assertEquals(dealJsons.size(), 1);
	}

	@Test
	//@PrepareForTest(de.hybris.platform.util.Config.class)
	public void testHasUpcomingDeals()
	{
		/*
		 * Due to it need Powermock and latest release of Mockito, it only can run at local.
		 */

		//		final Calendar calendar = Calendar.getInstance();
		//
		//		calendar.add(Calendar.DAY_OF_YEAR, 1);
		//		final Date deliveryDate = calendar.getTime();
		//		when(sessionService.getAttribute(SabmCoreConstants.SESSION_ATTR_DELIVERY_DATE)).thenReturn(deliveryDate);
		//
		//		final Set<Date> enabledDates = new HashSet<Date>();
		//		enabledDates.add(calendar.getTime()); // Delivery date
		//		calendar.add(Calendar.DAY_OF_YEAR, 1);
		//		enabledDates.add(calendar.getTime()); // Delivery date + 1
		//		when(customerFacade.enabledCalendarDates()).thenReturn(enabledDates);
		//
		//		final List<DealModel> deals = new ArrayList<DealModel>();
		//		deals.add(mock(DealModel.class));
		//
		//		PowerMockito.mockStatic(de.hybris.platform.util.Config.class);
		//		when(de.hybris.platform.util.Config.getInt("deal.valid.next.default.day", 14)).thenReturn(14);
		//		when(dealsService.getDeals(Mockito.any(B2BUnitModel.class), Mockito.any(Date.class), Mockito.any(Date.class)))
		//				.thenReturn(deals);
		//
		//		enabledDates.iterator().next();
		//		final Date qualifiedDate = enabledDates.iterator().next();
		//		when(dealsService.getValidationDeals(qualifiedDate, deals, Boolean.TRUE)).thenReturn(deals);
		//		final List<List<DealModel>> list = new ArrayList<List<DealModel>>();
		//		list.add(deals);
		//		when(dealsService.composeComplexFreeProducts(deals)).thenReturn(list);
		//
		//		@SuppressWarnings("unchecked")
		//		final Converter<List<DealModel>, DealJson> dealJsonConverter = mock(Converter.class);
		//		final List<DealJson> dealJsons = new ArrayList<DealJson>();
		//		dealJsons.add(mock(DealJson.class));
		//		when(dealJsonConverter.convert(deals)).thenReturn(dealJsons.get(0));
		//
		//		Assert.assertTrue(sabmDealsSearchFacadeImpl.hasUpcomingDeals());
		//
		//		final Set<Date> newEnabledDates = new HashSet<Date>();
		//		when(customerFacade.enabledCalendarDates()).thenReturn(newEnabledDates);
		//		Assert.assertTrue(!sabmDealsSearchFacadeImpl.hasUpcomingDeals());
	}

	@Test
	public void testgetSGASpecificDeals()
	{
		final AsahiDealModel deal = Mockito.mock(AsahiDealModel.class);
		Mockito.when(deal.getCode()).thenReturn("sga001");
		Mockito.when(asahiB2bUnitModel.getAsahiDeals()).thenReturn(Arrays.asList(deal));
		Mockito.when(dealsService.getSGASpecificDeals(Mockito.any())).thenReturn(Arrays.asList(deal));
		final AsahiDealData dealData = new AsahiDealData();
		dealData.setCode("sga001");
		given(asahiDealDataConverter.convert(Mockito.any())).willReturn(dealData);
		final List<AsahiDealData> deals = sabmDealsSearchFacadeImpl.getSGASpecificDeals(asahiB2bUnitModel);
		Assert.assertNotNull(dealData);
		Assert.assertEquals(Integer.valueOf(1).intValue(), deals.size());
	}

	@Test
	public void testGetSGADealsTitleForProductAndUnit()
	{
		final AsahiDealModel deal = Mockito.mock(AsahiDealModel.class);
		Mockito.when(deal.getCode()).thenReturn("sga001");
		Mockito.when(dealsService.getSGADealsForProductAndUnit(Mockito.anyString(), Mockito.any()))
				.thenReturn(Arrays.asList(deal));
		Mockito.when(asahiB2bUnitModel.getAsahiDeals()).thenReturn(Arrays.asList(deal));
		final AsahiDealData dealData = new AsahiDealData();
		dealData.setCode("sga001");
		dealData.setTitle("BUY 1 X and GET 1 Y FREE");
		given(asahiDealDataConverter.convert(Mockito.any())).willReturn(dealData);
		final List<AsahiDealData> deals = sabmDealsSearchFacadeImpl.getSGADealsDataForProductAndUnit("pcode", asahiB2bUnitModel);
		Assert.assertNotNull(dealData);
		Assert.assertEquals(Integer.valueOf(1).intValue(), deals.size());
		Assert.assertEquals("BUY 1 X and GET 1 Y FREE", deals.get(0).getTitle());
	}

	@Test
	public void testSaveAsahiRepDealChange()
	{
		Mockito.when(customerFacade.getB2BUnitForId(Mockito.anyString())).thenReturn(asahiB2bUnitModel);
		sabmDealsSearchFacadeImpl.saveAsahiRepDealChange("0100000000", Arrays.asList("3021000"), Arrays.asList("30218765"),
				Arrays.asList("testuser@test.com"), "Deals Available to you");
		Mockito.verify(customerFacade, times(1)).getB2BUnitForId(Mockito.anyString());
	}


	@Test
	public void testGetCustomerSpecificDeals()
	{
		final AsahiDealData dealData = new AsahiDealData();
		dealData.setCode("sga001");
		final AsahiDealModel deal = Mockito.mock(AsahiDealModel.class);
		Mockito.when(deal.getCode()).thenReturn("sga001");
		given(asahiDealDataConverter.convert(Mockito.any())).willReturn(dealData);
		Mockito.when(asahiB2bUnitModel.getAsahiDeals()).thenReturn(Arrays.asList(deal));
		Mockito.when(dealsService.getCustomerSpecificDeals(asahiB2bUnitModel)).thenReturn(Arrays.asList(deal));
		final List<AsahiDealData> deals = sabmDealsSearchFacadeImpl.getCustomerSpecificDeals(asahiB2bUnitModel);
		Assert.assertNotNull(dealData);
		Assert.assertEquals(Integer.valueOf(1).intValue(), deals.size());
	}

}