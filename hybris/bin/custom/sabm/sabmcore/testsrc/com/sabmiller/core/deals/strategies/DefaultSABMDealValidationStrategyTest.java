/**
 *
 */
package com.sabmiller.core.deals.strategies;

import de.hybris.bootstrap.annotations.UnitTest;

import java.util.Date;

import org.apache.commons.lang3.time.DateUtils;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.mockito.InjectMocks;
import org.mockito.MockitoAnnotations;

import com.sabmiller.core.model.DealModel;


/**
 * @author xue.zeng
 *
 */
@UnitTest
public class DefaultSABMDealValidationStrategyTest
{
	@InjectMocks
	private DefaultSABMDealValidationStrategy sabmDealValidationStrategy;

	@Before
	public void setUp()
	{
		MockitoAnnotations.initMocks(this);
	}

	@Test
	public void testValidateNoExpired()
	{
		final Date currentDate = new Date();
		final DealModel deal = new DealModel();

		// Scene 1: the validTo date is the current date
		deal.setValidTo(currentDate);
		final boolean result1 = sabmDealValidationStrategy.validateNoExpired(deal, currentDate);
		Assert.assertTrue(result1);

		// Scene 2: the validTo date is the previous date
		final Date previousDate = DateUtils.addDays(currentDate, -1);
		deal.setValidTo(previousDate);
		final boolean result2 = sabmDealValidationStrategy.validateNoExpired(deal, currentDate);
		Assert.assertFalse(result2);

		final Date previousMonthDate = DateUtils.addMonths(currentDate, -1);
		deal.setValidTo(previousMonthDate);
		final boolean result3 = sabmDealValidationStrategy.validateNoExpired(deal, currentDate);
		Assert.assertFalse(result3);

		final Date previousYearDate = DateUtils.addYears(currentDate, -1);
		deal.setValidTo(previousYearDate);
		final boolean result4 = sabmDealValidationStrategy.validateNoExpired(deal, currentDate);
		Assert.assertFalse(result4);

		// Scene 3: the validTo date is the last date
		final Date nextDate = DateUtils.addDays(currentDate, 1);
		deal.setValidTo(nextDate);
		final boolean result5 = sabmDealValidationStrategy.validateNoExpired(deal, currentDate);
		Assert.assertTrue(result5);

		final Date nextMonthDate = DateUtils.addMonths(currentDate, 1);
		deal.setValidTo(nextMonthDate);
		final boolean result6 = sabmDealValidationStrategy.validateNoExpired(deal, currentDate);
		Assert.assertTrue(result6);

		final Date nextYearDate = DateUtils.addYears(currentDate, 1);
		deal.setValidTo(nextYearDate);
		final boolean result7 = sabmDealValidationStrategy.validateNoExpired(deal, currentDate);
		Assert.assertTrue(result7);
	}

	@Test
	public void testValidateStarted()
	{
		final Date currentDate = new Date();
		final DealModel deal = new DealModel();

		// Scene 1: the validTo date is the current date
		deal.setValidFrom(currentDate);
		final boolean result1 = sabmDealValidationStrategy.validateStarted(deal, currentDate);
		Assert.assertTrue(result1);

		// Scene 2: the validTo date is the previous date
		final Date previousDate = DateUtils.addDays(currentDate, -1);
		deal.setValidFrom(previousDate);
		final boolean result2 = sabmDealValidationStrategy.validateStarted(deal, currentDate);
		Assert.assertTrue(result2);

		final Date previousMonthDate = DateUtils.addMonths(currentDate, -1);
		deal.setValidFrom(previousMonthDate);
		final boolean result3 = sabmDealValidationStrategy.validateStarted(deal, currentDate);
		Assert.assertTrue(result3);

		final Date previousYearDate = DateUtils.addYears(currentDate, -1);
		deal.setValidFrom(previousYearDate);
		final boolean result4 = sabmDealValidationStrategy.validateStarted(deal, currentDate);
		Assert.assertTrue(result4);

		// Scene 3: the validTo date is the last date
		final Date nextDate = DateUtils.addDays(currentDate, 1);
		deal.setValidFrom(nextDate);
		final boolean result5 = sabmDealValidationStrategy.validateStarted(deal, currentDate);
		Assert.assertFalse(result5);

		final Date nextMonthDate = DateUtils.addMonths(currentDate, 1);
		deal.setValidFrom(nextMonthDate);
		final boolean result6 = sabmDealValidationStrategy.validateStarted(deal, currentDate);
		Assert.assertFalse(result6);

		final Date nextYearDate = DateUtils.addYears(currentDate, 1);
		deal.setValidFrom(nextYearDate);
		final boolean result7 = sabmDealValidationStrategy.validateStarted(deal, currentDate);
		Assert.assertFalse(result7);
	}
}
