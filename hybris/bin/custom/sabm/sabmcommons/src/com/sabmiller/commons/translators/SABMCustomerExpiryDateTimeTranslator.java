/**
 *
 */
package com.sabmiller.commons.translators;

import de.hybris.platform.core.Registry;
import de.hybris.platform.impex.jalo.ImpExException;
import de.hybris.platform.impex.jalo.translators.AbstractSpecialValueTranslator;
import de.hybris.platform.jalo.Item;
import de.hybris.platform.servicelayer.model.ModelService;
import de.hybris.platform.store.BaseStoreModel;
import de.hybris.platform.store.services.BaseStoreService;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.TimeZone;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.sabmiller.core.model.SABMRecommendationModel;


/**
 * @author Siddarth
 *
 */
public class SABMCustomerExpiryDateTimeTranslator extends AbstractSpecialValueTranslator
{

	private ModelService modelService;
	private BaseStoreService baseStoreService;
	private static final Logger LOG = LoggerFactory.getLogger(SABMCustomerExpiryDateTimeTranslator.class);
	private static final String DATEFORMAT = "dd-MM-yyyy HH:mm:ss";

	@Override
	public String performExport(final Item item) throws ImpExException
	{
		try
		{
		if (Registry.getCoreApplicationContext().getBean("modelService") instanceof ModelService)
		{
			modelService = (ModelService) Registry.getCoreApplicationContext().getBean("modelService");
			final SABMRecommendationModel recommendationModel = modelService.get(item);
				final SimpleDateFormat dt1 = new SimpleDateFormat(DATEFORMAT);
				if (recommendationModel.getModifiedtime() != null)
			{
					return dt1.format(offsetTimeZone(recommendationModel.getModifiedtime())).toString();
			}
		}
		}
		catch (final Exception e)
		{
			LOG.error("Error during Recommendation export while exporting line");
		}
		return null;
	}

	private Date offsetTimeZone(final Date date)
	{

		if (Registry.getCoreApplicationContext().getBean("baseStoreService") instanceof BaseStoreService)
		{
			baseStoreService = (BaseStoreService) Registry.getCoreApplicationContext().getBean("baseStoreService");
			final BaseStoreModel baseStore = baseStoreService.getBaseStoreForUid("sabmStore");


			TimeZone toTimeZone = null;

			//Getting BaseStore timezone
			if (baseStore != null && baseStore.getTimeZone() != null)
			{
				toTimeZone = TimeZone.getTimeZone(baseStore.getTimeZone().getCode());
			}


		// Construct FROM and TO TimeZone instances
		final TimeZone fromTimeZone = Calendar.getInstance().getTimeZone();

		return new Date(
				date.getTime() - fromTimeZone.getOffset(date.getTime()) + toTimeZone.getOffset(date.getTime()));

		}
		return null;

	}
}