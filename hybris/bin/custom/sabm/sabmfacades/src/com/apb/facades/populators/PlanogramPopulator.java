/**
 *
 */
package com.apb.facades.populators;

import de.hybris.platform.converters.Populator;
import de.hybris.platform.servicelayer.dto.converter.ConversionException;

import org.apache.commons.lang3.StringUtils;

import com.asahi.facades.planograms.PlanogramData;
import com.sabmiller.core.model.PlanogramModel;


/**
 * @author Saumya.Mittal1
 *
 */
public class PlanogramPopulator implements Populator<PlanogramModel, PlanogramData>
{

	@Override
	public void populate(final PlanogramModel source, final PlanogramData target) throws ConversionException
	{
		target.setCode(source.getCode());
		target.setDocumentName(source.getDocumentName());
		target.setUploadedBy(source.getUploadedBy());
		if (null != source.getMedia())
		{
			target.setUploadedDate(source.getMedia().getCreationtime());
		}
		else
		{
			target.setUploadedDate(source.getCreationtime());
		}
		target.setMediaURL(null != source.getMedia() ? source.getMedia().getDownloadURL() : StringUtils.EMPTY);
	}

}
