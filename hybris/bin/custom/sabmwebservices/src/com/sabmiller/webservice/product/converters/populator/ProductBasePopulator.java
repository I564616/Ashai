/**
 *
 */
package com.sabmiller.webservice.product.converters.populator;

import de.hybris.platform.commercefacades.product.data.ProductData;
import de.hybris.platform.converters.Populator;

import jakarta.annotation.Resource;

import org.apache.commons.collections4.CollectionUtils;
import org.apache.commons.lang3.StringUtils;
import org.apache.log4j.Logger;

import com.sabmiller.core.product.SabmUnitService;
import com.sabmiller.webservice.importer.DataImportValidationException;
import com.sabmiller.webservice.product.Material;
import com.sabmiller.webservice.product.Material.GeneralData;
import com.sabmiller.webservice.product.Material.TaxClassification;
import com.sabmiller.webservice.product.util.SapHybrisUnitOfMeasureMapper;



/**
 * @author joshua.a.antony
 *
 */
public class ProductBasePopulator implements Populator<Material, ProductData>
{

	private static final Logger LOG = Logger.getLogger(ProductBasePopulator.class);
	private static final String TAX_CATEGORY = "Z9W0";
	private static final String TAX_CLASSIFICATION_MATERIAL = "1";
	@Resource(name = "sapHybrisUnitOfMeasureMapper")
	private SapHybrisUnitOfMeasureMapper mapper;

	@Resource(name = "unitService")
	private SabmUnitService unitService;

	/*
	 * (non-Javadoc)
	 *
	 * @see de.hybris.platform.converters.impl.AbstractConverter#populate(java.lang.Object, java.lang.Object)
	 */

	@Override
	public void populate(final Material material, final ProductData target)
	{
		target.setInternalId(material.getInternalID() != null ? material.getInternalID().getValue() : null);
		target.setName(getDescription(material));
		target.setCode(material.getMaterialTypeCode() != null ? material.getMaterialTypeCode().getValue() : null);
		target.setSapAvailability(material.getCrossPlantMaterialStatus());
        if(material.getBaseUnit() != null) {
            target.setUnit(mapper.getHybrisUomCode(material.getBaseUnit().getValue()));
        }

		if (CollectionUtils.isNotEmpty(material.getGeneralData()))
		{
			final GeneralData generalData = material.getGeneralData().get(0);
			target.setEan(generalData.getBaseUnitOfMeasureEAN());
			target.setWeight(generalData.getGrossWeight() + " " + mapper.getHybrisUomCode(generalData.getWeightUnit()));
			//target.setSizeUnit(mapper.getHybrisUomCode(generalData.getWeightUnit()));
		}

		LOG.debug("name : " + target.getName() + " , description : " + target.getDescription() + " , style : " + target.getStyle()
				+ " , " + "code : " + target.getCode() + " , unit : " + target.getUnit() + " , sapAvailability : "
				+ target.getSapAvailability() + " , ean : " + target.getEan() + " , weight : " + target.getWeight()
				+ " , sizeUnit : " + target.getSizeUnit());


		if (StringUtils.isBlank(target.getEan()))
		{
			throw new DataImportValidationException("EAN unvailable in the request. It is mandatory in Hybris !!!");
		}

		try
		{
			unitService.getUnitForCode(target.getUnit());
		}
		catch (final Exception e)
		{
			throw new DataImportValidationException("Unit " + material.getBaseUnit().getValue()
					+ " is invalid - it does not exist in Hybris");
		}
		populateWetFlag(material, target);
	}

	/**
	 *
	 */
	private void populateWetFlag(final Material material, final ProductData target)
	{
		if(null != material && null != material.getTaxClassification()) {
			for (final TaxClassification tc : material.getTaxClassification())
			{
				if (TAX_CATEGORY.equalsIgnoreCase(tc.getTaxCategory())
						&& TAX_CLASSIFICATION_MATERIAL.equalsIgnoreCase(tc.getTaxClassificationMaterial()))
				{
					target.setWetEligible(true);
				}
				else
				{
					target.setWetEligible(false);
				}

			}

		}

	}

	protected String getDescription(final Material material)
	{
		if (CollectionUtils.isNotEmpty(material.getDescription())
				&& material.getDescription().get(0).getDescription() != null)
		{
			return material.getDescription().get(0).getDescription().getValue();
		}
		return null;
	}

}
