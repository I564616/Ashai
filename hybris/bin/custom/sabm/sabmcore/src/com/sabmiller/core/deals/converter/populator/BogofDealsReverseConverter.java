/**
 *
 */
package com.sabmiller.core.deals.converter.populator;

import de.hybris.platform.converters.Populator;
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

public class BogofDealsReverseConverter implements Converter<DealsResponse, List<DealModel>>
{
	private static final Logger LOG = LoggerFactory.getLogger(BogofDealsReverseConverter.class);

	@Resource(name = "modelService")
	private ModelService modelService;

	@Resource(name = "dealsService")
	private DealsService dealsService;

	@Resource(name = "bogofProductDealConditionReversePopulator")
	Populator<DealItem, DealModel> bogofProductDealConditionReversePopulator;

	@Resource(name = "bogofMinQtyDealConditionReversePopulator")
	Populator<DealItem, DealModel> bogofMinQtyDealConditionReversePopulator;

	@Resource(name = "bogofDealBenefitReversePopulator")
	Populator<DealItem, DealModel> bogofDealBenefitReversePopulator;

	@Resource(name = "productService")
	private SabmProductService productService;

	@Resource(name = "catalogVersionDeterminationStrategy")
	private CatalogVersionDeterminationStrategy catalogVersionDeterminationStrategy;


	@Override
	public List<DealModel> convert(final DealsResponse bogofResponse)
	{
		return convert(bogofResponse, new ArrayList<DealModel>());
	}


	@Override
	public List<DealModel> convert(final DealsResponse bogofResponse, final List<DealModel> target)
	{
		final String salesOrg = bogofResponse.getSalesOrganisation();
		final String customerId = bogofResponse.getCustomer();

		final Map<Integer, DealModel> dealCodeDealMap = new HashMap<Integer, DealModel>();
		for (final DealItem bogofItem : ListUtils.emptyIfNull(bogofResponse.getItems()))
		{
			try
			{
				//Validate if the product exist in Hybris
				final SABMAlcoholVariantProductMaterialModel product = (SABMAlcoholVariantProductMaterialModel) productService
						.getProductForCode(catalogVersionDeterminationStrategy.onlineCatalogVersion(), bogofItem.getMaterial());
				if (product == null)
				{
					throw new ModelNotFoundException("Product " + bogofItem.getMaterial() + " not found! ");
				}

				final String ean = product.getBaseProduct().getCode();

				final DealCodeGeneratorParam param = new DealCodeGeneratorParam.Builder(customerId, salesOrg).material(ean)
						.validFrom(SabmDateUtils.toDate(bogofItem.getValidFrom()))
						.validTo(SabmDateUtils.toDate(bogofItem.getValidTo())).minQty(bogofItem.getMinimumQuantity())
						.uom(bogofItem.getUnitOfMeasure()).dealType(DealTypeEnum.BOGOF).build();

				final int dealCode = dealsService.generateDealsCode(param);

				LOG.debug("dealCode : {}, material : {}, uom : {}, qty : {}", dealCode, bogofItem.getMaterial(),
						bogofItem.getUnitOfMeasure(), bogofItem.getMinimumQuantity());

				final DealModel dealModel = dealCodeDealMap.containsKey(dealCode) ? dealCodeDealMap.get(dealCode)
						: findOrCreateDeal(String.valueOf(dealCode));

				if (modelService.isNew(dealModel))
				{
					populateCoreInfo(bogofItem, dealModel, dealCode);
					populateDealConditions(bogofItem, dealModel);
				}
				populateDealBenefits(bogofItem, dealModel);
				target.add(dealModel);
				dealCodeDealMap.put(dealCode, dealModel);
			}
			catch (final Exception e)
			{
				LOG.error(
						"Error occured while creating BOGOF Deal for product :" + bogofItem.getMaterial() + " , qty :"
								+ bogofItem.getMinimumQuantity() + " , uom:" + bogofItem.getUnitOfMeasure(), e);
			}
		}
		return target;
	}

	private void populateCoreInfo(final DealItem source, final DealModel target, final int dealCode)
	{
		LOG.debug("Populating the core information for deal " + dealCode + ". Source is "
				+ ReflectionToStringBuilder.toString(source));

		target.setCode(String.valueOf(dealCode));
		target.setValidFrom(source.getValidFrom().toGregorianCalendar().getTime());
		target.setValidTo(source.getValidTo().toGregorianCalendar().getTime());
		target.setDealType(DealTypeEnum.BOGOF);
	}


	/**
	 * Creates deal condition. It should be noted that there are no updates on Deal Condition. Deal Conditions are always
	 * created, never updated. If material is empty, {@link BogofProductDealConditionReversePopulator} populator is used
	 * to created ProductDealConditon item type, else {@link BogofMinQtyDealConditionReversePopulator} populator is used
	 * to create the MinQtyDealCondition item type
	 */
	private void populateDealConditions(final DealItem source, final DealModel target)
	{
		LOG.debug("Populatingthe deal condition for  " + target + ". Source is " + ReflectionToStringBuilder.toString(source));

		final DealConditionGroupModel dealConditionGroup = modelService.create(DealConditionGroupModel.class);
		target.setConditionGroup(dealConditionGroup);

		if (StringUtils.isNotBlank(source.getMaterial()))
		{
			bogofProductDealConditionReversePopulator.populate(source, target);
		}
		else
		{
			bogofMinQtyDealConditionReversePopulator.populate(source, target);
		}
	}


	/**
	 * Populate the discount deal benefits. Uses {@link DiscountDealBenefitReversePopulator} to create/update the
	 * benefits (depending on if the benefit already exist or not)
	 */
	private void populateDealBenefits(final DealItem source, final DealModel target)
	{
		bogofDealBenefitReversePopulator.populate(source, target);
	}

	protected DealModel findOrCreateDeal(final String dealCode)
	{
		final DealModel dealModel = dealsService.getDeal(String.valueOf(dealCode));
		return (dealModel != null) ? dealModel : modelService.<DealModel> create(DealModel.class);
	}
}
