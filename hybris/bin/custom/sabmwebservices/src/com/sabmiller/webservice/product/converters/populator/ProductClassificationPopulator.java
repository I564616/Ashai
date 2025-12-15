/**
 *
 */
package com.sabmiller.webservice.product.converters.populator;

import de.hybris.platform.commercefacades.product.data.ProductData;
import de.hybris.platform.converters.Populator;
import de.hybris.platform.servicelayer.dto.converter.ConversionException;

import jakarta.annotation.Resource;

import org.apache.commons.lang3.StringUtils;
import org.apache.log4j.Logger;

import com.sabmiller.core.util.SABMFormatterUtils;
import com.sabmiller.webservice.importer.DataImportValidationException;
import com.sabmiller.webservice.product.Material;
import com.sabmiller.webservice.product.Material.ClassificationData;


/**
 * @author joshua.a.antony
 *
 */
public class ProductClassificationPopulator implements Populator<Material, ProductData>
{

	private static final Logger LOG = Logger.getLogger(ProductClassificationPopulator.class);

	private static final String Z01 = "Z01";
	private static final String Z90 = "Z90";
	private static final String LEAD_SKU_CHARACTERISTIC_VAL = "X";

	private static final String CIDER = "CIDER";
	private static final String CIDER_CATEGORY = "120";
	private static final String ONE_HUNDRED_THIRTY = "130";

	@Resource(name = "sabFormatterUtil")
	private SABMFormatterUtils formatterUtils;

	private enum CharacteristicDesc
	{
		ABV("Alcohol Strength"), CAPACITY("Pack Volume"), CONTAINER("Pack Format"), BRAND("Brand Family"), PRESENTATION(
				"Pack Configuration"), STYLE("Style Category Code"), CATEGORY_ATTRIBUTE("Product Group"), CATEGORY_VARIETY(
				"Variety Code"), LEAD_SKU("Base SKU"), SUB_BRAND("Brand Code");

		private final String code;

		CharacteristicDesc(final String code)
		{
			this.code = code;
		}

		public String getCode()
		{
			return code;
		}
	}

	private void validate(final ProductData product)
	{
		if (StringUtils.isBlank(product.getAbv()))
		{
			throw new DataImportValidationException("Alcohol volume  unvailable in the request. It is mandatory in Hybris !!!");
		}

		if (StringUtils.isBlank(product.getBrand()))
		{
			throw new DataImportValidationException("Brand unvailable in the request. It is mandatory in Hybris !!!");
		}

		if (StringUtils.isBlank(product.getCategoryAttribute()))
		{
			throw new DataImportValidationException("Category Attribute unvailable in the request. It is mandatory in Hybris !!!");
		}

		if (StringUtils.isBlank(product.getCategoryVariety()))
		{
			throw new DataImportValidationException("Category Variety unvailable in the request. It is mandatory in Hybris !!!");
		}
	}

	/*
	 * (non-Javadoc)
	 *
	 * @see de.hybris.platform.converters.Populator#populate(java.lang.Object, java.lang.Object)
	 */
	@Override
	public void populate(final Material source, final ProductData target) throws ConversionException
	{
		if (source.getClassificationData() != null)
		{
			for (final ClassificationData cd : source.getClassificationData())
			{

				if (Z01.equalsIgnoreCase(cd.getClassType())
						&& CharacteristicDesc.ABV.getCode().equalsIgnoreCase(cd.getCharacteristicDescription()))
				{
					if (!StringUtils.isBlank(cd.getCharacteristicValue()))
					{
						target.setAbv(formatterUtils.formatABV(cd.getCharacteristicValue()));
					}
				}
				else if (Z01.equalsIgnoreCase(cd.getClassType())
						&& CharacteristicDesc.CAPACITY.getCode().equalsIgnoreCase(cd.getCharacteristicDescription()))
				{
					target.setCapacity(cd.getCharacteristicValue());
				}
				else if (Z01.equalsIgnoreCase(cd.getClassType())
						&& CharacteristicDesc.CONTAINER.getCode().equalsIgnoreCase(cd.getCharacteristicDescription()))
				{
					target.setContainer(formatterUtils.toTitleCase(cd.getCharacteristicValueDescription()));
				}
				else if (Z01.equalsIgnoreCase(cd.getClassType())
						&& CharacteristicDesc.BRAND.getCode().equalsIgnoreCase(cd.getCharacteristicDescription()))
				{
					if (!StringUtils.isBlank(cd.getCharacteristicValueDescription()))
					{
						target.setBrand(formatterUtils.toTitleCase(cd.getCharacteristicValueDescription()));
					}
				}
				else if (Z01.equalsIgnoreCase(cd.getClassType())
						&& CharacteristicDesc.PRESENTATION.getCode().equalsIgnoreCase(cd.getCharacteristicDescription()))
				{
					target.setPresentation(formatterUtils.formatPackageConfiguration(cd.getCharacteristicValue()));
				}
				else if (Z01.equalsIgnoreCase(cd.getClassType())
						&& CharacteristicDesc.CATEGORY_ATTRIBUTE.getCode().equalsIgnoreCase(cd.getCharacteristicDescription()))
				{
					if (!StringUtils.isBlank(cd.getCharacteristicValue()))
					{
						target.setCategoryAttribute(formatterUtils.toTitleCase(cd.getCharacteristicValue()));
					}
				}
				else if (Z01.equalsIgnoreCase(cd.getClassType())
						&& CharacteristicDesc.SUB_BRAND.getCode().equalsIgnoreCase(cd.getCharacteristicDescription()))
				{
					if (!StringUtils.isBlank(cd.getCharacteristicValueDescription()))
					{
						target.setSubBrand(formatterUtils.toTitleCase(cd.getCharacteristicValueDescription()));
					}
				}
				else if (Z90.equalsIgnoreCase(cd.getClassType())
						&& CharacteristicDesc.STYLE.getCode().equalsIgnoreCase(cd.getCharacteristicDescription()))
				{
					target.setStyle(formatterUtils.toTitleCase(cd.getCharacteristicValueDescription()));
				}
				else if (Z90.equalsIgnoreCase(cd.getClassType())
						&& CharacteristicDesc.CATEGORY_VARIETY.getCode().equalsIgnoreCase(cd.getCharacteristicDescription()))
				{
					if (!StringUtils.isBlank(cd.getCharacteristicValueDescription()))
					{
						target.setCategoryVariety(formatterUtils.toTitleCase(cd.getCharacteristicValueDescription()));
					}
				}
				else if (Z90.equalsIgnoreCase(cd.getClassType())
						&& CharacteristicDesc.LEAD_SKU.getCode().equalsIgnoreCase(cd.getCharacteristicDescription()))
				{
					target.setLeadSku(LEAD_SKU_CHARACTERISTIC_VAL.equalsIgnoreCase(cd.getCharacteristicValue()));
				}
			}

			recalculateCider(target);

			validate(target);

			LOG.debug("abv : " + target.getAbv() + " , capacity : " + target.getCapacity() + " , container : "
					+ target.getContainer() + " , brand : " + target.getBrand() + " , presentation : " + target.getPresentation()
					+ " , categoryVariety : " + target.getCategoryVariety() + " , categoryAttribute : "
					+ target.getCategoryAttribute() + " , style : " + target.getStyle() + " , leadSku : " + target.isLeadSku());
		}

	}

	private void recalculateCider(final ProductData productData)
	{
		if (ONE_HUNDRED_THIRTY.equals(productData.getCategoryAttribute())
				&& CIDER.equalsIgnoreCase(productData.getCategoryVariety()))
		{
			productData.setCategoryAttribute(CIDER_CATEGORY);
		}
	}

}
