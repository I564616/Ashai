/**
 *
 */
package com.sabmiller.core.util;

import de.hybris.platform.servicelayer.dto.converter.ConversionException;
import de.hybris.platform.servicelayer.dto.converter.Converter;


/**
 * @author joshua.a.antony
 *
 */
public interface SabmConverter<S, T, A> extends Converter<S, T>
{
	public abstract T convert(S paramSOURCE, T paramTARGET, A paramADDITIONALINFO) throws ConversionException;

}
