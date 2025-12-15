/**
 *
 */
package com.sabmiller.core.deals.converter.populator;

import de.hybris.platform.converters.Populator;
import de.hybris.platform.servicelayer.dto.converter.ConversionException;
import de.hybris.platform.servicelayer.dto.converter.Converter;
import de.hybris.platform.servicelayer.exceptions.ModelNotFoundException;
import de.hybris.platform.servicelayer.model.ModelService;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import jakarta.annotation.Resource;

import org.apache.commons.collections4.ListUtils;
import org.apache.commons.lang3.StringUtils;
import org.apache.commons.lang3.builder.ReflectionToStringBuilder;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.sabmiller.core.deals.services.DealsService;
import com.sabmiller.core.deals.vo.DealCodeGeneratorParam;
import com.sabmiller.core.deals.vo.DealsResponse;
import com.sabmiller.core.deals.vo.DealsResponse.DealItem;
import com.sabmiller.core.enums.DealTypeEnum;
import com.sabmiller.core.model.DealConditionGroupModel;
import com.sabmiller.core.model.DealModel;
import com.sabmiller.core.model.SABMAlcoholVariantProductMaterialModel;
import com.sabmiller.core.product.SabmProductService;
import com.sabmiller.core.product.strategy.CatalogVersionDeterminationStrategy;
import com.sabmiller.core.util.SabmDateUtils;


/**
 * Convert SAP Discount deal response to Hybris Model and persist the same. If deal does not exist in Hybris, a new one
 * is created. For any existing deals, the Deal Benefits are updated (as there is no need to update the Deal Conditon :
 * since its the deal condition that forms the PK along with from customer, sales org and other properties)
 *
 * @author joshua.a.antony
 */

public class DiscountDealsReverseConverter implements Converter<DealsResponse, List<DealModel>>
{
	private static final Logger LOG = LoggerFactory.getLogger(DiscountDealsReverseConverter.class);

	@Resource(name = "modelService")
	private ModelService modelService;

	@Resource(name = "dealsService")
	private DealsService dealsService;

	@Resource(name = "discountProductDealConditionReversePopulator")
	Populator<DealItem, DealModel> discountProductDealConditionReversePopulator;

	@Resource(name = "discountMinQtyDealConditionReversePopulator")
	Populator<DealItem, DealModel> discountMinQtyDealConditionReversePopulator;

	@Resource(name = "discountDealBenefitReversePopulator")
	Populator<DealItem, DealModel> discountDealBenefitReversePopulator;

	@Resource(name = "productService")
	private SabmProductService productService;

	@Resource(name = "catalogVersionDeterminationStrategy")
	private CatalogVersionDeterminationStrategy catalogVersionDeterminationStrategy;

	@Override
	public List<DealModel> convert(final DealsResponse discountResponse) throws ConversionException
	{
		return convert(discountResponse, new ArrayList<DealModel>());
	}


	@Override
	public List<DealModel> convert(final DealsResponse discountResponse, final List<DealModel> target) throws ConversionException
	{
		final String salesOrg = discountResponse.getSalesOrganisation();
		final String customerId = discountResponse.getCustomer();

		final Map<Integer, DealModel> dealCodeDealMap = new HashMap<Integer, DealModel>();
		for (final DealItem discountItem : ListUtils.emptyIfNull(discountResponse.getItems()))
		{
			try
			{
				//Validate if the product exist in Hybris
				final SABMAlcoholVariantProductMaterialModel product = (SABMAlcoholVariantProductMaterialModel) productService
						.getProductForCode(catalogVersionDeterminationStrategy.onlineCatalogVersion(), discountItem.getMaterial());
				if (product == null)
				{
					throw new ModelNotFoundException("Product " + discountItem.getMaterial() + " not found! ");
				}

				final String ean = product.getBaseProduct().getCode();

				final DealCodeGeneratorParam param = new DealCodeGeneratorParam.Builder(customerId, salesOrg).material(ean)
						.validFrom(SabmDateUtils.toDate(discountItem.getValidFrom()))
						.validTo(SabmDateUtils.toDate(discountItem.getValidTo())).minQty(discountItem.getMinimumQuantity())
						.uom(discountItem.getUnitOfMeasure()).dealType(getDealType()).build();

				final int dealCode = dealsService.generateDealsCode(param);

				LOG.debug("dealCode : {}, material : {}, uom : {}, qty : {}", dealCode, discountItem.getMaterial(),
						discountItem.getUnitOfMeasure(), discountItem.getMinimumQuantity());

				final DealModel dealModel = dealCodeDealMap.containsKey(dealCode) ? dealCodeDealMap.get(dealCode)
						: findOrCreateDeal(String.valueOf(dealCode));

				if (modelService.isNew(dealModel))
				{
					populateCoreInfo(discountItem, dealModel, dealCode);
					populateDealConditions(discountItem, dealModel);
				}
				populateDealBenefits(discountItem, dealModel);

				populateAdditionalDetails(discountItem, dealModel);

				target.add(dealModel);

				dealCodeDealMap.put(dealCode, dealModel);
			}
			catch (final Exception e)
			{
				LOG.error("Error occured while creating Discount Deal for product :" + discountItem.getMaterial() + " , qty :"
						+ discountItem.getMinimumQuantity() + " , uom:" + discountItem.getUnitOfMeasure(), e);
			}
		}
		return target;
	}

	/**
	 * Populate the core information without which the deal cannot exist.
	 */
	protected void populateCoreInfo(final DealItem discountItem, final DealModel target, final int dealCode)
	{
		LOG.debug("Populating the core information for deal " + dealCode + ". Source is "
				+ ReflectionToStringBuilder.toString(discountItem));

		target.setCode(String.valueOf(dealCode));
		target.setValidFrom(discountItem.getValidFrom().toGregorianCalendar().getTime());
		target.setValidTo(discountItem.getValidTo().toGregorianCalendar().getTime());
		target.setDealType(getDealType());
	}


	/**
	 * Creates deal condition. It should be noted that there are no updates on Deal Condition. Deal Conditions are always
	 * created, never updated. If material is empty, {@link DiscountProductDealConditionsReversePopulator} populator is
	 * used to created ProductDealConditon item type, else {@link DiscountMinQtyDealConditionReversePopulator} populator
	 * is used to create the MinQtyDealCondition item type
	 */
	private void populateDealConditions(final DealItem discountItem, final DealModel target)
	{
		LOG.debug("Populatingthe deal condition for  " + target + ". Source is " + ReflectionToStringBuilder.toString(discountItem));

		final DealConditionGroupModel dealConditionGroup = modelService.create(DealConditionGroupModel.class);
		target.setConditionGroup(dealConditionGroup);

		if (StringUtils.isNotBlank(discountItem.getMaterial()))
		{
			discountProductDealConditionReversePopulator.populate(discountItem, target);
		}
		else
		{
			discountMinQtyDealConditionReversePopulator.populate(discountItem, target);
		}
	}


	/**
	 * Populate the discount deal benefits. Uses {@link DiscountDealBenefitReversePopulator} to create/update the
	 * benefits (depending on if the benefit already exist or not)
	 */
	private void populateDealBenefits(final DealItem discountItem, final DealModel target)
	{
		discountDealBenefitReversePopulator.populate(discountItem, target);
	}

	protected void populateAdditionalDetails(final DealItem discountItem, final DealModel target)
	{
		//Empty Implementation. Sub class can hook into this implementation
	}

	protected DealModel findOrCreateDeal(final String dealCode)
	{
		final DealModel dealModel = dealsService.getDeal(String.valueOf(dealCode));
		return (dealModel != null) ? dealModel : modelService.<DealModel> create(DealModel.class);
	}

	protected DealTypeEnum getDealType()
	{
		return DealTypeEnum.DISCOUNT;
	}
}
