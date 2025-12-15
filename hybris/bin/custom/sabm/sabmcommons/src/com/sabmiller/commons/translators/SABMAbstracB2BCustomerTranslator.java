package com.sabmiller.commons.translators;

import de.hybris.platform.b2b.jalo.B2BCustomer;
import de.hybris.platform.b2b.model.B2BCustomerModel;
import de.hybris.platform.core.Registry;
import de.hybris.platform.impex.jalo.translators.AbstractSpecialValueTranslator;
import de.hybris.platform.jalo.Item;
import de.hybris.platform.servicelayer.model.ModelService;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;


/**
 * Translator of password exists for B2BCustomer export.
 */
public abstract class SABMAbstracB2BCustomerTranslator extends AbstractSpecialValueTranslator
{

	/** The Constant LOG. */
	private static final Logger LOG = LoggerFactory.getLogger(SABMAbstracB2BCustomerTranslator.class);

	/** The Service for Model. */
	private ModelService modelService;

	/** The customer. */
	private B2BCustomerModel customer;


	/**
	 * Checks if everything is valid to perform the item/attribute translation. This method will also setup the
	 * modelService in the class.
	 *
	 * @param item
	 *           the item
	 * @return true, if is valid
	 */
	protected boolean isValidAndSetup(final Item item)
	{

		if (Registry.getCoreApplicationContext().getBean("modelService") instanceof ModelService)
		{
			modelService = (ModelService) Registry.getCoreApplicationContext().getBean("modelService");
		}

		if (modelService == null || !(item instanceof B2BCustomer))
		{
			LOG.error("Unable to translate item [{}] with modelService [{}]", item, modelService);
			return false;
		}


		customer = modelService.get(item);

		if (customer == null)
		{
			LOG.error("No B2BCustomerModel found for item [{}]", item);
			return false;
		}

		return true;
	}


	/**
	 * Gets the model service.
	 *
	 * @return the modelService
	 */
	public ModelService getModelService()
	{
		return modelService;
	}


	/**
	 * Gets the customer.
	 *
	 * @return the customer
	 */
	public B2BCustomerModel getCustomer()
	{
		return customer;
	}


}
