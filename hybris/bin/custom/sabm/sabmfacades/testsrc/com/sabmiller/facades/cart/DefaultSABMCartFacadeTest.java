/**
 *
 */
package com.sabmiller.facades.cart;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;
import static org.mockito.ArgumentMatchers.argThat;
import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;
import org.mockito.ArgumentMatcher;
import de.hybris.bootstrap.annotations.UnitTest;
import de.hybris.platform.acceleratorservices.config.SiteConfigService;
import de.hybris.platform.b2b.model.B2BCustomerModel;
import de.hybris.platform.b2b.model.B2BUnitModel;
import de.hybris.platform.b2bacceleratorservices.company.B2BCommerceUnitService;
import de.hybris.platform.commercefacades.order.data.CartData;
import de.hybris.platform.commercefacades.order.data.OrderEntryData;
import de.hybris.platform.commercefacades.product.data.ProductData;
import de.hybris.platform.commercefacades.user.data.AddressData;
import de.hybris.platform.commerceservices.service.data.CommerceCheckoutParameter;
import de.hybris.platform.converters.Populator;
import de.hybris.platform.core.model.order.AbstractOrderEntryModel;
import de.hybris.platform.core.model.order.CartEntryModel;
import de.hybris.platform.core.model.order.CartModel;
import de.hybris.platform.core.model.order.delivery.DeliveryModeModel;
import de.hybris.platform.core.model.user.AddressModel;
import de.hybris.platform.order.CartService;
import de.hybris.platform.servicelayer.dto.converter.Converter;
import de.hybris.platform.servicelayer.model.ModelService;
import de.hybris.platform.servicelayer.session.SessionService;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.Date;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.apache.commons.collections4.CollectionUtils;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.MockitoAnnotations;
import org.mockito.Spy;
import org.mockito.invocation.InvocationOnMock;
import org.mockito.junit.MockitoJUnitRunner;
import org.mockito.stubbing.Answer;

import com.apb.core.deals.strategies.AsahiDealValidationStrategy;
import com.apb.core.util.AsahiSiteUtil;
import com.apb.product.strategy.AsahiInclusionExclusionProductStrategy;
import com.google.common.collect.Lists;
import com.google.common.collect.Sets;
import com.sabmiller.core.b2b.services.SABMDeliveryDateCutOffService;
import com.sabmiller.core.b2b.services.SabmB2BUnitService;
import com.sabmiller.core.cart.service.SABMCartService;
import com.sabmiller.core.constants.SabmCoreConstants;
import com.sabmiller.core.deals.dao.DealsDao;
import com.sabmiller.core.deals.services.DealConditionService;
import com.sabmiller.core.deals.services.DealsService;
import com.sabmiller.core.deals.services.response.ConflictGroup;
import com.sabmiller.core.deals.services.response.ConflictGroup.Conflict;
import com.sabmiller.core.deals.services.response.DealQualificationResponse;
import com.sabmiller.core.enums.DealConditionStatus;
import com.sabmiller.core.enums.OrderSimulationStatus;
import com.sabmiller.core.enums.PackType;
import com.sabmiller.core.model.AsahiDealModel;
import com.sabmiller.core.model.CartDealConditionModel;
import com.sabmiller.core.model.DealConditionGroupModel;
import com.sabmiller.core.model.DealModel;
import com.sabmiller.core.model.DeliveryDefaultAddressModel;
import com.sabmiller.core.model.FreeGoodsDealBenefitModel;
import com.sabmiller.core.model.ShippingCarrierModel;
import com.sabmiller.facades.cart.impl.DealAppliedTimesHelper;
import com.sabmiller.facades.cart.impl.DefaultSABMCartFacade;
import com.sabmiller.facades.deal.data.CartDealsJson;
import com.sabmiller.facades.deal.data.ConflictDealJson;
import com.sabmiller.facades.deal.data.DealBaseProductJson;
import com.sabmiller.facades.deal.data.DealFreeProductJson;
import com.sabmiller.facades.deal.data.DealJson;
import com.sabmiller.facades.deal.data.DealRangeJson;


/**
 * @author xue.zeng
 *
 */
@UnitTest
@RunWith(MockitoJUnitRunner.class)
public class DefaultSABMCartFacadeTest
{
	@Spy
	@InjectMocks
	private final DefaultSABMCartFacade sabmCartFacade = new DefaultSABMCartFacade();

	@Mock
	private CartService cartService;
	@Mock
	private DealsService dealsService;

	@Mock
	private B2BCommerceUnitService b2bCommerceUnitService;
	@Mock
	private DealConditionService dealConditionService;
	@Mock
	private Converter<List<DealModel>, ConflictDealJson> conflictDealJsonConverter;

	@Mock
	private SABMCartService sabmCartService;
	@Mock
	private SabmB2BUnitService b2bUnitService;
	@Mock
	private Converter<AddressModel, AddressData> addressConverter;
	@Mock
	private SessionService sessionService;
	@Mock
	private Populator<List<DealModel>, DealJson> dealProductPopulator;
	@Mock
	private Populator<List<DealModel>, DealJson> dealTitlePopulator;
	@Mock
	private Converter<CartModel, CartData> miniCartConverter;
	@Mock
	private DealAppliedTimesHelper dealAppliedTimesHelper;
	@Mock
	private ShippingCarrierModel b2bUnitCarrier, cartModelCarrier;
	@Mock
	private SiteConfigService siteConfigService;
	@Mock
	private ModelService modelService;
	@Mock
	private CartDealConditionModel dealConditionModel;
	@Mock
	private AbstractOrderEntryModel entry;
	@Mock
	private AsahiInclusionExclusionProductStrategy inclusionExclusionProductStrategy;
	@Mock
	private SABMDeliveryDateCutOffService sabmDeliveryDateCutOffService;
	@Mock
	private CartData cartData;
	@Mock
	private CartModel cartModel;
	@Mock
	private CartEntryModel freeCartEntry;
	@Mock
	private AsahiSiteUtil asahiSiteUtil;
	@Mock
	private DealsDao dealsDao;
	@Mock
	private AsahiDealModel deal;
	@Mock
	private AsahiDealValidationStrategy asahiDealValidationStrategy;
	@Mock
	private B2BUnitModel b2bUnitModel;
	@Mock
	private DeliveryModeModel deliveryMode;

	private final static String DEAL_CODE = "deal_12";

	@Before
	public void setUp()
	{
		//sabmCartFacade = new DefaultSABMCartFacade();

		MockitoAnnotations.initMocks(this);
		sabmCartFacade.setAddressConverter(addressConverter);
		final ModelService modelService = mock(ModelService.class);
		sabmCartFacade.setModelService(modelService);
		when(modelService.create(CartDealConditionModel.class)).thenReturn(dealConditionModel);
	}

	@Test
	public void testAddApplyDealToCart()
	{
		final CartModel cartModel = new CartModel();
		final DealModel dealModel = new DealModel();

		final CartDealConditionModel dealConditionModel = new CartDealConditionModel();
		final List<CartDealConditionModel> complexDealConditionModels = new ArrayList<CartDealConditionModel>();
		cartModel.setComplexDealConditions(complexDealConditionModels);

		when(cartService.hasSessionCart()).thenReturn(true);
		when(cartService.getSessionCart()).thenReturn(cartModel);
		when(dealsService.getDeal(DEAL_CODE)).thenReturn(dealModel);
		//when(modelService.create(CartDealConditionModel.class)).thenReturn(dealConditionModel);

		sabmCartFacade.addApplyDealToCart(DEAL_CODE, DealConditionStatus.MANUAL);

		assertEquals(cartModel.getComplexDealConditions().size(), 1);
		assertEquals(cartModel.getComplexDealConditions().get(0), dealConditionModel);
		assertEquals(cartModel.getComplexDealConditions().get(0).getDeal(), dealModel);

	}

	@Test
	public void testAddApplyDealToCartDealCodeIsNull()
	{
		final DealJson response = sabmCartFacade.addApplyDealToCart("", DealConditionStatus.MANUAL);
		assertEquals(null, response);
	}

	@Test
	public void testFindConflictingDeals()
	{
		final CartModel cart = mock(CartModel.class);
		final B2BUnitModel b2bUnit = mock(B2BUnitModel.class);

		given(sabmCartService.getSessionCart()).willReturn(cart);
		given(b2bCommerceUnitService.getParentUnit()).willReturn(b2bUnit);

		final DealQualificationResponse dealQualificationResponse = mock(DealQualificationResponse.class);
		final ConflictGroup conflictGroup = mock(ConflictGroup.class);
		final List<Conflict> conflicts = new ArrayList<>();

		final Conflict conflict1 = mock(Conflict.class);
		final Set<DealModel> deals1 = new HashSet();
		final DealModel dealModel1 = mock(DealModel.class);
		final DealModel dealModel2 = mock(DealModel.class);
		deals1.add(dealModel1);
		deals1.add(dealModel2);
		final ConflictDealJson conflictDealJson1 = new ConflictDealJson();
		given(conflictDealJsonConverter.convert(Arrays.asList(dealModel1))).willReturn(conflictDealJson1);
		final ConflictDealJson conflictDealJson2 = new ConflictDealJson();
		given(conflictDealJsonConverter.convert(Arrays.asList(dealModel2))).willReturn(conflictDealJson2);
		given(conflict1.getDeals()).willReturn(deals1);
		//given(dealsService.composeComplexFreeProducts(new ArrayList<>(deals1))).willReturn(new ArrayList<>(deals1));
		conflicts.add(conflict1);

		final Conflict conflict2 = mock(Conflict.class);
		final Set<DealModel> deals2 = new HashSet();
		final DealModel dealModel3 = mock(DealModel.class);
		final DealModel dealModel4 = mock(DealModel.class);
		deals2.add(dealModel3);
		deals2.add(dealModel4);
		final ConflictDealJson conflictDealJson3 = new ConflictDealJson();
		given(conflictDealJsonConverter.convert(Arrays.asList(dealModel3))).willReturn(conflictDealJson3);
		final ConflictDealJson conflictDealJson4 = new ConflictDealJson();
		given(conflictDealJsonConverter.convert(Arrays.asList(dealModel4))).willReturn(conflictDealJson4);
		given(conflict2.getDeals()).willReturn(deals2);
		//	given(dealsService.composeComplexFreeProducts(new ArrayList<>(deals2))).willReturn(new ArrayList<>(deals2));
		conflicts.add(conflict2);

		given(conflictGroup.getConflicts()).willReturn(conflicts);
		given(dealQualificationResponse.getConflictGroup()).willReturn(conflictGroup);
		given(dealConditionService.findQualifiedDeals(b2bUnit, cart)).willReturn(dealQualificationResponse);
		final HashMap<DealModel, List<DealModel>> map = new HashMap<DealModel, List<DealModel>>();
		map.put(dealModel1, Arrays.asList(dealModel1, dealModel2));
		given(dealConditionService.findConflictingDeals(Mockito.anyList(), Mockito.any())).willReturn(map);
		final CartDealsJson cartDealsJson = new CartDealsJson();
		sabmCartFacade.findConflictingDeals(cartDealsJson);
		final List<ConflictDealJson> conflictDealJsons1 = cartDealsJson.getConflict();

		assertEquals(2, conflictDealJsons1.size());
		assertEquals(conflictDealJson2, conflictDealJsons1.get(0));
		assertEquals(conflictDealJson1, conflictDealJsons1.get(1));

		final List<CartDealConditionModel> complexDealConditions = new ArrayList<>();
		final CartDealConditionModel conditionModel = mock(CartDealConditionModel.class);
		given(conditionModel.getDeal()).willReturn(dealModel1);
		given(conditionModel.getStatus()).willReturn(DealConditionStatus.MANUAL_CONFLICT);
		complexDealConditions.add(conditionModel);
		given(cart.getComplexDealConditions()).willReturn(complexDealConditions);

		sabmCartFacade.findConflictingDeals(cartDealsJson);
		final List<ConflictDealJson> conflictDealJsons2 = cartDealsJson.getConflict();

		assertEquals(2, conflictDealJsons2.size());
		assertEquals(conflictDealJson3, conflictDealJsons2.get(0));
		assertEquals(conflictDealJson4, conflictDealJsons2.get(1));

	}

	/**
	 * for deal's manual status. for SABMC-1152
	 */
	@Test
	public void testFindConflictingDeals2()
	{
		final CartModel cart = mock(CartModel.class);
		final CartDealsJson cartDealsJson = new CartDealsJson();

		given(sabmCartService.getSessionCart()).willReturn(null);
		sabmCartFacade.findConflictingDeals(cartDealsJson);
		assertTrue(CollectionUtils.isEmpty(cartDealsJson.getConflict()));
		assertTrue(CollectionUtils.isEmpty(cartDealsJson.getFree()));


		final B2BUnitModel b2bUnit = mock(B2BUnitModel.class);

		given(sabmCartService.getSessionCart()).willReturn(cart);
		given(b2bCommerceUnitService.getParentUnit()).willReturn(b2bUnit);

		final DealQualificationResponse dealQualificationResponse = mock(DealQualificationResponse.class);
		final ConflictGroup conflictGroup = mock(ConflictGroup.class);
		final List<Conflict> conflicts = new ArrayList<>();

		final Conflict conflict1 = mock(Conflict.class);
		Set<DealModel> deals1 = new HashSet<>();
		final DealModel dealModel1 = mock(DealModel.class);
		final DealModel dealModel2 = mock(DealModel.class);
		final DealModel dealModel31 = mock(DealModel.class);
		final DealModel dealModel32 = mock(DealModel.class);
		deals1.add(dealModel1);
		given(dealModel1.getCode()).willReturn("dm1");
		deals1.add(dealModel2);
		given(dealModel1.getCode()).willReturn("dm2");
		deals1.add(dealModel31);
		given(dealModel1.getCode()).willReturn("dm31");
		deals1.add(dealModel32);
		given(dealModel1.getCode()).willReturn("dm32");
		final List<DealModel> deallist1 = Arrays.asList(dealModel1);
		final List<DealModel> deallist2 = Arrays.asList(dealModel2);
		final List<DealModel> deallist3 = Arrays.asList(dealModel31, dealModel32);

		final ConflictDealJson conflictDealJson1 = new ConflictDealJson();
		conflictDealJson1.setCode("dm1");
		given(conflictDealJsonConverter.convert(deallist1)).willReturn(conflictDealJson1);
		final ConflictDealJson conflictDealJson2 = new ConflictDealJson();
		conflictDealJson2.setCode("dm2");
		given(conflictDealJsonConverter.convert(deallist2)).willReturn(conflictDealJson2);
		final ConflictDealJson conflictDealJson3 = new ConflictDealJson();
		conflictDealJson3.setCode("dm3");
		given(conflictDealJsonConverter.convert(deallist3)).willReturn(conflictDealJson3);
		given(conflict1.getDeals()).willReturn(deals1);
		conflicts.add(conflict1);


		given(conflictGroup.getConflicts()).willReturn(conflicts);
		given(dealQualificationResponse.getConflictGroup()).willReturn(conflictGroup);
		given(dealConditionService.findQualifiedDeals(b2bUnit, cart)).willReturn(dealQualificationResponse);

		final List<List<DealModel>> groupedDeals = new ArrayList<>();
		groupedDeals.add(deallist1);
		groupedDeals.add(deallist2);
		groupedDeals.add(deallist3);
		given(dealsService.composeComplexFreeProducts(new ArrayList<>(deals1))).willReturn(groupedDeals);

		sabmCartFacade.findConflictingDeals(cartDealsJson);
		final List<ConflictDealJson> conflictDealListJsons1 = cartDealsJson.getConflict();

		assertEquals(4, conflictDealListJsons1.size());
		assertEquals(conflictDealJson1, conflictDealListJsons1.get(0));

		final List<CartDealConditionModel> complexDealConditions = new ArrayList<>();
		final CartDealConditionModel conditionModel = mock(CartDealConditionModel.class);
		//		given(conditionModel.getDeal()).willReturn(dealModel1);
		complexDealConditions.add(conditionModel);
		given(cart.getComplexDealConditions()).willReturn(complexDealConditions);
		//
		//		sabmCartFacade.findConflictingDeals(cartDealsJson);
		//		final List<ConflictDealJson> conflictDealListJsons2 = cartDealsJson.getConflict();
		//
		//		assertEquals(0, conflictDealListJsons2.size());


		// if the conflict list contains only 1 deal(2 free gift deal).
		final DealModel dealModel41 = mock(DealModel.class);
		final DealModel dealModel42 = mock(DealModel.class);
		deals1 = new HashSet<>();
		deals1.add(dealModel41);
		deals1.add(dealModel42);
		final List<List<DealModel>> retdealslist = new ArrayList<>();
		retdealslist.add(new ArrayList<>(deals1));

		given(conflict1.getDeals()).willReturn(deals1);
		given(dealsService.composeComplexFreeProducts(new ArrayList<>(deals1))).willReturn(retdealslist);

		sabmCartFacade.findConflictingDeals(cartDealsJson);

		assertEquals(0, cartDealsJson.getConflict().size());


		// if the cart contains only 1 deal(2 free gift deal).
		deals1.add(dealModel1);
		deals1.add(dealModel2);

		final CartDealConditionModel conditionMode2 = mock(CartDealConditionModel.class);
		given(conditionModel.getDeal()).willReturn(dealModel41);
		given(conditionModel.getStatus()).willReturn(DealConditionStatus.MANUAL_CONFLICT);
		given(conditionMode2.getDeal()).willReturn(dealModel42);
		complexDealConditions.add(conditionMode2);

		given(conflict1.getDeals()).willReturn(deals1);
		given(dealsService.composeComplexFreeProducts(new ArrayList<>(deals1))).willReturn(retdealslist);

		sabmCartFacade.findConflictingDeals(cartDealsJson);

		assertEquals(0, cartDealsJson.getConflict().size());
	}

	/**
	 * for deal's manual status. for SABMC-1230
	 */
	@Test
	public void testFindConflictingDeals3()
	{
		class IsAnyList implements ArgumentMatcher<List<DealModel>>
		{
			@Override
			public boolean matches(final List<DealModel> in)
			{
				// YTODO Auto-generated method stub
				return false;
			}
		}
		;
		class IsDealJson implements ArgumentMatcher<DealJson>
		{
			@Override
			public boolean matches(final DealJson in)
			{
				// YTODO Auto-generated method stub
				return false;
			}
		}
		;


		final CartModel cart = mock(CartModel.class);

		final B2BUnitModel b2bUnit = mock(B2BUnitModel.class);

		given(sabmCartService.getSessionCart()).willReturn(cart);
		given(b2bCommerceUnitService.getParentUnit()).willReturn(b2bUnit);

		// mock 2 deals which has same trigger hash
		final DealModel dealModel1 = mock(DealModel.class);
		final DealModel dealModel2 = mock(DealModel.class);

		given(dealModel1.getTriggerHash()).willReturn("11111111");
		given(dealModel2.getTriggerHash()).willReturn("11111111");
		final Date validfromdate = new Date(2016, 1, 3);
		final Date validtodate = new Date(2016, 3, 3);
		given(dealModel1.getValidFrom()).willReturn(validfromdate);
		given(dealModel1.getValidTo()).willReturn(validtodate);
		given(dealModel2.getValidFrom()).willReturn(validfromdate);
		given(dealModel2.getValidTo()).willReturn(validtodate);

		// mock the benefit, condition group, proportional, valid time,  to be the same value.
		final FreeGoodsDealBenefitModel benefit1 = mock(FreeGoodsDealBenefitModel.class);
		final FreeGoodsDealBenefitModel benefit2 = mock(FreeGoodsDealBenefitModel.class);
		final DealConditionGroupModel conditiongroup1 = mock(DealConditionGroupModel.class);
		final DealConditionGroupModel conditiongroup2 = mock(DealConditionGroupModel.class);
		given(benefit1.getProportionalAmount()).willReturn(true);
		given(benefit1.getProportionalFreeGood()).willReturn(true);
		given(benefit2.getProportionalAmount()).willReturn(true);
		given(benefit2.getProportionalFreeGood()).willReturn(true);
		given(conditiongroup1.getDealBenefits()).willReturn(Arrays.asList(benefit1));
		given(conditiongroup2.getDealBenefits()).willReturn(Arrays.asList(benefit2));
		given(dealModel1.getConditionGroup()).willReturn(conditiongroup1);
		given(dealModel2.getConditionGroup()).willReturn(conditiongroup2);


		// mock a cart. it includes 2 free-good deal. which has the same trigger hash.
		final List<CartDealConditionModel> complexDealConditions = new ArrayList<>();
		final CartDealConditionModel conditionModel1 = mock(CartDealConditionModel.class);
		given(conditionModel1.getStatus()).willReturn(DealConditionStatus.MANUAL);
		given(conditionModel1.getDeal()).willReturn(dealModel1);
		complexDealConditions.add(conditionModel1);
		final CartDealConditionModel conditionModel2 = mock(CartDealConditionModel.class);
		given(conditionModel2.getStatus()).willReturn(DealConditionStatus.MANUAL);
		given(conditionModel2.getDeal()).willReturn(dealModel2);
		complexDealConditions.add(conditionModel2);
		given(cart.getComplexDealConditions()).willReturn(complexDealConditions);



		// mock if the ES only return 1 conflict group , contains only 2 deals which has same hash.
		final Set<DealModel> deals1 = new HashSet<>();
		final DealQualificationResponse dealQualificationResponse = mock(DealQualificationResponse.class);
		final ConflictGroup conflictGroup = mock(ConflictGroup.class);
		final Conflict conflict1 = mock(Conflict.class);
		deals1.add(dealModel1);
		given(dealModel1.getCode()).willReturn("5000000345");
		deals1.add(dealModel2);
		given(dealModel1.getCode()).willReturn("5000000346");
		final List<DealModel> deallist1 = Arrays.asList(dealModel1, dealModel2);
		final ConflictDealJson conflictDealJson1 = new ConflictDealJson();
		given(conflictDealJsonConverter.convert(deallist1)).willReturn(conflictDealJson1);
		given(conflict1.getDeals()).willReturn(deals1);
		given(conflictGroup.getConflicts()).willReturn(Arrays.asList(conflict1));
		given(dealQualificationResponse.getConflictGroup()).willReturn(conflictGroup);
		given(dealConditionService.findQualifiedDeals(b2bUnit, cart)).willReturn(dealQualificationResponse);

		final List<List<DealModel>> groupedDeals = new ArrayList<>();
		groupedDeals.add(deallist1);
		given(dealsService.composeComplexFreeProducts(new ArrayList<>(deals1))).willReturn(groupedDeals);

		final List<DealFreeProductJson> fplist = new ArrayList<>();
		fplist.add(mock(DealFreeProductJson.class));
		fplist.add(mock(DealFreeProductJson.class));


		Mockito.doAnswer(new Answer<Object>()
		{
			public Object answer(final InvocationOnMock invocation)
			{
				final Object[] args = invocation.getArguments();
				final DealJson dj = (DealJson) args[1];
				dj.setSelectableProducts(fplist);
				return null;
			}
		}).when(dealProductPopulator).populate(argThat(new IsAnyList()), argThat(new IsDealJson()));


		final CartDealsJson cartDealsJson = new CartDealsJson();
		sabmCartFacade.findConflictingDeals(cartDealsJson);

		assertEquals(0, cartDealsJson.getConflict().size());
		assertEquals(2, cartDealsJson.getFree().size());
	}


	@Test
	public void testGetRejectedDealFromCart()
	{
		final CartModel cartModel = mock(CartModel.class);
		final List<CartDealConditionModel> conditionModels = new ArrayList<>();
		final CartDealConditionModel conditionModel1 = mock(CartDealConditionModel.class);
		final CartDealConditionModel conditionModel2 = mock(CartDealConditionModel.class);
		final CartDealConditionModel conditionModel3 = mock(CartDealConditionModel.class);

		given(conditionModel1.getStatus()).willReturn(DealConditionStatus.REJECTED);
		final DealModel dealModel1 = mock(DealModel.class);
		given(conditionModel1.getDeal()).willReturn(dealModel1);

		given(conditionModel2.getStatus()).willReturn(DealConditionStatus.MANUAL);
		final DealModel dealModel2 = mock(DealModel.class);
		given(conditionModel2.getDeal()).willReturn(dealModel2);

		given(conditionModel3.getStatus()).willReturn(DealConditionStatus.REJECTED);
		final DealModel dealModel3 = mock(DealModel.class);
		given(conditionModel3.getDeal()).willReturn(dealModel3);

		conditionModels.add(conditionModel1);
		conditionModels.add(conditionModel2);
		conditionModels.add(conditionModel3);

		given(cartService.hasSessionCart()).willReturn(true);
		given(cartModel.getComplexDealConditions()).willReturn(conditionModels);
		given(cartService.getSessionCart()).willReturn(cartModel);

		final List<String> rejectedTitles = sabmCartFacade.getRejectedDealFromCart();
		assertEquals(2, rejectedTitles.size());
	}

	@Test
	public void testGetNonRejectedDealFromCart()
	{
		given(cartService.hasSessionCart()).willReturn(true);

		final List<String> rejectedTitles = sabmCartFacade.getRejectedDealFromCart();
		assertEquals(0, rejectedTitles.size());
	}

	@Test
	public void testGetDeliveryDefaultAddress()
	{
		final String unitId = "0000842292";
		final String partnerNumber = "0000842292";

		// Test cart is empty
		given(cartService.getSessionCart()).willReturn(null);
		final AddressData addressData = sabmCartFacade.getDeliveryDefaultAddress(unitId);
		assertEquals(null, addressData);

		// Test current user not set default delivery address
		final CartModel cart = new CartModel();
		final B2BUnitModel b2bUnit = new B2BUnitModel();
		b2bUnit.setUid(unitId);
		final AddressModel address = new AddressModel();
		final B2BCustomerModel b2bCustomer = new B2BCustomerModel();
		final Set<DeliveryDefaultAddressModel> deliveryDefaultAddresses = Sets.newConcurrentHashSet();
		final DeliveryDefaultAddressModel defaultAdress = new DeliveryDefaultAddressModel();
		defaultAdress.setB2bUnit(b2bUnit);
		defaultAdress.setAddress(address);
		deliveryDefaultAddresses.add(defaultAdress);
		cart.setUser(b2bCustomer);
		given(cartService.getSessionCart()).willReturn(cart);
		given(b2bUnitService.getUnitForUid(unitId)).willReturn(b2bUnit);
		final AddressData defaultAddressData1 = sabmCartFacade.getDeliveryDefaultAddress(unitId);
		assertEquals(null, defaultAddressData1);

		// Test current user had set default delivery address
		b2bCustomer.setDefaultAddresses(deliveryDefaultAddresses);
		final AddressData address2 = new AddressData();
		address2.setPartnerNumber(partnerNumber);
		given(addressConverter.convert(address)).willReturn(address2);
		final AddressData defaultAddressData = sabmCartFacade.getDeliveryDefaultAddress(unitId);
		assertEquals(partnerNumber, defaultAddressData.getPartnerNumber());
	}

	@Test
	public void testGetNeedRemovedDealCondition()
	{
		final Set<String> dealCodes = new HashSet<>();
		dealCodes.add("testCode1");
		dealCodes.add("testCode2");
		dealCodes.add("testCode3");

		final CartModel cartModel = mock(CartModel.class);
		final List<CartDealConditionModel> conditions = new ArrayList<>();

		final CartDealConditionModel condition1 = mock(CartDealConditionModel.class);
		final DealModel deal1 = mock(DealModel.class);
		given(deal1.getCode()).willReturn("testCode1");
		given(condition1.getDeal()).willReturn(deal1);
		given(condition1.getStatus()).willReturn(DealConditionStatus.MANUAL);
		conditions.add(condition1);

		final CartDealConditionModel condition2 = mock(CartDealConditionModel.class);
		final DealModel deal2 = mock(DealModel.class);
		given(deal2.getCode()).willReturn("testCode2");
		given(condition2.getDeal()).willReturn(deal2);
		given(condition2.getStatus()).willReturn(DealConditionStatus.MANUAL);
		conditions.add(condition2);

		given(cartModel.getComplexDealConditions()).willReturn(conditions);

		given(sessionService.getAttribute(SabmCoreConstants.SESSION_ATTR_CONFLICT_DEALS_CODE)).willReturn(dealCodes);

		//final List<CartDealConditionModel> result = sabmCartFacade.getNeedRemovedDealCondition(cartModel, "testCode3");
		//assertEquals(2, result.size());
	}

	@Test
	public void testSkipCheckCartContainDCN()
	{
		final String dealCode = "mockCode";
		final DealConditionStatus status = DealConditionStatus.MANUAL_CONFLICT;
		final CartModel cartModel = new CartModel();

		given(cartService.hasSessionCart()).willReturn(true);
		given(cartService.getSessionCart()).willReturn(cartModel);

		final DealModel dealModel = mock(DealModel.class);
		given(dealsService.getDeal(dealCode)).willReturn(dealModel);
		final CartDealConditionModel dealConditionModel = new CartDealConditionModel();
		//given(modelService.create(CartDealConditionModel.class)).willReturn(dealConditionModel);

		cartModel.setComplexDealConditions(Lists.newArrayList(dealConditionModel));
		sabmCartFacade.addApplyDealToCart(dealCode, status);

		Assert.assertEquals(OrderSimulationStatus.NEED_CALCULATION, cartModel.getOrderSimulationStatus());

	}

	@Test
	public void testCheckFreeGoodsDealAppliedTimes()
	{
		final DealJson dealJson = new DealJson();

		// build the condition
		final DealRangeJson dealRangeJson1 = new DealRangeJson();
		dealRangeJson1.setMinQty(0);
		final DealBaseProductJson baseProductJson1 = new DealBaseProductJson();
		baseProductJson1.setQty(3);
		baseProductJson1.setProductCode("productCode1");

		final DealBaseProductJson baseProductJson2 = new DealBaseProductJson();
		baseProductJson2.setQty(4);
		baseProductJson2.setProductCode("productCode2");

		final List<DealBaseProductJson> baseProductJsons1 = new ArrayList<>();
		baseProductJsons1.add(baseProductJson1);
		baseProductJsons1.add(baseProductJson2);
		dealRangeJson1.setBaseProducts(baseProductJsons1);

		final DealRangeJson dealRangeJson2 = new DealRangeJson();
		dealRangeJson2.setMinQty(5);
		final DealBaseProductJson baseProductJson3 = new DealBaseProductJson();
		baseProductJson3.setProductCode("productCode3");
		final DealBaseProductJson baseProductJson4 = new DealBaseProductJson();
		baseProductJson4.setProductCode("productCode4");

		final List<DealBaseProductJson> baseProductJsons2 = new ArrayList<>();
		baseProductJsons2.add(baseProductJson1);
		baseProductJsons2.add(baseProductJson2);
		dealRangeJson2.setBaseProducts(baseProductJsons2);

		final List<DealRangeJson> ranges = new ArrayList<>();
		ranges.add(dealRangeJson1);
		ranges.add(dealRangeJson2);
		dealJson.setRanges(ranges);

		// build the benefit
		final DealFreeProductJson freeProductJson1 = new DealFreeProductJson();
		freeProductJson1.setCode("code1");
		freeProductJson1.setProportionalFreeGood(true);
		final Map<Integer, Integer> qtyMap1 = new HashMap<>();
		qtyMap1.put(0, 1);
		freeProductJson1.setQty(qtyMap1);

		final DealFreeProductJson freeProductJson2 = new DealFreeProductJson();
		freeProductJson2.setCode("code1");
		freeProductJson2.setProportionalFreeGood(true);
		final Map<Integer, Integer> qtyMap2 = new HashMap<>();
		qtyMap2.put(0, 2);
		freeProductJson2.setQty(qtyMap2);

		final List<DealFreeProductJson> freeProductJsons = new ArrayList<>();
		freeProductJsons.add(freeProductJson1);
		freeProductJsons.add(freeProductJson2);
		dealJson.setSelectableProducts(freeProductJsons);

		final CartData cartData = mock(CartData.class);

		final OrderEntryData entryData1 = new OrderEntryData();
		entryData1.setQuantity(Long.valueOf(6));
		final ProductData productData1 = new ProductData();
		productData1.setCode("productCode1");
		entryData1.setProduct(productData1);

		final OrderEntryData entryData2 = new OrderEntryData();
		entryData2.setQuantity(Long.valueOf(9));
		final ProductData productData2 = new ProductData();
		productData2.setCode("productCode2");
		entryData2.setProduct(productData2);

		final OrderEntryData entryData3 = new OrderEntryData();
		entryData3.setQuantity(Long.valueOf(10));
		final ProductData productData3 = new ProductData();
		productData3.setCode("productCode3");
		entryData3.setProduct(productData3);

		final OrderEntryData entryData4 = new OrderEntryData();
		entryData4.setQuantity(Long.valueOf(6));
		final ProductData productData4 = new ProductData();
		productData4.setCode("productCode4");
		entryData4.setProduct(productData4);

		final List<OrderEntryData> entries = new ArrayList<>();
		entries.add(entryData1);
		entries.add(entryData2);
		entries.add(entryData3);
		entries.add(entryData4);

		given(cartData.getEntries()).willReturn(entries);
		given(sabmCartFacade.getSessionCartWithEntryOrdering(true)).willReturn(cartData);

		sabmCartFacade.checkFreeGoodsDealAppliedTimes(dealJson);

		assertEquals(Integer.valueOf(1), dealJson.getSelectableProducts().get(0).getQty().get(0));
	}

	@Test
	public void testIsExistBaseProduct()
	{
		// Check the cart without entry data
		when(cartService.hasSessionCart()).thenReturn(false);
		assertEquals(false, sabmCartFacade.isExistBaseProduct());

		final CartModel mockCart = mock(CartModel.class);
		final List<AbstractOrderEntryModel> cartEntrys = Lists.newArrayList();
		final AbstractOrderEntryModel cartEntry = new AbstractOrderEntryModel();
		cartEntry.setIsFreeGood(false);
		cartEntrys.add(cartEntry);
		when(cartService.hasSessionCart()).thenReturn(true);
		when(cartService.getSessionCart()).thenReturn(mockCart);
		when(cartService.getSessionCart().getEntries()).thenReturn(cartEntrys);
		when(sabmCartService.getSessionCart()).thenReturn(mockCart);
		when(mockCart.getEntries()).thenReturn(cartEntrys);

		// Check the cart with BaseProduct
		assertEquals(true, sabmCartFacade.isExistBaseProduct());

		// Check the cart without BaseProduct
		cartEntry.setIsFreeGood(true);
		assertEquals(false, sabmCartFacade.isExistBaseProduct());

		// Check the cart in both BaseProduct and FreeProduct
		final AbstractOrderEntryModel cartEntry2 = new AbstractOrderEntryModel();
		cartEntry2.setIsFreeGood(false);
		cartEntrys.add(cartEntry2);
		assertEquals(true, sabmCartFacade.isExistBaseProduct());
	}

	@Test
	public void validateShippingCarrierTest()
	{
		final Map<String, Object> deliveryPackType = new HashMap<>();
		deliveryPackType.put(PackType._TYPECODE, "deliveryType");

		final Date date = new Date();

		when(cartService.getSessionCart()).thenReturn(cartModel);
		when(cartModel.getRequestedDeliveryDate()).thenReturn(date);
		when(b2bCommerceUnitService.getParentUnit()).thenReturn(b2bUnitModel);

		when(sabmDeliveryDateCutOffService.getDeliveryDatePackType(b2bUnitModel, date)).thenReturn(deliveryPackType);
		when(b2bUnitModel.getShippingCarriers()).thenReturn(Collections.singletonList(b2bUnitCarrier));
		when(cartModel.getDeliveryShippingCarrier()).thenReturn(cartModelCarrier);
		when(cartModel.getDeliveryMode()).thenReturn(deliveryMode);
		when(deliveryMode.getCode()).thenReturn("deliveryCode");
		when(siteConfigService.getString(SabmCoreConstants.CART_DELIVERY_CUBARRANGED, "")).thenReturn("CartDeliveryCode");

		when(b2bUnitCarrier.getCustomerOwned()).thenReturn(true);

		sabmCartFacade.validateShippingCarrier();
		Mockito.verify(sabmCartService).setShippingCarrier(Mockito.any(CommerceCheckoutParameter.class));

	}

	@Test
	public void getMiniCartTest()
	{

		when(cartService.hasSessionCart()).thenReturn(true);
		when(asahiSiteUtil.isCub()).thenReturn(false);
		when(cartService.getSessionCart()).thenReturn(cartModel);
		when(asahiSiteUtil.isSga()).thenReturn(true);
		when(cartModel.getEntries()).thenReturn(Collections.singletonList(entry));
		when(entry.getIsFreeGood()).thenReturn(false);
		when(entry.getFreeGoodEntryNumber()).thenReturn("100");
		when(entry.getAsahiDealCode()).thenReturn("dealCode");
		when(dealsDao.getSgaDealByCode("dealCode")).thenReturn(deal);
		when(asahiDealValidationStrategy.validateDeal(deal)).thenReturn(false);
		//when(cartService.getEntryForNumber(Mockito.anyObject(), Mockito.anyObject())).thenReturn(freeCartEntry);
		when(entry.getFreeGoodsForDeal()).thenReturn("Valid");
		sabmCartFacade.setCartConverter(miniCartConverter);
		when(miniCartConverter.convert(cartModel)).thenReturn(cartData);
		assertNotNull(sabmCartFacade.getMiniCart());
	}




}
