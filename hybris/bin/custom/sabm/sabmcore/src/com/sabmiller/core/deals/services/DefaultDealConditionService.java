/**
 *
 */
package com.sabmiller.core.deals.services;

import de.hybris.platform.b2b.model.B2BUnitModel;
import de.hybris.platform.core.model.order.AbstractOrderEntryModel;
import de.hybris.platform.core.model.order.CartModel;
import de.hybris.platform.core.model.product.ProductModel;
import de.hybris.platform.util.Config;
import de.hybris.platform.variants.model.VariantProductModel;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;

import jakarta.annotation.Resource;

import org.apache.commons.collections4.CollectionUtils;
import org.apache.commons.lang3.BooleanUtils;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.sabm.core.config.SabmConfigurationService;
import com.sabmiller.core.comparators.DealScaleComparator;
import com.sabmiller.core.deals.services.response.ConflictGroup;
import com.sabmiller.core.deals.services.response.DealQualificationResponse;
import com.sabmiller.core.deals.services.response.PartialDealQualificationResponse;
import com.sabmiller.core.deals.services.response.PartialDealQualificationResponse.PartialAvailability;
import com.sabmiller.core.enums.DealTypeEnum;
import com.sabmiller.core.model.AbstractDealBenefitModel;
import com.sabmiller.core.model.AbstractDealConditionModel;
import com.sabmiller.core.model.CartDealConditionModel;
import com.sabmiller.core.model.ComplexDealConditionModel;
import com.sabmiller.core.model.DealConditionGroupModel;
import com.sabmiller.core.model.DealModel;
import com.sabmiller.core.model.DealScaleModel;
import com.sabmiller.core.model.FreeGoodsDealBenefitModel;
import com.sabmiller.core.model.ProductDealConditionModel;
import com.sabmiller.core.model.SABMAlcoholProductModel;
import com.sabmiller.core.model.SABMAlcoholVariantProductEANModel;
import com.sabmiller.core.model.SABMAlcoholVariantProductMaterialModel;
import com.sabmiller.core.product.SabmProductService;
import com.sabmiller.core.util.SabmUtils;


/**
 * Evaluates the cart against set of deals to figure out the matching deals. At its core, the output of this
 * implementation is to classify deals into three categories. Fully Qualified, Partially Qualified and Not Qualified. In
 * addition, this service is also responsible for identifying conflicts within the fully qualified deals (ex: 2 deals
 * might qualify individually against cart, however - combined together they may not in which case they end up
 * conflicting each other)
 *
 * @author joshua.a.antony
 *
 */
public class DefaultDealConditionService implements DealConditionService
{
	private static final Logger LOG = LoggerFactory.getLogger(DefaultDealConditionService.class);

	@Resource(name = "dealsService")
	protected DealsService dealsService;

	@Resource(name = "productService")
	private SabmProductService productService;

	@Resource(name = "sabmConfigurationService")
	private SabmConfigurationService sabmConfigurationService;

	@Override
	public DealQualificationResponse findQualifiedDeals(final B2BUnitModel b2bUnitModel, final CartModel cart)
	{

		final DealBucket dealBucket = findDealsAvailability(b2bUnitModel.getComplexDeals(), cart);

		final Map<DealModel, List<DealModel>> conflictingDeals = findConflictingDeals(dealBucket.greenDealsBucket, cart);

		return new DealQualificationResponse(dealBucket.greenDealsBucket, dealBucket.amberDealsBucket, dealBucket.redDealsBucket,
				new ConflictGroup(conflictingDeals));
	}


	@Override
	public List<DealModel> findFullyQualifiedDeals(final List<DealModel> deals, final CartModel cart)
	{
		final List<DealModel> fullyDeals = new ArrayList<>();
		final PartialDealQualificationResponse partiallyQualifiedDeals = findPartiallyQualifiedDeals(deals, cart, true);

		if (partiallyQualifiedDeals != null)
		{
			final Set<PartialAvailability> partialAvailabilites = partiallyQualifiedDeals.getPartialAvailabilites();

			for (final PartialAvailability partialAvailability : CollectionUtils.emptyIfNull(partialAvailabilites))
			{
				final DealModel deal = partialAvailability.getDeal();
				boolean addDeal = true;


				for (final DealScaleModel scale : CollectionUtils.emptyIfNull(deal.getConditionGroup().getDealScales()))
				{
					if (partialAvailability.getTotalAvailableQty() >= scale.getFrom())
					{
						addDeal = true;
						break;
					}
					addDeal = false;
				}
				for (final AbstractDealConditionModel condition : deal.getConditionGroup().getDealConditions())
				{
					final long requiredQty = partialAvailability.getRequiredQtyWithGivenDealCondition(condition);
					if (requiredQty > 0)
					{
						addDeal = false;
						break;
					}
				}

				if (addDeal)
				{
					fullyDeals.add(deal);
				}
			}
		}

		return fullyDeals;
	}

	@Override
	public PartialDealQualificationResponse findPartiallyQualifiedDeals(final List<DealModel> deals, final CartModel cart,
			final boolean addAll)
	{
		final PartialDealQualificationResponse response = new PartialDealQualificationResponse();

		final List<AbstractOrderEntryModel> availableCartEntries = getNonConsumedCartEntries(cart);

		final List<PartialDealAvailability> partialAvailabilites = new ArrayList<>();

		// To determine the valid deals
		final List<DealModel> dealsFiltered = dealsService.getValidationDeals(deals, Boolean.TRUE);

		for (final DealModel deal : getNonConsumedDeals(dealsFiltered, cart))
		{
			final DealWrapper dealWrapper = new DealWrapper(deal);
			final PartialDealAvailability partialDealAvailability = new DealQualificationStrategyFactory().find(dealWrapper)
					.checkPartialDealsAvailability(dealWrapper, availableCartEntries);

			LOG.debug("Partial Deal Availabilility of Deal : [{}] is [{}] \n --------X------------X------------X-----------",
					deal.getCode(), partialDealAvailability);

			if (partialDealAvailability.isAvailable())
			{
				partialAvailabilites.add(partialDealAvailability);
			}
		}

		for (final DealModel deal : getQualifiedScaleDeal(cart))
		{
			partialAvailabilites.add(new ScaleDealQualificationStrategy()
					.checkPartialAvailabilityOfAlreadyQualifiedDeals(new DealWrapper(deal), cart.getEntries()));
		}

		for (final DealModel deal : getQualifiedProportionalDeal(cart))
		{
			final DealWrapper dealWrapper = new DealWrapper(deal);
			final PartialDealAvailability partialDealAvailability = new DealQualificationStrategyFactory()
					.findAlreadyQualified(dealWrapper).checkPartialDealsAvailabilityOfAlreadyQualified(dealWrapper, cart);

			if (partialDealAvailability.isAvailable())
			{
				partialAvailabilites.add(partialDealAvailability);
			}
		}

		for (final PartialDealAvailability pda : partialAvailabilites)
		{
			/**
			 * Modified the logic as per incident
			 * "INC0343881 : Cart erroring when Great Northern bulk deal is added to cart" Fix.
			 */
			if (!addAll && dealsService.isManualScaleProportionByEachDeal(pda.deal.getConditionGroup().getDealBenefits()))
			{
				final List<AbstractOrderEntryModel> matchingCartEntries = new ArrayList<AbstractOrderEntryModel>();
				for (final DealTrigger dt : pda.getDealTriggers())
				{
					matchingCartEntries.addAll(pda.getEntriesMatchingTrigger(dt));
				}
				for (final DealTrigger dt : pda.getDealTriggers())
				{
					response.add(pda.deal, dt.dealCondition, dt.quantity, matchingCartEntries, pda.scale, pda.getRatio(), addAll);
				}
			}
			else
			{
				for (final DealTrigger dt : pda.getDealTriggers())
				{
					response.add(pda.deal, dt.dealCondition, dt.quantity, pda.getEntriesMatchingTrigger(dt), pda.scale, pda.getRatio(),
							addAll);
				}
			}
		}

		final Map<DealModel, List<DealModel>> conflictingDeals = findConflictingDeals(response.getAllPartiallyQualifiedDeals(),
				cart);
		response.setConflictGroup(new ConflictGroup(conflictingDeals));

		return response;
	}

	/**
	 * Given the list of deals, this method checks for all the deals that could conflict with each other. Deals are
	 * considered conflicting if the products/hierarchy constituting the deal trigger, are part of multiple deals.
	 */
	@Override
	public Map<DealModel, List<DealModel>> findConflictingDeals(final List<DealModel> deals, final CartModel cart)
	{
		final Map<DealModel, List<DealModel>> conflictDealsMap = new HashMap<>();
		for (final DealModel deal : deals)
		{
			final List<DealModel> conflictingDeals = findConflicts(deal, deals, cart);
			if (!conflictingDeals.isEmpty())
			{
				conflictDealsMap.put(deal, conflictingDeals);
			}
		}
		return conflictDealsMap;
	}


	/**
	 * Check if this particular deal can be applied to the cart. A deal can be applied if all the products/hierarchy in
	 * the deal conditions are available in the cart with the required quantity
	 */
	@Override
	public DealQualificationStatus checkDealQualification(final DealModel deal, final CartModel cart)
	{
		final DealWrapper dealWrapper = new DealWrapper(deal);
		return new DealQualificationStrategyFactory().find(dealWrapper).checkDealsAvailability(dealWrapper, cart);
	}


	/**
	 * Check if the deal conflicts with any of the deals in the list. Deals are considered conflicting if they have
	 * common products in the trigger conditions
	 */
	private List<DealModel> findConflicts(final DealModel deal, final List<DealModel> deals, final CartModel cart)
	{
		final List<DealModel> conflictingDeals = new ArrayList<>();
		final List<ProductModel> dealProducts = dealsService.findDealProducts(deal.getConditionGroup().getDealConditions());

		for (final DealModel otherDeal : deals)
		{
			/*
			 * First, check if there're products belong to both two deals.
			 */
			final List<ProductModel> otherProducts = dealsService
					.findDealProducts(otherDeal.getConditionGroup().getDealConditions());
			final Collection<ProductModel> crossProducts = CollectionUtils.intersection(otherProducts, dealProducts);

			/*
			 * Second, check if the cart has these products.
			 */
			for (final AbstractOrderEntryModel entry : cart.getEntries())
			{
				if (crossProducts.contains(entry.getProduct()))
				{
					conflictingDeals.add(otherDeal);
					break;
				}
			}
		}

		LOG.debug("Deal {} has total {} conflicting deals. {} ", deal.getCode(), conflictingDeals.size(),
				SabmUtils.getDealNumbers(conflictingDeals));

		return conflictingDeals;
	}

	/**
	 * Checks if the cart can satisfy the Deal Trigger, which involves matching the products/product Hierarchy in the
	 * trigger with the cart contents
	 */
	private boolean availableInCart(final CartModel cart, final DealTrigger dealTrigger, final List<DealTrigger> excludes)
	{
		if (dealTrigger.isProduct())
		{
			return isProductInCart(cart, dealTrigger.productCode, excludes, dealTrigger.quantity);
		}
		return isProductHierarcyInCart(cart, dealTrigger.productHierarchy, excludes, dealTrigger.quantity);
	}

	private List<AbstractOrderEntryModel> checkPartialDealAvailabilityInCart(final List<AbstractOrderEntryModel> cartEntries,
			final DealTrigger dealTrigger, final List<DealTrigger> excludes)
	{
		final List<AbstractOrderEntryModel> matchingCartEntries = new ArrayList<>();

		if (dealTrigger.isProduct())
		{
			final AbstractOrderEntryModel entry = lookupProductInCart(cartEntries, dealTrigger.productCode, excludes,
					dealTrigger.quantity, true);

			LOG.debug("lookupProductInCart() for Product [{}] returned [{}] ", dealTrigger.productCode, entry);
			if (entry != null)
			{
				matchingCartEntries.add(entry);
			}
		}
		else
		{
			final List<AbstractOrderEntryModel> entries = lookupProductHierarcyInCart(cartEntries, dealTrigger.productHierarchy,
					excludes, dealTrigger.quantity, true);

			LOG.debug("lookupProductHierarcyInCart() for Hierarchy [{}] returned {} ", dealTrigger.productHierarchy, entries);
			matchingCartEntries.addAll(entries);
		}
		LOG.debug("In checkPartialDealAvailabilityInCart(). Returning {} ", matchingCartEntries);
		return matchingCartEntries;
	}


	private List<AbstractOrderEntryModel> lookupProductHierarcyInCart(final CartModel cart,
			final ProductHierarchy productHierarchy, final List<DealTrigger> excludes)
	{
		return lookupProductHierarcyInCart(Collections.unmodifiableList(cart.getEntries()), productHierarchy, excludes);
	}


	private List<AbstractOrderEntryModel> lookupProductHierarcyInCart(final List<AbstractOrderEntryModel> cartEntries,
			final ProductHierarchy productHierarchy, final List<DealTrigger> excludes)
	{
		final List<AbstractOrderEntryModel> matchingCartEntries = new ArrayList<>();
		for (final AbstractOrderEntryModel cartEntry : cartEntries)
		{
			final SABMAlcoholVariantProductMaterialModel material = (SABMAlcoholVariantProductMaterialModel) cartEntry.getProduct();

			final boolean productFound = BooleanUtils.isNotTrue(cartEntry.getIsFreeGood())
					&& !isProductExcluded(material.getCode(), excludes) && productBelongsToHierarchy(material, productHierarchy);
			if (productFound)
			{
				LOG.debug("In lookupProductHierarcyInCart(). Found cart entry for Hierarchy [{}]. Cart Entry is [{}] ",
						productHierarchy, cartEntry);

				matchingCartEntries.add(cartEntry);
			}
		}
		LOG.debug("In lookupProductHierarcyInCart(). Cart Entry NOT found for Product Hierarchy [{}]", productHierarchy);
		return matchingCartEntries;
	}

	/**
	 * Checks if the product 'productCode' with given quantity is available in the cart along with ensuring that the
	 * 'excludes' trigger have been filtered out. Example : Buy a minimum of 10 cases of any products across the Matilda
	 * Bay and Miller ranges except Lazy Yak 4x6 375ml bottles. In this case, the excludes list contains Lazy Yak 4x6
	 * 375ml bottles and if the cart has just 10 cases of Lazy Yak 4x6 375ml bottles, the method will return false
	 *
	 */
	private boolean isProductInCart(final CartModel cart, final String productCode, final List<DealTrigger> excludes,
			final int quantity)
	{
		return isProductInCart(cart.getEntries(), productCode, excludes, quantity);
	}

	private boolean isProductInCart(final List<AbstractOrderEntryModel> cartEntries, final String productCode,
			final List<DealTrigger> excludes, final int quantity)
	{
		return lookupProductInCart(cartEntries, productCode, excludes, quantity, false) != null;
	}

	private AbstractOrderEntryModel lookupProductInCart(final CartModel cart, final String productCode,
			final List<DealTrigger> excludes)
	{
		return lookupProductInCart(Collections.unmodifiableList(cart.getEntries()), productCode, excludes);
	}


	private AbstractOrderEntryModel lookupProductInCart(final List<AbstractOrderEntryModel> cartEntries, final String productCode,
			final List<DealTrigger> excludes)
	{
		final ProductModel product = productService.getProductForCodeSafe(productCode);

		if (!(product instanceof SABMAlcoholVariantProductMaterialModel))
		{
			return null;
		}

		for (final AbstractOrderEntryModel cartEntry : cartEntries)
		{
			if (BooleanUtils.isNotTrue(cartEntry.getIsFreeGood())
					&& (cartEntry.getProduct().equals(product) || sameBaseProduct(product, cartEntry.getProduct()))
					&& !isProductExcluded(productCode, excludes))
			{
				LOG.debug("In lookupProductInCart(). Found cart entry for Product [{}]. Cart Entry is [{}] ", productCode, cartEntry);
				return cartEntry;
			}
		}
		LOG.debug("In lookupProductInCart(). Cart entry NOT found for Product [{}]. ", productCode);
		return null;
	}

	/**
	 * Check if the 2 products have the same base product.
	 *
	 * @param product1
	 *           the product1
	 * @param product2
	 *           the product2
	 * @return true, if successful
	 */
	private boolean sameBaseProduct(final ProductModel product1, final ProductModel product2)
	{
		if (product1 instanceof VariantProductModel && product2 instanceof VariantProductModel)
		{
			return ((VariantProductModel) product1).getBaseProduct().equals(((VariantProductModel) product2).getBaseProduct());
		}

		return false;
	}

	private AbstractOrderEntryModel lookupProductInCart(final List<AbstractOrderEntryModel> cartEntries, final String productCode,
			final List<DealTrigger> excludes, final int quantity, final boolean checkPartialAvailability)
	{
		final AbstractOrderEntryModel cartEntry = lookupProductInCart(cartEntries, productCode, excludes);

		if (cartEntry != null)
		{
			final boolean available = checkPartialAvailability ? thresholdQtyMatch(quantity, cartEntry.getQuantity().longValue())
					: cartEntry.getQuantity().longValue() >= quantity;
			if (available)
			{
				LOG.debug("In lookupProductInCart() Found cart entry for product {}, quantity {}, partialAvail {}. Returning {} ",
						productCode, quantity, checkPartialAvailability, cartEntry);

				return cartEntry;
			}
		}
		LOG.debug("In lookupProductInCart() Cart Entry NOT Found for product {}, quantity {}, partialAvail {} ", productCode,
				quantity, checkPartialAvailability);
		return null;
	}


	/**
	 * Check if items in the cart are part of the product hierarchy and have relevant quantity. Example : To match
	 * products for Buy 10 cases of any products across Carlton Draught hierarchy, all the items in the cart might have
	 * to be introspected. For ex : 2 products in the Carlton Draught family, each with 5 cases will satisfy this
	 * condition and return true
	 */
	private boolean isProductHierarcyInCart(final CartModel cart, final ProductHierarchy productHierarchy,
			final List<DealTrigger> excludes, final int quantity)
	{
		return isProductHierarcyInCart(cart.getEntries(), productHierarchy, excludes, quantity);
	}

	private boolean isProductHierarcyInCart(final List<AbstractOrderEntryModel> cartEntries,
			final ProductHierarchy productHierarchy, final List<DealTrigger> excludes, final int quantity)
	{
		return !lookupProductHierarcyInCart(cartEntries, productHierarchy, excludes, quantity, false).isEmpty();
	}

	private List<AbstractOrderEntryModel> lookupProductHierarcyInCart(final List<AbstractOrderEntryModel> availableCartEntries,
			final ProductHierarchy productHierarchy, final List<DealTrigger> excludes, final int quantity,
			final boolean checkPartialAvailability)
	{

		boolean available = false;
		long totalQuantityInCart = 0;
		final List<AbstractOrderEntryModel> cartEntries = lookupProductHierarcyInCart(availableCartEntries, productHierarchy,
				excludes);

		LOG.debug("There are total [{}] cart entries matching the product hierarchy [{}]", cartEntries.size(), productHierarchy);
		LOG.info("lookupProductHierarcyInCart : There are total [{}] cart entries matching the product hierarchy [{}]", cartEntries.size(), productHierarchy);

		for (final AbstractOrderEntryModel cartEntry : cartEntries)
		{
			totalQuantityInCart = totalQuantityInCart + cartEntry.getQuantity().longValue();
		}
		LOG.info("lookupProductHierarcyInCart : totalQuantityInCart = " + totalQuantityInCart + "Quantity= " + quantity);
		if (checkPartialAvailability)
		{
			available = thresholdQtyMatch(quantity, totalQuantityInCart);

			LOG.debug("Checking Partial Availability Hierarchy. Cart Qty : {} , Trigger Qty : {} ,Threshold {}% , available : {}",
					totalQuantityInCart, quantity, getDealsThresholdPercent(), available);
			LOG.info("lookupProductHierarcyInCart : Checking Partial Availability Hierarchy. Cart Qty : {} , Trigger Qty : {} ,Threshold {}% , available : {}",
					totalQuantityInCart, quantity, getDealsThresholdPercent(), available);
		}
		else
		{
			available = totalQuantityInCart >= quantity;
		}

		return available ? cartEntries : Collections.emptyList();
	}

	/**
	 * Verifies if the product is part of the excludes list. The excludes list can contain both products and hierarchy
	 * (in which case, a lookup is performed for the product in the hierarchy)
	 */
	private boolean isProductExcluded(final String productCode, final List<DealTrigger> excludes)
	{
		LOG.debug("Checking if the product : {} is part of the exclude list : {} ", productCode, excludes);

		for (final DealTrigger dealTrigger : CollectionUtils.emptyIfNull(excludes))
		{
			if (dealTrigger.isProduct() && productCode.equals(dealTrigger.productCode))
			{
				LOG.debug("Product {} is part of the exclusion list {}. isProductExcluded() returning false ", productCode, excludes);
				return true;
			}
			if (dealTrigger.isHierarchial() && productBelongsToHierarchy(productCode, dealTrigger.productHierarchy))
			{
				return false;
			}
		}
		return false;
	}

	/**
	 * Checks if the product belongs to the particular hierarchy. Example : Lazy Yak 4x6 375ml bottles belongs to Matilda
	 * Bay product hierarchy
	 */
	private boolean productBelongsToHierarchy(final String productCode, final ProductHierarchy productHierarchy)
	{
		final SABMAlcoholVariantProductMaterialModel material = (SABMAlcoholVariantProductMaterialModel) productService
				.getProductForCode(productCode);

		return productBelongsToHierarchy(material, productHierarchy);
	}

	/**
	 * Checks if the product belongs to the particular hierarchy. Example : Lazy Yak 4x6 375ml bottles belongs to Matilda
	 * Bay product hierarchy
	 */
	private boolean productBelongsToHierarchy(final SABMAlcoholVariantProductMaterialModel material,
			final ProductHierarchy productHierarchy)
	{
		final SABMAlcoholVariantProductEANModel ean = (SABMAlcoholVariantProductEANModel) material.getBaseProduct();
		final SABMAlcoholProductModel alcoholProduct = (SABMAlcoholProductModel) ean.getBaseProduct();

		return compareIfNotNull(productHierarchy.line, alcoholProduct.getLevel1())
				&& compareIfNotNull(productHierarchy.brand, alcoholProduct.getLevel2())
				&& compareIfNotNull(productHierarchy.variety, alcoholProduct.getLevel3())
				&& compareIfNotNull(productHierarchy.empties, ean.getLevel4())
				&& compareIfNotNull(productHierarchy.emptyType, ean.getLevel5())
				&& compareIfNotNull(productHierarchy.presentation, ean.getLevel6());
	}


	private boolean compareIfNotNull(final String trigger, final String hierarchy)
	{
		if (StringUtils.isNotBlank(trigger))
		{
			return trigger.equals(hierarchy);
		}
		return true;
	}

	private DealBucket findDealsAvailability(final Collection<DealModel> deals, final CartModel cart)
	{
		final DealBucket dealBucket = new DealBucket();
		final List<DealModel> dealOnlines = new ArrayList<>();
		LOG.debug("Deals before fro conflict deals to cart {} : {}", cart, deals);
		// get the Online deals
		final Collection<DealModel> complexOnlineDeals = dealsService.filterOnlineDeals(deals);
		dealOnlines.addAll(complexOnlineDeals);
		LOG.debug("Deals after online check conflict deals to cart {} : {}", cart, dealOnlines);
		// To determine the valid deals
		final List<DealModel> dealsFiltered = dealsService.getValidationDeals(dealOnlines, Boolean.TRUE);
		LOG.debug("Deals after fro conflict deals to cart {} : {}", cart, dealsFiltered);
		for (final DealModel eachDeal : dealsFiltered)
		{
			dealBucket.add(eachDeal, checkDealQualification(eachDeal, cart));
		}
		return dealBucket;
	}

	/**
	 * Wraps the {@link DealModel} and breaks down the trigger into 3 different classifications : mandatories, optionals
	 * and excludes. mandatories - products/hierarcy are are mandatory trigger for the deal, optionals -
	 * product/hierarchy that are not mandatory for the deal, excludes - the product against which the deal cannot be
	 * applied.
	 *
	 * Example 1: Buy a minimum of 10 cases of any products across the Matilda Bay and Miller ranges except Lazy Yak 4x6
	 * 375ml bottles, the mandatories will be empty, optionals will be Matilda Bay and Miller Ranges and excludes will be
	 * Lazy Yak 4x6 375ml bottles.
	 *
	 * Example 2 : Buy 5 cases of products across Matilda Bay and 4 cases of products across Miller ranges. Here,
	 * mandatories will be Matilda Bay and Miller, optionals and excludes will be empty
	 *
	 * @author joshua.a.antony
	 */
	class DealWrapper
	{
		private String dealCode;
		private final List<DealTrigger> mandatories = new ArrayList<>();
		private final List<DealTrigger> optionals = new ArrayList<>();
		private final List<DealTrigger> excludes = new ArrayList<>();
		private boolean scaleDeal;
		private List<Integer> scales;
		private DealModel deal;

		public DealWrapper(final DealModel deal)
		{
			wrap(deal);
		}

		protected void wrap(final DealModel deal)
		{
			this.deal = deal;
			this.dealCode = deal.getCode();
			this.scaleDeal = BooleanUtils.toBoolean(deal.getConditionGroup().getMultipleScales());
			this.scales = deal.getConditionGroup().getScales();

			if (DealTypeEnum.COMPLEX.equals(deal.getDealType()))
			{
				wrapComplexDeals(deal);
			}
			else
			{
				wrapNonComplexDeals(deal);
			}
		}

		private void wrapComplexDeals(final DealModel deal)
		{
			final DealConditionGroupModel dealConditionGroup = deal.getConditionGroup();

			final List<AbstractDealConditionModel> dealConditons = dealConditionGroup.getDealConditions();

			for (final AbstractDealConditionModel dc : dealConditons)
			{
				if (BooleanUtils.isTrue(dc.getExclude()))
				{
					excludes.add(new DealTrigger(dc));
				}
				else if (dc instanceof ProductDealConditionModel)
				{
					final ProductDealConditionModel pdc = (ProductDealConditionModel) dc;
					if (BooleanUtils.isTrue(pdc.getMandatory()) || countRealCondition(dealConditionGroup) == 1)
					{
						mandatories.add(new DealTrigger(pdc));
					}
					else
					{
						optionals.add(new DealTrigger(pdc));
					}

				}
				else if (dc instanceof ComplexDealConditionModel)
				{
					final ComplexDealConditionModel cdc = (ComplexDealConditionModel) dc;
					if (BooleanUtils.isTrue(cdc.getMandatory()) || countRealCondition(dealConditionGroup) == 1)
					{
						mandatories.add(new DealTrigger(cdc));
					}
					else
					{
						optionals.add(new DealTrigger(cdc));
					}
				}
			}
		}

		/**
		 * Count the number of conditions excluding the excluded ones.
		 *
		 * @param dealConditionGroup
		 *           the deal condition group
		 * @return the int
		 */
		private int countRealCondition(final DealConditionGroupModel dealConditionGroup)
		{
			int counter = 0;

			for (final AbstractDealConditionModel condition : dealConditionGroup.getDealConditions())
			{
				if (BooleanUtils.isNotTrue(condition.getExclude()))
				{
					counter++;
				}
			}

			return counter;
		}

		private void wrapNonComplexDeals(final DealModel deal)
		{
			final List<AbstractDealConditionModel> dealConditons = deal.getConditionGroup().getDealConditions();
			for (final AbstractDealConditionModel dc : dealConditons)
			{
				if (dc instanceof ProductDealConditionModel)
				{
					mandatories.add(new DealTrigger(dc));
				}
			}
		}

		public boolean hasMandatories()
		{
			return !mandatories.isEmpty();
		}

		public boolean hasOptional()
		{
			return !optionals.isEmpty();
		}

		public List<DealTrigger> getDealTriggers()
		{
			final List<DealTrigger> dealTriggers = new ArrayList<>();
			dealTriggers.addAll(mandatories);
			dealTriggers.addAll(optionals);
			return dealTriggers;
		}

		public boolean isScaleDeal()
		{
			return scaleDeal;
		}

		public String getDealCode()
		{
			return dealCode;
		}

		@Override
		public String toString()
		{
			final StringBuilder sb = new StringBuilder();
			sb.append("deal : " + getDealCode() + " , Is Scale Deal : " + scaleDeal + " , Total Mandatories : " + mandatories.size()
					+ " , Total Optionals : " + optionals.size() + " , Total Excludes : " + excludes.size());

			sb.append(" , Mandatories ");
			for (final DealTrigger dt : mandatories)
			{
				sb.append("[" + dt + "]");
			}
			sb.append(" , Optionals ");
			for (final DealTrigger dt : optionals)
			{
				sb.append("[" + dt + "]");
			}
			sb.append(" , Excludes ");
			for (final DealTrigger dt : excludes)
			{
				sb.append("[" + dt + "]");
			}
			return sb.toString();
		}
	}

	/**
	 * Representation of the top 3 level Product Hierarchy. Line (level 1), Brand (Level 2) and Variety/Sub Brand (Level
	 * 3) form the top 3 levels. In Hybris these 3 level information are stored in {@link SABMAlcoholProductModel}.
	 *
	 * Example: Carlton Draught, Miller Range, Matilda Bay can be represented by top 3 levels. It should be noted that
	 * these levels do not provide detail (if bottle/can, packaging size) information
	 *
	 * @author joshua.a.antony
	 */
	class ProductHierarchy
	{
		private final String line, brand, variety, empties, emptyType, presentation;

		public ProductHierarchy(final String line, final String brand, final String variety, final String empties,
				final String emptyType, final String presentation)
		{
			this.line = line;
			this.brand = brand;
			this.variety = variety;
			this.empties = empties;
			this.emptyType = emptyType;
			this.presentation = presentation;
		}

		@Override
		public String toString()
		{
			return "line : " + line + " , brand : " + brand + " , variety : " + variety + " , empties : " + empties
					+ " , emptyType : " + emptyType + " , presentation : " + presentation;
		}
	}

	/**
	 * Represents a single Trigger condition of deal. Example : Buy a minimum of 10 cases of any products across the
	 * Matilda Bay and Miller ranges.
	 *
	 * @author joshua.a.antony
	 */
	class DealTrigger
	{
		private final AbstractDealConditionModel dealCondition;
		private String productCode;
		private ProductHierarchy productHierarchy;
		private int quantity = -1;
		private boolean isMandatory = false;


		public DealTrigger(final AbstractDealConditionModel dealCondition)
		{
			this.dealCondition = dealCondition;
			this.isMandatory = BooleanUtils.isTrue(dealCondition.getMandatory());
			if (dealCondition instanceof ProductDealConditionModel)
			{
				final ProductDealConditionModel productDealCondition = (ProductDealConditionModel) dealCondition;
				this.productCode = productDealCondition.getProductCode();
				this.quantity = productDealCondition.getMinQty() == null ? 0 : productDealCondition.getMinQty();
			}
			else if (dealCondition instanceof ComplexDealConditionModel)
			{
				final ComplexDealConditionModel complexDealCondition = (ComplexDealConditionModel) dealCondition;
				this.productHierarchy = new ProductHierarchy(complexDealCondition.getLine(), complexDealCondition.getBrand(),
						complexDealCondition.getVariety(), complexDealCondition.getEmpties(), complexDealCondition.getEmptyType(),
						complexDealCondition.getPresentation());
				this.quantity = complexDealCondition.getQuantity() == null ? 0 : complexDealCondition.getQuantity();
			}
		}

		public boolean isMandatory()
		{
			return isMandatory;
		}

		private boolean isProduct()
		{
			return StringUtils.isNotBlank(productCode);
		}

		private boolean isHierarchial()
		{
			return productHierarchy != null;
		}

		@Override
		public String toString()
		{
			return "isProduct : " + isProduct() + " , isHierarchial : " + isHierarchial() + " , productCode : " + productCode
					+ " , quantity : " + quantity + " , productHierarchy : " + productHierarchy;
		}
	}

	/**
	 * Based on the pre-conditions and the scales of the deals, appropriate strategy is used.
	 *
	 * Example 1: Buy 5 cases of Carlton Draught AND 4 cases of Peroni, in this case both Carlton Draught & Peroni are
	 * required, hence the {@link MandatoriesDealQualificationStrategy} will be returned.
	 *
	 * Example 2: Buy 5 cases of Carlton Draught OR 4 cases of Peroni, since both Carlton Draught & Peroni are optional,
	 * {@link OptionalsDealQualificationStrategy} will be returned
	 *
	 * Example 3: Buy 10 cases across Matilda Bay and Miller Ranges - scale deal and
	 * {@link ScaleDealQualificationStrategy} will be returned
	 *
	 * Example 4 : Buy 10 cases across Matilda Bay and Miller Ranges with minimum of 5 cases of Lazy Yak. Here,
	 * {@link ScaleDealQualificationStrategy} is returned. However, scale deal will in turn execute the
	 * {@link MandatoriesDealQualificationStrategy} for the 5 cases of Lazy Yak
	 *
	 * @author joshua.a.antony
	 */
	private class DealQualificationStrategyFactory
	{
		public DealQualificationStrategy find(final DealWrapper dealWrapper)
		{
			if (dealWrapper.isScaleDeal())
			{
				return new ScaleDealQualificationStrategy();
			}
			if (dealWrapper.hasMandatories())
			{
				return new MandatoriesDealQualificationStrategy();
			}
			if (dealWrapper.hasOptional())
			{
				return new OptionalsDealQualificationStrategy();
			}
			return new NotQualifiedDealQualificationStrategy();
		}

		public DealQualificationStrategy findAlreadyQualified(final DealWrapper dealWrapper)
		{
			if (dealWrapper.hasMandatories())
			{
				return new MandatoriesDealQualificationStrategy();
			}
			if (dealWrapper.hasOptional())
			{
				return new OptionalsDealQualificationStrategy();
			}
			return new NotQualifiedDealQualificationStrategy();
		}
	}

	private interface DealQualificationStrategy
	{

		DealQualificationStatus checkDealsAvailability(DealWrapper dealWrapper, CartModel cart);

		PartialDealAvailability checkPartialDealsAvailability(DealWrapper dealWrapper, List<AbstractOrderEntryModel> cartEntries);

		PartialDealAvailability checkPartialDealsAvailabilityOfAlreadyQualified(DealWrapper dealWrapper, CartModel cart);
	}

	/**
	 * Determines if the given scale deal can be applied to the cart. Ex : Buy 10 cases of any products across Matilda
	 * Bay and Miller ranges. In this case, the cart is evaluated to check if the total products across Matilda Bay and
	 * Miller Ranges are greater than 10
	 *
	 * @author joshua.a.antony
	 */
	private class ScaleDealQualificationStrategy implements DealQualificationStrategy
	{

		@Override
		public DealQualificationStatus checkDealsAvailability(final DealWrapper dealWrapper, final CartModel cart)
		{
			return isScaleDealAvailable(dealWrapper, cart) ? DealQualificationStatus.QUALIFIED
					: DealQualificationStatus.NOT_QUALIFIED;
		}

		@Override
		public PartialDealAvailability checkPartialDealsAvailability(final DealWrapper dealWrapper,
				final List<AbstractOrderEntryModel> cartEntries)
		{
			LOG.debug("Deal {} - In ScaleDealQualificationStrategy.checkPartialDealsAvailability() ", dealWrapper.getDealCode());

			if (!new MandatoriesDealQualificationStrategy().checkPartialDealsAvailability(dealWrapper, cartEntries).isAvailable()
					&& dealWrapper.hasMandatories())
			{
				LOG.debug(
						"Deal {} . In ScaleDealQualificationStrategy.checkPartialDealsAvailability() - The mandatories in scale deal are not in cart!",
						dealWrapper.getDealCode());

				return new PartialDealAvailability(dealWrapper.deal, Collections.emptyMap());
			}

			final Map<DealTrigger, List<AbstractOrderEntryModel>> map = lookupCartEntriesMatchingDeal(dealWrapper, cartEntries,
					true);

			final long totalQuantityInCart = deriveTotalQty(map);

			if (thresholdQtyMatch(Collections.min(dealWrapper.scales).intValue(), totalQuantityInCart))
			{
				return new PartialDealAvailability(dealWrapper.deal, map, Collections.min(dealWrapper.scales));
			}

			LOG.debug("Scale Deal {} not available in cart. Total Qty {} , Scale {} ", dealWrapper.getDealCode(),
					totalQuantityInCart, Collections.min(dealWrapper.scales));
			return new PartialDealAvailability(dealWrapper.deal, Collections.emptyMap());
		}

		private boolean isScaleDealAvailable(final DealWrapper dealWrapper, final CartModel cart)
		{
			if (!new MandatoriesDealQualificationStrategy().isMandatoriesAvailableInCart(dealWrapper, cart))
			{
				LOG.debug("Deal {} . In isScaleDealAvailable() - The mandatories in scale deal are not in cart! ",
						dealWrapper.getDealCode());

				return false;
			}

			final long totalQuantity = deriveTotalQty(lookupCartEntriesMatchingDeal(dealWrapper, cart.getEntries()));

			LOG.debug("In isScaleDealAvailable(). Deal : {}  Total Quantity : {} , Scales : {} ", dealWrapper.getDealCode(),
					totalQuantity, dealWrapper.scales);

			return totalQuantity >= Collections.min(dealWrapper.scales);
		}

		private long deriveTotalQty(final Map<DealTrigger, List<AbstractOrderEntryModel>> map)
		{
			long totalQuantity = 0;
			for (final DealTrigger dt : map.keySet())
			{
				for (final AbstractOrderEntryModel cartEntry : map.get(dt))
				{
					totalQuantity = totalQuantity + cartEntry.getQuantity();
				}
			}
			return totalQuantity;
		}

		private Map<DealTrigger, List<AbstractOrderEntryModel>> lookupCartEntriesMatchingDeal(final DealWrapper dealWrapper,
				final List<AbstractOrderEntryModel> cartEntries)
		{
			return lookupCartEntriesMatchingDeal(dealWrapper, cartEntries, false);
		}

		private Map<DealTrigger, List<AbstractOrderEntryModel>> lookupCartEntriesMatchingDeal(final DealWrapper dealWrapper,
				final List<AbstractOrderEntryModel> cartEntries, final boolean partialAvailability)
		{
			final Map<DealTrigger, List<AbstractOrderEntryModel>> map = new HashMap<DealTrigger, List<AbstractOrderEntryModel>>();
			final List<DealTrigger> scaleDealTriggers = new ArrayList<>();
			scaleDealTriggers.addAll(dealWrapper.mandatories);
			scaleDealTriggers.addAll(dealWrapper.optionals);

			for (final DealTrigger eachDealTrigger : scaleDealTriggers)
			{
				if (eachDealTrigger.isProduct())
				{
					final AbstractOrderEntryModel cartEntry = lookupProductInCart(cartEntries, eachDealTrigger.productCode,
							dealWrapper.excludes);

					if (cartEntry != null)
					{
						final boolean available = partialAvailability
								? thresholdQtyMatch(eachDealTrigger.quantity, cartEntry.getQuantity())
								: cartEntry.getQuantity() >= eachDealTrigger.quantity;
						if (available)
						{
							LOG.debug("In lookupCartEntriesMatchingDeal() Found cart entry for Product {} !!!  ",
									eachDealTrigger.productCode);
							map.put(eachDealTrigger, Arrays.asList(cartEntry));
						}
					}
				}
				else if (eachDealTrigger.isHierarchial())
				{
					map.put(eachDealTrigger,
							lookupProductHierarcyInCart(cartEntries, eachDealTrigger.productHierarchy, dealWrapper.excludes));
				}
			}
			return map;
		}

		/**
		 * Checks for up selling of already qualified scale deals.
		 *
		 * Example : User has already qualified for Scale Deal X : Buy 10 cases of Product X to get a case of Product Z
		 * free. Buy 20 cases of Product X to get 3 cases of Product Z free. Now assume that cart has 18 cases of product
		 * X. Therefore, the user already qualified for Deal X for scale 10. However, this algorithm will take into
		 * account the other scale in the deal (which is 20) and make this deal eligible again for partial qualification
		 */
		public PartialDealAvailability checkPartialAvailabilityOfAlreadyQualifiedDeals(final DealWrapper dealWrapper,
				final List<AbstractOrderEntryModel> cartEntries)
		{
			final Map<DealTrigger, List<AbstractOrderEntryModel>> map = lookupCartEntriesMatchingDeal(dealWrapper, cartEntries,
					true);

			final long totalQuantityInCart = deriveTotalQty(map);
			int totalMatch = 0;
			int oldScale = 0;
			for (final Integer eachScale : dealWrapper.scales)
			{
				if (totalQuantityInCart - oldScale <= 0)
				{
					break;
				}
				if (thresholdQtyMatch(eachScale - oldScale, totalQuantityInCart - oldScale))
				{
					if (++totalMatch > 1)
					{
						//Modified As per incident:INC0323816 fix
						//return new PartialDealAvailability(dealWrapper.deal, map, eachScale);
						final double ratio = getRatioForScaleDeal(map, eachScale);
						final PartialDealAvailability partialDealAvailability = new PartialDealAvailability(dealWrapper.deal, map,
								eachScale);
						partialDealAvailability.setRatio((int) ratio);
						return partialDealAvailability;
					}
				}
				oldScale = eachScale;
			}
			return new PartialDealAvailability(dealWrapper.deal, Collections.emptyMap());
		}

		/*
		 * (non-Javadoc)
		 *
		 * @see com.sabmiller.core.deals.services.DefaultDealConditionService.DealQualificationStrategy#
		 * checkPartialDealsAvailabilityOfAlreadyQualified(com.sabmiller.core.deals.services.DefaultDealConditionService.
		 * DealWrapper, de.hybris.platform.core.model.order.CartModel)
		 */
		@Override
		public PartialDealAvailability checkPartialDealsAvailabilityOfAlreadyQualified(final DealWrapper dealWrapper,
				final CartModel cart)
		{
			return null;
		}

	}

	/**
	 * Verifies if each of the mandatory products that constitute the deal condition are available in the cart. Ex: Buy
	 * 10 cases of X and 5 cases of Y, here the presence of both these products (along with min quantity) are validated
	 * against the cart.
	 *
	 * @author joshua.a.antony
	 */
	private class MandatoriesDealQualificationStrategy implements DealQualificationStrategy
	{

		@Override
		public DealQualificationStatus checkDealsAvailability(final DealWrapper dealWrapper, final CartModel cart)
		{
			LOG.debug("Deal {} - In MandatoriesDealQualificationStrategy.checkDealsAvailability()", dealWrapper.getDealCode());

			return isMandatoriesAvailableInCart(dealWrapper, cart) ? DealQualificationStatus.QUALIFIED
					: DealQualificationStatus.NOT_QUALIFIED;
		}

		@Override
		public PartialDealAvailability checkPartialDealsAvailability(final DealWrapper dealWrapper,
				final List<AbstractOrderEntryModel> cartEntries)
		{
			LOG.debug("Deal {} - In MandatoriesDealQualificationStrategy.checkPartialDealsAvailability()",
					dealWrapper.getDealCode());

			final Map<DealTrigger, List<AbstractOrderEntryModel>> map = new HashMap<DealTrigger, List<AbstractOrderEntryModel>>();
			for (final DealTrigger dealTrigger : dealWrapper.mandatories)
			{
				final List<AbstractOrderEntryModel> matchingCartEntries = checkPartialDealAvailabilityInCart(cartEntries, dealTrigger,
						dealWrapper.excludes);

				if (matchingCartEntries.isEmpty())
				{
					return new PartialDealAvailability(dealWrapper.deal, Collections.emptyMap());
				}
				map.put(dealTrigger, matchingCartEntries);
			}
			//Fix as per INC0480663 : B2B - PQD Modal popping incorrectly
			if (Config.getBoolean("PQD.mandatory.partial.dealCondition", false))
			{
				if (dealWrapper.optionals != null && dealWrapper.optionals.size() > 0)
				{

					for (final DealTrigger dealTrigger : dealWrapper.optionals)
					{
						final List<AbstractOrderEntryModel> matchingCartEntries = checkPartialDealAvailabilityInCart(cartEntries,
								dealTrigger, dealWrapper.excludes);

						if (!matchingCartEntries.isEmpty())
						{
							map.put(dealTrigger, matchingCartEntries);
						}
					}
				}
			}
			return new PartialDealAvailability(dealWrapper.deal, map);
		}

		public PartialDealAvailability checkPartialDealsAvailabilityOfAlreadyQualified(final DealWrapper dealWrapper,
				final CartModel cart)
		{
			final PartialDealAvailability pda = checkPartialDealsAvailability(dealWrapper, cart.getEntries());

			double ratio = 0;
			boolean first = true;
			final Map<DealTrigger, Long> mapQuantity = new HashMap<>();

			long totalQtyDeal = 0;
			for (final Map.Entry<DealTrigger, List<AbstractOrderEntryModel>> entry : pda.map.entrySet())
			{
				final DealTrigger dealTrigger = entry.getKey();
				final List<AbstractOrderEntryModel> orderEntries = entry.getValue();

				long totalQty = 0;

				for (final AbstractOrderEntryModel orderEntry : orderEntries)
				{
					totalQty += orderEntry.getQuantity();
				}
				mapQuantity.put(dealTrigger, totalQty);
				totalQtyDeal += totalQty;
				if (first)
				{
					ratio = Math.ceil((double) totalQty / dealTrigger.quantity);
					first = false;
					continue;
				}

				final double ceilRatio = Math.ceil((double) totalQty / dealTrigger.quantity);
				if (ceilRatio < ratio)
				{
					ratio = ceilRatio;
				}
			}

			final List<DealScaleModel> dealScales = new ArrayList<>(pda.deal.getConditionGroup().getDealScales());

			if (CollectionUtils.isNotEmpty(dealScales))
			{
				Collections.sort(dealScales, DealScaleComparator.INSTANCE);
				if (dealScales.get(0).getFrom() > 0)
				{
					int scaleRatio = 1;
					if (dealsService.isManualScaleProportionByEachDeal(pda.deal.getConditionGroup().getDealBenefits()))
					{
						for (final DealScaleModel scale : dealScales)
						{
							if (totalQtyDeal > scale.getFrom())
							{
								scaleRatio++;
							}
						}
					}

					if (scaleRatio < ratio)
					{
						ratio = scaleRatio;
					}

					if (dealScales.size() >= scaleRatio)
					{
						pda.scale = dealScales.get(scaleRatio - 1).getFrom();
					}
				}
			}
			if (ratio > 1)
			{
				for (final DealTrigger trigger : pda.map.keySet())
				{
					final int previousRatioQty = trigger.quantity * (int) (ratio - 1);
					final Long prodQty = mapQuantity.get(trigger) - previousRatioQty;
					trigger.quantity = trigger.quantity * (int) ratio;

					final int triggerQty = trigger.quantity - previousRatioQty;

					if (!thresholdQtyMatch(triggerQty, prodQty))
					{
						pda.map.clear();
						break;
					}
				}

				pda.setRatio((int) ratio);
			}

			return pda;
		}


		/**
		 * Check if all the products that are part of the Deal Trigger are available in the cart. Example :For Deal Buy 10
		 * cases of Carlton Draught and 8 cases of Miller Range, this method will return true if the cart has products
		 * that added up to 10 cases of Carlton Draught and 8 cases of Miller
		 */
		private boolean isMandatoriesAvailableInCart(final DealWrapper dealWrapper, final CartModel cart)
		{
			for (final DealTrigger dealTrigger : dealWrapper.mandatories)
			{
				if (!availableInCart(cart, dealTrigger, dealWrapper.excludes))
				{
					LOG.debug("In isMandatoriesAvailableInCart().Deal {} - Deal Trigger not available in cart. Trigger is [{}] ",
							dealWrapper.getDealCode(), dealTrigger);
					return false;
				}
			}
			return true;
		}

	}

	/**
	 * Verifies if any one of the optional products that constitute the deal condition is available in the cart. Ex: Buy
	 * 10 cases of X or 5 cases of Y, here if the cart has 10 cases of X, then the condition for Y is not evaluated
	 *
	 * @author joshua.a.antony
	 */
	private class OptionalsDealQualificationStrategy implements DealQualificationStrategy
	{
		@Override
		public DealQualificationStatus checkDealsAvailability(final DealWrapper dealWrapper, final CartModel cart)
		{
			LOG.debug("Deal {} - In OptionalsDealQualificationStrategy.checkDealsAvailability()", dealWrapper.getDealCode());

			return anyOptionalsAvailableInCart(dealWrapper, cart) ? DealQualificationStatus.QUALIFIED
					: DealQualificationStatus.NOT_QUALIFIED;
		}

		@Override
		public PartialDealAvailability checkPartialDealsAvailability(final DealWrapper dealWrapper,
				final List<AbstractOrderEntryModel> cartEntries)
		{
			LOG.debug("Deal {} - In OptionalsDealQualificationStrategy.checkPartialDealsAvailability()", dealWrapper.getDealCode());

			final Map<DealTrigger, List<AbstractOrderEntryModel>> map = new HashMap<>();
			for (final DealTrigger dealTrigger : dealWrapper.optionals)
			{
				final List<AbstractOrderEntryModel> matchingCartEntries = checkPartialDealAvailabilityInCart(cartEntries, dealTrigger,
						dealWrapper.excludes);

				if (CollectionUtils.isNotEmpty(matchingCartEntries))
				{
					map.put(dealTrigger, matchingCartEntries);
				}
			}
			return new PartialDealAvailability(dealWrapper.deal, map);
		}

		/**
		 * Checks if any of the 'optional' products that are part of the deal (trigger), are available in the cart.
		 * Example : 'Buy 10 cases of Carlton Draught or 5 cases of Matilda Bay' deal will satisfy if the cart has either
		 * 5 cases of products in Matilda Bay or 10 case of any products in Carlton Draught range.
		 */
		private boolean anyOptionalsAvailableInCart(final DealWrapper dealWrapper, final CartModel cart)
		{
			for (final DealTrigger dt : dealWrapper.optionals)
			{
				if (availableInCart(cart, dt, dealWrapper.excludes))
				{
					LOG.debug("Deal - {} . In anyOptionalsAvailableInCart(). Found Deal Trigger in Cart {} ",
							dealWrapper.getDealCode(), dt);

					return true;
				}
			}
			LOG.debug("Deal - {} . In anyOptionalsAvailableInCart(). None of the trigger items available in cart",
					dealWrapper.getDealCode());

			return false;
		}

		/*
		 * (non-Javadoc)
		 *
		 * @see com.sabmiller.core.deals.services.DefaultDealConditionService.DealQualificationStrategy#
		 * checkPartialDealsAvailabilityOfAlreadyQualified(com.sabmiller.core.deals.services.DefaultDealConditionService.
		 * DealWrapper, de.hybris.platform.core.model.order.CartModel)
		 */
		@Override
		public PartialDealAvailability checkPartialDealsAvailabilityOfAlreadyQualified(final DealWrapper dealWrapper,
				final CartModel cart)
		{
			final PartialDealAvailability pda = checkPartialDealsAvailability(dealWrapper, cart.getEntries());

			long totalQtyDeal = 0;
			for (final Map.Entry<DealTrigger, List<AbstractOrderEntryModel>> entry : pda.map.entrySet())
			{
				long totalQty = 0;

				for (final AbstractOrderEntryModel orderEntry : entry.getValue())
				{
					totalQty += orderEntry.getQuantity();
				}
				totalQtyDeal += totalQty;
			}

			final List<DealScaleModel> dealScales = new ArrayList<>(pda.deal.getConditionGroup().getDealScales());

			Collections.sort(dealScales, DealScaleComparator.INSTANCE);
			//int scaleRatio = 1;
			int scaleRatio = 0;
			if (dealsService.isManualScaleProportionByEachDeal(pda.deal.getConditionGroup().getDealBenefits()))
			{
				for (final DealScaleModel scale : dealScales)
				{
					if (totalQtyDeal >= scale.getFrom())
					{
						scaleRatio++;
					}
				}

				if (scaleRatio > 1)
				{
					final int previousRatioQty = dealScales.get(scaleRatio - 2).getFrom();
					final Long prodQty = totalQtyDeal - previousRatioQty;

					final int triggerQty = dealScales.get(scaleRatio - 1).getFrom() - previousRatioQty;

					if (!thresholdQtyMatch(triggerQty, prodQty))
					{
						pda.map.clear();
					}

					pda.setRatio(scaleRatio);
					if (dealScales.size() >= scaleRatio)
					{
						pda.scale = dealScales.get(scaleRatio - 1).getFrom();
					}
				}
				else if (scaleRatio == 1)
				{
					final int previousRatioQty = 0;
					final Long prodQty = totalQtyDeal - previousRatioQty;

					final int triggerQty = dealScales.get(scaleRatio - 1).getFrom() - previousRatioQty;

					if (!thresholdQtyMatch(triggerQty, prodQty))
					{
						pda.map.clear();
					}

					pda.setRatio(scaleRatio);
					if (dealScales.size() >= scaleRatio)
					{
						pda.scale = dealScales.get(scaleRatio - 1).getFrom();
					}
				}
			}
			else
			{
				// if there is only one deal scale, and proportional flag is true.
				if (isProportionalMode(pda.deal.getConditionGroup()))
				{
					final int bonusRatioQty = pda.deal.getConditionGroup().getDealScales().get(0).getFrom();
					scaleRatio = ((int) totalQtyDeal / bonusRatioQty) + 1;
					final int previousRatioQty = (scaleRatio - 1) * bonusRatioQty;
					final int prodQty = (int) totalQtyDeal - previousRatioQty;

					if ((prodQty > 0) && thresholdQtyMatch(bonusRatioQty, prodQty))
					{
						pda.setRatio(scaleRatio);
						pda.scale = previousRatioQty + bonusRatioQty;
					}
					else
					{
						// exactly meet a bonus level, no need for partially notify.
						pda.map.clear();
					}
				}

			}
			return pda;
		}

		private boolean isProportionalMode(final DealConditionGroupModel dealConditionGroup)
		{
			final List<DealScaleModel> dealScales = dealConditionGroup.getDealScales();
			final List<AbstractDealBenefitModel> benefits = dealConditionGroup.getDealBenefits();
			boolean proportional = false;
			if (CollectionUtils.isNotEmpty(dealScales) && (dealScales.size() == 1) && (benefits.size() >= 1))
			{
				if (benefits.get(0) instanceof FreeGoodsDealBenefitModel)
				{
					proportional = benefits.get(0).getProportionalFreeGood();
				}
				else
				{
					proportional = benefits.get(0).getProportionalAmount();
				}
			}
			return proportional;
		}
	}

	private class NotQualifiedDealQualificationStrategy implements DealQualificationStrategy
	{

		@Override
		public DealQualificationStatus checkDealsAvailability(final DealWrapper dealWrapper, final CartModel cart)
		{
			return DealQualificationStatus.NOT_QUALIFIED;
		}

		@Override
		public PartialDealAvailability checkPartialDealsAvailability(final DealWrapper dealWrapper,
				final List<AbstractOrderEntryModel> cartEntries)
		{
			return new PartialDealAvailability(dealWrapper.deal, Collections.emptyMap());
		}

		/*
		 * (non-Javadoc)
		 *
		 * @see com.sabmiller.core.deals.services.DefaultDealConditionService.DealQualificationStrategy#
		 * checkPartialDealsAvailabilityOfAlreadyQualified(com.sabmiller.core.deals.services.DefaultDealConditionService.
		 * DealWrapper, de.hybris.platform.core.model.order.CartModel)
		 */
		@Override
		public PartialDealAvailability checkPartialDealsAvailabilityOfAlreadyQualified(final DealWrapper dealWrapper,
				final CartModel cart)
		{
			return null;
		}
	}

	/**
	 * Bucket to hold Green (Fully Qualified), Amber (Partially Qualified) and Red (Not Qualified) Deals.
	 *
	 * @author joshua.a.antony
	 */
	class DealBucket
	{
		protected List<DealModel> greenDealsBucket = new ArrayList<>();
		protected List<DealModel> amberDealsBucket = new ArrayList<>();
		protected List<DealModel> redDealsBucket = new ArrayList<>();

		Map<DealQualificationStatus, List<DealModel>> map = new HashMap<DefaultDealConditionService.DealQualificationStatus, List<DealModel>>();

		public DealBucket()
		{
			map.put(DealQualificationStatus.QUALIFIED, greenDealsBucket);
			map.put(DealQualificationStatus.PARTIALLY_QUALIFIED, amberDealsBucket);
			map.put(DealQualificationStatus.NOT_QUALIFIED, redDealsBucket);
		}

		public void add(final DealModel deal, final DealQualificationStatus availabilityStatus)
		{
			LOG.debug("Availability status of Deal {} is {} ", deal.getCode(), availabilityStatus);
			map.get(availabilityStatus).add(deal);
		}
	}

	enum DealQualificationStatus
	{
		QUALIFIED, PARTIALLY_QUALIFIED, NOT_QUALIFIED
	};


	/**
	 * Returns all the entries that are not yet taken by any deals
	 */
	private List<AbstractOrderEntryModel> getNonConsumedCartEntries(final CartModel cart)
	{
		final List<AbstractOrderEntryModel> availableEntries = new ArrayList<>();
		final List<AbstractOrderEntryModel> consumedEntries = new ArrayList<>();
		for (final CartDealConditionModel cdc : cart.getComplexDealConditions())
		{
			if (cdc.getDeal() != null)
			{
				final DealWrapper dealWrapper = new DealWrapper(cdc.getDeal());
				for (final DealTrigger dealTrigger : dealWrapper.getDealTriggers())
				{
					if (dealTrigger.isProduct())
					{
						consumedEntries.add(lookupProductInCart(cart, dealTrigger.productCode, dealWrapper.excludes));
					}
					else if (dealTrigger.isHierarchial())
					{
						consumedEntries.addAll(lookupProductHierarcyInCart(cart, dealTrigger.productHierarchy, dealWrapper.excludes));
					}
				}
			}
		}

		for (final AbstractOrderEntryModel cartEntry : cart.getEntries())
		{
			if (BooleanUtils.isNotTrue(cartEntry.getIsFreeGood()) && !consumedEntries.contains(cartEntry))
			{
				availableEntries.add(cartEntry);
			}
		}
		return availableEntries;
	}

	private List<DealModel> getNonConsumedDeals(final List<DealModel> deals, final CartModel cart)
	{
		final List<DealModel> nonQualfiedDeals = new ArrayList<>();
		for (final DealModel deal : deals)
		{
			if (!dealAlreadyQualified(deal, cart))
			{
				nonQualfiedDeals.add(deal);
			}
		}
		return nonQualfiedDeals;
	}

	private List<DealModel> getQualifiedScaleDeal(final CartModel cart)
	{
		final List<DealModel> deals = new ArrayList<>();
		final List<DealModel> qualifiedProportionalDeal = getQualifiedProportionalDeal(cart);
		for (final CartDealConditionModel qualifiedDeal : CollectionUtils.emptyIfNull(cart.getComplexDealConditions()))
		{
			if (qualifiedDeal != null && qualifiedDeal.getDeal() != null
					&& !qualifiedProportionalDeal.contains(qualifiedDeal.getDeal())
					&& qualifiedDeal.getDeal().getConditionGroup() != null
					&& qualifiedDeal.getDeal().getConditionGroup().getDealScales() != null
					&& qualifiedDeal.getDeal().getConditionGroup().getDealScales().size() > 1)
			{
				deals.add(qualifiedDeal.getDeal());
			}
		}
		return deals;
	}

	private List<DealModel> getQualifiedProportionalDeal(final CartModel cart)
	{
		final List<DealModel> deals = new ArrayList<>();
		for (final CartDealConditionModel qualifiedDeal : CollectionUtils.emptyIfNull(cart.getComplexDealConditions()))
		{
			if (qualifiedDeal != null && qualifiedDeal.getDeal() != null && qualifiedDeal.getDeal().getConditionGroup() != null
					&& CollectionUtils.isNotEmpty(qualifiedDeal.getDeal().getConditionGroup().getDealBenefits()))
			{
				if (dealsService.isManualScaleProportionByEachDeal(qualifiedDeal.getDeal().getConditionGroup().getDealBenefits()))
				{
					deals.add(qualifiedDeal.getDeal());
				}
				else
				{
					for (final AbstractDealBenefitModel benefit : qualifiedDeal.getDeal().getConditionGroup().getDealBenefits())
					{
						if (BooleanUtils.isTrue(benefit.getProportionalFreeGood()))
						{
							deals.add(qualifiedDeal.getDeal());
							break;
						}
					}
				}
			}
		}
		return deals;
	}

	private boolean dealAlreadyQualified(final DealModel deal, final CartModel cart)
	{
		for (final CartDealConditionModel qualifiedDeal : CollectionUtils.emptyIfNull(cart.getComplexDealConditions()))
		{
			if (deal != null && qualifiedDeal != null && qualifiedDeal.getDeal() != null)
			{
				if (StringUtils.equals(deal.getCode(), qualifiedDeal.getDeal().getCode()))
				{
					return true;
				}
			}
		}
		return false;
	}

	private boolean thresholdQtyMatch(final long requiredQty, final long actualQty)
	{
		return actualQty * (100 / getDealsThresholdPercent()) >= requiredQty;
	}

	/**
	 * If Deal Threshold not defined in configuration, default it to 100 which means that the Deal must be fully
	 * satisfied (as opposed to partial)
	 */
	private double getDealsThresholdPercent()
	{
		final Double threshold = sabmConfigurationService.getPartialDealThreshold();
		return threshold == null ? 80d : threshold;
	}

	private class PartialDealAvailability
	{
		private final Map<DealTrigger, List<AbstractOrderEntryModel>> map;
		private final DealModel deal;
		int scale;
		private int ratio;

		public PartialDealAvailability(final DealModel deal, final Map<DealTrigger, List<AbstractOrderEntryModel>> map)
		{
			this.deal = deal;
			this.map = map;
		}

		public PartialDealAvailability(final DealModel deal, final Map<DealTrigger, List<AbstractOrderEntryModel>> map,
				final int scale)
		{
			this(deal, map);
			this.scale = scale;
		}

		public int getRatio()
		{
			return ratio;
		}

		public void setRatio(final int ratio)
		{
			this.ratio = ratio;
		}

		public boolean isAvailable()
		{
			return map != null && !map.isEmpty();
		}

		public Set<DealTrigger> getDealTriggers()
		{
			return map.keySet();
		}

		public List<AbstractOrderEntryModel> getEntriesMatchingTrigger(final DealTrigger dealTrigger)
		{
			return map.get(dealTrigger);
		}

		@Override
		public String toString()
		{
			return "isAvailable : " + isAvailable() + " , dealTriggers : " + getDealTriggers() + " , entries : " + map.values();
		}
	}

	public void setProductService(final SabmProductService productService)
	{
		this.productService = productService;
	}

	public void setSabmConfigurationService(final SabmConfigurationService sabmConfigurationService)
	{
		this.sabmConfigurationService = sabmConfigurationService;
	}

	/**
	 * To get ratio for scale deals
	 */
	private double getRatioForScaleDeal(final Map<DealTrigger, List<AbstractOrderEntryModel>> map, final int eachScale)
	{

		double ratio = 0;
		boolean first = true;
		final Map<DealTrigger, Long> mapQuantity = new HashMap<>();

		//long totalQtyDeal = 0;
		for (final Map.Entry<DealTrigger, List<AbstractOrderEntryModel>> entry : map.entrySet())
		{
			final DealTrigger dealTrigger = entry.getKey();
			final List<AbstractOrderEntryModel> orderEntries = entry.getValue();

			long totalQty = 0;

			for (final AbstractOrderEntryModel orderEntry : orderEntries)
			{
				totalQty += orderEntry.getQuantity();
			}
			if (totalQty >= eachScale)
			{
				return ratio;
			}
			mapQuantity.put(dealTrigger, totalQty);
			//totalQtyDeal += totalQty;
			if (first)
			{
				ratio = Math.ceil((double) totalQty / dealTrigger.quantity);
				first = false;
				continue;
			}

			final double ceilRatio = Math.ceil((double) totalQty / dealTrigger.quantity);
			if (ceilRatio < ratio)
			{
				ratio = ceilRatio;
			}
		}
		if (ratio > 1)
		{
			for (final DealTrigger trigger : map.keySet())
			{
				final int previousRatioQty = trigger.quantity * (int) (ratio - 1);
				final Long prodQty = mapQuantity.get(trigger) - previousRatioQty;
				trigger.quantity = trigger.quantity * (int) ratio;

				final int triggerQty = trigger.quantity - previousRatioQty;

				if (!thresholdQtyMatch(triggerQty, prodQty))
				{
					map.clear();
					break;
				}
			}
			//pda.setRatio((int) ratio);
		}
		return ratio;
	}

}
