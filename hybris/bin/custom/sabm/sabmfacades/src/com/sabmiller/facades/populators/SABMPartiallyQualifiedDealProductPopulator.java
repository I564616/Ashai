package com.sabmiller.facades.populators;

import de.hybris.platform.converters.Populator;
import de.hybris.platform.core.model.product.ProductModel;
import de.hybris.platform.servicelayer.dto.converter.ConversionException;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.Stack;

import org.apache.commons.collections4.CollectionUtils;
import org.apache.commons.lang3.BooleanUtils;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.util.Assert;

import com.sabmiller.core.deals.services.response.PartialDealQualificationResponse.PartialAvailability;
import com.sabmiller.core.model.AbstractDealConditionModel;
import com.sabmiller.core.model.ComplexDealConditionModel;
import com.sabmiller.core.model.DealConditionGroupModel;
import com.sabmiller.core.model.DealModel;
import com.sabmiller.core.model.ProductDealConditionModel;
import com.sabmiller.core.model.SABMAlcoholVariantProductMaterialModel;
import com.sabmiller.facades.deal.data.DealBaseProductJson;
import com.sabmiller.facades.deal.data.DealJson;
import com.sabmiller.facades.deal.data.DealRangeJson;


/**
 * Modifies the qty selector value of the constructed Deal JSON for the PQD upsell popup in the cart page.
 *
 * @author wei.yang.ng@accenture.com
 */
public class SABMPartiallyQualifiedDealProductPopulator extends SABMAbstractDealPopulator
		implements Populator<PartialAvailability, DealJson>
{
	private static final Logger LOG = LoggerFactory.getLogger(SABMPartiallyQualifiedDealProductPopulator.class);

	/**
	 * Alter the quantity selector to reflect the amount required by the user to add to the cart. Logic as follows: 1.
	 * Iterate through the pre-conditions of the deal (assuming that there is only 1 range). 2. For each base product
	 * JSON, extract the product code. 3. Iterate through the deal conditions of the deal model. 4. Match the product
	 * code from the JSON to the deal condition, if code matches, then that is the deal condition we want to extract from
	 * the Availability map, which should yield us the required qty for the particular JSON product. - This only applies
	 * to the Product Deal Condition Model. 5. If deal condition is complex then set qty selector to 0. The min qty needs
	 * to be adjusted to reflect the required number of items that need to be added to the cart before add to cart can be
	 * clicked.
	 *
	 * @param source
	 * @param target
	 * @throws ConversionException
	 */
	@Override
	public void populate(final PartialAvailability source, final DealJson target) throws ConversionException
	{
		Assert.notNull(source, "Parameter source cannot be null.");
		Assert.notNull(target, "Parameter target cannot be null.");
		LOG.info("Setting up the product display for the PQD...");

		final DealModel pqd = source.getDeal();

		LOG.info(String.format("Total number of items in cart is [%d]", source.getTotalAvailableQty()));

		final List<DealRangeJson> dealRanges = target.getRanges();

		final Stack<ComplexDealConditionModel> complexDealsStack = prepareDealConditionStack(pqd);

		for (final DealRangeJson dealRange : dealRanges)
		{
			populatePQDProductLevelJSONAttributes(source, pqd, dealRange);

			if (dealRanges.size() == 1)
			{
				for (int i = 0; i < complexDealsStack.size(); i++)
				{
					populatePQDRangeLevelJSONAttributes(source, complexDealsStack, dealRange);
					i--;
				}
			}
			else
			{
				populatePQDRangeLevelJSONAttributes(source, complexDealsStack, dealRange);
			}
		}

		recalculateDealRangesQty(dealRanges);
	}

	/**
	 * Recalculate deal ranges qty.
	 *
	 * @param dealRanges
	 *           the deal ranges
	 */
	protected void recalculateDealRangesQty(final List<DealRangeJson> dealRanges)
	{
		for (final DealRangeJson range : dealRanges)
		{
			int minQty = 0;
			for (final DealBaseProductJson product : range.getBaseProducts())
			{
				if (product.getQty() != null)
				{
					minQty += product.getQty();
				}
			}

			if (range.getMinQty() == null || range.getMinQty() < minQty)
			{
				range.setMinQty(minQty);
			}
		}
	}

	/**
	 * Prepares a stack of deal conditions which can be popped.
	 *
	 * @param pqd
	 *           list of deal conditions to convert into a stack
	 * @return a stack of deal conditions.
	 */
	private Stack<ComplexDealConditionModel> prepareDealConditionStack(final DealModel pqd)
	{
		final Stack<ComplexDealConditionModel> complexDealsStack = new Stack<>();
		final ArrayList<AbstractDealConditionModel> reversedDealsList = new ArrayList<>(
				pqd.getConditionGroup().getDealConditions());
		Collections.reverse(reversedDealsList);
		for (final AbstractDealConditionModel dealCondition : reversedDealsList)
		{
			if (dealCondition instanceof ComplexDealConditionModel)
			{
				final List<SABMAlcoholVariantProductMaterialModel> materials = getProductService()
						.getProductByHierarchyFilterExcluded((ComplexDealConditionModel) dealCondition);
				final Set<ProductModel> baseProducts = new HashSet<>();

				for (final SABMAlcoholVariantProductMaterialModel material : materials)
				{
					baseProducts.add(material.getBaseProduct());

					if (baseProducts.size() > 1)
					{
						complexDealsStack.push((ComplexDealConditionModel) dealCondition);
						break;
					}

				}
			}
		}

		return complexDealsStack;
	}


	/**
	 * Populates the Product level JSON attributes.
	 *
	 * @param partialAvailability
	 *           the wrapper object holding the deal's product partial availability information.
	 * @param pqd
	 *           the partially qualified deal.
	 * @param dealRange
	 *           the deal range JSON containing the product level JSON to modify.
	 */
	private void populatePQDProductLevelJSONAttributes(final PartialAvailability partialAvailability, final DealModel pqd,
			final DealRangeJson dealRange)
	{
		final List<DealBaseProductJson> dealBaseProducts = dealRange.getBaseProducts();
		final List<Integer> updatesQty = new ArrayList<>();
		for (final DealBaseProductJson dealBaseProductJson : dealBaseProducts)
		{
			//get all the condition group from the partially qualified deal.
			final DealConditionGroupModel conditionGroup = pqd.getConditionGroup();
			// now get all the deal conditions from the group.
			final List<AbstractDealConditionModel> dealConditions = conditionGroup.getDealConditions();

			for (final AbstractDealConditionModel dealCondition : dealConditions)
			{
				if (BooleanUtils.isTrue(dealCondition.getExclude()))
				{
					continue;
				}

				//if the deal condition is simple then just set the required qty to be added to the cart in qty selector.
				if (dealCondition instanceof ProductDealConditionModel && StringUtils.equals(dealBaseProductJson.getProductCode(),
						((ProductDealConditionModel) dealCondition).getProductCode()))
				{
					if (BooleanUtils.isTrue(dealCondition.getMandatory()) || dealConditions.size() == 1)
					{
						final long requiredQty = partialAvailability.getRequiredQtyWithGivenDealCondition(dealCondition);
						updatesQty.add(dealBaseProductJson.getQty() - Long.valueOf(requiredQty).intValue());
						dealBaseProductJson.setQty(Long.valueOf(requiredQty).intValue() > 0 ? Long.valueOf(requiredQty).intValue() : 0);
					}
				}
				else if (dealCondition instanceof ComplexDealConditionModel)
				{
					final List<SABMAlcoholVariantProductMaterialModel> materials = getProductService()
							.getProductByHierarchyFilterExcluded((ComplexDealConditionModel) dealCondition);

					if (!productBelongToMaterials(dealBaseProductJson, materials))
					{
						continue;
					}

					final Set<ProductModel> baseProducts = new HashSet<>();

					for (final SABMAlcoholVariantProductMaterialModel material : materials)
					{
						baseProducts.add(material.getBaseProduct());
					}

					if (baseProducts.size() > 1)
					{
						dealBaseProductJson.setQty(0);
					}
					else
					{
						if (BooleanUtils.isTrue(dealCondition.getMandatory()) || dealConditions.size() == 1)
						{
							final long requiredQty = partialAvailability.getRequiredQtyWithGivenDealCondition(dealCondition);
							updatesQty.add(dealBaseProductJson.getQty() - Long.valueOf(requiredQty).intValue());
							dealBaseProductJson.setQty(Long.valueOf(requiredQty).intValue() > 0 ? Long.valueOf(requiredQty).intValue() : 0);
						}
						else if (BooleanUtils.isNotTrue(dealCondition.getMandatory()))
						{
							updatesQty.add((int) partialAvailability.getAvailableQtyWithGivenDealCondition(dealCondition));
						}
					}
				}
			}
		}

		for (final Integer qty : updatesQty)
		{
			if (dealRange.getMinQty() - qty >= 0)
			{
				dealRange.setMinQty(dealRange.getMinQty() - qty);
			}
		}
	}

	/**
	 * Product belong to materials.
	 *
	 * @param productJson
	 *           the product json
	 * @param materials
	 *           the materials
	 * @return true, if successful
	 */
	protected boolean productBelongToMaterials(final DealBaseProductJson productJson, final List<? extends ProductModel> materials)
	{
		if (CollectionUtils.isEmpty(materials) || productJson == null || StringUtils.isEmpty(productJson.getProductCode()))
		{
			return false;
		}

		for (final ProductModel productModel : materials)
		{
			if (StringUtils.equals(productModel.getCode(), productJson.getProductCode()))
			{
				return true;
			}
		}

		return false;
	}

	/**
	 * Populates the Range level JSON attributes. This is where we pop the stack so that the information is written once
	 * to the range level JSON.
	 *
	 * @param partialAvailability
	 *           the wrapper object holding the deal's product partial availability information.
	 * @param dealConditionStack
	 *           the stack of deal conditions used to retrieve the partial availability data.
	 * @param dealRange
	 *           the deal range JSON to modify.
	 */
	private void populatePQDRangeLevelJSONAttributes(final PartialAvailability partialAvailability,
			final Stack<ComplexDealConditionModel> dealConditionStack, final DealRangeJson dealRange)
	{
		if (dealConditionStack.isEmpty())
		{
			return;
		}
		final ComplexDealConditionModel dealCondition = dealConditionStack.pop();
		final long availableQty = partialAvailability.getAvailableQtyWithGivenDealCondition(dealCondition);
		if (StringUtils.isNotEmpty(dealCondition.getBrand()))
		{
			dealRange.setTitle(getComplexBrand(dealCondition.getBrand()));
		}
		dealRange.setMinQty(dealRange.getMinQty() - (int) availableQty);
	}

}
