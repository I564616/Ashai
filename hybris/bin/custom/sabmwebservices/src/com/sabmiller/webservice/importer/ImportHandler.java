/**
 *
 */
package com.sabmiller.webservice.importer;

import org.springframework.messaging.Message;


/**
 * ImportHandler base structure for the imports from SAP (Products, Customer, Product Exclusions etc...)
 *
 * @author joshua.a.antony
 */
public interface ImportHandler<RequestEntity, Response>
{
	Message savePayload(final Message message);

	String handleXSDValidationError(final String message, final String payloadId);

	<IC> Response executeImport(final RequestEntity requestEntity, final String payloadId, IC context);

	// method similar to executeImport but handle more generic request
	Response handleRequest(final RequestEntity requestEntity);

}
