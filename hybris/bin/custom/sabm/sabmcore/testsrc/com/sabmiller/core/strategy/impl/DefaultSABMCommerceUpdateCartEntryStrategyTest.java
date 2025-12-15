/**
 *
 */
package com.sabmiller.core.strategy.impl;

import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.when;

import de.hybris.platform.commerceservices.order.CommerceCartCalculationStrategy;
import de.hybris.platform.commerceservices.order.CommerceCartModification;
import de.hybris.platform.commerceservices.order.hook.CommerceUpdateCartEntryHook;
import de.hybris.platform.core.model.order.AbstractOrderEntryModel;
import de.hybris.platform.core.model.order.CartEntryModel;
import de.hybris.platform.core.model.order.CartModel;
import de.hybris.platform.servicelayer.config.ConfigurationService;
import de.hybris.platform.servicelayer.model.ModelService;

import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
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
 * The Unit test of DefaultSABMCommerceUpdateCartEntryStrategy
 *
 * @author xiaowu.a.zhang
 * @date 05/19/2016
 *
 */
public class DefaultSABMCommerceUpdateCartEntryStrategyTest
{
	@Mock
	private List<CommerceUpdateCartEntryHook> commerceUpdateCartEntryHooks;
	@Mock
	private ConfigurationService configurationService;
	@Mock
	private ModelService modelService;
	@Mock
	private CommerceCartCalculationStrategy commerceCartCalculationStrategy;
	@Mock
	private AsahiSiteUtil asahiSiteUtil;
	@Mock(name = "cartService")
	private SABMCartService sabmCartService;
	@InjectMocks
	private final DefaultSABMCommerceUpdateCartEntryStrategy defaultSABMCommerceUpdateCartEntryStrategy = new DefaultSABMCommerceUpdateCartEntryStrategy();

	@Before
	public void setUp()
	{
		MockitoAnnotations.initMocks(this);
		/*defaultSABMCommerceUpdateCartEntryStrategy = new DefaultSABMCommerceUpdateCartEntryStrategy();
		defaultSABMCommerceUpdateCartEntryStrategy.setCommerceUpdateCartEntryHooks(commerceUpdateCartEntryHooks);
		defaultSABMCommerceUpdateCartEntryStrategy.setConfigurationService(configurationService);
		defaultSABMCommerceUpdateCartEntryStrategy.setModelService(modelService);
		defaultSABMCommerceUpdateCartEntryStrategy.setCommerceCartCalculationStrategy(commerceCartCalculationStrategy);*/

	}

	@Test
	public void testmModifyEntry()
	{
		// test the sense of delete entry have the free product
		given(asahiSiteUtil.isCub()).willReturn(true);
		final CartModel cartModel = Mockito.mock(CartModel.class);
		final AbstractOrderEntryModel entry1 = new AbstractOrderEntryModel();
		entry1.setEntryNumber(1);
		final AbstractOrderEntryModel basicEntry = new CartEntryModel();
		final SABMAlcoholVariantProductMaterialModel productModel = Mockito.mock(SABMAlcoholVariantProductMaterialModel.class);
		final Map<String, Object> maxOrderQuantityMap = new HashMap<String, Object>();
		maxOrderQuantityMap.put(SabmCoreConstants.FINAL_MAX_ORDER_QTY, 3);
		maxOrderQuantityMap.put(SabmCoreConstants.TOTAL_ORDERED_QTY,2);
		maxOrderQuantityMap.put(SabmCoreConstants.FINAL_MAX_ORDER_QTY, 3);
		basicEntry.setProduct(productModel);
		basicEntry.setQuantity(Long.valueOf(0));
		basicEntry.setEntryNumber(3);
		basicEntry.setFreeGoodEntryNumber("5");
		basicEntry.setEntryGroupNumbers(new HashSet<Integer>(2));
		final AbstractOrderEntryModel freeEntry = new AbstractOrderEntryModel();
		freeEntry.setEntryNumber(5);
		freeEntry.setQuantity(Long.valueOf(1));
		final AbstractOrderEntryModel entry2 = new AbstractOrderEntryModel();
		entry2.setEntryNumber(7);

		final List<AbstractOrderEntryModel> entries = new ArrayList<AbstractOrderEntryModel>();
		entries.add(entry1);
		entries.add(basicEntry);
		entries.add(freeEntry);
		entries.add(entry2);

		//cartModel.setEntries(entries);
		final SABMAlcoholVariantProductEANModel alcoholVariantProductEANModel = Mockito.mock(SABMAlcoholVariantProductEANModel.class);
		when(productModel.getBaseProduct()).thenReturn(alcoholVariantProductEANModel);
		when(cartModel.getRequestedDeliveryDate()).thenReturn(new Date());
		when(sabmCartService.getFinalMaxOrderQty(productModel, new Date())).thenReturn(maxOrderQuantityMap);
		final CommerceCartModification commerceCartModification = defaultSABMCommerceUpdateCartEntryStrategy.modifyEntry(cartModel,
				basicEntry, 0, 0, 0);

		Assert.assertEquals(Integer.valueOf(1), entry1.getEntryNumber());
		Assert.assertEquals(productModel, commerceCartModification.getEntry().getProduct());

		// test the delete entry didn't have free entry
		final CartModel cartModel1 = new CartModel();
		final AbstractOrderEntryModel entry3 = new AbstractOrderEntryModel();
		entry3.setEntryNumber(1);
		final SABMAlcoholVariantProductMaterialModel productModel1 = Mockito.mock(SABMAlcoholVariantProductMaterialModel.class);
		entry3.setProduct(productModel1);
		entry3.setQuantity(Long.valueOf(0));
		entry3.setEntryGroupNumbers(new HashSet<Integer>(2));
		final List<AbstractOrderEntryModel> entries1 = new ArrayList<AbstractOrderEntryModel>();
		entries1.add(entry3);
		cartModel1.setEntries(entries1);

		final CommerceCartModification commerceCartModification1 = defaultSABMCommerceUpdateCartEntryStrategy
				.modifyEntry(cartModel1, entry3, 0, 0, 0);

		Assert.assertEquals(Integer.valueOf(0), entry3.getEntryNumber());
		Assert.assertEquals(productModel1, commerceCartModification1.getEntry().getProduct());

	}
}
