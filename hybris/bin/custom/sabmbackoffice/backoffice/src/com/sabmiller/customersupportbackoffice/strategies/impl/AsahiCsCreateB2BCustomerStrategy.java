/**
 *
 */
package com.sabmiller.customersupportbackoffice.strategies.impl;

import de.hybris.platform.b2b.model.B2BCustomerModel;
import de.hybris.platform.commerceservices.customer.DuplicateUidException;
import de.hybris.platform.commerceservices.security.SecureTokenService;
import de.hybris.platform.servicelayer.i18n.CommonI18NService;
import de.hybris.platform.servicelayer.model.ModelService;
import de.hybris.platform.customersupportbackoffice.widgets.DefaultCsFormInitialsFactory;import de.hybris.platform.servicelayer.time.TimeService;
import de.hybris.platform.site.BaseSiteService;

import jakarta.annotation.Resource;

import de.hybris.platform.site.BaseSiteService;

import com.apb.core.services.ApbCustomerAccountService;
import com.sabmiller.cockpitng.customersupportbackoffice.data.AsahiCsCreateB2BCustomerForm;


/**
 * @author Naveen.Wadhwani
 *
 */
public class AsahiCsCreateB2BCustomerStrategy implements AsahiCsCreateCustomerStrategy
{
	@Resource(name = "customerAccountService")
	private ApbCustomerAccountService customerAccountService;
	private SecureTokenService secureTokenService;
	private TimeService timeService;
	private BaseSiteService baseSiteService;
	private ModelService modelService;
	private CommonI18NService commonI18NService;
	@Resource(name = "csFormInitialsFactory")
	private DefaultCsFormInitialsFactory csFormInitialsFactory;



	/**
	 * This method will create the B2B customer
	 */
	public void createCustomer(final AsahiCsCreateB2BCustomerForm asahiCsCreateB2BCustomerForm) throws DuplicateUidException
	{

		final B2BCustomerModel b2bCustomerModel = (B2BCustomerModel) getModelService().create(B2BCustomerModel.class);

		b2bCustomerModel.setName(asahiCsCreateB2BCustomerForm.getName());
		b2bCustomerModel.setTitle(asahiCsCreateB2BCustomerForm.getTitle());
		b2bCustomerModel.setSessionLanguage(getCommonI18NService().getCurrentLanguage());
		b2bCustomerModel.setSessionCurrency(getCommonI18NService().getCurrentCurrency());
		b2bCustomerModel.setUid(asahiCsCreateB2BCustomerForm.getEmail().toLowerCase());
		b2bCustomerModel.setOriginalUid(asahiCsCreateB2BCustomerForm.getEmail());
		b2bCustomerModel.setDefaultB2BUnit(asahiCsCreateB2BCustomerForm.getDefaultB2BUnit());
		b2bCustomerModel.setEmail(asahiCsCreateB2BCustomerForm.getEmail());
		b2bCustomerModel.setActive(asahiCsCreateB2BCustomerForm.getActive());
		getBaseSiteService().setCurrentBaseSite(asahiCsCreateB2BCustomerForm.getSite(), false);
		// Sending the Password Reset only when the Customer is successfully Created
		customerAccountService.registerGuestForAnonymousCheckout(b2bCustomerModel, null);
		if (b2bCustomerModel.getActive() && null != b2bCustomerModel.getCustomerID())
		{
			customerAccountService.assistedForgotPassword(b2bCustomerModel);
		}

		if (null != asahiCsCreateB2BCustomerForm.getAddress() && asahiCsCreateB2BCustomerForm.getAddress().getCountry() != null)
		{
			customerAccountService.saveAddressEntry(b2bCustomerModel, asahiCsCreateB2BCustomerForm.getAddress());
			csFormInitialsFactory.setLastSavedAddress(asahiCsCreateB2BCustomerForm.getAddress());
		}
	}

	/**
	 * @return the baseSiteService
	 */
	public BaseSiteService getBaseSiteService()
	{
		return baseSiteService;
	}

	/**
	 * @param baseSiteService
	 *           the baseSiteService to set
	 */
	public void setBaseSiteService(BaseSiteService baseSiteService)
	{
		this.baseSiteService = baseSiteService;
	}

	/**
	 * @return the modelService
	 */
	public ModelService getModelService()
	{
		return modelService;
	}

	/**
	 * @param modelService
	 *           the modelService to set
	 */
	public void setModelService(ModelService modelService)
	{
		this.modelService = modelService;
	}

	/**
	 * @return the commonI18NService
	 */
	public CommonI18NService getCommonI18NService()
	{
		return commonI18NService;
	}

	/**
	 * @param commonI18NService
	 *           the commonI18NService to set
	 */
	public void setCommonI18NService(CommonI18NService commonI18NService)
	{
		this.commonI18NService = commonI18NService;
	}

	/**
	 * @return the timeService
	 */
	public TimeService getTimeService()
	{
		return timeService;
	}

	/**
	 * @param timeService
	 *           the timeService to set
	 */
	public void setTimeService(final TimeService timeService)
	{
		this.timeService = timeService;
	}

	/**
	 * @return the secureTokenService
	 */
	public SecureTokenService getSecureTokenService()
	{
		return secureTokenService;
	}

	/**
	 * @param secureTokenService
	 *           the secureTokenService to set
	 */
	public void setSecureTokenService(final SecureTokenService secureTokenService)
	{
		this.secureTokenService = secureTokenService;
	}
}
