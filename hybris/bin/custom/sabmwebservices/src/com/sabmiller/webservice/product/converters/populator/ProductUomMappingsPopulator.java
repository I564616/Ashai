/**
 *
 */
package com.sabmiller.webservice.product.converters.populator;

import com.sabmiller.core.constants.SabmCoreConstants;
import de.hybris.platform.commercefacades.product.data.ProductData;
import de.hybris.platform.converters.Populator;
import de.hybris.platform.servicelayer.dto.converter.ConversionException;

import java.util.ArrayList;
import java.util.List;

import jakarta.annotation.Resource;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.sabmiller.core.product.SabmUnitService;
import com.sabmiller.facades.constants.SabmFacadesConstants;
import com.sabmiller.facades.product.data.ProductUOMMappingData;
import com.sabmiller.webservice.product.Material;
import com.sabmiller.webservice.product.Material.AlternativeUoM;
import com.sabmiller.webservice.product.Material.ClassificationData;
import com.sabmiller.webservice.product.util.SapHybrisUnitOfMeasureMapper;


/**
 * @author joshua.a.antony
 *
 */
public class ProductUomMappingsPopulator implements Populator<Material, ProductData>
{

	private static final Logger LOG = LoggerFactory.getLogger(ProductUomMappingsPopulator.class);

	@Resource(name = "sapHybrisUnitOfMeasureMapper")
	private SapHybrisUnitOfMeasureMapper mapper;

	@Resource(name = "unitService")
	private SabmUnitService unitService;

	/*
	 * (non-Javadoc)
	 *
	 * @see de.hybris.platform.converters.Populator#populate(java.lang.Object, java.lang.Object)
	 */
	@Override
	public void populate(final Material source, final ProductData target) throws ConversionException
	{
		final List<AlternativeUoM> alternativeUoms = source.getAlternativeUoM();
		String packformat = null;


		for (final ClassificationData packageType : source.getClassificationData())
		{

			if ((SabmFacadesConstants.PACKFORMAT_CAN.equals(packageType.getCharacteristicValue())
					&& SabmFacadesConstants.PACKFORMAT.equals(packageType.getCharacteristicDescription()))
					|| (SabmFacadesConstants.PACKFORMAT_BOTTLE.equals(packageType.getCharacteristicValue())
							&& SabmFacadesConstants.PACKFORMAT.equals(packageType.getCharacteristicDescription()))
					|| (SabmFacadesConstants.UOM_KEG.equals(packageType.getCharacteristicValue())
							&& SabmFacadesConstants.PACKFORMAT.equals(packageType.getCharacteristicDescription()))
					|| (SabmFacadesConstants.PACKFORMAT_DRUM.equals(packageType.getCharacteristicValue())
							&& SabmFacadesConstants.PACKFORMAT.equals(packageType.getCharacteristicDescription())))
			{

				packformat = packageType.getCharacteristicValue();
			}
		}

		if (alternativeUoms != null)
		{
			final List<ProductUOMMappingData> uomMappingList = new ArrayList<ProductUOMMappingData>();
			for (final AlternativeUoM altUom : alternativeUoms)
			{
				try
				{
					final String fromUnit = mapper.getHybrisUomCode(altUom.getAlternativeUoM());
					final String toUnit = mapper.getHybrisUomCode(source.getBaseUnit().getValue());

					final ProductUOMMappingData uomMappingData = new ProductUOMMappingData();
					if (SabmFacadesConstants.PACKFORMAT_BOTTLE.equals(packformat)
							|| SabmFacadesConstants.PACKFORMAT_CAN.equals(packformat))
					{
						if ((fromUnit.equals(SabmCoreConstants.CASE_UOM_CODE) && toUnit.equals(SabmCoreConstants.CASE_UOM_CODE))
								|| (fromUnit.equals(SabmCoreConstants.LAYER_UOM_CODE) && toUnit.equals(SabmCoreConstants.CASE_UOM_CODE))
								|| (fromUnit.equals(SabmCoreConstants.PALLET_UOM_CODE) && toUnit.equals(SabmCoreConstants.CASE_UOM_CODE)))
						{
							uomMappingData.setFromUnit(fromUnit);
							uomMappingData.setQtyConversion(Double.valueOf(altUom.getNumerator()));
							uomMappingData.setToUnit(toUnit);
						}
					}
					else if (SabmFacadesConstants.UOM_KEG.equals(packformat))
					{
						if ((fromUnit.equals(SabmFacadesConstants.UOM_KEG) && toUnit.equals(SabmFacadesConstants.UOM_KEG)))
						{
							uomMappingData.setFromUnit(fromUnit);
							uomMappingData.setQtyConversion(Double.valueOf(altUom.getNumerator()));
							uomMappingData.setToUnit(toUnit);
						}
					}
					else if (SabmFacadesConstants.PACKFORMAT_DRUM.equals(packformat))
					{
						if ((fromUnit.equals(SabmFacadesConstants.UOM_DRUM) && toUnit.equals(SabmFacadesConstants.UOM_DRUM)))
						{
							uomMappingData.setFromUnit(fromUnit);
							uomMappingData.setQtyConversion(Double.valueOf(altUom.getNumerator()));
							uomMappingData.setToUnit(toUnit);
						}
					}
					else if (toUnit.equals(SabmFacadesConstants.UOM_DRUM))
					{
						if ((fromUnit.equals(SabmFacadesConstants.UOM_DRUM) && toUnit.equals(SabmFacadesConstants.UOM_DRUM)))
						{
							uomMappingData.setFromUnit(fromUnit);
							uomMappingData.setQtyConversion(Double.valueOf(altUom.getNumerator()));
							uomMappingData.setToUnit(toUnit);
						}
					}
					else if (toUnit.equals(SabmFacadesConstants.UOM_KEG))
					{
						if ((fromUnit.equals(SabmFacadesConstants.UOM_KEG) && toUnit.equals(SabmFacadesConstants.UOM_KEG)))
						{
							uomMappingData.setFromUnit(fromUnit);
							uomMappingData.setQtyConversion(Double.valueOf(altUom.getNumerator()));
							uomMappingData.setToUnit(toUnit);
						}
					}
					else
					{
						uomMappingData.setFromUnit(fromUnit);
						uomMappingData.setQtyConversion(Double.valueOf(altUom.getNumerator()));
						uomMappingData.setToUnit(toUnit);
					}

					uomMappingList.add(uomMappingData);
					LOG.debug("fromUnit : " + uomMappingData.getFromUnit() + " , toUnit : " + uomMappingData.getToUnit()
							+ " , conversion : " + uomMappingData.getQtyConversion());
				}
				catch (final Exception e)
				{
					LOG.error("Error occured while populating alternate uoms.... ", e.getMessage());
				}

			}

			target.setUomMappingList(uomMappingList);
		}

	}
}
