/**
 *
 */
package com.sabmiller.facades.populators;

import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.mock;

import de.hybris.bootstrap.annotations.UnitTest;
import de.hybris.platform.converters.Populator;

import java.util.ArrayList;
import java.util.List;

import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import com.sabmiller.core.model.DealModel;
import com.sabmiller.facades.deal.data.ConflictDealJson;
import com.sabmiller.facades.deal.data.DealJson;


/**
 *
 */
@UnitTest
public class SABMConflictDealJsonPopulatorTest
{
	@InjectMocks
	private final SABMConflictDealJsonPopulator sabmConflictDealJsonPopulator = new SABMConflictDealJsonPopulator();

	@Mock
	private Populator<List<DealModel>, DealJson> dealTitlePopulator;
	@Mock
	private SABMDealProductPopulator dealProductPopulator;;

	@Before
	public void setUp()
	{
		MockitoAnnotations.initMocks(this);
	}

	@Test
	public void testPopulate()
	{
		final DealModel source = mock(DealModel.class);
		final ConflictDealJson target = new ConflictDealJson();

		final List<DealModel> dealList = new ArrayList<DealModel>();
		dealList.add(source);
		given(source.getCode()).willReturn("testCode");
		
		sabmConflictDealJsonPopulator.populate(dealList, target);

		Assert.assertEquals("testCode", target.getCode());

	}

}
