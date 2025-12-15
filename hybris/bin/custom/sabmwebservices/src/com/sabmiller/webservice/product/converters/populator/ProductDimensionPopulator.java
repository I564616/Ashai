/**
 *
 */
package com.sabmiller.webservice.product.converters.populator;

import com.sabmiller.core.util.SABMFormatterUtils;
import com.sabmiller.webservice.product.Material;
import de.hybris.platform.commercefacades.product.data.ProductData;
import de.hybris.platform.converters.Populator;
import de.hybris.platform.servicelayer.dto.converter.ConversionException;
import org.apache.commons.collections4.CollectionUtils;
import org.apache.commons.lang3.ArrayUtils;
import org.apache.commons.lang3.StringUtils;
import org.apache.log4j.Logger;

import jakarta.annotation.Resource;


/**
 * @author joshua.a.antony
 *
 */
public class ProductDimensionPopulator implements Populator<Material, ProductData>
{

	private static final Logger LOG = Logger.getLogger(ProductDimensionPopulator.class);

	@Resource(name = "sabFormatterUtil")
	private SABMFormatterUtils formatterUtils;

	/*
	 * (non-Javadoc)
	 *
	 * @see de.hybris.platform.converters.Populator#populate(java.lang.Object, java.lang.Object)
	 */
	@Override
	public void populate(final Material source, final ProductData target) throws ConversionException
	{
		if (CollectionUtils.isNotEmpty(source.getGeneralData()))
		{
			final String dimensions = source.getGeneralData().get(0).getDimensions();
			if (StringUtils.isNotBlank(dimensions))
			{
				final String[] lwh = toLWH(dimensions);
				if (lwh != null && lwh.length >= 3)
				{
					target.setLength(formatterUtils.formatDimension(lwh[0]));
					target.setWidth(formatterUtils.formatDimension(lwh[1]));
					target.setHeight(formatterUtils.formatDimension(lwh[2]));
				}
			}
		}

		LOG.debug("Length : " + target.getLength() + " , Width : " + target.getWidth() + " , Height : " + target.getHeight());
	}

	private String[] toLWH(final String dimension)
	{
		final String[] lwh = dimension.split("X");
		if (lwh.length >= 3)
		{
			return lwh;
		}
		final String[] nextSplit = dimension.split("x");
		if (nextSplit.length >= 3)
		{
			return nextSplit;
		}
		return ArrayUtils.EMPTY_STRING_ARRAY;
	}

}
