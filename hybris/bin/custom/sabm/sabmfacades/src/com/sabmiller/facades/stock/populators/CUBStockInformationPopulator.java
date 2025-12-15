/**
 *
 */
package com.sabmiller.facades.stock.populators;

import de.hybris.platform.b2b.model.B2BUnitModel;
import de.hybris.platform.b2b.company.B2BCommerceUnitService;
import de.hybris.platform.basecommerce.enums.StockLevelStatus;
import de.hybris.platform.commercefacades.product.data.ProductData;
import de.hybris.platform.converters.Populator;
import de.hybris.platform.servicelayer.dto.converter.ConversionException;
import de.hybris.platform.servicelayer.exceptions.ModelNotFoundException;

import jakarta.annotation.Resource;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.sabm.core.config.SabmConfigurationService;
import com.sabmiller.core.b2b.services.CUBStockInformationService;
import com.sabmiller.core.enums.CUBStockStatus;
import com.sabmiller.core.model.CUBStockInformationModel;
import com.sabmiller.core.model.PlantModel;


/**
 * @author Siddarth
 *
 */
public class CUBStockInformationPopulator implements Populator<String, ProductData>
{

	private static final Logger LOG = LoggerFactory.getLogger(CUBStockInformationPopulator.class);

	@Resource
	private CUBStockInformationService cubStockInformationService;

	@Resource(name = "b2bCommerceUnitService")
	private B2BCommerceUnitService b2bCommerceUnitService;

	@Resource(name = "sabmConfigurationService")
	private SabmConfigurationService sabmConfigurationService;

	@Override
	public void populate(final String productSKU, final ProductData target) throws ConversionException
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
					setStockStatus(cubStockInformationModel, target);
				}
			}
		}
		catch (final ModelNotFoundException e)
		{
			LOG.warn("Model not found for EAN product or Plant:");
		}
		catch (final Exception e)
		{
			LOG.warn(e.getMessage());
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
