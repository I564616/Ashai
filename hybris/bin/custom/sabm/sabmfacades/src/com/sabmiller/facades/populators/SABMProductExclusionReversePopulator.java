/**
 *
 */
package com.sabmiller.facades.populators;

import de.hybris.platform.b2b.model.B2BUnitModel;
import de.hybris.platform.converters.Populator;
import de.hybris.platform.servicelayer.dto.converter.ConversionException;
import de.hybris.platform.servicelayer.exceptions.AmbiguousIdentifierException;

import jakarta.annotation.Resource;

import org.apache.commons.lang3.StringUtils;
import org.apache.log4j.Logger;
import org.springframework.util.Assert;

import com.sabmiller.core.b2b.services.SabmB2BUnitService;
import com.sabmiller.core.model.ProductExclusionModel;
import com.sabmiller.facades.customer.ProductExclusionData;


/**
 * The Class SABMProductExclusionReversePopulator.
 */
public class SABMProductExclusionReversePopulator implements Populator<ProductExclusionData, ProductExclusionModel>
{
	/** The Constant LOG. */
	private static final Logger LOG = Logger.getLogger(SABMProductExclusionReversePopulator.class);

	/** The b2b unit service. */
	@Resource(name = "b2bUnitService")
	private SabmB2BUnitService b2bUnitService;

	/*
	 * (non-Javadoc)
	 *
	 * @see de.hybris.platform.converters.Populator#populate(java.lang.Object, java.lang.Object)
	 */
	@Override
	public void populate(final ProductExclusionData source, final ProductExclusionModel target) throws ConversionException
	{
		Assert.notNull(source, "Parameter source cannot be null.");
		Assert.notNull(target, "Parameter target cannot be null.");

		if (StringUtils.isNotEmpty(source.getCustomer()))
		{
			B2BUnitModel unitModel = null;
			try
			{
				//Getting the B2BUnit using by uid, in case of exception setting null to the target
				unitModel = b2bUnitService.getUnitForUid(source.getCustomer());
			}
			catch (final AmbiguousIdentifierException e)
			{
				LOG.error(e, e);
			}

			target.setCustomer(unitModel);
		}

		target.setProduct(source.getProduct());
		target.setValidFrom(source.getValidFrom());
		target.setValidTo(source.getValidTo());
	}

}
