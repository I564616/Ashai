/**
 *
 */
package com.sabmiller.commons.translators;

import de.hybris.platform.core.Registry;
import de.hybris.platform.impex.jalo.ImpExException;
import de.hybris.platform.impex.jalo.translators.AbstractSpecialValueTranslator;
import de.hybris.platform.jalo.Item;
import de.hybris.platform.servicelayer.model.ModelService;

import java.util.Date;
import java.util.concurrent.TimeUnit;

import com.sabmiller.core.enums.RecommendationStatus;
import com.sabmiller.core.model.SABMRecommendationModel;


/**
 * @author Siddarth
 *
 */
public class SABMRecommendationDaysSpent extends AbstractSpecialValueTranslator
{

	private ModelService modelService;

	@Override
	public String performExport(final Item item) throws ImpExException
	{
		if (Registry.getCoreApplicationContext().getBean("modelService") instanceof ModelService)
		{
			modelService = (ModelService) Registry.getCoreApplicationContext().getBean("modelService");
			final SABMRecommendationModel recommendationModel = modelService.get(item);
			if (recommendationModel.getRecommendedDate() != null && recommendationModel.getCustomerActionDate() != null
					&& !recommendationModel.getStatus().equals(RecommendationStatus.RECOMMENDED)
					&& !recommendationModel.getStatus().equals(RecommendationStatus.EXPIRED))
			{
				return getDifferenceDays(recommendationModel.getRecommendedDate(), recommendationModel.getCustomerActionDate());
			}
		}

		return null;
	}

	public String getDifferenceDays(final Date d1, final Date d2)
	{
		final long diff = d2.getTime() - d1.getTime();
		return String.valueOf(TimeUnit.DAYS.convert(diff, TimeUnit.MILLISECONDS));
	}
}