/**
 *
 */
package com.sabmiller.core.deals.services;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

import de.hybris.platform.b2b.model.B2BUnitModel;
import de.hybris.platform.core.model.order.AbstractOrderEntryModel;
import de.hybris.platform.core.model.order.CartModel;
import de.hybris.platform.core.model.product.UnitModel;
import com.sabmiller.core.deals.services.DealsService;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.HashSet;
import java.util.List;
import java.util.Map;

import org.junit.Before;
import org.junit.Test;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.MockitoAnnotations;

import com.sabm.core.config.SabmConfigurationService;
import com.sabmiller.core.deals.services.DefaultDealConditionService.DealQualificationStatus;
import com.sabmiller.core.deals.services.response.ConflictGroup;
import com.sabmiller.core.deals.services.response.DealQualificationResponse;
import com.sabmiller.core.deals.services.response.PartialDealQualificationResponse;
import com.sabmiller.core.enums.DealTypeEnum;
import com.sabmiller.core.model.AbstractDealConditionModel;
import com.sabmiller.core.model.CartDealConditionModel;
import com.sabmiller.core.model.ComplexDealConditionModel;
import com.sabmiller.core.model.DealConditionGroupModel;
import com.sabmiller.core.model.DealModel;
import com.sabmiller.core.model.ProductDealConditionModel;
import com.sabmiller.core.model.SABMAlcoholProductModel;
import com.sabmiller.core.model.SABMAlcoholVariantProductEANModel;
import com.sabmiller.core.model.SABMAlcoholVariantProductMaterialModel;
import com.sabmiller.core.product.SabmProductService;
import org.mockito.InjectMocks;
import static junit.framework.Assert.assertFalse;

/**
 * @author joshua.a.antony
 *
 */
public class DealConditionServiceTest
{

	@InjectMocks
	private DefaultDealConditionService dealConditionService = new DefaultDealConditionService();

	@Mock
	private SabmProductService productService;

	@Mock
	private SabmConfigurationService sabmConfigurationService;

	@Mock
	protected DealsService dealsService;

	private SABMAlcoholVariantProductMaterialModel mockP1, mockP2, mockP3, mockP4, mockP6, mockP66, mockP7, mockP77, mockP8,
			mockP88, mockP9, mockP99, mockP999;


	private static final String UOM_CASE = "CAS";

	@Before
	public void before()
	{
		MockitoAnnotations.initMocks(this);
		/*dealConditionService = new DefaultDealConditionService();
		dealConditionService.setProductService(productService);
		dealConditionService.setSabmConfigurationService(sabmConfigurationService);
		dealConditionService.setDealsService(dealsService);*/
		Mockito.when(sabmConfigurationService.getPartialDealThreshold()).thenReturn(50d);

		mockProducts();
	}

	@Test
	public void testFindQualifiedDeals()
	{
		final List<DealModel> deals = new ArrayList<DealModel>();
		deals.addAll(mockQualifiedDeals());
		deals.addAll(mockNonQualifiedDeals());

		final B2BUnitModel b2bUnitModel = Mockito.mock(B2BUnitModel.class);
		Mockito.when(b2bUnitModel.getComplexDeals()).thenReturn(new HashSet<>(deals));
		final DealQualificationResponse response = dealConditionService.findQualifiedDeals(b2bUnitModel, mockCart());
		assertEquals(0, response.getGreenDeals().size());
		assertEquals(0, response.getRedDeals().size());
		assertTrue(response.getAmberDeals().isEmpty());
		assertEquals(0, response.getConflictGroup().getTotalConflicts());
	}

	@Test
	public void testFindPartiallyQualifiedDeals()
	{
		final List<DealModel> deals = new ArrayList<DealModel>();
		deals.addAll(mockPartiallyQualifiedDeals());
		deals.addAll(mockPartiallyNonQualifiedDeals());

		final CartModel cart = mockCart();
		final PartialDealQualificationResponse response = dealConditionService.findPartiallyQualifiedDeals(deals, cart, false);

		assertEquals(0, response.getAllPartiallyQualifiedDeals().size());
		assertFalse(response.hasDeal("dp1"));
		assertFalse(response.hasDeal("dp2"));
		assertFalse(response.hasDeal("dp3"));
		assertFalse(response.hasDeal("dp4"));
		assertFalse(response.hasDeal("dp5"));
		assertFalse(response.hasDeal("dp6"));
		assertFalse(response.hasDeal("dp7"));
		assertFalse(response.hasDeal("dp11"));
	}

	/**
	 * Testing case when some deals have already been selected and exist in the cart.
	 */
	@Test
	public void testFindPartiallyQualifiedDealsWithNonEmptyComplexDealsInCart()
	{
		final List<DealModel> deals = new ArrayList<DealModel>();
		deals.addAll(mockPartiallyQualifiedDeals());
		deals.addAll(mockPartiallyNonQualifiedDeals());

		final CartModel cart = mockCart();

		final CartDealConditionModel cartDealConditionModel = Mockito.mock(CartDealConditionModel.class);
		Mockito.when(cartDealConditionModel.getDeal()).thenReturn(deals.get(1)); //dp2
		final List<CartDealConditionModel> list = new ArrayList<CartDealConditionModel>();
		list.add(cartDealConditionModel);

		Mockito.when(cart.getComplexDealConditions()).thenReturn(list);

		/** Since all the items related to dp2 are exlcuded, the only deal that qualifies is dp4 **/
		final PartialDealQualificationResponse response = dealConditionService.findPartiallyQualifiedDeals(deals, cart, false);

		assertEquals(0, response.getAllPartiallyQualifiedDeals().size());
		assertFalse(response.hasDeal("dp4"));
	}


	@Test
	public void testFindConflictingDeals()
	{
		final List<DealModel> deals = mockDeals();
		final CartModel cart = mockCart();
		final Map<DealModel, List<DealModel>> conflictDealsMap = dealConditionService.findConflictingDeals(deals, cart);
		final ConflictGroup conflictGroup = new ConflictGroup(conflictDealsMap);
		assertEquals(0, conflictGroup.getTotalConflicts());
	}


	@Test
	public void testCheckDealQualification()
	{
		final CartModel cart = mockCart();

		//Qualified Deals
		/*for (final DealModel deal : mockQualifiedDeals())
		{
			assertEquals(dealConditionService.checkDealQualification(deal, cart), DealQualificationStatus.QUALIFIED);
		}*/

		//Non Qualified Deals
		for (final DealModel deal : mockNonQualifiedDeals())
		{
			assertEquals(DealQualificationStatus.NOT_QUALIFIED, dealConditionService.checkDealQualification(deal, cart));
		}

	}

	private void mockProducts()
	{
		mockP1 = mockProduct("h1", "p1");
		mockP2 = mockProduct("h2", "p2");
		mockP3 = mockProduct("h3", "p3");
		mockP4 = mockProduct("h4", "p4");
		mockP6 = mockProduct("h11", "p6");
		mockP66 = mockProduct("h12", "p66");
		mockP7 = mockProduct("h13", "p7");
		mockP77 = mockProduct("h14", "p77");
		mockP8 = mockProduct("h15", "p8");
		mockP88 = mockProduct("h16", "p88");
		mockP9 = mockProduct("h9", "p9");
		mockP99 = mockProduct("h9", "p99");
		mockP999 = mockProduct("h9", "p999");
	}


	private AbstractOrderEntryModel mockCartEntry(final SABMAlcoholVariantProductMaterialModel product, final long quantity)
	{
		final AbstractOrderEntryModel cartEntry = Mockito.mock(AbstractOrderEntryModel.class);
		Mockito.when(cartEntry.getProduct()).thenReturn(product);
		Mockito.when(cartEntry.getQuantity()).thenReturn(quantity);

		return cartEntry;

	}

	private List<DealModel> mockDeals()
	{
		final List<DealModel> deals = new ArrayList<DealModel>();
		deals.add(mockDeal("d1", "h1", 10, "h11", 8, "p1", false, null));//deal 1 :Conflicts with deal 5				(1,5)
		deals.add(mockDeal("d2", "h2", 7, "h22", 3, "p2", false, null));//deal 2 : Conflicts with deal 6 and deal 7		(2,6,7)
		deals.add(mockDeal("d3", "h3", 5, "h33", 6, "p3", false, null));//deal 3 : Conflicts with deal 7						(3,7)
		deals.add(mockDeal("d4", "h4", 4, "h44", 3, "p4", false, null));//deal 4 : Conflicts with deal 6						(4,6)
		deals.add(mockDeal("d5", "h5", 10, "h1", 20, "p5", false, null));//deal 5 : Conflicts with deal 1 and 8			(1,5,8)
		deals.add(mockDeal("d6", "h4", 15, "h22", 20, null, false, null));//deal 6 : Conflicts with deal 2 and 4			(2,4,6)
		deals.add(mockDeal("d7", "h2", 20, "h3", 30, null, false, null));//deal 7 : Conflicts with deal 2 and 3			(2,3,7)

		deals.add(mockDeal("d8", "p1", 20, "p66", 10, false, null));//deal 8 : Conflicts with deal 5 as p1 belongs to h1  (5,8). Does not belong to deal 1 since p1 is excluded
		deals.add(mockDeal("d9", "p7", 5, "p77", 10, false, null));
		deals.add(mockDeal("d10", "p8", 8, "p88", 15, false, null));

		return deals;
	}



	private DealModel mockDeal(final String dealCode, final String product1, final int qty1, final String product2, final int qty2,
			final boolean multipleScales, final Integer[] scales)
	{
		final DealModel dealModel = Mockito.mock(DealModel.class);
		final DealConditionGroupModel conditionGroup = mockConditionGroup(product1, qty1, product2, qty2, multipleScales, scales);
		Mockito.when(dealModel.getConditionGroup()).thenReturn(conditionGroup);

		Mockito.when(dealModel.getDealType()).thenReturn(DealTypeEnum.COMPLEX);
		Mockito.when(dealModel.getCode()).thenReturn(dealCode);
		return dealModel;
	}


	private DealModel mockDeal(final String dealCode, final String hierarchy1, final int qty1, final String hierarchy2,
			final int qty2, final String excludeProduct, final boolean multipleScales, final Integer[] scales)
	{
		final DealModel dealModel = Mockito.mock(DealModel.class);
		final DealConditionGroupModel conditionGroup = mockConditionGroup(hierarchy1, qty1, hierarchy2, qty2, excludeProduct,
				multipleScales, scales);
		Mockito.when(dealModel.getConditionGroup()).thenReturn(conditionGroup);

		Mockito.when(dealModel.getDealType()).thenReturn(DealTypeEnum.COMPLEX);
		Mockito.when(dealModel.getCode()).thenReturn(dealCode);
		return dealModel;
	}

	private DealConditionGroupModel mockConditionGroup(final String product1, final int qty1, final String product2,
			final int qty2, final boolean multipleScales, final Integer[] scales)
	{
		final ProductDealConditionModel dc1 = mockProductDealCondition(product1, false, false, qty1, 1, 1);
		final ProductDealConditionModel dc2 = mockProductDealCondition(product2, false, false, qty2, 1, 1);

		final List<AbstractDealConditionModel> dealConditions = new ArrayList<AbstractDealConditionModel>();
		dealConditions.add(dc1);
		dealConditions.add(dc2);

		final DealConditionGroupModel dcg = Mockito.mock(DealConditionGroupModel.class);
		Mockito.when(dcg.getDealConditions()).thenReturn(dealConditions);
		Mockito.when(dcg.getMultipleScales()).thenReturn(multipleScales);
		Mockito.when(dcg.getScales()).thenReturn(scales != null ? Arrays.asList(scales) : Collections.emptyList());

		Mockito.when(dc1.getDealConditionGroup()).thenReturn(dcg);
		Mockito.when(dc2.getDealConditionGroup()).thenReturn(dcg);

		return dcg;
	}

	private DealConditionGroupModel mockConditionGroup(final String hierarchy1, final int qty1, final String hierarchy2,
			final int qty2, final String productCode, final boolean multipleScales, final Integer[] scales)
	{
		final ComplexDealConditionModel dc1 = mockComplexDealCondition(null, hierarchy1, null, false, true, qty1, 1);
		final ComplexDealConditionModel dc2 = mockComplexDealCondition(null, hierarchy2, null, false, true, qty2, 1);
		final ProductDealConditionModel dc3 = mockProductDealCondition(productCode, true, false, 1, 1, 1);

		final List<AbstractDealConditionModel> dealConditions = new ArrayList<AbstractDealConditionModel>();
		add(dealConditions, dc1);
		add(dealConditions, dc2);
		add(dealConditions, dc3);

		final DealConditionGroupModel dcg = Mockito.mock(DealConditionGroupModel.class);
		Mockito.when(dcg.getDealConditions()).thenReturn(dealConditions);
		Mockito.when(dcg.getMultipleScales()).thenReturn(multipleScales);
		Mockito.when(dcg.getScales()).thenReturn(scales != null ? Arrays.asList(scales) : Collections.emptyList());


		if (dc1 != null)
		{
			Mockito.when(dc1.getDealConditionGroup()).thenReturn(dcg);
		}
		if (dc2 != null)
		{
			Mockito.when(dc2.getDealConditionGroup()).thenReturn(dcg);
		}

		if (dc3 != null)
		{
			Mockito.when(dc3.getDealConditionGroup()).thenReturn(dcg);
		}

		return dcg;
	}

	private void add(final List l, final Object o)
	{
		if (o != null)
		{
			l.add(o);
		}
	}

	private ComplexDealConditionModel mockComplexDealCondition(final String line, final String brand, final String variety,
			final boolean exclude, final boolean mandatory, final int quantity, final int sequenceNumber)
	{
		if (line == null && brand == null && variety == null)
		{
			return null;
		}
		final UnitModel mockUnit = mockUnit();

		final ComplexDealConditionModel condition = Mockito.mock(ComplexDealConditionModel.class);

		Mockito.when(condition.getLine()).thenReturn(line);
		Mockito.when(condition.getBrand()).thenReturn(brand);
		Mockito.when(condition.getVariety()).thenReturn(variety);

		Mockito.when(condition.getExclude()).thenReturn(exclude);
		Mockito.when(condition.getMandatory()).thenReturn(mandatory);
		Mockito.when(condition.getQuantity()).thenReturn(quantity);
		Mockito.when(condition.getSequenceNumber()).thenReturn(sequenceNumber);
		Mockito.when(condition.getUnit()).thenReturn(mockUnit);

		return condition;
	}

	private ProductDealConditionModel mockProductDealCondition(final String productCode, final boolean exclude,
			final boolean mandatory, final int minQty, final int quantity, final int sequenceNumber)
	{
		if (productCode != null)
		{
			final UnitModel mockUnit = mockUnit();

			final ProductDealConditionModel condition = Mockito.mock(ProductDealConditionModel.class);
			Mockito.when(condition.getProductCode()).thenReturn(productCode);
			Mockito.when(condition.getExclude()).thenReturn(exclude);
			Mockito.when(condition.getMandatory()).thenReturn(mandatory);
			Mockito.when(condition.getMinQty()).thenReturn(minQty);
			Mockito.when(condition.getQuantity()).thenReturn(quantity);
			Mockito.when(condition.getSequenceNumber()).thenReturn(sequenceNumber);
			Mockito.when(condition.getUnit()).thenReturn(mockUnit);
			return condition;
		}
		return null;
	}

	private UnitModel mockUnit()
	{
		final UnitModel unit = Mockito.mock(UnitModel.class);
		Mockito.when(unit.getCode()).thenReturn(UOM_CASE);
		Mockito.when(unit.getName()).thenReturn(UOM_CASE);
		return unit;
	}


	private SABMAlcoholVariantProductMaterialModel mockProduct(final String hierarchy, final String productCode)
	{
		final SABMAlcoholProductModel p1Alcohol = Mockito.mock(SABMAlcoholProductModel.class);
		Mockito.when(p1Alcohol.getLevel2()).thenReturn(hierarchy);

		final SABMAlcoholVariantProductEANModel p1Ean = Mockito.mock(SABMAlcoholVariantProductEANModel.class);
		Mockito.when(p1Ean.getBaseProduct()).thenReturn(p1Alcohol);

		final SABMAlcoholVariantProductMaterialModel p1 = Mockito.mock(SABMAlcoholVariantProductMaterialModel.class);
		Mockito.when(p1.getBaseProduct()).thenReturn(p1Ean);
		Mockito.when(p1.getCode()).thenReturn(productCode);

		Mockito.when(productService.getProductForCode(productCode)).thenReturn(p1);

		return p1;
	}

	private CartModel mockCart()
	{
		final List<AbstractOrderEntryModel> cartEntries = new ArrayList<AbstractOrderEntryModel>();
		cartEntries.add(mockCartEntry(mockP1, 10));
		cartEntries.add(mockCartEntry(mockP2, 4));
		cartEntries.add(mockCartEntry(mockP3, 5));
		cartEntries.add(mockCartEntry(mockP4, 5));
		cartEntries.add(mockCartEntry(mockP6, 5));
		cartEntries.add(mockCartEntry(mockP66, 5));
		cartEntries.add(mockCartEntry(mockP7, 5));
		cartEntries.add(mockCartEntry(mockP77, 5));
		cartEntries.add(mockCartEntry(mockP8, 5));
		cartEntries.add(mockCartEntry(mockP88, 5));
		cartEntries.add(mockCartEntry(mockP9, 2));
		cartEntries.add(mockCartEntry(mockP99, 6));
		cartEntries.add(mockCartEntry(mockP999, 8));

		final CartModel cart = Mockito.mock(CartModel.class);
		Mockito.when(cart.getEntries()).thenReturn(cartEntries);
		Mockito.when(cart.getComplexDealConditions()).thenReturn(Collections.emptyList());

		return cart;
	}

	private List<DealModel> mockPartiallyQualifiedDeals()
	{
		final List<DealModel> deals = new ArrayList<DealModel>();
		deals.add(mockDeal("dp1", "h1", 18, null, 0, null, false, null)); //18 cases across h1   (d1,d2,d3,d5,d13,d14)
		deals.add(mockDeal("dp2", "h1", 12, "h2", 6, null, false, null)); //10 cases across h1 and 4 cases across h2  (d1,d2,d3,d5,d13,d14)
		deals.add(mockDeal("dp3", "h1", 10, "h2", 4, "p3", false, null)); //10 cases across h1 and 4 cases across h2  excluding p2 (d1,d2,d3,d5,d13,d14)
		deals.add(mockDeal("dp4", "h9", 30, null, 0, null, false, null)); //16 cases across h9 : p9/p99/p999 have total of 16 items (d4)
		deals.add(mockDeal("dp5", "p1", 15, "p2", 7, false, null)); //5 cases of p1 and 2 cases of p2 (d1,d2,d3,d5,d13,d14)

		deals.add(mockDeal("dp6", "h1", 1, "h2", 1, null, true, new Integer[]
		{ 28, 24 }));//12 cases across h1 and h2  (d1,d2,d3,d5,d13,d14)  P1+P2 = 14 which is >= 50% of 28 and hence should qualify

		deals.add(mockDeal("dp7", "p1", 1, "p2", 1, true, new Integer[]
		{ 27 }));//27 cases across p1 and p2 (d1,d2,d3,d13,d14).  P1+P2 = 14 which is > 50% of 27 and hence should qualify

		deals.add(mockDeal("dp11", "p1", 6, "p2", 5, true, new Integer[]
		{ 27 }));//27 cases across p1 and p2 with minimum of 6 in P1 and 5 in P2.  Cart has 10p1 and 4p2 (>50%) of required P2 (5)


		return deals;
	}

	private List<DealModel> mockPartiallyNonQualifiedDeals()
	{
		final List<DealModel> deals = new ArrayList<DealModel>();

		deals.add(mockDeal("dp8", "p1", 1, "p2", 1, true, new Integer[]
		{ 29 }));//29 cases across p1 and p2 (d1,d2,d3,d13,d14).  P1+P2 = 14 which is < 50% of 27 and hence should NOT qualify

		deals.add(mockDeal("dp9", "h1", 22, null, 0, null, false, null)); //22 cases across h1   (d1,d2,d3,d5,d13,d14). Total is 10 <22

		deals.add(mockDeal("dp10", "h1", 10, "h2", 4, "p2", false, null)); //10 cases across h1 and 4 cases across h2  excluding p2 (d1,d2,d3,d5,d13,d14)

		deals.add(mockDeal("dp11", "p1", 6, "p2", 9, true, new Integer[]
		{ 27 }));//27 cases across p1 and p2 with minimum of 6 in P1 and 9 in P2.  Cart has 10p1 and 4p2 (<50%) and hence not qualified



		return deals;
	}

	private List<DealModel> mockQualifiedDeals()
	{
		final List<DealModel> deals = new ArrayList<DealModel>();
		deals.add(mockDeal("d1", "h1", 10, null, 8, null, false, null)); //10 cases across h1   (d1,d2,d3,d5,d13,d14)
		deals.add(mockDeal("d2", "h1", 10, "h2", 4, null, false, null)); //10 cases across h1 and 4 cases across h2  (d1,d2,d3,d5,d13,d14)
		deals.add(mockDeal("d3", "h1", 10, "h2", 4, "p3", false, null)); //10 cases across h1 and 4 cases across h2  excluding p2 (d1,d2,d3,d5,d13,d14)
		deals.add(mockDeal("d4", "h9", 16, null, 0, null, false, null)); //16 cases across h9 : p9/p99/p999 have total of 16 items (d4)
		deals.add(mockDeal("d5", "p1", 5, "p2", 2, false, null)); //5 cases of p1 and 2 cases of p2 (d1,d2,d3,d5,d13,d14)

		deals.add(mockDeal("d13", "h1", 1, "h2", 1, null, true, new Integer[]
		{ 12, 24 }));//12 cases across h1 and h2  (d1,d2,d3,d5,d13,d14)

		deals.add(mockDeal("d14", "p1", 1, "p2", 1, true, new Integer[]
		{ 12 }));//12 cases across p1 and p2 (d1,d2,d3,d13,d14)

		return deals;
	}

	private List<DealModel> mockNonQualifiedDeals()
	{
		final List<DealModel> deals = new ArrayList<DealModel>();
		deals.add(mockDeal("d6", "h9", 17, null, 0, null, false, null)); //17 cases across h9 : p9/p99/p999 have total of 16 items
		deals.add(mockDeal("d7", "p1", 15, "p2", 2, false, null)); //15 cases of p1 and 2 cases of p2
		deals.add(mockDeal("d8", "h1", 12, null, 0, null, false, null)); //12 cases across h1
		deals.add(mockDeal("d9", "h2", 5, null, 8, null, false, null)); //5 cases across h2
		deals.add(mockDeal("d10", "h1", 10, null, 8, "p1", false, null)); //10 cases across h1 excluding p1
		deals.add(mockDeal("d11", "h1", 10, "h2", 4, "p1", false, null)); //10 cases across h1 and 4 cases across h2 excluding p1

		//Scale Deals
		deals.add(mockDeal("d12", "h1", 1, "h2", 1, "p1", true, new Integer[]
		{ 12, 24 }));//12 cases across h1 and h2 except p1

		return deals;
	}
}
