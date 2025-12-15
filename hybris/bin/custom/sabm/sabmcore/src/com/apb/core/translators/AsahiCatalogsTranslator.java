package com.apb.core.translators;

import de.hybris.platform.impex.jalo.translators.AbstractValueTranslator;
import de.hybris.platform.jalo.Item;
import de.hybris.platform.jalo.JaloInvalidParameterException;

import java.util.HashSet;
import java.util.Set;

import org.apache.commons.lang3.StringUtils;

import com.apb.core.constants.ApbCoreConstants;


/**
 * @author Ashish.Monga Gets set of AsahiCatalogProductMappingModel for the catalogs.
 *
 */
public class AsahiCatalogsTranslator extends AbstractValueTranslator
{

	/*
	 * (non-Javadoc)
	 *
	 * @see de.hybris.platform.impex.jalo.translators.AbstractValueTranslator#importValue(java.lang.String,
	 * de.hybris.platform.jalo.Item) This method is used to get the Asahi Catalog Product Mapping Model for the
	 * corresponding catalog IDs.
	 */
	@Override
	public Object importValue(final String asahiCatalogs, final Item item) throws JaloInvalidParameterException
	{
		Set<String> asahiCatalogHierarchy = null;
		if (StringUtils.isNotEmpty(asahiCatalogs))
		{
			final String asahiCatalogsStr = asahiCatalogs.replaceAll(ApbCoreConstants.STRING_SEPARATOR_WHITESPACE, "");
			asahiCatalogHierarchy = new HashSet<String>();
			for (final String asahiCatalog : asahiCatalogsStr.split(ApbCoreConstants.STRING_SEPARATOR_PIPE))
			{
				asahiCatalogHierarchy.add(asahiCatalog);
			}
		}
		return asahiCatalogHierarchy;
	}

	@Override
	public String exportValue(final Object arg0) throws JaloInvalidParameterException
	{
		return null;
	}

}
