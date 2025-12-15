/**
 *
 */
package com.sabmiller.facades.order.hook.impl;

import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.mock;

import de.hybris.platform.commerceservices.service.data.CommerceCartParameter;
import de.hybris.platform.core.model.order.AbstractOrderEntryModel;
import de.hybris.platform.core.model.order.CartModel;
import de.hybris.platform.servicelayer.model.ModelService;

import java.util.List;

import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import com.apb.core.util.AsahiSiteUtil;
import com.google.common.collect.Lists;


/**
 * @author xue.zeng
 *
 */
public class SABMCommerceCartCalculationMethodHookTest
{
	@Mock
	private ModelService modelService;

	@Mock
	private AsahiSiteUtil asahiSiteUtil;
	@InjectMocks
	private SABMCommerceCartCalculationMethodHook sabmCommerceCartCalculationMethodHook;

	@Before
	public void setUp()
	{
		MockitoAnnotations.initMocks(this);
	}

	@Test
	public void testAfterCalculate()
	{
		final CommerceCartParameter cartParameter = mock(CommerceCartParameter.class);
		final CartModel cart = mock(CartModel.class);

		final List<AbstractOrderEntryModel> orderEntrys = Lists.newArrayList();
		final AbstractOrderEntryModel abstractOrderEntry = new AbstractOrderEntryModel();
		abstractOrderEntry.setIsChange(Boolean.TRUE);
		orderEntrys.add(abstractOrderEntry);

		given(cartParameter.getCart()).willReturn(cart);
		given(cart.getEntries()).willReturn(orderEntrys);
		given(asahiSiteUtil.isCub()).willReturn(true);
		sabmCommerceCartCalculationMethodHook.afterCalculate(cartParameter);
		Assert.assertEquals(false, orderEntrys.get(0).getIsChange().booleanValue());
	}
}
