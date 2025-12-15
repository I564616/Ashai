/**
 *
 */
package com.sabmiller.facades.order.impl;

import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

import de.hybris.bootstrap.annotations.UnitTest;
import de.hybris.platform.b2b.model.B2BUnitModel;
import de.hybris.platform.b2bcommercefacades.company.B2BUnitFacade;
import de.hybris.platform.b2b.company.B2BCommerceUnitService;
import de.hybris.platform.core.model.order.AbstractOrderEntryModel;
import de.hybris.platform.core.model.order.SABMOrderTemplateModel;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import com.sabmiller.commons.enumerations.OrderToCartStatus;
import com.sabmiller.core.b2b.services.SabmOrderTemplateService;
import com.sabmiller.facades.b2bunit.SabmB2BCommerceUnitFacade;


/**
 * @author xiaowu.a.zhang
 * @date 01/06/2016
 */
@UnitTest
public class DefaultSABMOrderFacadeUnitTest
{
	@InjectMocks
	private final DefaultSABMOrderFacade orderFacade = new DefaultSABMOrderFacade();

	@Mock
	private B2BCommerceUnitService b2bCommerceUnitService;

	@Mock
	private SabmOrderTemplateService sabmOrderTemplateService;
	
	@Mock
	private SabmB2BCommerceUnitFacade b2bUnitFacade;

	@Before
	public void setUp() throws Exception
	{
		MockitoAnnotations.initMocks(this);
	}

	@Test
	public void testAllEntryQuantityZero()
	{
		final String orderTemplateCode = "mockCode";
		final SABMOrderTemplateModel orderTemplate = mock(SABMOrderTemplateModel.class);

		final List<AbstractOrderEntryModel> entries = new ArrayList<>();
		final AbstractOrderEntryModel entryModel1 = mock(AbstractOrderEntryModel.class);
		final AbstractOrderEntryModel entryModel2 = mock(AbstractOrderEntryModel.class);
		entries.add(entryModel1);
		entries.add(entryModel2);
		when(orderTemplate.getEntries()).thenReturn(entries);

		final B2BUnitModel b2bUnit = mock(B2BUnitModel.class);
		when(b2bCommerceUnitService.getParentUnit()).thenReturn(b2bUnit);
		when(sabmOrderTemplateService.findOrderTemplateByCode(orderTemplateCode, b2bUnit)).thenReturn(orderTemplate);

		final Map<OrderToCartStatus, Object> resultMap = orderFacade.addToTemplate(orderTemplateCode);

		Assert.assertEquals(true, resultMap.containsKey(OrderToCartStatus.EMPTY_ADD_TO_CART));
	}

	@Test
	public void testAllEntryQuantityNotZero()
	{
		final String orderTemplateCode = "mockCode";
		final SABMOrderTemplateModel orderTemplate = mock(SABMOrderTemplateModel.class);

		final List<AbstractOrderEntryModel> entries = new ArrayList<>();
		final AbstractOrderEntryModel entryModel1 = mock(AbstractOrderEntryModel.class);
		when(entryModel1.getQuantity()).thenReturn(Long.valueOf(10));
		final AbstractOrderEntryModel entryModel2 = mock(AbstractOrderEntryModel.class);
		entries.add(entryModel1);
		entries.add(entryModel2);
		when(orderTemplate.getEntries()).thenReturn(entries);

		final B2BUnitModel b2bUnit = mock(B2BUnitModel.class);
		when(b2bCommerceUnitService.getParentUnit()).thenReturn(b2bUnit);
		when(sabmOrderTemplateService.findOrderTemplateByCode(orderTemplateCode, b2bUnit)).thenReturn(orderTemplate);
		
		final Map<OrderToCartStatus, Object> resultMap = orderFacade.addToTemplate(orderTemplateCode);

		Assert.assertEquals(false, resultMap.containsKey(OrderToCartStatus.EMPTY_ADD_TO_CART));
	}

}
