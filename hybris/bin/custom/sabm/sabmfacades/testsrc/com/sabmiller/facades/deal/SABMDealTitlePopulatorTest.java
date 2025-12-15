/**
 *
 */
package com.sabmiller.facades.deal;

import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.mock;

import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

import org.assertj.core.util.Arrays;
import org.junit.Before;
import org.junit.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.context.MessageSource;

import com.sabmiller.core.model.AbstractDealBenefitModel;
import com.sabmiller.core.model.DealConditionGroupModel;
import com.sabmiller.core.model.DealModel;
import com.sabmiller.core.model.DealScaleModel;
import com.sabmiller.core.model.DiscountDealBenefitModel;
import com.sabmiller.core.model.FreeGoodsDealBenefitModel;
import com.sabmiller.facades.populators.SABMDealTitlePopulator;


/**
 *
 */
public class SABMDealTitlePopulatorTest
{
	@InjectMocks
	private final SABMDealTitlePopulator populator = new SABMDealTitlePopulator();

	@Mock
	MessageSource messageSource;

	@Before
	public void setUp()
	{
		MockitoAnnotations.initMocks(this);
	}

	@Test
	public void testPopulateMaxTimesTitleSuffix01()
	{
		final DealModel deal1 = mock(DealModel.class);
		final List<DealModel> deals = new ArrayList<>();
		deals.add(deal1);
		final Locale locale = Locale.ENGLISH;
		given(messageSource.getMessage("text.deal.title.suffix", Arrays.array("1 deal"), locale))
				.willReturn("(Maximum of 1 deal per order)");
		given(messageSource.getMessage("text.deal.title.suffix", Arrays.array("3 deals"), locale))
				.willReturn("(Maximum of 3 deals per order)");
		final DealConditionGroupModel conditionGroup = mock(DealConditionGroupModel.class);
		final FreeGoodsDealBenefitModel benefit1 = mock(FreeGoodsDealBenefitModel.class);
		final FreeGoodsDealBenefitModel benefit2 = mock(FreeGoodsDealBenefitModel.class);
		final FreeGoodsDealBenefitModel benefit3 = mock(FreeGoodsDealBenefitModel.class);
		final DealScaleModel scale1 = mock(DealScaleModel.class);
		final DealScaleModel scale2 = mock(DealScaleModel.class);
		final DealScaleModel scale3 = mock(DealScaleModel.class);
		given(scale1.getScale()).willReturn("001");
		given(scale2.getScale()).willReturn("002");
		given(scale3.getScale()).willReturn("003");
		given(scale1.getFrom()).willReturn(10);
		given(scale2.getFrom()).willReturn(20);
		given(scale3.getFrom()).willReturn(30);
		final List<DealScaleModel> DealScaleModels = new ArrayList<DealScaleModel>();
		DealScaleModels.add(scale1);
		DealScaleModels.add(scale2);
		DealScaleModels.add(scale3);

		given(benefit1.getProportionalFreeGood()).willReturn(false);
		given(benefit2.getProportionalFreeGood()).willReturn(false);
		given(benefit3.getProportionalFreeGood()).willReturn(false);
		given(benefit1.getProportionalAmount()).willReturn(false);
		given(benefit2.getProportionalAmount()).willReturn(false);
		given(benefit3.getProportionalAmount()).willReturn(false);
		given(benefit1.getScale()).willReturn("001");
		given(benefit2.getScale()).willReturn("002");
		given(benefit3.getScale()).willReturn("003");
		given(benefit1.getQuantity()).willReturn(1);
		given(benefit2.getQuantity()).willReturn(2);
		given(benefit3.getQuantity()).willReturn(3);
		given(benefit1.getDealConditionGroup()).willReturn(conditionGroup);
		given(benefit2.getDealConditionGroup()).willReturn(conditionGroup);
		given(benefit3.getDealConditionGroup()).willReturn(conditionGroup);
		final List<AbstractDealBenefitModel> benefits = new ArrayList<AbstractDealBenefitModel>();
		benefits.add(benefit1);
		benefits.add(benefit2);
		benefits.add(benefit3);
		given(conditionGroup.getDealBenefits()).willReturn(benefits);
		given(conditionGroup.getDealScales()).willReturn(DealScaleModels);
		given(deal1.getConditionGroup()).willReturn(conditionGroup);
		/*
		 * Assert.assertEquals("(Maximum of 3 deals per order)", populator.populateTitleSuffix(deals, locale, new
		 * HashMap<String, String>()));
		 */
	}

	@Test
	public void testPopulateMaxTimesTitleSuffix02()
	{
		final DealModel deal1 = mock(DealModel.class);
		final List<DealModel> deals = new ArrayList<>();
		deals.add(deal1);
		final Locale locale = Locale.ENGLISH;
		given(messageSource.getMessage("text.deal.title.suffix", Arrays.array("1 deal"), locale))
				.willReturn("(Maximum of 1 deal per order)");
		given(messageSource.getMessage("text.deal.title.suffix", Arrays.array("3 deals"), locale))
				.willReturn("(Maximum of 3 deals per order)");
		final DealConditionGroupModel conditionGroup = mock(DealConditionGroupModel.class);
		final FreeGoodsDealBenefitModel benefit1 = mock(FreeGoodsDealBenefitModel.class);
		given(benefit1.getProportionalFreeGood()).willReturn(false);

		given(benefit1.getProportionalAmount()).willReturn(false);

		given(benefit1.getScale()).willReturn(null);

		given(benefit1.getQuantity()).willReturn(1);

		final List<AbstractDealBenefitModel> benefits = new ArrayList<AbstractDealBenefitModel>();
		benefits.add(benefit1);
		given(conditionGroup.getDealBenefits()).willReturn(benefits);
		given(deal1.getConditionGroup()).willReturn(conditionGroup);
		/*
		 * Assert.assertEquals("(Maximum of 1 deal per order)", populator.populateTitleSuffix(deals, locale, new
		 * HashMap<String, String>()));
		 */
	}

	@Test
	public void testPopulateMaxTimesTitleSuffix03()
	{
		final DealModel deal1 = mock(DealModel.class);
		final List<DealModel> deals = new ArrayList<>();
		deals.add(deal1);
		final Locale locale = Locale.ENGLISH;
		given(messageSource.getMessage("text.deal.title.suffix", Arrays.array("1 deal"), locale))
				.willReturn("(Maximum of 1 deal per order)");
		given(messageSource.getMessage("text.deal.title.suffix", Arrays.array("3 deals"), locale))
				.willReturn("(Maximum of 3 deals per order)");
		final DealConditionGroupModel conditionGroup = mock(DealConditionGroupModel.class);
		final FreeGoodsDealBenefitModel benefit1 = mock(FreeGoodsDealBenefitModel.class);
		final FreeGoodsDealBenefitModel benefit2 = mock(FreeGoodsDealBenefitModel.class);

		final DealScaleModel scale1 = mock(DealScaleModel.class);
		final DealScaleModel scale2 = mock(DealScaleModel.class);
		final DealScaleModel scale3 = mock(DealScaleModel.class);
		given(scale1.getScale()).willReturn("001");
		given(scale2.getScale()).willReturn("002");
		given(scale3.getScale()).willReturn("003");
		given(scale1.getFrom()).willReturn(10);
		given(scale2.getFrom()).willReturn(200);
		given(scale3.getFrom()).willReturn(30);
		final List<DealScaleModel> DealScaleModels = new ArrayList<DealScaleModel>();
		DealScaleModels.add(scale1);
		DealScaleModels.add(scale2);
		DealScaleModels.add(scale3);

		given(benefit1.getProportionalFreeGood()).willReturn(false);
		given(benefit2.getProportionalFreeGood()).willReturn(false);

		given(benefit1.getProportionalAmount()).willReturn(false);
		given(benefit2.getProportionalAmount()).willReturn(false);

		given(benefit1.getScale()).willReturn("001");
		given(benefit2.getScale()).willReturn("002");

		given(benefit1.getQuantity()).willReturn(1);
		given(benefit2.getQuantity()).willReturn(10);

		given(benefit1.getDealConditionGroup()).willReturn(conditionGroup);
		given(benefit2.getDealConditionGroup()).willReturn(conditionGroup);

		final List<AbstractDealBenefitModel> benefits = new ArrayList<AbstractDealBenefitModel>();
		benefits.add(benefit1);
		benefits.add(benefit2);
		given(conditionGroup.getDealBenefits()).willReturn(benefits);
		given(conditionGroup.getDealScales()).willReturn(DealScaleModels);
		given(deal1.getConditionGroup()).willReturn(conditionGroup);
		/*
		 * Assert.assertEquals("(Maximum of 1 deal per order)", populator.populateTitleSuffix(deals, locale, new
		 * HashMap<String, String>()));
		 */
	}

	@Test
	public void testPopulateMaxTimesTitleSuffix04()
	{
		final DealModel deal1 = mock(DealModel.class);
		final List<DealModel> deals = new ArrayList<>();
		deals.add(deal1);
		final Locale locale = Locale.ENGLISH;
		given(messageSource.getMessage("text.deal.title.suffix", Arrays.array("1 deal"), locale))
				.willReturn("(Maximum of 1 deal per order)");
		given(messageSource.getMessage("text.deal.title.suffix", Arrays.array("3 deals"), locale))
				.willReturn("(Maximum of 3 deals per order)");
		final DealConditionGroupModel conditionGroup = mock(DealConditionGroupModel.class);
		final FreeGoodsDealBenefitModel benefit1 = mock(FreeGoodsDealBenefitModel.class);
		final DiscountDealBenefitModel benefit2 = mock(DiscountDealBenefitModel.class);

		given(benefit1.getProportionalFreeGood()).willReturn(false);
		given(benefit2.getProportionalFreeGood()).willReturn(false);

		given(benefit1.getProportionalAmount()).willReturn(false);
		given(benefit2.getProportionalAmount()).willReturn(false);

		given(benefit1.getScale()).willReturn("10");
		given(benefit2.getScale()).willReturn("200");

		given(benefit1.getQuantity()).willReturn(1);
		given(benefit2.getAmount()).willReturn(20.00);

		given(benefit1.getDealConditionGroup()).willReturn(conditionGroup);
		given(benefit2.getDealConditionGroup()).willReturn(conditionGroup);

		final List<AbstractDealBenefitModel> benefits = new ArrayList<AbstractDealBenefitModel>();
		benefits.add(benefit1);
		benefits.add(benefit2);
		given(conditionGroup.getDealBenefits()).willReturn(benefits);

		given(deal1.getConditionGroup()).willReturn(conditionGroup);
		/*
		 * Assert.assertEquals("(Maximum of 1 deal per order)", populator.populateTitleSuffix(deals, locale, new
		 * HashMap<String, String>()));
		 */
	}

	@Test
	public void testPopulateMaxTimesTitleSuffix05()
	{
		final DealModel deal1 = mock(DealModel.class);
		final List<DealModel> deals = new ArrayList<>();
		deals.add(deal1);
		final Locale locale = Locale.ENGLISH;
		given(messageSource.getMessage("text.deal.title.suffix", Arrays.array("1 deal"), locale))
				.willReturn("(Maximum of 1 deal per order)");
		given(messageSource.getMessage("text.deal.title.suffix", Arrays.array("2 deals"), locale))
				.willReturn("(Maximum of 2 deals per order)");
		final DealConditionGroupModel conditionGroup = mock(DealConditionGroupModel.class);
		final DiscountDealBenefitModel benefit1 = mock(DiscountDealBenefitModel.class);
		final DiscountDealBenefitModel benefit2 = mock(DiscountDealBenefitModel.class);

		final DealScaleModel scale1 = mock(DealScaleModel.class);
		final DealScaleModel scale2 = mock(DealScaleModel.class);
		final DealScaleModel scale3 = mock(DealScaleModel.class);
		given(scale1.getScale()).willReturn("001");
		given(scale2.getScale()).willReturn("002");
		given(scale3.getScale()).willReturn("003");
		given(scale1.getFrom()).willReturn(10);
		given(scale2.getFrom()).willReturn(20);
		given(scale3.getFrom()).willReturn(30);
		final List<DealScaleModel> DealScaleModels = new ArrayList<DealScaleModel>();
		DealScaleModels.add(scale1);
		DealScaleModels.add(scale2);
		DealScaleModels.add(scale3);

		given(benefit1.getProportionalFreeGood()).willReturn(false);
		given(benefit2.getProportionalFreeGood()).willReturn(false);

		given(benefit1.getProportionalAmount()).willReturn(false);
		given(benefit2.getProportionalAmount()).willReturn(false);

		given(benefit1.getScale()).willReturn("001");
		given(benefit2.getScale()).willReturn("002");

		given(benefit1.getAmount()).willReturn(1.00);
		given(benefit2.getAmount()).willReturn(2.00);

		given(benefit1.getDealConditionGroup()).willReturn(conditionGroup);
		given(benefit2.getDealConditionGroup()).willReturn(conditionGroup);

		final List<AbstractDealBenefitModel> benefits = new ArrayList<AbstractDealBenefitModel>();
		benefits.add(benefit1);
		benefits.add(benefit2);
		given(conditionGroup.getDealBenefits()).willReturn(benefits);
		given(deal1.getConditionGroup()).willReturn(conditionGroup);
		given(conditionGroup.getDealScales()).willReturn(DealScaleModels);
		/*
		 * Assert.assertEquals("(Maximum of 2 deals per order)", populator.populateTitleSuffix(deals, locale, new
		 * HashMap<String, String>()));
		 */
	}

	@Test
	public void testPopulateMaxTimesTitleSuffix06()
	{
		final DealModel deal1 = mock(DealModel.class);
		final List<DealModel> deals = new ArrayList<>();
		deals.add(deal1);
		final Locale locale = Locale.ENGLISH;
		given(messageSource.getMessage("text.deal.title.suffix", Arrays.array("1 deal"), locale))
				.willReturn("(Maximum of 1 deal per order)");
		given(messageSource.getMessage("text.deal.title.suffix", Arrays.array("2 deals"), locale))
				.willReturn("(Maximum of 2 deals per order)");
		final DealConditionGroupModel conditionGroup = mock(DealConditionGroupModel.class);
		final DiscountDealBenefitModel benefit1 = mock(DiscountDealBenefitModel.class);
		final DiscountDealBenefitModel benefit2 = mock(DiscountDealBenefitModel.class);

		final DealScaleModel scale1 = mock(DealScaleModel.class);
		final DealScaleModel scale2 = mock(DealScaleModel.class);
		final DealScaleModel scale3 = mock(DealScaleModel.class);
		given(scale1.getScale()).willReturn("001");
		given(scale2.getScale()).willReturn("002");
		given(scale3.getScale()).willReturn("003");
		given(scale1.getFrom()).willReturn(14);
		given(scale2.getFrom()).willReturn(100);
		given(scale3.getFrom()).willReturn(30);
		final List<DealScaleModel> DealScaleModels = new ArrayList<DealScaleModel>();
		DealScaleModels.add(scale1);
		DealScaleModels.add(scale2);
		DealScaleModels.add(scale3);

		given(benefit1.getProportionalFreeGood()).willReturn(false);
		given(benefit2.getProportionalFreeGood()).willReturn(false);

		given(benefit1.getProportionalAmount()).willReturn(false);
		given(benefit2.getProportionalAmount()).willReturn(false);

		given(benefit1.getScale()).willReturn("001");
		given(benefit2.getScale()).willReturn("002");

		given(benefit1.getAmount()).willReturn(1.00);
		given(benefit2.getAmount()).willReturn(10.00);

		given(benefit1.getDealConditionGroup()).willReturn(conditionGroup);
		given(benefit2.getDealConditionGroup()).willReturn(conditionGroup);

		final List<AbstractDealBenefitModel> benefits = new ArrayList<AbstractDealBenefitModel>();
		benefits.add(benefit1);
		benefits.add(benefit2);
		given(conditionGroup.getDealBenefits()).willReturn(benefits);
		given(deal1.getConditionGroup()).willReturn(conditionGroup);
		given(conditionGroup.getDealScales()).willReturn(DealScaleModels);
		/*
		 * Assert.assertEquals("(Maximum of 1 deal per order)", populator.populateTitleSuffix(deals, locale, new
		 * HashMap<String, String>()));
		 */
	}
}
