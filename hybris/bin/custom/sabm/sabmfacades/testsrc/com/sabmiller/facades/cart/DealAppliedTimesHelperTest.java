/**
 *
 */
package com.sabmiller.facades.cart;

import static org.junit.Assert.assertEquals;
import static org.mockito.Mockito.mock;

import de.hybris.bootstrap.annotations.UnitTest;
import de.hybris.platform.commercefacades.order.data.CartData;
import de.hybris.platform.commercefacades.order.data.OrderEntryData;
import de.hybris.platform.commercefacades.product.data.ProductData;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.junit.Before;
import org.junit.Test;
import org.mockito.MockitoAnnotations;

import com.sabmiller.facades.cart.impl.DealAppliedTimesHelper;
import com.sabmiller.facades.deal.data.DealBaseProductJson;
import com.sabmiller.facades.deal.data.DealFreeProductJson;
import com.sabmiller.facades.deal.data.DealJson;
import com.sabmiller.facades.deal.data.DealRangeJson;


/**
 * DealAppliedTimesHelperTest
 *
 * @author xiaowu.a.zhang
 * @date 29/06/2016
 *
 */
@UnitTest
public class DealAppliedTimesHelperTest
{
	private DealAppliedTimesHelper dealAppliedTimesHelper;

	@Before
	public void setUp()
	{
		dealAppliedTimesHelper = new DealAppliedTimesHelper();

		MockitoAnnotations.initMocks(this);
	}

	@Test
	public void testCheckDealAppliedTimes()
	{
		final DealJson dealJson = new DealJson();

		// build the condition
		final DealRangeJson dealRangeJson1 = new DealRangeJson();
		dealRangeJson1.setMinQty(0);
		final DealBaseProductJson baseProductJson1 = new DealBaseProductJson();
		baseProductJson1.setQty(3);
		baseProductJson1.setProductCode("productCode1");

		final DealBaseProductJson baseProductJson2 = new DealBaseProductJson();
		baseProductJson2.setQty(4);
		baseProductJson2.setProductCode("productCode2");

		final List<DealBaseProductJson> baseProductJsons1 = new ArrayList<>();
		baseProductJsons1.add(baseProductJson1);
		baseProductJsons1.add(baseProductJson2);
		dealRangeJson1.setBaseProducts(baseProductJsons1);

		final DealRangeJson dealRangeJson2 = new DealRangeJson();
		dealRangeJson2.setMinQty(5);
		final DealBaseProductJson baseProductJson3 = new DealBaseProductJson();
		baseProductJson3.setProductCode("productCode3");
		final DealBaseProductJson baseProductJson4 = new DealBaseProductJson();
		baseProductJson4.setProductCode("productCode4");

		final List<DealBaseProductJson> baseProductJsons2 = new ArrayList<>();
		baseProductJsons2.add(baseProductJson1);
		baseProductJsons2.add(baseProductJson2);
		dealRangeJson2.setBaseProducts(baseProductJsons2);

		final List<DealRangeJson> ranges = new ArrayList<>();
		ranges.add(dealRangeJson1);
		ranges.add(dealRangeJson2);
		dealJson.setRanges(ranges);

		// build the benefit
		final DealFreeProductJson freeProductJson1 = new DealFreeProductJson();
		freeProductJson1.setCode("code1");
		freeProductJson1.setProportionalFreeGood(true);
		final Map<Integer, Integer> qtyMap1 = new HashMap<>();
		qtyMap1.put(0, 1);
		freeProductJson1.setQty(qtyMap1);

		final DealFreeProductJson freeProductJson2 = new DealFreeProductJson();
		freeProductJson2.setCode("code1");
		freeProductJson2.setProportionalFreeGood(true);
		final Map<Integer, Integer> qtyMap2 = new HashMap<>();
		qtyMap2.put(0, 2);
		freeProductJson2.setQty(qtyMap2);

		final List<DealFreeProductJson> freeProductJsons = new ArrayList<>();
		freeProductJsons.add(freeProductJson1);
		freeProductJsons.add(freeProductJson2);
		dealJson.setSelectableProducts(freeProductJsons);

		final CartData cartData = mock(CartData.class);

		final OrderEntryData entryData1 = new OrderEntryData();
		entryData1.setBaseQuantity(Long.valueOf(6));
		final ProductData productData1 = new ProductData();
		productData1.setCode("productCode1");
		entryData1.setProduct(productData1);

		final OrderEntryData entryData2 = new OrderEntryData();
		entryData2.setBaseQuantity(Long.valueOf(9));
		final ProductData productData2 = new ProductData();
		productData2.setCode("productCode2");
		entryData2.setProduct(productData2);

		final OrderEntryData entryData3 = new OrderEntryData();
		entryData3.setBaseQuantity(Long.valueOf(10));
		final ProductData productData3 = new ProductData();
		productData3.setCode("productCode3");
		entryData3.setProduct(productData3);

		final OrderEntryData entryData4 = new OrderEntryData();
		entryData4.setBaseQuantity(Long.valueOf(6));
		final ProductData productData4 = new ProductData();
		productData4.setCode("productCode4");
		entryData4.setProduct(productData4);

		final List<OrderEntryData> entries = new ArrayList<>();
		entries.add(entryData1);
		entries.add(entryData2);
		entries.add(entryData3);
		entries.add(entryData4);


		dealAppliedTimesHelper.checkDealAppliedTimes(dealJson, entries);
		;

		assertEquals(Integer.valueOf(2), dealJson.getSelectableProducts().get(0).getQty().get(0));
	}

}
