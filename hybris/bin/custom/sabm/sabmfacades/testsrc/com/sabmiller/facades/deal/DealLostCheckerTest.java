/**
 *
 */
package com.sabmiller.facades.deal;

import static org.mockito.BDDMockito.given;

import de.hybris.platform.core.model.order.AbstractOrderEntryModel;
import de.hybris.platform.core.model.order.CartEntryModel;
import de.hybris.platform.core.model.order.CartModel;

import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.List;

import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.MockitoAnnotations;

import com.sabmiller.core.deals.dao.DealsDao;
import com.sabmiller.core.deals.services.AbstractLostDealChecker;
import com.sabmiller.core.deals.services.DealsService;
import com.sabmiller.core.deals.services.DealsServiceImpl;
import com.sabmiller.core.deals.services.DifferentBenefitsMultipleScalesChecker;
import com.sabmiller.core.deals.services.LimitedDealChecker;
import com.sabmiller.core.deals.services.OneOrMoreProductHierarchyPerHierarchyChecker;
import com.sabmiller.core.deals.services.OneOrMoreProductHierarchyWithExceptionsChecker;
import com.sabmiller.core.deals.services.OneOrMoreSKUsPerItemChecker;
import com.sabmiller.core.deals.services.TwoOrMoreHierarchyAcrossWithExceptionsChecker;
import com.sabmiller.core.deals.services.TwoOrMoreProductHierarchyAcrossChecker;
import com.sabmiller.core.deals.services.TwoOrMoreSKUsAcrossChecker;
import com.sabmiller.core.deals.services.TwoOrMoreSKUsAcrossMinimumQtyPerSKUChecker;
import com.sabmiller.core.enums.DealConditionStatus;
import com.sabmiller.core.model.AbstractDealBenefitModel;
import com.sabmiller.core.model.AbstractDealConditionModel;
import com.sabmiller.core.model.CartDealConditionModel;
import com.sabmiller.core.model.ComplexDealConditionModel;
import com.sabmiller.core.model.DealConditionGroupModel;
import com.sabmiller.core.model.DealModel;
import com.sabmiller.core.model.DealScaleModel;
import com.sabmiller.core.model.FreeGoodsDealBenefitModel;
import com.sabmiller.core.model.ProductDealConditionModel;
import com.sabmiller.core.model.SABMAlcoholVariantProductMaterialModel;
import com.sabmiller.core.product.SabmProductService;


/**
 * The Class DealLostCheckerTest.
 */
public class DealLostCheckerTest
{

	/** The checker list. */
	List<AbstractLostDealChecker> checkerList = new ArrayList<>();

	/** The product service. */
	@Mock
	private SabmProductService productService;

	@Mock
	private DealsService dealsService;

	@InjectMocks
	private OneOrMoreProductHierarchyPerHierarchyChecker oneOrMoreProductHierarchyPerHierarchyChecker;
	@InjectMocks
	private OneOrMoreProductHierarchyWithExceptionsChecker oneOrMoreProductHierarchyWithExceptionsChecker;
	@InjectMocks
	private TwoOrMoreHierarchyAcrossWithExceptionsChecker twoOrMoreHierarchyAcrossWithExceptionsChecker;
	@InjectMocks
	private TwoOrMoreProductHierarchyAcrossChecker twoOrMoreProductHierarchyAcrossChecker;
	@InjectMocks
	private DifferentBenefitsMultipleScalesChecker differentBenefitsMultipleScalesChecker;
	@InjectMocks
	private OneOrMoreSKUsPerItemChecker oneOrMoreSKUsPerItemChecker;
	@InjectMocks
	private TwoOrMoreSKUsAcrossChecker twoOrMoreSKUsAcrossChecker;
	@InjectMocks
	private TwoOrMoreSKUsAcrossMinimumQtyPerSKUChecker twoOrMoreSKUsAcrossMinimumQtyPerSKUChecker;
	@InjectMocks
	private LimitedDealChecker limitedDealChecker;

	/**
	 * Inits the.
	 */
	@Before
	public void setUp()
	{
		MockitoAnnotations.initMocks(this);
		checkerList.add(differentBenefitsMultipleScalesChecker);
		checkerList.add(oneOrMoreProductHierarchyPerHierarchyChecker);
		checkerList.add(oneOrMoreProductHierarchyWithExceptionsChecker);
		checkerList.add(oneOrMoreSKUsPerItemChecker);
		checkerList.add(twoOrMoreHierarchyAcrossWithExceptionsChecker);
		checkerList.add(twoOrMoreProductHierarchyAcrossChecker);
		checkerList.add(twoOrMoreSKUsAcrossChecker);
		checkerList.add(twoOrMoreSKUsAcrossMinimumQtyPerSKUChecker);
		checkerList.add(limitedDealChecker);

	}

	/**
	 * Test deal001.
	 */
	@Test
	public void testDeal001()
	{
		//create deal
		final DealModel deal001_01 = createDeal001(true);
		final AbstractLostDealChecker checker = findChecker(deal001_01);

		Assert.assertNotNull(checker);
		//Assert.assertTrue(checker instanceof OneOrMoreSKUsPerItemChecker);
		Assert.assertTrue(checker.isThisDealType(deal001_01));

		//create cart
		final CartModel cart = createCart(4L, 5L, 8L, 8L, 8L);
		Assert.assertTrue(checker.isLostDeal(deal001_01, cart.getEntries().get(0), 3, cart));
		Assert.assertTrue(checker.isDeleteDeal(deal001_01, cart.getEntries().get(0), 3, cart));

		final DealModel deal001_02 = createDeal001(false);
		Assert.assertTrue(checker.isThisDealType(deal001_02));
		Assert.assertTrue(checker.isLostDeal(deal001_02, cart.getEntries().get(0), 3, cart));
		Assert.assertTrue(checker.isDeleteDeal(deal001_02, cart.getEntries().get(0), 3, cart));

	}

	/**
	 * Test deal002.
	 */
	@Test
	public void testDeal002()
	{
		//create deal
		final DealModel deal002_01 = createDeal002(true);
		AbstractLostDealChecker checker = findChecker(deal002_01);

		Assert.assertNotNull(checker);
		Assert.assertTrue(checker instanceof TwoOrMoreSKUsAcrossChecker);
		Assert.assertTrue(checker.isThisDealType(deal002_01));
		final CartModel cart1 = createCart(5L, 5L, 8L, 8L, 8L);
		//Assert.assertTrue(checker.isLostDeal(deal002_01, cart1.getEntries().get(0), 4, cart1));
		Assert.assertFalse(checker.isDeleteDeal(deal002_01, cart1.getEntries().get(0), 4, cart1));
		final CartModel cart2 = createCart(5L, 15L, 8L, 8L, 8L);
		Assert.assertFalse(checker.isLostDeal(deal002_01, cart2.getEntries().get(0), 4, cart2));

		final CartModel cart3 = createCart(6L, 15L, 8L, 8L, 8L);
		Assert.assertFalse(checker.isLostDeal(deal002_01, cart3.getEntries().get(0), 5, cart3));

		final DealModel deal002_02 = createDeal002(false);

		checker = findChecker(deal002_02);
		final CartModel cart4 = createCart(5L, 5L, 8L, 8L, 8L);
		Assert.assertFalse(checker.isLostDeal(deal002_02, cart4.getEntries().get(0), 4, cart4));

		final CartModel cart5 = createCart(5L, 15L, 8L, 8L, 8L);
		Assert.assertFalse(checker.isLostDeal(deal002_02, cart5.getEntries().get(0), 4, cart5));

		final CartModel cart6 = createCart(6L, 15L, 8L, 8L, 8L);
		Assert.assertFalse(checker.isLostDeal(deal002_02, cart6.getEntries().get(0), 5, cart6));
		Assert.assertFalse(checker.isDeleteDeal(deal002_02, cart6.getEntries().get(0), 5, cart6));

	}

	/**
	 * Test deal003.
	 */
	@Test
	public void testDeal003()
	{
		//create deal
		final DealModel deal003_01 = createDeal003(true);
		AbstractLostDealChecker checker = findChecker(deal003_01);
		Assert.assertNotNull(checker);
		Assert.assertTrue(checker instanceof OneOrMoreProductHierarchyPerHierarchyChecker);

		final CartModel cart = createCart(2L, 2L, 8L, 8L, 8L);
		List<SABMAlcoholVariantProductMaterialModel> brand1 = new ArrayList<SABMAlcoholVariantProductMaterialModel>();
		List<SABMAlcoholVariantProductMaterialModel> brand2 = new ArrayList<SABMAlcoholVariantProductMaterialModel>();
		brand1.add((SABMAlcoholVariantProductMaterialModel) cart.getEntries().get(0).getProduct());
		brand1.add((SABMAlcoholVariantProductMaterialModel) cart.getEntries().get(1).getProduct());
		brand2.add((SABMAlcoholVariantProductMaterialModel) cart.getEntries().get(2).getProduct());
		given(productService.getProductByLevel2("product1product2")).willReturn(brand1);
		given(productService.getProductByLevel2("product3product4")).willReturn(brand2);

		Assert.assertFalse(checker.isLostDeal(deal003_01, cart.getEntries().get(0), 1, cart));

		final CartModel cart1 = createCart(4L, 4L, 8L, 8L, 8L);
		brand1 = new ArrayList<SABMAlcoholVariantProductMaterialModel>();
		brand2 = new ArrayList<SABMAlcoholVariantProductMaterialModel>();
		brand1.add((SABMAlcoholVariantProductMaterialModel) cart1.getEntries().get(0).getProduct());
		brand1.add((SABMAlcoholVariantProductMaterialModel) cart1.getEntries().get(1).getProduct());
		brand2.add((SABMAlcoholVariantProductMaterialModel) cart1.getEntries().get(2).getProduct());
		brand2.add((SABMAlcoholVariantProductMaterialModel) cart1.getEntries().get(3).getProduct());
		given(productService.getProductByLevel2("product1product2")).willReturn(brand1);
		given(productService.getProductByLevel2("product3product4")).willReturn(brand2);

		Assert.assertFalse(checker.isLostDeal(deal003_01, cart1.getEntries().get(0), 3, cart1));

		final CartModel cart2 = createCart(4L, 4L, 2L, 3L, 8L);
		brand1 = new ArrayList<SABMAlcoholVariantProductMaterialModel>();
		brand2 = new ArrayList<SABMAlcoholVariantProductMaterialModel>();
		brand1.add((SABMAlcoholVariantProductMaterialModel) cart2.getEntries().get(0).getProduct());
		brand1.add((SABMAlcoholVariantProductMaterialModel) cart2.getEntries().get(1).getProduct());
		brand2.add((SABMAlcoholVariantProductMaterialModel) cart2.getEntries().get(2).getProduct());
		brand2.add((SABMAlcoholVariantProductMaterialModel) cart2.getEntries().get(3).getProduct());
		given(productService.getProductByLevel2("product1product2")).willReturn(brand1);
		given(productService.getProductByLevel2("product3product4")).willReturn(brand2);

		Assert.assertFalse(checker.isLostDeal(deal003_01, cart2.getEntries().get(0), 3, cart2));
		Assert.assertFalse(checker.isDeleteDeal(deal003_01, cart2.getEntries().get(0), 3, cart2));


		final DealModel deal003_02 = createDeal003(false);
		checker = findChecker(deal003_02);
		Assert.assertNotNull(checker);
		Assert.assertTrue(checker instanceof OneOrMoreProductHierarchyPerHierarchyChecker);

		final CartModel cart3 = createCart(4L, 4L, 2L, 3L, 8L);
		brand1 = new ArrayList<SABMAlcoholVariantProductMaterialModel>();
		brand2 = new ArrayList<SABMAlcoholVariantProductMaterialModel>();
		brand1.add((SABMAlcoholVariantProductMaterialModel) cart3.getEntries().get(0).getProduct());
		brand1.add((SABMAlcoholVariantProductMaterialModel) cart3.getEntries().get(1).getProduct());
		brand2.add((SABMAlcoholVariantProductMaterialModel) cart3.getEntries().get(2).getProduct());
		brand2.add((SABMAlcoholVariantProductMaterialModel) cart3.getEntries().get(3).getProduct());
		given(productService.getProductByLevel2("product1product2")).willReturn(brand1);
		given(productService.getProductByLevel2("product3product4")).willReturn(brand2);

		Assert.assertFalse(checker.isLostDeal(deal003_02, cart3.getEntries().get(0), 3, cart3));

		final CartModel cart4 = createCart(4L, 4L, 4L, 6L, 8L);
		brand1 = new ArrayList<SABMAlcoholVariantProductMaterialModel>();
		brand2 = new ArrayList<SABMAlcoholVariantProductMaterialModel>();
		brand1.add((SABMAlcoholVariantProductMaterialModel) cart4.getEntries().get(0).getProduct());
		brand1.add((SABMAlcoholVariantProductMaterialModel) cart4.getEntries().get(1).getProduct());
		brand2.add((SABMAlcoholVariantProductMaterialModel) cart4.getEntries().get(2).getProduct());
		brand2.add((SABMAlcoholVariantProductMaterialModel) cart4.getEntries().get(3).getProduct());
		given(productService.getProductByLevel2("product1product2")).willReturn(brand1);
		given(productService.getProductByLevel2("product3product4")).willReturn(brand2);

		Assert.assertFalse(checker.isLostDeal(deal003_02, cart4.getEntries().get(0), 3, cart4));

		final CartModel cart5 = createCart(2L, 2L, 4L, 6L, 8L);
		brand1 = new ArrayList<SABMAlcoholVariantProductMaterialModel>();
		brand2 = new ArrayList<SABMAlcoholVariantProductMaterialModel>();
		brand1.add((SABMAlcoholVariantProductMaterialModel) cart5.getEntries().get(0).getProduct());
		brand1.add((SABMAlcoholVariantProductMaterialModel) cart5.getEntries().get(1).getProduct());
		brand2.add((SABMAlcoholVariantProductMaterialModel) cart5.getEntries().get(2).getProduct());
		brand2.add((SABMAlcoholVariantProductMaterialModel) cart5.getEntries().get(3).getProduct());
		given(productService.getProductByLevel2("product1product2")).willReturn(brand1);
		given(productService.getProductByLevel2("product3product4")).willReturn(brand2);

		Assert.assertFalse(checker.isLostDeal(deal003_02, cart5.getEntries().get(0), 1, cart5));


	}

	/**
	 * Test deal004.
	 */
	@Test
	public void testDeal004()
	{
		//create deal
		final DealModel deal004_01 = createDeal004(true);
		AbstractLostDealChecker checker = findChecker(deal004_01);
		Assert.assertNotNull(checker);
		Assert.assertTrue(checker instanceof TwoOrMoreProductHierarchyAcrossChecker);

		final CartModel cart1 = createCart(2L, 2L, 1L, 5L, 8L);
		List<SABMAlcoholVariantProductMaterialModel> brand1 = new ArrayList<SABMAlcoholVariantProductMaterialModel>();
		List<SABMAlcoholVariantProductMaterialModel> brand2 = new ArrayList<SABMAlcoholVariantProductMaterialModel>();
		brand1.add((SABMAlcoholVariantProductMaterialModel) cart1.getEntries().get(0).getProduct());
		brand1.add((SABMAlcoholVariantProductMaterialModel) cart1.getEntries().get(1).getProduct());
		brand2.add((SABMAlcoholVariantProductMaterialModel) cart1.getEntries().get(2).getProduct());
		brand2.add((SABMAlcoholVariantProductMaterialModel) cart1.getEntries().get(3).getProduct());
		given(productService.getProductByLevel2("product1product2")).willReturn(brand1);
		given(productService.getProductByLevel2("product3product4")).willReturn(brand2);

		Assert.assertFalse(checker.isLostDeal(deal004_01, cart1.getEntries().get(0), 1, cart1));
		Assert.assertFalse(checker.isDeleteDeal(deal004_01, cart1.getEntries().get(0), 1, cart1));

		final CartModel cart2 = createCart(2L, 2L, 2L, 5L, 8L);
		brand1 = new ArrayList<SABMAlcoholVariantProductMaterialModel>();
		brand2 = new ArrayList<SABMAlcoholVariantProductMaterialModel>();
		brand1.add((SABMAlcoholVariantProductMaterialModel) cart2.getEntries().get(0).getProduct());
		brand1.add((SABMAlcoholVariantProductMaterialModel) cart2.getEntries().get(1).getProduct());
		brand2.add((SABMAlcoholVariantProductMaterialModel) cart2.getEntries().get(2).getProduct());
		brand2.add((SABMAlcoholVariantProductMaterialModel) cart2.getEntries().get(3).getProduct());
		given(productService.getProductByLevel2("product1product2")).willReturn(brand1);
		given(productService.getProductByLevel2("product3product4")).willReturn(brand2);

		Assert.assertFalse(checker.isLostDeal(deal004_01, cart2.getEntries().get(0), 1, cart2));
		Assert.assertFalse(checker.isDeleteDeal(deal004_01, cart2.getEntries().get(0), 1, cart2));

		final CartModel cart3 = createCart(2L, 2L, 2L, 14L, 8L);
		brand1 = new ArrayList<SABMAlcoholVariantProductMaterialModel>();
		brand2 = new ArrayList<SABMAlcoholVariantProductMaterialModel>();
		brand1.add((SABMAlcoholVariantProductMaterialModel) cart3.getEntries().get(0).getProduct());
		brand1.add((SABMAlcoholVariantProductMaterialModel) cart3.getEntries().get(1).getProduct());
		brand2.add((SABMAlcoholVariantProductMaterialModel) cart3.getEntries().get(2).getProduct());
		brand2.add((SABMAlcoholVariantProductMaterialModel) cart3.getEntries().get(3).getProduct());
		given(productService.getProductByLevel2("product1product2")).willReturn(brand1);
		given(productService.getProductByLevel2("product3product4")).willReturn(brand2);

		Assert.assertFalse(checker.isLostDeal(deal004_01, cart3.getEntries().get(0), 1, cart3));

		final CartModel cart4 = createCart(2L, 5L, 6L, 14L, 8L);
		brand1 = new ArrayList<SABMAlcoholVariantProductMaterialModel>();
		brand2 = new ArrayList<SABMAlcoholVariantProductMaterialModel>();
		brand1.add((SABMAlcoholVariantProductMaterialModel) cart4.getEntries().get(0).getProduct());
		brand1.add((SABMAlcoholVariantProductMaterialModel) cart4.getEntries().get(1).getProduct());
		brand2.add((SABMAlcoholVariantProductMaterialModel) cart4.getEntries().get(2).getProduct());
		brand2.add((SABMAlcoholVariantProductMaterialModel) cart4.getEntries().get(3).getProduct());
		given(productService.getProductByLevel2("product1product2")).willReturn(brand1);
		given(productService.getProductByLevel2("product3product4")).willReturn(brand2);

		Assert.assertFalse(checker.isLostDeal(deal004_01, cart4.getEntries().get(0), 1, cart4));


		//create deal
		final DealModel deal004_02 = createDeal004(false);
		checker = findChecker(deal004_02);
		Assert.assertNotNull(checker);
		Assert.assertTrue(checker instanceof TwoOrMoreProductHierarchyAcrossChecker);

		final CartModel cart5 = createCart(2L, 2L, 1L, 5L, 8L);
		brand1 = new ArrayList<SABMAlcoholVariantProductMaterialModel>();
		brand2 = new ArrayList<SABMAlcoholVariantProductMaterialModel>();
		brand1.add((SABMAlcoholVariantProductMaterialModel) cart5.getEntries().get(0).getProduct());
		brand1.add((SABMAlcoholVariantProductMaterialModel) cart5.getEntries().get(1).getProduct());
		brand2.add((SABMAlcoholVariantProductMaterialModel) cart5.getEntries().get(2).getProduct());
		brand2.add((SABMAlcoholVariantProductMaterialModel) cart5.getEntries().get(3).getProduct());
		given(productService.getProductByLevel2("product1product2")).willReturn(brand1);
		given(productService.getProductByLevel2("product3product4")).willReturn(brand2);

		Assert.assertFalse(checker.isLostDeal(deal004_02, cart5.getEntries().get(0), 1, cart5));


		final CartModel cart6 = createCart(2L, 2L, 1L, 15L, 8L);
		brand1 = new ArrayList<SABMAlcoholVariantProductMaterialModel>();
		brand2 = new ArrayList<SABMAlcoholVariantProductMaterialModel>();
		brand1.add((SABMAlcoholVariantProductMaterialModel) cart6.getEntries().get(0).getProduct());
		brand1.add((SABMAlcoholVariantProductMaterialModel) cart6.getEntries().get(1).getProduct());
		brand2.add((SABMAlcoholVariantProductMaterialModel) cart6.getEntries().get(2).getProduct());
		brand2.add((SABMAlcoholVariantProductMaterialModel) cart6.getEntries().get(3).getProduct());
		given(productService.getProductByLevel2("product1product2")).willReturn(brand1);
		given(productService.getProductByLevel2("product3product4")).willReturn(brand2);

		Assert.assertFalse(checker.isLostDeal(deal004_02, cart6.getEntries().get(0), 1, cart6));

	}

	/**
	 * Test deal005.
	 */
	@Test
	public void testDeal005()
	{
		//create deal
		final DealModel deal005_01 = createDeal005(true);
		AbstractLostDealChecker checker = findChecker(deal005_01);
		Assert.assertNotNull(checker);
		Assert.assertTrue(checker instanceof OneOrMoreProductHierarchyWithExceptionsChecker);

		final CartModel cart1 = createCart(3L, 1L, 1L, 5L, 8L);
		List<SABMAlcoholVariantProductMaterialModel> brand1 = new ArrayList<SABMAlcoholVariantProductMaterialModel>();
		List<SABMAlcoholVariantProductMaterialModel> brand2 = new ArrayList<SABMAlcoholVariantProductMaterialModel>();
		brand1.add((SABMAlcoholVariantProductMaterialModel) cart1.getEntries().get(0).getProduct());
		brand1.add((SABMAlcoholVariantProductMaterialModel) cart1.getEntries().get(1).getProduct());
		brand1.add((SABMAlcoholVariantProductMaterialModel) cart1.getEntries().get(4).getProduct());
		brand2.add((SABMAlcoholVariantProductMaterialModel) cart1.getEntries().get(2).getProduct());
		brand2.add((SABMAlcoholVariantProductMaterialModel) cart1.getEntries().get(3).getProduct());
		given(productService.getProductByLevel2("product1product2")).willReturn(brand1);
		given(productService.getProductByLevel2("product3product4")).willReturn(brand2);

		Assert.assertTrue(checker.isLostDeal(deal005_01, cart1.getEntries().get(0), 2, cart1));
		Assert.assertTrue(checker.isDeleteDeal(deal005_01, cart1.getEntries().get(0), 2, cart1));

		final CartModel cart2 = createCart(7L, 1L, 5L, 5L, 8L);
		brand1 = new ArrayList<SABMAlcoholVariantProductMaterialModel>();
		brand2 = new ArrayList<SABMAlcoholVariantProductMaterialModel>();
		brand1.add((SABMAlcoholVariantProductMaterialModel) cart2.getEntries().get(0).getProduct());
		brand1.add((SABMAlcoholVariantProductMaterialModel) cart2.getEntries().get(1).getProduct());
		brand1.add((SABMAlcoholVariantProductMaterialModel) cart2.getEntries().get(4).getProduct());
		brand2.add((SABMAlcoholVariantProductMaterialModel) cart2.getEntries().get(2).getProduct());
		brand2.add((SABMAlcoholVariantProductMaterialModel) cart2.getEntries().get(3).getProduct());
		given(productService.getProductByLevel2("product1product2")).willReturn(brand1);
		given(productService.getProductByLevel2("product3product4")).willReturn(brand2);

		Assert.assertTrue(checker.isLostDeal(deal005_01, cart2.getEntries().get(0), 6, cart2));

		final CartModel cart3 = createCart(9L, 1L, 2L, 2L, 8L);
		brand1 = new ArrayList<SABMAlcoholVariantProductMaterialModel>();
		brand2 = new ArrayList<SABMAlcoholVariantProductMaterialModel>();
		brand1.add((SABMAlcoholVariantProductMaterialModel) cart3.getEntries().get(0).getProduct());
		brand1.add((SABMAlcoholVariantProductMaterialModel) cart3.getEntries().get(1).getProduct());
		brand1.add((SABMAlcoholVariantProductMaterialModel) cart3.getEntries().get(4).getProduct());
		brand2.add((SABMAlcoholVariantProductMaterialModel) cart3.getEntries().get(2).getProduct());
		brand2.add((SABMAlcoholVariantProductMaterialModel) cart3.getEntries().get(3).getProduct());
		given(productService.getProductByLevel2("product1product2")).willReturn(brand1);
		given(productService.getProductByLevel2("product3product4")).willReturn(brand2);

		Assert.assertFalse(checker.isLostDeal(deal005_01, cart3.getEntries().get(0), 8, cart3));
		Assert.assertFalse(checker.isDeleteDeal(deal005_01, cart3.getEntries().get(0), 8, cart3));

		//create deal
		final DealModel deal005_02 = createDeal005(false);
		checker = findChecker(deal005_02);
		Assert.assertNotNull(checker);
		Assert.assertTrue(checker instanceof OneOrMoreProductHierarchyWithExceptionsChecker);

		final CartModel cart4 = createCart(8L, 2L, 5L, 5L, 8L);
		brand1 = new ArrayList<SABMAlcoholVariantProductMaterialModel>();
		brand2 = new ArrayList<SABMAlcoholVariantProductMaterialModel>();
		brand1.add((SABMAlcoholVariantProductMaterialModel) cart4.getEntries().get(0).getProduct());
		brand1.add((SABMAlcoholVariantProductMaterialModel) cart4.getEntries().get(1).getProduct());
		brand1.add((SABMAlcoholVariantProductMaterialModel) cart4.getEntries().get(4).getProduct());
		brand2.add((SABMAlcoholVariantProductMaterialModel) cart4.getEntries().get(2).getProduct());
		brand2.add((SABMAlcoholVariantProductMaterialModel) cart4.getEntries().get(3).getProduct());
		given(productService.getProductByLevel2("product1product2")).willReturn(brand1);
		given(productService.getProductByLevel2("product3product4")).willReturn(brand2);

		Assert.assertFalse(checker.isLostDeal(deal005_02, cart4.getEntries().get(0), 7, cart4));

		final CartModel cart5 = createCart(3L, 1L, 5L, 5L, 8L);
		brand1 = new ArrayList<SABMAlcoholVariantProductMaterialModel>();
		brand2 = new ArrayList<SABMAlcoholVariantProductMaterialModel>();
		brand1.add((SABMAlcoholVariantProductMaterialModel) cart5.getEntries().get(0).getProduct());
		brand1.add((SABMAlcoholVariantProductMaterialModel) cart5.getEntries().get(1).getProduct());
		brand1.add((SABMAlcoholVariantProductMaterialModel) cart5.getEntries().get(4).getProduct());
		brand2.add((SABMAlcoholVariantProductMaterialModel) cart5.getEntries().get(2).getProduct());
		brand2.add((SABMAlcoholVariantProductMaterialModel) cart5.getEntries().get(3).getProduct());
		given(productService.getProductByLevel2("product1product2")).willReturn(brand1);
		given(productService.getProductByLevel2("product3product4")).willReturn(brand2);

		Assert.assertTrue(checker.isLostDeal(deal005_02, cart5.getEntries().get(0), 2, cart5));
	}

	/**
	 * Test deal006.
	 */
	@Test
	public void testDeal006()
	{
		//create deal
		final DealModel deal006_01 = createDeal006(true);
		AbstractLostDealChecker checker = findChecker(deal006_01);
		Assert.assertNotNull(checker);
		Assert.assertTrue(checker instanceof TwoOrMoreHierarchyAcrossWithExceptionsChecker);

		final CartModel cart1 = createCart(4L, 2L, 1L, 5L, 8L);
		List<SABMAlcoholVariantProductMaterialModel> brand1 = new ArrayList<SABMAlcoholVariantProductMaterialModel>();
		List<SABMAlcoholVariantProductMaterialModel> brand2 = new ArrayList<SABMAlcoholVariantProductMaterialModel>();
		brand1.add((SABMAlcoholVariantProductMaterialModel) cart1.getEntries().get(0).getProduct());
		brand1.add((SABMAlcoholVariantProductMaterialModel) cart1.getEntries().get(1).getProduct());
		brand2.add((SABMAlcoholVariantProductMaterialModel) cart1.getEntries().get(2).getProduct());
		brand2.add((SABMAlcoholVariantProductMaterialModel) cart1.getEntries().get(3).getProduct());
		given(productService.getProductByLevel2("product1product2")).willReturn(brand1);
		given(productService.getProductByLevel2("product3product4")).willReturn(brand2);

		Assert.assertTrue(checker.isLostDeal(deal006_01, cart1.getEntries().get(0), 3, cart1));
		Assert.assertTrue(checker.isDeleteDeal(deal006_01, cart1.getEntries().get(0), 3, cart1));

		final CartModel cart2 = createCart(9L, 2L, 6L, 5L, 8L);
		brand1 = new ArrayList<SABMAlcoholVariantProductMaterialModel>();
		brand2 = new ArrayList<SABMAlcoholVariantProductMaterialModel>();
		brand1.add((SABMAlcoholVariantProductMaterialModel) cart2.getEntries().get(0).getProduct());
		brand1.add((SABMAlcoholVariantProductMaterialModel) cart2.getEntries().get(1).getProduct());
		brand2.add((SABMAlcoholVariantProductMaterialModel) cart2.getEntries().get(2).getProduct());
		brand2.add((SABMAlcoholVariantProductMaterialModel) cart2.getEntries().get(3).getProduct());
		given(productService.getProductByLevel2("product1product2")).willReturn(brand1);
		given(productService.getProductByLevel2("product3product4")).willReturn(brand2);

		Assert.assertTrue(checker.isLostDeal(deal006_01, cart2.getEntries().get(0), 8, cart2));

		final CartModel cart3 = createCart(9L, 2L, 7L, 5L, 8L);
		brand1 = new ArrayList<SABMAlcoholVariantProductMaterialModel>();
		brand2 = new ArrayList<SABMAlcoholVariantProductMaterialModel>();
		brand1.add((SABMAlcoholVariantProductMaterialModel) cart3.getEntries().get(0).getProduct());
		brand1.add((SABMAlcoholVariantProductMaterialModel) cart3.getEntries().get(1).getProduct());
		brand2.add((SABMAlcoholVariantProductMaterialModel) cart3.getEntries().get(2).getProduct());
		brand2.add((SABMAlcoholVariantProductMaterialModel) cart3.getEntries().get(3).getProduct());
		given(productService.getProductByLevel2("product1product2")).willReturn(brand1);
		given(productService.getProductByLevel2("product3product4")).willReturn(brand2);

		Assert.assertFalse(checker.isLostDeal(deal006_01, cart3.getEntries().get(0), 8, cart3));
		Assert.assertFalse(checker.isDeleteDeal(deal006_01, cart3.getEntries().get(0), 8, cart3));

		//create deal
		final DealModel deal006_02 = createDeal006(false);
		checker = findChecker(deal006_02);
		Assert.assertNotNull(checker);
		Assert.assertTrue(checker instanceof TwoOrMoreHierarchyAcrossWithExceptionsChecker);

		final CartModel cart4 = createCart(8L, 2L, 7L, 5L, 8L);
		brand1 = new ArrayList<SABMAlcoholVariantProductMaterialModel>();
		brand2 = new ArrayList<SABMAlcoholVariantProductMaterialModel>();
		brand1.add((SABMAlcoholVariantProductMaterialModel) cart4.getEntries().get(0).getProduct());
		brand1.add((SABMAlcoholVariantProductMaterialModel) cart4.getEntries().get(1).getProduct());
		brand2.add((SABMAlcoholVariantProductMaterialModel) cart4.getEntries().get(2).getProduct());
		brand2.add((SABMAlcoholVariantProductMaterialModel) cart4.getEntries().get(3).getProduct());
		given(productService.getProductByLevel2("product1product2")).willReturn(brand1);
		given(productService.getProductByLevel2("product3product4")).willReturn(brand2);

		Assert.assertFalse(checker.isLostDeal(deal006_02, cart4.getEntries().get(0), 8, cart4));

		final CartModel cart5 = createCart(4L, 2L, 3L, 3L, 8L);
		brand1 = new ArrayList<SABMAlcoholVariantProductMaterialModel>();
		brand2 = new ArrayList<SABMAlcoholVariantProductMaterialModel>();
		brand1.add((SABMAlcoholVariantProductMaterialModel) cart5.getEntries().get(0).getProduct());
		brand1.add((SABMAlcoholVariantProductMaterialModel) cart5.getEntries().get(1).getProduct());
		brand2.add((SABMAlcoholVariantProductMaterialModel) cart5.getEntries().get(2).getProduct());
		brand2.add((SABMAlcoholVariantProductMaterialModel) cart5.getEntries().get(3).getProduct());
		given(productService.getProductByLevel2("product1product2")).willReturn(brand1);
		given(productService.getProductByLevel2("product3product4")).willReturn(brand2);

		Assert.assertTrue(checker.isLostDeal(deal006_02, cart5.getEntries().get(0), 3, cart5));

	}

	/**
	 * Test deal007.
	 */
	@Test
	public void testDeal007()
	{
		//create deal
		final DealModel deal007_01 = createDeal007(true);
		AbstractLostDealChecker checker = findChecker(deal007_01);
		Assert.assertNotNull(checker);
		Assert.assertTrue(checker instanceof TwoOrMoreSKUsAcrossMinimumQtyPerSKUChecker);

		final CartModel cart1 = createCart(5L, 5L, 8L, 8L, 8L);
		Assert.assertTrue(checker.isLostDeal(deal007_01, cart1.getEntries().get(0), 4, cart1));
		Assert.assertTrue(checker.isDeleteDeal(deal007_01, cart1.getEntries().get(0), 4, cart1));

		final CartModel cart2 = createCart(4L, 7L, 8L, 8L, 8L);
		Assert.assertTrue(checker.isLostDeal(deal007_01, cart2.getEntries().get(0), 3, cart2));

		final CartModel cart3 = createCart(13L, 7L, 8L, 8L, 8L);
		Assert.assertTrue(checker.isLostDeal(deal007_01, cart3.getEntries().get(0), 12, cart3));

		final CartModel cart4 = createCart(4L, 17L, 8L, 8L, 8L);
		Assert.assertTrue(checker.isLostDeal(deal007_01, cart4.getEntries().get(0), 3, cart4));

		final CartModel cart5 = createCart(5L, 17L, 8L, 8L, 8L);
		Assert.assertFalse(checker.isLostDeal(deal007_01, cart5.getEntries().get(0), 4, cart5));
		Assert.assertFalse(checker.isDeleteDeal(deal007_01, cart5.getEntries().get(0), 4, cart5));

		final DealModel deal007_02 = createDeal007(false);
		checker = findChecker(deal007_02);
		Assert.assertNotNull(checker);
		Assert.assertTrue(checker instanceof TwoOrMoreSKUsAcrossMinimumQtyPerSKUChecker);
		final CartModel cart6 = createCart(13L, 7L, 8L, 8L, 8L);
		Assert.assertFalse(checker.isLostDeal(deal007_02, cart6.getEntries().get(0), 12, cart6));

		final CartModel cart7 = createCart(4L, 17L, 8L, 8L, 8L);
		Assert.assertTrue(checker.isLostDeal(deal007_02, cart7.getEntries().get(0), 3, cart7));

		final CartModel cart8 = createCart(5L, 5L, 8L, 8L, 8L);
		Assert.assertTrue(checker.isLostDeal(deal007_02, cart8.getEntries().get(0), 4, cart8));
	}

	/**
	 * Test deal008.
	 */
	@Test
	public void testDeal008()
	{
		//create deal
		final DealModel deal008_01 = createDeal008(true);
		final AbstractLostDealChecker checker = findChecker(deal008_01);
		//Assert.assertTrue(checker.isThisDealType(deal008_01));
		Assert.assertNotNull(checker);
		Assert.assertTrue(checker instanceof DifferentBenefitsMultipleScalesChecker);

		final CartModel cart1 = createCart(10L, 17L, 8L, 8L, 8L);
		Assert.assertTrue(checker.isLostDeal(deal008_01, cart1.getEntries().get(0), 9, cart1));
		Assert.assertTrue(checker.isDeleteDeal(deal008_01, cart1.getEntries().get(0), 9, cart1));

		final CartModel cart2 = createCart(11L, 17L, 8L, 8L, 8L);
		Assert.assertFalse(checker.isLostDeal(deal008_01, cart2.getEntries().get(0), 10, cart2));
		Assert.assertFalse(checker.isDeleteDeal(deal008_01, cart2.getEntries().get(0), 10, cart2));

		final CartModel cart3 = createCart(30L, 17L, 8L, 8L, 8L);
		Assert.assertTrue(checker.isLostDeal(deal008_01, cart3.getEntries().get(0), 29, cart3));

		final CartModel cart4 = createCart(31L, 17L, 8L, 8L, 8L);
		Assert.assertFalse(checker.isLostDeal(deal008_01, cart4.getEntries().get(0), 30, cart4));
	}

	/**
	 * Test deal009.
	 */
	@Test
	public void testDeal009()
	{
		//create deal
		final DealModel deal009_01 = createDeal009(true);
		final AbstractLostDealChecker checker = findChecker(deal009_01);
		Assert.assertTrue(checker instanceof LimitedDealChecker);
		final CartModel cart1 = createCart(10L, 17L, 8L, 8L, 8L);
		Assert.assertTrue(checker.isLostDeal(deal009_01, cart1.getEntries().get(0), 9, cart1));

		final CartModel cart2 = createCart(20L, 17L, 8L, 8L, 8L);
		Assert.assertTrue(checker.isLostDeal(deal009_01, cart2.getEntries().get(0), 19, cart1));

		final CartModel cart3 = createCart(21L, 17L, 8L, 8L, 8L);
		Assert.assertFalse(checker.isLostDeal(deal009_01, cart3.getEntries().get(0), 20, cart1));

		final CartModel cart4 = createCart(100L, 17L, 8L, 8L, 8L);
		Assert.assertTrue(checker.isLostDeal(deal009_01, cart4.getEntries().get(0), 99, cart1));

		final CartModel cart5 = createCart(110L, 17L, 8L, 8L, 8L);
		Assert.assertFalse(checker.isLostDeal(deal009_01, cart5.getEntries().get(0), 109, cart1));

		final CartModel cart6 = createCart(110L, 17L, 8L, 8L, 8L);
		Assert.assertFalse(checker.isLostDeal(deal009_01, cart6.getEntries().get(1), 109, cart1));
	}

	/**
	 * Creates the deal009.
	 *
	 * @param proportion
	 *           the proportion
	 * @return the deal model
	 */
	private DealModel createDeal009(final boolean proportion)
	{
		final DealModel deal = new DealModel();
		deal.setMaxConditionBaseValue((double) 100);
		final DealConditionGroupModel conditionGroup = new DealConditionGroupModel();
		final ProductDealConditionModel productCondtion1 = new ProductDealConditionModel();
		final FreeGoodsDealBenefitModel benefit = new FreeGoodsDealBenefitModel();
		benefit.setProportionalFreeGood(proportion);
		productCondtion1.setProductCode("product1");
		productCondtion1.setQuantity(10);
		final List<AbstractDealConditionModel> conditionList = new ArrayList<AbstractDealConditionModel>();
		final List<AbstractDealBenefitModel> benefitList = new ArrayList<AbstractDealBenefitModel>();
		conditionList.add(productCondtion1);
		benefitList.add(benefit);
		conditionGroup.setDealBenefits(benefitList);
		conditionGroup.setDealConditions(conditionList);
		conditionGroup.setMultipleScales(false);
		final List<Integer> scales = new ArrayList<Integer>();
		conditionGroup.setScales(scales);
		deal.setConditionGroup(conditionGroup);
		deal.setCode("deal");
		return deal;
	}

	/**
	 * Test deals service impl.
	 *
	 * @throws Exception
	 *            the exception
	 */
	@Test
	public void testDealsServiceImpl() throws Exception
	{
		final DealsServiceImpl service = new DealsServiceImpl();
		service.setLostDealCheckList(checkerList);
		final CartModel cart1 = createCartForService(8L, 17L, 8L, 10L, 8L);
		final DealsDao dao = Mockito.mock(DealsDao.class);
		final Field field = DealsServiceImpl.class.getDeclaredField("dealsDao");
		field.setAccessible(true);
		field.set(service, dao);

		//given(dao.getDeal("deal")).willReturn(deal008_01);

		//final List<DealModel> deal = service.getLostDeal(cart1, "0", 7);
		//Assert.assertEquals(2, deal.size());
	}

	/**
	 * Creates the cart for service.
	 *
	 * @param l
	 *           the l
	 * @param m
	 *           the m
	 * @param n
	 *           the n
	 * @param o
	 *           the o
	 * @param p
	 *           the p
	 * @return the cart model
	 */
	private CartModel createCartForService(final long l, final long m, final long n, final long o, final long p)
	{
		final CartModel cart = createCart(l, m, n, o, p);

		final DealModel deal001 = new DealModel();
		final DealConditionGroupModel conditionGroup = new DealConditionGroupModel();
		final ProductDealConditionModel productCondtion1 = new ProductDealConditionModel();
		final ProductDealConditionModel productCondtion2 = new ProductDealConditionModel();
		final FreeGoodsDealBenefitModel benefit = new FreeGoodsDealBenefitModel();
		benefit.setProportionalFreeGood(true);
		productCondtion1.setProductCode("product1");
		productCondtion1.setMinQty(4);
		productCondtion2.setProductCode("product2");
		productCondtion2.setMinQty(5);
		final List<AbstractDealConditionModel> conditionList = new ArrayList<AbstractDealConditionModel>();
		final List<AbstractDealBenefitModel> benefitList = new ArrayList<AbstractDealBenefitModel>();
		conditionList.add(productCondtion1);
		conditionList.add(productCondtion2);
		benefitList.add(benefit);
		conditionGroup.setDealBenefits(benefitList);
		conditionGroup.setDealConditions(conditionList);
		conditionGroup.setMultipleScales(false);
		final List<Integer> scales = new ArrayList<Integer>();
		conditionGroup.setScales(scales);
		deal001.setConditionGroup(conditionGroup);
		deal001.setCode("deal001");

		final DealModel deal002 = new DealModel();
		final DealConditionGroupModel conditionGroup002 = new DealConditionGroupModel();
		final ProductDealConditionModel productCondtion1002 = new ProductDealConditionModel();
		final ProductDealConditionModel productCondtion2002 = new ProductDealConditionModel();
		final FreeGoodsDealBenefitModel benefit002 = new FreeGoodsDealBenefitModel();
		benefit002.setProportionalFreeGood(true);
		productCondtion1002.setProductCode("product1");
		productCondtion1002.setMinQty(4);
		productCondtion2002.setProductCode("product4");
		productCondtion2002.setMinQty(5);
		final List<AbstractDealConditionModel> conditionList002 = new ArrayList<AbstractDealConditionModel>();
		final List<AbstractDealBenefitModel> benefitList002 = new ArrayList<AbstractDealBenefitModel>();
		conditionList002.add(productCondtion1002);
		conditionList002.add(productCondtion2002);
		benefitList002.add(benefit002);
		conditionGroup002.setDealBenefits(benefitList002);
		conditionGroup002.setDealConditions(conditionList002);
		conditionGroup002.setMultipleScales(false);
		final List<Integer> scales002 = new ArrayList<Integer>();
		conditionGroup002.setScales(scales002);
		deal002.setConditionGroup(conditionGroup002);
		deal002.setCode("deal002");
		final List<CartDealConditionModel> cartDealConditionList = new ArrayList<CartDealConditionModel>();
		final CartDealConditionModel cartDealCondition1 = new CartDealConditionModel();
		final CartDealConditionModel cartDealCondition2 = new CartDealConditionModel();
		cartDealCondition1.setStatus(DealConditionStatus.MANUAL);
		cartDealCondition2.setStatus(DealConditionStatus.MANUAL);
		cartDealCondition1.setDeal(deal001);
		cartDealCondition2.setDeal(deal002);
		cartDealConditionList.add(cartDealCondition1);
		cartDealConditionList.add(cartDealCondition2);
		cart.setComplexDealConditions(cartDealConditionList);
		return cart;
	}

	/**
	 * Creates the deal008.
	 *
	 * @param proportion
	 *           the proportion
	 * @return the deal model
	 */
	private DealModel createDeal008(final boolean proportion)
	{
		final DealModel deal = new DealModel();
		final DealConditionGroupModel conditionGroup = new DealConditionGroupModel();
		final ProductDealConditionModel productCondtion1 = new ProductDealConditionModel();

		final FreeGoodsDealBenefitModel benefit1 = new FreeGoodsDealBenefitModel();
		final FreeGoodsDealBenefitModel benefit2 = new FreeGoodsDealBenefitModel();
		benefit1.setProportionalFreeGood(proportion);
		benefit1.setScale("001");
		benefit2.setScale("002");
		benefit2.setProportionalFreeGood(proportion);
		productCondtion1.setProductCode("product1");
		//productCondtion1.setMinQty(4);

		final List<DealScaleModel> dealScales = new ArrayList<DealScaleModel>();
		final DealScaleModel scale1 = new DealScaleModel();
		final DealScaleModel scale2 = new DealScaleModel();
		scale1.setScale("001");
		scale2.setScale("002");
		scale1.setFrom(10);
		scale1.setTo(29);
		scale2.setFrom(30);
		scale2.setTo(9999);
		dealScales.add(scale1);
		dealScales.add(scale2);
		conditionGroup.setDealScales(dealScales);
		final List<AbstractDealConditionModel> conditionList = new ArrayList<AbstractDealConditionModel>();
		final List<AbstractDealBenefitModel> benefitList = new ArrayList<AbstractDealBenefitModel>();
		conditionList.add(productCondtion1);
		benefitList.add(benefit1);
		benefitList.add(benefit2);
		conditionGroup.setDealBenefits(benefitList);
		conditionGroup.setDealConditions(conditionList);
		conditionGroup.setMultipleScales(false);
		final List<Integer> scales = new ArrayList<Integer>();
		scales.add(10);
		scales.add(30);
		conditionGroup.setScales(scales);
		deal.setConditionGroup(conditionGroup);
		deal.setCode("deal");
		return deal;
	}

	/**
	 * Creates the deal007.
	 *
	 * @param proportion
	 *           the proportion
	 * @return the deal model
	 */
	private DealModel createDeal007(final boolean proportion)
	{
		final DealModel deal = new DealModel();
		final DealConditionGroupModel conditionGroup = new DealConditionGroupModel();
		final ProductDealConditionModel productCondtion1 = new ProductDealConditionModel();
		final ProductDealConditionModel productCondtion2 = new ProductDealConditionModel();
		final FreeGoodsDealBenefitModel benefit = new FreeGoodsDealBenefitModel();
		benefit.setProportionalFreeGood(proportion);
		productCondtion1.setProductCode("product1");
		productCondtion1.setMinQty(4);
		productCondtion2.setProductCode("product2");
		productCondtion2.setMinQty(5);
		final List<AbstractDealConditionModel> conditionList = new ArrayList<AbstractDealConditionModel>();
		final List<AbstractDealBenefitModel> benefitList = new ArrayList<AbstractDealBenefitModel>();
		conditionList.add(productCondtion1);
		conditionList.add(productCondtion2);
		benefitList.add(benefit);
		conditionGroup.setDealBenefits(benefitList);
		conditionGroup.setDealConditions(conditionList);
		conditionGroup.setMultipleScales(true);
		final List<Integer> scales = new ArrayList<Integer>();
		scales.add(10);
		conditionGroup.setScales(scales);
		final List<DealScaleModel> dealScales = new ArrayList<DealScaleModel>();
		final DealScaleModel scale1 = new DealScaleModel();
		final DealScaleModel scale2 = new DealScaleModel();
		scale1.setScale("001");
		scale2.setScale("002");
		scale1.setFrom(10);
		scale1.setTo(29);
		scale2.setFrom(30);
		scale2.setTo(9999);
		dealScales.add(scale1);
		dealScales.add(scale2);
		conditionGroup.setDealScales(dealScales);
		deal.setConditionGroup(conditionGroup);
		deal.setCode("deal");
		return deal;
	}

	/**
	 * Creates the deal006.
	 *
	 * @param proportion
	 *           the proportion
	 * @return the deal model
	 */
	private DealModel createDeal006(final boolean proportion)
	{
		final DealModel deal = new DealModel();
		final DealConditionGroupModel conditionGroup = new DealConditionGroupModel();
		final ComplexDealConditionModel complexCondition1 = new ComplexDealConditionModel();
		final ComplexDealConditionModel complexCondition2 = new ComplexDealConditionModel();
		final ProductDealConditionModel productcondition = new ProductDealConditionModel();
		final FreeGoodsDealBenefitModel benefit = new FreeGoodsDealBenefitModel();
		benefit.setProportionalFreeGood(proportion);
		productcondition.setProductCode("product2");
		complexCondition1.setBrand("product1product2");
		//complexCondition1.setQuantity(4);
		complexCondition2.setBrand("product3product4");
		//complexCondition2.setQuantity(5);
		final List<AbstractDealConditionModel> conditionList = new ArrayList<AbstractDealConditionModel>();
		final List<AbstractDealBenefitModel> benefitList = new ArrayList<AbstractDealBenefitModel>();
		conditionList.add(complexCondition1);
		conditionList.add(complexCondition2);
		conditionList.add(productcondition);
		benefitList.add(benefit);
		conditionGroup.setDealBenefits(benefitList);
		conditionGroup.setDealConditions(conditionList);
		conditionGroup.setMultipleScales(true);
		final DealScaleModel scale = new DealScaleModel();
		scale.setFrom(2);
		final List<DealScaleModel> scales = new ArrayList<DealScaleModel>();
		scales.add(scale);
		conditionGroup.setDealScales(scales);
		deal.setConditionGroup(conditionGroup);
		deal.setCode("deal");
		return deal;
	}

	/**
	 * Creates the deal005.
	 *
	 * @param proportion
	 *           the proportion
	 * @return the deal model
	 */
	private DealModel createDeal005(final boolean proportion)
	{
		final DealModel deal = new DealModel();
		final DealConditionGroupModel conditionGroup = new DealConditionGroupModel();
		final ComplexDealConditionModel complexCondition1 = new ComplexDealConditionModel();
		final ComplexDealConditionModel complexCondition2 = new ComplexDealConditionModel();
		final ProductDealConditionModel productcondition = new ProductDealConditionModel();
		final FreeGoodsDealBenefitModel benefit = new FreeGoodsDealBenefitModel();
		benefit.setProportionalFreeGood(proportion);
		productcondition.setProductCode("product5");
		complexCondition1.setBrand("product1product2");
		complexCondition1.setQuantity(4);
		complexCondition2.setBrand("product3product4");
		complexCondition2.setQuantity(5);
		final List<AbstractDealConditionModel> conditionList = new ArrayList<AbstractDealConditionModel>();
		final List<AbstractDealBenefitModel> benefitList = new ArrayList<AbstractDealBenefitModel>();
		conditionList.add(complexCondition1);
		conditionList.add(complexCondition2);
		conditionList.add(productcondition);
		benefitList.add(benefit);
		conditionGroup.setDealBenefits(benefitList);
		conditionGroup.setDealConditions(conditionList);
		conditionGroup.setMultipleScales(false);
		final DealScaleModel dealScale = new DealScaleModel();
		dealScale.setFrom(2);
		final List<DealScaleModel> dealScales = new ArrayList<DealScaleModel>();
		dealScales.add(dealScale);
		conditionGroup.setDealScales(dealScales);
		final List<Integer> scales = new ArrayList<Integer>();
		//scales.add(10);
		conditionGroup.setScales(scales);
		deal.setConditionGroup(conditionGroup);
		deal.setCode("deal");
		return deal;
	}

	/**
	 * Creates the deal004.
	 *
	 * @param proportion
	 *           the proportion
	 * @return the deal model
	 */
	private DealModel createDeal004(final boolean proportion)
	{
		final DealModel deal = new DealModel();
		final DealConditionGroupModel conditionGroup = new DealConditionGroupModel();
		final ComplexDealConditionModel complexCondition1 = new ComplexDealConditionModel();
		final ComplexDealConditionModel complexCondition2 = new ComplexDealConditionModel();
		final FreeGoodsDealBenefitModel benefit = new FreeGoodsDealBenefitModel();
		benefit.setProportionalFreeGood(proportion);
		benefit.setScale("001");
		complexCondition1.setBrand("product1product2");
		//complexCondition1.setQuantity(4);
		complexCondition2.setBrand("product3product4");
		//complexCondition2.setQuantity(5);
		final List<AbstractDealConditionModel> conditionList = new ArrayList<AbstractDealConditionModel>();
		final List<AbstractDealBenefitModel> benefitList = new ArrayList<AbstractDealBenefitModel>();
		conditionList.add(complexCondition1);
		conditionList.add(complexCondition2);
		benefitList.add(benefit);
		conditionGroup.setDealBenefits(benefitList);
		conditionGroup.setDealConditions(conditionList);
		conditionGroup.setMultipleScales(true);
		final List<Integer> scales = new ArrayList<Integer>();
		scales.add(10);
		final DealScaleModel dealScale = new DealScaleModel();
		dealScale.setScale("001");
		dealScale.setFrom(10);
		dealScale.setTo(9999);
		final List<DealScaleModel> DealScales = new ArrayList<DealScaleModel>();
		DealScales.add(dealScale);
		conditionGroup.setDealScales(DealScales);
		conditionGroup.setScales(scales);
		deal.setConditionGroup(conditionGroup);
		deal.setCode("deal");
		return deal;
	}

	/**
	 * Creates the deal003.
	 *
	 * @param proportion
	 *           the proportion
	 * @return the deal model
	 */
	private DealModel createDeal003(final boolean proportion)
	{
		final DealModel deal = new DealModel();
		final DealConditionGroupModel conditionGroup = new DealConditionGroupModel();
		final ComplexDealConditionModel complexCondition1 = new ComplexDealConditionModel();
		final ComplexDealConditionModel complexCondition2 = new ComplexDealConditionModel();
		final FreeGoodsDealBenefitModel benefit = new FreeGoodsDealBenefitModel();
		benefit.setProportionalFreeGood(proportion);
		complexCondition1.setBrand("product1product2");
		complexCondition1.setQuantity(4);
		complexCondition2.setBrand("product3product4");
		complexCondition2.setQuantity(5);
		final List<AbstractDealConditionModel> conditionList = new ArrayList<AbstractDealConditionModel>();
		final List<AbstractDealBenefitModel> benefitList = new ArrayList<AbstractDealBenefitModel>();
		conditionList.add(complexCondition1);
		conditionList.add(complexCondition2);
		benefitList.add(benefit);
		conditionGroup.setDealBenefits(benefitList);
		conditionGroup.setDealConditions(conditionList);
		conditionGroup.setMultipleScales(false);
		final List<Integer> scales = new ArrayList<Integer>();
		//scales.add(10);
		conditionGroup.setScales(scales);
		final DealScaleModel dealScale = new DealScaleModel();
		dealScale.setFrom(2);
		final List<DealScaleModel> dealScales = new ArrayList<DealScaleModel>();
		dealScales.add(dealScale);
		conditionGroup.setDealScales(dealScales);
		deal.setConditionGroup(conditionGroup);
		deal.setCode("deal");
		return deal;
	}

	/**
	 * Creates the deal002.
	 *
	 * @param proportion
	 *           the proportion
	 * @return the deal model
	 */
	private DealModel createDeal002(final boolean proportion)
	{
		final DealModel deal = new DealModel();
		final DealConditionGroupModel conditionGroup = new DealConditionGroupModel();
		final ProductDealConditionModel productCondtion1 = new ProductDealConditionModel();
		final ProductDealConditionModel productCondtion2 = new ProductDealConditionModel();
		final FreeGoodsDealBenefitModel benefit = new FreeGoodsDealBenefitModel();
		benefit.setProportionalFreeGood(proportion);
		productCondtion1.setProductCode("product1");
		//productCondtion1.setMinQty(4);
		productCondtion2.setProductCode("product2");
		//productCondtion2.setMinQty(5);
		final List<AbstractDealConditionModel> conditionList = new ArrayList<AbstractDealConditionModel>();
		final List<AbstractDealBenefitModel> benefitList = new ArrayList<AbstractDealBenefitModel>();
		conditionList.add(productCondtion1);
		conditionList.add(productCondtion2);
		benefitList.add(benefit);
		conditionGroup.setDealBenefits(benefitList);
		conditionGroup.setDealConditions(conditionList);
		conditionGroup.setMultipleScales(true);
		final DealScaleModel dscale = new DealScaleModel();
		dscale.setFrom(2);
		final List<DealScaleModel> dscales = new ArrayList<DealScaleModel>();
		dscales.add(dscale);
		conditionGroup.setDealScales(dscales);
		final List<Integer> scales = new ArrayList<Integer>();
		scales.add(10);
		conditionGroup.setScales(scales);
		deal.setConditionGroup(conditionGroup);
		deal.setCode("deal");
		return deal;
	}

	/**
	 * Find checker.
	 *
	 * @param deal
	 *           the deal
	 * @return the abstract lost deal checker
	 */
	private AbstractLostDealChecker findChecker(final DealModel deal)
	{
		AbstractLostDealChecker checker = null;
		for (final AbstractLostDealChecker abstractLostDealChecker : checkerList)
		{
			if (abstractLostDealChecker.isThisDealType(deal))
			{
				checker = abstractLostDealChecker;
			}

		}
		return checker;
	}

	/**
	 * Creates the cart.
	 *
	 * @param quantity1
	 *           the quantity1
	 * @param quantity2
	 *           the quantity2
	 * @param quantity3
	 *           the quantity3
	 * @param quantity4
	 *           the quantity4
	 * @param quantity5
	 *           the quantity5
	 * @return the cart model
	 */
	private CartModel createCart(final Long quantity1, final Long quantity2, final Long quantity3, final Long quantity4,
			final Long quantity5)
	{
		final CartModel cart = new CartModel();
		final CartEntryModel entry1 = new CartEntryModel();
		final CartEntryModel entry2 = new CartEntryModel();
		final CartEntryModel entry3 = new CartEntryModel();
		final CartEntryModel entry4 = new CartEntryModel();
		final CartEntryModel entry5 = new CartEntryModel();
		final SABMAlcoholVariantProductMaterialModel product1 = new SABMAlcoholVariantProductMaterialModel();
		final SABMAlcoholVariantProductMaterialModel product2 = new SABMAlcoholVariantProductMaterialModel();
		final SABMAlcoholVariantProductMaterialModel product3 = new SABMAlcoholVariantProductMaterialModel();
		final SABMAlcoholVariantProductMaterialModel product4 = new SABMAlcoholVariantProductMaterialModel();
		final SABMAlcoholVariantProductMaterialModel product5 = new SABMAlcoholVariantProductMaterialModel();
		product1.setCode("product1");
		product2.setCode("product2");
		product3.setCode("product3");
		product4.setCode("product4");
		product5.setCode("product1");

		entry1.setProduct(product1);
		entry1.setQuantity(quantity1);
		entry1.setEntryNumber(0);
		entry1.setTotalPrice(1000.00);
		entry2.setProduct(product2);
		entry2.setQuantity(quantity2);
		entry2.setEntryNumber(1);
		entry2.setTotalPrice(1000.00);
		entry3.setProduct(product3);
		entry3.setQuantity(quantity3);
		entry3.setEntryNumber(2);
		entry3.setTotalPrice(1000.00);
		entry4.setProduct(product4);
		entry4.setQuantity(quantity4);
		entry4.setEntryNumber(3);
		entry4.setTotalPrice(1000.00);
		entry5.setProduct(product5);
		entry5.setQuantity(quantity5);
		entry5.setEntryNumber(4);
		entry5.setTotalPrice(0.00);

		final List<String> deals = new ArrayList<String>();
		deals.add("deal");
		entry1.setDeals(deals);
		final List<AbstractOrderEntryModel> entries = new ArrayList<AbstractOrderEntryModel>();
		entries.add(entry1);
		entries.add(entry2);
		entries.add(entry3);
		entries.add(entry4);
		entries.add(entry5);
		cart.setEntries(entries);
		return cart;
	}

	/**
	 * Creates the deal001.
	 *
	 * @param proportion
	 *           the proportion
	 * @return the deal model
	 */
	private DealModel createDeal001(final boolean proportion)
	{
		final DealModel deal001 = new DealModel();
		final DealConditionGroupModel conditionGroup = new DealConditionGroupModel();
		final ProductDealConditionModel productCondtion1 = new ProductDealConditionModel();
		final ProductDealConditionModel productCondtion2 = new ProductDealConditionModel();
		final FreeGoodsDealBenefitModel benefit = new FreeGoodsDealBenefitModel();
		benefit.setProportionalFreeGood(proportion);
		productCondtion1.setProductCode("product1");
		productCondtion1.setMinQty(4);
		productCondtion2.setProductCode("product2");
		productCondtion2.setMinQty(5);
		final List<AbstractDealConditionModel> conditionList = new ArrayList<AbstractDealConditionModel>();
		final List<AbstractDealBenefitModel> benefitList = new ArrayList<AbstractDealBenefitModel>();
		conditionList.add(productCondtion1);
		conditionList.add(productCondtion2);
		benefitList.add(benefit);
		conditionGroup.setDealBenefits(benefitList);
		conditionGroup.setDealConditions(conditionList);
		conditionGroup.setMultipleScales(false);
		final List<Integer> scales = new ArrayList<Integer>();
		conditionGroup.setScales(scales);
		deal001.setConditionGroup(conditionGroup);
		deal001.setCode("deal");
		return deal001;
	}
}
