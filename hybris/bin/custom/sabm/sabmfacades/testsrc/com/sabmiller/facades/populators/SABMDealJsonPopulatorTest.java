/**
 *
 */
package com.sabmiller.facades.populators;

import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.mock;

import de.hybris.bootstrap.annotations.UnitTest;
import de.hybris.platform.b2b.company.B2BCommerceUnitService;
import de.hybris.platform.b2b.model.B2BCustomerModel;
import de.hybris.platform.core.PK;
import de.hybris.platform.core.model.c2l.CurrencyModel;
import de.hybris.platform.core.model.product.ProductModel;
import de.hybris.platform.core.model.product.UnitModel;
import de.hybris.platform.servicelayer.dto.converter.Converter;
import de.hybris.platform.servicelayer.i18n.I18NService;
import de.hybris.platform.servicelayer.session.SessionService;
import de.hybris.platform.servicelayer.user.UserService;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Calendar;
import java.util.Date;
import java.util.List;

import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.MockitoAnnotations;

import com.google.common.collect.Lists;
import com.sabmiller.core.cart.service.SABMCartService;
import com.sabmiller.core.constants.SabmCoreConstants;
import com.sabmiller.core.deals.strategies.SABMDealValidationStrategy;
import com.sabmiller.core.enums.DealTypeEnum;
import com.sabmiller.core.enums.RepDrivenDealStatus;
import com.sabmiller.core.model.AbstractDealBenefitModel;
import com.sabmiller.core.model.AbstractDealConditionModel;
import com.sabmiller.core.model.DealConditionGroupModel;
import com.sabmiller.core.model.DealModel;
import com.sabmiller.core.model.DiscountDealBenefitModel;
import com.sabmiller.core.model.FreeGoodsDealBenefitModel;
import com.sabmiller.core.model.ProductDealConditionModel;
import com.sabmiller.core.model.RepDrivenDealConditionStatusModel;
import com.sabmiller.core.product.SabmProductService;
import com.sabmiller.facades.deal.data.DealBaseProductJson;
import com.sabmiller.facades.deal.data.DealFreeProductJson;
import com.sabmiller.facades.deal.data.DealJson;


/**
 *
 */
@UnitTest
public class SABMDealJsonPopulatorTest
{

	@Mock
	private Converter<ProductModel, DealFreeProductJson> dealFreeProductJsonConverter;
	@Mock
	private Converter<ProductModel, DealBaseProductJson> dealBaseProductJsonConverter;
	@Mock
	private SabmProductService productService;
	@Mock
	private I18NService i18nService;
	@Mock
	private SessionService sessionService;
	@Mock
	private B2BCommerceUnitService b2bCommerceUnitService;
	@Mock
	private UserService userService;
	@Mock
	private SABMDealValidationStrategy dealValidationStrategy;
	@Mock
	private SABMCartService cartService;

	@InjectMocks
	private SABMDealJsonPopulator sabmDealJsonPopulator;
	@Before
	public void setUp()
	{
		MockitoAnnotations.initMocks(this);
	}



	@Test
	public void testPopulator()
	{
		final DealModel dealModel = mock(DealModel.class);

		final ProductModel product = mock(ProductModel.class);
		final ProductModel product1 = mock(ProductModel.class);
		final DealConditionGroupModel dealConditionGroupModel = mock(DealConditionGroupModel.class);
		final ProductDealConditionModel productDealConditionModel = mock(ProductDealConditionModel.class);
		final FreeGoodsDealBenefitModel freeGoodsDealBenefitModel = mock(FreeGoodsDealBenefitModel.class);
		final DiscountDealBenefitModel discountDealBenefitModel = mock(DiscountDealBenefitModel.class);
		final UnitModel unitModel1 = mock(UnitModel.class);
		final UnitModel unitModel2 = mock(UnitModel.class);
		final UnitModel unitModel3 = mock(UnitModel.class);
		final List<AbstractDealConditionModel> conditionModels = new ArrayList<AbstractDealConditionModel>();
		final List<AbstractDealBenefitModel> benefitModels = new ArrayList<AbstractDealBenefitModel>();

		given(unitModel1.getCode()).willReturn("unitCode1");
		given(unitModel1.getName()).willReturn("unitName1");
		given(unitModel2.getCode()).willReturn("unitCode2");
		given(unitModel2.getName()).willReturn("unitName2");
		given(unitModel3.getCode()).willReturn("unitCode3");
		given(unitModel3.getName()).willReturn("unitName3");
		given(productDealConditionModel.getMinQty()).willReturn(Integer.valueOf(5));
		given(productDealConditionModel.getUnit()).willReturn(unitModel1);



		given(freeGoodsDealBenefitModel.getQuantity()).willReturn(Integer.valueOf(7));
		given(freeGoodsDealBenefitModel.getUnit()).willReturn(unitModel3);
		final PK pk2 = PK.parse("1234501");
		final String productcode1 = "12345";
		given(product1.getPk()).willReturn(pk2);
		given(product1.getCode()).willReturn(productcode1);
		given(product1.getName()).willReturn("freeProduct");

		Mockito.when(freeGoodsDealBenefitModel.getProductCode()).thenReturn(productcode1);

		Mockito.when(productService.getProductForCode(product1.getCode())).thenReturn(product1);

		final DealFreeProductJson productJson1 = new DealFreeProductJson();
		productJson1.setTitle("freeProduct");
		given(dealFreeProductJsonConverter.convert(product1)).willReturn(productJson1);

		given(discountDealBenefitModel.getCurrency()).willReturn(Boolean.valueOf(true));

		final CurrencyModel currency = mock(CurrencyModel.class);
		given(currency.getSymbol()).willReturn("$");
		Mockito.when(i18nService.getCurrentCurrency()).thenReturn(currency);

		given(discountDealBenefitModel.getAmount()).willReturn(Double.valueOf(10));
		given(discountDealBenefitModel.getSaleUnit()).willReturn(Integer.valueOf(3));
		given(discountDealBenefitModel.getUnit()).willReturn(unitModel1);
		conditionModels.add(productDealConditionModel);
		benefitModels.add(freeGoodsDealBenefitModel);
		benefitModels.add(discountDealBenefitModel);
		given(dealConditionGroupModel.getDealConditions()).willReturn(conditionModels);
		given(dealConditionGroupModel.getDealBenefits()).willReturn(benefitModels);
		given(dealModel.getConditionGroup()).willReturn(dealConditionGroupModel);

		final Calendar calendar = Calendar.getInstance();
		final SimpleDateFormat sdf = new SimpleDateFormat("MM-dd-yyyy");
		Date date = null;

		try
		{
			date = sdf.parse("20-03-2016");
		}
		catch (final ParseException e)
		{
			e.printStackTrace();
		}

		calendar.setTime(date);

		final Date validFrom = calendar.getTime();
		calendar.add(Calendar.MONTH, 12);
		final Date validTo = calendar.getTime();

		final PK pk = PK.parse("12345");
		given(dealModel.getPk()).willReturn(pk);
		given(dealModel.getValidFrom()).willReturn(validFrom);
		given(dealModel.getValidTo()).willReturn(validTo);
		given(dealModel.getDealType()).willReturn(DealTypeEnum.DISCOUNT);
		given(dealModel.getCode()).willReturn("testCode");

		final RepDrivenDealConditionStatusModel dealConditionStatusModel = new RepDrivenDealConditionStatusModel();
		dealConditionStatusModel.setStatus(RepDrivenDealStatus.UNLOCKED);

		final String productcode = "123";

		final PK pk1 = PK.parse("123450");
		given(product.getPk()).willReturn(pk1);
		given(product.getCode()).willReturn(productcode);
		given(product.getName()).willReturn("productName");

		Mockito.when(productDealConditionModel.getProductCode()).thenReturn(productcode);

		Mockito.when(productService.getProductForCode(product.getCode())).thenReturn(product);

		final DealBaseProductJson productJson = new DealBaseProductJson();
		productJson.setTitle("testDealTitle");
		given(dealBaseProductJsonConverter.convert(product)).willReturn(productJson);

		final DealJson dealjson = new DealJson();

		sabmDealJsonPopulator.populate(Arrays.asList(dealModel), dealjson);

		Assert.assertNotNull(dealjson.getCode());


	}

	@Test
	public void testLimitedOffer()
	{
		final DealModel dealModel = mock(DealModel.class);
		given(dealModel.getDealType()).willReturn(DealTypeEnum.LIMITED);
		given(dealModel.getConditionGroup()).willReturn(mock(DealConditionGroupModel.class));
		given(dealModel.getConditionGroup().getDealBenefits()).willReturn(Arrays.asList(mock(AbstractDealBenefitModel.class)));
		given(userService.getCurrentUser()).willReturn(mock(B2BCustomerModel.class));
		given(sessionService.getAttribute(SabmCoreConstants.SESSION_ATTR_DELIVERY_DATE)).willReturn(null);
		given(dealValidationStrategy.validateNowAvailableDeal(dealModel)).willReturn(false);
		given(cartService.hasSessionCart()).willReturn(false);
		final DealJson dealJson = new DealJson();

		sabmDealJsonPopulator.populate(Lists.newArrayList(dealModel), dealJson);

		Assert.assertEquals(1, dealJson.getBadges().size());
		Assert.assertTrue(dealJson.getBadges().contains(2));
	}
}
