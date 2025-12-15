/**
 *
 */
package com.sabmiller.facades.populators;

import de.hybris.platform.b2b.model.B2BUnitModel;
import de.hybris.platform.b2b.company.B2BCommerceUnitService;
import de.hybris.platform.basecommerce.enums.StockLevelStatus;
import de.hybris.platform.commercefacades.order.data.OrderEntryData;
import de.hybris.platform.commercefacades.product.data.ProductData;
import de.hybris.platform.converters.Populator;
import de.hybris.platform.core.model.order.AbstractOrderEntryModel;
import de.hybris.platform.core.model.product.ProductModel;
import de.hybris.platform.core.model.product.UnitModel;
import de.hybris.platform.servicelayer.dto.converter.ConversionException;
import de.hybris.platform.servicelayer.exceptions.ModelNotFoundException;
import de.hybris.platform.variants.model.VariantProductModel;

import java.math.BigDecimal;
import java.util.List;

import jakarta.annotation.Resource;

import org.apache.commons.collections4.CollectionUtils;
import org.apache.commons.lang3.StringUtils;

import com.apb.core.util.AsahiSiteUtil;
import com.sabm.core.config.SabmConfigurationService;
import com.sabmiller.core.b2b.services.CUBStockInformationService;
import com.sabmiller.core.enums.CUBStockStatus;
import com.sabmiller.core.model.CUBStockInformationModel;
import com.sabmiller.core.model.PlantModel;
import com.sabmiller.core.model.ProductUOMMappingModel;
import com.sabmiller.core.model.SABMAlcoholVariantProductEANModel;
import com.sabmiller.facades.product.data.UomData;


/**
 * The SABMOrderEntryUnitSwitchPopulator to switch the unit to larger unit
 *
 * @author xiaowu.a.zhang
 * @data 2015-10-29
 *
 */
public class SABMOrderEntryUnitSwitchPopulator implements Populator<AbstractOrderEntryModel, OrderEntryData>
{

	/**
	 * Populate the target instance from the source instance.
	 *
	 * @param source
	 * @param target
	 * @throws ConversionException
	 *
	 */
	@Resource(name = "b2bCommerceUnitService")
	private B2BCommerceUnitService b2bCommerceUnitService;

	@Resource
	private CUBStockInformationService cubStockInformationService;

	@Resource(name = "sabmConfigurationService")
	private SabmConfigurationService sabmConfigurationService;
	
	@Resource
	private AsahiSiteUtil asahiSiteUtil;

	@Override
	public void populate(final AbstractOrderEntryModel source, final OrderEntryData target) throws ConversionException
	{
		if(asahiSiteUtil.isCub())
		{
   		if (source != null && target != null)
   		{
   			target.setBaseQuantity(source.getQuantity());
   
   			ProductModel variant = source.getProduct();
   			SABMAlcoholVariantProductEANModel eanProduct = null;
   
   			//Populating Stock Status
   			populateStockStatus(source.getProduct().getCode(), target);
   
   			//Checking if the source product is instanceof SABMAlcoholVariantProductEANModel because the attribute UomMappings belongs to it.
   			while (variant instanceof VariantProductModel)
   			{
   				if (variant.getClass().equals(SABMAlcoholVariantProductEANModel.class))
   				{
   					eanProduct = (SABMAlcoholVariantProductEANModel) variant;
   
   					if (eanProduct.getUnit() != null)
   					{
   						target.setBaseUnit(target.getUnit());
   						target.setUnit(convertUnit(eanProduct.getUnit()));
   					}
   
   					break;
   				}
   
   				variant = ((VariantProductModel) variant).getBaseProduct();
   			}
   
   			convertUnitByUom(source, target);
   		}
		}
	}

	/**
	 * try to convert the unit by uom
	 *
	 * @param source
	 * @param target
	 *
	 */
	protected void convertUnitByUom(final AbstractOrderEntryModel source, final OrderEntryData target)
	{
		final Long quantity = source.getQuantity();
		if (null == quantity)
		{
			return;
		}
		final SABMAlcoholVariantProductEANModel eanProduct = getEanProduct(source);
		if (null == eanProduct)
		{
			return;
		}

		final List<ProductUOMMappingModel> uomMappings = eanProduct.getUomMappings();

		if (CollectionUtils.isNotEmpty(uomMappings))
		{
			for (int i = 0; i < uomMappings.size(); i++)
			{
				final ProductUOMMappingModel productUOM = uomMappings.get(i);

				//Find the uomMapping match the source.unit and try to get the larger unit. SAB-572
				if (checkUomUsable(productUOM) && !source.getUnit().equals(productUOM.getFromUnit())
						&& source.getUnit().equals(productUOM.getToUnit()))
				{
					calculateQtyAndSetQtyUnit(target, quantity, productUOM);
				}
			}
		}
	}

	/**
	 * check if the unit in the uomMapping is usable,if there have data issue
	 *
	 * @param uom
	 * @return boolean
	 *
	 */
	private boolean checkUomUsable(final ProductUOMMappingModel uom)
	{
		if (uom.getFromUnit() != null && StringUtils.isNotBlank(uom.getFromUnit().getName()) && uom.getQtyConversion() != null)
		{
			return true;
		}
		return false;
	}

	/**
	 * Get SABMAlcoholVariantProductEANModel object
	 *
	 * @param source
	 * @return SABMAlcoholVariantProductEANModel
	 */
	private SABMAlcoholVariantProductEANModel getEanProduct(final AbstractOrderEntryModel source)
	{
		SABMAlcoholVariantProductEANModel eanProduct = null;
		ProductModel variant = source.getProduct();
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
	 * try to calculate the quantity and set it in OrderEntryData
	 *
	 * @param target
	 * @param productUOM
	 *
	 */
	protected void calculateQtyAndSetQtyUnit(final OrderEntryData target, final Long quantity,
			final ProductUOMMappingModel productUOM)
	{

		// If the quantity can be convert to larger unit, convert it and then set the larger one to the target.SAB-572
		final int calculatedBase = productUOM.getQtyConversion().intValue();
		if (calculatedBase != 0 && quantity.intValue() % calculatedBase == 0)
		{
			final long newQuantity = BigDecimal.valueOf(quantity.longValue())
					.divide(BigDecimal.valueOf(productUOM.getQtyConversion().doubleValue())).longValue();
			if (target.getQuantity().longValue() > newQuantity)
			{
				target.setQuantity(Long.valueOf(newQuantity));
				target.setUnit(convertUnit(productUOM.getFromUnit()));
			}
		}
	}

	/**
	 * try to convert the UomData
	 *
	 * @param unitModel
	 * @return UomData
	 */
	protected UomData convertUnit(final UnitModel unitModel)
	{
		final UomData unitData = new UomData();
		if (unitModel != null)
		{
			unitData.setCode(unitModel.getCode());
			unitData.setName(unitModel.getName());
			unitData.setPluralName(unitModel.getPluralName());
		}

		return unitData;
	}

	private void populateStockStatus(final String productSKU, final OrderEntryData target)
	{
		try
		{
			final B2BUnitModel parentB2bUnit = b2bCommerceUnitService.getParentUnit();
			final PlantModel plant = parentB2bUnit.getPlant();
			if (plant != null)
			{
				final CUBStockInformationModel cubStockInformationModel = cubStockInformationService
						.getCUBStockInformationForProductAndPlant(productSKU, plant);

				if (cubStockInformationModel != null)
				{
					setStockStatus(cubStockInformationModel, target.getProduct());
				}
			}
		}
		catch (final ModelNotFoundException e)
		{
			//LOG.warn("Model not found for EAN product or Plant:");
		}
		catch (final Exception e)
		{
			//LOG.warn(e.getMessage());
		}
	}

	private void setStockStatus(final CUBStockInformationModel cubStockInformationModel, final ProductData target)
	{
		if (cubStockInformationModel != null)
		{
			if (cubStockInformationModel.getStockStatus().equals(CUBStockStatus.OUTOFSTOCK))
			{
				if (sabmConfigurationService.isLowStockFlagEnforced())
				{
					target.setCubStockStatus(StockLevelStatus.LOWSTOCK);
				}
				else
				{
					target.setCubStockStatus(StockLevelStatus.OUTOFSTOCK);
				}
			}
			else if (cubStockInformationModel.getStockStatus().equals(CUBStockStatus.LOWSTOCK))
			{
				target.setCubStockStatus(StockLevelStatus.LOWSTOCK);
			}
		}
	}

}
