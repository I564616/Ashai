/**
 *
 */
package com.sabmiller.facades.populators;

import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.mock;

import de.hybris.bootstrap.annotations.UnitTest;
import de.hybris.platform.b2b.company.B2BCommerceUnitService;
import de.hybris.platform.b2b.model.B2BUnitModel;
import de.hybris.platform.comments.model.CommentModel;
import de.hybris.platform.commercefacades.comment.data.CommentData;
import de.hybris.platform.commercefacades.order.data.CCPaymentInfoData;
import de.hybris.platform.commercefacades.order.data.CartData;
import de.hybris.platform.commercefacades.order.data.DeliveryModeData;
import de.hybris.platform.commercefacades.order.data.OrderEntryData;
import de.hybris.platform.commercefacades.order.data.ZoneDeliveryModeData;
import de.hybris.platform.commercefacades.product.PriceDataFactory;
import de.hybris.platform.commercefacades.product.data.PriceData;
import de.hybris.platform.commercefacades.product.data.PriceDataType;
import de.hybris.platform.commercefacades.product.data.PromotionResultData;
import de.hybris.platform.commercefacades.user.data.AddressData;
import de.hybris.platform.converters.Populator;
import de.hybris.platform.converters.impl.AbstractPopulatingConverter;
import de.hybris.platform.core.PK;
import de.hybris.platform.core.model.c2l.CurrencyModel;
import de.hybris.platform.core.model.order.AbstractOrderEntryModel;
import de.hybris.platform.core.model.order.CartModel;
import de.hybris.platform.core.model.order.delivery.DeliveryModeModel;
import de.hybris.platform.core.model.order.payment.CreditCardPaymentInfoModel;
import de.hybris.platform.core.model.user.AddressModel;
import de.hybris.platform.deliveryzone.model.ZoneDeliveryModeModel;
import de.hybris.platform.promotions.PromotionsService;
import de.hybris.platform.promotions.model.PromotionResultModel;
import de.hybris.platform.servicelayer.dto.converter.Converter;
import de.hybris.platform.servicelayer.model.ModelService;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.assertj.core.util.Lists;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import com.apb.core.util.AsahiSiteUtil;
import com.sabmiller.core.cart.service.SABMCartService;
import com.sabmiller.core.deals.services.DealConditionService;
import com.sabmiller.core.deals.services.DealsService;
import com.sabmiller.core.enums.DealConditionStatus;
import com.sabmiller.core.model.CartDealConditionModel;
import com.sabmiller.core.model.DealModel;
import com.sabmiller.facades.deal.SABMDealsSearchFacade;
import com.sabmiller.facades.deal.data.DealJson;


/**
 * sabMCartPopulatorTest
 *
 * @author yuxiao.wang
 * @data 2015-11-24
 *
 */
@UnitTest
public class SABMCartPopulatorTest
{
	@InjectMocks
	private SABMCartPopulator sabMCartPopulator;

	@Mock
	private Populator<DealModel, DealJson> dealJsonPopulator;
	@Mock
	private ModelService modelService;
	@Mock
	private PromotionsService promotionsService;
	@Mock
	private PriceDataFactory priceDataFactory;
	@Mock
	private AbstractPopulatingConverter<AbstractOrderEntryModel, OrderEntryData> orderEntryConverter;
	@Mock
	private AbstractPopulatingConverter<AddressModel, AddressData> addressConverter;
	@Mock
	private AbstractPopulatingConverter<CreditCardPaymentInfoModel, CCPaymentInfoData> creditCardPaymentInfoConverter;
	@Mock
	private AbstractPopulatingConverter<DeliveryModeModel, DeliveryModeData> deliveryModeConverter;
	@Mock
	private AbstractPopulatingConverter<ZoneDeliveryModeModel, ZoneDeliveryModeData> zoneDeliveryModeConverter;
	@Mock
	private AbstractPopulatingConverter<PromotionResultModel, PromotionResultData> promotionResultConverter;
	@Mock
	private Populator<List<DealModel>, DealJson> dealTitlePopulator;
	@Mock
	private SABMCartService sabmCartService;
	@Mock
	private SABMCartService cartService;
	@Mock
	private DealsService dealsService;
	@Mock
	private SABMDealsSearchFacade sabmDealsSearchFacade;
	@Mock
	private Converter<CommentModel, CommentData> orderCommentConverter;
	@Mock
	private AsahiSiteUtil asahiSiteUtil;
	@Mock
	private B2BCommerceUnitService b2bCommerceUnitService;
	@Mock
	private DealConditionService dealConditionService;

	private CartModel cartModel;

	private CartData cartData;

	@Before
	public void setUp()
	{
		MockitoAnnotations.initMocks(this);

		sabMCartPopulator.setPriceDataFactory(priceDataFactory);
		sabMCartPopulator.setOrderEntryConverter(orderEntryConverter);

		sabMCartPopulator.setAddressConverter(addressConverter);
		sabMCartPopulator.setCreditCardPaymentInfoConverter(creditCardPaymentInfoConverter);
		sabMCartPopulator.setDeliveryModeConverter(deliveryModeConverter);
		sabMCartPopulator.setOrderEntryConverter(orderEntryConverter);
		sabMCartPopulator.setModelService(modelService);
		sabMCartPopulator.setPriceDataFactory(priceDataFactory);
		sabMCartPopulator.setPromotionResultConverter(promotionResultConverter);
		sabMCartPopulator.setPromotionsService(promotionsService);
		sabMCartPopulator.setZoneDeliveryModeConverter(zoneDeliveryModeConverter);
		sabMCartPopulator.setOrderCommentConverter(orderCommentConverter);

		cartModel = mock(CartModel.class);
		cartData = new CartData();
	}


	@Test
	public void testPopulator()
	{
		final CurrencyModel currencyModel = mock(CurrencyModel.class);
		final PriceData priceData = mock(PriceData.class);
		final DeliveryModeModel deliveryMode = mock(DeliveryModeModel.class);
		final PK pk = PK.parse("1234567");
		given(cartModel.getPk()).willReturn(pk);
		given(cartModel.getCurrency()).willReturn(currencyModel);
		given(cartModel.getDeliveryMode()).willReturn(deliveryMode);
		given(cartModel.getDeliveryCost()).willReturn(Double.valueOf(3.44));
		given(priceDataFactory.create(PriceDataType.BUY, BigDecimal.valueOf(234.56), currencyModel)).willReturn(priceData);
		given(priceDataFactory.create(PriceDataType.BUY, BigDecimal.valueOf(123.45), currencyModel)).willReturn(priceData);
		given(priceDataFactory.create(PriceDataType.BUY, BigDecimal.valueOf(12.34), currencyModel)).willReturn(priceData);
		given(priceDataFactory.create(PriceDataType.BUY, BigDecimal.valueOf(3.44), currencyModel)).willReturn(priceData);

		given(deliveryModeConverter.convert(deliveryMode)).willReturn(new DeliveryModeData());
		final List<CartDealConditionModel> complexDealConditions = new ArrayList<>();
		final CartDealConditionModel cartDealCondition1 = mock(CartDealConditionModel.class);
		final CartDealConditionModel cartDealCondition2 = mock(CartDealConditionModel.class);
		final CartDealConditionModel cartDealCondition3 = mock(CartDealConditionModel.class);
		final DealModel dealModel1 = mock(DealModel.class);
		final DealModel dealModel2 = mock(DealModel.class);
		final DealModel dealModel3 = mock(DealModel.class);

		given(cartDealCondition1.getDeal()).willReturn(dealModel1);
		given(cartDealCondition1.getStatus()).willReturn(DealConditionStatus.AUTOMATIC);
		complexDealConditions.add(cartDealCondition1);

		given(cartDealCondition2.getDeal()).willReturn(dealModel2);
		given(cartDealCondition2.getStatus()).willReturn(DealConditionStatus.MANUAL);
		complexDealConditions.add(cartDealCondition2);

		given(cartDealCondition3.getDeal()).willReturn(dealModel3);
		given(cartDealCondition3.getStatus()).willReturn(DealConditionStatus.AUTOMATIC);
		complexDealConditions.add(cartDealCondition3);

		given(cartModel.getComplexDealConditions()).willReturn(complexDealConditions);
		given(cartModel.getEntryGroups()).willReturn(null);
		given(asahiSiteUtil.isCub()).willReturn(true);

		sabMCartPopulator.populate(cartModel, cartData);

		//Assert.assertEquals(priceData, cartData.getDeposit());
		Assert.assertEquals(priceData, cartData.getDeliveryCost());
		Assert.assertEquals(2, cartData.getAutoAppliedDeals().size());

	}

	@SuppressWarnings("unchecked")
	@Test
	public void testChooseOtherFreeGoodFlag()
	{

		final List<DealModel> deals = new ArrayList<>();
		final DealModel deal1 = mock(DealModel.class);
		final DealModel deal2 = mock(DealModel.class);
		given(deal1.getCode()).willReturn("deal1");
		given(deal2.getCode()).willReturn("deal2");
		deals.add(deal2);
		deals.add(deal1);

		final List<DealModel> deals1 = new ArrayList<>();
		final DealModel deal3 = mock(DealModel.class);
		given(deal3.getCode()).willReturn("deal3");
		deals1.add(deal3);

		final Map<Integer, List<DealModel>> entryDeals = new HashMap<>();
		entryDeals.put(Integer.valueOf(1), deals);
		entryDeals.put(Integer.valueOf(3), deals1);

		final List<OrderEntryData> cartEntries = new ArrayList<>();

		final OrderEntryData EntryData = new OrderEntryData();
		EntryData.setEntryNumber(Integer.valueOf(1));
		EntryData.setFreeGoodEntryNumber("5");
		EntryData.setOfferData(Lists.emptyList());

		final OrderEntryData EntryData1 = new OrderEntryData();
		EntryData1.setEntryNumber(Integer.valueOf(3));
		EntryData1.setOfferData(Lists.emptyList());

		final OrderEntryData EntryData2 = new OrderEntryData();
		EntryData2.setEntryNumber(Integer.valueOf(5));
		EntryData2.setIsFreeGood(Boolean.TRUE);
		EntryData2.setFreeGoodsForDeal("deal1");
		EntryData2.setOfferData(Lists.emptyList());

		cartEntries.add(EntryData);
		cartEntries.add(EntryData1);
		cartEntries.add(EntryData2);
		cartData.setEntries(cartEntries);

		given(sabmCartService.getEntryApplyDeal(cartModel, false)).willReturn(entryDeals);
		given(dealsService.isDiscountDealExists(deals)).willReturn(false);
		given(dealsService.isDiscountDealExists(deals1)).willReturn(false);
		given(sabmDealsSearchFacade.searchDeals(true)).willReturn(Lists.emptyList());
		given(b2bCommerceUnitService.getParentUnit()).willReturn(mock(B2BUnitModel.class));
		given(cartService.getSessionCart()).willReturn(cartModel);
		given(dealsService.getValidatedNonComplexDeals(mock(B2BUnitModel.class))).willReturn(Lists.emptyList());
		given(dealConditionService.findFullyQualifiedDeals(Lists.emptyList(), cartModel)).willReturn(Collections.emptyList());

		sabMCartPopulator.addEntryDeals(entryDeals, cartData);

		Assert.assertEquals(3, cartData.getEntries().size());

		for (final OrderEntryData enty : cartData.getEntries())
		{
			if (enty.getEntryNumber().equals(Integer.parseInt("5")))
			{
				System.out.println(enty.isIsFreeGood());
				Assert.assertEquals(true, enty.isIsFreeGood());
			}
		}
	}

	@Test
	public void testPopulatorEntryDeal()
	{
		final CartModel cartModel1 = mock(CartModel.class);

		final CartData cartData = new CartData();
		final List<OrderEntryData> entryDatas = new ArrayList<>();

		final OrderEntryData entry1 = new OrderEntryData();
		entry1.setEntryNumber(0);
		entryDatas.add(entry1);

		final OrderEntryData entry2 = new OrderEntryData();
		entry2.setEntryNumber(1);
		entryDatas.add(entry2);

		cartData.setEntries(entryDatas);

		final List<DealModel> deals = new ArrayList<>();
		final DealModel deal1 = mock(DealModel.class);
		final DealModel deal2 = mock(DealModel.class);
		deals.add(deal2);
		deals.add(deal1);

		final Map<Integer, List<DealModel>> entryDeals = new HashMap<>();
		entryDeals.put(Integer.valueOf(1), deals);

		given(sabmCartService.getEntryApplyDeal(cartModel1, false)).willReturn(entryDeals);

		final DealJson dealJson = mock(DealJson.class);

		given(dealJson.getTitle()).willReturn("Titles");
		Assert.assertEquals(2, cartData.getEntries().size());
	}







}
