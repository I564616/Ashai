/**
 *
 */
package com.apb.facades.populators;

import java.util.Date;

import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.MockitoAnnotations;

import com.apb.core.util.AsahiDateUtil;
import com.apb.facades.deal.data.AsahiDealData;
import com.sabmiller.core.model.AsahiDealModel;


/**
 * @author Saumya.Mittal1
 *
 */
public class AsahiDealDataPopulatorTest
{


	@InjectMocks
	private final AsahiDealDataPopulator dealPopulator = new AsahiDealDataPopulator();

	@Mock
	private AsahiDateUtil asahiDateUtil;

	@Before
	public void setUp()
	{
		MockitoAnnotations.initMocks(this);
		Mockito.when(asahiDateUtil.getDifferenceInDays(Mockito.any(), Mockito.any())).thenReturn("10");
	}

	@Test
	public void testPopulate()
	{
		final AsahiDealModel source = Mockito.mock(AsahiDealModel.class);
		Mockito.when(source.getCode()).thenReturn("deal_001");
		Mockito.when(source.getValidFrom()).thenReturn(new Date());
		Mockito.when(source.getValidTo()).thenReturn(new Date(new Date().getTime() + 864000000));

		final AsahiDealData target = new AsahiDealData();
		dealPopulator.populate(source, target);
		Assert.assertEquals("deal_001", target.getCode());
	}

}
