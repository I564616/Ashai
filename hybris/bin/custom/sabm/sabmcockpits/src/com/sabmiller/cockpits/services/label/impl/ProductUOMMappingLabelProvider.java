package com.sabmiller.cockpits.services.label.impl;

import java.util.Locale;

import org.apache.commons.lang3.StringUtils;

import com.sabmiller.core.model.ProductUOMMappingModel;

import de.hybris.platform.cockpit.services.label.AbstractModelLabelProvider;


public class ProductUOMMappingLabelProvider extends AbstractModelLabelProvider<ProductUOMMappingModel>
{

	@Override
	protected String getItemLabel(ProductUOMMappingModel uomMapping)
	{
		return getItemLabel(uomMapping, null);
	}

	@Override
	protected String getItemLabel(ProductUOMMappingModel uomMapping, String languageIso)
	{
		StringBuilder stringBuilder = new StringBuilder();

		if (uomMapping != null)
		{
			Locale locale = StringUtils.isNotEmpty(languageIso) ? Locale.of(languageIso) : null;

			if (uomMapping.getFromUnit() != null)
			{
				stringBuilder.append("1 ");
				stringBuilder.append(locale == null ? uomMapping.getFromUnit().getName() : uomMapping.getFromUnit().getName(locale));
				stringBuilder.append(" = ");
			}

			if (uomMapping.getToUnit() != null)
			{
				stringBuilder.append(uomMapping.getQtyConversion());
				stringBuilder.append(" ");
				stringBuilder.append(locale == null ? uomMapping.getToUnit().getName() : uomMapping.getToUnit().getName(locale));
			}
		}

		return stringBuilder.toString();
	}

	@Override
	protected String getItemDescription(ProductUOMMappingModel uomMapping)
	{
		return StringUtils.EMPTY;
	}

	@Override
	protected String getItemDescription(ProductUOMMappingModel uomMapping, String languageIso)
	{
		return StringUtils.EMPTY;
	}

	@Override
	protected String getIconPath(ProductUOMMappingModel uomMapping)
	{
		return null;
	}

	@Override
	protected String getIconPath(ProductUOMMappingModel uomMapping, String languageIso)
	{
		return null;
	}


}
