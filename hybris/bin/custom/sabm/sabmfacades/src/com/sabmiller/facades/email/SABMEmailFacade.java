/**
 *
 */
package com.sabmiller.facades.email;

import com.sabmiller.commons.model.SystemEmailMessageModel;


/**
 * The Interface SABMEmailFacade.
 */
public interface SABMEmailFacade
{

	/**
	 * Send contact us email.
	 *
	 * @param subject
	 *           the subject
	 * @param message
	 *           the message
	 * @return the system email message model
	 */
	SystemEmailMessageModel sendContactUsEmail(String subject, String message);

	/**
	 * Send service request email.
	 *
	 * @param requestKey
	 *           the request key
	 * @param requestType
	 *           the request type
	 * @param text
	 *           the text
	 * @return the system email message model
	 */
	SystemEmailMessageModel sendServiceRequestEmail(String requestKey, String requestType, String text);
}
