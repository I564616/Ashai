/**
 *
 */
package com.sabmiller.core.deals.services;

import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.mock;

import de.hybris.platform.b2b.model.B2BUnitModel;
import de.hybris.platform.servicelayer.session.SessionService;

import java.util.Arrays;
import java.util.Collections;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import org.apache.commons.lang3.time.DateUtils;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.MockitoAnnotations;
import static org.assertj.core.api.Assertions.assertThat;

import com.google.common.collect.Lists;
import com.sabmiller.core.deals.dao.DealsDao;
import com.sabmiller.core.deals.strategies.SABMDealValidationStrategy;
import com.sabmiller.core.model.DealModel;
import java.util.Date;

/**
 * DealServiceImplTest1
 */
public class DealServiceImplTest1
{

	@InjectMocks
	private final DealsServiceImpl dealsServiceImpl = new DealsServiceImpl();

	@Mock
	private SABMDealValidationStrategy dealValidationStrategy;
	@Mock
	private SessionService sessionService;
	@Mock
	private DealsDao dealsDao;

	@Before
	public void setUp()
	{
		MockitoAnnotations.initMocks(this);
	}


	@Test
	public void testDealValidation()
	{
		final List<DealModel> complexAllDeals = Lists.newArrayList();
		final DealModel complexDeal = mock(DealModel.class);
		given(complexDeal.getCode()).willReturn("0000123");

		given(sessionService.getAttribute("session_delivery_date")).willReturn(new Date());

		given(complexDeal.getValidFrom()).willReturn(DateUtils.addDays(new Date(), -5));
		given(complexDeal.getValidTo()).willReturn(DateUtils.addDays(new Date(), 5));

		complexAllDeals.add(complexDeal);
		Mockito.when(dealValidationStrategy.validateDeal(complexDeal)).thenReturn(Boolean.TRUE);
		final List<DealModel> deals = dealsServiceImpl.getValidationDeals(complexAllDeals, Boolean.FALSE);

		Assert.assertEquals(1, deals.size());

		Assert.assertEquals("0000123", deals.get(0).getCode());


		final List<DealModel> complexAllDeals1 = Lists.newArrayList();
		final List<DealModel> deals1 = dealsServiceImpl.getValidationDeals(complexAllDeals1, Boolean.FALSE);

		assertThat(deals1).isEmpty();

		Mockito.when(dealValidationStrategy.validateDeal(complexDeal)).thenReturn(Boolean.FALSE);
		final List<DealModel> deals2 = dealsServiceImpl.getValidationDeals(complexAllDeals, Boolean.FALSE);

		Assert.assertEquals(0, deals2.size());

	}

	/**
	 * FOR SABMC-1276
	 */
	@Test
	public void testGetSpecificDeals()
	{
		final DealModel complexDeal1 = mock(DealModel.class);
		final DealModel complexDeal2 = mock(DealModel.class); // invalid one
		given(complexDeal1.getCode()).willReturn("0000123");
		given(complexDeal2.getCode()).willReturn("0000555");
		given(complexDeal1.getInStore()).willReturn(Boolean.TRUE);
		given(complexDeal2.getInStore()).willReturn(Boolean.TRUE);

		final List<DealModel> complexAllDeals = Arrays.asList(complexDeal1, complexDeal2);
		final Set<DealModel> complexAllDealsSet = new HashSet<DealModel>();
		complexAllDealsSet.addAll(complexAllDeals);

		final B2BUnitModel b2bUnit = mock(B2BUnitModel.class);

		given(b2bUnit.getComplexDeals()).willReturn(complexAllDealsSet);
		given(dealsDao.getNonComplexDeals(b2bUnit, true)).willReturn(Collections.emptyList());
		given(dealValidationStrategy.validateDeal(complexDeal1, b2bUnit)).willReturn(true);
		given(dealValidationStrategy.validateDeal(complexDeal2, b2bUnit)).willReturn(false);


		final List<DealModel> deals = dealsServiceImpl.getSpecificDeals(b2bUnit, true);
		Assert.assertEquals(1, deals.size());
		Assert.assertEquals("0000123", deals.get(0).getCode());

	}
}
