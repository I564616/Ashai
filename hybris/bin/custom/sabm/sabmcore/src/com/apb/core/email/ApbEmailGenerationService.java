package com.apb.core.email;


import de.hybris.platform.acceleratorservices.email.EmailGenerationService;
import de.hybris.platform.acceleratorservices.model.email.EmailAddressModel;
import de.hybris.platform.acceleratorservices.model.email.EmailMessageModel;
import de.hybris.platform.processengine.model.BusinessProcessModel;


/**
 *
 */
public interface ApbEmailGenerationService extends EmailGenerationService
{

	/**
	 * @param emailSubject
	 * @param emailBody
	 * @param emailAddressModel
	 * @param displayName
	 * @param replyToAddress
	 * @return
	 */
	EmailMessageModel createSuperEmailMessage(String emailSubject, String emailBody, EmailAddressModel emailAddressModel,
			final BusinessProcessModel businessProcessModel, String replyToAddress, String emailPage);
	

}
