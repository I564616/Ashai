/**
 *
 */
package com.sabmiller.core.strategy.impl;

import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.when;

import de.hybris.bootstrap.annotations.UnitTest;
import de.hybris.platform.commerceservices.order.CommerceCartModification;
import de.hybris.platform.commerceservices.order.CommerceCartModificationStatus;
import de.hybris.platform.core.model.order.AbstractOrderEntryModel;
import de.hybris.platform.core.model.order.SABMOrderTemplateModel;
import de.hybris.platform.servicelayer.model.ModelService;

import java.util.Date;
import java.util.HashMap;
import java.util.Map;

import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.MockitoAnnotations;

import com.apb.core.util.AsahiSiteUtil;
import com.sabmiller.core.cart.service.SABMCartService;
import com.sabmiller.core.constants.SabmCoreConstants;
import com.sabmiller.core.model.SABMAlcoholVariantProductEANModel;
import com.sabmiller.core.model.SABMAlcoholVariantProductMaterialModel;


/**
 *
 */
@UnitTest
public class DefaultSABMUpdateOrderTemplateEntryStrategyTest
{
	@InjectMocks
	private final DefaultSABMUpdateOrderTemplateEntryStrategy defaultSABMUpdateOrderTemplateEntryStrategy = new DefaultSABMUpdateOrderTemplateEntryStrategy();

	@Mock
	private ModelService modelService;

	@Mock
	private AsahiSiteUtil asahiSiteUtil;

	@Mock(name = "cartService")
	private SABMCartService sabmCartService;

	@Before
	public void setUp()
	{
		MockitoAnnotations.initMocks(this);
		defaultSABMUpdateOrderTemplateEntryStrategy.setModelService(modelService);
	}

	@Test
	public void testSuccessModifyEntry()
	{
		given(asahiSiteUtil.isCub()).willReturn(true);
		final SABMOrderTemplateModel mockCart = Mockito.mock(SABMOrderTemplateModel.class);
		final AbstractOrderEntryModel mockEntry = Mockito.mock(AbstractOrderEntryModel.class);
		final SABMAlcoholVariantProductMaterialModel productModel = Mockito.mock(SABMAlcoholVariantProductMaterialModel.class);
		final SABMAlcoholVariantProductEANModel alcoholVariantProductEANModel = Mockito
				.mock(SABMAlcoholVariantProductEANModel.class);
		final Map<String, Object> maxOrderQuantityMap = new HashMap<String, Object>();
		maxOrderQuantityMap.put(SabmCoreConstants.FINAL_MAX_ORDER_QTY, 3);
		maxOrderQuantityMap.put(SabmCoreConstants.TOTAL_ORDERED_QTY, 2);
		maxOrderQuantityMap.put(SabmCoreConstants.FINAL_MAX_ORDER_QTY, 5);

		when(productModel.getBaseProduct()).thenReturn(alcoholVariantProductEANModel);
		given(mockEntry.getQuantity()).willReturn(Long.valueOf(1));
		given(mockEntry.getProduct()).willReturn(productModel);
		when(mockCart.getRequestedDeliveryDate()).thenReturn(new Date());
		when(sabmCartService.getFinalMaxOrderQty(productModel, new Date())).thenReturn(maxOrderQuantityMap);
		final long actualAllowedQuantityChange = 2;
		final long newQuantity = 3;
		final Integer maxOrderQuantity = null;

		final CommerceCartModification modification = defaultSABMUpdateOrderTemplateEntryStrategy.modifyEntry(mockCart, mockEntry,
				actualAllowedQuantityChange, newQuantity, maxOrderQuantity);

		Assert.assertEquals(CommerceCartModificationStatus.SUCCESS, modification.getStatusCode());
		Assert.assertEquals(3, modification.getQuantity());
	}

	@Test
	public void testLowStockModifyEntry()
	{
		final SABMOrderTemplateModel mockCart = Mockito.mock(SABMOrderTemplateModel.class);
		final AbstractOrderEntryModel mockEntry = Mockito.mock(AbstractOrderEntryModel.class);
		given(mockEntry.getQuantity()).willReturn(Long.valueOf(1));
		final long actualAllowedQuantityChange = -2;
		final long newQuantity = 1;
		final Integer maxOrderQuantity = null;

		final CommerceCartModification modification = defaultSABMUpdateOrderTemplateEntryStrategy.modifyEntry(mockCart, mockEntry,
				actualAllowedQuantityChange, newQuantity, maxOrderQuantity);

		Assert.assertEquals(CommerceCartModificationStatus.LOW_STOCK, modification.getStatusCode());
		Assert.assertEquals(0, modification.getQuantity());
	}

	@Test
	public void testMaxOrderQuantityModifyEntry()
	{
		final SABMOrderTemplateModel mockCart = Mockito.mock(SABMOrderTemplateModel.class);
		final AbstractOrderEntryModel mockEntry = Mockito.mock(AbstractOrderEntryModel.class);
		given(mockEntry.getQuantity()).willReturn(Long.valueOf(1));
		final long actualAllowedQuantityChange = -2;
		final long newQuantity = 1;
		final Integer maxOrderQuantity = Integer.valueOf(0);

		final CommerceCartModification modification = defaultSABMUpdateOrderTemplateEntryStrategy.modifyEntry(mockCart, mockEntry,
				actualAllowedQuantityChange, newQuantity, maxOrderQuantity);

		Assert.assertEquals(CommerceCartModificationStatus.MAX_ORDER_QUANTITY_EXCEEDED, modification.getStatusCode());
		Assert.assertEquals(0, modification.getQuantity());
	}

	@Test
	public void testRemoveEntry()
	{
		final SABMOrderTemplateModel mockCart = Mockito.mock(SABMOrderTemplateModel.class);
		final AbstractOrderEntryModel mockEntry = Mockito.mock(AbstractOrderEntryModel.class);
		given(mockEntry.getQuantity()).willReturn(Long.valueOf(1));

		final CommerceCartModification modification = defaultSABMUpdateOrderTemplateEntryStrategy.removeEntry(mockCart, mockEntry);
		Assert.assertEquals(CommerceCartModificationStatus.SUCCESS, modification.getStatusCode());
		Assert.assertEquals(0, modification.getQuantity());
		Assert.assertEquals(-1, modification.getQuantityAdded());
	}
}
