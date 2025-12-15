/**
 *
 */
package com.sabmiller.integration.media.services;

import de.hybris.platform.mediaconversion.model.ConversionGroupModel;
import de.hybris.platform.servicelayer.exceptions.AmbiguousIdentifierException;
import de.hybris.platform.servicelayer.exceptions.UnknownIdentifierException;


/**
 * The Interface ConversionGroupService.
 */
public interface ConversionGroupService
{

	/**
	 * Gets the conversion group by code.
	 *
	 * @param code
	 *           the conversion group code
	 * @return the conversionGroup or null if code is null
	 * @throws UnknownIdentifierException
	 *            if no match is found
	 * @throws AmbiguousIdentifierException
	 *            if more than one ConversionGroupModel is found
	 */
	ConversionGroupModel getConversionGroupByCode(String code);
}
