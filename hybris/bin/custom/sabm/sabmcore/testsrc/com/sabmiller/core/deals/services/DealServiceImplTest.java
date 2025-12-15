/**
 *
 */
package com.sabmiller.core.deals.services;

import static org.mockito.Mockito.mock;

import de.hybris.bootstrap.annotations.UnitTest;
import de.hybris.platform.b2b.model.B2BUnitModel;
import de.hybris.platform.basecommerce.model.site.BaseSiteModel;
import de.hybris.platform.core.model.c2l.CurrencyModel;
import de.hybris.platform.core.model.c2l.LanguageModel;
import de.hybris.platform.core.model.user.UserModel;
import de.hybris.platform.servicelayer.event.EventService;
import de.hybris.platform.servicelayer.i18n.CommonI18NService;
import de.hybris.platform.site.BaseSiteService;
import de.hybris.platform.store.BaseStoreModel;
import de.hybris.platform.store.services.BaseStoreService;
import com.sabmiller.core.product.SabmProductService;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Date;
import java.util.HashSet;
import java.util.List;

import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.MockitoAnnotations;

import com.google.common.collect.Lists;
import com.sabmiller.core.deals.dao.DealsDao;
import com.sabmiller.core.enums.RepDrivenDealStatus;
import com.sabmiller.core.event.ConfirmEnableDealEmailEvent;
import com.sabmiller.core.model.AbstractDealConditionModel;
import com.sabmiller.core.model.ComplexDealConditionModel;
import com.sabmiller.core.model.DealConditionGroupModel;
import com.sabmiller.core.model.DealModel;
import com.sabmiller.core.model.MinQtyDealConditionModel;
import com.sabmiller.core.model.ProductDealConditionModel;
import com.sabmiller.core.model.RepDrivenDealConditionStatusModel;


/**
 *
 */
@UnitTest
public class DealServiceImplTest
{

	@Mock
	private EventService eventService;

	@Mock
	private BaseSiteService baseSiteService;

	@Mock
	private CommonI18NService commonI18NService;

	@Mock
	private BaseStoreService baseStoreService;

	@Mock
	private DealsDao dealDao;

	@Mock
	private SabmProductService productService;

	@InjectMocks
	private final DealsServiceImpl dealsServiceImpl = new DealsServiceImpl();

	@Before
	public void setUp()
	{
		MockitoAnnotations.initMocks(this);
	}

	@Test
	public void testInitializeEvent()
	{
		final BaseStoreModel baseStoreModel = mock(BaseStoreModel.class);
		final BaseSiteModel baseSiteModel = mock(BaseSiteModel.class);
		final UserModel userModel = mock(UserModel.class);
		final LanguageModel languageModel = mock(LanguageModel.class);
		final CurrencyModel currencyModel = mock(CurrencyModel.class);

		final ConfirmEnableDealEmailEvent confirmEnableDealEmailEvent = new ConfirmEnableDealEmailEvent("test",
				new ArrayList<String>(), new ArrayList<String>(), userModel, new ArrayList<String>(), new ArrayList<String>(),
				new B2BUnitModel(), "active");

		Mockito.when(baseStoreService.getBaseStoreForUid("sabmStore")).thenReturn(baseStoreModel);
		Mockito.when(baseSiteService.getBaseSiteForUID("sabmStore")).thenReturn(baseSiteModel);
		Mockito.when(commonI18NService.getLanguage("en")).thenReturn(languageModel);
		Mockito.when(commonI18NService.getCurrency("AUD")).thenReturn(currencyModel);

		dealsServiceImpl.initializeEvent(confirmEnableDealEmailEvent);
		Assert.assertEquals(baseStoreModel, confirmEnableDealEmailEvent.getBaseStore());
		Assert.assertEquals(baseSiteModel, confirmEnableDealEmailEvent.getSite());
		Assert.assertEquals(languageModel, confirmEnableDealEmailEvent.getLanguage());
		Assert.assertEquals(currencyModel, confirmEnableDealEmailEvent.getCurrency());
		Assert.assertNull(confirmEnableDealEmailEvent.getCustomer());
		Assert.assertEquals(userModel, confirmEnableDealEmailEvent.getFromUser());
	}

	@Test
	public void testGetDealsForProduct()
	{
		final String productCode = "1111111";
		final B2BUnitModel b2bUnitModel = Mockito.mock(B2BUnitModel.class);
		final Date fromDate = new Date(1461542400);
		final Date toDate = new Date(1461628800);

		final List<DealModel> nonComplexDeals = Lists.newArrayList();
		final DealModel deal = mock(DealModel.class);
		final RepDrivenDealConditionStatusModel nonRep = mock(RepDrivenDealConditionStatusModel.class);
		Mockito.when(nonRep.getStatus()).thenReturn(RepDrivenDealStatus.UNLOCKED);
		Mockito.when(deal.getRepDrivenDealStatus()).thenReturn(nonRep);
		Mockito.when(deal.getInStore()).thenReturn(Boolean.valueOf(true));
		nonComplexDeals.add(deal);

		final List<DealModel> complexAllDeals = Lists.newArrayList();
		final DealModel complexDeal = mock(DealModel.class);
		final RepDrivenDealConditionStatusModel complexRep = mock(RepDrivenDealConditionStatusModel.class);
		Mockito.when(complexRep.getStatus()).thenReturn(RepDrivenDealStatus.LOCKED);
		Mockito.when(complexDeal.getRepDrivenDealStatus()).thenReturn(complexRep);
		complexAllDeals.add(complexDeal);

		final DealConditionGroupModel dealConditionGroup = mock(DealConditionGroupModel.class);

		final List<AbstractDealConditionModel> dealConditions = Lists.newArrayList();


		Mockito.when(dealDao.getDealsForProduct(b2bUnitModel, Arrays.asList(productCode), fromDate, toDate))
				.thenReturn(nonComplexDeals);
		Mockito.when(b2bUnitModel.getComplexDeals()).thenReturn(new HashSet<>(complexAllDeals));
		Mockito.when(complexDeal.getCustomerPOType()).thenReturn("B2B");
		Mockito.when(complexDeal.getStatus()).thenReturn("R");
		Mockito.when(complexDeal.getStatus()).thenReturn("R");
		Mockito.when(complexDeal.getConditionGroup()).thenReturn(dealConditionGroup);
		Mockito.when(dealConditionGroup.getDealConditions()).thenReturn(dealConditions);
		final List<DealModel> normalDeals = dealsServiceImpl.getDealsForProduct(b2bUnitModel, Arrays.asList(productCode), fromDate,
				toDate);
		Assert.assertEquals(1, normalDeals.size());

		final ComplexDealConditionModel complexDealCondition = mock(ComplexDealConditionModel.class);
		dealConditions.add(complexDealCondition);
		Mockito.when(dealConditionGroup.getDealConditions()).thenReturn(dealConditions);
		Mockito.when(complexDealCondition.getProductCode()).thenReturn(productCode);
		final List<DealModel> allDeals = dealsServiceImpl.getDealsForProduct(b2bUnitModel, Arrays.asList(productCode), fromDate,
				toDate);
		Assert.assertEquals(1, allDeals.size());

		dealConditions.clear();
		final ProductDealConditionModel productDealCondition = mock(ProductDealConditionModel.class);
		dealConditions.add(productDealCondition);
		Mockito.when(dealConditionGroup.getDealConditions()).thenReturn(dealConditions);
		Mockito.when(productDealCondition.getProductCode()).thenReturn(productCode);
		final List<DealModel> allDeals2 = dealsServiceImpl.getDealsForProduct(b2bUnitModel, Arrays.asList(productCode), fromDate,
				toDate);
		Assert.assertEquals(2, allDeals2.size());

		dealConditions.clear();
		final MinQtyDealConditionModel minQtyDealCondition = mock(MinQtyDealConditionModel.class);
		dealConditions.add(minQtyDealCondition);
		Mockito.when(dealConditionGroup.getDealConditions()).thenReturn(dealConditions);
		final List<DealModel> normalDeals2 = dealsServiceImpl.getDealsForProduct(b2bUnitModel, Arrays.asList(productCode), fromDate,
				toDate);
		Assert.assertEquals(1, normalDeals2.size());
	}

	@Test
	public void testGetLostDeal()
	{
		// see the test case DealLostCheckerTest.testDealsServiceImpl();
	}
}
