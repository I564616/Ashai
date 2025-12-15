/**
 *
 */
package com.sabmiller.core.recommendation.deal.translator;

/**
 * @author anil.kumar.kuruba
 *
 */
/**
*
*/

import de.hybris.platform.core.Registry;
import de.hybris.platform.impex.jalo.ImpExException;
import de.hybris.platform.impex.jalo.translators.AbstractSpecialValueTranslator;
import de.hybris.platform.jalo.Item;
import de.hybris.platform.servicelayer.model.ModelService;

import java.text.SimpleDateFormat;

import org.springframework.beans.factory.annotation.Autowired;

import com.sabmiller.core.deals.services.DealsService;
import com.sabmiller.core.enums.RecommendationType;
import com.sabmiller.core.model.DealModel;
import com.sabmiller.core.model.SABMRecommendationModel;


/**
 * @author Anil
 *
 */
public class SABMRecommendationDealExpiryDateTranslator extends AbstractSpecialValueTranslator
{

	private ModelService modelService;
	@Autowired
	private DealsService dealsService;
	private DealModel dealModel;

	SimpleDateFormat formatter = new SimpleDateFormat("MM/dd/yyyy");
	String strDate;


	public DealsService getDealsService()
	{
		return dealsService;
	}

	public void setDealsService(final DealsService dealsService)
	{
		this.dealsService = dealsService;
	}



	/**
	 * Gets the model service.
	 *
	 * @return the model service
	 */
	public ModelService getModelService()
	{
		return modelService;
	}

	/**
	 * Sets the model service.
	 *
	 * @param modelService
	 *           the new model service
	 */
	public void setModelService(final ModelService modelService)
	{
		this.modelService = modelService;
	}



	//private DealModel dealModel;


	@Override
	public String performExport(final Item item) throws ImpExException
	{
		if (Registry.getCoreApplicationContext().getBean("modelService") instanceof ModelService)
		{
			modelService = (ModelService) Registry.getCoreApplicationContext().getBean("modelService");
			final SABMRecommendationModel recommendationModel = modelService.get(item);
			if (recommendationModel.getRecommendationType().equals(RecommendationType.DEAL)
					&& recommendationModel.getDealCode() != null)
			{
				if (recommendationModel.getDealExpiryDate() != null)
				{
					return getDealExpiryDate(recommendationModel.getDealCode());

				}
			}
		}

		return null;
	}

	public String getDealExpiryDate(final String dealCode)
	{
		dealsService = (DealsService) Registry.getCoreApplicationContext().getBean("dealsService");
		dealModel = dealsService.getDeal(dealCode) != null ? dealsService.getDeal(dealCode) : null;
		return dealModel != null ? formatter.format(dealModel.getValidTo()).toString() : "";
	}
}