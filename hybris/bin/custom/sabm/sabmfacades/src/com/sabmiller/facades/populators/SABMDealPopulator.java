/**
 *
 */
package com.sabmiller.facades.populators;

import de.hybris.platform.commercefacades.product.ProductFacade;
import de.hybris.platform.commercefacades.product.ProductOption;
import de.hybris.platform.commercefacades.product.data.ProductData;
import de.hybris.platform.converters.Populator;
import de.hybris.platform.core.model.product.ProductModel;
import de.hybris.platform.core.model.product.UnitModel;
import de.hybris.platform.product.ProductService;
import de.hybris.platform.servicelayer.dto.converter.ConversionException;
import de.hybris.platform.servicelayer.exceptions.AmbiguousIdentifierException;
import de.hybris.platform.servicelayer.exceptions.UnknownIdentifierException;
import de.hybris.platform.variants.model.VariantProductModel;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import org.apache.commons.collections4.CollectionUtils;
import org.apache.commons.lang3.time.DateFormatUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.util.Assert;

import com.sabmiller.core.model.AbstractDealBenefitModel;
import com.sabmiller.core.model.AbstractDealConditionModel;
import com.sabmiller.core.model.DealConditionGroupModel;
import com.sabmiller.core.model.DealModel;
import com.sabmiller.core.model.DiscountDealBenefitModel;
import com.sabmiller.core.model.FreeGoodsDealBenefitModel;
import com.sabmiller.core.model.MinQtyDealConditionModel;
import com.sabmiller.core.model.ProductDealConditionModel;
import com.sabmiller.core.model.SABMAlcoholVariantProductEANModel;
import com.sabmiller.facades.deal.data.DealBenefitData;
import com.sabmiller.facades.deal.data.DealConditionData;
import com.sabmiller.facades.deal.data.DealConditionGroupData;
import com.sabmiller.facades.deal.data.DealData;
import com.sabmiller.facades.product.data.UomData;


/**
 * Convert the DealModel to DealData
 *
 * @author xiaowu.a.zhang
 * @data 2015-12-04
 */
public class SABMDealPopulator implements Populator<DealModel, DealData>
{

	private ProductService productService;

	private SABMAlcoholProductPopulator sabmAlcoholProductPopulator;

	private ProductFacade productFacade;

	public static final String DATE_SAFE_FORMAT = "dd/MM/yyyy";

	/** The Constant LOG. */
	private static final Logger LOG = LoggerFactory.getLogger(SABMDealPopulator.class);

	private static final String ConditionType_PRODUCTCONDITION = "PRODUCTCONDITION";
	private static final String ConditionType_MINQTYCONDITION = "MINQTYCONDITION";
	private static final String BenefitType_FREEGOODSBENEFIT = "FREEGOODSBENEFIT";
	private static final String BenefitType_DISCOUNTBENEFIT = "DISCOUNTBENEFIT";

	/*
	 * (non-Javadoc)
	 *
	 * @see de.hybris.platform.converters.Populator#populate(java.lang.Object, java.lang.Object)
	 */
	@Override
	public void populate(final DealModel source, final DealData target) throws ConversionException
	{
		Assert.notNull(source, "Parameter source cannot be null.");
		Assert.notNull(target, "Parameter target cannot be null.");

		target.setCode(source.getCode());
		if (source.getDealType() != null)
		{
			target.setDealType(source.getDealType().getCode());
		}

		target.setValidFrom(DateFormatUtils.format(source.getValidFrom(), DATE_SAFE_FORMAT));
		target.setValidTo(DateFormatUtils.format(source.getValidTo(), DATE_SAFE_FORMAT));
		target.setDescription(source.getDescription());

		addConditionGroup(source, target);

	}

	/**
	 * Populate the ConditionGroup from Model to Data
	 *
	 * @param source
	 * @param target
	 *
	 */
	protected void addConditionGroup(final DealModel source, final DealData target)
	{
		if (source.getConditionGroup() != null)
		{
			final DealConditionGroupModel dealConditionGroupModel = source.getConditionGroup();

			final DealConditionGroupData dealConditionGroupData = new DealConditionGroupData();
			//If the Conditions is empty, will not populate it
			if (CollectionUtils.isNotEmpty(source.getConditionGroup().getDealConditions()))
			{
				final List<DealConditionData> dealConditions = new ArrayList<DealConditionData>();
				for (final AbstractDealConditionModel adc : dealConditionGroupModel.getDealConditions())
				{
					dealConditions.add(populateCondition(adc));
				}
				dealConditionGroupData.setDealConditions(dealConditions);
			}
			// If the Benefits is empty, will not populate it
			if (CollectionUtils.isNotEmpty(source.getConditionGroup().getDealBenefits()))
			{
				final List<DealBenefitData> dealBenefits = new ArrayList<DealBenefitData>();
				for (final AbstractDealBenefitModel adb : dealConditionGroupModel.getDealBenefits())
				{
					dealBenefits.add(populateBenefit(adb));
				}
				dealConditionGroupData.setDealBenefits(dealBenefits);
			}
			target.setDealConditionGroupData(dealConditionGroupData);
		}
	}

	/**
	 * Populate the Condition
	 *
	 * @param adc
	 * @return DealConditionData
	 */
	protected DealConditionData populateCondition(final AbstractDealConditionModel adc)
	{
		final DealConditionData dealConditionData = new DealConditionData();
		if (adc instanceof ProductDealConditionModel)
		{
			final ProductDealConditionModel pdc = (ProductDealConditionModel) adc;

			dealConditionData.setConditionType(ConditionType_PRODUCTCONDITION);
			dealConditionData.setUnit(convertUnit(pdc.getUnit()));
			dealConditionData.setMinQty(pdc.getMinQty());
			dealConditionData.setProduct(convertProduct(pdc.getProductCode()));
		}
		else if (adc instanceof MinQtyDealConditionModel)
		{
			final MinQtyDealConditionModel mqdc = (MinQtyDealConditionModel) adc;

			dealConditionData.setConditionType(ConditionType_MINQTYCONDITION);
			dealConditionData.setUnit(convertUnit(mqdc.getUnit()));
			dealConditionData.setMinQty(mqdc.getMinQty());
		}
		return dealConditionData;
	}

	/**
	 * Populate the Benefit
	 *
	 * @param adb
	 * @return DealBenefitData
	 */
	protected DealBenefitData populateBenefit(final AbstractDealBenefitModel adb)
	{
		final DealBenefitData dealBenefitData = new DealBenefitData();
		if (adb instanceof FreeGoodsDealBenefitModel)
		{
			final FreeGoodsDealBenefitModel fgdb = (FreeGoodsDealBenefitModel) adb;

			dealBenefitData.setBenefitType(BenefitType_FREEGOODSBENEFIT);
			dealBenefitData.setQuantity(fgdb.getQuantity());
			dealBenefitData.setUnit(convertUnit(fgdb.getUnit()));
			dealBenefitData.setProduct(convertProduct(fgdb.getProductCode()));
		}
		else if (adb instanceof DiscountDealBenefitModel)
		{
			final DiscountDealBenefitModel ddb = (DiscountDealBenefitModel) adb;

			dealBenefitData.setBenefitType(BenefitType_DISCOUNTBENEFIT);
			dealBenefitData.setAmount(Math.abs(ddb.getAmount()));
			dealBenefitData.setCurrency(ddb.getCurrency());
			dealBenefitData.setSaleUnit(ddb.getSaleUnit());
			dealBenefitData.setUnit(convertUnit(ddb.getUnit()));
		}
		return dealBenefitData;
	}

	/**
	 * try to convert the UomData
	 *
	 * @param unit
	 * @return UomData
	 */
	protected UomData convertUnit(final UnitModel unit)
	{
		final UomData unitData = new UomData();
		if (unit != null)
		{
			unitData.setName(unit.getName());
			unitData.setCode(unit.getCode());
		}

		return unitData;
	}

	/**
	 * try to convert the ProductData
	 *
	 * @param productCode
	 * @return ProductData
	 */
	protected ProductData convertProduct(final String productCode)
	{
		final List<ProductOption> options = new ArrayList<>(Arrays.asList(ProductOption.BASIC, ProductOption.URL,
				ProductOption.SUMMARY, ProductOption.GALLERY, ProductOption.CATEGORIES));
		try
		{
			final SABMAlcoholVariantProductEANModel eanProduct = getEanProduct(productCode);
			final ProductData productData = productFacade.getProductForOptions(eanProduct, options);
			sabmAlcoholProductPopulator.populate(eanProduct, productData);
			return productData;
		}
		catch (UnknownIdentifierException | AmbiguousIdentifierException | IllegalArgumentException e)
		{
			LOG.warn("Product [{}] not found - {}: {}", productCode, e);
		}
		return null;
	}

	/**
	 * Get SABMAlcoholVariantProductEANModel object
	 *
	 * @param productCode
	 * @return SABMAlcoholVariantProductEANModel
	 */
	private SABMAlcoholVariantProductEANModel getEanProduct(final String productCode)
	{
		SABMAlcoholVariantProductEANModel eanProduct = null;
		ProductModel variant = productService.getProductForCode(productCode);
		while (variant instanceof VariantProductModel)
		{
			if (variant.getClass().equals(SABMAlcoholVariantProductEANModel.class))
			{
				eanProduct = (SABMAlcoholVariantProductEANModel) variant;
				break;
			}

			variant = ((VariantProductModel) variant).getBaseProduct();
		}
		return eanProduct;
	}

	/**
	 * @return the productService
	 */
	public ProductService getProductService()
	{
		return productService;
	}

	/**
	 * @param productService
	 *           the productService to set
	 */
	public void setProductService(final ProductService productService)
	{
		this.productService = productService;
	}

	/**
	 * @return the sabmAlcoholProductPopulator
	 */
	public SABMAlcoholProductPopulator getSabmAlcoholProductPopulator()
	{
		return sabmAlcoholProductPopulator;
	}

	/**
	 * @param sabmAlcoholProductPopulator
	 *           the sabmAlcoholProductPopulator to set
	 */
	public void setSabmAlcoholProductPopulator(final SABMAlcoholProductPopulator sabmAlcoholProductPopulator)
	{
		this.sabmAlcoholProductPopulator = sabmAlcoholProductPopulator;
	}

	/**
	 * @return the productFacade
	 */
	public ProductFacade getProductFacade()
	{
		return productFacade;
	}

	/**
	 * @param productFacade
	 *           the productFacade to set
	 */
	public void setProductFacade(final ProductFacade productFacade)
	{
		this.productFacade = productFacade;
	}
}
